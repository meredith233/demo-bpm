package top.mrexgo.demobpm.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.mrexgo.demobpm.common.enums.NodeStatusEnum;

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
@EqualsAndHashCode(callSuper = false)
public class BpmProcessNode extends BpmProcessNodeTemplate {

    private Long nodeId;

    /**
     * 节点审批状态
     */
    private NodeStatusEnum nodeStatus;

    /**
     * 审批备注
     */
    private String auditMsg;

    /**
     * 以下两个参数仅 有子节点的节点 有效
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
    private List<BpmProcessNode> nodes;
}
