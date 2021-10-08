package com.facilio.bundle.command;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class AddBundleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleContext bundle = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);
		
		if(bundle.getCreatedTime() == null) {
			bundle.setCreatedTime(DateTimeUtil.getCurrenTime());
		}
		
		bundle.setModifiedTime(bundle.getCreatedTime());
		
		Map<String, Object> props = FieldUtil.getAsProperties(bundle);
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getBundleModule().getTableName())
				.fields(FieldFactory.getBundleFields())
				.addRecord(props);
		
		insert.save();
		
		Long id = (Long)props.get("id");
		
		bundle.setId(id);
		
		if(bundle.getParentBundleId() == null || bundle.getParentBundleId() <=0) {
			bundle.setParentBundleId(id);
			
			
			GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getBundleModule().getTableName())
					.fields(FieldFactory.getBundleFields())
					.andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getBundleModule()));
			
			update.update(FieldUtil.getAsProperties(bundle));
			
			addDefaultSystemBundleComponents(bundle);
		}
		
		return false;
	}

	private void addDefaultSystemBundleComponents(BundleContext bundle) throws Exception {
		// TODO Auto-generated method stub
		
		BundleContext systemBundle = BundleUtil.getDefaultSystemBundle();
		
		Map<String, FacilioField> bundleChangesetFieldMap = FieldFactory.getAsMap(FieldFactory.getBundleChangeSetFields());
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getBundleChangeSetModule().getTableName())
				.select(FieldFactory.getBundleChangeSetFields())
				.andCondition(CriteriaAPI.getCondition(bundleChangesetFieldMap.get("bundleId"), systemBundle.getId()+"", NumberOperators.EQUALS));
		
		
		List<Map<String, Object>> changeSetList = select.get();
		
		for(Map<String, Object> changeSet : changeSetList) {
			
			changeSet.put("bundleId", bundle.getId());
			changeSet.put("componentLastEditedTime", bundle.getCreatedTime());
		}
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getBundleChangeSetModule().getTableName())
				.fields(FieldFactory.getBundleChangeSetFields())
				.addRecords(changeSetList);
		
		insert.save();
	}

}
