package com.facilio.bundle.command;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bundle.bean.BundleBean;
import com.facilio.bundle.context.BundleChangeSetContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.oldsandbox.context.SandboxContext;
import com.facilio.v3.context.Constants;

public class GetSandboxChangeSetCommand extends FacilioCommand {
	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		SandboxContext sandbox = (SandboxContext) context.get(BundleConstants.Sandbox.SANDBOX);
		
		Map<String, Object> sandboxMap = BundleUtil.fetchBundleRelated(ModuleFactory.getSandboxModule(), FieldFactory.getSandboxFields(), null, CriteriaAPI.getIdCondition(sandbox.getId(), ModuleFactory.getSandboxModule())).get(0);
		
		sandbox = FieldUtil.getAsBeanFromMap(sandboxMap, SandboxContext.class);
		
		BundleBean bundleBean = Constants.getBundleBean(sandbox.getSandboxOrgId());
		
		List<BundleChangeSetContext> changeSet = bundleBean.getChangeSet();
		
		context.put(BundleConstants.BUNDLE_CHANGE_SET_LIST , changeSet);
		
		return false;
	}

}
