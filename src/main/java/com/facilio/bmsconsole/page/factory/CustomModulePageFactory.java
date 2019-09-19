package com.facilio.bmsconsole.page.factory;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class CustomModulePageFactory extends PageFactory {
	
	public static Page getCustomModulePage(ModuleBaseWithCustomFields record) throws Exception {
		Page page = new Page();
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		
		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		addSecondaryDetailsWidget(tab1Sec1);
		addCommonSubModuleGroup(tab1Sec1);
//		addRelatedListWidget(tab1Sec1);
//		addRelatedListWidget1(tab1Sec1);

		addRelatedList(tab1Sec1, record.getModuleId());

		return page;
	}

	private static void addRelatedList(Section section, long moduleId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> subModules =
				modBean.getSubModules(moduleId, FacilioModule.ModuleType.CUSTOM, FacilioModule.ModuleType.BASE_ENTITY);

		if (CollectionUtils.isNotEmpty(subModules)) {
			for (FacilioModule subModule : subModules) {
				List<FacilioField> allFields = modBean.getAllFields(subModule.getName());
				List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == moduleId)).collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(fields)) {
					PageWidget relatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
					JSONObject relatedList = new JSONObject();
					relatedList.put("module", subModule);
					relatedList.put("field", fields.get(0));
					relatedListWidget.setRelatedList(relatedList);
					relatedListWidget.addToLayoutParams(section, 24, 5);
					section.addWidget(relatedListWidget);
				}
			}
		}
	}

	private static void addSecondaryDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(detailsWidget);
	}
	
//	private static void addRelatedListWidget(Section section) {
//		PageWidget relatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
//		JSONObject relatedList = new JSONObject();
//		relatedList.put("moduleName", "students");
//		relatedList.put("fieldName", "Lookup");
//		relatedListWidget.setRelatedList(relatedList);
//		relatedListWidget.addToLayoutParams(section, 24, 5);
//		section.addWidget(relatedListWidget);
//	}
//
//	private static void addRelatedListWidget1(Section section) {
//		PageWidget relatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
//		JSONObject relatedList = new JSONObject();
//		relatedList.put("moduleName", "customers");
//		relatedList.put("fieldName", "Lookup");
//		relatedListWidget.setRelatedList(relatedList);
//		relatedListWidget.addToLayoutParams(section, 24, 5);
//		section.addWidget(relatedListWidget);
//	}

}
