package com.facilio.componentpackage.implementation;

import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.xml.builder.XMLBuilder;

import java.util.List;
import java.util.Map;

public class AppPackageBeanImpl implements PackageBean<ApplicationContext> {

    @Override
    public List<Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public List<Long> fetchCustomComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, ApplicationContext> fetchComponents(List<Long> ids) throws Exception {
        return null;
    }

    @Override
    public void convertToXMLComponent(ApplicationContext component, XMLBuilder element) throws Exception {

    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> updateComponentFromXML(Map<Long, XMLBuilder> idVscomponents) throws Exception {
        return null;
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }
}
