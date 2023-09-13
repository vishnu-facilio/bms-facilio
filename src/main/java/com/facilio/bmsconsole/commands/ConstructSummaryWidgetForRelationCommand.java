package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.module.GetSortableFieldsCommand;
import com.facilio.bmsconsole.context.SummaryWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ConstructSummaryWidgetForRelationCommand extends FacilioCommand {
    public static final Map<String, List<String>> MAIN_FIELD_MAP = Collections.unmodifiableMap(initMap());
    public static final List<String> INTERNAL_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] {
            FacilioConstants.ContextNames.SITE_ID, FacilioConstants.ContextNames.STATE_FLOW_ID, FacilioConstants.ContextNames.SLA_POLICY_ID, FacilioConstants.ContextNames.FORM_ID,  FacilioConstants.ContextNames.APPROVAL_STATUS, "approvalFlowId",
    }));
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SummaryWidget summaryWidget = (SummaryWidget) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);
        if(summaryWidget == null) {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        Objects.requireNonNull(module, "Module can't be null");

        List<String> customMainFields = module.isCustom() ? null : MAIN_FIELD_MAP.get(moduleName);

        List<FacilioField> fields = modBean.getAllFields(moduleName);

        if(CollectionUtils.isNotEmpty(fields)) {
            fields.removeIf(field -> INTERNAL_FIELDS.contains(field.getName()));

            fields.sort(Comparator.comparing(FacilioField::isMainField).reversed()
                    .thenComparing(FacilioField::getDisplayName));

            List<FacilioField> mainFields = fields.stream()
                    .filter(field -> field.isMainField() || (CollectionUtils.isNotEmpty(customMainFields) && customMainFields.contains(field.getName())))
                    .collect(Collectors.toList());
            fields.removeAll(mainFields);

            List<FacilioField> primaryDetailsfullSpanField = mainFields.stream()
                    .filter(f -> f.getDataTypeEnum() == FieldType.LARGE_TEXT || f.getDataTypeEnum() == FieldType.BIG_STRING)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(primaryDetailsfullSpanField)) {
                fields.removeAll(primaryDetailsfullSpanField);
            }

            List<FacilioField> otherDetailsfullSpanField = CollectionUtils.isEmpty(fields) ? null : fields.stream()
                    .filter(f -> f.getDataTypeEnum() == FieldType.LARGE_TEXT || f.getDataTypeEnum() == FieldType.BIG_STRING)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(otherDetailsfullSpanField)) {
                fields.removeAll(otherDetailsfullSpanField);
            }

            SummaryWidget relationSummaryWidget = new SummaryWidget();
            addWidgetGroupDetails("Primary Details", relationSummaryWidget, mainFields, primaryDetailsfullSpanField, 4);
            addWidgetGroupDetails("Other Details", relationSummaryWidget, fields, primaryDetailsfullSpanField, 4);

            context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, relationSummaryWidget);
        }
        }

        return false;
    }

    private static Map<String, List<String>> initMap() {
        Map<String, List<String>> mainFieldsMap = new HashMap<>();
        return mainFieldsMap;
    }

    private static void addWidgetGroupDetails(String displayName,SummaryWidget relationSummaryWidget, List<FacilioField> mainFields, List<FacilioField> fullSpanFields, int columns) throws Exception {
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        int rowIndex = 1, colIndex = 1;
        widgetGroup.setDisplayName(displayName);
        widgetGroup.setColumns(columns);
        widgetGroup.setSequenceNumber(10);

        for (FacilioField field : mainFields) {
            SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, field, rowIndex, colIndex, 1);
            if (colIndex + 1 == columns) {
                rowIndex++;
                colIndex=1;
            } else {
                colIndex++;
            }
        }

        if(CollectionUtils.isEmpty(fullSpanFields)) {
            if (colIndex != 1) {
                rowIndex++;
                colIndex = 1;
            }
            for (FacilioField field : fullSpanFields) {
                SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, field, rowIndex, colIndex, 4);
                rowIndex++;
            }
        }

        if(CollectionUtils.isEmpty(relationSummaryWidget.getGroups())) {
            relationSummaryWidget.setGroups(new ArrayList<>(Arrays.asList(widgetGroup)));
        } else {
            relationSummaryWidget.getGroups().add(widgetGroup);
        }
    }
}
