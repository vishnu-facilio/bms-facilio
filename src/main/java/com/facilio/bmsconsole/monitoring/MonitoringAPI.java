package com.facilio.bmsconsole.monitoring;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.time.DateTimeUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class MonitoringAPI {
    public static final Logger LOGGER = LogManager.getLogger(MonitoringAPI.class.getName());

    public static long getPreOpenWoCount(Long currentTime, long orgId) {
        long preOpenWoCount =0;

        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);

            SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
                    .aggregate(AggregateOperator.getAggregateOperator(BmsAggregateOperators.CommonAggregateOperator.ACTUAL.getStringValue()), FieldFactory.getIdField(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER)))
                    .module(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER))
                    .beanClass(WorkOrderContext.class)
                    .andCustomWhere("CREATED_TIME >= ? AND CREATED_TIME <= ? AND MODULE_STATE IS NULL", DateTimeUtil.getLastNHour(currentTime, 1), currentTime)
                    .skipModuleCriteria();

            SelectRecordsBuilder.BatchResult<WorkOrderContext> batchResult = builder.getInBatches("WorkOrders.ID", 5000);
            while (batchResult.hasNext()) {
                List<WorkOrderContext> workOrderContextList = batchResult.get();
                preOpenWoCount += workOrderContextList.get(0).getId();
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred during fetching count of pre-open workorders, " + e);
        }
        return preOpenWoCount;
    }

    public static String getKeyName(long orgId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(IAMAccountConstants.getOrgFields())
                .table(IAMAccountConstants.getOrgModule().getTableName());

        selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
        selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        if(props.isEmpty()){
            return "null";
        }
        Map<String, Object> props1 = props.get(0);
        String domainName = (String) props1.get("domain");
        return domainName;
    }

}
