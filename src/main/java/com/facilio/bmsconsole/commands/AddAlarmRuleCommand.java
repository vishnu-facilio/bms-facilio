package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;

public class AddAlarmRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();
		
		fillDefaultPropsForPreRequsite(preRequsiteRule);
		preRequsiteRule.setClearAlarm(alarmRule.isAutoClear());
		long ruleId = WorkflowRuleAPI.addWorkflowRule(preRequsiteRule);
		
		preRequsiteRule.setRuleGroupId(ruleId);
		WorkflowRuleAPI.updateWorkflowRule(preRequsiteRule);
		
		ReadingRuleAPI.addTriggerAndClearRule(alarmRule);
		return false;
	}
	
	private void fillDefaultPropsForPreRequsite(ReadingRuleContext preRequsiteRule) throws Exception {
		preRequsiteRule.setStatus(true);
		preRequsiteRule.setRuleType(WorkflowRuleContext.RuleType.READING_RULE);
		preRequsiteRule.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			
		FacilioChain addReadingChain = TransactionChainFactory.getAddCategoryReadingChain();
		
		FacilioContext context = addReadingChain.getContext();
		
		FacilioField logfield = getAlarmLogBooleanFields(preRequsiteRule);
		
		List<FacilioField> fields = new ArrayList();
		fields.add(logfield);
		
		context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.ASSET_CATEGORY);
		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, ReadingRuleAPI.ALARM_LOG_MODULE_TABLE_NAME);
		context.put(FacilioConstants.ContextNames.READING_DATA_META_TYPE,ReadingDataMeta.ReadingInputType.ALARM_POINT_FIELD);
		context.put(FacilioConstants.ContextNames.READING_NAME, preRequsiteRule.getName());
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, preRequsiteRule.getAssetCategoryId());
		context.put(FacilioConstants.ContextNames.VALIDATION_RULES, null);
		
		addReadingChain.execute();
		
		List<FacilioModule> modules = CommonCommandUtil.getModules(context);
		
		preRequsiteRule.setDataModuleId(modules.get(0).getModuleId());
		preRequsiteRule.setDataModuleFieldId(logfield.getId());
		
	}
	
	private FacilioField getAlarmLogBooleanFields(ReadingRuleContext preRequsiteRule) {
		
		BooleanField alarmLogField = new BooleanField();
		alarmLogField.setDisplayName(preRequsiteRule.getName());
		alarmLogField.setDataType(FieldType.BOOLEAN);
		alarmLogField.setDisplayType(FieldDisplayType.DECISION_BOX);
		alarmLogField.setTrueVal("Alarm");
		alarmLogField.setFalseVal("No Alarm");
		
		return alarmLogField;
	}

}
