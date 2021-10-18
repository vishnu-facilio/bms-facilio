package com.facilio.bundle.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bundle.context.BundleChangeSetContext;
import com.facilio.bundle.context.BundleContext;
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
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.fields.FacilioField;

import io.jsonwebtoken.lang.Collections;

public class FetchAllChangeSetForPackingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<BundleChangeSetContext> currentChangeset = (List<BundleChangeSetContext>) context.get(BundleConstants.BUNDLE_CHANGE_SET_LIST);
		
		BundleContext bundle = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);
		
		currentChangeset.addAll(fillChangeSetFromPerviousVersions(currentChangeset, bundle, context));
		
		BundleContext parentBundleObj = BundleUtil.getBundle(bundle.getParentBundleId());
		
		Map<Long, Double> bundleVsVersionMap = parentBundleObj.getChildVersions().stream().collect(Collectors.toMap(BundleContext::getId, BundleContext::getVersion));
		
		for(BundleChangeSetContext currentChange : currentChangeset) {
			
			Double version = bundleVsVersionMap.get(currentChange.getBundleId());
			
			currentChange.setTempVersion(version);
		}
		
		return false;
	}

	private List<BundleChangeSetContext> fillChangeSetFromPerviousVersions(List<BundleChangeSetContext> currentChangeset,BundleContext bundle, Context context) throws Exception {
		
		List<BundleChangeSetContext> oldChangeSetList = new ArrayList<BundleChangeSetContext>();
		
		Map<String, FacilioField> bundleChangeSetMap = FieldFactory.getAsMap(FieldFactory.getBundleChangeSetFields());
		
		List<Long> bundleIds = new ArrayList<Long>();
		
		if(!Collections.isEmpty(bundle.getChildVersions())) {
			
			bundleIds.addAll(bundle.getChildVersions().stream().map(BundleContext::getId).collect(Collectors.toList()));
			
			Map<BundleComponentsEnum, List<BundleChangeSetContext>> currentChangeSetMap = currentChangeset.stream().collect(Collectors.groupingBy(BundleChangeSetContext::getComponentTypeEnum));
			
			List<FacilioField> selectField = new ArrayList<FacilioField>();
			selectField.add(bundleChangeSetMap.get("componentId"));
			
			for(BundleComponentsEnum componentEnum : BundleComponentsEnum.values()) {
				
				List<Long> currentChangeSetComponentId = new ArrayList<Long>();
				if(!Collections.isEmpty(currentChangeSetMap.get(componentEnum))) {
					
					currentChangeSetComponentId.addAll(currentChangeSetMap.get(componentEnum).stream().map(BundleChangeSetContext::getComponentId).collect(Collectors.toList()));
				}
				
				GenericSelectRecordBuilder bundleChangeSetSelect = new GenericSelectRecordBuilder()
						.table(ModuleFactory.getBundleChangeSetModule().getTableName())
						.select(selectField)
						.aggregate(NumberAggregateOperator.MAX, bundleChangeSetMap.get("componentLastEditedTime"))
						.aggregate(NumberAggregateOperator.MAX, bundleChangeSetMap.get("componentMode"))	// check this line
						.aggregate(NumberAggregateOperator.MAX, bundleChangeSetMap.get("bundleId"))	// check this line
						.andCondition(CriteriaAPI.getCondition(bundleChangeSetMap.get("bundleId"), StringUtils.join(bundleIds,","), NumberOperators.EQUALS))
						.andCondition(CriteriaAPI.getCondition(bundleChangeSetMap.get("componentType"), componentEnum.getValue()+"", NumberOperators.EQUALS))
						.groupBy("COMPONENT_ID");
				
				if(!currentChangeSetComponentId.isEmpty()) {
					bundleChangeSetSelect.andCondition(CriteriaAPI.getCondition(bundleChangeSetMap.get("componentId"), StringUtils.join(currentChangeSetComponentId,","), NumberOperators.NOT_EQUALS));
				}
				
				List<Map<String, Object>> changeSetProps = bundleChangeSetSelect.get();
				
				List<BundleChangeSetContext> changeSetList = FieldUtil.getAsBeanListFromMapList(changeSetProps, BundleChangeSetContext.class);
				
				for(BundleChangeSetContext changeSet :changeSetList) {
					changeSet.setComponentTypeEnum(componentEnum);
				}
				
				oldChangeSetList.addAll(changeSetList);
				
			}
			
		}
		return oldChangeSetList;
		
	}
}
