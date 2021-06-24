package top.mrexgo.demobpm.core.assembler;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import top.mrexgo.demobpm.core.entity.BpmProcess;
import top.mrexgo.demobpm.core.entity.BpmProcessNode;
import top.mrexgo.demobpm.core.entity.BpmProcessNodeTemplate;
import top.mrexgo.demobpm.core.entity.BpmProcessTemplate;

@Mapper(
    componentModel = "spring",
    builder = @Builder(disableBuilder = true),
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProcessAssembler {
    BpmProcess fromTemplate(BpmProcessTemplate template);

    BpmProcessNode fromTemplateNode(BpmProcessNodeTemplate node);

    BpmProcessTemplate toTemplate(BpmProcess bpmProcess);

    @Mapping(target = "templateNodeId", source = "nodeId")
    BpmProcessNodeTemplate toTemplate(BpmProcessNode node);
}
