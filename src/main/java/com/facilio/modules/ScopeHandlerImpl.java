package com.facilio.modules;

import java.util.*;

import com.facilio.beans.GlobalScopeBean;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.GlobalScopeVariableEvaluationContext;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.util.GlobalScopeUtil;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.datastructure.dag.DAG;
import com.facilio.datastructure.dag.DAGCache;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.lang3.tuple.Pair;

public class ScopeHandlerImpl extends ScopeHandler {

    /*
     * Having separate methods because there might be need to handle scope based on select or insert.
     * e.g : Even though main app has site scope, it shouldn't be handled during site insertion
     * No separate method for delete because delete is handled via select
    */

    /*
        Insert will be called for each module separately
        e.g : if we are inserting WO, first this method will be called for Ticket, then for WO and so on
        Can be used add default value for scope variables
     */
    @Override
    public Collection<FacilioField> updateValuesForInsertAndGetFields(FacilioModule module, List<Map<String, Object>> props) {
        try {
			if (AccountUtil.applyDBScoping(module)){
				ScopeFieldsAndCriteria scopeFields = constructScopingFieldsAndCriteria(module, null, true);
				if(scopeFields != null) {
					Collection<FacilioField> fields = scopeFields.getFields();

					//can be removed once siteid is added as a field entry
					if(FieldUtil.isSiteIdFieldPresent(module, true)) {
						if (fields == null) {
							fields = new ArrayList<>();
						}
						fields.add((FieldFactory.getSiteIdField(module)));
					}

					return fields;
				}
			}
			else {
				if(FieldUtil.isSiteIdFieldPresent(module, true)) {
					return Collections.singletonList(FieldFactory.getSiteIdField(module));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }

    @Override
    public ScopeFieldsAndCriteria updateValuesForUpdateAndGetFieldsAndCriteria (FacilioModule module, Collection<FacilioModule> joinModules, Map<String, Object> prop) {
        try {
			if (AccountUtil.applyDBScoping(module)){
				Map<Long, ScopingConfigContext> scopingMap = AccountUtil.getCurrentAppScopingMap();
				if(MapUtils.isNotEmpty(scopingMap)) {
					ScopingConfigContext moduleScoping = scopingMap.get(module.getModuleId());
					if(moduleScoping != null) {
						ScopeFieldsAndCriteria scopeFieldCriteria = constructScopingFieldsAndCriteria(module, joinModules, false);
						Map<String, Object> globalScopingValues = AccountUtil.getSwitchScopingFieldMap();
						if(MapUtils.isNotEmpty(globalScopingValues)) {
							List<FacilioField> toRemove = new ArrayList();
							for(FacilioField field : scopeFieldCriteria.getFields()) {
								if(globalScopingValues.containsKey(field.getName())) {
									prop.remove(field.getName());
									toRemove.add(field);
								}
								else {
									FacilioModule extendedModule = module.getExtendModule();
					                while(extendedModule != null) {
					                	FacilioField parentModulefield = RecordAPI.getField(field.getName(), extendedModule.getName());
					                	if(parentModulefield != null){
					                		scopeFieldCriteria.getFields().add(parentModulefield);
					                	}
					                    extendedModule = extendedModule.getExtendModule();
					                }
								}
							}
							if(CollectionUtils.isNotEmpty(toRemove)){
								scopeFieldCriteria.getFields().removeAll(toRemove);
							}
							
						}
					}
				}
				if (FieldUtil.isSiteIdFieldPresent(module)) {
					return constructSiteFieldsAndCriteria(module, true);
				}
        	}
        	else if (FieldUtil.isSiteIdFieldPresent(module)) {
			    long currentSiteId = AccountUtil.getCurrentSiteId();
			    if (currentSiteId > 0) {
			        prop.remove("siteId"); //Not allowing to change site id if it's scope is set.
			    }
			    return constructSiteFieldsAndCriteria(module, true);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }

    @Override
    public ScopeFieldsAndCriteria getFieldsAndCriteriaForSelect(FacilioModule module, Collection<FacilioModule> joinModules) {
        try {
			if (AccountUtil.applyDBScoping(module)) {
				return constructScopingFieldsAndCriteria(module, joinModules, false);
			} else {
				//special handling for altayer - tenants module inorder to temporarily support multi site scoping. can be removed once multi select lookup is handled
				if (AccountUtil.getCurrentOrg() != null && (AccountUtil.getCurrentOrg().getOrgId() == 407l || AccountUtil.getCurrentOrg().getOrgId() == 418l || AccountUtil.getCurrentOrg().getOrgId() == 17l) && StringUtils.isNotEmpty(module.getName()) && module.getName().equals(FacilioConstants.ContextNames.TENANT)) {
					return null;
				}

				if (FieldUtil.isSiteIdFieldPresent(module)) {
					return constructSiteFieldsAndCriteria(module, false);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }
    
    private ScopeFieldsAndCriteria constructSiteFieldsAndCriteria (FacilioModule module, boolean isUpdate) throws Exception{
        FacilioField siteIdField = FieldFactory.getSiteIdField(module);
        long currentSiteId = AccountUtil.getCurrentSiteId();
        Criteria criteria = new Criteria();
        if (currentSiteId > 0) {// Currently we are handling site id criteria only for the module and not for all parent modules
            Condition siteCondition = CriteriaAPI.getCondition(siteIdField, String.valueOf(currentSiteId), NumberOperators.EQUALS);
            criteria.addAndCondition(siteCondition);
        }
        else {
			List<Long> mySitIds = CommonCommandUtil.getMySiteIds();
			if(CollectionUtils.isNotEmpty(mySitIds)) {
				Condition siteCondition = CriteriaAPI.getCondition(siteIdField, StringUtils.join(mySitIds, ","), NumberOperators.EQUALS);
				criteria.addAndCondition(siteCondition);
			}
		}
        List<FacilioField> fields = null;
        if (isUpdate) { //Will allow site id to be updated only if current site is -1. Also site fields of all parent modules needs to be added in fields because those have to be updated too
            if (currentSiteId == -1) {
                fields = new ArrayList<>();
                criteria = new Criteria();
                fields.add(siteIdField);
                FacilioModule extendedModule = module.getExtendModule();
                while(extendedModule != null) {
                    if (FieldUtil.isSiteIdFieldPresent(extendedModule)) {
                        fields.add(FieldFactory.getSiteIdField(extendedModule));
                    }
                    extendedModule = extendedModule.getExtendModule();
                }
            }
        }
        else {
            fields = Collections.singletonList(siteIdField);
        }
        return ScopeFieldsAndCriteria.of(fields, criteria);
    }

	private ScopeFieldsAndCriteria constructScopingFieldsAndCriteria(FacilioModule primaryModule, Collection<FacilioModule> joinModules, boolean isInsert) throws Exception {
		if(AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().isSuperAdmin()) {
			return ScopeFieldsAndCriteria.of(new ArrayList<>(), null);
		}
		if(joinModules == null) {
			joinModules = Collections.singletonList(primaryModule);
		}
		else {
			List<FacilioModule> moduleList = (List<FacilioModule>) joinModules;
			moduleList.add(0,primaryModule);
			joinModules = moduleList;
		}
		List<FacilioField> fields = new ArrayList<>();
		Criteria criteria = null;

		for(FacilioModule module : joinModules) {
			if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCOPE_VARIABLE)) {
				Pair<List<FacilioField>, Criteria> fieldsAndCriteriaForSubquery = constructScopeVariableCriteria(module);
				if (fieldsAndCriteriaForSubquery != null) {
					if(!isInsert) {
						if (CollectionUtils.isNotEmpty(fieldsAndCriteriaForSubquery.getLeft())) {
							fields.addAll(fieldsAndCriteriaForSubquery.getLeft());
						}
						if (criteria == null) {
							criteria = new Criteria();
						}
						criteria.andCriteria(fieldsAndCriteriaForSubquery.getRight());
					}
				}
			}
			ScopingConfigContext scoping = AccountUtil.getCurrentAppScopingMap(module.getModuleId());
			ScopingConfigContext moduleScoping = FieldUtil.cloneBean(scoping,ScopingConfigContext.class);
			if(moduleScoping != null) {
				if(!isInsert) {
					fields.addAll(ApplicationApi.computeValueForScopingField(moduleScoping, module));
					if (criteria == null) {
						criteria = new Criteria();
					}
					criteria.andCriteria(moduleScoping.getCriteria());
				}
			}
			if(module.hideFromParents()) {
				break;
			}
		}
		return ScopeFieldsAndCriteria.of(fields, criteria);
	}

	private Pair<List<FacilioField>, Criteria> constructScopeVariableCriteria(FacilioModule module) throws Exception {
		if(module != null && (module.getName().equals(FacilioConstants.Induction.INDUCTION_TEMPLATE) || module.getName().equals(FacilioConstants.Inspection.INSPECTION_TEMPLATE))){
			if(AccountUtil.getCurrentApp() != null) {
				return getDefaultCriteriaForModules(module);
			}
			return null;
		}
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCOPE_SUBQUERY)) {
			return getSubQueryCriteria(module);
		}
		return getGlobalScopeIdsBasedCriteria(module);
	}


	//only for induction template and inspection template - need to remove this once sites are moved to single field in module
	private Pair<List<FacilioField>, Criteria> getDefaultCriteriaForModules(FacilioModule module) throws Exception {
		List<FacilioField> fieldList = new ArrayList<>();
		Criteria criteria = new Criteria();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(module.getName().equals(FacilioConstants.Inspection.INSPECTION_TEMPLATE)){
			FacilioField siteId = modBean.getField("siteId",module.getName());
			fieldList.add(siteId);
			Condition condition = CriteriaAPI.getCondition(siteId, getEvaluatedValuesForValueGenerator("com.facilio.modules.SiteValueGenerator"), PickListOperators.IS);
			condition.setModuleName(module.getName());
			criteria.addAndCondition(condition);
			FacilioField multiSiteField = modBean.getField("sites",module.getName());
			Condition condition1 = CriteriaAPI.getCondition(multiSiteField,getEvaluatedValuesForValueGenerator("com.facilio.modules.ContainsSiteValueGenerator"), MultiFieldOperators.CONTAINS);
			condition1.setModuleName(module.getName());
			criteria.addOrCondition(condition1);
		} else if(module.getName().equals(FacilioConstants.Induction.INDUCTION_TEMPLATE)){
			FacilioField siteId = modBean.getField("siteApplyTo",module.getName());
			fieldList.add(siteId);
			Condition condition = CriteriaAPI.getCondition(siteId, Boolean.TRUE.toString(), BooleanOperators.IS);
			condition.setModuleName(module.getName());
			criteria.addAndCondition(condition);
			FacilioField multiSiteField = modBean.getField("sites",module.getName());
			Condition condition1 = CriteriaAPI.getCondition(multiSiteField,getEvaluatedValuesForValueGenerator("com.facilio.modules.ContainsSiteValueGenerator"), MultiFieldOperators.CONTAINS);
			condition1.setModuleName(module.getName());
			criteria.addOrCondition(condition1);
		} else {
			return null;
		}
		return Pair.of(fieldList, criteria);
	}

	private String getEvaluatedValuesForValueGenerator(String className) throws Exception {
		Class<? extends ValueGenerator> classObject = (Class<? extends ValueGenerator>) Class.forName(className);
		ValueGenerator valueGenerator = classObject.newInstance();
		String val = ScopeOperator.SCOPING_IS.getEvaluatedValues(valueGenerator);
		if(StringUtils.isNotEmpty(val)){
			return val;
		}
		return StringUtils.EMPTY;
	}

	private Pair<List<FacilioField>,Criteria> getGlobalScopeIdsBasedCriteria(FacilioModule module) throws Exception {
		List<FacilioField> fieldList = new ArrayList<>();
		Criteria criteria = new Criteria();
		Map<String, GlobalScopeVariableEvaluationContext> variableEvaluationList = AccountUtil.getGlobalScopeVariableValues();
		if(MapUtils.isNotEmpty(variableEvaluationList)){
			for(Map.Entry<String,GlobalScopeVariableEvaluationContext> entry : variableEvaluationList.entrySet()){
				GlobalScopeVariableEvaluationContext variableEvaluation = entry.getValue();
				if(module != null) {
					Pair<List<FacilioField>, Criteria> criteriaAndFields = variableEvaluation.getGlobalScopeVariableCriteriaForModule(module.getName());
					if(criteriaAndFields != null) {
						if (CollectionUtils.isNotEmpty(criteriaAndFields.getLeft())) {
							fieldList.addAll(criteriaAndFields.getLeft());
						}
						if (criteriaAndFields.getRight() != null){
							criteria.andCriteria(criteriaAndFields.getRight());
						}
					}
				}
			}
			return Pair.of(fieldList, criteria);
		}
		return null;
	}


	private Pair<List<FacilioField>, Criteria> getSubQueryCriteria(FacilioModule module) throws Exception {
		if(AccountUtil.getCurrentApp() != null) {
			GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
			DAGCache scopeGraph = scopeBean.getGlobalScopeGraph(AccountUtil.getCurrentApp().getId());
			return GlobalScopeUtil.getScopeCriteriaFromGraph(module, scopeGraph);
		}
		return null;
	}
}