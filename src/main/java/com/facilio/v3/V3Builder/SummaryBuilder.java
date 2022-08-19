package com.facilio.v3.V3Builder;

import org.apache.commons.chain.Command;

public interface SummaryBuilder {
    SummaryBuilder afterFetch(Command afterFetchCommand);
    SummaryBuilder beforeFetch(Command beforeFetchCommand);
    SummaryBuilder fetchSupplement(String moduleName, String fieldName);

    UpdateBuilder update();

    CreateBuilder create();

    DeleteBuilder delete();

    ListBuilder list();

    V3Config build();
}
