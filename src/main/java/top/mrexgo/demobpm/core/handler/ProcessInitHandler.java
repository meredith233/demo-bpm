package top.mrexgo.demobpm.core.handler;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
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
    private final ProcessNodeHandler processNodeHandler;

    public BpmProcess init(Integer type) {
        return doInit(type);
    }

    /**
     * 创建一个简单流程
     */
    private BpmProcess doInit(Integer type) {


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

    /**
     * 将流程的开始节点设置为完成，并ready下一节点，剩余节点状态置为future
     *
     * @param process
     */
    public void initStatus(BpmProcess process) {

    }
}
