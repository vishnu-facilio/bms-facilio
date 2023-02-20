package com.facilio.v3.V3Builder;

import org.apache.commons.chain.Command;

public interface DeleteBuilder {
    DeleteBuilder init(Command... initCommand);

    DeleteBuilder beforeDelete(Command... beforeDelete);

    DeleteBuilder afterDelete(Command... afterDeleteCommand);

    DeleteBuilder afterTransaction(Command... afterTransactionCommand);

    UpdateBuilder update();

    CreateBuilder create();
    PreCreateBuilder preCreate();
    PostCreateBuilder postCreate();

    ListBuilder list();

    SummaryBuilder summary();
    PickListBuilder pickList();

    V3Config build();

}
