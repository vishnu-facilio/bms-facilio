package com.facilio.v3.V3Builder;

import org.apache.commons.chain.Command;

public interface DeleteBuilder {
    DeleteBuilder init(Command initCommand);

    DeleteBuilder beforeDelete(Command beforeSaveCommand);

    DeleteBuilder afterDelete(Command afterSaveCommand);

    DeleteBuilder afterTransaction(Command afterTransactionCommand);

    UpdateBuilder update();

    CreateBuilder create();

    ListBuilder list();

    SummaryBuilder summary();

    V3Builder build();

}
