package com.facilio.bmsconsole.page.factory;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class TenantPageFactory extends PageFactory{
	
	public static Page getTenantPage(ModuleBaseWithCustomFields record) throws Exception {
		Page page = new Page();
		
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		
		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		addSecondaryDetailsWidget(tab1Sec1);
		addTenantSpecialWidget(tab1Sec1);
		if (record == null) {
			return page;
		}

		addRelatedList(tab1Sec1, record.getModuleId());
		addCommonSubModuleGroup(tab1Sec1);
		

		return page;
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
				
				if(subModule.getName().equals(FacilioConstants.ContextNames.TENANT_CONTACT) && !AccountUtil.isFeatureEnabled(FeatureLicense.PEOPLE_CONTACTS)) {
					continue;
				}
				List<FacilioField> allFields = modBean.getAllFields(subModule.getName());
				List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == moduleId)).collect(Collectors.toList());
				if ((CollectionUtils.isNotEmpty(fields)) && (!subModule.getName().equals("tenantspaces"))) {
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
		detailsWidget.addToLayoutParams(section, 24, 4);
		section.addWidget(detailsWidget);
	}

}
