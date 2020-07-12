package com.facilio.workflowv2.contexts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class WorkflowSpaceCategoryReadingContext extends WorkflowCategoryReadingContext {

	SpaceType spaceType;
	
	public SpaceType getSpaceType() {
		return spaceType;
	}

	public void setSpaceType(SpaceType spaceType) {
		this.spaceType = spaceType;
	}
	
	public WorkflowSpaceCategoryReadingContext(SpaceType spaceType,List<Long> parentIDs) {
		setSpaceType(spaceType);
		setParentIds(parentIDs);
	}

	@Override
	public FacilioField getField(String fieldName) throws Exception {
		
		FacilioChain getSpaceTypeReading = FacilioChainFactory.getReadingsForSpaceTypeChain();
		FacilioContext context = getSpaceTypeReading.getContext();
		context.put(FacilioConstants.ContextNames.SPACE_TYPE_ENUM,getSpaceType());
		getSpaceTypeReading.execute();
		List<FacilioModule> readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		
		Map<String,FacilioField> fieldMap = new HashMap<>();
		if (readings != null) {
			for(FacilioModule reading :readings) {
				for(FacilioField readingFields :reading.getFields()) {
					fieldMap.put(readingFields.getName(), readingFields);
				}
			}
		}
		
		FacilioField field = fieldMap.get(fieldName);
		
		if(field == null) {
			throw new Exception("field does not exist.");
		}
		return field;
	}
}
