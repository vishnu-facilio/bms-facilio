package com.facilio.bmsconsoleV3.signup.workOrder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.ArrayList;
import java.util.List;

public class AddWorkOrderModules extends SignUpData {

    FacilioModule ticketModule;
    FacilioModule failureClassModule;
    FacilioModule failureCodeModule;
    FacilioModule failureProblemModule;
    FacilioModule failureCauseModule;
    FacilioModule failureRemedyModule;

    @Override
    public void addData() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);
        failureClassModule = modBean.getModule(FacilioConstants.ContextNames.FAILURE_CLASS);
        failureCodeModule = modBean.getModule(FacilioConstants.ContextNames.FAILURE_CODE);
        failureProblemModule = modBean.getModule(FacilioConstants.ContextNames.FAILURE_CODE_PROBLEMS);
        failureCauseModule = modBean.getModule(FacilioConstants.ContextNames.FAILURE_CODE_CAUSES);
        failureRemedyModule = modBean.getModule(FacilioConstants.ContextNames.FAILURE_CODE_REMEDIES);

        FacilioModule workOrderFailureClassRel = getWorkOrderFailureClassRel();

        List<FacilioModule> modules = new ArrayList<>();
        modules.add(workOrderFailureClassRel);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
    }

    private FacilioModule getWorkOrderFailureClassRel() {
        final String name = FacilioConstants.ContextNames.WORKORDER_FAILURE_CLASS_RELATIONSHIP;
        final String displayName = "WorkOrder FailureClass Relationship";
        final String tableName = "WorkOrder_FailureClass_Relationship";
        final FacilioModule.ModuleType entityType = FacilioModule.ModuleType.BASE_ENTITY;
        FacilioModule woFcRelMod = new FacilioModule(name, displayName, tableName, entityType);

        List<FacilioField> fields = new ArrayList<>();
        LookupField ticket = FieldFactory.getDefaultField("parent",
                "Parent", "PARENT", FieldType.LOOKUP);
        ticket.setMainField(true);
        ticket.setLookupModule(ticketModule);
        fields.add(ticket);

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

        woFcRelMod.setFields(fields);
        return woFcRelMod;
    }
}
