package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.context.VisitorTypeContext;
import com.facilio.bmsconsole.context.VisitorTypeFormsContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
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
		
		Boolean fetchAll = (Boolean)context.get(ContextNames.FETCH_ALL);		
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
		//get and fill visitortypeCtx in Visitor Setting context
		
		ApplicationContext app = ApplicationApi.getApplicationForLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		long appId = app.getId();
		if(AccountUtil.getCurrentApp() != null && AccountUtil.getCurrentApp().getId() > 0) {
			appId = AccountUtil.getCurrentApp().getId();
		}
		
		selectBuilder=new GenericSelectRecordBuilder();
		selectBuilder.table(ModuleFactory.getVisitorTypeFormsModule().getTableName()).
		select(FieldFactory.getVisitorTypeFormsFields()).
		andCondition(CriteriaAPI.getCondition("VISITOR_TYPE_ID", "visitorTypeId", visitorTypeId+"", NumberOperators.EQUALS));
		if(fetchAll == null || fetchAll.equals(Boolean.FALSE)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", appId+"", NumberOperators.EQUALS));
		}
		List<Map<String,Object>> visitorTypeFormList = selectBuilder.get();
		Map<String,Object> visitorTypeForm = null;
		if(!CollectionUtils.isEmpty(visitorTypeFormList)) {
			visitorTypeForm = visitorTypeFormList.get(0);
		}
		
		VisitorTypeFormsContext visitorTypeFormContext = FieldUtil.getAsBeanFromMap(visitorTypeForm, VisitorTypeFormsContext.class);
		
		FacilioChain pickListChain = ReadOnlyChainFactory.fetchVisitorTypePicklistData();
		FacilioContext pickListContext =pickListChain.getContext();
		pickListContext.put(FacilioConstants.ContextNames.RECORD, visitorTypeCtx);
		pickListContext.put(FacilioConstants.ContextNames.ID, visitorTypeCtx.getId());
		
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(visitorTypeCtx));

		pickListChain.execute();
		
		VisitorTypeContext visitorTypeCtxFilled=(VisitorTypeContext)pickListContext.get(FacilioConstants.ContextNames.RECORD);
		visitorSettingsContext.setVisitorType(visitorTypeCtxFilled);
		
		visitorSettingsContext.setVisitorLogFormId(visitorTypeFormContext.getVisitorLogFormId());
		if(visitorTypeFormContext.getVisitorLogFormId() > 0) {
			FacilioForm visitorLogForm = FormsAPI.getFormFromDB(visitorTypeFormContext.getVisitorLogFormId());		
			visitorSettingsContext.setVisitorLogForm(visitorLogForm);
		}
		
		visitorSettingsContext.setVisitorInviteFormId(visitorTypeFormContext.getVisitorInviteFormId());
		if(visitorTypeFormContext.getVisitorInviteFormId() > 0) {
			FacilioForm visitorInviteForm = FormsAPI.getFormFromDB(visitorTypeFormContext.getVisitorInviteFormId());		
			visitorSettingsContext.setVisitorInviteForm(visitorInviteForm);
		}
		
		context.put(ContextNames.VISITOR_SETTINGS,visitorSettingsContext);
		
		List<FacilioForm> allForms = new ArrayList<FacilioForm>();
		if(fetchAll != null && fetchAll.equals(Boolean.TRUE)) {
			for(Map<String,Object> visitorType:visitorTypeFormList) {
				VisitorTypeFormsContext visitorTypeContext = FieldUtil.getAsBeanFromMap(visitorType, VisitorTypeFormsContext.class);
				if(visitorTypeContext.getVisitorLogFormId() > 0) {
					FacilioForm form = FormsAPI.getFormFromDB(visitorTypeContext.getVisitorLogFormId());		
					allForms.add(form);
				}
				
				if(visitorTypeContext.getVisitorInviteFormId() > 0) {
					FacilioForm form = FormsAPI.getFormFromDB(visitorTypeContext.getVisitorInviteFormId());		
					allForms.add(form);
				}
			}
		}
		
		context.put(ContextNames.FORMS_LIST,allForms);
		return false;
		
	}

}
