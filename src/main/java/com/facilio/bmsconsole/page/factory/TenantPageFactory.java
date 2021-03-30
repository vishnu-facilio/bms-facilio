package com.facilio.bmsconsole.page.factory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.modules.FieldFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class TenantPageFactory extends PageFactory{
	
	public static Page getTenantPage(ModuleBaseWithCustomFields record, FacilioModule module) throws Exception {
		Page page = new Page();
		
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		
		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		addSecondaryDetailsWidget(tab1Sec1);
		addTenantSpecialWidget(tab1Sec1);
		addCommonSubModuleWidget(tab1Sec1, module, record);

		if (record == null) {
			return page;
		}

		Tab tab2 = page.new Tab("related records");
		page.addTab(tab2);

		Section tab2Sec1 = page.new Section();
		tab2.addSection(tab2Sec1);

		if(AccountUtil.getCurrentOrg().getOrgId() == 321l) {
			addSpecialRelatedListWidgetForDemoOrg(tab2Sec1, record.getModuleId());
		}
		addRelatedList(tab2Sec1, record.getModuleId());

		return page;
	}

	private static void addSpecialRelatedListWidgetForDemoOrg(Section section, long moduleId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule custom_module_utility = modBean.getModule("custom_tenantutility");
		FacilioModule custom_module_billing = modBean.getModule("custom_tenantbilling");

		List<FacilioField> allFields = modBean.getAllFields(custom_module_utility.getName());
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(allFields);

		PageWidget relatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
		JSONObject relatedList = new JSONObject();
		relatedList.put("module", custom_module_utility);
		relatedList.put("field", fieldsAsMap.get("tenant"));
		relatedListWidget.setRelatedList(relatedList);
		relatedListWidget.addToLayoutParams(section, 24, 8);
		section.addWidget(relatedListWidget);

		List<FacilioField> billFields = modBean.getAllFields(custom_module_billing.getName());
		Map<String, FacilioField> billFieldsAsMap = FieldFactory.getAsMap(billFields);

		PageWidget billRelatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
		JSONObject billRelatedList = new JSONObject();
		billRelatedList.put("module", custom_module_billing);
		billRelatedList.put("field", billFieldsAsMap.get("tenanttobebilled"));
		billRelatedListWidget.setRelatedList(billRelatedList);
		billRelatedListWidget.addToLayoutParams(section, 24, 8);
		section.addWidget(billRelatedListWidget);
	}


	private static void addRelatedList(Section section, long moduleId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> subModules =
				modBean.getSubModules(moduleId, FacilioModule.ModuleType.BASE_ENTITY);

		if (CollectionUtils.isNotEmpty(subModules)) {
			for (FacilioModule subModule : subModules) {
				if(subModule.getName().equals(FacilioConstants.ContextNames.CONTACT) && AccountUtil.isFeatureEnabled(FeatureLicense.PEOPLE_CONTACTS)) {
					continue;
				}
				if(AccountUtil.getCurrentOrg().getOrgId() == 321l && (subModule.getName().equals("custom_tenantutility") || subModule.getName().equals("custom_tenantbilling"))) {
					continue;
				}
				
				if(subModule.getName().equals(FacilioConstants.ContextNames.TENANT_CONTACT) && !AccountUtil.isFeatureEnabled(FeatureLicense.PEOPLE_CONTACTS)) {
					continue;
				}
				List<FacilioField> allFields = modBean.getAllFields(subModule.getName());
				List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == moduleId)).collect(Collectors.toList());
				if ((CollectionUtils.isNotEmpty(fields)) && (!subModule.getName().equals("tenantunit")) && (!subModule.getName().equals("tenantspaces"))) {
					for (FacilioField field : fields) {
						PageWidget relatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
						JSONObject relatedList = new JSONObject();
						relatedList.put("module", subModule);
						relatedList.put("field", field);
						relatedListWidget.setRelatedList(relatedList);
						relatedListWidget.addToLayoutParams(section, 24, 8);
						section.addWidget(relatedListWidget);
					}
				}
			}
		}
	}

	private static void addTenantSpecialWidget(Section section) {
		PageWidget specialWidget = new PageWidget(WidgetType.TENANT_SPECIAL_WIDGET);
		specialWidget.addToLayoutParams(section, 24, 8);
		section.addWidget(specialWidget);
	}
	private static void addSecondaryDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 7);
		section.addWidget(detailsWidget);
	}

}
