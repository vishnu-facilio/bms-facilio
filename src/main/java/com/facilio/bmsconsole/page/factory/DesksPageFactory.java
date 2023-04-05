package com.facilio.bmsconsole.page.factory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class DesksPageFactory extends PageFactory {
    public static Page getDesksPage(V3DeskContext deskContext, FacilioModule module) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);

        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        addSecondaryDetailsWidget(tab1Sec1);
        addCommonSubModuleWidget(tab1Sec1, module, deskContext);

        Page.Tab tab2 = page.new Tab("Related");
        page.addTab(tab2);

        addRelationshipSection(page, tab2, module.getModuleId());

        Page.Section tab2Sec1 = getRelatedListSectionObj(page);
        tab2.addSection(tab2Sec1);

        addCombinedRelatedListWidget(tab2Sec1, FacilioConstants.ContextNames.MOVES, module.getModuleId(), "Moves");
        
        addBookingWidget(deskContext,module,tab2Sec1);
        
        addCombinedRelatedListWidget(tab2Sec1, FacilioConstants.ContextNames.SERVICE_REQUEST, resourceModule.getModuleId(), "Service Requests");

		Page.Tab tab3 = page.new Tab("History");;
		page.addTab(tab3);
		Page.Section tab4Sec1 = page.new Section();
		tab3.addSection(tab4Sec1);
		PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
		activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
		activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
		tab4Sec1.addWidget(activityWidget);

        return page;
    }

    public static void addBookingWidget(V3DeskContext deskContext, FacilioModule module, Page.Section section) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule bookingModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        if(deskContext.getDeskType() != 1) {

            List<FacilityContext> facilities = FacilityAPI.getFacilityList(deskContext.getId(),module.getModuleId());

            if (CollectionUtils.isNotEmpty(facilities)) {

                List<Long> Ids = facilities.stream().map(prop -> (long) prop.getId()).collect(Collectors.toList());
                PageWidget relatedListWidget = new PageWidget(PageWidget.WidgetType.BOOKINGS_RELATED_LIST);
                JSONObject relatedList = new JSONObject();
                relatedList.put("module", bookingModule);
                relatedList.put("field", fieldsAsMap.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY));
                relatedList.put("values", Ids);
                relatedListWidget.setRelatedList(relatedList);
                relatedListWidget.addToLayoutParams(section, 24, 10);
                section.addWidget(relatedListWidget);

            }

        }
    }
    private static void addSecondaryDetailsWidget(Page.Section section) {
        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(section, 24, 7);
        section.addWidget(detailsWidget);
    }

    public static void addRelatedListWidget(Page.Section section, String moduleName, long parenModuleId, String moduleDisplayName) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == parenModuleId)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(fields)) {
            for (FacilioField field : fields) {
                PageWidget relatedListWidget = new PageWidget(PageWidget.WidgetType.RELATED_LIST);
                JSONObject relatedList = new JSONObject();
                module.setDisplayName(moduleDisplayName);
                relatedList.put("module", module);
                relatedList.put("field", field);
                relatedListWidget.setRelatedList(relatedList);
                relatedListWidget.addToLayoutParams(section, 24, 10);
                section.addWidget(relatedListWidget);
            }
        }
    }
    
    public static void addCombinedRelatedListWidget(Page.Section section, String moduleName, long parenModuleId, String moduleDisplayName) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == parenModuleId)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(fields)) {
                PageWidget relatedListWidget = new PageWidget(PageWidget.WidgetType.COMBINED_RELATED_LIST);
                JSONObject relatedList = new JSONObject();
                module.setDisplayName(moduleDisplayName);
                relatedList.put("module", module);
                relatedList.put("fields", fields);
                relatedListWidget.setRelatedList(relatedList);
                relatedListWidget.addToLayoutParams(section, 24, 10);
                section.addWidget(relatedListWidget);
        }
    }
}
