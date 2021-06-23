package com.facilio.mv.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class UpdateMVAdjustmentCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		MVProjectWrapper mvProjectWrapperOld = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER_OLD);
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_ADJUSTMENT_MODULE);
		List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.MV_ADJUSTMENT_MODULE);
		
		List<MVAdjustment> adjustments = mvProjectWrapper.getAdjustments();
		
		List<MVAdjustment> oldAdjustments = mvProjectWrapperOld.getAdjustments();
		
		List<MVAdjustment> deletedAdjustments = new ArrayList<MVAdjustment>();
		
		for(MVAdjustment oldAdjustment :oldAdjustments) {
			
			boolean foundFlag = false;
			for(MVAdjustment adjustment :adjustments) {
				if(oldAdjustment.getId() == adjustment.getId()) {
					foundFlag = true;
					break;
				}
			}
			if(!foundFlag) {
				deletedAdjustments.add(oldAdjustment);
			}
		}
		
		if(adjustments != null) {
			
			for(MVAdjustment adjustment :adjustments) {
				
				if(adjustment.getId() < 0) {

					context.put(MVUtil.MV_ADJUSTMENT, adjustment);
					FacilioChain chain = TransactionChainFactory.getAddMVAdjustmentChain();
					chain.execute(context);
				}
				else {
					MVAdjustment oldAdjustment = getOldAdjustment(adjustment,oldAdjustments);
					if(adjustment.getFormulaField() != null) {
						adjustment.setConstant(-99d);
						if(oldAdjustment.getFormulaField() == null) {
							
							MVUtil.fillFormulaFieldDetailsForAdd(adjustment.getFormulaField(), mvProjectWrapper.getMvProject(),null,adjustment,context);
							context.put(FacilioConstants.ContextNames.FORMULA_FIELD, adjustment.getFormulaField());

							FacilioChain addEnpiChain = TransactionChainFactory.addFormulaFieldChain();
							addEnpiChain.execute(context);
						}
						else {
							context.put(FacilioConstants.ContextNames.FORMULA_FIELD, adjustment.getFormulaField());
							MVUtil.fillFormulaFieldDetailsForUpdate(adjustment.getFormulaField(), mvProjectWrapper.getMvProject(),null,adjustment,context);
							
							FacilioChain updateEnPIChain = TransactionChainFactory.updateFormulaChain();
							updateEnPIChain.execute(context);
						}
					}
					else {
						if(oldAdjustment.getFormulaField() != null) {
							context.put(FacilioConstants.ContextNames.RECORD_ID, oldAdjustment.getFormulaField().getId());
							context.put(FacilioConstants.ContextNames.IS_FORMULA_FIELD_OPERATION_FROM_M_AND_V,true);
							FacilioChain deleteEnPIChain = FacilioChainFactory.deleteFormulaChain();
							deleteEnPIChain.execute(context);
						}
					}
					MVUtil.updateMVAdjustment(adjustment);
				}
			}
		}
		
		for(MVAdjustment deletedAdjustment :deletedAdjustments) {
			
			DeleteRecordBuilder<MVAdjustment> delete = new DeleteRecordBuilder<MVAdjustment>()
					.module(module)
					.andCondition(CriteriaAPI.getIdCondition(deletedAdjustment.getId(), module));
			
			delete.delete();
			
			if(deletedAdjustment.getFormulaField() != null && deletedAdjustment.getFormulaField().getId() > 0) {
				context.put(FacilioConstants.ContextNames.RECORD_ID, deletedAdjustment.getFormulaField().getId());
				context.put(FacilioConstants.ContextNames.IS_FORMULA_FIELD_OPERATION_FROM_M_AND_V,true);
				FacilioChain deleteEnPIChain = FacilioChainFactory.deleteFormulaChain();
				deleteEnPIChain.execute(context);
			}
			
		}
		return false;
	}

	private MVAdjustment getOldAdjustment(MVAdjustment adjustment, List<MVAdjustment> oldAdjustments) {
		for(MVAdjustment oldAdjustment :oldAdjustments) {
			if(oldAdjustment.getId() == adjustment.getId()) {
				return oldAdjustment;
			}
		}
		return null;
	}


}