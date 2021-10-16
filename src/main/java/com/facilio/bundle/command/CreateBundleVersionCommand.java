package com.facilio.bundle.command;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bundle.context.BundleChangeSetContext;
import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class CreateBundleVersionCommand extends FacilioCommand {
	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleContext bundle = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);
		
		BundleUtil.setVersion(bundle);
		
		bundle.setCreatedTime(System.currentTimeMillis());
		bundle.setModifiedTime(bundle.getCreatedTime());
		
		FacilioChain addBundle = BundleTransactionChainFactory.addBundleChain();
		
		FacilioContext newcontext = addBundle.getContext();
		
		newcontext.put(BundleConstants.BUNDLE_CONTEXT, bundle);
		
		addBundle.execute();
		
		context.put(BundleConstants.BUNDLE_CONTEXT,  newcontext.get(BundleConstants.BUNDLE_CONTEXT));
		
		List<BundleChangeSetContext> changeSetList = (List<BundleChangeSetContext>) context.get(BundleConstants.BUNDLE_CHANGE_SET_LIST);
		
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getBundleChangeSetModule().getTableName())
				.fields(FieldFactory.getBundleChangeSetFields())
				;
		
		for(BundleChangeSetContext changeSet :changeSetList) {
			
			changeSet.setBundleId(bundle.getId());
			changeSet.setComponentLastEditedTime(bundle.getCreatedTime());
			
			insert.addRecord(FieldUtil.getAsProperties(changeSet));
		}
		
		insert.save();
		
		return false;
	}

}
