package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddOrUpdateSummaryWidgetGroupsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long summaryWidgetId = (Long) context.get(FacilioConstants.ContextNames.ID);
        Objects.requireNonNull(summaryWidgetId, "Summary widget id can't be null");

        List<SummaryWidgetGroup> summaryWidgetGroups = (List<SummaryWidgetGroup>) context.get(FacilioConstants.SummaryWidget.SUMMARY_WIDGET_GROUPS);
        List<SummaryWidgetGroup> existingSummaryWidgetGroups = (List<SummaryWidgetGroup>) context.get(FacilioConstants.SummaryWidget.EXISTING_SUMMARY_WIDGET_GROUPS);

        Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
        isSystem = isSystem != null?isSystem:false;

        List<String> nameList = new ArrayList<>();
        int sequenceNumber = 0;
        if (CollectionUtils.isEmpty(existingSummaryWidgetGroups)) {

            if (CollectionUtils.isNotEmpty(summaryWidgetGroups)) {
                List<SummaryWidgetGroupFields> summaryGroupFields = new ArrayList<>();

                for (SummaryWidgetGroup widgetGroup : summaryWidgetGroups) {

                    SummaryWidgetUtil.widgetGroupRowSpanValidator(widgetGroup);
                    widgetGroup.setSequenceNumber(sequenceNumber+=10);

                    setUniqueNameForWidgetGroup(widgetGroup, nameList, isSystem);
                    addWidgetGroupToDBAndAddWidgetGroupFields(summaryWidgetId, widgetGroup, summaryGroupFields);
                }

                context.put(FacilioConstants.SummaryWidget.SUMMARY_WIDGET_GROUP_FIELDS, summaryGroupFields);
            }
        } else {
            List<Long> existingSummaryWidgetGroupFieldIds = existingSummaryWidgetGroups.stream()
                                               .map(f->f.getFields()
                                                       .stream()
                                                       .map(SummaryWidgetGroupFields::getId)
                                                       .collect(Collectors.toList()))
                                               .flatMap(List::stream)
                                               .collect(Collectors.toList());

            List<SummaryWidgetGroup> newSummaryWidgetGroup = new ArrayList<>();
            List<Long> deletableGroupIds = existingSummaryWidgetGroups
                    .stream()
                    .map(SummaryWidgetGroup::getId)
                    .collect(Collectors.toList());

            List<SummaryWidgetGroupFields> newSummaryWidgetGroupFields = new ArrayList<>();
            List<SummaryWidgetGroupFields> updatableSummaryWidgetGroupFields = new ArrayList<>();


            for (SummaryWidgetGroup widgetGroup : summaryWidgetGroups) {

                SummaryWidgetUtil.widgetGroupRowSpanValidator(widgetGroup);
                widgetGroup.setSequenceNumber(sequenceNumber+=10);

                setUniqueNameForWidgetGroup(widgetGroup, nameList, isSystem);
                if(widgetGroup.getId() > 0) {
                    updateWidgetGroupInDBAndAddWidgetGroupFields(widgetGroup, newSummaryWidgetGroupFields, updatableSummaryWidgetGroupFields);
                    deletableGroupIds.remove(widgetGroup.getId());
                }
                else {
                    newSummaryWidgetGroup.add(widgetGroup);
                }

            }

            if(CollectionUtils.isNotEmpty(deletableGroupIds)) {
                SummaryWidgetUtil.deleteSummaryWidgetGroup(deletableGroupIds);
            }

            for (SummaryWidgetGroup widgetGroup : newSummaryWidgetGroup){
                    addWidgetGroupToDBAndAddWidgetGroupFields(summaryWidgetId, widgetGroup, newSummaryWidgetGroupFields);
            }

            context.put(FacilioConstants.SummaryWidget.SUMMARY_WIDGET_GROUP_FIELDS, newSummaryWidgetGroupFields);
            context.put(FacilioConstants.SummaryWidget.UPDATABLE_SUMMARY_WIDGET_GROUP_FIELDS, updatableSummaryWidgetGroupFields);
            context.put(FacilioConstants.SummaryWidget.EXISTING_SUMMARY_WIDGET_GROUP_FIELD_IDS, existingSummaryWidgetGroupFieldIds);

        }
        return false;
    }

    private void setUniqueNameForWidgetGroup(SummaryWidgetGroup widgetGroup, List<String> nameList, boolean isSystem){
        String name = widgetGroup.getDisplayName() != null ? widgetGroup.getDisplayName() : "summaryWidgetGroup";
        name = CustomPageAPI.generateUniqueName(name.toLowerCase().replaceAll("[^a-zA-Z0-9]+", ""), nameList,isSystem);
        nameList.add(name);
        widgetGroup.setName(name);
    }
    private void addWidgetGroupToDBAndAddWidgetGroupFields(long widgetId, SummaryWidgetGroup widgetGroup, List<SummaryWidgetGroupFields> fields) throws Exception{

        widgetGroup.setWidgetId(widgetId);
        SummaryWidgetUtil.insertSummaryWidgetGroupToDB(widgetGroup);

        fields.addAll(widgetGroup.getFields().stream()
                .peek(f -> {
                    f.setWidgetGroupId(widgetGroup.getId());
                    f.setWidgetId(widgetGroup.getWidgetId());
                })
                .collect(Collectors.toList()));
    }

    private void updateWidgetGroupInDBAndAddWidgetGroupFields(SummaryWidgetGroup widgetGroup, List<SummaryWidgetGroupFields> newSummaryWidgetGroupFields,
                                                                    List<SummaryWidgetGroupFields> updatableSummaryWidgetGroupFields) throws Exception {
        SummaryWidgetUtil.widgetGroupRowSpanValidator(widgetGroup);
        SummaryWidgetUtil.updateSummaryWidgetGroup(widgetGroup);

        newSummaryWidgetGroupFields.addAll(widgetGroup.getFields().stream()
                .filter(f -> f.getId() <= 0)
                .peek(f -> {
                    f.setWidgetGroupId(widgetGroup.getId());
                    f.setWidgetId(widgetGroup.getWidgetId());
                })
                .collect(Collectors.toList()));

        updatableSummaryWidgetGroupFields.addAll(widgetGroup.getFields().stream()
                .filter(f -> f.getId() > 0)
                .peek(f -> {
                    f.setWidgetGroupId(widgetGroup.getId());
                    f.setWidgetId(widgetGroup.getWidgetId());
                })
                .collect(Collectors.toList()));
    }
}
