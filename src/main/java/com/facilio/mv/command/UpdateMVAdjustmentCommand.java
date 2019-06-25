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
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;
import com.facilio.time.DateRange;

public class UpdateMVAdjustmentCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
					Chain chain = TransactionChainFactory.getAddMVAdjustmentChain();
					chain.execute(context);
				}
				else {
					
					context.put(FacilioConstants.ContextNames.FORMULA_FIELD, adjustment.getFormulaField());
					
					context.put(FacilioConstants.ContextNames.DATE_RANGE,new DateRange(adjustment.getStartTime(), adjustment.getEndTime()));
					
					Chain updateEnPIChain = FacilioChainFactory.updateFormulaChain();
					updateEnPIChain.execute(context);
					
					UpdateRecordBuilder<MVAdjustment> update = new UpdateRecordBuilder<MVAdjustment>()
							.module(module)
							.fields(fields)
							.andCondition(CriteriaAPI.getIdCondition(adjustment.getId(), module));
					
					update.update(adjustment);
				}
			}
		}
		
		for(MVAdjustment deletedAdjustment :deletedAdjustments) {
			
			DeleteRecordBuilder<MVAdjustment> delete = new DeleteRecordBuilder<MVAdjustment>()
					.module(module)
					.andCondition(CriteriaAPI.getIdCondition(deletedAdjustment.getId(), module));
			
			delete.delete();
		}
		return false;
	}


}