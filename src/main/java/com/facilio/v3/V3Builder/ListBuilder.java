package com.facilio.v3.V3Builder;

import org.apache.commons.chain.Command;

public interface ListBuilder {
    ListBuilder beforeFetch(Command criteriaCommand);

    ListBuilder afterFetch(Command afterFetchCommand);

    ListBuilder fetchSupplement(String moduleName, String fieldName);

    ListBuilder showStateFlowList();

    ListBuilder fetchRelations(String moduleName, String lookupField);

    ListBuilder beforeCount(Command beforeCountCommand);

    UpdateBuilder update();

    CreateBuilder create();

    DeleteBuilder delete();

    SummaryBuilder summary();

    V3Config build();
}
