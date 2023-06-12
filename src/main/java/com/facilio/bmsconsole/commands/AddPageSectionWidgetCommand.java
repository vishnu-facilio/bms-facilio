package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.context.WidgetConfigContext;
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
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class AddPageSectionWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long sectionId = (Long) context.get(FacilioConstants.CustomPage.SECTION_ID);
        if(sectionId<=0){
            throw new IllegalArgumentException("Invalid section id for creating Page_Section_Widget");
        }

        PageSectionWidgetContext widget = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET);
        FacilioModule widgetsModule = ModuleFactory.getPageSectionWidgetsModule();

        PagesContext.PageLayoutType layoutType = (PagesContext.PageLayoutType) context.get(FacilioConstants.CustomPage.LAYOUT_TYPE);
        layoutType = layoutType!=null? layoutType: PagesContext.PageLayoutType.WEB;

        if(widget != null) {

            FacilioField sectionIdField = FieldFactory.getNumberField("sectionId", "SECTION_ID", widgetsModule);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getEqualsCondition(sectionIdField, String.valueOf(sectionId)));

            widget.setSectionId(sectionId);

            if (widget.getSequenceNumber() <= 0) {
                double sequenceNumber = CustomPageAPI.getMaxSequenceNumber(widgetsModule, criteria);
                widget.setSequenceNumber(sequenceNumber + 10);
            }

            FacilioUtil.throwIllegalArgumentException(widget.getWidgetType() == null, "widgetType can't be null");

            WidgetConfigContext config = WidgetAPI.getWidgetConfiguration(widget.getWidgetType(), widget.getWidgetConfigId(),  widget.getWidgetConfigName(), layoutType);
            Objects.requireNonNull(config, "widget configuration does not exists for configId -- " +widget.getWidgetConfigId() +" or configName -- " +widget.getWidgetConfigName()
            +" for layoutType -- "+layoutType);

            widget.setWidgetConfigId(config.getId());
            widget.setConfigType(config.getConfigType());
            widget.setWidth(config.getMinWidth());
            widget.setHeight(config.getMinHeight());
            widget.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
            widget.setSysCreatedTime(System.currentTimeMillis());

            Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
            String name = StringUtils.isNotEmpty(widget.getName()) ? widget.getName() :
                    StringUtils.isNotEmpty(widget.getDisplayName())? widget.getDisplayName(): "widget";
            name = CustomPageAPI.getUniqueName(widgetsModule, criteria, sectionIdField, sectionId, name, isSystem);
            if((isSystem != null && isSystem) && StringUtils.isNotEmpty(widget.getName()) && !widget.getName().equalsIgnoreCase(name)) {
                throw new IllegalArgumentException("linkName already exists or given linkName for widget is invalid");
            }
            widget.setName(name);

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