package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.ReportInfo;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetScheduledViewCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long scheduledViewId = (long) context.get(FacilioConstants.ContextNames.ID);

        FacilioModule module = ModuleFactory.getViewScheduleInfoModule();
        List<FacilioField> fields = FieldFactory.getViewScheduleInfoFields();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(scheduledViewId, module));
        Map<String, Object> scheduleViewProp = selectBuilder.fetchFirst();

        ReportInfo report = null;
        if (MapUtils.isNotEmpty(scheduleViewProp)) {
            long viewId = (long) scheduleViewProp.get("viewId");
            Map<String, Object> viewObj = getFacilioView(viewId);

            report = FieldUtil.getAsBeanFromMap(scheduleViewProp, ReportInfo.class);
            report.setReportContext(FieldUtil.getAsBeanFromMap(scheduleViewProp, ReportContext.class));
            report.setEmailTemplate((EMailTemplate) TemplateAPI.getTemplate(report.getTemplateId()));
            report.setViewObj(viewObj);

            List<JobContext> activeJobs = FacilioTimer.getActiveJobs(Collections.singletonList(report.getId()), "ViewEmailScheduler");
            if (CollectionUtils.isNotEmpty(activeJobs)) {
                report.setJob(activeJobs.get(0));
            }
        }

        context.put(FacilioConstants.ViewConstants.SCHEDULED_VIEW, report);

        return false;
    }

    public static Map<String, Object> getFacilioView(long viewId) throws Exception {
        FacilioView facilioView = ViewAPI.getView(viewId);
        FacilioUtil.throwIllegalArgumentException(facilioView == null, "View not found");

        Map<String, Object> resultView = new HashMap<>();
        resultView.put("viewId", facilioView.getId());
        resultView.put("name", facilioView.getName());
        resultView.put("displayName", facilioView.getDisplayName());

        return resultView;
    }
}
