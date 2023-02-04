package com.facilio.componentpackage.implementation;

import com.facilio.componentpackage.interfaces.PackageBean;

import java.util.List;

public class ModulePackageBeanImpl<FacilioModule> extends PackageBeanImpl implements PackageBean {

    @Override
    public List fetchAllComponentsToPackage() throws Exception {
        return null;
    }

    @Override
    public List fetchComponents(List ids) throws Exception {
        return null;
    }

    @Override
    public void convertToXMLComponent(List components) throws Exception {

    }

    @Override
    public void validateComponentToCreate(List components) throws Exception {

    }

    @Override
    public void createComponentFromXML(List components) throws Exception {

    }

    @Override
    public void checkComponentAvailability(List ids) throws Exception {

    }

    @Override
    public void updateComponentFromXML(List components) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List components) throws Exception {

    }

}
