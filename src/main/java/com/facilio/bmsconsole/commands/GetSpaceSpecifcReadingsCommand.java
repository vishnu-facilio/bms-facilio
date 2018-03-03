package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetSpaceSpecifcReadingsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		
		if (parentId != -1) {
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

}
