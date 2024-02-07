<template>
  <div
    class="facilio-calendar"
    :class="[{ 'full-screen': isFullScreen }]"
    ref="resourceCalendar"
  >
    <div class="fc-calendar-top-bar white-bg inline-flex" v-if="showTopbar">
      <div class="inline-flex" style="width: 41%;">
        <slot name="triggerSelection"></slot>
        <el-button
          @click="todayClicked"
          class="mL10 fc-btn-grey-border35 pL20 pR20"
          >Today</el-button
        >
      </div>
      <div class="fc-text-pink bold flex-middle" style="width: 20%;">
        <new-date-picker
          v-if="showPicker"
          :dateObj="pickerObj"
          @date="navigateFromPicker"
          :zone="timeZone"
          :tabs="datePickerTabs()"
          class="facilio-resource-date-picker"
        ></new-date-picker>
      </div>

      <!-- Do not change the label , radio group does not provide a value and label , only label -->
      <div>
        <!-- <div class="" style="width: 31%;"> -->
        <el-radio-group
          v-model="currentView"
          @change="handleViewTabClick($event)"
          class="mR20 fc-pm-summary-planner-radio"
        >
          <el-radio-button
            v-for="(view, index) in views"
            :label="view"
            :key="index"
          >
            <div>{{ viewState[view].displayName }}</div>
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <div class="grid-container white-bg" v-if="!loading">
      <div class="fc-cal-header-row">
        <div
          v-for="(weekDay, index) in daysInWeek"
          :key="index"
          class="fc-cal-header-cell"
        >
          {{ weekDay }}
        </div>
      </div>
      <div class="fc-cal-body">
        <div
          v-for="(col, index) in gridColumns"
          :key="index"
          class="fc-cal-cell"
          :style="cellStyle"
          :class="[
            {
              'current-date': currentDateCol
                ? currentDateCol.index == col.index
                : false,
            },
          ]"
        >
          <div class="fc-cal-cell-m-date">
            <div
              class="fc-cal-cell-m-date-text"
              :class="[
                {
                  'prev-month':
                    col.start < currentViewState.start ||
                    col.start > currentViewState.end,
                },
              ]"
            >
              {{ col.label }}
            </div>
          </div>
          <div class="fc-cal-cell-body">
            <div
              v-for="(task, index) in col.tasks"
              :key="index"
              class="fc-task"
            >
              <slot name="taskContent" :event="task"> </slot>
            </div>
          </div>
        </div>
      </div>
    </div>
    <spinner v-else :show="loading" width="50" height="50"></spinner>
  </div>
</template>

<script>
import CalendarMixin from './CalendarMixin'
import NewDatePicker from '@/NewDatePicker'

export default {
  mixins: [CalendarMixin],
  props: {
    showTopbar: {
      type: Boolean,
      default() {
        return true
      },
    },
  },
  components: {
    NewDatePicker,
  },

  data() {
    return {}
  },

  watch: {},

  methods: {
    renderCalendar(taskList) {
      this.taskList = taskList
      this.generateColumnData()
      this.spreadTasksToColumns()
      this.loading = false
    },
    //dummy methods required by mixin
    clearAllSelections() {},
    scrollColToView() {},
    formatLabel() {},
  },
  computed: {
    cellStyle() {
      return {
        height: `calc( 100% / ${this.groupColumns.length} )`,
      }
    },
  },
}
</script>

<style lang="scss">
.facilio-calendar {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;

  .fc-calendar-top-bar {
    justify-content: space-between;
    padding: 20px 20px 10px;
  }

  .facilio-resource-date-picker .cal-left-btn,
  .facilio-resource-date-picker .cal-right-btn {
    padding: 0 !important;
  }
  .facilio-resource-date-picker .button-row .el-button {
    height: 36px !important;
  }
  // .fc-calendar-top-bar
  // {
  //   height:80px;
  // }

  .grid-container {
    //height: calc(100% - 80px);
    display: flex;
    flex-grow: 1;
    flex-direction: column;
  }
  .fc-cal-cell {
    border-right: 1px solid #f1f2f3;
    border-bottom: 1px solid #f1f2f3;
    display: flex;
    flex-direction: column;
    width: calc(100% / 7);
    &.current-date {
      background: #f3f6f9;
      // .fc-cal-cell-m-date-text{
      //   background: #ff3184;
      //   border-radius:50%;
      //   color: #ffffff;
      //   font-weight: bold;
      // }
    }
    // height: 50px;
  }
  .fc-cal-cell-m-date {
    display: flex;
    justify-content: flex-end;

    .fc-cal-cell-m-date-text {
      font-size: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-top: 7px;
      margin-right: 8px;
      //background: #ffffff;

      width: 23px;
      height: 23px;

      letter-spacing: 0.4px;
      //text-align: right;
      color: #324056;
      font-size: 13px;
      font-weight: 500;
      &.prev-month {
        color: #bfc3ca;
      }
    }
  }
  .fc-cal-header-row {
    width: 100%;
    height: 50px;
    display: flex;
    border-top: 1px solid #f1f2f3;
    border-bottom: 1px solid #f1f2f3;
  }
  .fc-cal-header-cell {
    text-transform: uppercase;
    // flex-grow: 1;
    width: calc(100% / 7);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 11px;
    font-weight: bold;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 1px;
    color: #324056;
  }
  .fc-cal-body {
    display: flex;
    flex-grow: 1;
    flex-direction: row;
    flex-wrap: wrap;
  }
  .fc-cal-cell-body {
    flex-grow: 1;
  }
}
</style>
