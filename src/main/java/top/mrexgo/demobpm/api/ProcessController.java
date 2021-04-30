package top.mrexgo.demobpm.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mrexgo.demobpm.core.service.ProcessService;

/**
 * @author liangjuhong
 * @since 2021/4/30 - 14:37
 */
@RestController
@RequestMapping("/process")
@RequiredArgsConstructor
public class ProcessController {
    
    private final ProcessService processService;
}
