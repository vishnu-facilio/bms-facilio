package com.facilio.v3.V3Builder;

import org.apache.commons.chain.Command;

public interface PreCreateBuilder {

    PreCreateBuilder init(Command... initCommand);
    PreCreateBuilder beforeSave(Command... beforeSaveCommand);
    PreCreateBuilder afterSave(Command... afterSaveCommand);
    UpdateBuilder update();
    DeleteBuilder delete();
    ListBuilder list();
    SummaryBuilder summary();
    PickListBuilder pickList();
    V3Config build();
}
