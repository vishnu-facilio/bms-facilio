package com.facilio.v3.V3Builder;

import org.apache.commons.chain.Command;

public interface ListBuilder {
    ListBuilder beforeFetch(Command criteriaCommand);

    ListBuilder afterFetch(Command afterFetchCommand);

    ListBuilder showStateFlowList();

    UpdateBuilder update();

    CreateBuilder create();

    DeleteBuilder delete();

    SummaryBuilder summary();

    V3Config build();
}
