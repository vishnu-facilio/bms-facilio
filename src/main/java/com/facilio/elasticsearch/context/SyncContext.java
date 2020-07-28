package com.facilio.elasticsearch.context;

import com.facilio.modules.FacilioModule;

public class SyncContext {

    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    private Long syncModuleId;
    public Long getSyncModuleId() {
        return syncModuleId;
    }
    public void setSyncModuleId(Long syncModuleId) {
        this.syncModuleId = syncModuleId;
    }

    private FacilioModule syncModule;
    public FacilioModule getSyncModule() {
        return syncModule;
    }
    public void setSyncModule(FacilioModule syncModule) {
        this.syncModule = syncModule;
    }

    private Boolean syncing;
    public Boolean getSyncing() {
        return syncing;
    }
    public void setSyncing(Boolean syncing) {
        this.syncing = syncing;
    }

    private Long lastSyncRecordId;
    public Long getLastSyncRecordId() {
        return lastSyncRecordId;
    }
    public void setLastSyncRecordId(Long lastSyncRecordId) {
        this.lastSyncRecordId = lastSyncRecordId;
    }
}
