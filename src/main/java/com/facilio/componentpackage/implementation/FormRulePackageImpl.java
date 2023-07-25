package com.facilio.componentpackage.implementation;

import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.xml.builder.XMLBuilder;

import java.util.List;
import java.util.Map;

public class FormRulePackageImpl implements PackageBean<FormRuleContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, FormRuleContext> fetchComponents(List<Long> ids) throws Exception {
        return null;
    }

    @Override
    public void convertToXMLComponent(FormRuleContext component, XMLBuilder element) throws Exception {

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
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        return null;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }
}
