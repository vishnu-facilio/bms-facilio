package com.facilio.dependentObjects;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DependencyConstants {

    public static class FeatureNames {
        public static final String TIMELINEVIEW = "Timeline View";
    }

    public static final String COMPONENT_NAME = "name";
    public static final String MODULE_NAME = "moduleName";

    //Add the feature table details here to find the dependencies
    public enum DependantFeatures {

        TIMELINE_VIEW(
                FeatureNames.TIMELINEVIEW,
                new FacilioModule[]{ModuleFactory.getTimelineViewModule(),ModuleFactory.getViewsModule()},
                new FacilioField[]{FieldFactory.getIdField(ModuleFactory.getTimelineViewModule()),
                                    FieldFactory.getIdField(ModuleFactory.getViewsModule())},
                FieldFactory.getField("displayName", "DISPLAY_NAME", ModuleFactory.getViewsModule(), FieldType.STRING),
                true,
                null,
                FieldFactory.getField("moduleName", "MODULENAME", ModuleFactory.getViewsModule(), FieldType.STRING)
        );

        private String featureName;
        private FacilioModule[] moduleArr;
        private FacilioField[] joinFields;
        private FacilioField nameField;
        //To fetch the module name of a linked component
        private Boolean isModuleBased;
        private FacilioField moduleIdField;
        private FacilioField moduleNameField;

        DependantFeatures(String featureName, FacilioModule[] moduleArr, FacilioField[] joinFields, FacilioField nameField,
                          Boolean isModuleBased, FacilioField moduleIdField, FacilioField moduleNameField)
        {
            this.featureName = featureName;
            this.moduleArr = moduleArr;
            this.joinFields = joinFields;
            this.nameField = nameField;
            this.isModuleBased = isModuleBased;
            this.moduleIdField = moduleIdField;
            this.moduleNameField = moduleNameField;
        }

        public String getFeatureName() {
            return featureName;
        }
        public void setFeatureName(String featureName) {
            this.featureName = featureName;
        }

        public FacilioModule[] getModuleArr() {
            return moduleArr;
        }
        public void setModuleArr(FacilioModule[] moduleArr) {
            this.moduleArr = moduleArr;
        }

        public FacilioField[] getJoinFields() {
            return joinFields;
        }
        public void setJoinFields(FacilioField[] joinFields) {
            this.joinFields = joinFields;
        }

        public FacilioField getNameField() {
            return nameField;
        }
        public void setNameField(FacilioField nameField) {
            this.nameField = nameField;
        }

        public Boolean getModuleBased() {
            return isModuleBased;
        }
        public void setModuleBased(Boolean moduleBased) {
            isModuleBased = moduleBased;
        }

        public FacilioField getModuleIdField() {
            return moduleIdField;
        }
        public void setModuleIdField(FacilioField moduleIdField) {
            this.moduleIdField = moduleIdField;
        }

        public FacilioField getModuleNameField() {
            return moduleNameField;
        }
        public void setModuleNameField(FacilioField moduleNameField) {
            this.moduleNameField = moduleNameField;
        }

    }

    public static class WeekendDependencies {
        public static final Map<DependantFeatures, FacilioField> featureVsDependantColumns = Collections.unmodifiableMap(initDependencyMap());

        //Add the features which are linked to Weekends
        private static Map<DependencyConstants.DependantFeatures, FacilioField> initDependencyMap() {
            //Feature Vs Linking Column Field
            Map<DependencyConstants.DependantFeatures, FacilioField> dependencyMap = new HashMap<>();
            dependencyMap.put(DependencyConstants.DependantFeatures.TIMELINE_VIEW,
                                    FieldFactory.getNumberField("weekendId", "WEEKENDID", ModuleFactory.getTimelineViewModule()));
            return dependencyMap;
        }
    }

}
