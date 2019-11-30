package com.facilio.bmsconsole.page.factory;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.context.WorkPermitContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class VendorPageFactory extends PageFactory{
	private static final Logger LOGGER = LogManager.getLogger(VendorPageFactory.class.getName());
	public static Page getVendorPage(VendorContext vendor) throws Exception {
		Page page = new Page();

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(vendor.getModuleId());

		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		// addPrimaryDetailsWidget(tab1Sec1);
		addSecondaryDetailsWidget(tab1Sec1);
		
		Section tab1Sec2 = page.new Section();
		tab1.addSection(tab1Sec2);
		addCommonSubModuleGroup(tab1Sec2);
		
		Tab tab2 = page.new Tab("related list");
		page.addTab(tab2);
		Section tab2Sec1 = page.new Section();
		tab2.addSection(tab2Sec1);
		addRelatedListWidget(tab2Sec1, "contact", vendor.getModuleId());
		Section tab2Sec2 = page.new Section();
		tab2.addSection(tab2Sec2);
		addRelatedListWidget(tab2Sec2, "insurance", vendor.getModuleId());
		Section tab2Sec3 = page.new Section();
		tab2.addSection(tab2Sec3);
		addRelatedListWidget(tab2Sec3, "workorder", vendor.getModuleId());
		Section tab2Sec4 = page.new Section();
		tab2.addSection(tab2Sec4);
		addRelatedListWidget(tab2Sec4, "workpermit", vendor.getModuleId());
		
//		Tab tab3 = page.new Tab("insurance");
//		page.addTab(tab3);
//		Section tab3Sec1 = page.new Section();
//		tab3.addSection(tab3Sec1);
//		addRelatedListWidget(tab3Sec1, "insurance", vendor.getModuleId());

//		Tab tab4 = page.new Tab("work orders");
//		page.addTab(tab4);
//		Section tab4Sec1 = page.new Section();
//		tab4.addSection(tab4Sec1);
//		addRelatedListWidget(tab4Sec1, "workorder", vendor.getModuleId());

//		Tab tab5 = page.new Tab("work permits");
//		page.addTab(tab5);
//		Section tab5Sec1 = page.new Section();
//		tab5.addSection(tab5Sec1);
//		addRelatedListWidget(tab5Sec1, "workpermit", vendor.getModuleId());
		
		return page;
	}

	private static void addPrimaryDetailsWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.PRIMARY_DETAILS_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 6);
		section.addWidget(pageWidget);
	}

	private static void addSecondaryDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(detailsWidget);
	}

	private static void addInsuranceWidget(Section section) {
		PageWidget plannedWidget = new PageWidget(WidgetType.INSURANCE, "insurance");
		plannedWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(plannedWidget);
	}
	
	private static void addRelatedListWidget(Section section, String moduleName, long parenModuleId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> allFields = modBean.getAllFields(module.getName());
		List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == parenModuleId)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(fields)) {
			for (FacilioField field : fields) {
				PageWidget relatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
				JSONObject relatedList = new JSONObject();
				relatedList.put("module", module);
				relatedList.put("field", field);
				relatedListWidget.setRelatedList(relatedList);
				relatedListWidget.addToLayoutParams(section, 24, 10);
				section.addWidget(relatedListWidget);
			}
		}
	}
	
}
