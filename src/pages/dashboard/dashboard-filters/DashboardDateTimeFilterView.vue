<template>
  <div>
    <el-select
      class="fc-input-full-border-select2 width100 resource-search"
      :multiple="isMultiple"
      filterable
      :collapse-tags="isMultiple"
      v-model="computeSelectdValue"
      @change="valueChanged"
      v-if="showDayOfWeek"
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
      class="fc-input-full-border-select2 width100 resource-search"
      :multiple="isMultiple"
      filterable
      :collapse-tags="isMultiple"
      v-model="computeSelectdValue"
      @change="valueChanged"
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
      class="fc-input-full-border-select2 width100 resource-search"
      :multiple="isMultiple"
      filterable
      :collapse-tags="isMultiple"
      v-model="computeSelectdValue"
      @change="valueChanged"
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
</template>

<script>
export default {
  props: ['dayOrHourValues', 'isMultiple', 'selectedOperator', 'userFilterObj'],
  data() {
    return {
      dynamicTags: [],
      inputVisible: false,
      inputValue: '',
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
      selectedDayOrHourValues: [],
    }
  },
  created() {
    this.init()
  },
  computed: {
    computeSelectdValue: {
      get() {
        if (this?.isMultiple) {
          return this.selectedDayOrHourValues
        } else {
          return this.selectedDayOrHourValues[0]
        }
      },
      set(value) {
        if (this?.isMultiple) {
          this.selectedDayOrHourValues = value
        } else {
          this.selectedDayOrHourValues = [value]
        }
      },
    },
  },
  methods: {
    init() {
      this.selectedDayOrHourValues = this.dayOrHourValues
      if (this.selectedOperator == '103') {
        this.showHourOfDay = true
      } else if (this.selectedOperator == '101') {
        this.showDayOfMonth = true
      } else if (this.selectedOperator == '85') {
        this.showDayOfWeek = true
      }
    },
    valueChanged() {
      this.userFilterObj.selectedDayOrHourValues = this.selectedDayOrHourValues
      this.$emit('valueChanged')
    },
  },
}
</script>
