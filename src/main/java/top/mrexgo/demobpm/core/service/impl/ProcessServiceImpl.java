package top.mrexgo.demobpm.core.service.impl;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import top.mrexgo.demobpm.common.enums.NodeStatusEnum;
import top.mrexgo.demobpm.common.enums.NodeTypeEnum;
import top.mrexgo.demobpm.common.exception.ServiceException;
import top.mrexgo.demobpm.common.utils.Base32Utils;
import top.mrexgo.demobpm.core.dto.AuditReqDTO;
import top.mrexgo.demobpm.core.entity.BpmProcess;
import top.mrexgo.demobpm.core.entity.BpmProcessNode;
import top.mrexgo.demobpm.core.handler.ProcessInitHandler;
import top.mrexgo.demobpm.core.service.ProcessService;
import top.mrexgo.demobpm.persistent.dao.ProcessMongoDAO;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author liangjuhong
 * @since 2021/4/30 - 14:39
 */
@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {

    private final ProcessMongoDAO mongoDAO;
    private final ProcessInitHandler processInitHandler;

    @Override
    public Long startProcess(Integer type) {
        BpmProcess p = processInitHandler.init(type);
        mongoDAO.saveProcess(p);
        return p.getProcessId();
    }

    @Override
    public void audit(AuditReqDTO dto) {
        BpmProcess p = mongoDAO.getProcess(dto.getProcessId());
        if (!NodeStatusEnum.WAITING.equals(p.getStatus())) {
            // 流程不在审批状态
            throw new ServiceException("流程无法审批");
        }
        dto.initLocation();
        if (CollectionUtils.isEmpty(dto.getProcessNodeLocation())) {
            // 没有定位信息，报错
            throw new ServiceException("没有节点定位信息");
        }
        if (!dto.getProcessNodeLocation().get(0).equals(p.getCurrentNodePosition())) {
            // 节点定位错误
            throw new ServiceException("节点定位信息有误");
        }
        this.doAudit(p, dto);
        mongoDAO.saveProcess(p);
    }

    private void doAudit(BpmProcess p, AuditReqDTO dto) {
        if (ObjectUtils.isNotEmpty(dto.getConditionParam())) {
            if (p.getConditionParam() == null) {
                p.setConditionParam(dto.getConditionParam());
            } else {
                p.getConditionParam().putAll(dto.getConditionParam());
            }
        }

        BpmProcessNode cur = p.getNodes().get(dto.getProcessNodeLocation().get(0));
        switch (dto.getAuditType()) {
            case PASS:
                boolean passFlag = this.handleSuccessNode(p, cur, 1, dto.getProcessNodeLocation().size(), dto);
                if (passFlag) {
                    boolean endFlag = p.readyNextNode();
                    if (endFlag) {
                        // 流程审核结束
                        p.setStatus(NodeStatusEnum.COMPLETE);
                    }
                }
                break;
            case NO_PASS:
                p.setStatus(NodeStatusEnum.NO_PASS);
                this.handleNoPassNode(cur, 1, dto.getProcessNodeLocation().size(), dto);
                break;
            case ROLLBACK:
                // 解决方案是直接从当前节点截断，在后面添加从驳回节点开始的剩余节点
                handRollBackNode(cur, 1, dto.getProcessNodeLocation().size(), dto);
                connectLast(p, dto);
                break;
            case TRANSPORT:
                // 判断当前审批节点父节点是否为会签节点，如果是则直接在父节点添加一个子节点，否则将当前节点改为会签节点
                handleTransportNode(cur, null, 1, dto.getProcessNodeLocation().size(), dto);
                break;
            case RE_LUNCH:
                break;
            default:
        }
    }

    @Override
    public BpmProcess listAuditNodes(Long id) {
        BpmProcess p = mongoDAO.getProcess(id);
        List<BpmProcessNode> auditNodes = new ArrayList<>();
        findAuditNodes(p, auditNodes);
        p.setNodes(auditNodes);
        return p;
    }

    private void findAuditNodes(BpmProcess p, List<BpmProcessNode> auditNodes) {
        BpmProcessNode node = p.getCurrentNode();
        if (NodeStatusEnum.READY.equals(node.getNodeStatus()) || NodeStatusEnum.WAITING.equals(node.getNodeStatus())) {
            findAuditNodes(node, auditNodes);
        } else {
            // 不在审核状态
            throw new ServiceException("审核状态异常");
        }
    }

    private void findAuditNodes(BpmProcessNode node, List<BpmProcessNode> auditNodes) {
        Queue<BpmProcessNode> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            BpmProcessNode current = queue.poll();

            switch (current.getNodeType()) {
                case SERIAL:
                case PARALLEL:
                case COUNTERSIGN:
                    for (int i = 0; i < current.getNodes().size(); i++) {
                        BpmProcessNode now = current.getNodes().get(i);
                        if (NodeStatusEnum.READY.equals(now.getNodeStatus()) || NodeStatusEnum.WAITING.equals(now.getNodeStatus())) {
                            queue.add(now);
                        }
                    }
                    break;
                case CONDITION:
                case NORMAL:
                    auditNodes.add(current);
                    break;
                default:
            }
        }
    }

    /**
     * 处理审批通过
     * 审核节点需要普通节点
     *
     * @param p
     * @param cur     当前节点
     * @param posSign 节点相对位置
     * @param size    定位列表大小
     * @param dto     审批信息
     */
    private boolean handleSuccessNode(BpmProcess p, BpmProcessNode cur, int posSign, int size, AuditReqDTO dto) {
        if (posSign == size) {
            // 到达最终节点位置
            if (!(NodeTypeEnum.NORMAL.equals(cur.getNodeType()) || NodeTypeEnum.CONDITION.equals(cur.getNodeType()))
                || !dto.getProcessNodeId().equals(cur.getNodeId())) {
                // 未到达叶子节点
                throw new ServiceException("审核节点错误");
            }
            cur.setNodeStatus(NodeStatusEnum.COMPLETE).setAuditMsg(dto.getAuditMsg());
            return true;
        } else {
            int pos = dto.getProcessNodeLocation().get(posSign);
            BpmProcessNode next = cur.getNodes().get(pos);
            boolean passFlag = handleSuccessNode(p, next, posSign + 1, size, dto);

            // 下级节点审批通过后处理
            if (passFlag) {
                cur.setFinished(cur.getFinished() + 1);
                if (cur.getFinished().equals(cur.getAllNeedFinish())) {
                    // 当前节点所需审核子节点全部审核完成
                    cur.setNodeStatus(NodeStatusEnum.COMPLETE);
                    cur.skipRest();
                    return true;
                } else {
                    if (NodeTypeEnum.SERIAL.equals(cur.getNodeType())) {
                        // 串行节点中一个子节点通过后需初始化下一节点
                        p.doReadyNode(cur.getNodes().get(cur.getFinished()));
                    }
                }
            }
            return false;
        }
    }

    private void handleNoPassNode(BpmProcessNode cur, int i, int size, AuditReqDTO dto) {
        if (i == size) {
            // 到达最终节点位置
            if (!NodeTypeEnum.NORMAL.equals(cur.getNodeType()) || !dto.getProcessNodeId().equals(cur.getNodeId())) {
                // 未到达叶子节点
                throw new ServiceException("审核节点错误");
            }
            cur.setNodeStatus(NodeStatusEnum.NO_PASS).setAuditMsg(dto.getAuditMsg());
        } else {
            int pos = dto.getProcessNodeLocation().get(i);
            BpmProcessNode next = cur.getNodes().get(pos);
            handleNoPassNode(next, i + 1, size, dto);
            cur.setNodeStatus(NodeStatusEnum.NO_PASS);
            cur.skipRest();
        }
    }

    private void handRollBackNode(BpmProcessNode cur, int i, int size, AuditReqDTO dto) {
        if (i == size) {
            // 到达最终节点位置
            if (!NodeTypeEnum.NORMAL.equals(cur.getNodeType()) || !dto.getProcessNodeId().equals(cur.getNodeId())) {
                // 未到达叶子节点
                throw new ServiceException("审核节点错误");
            }
            cur.setNodeStatus(NodeStatusEnum.ROLLBACK).setAuditMsg(dto.getAuditMsg());
        } else {
            int pos = dto.getProcessNodeLocation().get(i);
            BpmProcessNode next = cur.getNodes().get(pos);
            handleNoPassNode(next, i + 1, size, dto);
            cur.setNodeStatus(NodeStatusEnum.ROLLBACK);
            cur.skipRest();
        }
    }

    private void handleTransportNode(BpmProcessNode cur, BpmProcessNode parent, int i, int size, AuditReqDTO dto) {
        if (i == size) {
            // 到达最终节点位置
            if (!NodeTypeEnum.NORMAL.equals(cur.getNodeType()) || !dto.getProcessNodeId().equals(cur.getNodeId())) {
                // 未到达叶子节点
                throw new ServiceException("审核节点错误");
            }
            BpmProcessNode generated = BpmProcessNode.builder()
                .nodeId(111L)
                .nodeName(cur.getNodeName())
                .nodeType(NodeTypeEnum.NORMAL)
                .nodeStatus(NodeStatusEnum.READY).build();
            if (parent == null || !NodeTypeEnum.COUNTERSIGN.equals(parent.getNodeType())) {
                // 转换当前节点为会签节点
                cur.setNodeType(NodeTypeEnum.COUNTERSIGN)
                    .setNodeStatus(NodeStatusEnum.WAITING)
                    .setNodes(new ArrayList<>())
                    .setFinished(0);

                List<Integer> anotherLoc = new ArrayList<>(dto.getProcessNodeLocation());
                anotherLoc.add(0);
                BpmProcessNode another = BpmProcessNode.builder()
                    .nodeId(112L)
                    .nodeName(cur.getNodeName())
                    .nodeType(NodeTypeEnum.NORMAL)
                    .nodeStatus(NodeStatusEnum.READY)
                    .location(Base32Utils.base32ToString(JSONUtil.toJsonStr(anotherLoc))).build();
                cur.getNodes().add(another);

                List<Integer> generatedLoc = new ArrayList<>(dto.getProcessNodeLocation());
                generatedLoc.add(1);
                generated.setLocation(Base32Utils.base32ToString(JSONUtil.toJsonStr(generatedLoc)));
                cur.getNodes().add(generated);
                if (dto.getNeedAuditFlag()) {
                    cur.setAllNeedFinish(2);
                } else {
                    cur.setAllNeedFinish(1);
                }
            } else {
                // 为父节点添加一个子节点
                parent.getNodes().add(generated);
                dto.getProcessNodeLocation().remove(dto.getProcessNodeLocation().size() - 1);
                dto.getProcessNodeLocation().add(parent.getNodes().size() - 1);
                generated.setLocation(Base32Utils.base32ToString(JSONUtil.toJsonStr(dto.getProcessNodeLocation())));
                if (dto.getNeedAuditFlag()) {
                    parent.setAllNeedFinish(parent.getAllNeedFinish() + 1);
                } else {
                    cur.setNodeStatus(NodeStatusEnum.SKIP);
                }
            }
        } else {
            int pos = dto.getProcessNodeLocation().get(i);
            BpmProcessNode next = cur.getNodes().get(pos);
            handleTransportNode(next, cur, i + 1, size, dto);
        }
    }

    private void connectLast(BpmProcess cur, AuditReqDTO dto) {
        BpmProcess template = processInitHandler.init(cur.getProcessType());
        // 从当前节点截断
        cur.setNodes(cur.getNodes().subList(0, cur.getCurrentNodePosition()));
        // 从驳回节点接上后续
        int index = this.getIndexOfRollBackTo(template, dto.getRollbackTo());
        cur.getNodes().addAll(template.getNodes().subList(index, template.getNodes().size() - 1));

        // 初始化后续节点
        cur.readyNextNode();

        // 重新计算定位值
        processInitHandler.initLocation(cur);
    }

    private int getIndexOfRollBackTo(BpmProcess template, Long rollbackTo) {
        int size = template.getNodes().size();
        for (int i = 0; i < size; i++) {
            BpmProcessNode node = template.getNodes().get(i);
            if (rollbackTo.equals(node.getTemplateNodeId())) {
                return i;
            }
        }
        throw new ServiceException("未查询到驳回节点");
    }
}
