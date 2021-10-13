package com.facilio.bundle.command;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.context.InstalledBundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetAllInstalledBundlesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getInstalledBundleModule().getTableName())
				.select(FieldFactory.getInstalledBundleFields())
				;
		
		List<Map<String, Object>> props = builder.get();
		
		 List<InstalledBundleContext> installedBundles = FieldUtil.getAsBeanListFromMapList(props, InstalledBundleContext.class);
		 
		 
		 context.put(BundleConstants.INSTALLED_BUNDLES, installedBundles);
		
		return false;
	}

}
