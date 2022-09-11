package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.Logger;

import java.util.*;

public class WorkflowLogModule extends BaseModuleConfig{
    public WorkflowLogModule(){
        setModuleName(FacilioConstants.Workflow.WORKFLOW_LOG);
    }

    private static final Logger LOGGER = Logger.getLogger(WorkflowLogModule.class.getName());

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workflowlogModule = new ArrayList<FacilioView>();
        workflowlogModule.add(getWorkflowLog().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Workflow.WORKFLOW_LOG);
        groupDetails.put("views", workflowlogModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getWorkflowLog() {

        FacilioView workflowLogView = new FacilioView();
        workflowLogView.setName("all");
        workflowLogView.setDisplayName(FacilioConstants.Workflow.WORKFLOW_LOG_VIEW);

        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.Workflow.WORKFLOW_LOG);
            createdTime.setModule(module);
        } catch (Exception e) {
            LOGGER.info(e);
        }

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        workflowLogView.setSortFields(sortFields);

        return workflowLogView;
    }
}

