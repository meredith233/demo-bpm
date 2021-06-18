package top.mrexgo.demobpm.core.assembler;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    builder = @Builder(disableBuilder = true),
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public class ProcessAssembler {
}
