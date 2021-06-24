package top.mrexgo.demobpm.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.MongoId;
import top.mrexgo.demobpm.common.annotation.IncKey;
import top.mrexgo.demobpm.common.enums.NodeStatusEnum;

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
    @Indexed
    private Integer processType;

    /**
     * 流程状态
     */
    private NodeStatusEnum status = NodeStatusEnum.WAITING;

    /**
     * 当前流程待审核节点位置
     */
    private Integer currentNodePosition = 1;

    /**
     * 流程节点
     * 这个流程类相当于一个串行节点，这样可以少套一层
     */
    private List<BpmProcessNodeTemplate> nodes;

    public void calAllNeedFinish() {
        for (BpmProcessNodeTemplate node : this.getNodes()) {
            calAllNeedFinish(node);
        }
    }

    private void calAllNeedFinish(BpmProcessNodeTemplate bpmProcessNode) {
        switch (bpmProcessNode.getNodeType()) {
            case SERIAL:
            case COUNTERSIGN:
                bpmProcessNode.setAllNeedFinish(bpmProcessNode.getNodes().size());
                for (BpmProcessNodeTemplate next : bpmProcessNode.getNodes()) {
                    calAllNeedFinish(next);
                }
                break;
            case PARALLEL:
                bpmProcessNode.setAllNeedFinish(1);
                for (BpmProcessNodeTemplate next : bpmProcessNode.getNodes()) {
                    calAllNeedFinish(next);
                }
                break;
            default:
        }
    }
}
