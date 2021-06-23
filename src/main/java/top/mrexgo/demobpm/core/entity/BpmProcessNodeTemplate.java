package top.mrexgo.demobpm.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.mrexgo.demobpm.common.enums.NodeTypeEnum;

import java.util.List;

/**
 * @author liangjuhong
 * @since 2021/6/18
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BpmProcessNodeTemplate {

    /**
     * 模板节点id
     */
    private Long templateNodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型
     */
    private NodeTypeEnum nodeType;

    /**
     * 条件语句
     * 语法规则待定
     */
    private String conditionStr;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 子节点（可为空）
     */
    private List<BpmProcessNodeTemplate> nodes;
}
