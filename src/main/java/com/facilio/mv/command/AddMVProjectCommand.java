package com.facilio.mv.command;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class AddMVProjectCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		MVProjectContext mvProject = mvProjectWrapper.getMvProject();
		
		mvProject.setStatus(Boolean.TRUE);  					//setting project active
		mvProject.setIsLocked(Boolean.TRUE);
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(mvProject.getMeter().getId() == -1l) {
			
			FacilioChain addAssetChain = FacilioChainFactory.getAddEnergyMeterChain();
			
			FacilioContext context1 = addAssetChain.getContext();
			
			context1.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
			context1.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
			context1.put(FacilioConstants.ContextNames.RECORD, mvProject.getMeter());
			addAssetChain.execute();
			
			mvProject.setAutoGenVmMeter(true);
		}
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		
		InsertRecordBuilder<MVProjectContext> insert = new InsertRecordBuilder<MVProjectContext>()
				.module(module)
				.fields(fields)
				.addRecord(mvProject);
		
		insert.save();
		
		if(mvProject.getSaveGoalFormulaField() != null) {
			
			FacilioChain addEnpiChain = TransactionChainFactory.addFormulaFieldChain(true);
			
			FacilioContext context1 = addEnpiChain.getContext();
			
			context1.put(FacilioConstants.ContextNames.FORMULA_FIELD, mvProject.getSaveGoalFormulaField());
			context1.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST,Collections.singletonList(MVUtil.getMVSaveGoalReadingField()));
			context1.put(FacilioConstants.ContextNames.MODULE,MVUtil.getMVSaveGoalReadingField().getModule());
			context1.put(FacilioConstants.ContextNames.IS_FORMULA_FIELD_OPERATION_FROM_M_AND_V,true);
			
			FormulaFieldContext formulaField = mvProject.getSaveGoalFormulaField();
			
			formulaField.setModule(MVUtil.getMVSaveGoalReadingField().getModule());
			formulaField.setReadingField(MVUtil.getMVSaveGoalReadingField());
			
			MVUtil.fillFormulaFieldDetailsForAdd(mvProject.getSaveGoalFormulaField(), mvProjectWrapper.getMvProject(),null,null,context1);
			
			addEnpiChain.execute();
			
			mvProject.setSaveGoalFormulaField(formulaField);
			
			MVUtil.updateMVProject(mvProject);
		}
		
		return false;
	}


}
