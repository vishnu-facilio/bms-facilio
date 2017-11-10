package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetBaseSpaceChildrenCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		
		Long spaceId = (Long) context.get(FacilioConstants.ContextNames.SPACE_ID);
		String spaceType = (String) context.get(FacilioConstants.ContextNames.SPACE_TYPE);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
		
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(BaseSpaceContext.class);
		
		if (BaseSpaceContext.SpaceType.SITE.getStringVal().equalsIgnoreCase(spaceType)) {
			selectBuilder.andCustomWhere("BaseSpace.SITE_ID = ? AND BaseSpace.BUILDING_ID IS NULL AND BaseSpace.FLOOR_ID IS NULL", spaceId);
		}
		else if (BaseSpaceContext.SpaceType.BUILDING.getStringVal().equalsIgnoreCase(spaceType)) {
			selectBuilder.andCustomWhere("BaseSpace.BUILDING_ID = ? AND BaseSpace.FLOOR_ID IS NULL", spaceId);
		}
		else if (BaseSpaceContext.SpaceType.FLOOR.getStringVal().equalsIgnoreCase(spaceType)) {
			selectBuilder.andCustomWhere("BaseSpace.FLOOR_ID = ?", spaceId);
		}
		
		List<BaseSpaceContext> spaces = selectBuilder.get();
		
		context.put(FacilioConstants.ContextNames.BASE_SPACE_LIST, spaces);
		
		return false;
	}

}
