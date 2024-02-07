<template>
  <div>
    <div v-if="showToday" class="vue-cal-header-wrapper">
      <div class="vuecal-top-heading">
        <div class="vue-schedule">{{ 'Schedule' }}</div>
        <div class="vuecal-top-today">
          <el-tooltip
            placement="top"
            trigger="hover"
            content="Go to current booking"
            v-if="goToBooking"
          >
            <el-button
              class="vuecal-today-icon"
              @click="goToCurrentBooking"
              v-if="goToBooking && !isBookingCancel"
            >
              <inline-svg
                src="svgs/agent/currentBooking"
                iconClass="icon text-center icon-md vertical-bottom"
              ></inline-svg>
            </el-button>
          </el-tooltip>

          <el-button
            class="vuecal-today-button mL10"
            @click="$refs.vuecal.switchView('day', new Date())"
            v-if="showToday"
          >
            {{ 'Today' }}
          </el-button>
        </div>
      </div>
    </div>

    <vue-cal
      class="vuecal--facilio--theme"
      ref="vuecal"
      id="vuecal"
      :class="calenderClass"
      :disable-views="['week', 'month', 'year', 'years']"
      hide-view-selector
      active-view="day"
      :events="localEvents"
      :time-cell-height="timeCellHeight"
      :selected-date="selectedDate"
      :timeFormat="timeFormat"
      @ready="afterRenderhook"
      @view-change="updateDayEvents($event, $refs)"
      :on-event-click="onEventClick"
      :editable-events="{
        title: false,
        drag: false,
        resize: false,
        delete: false,
        create: false,
      }"
    >
      <template #event="{ event}">
        <el-popover
          placement="left"
          width="450px"
          trigger="click"
          :close-delay="0"
          :open-delay="0"
          @hide="handleCloseDialog"
        >
          <div style="width:450px;min-height:200px">
            <!-- <div style="height:50px">
              <div slot="title" class="header-items">
                <div v-if="showEdit" class="dialog-header-item">
                  <inline-svg
                    src="svgs/employeePortal/MaterialSymbolsEditOutlineRounded"
                    iconClass="icon text-center icon-md vertical-bottom mB2"
                  ></inline-svg>
                </div>
                <div class="dialog-header-item" style="margin-right:0px">
                  <inline-svg
                    src="svgs/employeePortal/MaterialSymbolsDeleteOutlineRounded"
                    iconClass="icon text-center icon-md vertical-bottom mB2"
                  ></inline-svg>
                </div>
              </div>
            </div> -->
            <SpaceBookingOverviewDialog
              v-if="showDialog"
              :moduleName="moduleName"
              :bookingId="bookingId"
              :selectedEvent="selectedEvent"
              @close="handleCloseDialog"
            ></SpaceBookingOverviewDialog>
          </div>
          <div
            class="event-object"
            slot="reference"
            :class="reSizeEventBox(event)"
          >
            <div class="vuecal__event-title" v-html="event.title" />

            <div class="vuecal__event-time">
              <span>{{ event.start.formatTime(eventTimeFormat) }}</span
              >{{ ' to ' }}
              <span>{{ event.end.formatTime(eventTimeFormat) }}</span>
            </div>
          </div>
        </el-popover>
      </template>
    </vue-cal>
  </div>
</template>

<script>
import VueCal from 'vue-cal'
import 'vue-cal/dist/vuecal.css'
import { API } from '@facilio/api'
import moment from 'moment-timezone'
import { deepCloneObject } from 'util/utility-methods'
import SpaceBookingOverviewDialog from 'src/components/SpaceBookingOverviewDialog.vue'
import Constants from 'util/constant'

export default {
  props: [
    'startHour',
    'startDate',
    'endDate',
    'selectDate',
    'spaceId',
    'moduleName',
    'spaceCategory',
    'showToday',
    'goToBooking',
    'bookingHour',
    'bookingDate',
    'events',
    'height',
    'isBookingCancel',
  ],
  components: { VueCal, SpaceBookingOverviewDialog },
  data() {
    return {
      space: null,
      isLoading: false,
      timeCellHeight: 40,
      pointDate: null,
      localEvents: [],
      days: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
      selectedDate: null,
      booking: null,
      showPopup: false,
      bookingId: null,
      selectedEvent: null,
      showDialog: false,
      calenderClass: 'vuecal--facilio--theme',
    }
  },
  mounted() {
    this.selectedDate = deepCloneObject(this.selectDate)
    this.init()
  },
  computed: {
    calHeight() {
      return this.height || 400
    },
    getCurrentTime() {
      return this.$helpers.getOrgMoment().valueOf()
    },
    timeFormat() {
      if (this.$timeformat === 'hh:mm A') {
        return 'h {am}'
      }
      return 'HH:mm'
    },
    eventTimeFormat() {
      if (this.$timeformat === 'hh:mm A') {
        return 'hh:mm {am}'
      }
      return 'HH:mm'
    },
    showEdit() {
      let { $org, $hasPermission } = this
      let { isCustomModulePermissionsEnabled: orgIdsToCheck } = Constants
      let canShowEdit = orgIdsToCheck($org.id)
        ? $hasPermission(`${this.moduleName}:UPDATE`)
        : true
      return canShowEdit
    },
  },
  methods: {
    reSizeEventBox(event) {
      let { startTimeMinutes, endTimeMinutes } = event || null
      let diff = endTimeMinutes - startTimeMinutes
      if (diff <= 15) {
        this.calenderClass = 'line-div'
      } else if (diff >= 15 && diff <= 30) {
        this.calenderClass = 'multyline-div'
      }
    },
    handleCloseDialog() {
      this.showDialog = false
    },
    async init() {
      if (this.$timeformat === 'hh:mm A') {
        this.timeformat = 'h:mm'
      }
      await this.loadSpaceBookingRecords(
        this.startDate,
        this.endDate,
        this.spaceId
      )
    },
    afterRenderhook() {
      this.scrollToElement()
    },
    scrollToElement() {
      let calendar = document.getElementsByClassName('vuecal__bg')[0]
      calendar.scrollTo({
        top: this.startHour * this.timeCellHeight,
        behavior: 'smooth',
      })
    },
    updateDayEvents(event, ref) {
      let start = new Date(event.startDate)
      this.selectedDate = start.format('YYYY-MM-DD')
      let end = new Date(event.endDate)
      ref.vuecal.switchView('day', new Date(start.getTime()))
      let startDate = start.getTime()
      let endDate = end.getTime()
      this.loadSpaceBookingRecords(startDate, endDate, this.spaceId)
    },
    async loadSpaceBookingRecords(startTime, endTime, spaceId) {
      try {
        this.isLoading = true
        let { moduleName } = this
        let url = `/v3/modules/data/list`
        let viewname = 'all'
        let filters = JSON.stringify({
          bookingEndTime: {
            operatorId: 18,
            value: [String(endTime + 1)],
          },
          bookingStartTime: {
            operatorId: 19,
            value: [String(startTime - 1)],
          },
          space: { operatorId: 36, value: [String(spaceId)] },
          isCancelled: { operatorId: 15, value: ['false'] },
        })
        let params = {
          moduleName,
          viewname,
          filters,
          page: 1,
          perPage: 50,
          withCount: false,
        }
        let { data, error } = await API.get(url, params)
        if (error) {
          let { message } = error
          this.$message.error(message || 'Error Occured')
        } else {
          this.bookings = data.spacebooking
          this.generateEvents()
          this.isLoading = false
        }
      } catch (error) {
        this.$message.error(error)
      }
    },
    generateEvents() {
      this.localEvents = []
      this.bookings.forEach(booking => {
        let obj = {}
        let startDate = new Date(booking.bookingStartTime)
        let endDate = new Date(booking.bookingEndTime)
        this.$set(obj, 'start', startDate.format('YYYY-MM-DD HH:mm'))
        this.$set(obj, 'end', endDate.format('YYYY-MM-DD HH:mm'))
        this.$set(obj, 'title', booking.name)
        this.$set(obj, 'bookingId', booking.id)
        this.$set(obj, 'split', 1)
        this.$set(obj, 'color', '#0053cc')
        if (booking.isCancelled) {
          this.$set(obj, 'class', 'cancel-booking')
          this.$set(obj, 'color', '#ff6666e6')
        }
        if (booking.moduleState.status === 'checkedIn') {
          this.$set(obj, 'class', 'check-in')
          this.$set(obj, 'color', '#1cdb98e6')
        }
        this.localEvents.push(obj)
        if (this.events?.size > 0) {
          this.localEvents.push(this.events[0])
        }
      })
    },
    goToCurrentBooking() {
      let start = new Date(this.bookingDate)
      this.selectedDate = start.format('YYYY-MM-DD')
      this.$refs.vuecal.switchView('day', new Date(start.getTime()))
      let startDate = moment(this.bookingDate)
        .startOf('day')
        .valueOf()
      let endDate = moment(this.bookingDate)
        .endOf('day')
        .valueOf()
      this.loadSpaceBookingRecords(startDate, endDate, this.spaceId)
      let calendar = document.getElementsByClassName('vuecal__bg')[0]
      calendar.scrollTo({
        top: this.bookingHour * this.timeCellHeight,
        behavior: 'smooth',
      })
    },
    onEventClick(event, e) {
      this.selectedEvent = event
      let id = event.bookingId
      this.bookingId = id
      this.showDialog = true
      // Prevent navigating to narrower view (default vue-cal behavior).
      e.stopPropagation()
    },
    createBooking(event) {
      let { start, end } = event
      let obj = {
        bookingStartTime: start,
        bookingEndTime: end,
      }
      this.$emit('book', obj)
    },
  },
}
</script>

<style lang="scss">
.vue-cal-header-wrapper {
  height: 45px;
  .vuecal-top-heading {
    background-color: #fff;
    text-align: center;
    height: inherit;
    display: flex;
    .vue-schedule {
      color: #334056;
      padding: 0 15px;
      font-size: 14px;
      font-weight: 500;
      display: flex;
      align-items: center;
      letter-spacing: 0.5px;
    }
    .vuecal-top-today {
      width: 100%;
      padding: 0 15px;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      .vuecal-today-button {
        color: #0053cc;
        border: 1px solid #0053cc;
        font-size: 13px;
        padding: 0px 8px;
        height: 24px;
        border-radius: 3px;
        background-color: #fff;
        cursor: pointer;
        &:hover {
          background-color: #0053cc;
          color: #fff;
        }
      }
      .vuecal-today-icon {
        color: #0053cc;
        border: none;
        font-size: 13px;
        padding: 0px;
        height: 24px;
        border-radius: 3px;
        background-color: #fff;
        cursor: pointer;
      }
    }
  }
}
.vuecal--facilio--theme {
  font-size: 12px;
  background-color: #fff;
  .vuecal__menu {
    background-color: rgb(239 239 254);
  }
  .vuecal__cell-events-count {
    background-color: rgb(239 239 254);
  }
  .vuecal__title-bar {
    background-color: rgb(222 236 255);
    min-height: 3em;
  }
  // .vuecal__body {
  //   background-color: rgba(226, 242, 253, 0.7);
  // }
  .vuecal__cell--today,
  .vuecal__cell--current {
    background-color: #ffffff70;
  }
  .vuecal:not(.vuecal--day-view) .vuecal__cell--selected {
    background-color: #0053cc;
  }
  .vuecal__cell--selected:before {
    background-color: #ffffff70;
  }
  /* Cells and buttons get highlighted when an event is dragged over it. */
  .vuecal__cell--highlighted:not(.vuecal__cell--has-splits),
  .vuecal__cell-split--highlighted {
    background-color: rgb(239 239 254);
  }
  .vuecal__arrow.vuecal__arrow--highlighted,
  .vuecal__view-btn.vuecal__view-btn--highlighted {
    background-color: rgb(239 239 254);
  }
  .vuecal__event {
    background-color: #0053cc;
    color: #fff;
    border-radius: 4px;
    border: 12px #fff;
    font-size: 12px;
  }

  .vuecal__cell-content {
    margin-left: 2px;
    width: 90%;
  }
  .vuecal__now-line {
    color: #0053cc;
  }
  .vuecal__title {
    font-size: 14px;
    font-weight: 500;
    color: #324056;
  }
  .vuecal__time-column {
    width: 4.2em;
  }
  .angle {
    color: #0053cc;
  }
  .vuecal__event-title {
    text-align: left;
    font-size: 12px;
    font-weight: 500;
    margin-bottom: 5px;
  }
  .vuecal__event-time {
    text-align: left;
    font-size: 10px;
  }
  .vuecal__time-cell-label {
    font-size: 11px;
    color: #334056;
    text-transform: uppercase;
    position: relative;
    top: 6px;
    right: 3px;
  }
  .vuecal__event.cancel-booking {
    background-color: rgba(255, 102, 102, 0.9);
    border: 1px solid rgb(235, 82, 82);
    color: #fff;
    border-radius: 4px;
    font-size: 12px;
  }
  .vuecal__event.check-in {
    background-color: rgba(28, 219, 152, 0.9);
    border: 1px solid rgb(24, 210, 161);
    color: #fff;
    border-radius: 4px;
    font-size: 12px;
  }
  .vuecal__now-line {
    color: #ee518f;
  }
}
.line-div {
  .event-object {
    flex-direction: row !important;
    justify-content: flex-start !important;
    padding: 0 10px !important;
  }
  .vuecal__event-title {
    line-height: 14px;
    margin: 0 15px 0 0;
  }
  .vuecal__event {
    background-color: #0053cc;
    color: #fff;
    border-radius: 4px;
    border: 12px #fff;
    font-size: 12px;
    height: 15px !important;
  }
  .vuecal__event-time {
    display: flex;
    align-items: center;
  }
}
.multyline-div {
  .event-object {
    padding: 0 10px !important;
  }
  .vuecal__event-title {
    margin-bottom: 2px;
  }
  .vuecal__event {
    background-color: #0053cc;
    color: #fff;
    border-radius: 4px;
    border: 12px #fff;
    font-size: 12px;
    height: 30px !important;
  }
}
.vuecal__time-cell {
  font-size: 12px;
  color: #334056;
}
.vuecal-date-header {
  background-color: #5d61ff;
  height: 30px;
  width: 30px;
  border-radius: 15px;
  padding: 5px;
  color: #fff;
  font-size: 1rem;
}
.vuecal-heading-text {
  text-align: left;
  font-size: 14px;
  font-weight: 600;
  padding: 0px 6px 0px 20px;
  line-height: 22px;
}
.vuecal-sub-heading {
  font-size: 16;
  font-weight: 400;
}

.event-object.el-popover__reference {
  display: flex;
  flex-direction: column;
  justify-content: center;
  cursor: pointer;
  padding: 5px 10px;
}
</style>
