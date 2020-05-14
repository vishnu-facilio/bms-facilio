package com.facilio.bmsconsole.commands;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class AddHistoricalOperationalAlarmCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(AddHistoricalOperationalAlarmCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
    		
    		if (!AccountUtil.isFeatureEnabled(FeatureLicense.OPERATIONAL_ALARM)) {
    			return false;
    		}
    	
        long assetId = (long) context.get(FacilioConstants.ContextNames.RESOURCE_ID);
        
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Map<String, Object>> fieldIds = AssetsAPI.getRunStatusFields(assetId);
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
                
                
                long startTime = DateTimeUtil.addYears(DateTimeUtil.getCurrenTime(), -1);
                GenericSelectRecordBuilder getMaxTTime = new GenericSelectRecordBuilder()
                        .table(readingModule.getTableName())
                        .select(selectField)
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), assetId+"", NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), startTime+","+System.currentTimeMillis(), DateOperators.BETWEEN))
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
