package com.facilio.bmsconsoleV3.commands.floorplan;

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

import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class UpdateMarkerCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

		List<V3MarkerContext> markers = recordMap.get(FacilioConstants.ContextNames.Floorplan.MARKER);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule floorplanmarkers = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKER);
		List<FacilioField> fields = modBean.getAllFields(floorplanmarkers.getName());
		FacilioModule deskModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
	
				if (CollectionUtils.isNotEmpty(markers)) {

					for (V3MarkerContext marker : markers) {
						V3DeskContext desk = marker.getDesk();
						if (desk != null) {
							marker.setMarkerModuleId(deskModule.getModuleId());
							marker.setRecordId(desk.getId());
						}
                       V3RecordAPI.updateRecord(marker, floorplanmarkers, fields);
					}
				}


		return false;
	}
}