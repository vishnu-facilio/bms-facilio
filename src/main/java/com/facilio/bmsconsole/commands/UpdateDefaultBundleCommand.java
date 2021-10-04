package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class UpdateDefaultBundleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		// this is to make the bundle created time is after all the components added time. 
		// this will make sure fetching components for next version will not bring internal-system components.
		
		BundleContext defaultBundle = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);
		
		defaultBundle.setCreatedTime(System.currentTimeMillis());
		defaultBundle.setModifiedTime(defaultBundle.getCreatedTime());
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getBundleModule().getTableName())
				.fields(FieldFactory.getBundleFields())
				.andCondition(CriteriaAPI.getIdCondition(defaultBundle.getId(), ModuleFactory.getBundleModule()));
		
		update.update(FieldUtil.getAsProperties(defaultBundle));
		
		 Map<String, FacilioField> changeSetFieldsMap = FieldFactory.getAsMap(FieldFactory.getBundleChangeSetFields());
		
		update = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getBundleChangeSetModule().getTableName())
				.fields(FieldFactory.getBundleChangeSetFields())
				.andCondition(CriteriaAPI.getCondition(changeSetFieldsMap.get("bundleId"), defaultBundle.getId()+"",NumberOperators.EQUALS));
		
		Map<String, Object> updateProps = new HashMap<String, Object>();
		
		updateProps.put("componentLastEditedTime", defaultBundle.getCreatedTime());
		
		update.update(updateProps);
		
		return false;
	}

}
