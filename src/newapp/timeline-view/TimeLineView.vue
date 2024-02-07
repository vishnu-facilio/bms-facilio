<template>
  <CommonLayout
    :moduleName="moduleName"
    :getPageTitle="() => `Scheduler View`"
    :pathPrefix="`/app/scheduler/${moduleName}`"
  >
    <template v-if="!loading && isEmpty(viewname)" #header-container>
      <div></div>
    </template>
    <template v-if="!loading && isEmpty(viewname)" #views-list>
      <div></div
    ></template>
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

    <template #header>
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
      class="timeline-view-empty-state-container"
    >
      <inline-svg
        src="svgs/no-configuration"
        class="d-flex module-view-empty-state"
        iconClass="icon"
      ></inline-svg>
      <div class="mB20 label-txt-black f14 self-center">
        {{ $t('viewsmanager.list.no_view_config') }}
      </div>
      <el-button type="primary" class="add-view-btn" @click="openViewCreation">
        <span class="btn-label">{{ $t('viewsmanager.list.add_view') }}</span>
      </el-button>
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
    >
      <template #spinner>
        <spinner :show="true" size="80"></spinner>
      </template>
      <template #ftags>
        <FTags :key="`ftags-list-${moduleName}`"></FTags>
      </template>
    </SchedulerComponent>
    <portal to="view-manager-link">
      <router-link
        tag="div"
        :to="{ name: 'resource-scheduler-viewmanager' }"
        class="view-manager-btn"
      >
        <inline-svg
          src="svgs/hamburger-menu"
          class="d-flex"
          iconClass="icon icon-sm"
        ></inline-svg>
        <span class="label mL10 text-uppercase">
          {{ $t('viewsmanager.list.views_manager') }}
        </span>
      </router-link>
    </portal>
  </CommonLayout>
</template>
<script>
import { SchedulerComponent } from '@facilio/scheduler'
import DatePicker from '@/NewDatePicker'
import NewDateHelper from 'src/components/mixins/NewDateHelper'
import CommonLayout from './SchedulerLayout.vue'
import AdvancedSearch from '../components/search/AdvancedSearch.vue'
import FTags from './components/FTags.vue'
import { mapGetters, mapState } from 'vuex'
import isEqual from 'lodash/isEqual'
import { isEmpty } from '@facilio/utils/validation'
import {
  findRouteForModule,
  pageTypes,
  isWebTabsEnabled,
  findRouteForTab,
} from '@facilio/router'

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
  props: ['moduleName', 'viewname', 'tabId'],
  components: {
    SchedulerComponent,
    DatePicker,
    CommonLayout,
    AdvancedSearch,
    FTags,
  },
  data() {
    return {
      showPicker: false,
      timeStamp: null,
      datePickerObj: null,
      datePickerTabs,
      loading: true,
      isEmpty,
    }
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
      viewDetail: state => state.view.currentViewDetail,
      isViewLoading: state => state.view.isLoading,
      isViewDetailLoading: state => state.view.detailLoading,
      groupViews: state => {
        let { view } = state || {}
        let { groupViews } = view || {}
        return !isEmpty(groupViews) ? groupViews : []
      },
    }),
    ...mapGetters([
      'isStatusLocked',
      'getApprovalStatus',
      'getTicketStatus',
      'getSite',
    ]),
    moduleDisplayName() {
      return this.metaInfo?.displayName || this.moduleName
    },
    hasViewList() {
      return (this.groupViews || []).some(grp => !isEmpty(grp.views))
    },
    viewLoading() {
      return this.isViewLoading || this.isViewDetailLoading
    },
    uniqueKey() {
      let { moduleName, viewname, tabId } = this
      return `${moduleName}_${viewname}_${tabId}`
    },
  },
  watch: {
    tabId: {
      handler(newVal, oldVal) {
        if (!isEmpty(newVal) && !isEqual(newVal, oldVal)) {
          this.loading = true
          this.init()
        }
      },
      immediate: true,
    },
    timeStamp(newVal) {
      this.setQuery(newVal)
    },
    viewDetail(newVal, oldVal) {
      if (!isEmpty(newVal) && !isEqual(newVal, oldVal)) {
        let { moduleName } = newVal || {}
        if (moduleName === this.moduleName) this.loading = false
      }
    },
    viewLoading(newVal) {
      if (!newVal) {
        if (!this.hasViewList && isEmpty(this.viewname)) this.loading = false
      } else this.loading = true
    },
    uniqueKey: {
      handler() {
        let { name, moduleName } = this.viewDetail || {}

        if (name) {
          if (this.viewname) this.loading = name !== this.viewname
          else if (moduleName === this.moduleName) this.loading = true
        }
      },
      immediate: true,
    },
  },
  methods: {
    init() {
      this.$store.dispatch('loadTicketStatus', this.moduleName || '')
      this.$store.dispatch('loadApprovalStatus')
      this.$store.dispatch('loadSite')
    },
    setPickerObj({ view, startTime }) {
      this.showPicker = false

      this.datePickerObj = NewDateHelper.getDatePickerObject(
        viewVsOperatorId[view],
        `${startTime}`
      )
      this.setQuery(this.datePickerObj)
      this.$nextTick(() => {
        this.showPicker = true
      })
    },
    setQuery(dateObj) {
      let { operationOn, value } = dateObj || {}
      let [startTime, endTime] = value || []
      let currentView = operationOn.toUpperCase()
      let { query } = this.$route || {}

      this.$router
        .push({
          query: { ...query, currentView, startTime, endTime },
        })
        .catch(() => {})
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
    openViewCreation() {
      let { moduleName, $route } = this
      let { query } = $route

      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.TIMELINE_VIEW_MANAGER, {
          moduleName,
        })

        name && this.$router.push({ name, query })
      } else {
        this.$router.push({ name: 'resource-scheduler-viewmanager', query })
      }
    },
  },
}
</script>
<style lang="scss">
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
.timeline-view-empty-state-container {
  display: flex;
  flex-grow: 1;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fff;
  margin: 10px;

  .module-view-empty-state svg.icon {
    width: 150px;
    height: 120px;
  }
  .add-view-btn {
    background-color: #39b2c2;
    line-height: normal;
    padding: 11px 17px;
    border: solid 1px rgba(0, 0, 0, 0);
    margin-bottom: 30px;
  }
}
</style>
