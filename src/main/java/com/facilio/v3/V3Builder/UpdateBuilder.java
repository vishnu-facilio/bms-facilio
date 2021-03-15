package com.facilio.v3.V3Builder;

import org.apache.commons.chain.Command;

public interface UpdateBuilder {
    UpdateBuilder init(Command... initCommand);

    UpdateBuilder beforeSave(Command... beforeSaveCommand);

    UpdateBuilder afterSave(Command... afterSaveCommand);

    UpdateBuilder afterTransaction(Command... afterTransactionCommand);

    CreateBuilder create();

    DeleteBuilder delete();

    ListBuilder list();

    SummaryBuilder summary();

    V3Config build();
}
