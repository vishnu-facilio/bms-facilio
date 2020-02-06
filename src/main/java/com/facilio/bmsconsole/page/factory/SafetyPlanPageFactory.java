package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.HazardContext;
import com.facilio.bmsconsole.context.PrecautionContext;
import com.facilio.bmsconsole.context.SafetyPlanContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class SafetyPlanPageFactory extends PageFactory{
    public static Page getSafetyPlanPage(SafetyPlanContext safetyPlan) throws Exception {
        Page page = new Page();

        Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        addSecondaryDetailsWidget(tab1Sec1);
        addCommonSubModuleGroup(tab1Sec1);

        Tab tab2 = page.new Tab("related list");
        page.addTab(tab2);

        Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        addRelatedListWidget(tab2Sec1, "safetyPlanHazard", safetyPlan.getModuleId());
        addSafetyPlanHazardsWidget(tab2Sec1);

        return page;
    }

    public static Page getHazardPage(HazardContext hazard) throws Exception {
        Page page = new Page();

        Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        addSecondaryDetailsWidget(tab1Sec1);
        addCommonSubModuleGroup(tab1Sec1);

        Tab tab2 = page.new Tab("related list");
        page.addTab(tab2);

        Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        addRelatedListWidget(tab2Sec1, "safetyPlanHazard", hazard.getModuleId());
        addRelatedListWidget(tab2Sec1, "hazardPrecaution", hazard.getModuleId());

        return page;
    }
    public static Page getPrecautionPage(PrecautionContext precaution) throws Exception {
        Page page = new Page();

        Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        addSecondaryDetailsWidget(tab1Sec1);
        addCommonSubModuleGroup(tab1Sec1);

        Tab tab2 = page.new Tab("related list");
        page.addTab(tab2);

        Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        addRelatedListWidget(tab2Sec1, "hazardPrecaution", precaution.getModuleId());

        return page;
    }

    private static void addSecondaryDetailsWidget(Section section) {
        PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(section, 24, 4);
        section.addWidget(detailsWidget);
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
    private static void addSafetyPlanHazardsWidget(Section section) {
        PageWidget widget = new PageWidget(WidgetType.SAFETY_PLAN_HAZARDS, "safetyPlanHazards");
        widget.addToLayoutParams(section, 24, 5);
        section.addWidget(widget);
    }

}
