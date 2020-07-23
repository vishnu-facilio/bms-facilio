package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.jobs.OpenScheduledWO;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.JobContext;

public class OpenUnOpenedWos extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
//        User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
//        if (superAdmin.getOuid() != AccountUtil.getCurrentUser().getOuid()) {
//            if (AccountUtil.getCurrentOrg().getOrgId() != 176L) { // FIXME
//                throw new IllegalArgumentException("You do not have the permission");
//            }
//        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        long maxTime = System.currentTimeMillis();
        long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);

        if (startTime < 0) {
            throw new IllegalArgumentException("Missing startTime");
        }

        Integer limit = (Integer) context.get(FacilioConstants.ContextNames.LIMIT_VALUE);

        SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.select(fields)
                .module(module)
                .beanClass(WorkOrderContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), CommonOperators.IS_EMPTY))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("jobStatus"), PMJobsContext.PMJobsStatus.ACTIVE.getValue() + "," + PMJobsContext.PMJobsStatus.SCHEDULED.getValue(), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("scheduledStart"), String.valueOf(maxTime), NumberOperators.LESS_THAN))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("scheduledStart"), String.valueOf(startTime), NumberOperators.GREATER_THAN))
                .andCustomWhere("WorkOrders.PM_ID IS NOT NULL")
                .skipModuleCriteria();
        if (limit != null && limit > 0) {
            selectRecordsBuilder.limit(limit);
        }

        List<WorkOrderContext> wos = selectRecordsBuilder.get();

        if (wos == null || wos.isEmpty()) {
            return false;
        }

        List<Long> workOrderIds = wos.stream().map(WorkOrderContext::getId).collect(Collectors.toList());

        context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrderIds);
        Boolean doNotExecute = (Boolean) context.get(FacilioConstants.ContextNames.DO_NOT_EXECUTE);

        if (doNotExecute != null && doNotExecute) {
            return false;
        }

        for (long woId: workOrderIds) {
            OpenScheduledWO openScheduledWO = new OpenScheduledWO();
            JobContext jc = new JobContext();
            jc.setJobId(woId);
            openScheduledWO.execute(jc);
            BmsJobUtil.deleteJobWithProps(woId, "OpenScheduledWO");
        }

        return false;
    }
}
