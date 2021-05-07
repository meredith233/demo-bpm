package top.mrexgo.demobpm.core.service;

import top.mrexgo.demobpm.core.dto.AuditReqDTO;
import top.mrexgo.demobpm.core.entity.BpmProcess;

/**
 * @author liangjuhong
 * @since 2021/4/30 - 14:38
 */
public interface ProcessService {
    void startProcess();

    void audit(AuditReqDTO dto);

    BpmProcess listAuditNodes(Long id);
}
