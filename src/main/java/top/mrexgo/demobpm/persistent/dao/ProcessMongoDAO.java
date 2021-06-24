package top.mrexgo.demobpm.persistent.dao;

import top.mrexgo.demobpm.core.entity.BpmProcess;
import top.mrexgo.demobpm.core.entity.BpmProcessTemplate;

/**
 * @author liangjuhong
 * @since 2021/4/30 - 16:21
 */
public interface ProcessMongoDAO {
    void saveProcess(BpmProcess bpmProcess);

    void saveProcessTemplate(BpmProcessTemplate bpmProcessTemplate);

    BpmProcessTemplate getProcessTemplateByType(Integer type);

    BpmProcess getProcess(Long processId);
}
