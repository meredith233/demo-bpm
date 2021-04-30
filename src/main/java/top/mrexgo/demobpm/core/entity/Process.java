package top.mrexgo.demobpm.core.entity;

import lombok.Data;

import java.util.List;

/**
 * @author mrexgo
 * @since 2021/4/30 - 14:08
 */
@Data
public class Process {
    
    private Long processId;
    
    private String name;
    
    private Integer processType;
    
    private List<ProcessNode> nodes;
}
