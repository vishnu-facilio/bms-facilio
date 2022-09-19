package com.facilio.bmsconsole.formFactory;

import com.facilio.bmsconsole.formCommands.*;
import com.facilio.chain.FacilioChain;



public class FacilioFormChainFactory {

    public static FacilioChain getFormMetaChain() {
        FacilioChain c = FacilioChain.getNonTransactionChain();
        c.addCommand(new GetFormMetaCommand());
        c.addCommand(new HandleFormFieldsCommand());
        c.addCommand(new GetFormRuleFields());
        return c;
    }

    public  static  FacilioChain getFormListFromDB(){
        FacilioChain c = FacilioChain.getNonTransactionChain();
        c.addCommand(new GetFormListCommand());
        return c;
    }

    public static FacilioChain getAddFormCommand() {
        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new AddFormCommand());
        c.addCommand(new AddSystemFieldsCommand());
        c.addCommand(new AddFormForCustomModuleCommand());
        c.addCommand(new AddFormSiteRelationCommand());

        return c;
    }

    public static FacilioChain getAddModuleChain() {
        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new CreateCustomModuleCommand());
        c.addCommand(new AddSystemFieldsCommand());
        c.addCommand(commonAddModuleChain());
        c.addCommand(new AddDefaultFormForCustomModuleCommand());
        c.addCommand(new AddDefaultStateFlowCommand());
        c.addCommand(new AddSubModulesSystemFieldsCommad());
        c.addCommand(new CreateDefaultViewCommad());
        c.addCommand(commonAddModuleChain());
//			c.addCommand(new CreateCustomModuleDefaultSubModuleCommand());
        return c;
    }

    public static FacilioChain commonAddModuleChain() {
        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new AddModulesCommand());
//			c.addCommand(new SetColumnNameForNewCFsCommand());
        c.addCommand(new AddFieldsCommand());

        return c;
    }
    public static FacilioChain getAddSubformChain() {
        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new ValidateSubFormRequestCommand());
        c.addCommand(new GenerateSubformLinkNameCommand());
        c.addCommand(getAddFormCommand());
        return c;
    }
    public static FacilioChain getFormSectionChain() {
        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new AddFormSectionCommand());
        c.addCommand(new AddFormFieldsFromSection());
        return c;
    }
}
