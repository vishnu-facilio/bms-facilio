package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ToolTransactionsModule extends BaseModuleConfig{
    public ToolTransactionsModule(){
        setModuleName(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> toolTransaction = new ArrayList<FacilioView>();
        toolTransaction.add(getToolPendingApproval().setOrder(order++));
        toolTransaction.add(getAllToolApproval().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
        groupDetails.put("views", toolTransaction);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getToolPendingApproval() {

        Criteria criteria = getToolApprovalStateCriteria(ApprovalState.REQUESTED);

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getToolTransactionsModule());

        FacilioView requestedItemApproval = new FacilioView();
        requestedItemApproval.setName("pendingtool");
        requestedItemApproval.setDisplayName("Pending Tool Approvals");
        requestedItemApproval.setCriteria(criteria);
        requestedItemApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        requestedItemApproval.setAppLinkNames(appLinkNames);

        return requestedItemApproval;
    }

    private static FacilioView getAllToolApproval() {

        Criteria criteria = getAllToolApprovalStateCriteria();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getToolTransactionsModule());

        FacilioView rejectedApproval = new FacilioView();
        rejectedApproval.setName("alltool");
        rejectedApproval.setDisplayName("All Tool Approvals");
        rejectedApproval.setCriteria(criteria);
        rejectedApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        rejectedApproval.setAppLinkNames(appLinkNames);

        return rejectedApproval;
    }

    public static Criteria getToolApprovalStateCriteria(ApprovalState status) {

        FacilioField field = new FacilioField();
        field.setName("approvedState");
        field.setColumnName("APPROVED_STATE");
        field.setDataType(FieldType.NUMBER);
        FacilioModule approvalStateModule = ModuleFactory.getToolTransactionsModule();
        field.setModule(approvalStateModule);

        Condition condition = new Condition();
        condition.setField(field);
        condition.setOperator(NumberOperators.EQUALS);
        condition.setValue(String.valueOf(status.getValue()));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(condition);

        return criteria;
    }

    public static Criteria getAllToolApprovalStateCriteria() {
        FacilioField field = new FacilioField();
        field.setName("approvedState");
        field.setColumnName("APPROVED_STATE");
        field.setDataType(FieldType.NUMBER);
        FacilioModule approvalStateModule = ModuleFactory.getToolTransactionsModule();
        field.setModule(approvalStateModule);

        Condition condition = new Condition();
        condition.setField(field);
        condition.setOperator(NumberOperators.EQUALS);
        List<String> list = Arrays.asList(String.valueOf(ApprovalState.REQUESTED.getValue()),
                String.valueOf(ApprovalState.REJECTED.getValue()), String.valueOf(ApprovalState.APPROVED.getValue()));
        condition.setValue(String.join(", ", list));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(condition);

        return criteria;
    }
}
