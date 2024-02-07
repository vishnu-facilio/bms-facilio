<script>
import { timeAggregation } from '../PivotDefaults'
import Vue from 'vue'
import moment from 'moment-timezone'
import { mapGetters } from 'vuex'
import {
  isNumberField,
  isDecimalField,
  isIdField,
  isDateTimeField,
  isDateField,
} from '@facilio/utils/field'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['value', 'pivotTable', 'prop', 'visualConfig', 'referenceValue'],
  mixins: [NewDataFormatHelper],
  computed: {
    ...mapGetters(['getCurrentUser']),
    valueInPercentage() {
      let result = 100
      if (
        this.visualConfig.compareWith == 'constant' &&
        this.visualConfig.constantValue
      ) {
        let currentValue =
          typeof this.value == 'string'
            ? this.value?.replaceAll(',', '')
            : this.value
        if (currentValue != undefined && !isNaN(currentValue)) {
          result =
            (Number(currentValue) / Number(this.visualConfig.constantValue)) *
            100
        }
      } else if (
        this.visualConfig.compareWith == 'maxValue' &&
        this.visualConfig.columnMaxValue
      ) {
        result = (this.value / this.visualConfig.columnMaxValue) * 100
      } else if (
        this.visualConfig.compareWith == 'referenceColumn' &&
        this.referenceValue
      ) {
        let currentValue =
          typeof this.value == 'string'
            ? this.value?.replaceAll(',', '')
            : this.value
        let referenceValue =
          typeof this.referenceValue == 'string'
            ? this.referenceValue?.replaceAll(',', '')
            : this.referenceValue
        if (
          currentValue != undefined &&
          !isNaN(currentValue) &&
          !isNaN(referenceValue)
        ) {
          result = (Number(currentValue) / Number(referenceValue)) * 100
          result = result - 100
        } else if (
          currentValue != undefined &&
          !isNaN(currentValue) &&
          Number(currentValue) <= 0 &&
          !isNaN(referenceValue) &&
          Number(referenceValue) <= 0
        ) {
          result = 0
        }
      } else if (!this.referenceValue) {
        result = this.value * 100
        result = result - 100
      }

      result = Number(result).toFixed(2)

      return parseFloat(result)
    },
    absValueInPercentage() {
      let result = Math.abs(this.valueInPercentage)
      return result
    },
  },
  data() {
    return {
      timeAggregation,
    }
  },
  methods: {
    isEmpty,
    getFormattedCellValue(value, prop) {
      if (prop.startsWith('number')) return value
      if (prop.startsWith('formula')) {
        return value ? value : '--'
      }

      let { dataAlias, rowAlias } = this.pivotTable || {}
      let field = dataAlias[prop] ? dataAlias[prop].field : rowAlias[prop].field

      if (prop.startsWith('data') && isEmpty(value)) {
        let aggr = this.pivotTable.values.find(e => e.alias === prop).aggr
        if (aggr === 1) return 0
      }

      if (isEmpty(value)) {
        return '--'
      }

      if (isIdField(field) && prop.startsWith('row')) {
        return '#' + value
      }
      if (isNumberField(field) && field.displayType == 'DURATION') {
        if (!isEmpty(value)) {
          value = value.replaceAll(',', '')
        }
        if (
          !isNaN(value) &&
          isEmpty(field.unit) &&
          isEmpty(field.unitId) &&
          isEmpty(field.unitEnum)
        ) {
          return this.$helpers.getFormattedDuration(Number(value), 'seconds')
        }
      }
      if (isDateTimeField(field) || isDateField(field)) {
        let aggr = 0
        this.pivotTable?.rows?.forEach(e => {
          if (e.alias === prop) {
            aggr = e.field.aggr > 0 ? e.field.aggr : 0
            if (!isEmpty(e.selectedTimeAggr) && aggr <= 0) {
              aggr = aggr > 0 ? aggr : e.selectedTimeAggr
            }
          }
        })
        let timeAggr = this.timeAggregation.find(obj => obj.value === aggr)
        return this.formatDate(value, timeAggr.enumValue)
      }
      return value
    },
    formatDate(date, period) {
      let dateFormat = this.getDateFormat(period)
      if (this.getCurrentUser()?.timezone === null) {
        return moment(new Date(date))
          .tz(Vue.prototype.$timezone)
          .format(dateFormat.tooltip)
      } else {
        return moment(new Date(date))
          .tz(this.getCurrentUser()?.timezone)
          .format(dateFormat.tooltip)
      }
    },
    checkNumberField(prop) {
      let { dataAlias, rowAlias } = this.pivotTable || {}
      let field = dataAlias[prop] ? dataAlias[prop].field : rowAlias[prop].field

      return !isEmpty(field)
        ? isNumberField(field) || isDecimalField(field) || isIdField(field)
        : false
    },
  },
}
</script>
