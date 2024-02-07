<template>
  <div class="pm-wo-card">
    <div class="height300 d-flex justify-center align-center" v-if="loading">
      <spinner v-bind:show="true" size="80"></spinner>
    </div>
    <div v-else>
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
          {{ workorder.subject }}
        </div>
      </div>
      <div class="pm-wo-card-body">
        <el-row class="pB30">
          <el-col :span="10">
            <div class="fc-black-13 fwBold text-left pT15">Assigned To</div>
          </el-col>
          <el-col :span="14">
            <div class="fc-black-13 text-left pT15">
              {{ $getProperty(workorder, 'assignedTo.name', 'Unassigned') }}

              {{
                $getProperty(workorder, 'assignedTo.name', null)
                  ? `/${$getProperty(workorder, 'assignmentGroup.name', '--')}`
                  : null
              }}
            </div>
          </el-col>
        </el-row>

        <el-row class>
          <el-col :span="10">
            <div class="fc-black-13 fwBold text-left">
              {{ resourceTitle }}
            </div>
          </el-col>
          <el-col :span="14">
            <div class="fc-black-13 text-left">
              {{ $getProperty(workorder, 'resource.name', '--') }}
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
  </div>
</template>
<script>
import PlannerApiCalls from 'pages/workorder/PlannerAPICalls'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import Spinner from '@/Spinner'
import { API } from '@facilio/api'
export default {
  components: { Spinner },
  props: ['plannerEvent'],
  data() {
    return {
      loading: true,
      workorder: null,
    }
  },
  async created() {
    let { data, error } = await API.get(
      `/workorder/summary/${this.plannerEvent.event.id}`
    )

    if (error) {
      this.$message.error(
        'Error fetching workorder details for ',
        this.plannerEvent.id
      )
      console.error(error)
    } else {
      this.workorder = data.workorder
      this.loading = false
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
  },
  computed: {
    resourceTitle() {
      //associated resource field,check if asset or space
      if (this.plannerEvent.event.resource.resourceType === 2) {
        return 'Asset'
      } else {
        //==1 is space,workorder .resouce can only be asset or space
        return 'Space'
      }
    },
  },
}
</script>

<style lang="scss"></style>
