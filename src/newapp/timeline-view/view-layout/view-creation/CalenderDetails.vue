<template>
  <div class="calender-form-main">
    <el-form
      :model="details"
      :rules="rules"
      ref="calender-form"
      label-width="180px"
      label-position="top"
    >
      <el-form-item
        prop="startDateFieldId"
        label="StartTime Field"
        class="mB10"
      >
        <el-select
          v-model="details.startDateFieldId"
          placeholder="Select the start-time field"
          class="fc-input-full-border-select2 width100"
          filterable
        >
          <el-option
            v-for="startTimeFld in startTimeFieldList"
            :key="startTimeFld.id"
            :label="startTimeFld.displayName"
            :value="startTimeFld.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item prop="endDateFieldId" label="EndTime Field" class="mB10">
        <el-select
          v-model="details.endDateFieldId"
          placeholder="Select the end-time field"
          class="fc-input-full-border-select2 width100"
          filterable
        >
          <el-option
            v-for="endTimeFld in endTimeFieldList"
            :key="endTimeFld.id"
            :label="endTimeFld.displayName"
            :value="endTimeFld.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        prop="defaultCalendarView"
        label="Default Calender View"
        class="mB10"
      >
        <el-select
          v-model="details.defaultCalendarView"
          placeholder="Select the calender view"
          class="fc-input-full-border2 width100 fc-tag"
          filterable
        >
          <el-option
            v-for="(view, viewId) in calenderViews"
            :key="`${viewId}-${view}`"
            :label="view"
            :value="parseInt(viewId)"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        prop="weekendId"
        label="Customize Weekend"
        class="mB10 weekend-form"
      >
        <el-select
          v-model="details.weekendId"
          placeholder="Select the weekend"
          class="fc-input-full-border2 width100 fc-tag"
          popper-class="view-creation-folder-selection-popper timeline-weekend-popover"
          filterable
          clearable
          @clear="details.disableWeekends = true"
        >
          <el-option
            v-for="weekend in customizedWeekendList"
            :key="weekend.id"
            :label="weekend.name"
            :value="weekend.id"
          >
            <div>
              <div>{{ weekend.name }}</div>
              <div class="selected-days">{{ weekend.displayValue }}</div>
            </div>
            <div>
              <i
                class="el-icon-edit edit-icon pR15 weekend-actions"
                @click="editWeekend(weekend)"
              ></i>
              <i
                class="el-icon-delete fc-delete-icon weekend-actions"
                @click="deleteWeekend(weekend)"
              ></i>
            </div>
          </el-option>
          <div class="search-select-filter-btn">
            <el-button
              @click="createWeekend"
              class="pL20 width100 text-left addFolder-btn"
            >
              + {{ $t('viewsmanager.calender.add_weekend') }}
            </el-button>
          </div>
          <div slot="empty" class="el-select-dropdown__empty">
            <div class="search-select-filter-btn">
              <el-button
                @click="createWeekend"
                class="pL20 width100 text-left addFolder-btn"
              >
                + {{ $t('viewsmanager.calender.add_weekend') }}
              </el-button>
            </div>
          </div>
        </el-select>
      </el-form-item>
    </el-form>
    <el-form label-position="left" class="timeline-view-schedule-weekend">
      <el-form-item
        prop="disableWeekends"
        label="Schedule events on weekends"
        :disabled="$validation.isEmpty(details.weekendId)"
        class="mB10 mT10 flex-middle"
      >
        <el-radio
          v-model="details.disableWeekends"
          :label="false"
          :disabled="$validation.isEmpty(details.weekendId)"
          class="fc-radio-btn"
        >
          {{ 'Allow' }}
        </el-radio>
        <el-radio
          v-model="details.disableWeekends"
          :label="true"
          :disabled="$validation.isEmpty(details.weekendId)"
          class="fc-radio-btn"
        >
          {{ "Don't Allow" }}
        </el-radio>
      </el-form-item>
    </el-form>
    <WeekendForm
      v-if="showWeekendForm"
      :weekend="weekendObj"
      @onSave="saveAndUpdateWeekend"
      @onClose="showWeekendForm = false"
    ></WeekendForm>
  </div>
</template>
<script>
import { dataTypes } from './schedulerViewUtil'
import Constants from 'util/constant'
import WeekendForm from './components/WeekendForm.vue'
import { API } from '@facilio/api'

const calenderViews = {
  1: 'Day',
  2: 'Week',
  3: 'Month',
  4: 'Year',
}
export default {
  props: [
    'viewDetails',
    'isNew',
    'moduleFields',
    'saveAsNew',
    'weekendsList',
    'updateWeekEnd',
  ],
  components: { WeekendForm },
  data() {
    return {
      details: {
        startDateFieldId: null,
        endDateFieldId: null,
        weekendId: null,
        disableWeekends: true,
        defaultCalendarView: 1,
      },
      showWeekendForm: false,
      weekendObj: null,
      rules: {
        startDateFieldId: {
          required: true,
          message: 'Please select start-time field',
          trigger: 'blur',
        },
        endDateFieldId: {
          required: true,
          message: 'Please select end-time field',
          trigger: 'blur',
        },
      },
      calenderViews,
    }
  },
  created() {
    if (!this.isNew || this.saveAsNew) {
      let {
        startDateFieldId,
        endDateFieldId,
        weekendId,
        disableWeekends,
        defaultCalendarView,
      } = this.viewDetails || {}
      this.details = {
        startDateFieldId,
        endDateFieldId,
        weekendId: weekendId > 0 ? weekendId : null,
        disableWeekends,
        defaultCalendarView,
      }
    }
  },
  computed: {
    startTimeFieldList() {
      let { DATE, DATE_TIME } = dataTypes
      return (this.moduleFields || []).filter(fld =>
        [DATE, DATE_TIME].includes(fld.dataType)
      )
    },
    endTimeFieldList() {
      let { DATE, DATE_TIME } = dataTypes
      return (this.moduleFields || []).filter(fld =>
        [DATE, DATE_TIME].includes(fld.dataType)
      )
    },
    customizedWeekendList() {
      let { weekendsList } = this

      return weekendsList.map(weekend => {
        let { id, name, value } = weekend || {}
        let weekDays = Constants.WEEK_DAYS

        value = (JSON.parse(value) || {}).All || []

        let displayValue = value.reduce((displayVal, day, index) => {
          if (index !== 0) {
            displayVal += ', '
          }
          displayVal += weekDays[day]
          return displayVal
        }, '')

        return { id, name, value, displayValue }
      })
    },
  },

  methods: {
    createWeekend() {
      this.weekendObj = null
      this.showWeekendForm = true
    },
    editWeekend(weekend) {
      this.weekendObj = weekend
      this.showWeekendForm = true
    },
    async deleteWeekend(weekend) {
      let value = await this.$dialog.confirm({
        title: 'Delete Weekend',
        htmlMessage: 'Are you sure want to delete this condition?',
        rbDanger: true,
        rbLabel: this.$t('common._common.confirm'),
      })

      if (!value) return

      let { id } = weekend || {}
      let { error } = await API.delete('v2/weekends/delete', {
        weekend: { id },
      })

      if (error) {
        this.$message.error(error.message || 'Failed to delete weekend')
      } else {
        this.$message.success('Weekend deleted successfully')
        this.updateWeekEnd(weekend, true)
      }
    },
    saveAndUpdateWeekend(weekend) {
      this.details.weekendId = weekend.id
      this.updateWeekEnd(weekend)
    },
    async validate() {
      try {
        return await this.$refs['calender-form'].validate()
      } catch {
        return false
      }
    },
    serialize() {
      return this.details
    },
  },
}
</script>
<style lang="scss">
.timeline-weekend-popover {
  .el-select-dropdown__item {
    font-weight: 400;
    line-height: unset;
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 40px;
  }
  .selected-days {
    font-size: 11px;
    margin-top: 4px;
    color: #999999;
  }
  .weekend-actions {
    visibility: hidden;
  }
  .el-select-dropdown__item:hover {
    .weekend-actions {
      visibility: visible;
    }
  }
  .el-select-dropdown__item.selected {
    .selected-days {
      color: var(--fc-theme-color);
      opacity: 0.8;
    }
  }
  .empty-text {
    padding: 10px 0;
    margin: 0;
    text-align: center;
    color: #999;
    font-size: 14px;
    line-height: 24px;
  }
}
.timeline-view-schedule-weekend {
  .el-form-item__label {
    width: 300px;
  }
  .el-form-item__content {
    margin: auto;
  }
}
</style>
