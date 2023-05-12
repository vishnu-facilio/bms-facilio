package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WidgetGroupWidgetContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.WidgetGroupUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddWidgetGroupWidgetsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<WidgetGroupWidgetContext> widgets = (List<WidgetGroupWidgetContext>) context.get(FacilioConstants.WidgetGroup.WIDGETGROUP_WIDGETS);
        List<Long> sectionIds = (List<Long>) context.get(FacilioConstants.WidgetGroup.WIDGETGROUP_SECTION_IDS);

        FacilioModule module = ModuleFactory.getWidgetGroupWidgetsModule();
        if (CollectionUtils.isNotEmpty(widgets)) {
            FacilioField widgetGroupSectionIdField = FieldFactory.getNumberField("sectionId", "SECTION_ID", module);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getConditionFromList("SECTION_ID", "sectionId", sectionIds, NumberOperators.EQUALS));
            Map<Long, List<String>> existingWidgetName = CustomPageAPI.getExistingNameListAsMap(module, criteria, widgetGroupSectionIdField);
            double sequenceNumber = 0;

            for (WidgetGroupWidgetContext widget : widgets) {
                if(sequenceNumber <= 0) {
                    widget.setSequenceNumber(sequenceNumber+=10);
                }
                FacilioUtil.throwIllegalArgumentException(widget.getWidgetType() == null, "WidgetType can't be null");

                FacilioUtil.throwIllegalArgumentException(widget.getWidgetType() == PageWidget.WidgetType.WIDGET_GROUP, "widegtGroup's widgetType can't be 'WIDGET_GROUP'");
                widget.setSysCreatedTime(System.currentTimeMillis());

                Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
                String name = widget.getDisplayName()!=null?widget.getDisplayName():"widgetGroupWidget";
                name = name.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
                name = existingWidgetName.get(widget.getSectionId())!=null?CustomPageAPI.generateUniqueName(name,existingWidgetName.get(widget.getSectionId()),isSystem):name;
                widget.setName(name);

                if (existingWidgetName.containsKey(widget.getSectionId())) {
                    existingWidgetName.get(widget.getSectionId()).add(name);
                } else {
                    List<String> nameslist = new ArrayList<>();
                    nameslist.add(name);
                    existingWidgetName.put(widget.getSectionId(), nameslist);
                }
            }
            WidgetGroupUtil.insertWidgetGroupWidgetsToDB(widgets);
        }
        return false;
    }
}
