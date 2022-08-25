package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AddCustomModuleDataFailureClassModuleRelationship extends FacilioCommand {

    private static Logger LOGGER =
            LogManager.getLogger(AddCustomModuleDataFailureClassModuleRelationship.class.getName());

    FacilioModule failureClassModule;
    FacilioModule failureCodeModule;
    FacilioModule failureProblemModule;
    FacilioModule failureCauseModule;
    FacilioModule failureRemedyModule;
    FacilioModule customModule;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean failureClassEnabled = (boolean)
                context.get(FacilioConstants.ContextNames.FAILURE_REPORTING_ENABLED);
        if (!failureClassEnabled) {
            LOGGER.info("failure reporting not enabled for the module");
            return false;
        }
        LOGGER.info("creating failure reporting relation module");

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        customModule = (FacilioModule) context.get("module");
        failureClassModule = modBean.getModule(FacilioConstants.ContextNames.FAILURE_CLASS);
        failureCodeModule = modBean.getModule(FacilioConstants.ContextNames.FAILURE_CODE);
        failureProblemModule = modBean.getModule(FacilioConstants.ContextNames.FAILURE_CODE_PROBLEMS);
        failureCauseModule = modBean.getModule(FacilioConstants.ContextNames.FAILURE_CODE_CAUSES);
        failureRemedyModule = modBean.getModule(FacilioConstants.ContextNames.FAILURE_CODE_REMEDIES);


        FacilioModule cmFailureClassRel =
                composeRelationshipModule(customModule.getName(), customModule.getDisplayName());

        List<FacilioModule> modules = new ArrayList<>();
        modules.add(cmFailureClassRel);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        return false;
    }

    private FacilioModule composeRelationshipModule(String name, String displayName) throws Exception {
        if (FacilioUtil.isEmptyOrNull(name) || FacilioUtil.isEmptyOrNull(displayName)) {
            throw new IllegalArgumentException("name/ displayName cannot be empty");
        }
        name = name + "FailureClassRelationship";
        displayName = displayName + " FailureClass Relationship";
        final String tableName = "CustomModuleData_FailureClass_Relationship";
        final FacilioModule.ModuleType entityType =
                FacilioModule.ModuleType.CUSTOM_MODULE_DATA_FAILURE_CLASS_RELATIONSHIP;
        FacilioModule cmFcRelMod = new FacilioModule(name, displayName, tableName, entityType);

        List<FacilioField> fields = new ArrayList<>();
        LookupField record = FieldFactory.getDefaultField("parent",
                "Parent", "PARENT", FieldType.LOOKUP);
        record.setMainField(true);
        record.setLookupModule(customModule);
        fields.add(record);

        LookupField failureClass = FieldFactory.getDefaultField("failureClass",
                "Failure Class", "FAILURE_CLASS", FieldType.LOOKUP);
        failureClass.setLookupModule(failureClassModule);
        fields.add(failureClass);

        LookupField failureCode = FieldFactory.getDefaultField("failureCode",
                "Failure Code", "FAILURE_CODE", FieldType.LOOKUP);
        failureCode.setLookupModule(failureCodeModule);
        fields.add(failureCode);

        LookupField failureProblem = FieldFactory.getDefaultField("failureProblem",
                "Failure Problem", "FAILURE_PROBLEM", FieldType.LOOKUP);
        failureProblem.setLookupModule(failureProblemModule);
        fields.add(failureProblem);

        LookupField failureCause = FieldFactory.getDefaultField("failureCause",
                "Failure Cause", "FAILURE_CAUSE", FieldType.LOOKUP);
        failureCause.setLookupModule(failureCauseModule);
        fields.add(failureCause);

        LookupField failureRemedy = FieldFactory.getDefaultField("failureRemedy",
                "Failure Remedy", "FAILURE_REMEDY", FieldType.LOOKUP);
        failureRemedy.setLookupModule(failureRemedyModule);
        fields.add(failureRemedy);

        cmFcRelMod.setFields(fields);
        return cmFcRelMod;
    }
}
