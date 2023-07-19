package com.facilio.bmsconsole.page.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class PeopleModulesPageFactory extends PageFactory {
	
	public static Page getPeopleModulePage(PeopleContext record, FacilioModule module) throws Exception {
		Page page = new Page();
		
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		
		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		List<String> formSubModules = new ArrayList<>();
		
		SummaryOrderType orderType = SummaryOrderType.FORM;
		addSecondaryDetailsWidget(tab1Sec1, record, module, formSubModules, orderType);
		if (record == null) {
			return page;
		}

		Tab tab3=page.new Tab("Skill");
		page.addTab(tab3);

		Tab tab2 = page.new Tab("Related");
		boolean isRelationshipNeeded = addRelationshipSection(page, tab2, record.getModuleId());
		Section tab2Sec1 = getRelatedListSectionObj(page);
		tab2.addSection(tab2Sec1);
		addRelatedListWidgets(tab2Sec1, record.getModuleId(), formSubModules, false);

		addBookingWidget(module,record,tab2Sec1);
		
		if((CollectionUtils.isNotEmpty(tab2Sec1.getWidgets())) || isRelationshipNeeded) {
			page.addTab(tab2);
		}
		addCommonSubModuleWidget(tab1Sec1, module, record, null,false);

		return page;
	}
	
	public static void addBookingWidget(FacilioModule module,PeopleContext record,Section section) throws Exception {
		//Booking module hardcoded in people module related list - to be removed
		if(module != null && "people".equalsIgnoreCase(module.getName())) {
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
			relatedListWidget.addToLayoutParams(section, 24, 10);
			section.addWidget(relatedListWidget);
		}
	}
}