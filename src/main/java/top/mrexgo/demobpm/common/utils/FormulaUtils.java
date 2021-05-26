package top.mrexgo.demobpm.common.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.mrexgo.demobpm.common.enums.FormulatorNodeTypeEnum;
import top.mrexgo.demobpm.common.exception.ServiceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author liangjuhong
 * @since 2021/5/25
 **/
public class FormulaUtils {

    private static final List<Character> VALID_OPERATORS;

    static {
        List<Character> temp = new ArrayList<>();
        temp.add('+');
        temp.add('-');
        temp.add('<');
        temp.add('>');
        temp.add('=');
        VALID_OPERATORS = Collections.unmodifiableList(temp);
    }

    public boolean validCheck(String condition) {
        Formulator formulator = new Formulator(condition);
        return formulator.checkValid();
    }

    public boolean paramCheck(String condition, Map<String, Integer> param) {

        return false;
    }

    @NoArgsConstructor
    @Data
    private class Formulator {

        private String condition;

        private List<Node> nodeList = new ArrayList<>();

        public Formulator(String condition) {
            this.condition = condition.trim();
        }

        private void init() {
            this.condition = condition.trim();
            int len = this.condition.length();
            for (int i = 0; i < len; i++) {
                char c = this.condition.charAt(i);
                if ('0' <= c && c <= '9') {
                    int temp = c;
                    int j = i + 1;
                    while (j < len) {
                        c = this.condition.charAt(j);
                        if ('0' <= c && c <= '9') {
                            temp *= 10;
                            temp += c;
                        } else {
                            break;
                        }
                    }
                    nodeList.add(Node.builder().nodeType(FormulatorNodeTypeEnum.NUMBER).value(temp).build());
                    i = j - 1;
                } else if (c == '$') {
                    if (i + 1 >= len || this.condition.charAt(i + 1) != '{') {
                        throw new ServiceException("非法参数表达式");
                    }
                    int j = i + 2;
                    while (j < len && this.condition.charAt(j) != '}') {
                        j++;
                    }
                    if (this.condition.charAt(j) != '}') {
                        throw new ServiceException("非法参数表达式");
                    }
                    nodeList.add(Node.builder().nodeType(FormulatorNodeTypeEnum.PARAM)
                        .paramKey(this.condition.substring(i, j)).build());
                    i = j + 1;
                } else if (VALID_OPERATORS.contains(c)) {
                    nodeList.add(Node.builder().nodeType(FormulatorNodeTypeEnum.OPERATOR)
                        .operator(c).build());
                } else {
                    throw new ServiceException("非法字符");
                }
            }
        }

        public boolean checkValid() {
            try {
                init();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        private class Node {

            private FormulatorNodeTypeEnum nodeType;

            private String paramKey;

            private Integer value;

            private Character operator;

        }
    }
}
