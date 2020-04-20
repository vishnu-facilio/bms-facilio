package com.facilio.bmsconsole.commands;
import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddHistoricalOperationalAlarmCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(AddHistoricalOperationalAlarmCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long assetId = (long) context.get(FacilioConstants.ContextNames.RESOURCE_ID);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule readingDataMeta = modBean.getModule(FacilioConstants.ContextNames.READING_DATA_META);
        FacilioModule fieldsModule = ModuleFactory.getFieldsModule();
        List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
        fields.addAll(FieldFactory.getSelectFieldFields());
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(readingDataMeta.getTableName())
                .select(fields)
                .innerJoin(fieldsModule.getTableName())
                .on(fieldsModule.getTableName() +".FIELDID = "+ readingDataMeta.getTableName()+".FIELD_ID")
                .andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resourceId", assetId +"", NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("IS_DEFAULT", "isDefault", String.valueOf(true), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition( "VALUE","value", "-1", NumberOperators.NOT_EQUALS))
                .andCondition(CriteriaAPI.getCondition("NAME", "name",
                        "runStatus", StringOperators.IS))
                .orCondition(CriteriaAPI.getCondition("NAME", "name",
                        "pumpRunStatus", StringOperators.IS));

        List<Map<String, Object>> fieldIds = selectBuilder.get();
        if (fieldIds != null && fieldIds.size() > 0 ) {
            for (Map<String, Object> field: fieldIds) {
                // Max and Min date range to run historical
                long readingsFieldId = (long) field.get("fieldId");
                FacilioField readingfield = modBean.getField(readingsFieldId);
                List<FacilioField> readingfields = modBean.getAllFields(readingfield.getModule().getName());
                Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(readingfields);
                FacilioModule readingModule = modBean.getModule(readingfield.getModule().getName());
                FacilioField maxField = BmsAggregateOperators.NumberAggregateOperator.MAX.getSelectField(fieldMap.get("ttime"));
                maxField.setName("max");
                FacilioField minField = BmsAggregateOperators.NumberAggregateOperator.MIN.getSelectField(fieldMap.get("ttime"));
                minField.setName("min");
                List<FacilioField> selectField = new ArrayList<>();
                selectField.add(maxField);
                selectField.add(minField);
                GenericSelectRecordBuilder getMaxTTime = new GenericSelectRecordBuilder()
                        .table(readingModule.getTableName())
                        .select(selectField)
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), assetId+"", NumberOperators.EQUALS))
                        ;
                List<Map<String, Object>> maxTimeProps = getMaxTTime.get();
                if(!maxTimeProps.isEmpty()) {
                    for (Map<String, Object> prop : maxTimeProps) {
                        long maxTtime = (long) prop.get("max");
                        long minTtime = (long) prop.get("min");
                        List<Long> resorceList = new ArrayList<>();
                        if (maxTtime > 0 && minTtime > 0) {
                            resorceList.add(assetId);
                            context.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange(minTtime, maxTtime));
                            context.put(FacilioConstants.ContextNames.RESOURCE_LIST, resorceList);
                            FacilioChain chain = TransactionChainFactory.getExecuteHistoricalRunOpAlarm();
                            chain.execute(context);
                        }

                    }
                }

            }
        }

        LOGGER.info(fieldIds);
        return false;
    }
}
