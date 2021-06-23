package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetMovementContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateChangeSet;

public class AddDefaultChangeSetForCompleteMoveCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long recordId = (long)context.get(FacilioConstants.ContextNames.RECORD_ID);
		if(recordId > 0) {
			Map<Long, List<UpdateChangeSet>> changeSet = new HashMap<Long, List<UpdateChangeSet>>();
			List<UpdateChangeSet> changes = new ArrayList<UpdateChangeSet>();
			UpdateChangeSet updateChangeState = new UpdateChangeSet();
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_MOVEMENT);
			long fieldId = modBean.getField("moduleState", module.getName()).getFieldId();
			
			SelectRecordsBuilder<AssetMovementContext> builder = new SelectRecordsBuilder<AssetMovementContext>()
					.moduleName(FacilioConstants.ContextNames.ASSET_MOVEMENT)
					.beanClass(AssetMovementContext.class)
					.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_MOVEMENT))
					.andCondition(CriteriaAPI.getIdCondition(recordId, module));
			AssetMovementContext assetMovement = builder.fetchFirst();
					
			if(assetMovement != null) {
				updateChangeState.setFieldId(fieldId);
				updateChangeState.setNewValue(assetMovement.getModuleState().getId());
				changes.add(updateChangeState);
				changeSet.put(recordId, changes);
				context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, changeSet);
			}
		}
		
		return false;
	}

}
