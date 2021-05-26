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
 * @since 2021/5/26
 **/
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FormulatorNodeTypeEnum {
    /**
     * 匹配器节点类型枚举
     */
    NUMBER(1, "数字"),
    PARAM(2, "参数"),
    OPERATOR(3, "运算符");

    private Integer value;
    private String label;

    @JsonCreator
    public static FormulatorNodeTypeEnum fromValue(Object type) {
        if (type instanceof Integer) {
            return Arrays.stream(FormulatorNodeTypeEnum.values())
                .filter(typeEnum -> typeEnum.getValue().equals(type))
                .findFirst().orElse(null);
        } else if (type instanceof Map) {
            Map baseEnum = (Map) type;
            return Arrays.stream(FormulatorNodeTypeEnum.values())
                .filter(typeEnum -> typeEnum.getValue().equals(baseEnum.get("value")))
                .findFirst().orElse(null);
        }
        return null;
    }
}
