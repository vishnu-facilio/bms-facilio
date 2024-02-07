<template>
  <el-form ref="visual-cal" :model="calendarViewContext" :rules="rules">
    <div v-if="isLoading" class="shimmer-cal">
      <div v-for="i in 2" :key="i" class="cal-box loading-shimmer"></div>
    </div>
    <div v-else class="d-flex width100">
      <el-form-item label="From" prop="startDateFieldId" class="width100">
        <el-select
          filterable
          clearable
          v-model="calendarViewContext.startDateFieldId"
          class="fc-input-full-border2 width100 pR20"
          @change="calendarViewContext.endDateFieldId = null"
        >
          <el-option
            v-for="startTimeFld in timeFieldList"
            :key="startTimeFld.id"
            :label="startTimeFld.displayName"
            :value="startTimeFld.id"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="To" prop="endDateFieldId" class="width100">
        <el-select
          filterable
          clearable
          v-model="calendarViewContext.endDateFieldId"
          class="fc-input-full-border2 width100"
        >
          <el-option
            v-for="endTimeFld in endTimeFieldList"
            :key="endTimeFld.id"
            :label="endTimeFld.displayName"
            :value="endTimeFld.id"
          >
          </el-option>
        </el-select>
      </el-form-item>
    </div>
  </el-form>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

const calenderViews = {
  // 1: 'Day',
  // 2: 'Week',
  3: 'Month',
}
const dataTypes = {
  DATE: 5,
  DATE_TIME: 6,
}
export default {
  props: ['moduleName', 'viewDetail'],
  data() {
    return {
      calenderViews,
      metaInfoObj: {},
      isLoading: false,
      calendarViewContext: {
        startDateFieldId: null,
        endDateFieldId: null,
        defaultCalendarView: null,
      },
      rules: {
        startDateFieldId: [
          {
            required: true,
            message: this.$t('viewsmanager.calender.from_date'),
            trigger: 'blur',
          },
        ],
      },
    }
  },
  mounted() {
    let { moduleName, viewDetail } = this
    this.fetchModuleMetaInfo(moduleName)
    if (!isEmpty(viewDetail?.calendarViewContext))
      this.calendarViewContext = this.viewDetail?.calendarViewContext
  },
  computed: {
    moduleFields() {
      let fields = this.$getProperty(this.metaInfoObj, 'fields', []) || []
      return (
        (fields || []).filter(field => field.displayTypeEnum !== 'TASKS') || []
      )
    },
    timeFieldList() {
      let { DATE, DATE_TIME } = dataTypes
      return (this.moduleFields || []).filter(fld =>
        [DATE, DATE_TIME].includes(fld.dataType)
      )
    },
    endTimeFieldList() {
      return this.timeFieldList.filter(
        fld => this.calendarViewContext?.startDateFieldId != fld.id
      )
    },
  },
  methods: {
    async fetchModuleMetaInfo(moduleName) {
      if (!isEmpty(moduleName)) {
        this.isLoading = true
        await API.get('/module/metafields?moduleName=' + moduleName).then(
          ({ data, error }) => {
            if (error) {
              this.$message.error(error.message || 'Error Occured')
            } else {
              this.metaInfoObj = this.$getProperty(data, 'meta', {}) || {}
            }
          }
        )
      }
      this.isLoading = false
    },
    serializeData() {
      let {
        startDateFieldId,
        endDateFieldId,
        defaultCalendarView,
      } = this.calendarViewContext
      let calendarViewContext = {
        startDateFieldId,
        endDateFieldId,
        defaultCalendarView,
      }
      return { calendarViewContext, isCalendarView: true }
    },
    async validate() {
      let validCal = false

      try {
        validCal = await this.$refs['visual-cal'].validate()
      } catch {
        validCal = false
      }
      return validCal
    },
  },
}
</script>
<style lang="scss">
.shimmer-cal {
  display: flex;
  gap: 20px;

  .cal-box {
    width: 100%;
    height: 35px;
  }
}
</style>
