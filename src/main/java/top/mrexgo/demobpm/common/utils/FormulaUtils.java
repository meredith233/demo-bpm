package top.mrexgo.demobpm.common.utils;

import java.util.Map;

/**
 * @author liangjuhong
 * @since 2021/5/25
 **/
public class FormulaUtils {

    public static boolean validCheck(String condition) {
        Formulator formulator = new Formulator(condition);
        return formulator.checkValid();
    }

    public static boolean paramCheck(String condition, Map<String, Integer> param) {
        Formulator formulator = new Formulator(condition);
        return formulator.paramCheck(param);
    }
}
