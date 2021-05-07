package top.mrexgo.demobpm.persistent.dao;

import top.mrexgo.demobpm.core.entity.BpmProcess;

/**
 * @author liangjuhong
 * @since 2021/4/30 - 16:21
 */
public interface ProcessMongoDAO {
    void saveProcess(BpmProcess bpmProcess);

    BpmProcess getProcess(Long processId);
}
