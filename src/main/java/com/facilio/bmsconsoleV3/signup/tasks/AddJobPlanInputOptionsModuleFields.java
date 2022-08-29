package com.facilio.bmsconsoleV3.signup.tasks;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.*;

import java.util.ArrayList;
import java.util.List;

/**
 * AddJobPlanInputOptionsModuleFields adds the Module-Field entries for Input Options of
 * - JobPlan Task
 * - JobPlan Section
 */
public class AddJobPlanInputOptionsModuleFields extends SignUpData {

    @Override
    public void addData() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        /**
         * JobPlan
         */

        /* JobPlan Task */
        FacilioModule jobPlanTaskModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_TASK);
        FacilioModule jobPlanTaskInputOptionsModule = constructJobPlanTaskInputOptionsModule();
        // Add jobPlanTaskInputOptions module
        SignupUtil.addModules(jobPlanTaskInputOptionsModule);
        /* Adding SubModulesRel for jobPlanTask & jobPlanTaskInputOptions */
        modBean.addSubModule(jobPlanTaskModule.getModuleId(), jobPlanTaskInputOptionsModule.getModuleId());

        /* JobPlan Section */
        FacilioModule jobPlanSectionModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_SECTION);
        FacilioModule jobPlanSectionInputOptionsModule = constructJobPlanSectionInputOptionsModule();
        // Add jobPlanTaskInputOptions module
        SignupUtil.addModules(jobPlanSectionInputOptionsModule);
        /* Adding SubModulesRel for jobPlanSection & jobPlanSectionInputOptions */
        modBean.addSubModule(jobPlanSectionModule.getModuleId(), jobPlanSectionInputOptionsModule.getModuleId());
    }

    /*
        Helper function to construct the jobPlanTaskInputOptions module and its fields.
     */
    private FacilioModule constructJobPlanTaskInputOptionsModule() throws Exception {

        FacilioModule taskInputOptionsModule = ModuleFactory.getJobPlanTaskInputOptionsModule();

        List<FacilioField> fields = new ArrayList<>(FieldFactory.getJobPlanTaskInputOptionsFields().values());

        taskInputOptionsModule.setFields(fields);

        return taskInputOptionsModule;
    }

    /*
        Helper function to construct the jobPlanSectionInputOptions module and its fields.
     */
    private FacilioModule constructJobPlanSectionInputOptionsModule() throws Exception {

        FacilioModule sectionInputOptionsModule = ModuleFactory.getJobPlanSectionInputOptionsModule();
        List<FacilioField> fields = new ArrayList<>(FieldFactory.getJobPlanSectionInputOptionsFields().values());
        sectionInputOptionsModule.setFields(fields);

        return sectionInputOptionsModule;
    }
}
