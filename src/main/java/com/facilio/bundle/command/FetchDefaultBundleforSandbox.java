package com.facilio.bundle.command;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.context.BundleContext.BundleTypeEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class FetchDefaultBundleforSandbox extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Map<String, Object> bundleMap = BundleUtil.fetchBundleRelated(ModuleFactory.getBundleModule(), FieldFactory.getBundleFields(), null, CriteriaAPI.getCondition("TYPE", "type", BundleTypeEnum.SANDBOX_UN_MANAGED.getValue()+"", NumberOperators.EQUALS)).get(0);
				
		BundleContext bundle = FieldUtil.getAsBeanFromMap(bundleMap, BundleContext.class);
		
		context.put(BundleConstants.BUNDLE_CONTEXT,bundle);
		
		return false;
	}

}
