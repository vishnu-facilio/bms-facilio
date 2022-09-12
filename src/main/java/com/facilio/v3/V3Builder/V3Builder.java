package com.facilio.v3.V3Builder;

public interface V3Builder {
    CreateBuilder create();

    PreCreateBuilder preCreate();
    PostCreateBuilder postCreate();
    UpdateBuilder update();
    DeleteBuilder delete();
    SummaryBuilder summary();
    ListBuilder list();
    V3Config build();
}
