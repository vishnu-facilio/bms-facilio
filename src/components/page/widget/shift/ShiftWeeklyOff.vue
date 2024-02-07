<template>
  <div class="box">
    <div class="label">{{ $t('common.shift.weekly_off') }}</div>
    <table style="width: 100%">
      <tr class="weeks-header">
        <td v-for="week in weeks" :key="week">{{ week }}</td>
      </tr>
      <tr v-for="(d, ix) in config" :key="d.day + ix">
        <td>{{ d.day }}</td>
        <td
          v-for="(isDayOff, jx) in d.conf"
          :key="d.day + isDayOff + jx"
          class="weekly-off-row"
        >
          <div v-if="isDayOff" class="strike-mark">
            <center>{{ $t('common.shift.mark') }}</center>
          </div>
        </td>
      </tr>
    </table>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['details'],
  name: 'WeeklyOff',
  data() {
    return {
      weeks: ['', '1ST', '2ND', '3RD', '4TH', '5TH'],
      emptyConfig: [
        { day: 'SUN', conf: [false, false, false, false, false] },
        { day: 'MON', conf: [false, false, false, false, false] },
        { day: 'TUE', conf: [false, false, false, false, false] },
        { day: 'WED', conf: [false, false, false, false, false] },
        { day: 'THU', conf: [false, false, false, false, false] },
        { day: 'FRI', conf: [false, false, false, false, false] },
        { day: 'SAT', conf: [false, false, false, false, false] },
      ],
    }
  },
  computed: {
    widgetTitle() {
      return 'WeeklyOff'
    },
    shift() {
      return this.details
    },
    config() {
      let { weekend } = this.shift
      if (!isEmpty(weekend)) {
        const shiftWeeklyOff = JSON.parse(this.shift.weekend)
        let daysOffMatrix = { ...this.emptyConfig }

        for (let weekNumber in shiftWeeklyOff) {
          const daysOffInTheWeek = shiftWeeklyOff[weekNumber]

          for (const day of daysOffInTheWeek) {
            daysOffMatrix[day - 1].conf[weekNumber - 1] = true
          }
        }

        return daysOffMatrix
      }
      return this.emptyConfig
    },
  },
}
</script>

<style scoped>
table {
  color: #b5b5b7;
  font-size: 1.1em;
}

.legend {
  color: black;
}
.label {
  margin-bottom: 20px;
  font-size: 1.1em;
}

.box {
  padding: 20px;
}

.weeks-header > td:not(:first-child) {
  border-bottom: 1px solid #e9eef4;
}

.weekly-off-row {
  border-bottom: 1px solid #e9eef4;
}
.strike-mark {
  background-color: #eafdff;
  color: #39b2c2;
  font-weight: 400;
  height: 20px;
  width: 20px;
  border-radius: 50%;
  text-align: center;
}

td {
  padding: 5px;
}
</style>
