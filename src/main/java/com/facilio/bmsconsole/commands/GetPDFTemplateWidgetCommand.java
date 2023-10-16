package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PdfTemplateWidgetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Map;

public class GetPDFTemplateWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);

        PdfTemplateWidgetContext widget = new PdfTemplateWidgetContext();

        if(widgetId != null) {
            FacilioModule module = ModuleFactory.getPdfTemplateWidgetRelation();
            FacilioField widgetIdField = FieldFactory.getWidgetIdField(module, widget.getWidgetWrapperType());

            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getPdfTemplateWidgetRelationFields())
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getEqualsCondition(widgetIdField, String.valueOf(widgetId)));
            Map<String, Object> templateWidgetRelDetails = selectRecordBuilder.fetchFirst();

            if(templateWidgetRelDetails != null) {
                Long templateId = (Long) templateWidgetRelDetails.get("templateId");
                widget.setTemplateId(templateId);
            }else{
                widget.setTemplateId(null);
            }
        }

        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, widget);
        return false;
    }
}
