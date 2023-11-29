package com.facilio.bmsconsoleV3.signup.breakMod;


import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.List;

public class AddBreakModule extends SignUpData {
    private final int CASCADE_DELETE = 2;

    @Override
    public void addData() throws Exception {

        createMod(composeBreakMod());
        createBreakChildModules();

        FacilioModule breakRichTextMod = Constants.getModBean().getModule(FacilioConstants.Break.BREAK_RICH_TEXT);
        FacilioModule breakMod = Constants.getModBean().getModule(FacilioConstants.Break.BREAK);
        persistRichTextDescriptionFieldForBreak(breakRichTextMod, breakMod);

        addShiftsFieldToBreakModule();
    }

    public void addShiftsFieldToBreakModule() throws Exception {
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();
        FacilioModule breakMod = Constants.getModBean().getModule(FacilioConstants.Break.BREAK);
        FacilioModule shiftBreakRelMod = Constants.getModBean().getModule(FacilioConstants.ContextNames.SHIFT_BREAK_REL);

        MultiLookupField shiftsField = FieldFactory.getDefaultField("shifts", "Shifts", null, FieldType.MULTI_LOOKUP);
        shiftsField.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        shiftsField.setRequired(true);
        shiftsField.setDisabled(false);
        shiftsField.setDefault(true);
        shiftsField.setMainField(false);
        shiftsField.setOrgId(orgId);
        shiftsField.setModule(breakMod);
        shiftsField.setLookupModuleId(shiftBreakRelMod.getModuleId());
        shiftsField.setRelModuleId(shiftBreakRelMod.getModuleId());
        shiftsField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.RIGHT);

        Constants.getModBean().addField(shiftsField);
    }

    public void createMod(FacilioModule mod) throws Exception {
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(mod));
        addModuleChain.execute();
    }

    public void createBreakChildModules() throws Exception {

        FacilioModule breakMod = Constants.getModBean().getModule(FacilioConstants.Break.BREAK);
        List<FacilioModule> childModules = new ArrayList<>();
        childModules.add(composeBreakRichTextModule());
        childModules.add(composeShiftBreakRelMod());
        FacilioChain chain = TransactionChainFactory.addSystemModuleChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, childModules);
        chain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, breakMod.getName());
        chain.getContext().put(FacilioConstants.ContextNames.DELETE_TYPE, CASCADE_DELETE);
        chain.execute();
        
    }

    public void persistRichTextDescriptionFieldForBreak(FacilioModule breakRichTextMod, FacilioModule breakMod) throws Exception {
        LargeTextField richTextField = FieldFactory.getDefaultField("description", "Description", null, FieldType.LARGE_TEXT);
        richTextField.setModule(breakMod);
        richTextField.setRelModuleId(breakRichTextMod.getModuleId());
        Constants.getModBean().addField(richTextField);
    }

    public FacilioModule composeBreakRichTextModule() throws Exception {
        FacilioModule breakMod = Constants.getModBean().getModule(FacilioConstants.Break.BREAK);
        FacilioModule mod = new FacilioModule(FacilioConstants.Break.BREAK_RICH_TEXT,
                "Break Rich Text",
                "Break_Rich_Text",
                FacilioModule.ModuleType.SUB_ENTITY
        );
        List<FacilioField> fields = new ArrayList<>();
        LookupField parentField = FieldFactory.getDefaultField("parentId", "Parent ID", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(breakMod);
        fields.add(parentField);
        fields.add(FieldFactory.getDefaultField("fileId", "File ID", "FILE_ID", FieldType.NUMBER));
        mod.setFields(fields);
        return mod;
    }

    public FacilioModule composeBreakMod() {

        FacilioModule mod = new FacilioModule(FacilioConstants.Break.BREAK,
                "Break",
                "Break",
                FacilioModule.ModuleType.BASE_ENTITY
        );

        List<FacilioField> fields = new ArrayList<>();

        fields.add(new StringField(mod,"name","Name",FacilioField.FieldDisplayType.TEXTBOX,"NAME", FieldType.STRING,null,null,true,true));

        FacilioField breakTime = FieldFactory.getDefaultField("breakTime","Break Time","BREAK_TIME",FieldType.NUMBER);
        breakTime.setDefault(true);
        breakTime.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        fields.add(breakTime);

        StringSystemEnumField breakType = FieldFactory.getDefaultField("breakType", "Break Type", "BREAK_TYPE", FieldType.STRING_SYSTEM_ENUM);
        breakType.setEnumName("BreakType");
        fields.add(breakType);

        StringSystemEnumField breakMode = FieldFactory.getDefaultField("breakMode", "Break Mode", "BREAK_MODE", FieldType.STRING_SYSTEM_ENUM);
        breakMode.setEnumName("BreakMode");
        fields.add(breakMode);

        mod.setFields(fields);

        return mod;
    }

    public FacilioModule composeShiftBreakRelMod() throws Exception {

        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();

        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.SHIFT_BREAK_REL, "Shift Break Rel",
                "Shift_Break_Rel", FacilioModule.ModuleType.SUB_ENTITY, false);
        module.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();
        FacilioModule shiftMod = Constants.getModBean().getModule(FacilioConstants.ContextNames.SHIFT);
        LookupField shiftIDField = SignupUtil.getLookupField(module, shiftMod, "left", "Shift ID", "SHIFT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(shiftIDField);

        FacilioModule breakMod = Constants.getModBean().getModule(FacilioConstants.ContextNames.SHIFT);
        LookupField rightField = SignupUtil.getLookupField(module, breakMod,
                "right", "Break ID", "BREAK_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(rightField);
        module.setFields(fields);

        return module;
    }
}
