<template>
  <div class="scheduler-list" style="overflow-x:hidden;overflow-y:hidden;">
    <setup-header>
      <template #heading>
        {{ $t('common._common.scheduler') }}
      </template>
      <template #description>
        {{ $t('common._common.list_of_all_scheduler') }}
      </template>
      <template #actions>
        <el-button type="primary" class="setup-el-btn" @click="addSchedule">
          {{ $t('common.header.add_scheduler') }}
        </el-button>
      </template>
      <template #searchAndPagination style="padding:0">
        <div style="height:50px;padding:5px 0;display:flex;align-items:center;">
          <f-search class="mL20" v-model="schedulerList"></f-search>
        </div>
      </template>
    </setup-header>
    <spinner v-if="loading" :show="loading" size="80"></spinner>

    <el-table
      v-else
      :data="scheduler"
      :cell-style="{ padding: '12px 30px' }"
      :empty-text="$t('common.header.no_schedule_available')"
      style="width: calc(100% - 20px);"
      height="calc(100% - 180px)"
      class="overflow-x-hidden mT10 mR15 mB30 mL10"
    >
      <el-table-column
        :label="$t('common.products.name')"
        prop="name"
      ></el-table-column>
      <el-table-column
        :label="$t('common._common.frequency')"
        prop="frequency"
      ></el-table-column>
      <el-table-column
        :label="$t('common.header.next_execution')"
        prop="nextExecutionTime"
      >
        <template v-slot="nextExecutionTime">
          <div>
            {{
              $getProperty(nextExecutionTime, 'row.nextExecutionTime', '---')
            }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('common.date_picker.last_modified_time')"
        prop="modifiedTime"
      >
        <template v-slot="modifiedTime">
          <div v-if="$getProperty(modifiedTime, 'row.modifiedTime')">
            {{ modifiedTime.row.modifiedTime | formatDate() }}
          </div>
          <div v-else>---</div>
        </template>
      </el-table-column>
      <el-table-column class="visibility-visible-actions" width="150px">
        <template v-slot="schedule">
          <div class="schedule-icons">
            <i
              class="visibility-hide-actions el-icon-edit fc-scheduler-list-edit"
              data-arrow="true"
              :title="$t('common.header.edit_schedule')"
              v-tippy
              @click="editSchedule(schedule.row.id)"
            ></i>
            <i
              class="visibility-hide-actions el-icon-delete fc-scheduler-list-delete"
              data-arrow="true"
              :title="$t('common.header.delete_schedule')"
              v-tippy
              @click="deleteSchedule(schedule.row.id)"
            ></i>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <NewScheduler
      v-if="showAddSchedule"
      :selectedSchedule="selectedSchedule"
      @onSave="loadScheduleList"
      @onClose="showAddSchedule = false"
    ></NewScheduler>
  </div>
</template>
<script>
import NewScheduler from './NewScheduler'
import moment from 'moment-timezone'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { frequencyTypes } from './schedulerFrequencyUtil'
import SetupHeader from 'pages/setup/components/SetupHeader'
import FSearch from '@/FSearch'
export default {
  components: { NewScheduler, SetupHeader, FSearch },

  data() {
    return {
      loading: false,
      schedulerList: [],
      showAddSchedule: false,
      selectedSchedule: null,
      frequencyTypes,
    }
  },
  title() {
    return 'Scheduler'
  },
  created() {
    this.loadScheduleList()
  },
  computed: {
    dayTypes() {
      // based on facilio frequency
      return {
        1: 'day',
        2: 'week',
        3: 'month',
        4: 'month',
        5: 'month',
        6: 'year',
      }
    },
    scheduler() {
      return this.schedulerList.map(scheduleObj => {
        let {
          DO_NOT_REPEAT,
          DAILY,
          WEEKLY,
          MONTHLY,
          QUARTERLY,
          HALF_YEARLY,
          ANNUALLY,
        } = this.frequencyTypes
        let {
          name,
          schedule,
          startTime,
          id,
          modifiedTime,
          nextExecutionTime,
        } = scheduleObj
        let { weekFrequency } = schedule
        let {
          frequency: scheduleFrequency,
          frequencyType: scheduleFrequencyType,
        } = schedule || {}
        let frequencyType = scheduleFrequencyType

        if (![DO_NOT_REPEAT, DAILY, WEEKLY].includes(scheduleFrequencyType)) {
          let Date = { 3: MONTHLY, 5: ANNUALLY, 7: QUARTERLY, 9: HALF_YEARLY }
          let Week = { 4: MONTHLY, 6: ANNUALLY, 8: QUARTERLY, 10: HALF_YEARLY }

          frequencyType =
            weekFrequency === -1 ? Date[frequencyType] : Week[frequencyType]
        }

        let startDate = !isEmpty(startTime)
          ? this.getForamtedDate(startTime, 'DD MMM YYYY')
          : '---'
        let frequency
        let frequencyInterval
        let facilioFrequencyTypes = {
          ...this.$constants.FACILIO_FREQUENCY,
          0: 'Do not repeat',
        }
        if (!isEmpty(scheduleFrequencyType)) {
          frequency = facilioFrequencyTypes[frequencyType]
          frequencyInterval = this.getFrequency(
            frequencyType,
            scheduleFrequency
          )
        } else {
          frequency = '---'
          frequencyInterval = '---'
        }
        //Formatting next execution time

        if (!isEmpty(nextExecutionTime) && nextExecutionTime != -1)
          nextExecutionTime = this.getForamtedDate(nextExecutionTime)
        else nextExecutionTime = '---'
        return {
          id,
          name,
          frequency,
          startDate,
          frequencyInterval,
          modifiedTime,
          nextExecutionTime,
        }
      })
    },
  },
  methods: {
    getFrequency(frequencyType, frequency) {
      if (frequencyType === 0) return '---'
      let text = frequency + ' ' + this.dayTypes[frequencyType]
      return frequency > 1 ? text + 's' : text
    },
    getForamtedDate(value, format) {
      return format
        ? moment(value).format(format)
        : this.$options.filters.formatDate(value, false, false)
    },
    async loadScheduleList() {
      this.loading = true

      let { error, data } = await API.get(
        '/v2/workflow/getScheduledWorkflowList'
      )

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.schedulerList = data.scheduledWorkflowContextList || []
      }
      this.loading = false
    },
    addSchedule() {
      this.selectedSchedule = null
      this.showAddSchedule = true
    },
    editSchedule(id) {
      this.selectedSchedule = this.schedulerList.find(s => s.id === id) || {}
      this.showAddSchedule = true
    },
    async deleteSchedule(id) {
      let { error } = await API.post('/v2/workflow/deleteScheduledWorkflow', {
        scheduledWorkflow: { id },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let idx = this.schedulerList.findIndex(s => s.id === id)

        if (!isEmpty(idx)) {
          this.schedulerList.splice(idx, 1)
        }
        this.$message.success(
          this.$t('common.products.schedule_deleted_succesfully')
        )
      }
    },
  },
}
</script>
<style lang="scss">
.scheduler-list {
  height: calc(100vh - 50px);

  .scheduler-header {
    margin-bottom: 50px;
    display: flex;
    justify-content: space-between;
  }
  .scheduler-name {
    font-size: 18px;
    color: #000000;
    letter-spacing: 0.7px;
    padding-bottom: 5px;
    text-transform: capitalize;
  }
  .scheduler-desc {
    font-size: 13px;
    color: #808080;
    letter-spacing: 0.3px;
  }
  .schedule-icons {
    display: flex;
    height: 100%;
  }
  .el-table--enable-row-hover .el-table__body tr:hover > td {
    background-color: rgba(0, 0, 0, 0) !important;
  }
  .el-table__cell {
    padding: 12px 20px !important;
  }
}
.fc-setup-actions-header {
  padding-top: 0;
  padding-bottom: 0;
}
</style>
