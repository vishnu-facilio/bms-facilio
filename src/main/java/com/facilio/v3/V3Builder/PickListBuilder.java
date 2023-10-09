package com.facilio.v3.V3Builder;

import org.apache.commons.chain.Command;
public interface PickListBuilder {

    PickListBuilder beforeFetch(Command criteriaCommand);
    PickListBuilder afterFetch(Command afterFetchCommand);
    PickListBuilder setSecondaryField(String moduleName, String fieldName);
    PickListBuilder setFourthField(String moduleName, String fieldName);
    PickListBuilder setMainField(String moduleName, String fieldName);
    PickListBuilder setSubmoduleType(String moduleName, String fieldName);
    PickListBuilder setColorField(String moduleName, String fieldName);
    PickListBuilder setAccentField(String moduleName, String fieldName);
    PickListBuilder setSeverityLevel(String severityLevel);
    PickListBuilder setSubModuleTypeField(String moduleName, String fieldName);
    UpdateBuilder update();
    CreateBuilder create();
    PreCreateBuilder preCreate();
    PostCreateBuilder postCreate();
    DeleteBuilder delete();
    SummaryBuilder summary();
    ListBuilder list();
    V3Config build();
}
