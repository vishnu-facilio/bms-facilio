package com.facilio.bundle.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.map.HashedMap;

import com.facilio.bundle.context.BundleChangeSetContext;
import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.context.BundleContext.BundleTypeEnum;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class FetchBundleChangeSetCommand extends FacilioCommand {
	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleContext bundle = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);
		
		List<BundleChangeSetContext> changeSetList = BundleUtil.getAllChangeSet(bundle);
		
		context.put(BundleConstants.BUNDLE_CHANGE_SET_LIST , changeSetList);
		
		return false;
	}

//	private void fillChangeSetCache(BundleContext bundle) throws Exception {
//
//		Map<String, FacilioField> bundleChangeSetMap = FieldFactory.getAsMap(FieldFactory.getBundleChangeSetFields());
//		
//		GenericSelectRecordBuilder bundleChangeSetSelect = new GenericSelectRecordBuilder()
//				.table(ModuleFactory.getBundleChangeSetModule().getTableName())
//				.select(FieldFactory.getBundleChangeSetFields())
//				.andCondition(CriteriaAPI.getCondition(bundleChangeSetMap.get("bundleId"), bundle.getId()+"", NumberOperators.EQUALS));
//		
//		List<Map<String, Object>> changeSetProps = bundleChangeSetSelect.get();
//		
//		List<BundleChangeSetContext> changeSetList = FieldUtil.getAsBeanListFromMapList(changeSetProps, BundleChangeSetContext.class);
//		
//		Map<BundleComponentsEnum,List<BundleChangeSetContext>> changeSetCache = new HashedMap<BundleComponentsEnum, List<BundleChangeSetContext>>();
//		
//		
//		for(BundleChangeSetContext changeSet : changeSetList) {
//			
//			List<BundleChangeSetContext> componentList = changeSetCache.get(changeSet.getComponentTypeEnum());
//			
//			componentList = componentList == null ? new ArrayList<BundleChangeSetContext>() : componentList;
//			
//			componentList.add(changeSet);
//			
//			changeSetCache.put(changeSet.getComponentTypeEnum(), componentList);
//		}
//		
//		bundle.setChangeSetCache(changeSetCache);
//	}

}
