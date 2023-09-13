package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageColumnContext;
import com.facilio.bmsconsole.context.PageSectionContext;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class AddPageColumnsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
        Map<Long, List<PageColumnContext>> tabColumnsMap = (Map<Long, List<PageColumnContext>>) context.get(FacilioConstants.CustomPage.TAB_COLUMNS_MAP);

        FacilioModule columnsModule = ModuleFactory.getPageColumnsModule();
        List<PageColumnContext> columns = new ArrayList<>();

        if (MapUtils.isNotEmpty(tabColumnsMap)) {
            long currentUser = AccountUtil.getCurrentUser().getId();
            long currentTime = System.currentTimeMillis();
            FacilioField tabIdField = FieldFactory.getNumberField("tabId", "TAB_ID", columnsModule);

            Criteria fetchExistingNamesCriteria = new Criteria();
            fetchExistingNamesCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(tabIdField, tabColumnsMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(", "))));
            Map<Long, List<String>> existingNamesMap = CustomPageAPI.getExistingNameListAsMap(columnsModule, fetchExistingNamesCriteria, tabIdField);

            for (Map.Entry<Long, List<PageColumnContext>> entry : tabColumnsMap.entrySet()) {
                long tabId = entry.getKey();
                List<PageColumnContext> tabColumns = entry.getValue();

                if (CollectionUtils.isNotEmpty(tabColumns)) {
                    long columnWidthSum = tabColumns.stream().mapToLong(PageColumnContext::getWidth).sum();
                    FacilioUtil.throwIllegalArgumentException(columnWidthSum != 12, "Invalid column widths");
                    FacilioUtil.throwIllegalArgumentException(getExistingColumnsWidth(tabId) != 0, "Column(s) already exists for tabId -- " + tabId);

                    Criteria tabIdCriteria = new Criteria();
                    tabIdCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(tabIdField, String.valueOf(tabId)));
                    double sequenceNumber = CustomPageAPI.getMaxSequenceNumber(columnsModule, tabIdCriteria);

                    for (PageColumnContext column : tabColumns) {
                        column.setTabId(tabId);

                        String name = CustomPageAPI.getLinkNameFromObjectOrDefault(column, "column");
                        name = CustomPageAPI.generateUniqueName(name.toLowerCase().replaceAll("[^a-zA-Z0-9]+", ""), existingNamesMap.get(tabId), isSystem);
                        if ((isSystem != null && isSystem) && StringUtils.isNotEmpty(column.getName()) && !column.getName().equalsIgnoreCase(name)) {
                            throw new IllegalArgumentException("linkName already exists, given linkName for column is invalid");
                        }
                        CustomPageAPI.addNameToMap(name, tabId, existingNamesMap);

                        column.setName(name);
                        if (column.getSequenceNumber() <= 0) {
                            column.setSequenceNumber(sequenceNumber += 10);
                        }
                        column.setSysCreatedBy(currentUser);
                        column.setSysCreatedTime(currentTime);

                        columns.add(column);
                    }

                }
            }
            CustomPageAPI.insertPageColumnsToDB(columns);

            //below codes update sysModifiedTime in parent tables
            Map<String, Object> sysModifiedProps = new HashMap<>();
            sysModifiedProps.put("sysModifiedBy", currentUser);
            sysModifiedProps.put("sysModifiedTime", currentTime);
            CustomPageAPI.updateSysModifiedFields(columns.get(0).getTabId(), sysModifiedProps, CustomPageAPI.PageComponent.TAB);
        }

        return false;
    }

    private long getExistingColumnsWidth(long tabId) throws Exception {
        FacilioModule module = ModuleFactory.getPageColumnsModule();
        List<FacilioField> fields = FieldFactory.getPageColumnsFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(new HashSet<>())
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("tabId"), String.valueOf(tabId)))
                .aggregate(BmsAggregateOperators.NumberAggregateOperator.SUM, fieldsMap.get("width"));
        Map<String, Object> map = builder.fetchFirst();
        if (MapUtils.isNotEmpty(map)) {
            return ((BigDecimal) map.get("width")).longValue();
        }
        return 0;
    }
}