package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.reservation.ReservationContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;

public class SpecialHandlingToGetModuleDataListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<? extends ModuleBaseWithCustomFields> records = (List<? extends ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (CollectionUtils.isNotEmpty(records)) {
			switch (moduleName) {
				case FacilioConstants.ContextNames.ASSET_CATEGORY:
					for(AssetCategoryContext record: (List<AssetCategoryContext>)records) {
						record.setModuleName(modBean.getModule(record.getAssetModuleID()).getName());
					}
					break;
				case FacilioConstants.ContextNames.Reservation.RESERVATION:
					List<Long> spaceIds = ((List<ReservationContext>)records).stream().map(ReservationContext::getSpace).map(SpaceContext::getId).collect(Collectors.toList());
					Map<Long, SpaceContext> spaceMap = SpaceAPI.getSpaceMap(spaceIds);
					for(ReservationContext record:(List<ReservationContext>)records) {
						record.setSpace(spaceMap.get(record.getSpace().getId()));
					}
					break;
			}
		}
		if(moduleName.equals("assetcategory") && records != null && !records.isEmpty()) {
		}
		return false;
	}

}
