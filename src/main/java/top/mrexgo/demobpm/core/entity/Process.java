package top.mrexgo.demobpm.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.MongoId;
import top.mrexgo.demobpm.common.enums.NodeStatusEnum;

import java.util.List;

/**
 * @author mrexgo
 * @since 2021/4/30 - 14:08
 * 一个流程类相当于一个串行流程节点
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Process {

    @MongoId
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
     * 流程状态
     */
    private NodeStatusEnum status;

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
