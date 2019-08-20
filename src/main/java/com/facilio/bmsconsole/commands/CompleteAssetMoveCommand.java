package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;

;

public class CompleteAssetMoveCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>)context.get(FacilioConstants.ContextNames.CHANGE_SET_MAP);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(CollectionUtils.isNotEmpty(recordIds) && changeSet != null && !changeSet.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if(!changeSet.isEmpty()) {
				for(Long recordId : recordIds) {
					List<UpdateChangeSet> updatedSet = changeSet.get(recordId);
					if(!updatedSet.isEmpty()) {
						for(UpdateChangeSet changes : updatedSet) {
							FacilioField field = modBean.getField(changes.getFieldId()) ;
							if(field != null) {
								if(field.getName() == "moduleState") {
									if(changes.getNewValue() == "Completed") {
										AssetsAPI.updateAssetMovement(recordId);
									}
								}
							}
						}
					}
				}
			}
		}
			
		return false;
	}

}