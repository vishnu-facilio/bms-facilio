<template>
  <div class="pm-tl-view">
    <PMTimeLineLayout>
      <template #planner-selector>
        <el-select
          filterable
          default-first-option
          v-model="activePlanner"
          class="fc-input-full-border-select2 width200 mT10 mR15 pm-planner-selector"
          size="mini"
          @change="fetchPlannerContext()"
        >
          <el-option
            v-for="(planner, index) in plannerList"
            :key="index"
            :label="planner.name"
            :value="planner.id"
          />
        </el-select>
      </template>
      <template #pm-assignment>
        <div
          v-if="!$validation.isEmpty(viewOptions)"
          class="p10 pm-timeline-radio-group"
        >
          <el-radio-group
            v-model="groupBy"
            size="small"
            @change="loadCurrentView()"
          >
            <el-radio-button
              v-for="(view, index) in viewOptions"
              :key="`view - ${index}`"
              :label="view.value"
              border
              >{{ view.label }}</el-radio-button
            >
          </el-radio-group>
        </div>
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
      <template #search-customization>
        <portal-target name="event-save"></portal-target>
        <AdvancedSearch
          :key="`${moduleName}-search`"
          :moduleName="moduleName"
          :moduleDisplayName="moduleDisplayName"
          :hideQuery="true"
          :filterList="appliedFilter"
          @applyFilters="setAppliedfilter"
        ></AdvancedSearch>
      </template>
      <template #schedular>
        <div v-if="loading" class="height-100 d-flex">
          <spinner :show="true" size="80"></spinner>
        </div>

        <SchedulerComponent
          v-else
          :moduleName="moduleName"
          :viewname="groupBy"
          :viewDetails="currentViewDetail"
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
          :filterObj="appliedFilter"
          @viewChanged="setPickerObj"
          @openSummary="openSummary"
        >
          <template #spinner>
            <spinner :show="true" size="80"></spinner>
          </template>
          <template #ftags>
            <FTags
              :key="`ftags-list-${moduleName}`"
              :moduleName="moduleName"
              :filters="appliedFilter"
              :hideQuery="true"
              :hideSaveAs="true"
              @updateFilters="setAppliedfilter"
              @clearFilters="setAppliedfilter({})"
            ></FTags>
          </template>
        </SchedulerComponent>
      </template>
    </PMTimeLineLayout>
  </div>
</template>
<script>
import PMTimeLineLayout from './PmTimeLineLayout.vue'
import AdvancedSearch from 'newapp/components/search/AdvancedSearchUI'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { SchedulerComponent } from '@facilio/scheduler'
import DatePicker from '@/NewDatePicker'
import NewDateHelper from 'src/components/mixins/NewDateHelper'
import { FTags } from '@facilio/criteria'
import { mapGetters, mapState } from 'vuex'
import isEqual from 'lodash/isEqual'
import {
  findRouteForModule,
  pageTypes,
  isWebTabsEnabled,
} from '@facilio/router'
import {
  getRelatedFieldName,
  getUnRelatedModuleSummary,
} from 'src/util/relatedFieldUtil'

// Needed for Schedular Component
const datePickerTabs = {
  enableByOperationOnId: true,
  disableDefaultLabels: true,
  enabledTabs: ['D', 'W', 'M', 'Y'],
  loadAdditional: {
    year: { period: 'start', label: 'year', operation: 'add', value: 4 },
  },
}
const viewVsOperatorId = {
  DAY: 62,
  WEEK: 63,
  MONTH: 64,
  YEAR: 65,
}

export default {
  name: 'PmTimeLineView',
  components: {
    PMTimeLineLayout,
    AdvancedSearch,
    SchedulerComponent,
    FTags,
    DatePicker,
  },
  data() {
    return {
      plannerList: [],
      activePlanner: null,
      activePlannerContext: null,
      groupBy: null,
      viewOptions: [],
      loading: false,
      showPicker: false,
      timeStamp: null,
      datePickerObj: null,
      datePickerTabs,
      currentViewDetail: {},
      appliedFilter: {},
    }
  },
  created() {
    this.initTimeLine()
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
    ...mapGetters([
      'isStatusLocked',
      'getApprovalStatus',
      'getTicketStatus',
      'getSite',
    ]),
    moduleName() {
      return 'workorder'
    },
    moduleDisplayName() {
      return 'WorkOrder'
    },
    pmId() {
      let { $attrs } = this
      let { details } = $attrs || {}
      let { id: pmId = null } = details || {}

      return pmId
    },
  },
  watch: {
    groupBy() {
      this.loading = true
    },
    activePlannerContext(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.loadCurrentView()
      }
    },
    currentViewDetail(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.loading = false
      }
    },
  },
  methods: {
    async initTimeLine() {
      let { moduleName } = this
      await this.loadPlanners()
      await this.loadCurrentView()
      this.$store.dispatch('view/loadModuleMeta', moduleName)
      this.$store.dispatch('loadTicketStatus', moduleName)
      this.$store.dispatch('loadApprovalStatus')
      this.$store.dispatch('loadSite')
    },
    async loadPlanners(force = false) {
      this.loading = true
      let { pmId: id } = this
      let relatedFieldName = getRelatedFieldName(
        'plannedmaintenance',
        'pmPlanner'
      )
      let relatedConfig = {
        moduleName: 'plannedmaintenance',
        id,
        relatedModuleName: 'pmPlanner',
        relatedFieldName,
      }
      let { error, list } = await API.fetchAllRelatedList(
        relatedConfig,
        {},
        {
          force,
        }
      )

      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$set(this, 'plannerList', list)
        if (!isEmpty(list)) {
          let initialPlanner = list[0] || {}
          let { id: initialPlannerId = null } = initialPlanner || {}

          this.$set(this, 'activePlanner', initialPlannerId)
          await this.fetchPlannerContext()
        }
      }
      this.loading = false
    },
    async fetchPlannerContext() {
      this.viewOptions = []
      let { activePlanner } = this
      let { pmPlanner } = await getUnRelatedModuleSummary(
        'plannedmaintenance',
        'pmPlanner',
        activePlanner
      )
      let { resourceTimelineView, staffTimelineView } = pmPlanner || {}
      let { displayName: assetViewDisplayName, name: assetViewName } =
        resourceTimelineView || {}
      let { displayName: staffViewDisplayName, name: staffViewName } =
        staffTimelineView || {}

      if (!isEmpty(assetViewName) && !isEmpty(staffViewName)) {
        this.viewOptions.push(
          { label: assetViewDisplayName, value: assetViewName },
          { label: staffViewDisplayName, value: staffViewName }
        )
        this.groupBy = assetViewName
      }
      this.$set(this, 'activePlannerContext', pmPlanner)
    },
    loadCurrentView() {
      this.loading = true
      let { groupBy, activePlannerContext } = this
      let { resourceTimelineView = {}, staffTimelineView = {} } =
        activePlannerContext || {}
      if (!isEmpty(groupBy)) {
        if (groupBy.includes('resource')) {
          this.currentViewDetail = resourceTimelineView
        } else {
          this.currentViewDetail = staffTimelineView
        }
      } else {
        this.currentViewDetail = null
      }
      this.loading = false
    },
    setPickerObj({ view, startTime }) {
      this.showPicker = false
      this.datePickerObj = NewDateHelper.getDatePickerObject(
        viewVsOperatorId[view],
        `${startTime}`
      )
      this.$nextTick(() => {
        this.showPicker = true
      })
    },
    getSiteName(field, record) {
      let site = this.getSite(record[field.name])
      let value = site && site.name ? site.name : '---'
      return value
    },
    getRouteUrl(id) {
      let { moduleName } = this
      let routerHref = null

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          let { href } = this.$router.resolve({
            name,
            params: { viewname: 'all', id },
          })
          routerHref = href
        }
      } else {
        let { href } = this.$router.resolve({
          name: 'wosummarynew',
          params: { id },
        })
        routerHref = href
      }
      return routerHref
    },
    openSummary({ id }) {
      window.open(this.getRouteUrl(id), '_blank')
    },
    setAppliedfilter(filterObj) {
      this.appliedFilter = filterObj
    },
  },
}
</script>

// Not scoped for overriding element-css have wrapped with unique wrapper-class
<style lang="scss">
.pm-tl-view {
  background-color: #f7f8f9;
  .pm-timeline-radio-group {
    display: flex;
    justify-content: center;
    .el-radio-group {
      width: 100%;
      display: inline-flex;
      label,
      span {
        width: 100%;
      }
    }
  }
  .pm-planner-selector {
    .el-input__inner {
      height: 32px !important;
    }
  }
  .scheduler.facilio-resource-date-picker {
    margin-left: 20px;

    .button-row {
      height: 30px;
      padding: 0px;
      display: flex;
      align-items: center;
      color: #324056;
      border-radius: 3px;
      border: solid 1px #e3eaed;

      .el-button {
        font-size: 14px;
        letter-spacing: 0.5px;
        color: #324056 !important;
        padding: 7px 15px !important;
        border-radius: 0px;
        font-weight: normal;

        &:hover {
          background-color: transparent !important;
        }
      }

      .cal-left-btn,
      .cal-right-btn {
        height: 28px;
        width: 28px;

        .date-arrow {
          padding: 0px;
          font-size: 16px;
          font-weight: bold;
        }
      }
      &:hover {
        border: 1px solid #4d95ff;
      }
    }
  }
}
</style>
