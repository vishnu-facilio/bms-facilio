package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SystemButtonApi {

    public static List<WorkflowRuleContext> getSystemButtons(FacilioModule module, CustomButtonRuleContext.PositionType... positionTypes) throws Exception{
        if(positionTypes.length == 0){
            throw new IllegalArgumentException("Position Type cannot be null");
        }
        List<Integer> positionTypeInts = new ArrayList<>();
        for (CustomButtonRuleContext.PositionType positionType : positionTypes) {
            positionTypeInts.add(positionType.getIndex());
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("POSITION_TYPE","positionType", StringUtils.join(positionTypeInts,","),
                NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID","moduleId",String.valueOf(module.getModuleId()),NumberOperators.EQUALS));

        Criteria appCriteria = new Criteria();
        appCriteria.addAndCondition(CriteriaAPI.getCondition("APP_ID","appId","NULL", CommonOperators.IS_EMPTY));
        ApplicationContext app = AccountUtil.getCurrentApp();
        if (app != null) {
            appCriteria.addOrCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(app.getId()), NumberOperators.EQUALS));
        }
        criteria.andCriteria(appCriteria);

        String joinCondition = "SystemButtonAppRel.SYSTEM_BUTTON_ID = " + ModuleFactory.getSystemButtonRuleModule().getTableName() + ".ID";
        List<WorkflowRuleContext> systemButtons = WorkflowRuleAPI.getExtendedButtonRules(ModuleFactory.getSystemButtonRuleModule(), FieldFactory.getSystemButtonRuleFields(),
                ModuleFactory.getSystemButtonAppRelModule(),FieldFactory.getSystemButtonAppRelFields(),joinCondition,criteria, null,null, SystemButtonRuleContext.class);

        systemButtons = WorkflowRuleAPI.getWorkFlowsFromMapList(FieldUtil.getAsMapList(systemButtons,SystemButtonRuleContext.class),true,true);

        List<WorkflowRuleContext> systemButtonList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(systemButtons)) {
            systemButtonList.addAll(systemButtons.stream().filter(button -> !((SystemButtonRuleContext)button).isPermissionRequired()).collect(Collectors.toList()));
            for (WorkflowRuleContext systembutton : systemButtons) {
                if (((SystemButtonRuleContext) systembutton).isPermissionRequired()) {
                    String permissionAction = ((SystemButtonRuleContext) systembutton).getPermission();
                    WebTabContext webTab = AccountUtil.getCurrentTab();
                    long tabId = webTab != null ? webTab.getId() : -1;
                    boolean hasPermission = WebTabUtil.currentUserHasPermission(tabId, permissionAction);
                    if (hasPermission) {
                        systemButtonList.add(systembutton);
                    }
                }
            }
        }

        return systemButtonList;
    }

    public static List<WorkflowRuleContext> getExecutableSystemButtons(List<WorkflowRuleContext> systemButtons, String moduleName, ModuleBaseWithCustomFields record, Context context)throws Exception{
        List<WorkflowRuleContext> availableSystemButtons = null;
        if(CollectionUtils.isNotEmpty(systemButtons)){
            availableSystemButtons = new ArrayList<>();
            Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, WorkflowRuleAPI.getOrgPlaceHolders());
            Iterator<WorkflowRuleContext> iterator = systemButtons.iterator();;
            while (iterator.hasNext()){
                WorkflowRuleContext workflowRule = iterator.next();
                boolean evaluate = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(workflowRule, moduleName, record, null, recordPlaceHolders, (FacilioContext) context, false);
                if (evaluate) {
                    availableSystemButtons.add(workflowRule);
                }
            }
        }

        return availableSystemButtons;

    }

    public static WorkflowRuleContext getSystemButton(FacilioModule module, String identifier) throws Exception{
        if (identifier == null){
            return null;
        }
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID","moduleId",String.valueOf(module.getModuleId()),NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("IDENTIFIER","identifier",identifier, StringOperators.IS));

        List<WorkflowRuleContext> systemButtons = WorkflowRuleAPI.getExtendedWorkflowRules(ModuleFactory.getSystemButtonRuleModule(), FieldFactory.getSystemButtonRuleFields(),
                criteria,null,null, SystemButtonRuleContext.class);

        systemButtons = WorkflowRuleAPI.getWorkFlowsFromMapList(FieldUtil.getAsMapList(systemButtons,SystemButtonRuleContext.class),true,true);

        WorkflowRuleContext systemButton = CollectionUtils.isNotEmpty(systemButtons) ? systemButtons.get(0) : null;
        return systemButton;
    }

    public static void addSystemButton(String moduleName, SystemButtonRuleContext rule) throws Exception {
        if(rule == null){
            return;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        rule.setRuleType(WorkflowRuleContext.RuleType.SYSTEM_BUTTON);
        rule.setActivityType(EventType.CUSTOM_BUTTON);
        String identifier = rule.getIdentifier();
        identifier = identifier.replaceAll("\\s+","");
        rule.setIdentifier(identifier);

        if (module != null){
            rule.setModule(module);
        }

        FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();;
        FacilioContext workflowContext = chain.getContext();
        workflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
        chain.execute();
    }

    public static void deleteSystemButtons(List<Long> ids) throws Exception{

        FacilioChain chain = TransactionChainFactory.getDeleteWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, ids);
        chain.execute();

    }

    public static void deleteSystemButtonAppRelForButtonId(long systemButtonId) throws Exception{

        if (systemButtonId == -1l){
            return;
        }

        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getSystemButtonAppRelModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("SYSTEM_BUTTON_ID","systemButtonId", String.valueOf(systemButtonId),NumberOperators.EQUALS));

        builder.delete();

    }

    public static void addCreateButton(String moduleName) throws Exception{
        addCreateButtonWithCustomName(moduleName,"Create");
    }

    public static void addCreateButtonWithModuleDisplayName(String moduleName) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        FacilioUtil.throwIllegalArgumentException(module == null,"Module cannot be null");
        addCreateButtonWithCustomName(moduleName, "New " + module.getDisplayName());
    }

    public static void addCreateButtonWithCustomName(String moduleName , String btnDisPlayName) throws Exception{
        SystemButtonRuleContext createButton = new SystemButtonRuleContext();
        createButton.setName(btnDisPlayName);
        createButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        createButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        createButton.setIdentifier("create");
        createButton.setPermissionRequired(true);
        createButton.setPermission("CREATE");
        addSystemButton(moduleName, createButton);
    }

    public static void addListEditButton(String moduleName) throws Exception{
        SystemButtonRuleContext listEditButton = new SystemButtonRuleContext();
        listEditButton.setName("Edit");
        listEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        listEditButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listEditButton.setIdentifier("edit_list");
        listEditButton.setPermissionRequired(true);
        listEditButton.setPermission("UPDATE");
        addSystemButton(moduleName, listEditButton);
    }

    public static void addSummaryEditButton(String moduleName) throws Exception{
        SystemButtonRuleContext summaryEditButton = new SystemButtonRuleContext();
        summaryEditButton.setName("Edit");
        summaryEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        summaryEditButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        summaryEditButton.setIdentifier("edit_summary");
        summaryEditButton.setPermissionRequired(true);
        summaryEditButton.setPermission("UPDATE");
        addSystemButton(moduleName, summaryEditButton);
    }

    public static void addListDeleteButton(String moduleName) throws Exception{
        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        addSystemButton(moduleName, listDeleteButton);
    }

    public static void addBulkDeleteButton(String moduleName) throws Exception{
        SystemButtonRuleContext bulkDeleteButton = new SystemButtonRuleContext();
        bulkDeleteButton.setName("Delete");
        bulkDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        bulkDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        bulkDeleteButton.setIdentifier("delete_bulk");
        bulkDeleteButton.setPermissionRequired(true);
        bulkDeleteButton.setPermission("DELETE");
        addSystemButton(moduleName, bulkDeleteButton);
    }

    public static void addExportAsCSV(String moduleName) throws Exception{
        SystemButtonRuleContext exportAsCSVButton = new SystemButtonRuleContext();
        exportAsCSVButton.setName("Export As CSV");
        exportAsCSVButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsCSVButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsCSVButton.setIdentifier("export_as_csv");
        exportAsCSVButton.setPermissionRequired(true);
        exportAsCSVButton.setPermission("EXPORT");
        addSystemButton(moduleName, exportAsCSVButton);
    }

    public static void addExportAsExcel(String moduleName) throws Exception{
        SystemButtonRuleContext exportAsExcelButton = new SystemButtonRuleContext();
        exportAsExcelButton.setName("Export As Excel");
        exportAsExcelButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsExcelButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsExcelButton.setIdentifier("export_as_excel");
        exportAsExcelButton.setPermissionRequired(true);
        exportAsExcelButton.setPermission("EXPORT");
        addSystemButton(moduleName, exportAsExcelButton);
    }
}
