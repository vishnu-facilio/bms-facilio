package com.facilio.componentpackage.interfaces;

import com.facilio.chain.FacilioContext;

import java.util.List;

public interface PackageBean<T> {

    public List<T> fetchAllComponentsToPackage() throws Exception;

    public List<T> fetchComponents(List<Long> ids) throws Exception;

    public void convertToXMLComponent(List<T> components) throws Exception;

    public void validateComponentToCreate(List<T> components) throws Exception;

    public void createComponentFromXML(List<T> components) throws Exception;

    public void checkComponentAvailability(List<Long> ids) throws Exception;

    public void updateComponentFromXML(List<T> components) throws Exception;

    public void deleteComponentFromXML(List<T> components) throws Exception;

}
