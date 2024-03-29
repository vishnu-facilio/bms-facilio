package com.facilio.mv.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.context.FormulaFieldContext.ResourceType;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;

public class MVUtil {
	
	public static String MV_PROJECT_WRAPPER = "mvProjectWrapper";
	public static String MV_PROJECT_WRAPPER_OLD = "mvProjectWrapperOld";
	
	public static String MV_PROJECTS = "mvprojects";
	
	public static String MV_BASELINE = "mvBaseline";
	public static String MV_BASELINES = "mvBaselines";
	public static String MV_ADJUSTMENT = "mvAdjustment";
	public static String MV_ADJUSTMENTS = "mvAdjustments";
	public static String MV_ADJUSTMENT_VS_BASELINE = "mvAdjustmentVsBaseline";
	public static String MV_ADJUSTMENT_VS_BASELINES = "mvAdjustmentVsBaselines";
	
	public static String MV_PROJECTS_WIDGET_ID = "widgetId";
	
	public static String RESULT_JSON = "resultJSON";
	
	public static String MV_TARGET_CONSUMPUTION_MODULE = "mvtargetsaving";
	public static String MV_SAVED_CONSUMPUTION_MODULE = "mvactualsaving";
	public static String MV_SAVED_CUMULATIVE_MODULE = "mvcumulativesaving";
	public static String MV_SAVED_PERCENTAGE_MODULE = "mvsavingpercentage";
	
	public static String MV_TARGET_CONSUMPUTION_FIELD = "targetsaving";
	public static String MV_SAVED_CONSUMPPTION_FIELD = "actualsaving";
	public static String MV_SAVED_CUMULATIVE_FIELD = "cumulativesaving";
	public static String MV_SAVED_PERCENTAGE_FIELD = "savingpercentage";
	
	
	public static String MV_SAVE_GOAL_MODULE = "mvsavegoalreading";
	
	public static String MV_SAVE_GOAL_FIELD = "mvsavegoal";
	
	public static String MV_BASELINE_READINGS_MODULE = "mvbaselinereading";
	
	public static String MV_BASELINE_WITH_AJUSTMENT_READINGS_MODULE = "mvbaselinewithadjustmentreading";
	
	public static String MV_AJUSTMENT_READINGS_MODULE = "mvadjustmentreading";
	
	public static String MV_READINGS_BASELINE_FIELD = "baseline";
	public static String MV_READINGS_BASELINE_ADJUSTMENT_FIELD = "adjustedBaseline";
	public static String MV_READINGS_ADJUSTMENT_FIELD = "adjustment";
	
    public static String WORKLFOW_MODULE_INITITALIZATION_STMT = "module = Module(\"${moduleName}\");";
    public static String WORKLFOW_VALUE_FETCH_STMT = "module.fetch({criteria : [parentId == ${parentId} && ttime>=startTime && ttime <endTime],field : \"${fieldName}\",aggregation : \"sum\"});";
    
    public static String WORKLFOW_VALUE_FETCH_STMT_WITHOUT_START_TIME = "module.fetch({criteria : [parentId == ${parentId} && ttime <endTime],field : \"${fieldName}\",aggregation : \"sum\"});";
    public static String WORKLFOW_VALUE_NULL_CHECK_STMT = "if(${var} == null) { ${var} = 0; }";
    public static String WORKLFOW_ADJ_DATE_RANGE_CHECK = "if(startTime >= ${startTime} && endTime < ${endTime}){";
    
    
    public static FacilioField getMVSaveGoalReadingField() throws Exception {
    	
    	ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	FacilioModule module = modbean.getModule(MV_SAVE_GOAL_MODULE);
    	
    	FacilioField field = modbean.getField(MV_SAVE_GOAL_FIELD, MV_SAVE_GOAL_MODULE);
    	
    	field.setModule(module);
    	
    	return field;
    }
    
    public static FacilioField getMVTargetConsumptionField() throws Exception {
    	
    	ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	FacilioModule module = modbean.getModule(MV_TARGET_CONSUMPUTION_MODULE);
    	
    	FacilioField field = modbean.getField(MV_TARGET_CONSUMPUTION_FIELD, MV_TARGET_CONSUMPUTION_MODULE);
    	
    	field.setModule(module);
    	
    	return field;
    }

    public static FacilioField getMVSavedConsumptionField() throws Exception {
    	
    	ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	FacilioModule module = modbean.getModule(MV_SAVED_CONSUMPUTION_MODULE);
    	
    	FacilioField field = modbean.getField(MV_SAVED_CONSUMPPTION_FIELD, MV_SAVED_CONSUMPUTION_MODULE);
    	
    	field.setModule(module);
    	
    	return field;
    }
    
    public static FacilioField getMVCumulativeSavedConsumptionField() throws Exception {
    	
    	ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	FacilioModule module = modbean.getModule(MV_SAVED_CUMULATIVE_MODULE);
    	
    	FacilioField field = modbean.getField(MV_SAVED_CUMULATIVE_FIELD, MV_SAVED_CUMULATIVE_MODULE);
    	
    	field.setModule(module);
    	
    	return field;
    }
    
    public static FacilioField getMVSavedPercentageField() throws Exception {
    	
    	ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	FacilioModule module = modbean.getModule(MV_SAVED_PERCENTAGE_MODULE);
    	
    	FacilioField field = modbean.getField(MV_SAVED_PERCENTAGE_FIELD, MV_SAVED_PERCENTAGE_MODULE);
    	
    	field.setModule(module);
    	
    	return field;
    }
    
    public static FacilioField getMVBaselineReadingField() throws Exception {
    	
    	ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	FacilioModule module = modbean.getModule(MV_BASELINE_READINGS_MODULE);
    	
    	FacilioField field = modbean.getField(MV_READINGS_BASELINE_FIELD, MV_BASELINE_READINGS_MODULE);
    	
    	field.setModule(module);
    	
    	return field;
    }
    
    public static FacilioField getMVAdjustmentReadingField(int count) throws Exception {
    	
    	ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	FacilioModule module = modbean.getModule(MV_AJUSTMENT_READINGS_MODULE);
    	
    	FacilioField field = modbean.getField(MV_READINGS_ADJUSTMENT_FIELD+count, MV_AJUSTMENT_READINGS_MODULE);
    	
    	field.setModule(module);
    	
    	return field;
    }
    
    public static FacilioField getMVBaselineAdjustmentReadingField() throws Exception {
    	
    	ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	FacilioModule module = modbean.getModule(MV_BASELINE_WITH_AJUSTMENT_READINGS_MODULE);
    	
    	FacilioField field = modbean.getField(MV_READINGS_BASELINE_ADJUSTMENT_FIELD, MV_BASELINE_WITH_AJUSTMENT_READINGS_MODULE);
    	
    	field.setModule(module);
    	
    	return field;
    }
	
	public static void fillFormulaFieldDetailsForAdd(FormulaFieldContext formulaFieldContext,MVProjectContext mvProject,MVBaseline baseline,MVAdjustment mvAdjustment, Context context) throws Exception {
		
		formulaFieldContext.setFormulaFieldType(FormulaFieldType.M_AND_V_ENPI);
		formulaFieldContext.setTriggerType(TriggerType.SCHEDULE);
		formulaFieldContext.setResourceId(mvProject.getId());
		
//		AssetContext asset = AssetsAPI.getAssetInfo(mvProject.getMeter().getId());
//		formulaFieldContext.setAssetCategoryId(asset.getCategory().getId());
//		formulaFieldContext.setIncludedResources(Collections.singletonList(mvProject.getMeter().getId()));
		
		formulaFieldContext.setResourceType(ResourceType.ONE_RESOURCE);
		formulaFieldContext.setFrequency(mvProject.getFrequency());
		context.put(FacilioConstants.ContextNames.SKIP_FORMULA_HISTORICAL_SCHEDULING, Boolean.TRUE);
		if(baseline != null) {
			
			if(formulaFieldContext.getName() == null) {
				formulaFieldContext.setName(mvProject.getName() +" - "+baseline.getName());
			}
			formulaFieldContext.setStartTime(baseline.getStartTime());
			formulaFieldContext.setEndTime(mvProject.getReportingPeriodEndTime());
		}
		else if(mvAdjustment != null) {
			if(mvAdjustment.getFrequency() > 0) {
				formulaFieldContext.setFrequency(mvAdjustment.getFrequency());
			}
			if(formulaFieldContext.getName() == null) {
				formulaFieldContext.setName(mvProject.getName() +" - "+mvAdjustment.getName());
			}
			formulaFieldContext.setStartTime(mvAdjustment.getStartTime());
			formulaFieldContext.setEndTime(mvAdjustment.getEndTime());
		}
		else {
			formulaFieldContext.setStartTime(mvProject.getReportingPeriodStartTime());
			formulaFieldContext.setEndTime(mvProject.getReportingPeriodEndTime());
			formulaFieldContext.setName(mvProject.getName() +" - Save Goal Formula");
		}
	}
	
	public static void fillFormulaFieldDetailsForUpdate(FormulaFieldContext formulaFieldContext,MVProjectContext mvProject,MVBaseline baseline,MVAdjustment mvAdjustment, Context context) throws Exception {
		
		formulaFieldContext.setFormulaFieldType(null);
		formulaFieldContext.setInterval(-1);
		formulaFieldContext.setTriggerType(-1);
		formulaFieldContext.setResourceId(-1);
		
//		formulaFieldContext.setAssetCategoryId(-1l);
//		formulaFieldContext.setIncludedResources(Collections.singletonList(mvProject.getMeter().getId()));
		
		formulaFieldContext.setResourceType(null);
		formulaFieldContext.setFrequency(mvProject.getFrequency());
		context.put(FacilioConstants.ContextNames.SKIP_FORMULA_HISTORICAL_SCHEDULING, Boolean.TRUE);
		if(baseline != null) {
			if(formulaFieldContext.getName() == null) {
				formulaFieldContext.setName(mvProject.getName() +" - "+baseline.getName());
			}
			formulaFieldContext.setStartTime(baseline.getStartTime());
			formulaFieldContext.setEndTime(mvProject.getReportingPeriodEndTime());
		}
		else if(mvAdjustment != null) {
			if(mvAdjustment.getFrequency() > 0) {
				formulaFieldContext.setFrequency(mvAdjustment.getFrequency());
			}
			if(formulaFieldContext.getName() == null) {
				formulaFieldContext.setName(mvProject.getName() +" - "+mvAdjustment.getName());
			}
			formulaFieldContext.setStartTime(mvAdjustment.getStartTime());
			formulaFieldContext.setEndTime(mvAdjustment.getEndTime());
		}
		else {
			formulaFieldContext.setStartTime(mvProject.getReportingPeriodStartTime());
			formulaFieldContext.setEndTime(mvProject.getReportingPeriodEndTime());
			formulaFieldContext.setName(mvProject.getName() +" - Save Goal Formula");
		}
	}
	
	public static MVProjectWrapper getMVProject(long id) throws Exception {
		
		MVProjectWrapper mvProjectWrapper = new MVProjectWrapper();
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule mvProjectModule = modbean.getModule(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		List<FacilioField> mvProjectFields = modbean.getAllFields(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		
		SelectRecordsBuilder<MVProjectContext> selectProject = new SelectRecordsBuilder<MVProjectContext>()
				.module(mvProjectModule)
				.select(mvProjectFields)
				.beanClass(MVProjectContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, mvProjectModule));
		
		List<MVProjectContext> props = selectProject.get();
		if(props == null || props.isEmpty()) {
			throw new Exception("MV Project With ID - "+id+" does not exist");
		}
		MVProjectContext mvProject = props.get(0);
		
		long meterId = mvProject.getMeter().getId();
		mvProject.setMeter(AssetsAPI.getAssetInfo(meterId, true));
		
		mvProjectWrapper.setMvProject(mvProject);
		
		if(mvProject.getSaveGoalFormulaField() != null && mvProject.getSaveGoalFormulaField().getId() > 0) {
			FormulaFieldContext formula = FormulaFieldAPI.getFormulaField(mvProject.getSaveGoalFormulaField().getId());
			mvProject.setSaveGoalFormulaField(formula);
		}
		
		FacilioModule mvBaselineModule = modbean.getModule(FacilioConstants.ContextNames.MV_BASELINE_MODULE);
		List<FacilioField> mvBaselineFields = modbean.getAllFields(FacilioConstants.ContextNames.MV_BASELINE_MODULE);
		
		Map<String, FacilioField> baselineFieldMap = FieldFactory.getAsMap(mvBaselineFields);
		
		SelectRecordsBuilder<MVBaseline> selectBaseline = new SelectRecordsBuilder<MVBaseline>()
				.module(mvBaselineModule)
				.select(mvBaselineFields)
				.beanClass(MVBaseline.class)
				.andCondition(CriteriaAPI.getCondition(baselineFieldMap.get("project"), id+"", NumberOperators.EQUALS));
		
		List<MVBaseline> mvBaselines = selectBaseline.get();
		
		
		for(MVBaseline mvBaseline :mvBaselines) {
			FormulaFieldContext formula = FormulaFieldAPI.getFormulaField(mvBaseline.getFormulaField().getId());
			mvBaseline.setFormulaField(formula);
			formula = FormulaFieldAPI.getFormulaField(mvBaseline.getFormulaFieldWithAjustment().getId());
			mvBaseline.setFormulaFieldWithAjustment(formula);
			if (mvBaseline.getTargetConsumption() != null) {
				formula = FormulaFieldAPI.getFormulaField(mvBaseline.getTargetConsumption().getId());

				mvBaseline.setTargetConsumption(formula);
			}
			if (mvBaseline.getSavedConsumption() != null) {
				formula = FormulaFieldAPI.getFormulaField(mvBaseline.getSavedConsumption().getId());

				mvBaseline.setSavedConsumption(formula);
			}
			if (mvBaseline.getCumulativeSavedConsumption() != null) {
				formula = FormulaFieldAPI.getFormulaField(mvBaseline.getCumulativeSavedConsumption().getId());

				mvBaseline.setCumulativeSavedConsumption(formula);
			}
			if (mvBaseline.getPercentageSavedConsumption() != null) {
				formula = FormulaFieldAPI.getFormulaField(mvBaseline.getPercentageSavedConsumption().getId());

				mvBaseline.setPercentageSavedConsumption(formula);
			}
		}
		
		mvProjectWrapper.setBaselines(mvBaselines);
		
		FacilioModule mvAjustmentModule = modbean.getModule(FacilioConstants.ContextNames.MV_ADJUSTMENT_MODULE);
		List<FacilioField> mvAjustmentFields = modbean.getAllFields(FacilioConstants.ContextNames.MV_ADJUSTMENT_MODULE);
		
		Map<String, FacilioField> ajustmentFieldMap = FieldFactory.getAsMap(mvAjustmentFields);
		
		SelectRecordsBuilder<MVAdjustment> selectAjustment = new SelectRecordsBuilder<MVAdjustment>()
				.module(mvAjustmentModule)
				.select(mvAjustmentFields)
				.beanClass(MVAdjustment.class)
				.andCondition(CriteriaAPI.getCondition(ajustmentFieldMap.get("project"), id+"", NumberOperators.EQUALS));
		
		List<MVAdjustment> mvAdjustments = selectAjustment.get();
		
		
		for(MVAdjustment mvAdjustment :mvAdjustments) {
			if(mvAdjustment.getFormulaField() != null && mvAdjustment.getFormulaField().getId() > 0) {
				FormulaFieldContext formula = FormulaFieldAPI.getFormulaField(mvAdjustment.getFormulaField().getId());
				mvAdjustment.setFormulaField(formula);
			}
		}
		
		mvProjectWrapper.setAdjustments(mvAdjustments);
		
		return mvProjectWrapper;
	}
	
	public static MVProjectContext getMVProjectContext(long projectId) throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule mvProjectModule = modbean.getModule(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		List<FacilioField> mvProjectFields = modbean.getAllFields(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		
		SelectRecordsBuilder<MVProjectContext> selectProject = new SelectRecordsBuilder<MVProjectContext>()
				.module(mvProjectModule)
				.select(mvProjectFields)
				.beanClass(MVProjectContext.class)
				.andCondition(CriteriaAPI.getIdCondition(projectId, mvProjectModule));
		
		return  selectProject.get().get(0);
		
	}
	
	public static MVBaseline getMVBaseline(Long mvBaselineId) throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule mvBaselineModule = modbean.getModule(FacilioConstants.ContextNames.MV_BASELINE_MODULE);
		List<FacilioField> mvBaselineFields = modbean.getAllFields(FacilioConstants.ContextNames.MV_BASELINE_MODULE);
		
		SelectRecordsBuilder<MVBaseline> selectBaseline = new SelectRecordsBuilder<MVBaseline>()
				.module(mvBaselineModule)
				.select(mvBaselineFields)
				.beanClass(MVBaseline.class)
				.andCondition(CriteriaAPI.getIdCondition(mvBaselineId, mvBaselineModule));
		
		List<MVBaseline> mvBaselines = selectBaseline.get();
		if(mvBaselines != null && !mvBaselines.isEmpty()) {
			return mvBaselines.get(0);
		}
		return null;
	}
	public static MVAdjustment getMVAdjustment(Long mvAdjustmentId) throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule mvAjustmentModule = modbean.getModule(FacilioConstants.ContextNames.MV_ADJUSTMENT_MODULE);
		List<FacilioField> mvAjustmentFields = modbean.getAllFields(FacilioConstants.ContextNames.MV_ADJUSTMENT_MODULE);
		
		SelectRecordsBuilder<MVAdjustment> selectAjustment = new SelectRecordsBuilder<MVAdjustment>()
				.module(mvAjustmentModule)
				.select(mvAjustmentFields)
				.beanClass(MVAdjustment.class)
				.andCondition(CriteriaAPI.getIdCondition(mvAdjustmentId, mvAjustmentModule));
		
		List<MVAdjustment> mvAdjustments = selectAjustment.get();
		
		if(mvAdjustments != null && !mvAdjustments.isEmpty()) {
			return mvAdjustments.get(0);
		}
		return null;
	}

	public static List<MVProjectContext> getMVProjects(Boolean isOpen,Criteria criteria) throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule mvProjectModule = modbean.getModule(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		List<FacilioField> mvProjectFields = modbean.getAllFields(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(mvProjectFields);

		SelectRecordsBuilder<MVProjectContext> selectProject = new SelectRecordsBuilder<MVProjectContext>()
				.module(mvProjectModule)
				.select(mvProjectFields)
				.beanClass(MVProjectContext.class);
		
		if(criteria != null) {
			selectProject.andCriteria(criteria);
		}
		
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		if(scopeCriteria != null){
			selectProject.andCriteria(scopeCriteria);
		}

		if(isOpen != null) {
			selectProject.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), isOpen.toString(), BooleanOperators.IS));
		}
		List<MVProjectContext> mvProjects = selectProject.get();
		if (CollectionUtils.isNotEmpty(mvProjects)) {
			List<Long> resourceIds = mvProjects.stream().map(project -> project.getMeter().getId()).collect(Collectors.toList());
			Map<Long, ResourceContext> resources = ResourceAPI.getExtendedResourcesAsMapFromIds(resourceIds, true);
			if(resources != null && !resources.isEmpty()) {
				for(MVProjectContext project : mvProjects) {
					ResourceContext resourceDetail = resources.get(project.getMeter().getId());
					project.setMeter((AssetContext) resourceDetail);
				}
			}
		}
		
		return mvProjects;
		
	}
	
	public static void updateMVProject(MVProjectContext mvProject) throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		
		UpdateRecordBuilder<MVProjectContext> update = new UpdateRecordBuilder<MVProjectContext>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(mvProject.getId(), module));
		
		update.update(mvProject);
	}
	
	public static void updateMVBaseline(MVBaseline baseline) throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_BASELINE_MODULE);
		List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.MV_BASELINE_MODULE);
		
		UpdateRecordBuilder<MVBaseline> update = new UpdateRecordBuilder<MVBaseline>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(baseline.getId(), module));
		
		update.update(baseline);
	}
	
	public static void updateMVAdjustment(MVAdjustment adjustment) throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_ADJUSTMENT_MODULE);
		List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.MV_ADJUSTMENT_MODULE);
		
		UpdateRecordBuilder<MVAdjustment> update = new UpdateRecordBuilder<MVAdjustment>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(adjustment.getId(), module));
		
		update.update(adjustment);
	}
	
}
