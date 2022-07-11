package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.BaseCriteriaAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.ResponseContext;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.v3.context.Constants;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteUnAnsweredSurveyJob extends FacilioJob{
	@Override
	public void execute(JobContext jobContext) throws Exception{

		FacilioChain chain = TransactionChainFactory.updateSurveyMarkAsDeleteChain();
		FacilioContext context = chain.getContext();
		context.put("responseIds",fetchUnansweredExpiredSurvey());

		chain.execute();
	}

	private List<Long> fetchUnansweredExpiredSurvey() throws Exception{

		ModuleBean bean = Constants.getModBean();
		FacilioModule module = bean.getModule(FacilioConstants.QAndA.RESPONSE);

		List<FacilioField> fields = bean.getAllFields(module.getName());
		fields.add(FieldFactory.getIdField(module));

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													 .select(fields)
													 .table(module.getTableName())
													 .andCondition(CriteriaAPI.getCondition("SYS_DELETED_TIME", "sysDeletedTime", null,CommonOperators.IS_EMPTY))
													 .andCondition(CriteriaAPI.getCondition("EXPIRY_DATE","expiryDate",null, CommonOperators.IS_NOT_EMPTY))
													 .andCondition(CriteriaAPI.getCondition("RESPONSE_STATUS","responseStatus",String.valueOf(2), NumberOperators.EQUALS))
													 .andCondition(CriteriaAPI.getCondition("EXPIRY_DATE","expiryDate",String.valueOf(System.currentTimeMillis()), NumberOperators.LESS_THAN));

		List<Map<String,Object>> props = builder.get();

		List<Long> responseIds = props.stream().map(p-> (Long)p.get("id")).collect(Collectors.toList());

		return responseIds;
	}
}
