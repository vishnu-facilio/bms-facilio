<template>
  <div class="h100 height100 overflow-hidden">
    <div
      class="setting-header2 position-relative fp-header indoor-fp-topbar p10"
    >
      <div
        class="setting-title-block fp-chooser-new"
        @click="showBuildingFilter"
      >
        <div class="fc-black3-16 fw4 f15 line-height20">
          <i class="el-icon-office-building f18 mR10"></i>
          <span v-if="building">{{ `${building.name}` }}</span>
          <span v-else>{{ `Building` }}</span>
          <span>
            <i
              class="el-icon-arrow-right fc-grey-text12-light f18 pR5 pL5 vertical-bottom bold"
            ></i>
          </span>
          <span v-if="floor">{{ `${floor.name}` }}</span>
          <span v-else>{{ `Floor` }}</span>
        </div>
      </div>
      <portal-target
        name="indoorFloorPlanLayout"
        class="indoor-fp-layout-portal"
      >
      </portal-target>

      <div class="action-btn setting-page-btn flex mT5" v-if="!portalLayout">
        <el-radio-group
          v-model="viewerMode"
          class="fp-mode-switch"
          v-if="
            showViewOption &&
              (showViewAssignmentOption ||
                showAssignmentDepartmentOption ||
                showAssignmentOwnOption) &&
              (showViewBookingOption ||
                showBookingDepartmentOption ||
                showBookingOwnOption)
          "
        >
          <el-radio-button label="ASSIGNMENT">
            <div class="flex-middle">
              <inline-svg
                src="svgs/user-check"
                iconClass="icon text-center icon-sm-md"
              ></inline-svg>
              <div class="pL5">
                Assignment
              </div>
            </div>
          </el-radio-button>
          <el-radio-button label="BOOKING">
            <div class="flex-middle">
              <inline-svg
                src="svgs/calendar2"
                iconClass="icon text-center icon-sm-md"
              ></inline-svg>
              <div class="pL5">Booking</div>
            </div>
          </el-radio-button>
        </el-radio-group>
        <!-- <div class="f20">
          <inline-svg
            src="svgs/filter3"
            class="vertical-middle fc-filter-icon"
            iconClass="icon icon-md"
          ></inline-svg>
        </div> -->
        <el-dropdown
          v-if="showViewOption && (showEditOption || showCreateOption)"
          class="pointer fc-actions-floor"
          @command="actions"
          trigger="click"
        >
          <span class="el-dropdown-link">
            <i class="el-icon-more rotate-90 pointer"></i>
          </span>
          <el-dropdown-menu slot="dropdown" class="dashboard-subheader-dp">
            <el-dropdown-item command="edit" v-if="showEditOption">
              <div>
                {{ $t('common._common.edit') }}
              </div>
            </el-dropdown-item>
            <el-dropdown-item command="new" v-if="showCreateOption">
              <div>
                {{ 'Add Floor Plan' }}
              </div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <IndoorFloorPlanAssignmentView
      ref="assignmentView"
      v-if="!loading && floorplanIdFromRoute && viewerMode == 'ASSIGNMENT'"
      :id="floorplanIdFromRoute"
      :building="building"
      :floor="floor"
      @floorPlanLoaded="getFloorDetails"
    ></IndoorFloorPlanAssignmentView>

    <IndoorFloorPlanBookingsView
      ref="bookingView"
      v-else-if="!loading && floorplanIdFromRoute && viewerMode == 'BOOKING'"
      :id="floorplanIdFromRoute"
      :customTime="customTime"
      @floorPlanLoaded="getFloorDetails"
    ></IndoorFloorPlanBookingsView>

    <div
      class="formbuilder-fullscreen-popup floor-plan-builder height100"
      v-if="fpEditor"
    >
      <FloorPlanEditor
        @close="closeEditor"
        :id="floorplanIdFromRoute"
        :visibile.sync="fpEditor"
      ></FloorPlanEditor>
    </div>

    <!-- filter dialog -->
    <IndoorFloorPlanSwitcher
      v-if="filterDialogOpen"
      :visibility.sync="filterDialogOpen"
      :floorplanId="floorplanIdFromRoute"
      :floorId="floorId"
      :buildingId="buildingId"
    ></IndoorFloorPlanSwitcher>

    <floorplanUploader
      v-if="newFp"
      :visibility.sync="newFp"
      @saved="floorplanSaved"
      :moduleName="'indoorfloorplan'"
    ></floorplanUploader>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import { API } from '@facilio/api'
import IndoorFloorPlanAssignmentView from 'pages/indoorFloorPlan/IndoorFloorPlanAssignmentView'
import IndoorFloorPlanBookingsView from 'pages/indoorFloorPlan/IndoorFloorPlanBookingsView'
import IndoorFloorPlanSwitcher from 'pages/indoorFloorPlan/components/IndoorFloorPlanSwitcher'
import floorplanUploader from 'pages/indoorFloorPlan/IndoorFloorPlanForm.vue'
import { getApp } from '@facilio/router'
import { eventBus } from '@/page/widget/utils/eventBus'

export default {
  components: {
    IndoorFloorPlanAssignmentView,
    IndoorFloorPlanSwitcher,
    IndoorFloorPlanBookingsView,
    floorplanUploader,
    FloorPlanEditor: () =>
      import('pages/indoorFloorPlan/IndoorFloorPlanEditor'),
  },
  data() {
    return {
      fpEditor: false,
      customTime: {},
      floorlPlanList: [],
      loading: true,
      newFp: false,
      viewerMode: 'ASSIGNMENT',
      filterDialogOpen: false,
      building: {
        name: '...loading',
      },
      floor: {
        name: '...loading',
      },
    }
  },
  watch: {
    floorplanIdFromRoute: {
      immediate: true,
      handler() {
        {
          if (this.floorIdfromRouted && !this.floorplanIdFromRoute) {
            this.fetchFloorDetails(this.floorIdfromRouted)
          } else if (!this.floorIdfromRouted && !this.floorplanIdFromRoute) {
            //first time entering floor plan layout,no id in route url
            this.loadDefaultFloorPlan() //default id appened to route and next watch call lands up in next block
          } else {
            this.changeFloorPlan()
            this.tempFuntion()
            //this.loadFloorAndBuildingInfo()
          }
        }
      },
    },
  },
  created() {
    eventBus.$on('SWITCH_FLOORPLAN_MODE', mode => {
      if (mode) {
        this.setViewModefromSwitcher(mode)
      }
    })
    if (this.showViewOption === false) {
      this.viewerMode = 'ASSIGNMENT'
    } else {
      if (
        this.showViewAssignmentOption ||
        this.showAssignmentDepartmentOption ||
        this.showAssignmentOwnOption
      ) {
        this.viewerMode = 'ASSIGNMENT'
      } else {
        this.viewerMode = 'BOOKING'
      }
    }
    if (this.portalLayout) {
      this.viewerMode = 'BOOKING'
    }
    if (this.$route.query.viewMode == 'BOOKING') {
      this.viewerMode = 'BOOKING'
      this.customTime = {
        date: this.$route.query.date,
        startTime: this.$route.query.startTime,
        endTime: this.$route.query.endTime,
      }
    }
  },
  computed: {
    ...mapGetters(['getCurrentUser']),
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),

    hasEditPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('EDIT', currentTab)
    },

    hasCreatePermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('CREATE', currentTab)
    },
    hasViewPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW', currentTab)
    },
    hasViewAssignmentPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_ASSIGNMENT', currentTab)
    },

    hasAssignmentDepartmentPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_ASSIGNMENT_DEPARTMENT', currentTab)
    },
    hasAssignmentOwnPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_ASSIGNMENT_OWN', currentTab)
    },
    hasViewBookingPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_BOOKING', currentTab)
    },
    hasBookingDepartmentPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_BOOKING_DEPARTMENT', currentTab)
    },
    hasBookingOwnPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_BOOKING_OWN', currentTab)
    },
    portalLayout() {
      // if (this.$account.org.orgId === 429) {
      //   return true
      // }
      let {
        appCategory: { PORTALS },
      } = this.$constants
      if (getApp() && getApp().appCategory === PORTALS) {
        return true
      }
      return false
    },
    floorplanIdFromRoute() {
      if (this.$route && this.$route.params.floorplanid) {
        return parseInt(this.$route.params.floorplanid)
      }
      return null
    },
    floorIdfromRouted() {
      if (this.$route && this.$route.params.floorId) {
        return parseInt(this.$route.params.floorId)
      }
      return null
    },
    floorId() {
      let { floor } = this
      return floor && floor.id ? floor.id : null
    },
    buildingId() {
      let { building } = this
      return building && building.id ? building.id : null
    },
    showEditOption() {
      if (this.hasViewPermission) {
        if (this.hasEditPermission) {
          return true
        }
      }
      return false
    },
    showCreateOption() {
      if (this.hasViewPermission) {
        if (this.hasCreatePermission) {
          return true
        }
      }
      return false
    },
    showViewOption() {
      if (this.hasViewPermission) {
        return true
      }
      return false
    },
    showViewAssignmentOption() {
      if (this.hasViewPermission) {
        if (this.hasViewAssignmentPermission) {
          return true
        }
      }
      return false
    },
    showAssignmentDepartmentOption() {
      if (this.hasViewPermission) {
        if (this.hasAssignmentDepartmentPermission) {
          return true
        }
      }
      return false
    },
    showAssignmentOwnOption() {
      if (this.hasViewPermission) {
        if (this.hasAssignmentOwnPermission) {
          return true
        }
      }
      return false
    },
    showViewBookingOption() {
      if (this.hasViewPermission) {
        if (this.hasViewBookingPermission) {
          return true
        }
      }
      return false
    },
    showBookingDepartmentOption() {
      if (this.hasViewPermission) {
        if (this.hasBookingDepartmentPermission) {
          return true
        }
      }
      return false
    },
    showBookingOwnOption() {
      if (this.hasViewPermission) {
        if (this.hasBookingOwnPermission) {
          return true
        }
      }
      return false
    },
  },
  beforeDestroy() {
    eventBus.$off('SWITCH_FLOORPLAN_MODE', () => {
      // handel floorplan swicth mode event off
    })
  },
  methods: {
    setViewModefromSwitcher(mode) {
      if (mode && mode !== 'edit') {
        this.viewerMode = mode
      } else if (mode == 'edit') {
        this.openEditor()
      }
    },
    tempFuntion() {
      this.floor = {
        name: '...loading',
      }
      this.building = {
        name: '...loading',
      }
    },
    getFloorDetails(floorplan) {
      if (floorplan?.floor?.id) {
        this.floor = floorplan.floor
      } else {
        this.floor = {
          name: 'Floor',
        }
      }
      if (floorplan?.building?.id) {
        this.building = floorplan.building
      } else {
        this.building = {
          name: 'Building',
        }
      }
    },
    async loadDefaultFloorPlan() {
      //on layout load for floor plan, if floorplan id present in query load particular floor plan
      //For no floorplan ID in query
      //user associated with particular desk,redirect page to include that plan id in query
      //Not assiciated.load random floor plan
      let currentlyAssignedFloor = await this.getCurrentUserFloor()

      let { floorId, indoorFloorPlanId } = this.formatFloorObject(
        currentlyAssignedFloor
      )

      if (indoorFloorPlanId) {
        this.$router.push({
          params: {
            floorId: floorId,
            floorplanid: indoorFloorPlanId,
          },
        })
      } else {
        this.getFirstActivePlanFromFloorList()
      }
    },

    formatFloorObject(floor) {
      return {
        indoorFloorPlanId: floor?.data?.indoorFloorPlanId,
        floorId: floor?.id,
      }
    },
    async fetchFloorDetails(floorId) {
      let params = {
        floorId: floorId,
      }
      let { data } = await API.post(`v2/floor/details`, params)
      if (data?.floor?.id && data?.floor?.indoorFloorPlanId) {
        this.$router.push({
          params: {
            floorId: data.floor.id,
            floorplanid: data.floor.indoorFloorPlanId,
          },
        })
      } else {
        this.loadDefaultFloorPlan()
      }
    },

    async getCurrentUserFloor() {
      let user = this.getCurrentUser()

      let url = 'v2/servicePortalHome'
      let params = { fetchOnlyDesk: true, count: 1, recordId: user.peopleId }
      let { data, error } = await API.get(url, params)

      if (error) {
        console.error('error fetching employee from user record')
      } else {
        let floor = data?.desks?.[0]?.floor
        //console.log('floorplanId associated with current user is ',floorPlanId)
        return floor
      }
    },
    floorplanSaved(indoorfloorplan) {
      if (indoorfloorplan && indoorfloorplan.id) {
        this.$router.replace({
          params: { floorplanid: indoorfloorplan.id },
        })
        this.$nextTick(() => {
          this.openEditor()
        })
      }
    },
    async loadFloorAndBuildingInfo() {
      let params = {
        viewName: 'all',
        page: 1,
        perPage: 1,
        moduleName: 'floor',
        filters: JSON.stringify({
          indoorFloorPlanId: {
            operatorId: 36,
            value: [String(this.floorplanIdFromRoute)],
          },
        }),
      }
      let { data, error } = await API.get('v2/module/data/list', params)
      if (error) {
        this.$message.error('Error loading floor and building info')
      } else {
        this.floor = data.moduleDatas[0]
        if (this.floor) {
          this.building = this.floor.building
        } else {
          this.building = null
        }
      }
    },

    changeFloorPlan() {
      this.loading = true
      this.$nextTick(() => {
        this.loading = false
      })
    },
    openEditor() {
      this.fpEditor = true
      if (this.viewerMode === 'ASSIGNMENT') {
        if (this.$refs['assignmentView']) {
          this.$refs['assignmentView'].removeEventListenerevents &&
            this.$refs['assignmentView'].removeEventListenerevents()
        }
      }
    },
    actions(name) {
      if (name === 'edit') {
        this.openEditor()
      } else if (name === 'new') {
        this.newFp = true
      }
    },
    closeEditor() {
      this.fpEditor = false
      if (this.viewerMode === 'ASSIGNMENT') {
        if (this.$refs['assignmentView']) {
          this.$refs['assignmentView'].addEventListenerevents &&
            this.$refs['assignmentView'].addEventListenerevents()
          this.$refs['assignmentView'].init()
        }
      } else if (this.viewerMode === 'BOOKING') {
        if (this.$refs['bookingView']) {
          this.$refs['bookingView'].addEventListenerevents &&
            this.$refs['bookingView'].addEventListenerevents()
          this.$refs['bookingView'].init()
        }
      }
    },
    showBuildingFilter(props) {
      if (!props?.disableDialog) {
        this.filterDialogOpen = true
      }
    },
    async getFirstActivePlanFromFloorList() {
      //get first floor with an  active floor plan
      let params = {
        viewName: 'all',
        page: 1,
        perPage: 1,
        moduleName: 'floor',
        filters: JSON.stringify({
          indoorFloorPlanId: {
            operatorId: 2,
            value: [],
          },
        }),
      }
      let { data, error } = await API.get('v2/module/data/list', params)

      if (!error) {
        let floorPlanIdFromList = data.moduleDatas[0].indoorFloorPlanId
        let floor = data.moduleDatas[0]
        this.$router.push({
          params: { floorId: floor.id, floorplanid: floorPlanIdFromList },
        })
      } else {
        this.$error.message('Error loading all floors', error)
      }
      this.loading = false
    },
  },
}
</script>
<style lang="scss">
.fp-mode-switch {
  .el-radio-button__inner {
    padding: 7px 20px;
    border: 1px solid #efefef;
    color: #324056;
  }

  .el-radio-button__orig-radio:checked + .el-radio-button__inner {
    background: #ff3184;
    border-color: #ff3184;
    box-shadow: -1px 0 0 0 #ff3184;
  }
}
</style>
