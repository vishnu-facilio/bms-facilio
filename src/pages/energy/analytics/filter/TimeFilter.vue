<template>
  <div class="height400 overflow-y-scroll pB80">
    <div>
      <div>
        <div class="label-txt-black f14 pB5 bold">Filter by Day</div>
        <el-row class="pT10 flex-middle">
          <el-select
            v-model="config.type"
            placeholder="Select"
            class="fc-input-full-border2 time_type"
            @change="typeChange"
          >
            <el-option label="None" value="0"></el-option>
            <el-option label="Weekend" value="1"></el-option>
            <el-option label="Weekdays" value="2"></el-option>
            <el-option label="Custom" value="3"></el-option>
          </el-select>
          <el-checkbox-group
            class="time_chgroup fc-checkbox-group mL20"
            v-model="config.days"
            @change="daysChange"
          >
            <el-checkbox-button
              v-for="(value, key) in daysObj"
              :label="parseInt(key)"
              :key="key"
              >{{ value }}
            </el-checkbox-button>
          </el-checkbox-group>
        </el-row>
      </div>
      <div class="mT40">
        <div class="label-txt-black f14 pB5 bold">Filter by Time</div>
        <div v-if="Object.keys(this.config.time).length > 0">
          <el-row
            v-for="(interval, key) in config.time"
            :key="key"
            class="flex-middle pT10"
          >
            <el-col :span="10">
              <el-select
                v-model="interval[0]"
                placeholder="Select time"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="option in rangeOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                >
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="1">
              <i class="el-icon-minus lable-txt-black pL10 pR10 fwBold"></i>
            </el-col>
            <el-col :span="10">
              <el-select
                v-model="interval[1]"
                placeholder="Select"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="option in rangeOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                >
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="3">
              <div class="flex-middle pL20">
                <img
                  v-if="Object.keys(config.time).length < 3"
                  src="~assets/add-icon.svg"
                  style="height:18px;width:18px;cursor:pointer;"
                  class="delete-icon"
                  @click="addInterval"
                />
                <img
                  src="~assets/remove-icon.svg"
                  style="height:18px;width:18px;margin-left:15px;cursor:pointer;"
                  class="delete-icon"
                  @click="deleteInterval(key)"
                />
              </div>
            </el-col>
          </el-row>
        </div>
        <div v-else>
          <el-button
            type="primary"
            class="setup-el-btn mT10"
            @click="addInterval"
          >
            <i class="el-icon-circle-plus-outline f20 bold pR5 vertical-sub"></i
            >Add Filter</el-button
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import isEqual from 'lodash/isEqual'
export default {
  name: 'TimeFilter',
  props: ['config'],
  data() {
    return {
      daysObj: {
        1: 'Mon',
        2: 'Tue',
        3: 'Wed',
        4: 'Thu',
        5: 'Fri',
        6: 'Sat',
        7: 'Sun',
      },
      monthsObj: {
        1: 'Jan',
        2: 'Feb',
        3: 'Mar',
        4: 'Apr',
        5: 'May',
        6: 'Jun',
        7: 'Jul',
        8: 'Aug',
        9: 'Sep',
        10: 'Oct',
        11: 'Nov',
        12: 'Dec',
      },
      rangeOptions: [
        {
          value: '00:00',
          label: '12:00 AM',
        },
        {
          value: '01:00',
          label: '01:00 AM',
        },
        {
          value: '02:00',
          label: '02:00 AM',
        },
        {
          value: '03:00',
          label: '03:00 AM',
        },
        {
          value: '04:00',
          label: '04:00 AM',
        },
        {
          value: '05:00',
          label: '05:00 AM',
        },
        {
          value: '06:00',
          label: '06:00 AM',
        },
        {
          value: '07:00',
          label: '07:00 AM',
        },
        {
          value: '08:00',
          label: '08:00 AM',
        },
        {
          value: '09:00',
          label: '09:00 AM',
        },
        {
          value: '10:00',
          label: '10:00 AM',
        },
        {
          value: '11:00',
          label: '11:00 AM',
        },
        {
          value: '12:00',
          label: '12:00 PM',
        },
        {
          value: '13:00',
          label: '01:00 PM',
        },
        {
          value: '14:00',
          label: '02:00 PM',
        },
        {
          value: '15:00',
          label: '03:00 PM',
        },
        {
          value: '16:00',
          label: '04:00 PM',
        },
        {
          value: '17:00',
          label: '05:00 PM',
        },
        {
          value: '18:00',
          label: '06:00 PM',
        },
        {
          value: '19:00',
          label: '07:00 PM',
        },
        {
          value: '20:00',
          label: '08:00 PM',
        },
        {
          value: '21:00',
          label: '09:00 PM',
        },
        {
          value: '22:00',
          label: '10:00 PM',
        },
        {
          value: '23:00',
          label: '11:00 PM',
        },
        {
          value: '23:59',
          label: '11:59 PM',
        },
      ],
    }
  },
  methods: {
    typeChange() {
      if (this.config.type == 0) {
        this.config.days = []
      } else if (this.config.type == 1) {
        this.config.days = [6, 7]
      } else if (this.config.type == 2) {
        this.config.days = [1, 2, 3, 4, 5]
      }
    },
    daysChange() {
      let dayLen = this.config.days.length
      let type = '0'
      if (dayLen > 0 && isEqual(this.config.days, [6, 7])) {
        type = '1'
      } else if (dayLen > 0 && isEqual(this.config.days, [1, 2, 3, 4, 5])) {
        type = '2'
      } else {
        type = '3'
      }
      this.$set(this.config, 'type', type)
    },
    addInterval() {
      let key
      let intervalsLength = Object.keys(this.config.time).length
      if (intervalsLength === 0) {
        key = 1
      } else {
        key = Number(Object.keys(this.config.time)[intervalsLength - 1]) + 1
      }
      this.$set(this.config.time, key, ['09:00', '18:00'])
    },
    deleteInterval(key) {
      this.$delete(this.config.time, key)
    },
  },
}
</script>
<style>
.time_chgroup .el-checkbox {
  overflow: hidden;
  padding: 10px 0;
  margin-left: 20px;
}

.time_chgroup .el-checkbox-button__inner {
  border-radius: 5px !important;

  border: 1px solid #d0d9e2 !important;
  padding: 12px 13px !important;
  margin-right: 15px;
}
</style>
