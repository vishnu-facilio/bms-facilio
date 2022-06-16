package com.facilio.bmsconsole.jobs;

import com.amazonaws.util.CollectionUtils;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.email.EmailClient;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class PreOpenWoObservation extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(PreOpenWoObservation.class.getName());

    @Override
    public void execute(JobContext jobContext) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        // JOB_STATUS of 1, filters WOs which are in pre-created state
        Condition jobStatusCondition = CriteriaAPI.getCondition(fieldMap.get("jobStatus"), "1", NumberOperators.EQUALS);

        // PM_ID not being empty, filters WOs which are generated only from PM.
        Condition pmCondition = CriteriaAPI.getCondition(fieldMap.get("pm"), CommonOperators.IS_NOT_EMPTY);

        // 1 hour of observation range
        Condition fromTime = CriteriaAPI.getCondition(
                fieldMap.get("createdTime"),
                "unix_timestamp(DATE_SUB(NOW(), INTERVAL 1 HOUR)) * 1000", NumberOperators.GREATER_THAN_EQUAL);
        Condition toTime = CriteriaAPI.getCondition(
                fieldMap.get("createdTime"),
                "unix_timestamp(NOW()) * 1000", NumberOperators.LESS_THAN);

        SelectRecordsBuilder<V3WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.select(fields)
                .module(module)
                .beanClass(V3WorkOrderContext.class)
                .andCondition(pmCondition)
                .andCondition(jobStatusCondition)
                .andCondition(fromTime)
                .andCondition(toTime)
                .skipModuleCriteria();
        List<V3WorkOrderContext> wos = selectRecordsBuilder.get();

        if (CollectionUtils.isNullOrEmpty(wos)) {
            LOGGER.info("pre-open observation :: all workorders intact, no anomalies");
            return;
        }

        StringBuilder bdr = new StringBuilder();
        for (V3WorkOrderContext wo : wos) {
            bdr.append("ORG ID:" + wo.getOrgId() + "; WO ID: " + wo.getId() + "; PM ID: " + wo.getPm() + "\n");
            LOGGER.warn("pre-open observation :: erroneous WO - ORG ID:" + wo.getOrgId() + "; WO ID: " + wo.getId() + "; PM ID: " + wo.getPm().getId());
        }

        try {
            SendEmailAlert(bdr.toString(), jobContext.getOrgId());
        } catch (Exception e) {
            LOGGER.error("Email Exception: " + e);
        }

    }

    private void SendEmailAlert(String message, long orgID) throws Exception {
        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PM_OBSERVATION)) {
            LOGGER.info("skipping email");
            return;
        }
        LOGGER.info("sending email");
        EMailTemplate template = new EMailTemplate();
        template.setFrom(EmailClient.getFromEmail("alert"));
        template.setTo("pm-issues@facilio.com");
        template.setMessage(message);
        template.setSubject("PreOpen Observation Alert | ORG " + orgID);
        JSONObject emailJSON = template.getOriginalTemplate();
        FacilioFactory.getEmailClient().sendEmail(emailJSON);
    }
}
