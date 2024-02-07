<template>
  <div id="kpi_layout1" class="dragabale-card height100">
    <card-loading v-if="isLoading"></card-loading>
    <div
      v-else
      class="cb-card-container d-flex flex-direction-column p25 pB20"
      :style="{
        backgroundColor: deftheme ? '#170238' : cardStyle.backgroundColor,
      }"
    >
      <div
        class="f15 bold mB-auto"
        :style="{ color: deftheme ? '#fff' : cardStyle.primaryColor }"
      >
        {{ cardData.title || 'Kpi Card' }}
      </div>
      <div class="mB-auto" @click="triggerAction()">
        <div
          class="f35"
          :style="{
            color: deftheme ? '#fff' : cardStyle.primaryColor,
            ...getAdditionalStyle(cardStyle),
          }"
          v-if="cardStyle.displayValue"
        >
          {{ cardStyle.displayValue }}
        </div>
        <template v-else>
          <div
            class="f35"
            :style="{ color: deftheme ? '#fff' : cardStyle.primaryColor }"
            v-if="localUnit"
          >
            <span class=" bold weather-unit" v-if="localUnit.position === 1">{{
              localUnit.unit
            }}</span>
            {{ cardDataValue }}
            <span class=" bold weather-unit" v-if="localUnit.position === 2">{{
              localUnit.unit
            }}</span>
          </div>
          <div
            class="f35"
            :style="{ color: deftheme ? '#fff' : cardStyle.primaryColor }"
            v-else
          >
            <span class=" bold weather-unit" v-if="unit.includes('$')">{{
              unit
            }}</span>
            {{ cardDataValue }}
            <span class=" bold weather-unit" v-if="!unit.includes('$')">{{
              unit
            }}</span>
          </div>
        </template>

        <div
          class="f13"
          :style="{ color: deftheme ? '#fff' : cardStyle.secondaryColor }"
        >
          {{ secondaryText }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Card from '../base/Card'
import { isNumber, isEmpty } from '@facilio/utils/validation'
import { deepCloneObject } from 'util/utility-methods'
import { API } from '@facilio/api'
import cardHelper from 'pages/card-builder/card-helpers.js'
export default {
  extends: Card,
  mixins: [cardHelper],
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
    deftheme() {
      if (
        (this.cardStyle.backgroundColor === '#FFFFFF' ||
          this.cardStyle.backgroundColor === '#FFF') &&
        this.cardStyle.primaryColor === '#110d24' &&
        this.cardStyle.secondaryColor === '#969caf' &&
        this.curtheme
      ) {
        return true
      } else {
        return false
      }
    },
    curtheme() {
      let strtheme = window.localStorage.getItem('theme')
      if (strtheme === '' || strtheme === 'white') {
        return false
      } else strtheme === 'black'
      {
        return true
      }
    },
    cardDataValue() {
      let {
        cardData: {
          value: { value },
        },
      } = this
      let decimalPlace =
        this.cardStyle && this.cardStyle.hasOwnProperty('decimalPlace')
          ? this.cardStyle.decimalPlace
          : -1
      return this.getFormatedValue(value, decimalPlace)
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
          // this.loadFields(moduleName)
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
        let { moduleData, fields, variables } = this.cardData.value
        if (subText && subText.trim() !== '') {
          if (moduleData) {
            fields.forEach(field => {
              subText = subText.replace(
                '${' + field.name + '}',
                this.formatValueByFieldType(moduleData[field.name], field)
              )
            })
          } else if (variables) {
            variables.forEach(field => {
              subText = subText.replace(
                '${' + field.name + '}',
                this.formatValueByFieldType(variables[field.name], field)
              )
            })
          } else if (fields) {
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
