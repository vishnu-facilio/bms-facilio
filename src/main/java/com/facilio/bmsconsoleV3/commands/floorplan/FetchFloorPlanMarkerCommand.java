package com.facilio.bmsconsoleV3.commands.floorplan;

import org.apache.commons.collections4.CollectionUtils;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.floorplan.V3FloorplanMarkersContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchFloorPlanMarkerCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

		List<V3FloorplanMarkersContext> markers = recordMap.get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN_MARKER);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule markermodule = modBean.getModule(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN_OBJECTS);
		List<FacilioField> fields = modBean.getAllFields(markermodule.getName());

		if (CollectionUtils.isNotEmpty(markers)) {

			for (V3FloorplanMarkersContext marker : markers) {
				marker.setAvatarUrl(marker.getAvatarUrl());
			}

		}
				

		return false;
	}
}