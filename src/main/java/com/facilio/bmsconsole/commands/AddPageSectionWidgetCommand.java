package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class AddPageSectionWidgetCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(AddPageSectionWidgetCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long sectionId = (Long) context.get(FacilioConstants.CustomPage.SECTION_ID);
        if(sectionId<=0){
            LOGGER.error("Invalid Section Id For Creating Page_Section_Widget");
            throw new IllegalArgumentException("Invalid section id for creating Page_Section_Widget");
        }

        PageSectionWidgetContext widget = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET);
        FacilioModule widgetsModule = ModuleFactory.getPageSectionWidgetsModule();

        if(widget != null) {

            FacilioField sectionIdField = FieldFactory.getNumberField("sectionId", "SECTION_ID", widgetsModule);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getEqualsCondition(sectionIdField, String.valueOf(sectionId)));

            widget.setSectionId(sectionId);

            if (widget.getSequenceNumber() <= 0) {
                double sequenceNumber = CustomPageAPI.getMaxSequenceNumber(widgetsModule, criteria);
                widget.setSequenceNumber(sequenceNumber + 10);
            }

            if (widget.getWidgetType() == null ) {
                LOGGER.error("WidgetType Should be Defined");
                throw new IllegalArgumentException("WidgetType should be defined");
            }

            if (widget.getConfigType() == null) {
                LOGGER.error("Widget ConfigType should be defined");
                throw new IllegalArgumentException("Widget ConfigType should be defined for widgets");
            }

            if (widget.getConfigType() == PageSectionWidgetContext.ConfigType.FIXED && !(widget.getWidth() + widget.getPositionX() <= getColumnWidth(sectionId))) {
                LOGGER.error("Widget Width Exceeded the Column Limit");
                throw new IllegalArgumentException("Widget width exceeded the column limit");
            }

            if(widget.getConfigType() == PageSectionWidgetContext.ConfigType.FLEXIBLE) {
                widget.setWidth(-1);
            }
            Long widgetConfigId = WidgetAPI.getWidgetConfigId(widget.getWidgetType(), widget.getWidth(), widget.getHeight(), widget.getConfigType());
            Objects.requireNonNull(widgetConfigId, "Widget Configuration does not exists");
            widget.setWidgetConfigId(widgetConfigId);

            Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
            String name = StringUtils.isNotEmpty(widget.getName()) ? widget.getName() :
                    StringUtils.isNotEmpty(widget.getDisplayName())? widget.getDisplayName(): "widget";
            name = CustomPageAPI.getUniqueName(widgetsModule, criteria, sectionIdField, sectionId, name, isSystem);
            if((isSystem != null && isSystem) && StringUtils.isNotEmpty(widget.getName()) && !widget.getName().equalsIgnoreCase(name)) {
                throw new IllegalArgumentException("linkName already exists, given linkName for widget is invalid");
            }
            widget.setName(name);

            widget.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
            widget.setSysCreatedTime(System.currentTimeMillis());

            CustomPageAPI.insertPageSectionWidgettoDB(widget);
            context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID, widget.getId());

            if(widget.getWidgetDetail() != null) {
                context.put(FacilioConstants.CustomPage.WIDGET_DETAIL,
                        CustomPageAPI.parsePageWidgetDetails(widget.getWidgetType(), widget.getWidgetDetail()));
            }

        }
            return false;
    }

    public static double getColumnWidth(long sectionId) throws Exception {
        if (sectionId > 0) {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getPageColumnsModule().getTableName())
                    .select(Arrays.asList(FieldFactory.getNumberField("width", "WIDTH", ModuleFactory.getPageColumnsModule())))
                    .innerJoin(ModuleFactory.getPageSectionsModule().getTableName())
                    .on("Page_Columns.ID = Page_Sections.COLUMN_ID")
                    .andCondition(CriteriaAPI.getEqualsCondition(FieldFactory.getIdField(ModuleFactory.getPageSectionsModule()), String.valueOf(sectionId)));

            Map<String, Object> map = builder.fetchFirst();

            if (MapUtils.isNotEmpty(map)) {
                return (((Long) map.get("width")));
            }
        }
        return 0D;
    }

}