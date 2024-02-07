<template>
  <div class="new-planner pm-resource-planner pR15 pL15">
    <facilio-resource-planner
      v-bind:plannerType="'ASSET_PLANNER'"
      ref="resourcePlanner"
      @viewChanged="handleViewChanged"
      v-bind:isServerRows="true"
      :customizationSettings="customizationSettings"
      :paginate="paginate"
      :pageSize="perPage"
      defaultView="YEAR"
      :resourceGrouping="grouping"
      :saving="requestPending"
      v-bind:isPMAssetPlanner="true"
      :isDragDropAllowed="enableDragAndDrop"
      v-bind:allowDownload="true"
      @dropped="handleDrop"
      noDataText="No workorders"
    >
      <template v-slot:taskContent="eventProps">
        <div class="rp-task-text ellipsis">
          {{ eventProps.event[eventProps.displayType] }}
        </div>
      </template>
      <template slot="plannerLeftTop">
        <planner-filter
          :filterOptions="assetFilters"
          @filterChanged="handleFilterChange"
        >
        </planner-filter>
      </template>
      <!-- 2 COL LAYOUT odd in col 1 and even in col 2 -->
      <template slot="plannerRightTop">
        <planner-legend
          :legendMap="legendMap"
          :legendName="legendName"
        ></planner-legend>
      </template>
      <!-- slot for card is releaved only after event has been clicked -->
      <!-- to do , just pass the dom in an event , take out this slot and render card fully outside -->
      <template v-slot:card="slotProps">
        <pm-wo-card
          :plannerEvent="slotProps"
          :allowStaffReAssign="true"
          @close="$refs['resourcePlanner'].handleCardClose"
          @deleteWo="$refs['resourcePlanner'].deleteEvent($event)"
        ></pm-wo-card>
      </template>
    </facilio-resource-planner>
  </div>
</template>

<script>
import FacilioResourcePlanner from './FacilioResourcePlanner'
import LegendMixin from 'pages/workorder/LegendHelper'
import PlannerLegend from 'pages/workorder/PlannerLegend'
import PlannerApiCalls from 'pages/workorder/PlannerAPICalls'
import plannerFilter from 'pages/workorder/PMPlannerFilter'
import { mapGetters } from 'vuex'
import PmWoCard from 'pages/workorder/PMWoCard'
import { getSites, getBuildings } from 'pages/workorder/wo-util.js'

export default {
  mixins: [LegendMixin],
  props: ['disablePaginate'],
  components: {
    'facilio-resource-planner': FacilioResourcePlanner,
    PmWoCard,
    plannerFilter,
    plannerLegend: PlannerLegend,
  },
  beforeUpdate() {},
  updated() {
    console.warn('Parent Updated ')
  },
  watch: {
    appliedWoFilters(newVal, oldVal) {
      console.log('wo filters=', newVal)
      this.$refs['resourcePlanner'].refreshGrid()
    },
  },
  data() {
    return {
      // pmList: []
      // jobsData: [],
      customizationSettings: {
        columnSettings: true,
        legendSettings: true,
        calendarSettings: true,
      },
      legendMap: null,
      storeDepsLoaded: false,
      enableDragAndDrop: false,
      paginate: true,
      perPage: 15,
      grouping: false,
      assetFilters: {
        siteId: null,
        assetCategoryId: null,
        buildingId: null,
      },
      isGlobalSite: false,

      legendColorAttribute: null,
      legendName: null,
      requestPending: false,
      siteList: [],
      buildings: [],
    }
  },
  async created() {
    await this.initResources()
    this.setTitle(this.$t('maintenance.calender.asset_planner'))
    //temp for debugging purposes
    // *********************
    if (this.$route.query.paginate != null) {
      this.paginate = JSON.parse(this.$route.query.paginate)
    }
    if (this.disablePaginate) {
      this.paginate = false
    }
    if (this.$route.query.perPage != null) {
      this.perPage = JSON.parse(this.$route.query.perPage)
    }
    // *********************
    if (this.$route.query.assetFilters) {
      this.assetFilters = JSON.parse(this.$route.query.assetFilters)
    } else {
      this.assetFilters.siteId = this.getCurrentSiteId()
      if (this.assetFilters.siteId == -1) {
        //if user in all sites mode , select the first site
        this.assetFilters.siteId = this.siteList[0].id
      } else {
        this.isGlobalSite = true
      }
      this.setQueryParams()
    }
  },
  mounted() {},
  computed: {
    ...mapGetters(['getCurrentSiteId']),
    appliedWoFilters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
  },
  methods: {
    async initResources() {
      this.siteList = await getSites()
      this.buildings = await getBuildings()
    },
    handleViewChanged(e) {
      this.legendColorAttribute = e.settings.legendSettings[0].name
      this.legendName = e.settings.legendSettings[0].displayName
      this.plannerSettings = e.settings
      this.grouping = e.settings.columnSettings.find(
        e => e.name == 'categoryName'
      ).enabled

      //if atleast one draggable column is enabled enable drag and drop in pm planner
      let enabledMetrics = e.settings.timeMetricSettings.filter(e => e.enabled)
      if (
        (enabledMetrics.length == 1 && enabledMetrics[0].name == 'planned') ||
        enabledMetrics[0].name == 'due'
      ) {
        this.enableDragAndDrop = true
      } else {
        this.enableDragAndDrop = false
      }
      console.log('fetching data for', e)
      console.log('current site is ', this.getCurrentSiteId())
      this.fetchPMJobs(e).then(() => {
        this.colorCodeWorkOrders(
          this.legendColorAttribute,
          this.jobsData.data.flat(),
          enabledMetrics
        ).then(legend => {
          this.legendMap = legend
          console.log('legend map', this.legendMap)
          this.renderJobs()
        })
      })
    },
    async fetchPMJobs(e) {
      let pmResp = await PlannerApiCalls.getAllJobs(
        this.assetFilters,
        this.appliedWoFilters,
        e,
        'ASSET_PLANNER'
      )
      //sort each row based on time
      pmResp.data.forEach(row => {
        row.forEach(e => {
          e.colorIndex = -1
          e.frequencyID = e.frequency //save frequency ID ie 1,2,4 in id field and actualy string in frequency field
          e.frequency = this.$constants.FACILIO_FREQUENCY[
            e.frequency
          ].substring(0, 1)
        })
        row.sort((e1, e2) => {
          return e1.start - e2.start
        })
      })
      this.jobsData = pmResp
      // console.log('jobs data  stored in comp is ',this.jobsData)
      return
    },
    renderJobs() {
      //let beforeRenderJobs=performance.now()
      this.$refs['resourcePlanner'].renderPlannerNew(
        this.jobsData.resourceHeaders,
        this.jobsData.resourceTitles,
        this.jobsData.data
      )
      //console.warn('time for renderJobs',performance.now()-beforeRenderJobs)
    },
    setQueryParams() {
      console.log('setting query params')
      this.$router.replace({
        query: {
          ...this.$route.query,
          assetFilters: JSON.stringify(this.assetFilters),
        },
      })
    },
    handleFilterChange(changedFilter) {
      this.assetFilters = changedFilter
      this.setQueryParams()
      this.showFilter = false
      this.$refs['resourcePlanner'].refreshGrid()
    },

    handleDrop(e) {
      let wo = e.event
      console.log('handling drop', e)
      let url = '/v2/pmplanner/jobs/update'

      let workorder = {
        id: wo.id,
      }
      if (wo.time == 'planned') {
        workorder.scheduledStart = wo.start
      } else if (wo.time == 'due') {
        workorder.dueDate = wo.start
      } else {
        console.error('adjusting unallowed metric ')
        return
      }

      this.requestPending = true
      this.$http.post(url, { workorder }).then(resp => {
        this.requestPending = false
        console.log('update  WO response', resp.data)
      })
    },
  },
}
</script>

<style lang="scss">
.new-planner {
  // width: 100%;
  overflow: scroll;
  .facilio-resource-planner {
    .table-container {
      height: calc(100vh - 210px);
    }
  }

  .facilio-resource-date-picker-container {
    width: unset !important;
    //margin-left:50px;//hacky alignment
  }
}
</style>
