package com.facilio.bmsconsoleV3.signup.routes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.StringField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.v3.context.Constants;

public class AddRouteModules extends SignUpData{
	
	@Override
	public void addData() throws Exception {	    
	    List<FacilioModule> modules = new ArrayList<>();
	    
	    FacilioModule routesModule = constructRoutesModule();
	    modules.add(routesModule);
	    
	    FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
     
        addDefaultFormForRoute(routesModule);
	}
	
	private FacilioModule constructRoutesModule() throws Exception{
		
		FacilioModule module = new FacilioModule(FacilioConstants.Routes.NAME,
				FacilioConstants.Routes.DISPLAY_NAME,
				FacilioConstants.Routes.TABLE_NAME,
                FacilioModule.ModuleType.BASE_ENTITY
                );
		List<FacilioField> fields = new ArrayList<>();

		NumberField siteField = (NumberField) FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER);
        fields.add(siteField);
        
        StringField nameField = (StringField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING,true);
        fields.add(nameField);

		fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));
		
		module.setFields(fields);
		return module;
		
	}		
	
	public void addDefaultFormForRoute(FacilioModule routesModule) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioForm defaultForm = new FacilioForm();
		defaultForm.setName("standard");
		defaultForm.setModule(routesModule);
		defaultForm.setDisplayName("Standard");
		defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
		defaultForm.setShowInWeb(true);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(routesModule.getName()));
		List<FormSection> sections = new ArrayList<FormSection>();
		FormSection section = new FormSection();
		section.setName("Route");
		section.setSectionType(FormSection.SectionType.FIELDS);
		section.setShowLabel(true);
		
		List<FormField> fields = new ArrayList<>();
		int seq = 0;
		fields.add(new FormField(fieldMap.get("name").getFieldId(), "name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, ++seq, 1));
		fields.add(new FormField(fieldMap.get("description").getFieldId(), "description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, ++seq, 1));
		fields.add(new FormField(fieldMap.get("siteId").getFieldId(), "siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, ++seq, 1));
		
		section.setFields(fields);
		section.setSequenceNumber(1);
		sections.add(section);
				
		defaultForm.setSections(sections);
		
		FormsAPI.createForm(defaultForm, routesModule);		
	}
	
}