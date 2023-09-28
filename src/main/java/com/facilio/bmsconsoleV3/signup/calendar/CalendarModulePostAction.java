package com.facilio.bmsconsoleV3.signup.calendar;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CalendarModulePostAction extends BaseModuleConfig {
    public CalendarModulePostAction(){
        setModuleName(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
    }

    @Override
    public void addData() throws Exception {
        super.addData();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        Map<String, FacilioForm> forms = FormsAPI.getFormsFromDB(module.getName(), Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        for(FacilioForm form : forms.values()){
            disableFieldsOnEditFormRule(form);
        }
    }
    public static void disableFieldsOnEditFormRule(FacilioForm form) throws Exception{
        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Calendar Edit Form Disability  Rule");
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FORM_ON_LOAD.getIntVal());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
        singleRule.setExecuteType(2);
        singleRule.setFormId(form.getId());

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
        FormRuleActionContext filterAction = new FormRuleActionContext();
        filterAction.setActionType(FormActionType.DISABLE_FIELD.getVal());

        List<FormRuleActionFieldsContext> actionFieldsContexts = new ArrayList<>();

        FormRuleActionFieldsContext typeActionField = new FormRuleActionFieldsContext();
        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();

        typeActionField.setFormFieldName("Type");
        actionField.setFormFieldName("Client");
        actionFieldsContexts.add(typeActionField);
        actionFieldsContexts.add(actionField);

        filterAction.setFormRuleActionFieldsContext(actionFieldsContexts);

        actions.add(filterAction);

        singleRule.setActions(actions);

        FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
        Context context = chain.getContext();

        context.put(FormRuleAPI.FORM_RULE_CONTEXT,singleRule);

        chain.execute();

    }
}
