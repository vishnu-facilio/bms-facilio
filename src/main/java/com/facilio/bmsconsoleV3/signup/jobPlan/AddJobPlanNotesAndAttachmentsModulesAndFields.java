package com.facilio.bmsconsoleV3.signup.jobPlan;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.StringField;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AddJobPlanNotesAndAttachmentsModulesAndFields add the jobplannotes, jobPlanAttachments modules and fields entry, for
 * JobPlan Module.
 */
public class AddJobPlanNotesAndAttachmentsModulesAndFields extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jobPlanModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);

        if(jobPlanModule != null){
            long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();

            FacilioModule jobPlanNotesModule = constructJobPlanNotesModule(modBean, orgId);
            FacilioModule jobPlanAttachmentsModule = constructJobPlanAttachmentsModule(modBean, orgId);

            // add jobplannotes, jobPlanAttachments  modules
            SignupUtil.addModules(jobPlanNotesModule, jobPlanAttachmentsModule);

            /* Adding SubModulesRel for jobplan & jobplannotes */
            modBean.addSubModule(jobPlanModule.getModuleId(), jobPlanNotesModule.getModuleId());

            /* Adding SubModulesRel for jobplan & jobplanattachments */
            modBean.addSubModule(jobPlanModule.getModuleId(), jobPlanAttachmentsModule.getModuleId());

        }
    }

    /*
        Helper function to construct the jobplannotes module and its fields.
     */
    private FacilioModule constructJobPlanNotesModule(ModuleBean modBean, long orgId) throws Exception {
        FacilioModule jobPlanNotesModule = new FacilioModule("jobplannotes", "JobPlan Notes",
                "JobPlan_Notes", FacilioModule.ModuleType.NOTES, null,
                null);

        List<FacilioField> fields = new ArrayList<>();

        FacilioField createdTimeField = new FacilioField(jobPlanNotesModule, "createdTime", "Created Time",
                FacilioField.FieldDisplayType.DATETIME, "CREATED_TIME", FieldType.DATE_TIME,
                true, false, true, false);
        fields.add(createdTimeField);

        LookupField createdByField = SignupUtil.getLookupField(jobPlanNotesModule, null, "createdBy",
                "Created By", "CREATED_BY", "users", FacilioField.FieldDisplayType.LOOKUP_POPUP,
                false, false, true, orgId);
        fields.add(createdByField);

        NumberField parentIdField = SignupUtil.getNumberField(jobPlanNotesModule,
                "parentId", "Parent", "PARENT_ID", FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(parentIdField);

        StringField titleField = SignupUtil.getStringField(jobPlanNotesModule,
                "title", "Title",  "TITLE", FacilioField.FieldDisplayType.TEXTBOX,
                false, false, true, false,orgId);
        fields.add(titleField);

        StringField bodyField = SignupUtil.getStringField(jobPlanNotesModule,
                "body", "Body", "BODY", FacilioField.FieldDisplayType.TEXTAREA,
                false, false, true, false,orgId);
        fields.add(bodyField);

        jobPlanNotesModule.setFields(fields);

        return jobPlanNotesModule;
    }

    /*
         Helper function to construct the jobplanattachments module and its fields.
      */
    private FacilioModule constructJobPlanAttachmentsModule(ModuleBean modBean, long orgId) {
        FacilioModule jobPlanAttachmentsModule = new FacilioModule("jobplanattachments", "JobPlan Attachments", 
                "JobPlan_Attachments", FacilioModule.ModuleType.ATTACHMENTS, null,
                null);

        List<FacilioField> fields = new ArrayList<>();

        NumberField fieldIdField = SignupUtil.getNumberField(jobPlanAttachmentsModule, "fileId", "File ID",
                "FILE_ID", FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fieldIdField.setMainField(true);
        fields.add(fieldIdField);

        NumberField parentIdField = SignupUtil.getNumberField(jobPlanAttachmentsModule,
                "parentId", "Parent", "PARENT_ID", FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(parentIdField);

        FacilioField createdTimeField = SignupUtil.getNumberField(jobPlanAttachmentsModule,
                "createdTime", "Created Time","CREATED_TIME",
                FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(createdTimeField);

        FacilioField attachmentTypeField = SignupUtil.getNumberField(jobPlanAttachmentsModule,
                "type", "Type", "ATTACHMENT_TYPE",
                FacilioField.FieldDisplayType.NUMBER,
                true, false, true, orgId);
        fields.add(attachmentTypeField);


        jobPlanAttachmentsModule.setFields(fields);

        return jobPlanAttachmentsModule;
    }
}
