package com.facilio.bundle.command;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class FetchAllBundleCommand extends FacilioCommand {
	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getBundleFields());
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getBundleModule().getTableName())
				.select(FieldFactory.getBundleFields())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("type"), BundleContext.BundleTypeEnum.UN_MANAGED_SYSTEM.getValue()+"", NumberOperators.NOT_EQUALS))
				.andCustomWhere("ID = PARENT_BUNDLE")
				;
		
		List<Map<String, Object>> props = builder.get();
		
		 List<BundleContext> bundles = FieldUtil.getAsBeanListFromMapList(props, BundleContext.class);
		 
		 context.put(BundleConstants.BUNDLE_CONTEXT_LIST, bundles);
		
		return false;
	}

}
