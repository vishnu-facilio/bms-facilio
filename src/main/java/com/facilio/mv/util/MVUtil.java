package com.facilio.mv.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.context.FormulaFieldContext.ResourceType;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVAdjustmentVsBaseline;
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
	
    public static String WORKLFOW_MODULE_INITITALIZATION_STMT = "module = Module(\"${moduleName}\");";
    public static String WORKLFOW_VALUE_FETCH_STMT = "module.fetch({criteria : [parentId == ${parentId}],field : \"${fieldName}\",aggregation : \"lastValue\"});";
	
	public static void fillFormulaFieldDetails(FormulaFieldContext formulaFieldContext,MVProjectContext mvProject,MVBaseline baseline,MVAdjustment mvAdjustment) {
		
		formulaFieldContext.setFormulaFieldType(FormulaFieldType.ENPI);
		formulaFieldContext.setTriggerType(TriggerType.SCHEDULE);
		formulaFieldContext.setResourceId(mvProject.getMeter().getId());
		formulaFieldContext.setResourceType(ResourceType.ONE_RESOURCE);
		formulaFieldContext.setFrequency(mvProject.getFrequency());
		if(baseline != null) {
			formulaFieldContext.setName(baseline.getName());
		}
		else if(mvAdjustment != null) {
			formulaFieldContext.setName(mvAdjustment.getName());
		}
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
		
		MVProjectContext mvProject = selectProject.get().get(0);
		
		mvProjectWrapper.setMvProject(mvProject);
		
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
		
		Map<Long,MVAdjustment> mvAdjustmentsIdMap = new HashMap<Long, MVAdjustment>();
		
		for(MVAdjustment mvAdjustment :mvAdjustments) {
			FormulaFieldContext formula = FormulaFieldAPI.getFormulaField(mvAdjustment.getFormulaField().getId());
			mvAdjustment.setFormulaField(formula);
			mvAdjustmentsIdMap.put(mvAdjustment.getId(), mvAdjustment);
		}
		
		mvProjectWrapper.setAdjustments(mvAdjustments);
		
		Map<String, FacilioField> mvAjustmentVsBaselineFieldMap = FieldFactory.getAsMap(FieldFactory.getMVAjuststmentVsBaselineFields());
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getMVAjuststmentVsBaselineModule().getTableName())
				.select(FieldFactory.getMVAjuststmentVsBaselineFields())
				.andCondition(CriteriaAPI.getCondition(mvAjustmentVsBaselineFieldMap.get("projectId"), id+"", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = select.get();
		
		List<MVAdjustmentVsBaseline> mvAjustmentVsBaselines = FieldUtil.getAsBeanListFromMapList(props, MVAdjustmentVsBaseline.class);
		
		for(MVAdjustmentVsBaseline mvAjustmentVsBaseline :mvAjustmentVsBaselines) {
			MVAdjustment adjustment = mvAdjustmentsIdMap.get(mvAjustmentVsBaseline.getAdjustmentId());
			List<MVAdjustmentVsBaseline> adjustmentVsBaselines = adjustment.getAdjustmentVsBaseline() == null ? new ArrayList<>() : adjustment.getAdjustmentVsBaseline();
			adjustmentVsBaselines.add(mvAjustmentVsBaseline);
			adjustment.setAdjustmentVsBaseline(mvAjustmentVsBaselines);
		}
		
		return mvProjectWrapper;
	}
	
	
	public static List<MVProjectContext> getMVProjects() throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule mvProjectModule = modbean.getModule(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		List<FacilioField> mvProjectFields = modbean.getAllFields(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		
		SelectRecordsBuilder<MVProjectContext> selectProject = new SelectRecordsBuilder<MVProjectContext>()
				.module(mvProjectModule)
				.select(mvProjectFields)
				.beanClass(MVProjectContext.class);
		
		List<MVProjectContext> mvProjects = selectProject.get();
		
		return mvProjects;
		
	}
}
