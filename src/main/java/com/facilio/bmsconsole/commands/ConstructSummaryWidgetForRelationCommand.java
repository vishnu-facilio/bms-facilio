package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SummaryWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fields.context.FieldListType;
import com.facilio.fields.util.FieldsConfigChainUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.SummaryWidgetUtil;
import com.facilio.v3.context.Constants;
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

            FacilioContext fieldsContext = FieldsConfigChainUtil.fetchFieldList(moduleName, AccountUtil.getCurrentApp().getId(), FieldListType.RELATIONSHIP_SUMMARY_WIDGET_FIELDS, null);
            List<SummaryWidgetGroupFields> summaryWidgetGroupFields = (List<SummaryWidgetGroupFields>) fieldsContext.get(FacilioConstants.ContextNames.FIELDS);

            if (CollectionUtils.isNotEmpty(summaryWidgetGroupFields)) {
                constructSummaryWidgetFromSummaryFields(context, moduleName, summaryWidgetGroupFields, customMainFields);
            } else {
                List<FacilioField> fields = modBean.getAllFields(moduleName);
                constructSummaryWidgetFromFields(context, fields, customMainFields);
            }
        }

        return false;
    }

    private void constructSummaryWidgetFromSummaryFields(Context context, String moduleName, List<SummaryWidgetGroupFields> summaryWidgetGroupFields, List<String> customMainFields) throws Exception {
        if(CollectionUtils.isNotEmpty(summaryWidgetGroupFields)) {
            summaryWidgetGroupFields.removeIf(field -> INTERNAL_FIELDS.contains(field.getName()));
            ModuleBean modBean = Constants.getModBean();
            FacilioField primaryField = modBean.getPrimaryField(moduleName);

            List<SummaryWidgetGroupFields> mainFields = summaryWidgetGroupFields.stream()
                    .filter(field -> primaryField.getName().equals(field.getName()) || (CollectionUtils.isNotEmpty(customMainFields) && customMainFields.contains(field.getName())))
                    .collect(Collectors.toList());
            summaryWidgetGroupFields.removeAll(mainFields);

            List<SummaryWidgetGroupFields> primaryDetailsfullSpanField = mainFields.stream()
                    .filter(f -> f.getField().getDataTypeEnum() == FieldType.LARGE_TEXT || f.getField().getDataTypeEnum() == FieldType.BIG_STRING)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(primaryDetailsfullSpanField)) {
                summaryWidgetGroupFields.removeAll(primaryDetailsfullSpanField);
            }

            List<SummaryWidgetGroupFields> otherDetailsfullSpanField = CollectionUtils.isEmpty(summaryWidgetGroupFields) ? null : summaryWidgetGroupFields.stream()
                    .filter(f -> f.getField().getDataTypeEnum() == FieldType.LARGE_TEXT || f.getField().getDataTypeEnum() == FieldType.BIG_STRING)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(otherDetailsfullSpanField)) {
                summaryWidgetGroupFields.removeAll(otherDetailsfullSpanField);
            }

            SummaryWidget relationSummaryWidget = new SummaryWidget();

            addRelationshipWidgetGroupDetails("Primary Details", relationSummaryWidget, mainFields, primaryDetailsfullSpanField, 4);
            addRelationshipWidgetGroupDetails("Other Details", relationSummaryWidget, summaryWidgetGroupFields, primaryDetailsfullSpanField, 4);

            context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, relationSummaryWidget);
        }

    }

    private static void constructSummaryWidgetFromFields(Context context, List<FacilioField> fields, List<String> customMainFields) throws Exception {
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
    private static void addRelationshipWidgetGroupDetails(String displayName,SummaryWidget relationSummaryWidget, List<SummaryWidgetGroupFields> mainFields, List<SummaryWidgetGroupFields> fullSpanFields, int columns) throws Exception {
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        widgetGroup.setDisplayName(displayName);
        widgetGroup.setColumns(columns);
        widgetGroup.setSequenceNumber(10);

        int rowIndex = 1, colIndex = 1;
        for (SummaryWidgetGroupFields mainField : mainFields) {
            SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, mainField.getField(), rowIndex, colIndex, 1);
            if (colIndex + 1 == 4) {
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
            for (SummaryWidgetGroupFields field : fullSpanFields) {
                SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, field.getField(), rowIndex, colIndex, 4);
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
