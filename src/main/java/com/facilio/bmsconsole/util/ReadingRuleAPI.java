package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class ReadingRuleAPI extends WorkflowRuleAPI {
	public static void addReadingRuleInclusionsExlusions(ReadingRuleContext rule) throws SQLException, RuntimeException {
		if (rule.getAssetCategoryId() != -1) {
			List<Map<String, Object>> inclusionExclusionList = new ArrayList<>();
			getInclusionExclusionList(rule.getId(), rule.getIncludedResources(), true, inclusionExclusionList);
			getInclusionExclusionList(rule.getId(), rule.getExcludedResources(), false, inclusionExclusionList);
			
			if (!inclusionExclusionList.isEmpty()) {
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(ModuleFactory.getReadingRuleInclusionsExclusionsModule().getTableName())
															.fields(FieldFactory.getReadingRuleInclusionsExclusionsFields())
															.addRecords(inclusionExclusionList);
				insertBuilder.save();
			}
		}
	}
	
	private static void getInclusionExclusionList(long ruleId, List<Long> resources, boolean isInclude, List<Map<String, Object>> inclusionExclusionList) {
		if (resources != null && !resources.isEmpty()) {
			long orgId = AccountUtil.getCurrentOrg().getId();
			for (Long resourceId : resources) {
				Map<String, Object> prop = new HashMap<>();
				prop.put("orgId", orgId);
				prop.put("ruleId", ruleId);
				prop.put("resourceId", resourceId);
				prop.put("isInclude", isInclude);
				
				inclusionExclusionList.add(prop);
			}
		}
	}
	
	public static int updateLastValueInReadingRule(long ruleId, long value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		ReadingRuleContext rule = new ReadingRuleContext();
		rule.setLastValue(value);
		
		return updateReadingRule(rule, ruleId);
	}
	
	public static ReadingRuleContext updateReadingRuleWithChildren(ReadingRuleContext rule) throws Exception {
		ReadingRuleContext oldRule = (ReadingRuleContext) getWorkflowRule(rule.getId());
		updateWorkflowRuleChildIds(rule);
		updateReadingRule(rule, rule.getId());
		deleteChildIdsForWorkflow(oldRule, rule);
		
		if (rule.getName() == null) {
			rule.setName(oldRule.getName());
		}
		return rule;
	}
	
	public static int updateReadingRule(ReadingRuleContext readingRule, long ruleId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(FieldFactory.getReadingRuleFields());
		
		FacilioModule workflowModule = ModuleFactory.getWorkflowRuleModule();
		FacilioModule readingRuleModule = ModuleFactory.getReadingRuleModule();
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(fields)
														.table(readingRuleModule.getTableName())
														.innerJoin(workflowModule.getTableName())
														.on(readingRuleModule.getTableName()+".ID = "+workflowModule.getTableName()+".ID")
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(workflowModule))
														.andCondition(CriteriaAPI.getIdCondition(ruleId, readingRuleModule));
		
		return updateBuilder.update(FieldUtil.getAsProperties(readingRule));
	}
	
	protected static void setMatchedResources (ReadingRuleContext readingRule) throws Exception {
		if (readingRule.getAssetCategoryId() == -1) {
			long resourceId = readingRule.getResourceId();
			readingRule.setMatchedResources(Collections.singletonMap(resourceId, ResourceAPI.getExtendedResource(resourceId)));
		}
		else {
			List<AssetContext> categoryAssets = AssetsAPI.getAssetListOfCategory(readingRule.getAssetCategoryId());
			if (categoryAssets != null && !categoryAssets.isEmpty()) {
				fetchInclusionsExclusions(readingRule);
				
				Map<Long, ResourceContext> matchedResources = new HashMap<>();
				for (AssetContext asset : categoryAssets) {
					if ( (readingRule.getIncludedResources() == null 
							|| readingRule.getIncludedResources().isEmpty() 
							|| readingRule.getIncludedResources().contains(asset.getId()))
							&& (readingRule.getExcludedResources() == null 
								|| readingRule.getExcludedResources().isEmpty()
								|| !readingRule.getExcludedResources().contains(asset.getId()))
							) {
						matchedResources.put(asset.getId(), asset);
					}
				}
				readingRule.setMatchedResources(matchedResources);
			}
		}
	}
	
	private static void fetchInclusionsExclusions (ReadingRuleContext readingRule) throws Exception {
		FacilioModule module = ModuleFactory.getReadingRuleInclusionsExclusionsModule();
		List<FacilioField> fields = FieldFactory.getReadingRuleInclusionsExclusionsFields();
		FacilioField ruleId = FieldFactory.getAsMap(fields).get("ruleId");
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(module.getTableName())
																.select(fields)
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getCondition(ruleId, String.valueOf(readingRule.getId()), PickListOperators.IS));
		
		List<Map<String, Object>> props = selectRecordBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<Long> includedResources = new ArrayList<>();
			List<Long> excludedResources = new ArrayList<>();
			
			for (Map<String, Object> prop : props) {
				boolean isInclude = (boolean) prop.get("isInclude");
				if (isInclude) {
					includedResources.add((Long) prop.get("resourceId"));
				}
				else {
					excludedResources.add((Long) prop.get("resourceId"));
				}
			}
			
			if (!includedResources.isEmpty()) {
				readingRule.setIncludedResources(includedResources);
			}
			if (!excludedResources.isEmpty()) {
				readingRule.setExcludedResources(excludedResources);
			}
		}
	}
	
	public static List<ReadingRuleContext> getReadingRules() throws Exception {
		return getReadingRules(null);
	}
	
	public static List<ReadingRuleContext> getReadingRules(Criteria criteria) throws Exception {
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		fields.addAll(FieldFactory.getReadingRuleFields());
		
		FacilioModule workflowModule = ModuleFactory.getWorkflowRuleModule();
		FacilioModule readingRuleModule = ModuleFactory.getReadingRuleModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(readingRuleModule.getTableName())
														.innerJoin(workflowModule.getTableName())
														.on(readingRuleModule.getTableName()+".ID = "+workflowModule.getTableName()+".ID")
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(workflowModule));
		
		if(criteria != null) {
			selectBuilder.andCriteria(criteria);
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			List<ReadingRuleContext> readingRules = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				ReadingRuleContext readingRule = FieldUtil.getAsBeanFromMap(prop, ReadingRuleContext.class);
				if (readingRule.getCriteriaId() > 0) {
					readingRule.setCriteria(CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), readingRule.getCriteriaId()));
				}
				readingRules.add(readingRule);
			}
			return readingRules;
		}
		return null;
	}
}
