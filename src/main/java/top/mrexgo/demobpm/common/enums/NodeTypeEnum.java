package top.mrexgo.demobpm.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Map;

/**
 * @author liangjuhong
 * @since 2021/5/7 - 14:50
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NodeTypeEnum {
    /**
     * 流程节点类型枚举
     */
    START(1, "开始节点"),
    SERIAL(2, "串行节点"),
    PARALLEL(3, "并行节点"),
    COUNTERSIGN(4, "会签节点"),
    // 条件节点与普通节点没有子节点
    CONDITION(5, "条件节点"),
    NORMAL(10, "普通节点"),
    END(99, "结束节点");

    private Integer value;
    private String label;

    @JsonCreator
    public static NodeTypeEnum fromValue(Object type) {
        if (type instanceof Integer) {
            return Arrays.stream(NodeTypeEnum.values())
                .filter(typeEnum -> typeEnum.getValue().equals(type))
                .findFirst().orElse(null);
        } else if (type instanceof Map) {
            Map baseEnum = (Map) type;
            return Arrays.stream(NodeTypeEnum.values())
                .filter(typeEnum -> typeEnum.getValue().equals(baseEnum.get("value")))
                .findFirst().orElse(null);
        }
        return null;
    }
}
