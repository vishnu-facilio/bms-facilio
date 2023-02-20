package com.facilio.v3.V3Builder;

import org.apache.commons.chain.Command;

public interface UpdateBuilder {
    UpdateBuilder init(Command... initCommand);

    UpdateBuilder beforeSave(Command... beforeSaveCommand);

    UpdateBuilder afterSave(Command... afterSaveCommand);

    UpdateBuilder afterTransaction(Command... afterTransactionCommand);

    UpdateBuilder activitySaveCommand(Command... activitySaveCommand);

    CreateBuilder create();
    PreCreateBuilder preCreate();
    PostCreateBuilder postCreate();

    DeleteBuilder delete();

    ListBuilder list();

    SummaryBuilder summary();
    PickListBuilder pickList();

    V3Config build();
}
