package com.facilio.qa.command;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.bmsconsoleV3.util.InspectionAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.mssql.SelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;

@Log4j
public class FetchRelatedItemsForInspectionTemplateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<InspectionTemplateContext> inspections = Constants.getRecordList((FacilioContext) context);
		LOGGER.info("Reached Fetch Related Items for Inspection Template Command");
		if (CollectionUtils.isNotEmpty(inspections)) {

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			List<Long> inspectionTemplateIds = inspections.stream().map(InspectionTemplateContext::getId).collect(Collectors.toList());

			LOGGER.info("Inspection Template Ids : "+inspectionTemplateIds);

			Map<String, FacilioField> triggerFieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER));

			List<InspectionTriggerContext> triggers = InspectionAPI.getInspectionTrigger(CriteriaAPI.getCondition(triggerFieldMap.get("parent"), inspectionTemplateIds, NumberOperators.EQUALS), true);

			if (triggers != null) {
				Map<Long, List<InspectionTriggerContext>> triggerMap = triggers.stream().collect(Collectors.groupingBy(InspectionTriggerContext::getParentId));

				inspections.forEach((inspection) -> {
					inspection.setTriggers(triggerMap.get(inspection.getId()));
					
				});
				
				fillLastTriggeredTime(inspections);
			}
		}
		
		return false;
	}

	private void fillLastTriggeredTime(List<InspectionTemplateContext> inspections) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_RESPONSE);
		
		Map<String, FacilioField> filedMap = FieldFactory.getAsMap(fields);
		
		for(InspectionTemplateContext inspection : inspections) {
			SelectRecordsBuilder<InspectionResponseContext> select = new SelectRecordsBuilder<InspectionResponseContext>() 
					.moduleName(FacilioConstants.Inspection.INSPECTION_RESPONSE)
					.select(Collections.singletonList(filedMap.get("createdTime")))
					.aggregate(NumberAggregateOperator.MAX, filedMap.get("createdTime"))
					.beanClass(InspectionResponseContext.class)
					.andCondition(CriteriaAPI.getCondition(filedMap.get("parent"), inspection.getId()+"", NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(filedMap.get("status"), InspectionResponseContext.Status.OPEN.getIndex()+"", NumberOperators.EQUALS))
					.groupBy("CREATED_TIME");
					
			List<Map<String, Object>> props = select.getAsProps();
			
			if(props != null && !props.isEmpty()) {
				inspection.setDatum("lastTriggeredTime", props.get(0).get("createdTime"));
			}
		}
		
	}

}
