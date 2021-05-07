package top.mrexgo.demobpm.core.entity;

import lombok.Data;

import java.util.List;

/**
 * @author mrexgo
 * @since 2021/4/30 - 14:08
 */
@Data
public class Process {

    private Long processId;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 流程类型
     */
    private Integer processType;

    /**
     * 当前流程待审核节点位置
     */
    private Integer currentNode;

    /**
     * 流程节点
     * 这个流程类相当于一个串行节点，这样可以少套一层
     */
    private List<ProcessNode> nodes;
}
