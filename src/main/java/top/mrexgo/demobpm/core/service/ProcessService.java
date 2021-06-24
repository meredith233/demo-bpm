package top.mrexgo.demobpm.core.service;

import top.mrexgo.demobpm.core.dto.AuditReqDTO;
import top.mrexgo.demobpm.core.entity.BpmProcess;

/**
 * @author liangjuhong
 * @since 2021/4/30 - 14:38
 */
public interface ProcessService {

    /**
     * 开始流程
     *
     * @return 流程id
     */
    Long startProcess(Integer type);

    void audit(AuditReqDTO dto);

    BpmProcess listAuditNodes(Long id);
}
