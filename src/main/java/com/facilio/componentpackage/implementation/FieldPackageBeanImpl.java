package com.facilio.componentpackage.implementation;

import com.facilio.componentpackage.constants.PackageConstants.FieldXMLConstants;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.modules.fields.FacilioField;
import com.facilio.xml.builder.XMLBuilder;

import java.util.List;
import java.util.Map;

public class FieldPackageBeanImpl implements PackageBean<FacilioField> {

    @Override
    public List<Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public List<Long> fetchCustomComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, FacilioField> fetchComponents(List<Long> ids) throws Exception {
        return null;
    }

    @Override
    public void convertToXMLComponent(FacilioField facilioField, XMLBuilder fieldElement) throws Exception {
        fieldElement.element(PackageConstants.NAME).text(facilioField.getName());
        fieldElement.element(PackageConstants.DISPLAY_NAME).text(facilioField.getDisplayName());
        fieldElement.element(PackageConstants.MODULENAME).text(facilioField.getModule().getName());
        fieldElement.element(FieldXMLConstants.REQUIRED).text(String.valueOf(facilioField.isRequired()));
        fieldElement.element(FieldXMLConstants.IS_DEFAULT).text(String.valueOf(facilioField.isDefault()));
        fieldElement.element(FieldXMLConstants.DATA_TYPE).text(String.valueOf(facilioField.getDataType()));
        fieldElement.element(FieldXMLConstants.MAIN_FIELD).text(String.valueOf(facilioField.isMainField()));
        fieldElement.element(FieldXMLConstants.DISPLAY_TYPE).text(String.valueOf(facilioField.getDisplayTypeInt()));

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
