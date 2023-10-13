package com.facilio.flows.context;

import com.facilio.flowengine.enums.FlowVariableDataType;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.jgrapht.alg.interfaces.FlowAlgorithm;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class FlowContext implements Serializable {

    private long id;
    private String name;
    private String description;
    private long moduleId = -1;
    private long createdBy = -1;
    private long modifiedBy = -1;

    private long sysCreatedTime = -1;
    private long sysModifiedTime = -1;

    private List<ParameterContext> parameters;
    private String returnVariableName;
    private FlowVariableDataType returnVariableDataType;

    public FlowVariableDataType getReturnVariableDataType() {
        return returnVariableDataType;
    }

    public void setReturnVariableDataType(FlowVariableDataType returnVariableDataType) {
        this.returnVariableDataType = returnVariableDataType;
    }
    public void setReturnVariableDataType(String returnVariableDataType) {
        this.returnVariableDataType = FlowVariableDataType.valueOf(returnVariableDataType);
    }

    private FlowType flowType;
    public int getFlowType() {
        if (flowType == null) {
            return -1;
        }
        return flowType.getIndex();
    }

    public void setFlowType(FlowType flowType) {
        this.flowType = flowType;
    }

    public FlowType getFlowTypeEnum() {
        return flowType;
    }

    public void setFlowType(int flowType) {
        this.flowType = FlowType.valueOf(flowType);
    }

    @JsonIgnore
    public String getModuleName() throws Exception {
        if(moduleId!=-1l){
            FacilioModule module = Constants.getModBean().getModule(moduleId);
           if(module!=null){
               return module.getName();
           }
        }
        return null;
    }
    @JsonIgnore
    public String getModuleDisplayName() throws Exception {
        if(moduleId!=-1l){
            FacilioModule module = Constants.getModBean().getModule(moduleId);
            if(module!=null){
                return module.getDisplayName();
            }
        }
        return null;
    }

}
