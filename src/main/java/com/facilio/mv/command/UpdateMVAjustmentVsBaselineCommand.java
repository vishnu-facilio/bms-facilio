package com.facilio.mv.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVAdjustmentVsBaseline;
import com.facilio.mv.context.MVProject;
import com.facilio.mv.util.MVUtil;

public class UpdateMVAjustmentVsBaselineCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		MVProject mvProject = (MVProject) context.get(MVUtil.MV_PROJECT);
		MVProject mvProjectOld = (MVProject) context.get(MVUtil.MV_PROJECT_OLD);
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = ModuleFactory.getMVAjuststmentVsBaselineModule();
		List<FacilioField> fields = FieldFactory.getMVAjuststmentVsBaselineFields();
		
		List<MVAdjustmentVsBaseline> adjustmentVsBaselines = new ArrayList<>();
		
		List<MVAdjustment> adjustments =  mvProject.getAdjustments();
		for(MVAdjustment adjustment :adjustments) {
			adjustmentVsBaselines.addAll(adjustment.getAjustmentVsBaseline());
		}
		
		List<MVAdjustmentVsBaseline> oldAdjustmentVsBaselines = new ArrayList<>();
		
		List<MVAdjustmentVsBaseline> deletedAdjustmentVsBaselines = new ArrayList<MVAdjustmentVsBaseline>();
		
		for(MVAdjustmentVsBaseline oldAdjustmentVsBaseline :oldAdjustmentVsBaselines) {
			
			boolean foundFlag = false;
			for(MVAdjustmentVsBaseline adjustmentVsBaseline :adjustmentVsBaselines) {
				if(oldAdjustmentVsBaseline.getId() == adjustmentVsBaseline.getId()) {
					foundFlag = true;
					break;
				}
			}
			if(!foundFlag) {
				deletedAdjustmentVsBaselines.add(oldAdjustmentVsBaseline);
			}
		}
		
		if(adjustmentVsBaselines != null) {
			
			for(MVAdjustmentVsBaseline adjustmentVsBaseline :adjustmentVsBaselines) {
				
				if(adjustmentVsBaseline.getId() < 0) {

					context.put(MVUtil.MV_ADJUSTMENT_VS_BASELINE, adjustmentVsBaseline);
					Chain chain = TransactionChainFactory.getAddMVAjustmentVsBaselineChain();
					chain.execute(context);
				}
				else {
					
					context.put(FacilioConstants.ContextNames.FORMULA_FIELD, adjustmentVsBaseline.getFormulaField());
					
					Chain updateEnPIChain = FacilioChainFactory.updateFormulaChain();
					updateEnPIChain.execute(context);
					
					GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
							.table(module.getTableName())
							.fields(fields)
							.andCondition(CriteriaAPI.getIdCondition(adjustmentVsBaseline.getId(), module));
					
					update.update(FieldUtil.getAsProperties(adjustmentVsBaseline));
				}
			}
		}
		
		for(MVAdjustmentVsBaseline deletedAdjustmentVsBaseline :deletedAdjustmentVsBaselines) {
			
			GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getIdCondition(deletedAdjustmentVsBaseline.getId(), module));
			
			delete.delete();
		}
		return false;
	}


}