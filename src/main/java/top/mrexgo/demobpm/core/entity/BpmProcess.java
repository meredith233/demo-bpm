package top.mrexgo.demobpm.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.MongoId;
import top.mrexgo.demobpm.common.annotation.IncKey;
import top.mrexgo.demobpm.common.enums.NodeStatusEnum;
import top.mrexgo.demobpm.common.exception.ServiceException;
import top.mrexgo.demobpm.common.utils.FormulaUtils;

import java.util.List;
import java.util.Map;

/**
 * @author mrexgo
 * @since 2021/4/30 - 14:08
 * 一个流程类相当于一个串行流程节点
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BpmProcess {

    @IncKey
    @MongoId
    private Long processId;

    /**
     * 模板流程id
     */
    private Long templateProcessId;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 流程类型
     */
    private Integer processType;

    /**
     * 流程状态
     */
    private NodeStatusEnum status;

    /**
     * 当前流程待审核节点位置
     */
    private Integer currentNodePosition;

    /**
     * 流程节点
     * 这个流程类相当于一个串行节点，这样可以少套一层
     */
    private List<BpmProcessNode> nodes;

    /**
     * 用于条件节点的参数
     */
    private Map<String, Integer> conditionParam;

    /**
     * 当前待审核人
     */
    private List<Long> currentAuditor;

    public BpmProcessNode getCurrentNode() {
        return nodes.get(currentNodePosition);
    }

    public boolean readyNextNode() {
        this.setCurrentNodePosition(this.getCurrentNodePosition() + 1);
        return doReadyNode(getCurrentNode());
    }

    public boolean doReadyNode(BpmProcessNode node) {
        if (!NodeStatusEnum.FUTURE.equals(node.getNodeStatus())) {
            // 初始化状态错误
            throw new ServiceException("下一节点初始化失败");
        }
        switch (node.getNodeType()) {
            case SERIAL:
                // 当前初始化节点为串行节点使仅初始化第一个子节点
                node.setNodeStatus(NodeStatusEnum.WAITING);
                BpmProcessNode first = node.getNodes().get(0);
                doReadyNode(first);
                break;
            case PARALLEL:
            case COUNTERSIGN:
                // 当前初始化节点为并行或会签节点使需初始化所有子节点
                node.setNodeStatus(NodeStatusEnum.WAITING);
                for (BpmProcessNode next : node.getNodes()) {
                    doReadyNode(next);
                }
                break;
            case CONDITION:
                boolean flag = FormulaUtils.paramCheck(node.getConditionStr(), conditionParam);
                if (flag) {
                    node.setNodeStatus(NodeStatusEnum.READY);
                } else {
                    node.setNodeStatus(NodeStatusEnum.SKIP);
                    this.setCurrentNodePosition(this.getCurrentNodePosition() + 1);
                    doReadyNode(this.getCurrentNode());
                }
                break;
            case NORMAL:
                node.setNodeStatus(NodeStatusEnum.READY);
                break;
            case END:
                node.setNodeStatus(NodeStatusEnum.COMPLETE);
                return true;
            default:
        }
        return false;
    }

    public void initStatus() {
        this.setStatus(NodeStatusEnum.WAITING);
        for (BpmProcessNode node : this.getNodes()) {
            initStatus(node);
        }
    }

    private void initStatus(BpmProcessNode node) {
        switch (node.getNodeType()) {
            case START:
                node.setNodeStatus(NodeStatusEnum.COMPLETE);
                break;
            case CONDITION:
            case NORMAL:
            case END:
                node.setNodeStatus(NodeStatusEnum.FUTURE);
                break;
            case PARALLEL:
            case SERIAL:
            case COUNTERSIGN:
                node.setNodeStatus(NodeStatusEnum.FUTURE);
                for (BpmProcessNode child : node.getNodes()) {
                    initStatus(child);
                }
                break;
            default:
        }
    }
}
