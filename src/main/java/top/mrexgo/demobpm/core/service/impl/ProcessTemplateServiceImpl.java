package top.mrexgo.demobpm.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mrexgo.demobpm.common.enums.NodeTypeEnum;
import top.mrexgo.demobpm.core.assembler.ProcessAssembler;
import top.mrexgo.demobpm.core.entity.BpmProcess;
import top.mrexgo.demobpm.core.entity.BpmProcessNode;
import top.mrexgo.demobpm.core.entity.BpmProcessTemplate;
import top.mrexgo.demobpm.core.handler.ProcessInitHandler;
import top.mrexgo.demobpm.core.service.ProcessTemplateService;
import top.mrexgo.demobpm.persistent.dao.ProcessMongoDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangjuhong
 * @since 2021-06-18
 */
@Service
@RequiredArgsConstructor
public class ProcessTemplateServiceImpl implements ProcessTemplateService {

    private final ProcessMongoDAO dao;
    private final ProcessInitHandler initHandler;
    private final ProcessAssembler assembler;

    private void addTemplate() {
        BpmProcess bpmProcess = new BpmProcess();
        bpmProcess.setProcessType(1).setName("模板流程");
        List<BpmProcessNode> nodes = new ArrayList<>();
        nodes.add(BpmProcessNode.builder().nodeName("开始节点").nodeType(NodeTypeEnum.START).build());
        nodes.add(BpmProcessNode.builder().nodeName("节点1").nodeType(NodeTypeEnum.NORMAL).build());
        nodes.add(BpmProcessNode.builder().nodeName("节点2").nodeType(NodeTypeEnum.NORMAL).build());
        nodes.add(BpmProcessNode.builder().nodeName("条件节点1").conditionStr("${days} > 3").nodeType(NodeTypeEnum.CONDITION).build());
        // 子节点有一个审核通过即通过
        nodes.add(BpmProcessNode.builder().nodeName("并行节点1").nodeType(NodeTypeEnum.PARALLEL).nodes(new ArrayList<BpmProcessNode>() {{
            add(BpmProcessNode.builder().nodeName("并行1节点1").nodeType(NodeTypeEnum.NORMAL).build());
            add(BpmProcessNode.builder().nodeName("并行1节点2").nodeType(NodeTypeEnum.NORMAL).build());
            add(BpmProcessNode.builder().nodeName("并行1串行1").nodeType(NodeTypeEnum.SERIAL).nodes(new ArrayList<BpmProcessNode>() {{
                add(BpmProcessNode.builder().nodeName("并行1串行1节点1").nodeType(NodeTypeEnum.NORMAL).build());
                add(BpmProcessNode.builder().nodeName("并行1串行1节点2").nodeType(NodeTypeEnum.NORMAL).build());
            }}).build());
        }}).build());
        // 所有子节点通过才通过
        nodes.add(BpmProcessNode.builder().nodeName("会签节点1").nodeType(NodeTypeEnum.COUNTERSIGN).nodes(new ArrayList<BpmProcessNode>() {{
            add(BpmProcessNode.builder().nodeName("会签1节点1").nodeType(NodeTypeEnum.NORMAL).build());
            add(BpmProcessNode.builder().nodeName("会签1节点2").nodeType(NodeTypeEnum.NORMAL).build());
        }}).build());
        nodes.add(BpmProcessNode.builder().nodeName("结束节点").nodeType(NodeTypeEnum.END).build());
        bpmProcess.setNodes(nodes);

        initHandler.initLocation(bpmProcess);

        BpmProcessTemplate template = assembler.toTemplate(bpmProcess);
        template.calAllNeedFinish();
        template.initStatus();
        dao.saveProcessTemplate(template);
    }
}
