<template>
  <div class="pm-wo-card">
    <div class="pm-wo-card-header">
      <div class>
        <span class="fc-black-12 pR10 white-color">{{
          $helpers.formatDateFull(plannerEvent.event.start)
        }}</span>
        <span class="separator white-color">|</span>
        <span class="fc-black-11 white-color">{{
          $helpers.formatTimeFull(plannerEvent.event.start)
        }}</span>
      </div>
      <div class="pm-wo-card-header-close" @click="$emit('close')">
        <i class="el-icon-close fc-black3-16 fwBold white-color"></i>
      </div>
      <div class="fc-black3-16 pT10 white-color">
        {{ plannerEvent.event.subject }}
      </div>
    </div>
    <div class="pm-wo-card-body">
      <el-row class="pB30">
        <el-col :span="10">
          <div class="fc-black-13 fwBold text-left pT15">Assigned To</div>
        </el-col>
        <el-col :span="14">
          <div class="fc-black-13 text-left" v-if="allowStaffReAssign">
            <el-select
              v-model="woAssignedTo"
              @change="reAssignStaff"
              class="fc-input-full-border2 width100"
              :disabled="plannerEvent.col.past"
            >
              <el-option
                v-for="staff in staffList"
                :key="staff.id"
                :label="staff.name"
                :value="staff.id"
              ></el-option>
            </el-select>
          </div>
          <div v-else class="fc-black-13 text-left pT15">
            {{
              $getProperty(plannerEvent.event, 'assignedTo.name', 'Unassigned')
            }}

            {{
              $getProperty(plannerEvent.event, 'assignedTo.name', null)
                ? `/${$getProperty(
                    plannerEvent.event,
                    'assignmentGroup.name',
                    '--'
                  )}`
                : null
            }}
          </div>
        </el-col>
      </el-row>

      <el-row class>
        <el-col :span="10">
          <div class="fc-black-13 fwBold text-left">
            {{ getResourceTitle() }}
          </div>
        </el-col>
        <el-col :span="14">
          <div class="fc-black-13 text-left">
            {{
              plannerEvent.event.asset ||
                plannerEvent.event.space ||
                plannerEvent.event.resource.name
            }}
            <!-- asset field is  asset name handled specially in Pmplannerjobs api -->
            <!-- PM Asset planner and PM Staff planner Resp -->
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
          :to="openWoSummary(plannerEvent.event.id)"
          >View Details</router-link
        >
      </div>
      <div class="text-right">
        <el-button
          v-if="!plannerEvent.col.past"
          @click="handleDelete(plannerEvent.event.id)"
          class="fc-btn-icon-delete pointer"
        >
          <i class="el-icon-delete pR10"></i> Delete
        </el-button>
      </div>
    </div>
  </div>
</template>
<script>
import PlannerApiCalls from 'pages/workorder/PlannerAPICalls'
import { mapState } from 'vuex'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['plannerEvent', 'allowStaffReAssign'],
  data() {
    return {
      woAssignedTo: null,
    }
  },
  created() {
    this.$store.dispatch('loadUsers')

    if (
      this.plannerEvent.event.assignedTo &&
      this.plannerEvent.event.assignedTo.id
    ) {
      this.woAssignedTo = this.plannerEvent.event.assignedTo.id
    } else {
      this.woAssignedTo = null //unassigned
    }
  },
  methods: {
    openWoSummary(id) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.OVERVIEW) || {}

        if (name) {
          return this.$router.resolve({
            name,
            params: {
              viewname: 'all',
              id,
            },
          }).href
        }
      } else {
        return {
          name: 'wosummarynew',
          params: { id },
        }
      }
    },
    getResourceTitle() {
      if (this.plannerEvent.event.asset) {
        //asset field contains actualy asset display name, pmplannerjobsapi
        return 'Asset'
      } else if (this.plannerEvent.event.space) {
        return 'Space'
      }

      //when using normal workorder api for staff planner
      else {
        //associated resource field,check if asset or space
        if (this.plannerEvent.event.resource.resourceType === 2) {
          return 'Asset'
        } else if (this.plannerEvent.event.resource.resourceType === 1) {
          return 'Space'
        }
      }
    },
    handleDelete(workorderID) {
      console.log('delet wo called', workorderID)

      this.$dialog
        .confirm({
          title: this.$t('maintenance._workorder.delete_wo'),
          message: this.$t('maintenance._workorder.delete_wo_body'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(confirmDelete => {
          if (confirmDelete) {
            this.$emit('deleteWo', workorderID)
            PlannerApiCalls.deleteWorkOrder(workorderID).then(e => {
              console.log('workorder deleted', e)
            })
          }
        })
    },
    reAssignStaff() {
      //mutate original event and make api call
      this.plannerEvent.event.assignedTo = { id: this.woAssignedTo }
      PlannerApiCalls.assignStaff(this.plannerEvent.event.id, this.woAssignedTo)
    },
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),

    //filter users based on assigment group on workorder, if no group show all users in dropdown
    staffList() {
      let assignmentGroup = this.plannerEvent.event.assignmentGroup
        ? this.plannerEvent.event.assignmentGroup.id
        : null
      let activeUsers = this.users.filter(user => user.userStatus) //show only active users to reassign to

      if (assignmentGroup) {
        activeUsers = activeUsers.filter(user => {
          return user.groups
            ? user.groups.indexOf(assignmentGroup) != -1
            : false
        })
      }

      //if current  assignee not in  staff list  , (may have been disabled after assignment ), fetch from overall list and push in front
      if (
        this.woAssignedTo &&
        activeUsers.find(user => user.id == this.woAssignedTo) == null
      ) {
        activeUsers.unshift(this.$store.getters.getUser(this.woAssignedTo))
      }
      return activeUsers
    },
  },
}
</script>

<style lang="scss"></style>
