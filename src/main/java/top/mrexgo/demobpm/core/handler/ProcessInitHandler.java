package top.mrexgo.demobpm.core.handler;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import top.mrexgo.demobpm.common.enums.NodeStatusEnum;
import top.mrexgo.demobpm.common.enums.NodeTypeEnum;
import top.mrexgo.demobpm.common.utils.Base32Utils;
import top.mrexgo.demobpm.core.entity.BpmProcess;
import top.mrexgo.demobpm.core.entity.BpmProcessNode;
import top.mrexgo.demobpm.persistent.dao.ProcessMongoDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangjuhong
 * @since 2021/6/18
 * 提供初始化流程相关逻辑
 */
@Component
@RequiredArgsConstructor
public class ProcessInitHandler {

    private final ProcessMongoDAO mongoDAO;
    private final Snowflake snowflake;

    public BpmProcess init(Integer type) {
        return doInit(type);
    }

    /**
     * 创建一个简单流程
     */
    private BpmProcess doInit(Integer type) {
        BpmProcess bpmProcess = new BpmProcess();
        bpmProcess.setProcessType(1).setName("模板流程").setCurrentNodePosition(1).setStatus(NodeStatusEnum.WAITING);
        List<BpmProcessNode> nodes = new ArrayList<>();
        nodes.add(BpmProcessNode.builder().nodeName("开始节点").nodeStatus(NodeStatusEnum.COMPLETE).nodeType(NodeTypeEnum.START).build());
        nodes.add(BpmProcessNode.builder().nodeName("节点1").nodeStatus(NodeStatusEnum.READY).nodeType(NodeTypeEnum.NORMAL).build());
        nodes.add(BpmProcessNode.builder().nodeName("节点2").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.NORMAL).build());
        nodes.add(BpmProcessNode.builder().nodeName("条件节点1").nodeStatus(NodeStatusEnum.FUTURE).conditionStr("${days} > 3").nodeType(NodeTypeEnum.CONDITION).build());
        // 子节点有一个审核通过即通过
        nodes.add(BpmProcessNode.builder().nodeName("并行节点1").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.PARALLEL).nodes(new ArrayList<BpmProcessNode>() {{
            add(BpmProcessNode.builder().nodeName("并行1节点1").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.NORMAL).build());
            add(BpmProcessNode.builder().nodeName("并行1节点2").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.NORMAL).build());
            add(BpmProcessNode.builder().nodeName("并行1串行1").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.SERIAL).nodes(new ArrayList<BpmProcessNode>() {{
                add(BpmProcessNode.builder().nodeName("并行1串行1节点1").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.NORMAL).build());
                add(BpmProcessNode.builder().nodeName("并行1串行1节点2").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.NORMAL).build());
            }}).build());
        }}).build());
        // 所有子节点通过才通过
        nodes.add(BpmProcessNode.builder().nodeName("会签节点1").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.COUNTERSIGN).nodes(new ArrayList<BpmProcessNode>() {{
            add(BpmProcessNode.builder().nodeName("会签1节点1").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.NORMAL).build());
            add(BpmProcessNode.builder().nodeName("会签1节点2").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.NORMAL).build());
        }}).build());
        nodes.add(BpmProcessNode.builder().nodeName("结束节点").nodeStatus(NodeStatusEnum.FUTURE).nodeType(NodeTypeEnum.END).build());
        bpmProcess.setNodes(nodes);

        initLocation(bpmProcess);
        calAllNeedFinish(bpmProcess);
        return bpmProcess;
    }

    private void calAllNeedFinish(BpmProcess bpmProcess) {
        for (BpmProcessNode node : bpmProcess.getNodes()) {
            calAllNeedFinish(node);
        }
    }

    private void calAllNeedFinish(BpmProcessNode bpmProcessNode) {
        switch (bpmProcessNode.getNodeType()) {
            case SERIAL:
            case COUNTERSIGN:
                bpmProcessNode.setAllNeedFinish(bpmProcessNode.getNodes().size());
                for (BpmProcessNode next : bpmProcessNode.getNodes()) {
                    calAllNeedFinish(next);
                }
                break;
            case PARALLEL:
                bpmProcessNode.setAllNeedFinish(1);
                for (BpmProcessNode next : bpmProcessNode.getNodes()) {
                    calAllNeedFinish(next);
                }
                break;
            default:
        }
    }

    public void initLocation(BpmProcess bpmProcess) {
        List<Integer> loc = new ArrayList<>();
        initLocation(bpmProcess.getNodes(), loc);
    }

    private void initLocation(List<BpmProcessNode> nodes, List<Integer> loc) {
        for (int i = 0; i < nodes.size(); i++) {
            loc.add(i);
            BpmProcessNode node = nodes.get(i);
            List<Integer> newLoc = new ArrayList<>(loc);
            node.setLocation(Base32Utils.base32ToString(JSONUtil.toJsonStr(newLoc)));
            node.setNodeId(snowflake.nextId());
            node.setFinished(0);
            if (CollectionUtils.isNotEmpty(node.getNodes())) {
                initLocation(node.getNodes(), newLoc);
            }
            loc.remove(loc.size() - 1);
        }
    }
}
