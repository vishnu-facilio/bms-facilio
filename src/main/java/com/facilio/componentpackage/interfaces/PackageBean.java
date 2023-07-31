package com.facilio.componentpackage.interfaces;

import com.facilio.xml.builder.XMLBuilder;

import java.util.List;
import java.util.Map;

public interface PackageBean<T> {

    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception;

    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception;

    public Map<Long, T> fetchComponents(List<Long> ids) throws Exception;

    public void convertToXMLComponent(T component, XMLBuilder element) throws Exception;

    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception;

    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception;

    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception;

    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception;

    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception;

    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception;

    public void deleteComponentFromXML(List<Long> ids) throws Exception;

    default void addPickListConf() throws Exception {

    }

}
