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
                    actions = ActionAPI.addQandARuleActions(actions,rule.getName());
                    addRuleActionRel(rule.getId(), actions);
                    rule.setActions(actions);
                }
            }
        }
        return false;
    }

    private void addRuleActionRel(Long ruleId, List<ActionContext> actions) throws SQLException {

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(Constants.ModuleFactory.evalRuleActionRelModule().getTableName())
                .fields(Constants.FieldFactory.evalRuleActionRelFields());

        for (ActionContext action : actions) {

            Map<String, Object> props = new HashMap<>();
            props.put("ruleId", ruleId);
            props.put("actionId", action.getId());

            insertBuilder.addRecord(props);
            insertBuilder.save();
        }
    }
}
