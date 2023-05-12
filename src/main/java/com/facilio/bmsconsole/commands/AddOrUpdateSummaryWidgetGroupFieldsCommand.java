package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AddOrUpdateSummaryWidgetGroupFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<SummaryWidgetGroupFields> summaryWidgetGroupFields = (List<SummaryWidgetGroupFields>) context.get(FacilioConstants.SummaryWidget.SUMMARY_WIDGET_GROUP_FIELDS);
        List<SummaryWidgetGroupFields> updatableSummaryWidgetGroupFields = (List<SummaryWidgetGroupFields>) context.get(FacilioConstants.SummaryWidget.UPDATABLE_SUMMARY_WIDGET_GROUP_FIELDS);
        List<Long> existingFieldIds = (List<Long>) context.get(FacilioConstants.SummaryWidget.EXISTING_SUMMARY_WIDGET_GROUP_FIELD_IDS);

        List<Long> deletableFieldIds = existingFieldIds != null ? new ArrayList<>(existingFieldIds) : new ArrayList<>();
        if (CollectionUtils.isNotEmpty(updatableSummaryWidgetGroupFields)) {
            for (SummaryWidgetGroupFields field : updatableSummaryWidgetGroupFields) {
                SummaryWidgetUtil.updateSummaryWidgetGroupFields(field);
                deletableFieldIds.remove(field.getId());
            }
        }

        if (CollectionUtils.isNotEmpty(deletableFieldIds)) {
            SummaryWidgetUtil.deleteSummaryWidgetGroupFiedls(deletableFieldIds);
        }

        if (CollectionUtils.isNotEmpty(summaryWidgetGroupFields)) {
//            fieldsInputValidation(summaryWidgetGroupFields);

            SummaryWidgetUtil.insertSummaryWidgetGroupFieldsToDB(summaryWidgetGroupFields);
        }
        return false;
    }

    private void fieldsInputValidation(List<SummaryWidgetGroupFields> fields) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> allGroupFieldIds = fields.stream()
                .map(SummaryWidgetGroupFields::getFieldId).collect(Collectors.toList());
        List<FacilioField> allModBeanFields = modBean.getFields(allGroupFieldIds);
        Map<Long, FacilioField> allModBeanFieldsMap = allModBeanFields.stream().collect(Collectors.toMap(FacilioField::getFieldId, Function.identity(), (oldValue, newValue) -> newValue));

        for(SummaryWidgetGroupFields field : fields) {
            if(field.getFieldId() > 0 && allModBeanFieldsMap.containsKey(field.getFieldId())) {
                FacilioField modField = allModBeanFieldsMap.get(field.getFieldId());
                field.setName(modField.getName());
            }
            else if(field.getParentLookupFieldId() <= 0) {
                throw new IllegalArgumentException("Invalid field");
            }
        }
    }
}
