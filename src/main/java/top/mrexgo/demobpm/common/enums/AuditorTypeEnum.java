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
public enum AuditorTypeEnum {
    /**
     * 审核人类型枚举
     */
    PASS(1, "指定人"),
    POSITION(2, "职务"),
    ORGANIZATION(3, "部门");

    private Integer value;
    private String label;

    @JsonCreator
    public static AuditorTypeEnum fromValue(Object type) {
        if (type instanceof Integer) {
            return Arrays.stream(AuditorTypeEnum.values())
                .filter(typeEnum -> typeEnum.getValue().equals(type))
                .findFirst().orElse(null);
        } else if (type instanceof Map) {
            Map baseEnum = (Map) type;
            return Arrays.stream(AuditorTypeEnum.values())
                .filter(typeEnum -> typeEnum.getValue().equals(baseEnum.get("value")))
                .findFirst().orElse(null);
        }
        return null;
    }
}
