package com.facilio.qa.rules.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.templates.DefaultTemplate;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.pojo.QAndARule;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class AddRuleActionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<QAndARule> rules = (List<QAndARule>) context.get(Constants.Command.RULES);

        if (CollectionUtils.isNotEmpty(rules)) {
            for (QAndARule rule : rules) {
                List<ActionContext> actions = rule.getActions();
                if (CollectionUtils.isNotEmpty(actions)) {
                    actions = addActions(actions, rule);

                    addQandARuleActionRel(rule.getId(), actions);
                    rule.setActions(actions);
                }
            }
        }
        return false;
    }

    private void addQandARuleActionRel(Long ruleId, List<ActionContext> actions) throws SQLException {
        for(ActionContext action:actions) {
            Map<String, Object> qandaRuleActionProps = new HashMap<String, Object>();
            qandaRuleActionProps.put("ruleId", ruleId);
            qandaRuleActionProps.put("actionId", action.getId());
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(Constants.ModuleFactory.evalRuleActionRelModule().getTableName())
                    .fields(Constants.FieldFactory.evalRuleActionRelFields())
                    .addRecord(qandaRuleActionProps);
            insertBuilder.save();
        }
    }

    private List<ActionContext> addActions(List<ActionContext> actions, QAndARule rule) throws Exception {

        List<ActionContext> actionsToBeAdded = new ArrayList<>();
        for (ActionContext action : actions) {
            if (action.getId() == -1) {
                if (action.getTemplate() == null && action.getTemplateJson() != null) {
                    switch (action.getActionTypeEnum()) {
                        default:
                            break;
                    }
                }
                if (action.getTemplateId() == -1) {
                    action.setTemplateId(TemplateAPI.addTemplate(action.getTemplate()));
                }
                actionsToBeAdded.add(action);
            }
        }
        ActionAPI.addActions(actionsToBeAdded);
        return actions;
    }
}
