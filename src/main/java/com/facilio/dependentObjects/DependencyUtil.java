package com.facilio.dependentObjects;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.exception.DependencyException;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class DependencyUtil {

    public static JSONObject getDependantComponentDetails(Map<DependencyConstants.DependantFeatures, FacilioField> dependencyMap, Object linkingId) throws Exception {
        return getDependantComponentDetails(dependencyMap, linkingId, false);
    }
    public static JSONObject getDependantComponentDetails(Map<DependencyConstants.DependantFeatures, FacilioField> dependencyMap, Object linkingId, boolean throwError) throws Exception {

        JSONObject featureVsDetails = new JSONObject();
        for(Map.Entry<DependencyConstants.DependantFeatures, FacilioField> dependantConfig : dependencyMap.entrySet())
        {
            DependencyConstants.DependantFeatures dependencyDetails = dependantConfig.getKey();
            FacilioModule[] dependantModules = dependencyDetails.getModuleArr();
            FacilioField[] joinFields = dependencyDetails.getJoinFields();
            FacilioField linkingField = dependantConfig.getValue();
            FacilioField nameField = dependencyDetails.getNameField();
            Boolean isModuleBased = dependencyDetails.getModuleBased();
            FacilioField moduleNameField = dependencyDetails.getModuleNameField();
            FacilioField moduleIdField = dependencyDetails.getModuleIdField();

            List<FacilioField> fields = new ArrayList<>();
            fields.addAll(Arrays.asList(joinFields));
            fields.add(dependencyDetails.getNameField());
            if(isModuleBased && moduleNameField != null) {
                fields.add(dependencyDetails.getModuleNameField());
            }
            if(isModuleBased && moduleIdField != null) {
                fields.add(dependencyDetails.getModuleIdField());
            }

            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
            builder.table(dependantModules[0].getTableName());
            if(dependantModules.length > 1) {
                for(int i = 1; i < dependantModules.length ; i++) {
                    builder.innerJoin(dependantModules[i].getTableName())
                            .on(joinFields[i-1].getCompleteColumnName() +"="+ joinFields[i].getCompleteColumnName());
                }
            }
            builder.andCondition(CriteriaAPI.getCondition(linkingField, String.valueOf(linkingId), NumberOperators.EQUALS));
            builder.select(fields);
            List<Map<String, Object>> props = builder.get();

            if(!CollectionUtils.isEmpty(props))
            {
                JSONArray resultList = (featureVsDetails.containsKey(dependencyDetails.getFeatureName())) ? (JSONArray) featureVsDetails.get(dependencyDetails.getFeatureName()) : new JSONArray();
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                for(Map<String, Object> prop :props) {

                    JSONObject resultMap = new JSONObject();
                    resultMap.put(DependencyConstants.COMPONENT_NAME, prop.get(nameField.getName()));
                    if(isModuleBased)
                    {
                        if(moduleNameField != null)
                        {
                            resultMap.put(DependencyConstants.MODULE_NAME, modBean.getModule((String)prop.get(moduleNameField.getName())).getDisplayName());
                        }
                        else
                        {
                            resultMap.put(DependencyConstants.MODULE_NAME, modBean.getModule((String)prop.get(moduleIdField.getName())).getDisplayName());
                        }
                    }
                    resultList.add(resultMap);
                }
                featureVsDetails.put(dependencyDetails.getFeatureName(), resultList);
            }
        }
        if(throwError && featureVsDetails != null && featureVsDetails.size() > 0)
        {
            throw new DependencyException(ErrorCode.DEPENDENCY_EXISTS, "Dependent components exists", featureVsDetails);
        }
        return featureVsDetails;
    }
}
