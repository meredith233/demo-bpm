package top.mrexgo.demobpm.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.MongoId;
import top.mrexgo.demobpm.common.annotation.IncKey;

import java.util.List;

/**
 * @author liangjuhong
 * @since 2021/6/18
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BpmProcessTemplate {

    /**
     * 模板流程id
     */
    @MongoId
    @IncKey
    private Long templateProcessId;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 流程类型
     */
    private Integer processType;

    /**
     * 流程节点
     * 这个流程类相当于一个串行节点，这样可以少套一层
     */
    private List<BpmProcessNodeTemplate> nodes;
}
