package com.facilio.qa.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.qa.context.AnswerContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FetchAnswersFromTemplateAndResponseCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		long templateId = (long) context.get(FacilioConstants.QAndA.Command.TEMPLATE_ID);
		long responseId = (long) context.get(FacilioConstants.QAndA.Command.RESPONSE_ID);

		ModuleBean modBean = Constants.getModBean();
		FacilioModule module = modBean.getModule(FacilioConstants.QAndA.ANSWER);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField parentField = fieldMap.get("parent");
		FacilioField responseField = fieldMap.get("response");

		SelectRecordsBuilder<AnswerContext> builder = new SelectRecordsBuilder<AnswerContext>()
															  .select(fields)
															  .module(module)
															  .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
															  .andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(templateId), PickListOperators.IS))
															  .andCondition(CriteriaAPI.getCondition(responseField, String.valueOf(responseId), PickListOperators.IS))
				;

		builder.fetchSupplements(fields.stream()
										 .filter(f -> f.getDataTypeEnum().isRelRecordField())
										 .map(SupplementRecord.class::cast)
										 .collect(Collectors.toList()));

		List<AnswerContext> answers = builder.get();

		context.put(FacilioConstants.QAndA.Command.ANSWER_LIST,answers);

		return false;
	}
}
