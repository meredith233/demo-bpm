package top.mrexgo.demobpm.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.mrexgo.demobpm.common.enums.AuditTypeEnum;

import java.io.Serializable;
import java.util.List;

/**
 * @author liangjuhong
 * @since 2021/5/7 - 15:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditReqDTO implements Serializable {

    private Long processId;

    private Long processNodeId;

    /**
     * 审批备注
     */
    private String auditMsg;

    /**
     * 审批类型
     */
    private AuditTypeEnum auditType;

    /**
     * 转发审批时使用
     * 转发给指定人
     */
    private Long transportTo;

    /**
     * 驳回时使用
     * 驳回到指定节点
     */
    private Integer rollbackTo;

    /**
     * 当前审核节点定位值
     */
    private List<Integer> processNodeLocation;
}
