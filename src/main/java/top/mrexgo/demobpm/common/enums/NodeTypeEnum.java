package top.mrexgo.demobpm.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author liangjuhong
 * @since 2021/5/7 - 14:50
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum NodeTypeEnum {
    /**
     * 流程节点类型枚举
     */
    START(1, "开始节点"),
    SERIAL(2, "串行节点"),
    PARALLEL(3, "并行节点"),
    COUNTERSIGN(4, "会签节点"),
    CONDITION(5, "条件节点"),
    NORMAL(10, "普通节点"),
    END(99, "结束节点");

    private Integer key;
    private String value;
}
