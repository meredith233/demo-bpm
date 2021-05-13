package top.mrexgo.demobpm.persistent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author liangjuhong
 * @since 2021/5/13 - 11:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("bpm_template")
public class ProcessNodePO extends BasePO {

    private Long processId;

    private Long parentNodeId;

    private String nodeName;

    private Integer nodeType;

    private String conditionStr;

    private Integer allNeedFinish;

    private String location;

    private Integer sort;
}
