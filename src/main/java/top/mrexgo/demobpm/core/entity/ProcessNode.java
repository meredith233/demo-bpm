package top.mrexgo.demobpm.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.MongoId;
import top.mrexgo.demobpm.common.enums.NodeStatusEnum;
import top.mrexgo.demobpm.common.enums.NodeTypeEnum;

import java.io.Serializable;
import java.util.List;

/**
 * @author mrexgo
 * @since 2021/4/30 - 14:09
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProcessNode implements Serializable {

    @MongoId
    private Long nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型
     */
    private NodeTypeEnum nodeType;

    /**
     * 节点审批状态
     */
    private NodeStatusEnum nodeStatus;

    /**
     * 审批备注
     */
    private String auditMsg;

    /**
     * 条件语句
     * 语法规则待定
     */
    private String condition;

    /**
     * 以下两个参数仅 条件节点 有效
     * 已审核完成子节点
     */
    private Integer finished;

    /**
     * 需要审核子节点数
     */
    private Integer allNeedFinish;

    /**
     * 节点定位值
     */
    private String location;

    /**
     * 子节点（可为空）
     */
    private List<ProcessNode> nodes;
}
