package top.mrexgo.demobpm.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author liangjuhong
 * @since 2021/5/26
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Auditor implements Serializable {

    private Long templateNodeId;

    /**
     * 审核人
     */
    private List<Long> auditorIds;

    /**
     * 审核人职位/部门/机构
     */
    private List<Long> auditorDeptIds;
}
