package top.mrexgo.demobpm.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author liangjuhong
 * @since 2021/5/7 - 15:01
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum NodeStatusEnum {

    /**
     * 节点审核状态枚举
     */
    READY(1, "待审批"),
    COMPLETE(2, "审批完成"),
    FUTURE(3, "未到达该审批节点"),
    WAITING(4, "等待子节点审批"),
    SKIP(5, "跳过审批"),
    NO_PASS(6, "不通过");

    private Integer key;
    private String value;
}
