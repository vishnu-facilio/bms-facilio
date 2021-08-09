package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class ChangeFormRuleStatusCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		Boolean status = formRule.getStatus();
		if (formRule != null && formRule.getId() > 0 && status != null) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getFormRuleModule().getTableName())
					.fields(FieldFactory.getFormRuleFields())
					.andCondition(CriteriaAPI.getIdCondition(formRule.getId(), ModuleFactory.getFormRuleModule()));

			Map<String, Object> props = FieldUtil.getAsProperties(formRule);
			props.put("status", status);
			updateBuilder.update(props);
		}
		
		return false;
	}

}
