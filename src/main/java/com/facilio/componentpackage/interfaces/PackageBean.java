package com.facilio.componentpackage.interfaces;

import com.facilio.xml.builder.XMLBuilder;

import java.util.List;
import java.util.Map;

public interface PackageBean<T> {

    public List<Long> fetchSystemComponentIdsToPackage() throws Exception;

    public List<Long> fetchCustomComponentIdsToPackage() throws Exception;

    public Map<Long, T> fetchComponents(List<Long> ids) throws Exception;

    public void convertToXMLComponent(T component, XMLBuilder element) throws Exception;

    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception;

    public Map<String, Long> createComponentFromXML(List<XMLBuilder> components) throws Exception;

    public Map<String, Long> updateComponentFromXML(Map<Long, XMLBuilder> idVscomponents) throws Exception;

    public void deleteComponentFromXML(List<Long> ids) throws Exception;

}
