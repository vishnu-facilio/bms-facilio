package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ItemTransactionsModule extends BaseModuleConfig{
    public ItemTransactionsModule(){
        setModuleName(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> itemTransaction = new ArrayList<FacilioView>();
        itemTransaction.add(getItemPendingApproval().setOrder(order++));
        itemTransaction.add(getAllItemApproval().setOrder(order++));
        itemTransaction.add(getAllIssuedItems().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
        groupDetails.put("views", itemTransaction);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getAllIssuedItems() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getItemTransactionsModule());

        FacilioView allView = new FacilioView();
        allView.setName("issued-items");
        allView.setDisplayName("Issued Items");

        allView.setFields(getAllIssuedItemsViewColumns());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private List<ViewField> getAllIssuedItemsViewColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();
        columns.add(new ViewField("item","Item"));
        columns.add(new ViewField("storeRoom","Storeroom"));
        columns.add(new ViewField("quantity","Quantity"));
        columns.add(new ViewField("remainingQuantity","Remaining Quantity"));
        columns.add(new ViewField("issuedTo","Issued To"));
        return columns;
    }

    private static FacilioView getItemPendingApproval() {

        Criteria criteria = getItemApprovalStateCriteria(ApprovalState.REQUESTED);

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getItemTransactionsModule());

        FacilioView requestedItemApproval = new FacilioView();
        requestedItemApproval.setName("pendingitem");
        requestedItemApproval.setDisplayName("Pending Item Approvals");
        requestedItemApproval.setCriteria(criteria);
//		requestedItemApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        requestedItemApproval.setAppLinkNames(appLinkNames);

        return requestedItemApproval;
    }

    private static FacilioView getAllItemApproval() {

        Criteria criteria = getAllItemApprovalStateCriteria();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getItemTransactionsModule());

        FacilioView rejectedApproval = new FacilioView();
        rejectedApproval.setName("allitem");
        rejectedApproval.setDisplayName("All Item Approvals");
        rejectedApproval.setCriteria(criteria);
        rejectedApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        rejectedApproval.setAppLinkNames(appLinkNames);

        return rejectedApproval;
    }

    public static Criteria getAllItemApprovalStateCriteria() {
        FacilioField field = new FacilioField();
        field.setName("approvedState");
        field.setColumnName("APPROVED_STATE");
        field.setDataType(FieldType.NUMBER);
        FacilioModule approvalStateModule = ModuleFactory.getItemTransactionsModule();
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

    public static Criteria getItemApprovalStateCriteria(ApprovalState status) {

        FacilioField field = new FacilioField();
        field.setName("approvedState");
        field.setColumnName("APPROVED_STATE");
        field.setDataType(FieldType.NUMBER);
        FacilioModule approvalStateModule = ModuleFactory.getItemTransactionsModule();
        field.setModule(approvalStateModule);

        Condition condition = new Condition();
        condition.setField(field);
        condition.setOperator(NumberOperators.EQUALS);
        condition.setValue(String.valueOf(status.getValue()));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(condition);

        return criteria;
    }
}
