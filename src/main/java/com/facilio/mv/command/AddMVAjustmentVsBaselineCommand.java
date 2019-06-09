package com.facilio.mv.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVAdjustmentVsBaseline;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProject;
import com.facilio.mv.util.MVUtil;

public class AddMVAjustmentVsBaselineCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		MVProject mvProject = (MVProject) context.get(MVUtil.MV_PROJECT);
		
		List<MVAdjustmentVsBaseline> ajustmentsVsBaselineContexts = mvProject.getAjustmentVsBaseline();
		
		Map<String,MVBaseline> baselineNameMap = getBaseLineNameMap(mvProject);
		
		Map<String,MVAdjustment> adjustmentNameMap = getAjustmentNameMap(mvProject);
		
		fillMVAjustmentContext(ajustmentsVsBaselineContexts, baselineNameMap, adjustmentNameMap);
		
		FacilioModule module = ModuleFactory.getMVAjuststmentVsBaselineModule();
		List<FacilioField> fields = FieldFactory.getMVAjuststmentVsBaselineFields();
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(fields);
		
		for(MVAdjustmentVsBaseline ajustmentsVsBaselineContext :ajustmentsVsBaselineContexts ) {
			insert.addRecord(FieldUtil.getAsProperties(ajustmentsVsBaselineContext));
		}
		
		insert.save();
		
		return false;
	}
	
	void fillMVAjustmentContext(List<MVAdjustmentVsBaseline> ajustmentsVsBaselineContexts,Map<String,MVBaseline> baselineNameMap,Map<String,MVAdjustment> adjustmentNameMap) {
		
		for(MVAdjustmentVsBaseline ajustmentsVsBaselineContext :ajustmentsVsBaselineContexts) {
			ajustmentsVsBaselineContext.setAdjustmentId(adjustmentNameMap.get(ajustmentsVsBaselineContext.getAdjustmentName()).getId());
			ajustmentsVsBaselineContext.setBaselineId(baselineNameMap.get(ajustmentsVsBaselineContext.getBaselineName()).getId());
		}
	}

	
	Map<String,MVBaseline> getBaseLineNameMap(MVProject mvProject) {
		
		List<MVBaseline> baselines = mvProject.getBaselines();
		Map<String,MVBaseline> baselineNameMap = new HashMap<>();
		if(baselines != null) {
			for(MVBaseline baseline :baselines) {
				baselineNameMap.put(baseline.getName(), baseline);
			}
		}
		return baselineNameMap;
	}
	
	Map<String,MVAdjustment> getAjustmentNameMap(MVProject mvProject) {
		
		List<MVAdjustment> adjustments = mvProject.getAjustments();
		Map<String,MVAdjustment> adjustmentNameMap = new HashMap<>();
		if(adjustments != null) {
			for(MVAdjustment adjustment :adjustments) {
				adjustmentNameMap.put(adjustment.getName(), adjustment);
			}
		}
		return adjustmentNameMap;
	}
}
