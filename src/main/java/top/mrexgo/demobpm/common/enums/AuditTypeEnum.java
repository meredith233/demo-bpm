package top.mrexgo.demobpm.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author liangjuhong
 * @since 2021/5/7 - 15:42
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum AuditTypeEnum {
    /**
     * 审核类型枚举
     */
    PASS(1, "审核通过"),
    NO_PASS(2, "审核不通过"),
    ROLLBACK(3, "驳回"),
    TRANSPORT(4, "转代理审批"),
    RE_LUNCH(5, "重新发起");

    private Integer key;
    private String value;
}
