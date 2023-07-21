package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.context.WidgetConfigContext;
import com.facilio.bmsconsole.context.WidgetGroupWidgetContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.bmsconsole.util.WidgetGroupUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetConfigUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
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
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddWidgetGroupWidgetsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<WidgetGroupWidgetContext> widgets = (List<WidgetGroupWidgetContext>) context.get(FacilioConstants.WidgetGroup.WIDGETGROUP_WIDGETS);
        List<Long> sectionIds = (List<Long>) context.get(FacilioConstants.WidgetGroup.WIDGETGROUP_SECTION_IDS);
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        FacilioModule module = ModuleFactory.getWidgetGroupWidgetsModule();

        PagesContext.PageLayoutType layoutType = (PagesContext.PageLayoutType) context.get(FacilioConstants.CustomPage.LAYOUT_TYPE);
        layoutType = layoutType!=null? layoutType: PagesContext.PageLayoutType.WEB;

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

                FacilioUtil.throwIllegalArgumentException(widget.getWidgetType() == null, "widgetType can't be null");
                FacilioUtil.throwIllegalArgumentException(widget.getWidgetType() == PageWidget.WidgetType.WIDGET_GROUP, "widegtGroup's widgetType can't be 'WIDGET_GROUP'");

                WidgetConfigContext config = WidgetAPI.getWidgetConfiguration(widget.getWidgetType(), widget.getWidgetConfigId(),  widget.getWidgetConfigName(), layoutType);
                Objects.requireNonNull(config, "widgetGroup's  widget configuration does not exists for configId -- " +widget.getWidgetConfigId() +" or configName -- " +widget.getWidgetConfigName()
                        +" for layoutType -- "+layoutType);

                widget.setWidgetConfigId(config.getId());
                widget.setConfigType(config.getConfigType());
                widget.setWidth(config.getMinWidth());
                widget.setHeight(config.getMinHeight());
                widget.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
                widget.setSysCreatedTime(System.currentTimeMillis());

                Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
                String name = StringUtils.isNotEmpty(widget.getName()) ? widget.getName() :
                        StringUtils.isNotEmpty(widget.getDisplayName())? widget.getDisplayName(): "widgetGroupWidget";
                name = name.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
                name = CustomPageAPI.generateUniqueName(name,existingWidgetName.get(widget.getSectionId()),isSystem);
                if((isSystem != null && isSystem) && StringUtils.isNotEmpty(widget.getName()) && !widget.getName().equalsIgnoreCase(name)) {
                    throw new IllegalArgumentException("linkName already exists or given linkName for widget is invalid");
                }
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

            for(WidgetGroupWidgetContext widget : widgets) {
                if(widget.getWidgetDetail() != null ) {
                    WidgetConfigUtil.addWidgetDetail(appId, moduleName, widget, WidgetWrapperType.WIDGET_GROUP);
                }
            }
        }
        return false;
    }
}
