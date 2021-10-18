package com.facilio.bundle.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;

import com.facilio.bundle.context.BundleChangeSetContext;
import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.context.BundleContext.BundleTypeEnum;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import io.jsonwebtoken.lang.Collections;

public class FetchBundleChangeSetCommand extends FacilioCommand {
	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleContext bundle = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);
		
		fillChangeSetCache(bundle,context);
		
		List<BundleChangeSetContext> returnChangeset = new ArrayList<BundleChangeSetContext>();
		
		Map<BundleComponentsEnum, ArrayList<BundleComponentsEnum>> parentChildMap = BundleComponentsEnum.getParentChildMap();
		
		Queue<BundleComponentsEnum> componentsQueue = new LinkedList<BundleComponentsEnum>();
		
		componentsQueue.addAll(BundleComponentsEnum.getParentComponentList());
		
		while(!componentsQueue.isEmpty()) {
			
			BundleComponentsEnum component = componentsQueue.poll();
			
			BundleComponentInterface componentClass = component.getBundleComponentClassInstance();
			
			context.put(BundleConstants.COMPONENT, component);
			context.put(BundleConstants.BUNDLE_CONTEXT, bundle);
			
			componentClass.getAddedChangeSet((FacilioContext)context);
			
			List<BundleChangeSetContext> addedChangeSet = (List<BundleChangeSetContext>) context.get(BundleConstants.CHANGE_SET);
			
			context.put(BundleConstants.CHANGE_SET, null);
			
			componentClass.getModifiedChangeSet((FacilioContext)context);
			
			List<BundleChangeSetContext> modifiedChangeSet = (List<BundleChangeSetContext>) context.get(BundleConstants.CHANGE_SET);
			
			context.put(BundleConstants.CHANGE_SET, null);
			
			componentClass.getDeletedChangeSet((FacilioContext)context);
			
			List<BundleChangeSetContext> deletedChangeSet = (List<BundleChangeSetContext>) context.get(BundleConstants.CHANGE_SET);
			
			context.put(BundleConstants.CHANGE_SET, null);
			
			if(addedChangeSet != null) {
				returnChangeset.addAll(addedChangeSet);
			}
			if(modifiedChangeSet != null) {
				returnChangeset.addAll(modifiedChangeSet);
			}
			if(deletedChangeSet != null) {
				returnChangeset.addAll(deletedChangeSet);
			}
			
			ArrayList<BundleComponentsEnum> childList = parentChildMap.get(component);
			
			if(childList != null) {
				componentsQueue.addAll(childList);
			}
			
		}
		
		context.put(BundleConstants.BUNDLE_CHANGE_SET_LIST , returnChangeset);
		
		return false;
	}

	private void fillChangeSetCache(BundleContext bundle, Context context) throws Exception {
		
		Map<BundleComponentsEnum,List<BundleChangeSetContext>> changeSetCache = new HashedMap<BundleComponentsEnum, List<BundleChangeSetContext>>();
		
		Map<String, FacilioField> bundleChangeSetMap = FieldFactory.getAsMap(FieldFactory.getBundleChangeSetFields());
		
		List<Long> bundleIds = new ArrayList<Long>();
		
		bundleIds.add(bundle.getId());
		
		if(!Collections.isEmpty(bundle.getChildVersions())) {
			
			bundleIds.addAll(bundle.getChildVersions().stream().map(BundleContext::getId).collect(Collectors.toList()));
		}
		
		List<FacilioField> selectField = new ArrayList<FacilioField>();
		selectField.add(bundleChangeSetMap.get("componentId"));
		
		for(BundleComponentsEnum componentEnum : BundleComponentsEnum.values()) {
			
			GenericSelectRecordBuilder bundleChangeSetSelect = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getBundleChangeSetModule().getTableName())
					.select(selectField)
					.aggregate(NumberAggregateOperator.MAX, bundleChangeSetMap.get("componentLastEditedTime"))
					.aggregate(NumberAggregateOperator.MAX, bundleChangeSetMap.get("componentMode"))	// check this line
					.andCondition(CriteriaAPI.getCondition(bundleChangeSetMap.get("bundleId"), StringUtils.join(bundleIds,","), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(bundleChangeSetMap.get("componentType"), componentEnum.getValue()+"", NumberOperators.EQUALS))
					.groupBy("COMPONENT_ID");
			
			List<Map<String, Object>> changeSetProps = bundleChangeSetSelect.get();
			
			List<BundleChangeSetContext> changeSetList = FieldUtil.getAsBeanListFromMapList(changeSetProps, BundleChangeSetContext.class);
			
			changeSetCache.put(componentEnum, changeSetList);
			
		}
		
		context.put(BundleConstants.CHANGE_SET_CACHE, changeSetCache);
	}

}
