package com.facilio.v3.V3Builder;

import org.apache.commons.chain.Command;

public interface PostCreateBuilder {

    PostCreateBuilder afterSave(Command... afterSaveCommand);
    PostCreateBuilder afterTransaction(Command... afterTransactionCommand);
    PostCreateBuilder activitySaveCommand(Command... activitySaveCommand);
    UpdateBuilder update();
    DeleteBuilder delete();
    ListBuilder list();
    SummaryBuilder summary();
    PickListBuilder pickList();
    V3Config build();
}
