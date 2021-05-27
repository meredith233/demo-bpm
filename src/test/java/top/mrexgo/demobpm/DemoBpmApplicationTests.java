package top.mrexgo.demobpm;

import cn.hutool.core.map.MapUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.mrexgo.demobpm.common.enums.AuditTypeEnum;
import top.mrexgo.demobpm.common.utils.FormulaUtils;
import top.mrexgo.demobpm.core.dto.AuditReqDTO;
import top.mrexgo.demobpm.core.entity.BpmProcess;
import top.mrexgo.demobpm.core.service.ProcessService;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class DemoBpmApplicationTests {

    @Autowired
    private ProcessService processService;

    @Test
    void testFormulation() {
        String condition = "${days} * 99 > 327";
        Map<String, Integer> param = new HashMap<>();
        param.put("days", 5);
        boolean flag = FormulaUtils.paramCheck(condition, param);
        assert flag;
    }

    @Test
    void testSimpleProcess() {
        Long processId = processService.startProcess();
        BpmProcess p = processService.listAuditNodes(processId);
        processService.audit(new AuditReqDTO().setProcessId(processId).setAuditMsg("通过")
            .setAuditType(AuditTypeEnum.PASS)
            .setProcessNodeId(p.getNodes().get(0).getNodeId())
            .setLocation(p.getNodes().get(0).getLocation()));
        p = processService.listAuditNodes(processId);
        processService.audit(new AuditReqDTO().setProcessId(processId).setAuditMsg("通过")
            .setAuditType(AuditTypeEnum.PASS)
            .setProcessNodeId(p.getNodes().get(0).getNodeId())
            .setConditionParam(MapUtil.of("days", 5))
            .setLocation(p.getNodes().get(0).getLocation()));
        p = processService.listAuditNodes(processId);
        processService.audit(new AuditReqDTO().setProcessId(processId).setAuditMsg("通过")
            .setAuditType(AuditTypeEnum.PASS)
            .setProcessNodeId(p.getNodes().get(0).getNodeId())
            .setLocation(p.getNodes().get(0).getLocation()));
    }

}
