package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageColumnContext;
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
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Logger;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class AddPageColumnsCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(AddPageColumnsCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long tabId = (long) context.get(FacilioConstants.CustomPage.TAB_ID);
        if(tabId <= 0){
            LOGGER.error("Invalid Tab Id For Creating Column");
            throw new IllegalArgumentException("Invalid Tab Id For Creating Column");
        }

        Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
        List<Long> columnWidths = (List<Long>) context.get(FacilioConstants.CustomPage.COLUMN_WIDTHS);
        Long columnWidthSum = columnWidths.stream().mapToLong(Long::longValue).sum();
        FacilioModule columnsModule = ModuleFactory.getPageColumnsModule();

        FacilioUtil.throwIllegalArgumentException(columnWidthSum != 12, "Invalid column widths");
        FacilioUtil.throwIllegalArgumentException(getExistingColumnsWidth(tabId) != 0, "Column already exists");

        if(CollectionUtils.isNotEmpty(columnWidths)) {

            FacilioField tabIdField = FieldFactory.getNumberField("tabId", "TAB_ID",columnsModule);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getEqualsCondition(tabIdField,String.valueOf(tabId)));

            List<String> nameList = new ArrayList<>();
            double sequenceNumber = 0;
            long createdTime = System.currentTimeMillis();
            long currentUser = Objects.requireNonNull(AccountUtil.getCurrentUser()).getId();

            List<PageColumnContext> columns = new ArrayList<>();

            for(long width : columnWidths){
                PageColumnContext column = new PageColumnContext();

                column.setTabId(tabId);
                column.setWidth(width);
                column.setSequenceNumber(sequenceNumber+=10);

                String name = column.getDisplayName() != null ? column.getDisplayName() : "column";
                name = CustomPageAPI.generateUniqueName(name, nameList, isSystem);
                nameList.add(name);

                column.setName(name);
                column.setSysCreatedBy(currentUser);
                column.setSysCreatedTime(createdTime);

                columns.add(column);
            }

            columns = CustomPageAPI.insertPageColumnsToDB(columns);

            if(columns != null) {
                List<Long> ids = columns.stream().map(PageColumnContext::getId).collect(Collectors.toList());
                context.put(FacilioConstants.CustomPage.COLUMN_IDS, ids);
            }
        }
         return false;
    }
   private long getExistingColumnsWidth(long tabId)throws Exception{
        FacilioModule module = ModuleFactory.getPageColumnsModule();
        List<FacilioField> fields = FieldFactory.getPageColumnsFields();
        Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(new HashSet<>())
                .andCondition(CriteriaAPI.getEqualsCondition(fieldsMap.get("tabId"),String.valueOf(tabId)))
                .aggregate(BmsAggregateOperators.NumberAggregateOperator.SUM,fieldsMap.get("width"));
        Map<String, Object> map = builder.fetchFirst();
        if (MapUtils.isNotEmpty(map)) {
            return ((BigDecimal) map.get("width")).longValue();
        }
        return 0;
    }
}