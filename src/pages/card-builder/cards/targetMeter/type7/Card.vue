<template>
  <div class="dragabale-card height100">
    <card-loading v-if="isLoading"></card-loading>
    <div
      v-else
      class="cb-card-container d-flex flex-direction-column"
      :style="{ backgroundColor: cardStyle.backgroundColor }"
    >
      <div
        class="f15 bold mB-auto card-header-block"
        :style="{ color: cardStyle.primaryColor }"
      >
        {{ cardData.title || 'Target Meter 7' }}
      </div>
      <div
        class="mB-auto p25 height100 solidgauge-section"
        v-resize
        @resize="onResize"
      >
        <solid-gauge
          :data="values"
          :maxValue="maxValue"
          :centerText="centerText"
          :styles="cardStyle"
          :editMode="editMode"
          :centerTextJson="centerTextJson"
        ></solid-gauge>
      </div>
    </div>
  </div>
</template>

<script>
import Card from 'pages/card-builder/cards/kpi/type1/Card'
import { isNumber, isEmpty } from '@facilio/utils/validation'
import { deepCloneObject } from 'util/utility-methods'
import solidGauge from '@/SolidGauge.vue'
import { API } from '@facilio/api'
export default {
  extends: Card,
  components: { solidGauge },
  props: ['editMode'],
  data() {
    return {
      report: null,
    }
  },
  mounted() {
    this.$nextTick(rt => {
      this.subtextAction()
    })
    this.findKpiField()
    this.setReadingObj()
  },
  watch: {
    cardData: {
      handler: function(newVal, oldVal) {
        if (newVal) {
          this.configWatchHook()
        }
      },
      deep: true,
    },
  },
  computed: {
    values() {
      let gaugeData = []
      let { cardData, cardParam } = this
      if (cardData && cardData.values && cardParam) {
        cardData.values.forEach((rt, index) => {
          let kpi = this.cardParam.kpis[index]
          if (kpi) {
            // let fieldName = rt.kpi.metricName
            // let field = rt.fields.find(rt => rt.name === fieldName)
            let unit = ''
            // if (field && field.unit) {
            //   unit = field.unit
            // }
            if (rt.unit) {
              unit = rt.unit
            }
            let showPathtext = this.cardStyle.showPathtext
            let v = {
              text:
                rt.value && showPathtext
                  ? `${this.formatDecimal(rt.value)} ${unit}`
                  : '',
              value: rt.value,
              formatedValue: this.formatDecimal(rt.value),
              lable: kpi.label,
              color: kpi.pathColor,
              textColor: kpi.textColor,
              showPathtext: kpi.showPathtext,
              unit: unit,
            }
            gaugeData.push(v)
          }
        })
      }

      return gaugeData
    },
    maxValue() {
      if (
        this.cardData &&
        this.cardData.maxValue &&
        this.cardData.maxValue.value
      ) {
        return this.cardData.maxValue.value
      }
      return 0
    },
    centerText() {
      if (
        this.cardData &&
        this.cardData.centerValue &&
        this.cardData.centerValue.value
      ) {
        return isNaN(Number(this.cardData.centerValue.value))
          ? this.cardData.centerValue.value
          : this.formatDecimal(Number(this.cardData.centerValue.value))
      }
      return ''
    },
    centerTextJson() {
      let unit =
        this.cardData.centerValue && this.cardData.centerValue.unit
          ? this.cardData.centerValue.unit
          : ''
      if (this.cardParam.centerTextType === 'text') {
        return {
          label: 'Value',
          value: this.cardParam.centerText,
          unit: '',
        }
      } else {
        let val =
          this.cardData.centerValue && this.cardData.centerValue.value
            ? this.cardData.centerValue.value
            : 0
        let kpiName =
          this.cardData.centerValue && this.cardData.centerValue.kpi
            ? this.cardData.centerValue.kpi.name
            : ''
        return {
          label: kpiName,
          value: this.formatDecimal(val),
          unit: unit,
        }
      }
    },
    cardParam() {
      if (this.cardParams) {
        return this.cardParams
      } else if (this.cardDataObj) {
        return this.cardDataObj
      } else {
        return null
      }
    },
    secondaryText() {
      if (this.cardData) {
        let { cardData } = this
        if (cardData.formatedSubText) {
          return cardData.formatedSubText
        } else if (cardData.period) {
          return cardData.period
        } else {
          return ''
        }
      }
      return ''
    },
  },
  methods: {
    onResize() {
      window.dispatchEvent(new Event('resize'))
    },
    setReadingObj() {
      let data =
        this.cardDrilldown &&
        this.cardDrilldown.default &&
        this.cardDrilldown.default.data
          ? this.cardDrilldown.default.data
          : null
      if (data && data.reportId) {
        this.loadReport(data.reportId)
      }
    },
    async loadReport(reportId) {
      let resp = await API.put(`v3/report/execute`, {
        reportId: reportId,
      })
      let result = resp.data
      this.report = result.report
    },
    configWatchHook() {
      this.$nextTick(() => {
        this.subtextAction()
      })
    },
    findKpiField() {
      // to be removed
      let { kpi } = this.cardData.value || {}
      if (!isEmpty(kpi)) {
        let moduleName = this.$getProperty(kpi, 'moduleName', null)
        if (!isEmpty(moduleName)) {
          this.loadFields(moduleName)
        }
      }
    },
    loadFields(moduleName) {
      this.$http.get(`/module/meta?moduleName=${moduleName}`).then(response => {
        let fields = response.data.meta.fields
        // this will be handled in server itself
        if (this.cardData && this.cardData.value && this.cardData.value.kpi) {
          this.$set(this.cardData.value.kpi, 'fields', fields)
        }
      })
    },
    subtextAction() {
      if (this.cardData && this.cardData.value && this.cardParam) {
        let { subText } = this.cardParam
        let { moduleData, fields } = this.cardData.value
        if (subText && subText.trim() !== '' && moduleData) {
          if (moduleData) {
            fields.forEach(field => {
              subText = subText.replace(
                '${' + field.name + '}',
                this.formatValueByFieldType(moduleData[field.name], field)
              )
            })
          } else {
            fields.forEach(field => {
              subText = subText.replace(
                '${' + field.name + '}',
                this.formatValueByFieldType('', field)
              )
            })
          }
          this.$set(this.cardData, 'formatedSubText', subText)
        }
      }
    },
  },
}
</script>
<style scoped>
.solidgauge-section {
  overflow: hidden;
}
</style>
