package top.mrexgo.demobpm.persistent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.mrexgo.demobpm.common.enums.AuditorTypeEnum;

import java.util.List;

/**
 * @author liangjuhong
 * @since 2021/5/26
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("bpm_template_auditor")
public class AuditorPO extends BasePO {

    private Long processId;

    private Long processNodeId;

    private AuditorTypeEnum auditorType;

    private List<Long> auditorIds;
}
