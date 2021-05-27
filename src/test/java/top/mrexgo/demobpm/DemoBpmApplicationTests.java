package top.mrexgo.demobpm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.mrexgo.demobpm.common.utils.FormulaUtils;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class DemoBpmApplicationTests {

    @Test
    void contextLoads() {
        String content = "${days} > 3";
        Map<String, Integer> param = new HashMap<>();
        param.put("days", 5);
        boolean flag = FormulaUtils.paramCheck(content, param);
        System.out.println(flag);
    }

}
