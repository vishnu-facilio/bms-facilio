<template>
  <el-card class="box-card fc-recently-received-card fc-visit-card">
    <div class="fc-visit-header">
      <inline-svg
        src="svgs/employeePortal/mybooking"
        iconClass="icon text-center icon-md vertical-bottom"
      ></inline-svg>
      <div class="fc-portal-title-txt-14 fw5">
        Plan your next Visit
      </div>
    </div>
    <div class="fc-visit-body">
      <el-row>
        <el-col
          :span="9"
          class="fc-portal-border-right fc-card-padding-visit-body"
        >
          <div class="fc-grey-10">Date</div>
          <div class="fc-portal-txt-16 fw5 fw5 pT5 date-picker-container">
            <FDatePicker
              v-bind:hideClear="true"
              v-model="date"
              :type="'date'"
              class="date-picker-lol"
              @change="dateChanged"
            ></FDatePicker>
          </div>
        </el-col>
        <el-col :span="15" class="fc-card-padding-visit-body">
          <div class="fc-grey-10">Time</div>
          <div class="fc-portal-txt-16 fw5 fw5 pT5 d-flex">
            <!-- <TimeRangePicker v-model="timeRange" @change="dateChanged">
            </TimeRangePicker> -->
            <div class="time-range-picker-container">
              <el-time-select
                class="start-time-picker"
                placeholder="Start time"
                v-model="timeRange.timeMillis[0]"
                :picker-options="{
                  start: '00:00',
                  step: '00:30',
                  end: '23:30',
                }"
              >
              </el-time-select>
              <el-time-select
                class="end-time-picker"
                placeholder="End time"
                v-model="timeRange.timeMillis[1]"
                :picker-options="{
                  start: '00:00',
                  step: '00:30',
                  end: '23:30',
                  minTime: timeRange.timeMillis[0],
                }"
              >
              </el-time-select>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    <div class="fc-visit-footer">
      <el-button @click="findSpace" class="fc-visit-footer-btn">
        Find Space
      </el-button>
    </div>
  </el-card>
</template>
<script>
import FDatePicker from 'pages/assets/overview/FDatePicker'
import moment from 'moment'
import { findRouteForTab, tabTypes, getApp } from '@facilio/router'
import { API } from '@facilio/api'
import baseWidget from 'src/components/homepage/HomepageWidgetComponents/BaseWidget.vue'
export default {
  extends: baseWidget,
  components: {
    FDatePicker,
  },
  data() {
    return {
      timeRange: {
        timeMillis: ['9:00', '17:00'], //default 9AM-5PM
        timeStrings: ['09:00 : AM', '05:00 : PM'],
      },
      date: moment()
        .startOf('day')
        .valueOf(),
      floorId: null,
      floorplanId: null,
    }
  },
  mounted() {
    this.getFirstActivePlanFromFloorList()
  },
  methods: {
    toMillis(val) {
      let millis =
        Number(val.split(':')[0]) * 60 * 60 * 1000 +
        Number(val.split(':')[1]) * 60 * 1000
      return millis
    },
    dateChanged() {
      this.$emit('input', {
        startTime: this.date + this.timeRange.timeMillis[0],
        endTime: this.date + this.timeRange.timeMillis[1],
      })
      this.$emit('change', {
        startTime: this.date + this.timeRange.timeMillis[0],
        endTime: this.date + this.timeRange.timeMillis[1],
      })
    },
    findSpace() {
      let { path } = findRouteForTab(tabTypes.INDOOR_FLOORPLAN)
      // 'floorplan/floor-map/indoorfloorplan/:floorId?/:floorplanid?'
      if (path) {
        path = path.replaceAll(':floorId?', this.floorId)
        path = path.replaceAll(':floorplanid?', this.floorplanId)
      }
      console.log('path', path)
      this.$router.push({
        path: `/employee/${path}`,
        query: {
          viewMode: 'BOOKING',
          date: this.date,
          startTime: this.toMillis(this.timeRange.timeMillis[0]),
          endTime: this.toMillis(this.timeRange.timeMillis[1]),
        },
      })
    },
    async getFirstActivePlanFromFloorList() {
      //get first floor with an  active floor plan
      let params = {
        viewName: 'all',
        page: 1,
        perPage: 1,
        moduleName: 'floor',
        filters: JSON.stringify({
          indoorFloorPlanId: {
            operatorId: 2,
            value: [],
          },
        }),
      }
      let { data, error } = await API.get('v2/module/data/list', params)

      if (!error) {
        let floorPlanIdFromList = data.moduleDatas[0].indoorFloorPlanId
        let floor = data.moduleDatas[0]
        this.floorId = floor.id
        this.floorplanId = floorPlanIdFromList
      } else {
        this.$error.message('Error loading all floors', error)
      }
      this.loading = false
    },
  },
}
</script>
<style lang="scss">
.time-range-picker-container {
  display: flex;
  width: 100%;
  justify-content: space-between;
}
.time-range-picker-container .el-input .el-input__inner {
  height: 40px;
  font-family: 'Aktiv-Grotesk', Helvetica, Arial, sans-serif;
  font-size: 16px;
  font-weight: 500;
  color: #464646;
  border: none;
  border: solid 1px transparent;
  cursor: pointer;
  &:hover {
    border-color: #0053cc !important;
  }
}
.date-picker-container .el-input .el-input__inner {
  height: 40px;
  width: 170px;
  font-family: 'Aktiv-Grotesk', Helvetica, Arial, sans-serif;
  font-size: 16px;
  font-weight: 500;
  color: #464646;
  border: none;
  border: solid 1px transparent;
  cursor: pointer;

  &:hover {
    border-color: #0053cc !important;
  }
}
</style>
