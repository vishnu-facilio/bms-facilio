<template>
  <CommonLayout :moduleName="moduleName" :getPageTitle="getPageTitle">
    <template v-if="!loading && isEmpty(viewname)" #header-container>
      <div style="height: 32px;"></div>
    </template>
    <template #calendar-actions>
      <portal-target name="scheduler-today"></portal-target>
      <DatePicker
        v-if="showPicker"
        :dateObj="datePickerObj"
        @date="dateObj => (timeStamp = dateObj)"
        :zone="$timezone"
        :tabs="datePickerTabs"
        class="facilio-resource-date-picker scheduler"
      ></DatePicker>
      <portal-target name="calendar-view"></portal-target>
    </template>

    <template v-if="!isEmpty(viewname)" #header>
      <portal-target name="event-save"></portal-target>
      <AdvancedSearch
        :key="`${moduleName}-search`"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearch>
    </template>

    <div v-if="loading" class="height-100 d-flex">
      <spinner :show="true" size="80"></spinner>
    </div>
    <div
      v-else-if="isEmpty(viewname)"
      class="timeline-view-empty-state-container height-100"
    >
      <inline-svg
        src="svgs/no-configuration"
        class="d-flex module-view-empty-state"
        iconClass="icon"
      ></inline-svg>
      <div class="mB20 label-txt-black f14 self-center">
        {{ $t('viewsmanager.list.no_view_config') }}
      </div>
    </div>
    <SchedulerComponent
      v-else
      :key="uniqueKey"
      :moduleName="moduleName"
      :viewname="viewname"
      :viewDetails="viewDetail"
      :timezone="$timezone"
      :dateformat="$dateformat"
      :timeformat="$timeformat"
      :timeStamp="timeStamp"
      :metaInfo="metaInfo"
      :isStatusLocked="isStatusLocked"
      :getApprovalStatus="getApprovalStatus"
      :getTicketStatus="getTicketStatus"
      :getSiteName="getSiteName"
      :createPermission="$hasPermission(`${moduleName}:CREATE`)"
      :updatePermission="$hasPermission(`${moduleName}:UPDATE`)"
      :dialogPrompt="$dialog"
      @viewChanged="setPickerObj"
      @openSummary="openSummary"
      class="portal-timeline-view"
    >
      <template #spinner>
        <spinner :show="true" size="80"></spinner>
      </template>
      <template #ftags>
        <FTags :key="`ftags-list-${moduleName}`"></FTags>
      </template>
    </SchedulerComponent>
  </CommonLayout>
</template>
<script>
import TimeLineView from 'newapp/timeline-view/TimeLineView.vue'
import CommonLayout from './TimeLineLayout.vue'

export default {
  extends: TimeLineView,
  components: { CommonLayout },
  methods: {
    getPageTitle() {
      return `${this.moduleDisplayName} Scheduler View`
    },
  },
}
</script>
<style lang="scss">
.portal-timeline-view {
  .sidebar-list-container,
  .unscheduled-sidebar-list {
    border-top: 1px solid #f4f3f3;
  }
  .sidebar-open-popup {
    display: flex;
    flex-direction: column;

    .sidebar-header {
      flex-shrink: 0;
    }
    .header-actions {
      flex-shrink: 0;
    }
    .sidebar-event-list {
      flex-grow: 1;
      height: auto;
    }
  }
}
</style>
