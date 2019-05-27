package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetBaseSpaceChildrenCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		
		Long spaceId = (Long) context.get(FacilioConstants.ContextNames.SPACE_ID);
		String spaceType = (String) context.get(FacilioConstants.ContextNames.SPACE_TYPE);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
		Boolean isZOne = (Boolean) context.get(FacilioConstants.ContextNames.IS_ZONE);
		
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(BaseSpaceContext.class);
		
		if (BaseSpaceContext.SpaceType.SITE.getStringVal().equalsIgnoreCase(spaceType)) {
			StringBuilder builder = new StringBuilder("BaseSpace.SITE_ID = ? AND (BaseSpace.SPACE_TYPE = ? ");
			if(isZOne) {
				builder.append("OR (BaseSpace.SPACE_TYPE = ").append(SpaceType.ZONE.getIntVal());
				builder.append(" AND BaseSpace.FLOOR_ID IS NULL AND BaseSpace.BUILDING_ID IS NULL)");
			}
			builder.append(")");				
			selectBuilder.andCustomWhere(builder.toString(), spaceId, SpaceType.BUILDING.getIntVal());
		}
		else if (BaseSpaceContext.SpaceType.BUILDING.getStringVal().equalsIgnoreCase(spaceType)) {
			StringBuilder builder = new StringBuilder("BaseSpace.BUILDING_ID = ? AND (BaseSpace.SPACE_TYPE = ? ");
			if(isZOne) {
				builder.append("OR (BaseSpace.SPACE_TYPE = ").append(SpaceType.ZONE.getIntVal());
				builder.append(" AND BaseSpace.FLOOR_ID IS NULL)");
			}
			builder.append(")");
			selectBuilder.andCustomWhere(builder.toString(), spaceId, SpaceType.FLOOR.getIntVal());
		}
		else if (BaseSpaceContext.SpaceType.FLOOR.getStringVal().equalsIgnoreCase(spaceType)) {
			StringBuilder builder = new StringBuilder("BaseSpace.FLOOR_ID = ? AND (BaseSpace.SPACE_TYPE = ? ");
			if(isZOne) {
				builder.append("OR BaseSpace.SPACE_TYPE = ").append(SpaceType.ZONE.getIntVal());
			}
			builder.append(")");
			selectBuilder.andCustomWhere(builder.toString(), spaceId, SpaceType.SPACE.getIntVal());
		}
		else if (BaseSpaceContext.SpaceType.ZONE.getStringVal().equalsIgnoreCase(spaceType)) {
			selectBuilder.andCustomWhere("BaseSpace.FLOOR_ID = ? AND BaseSpace.SPACE_TYPE = ?", spaceId, SpaceType.ZONE.getIntVal());
		}
		List<BaseSpaceContext> spaces = selectBuilder.get();
		// System.out.println(selectBuilder.toString());
		context.put(FacilioConstants.ContextNames.BASE_SPACE_LIST, spaces);
		
		return false;
	}

}
