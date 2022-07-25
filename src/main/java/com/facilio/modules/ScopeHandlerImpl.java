package com.facilio.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;

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
		if(joinModules == null) {
			joinModules = Collections.singletonList(primaryModule);
		}
		else {
			joinModules.add(primaryModule);
		}
		List<FacilioField> fields = new ArrayList<FacilioField>();
		Criteria criteria = null;

		for(FacilioModule module : joinModules) {
			ScopingConfigContext moduleScoping = AccountUtil.getCurrentAppScopingMap(module.getModuleId());
			if(moduleScoping != null) {
				fields.addAll(ApplicationApi.computeValueForScopingField(moduleScoping, module));
				if(!isInsert) {
					if (criteria == null) {
						criteria = new Criteria();
					}
					criteria.andCriteria(moduleScoping.getCriteria());
				}
			}
		}
		return ScopeFieldsAndCriteria.of(fields, criteria);
	}
}
