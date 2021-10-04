package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bundle.command.BundleTransactionChainFactory;
import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;

public class AddDefaultBundleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Organization org = AccountUtil.getCurrentOrg();
		
		BundleContext defaultBundle = new BundleContext();
		
		defaultBundle.setBundleName("default");
		defaultBundle.setBundleGlobalName("org.facilio."+org.getDomain()+".default");
		defaultBundle.setOrgId(org.getOrgId());
		defaultBundle.setVersion("0.0.1");
		defaultBundle.setTypeEnum(BundleContext.BundleTypeEnum.UN_MANAGED_SYSTEM);
		defaultBundle.setCreatedTime(org.getCreatedTime());
		defaultBundle.setModifiedTime(org.getCreatedTime());
		
		FacilioChain addBundle = BundleTransactionChainFactory.addBundleChain();
		
		FacilioContext contextnew = addBundle.getContext();
		
		contextnew.put(BundleConstants.BUNDLE_CONTEXT, defaultBundle);
		
		addBundle.execute();
		
		context.put(BundleConstants.BUNDLE_CONTEXT, defaultBundle);
		
		return false;
	}

}
