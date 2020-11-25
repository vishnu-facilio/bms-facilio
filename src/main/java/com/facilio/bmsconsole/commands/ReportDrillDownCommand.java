package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDrilldownParamsContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.util.ReportUtil;

public class ReportDrilldownCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ReportDrilldownParamsContext drillDownParamCtx = (ReportDrilldownParamsContext) context
				.get(FacilioConstants.ContextNames.REPORT_DRILLDOWN_PARAMS);
		
		if (drillDownParamCtx != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

			ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);

			
			FacilioModule module = null;

			if (reportContext.getModuleId() > 0) {
				module = modBean.getModule(reportContext.getModuleId());
			} else if (StringUtils.isNotEmpty(moduleName)) {
				module = modBean.getModule(moduleName);
			}

			if (module == null) {
				throw new Exception("Module name should not be empty");
			}

			if (reportContext.getDataPoints() == null || reportContext.getDataPoints().isEmpty()) {
				return false;
			}
			ReportFieldContext dPXAxis=this.getDataPointXAxis(drillDownParamCtx.getxField(),module);

			Criteria drilldownCriteria = getDrillDownCriteria(drillDownParamCtx.getCriteria());

		}
		return false;
	}

	private ReportFieldContext getDataPointXAxis(JSONObject xAxisJSON,FacilioModule module) throws Exception {
		ReportFieldContext xAxis = new ReportFieldContext();

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField xField = null;
		if (xAxisJSON.containsKey("field_id")) {
			Object fieldId = xAxisJSON.get("field_id");
			xField = ReportUtil.getField(modBean, fieldId,module);
		}
		if (xAxisJSON.containsKey("lookupFieldId")) {
			xAxis.setLookupFieldId((Long) xAxisJSON.get("lookupFieldId"));
		}
		return xAxis;
	}

	private Criteria getDrillDownCriteria(JSONArray drilldownParamsCriteria) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Criteria criteria = new Criteria();

		for (Object drillStepCriteria : drilldownParamsCriteria) {
			JSONObject drillStepCriteriaJson = (JSONObject) drillStepCriteria;
			JSONObject xField = (JSONObject) drillStepCriteriaJson.get("xField");
			long fieldId = (long) xField.get("field_id");
			long moduleId = (long) xField.get("module_id");
			int xAggrOperator = (int) xField.get("xAggr");
			AggregateOperator xAggr = AggregateOperator.getAggregateOperator(xAggrOperator);
			String commaSepValues = (String) drillStepCriteriaJson.get("values");

			FacilioModule module = modBean.getModule(moduleId);
			FacilioField xAxisField = modBean.getField(fieldId);

			Condition condition = CriteriaAPI.getCondition(xAxisField, commaSepValues, getOperator(xAxisField, xAggr));
			criteria.addAndCondition(condition);
		}

		return criteria;
	}

	private Operator getOperator(FacilioField field, AggregateOperator xAggr) {
		Operator operator = null;
		FieldType dataType = field.getDataTypeEnum();

		switch (dataType) {
		case ENUM:
		case SYSTEM_ENUM:
			operator = PickListOperators.IS;

			break;
		case LOOKUP:
			LookupField lookupField = (LookupField) field;
			String lookupModuleName = lookupField.getLookupModule().getName();
			if (lookupModuleName.equals("basespace")
					|| (lookupModuleName.equals("resource") && ReportUtil.isSpaceAggregation(xAggr))) {
				operator = BuildingOperator.BUILDING_IS;
			} else {
				operator = PickListOperators.IS;
			}

			break;
		case DATE:
		case DATE_TIME:
			operator = DateOperators.BETWEEN;

			break;

		}
		return operator;
	}

}
