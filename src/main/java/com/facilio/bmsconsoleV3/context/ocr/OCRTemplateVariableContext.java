package com.facilio.bmsconsoleV3.context.ocr;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import com.facilio.workflows.conditions.context.WorkflowCondition;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OCRTemplateVariableContext extends V3Context {

	OCRTemplateContext template;
	String constantValue;
	long workflowId;
	String name;
	
	WorkflowContext workflow;
	
	private TemplateVariableTypeEnum type;
    
    public TemplateVariableTypeEnum getTypeEnum() {
    	return type;
    }

    public Integer getType() {
        if (type == null) {
            return null;
        }
        return type.getIndex();
    }
    public void setType(Integer type) {
        if (type != null) {
            this.type = TemplateVariableTypeEnum.valueOf(type);
        } else {
            this.type = null;
        }
    }
	
	@AllArgsConstructor
    public static enum TemplateVariableTypeEnum implements FacilioIntEnum {
        CONSTANT("Key Value") {
			@Override
			public Object execute(OCRTemplateVariableContext variable) {
				// TODO Auto-generated method stub
				return variable.getConstantValue();
			}
		},
        SCRIPT("Tables") {
			@Override
			public Object execute(OCRTemplateVariableContext variable) throws Exception {
				// TODO Auto-generated method stub
				
				FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
				
				FacilioContext context = chain.getContext();
				
				context.put(WorkflowV2Util.WORKFLOW_CONTEXT, variable.getWorkflow());
				
				chain.execute();
				
				return variable.getWorkflow().getReturnValue();
			}
		},
        ;
        public int getVal() {
            return ordinal() + 1;
        }
        String name;
        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }
        private static final TemplateVariableTypeEnum[] RULE_TYPES = TemplateVariableTypeEnum.values();
        public static TemplateVariableTypeEnum valueOf(int type) {
            if (type > 0 && type <= RULE_TYPES.length) {
                return RULE_TYPES[type - 1];
            }
            return null;
        }

        public abstract Object execute(OCRTemplateVariableContext variable) throws Exception;
    }
}
