package top.mrexgo.demobpm.common.utils;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import top.mrexgo.demobpm.common.enums.FormulatorNodeTypeEnum;
import top.mrexgo.demobpm.common.exception.ServiceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author liangjuhong
 * @since 2021/5/27 - 11:49
 */
@AllArgsConstructor
@NoArgsConstructor
public class Formulator {

    private static final List<Character> VALID_OPERATORS;
    private static final List<Character> JUDGE_OPERATORS;

    static {
        List<Character> temp = new ArrayList<>();
        temp.add('+');
        temp.add('-');
        temp.add('<');
        temp.add('>');
        temp.add('=');
        VALID_OPERATORS = Collections.unmodifiableList(temp);

        temp = new ArrayList<>();
        temp.add('<');
        temp.add('>');
        temp.add('=');
        JUDGE_OPERATORS = Collections.unmodifiableList(temp);
    }

    private final List<FormulaNode> nodeList = new ArrayList<>();
    private String condition;
    private Map<String, Integer> param;

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
                nodeList.add(FormulaNode.builder().nodeType(FormulatorNodeTypeEnum.NUMBER).value(temp).build());
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
                nodeList.add(FormulaNode.builder().nodeType(FormulatorNodeTypeEnum.PARAM)
                    .paramKey(this.condition.substring(i, j)).build());
                i = j + 1;
            } else if (VALID_OPERATORS.contains(c)) {
                nodeList.add(FormulaNode.builder().nodeType(FormulatorNodeTypeEnum.OPERATOR)
                    .operator(c).build());
            } else {
                throw new ServiceException("非法字符");
            }
        }
        if (CollectionUtils.isEmpty(nodeList)) {
            throw new ServiceException("节点列表为空");
        }
        long cnt = nodeList.stream()
            .filter(i -> FormulatorNodeTypeEnum.OPERATOR.equals(i.getNodeType()))
            .map(FormulaNode::getOperator).filter(JUDGE_OPERATORS::contains).count();
        if (cnt != 1) {
            throw new ServiceException("判断运算符只能有一个");
        }
    }

    private boolean simpleTest() {
        int size = nodeList.size();
        int splitSign = 0;
        for (splitSign = 0; splitSign < size; splitSign++) {
            FormulaNode cur = nodeList.get(splitSign);
            if (FormulatorNodeTypeEnum.OPERATOR.equals(cur.getNodeType())
                && JUDGE_OPERATORS.contains(cur.getOperator())) {
                break;
            }
        }
        int left = testCal(0, splitSign);
        int right = testCal(splitSign + 1, size);
        FormulaNode split = nodeList.get(splitSign);
        boolean flag;
        switch (split.getOperator()) {
            case '>':
                flag = left > right;
                break;
            case '<':
                flag = left < right;
                break;
            case '=':
                flag = left == right;
                break;
            default:
                throw new ServiceException("非法判断字符");
        }
        return flag;
    }

    /**
     * 左闭右开计算
     *
     * @param left  left
     * @param right right
     * @return ans
     */
    private int testCal(int left, int right) {
        Deque<Integer> stack = new LinkedList<>();
        char preSign = '+';
        int num = 0;
        for (int i = left; i < right; i++) {
            FormulaNode cur = nodeList.get(i);
            if (FormulatorNodeTypeEnum.OPERATOR.equals(cur.getNodeType())) {
                if (i - 1 < 0) {
                    throw new ServiceException("非法表达式");
                }
                FormulaNode pre = nodeList.get(i - 1);
                if (FormulatorNodeTypeEnum.NUMBER.equals(pre.getNodeType())) {
                    num = pre.getValue();
                } else if (FormulatorNodeTypeEnum.PARAM.equals(pre.getNodeType())) {
                    num = 1;
                } else {
                    throw new ServiceException("非法表达式");
                }
                switch (preSign) {
                    case '+':
                        stack.push(num);
                        break;
                    case '-':
                        stack.push(-num);
                        break;
                    case '*':
                        stack.push(stack.pop() * num);
                        break;
                    case '/':
                        stack.push(stack.pop() / num);
                        break;
                    default:
                }
                preSign = cur.getOperator();
            }
        }
        int ans = 0;
        while (!stack.isEmpty()) {
            ans += stack.pop();
        }
        return ans;
    }

    public boolean checkValid() {
        try {
            init();
            simpleTest();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean paramCheck(Map<String, Integer> param) {
        this.param = param;
        init();
        return paramTest();
    }

    private boolean paramTest() {
        int size = nodeList.size();
        int splitSign = 0;
        for (splitSign = 0; splitSign < size; splitSign++) {
            FormulaNode cur = nodeList.get(splitSign);
            if (FormulatorNodeTypeEnum.OPERATOR.equals(cur.getNodeType())
                && JUDGE_OPERATORS.contains(cur.getOperator())) {
                break;
            }
        }
        int left = paramCal(0, splitSign);
        int right = paramCal(splitSign + 1, size);
        FormulaNode split = nodeList.get(splitSign);
        boolean flag;
        switch (split.getOperator()) {
            case '>':
                flag = left > right;
                break;
            case '<':
                flag = left < right;
                break;
            case '=':
                flag = left == right;
                break;
            default:
                throw new ServiceException("非法判断字符");
        }
        return flag;
    }

    private int paramCal(int left, int right) {
        Deque<Integer> stack = new LinkedList<>();
        char preSign = '+';
        int num = 0;
        for (int i = left; i < right; i++) {
            FormulaNode cur = nodeList.get(i);
            if (FormulatorNodeTypeEnum.OPERATOR.equals(cur.getNodeType())) {
                if (i - 1 < 0) {
                    throw new ServiceException("非法表达式");
                }
                FormulaNode pre = nodeList.get(i - 1);
                if (FormulatorNodeTypeEnum.NUMBER.equals(pre.getNodeType())) {
                    num = pre.getValue();
                } else if (FormulatorNodeTypeEnum.PARAM.equals(pre.getNodeType())) {
                    Integer temp = param.get(pre.getParamKey());
                    if (temp == null) {
                        throw new ServiceException("参数{" + pre.getParamKey() + "}不存在");
                    }
                    num = temp;
                } else {
                    throw new ServiceException("非法表达式");
                }
                switch (preSign) {
                    case '+':
                        stack.push(num);
                        break;
                    case '-':
                        stack.push(-num);
                        break;
                    case '*':
                        stack.push(stack.pop() * num);
                        break;
                    case '/':
                        stack.push(stack.pop() / num);
                        break;
                    default:
                }
                preSign = cur.getOperator();
            }
        }
        int ans = 0;
        while (!stack.isEmpty()) {
            ans += stack.pop();
        }
        return ans;
    }
}
