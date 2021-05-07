package top.mrexgo.demobpm.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mrexgo.demobpm.core.dto.AuditReqDTO;
import top.mrexgo.demobpm.core.entity.BpmProcess;
import top.mrexgo.demobpm.core.entity.BpmProcessNode;
import top.mrexgo.demobpm.core.service.ProcessService;

import java.util.List;

/**
 * @author liangjuhong
 * @since 2021/4/30 - 14:37
 */
@RestController
@RequestMapping("/process")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;

    @GetMapping
    public void start() {
        processService.startProcess();
    }

    @PostMapping("/audit")
    public void audit(@RequestBody AuditReqDTO dto) {
        processService.audit(dto);
    }

    @GetMapping("/{id}")
    public BpmProcess listAuditNodes(@PathVariable("id") Long id) {
        return processService.listAuditNodes(id);
    }

    @GetMapping("/items")
    public List<BpmProcessNode> listFirstLevelNodes() {
        return null;
    }
}
