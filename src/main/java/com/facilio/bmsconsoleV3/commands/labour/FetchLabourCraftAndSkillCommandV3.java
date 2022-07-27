package com.facilio.bmsconsoleV3.commands.labour;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.SkillsContext;
import com.facilio.bmsconsoleV3.context.labour.LabourContextV3;
import com.facilio.bmsconsoleV3.context.labour.LabourCraftAndSkillContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FetchLabourCraftAndSkillCommandV3 extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		List<LabourContextV3> labours = Constants.getRecordList((FacilioContext) context);

		if(CollectionUtils.isNotEmpty(labours)){

			List<Long> labourIds = labours.stream().map(LabourContextV3::getId).collect(Collectors.toList());

			ModuleBean bean = Constants.getModBean();

			FacilioModule module = bean.getModule(FacilioConstants.CraftAndSKills.LABOUR_CRAFT);

			List<FacilioField> fields = bean.getAllFields(module.getName());

			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

			List<LookupField> lookupFields = Arrays.asList((LookupField) fieldsAsMap.get("craft"), (LookupField) fieldsAsMap.get("skill"));

			SelectRecordsBuilder<LabourCraftAndSkillContext> select = new SelectRecordsBuilder<LabourCraftAndSkillContext>().moduleName(FacilioConstants.CraftAndSKills.LABOUR_CRAFT)
																			  .select(fields)
																			  .beanClass(LabourCraftAndSkillContext.class)
																			  .andCondition(CriteriaAPI.getCondition(Constants.getModBean().getField("labour", FacilioConstants.CraftAndSKills.LABOUR_CRAFT), labourIds, NumberOperators.EQUALS));

			select.fetchSupplements(lookupFields);

			List<LabourCraftAndSkillContext> labourCrafts = select.get();

			if(CollectionUtils.isNotEmpty(labourCrafts)){

				Map<Long, List<LabourCraftAndSkillContext>> labourIdVsLabourContext = labourCrafts.stream().collect(Collectors.groupingBy(p -> p.getLabour().getId()));

				labours.forEach(labour -> labour.setLabourCrafts(labourIdVsLabourContext.get(labour.getId())));
			}
		}

		return false;
	}
}
