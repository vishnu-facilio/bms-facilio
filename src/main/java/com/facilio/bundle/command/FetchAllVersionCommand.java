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
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class FetchAllVersionCommand extends FacilioCommand {
	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		BundleContext bundle = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getBundleFields());
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getBundleModule().getTableName())
				.select(FieldFactory.getBundleFields())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentBundleId"), bundle.getParentBundleId()+"", NumberOperators.EQUALS))
				.andCustomWhere("ID != PARENT_BUNDLE");
				;
		
		List<Map<String, Object>> props = builder.get();
		
		 List<BundleContext> bundles = FieldUtil.getAsBeanListFromMapList(props, BundleContext.class);
		 
		 
		 for(BundleContext bundle1: bundles) {
			 String downloadUrl = FacilioFactory.getFileStore().getDownloadUrl(bundle1.getBundleFileId());
			 bundle1.setDownloadUrl(downloadUrl);
		 }
		 
		 context.put(BundleConstants.BUNDLE_VERSION_LIST, bundles);
		
		return false;
	}

}
