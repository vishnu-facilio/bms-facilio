<template>
  <spinner
    v-if="attendance.isLoading"
    :show="attendance.isLoading"
    size="100"
  ></spinner>
  <div v-else class="attendance-list-container">
    <div class="attendance-list-head">
      <div>
        {{ currentEmployeeName }}
      </div>
      <div>
        <TransitionButtons
          v-if="showTransitionButtons"
          :employeeID="employeeID"
          :allowDateTimeSelector="allowDateTimeSelector"
          :showAllButtons="true"
        />
      </div>
    </div>
    <div class="attendance-list-body">
      <el-table
        id="attendance-list-table"
        :data="attendance.list"
        max-height="500"
        @row-click="toggleAttendanceSummary"
        :cell-style="{ padding: '10px' }"
        :header-cell-style="{ padding: '10px' }"
      >
        <el-table-column label="Date">
          <template v-slot="data">
            <div>{{ formatDateWithoutTime(data.row.day) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="Check-In">
          <template v-slot="data">
            <div>{{ formatDateWithOnlyTime(data.row.checkInTime) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="Check-Out">
          <template v-slot="data">
            <div>{{ formatDateWithOnlyTime(data.row.checkOutTime) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="Working Hours">
          <template v-slot="data">
            <div>{{ msToHoursAndMinutes(data.row.workingHours) }}</div>
          </template>
        </el-table-column>

        <el-table-column label="Status">
          <template v-slot="data">
            <div>{{ getDisplayNameForStatus(data.row.status) }}</div>
          </template>
        </el-table-column>

        <el-table-column label="Shift">
          <template v-slot="data">
            <div>{{ prettifyEmpty($getProperty(data.row.shift, 'name')) }}</div>
          </template>
        </el-table-column>

        <el-table-column label="Paid Break">
          <template v-slot="data">
            <div>
              {{ msToHoursAndMinutes(data.row.totalPaidBreakHrs) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="Unpaid Break">
          <template v-slot="data">
            <div>
              {{ msToHoursAndMinutes(data.row.totalUnpaidBreakHrs) }}
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <AttendanceSummary
      v-if="showAttendanceSummary"
      :visibility.sync="showAttendanceSummary"
      :attendance="selectedAttendance"
      :peopleID="selectedEmployee.id"
      :myAttendance="myAttendance"
      @close-attendance-summary="toggleAttendanceSummary(null)"
    >
    </AttendanceSummary>
  </div>
</template>
<script>
import AttendanceView from 'src/pages/peopleV2/attendance/AttendanceView.vue'
export default {
  name: 'ListView',
  extends: AttendanceView,
  data() {
    return {
      showAttendanceSummary: false,
    }
  },
  methods: {
    toggleAttendanceSummary(obj) {
      this.selectedAttendance = obj
      this.showAttendanceSummary = !this.showAttendanceSummary
    },
  },
}
</script>
<style scoped>
.sort-icon {
  cursor: pointer;
}
.close-icon {
  cursor: pointer;
  margin: 10px;
}
.search-icon {
  cursor: pointer;
  border-right: 1px solid #e0e0e0;
  padding-right: 7px;
  margin-right: 7px;
}
.list-view-container {
  display: flex;
  padding: 5px;
  box-sizing: border-box;
  padding: 10px;
  height: 85vh;
}
.employee-list-container {
  display: flex;
  flex-direction: column;
  border: solid 1px #e0e0e0;
  background-color: white;
}
.employee-list-head {
  width: 230px;
  min-height: 61px;
  display: flex;
  justify-content: space-between;
  font-weight: bold;
  padding: 15px;
  align-content: center;
  align-items: center;
  margin-left: 5px;
}
.employee-list-head {
  flex-basis: 10%;
}
.employee-list-body {
  width: 235px;
  overflow: scroll;
  min-height: 520px;
}
.employee-list-item {
  padding: 15px 15px 35px 15px;
  border-top: solid 1px #e0e0e0;
  font-weight: bold;
}
.employee-list-item:hover {
  cursor: pointer;
}
.active {
  border-left: 5px solid #f53085;
  padding-left: 10px;
  background-color: #f6f8fc;
}
.employee-list-foot {
  width: 230px;
  flex-basis: 10%;
  display: flex;
  justify-content: center;
  align-items: center;
  border-top: solid 1px #e0e0e0;
}
.attendance-list-container {
  width: 100%;
  background-color: white;
}
.attendance-list-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: solid 1px #e0e0e0;
  padding: 15px 15px 15px 15px;
  font-weight: bold;
}
</style>
<style lang="scss">
.employee-list-foot {
  .fc-black-small-txt-12 {
    color: #0f81fe;
  }
}
</style>
