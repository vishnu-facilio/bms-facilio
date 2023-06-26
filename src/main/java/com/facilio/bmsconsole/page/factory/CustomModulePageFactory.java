package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomModulePageFactory extends PageFactory {
	
	public static Page getCustomModulePage(ModuleBaseWithCustomFields record, FacilioModule module) throws Exception {
		Page page = new Page();

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		
		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		List<String> formSubModules = new ArrayList<>();
		
		boolean isAtg = AccountUtil.getCurrentOrg().getOrgId() == 406 && record.getFormId() != -1;
		SummaryOrderType orderType = isAtg ? SummaryOrderType.FORM_SECTION : SummaryOrderType.FORM;
		addSecondaryDetailsWidget(tab1Sec1, record, module, formSubModules, orderType);
		if (record == null) {
			return page;
		}

		Tab tab2 = page.new Tab("Related");

		boolean isRelationshipAdded = addRelationshipSection(page, tab2, record.getModuleId());

		Section tab2Sec1 = getRelatedListSectionObj(page);

		addRelatedListWidgets(tab2Sec1, record.getModuleId(), formSubModules, false);
		
		// Temp - to add seperate pagefactory for employee module
		if( module != null && "people".equalsIgnoreCase(module.getName())) {
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

		Organization organization = AccountUtil.getCurrentOrg();
		if((organization != null) && (organization.getId() == 173l) && ("custom_relatedlistmodule").equalsIgnoreCase(module.getName())) {
			List<FacilioModule> subModules =
					modBean.getSubModules(module.getModuleId(), FacilioModule.ModuleType.BASE_ENTITY,
							FacilioModule.ModuleType.Q_AND_A_RESPONSE,
							FacilioModule.ModuleType.Q_AND_A
					);
			if (CollectionUtils.isNotEmpty(subModules)) {
				for (FacilioModule subModule : subModules) {
					if (subModule.isModuleHidden() || !subModule.getName().equals("custom_summarywidgetmodule")) {
						continue;
					}
					List<FacilioField> allFields = modBean.getAllFields(subModule.getName());
					List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == module.getModuleId())).collect(Collectors.toList());
					if (CollectionUtils.isNotEmpty(fields)) {
						for (FacilioField field : fields) {
							PageWidget relatedListWidget = new PageWidget(PageWidget.WidgetType.NEW_RELATED_LIST);
							JSONObject relatedList = new JSONObject();
							relatedList.put("module", subModule);
							relatedList.put("field", field);
							relatedList.put("summaryWidgetName", FacilioConstants.WidgetNames.MAIN_SUMMARY_WIDGET);
							relatedListWidget.setRelatedList(relatedList);
							relatedListWidget.addToLayoutParams(tab2Sec1, 24, 8);
							tab2Sec1.addWidget(relatedListWidget);
						}
					}
				}
			}
		}

		if(CollectionUtils.isNotEmpty(tab2Sec1.getWidgets())) {
			tab2.addSection(tab2Sec1);
		}
		if(CollectionUtils.isNotEmpty(tab2Sec1.getWidgets()) || isRelationshipAdded) {
			page.addTab(tab2);
		}

		//atre notes widgets renamed to Comments
		HashMap<String, String> titleMap = null;
		if(AccountUtil.getCurrentOrg().getOrgId() == 407l || AccountUtil.getCurrentOrg().getOrgId() == 418l) {
			titleMap = new HashMap<>();
			titleMap.put("notes", "Comment");
			titleMap.put("documents", "Attachment");
		}

		boolean isAtreTenantHideCommentsModules = AccountUtil.getCurrentOrg().getOrgId() == 418l && AccountUtil.getCurrentApp() != null && AccountUtil.getCurrentApp().getLinkName().equals(ApplicationLinkNames.TENANT_PORTAL_APP) && (module.getName().equals("custom_payment") || module.getName().equals("custom_contracts") || module.getName().equals("custom_receipts"));

		//handling notify req for atre custom module - incident management -> temp solution
		if(module.getName().equals("custom_incidentmanagement_1")) {
			addCommonSubModuleWidget(tab1Sec1, module, record, titleMap,true);
		}
		else if(isAtreTenantHideCommentsModules) {
			if(!module.getName().equals("custom_contracts")) {
				PageWidget.WidgetType widgetType = PageWidget.WidgetType.ATTACHMENT;
				addCommonSubModuleWidget(tab1Sec1, module, record, titleMap, false, widgetType);
			}
		}else{
			addCommonSubModuleWidget(tab1Sec1, module, record, titleMap,false);
		}

		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLASSIFICATION)){
			Page.Tab tab3 = page.new Tab("Specification");
			page.addTab(tab3);
			Page.Section tab3Sec1 = page.new Section();
			tab3.addSection(tab3Sec1);
			PageWidget classificationWidget = new PageWidget(PageWidget.WidgetType.CLASSIFICATION);
			classificationWidget.setName("Classification");
			classificationWidget.addToLayoutParams(tab3Sec1, 24, 8);
			classificationWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.SITE_ACTIVITY);
			tab3Sec1.addWidget(classificationWidget);
		}

		ApplicationContext app = AccountUtil.getCurrentApp();

		if (app != null && app.getDomainType() == AppDomain.AppDomainType.FACILIO.getIndex() && module != null && !"serviceRequest".equalsIgnoreCase(module.getName())) {
			 Page.Tab tab3 = page.new Tab("History");;
		        page.addTab(tab3);
		        Page.Section tab4Sec1 = page.new Section();
		        tab3.addSection(tab4Sec1);
		        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
		        activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
		        activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
		        tab4Sec1.addWidget(activityWidget);
		}

		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.FAILURE_CODES)) {
			addFailureReportTab(page);
		}

		List<FacilioModule> timelogModule = modBean.getSubModules(module.getModuleId(), FacilioModule.ModuleType.TIME_LOG);
		AppDomain domain = AccountUtil.getCurrentUser().getAppDomain();
		if (domain != null && domain.getAppDomainTypeEnum() == AppDomain.AppDomainType.FACILIO) {
			if (module.isStateFlowEnabled() && ((CollectionUtils.isNotEmpty(timelogModule)) && (timelogModule.size() > 0))) {
				addTimelogTab(page);
			}
		}
		return page;
	}
	private static void addFailureReportTab(Page page){
		Page.Tab failureReportTab = page.new Tab("Failure Report");
		page.addTab(failureReportTab);

		Page.Section failureReportSection = page.new Section();
		failureReportTab.addSection(failureReportSection);

		PageWidget failureReport = new PageWidget(PageWidget.WidgetType.FAILURE_REPORT);
		failureReport.addToLayoutParams(failureReportSection, 24, 8);
		failureReport.addToWidgetParams("card", "failurereports");
		failureReportSection.addWidget(failureReport);
	}

	private static void addTimelogTab(Page page){
		Page.Tab timeLogTab = page.new Tab("Time Log");
		page.addTab(timeLogTab);

		Page.Section timeLogSection = page.new Section();
		timeLogTab.addSection(timeLogSection);

		PageWidget timeLog = new PageWidget(PageWidget.WidgetType.STATE_TRANSITION_TIME_LOG);
		timeLog.addToLayoutParams(timeLogSection,24,8);
		timeLog.addToWidgetParams("card","timeLog");
		timeLogSection.addWidget(timeLog);

	}

}
