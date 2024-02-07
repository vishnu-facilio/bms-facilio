<template>
  <div class="pm-resource-planner pm-summary-planner">
    <facilio-resource-planner
      @ready="loaded = true"
      v-bind:plannerType="'ASSET_PLANNER'"
      :views="isStaffPlanner ? staffViews : assetViews"
      :defaultView="isStaffPlanner ? 'WEEK' : 'YEAR'"
      ref="resourcePlanner"
      @viewChanged="handleViewChange"
      @dropped="handleDrop"
      :isPMAssetPlanner="
        this.resourceKey == 'asset' || this.resourceKey == 'space'
      "
      :isRowDragRestrict="
        this.resourceKey == 'asset' || this.resourceKey == 'space'
      "
      :saving="requestPending"
      v-bind:allowDownload="true"
      noDataText="No workorders"
    >
      <template slot="resourceSelection">
        <div
          class="width200 fc-input-full-border2 mR10 fc-input-border-remove fc-input-full-border2-bold mL8"
        >
          {{ resourceMeta[resourceKey].name }}
        </div>
      </template>
      <template slot="plannerLeftTop">
        <el-select
          v-model="selectedTrigger"
          @change="triggerChanged"
          class="width200 fc-input-full-border2 mR10"
          trigger="click"
        >
          <el-option
            v-for="(trigger, index) in triggerOptions"
            :key="index"
            :label="trigger.name"
            :value="trigger.id"
          ></el-option>
        </el-select>
      </template>
      <template v-slot:card="slotProps" v-if="loaded">
        <div class="pm-wo-card">
          <div class="pm-wo-card-header">
            <div class="">
              <span class="fc-black-12 pR10 white-color">
                {{ $helpers.formatDateFull(slotProps.event.start) }}
              </span>
              <span class="separator white-color">|</span>
              <span class="fc-black-11 white-color">{{
                $helpers.formatTimeFull(slotProps.event.start)
              }}</span>
            </div>
            <div
              class="pm-wo-card-header-close"
              @click="$refs['resourcePlanner'].handleCardClose"
            >
              <i class="el-icon-close fc-black3-16 fwBold white-color"></i>
            </div>
            <div class="fc-black3-16 pT10 white-color">
              {{ slotProps.event.subject }}
            </div>
          </div>
          <div class="pm-wo-card-body">
            <el-row class="pB30">
              <el-col :span="10">
                <div class="fc-black-13 fwBold text-left pT15">Assigned To</div>
              </el-col>
              <el-col :span="14">
                <div class="fc-black-13 text-left">
                  <el-select
                    v-model="slotProps.event.staff"
                    @change="handleStaffChange(slotProps.event, $event)"
                    class="fc-input-full-border2 width100"
                    :disabled="slotProps.col.past"
                  >
                    <el-option
                      v-for="staff in resources.staff"
                      :key="staff.id"
                      :label="staff.title"
                      :value="staff.id"
                    >
                    </el-option>
                  </el-select>
                </div>
              </el-col>
            </el-row>

            <el-row class="">
              <el-col :span="10">
                <div class="fc-black-13 fwBold text-left">Asset/Space</div>
              </el-col>
              <el-col :span="14">
                <div class="fc-black-13 text-left">
                  {{ getResourceDetails(slotProps.event.id) }}
                </div>
              </el-col>
            </el-row>
          </div>
          <div
            class="pm-wo-card-footer display-flex-between-space flex-middle p30 pT10 pB20"
          >
            <div class="text-left">
              <router-link
                class="fc-dark-blue3-12 pointer"
                :to="redirectToWO(slotProps.event.id)"
              >
                View Details
              </router-link>
            </div>
            <div class="text-right">
              <el-button
                v-if="!slotProps.col.past"
                @click="handleDelete(slotProps.event.id)"
                class="fc-btn-icon-delete pointer"
              >
                <i class="el-icon-delete pR10"></i> Delete</el-button
              >
            </div>
          </div>
        </div>
      </template>
      <template v-slot:taskContent="eventProps">
        <div class="rp-task-text ellipsis">
          {{ eventProps.event[eventProps.displayType] }}
        </div>
      </template>
    </facilio-resource-planner>
  </div>
</template>

<script>
import PlannerApiCalls from 'pages/workorder/PlannerAPICalls'
import FacilioResourcePlanner from 'pages/workorder/FacilioResourcePlanner'
import { mapState } from 'vuex'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  components: {
    'facilio-resource-planner': FacilioResourcePlanner,
  },
  props: {
    pm: {
      type: Object,
    },
    isStaffPlanner: {
      type: Boolean,
    },
  },
  data() {
    return {
      loaded: false,
      assetViews: ['WEEK', 'MONTH', 'YEAR'],
      staffViews: ['DAY', 'WEEK', 'MONTH'],
      isJobApi: true,
      workorders: [],
      resourceKey: 'staff',
      resourceType: null,
      selectedTrigger: null,
      triggerOptions: [],
      resources: {
        // asset: [],
        // space: [],
        staff: this.getStaffList(),
      },
      resourceMeta: {
        staff: { name: 'Staff' },
      },
      requestPending: false,
    }
  },

  mounted() {
    this.getTriggerOptions()
  },
  created() {
    this.$store.dispatch('loadUsers')
  },

  computed: {
    ...mapState({
      users: state => state.users,
    }),
  },

  methods: {
    redirectToWO(id) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.OVERVIEW) || {}

        if (name) {
          return { name, params: { viewname: 'all', id } }
        }
      } else {
        return { path: `/app/wo/orders/summary/${id}` }
      }
    },
    setQueryParams() {
      this.$router.replace({
        query: {
          ...this.$route.query,
          triggerId: this.selectedTrigger,
          pmId: this.pm.id,
          pmSubject: encodeURIComponent(this.pm.name),
          triggerName: encodeURIComponent(
            this.getTriggerName(this.selectedTrigger)
          ),
          assetFilters: JSON.stringify({
            siteId: this.pm.siteId,
            assetCategoryId: this.pm.assetCategoryId,
            buildingId: null,
          }),
        },
      })
    },
    handleDelete(workorderID) {
      PlannerApiCalls.deleteWorkOrder(workorderID).then(() => {
        this.$refs['resourcePlanner'].deleteEvent(workorderID)
      })
    },
    refresh() {
      this.$refs['resourcePlanner'].refreshGrid()
    },
    getFilteredEventList() {
      if (this.selectedTrigger == 'ALL') {
        return this.workorders
      } else {
        return this.workorders.filter(e => {
          return e.triggerID == this.selectedTrigger
        })
      }
    },

    getTriggerOptions() {
      //clone trigger options from PM object and use for select box
      this.triggerOptions = this.$helpers
        .cloneObject(this.pm.triggers)
        .filter(e => {
          return e.triggerExecutionSource == 1
        })
        .map(e => {
          return {
            id: e.id,
            name: e.name,
            frequency: e.frequencyEnum,
          }
        })

      if (this.triggerOptions.length > 1) {
        this.triggerOptions.unshift({
          id: -1,
          name: 'All Triggers',
          frequency: null,
        })
      }
      if (this.$route.query.triggerId) {
        //if trigger id present in query param persist
        this.selectedTrigger = this.$route.query.triggerId
      } else {
        //set default trigger , select 1st trigger id , if more than 1 trigger select all as default
        this.selectedTrigger = this.triggerOptions[0].id
        if (this.triggerOptions.length > 1) {
          this.selectedTrigger = -1
        }

        this.setQueryParams()
      }
    },
    getTriggerName(triggerId) {
      if (triggerId == -1) {
        return 'All Triggers'
      } else {
        return this.triggerOptions.find(t => t.id == triggerId).name
      }
    },

    triggerChanged() {
      this.setQueryParams()
      this.$refs['resourcePlanner'].refreshGrid()
      //this.refreshPlanner()
      //this.fetchData(this.currentRange)
    },
    handleDrop(e) {
      this.requestPending = true
      let url = '/v2/pmplanner/jobs/update'

      //for staff view alone we can drag drop across rows
      let newAssignTo = e.destinationCell.rowId

      let reqJson = {
        workorder: {
          id: e.event.id,
          scheduledStart: e.event.start,
        },
      }
      // Same comp used for staff and asset planner ,if staff plan ,reassign the assignedTo Id to row Id(staffId=>rowID here)
      if (this.resourceKey == 'staff') {
        reqJson.workorder.assignedTo = { id: newAssignTo }
      }

      this.$http.post(url, reqJson).then(() => {
        this.requestPending = false
      })
    },
    handleStaffChange(event, newAssignToID) {
      //staff reassigned from outside planner component .
      //need to make api call here and sync the planner view when in staff mode
      this.requestPending = true
      let url = '/v2/pmplanner/jobs/update'

      let reqJson = {
        workorder: {
          id: event.id,
          scheduledStart: event.start,
          assignedTo: { id: newAssignToID },
        },
      }

      this.$http.post(url, reqJson).then(() => {
        if (this.resourceKey == 'staff') {
          this.$refs['resourcePlanner'].reAssignResource(
            event.id,
            newAssignToID
          )
        }
        this.requestPending = false
      })
    },
    resourceChanged() {
      //call handler aysnchronously , as select box close hangs due to long task
      setTimeout(() => {
        this.$refs['resourcePlanner'].changeResource(
          this.resources[this.resourceKey],
          this.resourceKey,
          this.resourceKey == 'asset' || this.resourceKey == 'space'
        )
      }, 0)
    },
    handleViewChange(e) {
      this.currentRange = e
      this.$emit('viewChanged', e)
      this.fetchData(e)
    },
    getStaffList() {
      //need to show all staff ,
      /*computed property isnt available when this runs,so accessing this.users fails
        TO DO , fix the data model to not have stafflist like this
       */
      let staffList = [
        { id: -99, title: 'Unassigned' },
        ...this.$store.state.users.map(user => ({
          id: user.id,
          title: user.name,
        })),
      ]
      this.sortResources(staffList, 'id')
      return staffList
    },
    fetchData(range) {
      //fetch all workorders for a PM for given duration

      let url = '/v2/workorders/newcalendar/list/all'

      let params = {
        // page: 1,
        perPage: -1,

        filters: {
          pm: {
            operatorId: 36,
            value: [this.pm.id + ''],
          },
          scheduledStart: {
            operatorId: 20,
            value: [range.start.toString(), range.end.toString()],
          },
        },
        orderBy: 'scheduledStart',
        orderType: 'desc',
        includeParentFilter: true,
      }

      url +=
        '?perPage=' +
        params.perPage +
        '&filters=' +
        encodeURI(JSON.stringify(params.filters)) +
        '&orderBy=' +
        params.orderBy +
        '&orderType=' +
        params.orderType +
        '&includeParentFilter=' +
        params.includeParentFilter

      this.$http.get(url).then(resp => {
        this.setData(resp)
      })
    },

    setData(response) {
      //asset and spaces for the set of workorders
      let workorders = []
      let assets = {}
      let spaces = {}
      response.data.result.workorders.forEach(e => {
        // console.log(e.trigger.id)
        //skip workorders without a trigger (created via execute now)
        if (!e.trigger) return
        //trigger filter here itself
        else if (
          //TO DO , move filter to server
          this.selectedTrigger != -1 &&
          e.trigger.id != this.selectedTrigger
        )
          return

        let wo = {
          start: e.scheduledStart,
          subject: e.subject,
          staff: e.assignedTo ? e.assignedTo.id : -99, //assigned staff id
          id: e.id,
          triggerID: e.trigger.id,
          frequency: this.getFrequency(e.trigger.id),
        }

        let mappedResource = e.resource
        //mapped resource can be a site or space
        if (mappedResource.resourceType == 1) {
          //space
          spaces[mappedResource.id] = {
            id: mappedResource.id,
            title: mappedResource.name,
          }
          wo.space = mappedResource.id
        } else if (mappedResource.resourceType == 2) {
          //asset
          wo.asset = mappedResource.id
          assets[mappedResource.id] = {
            id: mappedResource.id,
            title: mappedResource.name,
          }
        }

        workorders.push(wo)
      })

      //redundant client sorting
      workorders.sort((e1, e2) => {
        return e1.start - e2.start
      })

      this.workorders = workorders
      // this.eventList=this.getFilteredEventList()
      //Pm is mapped either to asset or space,only push valid option inside select box
      if (!this.$helpers.isEmpty(assets)) {
        this.$set(this.resources, 'asset', Object.values(assets))
        this.sortResources(this.resources['asset'], 'id')
        this.$set(this.resourceMeta, 'asset', { name: 'Assets' })
        this.resourceType = 'asset'
        if (!this.isStaffPlanner) this.resourceKey = 'asset' //set default view to space/asset
      }
      if (!this.$helpers.isEmpty(spaces)) {
        this.$set(this.resources, 'space', Object.values(spaces))
        this.sortResources(this.resources['space'], 'id')
        this.$set(this.resourceMeta, 'space', { name: 'Spaces' })
        this.resourceType = 'space'
        if (!this.isStaffPlanner) this.resourceKey = 'space'
      }

      this.refreshPlanner()
      //this.resources.space = Object.values(spaces);
    },

    sortResources(resourceList, key) {
      resourceList.sort((e1, e2) => {
        return e1[key] - e2[key]
      })
    },
    refreshPlanner() {
      //prevent unneeded reactivity overhead by cloning object
      this.$refs['resourcePlanner'].renderPlanner(
        this.workorders,
        this.workorders.length == 0 ? [] : this.resources[this.resourceKey], //planner uses no data state only when resources and tasks are empty
        this.resourceKey
      )
    },
    getFrequency(triggerID) {
      let trigger = this.pm.triggers.find(e => {
        return e.id == triggerID
      })
      return trigger.frequencyEnum.substring(0, 1)
    },
    //find workorder from wo list , get staff/asset id from wo and get full details from resourceList
    findWorkOrder(id) {
      return this.workorders.find(e => {
        return e.id == id
      })
    },
    getStaffTitle(staffID) {
      let staff = this.resources.staff.find(e => {
        return e.id == staffID
      })
      return staff.title
    },
    getResourceDetails(workorderID) {
      if (workorderID == null) return
      let workorder = this.findWorkOrder(workorderID)
      let resource = this.resources[this.resourceType].find(e => {
        return e.id == workorder[this.resourceType]
      })
      return resource.title
    },
  },
}
</script>

<style lang="scss">
.pm-summary-planner {
  .facilio-resource-planner {
    .table-container {
      height: calc(
        100vh - 320px
      ); //table container should take up rest of area after top bars etc
    }
  }
}
</style>
