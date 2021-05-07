package top.mrexgo.demobpm.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Map;

/**
 * @author liangjuhong
 * @since 2021/5/7 - 15:42
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AuditTypeEnum {
    /**
     * 审核类型枚举
     */
    PASS(1, "审核通过"),
    NO_PASS(2, "审核不通过"),
    ROLLBACK(3, "驳回"),
    TRANSPORT(4, "转代理审批"),
    RE_LUNCH(5, "重新发起");

    @EnumValue
    private Integer value;
    private String label;

    @JsonCreator
    public static AuditTypeEnum fromValue(Object type) {
        if (type instanceof Integer) {
            return Arrays.stream(AuditTypeEnum.values())
                .filter(typeEnum -> typeEnum.getValue().equals(type))
                .findFirst().orElse(null);
        } else if (type instanceof Map) {
            Map baseEnum = (Map) type;
            return Arrays.stream(AuditTypeEnum.values())
                .filter(typeEnum -> typeEnum.getValue().equals(baseEnum.get("value")))
                .findFirst().orElse(null);
        }
        return null;
    }
}
