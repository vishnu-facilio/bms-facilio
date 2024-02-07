<template>
  <div class="shift-planner">
    <facilio-resource-planner
      :views="['WEEK', 'MONTH']"
      defaultView="MONTH"
      v-bind:isDragDropAllowed="false"
      @viewChanged="handleViewChange"
      ref="planner"
      v-bind:allowUserSettings="false"
      :defaultSettings="getShiftPlannerSettings()"
      noDataText="No Shifts "
    >
      <template v-slot:taskContent="eventProps">
        <div
          :class="[{ holiday: isHoliday(eventProps.event) }]"
          class="rp-shift"
          :style="{
            'background-color': getShift(eventProps.event.shiftID).colorCode,
          }"
        >
          <div class="shft-name">
            {{ getShift(eventProps.event.shiftID).name }}
          </div>
          <div class="shft-time">
            {{
              getShift(eventProps.event.shiftID).startTime +
                ' - ' +
                getShift(eventProps.event.shiftID).endTime
            }}
          </div>
        </div>
      </template>
      <!-- Planner top left cell is a slot -->
      <template slot="resourceSelection">
        <div
          class="width200 fc-input-full-border2 mR10 fc-input-border-remove fc-input-full-border2-bold mL8"
        >
          {{ $t('common.header.employee') }}
        </div>
      </template>
      <template v-slot:card="eventProps">
        <div class="user-shift-card">
          <el-select
            @change="reAssignShift(eventProps.event, $event)"
            v-model="eventProps.event.shiftID"
          >
            <el-option
              v-for="(shift, index) in shiftList"
              :key="index"
              :label="shift.name"
              :value="shift.id"
            ></el-option>
          </el-select>
        </div>
      </template>
    </facilio-resource-planner>
  </div>
</template>

<script>
import FacilioResourcePlanner from 'pages/workorder/FacilioResourcePlanner'
import http from 'src/util/http'
import { mapState } from 'vuex'

export default {
  components: {
    'facilio-resource-planner': FacilioResourcePlanner,
  },
  created() {
    this.$store.dispatch('loadUsers')
    this.$store.dispatch('loadShifts')
    this.getShiftList().then(data => {
      this.shiftList = data
    })
  },
  data() {
    return {
      staffList: this.getStaffList(),
      shiftList: [],
      id: 0,
      shiftUserMapping: [],
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
  },
  methods: {
    getShiftPlannerSettings() {
      return {
        viewSettings: {
          MONTH: {
            displayType: 'dateTime',
            gridLines: 'DAY',
            grouping: 'NONE',
          },
          YEAR: {
            displayType: 'dateTime',
            gridLines: 'WEEK',
            grouping: 'MONTH',
          },
          WEEK: { displayType: 'dateTime', gridLines: 'DAY', grouping: 'NONE' },
          DAY: { displayType: 'dateTime', gridLines: '1H', grouping: '4H' },
        },
        moveType: 'single', //just pass everything for now
      }
    },
    getShift(id) {
      return this.shiftList.find(e => {
        return e.id == id
      })
    },

    idGen() {
      return ++this.id
    },
    getStaffList() {
      let staffList = [
        ...this.users.map(user => ({ id: user.id, title: user.name })),
      ]
      staffList.sort((e1, e2) => {
        return e1.id - e2.id
      })
      return staffList
    },
    async getShiftList() {
      let url = '/v2/shift/list'
      try {
        let resp = await http.get(url)
        return resp.data.result.shifts.map(e => {
          return {
            id: e.id,
            name: e.name,
            startTime: this.$helpers.get12HrTime(e.startTime * 1000, 'hh:mm a'),
            endTime: this.$helpers.get12HrTime(e.endTime * 1000, 'hh:mm a'),
            weekendJSON: e.weekendJSON,
            colorCode: e.colorCode,
          }
        })
      } catch (e) {
        console.log('failed ', url)
        throw e
      }
    },

    async getUserShifts(startDate, endDate) {
      let url = 'v2/shift/shiftUserMapping'
      let reqJSON = {
        startDate,
        endDate,
      }
      try {
        let resp = await http.post(url, reqJSON)
        return resp.data.result.shiftUserMapping.map(e => {
          return {
            id: e.id,
            staff: e.ouid,
            start: e.startTime,
            end: e.endTime,
            shiftID: e.shiftId,
          }
        })
      } catch (e) {
        console.log('failed ', url)
        throw e
      }
    },
    //for now we support only day
    getMilliseconds(interval) {
      switch (interval) {
        case 'DAY':
          return 24 * 60 * 60 * 1000
      }
    },
    reAssignShift(event, newShiftID) {
      console.log('reassigning ', event, newShiftID)
      this.reAssignShiftAPI(event, newShiftID).then(resp => {
        console.log(resp)
      })
    },
    async reAssignShiftAPI(event, newShiftID) {
      let url = '/v2/shift/addShiftUserMapping'

      let reqJSON = {
        orgUserId: event.staff,
        startDate: event.start,
        endDate: event.end,
        id: newShiftID,
      }
      let resp = await this.$http.post(url, reqJSON)
      return resp.data
    },

    handleViewChange(e) {
      this.getUserShifts(e.start, e.end).then(data => {
        this.setUserShiftData(data, this.getMilliseconds(e.interval))
        this.shiftUserMapping.sort((e1, e2) => {
          return e1.start - e2.start
        })
        this.$refs['planner'].renderPlanner(
          this.shiftUserMapping,
          this.staffList,
          'staff',
          true
        )
      })
    },
    setUserShiftData(data, interval) {
      this.shiftUserMapping = []
      //shift user mapping data format is , jun1-jun15 ->day shift ,
      //Planner requires jun1->shift,June2->shift ...so for shifts with range greater than interval(single day )
      //split into multiple event objects

      data.forEach(userShiftObj => {
        if (userShiftObj.end - userShiftObj.start < interval) {
          userShiftObj.id = this.idGen()
          this.shiftUserMapping.push(userShiftObj)
        } else {
          this.shiftUserMapping.push(
            ...this.splitShiftObject(userShiftObj, interval)
          )
        }
      })
    },
    splitShiftObject(userShiftObj, interval) {
      let splitShifts = []
      for (let i = userShiftObj.start; i < userShiftObj.end; i += interval) {
        splitShifts.push({
          id: this.idGen(),
          start: i, //start time set to start of end
          end: i + interval - 1, //end time to last millisecond in same day,
          shiftID: userShiftObj.shiftID,
          staff: userShiftObj.staff,
        })
      }
      return splitShifts
    },
    isHoliday(event) {
      let shift = this.getShift(event.shiftID)
      let holidayJson = shift.weekendJSON

      let weekNo = this.$helpers.weekOfMonth(event.start)
      let dayOfWeek = this.$helpers.getOrgMoment(event.start).isoWeekday()

      //1 monday,2 tues ...always
      if (holidayJson[weekNo]) {
        if (holidayJson[weekNo].indexOf(dayOfWeek) > -1) {
          console.log('its an holiday')
          return true
        }
      }
      return false
    },
  },
}
</script>

<style lang="scss">
.shift-planner {
  height: 100%;
  width: 100%;
  //Temp fix , do not do like this
  .facilio-resource-planner {
    .pm-fullscreen-btn {
      padding-left: 9px !important;
      padding-right: 9px !important;
      border-left: 1px solid rgb(217, 224, 231) !important;
    }
    .table-container {
      height: calc(100vh - 200px);
    }
    .rp-cell {
      min-width: 155px;
      min-height: 75px;
      height: 0 !important;
      //for shift planner only one task per cell always and cell heights are always same
      .rp-task {
        width: 100%;
        height: 100%;

        .rp-shift {
          width: 100%;
          height: 100%;
          padding: 15px;
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          &.holiday {
            // background: #d9d9d9;
          }

          .shft-name {
            width: 100px;
            text-align: center;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            font-size: 13px;
            letter-spacing: 0.4px;
            font-weight: 500;
            color: #324056;
          }
          .shft-time {
            color: #8ca1ad;
            margin-top: 3px;
            letter-spacing: 0.3px;
            font-size: 11px;
          }
        }
      }
    }
  }
  .user-shift-card {
    width: 440px;
    height: 300px;
    box-shadow: 0 6px 23px 0 rgba(21, 18, 51, 0.09);
    background-color: #ffffff;
  }
}
</style>
