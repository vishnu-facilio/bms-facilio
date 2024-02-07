<template>
  <div
    class="header-sidebar-hide pm-planner-pdf-page"
    :class="{ 'header-sidebar-hide': $route.path === 'pdf/pmPlannerpdf' }"
  >
    <div class="pm-planner-pdf-con" v-if="!loading">
      <!-- overall planner -->
      <div class="pm-pdf-header">
        <div class="flex-middle justify-content-space">
          <div class="customer-logo">
            <div v-show="$org.logoUrl">
              <img :src="$org.logoUrl" style="width: 100px;" />
            </div>
          </div>
          <div class="pm-pdf-heading text-center">
            PM Calendar {{ currentRangeTitle }}
          </div>
          <div class="facil-logo">
            <img src="~assets/facilio-blue-logo.svg" style="width: 80px;" />
          </div>
        </div>
      </div>

      <div class="flex-middle justify-content-center pB20">
        <div
          class="fc-black-com f12 bold"
          v-if="assetFilters.hasOwnProperty('siteId')"
        >
          Site : <span class="fc-black-com f12 fw4">{{ site }}</span>
        </div>
        <div
          class="fc-black-com f12 bold pL30"
          v-if="assetFilters.hasOwnProperty('buildingId')"
        >
          Building : <span class="fc-black-com f12 fw4">{{ building }}</span>
        </div>
        <div
          class="fc-black-com bold f12 pL30"
          v-if="assetFilters.hasOwnProperty('assetCategoryId')"
        >
          Category : <span class="fc-black-com f12 fw4">{{ category }}</span>
        </div>

        <div
          class="fc-black-com bold f12 pL30"
          v-if="assetFilters.hasOwnProperty('teamId')"
        >
          Team : <span class="fc-black-com f12 fw4">{{ team }}</span>
        </div>
        <div
          class="fc-black-com bold f12 pL30"
          v-if="assetFilters.hasOwnProperty('floorId')"
        >
          Floor : <span class="fc-black-com f12 fw4">{{ floor }}</span>
        </div>
        <div class="fc-black-com f12 bold pL30" v-if="trigger">
          Trigger : <span class="fc-black-com f12 fw4">{{ trigger }}</span>
        </div>
      </div>
      <component v-if="!isSinglePM" :is="plannerType"> </component>
      <!-- single pm planner ,required PM meta details-->
      <pm-summary-planner
        v-if="isSinglePM && pmMetaLoaded"
        :pm="pmMeta"
        v-bind:isStaffPlanner="isStaffPlanner"
      ></pm-summary-planner>
    </div>
  </div>
</template>
<script>
import assetPlanner from 'src/pages/workorder/PMAssetPlanner'
import staffPlanner from 'src/pages/workorder/PMStaffPlanner'
import spacePlanner from 'src/pages/workorder/PMSpacePlanner'

import pmSummaryPlanner from 'src/pages/workorder/preventive/v1/PmSummaryPlannerView'
import { mapGetters } from 'vuex'

export default {
  created() {
    this.isSinglePM = this.$route.query.pmId != null
    this.isStaffPlanner = this.$route.query.staff
    if (this.isSinglePM) {
      this.$http
        .get(
          '/workorder/preventiveMaintenanceSummary?id=' +
            parseInt(this.$route.query.pmId)
        )
        .then(resp => {
          this.pmMeta = resp.data.preventivemaintenance
          this.pmMetaLoaded = true
        })
    }
    if (this.$route.query.triggerId) {
      this.trigger = decodeURIComponent(this.$route.query.triggerName)
    }
    if (this.$route.query.pmSubject) {
      this.trigger = decodeURIComponent(this.$route.query.pmSubject)
    }
    //when sites/buildings etc are removed from account api , need to dispatch those too

    Promise.all([
      this.$store.dispatch('loadAssetCategory'),
      this.$store.dispatch('loadBuildings'),
      this.$store.dispatch('loadSites'),
      this.$store.dispatch('loadAssetCategory'),
      this.$store.dispatch('loadGroups'),
    ]).then(e => {
      this.loading = false
      if (this.$route.query.assetFilters) {
        //set assetFilters only after all store data loaded, to avoid nullpointer on computed props
        this.assetFilters = JSON.parse(this.$route.query.assetFilters)
        this.setFilterNames()
      }
    })

    this.plannerType = this.$route.query.plannerType
  },
  props: [],
  components: {
    ASSET_PLANNER: assetPlanner,
    STAFF_PLANNER: staffPlanner,
    SPACE_PLANNER: spacePlanner,
    pmSummaryPlanner,
  },
  data() {
    return {
      loading: true,
      isSinglePM: false,
      isStaffPlanner: false,
      pmMetaLoaded: false,
      pmMeta: null,
      trigger: null,
      subject: null,
      assetFilters: {},
      plannerType: null,
      floor: null,
      team: null,
    }
  },
  methods: {
    //for floor and team
    setFilterNames() {
      if (this.assetFilters.hasOwnProperty('teamId')) {
        console.log('get and set team')
        if (this.assetFilters.teamId) {
          this.team = this.$store.getters.getGroup(
            this.assetFilters.teamId
          ).name
        } else {
          this.team = 'All teams'
        }
      }
      if (this.assetFilters.hasOwnProperty('floorId')) {
        if (this.assetFilters.floorId) {
          console.log('get and set floor')
          let url = 'floor/' + this.assetFilters.floorId
          this.$http.get(url).then(resp => {
            this.floor = resp.data.record.name
          })
        } else {
          this.floor = 'All floors'
        }
      }
    },
  },
  computed: {
    ...mapGetters(['getAssetCategory']),
    category() {
      if (this.assetFilters && this.assetFilters.assetCategoryId) {
        return this.getAssetCategory(this.assetFilters.assetCategoryId).name
      } else {
        return 'All'
      }
    },
    site() {
      if (this.assetFilters && this.assetFilters.siteId) {
        return this.$store.getters.getSite(this.assetFilters.siteId).name
      }
    },
    building() {
      if (this.assetFilters && this.assetFilters.buildingId) {
        return this.$store.getters.getBuildingNameById(
          this.assetFilters.buildingId
        )
      } else if (!this.isSinglePM) {
        return 'All '
      }
      //for all pm no building filter implies all ,for single , dont even mention building
    },
    currentRangeTitle() {
      let startTime = Number.parseInt(this.$route.query.startTime)
      let endTime = Number.parseInt(this.$route.query.endTime)

      switch (this.$route.query.calendarTab) {
        case 'YEAR':
          return this.$helpers.getOrgMoment(startTime).format('YYYY')

        case 'MONTH':
          return (
            this.$helpers.getOrgMoment(startTime).format('MMMM') +
            '  ' +
            this.$helpers.getOrgMoment(startTime).format('YYYY')
          )

        case 'WEEK':
          return (
            this.$helpers.getOrgMoment(startTime).format('MMMM') +
            '  ' +
            this.$helpers.getOrgMoment(startTime).format('DD') +
            ' - ' +
            this.$helpers.getOrgMoment(endTime).format('DD') +
            ' ' +
            this.$helpers.getOrgMoment(endTime).format('MMMM')
          )

        case 'DAY':
          return this.$helpers.getOrgMoment(startTime).format('DD-MMMM-YYYY')
        default:
          return null
      }
    },
  },
}
</script>

<style lang="scss">
.header-sidebar-hide .layout-header {
  display: none;
}
.header-sidebar-hide {
  width: 100%;
  height: 100vh !important;
  padding-left: 0 !important;
  background: #fff;
  overflow-y: scroll;
  overflow-x: hidden;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999999;
}
.pm-planner-pdf-con {
  width: 100%;
  height: 100%;
  margin-bottom: 30px;
  overflow: scroll;
  box-sizing: border-box;
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;

  td.rp-group-boundary {
    border-right: 1px solid rgba(0, 0, 0, 0.4);
  }
  th.rp-group-boundary {
    border-right: 1px solid rgba(0, 0, 0, 0.4);
  }
  .filter-container {
    display: none;
  }
  .pm-pdf-header {
    display: block;
    padding: 20px 20px 10px !important;
  }
  .pm-pdf-heading {
    font-size: 18px;
    font-weight: bold;
    letter-spacing: 1px;
    text-align: center;
    color: #324056;
    text-transform: uppercase;
  }
  .facilio-resource-planner .table-container .rp-grid-header-cell,
  .facilio-resource-planner .table-container .rp-resource-item-cell,
  .facilio-resource-planner .rp-task,
  .facilio-resource-planner .table-container .rp-group-header-cell {
    font-size: 10px !important;
    background: none;
  }
  .facilio-resource-planner .table-container .rp-group-header-cell {
    line-height: 20px;
    height: 20px;
  }
  .facilio-resource-planner .rp-task {
    font-weight: 400;
    border: none;
    height: 35px;
    line-height: 35px !important;
  }
  .facilio-resource-planner .table-container .rp-resource-item-cell {
    width: 150px;
    padding-left: 10px !important;
    line-height: 16px;
  }
  .fc-rp-top-bar,
  .fc-pm-summary-header .el-tabs__header,
  .fc-pm-summary-header .fL,
  .fc-pm-summary-header .fR {
    display: none;
  }
  .fc-pm-summary-tab {
    margin-top: 0;
  }
  .facilio-resource-planner .table-container {
    height: auto !important;
    overflow: scroll;
  }
  .pm-resource-planner {
    padding-bottom: 20px !important;
    overflow: scroll !important;
  }
  .facilio-resource-planner .table-container .rp-resource-item {
    text-align: left !important;
    width: 100px;
    position: static !important;
  }
  .facilio-resource-planner .table-container {
    border-top: none !important;
  }
  .pm-planner-pdf-con
    .facilio-resource-planner
    .table-container
    .rp-resource-item-cell {
    position: static;
  }
  .rp-type-header-cell {
    font-size: 10px;
    padding-left: 10px !important;
  }
  table {
    page-break-after: auto;
  }
  tr {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  td {
    page-break-inside: avoid;
    page-break-after: auto;
    border-bottom: 1px solid rgba(0, 0, 0, 0.4);
    border-right: 1px solid rgba(0, 0, 0, 0.4);
  }
  thead {
    display: table-header-group;
  }
  tfoot {
    display: table-footer-group;
  }
  .facilio-resource-planner table {
    border: none !important;
  }
  .pm-resource-planner .rp-cell {
    min-width: 32px !important;
    padding: 0;
    margin: 0;
    min-height: 20px !important;
  }
  .rp-type-header-cell .width200 {
    text-align: center;
  }
  .rp-type-header-cell,
  .facilio-resource-planner .table-container .rp-type-header {
    width: inherit !important;
  }

  .pm-resource-planner .rp-task-text {
    width: inherit !important;
    min-width: inherit !important;
    overflow: inherit;
    text-overflow: inherit;
    word-break: break-all;
    font-size: 10px;
    background: none;
  }
  .facilio-resource-planner .table-container .rp-grid-header-cell {
    line-height: 20px;
    width: auto;
  }
  .facilio-resource-planner {
    width: 100%;
    height: 100%;
    position: relative;
  }
  .fc-pm-summary-tab.hide-overflow .el-tabs__content {
    overflow: scroll;
    padding-bottom: 100px;
  }
  .facilio-resource-planner .table-container .rp-group-header,
  .facilio-resource-planner .table-container .is-grouped .rp-grid-header {
    position: relative !important;
    top: 0;
  }
  .facilio-resource-planner .table-container table th,
  .facilio-resource-planner .table-container table td {
    border-bottom: 1px solid rgba(0, 0, 0, 0.4);
    border-right: 1px solid rgba(0, 0, 0, 0.4);
  }
  .facilio-resource-planner .table-container table td.rp-group-boundary {
    border-right: 1px solid rgba(0, 0, 0, 0.4);
  }
  .facilio-resource-planner .table-container table {
    border-collapse: collapse;
    border: 1px solid rgba(0, 0, 0, 0.4) !important;
  }
  .facilio-resource-planner .table-container table th.rp-group-boundary {
    border-right: 1px solid rgba(0, 0, 0, 0.4);
  }
  .fc-white-theme .subheader-section {
    display: none;
  }
  .facilio-resource-planner .rp-task {
    height: auto;
    line-height: 20px !important;
  }

  .rp-group-header-cell {
    height: auto;
    line-height: 40px;
  }
  .pm-resource-planner .rp-cell {
    background: transparent;
  }
  .new-planner .facilio-resource-planner .table-container {
    height: 100% !important;
  }
  .pm-planner-pdf-page {
    height: 100vh !important;
    overflow: scroll;
  }
  .facilio-resource-planner .table-container .rp-type-header {
    width: 40px;
    position: initial;
  }
  .pm-resource-planner .rp-task {
    margin-bottom: 0;
  }
  .facilio-resource-planner .table-container {
    border-left: none;
    border-right: none;
  }
  .facilio-resource-planner .grid-container {
    height: auto;
  }
}
.header-sidebar-hide .facilio-resource-planner .fc-rp-top-bar {
  display: none;
}
.customer-logo {
  width: 20%;
  text-align: left;
}
.pm-pdf-heading {
  width: 80%;
  text-align: center;
}
.facil-logo {
  width: 20%;
  text-align: right;
}

.rp-seperator-item {
  position: static !important;
}
.rp-seperator-item-cell {
  min-height: 40px;
}
.rp-seperator-icon {
  opacity: 0 !important;
}
@media print {
  @page {
    size: landscape;
    overflow: visible !important;
  }
  .pm-planner-pdf-con {
    width: 100%;
    height: 100%;
    overflow-y: scroll;
    display: block;
    page-break-inside: avoid;
    page-break-after: auto;
  }
  * {
    margin: 0 !important;
    padding: 0 !important;
  }
  .header-sidebar-hide {
    height: 100% !important;
    position: relative !important;
  }
  html,
  body {
    background: #fff;
    display: block;
    width: auto;
    height: auto;
    page-break-after: avoid;
    page-break-before: avoid;
  }
  .scrollable {
    overflow: visible !important;
    margin-top: 0 !important;
    padding-top: 0px !important;
  }
  .normal main {
    display: block;
    width: auto;
    height: auto;
  }
  .scrollable {
    margin-top: 0 !important;
    padding-top: 0px !important;
  }
  .pm-planner-pdf-con .facilio-resource-planner {
    height: 100% !important;
  }
  table {
    page-break-inside: auto;
    page-break-after: auto;
  }
  tr {
    page-break-inside: avoid !important;
    page-break-after: auto !important;
  }
  td {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  tfoot {
    display: table-footer-group;
  }
  .pm-resource-planner {
    padding-bottom: 0px !important;
    zoom: 17%;
  }
  .new-planner .facilio-resource-planner .table-container {
    height: 100% !important;
    scroll-behavior: inherit;
  }
  .pm-planner-pdf-page {
    overflow: visible !important;
  }
  .facilio-resource-planner .table-container .rp-group-header-cell {
    line-height: 40px !important;
    height: 40px !important;
  }
  .facilio-resource-planner .table-container .rp-resource-item-cell {
    width: 200px !important;
    padding-left: 10px !important;
    line-height: 40px !important;
  }
  .facilio-resource-planner .table-container .rp-grid-header-cell {
    line-height: 50px !important;
    //width: 60px !important; remove this ,
    height: 50px !important;
  }
  .facilio-resource-planner .table-container .rp-group-header-cell {
    line-height: 60px !important;
    height: 60px !important;
  }
  .pm-resource-planner .rp-cell {
    min-height: 60px !important;
  }
  .pm-resource-planner .rp-task-text {
    height: 50px !important;
    line-height: 50px !important;
  }
  //DONT change, this has to be more specific than pmplanner.scss to override colors
  .pm-resource-planner .facilio-resource-planner .rp-task {
    height: auto !important;
    line-height: 20px !important;

    -webkit-print-color-adjust: exact;
  }

  .facilio-resource-planner .table-container .rp-resource-item-cell {
    width: 200px !important;
  }
  .new-planner .facilio-resource-planner .table-container {
    padding-bottom: 0 !important;
  }
}
</style>
