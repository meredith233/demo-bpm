package top.mrexgo.demobpm.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.stereotype.Service;
import top.mrexgo.demobpm.common.enums.NodeStatusEnum;
import top.mrexgo.demobpm.common.enums.NodeTypeEnum;
import top.mrexgo.demobpm.core.dto.AuditReqDTO;
import top.mrexgo.demobpm.core.entity.Process;
import top.mrexgo.demobpm.core.entity.ProcessNode;
import top.mrexgo.demobpm.core.service.ProcessService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangjuhong
 * @since 2021/4/30 - 14:39
 */
@Service
public class ProcessServiceImpl implements ProcessService {

    private Process p;

    @Override
    public void startProcess() {
        this.init();
    }

    @Override
    public void audit(AuditReqDTO dto) {
        if (CollectionUtils.isEmpty(dto.getProcessNodeLocation())) {
            // 没有定位信息，报错
            throw new RuntimeException("没有节点定位信息");
        }
        if (!dto.getProcessNodeLocation().get(0).equals(p.getCurrentNode())) {
            // 节点定位错误
            throw new RuntimeException("节点定位信息有误");
        }
        ProcessNode cur = p.getNodes().get(dto.getProcessNodeLocation().get(0));

        switch (dto.getAuditType()) {
            case PASS:
                boolean passFlag = this.handleSuccessNode(cur, 1, dto.getProcessNodeLocation().size(), dto);
                if (passFlag) {
                    p.setCurrentNode(p.getCurrentNode() + 1);
                    // todo 预处理下一节点
                }
                break;
            case NO_PASS:
                break;
            case ROLLBACK:
                break;
            case TRANSPORT:
                break;
            case RE_LUNCH:
                break;
            default:
        }


        // 审核通过后，对下一节点进行预处理
    }

    /**
     * 处理审批通过
     * 审核节点需要普通节点
     *
     * @param cur     当前节点
     * @param posSign 节点相对位置
     * @param size    定位列表大小
     * @param dto     审批信息
     */
    private boolean handleSuccessNode(ProcessNode cur, int posSign, int size, AuditReqDTO dto) {
        if (posSign == size) {
            // 到达最终节点位置
            if (!NodeTypeEnum.NORMAL.equals(cur.getNodeType()) || !dto.getProcessNodeId().equals(cur.getNodeId())) {
                // 未到达叶子节点
                throw new RuntimeException("审核节点错误");
            }
            cur.setNodeStatus(NodeStatusEnum.COMPLETE).setAuditMsg(dto.getAuditMsg()).setFinished(1);
            return true;
        } else {
            int pos = dto.getProcessNodeLocation().get(posSign);
            ProcessNode next = cur.getNodes().get(pos);
            boolean passFlag = handleSuccessNode(next, posSign + 1, size, dto);

            // 下级节点审批通过后处理
            if (passFlag) {
                cur.setFinished(cur.getFinished() + 1);
                return cur.getFinished().equals(cur.getAllNeedFinish());
            }
            return false;
        }
    }

    // 创建一个简单流程
    private void init() {
        Process process = new Process();
        process.setProcessType(1);
        process.setName("模板流程");
        process.setProcessId(1L);
        process.setCurrentNode(1);
        List<ProcessNode> nodes = new ArrayList<>();
        nodes.add(ProcessNode.builder().nodeId(1L).nodeName("开始节点").nodeStatus(NodeStatusEnum.COMPLETE).nodeType(NodeTypeEnum.START).build());
        nodes.add(ProcessNode.builder().nodeId(2L).nodeName("节点1").nodeStatus(NodeStatusEnum.READY).nodeType(NodeTypeEnum.NORMAL).build());
        nodes.add(ProcessNode.builder().nodeId(3L).nodeName("节点2").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.NORMAL).build());
        // 子节点有一个审核通过即通过
        nodes.add(ProcessNode.builder().nodeId(4L).nodeName("并行节点1").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.PARALLEL).allNeedFinish(1).nodes(new ArrayList<ProcessNode>() {{
            add(ProcessNode.builder().nodeId(41L).nodeName("并行1节点1").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.NORMAL).build());
            add(ProcessNode.builder().nodeId(42L).nodeName("并行1节点2").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.NORMAL).build());
            add(ProcessNode.builder().nodeId(43L).nodeName("并行1串行1").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.SERIAL).nodes(new ArrayList<ProcessNode>() {{
                add(ProcessNode.builder().nodeId(51L).nodeName("并行1串行1节点1").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.NORMAL).build());
                add(ProcessNode.builder().nodeId(52L).nodeName("并行1串行1节点2").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.NORMAL).build());
            }}).build());
        }}).build());
        // 所有子节点通过才通过
        nodes.add(ProcessNode.builder().nodeId(5L).nodeName("会签节点1").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.COUNTERSIGN).allNeedFinish(2).nodes(new ArrayList<ProcessNode>() {{
            add(ProcessNode.builder().nodeId(51L).nodeName("会签1节点1").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.NORMAL).build());
            add(ProcessNode.builder().nodeId(52L).nodeName("会签1节点2").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.NORMAL).build());
        }}).build());
        nodes.add(ProcessNode.builder().nodeId(99L).nodeName("结束节点").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.END).build());
        process.setNodes(nodes);
        p = process;
    }
}
