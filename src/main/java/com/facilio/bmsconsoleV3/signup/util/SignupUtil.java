package com.facilio.bmsconsoleV3.signup.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.facilio.bmsconsole.commands.AddSubModulesSystemFieldsCommad;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

public class SignupUtil {
	public static void addNotesAndAttachmentModule(FacilioModule module) throws Exception {
		// TODO Auto-generated method stub
		
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule customNotesModule = new FacilioModule();
        customNotesModule.setName(module.getName() + "notes");
        customNotesModule.setDisplayName(module.getDisplayName() + " Notes");
        customNotesModule.setTableName("Notes");
        customNotesModule.setType(FacilioModule.ModuleType.NOTES);
        modules.add(customNotesModule);


        FacilioModule customAttachmentModule = new FacilioModule();
        customAttachmentModule.setName(module.getName() + "attachments");
        customAttachmentModule.setDisplayName(module.getDisplayName() + " Attachments");
        customAttachmentModule.setTableName("Attachments");
        customAttachmentModule.setType(FacilioModule.ModuleType.ATTACHMENTS);

        modules.add(customAttachmentModule);

        if (modules != null && modules.size() > 0) {
            for (FacilioModule subModule: modules) {
            	AddSubModulesSystemFieldsCommad.addModuleBasedFields(module, subModule);
            }
        }
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
    	
	}

        /**
         * Helper methods declared to get the different FacilioFields.
         * viz. {@link StringField}, {@link NumberField}, {@link BooleanField}, {@link SystemEnumField}, {@link LookupField}
         */

        /**
         * Helper method to get {@link StringField}
         */
        public static StringField getStringField(FacilioModule module, String name, String displayName, String columnName, FacilioField.FieldDisplayType displayType,
                                           Boolean required, Boolean disabled, Boolean isDefault, Boolean isMainField, Long orgId) {
                StringField stringField = FieldFactory.getField(name, displayName, columnName, module, FieldType.STRING);
                stringField.setDisplayType(displayType);
                stringField.setRequired(required);
                stringField.setDisabled(disabled);
                stringField.setDefault(isDefault);
                stringField.setMainField(isMainField);
                stringField.setOrgId(orgId);
                return stringField;
        }

        /**
         * Helper method to get {@link NumberField}
         */
        public static NumberField getNumberField(FacilioModule module, String name, String displayName, String columnName,
                                           FacilioField.FieldDisplayType displayType, Boolean required, Boolean disabled,
                                           Boolean isDefault, Long orgId) {
                NumberField numberField = FieldFactory.getField(name, displayName, columnName, module, FieldType.NUMBER);
                numberField.setDisplayType(displayType);
                numberField.setRequired(required);
                numberField.setDisabled(disabled);
                numberField.setDefault(isDefault);
                numberField.setOrgId(orgId);
                return numberField;
        }

        /**
         * Helper method to get {@link BooleanField}
         */
        public static BooleanField getBooleanField(FacilioModule module, String name, String displayName, String columnName,
                                             FacilioField.FieldDisplayType displayType, Long accessType, Boolean required,
                                             Boolean disabled, Boolean isDefault, Long orgId) {
                BooleanField booleanField = FieldFactory.getField(name, displayName, columnName, module, FieldType.BOOLEAN);
                booleanField.setDisplayType(displayType);
                booleanField.setRequired(required);
                booleanField.setDisabled(disabled);
                booleanField.setDefault(isDefault);
                booleanField.setOrgId(orgId);

                if (accessType != null) {
                        booleanField.setAccessType(accessType);
                }
                return booleanField;
        }

        /**
         * Helper method to get {@link SystemEnumField}
         */
        public static SystemEnumField getSystemEnumField(FacilioModule module, String name, String displayName, String columnName,
                                                   String enumName, FacilioField.FieldDisplayType displayType, Boolean required,
                                                   Boolean disabled, Boolean isDefault, Long orgId) {
                SystemEnumField systemEnumField = FieldFactory.getField(name, displayName, columnName, module, FieldType.SYSTEM_ENUM);
                systemEnumField.setDisplayType(displayType);
                systemEnumField.setRequired(required);
                systemEnumField.setDisabled(disabled);
                systemEnumField.setDefault(isDefault);
                systemEnumField.setOrgId(orgId);
                systemEnumField.setEnumName(enumName);
                return systemEnumField;
        }

        /**
         * Helper method to get {@link LookupField}
         */
        public static LookupField getLookupField(FacilioModule fieldModule, FacilioModule lookUpModule, String name, String displayName,
                                           String columnName, String specialType, FacilioField.FieldDisplayType displayType, Boolean required,
                                           Boolean disabled, Boolean isDefault, Long orgId) {
                return getLookupField(fieldModule, lookUpModule, name, displayName, columnName, specialType, displayType, required, disabled, isDefault, orgId, false);
        }
        public static LookupField getLookupField(FacilioModule fieldModule, FacilioModule lookUpModule, String name, String displayName,
                                           String columnName, String specialType, FacilioField.FieldDisplayType displayType, Boolean required,
                                           Boolean disabled, Boolean isDefault, Long orgId, boolean isMainField) {
                LookupField lookupField = FieldFactory.getField(name, displayName, columnName, fieldModule, FieldType.LOOKUP);
                lookupField.setDisplayType(displayType);
                lookupField.setRequired(required);
                lookupField.setDisabled(disabled);
                lookupField.setDefault(isDefault);
                lookupField.setOrgId(orgId);
                lookupField.setMainField(isMainField);

                if (lookUpModule != null) {
                        lookupField.setLookupModule(lookUpModule);
                        lookupField.setLookupModuleId(lookUpModule.getModuleId());
                }

                if (specialType != null) {
                        lookupField.setSpecialType(specialType);
                }
                return lookupField;
        }

        /**
         * Helper method to add modules via using SystemModuleChain
         *
         * @param modules - List of modules to be added as System Module
         * @throws Exception
         */
        public static void addModules(FacilioModule... modules) throws Exception {
                FacilioChain addModulesChain = TransactionChainFactory.addSystemModuleChain();
                addModulesChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Arrays.asList(modules));
                addModulesChain.execute();
        }
}
