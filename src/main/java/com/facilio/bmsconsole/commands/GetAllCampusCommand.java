package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;

public class GetAllCampusCommand implements Command{

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		
		SelectRecordsBuilder<SiteContext> builder = new SelectRecordsBuilder<SiteContext>()
				.table(dataTableName)
				.moduleName(moduleName)
				.beanClass(SiteContext.class)
				.select(fields)
				.maxLevel(2)
				.orderBy("-BaseSpace.LOCAL_ID desc,ID");
		Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(moduleName);
		if(scopeCriteria != null)
		{
			builder.andCriteria(scopeCriteria);
		}

		List<SiteContext> campuses = builder.get();
		List<Long> spaceId = new ArrayList<Long>();
		campuses.forEach((e) -> spaceId.add(e.getId()));
		context.put(FacilioConstants.ContextNames.SITE_LIST, campuses);
		
		return false;
	}

}
