package com.facilio.qa.command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.induction.InductionResponseContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTemplateContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTriggerContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;

public class DeleteInductionTriggersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<InductionTemplateContext> inductions = Constants.getRecordList((FacilioContext) context);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Long> inductionIds = inductions.stream()
			.map(InductionTemplateContext::getId)
			.collect(Collectors.toList())
			;
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Induction.INDUCTION_TRIGGER);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		DeleteRecordBuilder<InductionTriggerContext> deleteBuilder = new DeleteRecordBuilder<InductionTriggerContext>()
				.module(modBean.getModule(FacilioConstants.Induction.INDUCTION_TRIGGER))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parent"), inductionIds, NumberOperators.EQUALS));
		deleteBuilder.delete();
		
		fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Induction.INDUCTION_RESPONSE));
		
		DeleteRecordBuilder<InductionResponseContext> deleteBuilder1 = new DeleteRecordBuilder<InductionResponseContext>()
				.module(modBean.getModule(FacilioConstants.Induction.INDUCTION_RESPONSE))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("sysCreatedTime"), DateTimeUtil.getCurrenTime()+"", DateOperators.IS_AFTER))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), InductionResponseContext.Status.PRE_OPEN.getIndex()+"", EnumOperators.IS));
		deleteBuilder1.delete();
		
		return false;
	}

}
