<template>
  <div class="facility-slot-info" ref="slot-container">
    <div>
      <el-row class="border-bottom slot-padding">
        <el-col :span="12">
          <el-row>
            <el-col :span="12" class="bold-label">Slot Duration</el-col>
            <el-col :span="12" class="label">{{
              !$validation.isEmpty(details.slotDuration)
                ? $helpers.getFormattedDuration(details.slotDuration, 's')
                : '---'
            }}</el-col>
          </el-row>
        </el-col>
        <el-col :span="12">
          <el-row>
            <el-col :span="12" class="bold-label"
              >Max Slot Booking Allowed</el-col
            >
            <el-col :span="12" class="label">{{
              !$validation.isEmpty(details.maxSlotBookingAllowed)
                ? details.maxSlotBookingAllowed
                : '---'
            }}</el-col>
          </el-row>
        </el-col>
      </el-row>
      <el-row class="border-bottom slot-padding">
        <el-col :span="12">
          <el-row>
            <el-col :span="12" class="bold-label">Parallel Booking</el-col>
            <el-col :span="12" class="label">{{
              details.isMultiBookingPerSlotAllowed ? 'Allowed' : 'Not Allowed'
            }}</el-col>
          </el-row>
        </el-col>
        <el-col :span="12">
          <el-row>
            <el-col :span="12" class="bold-label">Advance Booking Days</el-col>
            <el-col :span="12" class="label">
              {{
                !$validation.isEmpty(details.bookingAdvancePeriodInDays)
                  ? `Before ${details.bookingAdvancePeriodInDays} Days`
                  : '---'
              }}</el-col
            >
          </el-row>
        </el-col>
      </el-row>
      <el-row class="border-bottom slot-padding">
        <el-col :span="12">
          <el-row>
            <el-col :span="12" class="bold-label">Attendee List</el-col>
            <el-col :span="12" class="label">{{
              details.isAttendeeListNeeded ? 'Mandatory' : 'Not Mandatory'
            }}</el-col>
          </el-row>
        </el-col>
        <el-col :span="12">
          <el-row>
            <el-col :span="12" class="bold-label">Chargeable</el-col>
            <el-col :span="12" class="label">{{
              details.isChargeable ? 'Yes' : 'No'
            }}</el-col>
          </el-row>
        </el-col>
      </el-row>
    </div>
    <div
      v-if="$validation.isEmpty(details.weekDayAvailabilities)"
      class="d-flex justify-content-center flex-direction-column align-center height100"
    >
      <div>
        <inline-svg
          src="svgs/emptystate/commonempty"
          class="vertical-middle'"
          iconClass="icon icon-80"
        ></inline-svg>
      </div>
      <div class="fc-black3-16 self-center bold">
        No Slot Information Available
      </div>
    </div>
    <table v-else class="facility-summary-table p10 mT10">
      <thead>
        <tr>
          <th class="width300px">DAY</th>
          <th class="width300px">TIME</th>
          <th v-if="!$validation.isEmpty(details) && details.isChargeable">
            SLOT COST
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(slot, index) in details.weekDayAvailabilities" :key="index">
          <td>{{ slot.dayOfWeekEnum | pascalCase }}</td>
          <td>{{ `${slot.startTime} - ${slot.endTime}` }}</td>
          <td v-if="!$validation.isEmpty(details) && details.isChargeable">
            <currency :value="slot.cost || 0"></currency>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['widget', 'details', 'resizeWidget'],
  created() {
    this.autoResize()
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        if (!isEmpty(this.$refs['slot-container'])) {
          let height = this.$refs['slot-container'].scrollHeight + 30
          let width = this.$refs['slot-container'].scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height, width })
          }
        }
      })
    },
  },
}
</script>
