package top.mrexgo.demobpm.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author mrexgo
 * @since 2021/4/30 - 14:09
 */
@Data
public class ProcessNode implements Serializable {
    
    private Long nodeId;
    
    private String nodeName;
    
    private Integer nodeType;
    
    private Integer nodeStatus;
    
    private String auditMsg;
    
    private List<ProcessNode> nodes;
}
