<template>
  <div>
    <div
      v-if="canShowCalendarView"
      class="d-flex justify-content-space align-center pB20"
    >
      <el-tabs
        v-model="visualType"
        class="width100 visual-type"
        @tab-click="switchListandCalendar"
      >
        <el-tab-pane :disabled="!isListView" name="list">
          <span slot="label" class=" visual-tab">
            <fc-icon
              group="action"
              name="option"
              size="16"
              :color="[!isListView && '#c0c4cc']"
            ></fc-icon>
            {{ $t('viewsmanager.visualization_type.list') }}</span
          >
        </el-tab-pane>
        <el-tab-pane :disabled="!isCalendarView" name="calendar">
          <span slot="label" class="visual-tab">
            <fc-icon
              group="dsm"
              name="calendar"
              size="16"
              :color="[!isCalendarView && '#c0c4cc']"
            ></fc-icon>
            {{ $t('viewsmanager.visualization_type.calendar') }}</span
          >
        </el-tab-pane>
      </el-tabs>
      <el-dropdown :hide-on-click="false" trigger="hover">
        <span class="el-dropdown-link pointer">
          <fc-icon
            group="default"
            name="ellipsis-vertical"
            :color="isIconActive(isCalendarView)"
          ></fc-icon>
        </span>
        <el-dropdown-menu slot="dropdown" style="width:200px;">
          <el-dropdown-item>
            <div class="d-flex justify-content-space align-center">
              {{ $t('viewsmanager.visualization_type.list') }}
              <el-switch
                v-model="isListView"
                :disabled="!isCalendarView"
                @change="toggleVisual('list')"
              >
              </el-switch></div
          ></el-dropdown-item>
          <el-dropdown-item
            ><div class="d-flex justify-content-space align-center">
              {{ $t('viewsmanager.visualization_type.calendar') }}
              <el-switch
                v-model="isCalendarView"
                :disabled="!isListView"
                @change="toggleVisual('calendar')"
              ></el-switch></div
          ></el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <CustomizeColumns
      v-show="showList"
      ref="custcolm-section"
      :saveAsNew="saveAsNew"
      :isNewView="isNewView"
      :viewDetail="viewDetail"
      :moduleName="moduleName"
    />

    <template v-if="canShowCalendarView">
      <Calendar
        v-show="!showList"
        ref="calendar-section"
        :moduleName="moduleName"
        :viewDetail="viewDetail"
        class="tab-height"
      ></Calendar>

      <div v-show="showList">
        <div class="d-flex mT20 pB10">
          <span class="f14 bold">{{
            $t('viewsmanager.sorting.heading_name')
          }}</span>
          <el-switch
            v-model="isSortingEnable"
            active-color="rgba(57, 178, 194, 0.8)"
            inactive-color="#e5e5e5"
            class="pL20"
          ></el-switch>
        </div>
        <!-- sorting handled here for licensed enabled  -->
        <Sorting
          v-if="isSortingEnable"
          ref="sorting-section"
          :moduleName="moduleName"
          :viewDetail="viewDetail"
        />
      </div>
    </template>
  </div>
</template>

<script>
import Calendar from './Calendar.vue'
import CustomizeColumns from './CustomizeColumns.vue'
import Sorting from './Sorting.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: [
    'viewDetail',
    'isNewView',
    'moduleName',
    'saveAsNew',
    'canShowCalendarView',
  ],
  components: { Calendar, CustomizeColumns, Sorting },
  data() {
    return {
      isListView: false,
      isCalendarView: false,
      visualType: '',
      showList: true,
      isSortingEnable: false,
    }
  },
  created() {
    if (!isEmpty(this.viewDetail)) {
      this.isListView = this.viewDetail?.listView
      this.isCalendarView = this.viewDetail?.calendarView

      if (this.isListView || (!this.isListView && !this.isCalendarView)) {
        this.showList = true
        this.visualType = 'list'
        this.computeSort()
      } else {
        this.showList = false
        this.visualType = 'calendar'
      }
    }
    if (this.isNewView) {
      this.visualType = 'list'
      this.showList = this.isListView = true
    }
  },
  computed: {
    isCalendarViewEnabled() {
      return (
        this.isCalendarView && this.$helpers.isLicenseEnabled('CALENDAR_VIEW')
      )
    },
  },
  methods: {
    switchListandCalendar(event) {
      let { name } = event || {}
      this.showList = name === 'list'
    },
    computeSort() {
      let { viewDetail } = this
      let { sortFields } = viewDetail || {}

      this.isSortingEnable = !isEmpty(sortFields)
    },
    toggleVisual(event) {
      let { isListView, isCalendarView } = this
      let bothEnabled = isListView && isCalendarView

      if (!bothEnabled) {
        this.showList = isListView
        this.visualType = isListView ? 'list' : 'calendar'
      } else {
        this.switchListandCalendar({ name: event })
        this.visualType = event
      }
    },
    isIconActive(showList) {
      return showList ? '#007adb' : 'black'
    },
    async validate() {
      let { isListView, isCalendarViewEnabled } = this

      if (!isListView && !isCalendarViewEnabled) {
        this.$message({
          message: this.$t('viewsmanager.visualization_type.error_msg'),
          type: 'error',
        })
        return false
      }

      let custcolmValidate = false,
        calendarValidate = false

      if (isListView)
        custcolmValidate = await this.$refs['custcolm-section'].validate()
      if (isCalendarViewEnabled)
        calendarValidate = await this.$refs['calendar-section'].validate()

      if (isListView && isCalendarViewEnabled) {
        if (custcolmValidate && calendarValidate) return true
        else if (custcolmValidate) {
          this.showList = false
          this.visualType = 'calendar'
          return false
        } else {
          this.showList = true
          this.visualType = 'list'
          return false
        }
      }

      return custcolmValidate || calendarValidate
    },
    serializeData() {
      let { isListView, isCalendarViewEnabled } = this
      let listData = {},
        calendarData = {}

      if (isListView) listData = this.$refs['custcolm-section'].serializeData()
      if (isCalendarViewEnabled)
        calendarData = this.$refs['calendar-section'].serializeData()

      let sortComponent = this.$refs['sorting-section']
      let sortConfigData = { orderBy: null, orderType: null }

      if (!isEmpty(sortComponent) && isListView) {
        sortConfigData = sortComponent.serializeData()
      }

      let visualization = {
        ...listData,
        ...calendarData,
        isListView,
        isCalendarView: isCalendarViewEnabled,
      }

      return { visualization, sortConfigData }
    },
  },
}
</script>

<style scoped>
.sh-selection-bar {
  width: 100%;
  margin-top: 8px;
  border-bottom: 1px solid #324056;
}
.type-selection-bar {
  width: 100%;
  margin-top: 8px;
  border-bottom: 1px solid #c0c4cc;
}
.active-type {
  color: #c0c4cc;
}
.active-color {
  color: #007adb;
}
.visual-tab {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 500;
  text-transform: capitalize;
  font-size: 14px;
}
.tab-height {
  min-height: 350px;
}
.disabled-color {
  color: #c0c4cc;
}
</style>
<style lang="scss">
.visual-type {
  z-index: 0;
  .el-tabs__nav.is-top {
    width: 100%;
  }
  .el-tabs__item {
    width: 50%;

    &:hover {
      color: inherit !important;
    }
  }
  .el-tabs__item.is-disabled {
    cursor: not-allowed;
    &:hover {
      color: #c0c4cc !important;
    }
  }
  .el-tabs__item.is-active {
    color: #324056;
  }

  .el-tabs__active-bar {
    height: 1px;
    width: 50% !important;
    background-color: #324056;
  }
}
</style>
