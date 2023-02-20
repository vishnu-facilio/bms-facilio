package com.facilio.v3.V3Builder;

import org.apache.commons.chain.Command;

public interface CreateBuilder {
    CreateBuilder init(Command... initCommand);

    CreateBuilder beforeSave(Command... beforeSaveCommand);

    CreateBuilder afterSave(Command... afterSaveCommand);

    CreateBuilder afterTransaction(Command... afterTransactionCommand);

    CreateBuilder activitySaveCommand(Command... activitySaveCommand);

    UpdateBuilder update();

    DeleteBuilder delete();

    ListBuilder list();

    SummaryBuilder summary();
    PreCreateBuilder preCreate();
    PostCreateBuilder postCreate();
    PickListBuilder pickList();

    V3Config build();
}
