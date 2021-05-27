package top.mrexgo.demobpm.common.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.mrexgo.demobpm.common.enums.FormulatorNodeTypeEnum;

import java.io.Serializable;

/**
 * @author liangjuhong
 * @since 2021/5/27 - 11:42
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormulaNode implements Serializable {

    private FormulatorNodeTypeEnum nodeType;

    private String paramKey;

    private Integer value;

    private Character operator;
}
