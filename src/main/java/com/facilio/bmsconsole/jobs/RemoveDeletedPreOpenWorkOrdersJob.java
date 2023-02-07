package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RemoveDeletedPreOpenWorkOrdersJob deletes the "Deleted Pre-Open WorkOrders" that are older than 30 days.
 */
public class RemoveDeletedPreOpenWorkOrdersJob extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(RemoveDeletedPreOpenWorkOrdersJob.class.getName());
    private long orgID = -1L;
    private static final int batchSize = 5000;

    @Override
    public void execute(JobContext jobContext) throws Exception {
        try {

            List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
            if (CollectionUtils.isNotEmpty(orgs)) {
                for (Organization org : orgs) {
                    if (org.getOrgId() > 0) {
                        orgID = org.getOrgId();
                        AccountUtil.setCurrentAccount(orgID);
                        batchDeletePreOpenWorkOrders();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred when ORGID = " + orgID, e);
        }
    }

    /**
     * This method SELECT & DELETE the "Pre-Open workorders that are deleted", in batch.
     *
     * @throws Exception
     */
    private void batchDeletePreOpenWorkOrders() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        Criteria criteria = buildCriteriaForDeletingPreOpenWorkOrders(module, fieldMap);

        SelectRecordsBuilder<V3WorkOrderContext> builder = new SelectRecordsBuilder<V3WorkOrderContext>()
                .module(module)
                .select(fields)
                .beanClass(V3WorkOrderContext.class)
                .andCriteria(criteria)
                .skipModuleCriteria()
                .fetchDeleted();

        SelectRecordsBuilder.BatchResult<V3WorkOrderContext> batchResult = builder.getInBatches(FieldFactory.getIdField(module).getCompleteColumnName(), batchSize);

        List<List<Long>> workOrderIdsToBeDeleted = new ArrayList<>();
        int i = 1;

        while (batchResult.hasNext()) {
            List<V3WorkOrderContext> workOrderContextList = batchResult.get();
            LOGGER.info("Batch ID = " + i++ + ". Count of WorkOrder Records Fetched for this batch = " + workOrderContextList.size());
            workOrderIdsToBeDeleted.add(workOrderContextList.stream().map(V3WorkOrderContext::getId).collect(Collectors.toList()));
        }

        for (List<Long> workOrderIds : workOrderIdsToBeDeleted) {
            LOGGER.info("PreOpen WorkOrder Ids to be deleted = " + workOrderIds);
            DeleteRecordBuilder<V3WorkOrderContext> deleteRecordBuilder = new DeleteRecordBuilder<V3WorkOrderContext>()
                    .module(module)
                    .skipModuleCriteria();
            int workOrderDeletedCount = deleteRecordBuilder.batchDeleteById(workOrderIds);
            LOGGER.info("Count of PreOpen WorkOrders deleted = " + workOrderDeletedCount);
        }

        if(CollectionUtils.isEmpty(workOrderIdsToBeDeleted)){
            LOGGER.info("No PreOpen WorkOrders to be deleted. ORGID = " + orgID);
        }
    }

    /**
     * This method returns the criteria required to SELECT the "Pre-Open workorders that are deleted".
     *
     * @param module
     * @param fieldMap
     * @return
     */
    private Criteria buildCriteriaForDeletingPreOpenWorkOrders(FacilioModule module, Map<String, FacilioField> fieldMap) {
        Criteria criteria = new Criteria();
        long dateTimeUtil = DateTimeUtil.addDays(System.currentTimeMillis(), -30);
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(dateTimeUtil), NumberOperators.LESS_THAN));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), CommonOperators.IS_EMPTY));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("jobStatus"),
                String.valueOf(V3WorkOrderContext.JobsStatus.ACTIVE.getValue()), NumberOperators.EQUALS));

        Criteria criteria1 = new Criteria();
        criteria1.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), CommonOperators.IS_NOT_EMPTY));
        criteria1.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("pmV2"), CommonOperators.IS_NOT_EMPTY));
        criteria.andCriteria(criteria1);

        FacilioField sysDeletedField = FieldFactory.getIsDeletedField(module.getParentModule());
        criteria.addAndCondition(CriteriaAPI.getCondition(sysDeletedField, String.valueOf(1), NumberOperators.EQUALS));

        return criteria;
    }
}
