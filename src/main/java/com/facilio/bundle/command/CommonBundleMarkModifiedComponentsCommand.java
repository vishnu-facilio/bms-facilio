package com.facilio.bundle.command;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

import lombok.extern.log4j.Log4j;

@Log4j
public class CommonBundleMarkModifiedComponentsCommand extends FacilioCommand {

	
	BundleComponentsEnum componentEnum;
	BundleModeEnum mode;
	
	public CommonBundleMarkModifiedComponentsCommand(BundleComponentsEnum componentEnum,BundleModeEnum mode) {
		// TODO Auto-generated constructor stub
		this.componentEnum = componentEnum;
		this.mode = mode;
	}
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		try {
			switch(componentEnum) {
			case MODULE:
				
				FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
				BundleUtil.markModifiedComponent(componentEnum, module.getModuleId(), module.getDisplayName(), mode);
				break;
			case FIELD:
				
				List<Long> fieldIds = (List<Long>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_IDS);
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				for(Long fieldId : fieldIds) {
					
					FacilioField field = modBean.getField(fieldId);
					
					BundleUtil.markModifiedComponent(componentEnum, field.getFieldId(), field.getDisplayName(), mode);
				}
				break;
			case FUNCTION_NAME_SPACE:
				
				WorkflowNamespaceContext workflowNamespaceContext = (WorkflowNamespaceContext) context.get(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT);
				
				BundleUtil.markModifiedComponent(componentEnum, workflowNamespaceContext.getId(), workflowNamespaceContext.getName(), BundleModeEnum.ADD);
				
				break;
			case FUNCTION:
				
				WorkflowUserFunctionContext userFunctionContext = (WorkflowUserFunctionContext) context.get(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT);
				
				BundleUtil.markModifiedComponent(componentEnum, userFunctionContext.getId(), userFunctionContext.getName(), BundleModeEnum.ADD);
				break;
			}
		}
		catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		return false;
	}

}
