package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

import java.util.List;

public class AddOrUpdateStateTransitionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		StateflowTransitionContext stateTransition = (StateflowTransitionContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		if (stateTransition != null) {
			FacilioChain chain;
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if(stateTransition.getQrFieldId() > 0){
				FacilioField qrField = modBean.getField(stateTransition.getQrFieldId());
				validateQrField(modBean,stateTransition.getModuleName(),qrField,stateTransition);
				stateTransition.setQrField(qrField);
			}

			if(stateTransition.getLocationFieldId() > 0){
				FacilioField locationField = modBean.getField(stateTransition.getLocationFieldId());
				validateLocationField(locationField,modBean,stateTransition);
				stateTransition.setLocationField(locationField);

				FacilioUtil.throwIllegalArgumentException(stateTransition.getRadius() == null,"radius cannot be empty");
			}

			if (stateTransition.getId() < 0) {
				chain = TransactionChainFactory.addWorkflowRuleChain();
			} 
			else {
				chain = TransactionChainFactory.updateWorkflowRuleChain();
			}
			chain.execute(context);
		}
		return false;
	}

	public void validateQrField(ModuleBean modBean,String stateTransitionModuleName,FacilioField qrField,StateflowTransitionContext stateTransition) throws  Exception{
		if(!(qrField instanceof LookupField) && modBean.getModule(stateTransitionModuleName).getExtendedModuleIds().contains(qrField.getModuleId())) {
			if ( qrField.getDataTypeEnum() != FieldType.STRING ) {
				throw new IllegalArgumentException("Invalid field type");
			}
		}else{
			FacilioField qrLookupField = modBean.getField(stateTransition.getQrLookupFieldId(),((LookupField) qrField).getLookupModuleId());
			boolean checkLookupQr = false;
			List<FacilioField> fields = modBean.getAllFields(stateTransitionModuleName);
			for(FacilioField field:fields){
				if (field.getDataTypeEnum() == FieldType.LOOKUP){
					if(((LookupField) field).getLookupModule().getExtendedModuleIds().contains((qrLookupField).getModuleId())){
						checkLookupQr = true;
						break;
					}
				}
			}
			if (checkLookupQr) {
				if (qrLookupField.getDataTypeEnum() != FieldType.STRING) {
					throw new IllegalArgumentException("Invalid field type");
				}
			}else{
				throw new Exception("Qr field is invalid");
			}
		}
	}

	public void validateLocationField(FacilioField locationField,ModuleBean moduleBean,StateflowTransitionContext stateTransition) throws Exception {
		if(locationField instanceof LookupField){
			if(stateTransition.getLocationLookupFieldId()>0){
				locationField = moduleBean.getField(stateTransition.getLocationLookupFieldId(),((LookupField) locationField).getLookupModule().getModuleId());
			}
		}
		FacilioUtil.throwIllegalArgumentException(locationField == null,"Location field cannot be empty ");

		if((locationField.getDataTypeEnum() != FieldType.STRING || !(locationField.getDisplayType().equals(FacilioField.FieldDisplayType.GEO_LOCATION))) && (locationField.getDataTypeEnum() != FieldType.LOOKUP || !((LookupField) locationField).getLookupModule().equals(moduleBean.getModule(FacilioConstants.ContextNames.LOCATION)))){
			throw new IllegalArgumentException("Invalid field type");
		}

	}

}
