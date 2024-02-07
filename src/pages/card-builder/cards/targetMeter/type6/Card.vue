<template>
  <div class="dragabale-card height100" v-if="isLoading">
    <card-loading></card-loading>
  </div>
  <div class="dragabale-card text-center height100" id="gaugecard" v-else>
    <div class="card-header-block f15 bold">{{ cardData.title }}</div>
    <div class="position-relative">
      <GaugeChart
        ref="gauge"
        :fit="true"
        :gaugeData="gaugeData"
        class="gaugeHeight card-gauge"
      ></GaugeChart>
      <div class="current-value-container">
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
          <span
            class="current-unit"
            v-if="cardData.value && cardData.value.unit"
          >
            {{ cardData.value.unit }}
          </span>
        </div>
        <div
          class="current-total bold text-uppercase"
          style="color: #5310c6;"
          v-if="totalText"
        >
          {{ totalText }}
          <span v-if="cardData.value && cardData.value.unit">
            {{ cardData.value.unit }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Card from '../type1/Card'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: Card,
  data() {
    return {
      totalText: null,
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
          type: 3,
          offsets: ['0', '31', '70', '100'],
          colors: ['#ff7878', '#7d49ff', '#514dff', '#1eb9b7'],
          backgroundColors: ['#fff', '#fff'],
        },
      },
    }
  },
  methods: {
    constructGaugeData() {
      let { cardData, cardStyle, cardState } = this

      let minValue = this.$getProperty(cardData, 'minValue.value')
      let maxValue = this.$getProperty(cardData, 'maxValue.value')
      let baseValue = this.$getProperty(cardData, 'value.value')
      let arcWidth = this.$getProperty(cardStyle, 'arcWidth') || null
      let showTooltip = this.$getProperty(cardStyle, 'showTooltip') || false
      let innerRadius = arcWidth ? 27 : null
      let outerRadius = arcWidth ? 29 + arcWidth : null

      if (!isEmpty(baseValue) && isEmpty(maxValue)) {
        maxValue = Math.ceil(parseInt(baseValue) / 100) * 100
      }

      if (isEmpty(minValue)) {
        minValue = 0
      }

      let isValueOverMax = parseInt(maxValue) < parseInt(baseValue)

      if (maxValue) this.totalText = `Out of ${maxValue}`

      let colors, offsets

      if (!isEmpty(cardStyle)) colors = cardStyle.ranges.map(r => r.color)
      if (isEmpty(colors)) {
        colors = this.gaugeData.config.colors
      } else {
        colors.unshift(colors[0])
      }

      if (!isEmpty(cardStyle)) offsets = cardStyle.ranges.map(r => r.limit)
      if (isEmpty(offsets)) {
        offsets = this.gaugeData.config.offsets
      } else {
        offsets.unshift('0')
      }

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
            colors,
            offsets,
            type: 3,
            innerRadius,
            outerRadius,
            showTooltip,
            ticks: this.calculateTicks(),
          },
        })
      }
    },
    calculateTicks() {
      let { cardData, cardStyle } = this
      if (isEmpty(cardData) || isEmpty(cardStyle)) return

      let color = cardStyle.tickColor
      let count = cardStyle.ranges.length - 1
      let labels = cardStyle.ranges.map(r => r.label)

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

      let index = 0
      for (let tick = initialValue; tick <= finalValue; tick += tickValue) {
        ticks.push({
          value: Number.isInteger(tick) ? tick : tick.toFixed(1),
          displayValue: String(labels[index]),
          color: color,
        })
        index += 1
      }

      return ticks
    },
  },
}
</script>
<style lang="scss" scoped>
.current-value-container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -60%);
}
// Using the vw scale because gauge component is using that
.current-value {
  font-size: 1.15vw;
  cursor: pointer;
}
.current-unit {
  font-size: 1vw;
}
.current-total {
  font-size: 0.65vw;
}
.gaugeHeight {
  height: calc(100% - 48px);
}
</style>
