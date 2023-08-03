package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ValidationRulesAPI;
import com.facilio.bmsconsole.workflow.rule.ValidationContext;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.ValidationRulePackageUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationRulePackageBeanImpl implements PackageBean<ValidationContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return ValidationRulePackageUtil.getFormValidationRuleIdVsFormId();
    }

    @Override
    public Map<Long, ValidationContext> fetchComponents(List<Long> ids) throws Exception {
        return ValidationRulePackageUtil.getAllValidationRule(ids);
    }

    @Override
    public void convertToXMLComponent(ValidationContext validationRule, XMLBuilder validationRuleElement) throws Exception {

        if(validationRule==null &&validationRule.getParentId()<0 && validationRule.getId()<0 && (validationRule.getNamedCriteriaId()<0 || validationRule.getNamedCriteria()==null)){
            return;
        }

        ValidationRulePackageUtil.convertValidationRuleToXMLComponent(validationRule,validationRuleElement);
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        return ValidationRulePackageUtil.getExistingIdsByXMLData(uniqueIdVsXMLData);
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();

        ValidationContext validationRule = null;
        FacilioModule validationModule = ModuleFactory.getFormValidationRuleModule();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder validationRuleElement = idVsData.getValue();
            ModuleBean moduleBean = Constants.getModBean();

            validationRule = ValidationRulePackageUtil.constructValidationRuleFromXMLBuilder(validationRuleElement,moduleBean);

            ValidationRulesAPI.addOrUpdateFormValidations(validationRule,validationModule);

            uniqueIdentifierVsComponentId.put(idVsData.getKey(), validationRule.getId());

        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule validationModule = ModuleFactory.getFormValidationRuleModule();

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long ruleId = idVsData.getKey();

            XMLBuilder ruleElement = idVsData.getValue();

            ValidationContext validationRule = ValidationRulePackageUtil.constructValidationRuleFromXMLBuilder(ruleElement,moduleBean);
            validationRule.setId(ruleId);

            ValidationRulesAPI.addOrUpdateFormValidations(validationRule,validationModule);

        }

    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

        ValidationRulesAPI.deleteValidations(ids, ModuleFactory.getFormValidationRuleModule());

    }
}
