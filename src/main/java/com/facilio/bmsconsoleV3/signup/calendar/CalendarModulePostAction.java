package com.facilio.bmsconsoleV3.signup.calendar;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CalendarModulePostAction extends BaseModuleConfig {
    public CalendarModulePostAction(){
        setModuleName(FacilioConstants.Calendar.CALENDAR_EVENT_MAPPING_MODULE_NAME);
    }

    @Override
    public void addForms(List<ApplicationContext> allApplications) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        Map<String, FacilioForm> forms = FormsAPI.getFormsFromDB(module.getName(), Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        for(FacilioForm form : forms.values()){
            Map<Long, FormField> formFieldMap = form.getSections().stream().map(FormSection::getFields).flatMap(List::stream).collect(Collectors.toMap(FormField::getFieldId, Function.identity()));
            disableFieldsOnEditFormRule(form,fieldMap,formFieldMap);
        }
    }
    public static void disableFieldsOnEditFormRule(FacilioForm form,Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception{
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

        typeActionField.setFormFieldId(formFieldMap.get(fieldMap.get("client").getId()).getId());
        actionField.setFormFieldId(formFieldMap.get(fieldMap.get("calendarType").getId()).getId());
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
