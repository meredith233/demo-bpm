package top.mrexgo.demobpm.core.handler;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import top.mrexgo.demobpm.common.utils.Base32Utils;
import top.mrexgo.demobpm.core.assembler.ProcessAssembler;
import top.mrexgo.demobpm.core.entity.BpmProcess;
import top.mrexgo.demobpm.core.entity.BpmProcessNode;
import top.mrexgo.demobpm.core.entity.BpmProcessTemplate;
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
    private final ProcessAssembler assembler;

    public BpmProcess init(Integer type) {
        return doInit(mongoDAO.getProcessTemplateByType(type));
    }

    /**
     * 创建一个简单流程
     */
    private BpmProcess doInit(BpmProcessTemplate template) {
        return assembler.fromTemplate(template);
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
