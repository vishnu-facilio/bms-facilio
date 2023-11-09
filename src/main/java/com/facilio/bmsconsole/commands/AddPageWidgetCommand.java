package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddPageWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        PagesContext.PageLayoutType layoutType = (PagesContext.PageLayoutType) context.get(FacilioConstants.CustomPage.LAYOUT_TYPE);
        layoutType = layoutType != null ? layoutType : PagesContext.PageLayoutType.WEB;
        Long sectionId = (Long) context.get(FacilioConstants.CustomPage.SECTION_ID);
        if (sectionId <= 0) {
            throw new IllegalArgumentException("Invalid section id for creating Page_Section_Widget");
        }

        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);
        Objects.requireNonNull(widgetWrapperType, "WidgetWrapperType should be defined");
        PageSectionWidgetContext widget = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.WIDGET);

        FacilioModule widgetsModule = CustomPageAPI.getModuleForWidgetWrapperType( widgetWrapperType);
        FacilioField sectionIdField = FieldFactory.getNumberField("sectionId", "SECTION_ID", widgetsModule);


        Criteria sectionIdCriteria = new Criteria();
        sectionIdCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(sectionIdField, String.valueOf(sectionId)));
        Map<Long, List<String>> existingNamesMap = CustomPageAPI.getExistingNameListAsMap(widgetsModule, sectionIdCriteria, sectionIdField);
        double sequenceNumber = CustomPageAPI.getMaxSequenceNumber(widgetsModule, sectionIdCriteria);

        widget.setSectionId(sectionId);
        CustomPageAPI.validatePageWidget(widget, widgetWrapperType);

        String name = CustomPageAPI.getLinkNameFromObjectOrDefault(widget, "widget");
        name = CustomPageAPI.generateUniqueName(name, existingNamesMap.get(sectionId), isSystem);
        if ((isSystem != null && isSystem) && StringUtils.isNotEmpty(widget.getName()) && !widget.getName().equalsIgnoreCase(name)) {
            throw new IllegalArgumentException("linkName already exists or given linkName for widget is invalid");
        }
        widget.setName(name);
        WidgetAPI.setConfigDetailsForWidgets(moduleName, layoutType, widget);
        widget.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
        widget.setSysCreatedTime(System.currentTimeMillis());
        if (widget.getSequenceNumber() <= 0) {
            widget.setSequenceNumber(sequenceNumber+=10);
        }
        CustomPageAPI.insertPageWidget(Arrays.asList(widget), widgetWrapperType);

        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL,
                WidgetAPI.parsePageWidgetDetails(widget.getWidgetType(), widget.getWidgetDetail()));
        context.put(FacilioConstants.CustomPage.WIDGETID, widget.getId());
        return false;
    }
}
