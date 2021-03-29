package com.facilio.bmsconsoleV3.commands.floorplan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class UpdateDeskCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

		List<V3MarkerContext> markers = recordMap.get(FacilioConstants.ContextNames.Floorplan.MARKER);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule deskModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
		List<FacilioField> fields = modBean.getAllFields(deskModule.getName());

        if (CollectionUtils.isNotEmpty(markers)) {
       			 List<V3DeskContext> addDeskList = new ArrayList<V3DeskContext>();

					for (V3MarkerContext marker : markers) {
						V3DeskContext desk = marker.getDesk();
						if (desk != null) {
                            if (desk.getId() > 0) {
                                V3RecordAPI.updateRecord(desk, deskModule, fields);
                            }
                            if (desk.getId() < 0 || desk != null) {
                                addDeskList.add(desk);
                            }
						}

					}

                    if (CollectionUtils.sizeIsEmpty(addDeskList)) {
                      V3RecordAPI.addRecord(false, addDeskList, deskModule, fields);
                    }


				}


		return false;
	}
}