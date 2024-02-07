<template>
  <div class="new-planner pm-resource-planner pR15 pL15">
    <facilio-resource-planner
      v-bind:plannerType="'STAFF_PLANNER'"
      :customizationSettings="customizationSettings"
      ref="resourcePlanner"
      :views="['DAY', 'WEEK', 'MONTH']"
      @viewChanged="handleViewChanged"
      defaultView="MONTH"
      :saving="requestPending"
      :isDragDropAllowed="enableDragAndDrop"
      :isRowDragRestrict="false"
      v-bind:allowDownload="true"
      @dropped="handleDrop"
      noDataText="No workorders"
    >
      <!-- this slot can we should be changed  as Title,TOp left cell in table , Irrelevant now ,previously used to switch between staff/asset/space planners without reloading,now each in different tab-->
      <template slot="resourceSelection">
        <div
          class="width200 fc-input-full-border2 mR10 fc-input-border-remove fc-input-full-border2-bold"
        >
          Staff
        </div>
      </template>
      <template v-slot:taskContent="eventProps">
        <div class="rp-task-text ellipsis">
          {{ eventProps.event[eventProps.displayType] }}
        </div>
      </template>
      <template slot="plannerLeftTop">
        <planner-filter
          @filterChanged="handleFilterChange"
          :filterOptions="assetFilters"
        >
        </planner-filter>
      </template>
      <template slot="plannerRightTop">
        <planner-legend
          :legendMap="legendMap"
          :legendName="legendName"
        ></planner-legend>
      </template>
      <!-- slot for card is releaved only after event has been clicked -->
      <!-- to do , just pass the dom in an event , take out this slot and render card fully outside -->
      <template v-slot:card="slotProps">
        <StaffPlannerCard
          :plannerEvent="slotProps"
          @close="$refs['resourcePlanner'].handleCardClose"
          @deleteWo="$refs['resourcePlanner'].deleteEvent($event)"
        ></StaffPlannerCard>
      </template>
    </facilio-resource-planner>
  </div>
</template>

<script>
import FacilioResourcePlanner from './FacilioResourcePlanner'
import PlannerApiCalls from 'pages/workorder/PlannerAPICalls'
import plannerFilter from 'pages/workorder/PMPlannerFilter'
import { mapState, mapGetters } from 'vuex'
import StaffPlannerCard from 'pages/workorder/StaffPlannerCard'
import LegendMixin from 'pages/workorder/LegendHelper'
import PlannerLegend from 'pages/workorder/PlannerLegend'
import { getSites, getBuildings } from 'pages/workorder/wo-util.js'
import { isEmpty } from '@facilio/utils/validation'

export default {
  mixins: [LegendMixin],
  components: {
    'facilio-resource-planner': FacilioResourcePlanner,
    StaffPlannerCard,
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
    sites(newVal, oldVal) {
      if (!isEmpty(newVal)) {
        let { assetFilters } = this
        let { siteId } = assetFilters
        if ((isEmpty(siteId) || siteId < 0) && !isEmpty(newVal[0])) {
          this.assetFilters.siteId = newVal[0].id
        }
      }
    },
  },
  data() {
    return {
      // pmList: []
      legendMap: null,
      customizationSettings: { legendSettings: true },
      jobsData: [],
      staffList: [],
      storeDepsLoaded: false,
      enableDragAndDrop: true,
      perPage: 15,
      grouping: false,
      assetFilters: {
        siteId: null,
        buildingId: null,
        teamId: null,
      },
      isGlobalSite: false,

      legendColorAttribute: null,
      legendName: null,
      requestPending: false,
      sites: [],
    }
  },
  created() {
    this.setTitle(this.$t('maintenance.calender.staff_planner'))
    this.$store.dispatch('view/loadModuleMeta', 'workorder')
    //wait for store dependencies to load for applying filters
    Promise.all([
      this.$store.dispatch('loadTicketCategory'),
      this.$store.dispatch('loadTicketType'),
      this.$store.dispatch('loadSite'),
      this.$store.dispatch('loadTicketStatus', 'workorder'),
      this.$store.dispatch('loadTicketPriority'),
      this.$store.dispatch('loadUsers'),
      //this.fetchSitesList(),
    ]).then(() => {
      this.storeDepsLoaded = true
    })

    if (this.$route.query.assetFilters) {
      this.assetFilters = JSON.parse(this.$route.query.assetFilters)
    } else {
      this.assetFilters.siteId = this.getCurrentSiteId()
      if (this.assetFilters.siteId == -1) {
        //if user in all sites mode , select the first site
        if (!isEmpty(this.sites[0])) {
          this.assetFilters.siteId = this.sites[0].id
        }
      } else {
        this.isGlobalSite = true
      }
      this.setQueryParams()
    }
  },
  mounted() {},
  computed: {
    ...mapState({
      users: state => state.users,
      //sites: state => state.site,
    }),

    ...mapGetters(['getCurrentSiteId']),

    appliedWoFilters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
  },
  methods: {
    handleViewChanged(e) {
      this.legendColorAttribute = e.settings.legendSettings[0].name
      this.legendName = e.settings.legendSettings[0].displayName

      console.log('fetching data for', e)
      this.fetchSitesList().then(() => {
        this.fetchData(e).then(() => {
          this.colorCodeWorkOrders(
            this.legendColorAttribute,
            this.jobsData
          ).then(legend => {
            this.legendMap = legend
            console.log('legend map', this.legendMap)
            this.renderJobs()
          })
        })
      })

      // })
    },
    getStaffList() {
      //need to show all staff ,
      let staffList = [
        { id: -99, title: 'Unassigned', userStatus: true }, //add a dummy staff entry for unassigned , unassgined tasks have Id set to -99
        ...this.users
          .filter(user => {
            //   if(!user.userStatus)return false
            //need to do , show disabled users if they have pending workorders

            //based on applied filters check if staff can be included in the final list

            let teamCondition = true

            // if team filter not even applied  always true,  show all  users in final list
            if (this.assetFilters.teamId) {
              if (user.groups) {
                //if user has teams check if selected team in filter matches
                teamCondition = user.groups.includes(this.assetFilters.teamId)
              } else {
                teamCondition = false //not in any team
              }
            }

            let siteCondition = true

            if (this.assetFilters.siteId) {
              if (user.accessibleSpace) {
                siteCondition = user.accessibleSpace.includes(
                  this.assetFilters.siteId
                )
              }
              //if no accessible spaces , it means user has access to all spaces
            }

            let buildingCondition = true

            if (this.assetFilters.buildingId) {
              //when building filter applied and user has access to entire site , show user in list

              if (user.accessibleSpace) {
                buildingCondition =
                  user.accessibleSpace.includes(this.assetFilters.siteId) ||
                  user.accessibleSpace.includes(this.assetFilters.buildingId)
              }
              //if no accessible spaces , it means user has access to all buildings
            }

            return siteCondition && buildingCondition && teamCondition
          })
          .map(user => ({
            id: user.id,
            title: user.name,
            userStatus: user.userStatus,
          })),
      ]
      staffList.sort((e1, e2) => e1.id - e2.id) //sort workorders in time asc order
      return staffList
    },
    async fetchData(e) {
      let staffJobsResp = await PlannerApiCalls.getStaffJobs(
        e,
        this.assetFilters.siteId,
        this.assetFilters.buildingId,
        this.assetFilters.teamId,
        this.appliedWoFilters
      )

      this.jobsData = staffJobsResp.data.result.workorders
        .filter(wo => wo.trigger && wo.trigger.frequency > 0) //show only workorders generated from trigger and having a schedule(frequency)
        .map(wo => {
          return {
            start: wo.scheduledStart,
            frequency: wo.trigger.frequencyEnum.substring(0, 1),
            frequencyID: wo.trigger.frequency,
            ...wo,
            resourceId: this.$getProperty(wo, 'assignedTo.id', -99),
          }
        })
        .sort((e1, e2) => e1.start - e2.start) //sort workorders in time asc order
      // getStaffList is called only after workorders are fetched

      this.staffList = this.getStaffList(this.assetFilters.teamId)

      //remove disabled users if they don't have any associated WOs in the given time period
      this.staffList = this.staffList.filter(user => {
        if (!user.userStatus) {
          //disabled user
          return this.jobsData.find(wo => wo.resourceId == user.id)
        } else {
          return true
        }
      })

      return
    },
    renderJobs() {
      //let beforeRenderJobs=performance.now()
      this.$refs['resourcePlanner'].renderPlanner(
        this.jobsData,
        this.staffList,
        'resourceId'
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
      let event = e.event
      console.log('handling drop', e)
      let url = '/v2/pmplanner/jobs/update'

      let workorder = {
        id: event.id,
        assignedTo: { id: event.resourceId },
        scheduledStart: event.start,
      }

      this.requestPending = true
      this.$http.post(url, { workorder }).then(resp => {
        this.requestPending = false
        console.log('update  WO response', resp.data)
        event.assignedTo = resp.data.result.workorder.assignedTo
        event.assignmentGroup = resp.data.result.workorder.assignmentGroup
      })
    },
    async fetchSitesList() {
      let sitesList = await getSites()
      this.$set(this, 'sites', sitesList)
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
}
</style>
