package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class UpdateZoneCommand implements Command {

	@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {
		

		ZoneContext zone = (ZoneContext) context.get(FacilioConstants.ContextNames.ZONE);
		List<Long> children = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(zone != null && children != null && !children.isEmpty()) 
		{
			zone.setSpaceType(SpaceType.ZONE);
			
			SpaceAPI.updateZoneInfo(zone, children);
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(zone.getId(), module));
															
			updateBuilder.update(zone);
			SpaceAPI.deleteZoneChildren(zone.getId());
			SpaceAPI.updateHelperFields(zone);
			SpaceAPI.addZoneChildren(zone, children);
		}
		else 
		{
			throw new IllegalArgumentException("Zone Object cannot be null");
		}
		return false;
		
	}

}
