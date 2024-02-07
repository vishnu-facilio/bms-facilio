<template>
  <div class="facility-availability-container">
    <el-row
      v-for="(record, index) in model.facilityWeekdayAvailability"
      :key="index"
      :gutter="20"
      type="flex"
      align="middle"
      class="mB20"
    >
      <el-col :span="6">
        <el-checkbox v-model="record.isSelected">{{
          $constants.WEEK_DAYS[record.dayOfWeek]
        }}</el-checkbox>
      </el-col>
      <el-col :span="6">
        <el-time-select
          v-model="record.startTime"
          :picker-options="{
            start: '00:00',
            step: '00:15',
            end: '23:45',
            maxTime: record.endTime,
          }"
          class="width100 fc-input-full-border-select2"
        >
        </el-time-select>
      </el-col>
      <el-col :span="6">
        <el-time-select
          v-model="record.endTime"
          :picker-options="{
            start: '00:00',
            step: '00:15',
            end: '23:45',
            minTime: record.startTime,
          }"
          class="width100 fc-input-full-border-select2"
        >
        </el-time-select>
      </el-col>
      <el-col v-if="model.isChargeable" :span="6">
        <el-input
          v-model="record.cost"
          type="number"
          class="fc-input-full-border2 currency-suffix"
        >
          <template v-slot:suffix>
            {{ $currency }}
          </template>
        </el-input>
      </el-col>
    </el-row>

    <div
      v-if="(moduleData || {}).slotGeneratedUpto"
      class="fc-warning-tag-1 flex-middle justify-content-center pT0 pB0 f13"
    >
      <inline-svg
        src="svgs/alert"
        iconClass="icon text-left icon-sm fill-yellow vertical-text-top "
      ></inline-svg>
      <div class="pL10 break-word">
        <ol>
          <li class="pB5">
            {{
              `While editing the working hours, any booking made outside of the designated working hours, the system will automatically cancel it.`
            }}
          </li>
          <li>
            {{
              `If the slot duration is updated, all the bookings will be cancelled by the system automatically.`
            }}
          </li>
        </ol>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: ['model', 'field', 'moduleData'],
}
</script>
