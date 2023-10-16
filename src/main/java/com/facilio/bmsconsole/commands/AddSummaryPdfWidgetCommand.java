package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.context.PdfTemplateWidgetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.Map;

public class AddSummaryPdfWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PdfTemplateWidgetContext templateWidgetContext = (PdfTemplateWidgetContext) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        PageSectionWidgetContext widget = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.WIDGET);

        FacilioModule module = ModuleFactory.getPdfTemplateWidgetRelation();
        FacilioField widgetIdField = FieldFactory.getWidgetIdField(module, widget.getWidgetWrapperType());

        if(widgetId != null){
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getPdfTemplateWidgetRelationFields())
                    .table(ModuleFactory.getPdfTemplateWidgetRelation().getTableName())
                    .andCondition(CriteriaAPI.getEqualsCondition(widgetIdField, String.valueOf(widgetId)));
            Map<String,Object> templateWidgetRelDetails = selectRecordBuilder.fetchFirst();

            Map<String,Object> prop = new HashMap<>();

            if(templateWidgetContext != null) {
                prop.put("templateId", templateWidgetContext.getTemplateId());

                if (templateWidgetRelDetails != null) {
                    GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                            .ignoreSplNullHandling()
                            .table(ModuleFactory.getPdfTemplateWidgetRelation().getTableName())
                            .fields(FieldFactory.getPdfTemplateWidgetRelationFields())
                            .andCondition(CriteriaAPI.getEqualsCondition(widgetIdField, String.valueOf(widgetId)));
                    updateRecordBuilder.update(prop);
                } else {
                    prop.put("widgetId", widgetId);
                    GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                            .table(ModuleFactory.getPdfTemplateWidgetRelation().getTableName())
                            .fields(FieldFactory.getPdfTemplateWidgetRelationFields())
                            .addRecord(prop);
                    insertRecordBuilder.save();
                }
            }
        }

        return false;
    }
}
