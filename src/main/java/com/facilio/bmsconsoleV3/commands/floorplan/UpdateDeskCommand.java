package com.facilio.bmsconsoleV3.commands.floorplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerContext;
import com.facilio.bmsconsoleV3.util.DesksAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class UpdateDeskCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

		List<V3MarkerContext> markers = recordMap.get(FacilioConstants.ContextNames.Floorplan.MARKER);
		
		Map<Long, List<UpdateChangeSet>> deskChangeSet = new HashMap<Long, List<UpdateChangeSet>>();

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule deskModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
		List<FacilioField> fields = modBean.getAllFields(deskModule.getName());

        if (CollectionUtils.isNotEmpty(markers)) {
       			 List<V3DeskContext> addDeskList = new ArrayList<V3DeskContext>();

					for (V3MarkerContext marker : markers) {
						V3DeskContext desk = marker.getDesk();
						if (desk != null) {
                            if (desk.getId() > 0) {
                            	Map<Long, List<UpdateChangeSet>> changes = V3RecordAPI.updateRecord(desk, deskModule, fields, true);
                            	if(changes != null && !changes.isEmpty()) {
                            		deskChangeSet.putAll(changes);
                            	}
                            }
                            if (desk.getId() < 0 || desk != null) {
                                addDeskList.add(desk);
                            }
						}

					}

                    if (CollectionUtils.isNotEmpty(addDeskList)) {
                    	Map<Long, List<UpdateChangeSet>> changes = V3RecordAPI.addRecord(false, addDeskList, deskModule, fields, true);
                    	if(changes != null && !changes.isEmpty()) {
                    		deskChangeSet.putAll(changes);
                    	}
                    }
                    if(deskChangeSet != null && !deskChangeSet.isEmpty()) {
                    for(Long deskId : deskChangeSet.keySet()) {
						V3DeskContext desk = (V3DeskContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.DESKS, deskId, V3DeskContext.class);
						
						DesksAPI.AddorDeleteFacilityForDesks(desk);
					}
        }

				}


		return false;
	}
}