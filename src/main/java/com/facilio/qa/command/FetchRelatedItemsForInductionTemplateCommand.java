package com.facilio.qa.command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.induction.InductionTemplateContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTriggerContext;
import com.facilio.bmsconsoleV3.util.InductionAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;

public class FetchRelatedItemsForInductionTemplateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<InductionTemplateContext> inductions = Constants.getRecordList((FacilioContext) context);

		if (CollectionUtils.isNotEmpty(inductions)) {

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			List<Long> inspectionTemplateIds = inductions.stream().map(InductionTemplateContext::getId).collect(Collectors.toList());

			Map<String, FacilioField> triggerFieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Induction.INDUCTION_TRIGGER));

			List<InductionTriggerContext> triggers = InductionAPI.getInductionTrigger(CriteriaAPI.getCondition(triggerFieldMap.get("parent"), inspectionTemplateIds, NumberOperators.EQUALS), true);

			if (triggers != null) {
				Map<Long, List<InductionTriggerContext>> triggerMap = triggers.stream().collect(Collectors.groupingBy(InductionTriggerContext::getParentId));

				inductions.forEach((inspection) -> {
					inspection.setTriggers(triggerMap.get(inspection.getId()));
				});
			}
		}
		
		return false;
	}

}
