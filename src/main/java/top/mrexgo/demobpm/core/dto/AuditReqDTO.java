package top.mrexgo.demobpm.core.dto;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.mrexgo.demobpm.common.enums.AuditTypeEnum;
import top.mrexgo.demobpm.common.utils.Base32Utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
     * 转发审批时使用，当前人是否需要还审批
     */
    private Boolean needAuditFlag;

    /**
     * 驳回时使用
     * 驳回到指定节点
     */
    private Integer rollbackTo;

    /**
     * 用于条件审核节点的参数
     */
    private Map<String, Integer> conditionParam;

    /**
     * 当前审核节点定位值
     */
    private String location;
    private List<Integer> processNodeLocation;

    public void initLocation() {
        String s = Base32Utils.base32Decode(location);
        processNodeLocation = JSONUtil.toList(s, Integer.class);
    }
}
