package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetSpaceSpecifcReadingsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		
		if (parentId != -1) {
			if (isSpaceSpace(parentId, context)) {
				return false;
			}
			
			List<FacilioField> fields = FieldFactory.getBasespaceReadingsFields();
			FacilioField spaceField = FieldFactory.getAsMap(fields).get("spaceId");
			FacilioModule module = ModuleFactory.getBasespaceReadingsModule();
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table(module.getTableName())
															.andCondition(CriteriaAPI.getCondition(spaceField, String.valueOf(parentId), PickListOperators.IS));

			List<Map<String, Object>> props = selectBuilder.get();
			
			if(props != null && !props.isEmpty()) {
				List<FacilioModule> readings = new ArrayList<>();
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				for(Map<String, Object> prop : props) {
					readings.add(modBean.getModule((long) prop.get("readingId")));
				}
				context.put(FacilioConstants.ContextNames.MODULE_LIST, readings);
			}
		}
		else {
			throw new IllegalArgumentException("Parent ID cannot be null during addition of reading for category");
		}
		
		return false;
	}
	
	private boolean isSpaceSpace(long parentId, Context context) throws Exception {
		BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(parentId);
		if (baseSpace.getSpaceTypeEnum() == SpaceType.SPACE) {
			SpaceContext space = SpaceAPI.getSpace(parentId);
			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, space.getSpaceCategory() != null?space.getSpaceCategory().getId():-1);
			context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getSpaceCategoryReadingRelModule());
			return true;
		}
		return false;
	}

}
