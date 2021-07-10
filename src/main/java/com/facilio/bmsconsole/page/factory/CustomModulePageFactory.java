package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.forms.FormSection.SectionType;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomModulePageFactory extends PageFactory {
	
	public static Page getCustomModulePage(ModuleBaseWithCustomFields record, FacilioModule module) throws Exception {
		Page page = new Page();
		
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		
		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		List<String> formSubModules = new ArrayList<>();
		addSecondaryDetailsWidget(tab1Sec1, record, module, formSubModules);
		if (record == null) {
			return page;
		}

		Tab tab2 = page.new Tab("related list");

		Section tab2Sec1 = page.new Section();
		tab2.addSection(tab2Sec1);
		addRelatedListWidgets(tab2Sec1, record.getModuleId(), formSubModules, false);
		
		// Temp - to add seperate pagefactory for employee module
		if( module != null && "people".equalsIgnoreCase(module.getName())) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule bookingModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
	        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
	        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
         	List<Long> Ids = new ArrayList<>();
         	Ids.add(record.getId());
         	PageWidget relatedListWidget = new PageWidget(PageWidget.WidgetType.BOOKINGS_RELATED_LIST);
            JSONObject relatedList = new JSONObject();
            relatedList.put("module", bookingModule);
            relatedList.put("field", fieldsAsMap.get(FacilioConstants.ContextNames.FacilityBooking.RESERVED_FOR));
            relatedList.put("values", Ids);
            relatedListWidget.setRelatedList(relatedList);
            relatedListWidget.addToLayoutParams(tab2Sec1, 24, 10);
            tab2Sec1.addWidget(relatedListWidget);
        
        }
		
		if(CollectionUtils.isNotEmpty(tab2Sec1.getWidgets())) {
			page.addTab(tab2);
		}

		//atre notes widgets renamed to Comments
		HashMap<String, String> titleMap = null;
		if(AccountUtil.getCurrentOrg().getOrgId() == 407l || AccountUtil.getCurrentOrg().getOrgId() == 418l) {
			titleMap = new HashMap<>();
			titleMap.put("notes", "Comment");
			titleMap.put("documents", "Attachment");
		}

		//handling notify req for atre custom module - incident management -> temp solution
		if(module.getName().equals("custom_incidentmanagement_1")) {
			addCommonSubModuleWidget(tab1Sec1, module, record, titleMap,true);
		}
		else {
			addCommonSubModuleWidget(tab1Sec1, module, record, titleMap,false);
		}

		ApplicationContext app = AccountUtil.getCurrentApp();
		if (app != null && app.getLinkName().equals(ApplicationLinkNames.FACILIO_MAIN_APP) && module != null && !"serviceRequest".equalsIgnoreCase(module.getName())) {
			 Page.Tab tab3 = page.new Tab("Activity");
		        page.addTab(tab3);
		        Page.Section tab4Sec1 = page.new Section();
		        tab3.addSection(tab4Sec1);
		        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
		        activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
		        activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
		        tab4Sec1.addWidget(activityWidget);
		}
		
		return page;
	}

	private static void addSecondaryDetailsWidget(Section section, ModuleBaseWithCustomFields record, FacilioModule module, List<String> formRelModules) throws Exception {
		
		if (AccountUtil.getCurrentOrg().getOrgId() == 406) {
			if (record.getFormId() != -1) {
				long moduleId = module.getModuleId();
				FacilioForm form = fetchForm(record.getFormId(), module.getName());
				List<FormSection> sections = form.getSections();
				for (FormSection formSection: sections) {
					if (formSection.getName() == null) {
						continue;
					}
					if (formSection.getSectionTypeEnum() == SectionType.SUB_FORM) {
						String moduleName = formSection.getSubForm().getModule().getName();
						formRelModules.add(moduleName);
						addRelatedListWidgets(section, moduleId, Collections.singletonList(moduleName), true);
					}
					else {
						PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
						detailsWidget.addToLayoutParams(section, 24, 7);
						detailsWidget.addToWidgetParams("formSectionId", formSection.getId());
						section.addWidget(detailsWidget);
					}
				}
			}
		}
		else {
			PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
			detailsWidget.addToLayoutParams(section, 24, 7);
			section.addWidget(detailsWidget);
		}
		
	}

	private static FacilioForm fetchForm(long formId, String moduleName) throws Exception {
		FacilioChain chain = FacilioChainFactory.getFormMetaChain();
		Context formContext = chain.getContext();

		formContext.put(FacilioConstants.ContextNames.FORM_ID, formId);
		formContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		chain.execute();

		return (FacilioForm) formContext.get(FacilioConstants.ContextNames.FORM);
	}

}
