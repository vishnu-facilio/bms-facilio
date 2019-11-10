package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.context.VisitorTypeContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class FetchVisitorTypeSettingsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// Add Picklist entry for visitor and insert that ID Into table
		FacilioModule visitorSettingsModule=ModuleFactory.getVisitorSettingsModule();
		List<FacilioField> visitorSettingsFields=FieldFactory.getVisitorSettingsFields();
		VisitorTypeContext visitorTypeCtx=(VisitorTypeContext)context.get(ContextNames.VISITOR_TYPE_PICKLIST_OPTION);		
		long visitorTypeId=visitorTypeCtx.getId();
		
		GenericSelectRecordBuilder selectBuilder=new GenericSelectRecordBuilder();
		selectBuilder.table(visitorSettingsModule.getTableName()).
		select(visitorSettingsFields).
		andCondition(CriteriaAPI.getCondition("VISITOR_TYPE_ID", "visitorTypeId", visitorTypeId+"", NumberOperators.EQUALS));
		Map<String,Object> visitorSetting= selectBuilder.get().get(0);
		
		VisitorSettingsContext visitorSettingsContext=FieldUtil.getAsBeanFromMap(visitorSetting, VisitorSettingsContext.class);
		//get and fill visitortype in context
		FacilioChain pickListChain = FacilioChainFactory.getPickListChain();
		FacilioContext pickListContext =pickListChain.getContext();
		pickListContext.put(ContextNames.MODULE_NAME, "visitorType");
		pickListChain.execute();
		
		Map<Long, String> pickListMap=(Map<Long, String>) pickListContext.get(FacilioConstants.ContextNames.PICKLIST);
		String visitorTypeName=pickListMap.get(visitorTypeId);
		visitorTypeCtx.setName(visitorTypeName);
		
		visitorSettingsContext.setVisitorType(visitorTypeCtx);
        
		Long formId=visitorSettingsContext.getVisitorFormId();
		
		//get and fill form object in context		
		
		FacilioChain formChain = FacilioChainFactory.getFormMetaChain();
		FacilioContext formContext=formChain.getContext();
			
		formContext.put(FacilioConstants.ContextNames.MODULE_NAME, ContextNames.VISITOR);
		
		if(formId>0)
		{
			formContext.put(FacilioConstants.ContextNames.FORM_ID, formId);

			
		}
		else {
			formContext.put(FacilioConstants.ContextNames.FORM_ID, formId);

			formContext.put(FacilioConstants.ContextNames.FORM_NAME, visitorTypeName);
		}
		
		formChain.execute();
		FacilioForm form = (FacilioForm) formContext.get(FacilioConstants.ContextNames.FORM);
		
		visitorSettingsContext.setForm(form);
		
		
		context.put(ContextNames.VISITOR_SETTINGS,visitorSettingsContext);
		
		return false;
		
	}

}
