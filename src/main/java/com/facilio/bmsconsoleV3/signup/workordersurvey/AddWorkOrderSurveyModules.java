package com.facilio.bmsconsoleV3.signup.workordersurvey;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;

import java.util.*;

public class AddWorkOrderSurveyModules extends BaseModuleConfig {

	public AddWorkOrderSurveyModules() throws Exception {
		setModuleName(FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_RESPONSE);
	}
	@Override
	public void addData () throws Exception {

		ModuleBean modBean = Constants.getModBean();

		List< FacilioModule > modules = new ArrayList<> ();

		FacilioModule workOrderSurvey = constructSurvey (modBean);
		modules.add (workOrderSurvey);
		FacilioModule surveyResponseModule = constructSurveyResponse (modBean, workOrderSurvey);
		modules.add (surveyResponseModule);

		FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain ();
		addModuleChain.getContext ().put (FacilioConstants.ContextNames.MODULE_LIST, modules);
		addModuleChain.execute ();

		addWorkOrderRollupFields(modBean);
		addSurveyResponseRollUpToWorkOrder(modBean, surveyResponseModule);

	}

	private void addWorkOrderRollupFields(ModuleBean modBean) throws Exception{

		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getDefaultField("fullScore", "Full Score", "FULL_SCORE", FieldType.DECIMAL));
		fields.add(FieldFactory.getDefaultField("totalScore", "Total Score", "TOTAL_SCORE", FieldType.DECIMAL));

		FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, workOrderModule.getName());
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
		chain.execute();

	}

	private void addSurveyResponseRollUpToWorkOrder(ModuleBean modBean, FacilioModule surveyResponseModule) throws Exception{

		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

		FacilioField surveyParentIdField = modBean.getField("parentId", surveyResponseModule.getName());
		FacilioUtil.throwIllegalArgumentException(surveyParentIdField == null, "Parent field of WorkOrder Survey Response cannot be null. This shouldn't happen");

		FacilioField surveyFullScoreField = modBean.getField("fullScore", surveyResponseModule.getName());
		FacilioUtil.throwIllegalArgumentException(surveyFullScoreField == null, "Full score  field of SurveyResponse cannot be null. This shouldn't happen");

		FacilioField surveyTotalScoreField = modBean.getField("totalScore", surveyResponseModule.getName());
		FacilioUtil.throwIllegalArgumentException(surveyFullScoreField == null, "Total score  field of SurveyResponse cannot be null. This shouldn't happen");

		FacilioField workOrderFullScoreField = modBean.getField("fullScore", workOrderModule.getName());
		FacilioUtil.throwIllegalArgumentException(surveyFullScoreField == null, "Full score  field of SurveyResponse cannot be null. This shouldn't happen");

		FacilioField workOrderTotalScoreField = modBean.getField("totalScore", workOrderModule.getName());
		FacilioUtil.throwIllegalArgumentException(surveyFullScoreField == null, "Total score  field of SurveyResponse cannot be null. This shouldn't happen");

		List<RollUpField> rollUpFields = new ArrayList<>();
		rollUpFields.add(constructRollUpField("WorkOrder survey full score RollUp",surveyResponseModule , surveyParentIdField,workOrderModule, workOrderFullScoreField, surveyFullScoreField));
		rollUpFields.add(constructRollUpField("WorkOrder survey Total score RollUp", surveyResponseModule, surveyParentIdField,workOrderModule , workOrderTotalScoreField, surveyTotalScoreField));
		RollUpFieldUtil.addRollUpField(rollUpFields);
	}
	private static RollUpField constructRollUpField(String desc, FacilioModule childModule, FacilioField childLookUpField, FacilioModule parentModule, FacilioField parentRollUpField, FacilioField aggregateField){

		RollUpField rollUp = new RollUpField();
		rollUp.setDescription(desc);
		rollUp.setAggregateFunctionId(BmsAggregateOperators.NumberAggregateOperator.SUM.getValue());
		rollUp.setChildModuleId(childModule.getModuleId());
		rollUp.setChildFieldId(childLookUpField.getFieldId());
		rollUp.setParentModuleId(parentModule.getModuleId());
		rollUp.setParentRollUpFieldId(parentRollUpField.getFieldId());
		rollUp.setAggregateFieldId(aggregateField.getFieldId());
		rollUp.setIsSystemRollUpField(true);

		return rollUp;
	}
	private FacilioModule constructSurveyResponse (ModuleBean modBean, FacilioModule workOrderSurvey) throws Exception {

		FacilioModule module = new FacilioModule (FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_RESPONSE,
				"WorkOrder Survey Responses",
				"WorkOrder_Survey_Responses",
				FacilioModule.ModuleType.Q_AND_A_RESPONSE,
				modBean.getModule (FacilioConstants.Survey.SURVEY_RESPONSE),
				true
		);

		List< FacilioField > fields = new ArrayList<> ();

		LookupField parentField = ( LookupField ) FieldFactory.getDefaultField ("parentId", "Parent Id", "PARENT_ID", FieldType.LOOKUP, true);
		parentField.setLookupModule (modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		fields.add (parentField);

		module.setFields(fields);

		return module;
	}

	private FacilioModule constructSurvey (ModuleBean modBean) throws Exception {

		FacilioModule module = new FacilioModule (FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_TEMPLATE,
				"WorkOrder Survey Templates",
				"WorkOrder_Survey_Templates",
				FacilioModule.ModuleType.Q_AND_A,
				modBean.getModule (FacilioConstants.Survey.SURVEY_TEMPLATE),
				true
		);

		return module;
	}
	@Override
	public List<Map<String, Object>> getViewsAndGroups() throws Exception {
		List<Map<String, Object>> groupVsViews = new ArrayList<>();
		Map<String, Object> groupDetails;

		int order = 1;
		ArrayList<FacilioView> surveyResponse = new ArrayList<FacilioView>();
		surveyResponse.add(getViewOne().setOrder(order++));

		groupDetails = new HashMap<>();
		groupDetails.put("name", "systemviews");
		groupDetails.put("displayName", "System Views");
		groupDetails.put("moduleName", FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_RESPONSE);
		groupDetails.put("views", surveyResponse);
		groupVsViews.add(groupDetails);

		return groupVsViews;
	}
	private static FacilioView getViewOne() throws Exception{

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "WorkOrder_Survey_Responses.ID", FieldType.NUMBER), true));

		FacilioView workOrderSurveyListView = new FacilioView();
		workOrderSurveyListView.setName("workordersurveylist");

		workOrderSurveyListView.setModuleName(FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_RESPONSE);
		workOrderSurveyListView.setDisplayName("All Surveys");
		workOrderSurveyListView.setSortFields(sortFields);

		List<ViewField> columns = new ArrayList<ViewField>();

		columns.add(new ViewField("name", "Survey"));
		columns.add(new ViewField("responseStatus", "Completion Status"));
		columns.add(new ViewField("assignedTo", "Survey Respondent"));

		workOrderSurveyListView.setFields(columns);
		return workOrderSurveyListView;
	}

}
