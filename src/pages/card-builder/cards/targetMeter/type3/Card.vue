<template>
  <div class="dragabale-card height100" v-if="isLoading">
    <card-loading></card-loading>
  </div>
  <div class="dragabale-card text-center height100" id="gaugecard" v-else>
    <div class="card-header-block f15 bold">{{ cardData.title }}</div>
    <GaugeChart
      ref="gauge"
      :fit="true"
      :gaugeData="gaugeData"
      class="pL20 pR20 gaugeHeight card-gauge"
    ></GaugeChart>
    <div
      class="current-value"
      @click="triggerAction()"
      v-if="cardStyle.displayValue"
      :style="{
        ...getAdditionalStyle(cardStyle),
      }"
    >
      {{ cardStyle.displayValue }}
    </div>
    <div
      class="current-value"
      @click="triggerAction()"
      :style="{
        ...getAdditionalStyle(cardStyle),
      }"
      v-else
    >
      {{ cardData.value ? formatDecimal(cardData.value.value) : '--' }}
      <span class="f18" v-if="cardData.value && cardData.value.unit">
        {{ cardData.value.unit }}
      </span>
    </div>
    <div class="f11 bold" style="color: #5310c6;">{{ label }}</div>
  </div>
</template>
<script>
import Card from '../type1/Card'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: Card,
  methods: {
    constructGaugeData() {
      let { cardData, cardStyle } = this

      let minValue = this.$getProperty(cardData, 'minValue.value')
      let maxValue = this.$getProperty(cardData, 'maxValue.value')
      let baseValue = this.$getProperty(cardData, 'value.value')

      if (!isEmpty(baseValue) && isEmpty(maxValue)) {
        maxValue = Math.ceil(parseInt(baseValue) / 100) * 100
      }

      if (isEmpty(minValue)) {
        minValue = 0
      }

      let isValueOverMax = parseInt(maxValue) < parseInt(baseValue)

      if (!isEmpty(cardData)) {
        this.$set(this, 'gaugeData', {
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
            ...this.serializeConditionalState(cardStyle),
            type: 2,
            ticks: this.calculateTicks(),
          },
        })
      }
    },
    calculateTicks() {
      let { cardData, cardStyle } = this
      let color = cardStyle.tickColor
      let count = (cardData.tickCount || 5) - 1

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

      let tickValue =
        (parseFloat(maxValue) - parseFloat(minValue)) / parseFloat(count)
      let ticks = []
      let initialValue = parseFloat(isValueUnderMin ? baseValue : minValue)
      let finalValue = parseFloat(isValueOverMax ? baseValue : maxValue)

      for (let tick = initialValue; tick <= finalValue; tick += tickValue) {
        let tick_val = tick
        if (tick > 999) {
          tick_val = String(
            Number.isInteger(tick)
              ? this.formatMaxValue(tick)
              : this.formatMaxValue(tick.toFixed(1))
          )
        }

        ticks.push({
          value: Number.isInteger(tick) ? tick : tick.toFixed(1),
          displayValue: tick_val,
          color: color,
        })
      }

      return ticks
    },
  },
}
</script>
<style lang="scss" scoped>
.current-value {
  margin-top: -55px;
  font-size: 20px;
  cursor: pointer;
}
.gaugeHeight {
  height: calc(100% - 48px);
}
</style>
