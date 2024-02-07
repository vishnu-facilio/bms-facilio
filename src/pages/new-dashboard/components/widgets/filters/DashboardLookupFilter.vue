<template>
  <div
    class="dashboard-lookup-filter"
    :class="enable_slider ? 'sliderWidth' : ''"
  >
    <div class="filter-label">
      <p class="filter-label-text mB0  textoverflow-ellipsis width100">
        {{ userFilterObj.label }}
      </p>

      <inline-svg
        @click.native="$emit('edit')"
        v-if="isEditMode"
        class="pointer opacity-0 filter-hover-icon mR10"
        src="svgs/pencil"
        iconClass="icon icon-sm"
        iconStyle="color:#ee518f"
      ></inline-svg>
      <inline-svg
        @click.native="$emit('delete')"
        v-if="isEditMode"
        class="pointer opacity-0 filter-hover-icon"
        src="svgs/delete"
        iconClass="icon icon-sm"
        iconStyle="color:#e77070"
      ></inline-svg>
    </div>

    <el-select
      class="fc-input-full-border-h35"
      placeholder="loading ..."
      v-if="initialOptionsLoading && !isEditMode"
      :value="{}"
    >
      <!-- el select ,ID is shown in the selext box until options are loaded. to avoid ID flickering ,use temp empty select box with placeholder -->
    </el-select>
    <template v-if="!initialOptionsLoading && !isEditMode">
      <FLookupFieldWithDefaultListWrapper
        class="rm-arrow"
        v-if="userFilterObj.module"
        v-model="elSelectModel"
        :label="userFilterObj.module.displayName"
        :picklistOptions="picklistOptions"
        :defaultList="defaultList"
        :criteria="criteria"
        :userfilmoduleEnum="userFilterObj.module.typeEnum"
        :filterConstruction="filterConstruction"
        @recordSelected="value => setValue(value)"
        :field="{
          lookupModule: { name: userFilterObj.module.name },
          multiple: isMultiple,
        }"
        :parentModuleId="parentModuleId"
        :selectedOptions="selectedOptions"
      ></FLookupFieldWithDefaultListWrapper>

      <div
        v-else-if="
          userFilterObj &&
            userFilterObj.field &&
            userFilterObj.field.dataTypeEnum === 'DATE_TIME'
        "
      >
        <el-select
          v-if="false"
          placeholder="Type to search"
          :loading="initialOptionsLoading || remoteOptionsLoading"
          loading-text="loading ... "
          class=" fc-input-full-border-select2 width100 resource-search"
          collapse-tags
          filterable
          v-model="elSelectModel"
          @change="setSliderRange"
          @visible-change="dropDownVisibiltyChanged"
          :remote="false"
          :remote-method="handleRemoteSearch"
        >
          <el-option
            v-for="option in elSelectOptions"
            :label="option.label"
            :value="option.value"
            :key="option.value"
          >
          </el-option>
        </el-select>
        <div class="block" style="padding:0px 3px;">
          <el-slider
            v-model="slider_range"
            range
            show-stops
            :min="min_slider_range"
            :max="max_slider_range"
            :marks="marks"
            :format-tooltip="formatSliderTipVal"
            v-if="enable_slider"
            @change="setChangedSliderValue"
          >
          </el-slider>
          <DashboardDateTimeFilterView
            v-if="enable_time_dropdown"
            :dayOrHourValues="selectedDayOrHourValues"
            :isMultiple="isMultiple"
            :selectedOperator="userFilterObj.defaultValues[0]"
            :userFilterObj="userFilterObj"
            @valueChanged="valueChanged"
          >
          </DashboardDateTimeFilterView>
        </div>
      </div>
      <el-select
        v-else
        placeholder="Type to search"
        :loading="initialOptionsLoading || remoteOptionsLoading"
        loading-text="loading ... "
        class=" fc-input-full-border-select2 width100 resource-search"
        :multiple="userFilterObj.componentType == 2"
        collapse-tags
        filterable
        v-model="elSelectModel"
        @change="valueChanged"
        @visible-change="dropDownVisibiltyChanged"
        :remote="false"
        :remote-method="handleRemoteSearch"
      >
        <el-option
          v-for="option in elSelectOptions"
          :label="option.label"
          :value="option.value"
          :key="option.value"
        >
        </el-option>
      </el-select>
    </template>
    <!-- dummy filter edit mode  -->
    <el-select
      :disabled="true"
      placeholder="Select"
      class="fc-input-full-border-h35"
      filterable
      v-model="elSelectModel"
      v-else-if="isEditMode"
    >
      <el-option label="select" value="dummy"> </el-option>
    </el-select>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import BaseWidgetMixin from 'src/pages/new-dashboard/components/widgets/BaseWidgetMixin.js'
import { constructFieldOptions } from '@facilio/utils/utility-methods'
import debounce from 'lodash/debounce'
import { API } from '@facilio/api'
import FLookupFieldWithDefaultListWrapper from 'src/pages/new-dashboard/components/widgets/filters/FLookupFieldWithDefaultListWrapper.vue'
// import FLookupFieldWithDefaultListWrapper from '@/FLookupFieldWithDefaultListWrapper'
import { deepCloneObject } from 'util/utility-methods'
import DashboardDateTimeFilterView from './DashboardDateTimeFilterView'

export default {
  mixins: [BaseWidgetMixin],
  props: ['isEditMode', 'value', 'userFilterObj', 'cascadingFilterJson'],
  data() {
    return {
      actionPicklistOptions: [],
      picklistOptions: [],
      actionFilter: {},
      lookupOptions: [],
      moduleList: [],
      remoteOptionsLoading: false,
      initialOptionsLoading: true,
      dateFieldOptions: [
        { label: 'Hours of day', value: '103' },
        { label: 'Day of week', value: '85' },
        { label: 'Day of month', value: '101' },
      ],
      enable_slider: false,
      slider_range: [],
      max_slider_range: 31,
      min_slider_range: 0,
      enable_time_dropdown: false,
      selectedDayOrHourValues: [],
      marks: {},
      hour12Format: 'hh:mm A',
      daysOfWeek: {
        1: 'Mon',
        2: 'Tue',
        3: 'Wed',
        4: 'Thu',
        5: 'Fri',
        6: 'Sat',
        7: 'Sun',
      },
      hourOfDaysMarks: {
        0: '12 AM',
        1: '1 AM',
        2: '2 AM',
        3: '3 AM',
        4: '4 AM',
        5: '5 AM',
        6: '6 AM',
        7: '7 AM',
        8: '8 AM',
        9: '9 AM',
        10: '10 AM',
        11: '11 AM',
        12: '12 PM',
        13: '1 PM',
        14: '2 PM',
        15: '3 PM',
        16: '4 PM',
        17: '5 PM',
        18: '6 PM',
        19: '7 PM',
        20: '8 PM',
        21: '9 PM',
        22: '10 PM',
        23: '11 PM',
      },
    }
  },
  components: {
    FLookupFieldWithDefaultListWrapper,
    DashboardDateTimeFilterView,
  },
  created() {
    this.getModuleList()
    if (this.isEditMode) {
      return
    } else {
      this.init()
    }
  },
  methods: {
    applyAction(action) {
      const { id: widgetId } = this ?? {}
      const { widget_id: actionWidgetId } = action ?? {}
      const self = this
      if (widgetId == actionWidgetId) {
        const { actionMeta: actions } = action ?? {}
        Object.keys(actions).forEach(key => {
          if (key == 'FILTER') {
            self.actionFilter = actions[key]
          } else if (key == 'PICK_LIST') {
            this.actionPicklistOptions = actions[key]
          }
        })
        this.getOptions()
      }
    },
    formatSliderTipVal(val) {
      let selected_operator =
        this.userFilterObj?.defaultValues?.length > 0
          ? this.userFilterObj?.defaultValues[0]
          : ''
      if (
        selected_operator === '103' &&
        this.$timeformat === this.hour12Format
      ) {
        return this.hourOfDaysMarks[val]
      } else if (selected_operator === '85') {
        return this.daysOfWeek[val]
      }
      return val
    },
    setChangedSliderValue() {
      if (this.userFilterObj?.field?.dataTypeEnum == 'DATE_TIME') {
        if (this.slider_range && this.slider_range.length == 2) {
          let start = this.slider_range[0]
          let end = this.slider_range[1]
          this.setMarks(this?.userFilterObj?.defaultValues[0], start, end)
          let dateTimeValues = []
          for (start; start <= end; start++) {
            dateTimeValues.push(start.toString())
          }
          this.userFilterObj.dateTimeValues = dateTimeValues
          this.valueChanged()
        }
      }
    },
    setMarks(operator_value, start, end) {
      this.marks = {}
      if (operator_value && operator_value === '85') {
        this.marks[start] = this.daysOfWeek[start]
        this.marks[end] = this.daysOfWeek[end]
      } else if (
        operator_value &&
        operator_value === '103' &&
        this?.$timeformat === this.hour12Format
      ) {
        this.marks[start] = this.hourOfDaysMarks[start]
        this.marks[end] = this.hourOfDaysMarks[end]
      } else {
        this.marks[start] = start + ''
        this.marks[end] = end + ''
      }
    },
    setSliderRange(value) {
      if (value == 85) {
        this.slider_range = [1, 7]
        this.max_slider_range = 7
        this.min_slider_range = 1
        this.enable_slider = true
        this.marks = { 1: 'Mon', 7: 'Sun' }
      } else if (value == 101) {
        this.slider_range = [1, 31]
        this.max_slider_range = 31
        this.min_slider_range = 1
        this.marks = { 1: '1', 31: '31' }
        this.enable_slider = true
      } else if (value == 103) {
        this.slider_range = [0, 23]
        this.max_slider_range = 23
        this.min_slider_range = 0
        if (this.$timeformat === this.hour12Format) {
          this.marks = {
            0: this.hourOfDaysMarks[0],
            23: this.hourOfDaysMarks[23],
          }
        } else {
          this.marks = { 0: '0', 23: '23' }
        }
        this.enable_slider = true
      }
      this.valueChanged()
    },
    setValue(value) {
      this.valueChanged()
    },
    dropDownVisibiltyChanged(visibility) {
      //reload options on close

      if (!visibility && this.isLazyLoad) {
        console.log('refetching initial options')
        //lookup type , for PICKLIST_LOOKUP fetch all at once, for non picklist lookup like space ,asset , lazy load
        this.handleRemoteSearch()
      }
    },
    init() {
      if (
        this.userFilterObj.field &&
        this.userFilterObj.field.dataTypeEnum == 'ENUM'
      ) {
        //for type=ENUM  ,options present inside field object itself
        this.lookupOptions = constructFieldOptions(
          this.userFilterObj.field.enumMap
        ).map(e => {
          return {
            value: String(e.value),
            label: e.label,
          }
        })
        this.initialOptionsLoading = false
      } //no field object , its a lookup filter
      else if (
        this.userFilterObj.field &&
        this.userFilterObj.field.dataTypeEnum === 'DATE_TIME'
      ) {
        let operator_value = null
        if (this.userFilterObj?.defaultValues?.length > 0) {
          operator_value = this.userFilterObj.defaultValues[0]
          if (operator_value == '103') {
            this.max_slider_range = 23
            this.min_slider_range = 0
            if (this?.$timeformat === this.hour12Format) {
              this.marks = {
                0: this.hourOfDaysMarks[0],
                23: this.hourOfDaysMarks[23],
              }
            } else {
              this.marks = { 0: '0', 23: '23' }
            }
            this.lookupOptions = [this.dateFieldOptions[0]]
          } else if (operator_value == '101') {
            this.max_slider_range = 31
            this.min_slider_range = 1
            this.marks = { 1: '1', 31: '31' }
            this.lookupOptions = [this.dateFieldOptions[2]]
          } else if (operator_value == '85') {
            this.max_slider_range = 7
            this.min_slider_range = 1
            this.marks = { 1: 'Mon', 7: 'Sun' }
            this.lookupOptions = [this.dateFieldOptions[1]]
          }
        }
        // this.lookupOptions = this.dateFieldOptions
        this.initialOptionsLoading = false
        if (this.userFilterObj?.selectedSliderRangeValues?.length > 0) {
          this.enable_slider = true
          this.slider_range = this.userFilterObj.selectedSliderRangeValues
          if (this.slider_range?.length == 2) {
            this.marks = {}
            if (operator_value === '85') {
              this.marks[this.slider_range[0]] = this.daysOfWeek[
                this.slider_range[0]
              ]
              this.marks[this.slider_range[1]] = this.daysOfWeek[
                this.slider_range[1]
              ]
            } else if (
              operator_value === '103' &&
              this.$timeformat === this.hour12Format
            ) {
              this.marks[this.slider_range[0]] = this.hourOfDaysMarks[
                this.slider_range[0]
              ]
              this.marks[this.slider_range[1]] = this.hourOfDaysMarks[
                this.slider_range[1]
              ]
            } else {
              this.marks[this.slider_range[0]] = this.slider_range[0] + ''
              this.marks[this.slider_range[1]] = this.slider_range[1] + ''
            }
          }
        } else if (this.userFilterObj?.selectedDayOrHourValues?.length > 0) {
          this.enable_slider = false
          this.enable_time_dropdown = true
          this.selectedDayOrHourValues = this.userFilterObj.selectedDayOrHourValues
        }
      } else if (this.userFilterObj.module) {
        //lookup type , for PICKLIST_LOOKUP fetch all at once, for non picklist lookup like space ,asset , lazy load

        this.getOptions(null, true).then(options => {
          this.lookupOptions = options
          this.initialOptionsLoading = false
        })
      }
    },
    valueChanged() {
      const { id: filterId } = this ?? {}
      this.$emit('valueChanged', filterId)
    },
    handleRemoteSearch: debounce(async function(search) {
      //handleRemoteSearch ONLY GETS CALLED 1 second after user finished typing

      console.log('remote search', search)
      this.remoteOptionsLoading = true
      this.lookupOptions = await this.getOptions(search)
      this.remoteOptionsLoading = false
    }, 1000),

    async getModuleList() {
      let { data, error } = await API.get('v3/modules/list/all')
      if (!error) {
        let { customModules, systemModules } = data
        this.moduleList = [...customModules, ...systemModules]
      }
    },

    filterConstruction() {
      let { parentModuleId } = this
      let filters = {}
      if (parentModuleId) {
        filters = {
          parentModuleId: {
            operatorId: 9,
            value: [`${parentModuleId}`],
          },
        }
      }
      return filters
    },

    async getOptions(search, isInitialFetch) {
      //,called for PICKLIST where data_type=lookup
      //for baseentity module type lookup , lazy load done,remote search ,
      // for picklist module lookup load all values upfront,this method called only on init

      let params = {}

      if (this.isLazyLoad) {
        params.search = search
        if (isEmpty(params.search)) {
          //set "" to null ,when user delete search param ,must load first 10 options not options for "" which is empty
          params.search = null
        }

        params.page = 1
        params.perPage = 10

        //make sure currently selected lookup record always resolves  its  id ->label,
        let currentySelectedOptions = this.value.filter(e => {
          return e != 'all' && e != 'others'
        })
        currentySelectedOptions = currentySelectedOptions.map(e => Number(e))

        if (this.userFilterObj.optionType == 2) {
          let criteria = this.userFilterObj.criteria
          if (criteria && criteria.conditions) {
            //dont send operator as there is no enum setter for operator,causes struts error,operatorId is sent which will suffice

            for (const conditionId in criteria.conditions) {
              delete criteria.conditions[conditionId].operator
            }
          }
          params.criteria = criteria
        }
      }

      if (this.cascadingFilterJson) {
        //stringify and send cascading filters to v2picklist api
        console.log(
          'cascading filter being applied to ',
          this.userFilterObj.label +
            'is :' +
            JSON.stringify(this.cascadingFilterJson)
        )
        params.filters = JSON.stringify(this.cascadingFilterJson)
      }
      //else for picklist type lookups can load all records as there will be few in number, no params

      //basespace response returing an array instead of map , must fix api
      let { userFilterObj } = this
      let { module } = userFilterObj || {}
      let { field } = userFilterObj || {}
      let { name } = module || field?.module?.name || {}
      if (isEmpty(name)) {
        name = field?.module?.name
      }
      const { actionFilter } = this ?? {}
      if (!isEmpty(actionFilter)) {
        const filters = {}
        try {
          const {
            criteria: { conditions },
          } = actionFilter ?? {}
          const getArray = stringList => {
            let array = stringList.split(',').map(i => i.trim())
            return array
          }
          Object.keys(conditions).forEach(k => {
            const condition = conditions[k]
            const { fieldName, operatorId, value } = condition
            filters[fieldName] = { operatorId, value: getArray(value) }
          })
          params['filters'] = JSON.stringify(filters)
        } catch (err) {
          console.error(err)
        }
      }
      if (name !== 'ticketstatus') {
        let { data, error } = await API.post(`v3/picklist/${name}`, params)

        if (error) {
          this.$message.error(error.message)
        } else {
          let options = []
          let { pickList, basespaces } = data
          this.picklistOptions = pickList
          if (pickList) {
            Object.keys(pickList).forEach(key => {
              options.push({
                label: pickList[key],
                value: String(key),
              })
            })
          } else if (basespaces) {
            if (!isEmpty(basespaces)) {
              options = basespaces.map(e => {
                return { value: String(e.id), label: e.name }
              })
            }
          }
          this.$forceUpdate()
          return options
        }
      }
    },
  },

  computed: {
    id() {
      return this?.userFilterObj?.widget_id
    },
    criteria() {
      if (this.userFilterObj.optionType == 2) {
        return this.userFilterObj.criteria
      }
      return null
    },
    parentModuleId() {
      let { userFilterObj } = this
      let { parentModuleName } = userFilterObj || {}
      if (parentModuleName) {
        let modules = this.moduleList.find(m => m.name === parentModuleName)
        if (!isEmpty(modules)) {
          return modules.moduleId
        }
      }
      return null
    },
    selectedOptions() {
      let { userFilterObj } = this
      let { selectedOptions } = userFilterObj || {}
      return selectedOptions || []
    },
    isMultiple() {
      if (
        this.userFilterObj?.componentType &&
        this.userFilterObj.componentType === 2
      ) {
        return true
      }
      return false
    },
    defaultList() {
      let options = []
      if (this.userFilterObj.isAllOptionEnabled) {
        options.unshift({ label: 'All', value: 'all' })
      }
      if (
        this.userFilterObj.optionType == 2 &&
        this.userFilterObj.isOthersOptionEnabled
      ) {
        options.push({ label: 'Others', value: 'others' })
      }
      return options
    },
    isLazyLoad() {
      if (this.isEditMode) {
        return
      }
      //for non picklist lookup module ,need to lazy load the options
      return (
        this.userFilterObj.module &&
        this.userFilterObj.module.typeEnum != 'PICK_LIST'
      )
    },
    elSelectModel: {
      get() {
        if (this.isEditMode) {
          return
        }
        if (this.userFilterObj.componentType == 1) {
          //single select
          //v -model passed to LookupFilter component is always  of form filterId:[value] , for single select return value as a single element
          return this.value[0]
        } else if (this.userFilterObj.componentType == 2) {
          //multi select
          return this.value
        } else {
          return null
        }
      },
      set(value) {
        //add toggle logic for all,
        if (this.userFilterObj.componentType == 1) {
          this.$emit('input', [String(value)])
        } else if (this.userFilterObj.componentType == 2) {
          let val = []
          value.forEach(rt => {
            val.push(String(rt))
          })
          this.$emit('input', val)
        }
      },
    },
    elSelectOptions() {
      if (!isEmpty(this.actionPicklistOptions)) {
        return this.actionPicklistOptions
      }
      if (this.isEditMode) {
        return
      }

      let options = deepCloneObject(this.lookupOptions)
      //for enum and lookup with module=picklist filter options if only certain options are selected
      if (this.userFilterObj.optionType == 2 && !this.isLazyLoad) {
        options = options.filter(e => {
          return this.userFilterObj.selectedOptions.includes(e.value)
        })
      }

      if (
        this.userFilterObj?.field?.dataTypeEnum !== 'DATE_TIME' &&
        this.userFilterObj.isAllOptionEnabled
      ) {
        options.unshift({ label: 'All', value: 'all' })
      }

      if (
        this.userFilterObj.optionType == 2 &&
        this.userFilterObj.isOthersOptionEnabled
      ) {
        options.push({ label: 'Others', value: 'others' })
      }
      return options
    },
  },
}
</script>

<style lang="scss">
@import './styles/_variables';
.dashboard-lookup-filter {
  &:hover {
    .filter-hover-icon {
      opacity: 1;
    }
  }
  .rm-arrow {
    .el-select .el-input .el-icon-arrow-up,
    .el-select .el-input .el-icon-arrow-down {
      display: none;
    }
  }

  .filter-label {
    display: flex;
    margin-bottom: 7px;

    margin-left: 3px;

    .el-input__inner {
      color: $db-black;
      letter-spacing: 0.5px;
      padding-left: 15px !important;
      padding-right: 15px !important;
    }
  }
  .filter-label-text {
    font-size: 11px;
    line-height: 11px;
    font-weight: 500;
    letter-spacing: 1px;
    color: $db-black;
    text-transform: uppercase;
  }
  .sliderText {
    line-height: 16px !important;
    height: 18px;
  }
  .sliderLabelAlign {
    text-align: center;
  }
  .sliderTextMargin {
    margin-left: 115px;
    margin-right: 28px;
  }
  .el-slider__marks-text {
    color: #324056;
    font-size: 11px;
    white-space: nowrap;
  }
}
</style>
