<template>
  <div class="control-group-summary">
    <div
      class="summaryHeader fc-pm-summary-header fc-new-pm-summary-header d-flex"
    >
      <div class="d-flex flex-row justify-between width100">
        <div class="primary-field summary-header-heading pT5">
          <div>
            <template v-if="!$validation.isEmpty(record)">
              <span class="fc-id pointer f14 mR5" @click="openGroupList">
                <i class="el-icon-back mR3"></i> #{{ record.id }}
              </span>
              <div
                class="heading-black18 f20 max-width500px textoverflow-ellipsis"
              >
                {{ record.name }}
              </div>
            </template>
          </div>
        </div>

        <div class="display-flex mB30">
          <el-button
            @click="showChangeSchedule = true"
            type="button"
            class="fc-wo-border-btn letter-spacing-normal"
          >
            Change Schedule
          </el-button>
        </div>
      </div>
    </div>
    <div
      class="clearboth fc-pm-summary-tab wo-summary-container groups-tabs-container"
    >
      <el-tabs
        v-if="!$validation.isEmpty(record)"
        v-model="activeTab"
        class="group-tabs"
      >
        <el-tab-pane label="PREVIEW" name="schedule">
          <ScheduleTab
            :moduleName="moduleName"
            :group="record"
            :isLoading="loading"
            @deleteException="deleteException"
            @editException="editException"
          />
        </el-tab-pane>
        <el-tab-pane label="GROUP TYPES" name="groupType" lazy>
          <GroupTypesTab
            :group="record"
            :groupSections="groupTypes"
            :isLoading="loading"
          />
        </el-tab-pane>
        <el-tab-pane label="SCHEDULE CHANGES" name="scheduleChanges" lazy>
          <ExceptionList
            v-if="activeTab === 'scheduleChanges'"
            :group="record"
            :isLoading="loading"
            :tenantId="tenantId"
            @onDelete="scheduleChange"
            @editRecord="editException"
            style="height:calc(100vh - 155px);"
            moduleName="controlScheduleExceptionTenant"
          />
        </el-tab-pane>
      </el-tabs>
    </div>
    <ChangeSchedule
      v-if="showChangeSchedule"
      :closeDialog="closeDialog"
      :group="record"
      @onSave="scheduleChange"
      :record="editDeleteSlot"
      :isTenantGroup="true"
      :tenantId="tenantId"
      moduleName="controlScheduleExceptionTenant"
    />
    <DeleteSchedule
      v-if="showDeleteDialog"
      :closeDialog="closeDialog"
      :recordId="exceptionId"
      :slotData="editDeleteSlot"
      moduleName="controlScheduleExceptionTenant"
    />
  </div>
</template>

<script>
import ControlGroupSummary from 'pages/controls/controlgroups/ControlGroupSummary'
import { findRouteForModule, pageTypes } from '@facilio/router'
export default {
  extends: ControlGroupSummary,
  methods: {
    openGroupList() {
      let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
      if (name) {
        this.$router.push({
          name,
        })
      }
    },
  },
}
</script>

<style scoped>
.wo-summary-container {
  margin: -40px 20px 0px;
}

.summaryHeader {
  min-height: 80px;
  padding: 15px 20px 10px 20px;
  background: #fff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}
.back-button-group {
  font-size: 12px;
  color: #3478f6;
  cursor: pointer;
}
</style>
<style lang="scss">
.groups-tabs-container {
  .group-tabs > .el-tabs__header {
    margin: 0px 0px 20px;
  }
  .group-type-container {
    height: calc(100vh - 150px) !important;
    overflow: scroll;
  }
  .schedule-visualizer-container {
    height: calc(100vh - 260px);
  }
}
</style>
