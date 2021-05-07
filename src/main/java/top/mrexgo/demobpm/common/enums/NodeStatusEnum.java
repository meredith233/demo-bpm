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
 * @since 2021/5/7 - 15:01
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NodeStatusEnum {

    /**
     * 节点审核状态枚举
     */
    READY(1, "待审批"),
    COMPLETE(2, "审批完成"),
    FUTURE(3, "未到达该审批节点"),
    WAITING(4, "等待子节点审批"),
    SKIP(5, "跳过审批"),
    NO_PASS(6, "不通过"),
    ROLLBACK(7, "驳回"),
    DELETE(99, "已删除");

    @EnumValue
    private Integer value;
    private String label;

    @JsonCreator
    public static NodeStatusEnum fromValue(Object type) {
        if (type instanceof Integer) {
            return Arrays.stream(NodeStatusEnum.values())
                .filter(typeEnum -> typeEnum.getValue().equals(type))
                .findFirst().orElse(null);
        } else if (type instanceof Map) {
            Map baseEnum = (Map) type;
            return Arrays.stream(NodeStatusEnum.values())
                .filter(typeEnum -> typeEnum.getValue().equals(baseEnum.get("value")))
                .findFirst().orElse(null);
        }
        return null;
    }

}
