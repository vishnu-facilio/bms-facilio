<template>
  <div
    :visibility="visibility"
    class="comment-dialog attendance-summary"
    style="top: 50px; position: fixed"
  >
    <div class="comment-dialog-header">
      <h3 class="comment-dialog-heading" style="text-transform: uppercase">
        {{ formatDateWithoutTime(attendance.day) }}
      </h3>
      <div class="comment-close">
        <el-tooltip
          class="item"
          effect="dark"
          content="Close"
          placement="bottom"
        >
          <i
            class="el-icon-close"
            aria-hidden="true"
            v-on:click="closeSummary()"
          ></i>
        </el-tooltip>
      </div>
    </div>
    <div class="summary-dialog-body">
      <div class="section-heading">{{ $t('common.products.summary') }}</div>

      <table class="summary-table">
        <tr>
          <td>
            <div class="label">{{ $t('common.products.date') }}</div>
            <div>{{ formatDateWithoutTime(attendance.day) }}</div>
          </td>
          <td>
            <div class="label">
              {{ $t('common.products.attendanceStatus') }}
            </div>
            <div>{{ getDisplayNameForStatus(attendance.status) }}</div>
          </td>
        </tr>
        <tr>
          <td>
            <div class="label">{{ $t('common.products.shift') }}</div>
            <div>
              {{ prettifyEmpty($getProperty(attendance, 'shift.name')) }}
            </div>
          </td>
          <td>
            <div class="label">
              {{ $t('common.products.workingHours') }}
            </div>
            <div>{{ msToHoursAndMinutes(attendance.workingHours) }}</div>
          </td>
        </tr>
        <tr>
          <td>
            <div class="label">
              {{ $t('common.products.paidBreakHours') }}
            </div>
            <div>{{ msToHoursAndMinutes(attendance.totalPaidBreakHrs) }}</div>
          </td>
          <td>
            <div class="label">
              {{ $t('common.products.unPaidBreakHours') }}
            </div>
            <div>{{ msToHoursAndMinutes(attendance.totalUnpaidBreakHrs) }}</div>
          </td>
        </tr>
      </table>

      <div class="section-heading">
        {{ $t('common.products.transactions') }}
      </div>
      <el-skeleton class="mT30" v-if="transactions.isLoading" animated />
      <el-table
        v-else
        :data="transactions.list"
        style="width: 100%"
        max-height="300"
        :cell-style="{ padding: '10px' }"
        :header-cell-style="{ padding: '10px' }"
        :row-style="{ padding: '0px' }"
      >
        <el-table-column label="Type">
          <template slot-scope="props">
            <div>
              {{ getDisplayNameForTransition(props.row.transactionType) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="Time">
          <template v-slot="data">
            <div>{{ formatDateWithOnlyTime(data.row.transactionTime) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="Method" prop="sourceType"> </el-table-column>
        <el-table-column v-if="hasUpdatePermission">
          <template slot-scope="props">
            <i
              class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
              data-arrow="true"
              :title="$t('common._common.edit')"
              @click="editAttendnce(props.row)"
              v-tippy
            ></i>
          </template>
        </el-table-column>
        <el-table-column type="expand" class-name="tx_col">
          <template slot-scope="props">
            <div class="notes">{{ prettifyEmpty(props.row.notes) }}</div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import PeopleMixin from 'src/pages/peopleV2/peopleMixin.vue'
import { API } from '@facilio/api'
export default {
  name: 'AttendanceSummary',
  props: ['visibility', 'attendance', 'peopleID', 'myAttendance'],
  mixins: [PeopleMixin],
  created() {
    this.fetchAttendanceTransactionsForCurrentPeople()
  },
  data() {
    return {
      transactions: {
        list: [],
        isLoading: true,
      },
    }
  },
  computed: {
    hasUpdatePermission() {
      return !this.myAttendance
    },
  },
  methods: {
    editAttendnce(tx) {
      this.$emit('close-attendance-summary')
      this.$root.$emit('edit-attendance-transaction', tx)
    },
    closeSummary() {
      this.$emit('close-attendance-summary')
    },
    fetchAttendanceTransactionsForCurrentPeople() {
      this.fetchAttendanceTransactions(this.attendance.day, this.peopleID)
    },
    async fetchAttendanceTransactions(date, peopleID) {
      if (!(date || peopleID)) {
        console.warn('precondition failure in fetching attendance transactions')
        return
      }
      this.transactions.isLoading = true

      let route = `/v3/attendancetransaction/view?peopleID=${peopleID}&date=${date}`
      let { error, data } = await API.get(route)
      if (error) {
        error.message || this.$t('common._common.error_occured')
      } else {
        this.transactions.list = data.attendanceTransaction
      }
      this.transactions.isLoading = false
    },
  },
}
</script>

<style scoped>
.summary-dialog-body {
  padding: 10px;
  color: #324056;
}
.label {
  font-weight: bold;
  margin-bottom: 10px;
}

.summary-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0px 2.5em;
}

.section-heading {
  font-weight: 600;
  font-size: 1.2em;
  margin-top: 10px;
}
.notes {
  padding: 10px;
  background-color: #fafafa;
}
</style>
