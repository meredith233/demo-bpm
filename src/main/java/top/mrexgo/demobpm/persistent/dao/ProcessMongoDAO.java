package top.mrexgo.demobpm.persistent.dao;

import top.mrexgo.demobpm.core.entity.Process;

/**
 * @author liangjuhong
 * @since 2021/4/30 - 16:21
 */
public interface ProcessMongoDAO {
    void saveProcess(Process process);

    Process getProcess(Long processId);
}
