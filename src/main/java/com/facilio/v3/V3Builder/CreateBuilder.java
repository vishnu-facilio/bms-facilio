package com.facilio.v3.V3Builder;

import org.apache.commons.chain.Command;

public interface CreateBuilder {
    CreateBuilder init(Command... initCommand);

    CreateBuilder beforeSave(Command... beforeSaveCommand);

    CreateBuilder afterSave(Command... afterSaveCommand);

    CreateBuilder afterTransaction(Command... afterTransactionCommand);

    UpdateBuilder update();

    DeleteBuilder delete();

    ListBuilder list();

    SummaryBuilder summary();

    V3Config build();
}
