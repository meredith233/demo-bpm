package top.mrexgo.demobpm.core.handler;

import top.mrexgo.demobpm.common.enums.NodeStatusEnum;
import top.mrexgo.demobpm.common.exception.ServiceException;
import top.mrexgo.demobpm.common.utils.FormulaUtils;
import top.mrexgo.demobpm.core.entity.BpmProcess;
import top.mrexgo.demobpm.core.entity.BpmProcessNode;

import java.util.Map;

/**
 * @author liangjuhong
 * @since 2021/6/23
 */
public class ProcessNodeHandler {

    public boolean readyNode(BpmProcess p, BpmProcessNode node, Map<String, Integer> conditionParam) {
        if (!NodeStatusEnum.FUTURE.equals(node.getNodeStatus())) {
            // 初始化状态错误
            throw new ServiceException("下一节点初始化失败");
        }
        switch (node.getNodeType()) {
            case SERIAL:
                // 当前初始化节点为串行节点使仅初始化第一个子节点
                node.setNodeStatus(NodeStatusEnum.WAITING);
                BpmProcessNode first = node.getNodes().get(0);
                readyNode(p, first, conditionParam);
                break;
            case PARALLEL:
            case COUNTERSIGN:
                // 当前初始化节点为并行或会签节点使需初始化所有子节点
                node.setNodeStatus(NodeStatusEnum.WAITING);
                for (BpmProcessNode next : node.getNodes()) {
                    readyNode(p, next, conditionParam);
                }
                break;
            case CONDITION:
                boolean flag = FormulaUtils.paramCheck(node.getConditionStr(), conditionParam);
                if (flag) {
                    node.setNodeStatus(NodeStatusEnum.READY);
                } else {
                    node.setNodeStatus(NodeStatusEnum.SKIP);
                    p.setCurrentNodePosition(p.getCurrentNodePosition() + 1);
                    readyNode(p, p.getCurrentNode(), conditionParam);
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
}
