<template>
  <div class="db-user-filter-customization">
    <el-dialog
      :visible="visibility"
      class="db-user-filter-customization-dialog"
      :show-close="false"
      :append-to-body="true"
      :title="
        (userDTFilterConfig.field && userDTFilterConfig.field.displayName) ||
          (userDTFilterConfig.module && userDTFilterConfig.module.displayName) +
            '- FILTER CUSTOMIZATION'
      "
      width="50%"
    >
      <div class="body p25">
        <div class="filter-customization-container">
          <div class="filter-label title mB10">
            {{ $t('dashboardfilters.filter_label') }}
          </div>
          <el-input
            class="fc-input-full-border2 mB20 width60"
            v-model="userDTFilterConfig.label"
          ></el-input>

          <div class="filter-option-type title mB10">
            {{ $t('dashboardfilters.select_operator') }}
          </div>
          <el-select
            class="fc-input-full-border-h35 mB20 width60"
            v-model="elSelectModel"
            filterable
            @change="typeChange"
          >
            <el-option
              v-for="(value, key) in selectedOptions"
              :label="value.label"
              :key="key"
              :value="value.value"
            >
            </el-option>
          </el-select>
          <div class="filter-option-type title mB10">
            {{ $t('dashboardfilters.display_type_label') }}
          </div>
          <el-select
            class="fc-input-full-border-h35 mB20 width60"
            v-model="userDTFilterConfig.filterDisplayType"
          >
            <el-option v-bind:value="1" label="Slider"> </el-option>
            <el-option v-bind:value="2" label="Selectbox"> </el-option>
          </el-select>
          <div v-if="userDTFilterConfig.filterDisplayType == 2">
            <div class="filter-option-type title mB10">
              {{ $t('dashboardfilters.selection_type_label') }}
            </div>
            <el-select
              class="fc-input-full-border-h35 mB20 width60"
              v-model="userDTFilterConfig.componentType"
            >
              <el-option v-bind:value="1" label="Single select"> </el-option>
              <el-option v-bind:value="2" label="Multi select"> </el-option>
            </el-select>
          </div>
          <div v-if="this.operator_value != ''">
            <div class="mB15 mT20">
              <div class="block">
                <el-slider
                  v-model="slider_range"
                  range
                  show-stops
                  :min="min_slider_range"
                  :max="max_slider_range"
                  v-if="userDTFilterConfig.filterDisplayType == 1"
                >
                </el-slider>
              </div>
            </div>
            <div v-if="userDTFilterConfig.filterDisplayType === 2">
              <el-select
                class="fc-input-full-border-h35 mB20 width60"
                :multiple="userDTFilterConfig.componentType === 2"
                filterable
                :collapse-tags="userDTFilterConfig.componentType === 2"
                v-if="showDayOfWeek"
                v-model="computeSelectdValue"
              >
                <el-option
                  v-for="weeks in daysOfWeek"
                  :label="weeks.label"
                  :key="weeks.value"
                  :value="weeks.value"
                >
                </el-option>
              </el-select>
              <el-select
                class="fc-input-full-border-h35 mB20 width60"
                :multiple="userDTFilterConfig.componentType === 2"
                filterable
                :collapse-tags="userDTFilterConfig.componentType === 2"
                v-model="computeSelectdValue"
                v-if="showDayOfMonth"
              >
                <el-option
                  v-for="monthday in daysOfMonth"
                  :label="parseInt(monthday)"
                  :key="monthday"
                  :value="monthday"
                >
                </el-option>
              </el-select>
              <el-select
                class="fc-input-full-border-h35 mB20 width60"
                :multiple="userDTFilterConfig.componentType === 2"
                filterable
                :collapse-tags="userDTFilterConfig.componentType === 2"
                v-model="computeSelectdValue"
                v-if="showHourOfDay"
              >
                <el-option
                  v-for="hourDay in hourOfDay"
                  :label="parseInt(hourDay)"
                  :key="hourDay"
                  :value="hourDay"
                >
                </el-option>
              </el-select>
            </div>
          </div>
        </div>
      </div>

      <div class="dialog-save-cancel">
        <el-button class="modal-btn-cancel" @click="closeDialog">
          {{ $t('dashboardfilters.cancel') }}</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="saveFilterConfig"
        >
          {{ $t('dashboardfilters.done') }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
export default {
  components: {
    NewCriteriaBuilder,
  },
  props: ['userDTFilterConfig', 'visibility'],
  data() {
    return {
      // userDTFilterConfig:null,
      loading: false,
      defaultOptions: [],
      operator_value: '',
      daysOfWeek: [
        { value: '1', label: 'Mon' },
        { value: '2', label: 'Tue' },
        { value: '3', label: 'Wed' },
        { value: '4', label: 'Thu' },
        { value: '5', label: 'Fri' },
        { value: '6', label: 'Sat' },
        { value: '7', label: 'Sun' },
      ],
      daysOfMonth: [
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        '10',
        '11',
        '12',
        '13',
        '14',
        '15',
        '16',
        '17',
        '18',
        '19',
        '20',
        '21',
        '22',
        '23',
        '24',
        '25',
        '26',
        '27',
        '28',
        '29',
        '30',
        '31',
      ],
      hourOfDay: [
        '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        '10',
        '11',
        '12',
        '13',
        '14',
        '15',
        '16',
        '17',
        '18',
        '19',
        '20',
        '21',
        '22',
        '23',
      ],
      showHourOfDay: false,
      showDayOfWeek: false,
      showDayOfMonth: false,
      slider_range: [0, 31],
      max_slider_range: 31,
      min_slider_range: 0,
      selectedOptions: [
        { label: 'Hours of day', value: '103' },
        { label: 'Day of week', value: '85' },
        { label: 'Day of month', value: '101' },
      ],
      selectedDayOrHourValues: [],
      enable_operator_validation: false,
    }
  },
  created() {
    this.init()
  },
  computed: {
    elSelectModel: {
      get() {
        //single select
        //v -model passed to datetime filter component is always single select return value as a single element
        if (this?.userDTFilterConfig?.defaultValues?.length <= 0) {
          this.userDTFilterConfig.defaultValues[0] = '103'
          this.operator_value = '103'
        }
        return this.userDTFilterConfig.defaultValues[0]
      },
      set(value) {
        this.userDTFilterConfig.defaultValues = [value]
      },
    },
    computeSelectdValue: {
      get() {
        if (this.userDTFilterConfig.componentType === 1) {
          return this.selectedDayOrHourValues[0]
        }
        return this.selectedDayOrHourValues
      },
      set(value) {
        if (this.userDTFilterConfig.componentType === 1) {
          this.selectedDayOrHourValues = [value]
        } else {
          this.selectedDayOrHourValues = value
        }
      },
    },
  },
  methods: {
    setDefaultSliderRange(operator_type, range, min, max) {
      this.operator_value = operator_type
      if (operator_type == '103') {
        this.slider_range = range && range.length > 0 ? range : [0, 23]
        this.min_slider_range = min ? min : 0
        this.max_slider_range = max ? max : 23
        this.showHourOfDay = true
      } else if (operator_type == '101') {
        this.slider_range = range && range.length > 0 ? range : [1, 31]
        this.min_slider_range = min ? min : 1
        this.max_slider_range = max ? max : 31
        this.showDayOfMonth = true
      } else if (operator_type == '85') {
        this.slider_range = range && range.length > 0 ? range : [1, 7]
        this.min_slider_range = min ? min : 1
        this.max_slider_range = max ? max : 7
        this.showDayOfWeek = true
      }
    },
    init() {
      if (!(this.userDTFilterConfig?.defaultValues?.length > 0)) {
        this.userDTFilterConfig.defaultValues = ['103']
      }
      if (this.userDTFilterConfig?.defaultValues?.length > 0) {
        this.setDefaultSliderRange(this.userDTFilterConfig?.defaultValues[0])
      }
      if (
        this.userDTFilterConfig?.defaultValues &&
        this.userDTFilterConfig?.selectedSliderRangeValues?.length > 0
      ) {
        this.slider_range = this.userDTFilterConfig.selectedSliderRangeValues
        this.operator_value = this.userDTFilterConfig.defaultValues[0]
      } else if (
        this.userDTFilterConfig?.defaultValues &&
        this.userDTFilterConfig?.selectedDayOrHourValues?.length > 0
      ) {
        this.operator_value = this.userDTFilterConfig.defaultValues[0]
        if (this.operator_value == '103') {
          this.showHourOfDay = true
        } else if (this.operator_value == '101') {
          this.showDayOfMonth = true
        } else if (this.operator_value == '85') {
          this.showDayOfWeek = true
        }
        this.selectedDayOrHourValues = this.userDTFilterConfig?.selectedDayOrHourValues
      }
    },
    typeChange(operator_value) {
      this.operator_value = operator_value
      this.selectedDayOrHourValues = []
      if (operator_value == 85) {
        this.showDayOfWeek = true
        this.showDayOfMonth = false
        this.showHourOfDay = false
        this.slider_range = [1, 7]
        this.max_slider_range = 7
        this.min_slider_range = 1
      } else if (operator_value == 101) {
        this.showDayOfMonth = true
        this.showDayOfWeek = false
        this.showHourOfDay = false
        this.slider_range = [1, 31]
        this.max_slider_range = 31
        this.min_slider_range = 1
      } else if (operator_value == 103) {
        this.showHourOfDay = true
        this.showDayOfMonth = false
        this.showDayOfWeek = false
        this.slider_range = [0, 23]
        this.max_slider_range = 23
        this.min_slider_range = 0
      }
    },
    closeDialog() {
      this.$emit('updatevisibility', false)
    },
    saveFilterConfig() {
      this.userDTFilterConfig.defaultValues = [this.operator_value.toString()]
      if (this.operator_value !== '') {
        let filterDisplayType = this.userDTFilterConfig.filterDisplayType
        if (filterDisplayType === 1) {
          this.userDTFilterConfig.selectedSliderRangeValues = this.slider_range
          this.selectedDayOrHourValues = []
          this.userDTFilterConfig.selectedDayOrHourValues = []
        } else {
          let selectedDaysorHours = []
          for (let days in this.selectedDayOrHourValues) {
            selectedDaysorHours.push(
              this.selectedDayOrHourValues[days].toString()
            )
          }
          this.userDTFilterConfig.selectedDayOrHourValues = selectedDaysorHours
          this.userDTFilterConfig.selectedSliderRangeValues = []
        }
        this.$emit('filterPropertiesChanged', this.userDTFilterConfig)
      }
    },

    loadOptionLimitConfig() {
      if (this.userDTFilterConfig?.field?.dataTypeEnum == 'DATE_TIME') {
        this.defaultOptions = this.selectedOptions
      }
    },
  },
}
</script>

<style lang="scss">
.db-user-filter-customization-dialog {
  .spinner {
    margin-top: 200px;
  }

  .checkbox-list {
    .el-checkbox-group {
      height: 180px;
    }

    .el-checkbox {
      flex: 0 0 40%;
    }
  }

  .el-dialog__header {
    border-bottom: 1px solid #eff1f4;
  }
  .el-dialog__body {
    padding: 0px;
  }
  .body {
    height: 500px;
    overflow-y: scroll;
  }
  .filter-customization-container {
    height: 100%;
  }

  .modal-btn-save {
    margin-left: 0px !important;
  }

  .title {
    height: 14px;
    font-size: 14px;
    letter-spacing: 0.5px;
    color: #6b7e91;
  }
  .option-config-separator {
    height: 1px;
    border-bottom: solid 1px #eff1f4;
  }
  .validationMsg {
    color: #f56c6c;
    padding-top: 10px;
  }
}
</style>
