package com.facilio.componentpackage.implementation;

import com.facilio.componentpackage.constants.PackageConstants.FieldConstants;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.modules.fields.FacilioField;
import com.facilio.xml.builder.XMLBuilder;

import java.util.List;
import java.util.Map;

public class FieldPackageBeanImpl implements PackageBean<FacilioField> {

    @Override
    public List<Long> fetchAllComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, FacilioField> fetchComponents(List<Long> ids) throws Exception {
        return null;
    }

    @Override
    public XMLBuilder convertToXMLComponent(FacilioField facilioField) throws Exception {
            XMLBuilder fieldElement = XMLBuilder.create(ComponentType.FIELD.getValue());
            fieldElement.element(PackageConstants.NAME).text(facilioField.getName());
            fieldElement.element(PackageConstants.DISPLAY_NAME).text(facilioField.getDisplayName());
            fieldElement.element(PackageConstants.MODULENAME).text(facilioField.getModule().getName());
            fieldElement.element(FieldConstants.REQUIRED).text(String.valueOf(facilioField.isRequired()));
            fieldElement.element(FieldConstants.IS_DEFAULT).text(String.valueOf(facilioField.isDefault()));
            fieldElement.element(FieldConstants.DATA_TYPE).text(String.valueOf(facilioField.getDataType()));
            fieldElement.element(FieldConstants.MAIN_FIELD).text(String.valueOf(facilioField.isMainField()));
            fieldElement.element(FieldConstants.DISPLAY_TYPE).text(String.valueOf(facilioField.getDisplayTypeInt()));

        return fieldElement;
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
    public Map<String, Long> updateComponentFromXML(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }
}
