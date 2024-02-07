<template>
  <div class="dragabale-card height100" v-if="isLoading">
    <card-loading></card-loading>
  </div>
  <div class="dragabale-card text-center" v-else>
    <div class="card-header-block f15 bold">{{ cardData.title }}</div>
    <GaugeChart :gaugeData="gaugeData" class="mT10 pL10 pR10"></GaugeChart>
    <div
      class="f30 card-value"
      @click="triggerAction()"
      :style="{
        ...getAdditionalStyle(cardStyle),
      }"
      v-if="cardStyle.displayValue"
    >
      {{ cardStyle.displayValue }}
    </div>
    <div
      class="f30 card-value"
      @click="triggerAction()"
      :style="{
        ...getAdditionalStyle(cardStyle),
      }"
      v-else
    >
      {{ cardData.value ? formatDecimal(cardData.value.value) : '--' }}
      <span class="f18" v-if="cardData.value && cardData.value.unit">{{
        cardData.value.unit
      }}</span>
    </div>
    <div class="f11 bold" style="color: #5310c6;">{{ gaugeLabel }}</div>
  </div>
</template>
<script>
import Card from '../base/Card'
import GaugeChart from '@/NewGaugeChart'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: Card,
  components: {
    GaugeChart,
  },
  data() {
    return {
      label: 'Current Value',
      gaugeData: {
        data: {
          baseValue: 0,
          minValue: 0,
          maxValue: 100,
        },
        tooltipdata: [
          {
            label: 'Target',
            value: 100,
          },
          {
            label: 'Achieved',
            value: 0,
          },
          {
            label: 'Remaining',
            value: 0,
          },
        ],
        config: {
          type: 1,
          colors: ['#ff7878', '#7d49ff', '#514dff', '#1eb9b7'],
          backgroundColors: ['#fff', '#fff'],
        },
      },
    }
  },
  computed: {
    gaugeLabel() {
      // to be removed
      if (window.orgId === 405) {
        return this.cardData.period
      }
      return this.label
    },
  },
  methods: {
    constructGaugeData() {
      let { cardData, cardStyle } = this

      let minValue = this.$getProperty(cardData, 'minValue.value')
      let maxValue = this.$getProperty(cardData, 'maxValue.value')
      let baseValue = this.$getProperty(cardData, 'value.value')

      let isValueUnderMin = parseInt(minValue) > parseInt(baseValue)
      let isValueOverMax = parseInt(maxValue) < parseInt(baseValue)

      if (!isEmpty(baseValue) && isEmpty(maxValue)) {
        maxValue = Math.ceil(parseInt(baseValue) / 100) * 100
      }

      if (isEmpty(minValue)) {
        minValue = 0
      }
      if (isEmpty(maxValue)) {
        maxValue = 100
      }

      if (!isEmpty(cardData)) {
        this.gaugeData = {
          ...this.gaugeData,
          data: {
            ...this.gaugeData.data,
            ...{
              baseValue: Number(baseValue),
              minValue: Number(minValue),
              maxValue: Number(maxValue),
            },
          },
          tooltipdata: [
            {
              label: 'Safe Limit',
              value: `${minValue} - ${maxValue}`,
            },
            {
              label: 'Achieved',
              value: baseValue,
            },
            ...[
              isValueOverMax
                ? {
                    label: 'Exceeded By',
                    value: baseValue - maxValue,
                  }
                : {
                    label: 'Remaining',
                    value: maxValue - baseValue,
                  },
            ],
          ],
          config: {
            ...this.gaugeData.config,
            startLabel: String(
              isValueUnderMin
                ? this.formatMaxValue(Math.floor(baseValue))
                : this.formatMaxValue(Math.floor(minValue))
            ),
            endLabel: String(
              isValueOverMax
                ? this.formatMaxValue(Math.ceil(baseValue))
                : this.formatMaxValue(Math.ceil(maxValue))
            ),
            ...this.serializeConditionalState(cardStyle),
          },
        }
      }
    },
    formatMaxValue(maxValue) {
      let max_val = String(maxValue)
      let before_after_decimal = max_val.split('.')
      if (max_val.length > 3 && before_after_decimal[0]) {
        const no_of_digits = before_after_decimal[0].length
        if (no_of_digits > 3 && no_of_digits < 7) {
          let res = String(Number(before_after_decimal[0]) / 1000)
          return this.setMaxValueResult(res) + ' K'
        } else if (no_of_digits >= 7 && no_of_digits < 10) {
          let res = String(Number(before_after_decimal[0]) / 1000000)
          return this.setMaxValueResult(res) + ' M'
        } else if (no_of_digits >= 10 && no_of_digits < 13) {
          let res = String(Number(before_after_decimal[0]) / 1000000000)
          return this.setMaxValueResult(res) + ' B'
        } else if (no_of_digits >= 13 && no_of_digits < 16) {
          let res = String(Number(before_after_decimal[0]) / 1000000000000)
          return this.setMaxValueResult(res) + ' T'
        }
      }
      return max_val
    },
    setMaxValueResult(res) {
      let result =
        res.length > 3 && res.indexOf('.') ? res.substring(0, 4) : res
      return result.substring(result.length - 1, result.length) == '.'
        ? result.substring(0, result.length - 1)
        : result
    },
    serializeConditionalState(styles) {
      let { props } = styles
      return {
        ...props,
        colors: styles.colors.map(color => {
          if (color.hex) {
            return color.hex
          } else {
            return color
          }
        }),
        backgroundColors: styles.backgroundColors.map(color => {
          if (color.hex) {
            return color.hex
          } else {
            return color
          }
        }),
      }
    },
  },
}
</script>
