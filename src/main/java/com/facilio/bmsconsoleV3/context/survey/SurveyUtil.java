package com.facilio.bmsconsoleV3.context.survey;

import com.facilio.bmsconsole.workflow.rule.SurveyResponseRuleContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

import java.util.List;

public class SurveyUtil{

	public static SurveyResponseRuleContext fetchChildRuleId(List<Long> ruleIds) throws Exception{

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(FieldFactory.getSurveyResponseRuleFields()).table(ModuleFactory.getSurveyResponseRuleModule().getTableName()).andCondition(CriteriaAPI.getIdCondition(ruleIds, ModuleFactory.getSurveyResponseRuleModule()));

		return FieldUtil.getAsBeanFromMap(builder.fetchFirst(), SurveyResponseRuleContext.class);
	}
}
