package top.mrexgo.demobpm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.mrexgo.demobpm.common.utils.FormulaUtils;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class DemoBpmApplicationTests {

    @Test
    void testFormulation() {
        String condition = "${days} * 99 > 327";
        Map<String, Integer> param = new HashMap<>();
        param.put("days", 5);
        boolean flag = FormulaUtils.paramCheck(condition, param);
        assert flag;
    }

}
