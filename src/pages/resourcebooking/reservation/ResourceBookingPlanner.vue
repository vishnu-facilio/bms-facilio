<template>
  <div class="new-planner booking-resource-planner pR10 pL10 mT10">
    <facilio-resource-planner
      :views="['DAY']"
      defaultView="DAY"
      v-bind:isDragDropAllowed="false"
      @viewChanged="handleViewChange"
      @cellClicked="handleCellClick"
      ref="resourcePlanner"
      v-bind:allowUserSettings="false"
      v-bind:allowDownload="false"
      :defaultSettings="getBookingPlannerSettings()"
      v-bind:allowStetch="true"
      noDataText="No spaces"
    >
      <template slot="resourceSelection">
        <div
          class="width200 fc-input-full-border2 mR10 fc-input-border-remove fc-input-full-border2-bold mL8"
        >
          Spaces
        </div>
      </template>
      <template v-slot:taskContent="eventProps">
        {{ eventProps.event['name'] }}
      </template>
      <template slot="plannerLeftTop">
        <div class="pm-resource-input-search">
          <div class="flex-middle">
            <div
              class="fc-border-input-div2 pointer"
              style="width: 285px;"
              v-on:click.stop="showFilter = true"
            >
              <div class="label-txt-black">
                <!-- Vision HQ -->
                {{ spaceFilters.siteName }}
              </div>
              <div class="fc-grey-text f11">
                {{ spaceFilters.buildingName || 'All buildings' }} /
                {{ spaceFilters.floorName || 'All floors' }}
              </div>
            </div>
            <el-button
              type="primmary"
              class="pm-search-btn pointer"
              v-on:click.stop="showFilter = true"
            >
              <InlineSvg
                src="svgs/filter"
                iconClass="icon icon-md text-center vertical-baseline"
              ></InlineSvg>
            </el-button>
          </div>
          <booking-filter
            v-if="showFilter"
            @close="showFilter = false"
            @save="handleFilterChange"
            :filterOptions="spaceFilters"
          >
          </booking-filter>
        </div>
      </template>
      <!-- slot for card is releaved only after event has been clicked -->
      <!-- to do , just pass the dom in an event , take out this slot and render card fully outside -->
      <template v-slot:card="slotProps">
        <pm-wo-card
          :plannerEvent="slotProps"
          @close="$refs['resourcePlanner'].handleCardClose"
          @deleteWo="$refs['resourcePlanner'].deleteEvent($event)"
        ></pm-wo-card>
      </template>
    </facilio-resource-planner>
    <reservation-form
      v-if="showReservationForm"
      :preFillObj="reservationObj"
      :showCreateNewDialog.sync="showReservationForm"
      @saved="$refs['resourcePlanner'].refreshGrid()"
    ></reservation-form>
  </div>
</template>

<script>
import FacilioResourcePlanner from 'src/pages/workorder/FacilioResourcePlanner'
import PlannerApiCalls from 'pages/workorder/PlannerAPICalls'
import bookingFilter from './ResourceBookingFilter'
import ReservationForm from './ReservationForm'
import { mapState, mapGetters } from 'vuex'
import PmWoCard from './ReservationCard'

export default {
  props: ['disablePaginate'],
  components: {
    'facilio-resource-planner': FacilioResourcePlanner,
    PmWoCard,
    bookingFilter,
    ReservationForm,
  },
  beforeUpdate() {},
  updated() {
    console.warn('Parent Updated ')
  },
  data() {
    return {
      // pmList: []
      showReservationForm: false,
      reservationObj: null,
      bookingsData: [],
      spaceList: [],
      storeDepsLoaded: false,
      enableDragAndDrop: false,
      paginate: true,
      perPage: 15,
      grouping: false,
      spaceFilters: {
        siteId: null,
        siteName: null,
        spaceCategory: null,
        buildingId: null,
        buildingName: null,
        floorId: null,
        floorName: null,
      },
      isGlobalSite: false,

      legendColorAttribute: null,
      requestPending: false,

      showFilter: false,
    }
  },
  created() {
    this.$store.dispatch('loadSite')

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
    if (this.$route.query.spaceFilters) {
      this.spaceFilters = JSON.parse(this.$route.query.spaceFilters)
    } else {
      this.spaceFilters.siteId = this.getCurrentSiteId()
      if (this.spaceFilters.siteId == -1) {
        //if user in all sites mode , select the first site
        this.spaceFilters.siteId = this.sites[0].id
        this.spaceFilters.siteName = this.$store.getters.getSite(
          this.spaceFilters.siteId
        ).name
      } else {
        this.isGlobalSite = true
        this.spaceFilters.siteName = this.$store.getters.getSite(
          this.spaceFilters.siteId
        ).name
      }
      //this.setQueryParams()
    }
  },
  mounted() {},
  computed: {
    ...mapState({
      sites: state => state.site,
    }),
    ...mapGetters(['getCurrentSiteId']),
  },
  methods: {
    handleViewChange(calEvent) {
      console.log('fetching data for', calEvent)

      this.fetchBookingPlannerData(calEvent).then(() => {
        this.renderBookings()
      })
    },
    handleCellClick(event) {
      this.reservationObj = {
        scheduledStartTime: event.col.start,
        space: this.spaceResp.records.find(e => e.id == event.cell.rowId),
        siteId:
          this.spaceResp.records.find(e => e.id == event.cell.rowId).siteId +
          '',
        durationType: '2',
      }
      this.showReservationForm = true

      console.log(event)
    },
    //need to fetch resource list and resource booking
    async fetchBookingPlannerData(calEvent) {
      let spaceResp = await PlannerApiCalls.filteredSpaceList(this.spaceFilters)
      this.spaceResp = spaceResp
      this.spaceList = spaceResp.records.map(e => {
        return { id: e.id, title: e.name }
      })
      // console.log('jobs data  stored in comp is ',this.bookingsData)
      let reservationResp = await PlannerApiCalls.getBookings(
        this.spaceList,
        calEvent
      )

      reservationResp.result.reservations.forEach(e => {
        ;(e.start = e.scheduledStartTime),
          (e.end = e.scheduledEndTime),
          (e.resourceId = e.space.id)
      })
      this.bookingsData = reservationResp.result.reservations

      return
    },
    renderBookings() {
      //let beforerenderBookings=performance.now()
      this.$refs['resourcePlanner'].renderPlanner(
        this.bookingsData,
        this.spaceList,
        'resourceId'
      )
      //console.warn('time for renderBookings',performance.now()-beforerenderBookings)
    },
    setQueryParams() {
      console.log('setting query params')
      this.$router.replace({
        query: {
          ...this.$route.query,
          spaceFilters: JSON.stringify(this.spaceFilters),
        },
      })
    },
    handleFilterChange(changedFilter) {
      this.spaceFilters = changedFilter
      //this.setQueryParams()
      this.showFilter = false
      this.$refs['resourcePlanner'].refreshGrid()
    },

    getBookingPlannerSettings() {
      return {
        viewSettings: {
          MONTH: {
            displayType: 'dateTime',
            gridLines: 'DAY',
            grouping: 'NONE',
          },
          DAY: {
            displayType: 'dateTime',
            gridLines: '.5H',
            grouping: '1H',
            showGridLineHeaders: false,
          },
        },
        moveType: 'single', //just pass everything for now
      }
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
  .pm-resource-input-search {
    position: relative;
    .el-input-group__append {
      border-top-left-radius: 0;
      border-bottom-left-radius: 0;
      background: none;
    }
    .fc-border-input-div2 {
      border-top-right-radius: 0;
      border-bottom-right-radius: 0;
      border-right: none;
      border-radius: 0;
    }
    .label-txt-black {
      line-height: 13px;
      padding-top: 7px;
      font-weight: 500;
      max-width: 280px;
      text-overflow: ellipsis;
      overflow: hidden;
      white-space: nowrap;
      font-size: 12px;
    }
    .fc-grey-text {
      line-height: 12px;
      text-transform: unset;
      max-width: 280px;
      text-overflow: ellipsis;
      overflow: hidden;
      white-space: nowrap;
      font-size: 11px;
      letter-spacing: 0.3px;
      opacity: 0.6;
    }
  }
  .pm-resource-input-select-con {
    width: 333px;
    height: 360px;
    left: 0;
    right: 0;
    position: absolute;
    z-index: 200;
    box-shadow: 0 8px 20px 0 rgba(19, 23, 63, 0.09);
    border: 1px solid #d0d9e2;
    background-color: #ffffff;
    border-radius: 0;
    top: 40px;
    border-top: none;
  }
  .pm-search-btn {
    padding: 9px 14px 8px;
    border-radius: 0;
    border: 1px solid #d0d9e2;
    background: none;
    &:hover {
      background: #ffffff;
      border: 1px solid #d0d9e2;
      color: #324056;
    }
  }
  .showfilter-close-icon {
    position: absolute;
    right: 10px;
    top: 10px;
    font-size: 18px;
    font-weight: bold;
    cursor: pointer;
    color: #324056;
  }
}
</style>
