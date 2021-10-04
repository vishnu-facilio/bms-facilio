package com.facilio.bundle.command;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
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
		bundle.setParentBundleId(id);
		
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getBundleModule().getTableName())
				.fields(FieldFactory.getBundleFields())
				.andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getBundleModule()));
		
		update.update(FieldUtil.getAsProperties(bundle));
		
		return false;
	}

}
