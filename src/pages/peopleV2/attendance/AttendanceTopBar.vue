<template>
  <div class="d-flex attendance-top-bar">
    <div class="d-flex attendance-top-bar-left-child">
      <el-radio-group v-model="viewMode" size="small" @change="updateViewMode">
        <el-radio-button label="Calendar View"></el-radio-button>
        <el-radio-button label="List View"></el-radio-button>
      </el-radio-group>

      <new-date-picker
        class="picker"
        :zone="$timezone"
        :dateObj="dateFilter"
        :tabs="dateRange.enabledTabs"
        @date="setDateFilter"
      ></new-date-picker>
    </div>
    <TransitionButtons
      style="margin-top: -8px"
      v-if="showTransitionButtons"
      :employeeID="employeeID"
      :allowDateTimeSelector="false"
    />
  </div>
</template>
<script>
import NewDateHelper from 'src/components/mixins/NewDateHelper'
import NewDatePicker from '@/NewDatePicker'
import TransitionButtons from 'src/pages/peopleV2/attendance/TransitionButtons.vue'

function computeDefaultRange() {
  const date = new Date()
  const firstDay = new Date(date.getFullYear(), date.getMonth(), 1)
  const lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0)
  return [firstDay, lastDay]
}
export default {
  props: {
    selectedEmployee: {
      required: true,
      type: Object,
    },
    showTransitionButtons: {
      type: Boolean,
      default: false,
    },
  },
  components: {
    NewDatePicker,
    TransitionButtons,
  },
  data() {
    return {
      dateRange: {
        enabledTabs: {
          enabledTabs: ['M'],
        },
      },
      dateFilter: NewDateHelper.getDatePickerObject(
        64, // month
        computeDefaultRange()
      ),
      timelineRange: null,
      viewMode: 'Calendar View',
    }
  },
  computed: {
    employeeID() {
      const { id } = this.selectedEmployee || -1
      return id
    },
  },
  methods: {
    updateViewMode() {
      this.$emit('viewModeUpdate', this.viewMode)
    },
    setTimelineRange(from, to) {
      this.timelineRange = { from, to }
      this.$emit('timelineRangeUpdate', this.timelineRange)
    },
    setDateFilter(data) {
      const range = data?.value
      if (!range) {
        return
      }
      const from = range[0]
      const to = range[1]

      this.setTimelineRange(from, to)
      this.dateFilter = data
    },
  },
}
</script>
<style scoped>
.attendance-top-bar {
  background-color: white;
  justify-content: space-between;
  height: 56px;
  font-size: 14px;
  padding-top: 19px;
  padding-left: 15px;
  padding-bottom: 18px;
  padding-right: 8px;
}
.create-btn {
  margin-top: -10px;
}
.label {
  padding: 5px;
  font-weight: 500;
}

.export-button:hover {
  cursor: pointer;
}
</style>
<style lang="scss">
.attendance-top-bar {
  .el-input__icon {
    display: flex;
    align-items: center;
  }
  .modal-dialog-footer {
    min-height: 300px;
  }
  .picker {
    margin-top: -2px;
    margin-left: 20px;
  }
}
.attendance-top-bar-left-child {
  margin-top: -8px;
}
</style>
