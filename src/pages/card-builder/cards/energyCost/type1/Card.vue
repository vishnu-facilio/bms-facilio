<template>
  <div class="dragabale-card height100">
    <card-loading v-if="isLoading"></card-loading>
    <div
      v-else
      :class="[
        'cb-card-container d-flex flex-direction-column p20 weather-card text-center',
        'cost-card',
      ]"
    >
      <div class="mT5  cost-text">
        {{ cardData.title || 'ENERGY COST' }}
      </div>

      <div class="mT10 pointer" @click="triggerAction()">
        <div v-if="cardData.value" class="mL10 degree inline vertical-middle">
          <span
            v-if="formattedValue !== '--' && $currency.includes('$')"
            class="weather-unit"
          >
            {{ $currency }}
          </span>
          <span :style="valueStyle">{{ formattedValue }}</span>
          <span
            v-if="formattedValue !== '--' && !$currency.includes('$')"
            class="weather-unit"
          >
            {{ $currency }}
          </span>
        </div>
      </div>

      <div class="mT10 cost-text text-uppercase op8">
        {{ cardData.period }}
      </div>
    </div>
  </div>
</template>
<script>
import Card from '../base/Card'
import { isEmpty } from '@facilio/utils/validation'
import { format } from 'd3-format'

export default {
  extends: Card,
  computed: {
    valueStyle() {
      let { value: cost } = this.cardData

      if (isEmpty(cost) || isEmpty(cost.value)) {
        return null
      } else {
        let { value } = cost
        let font = 2.5

        if (value.length < 7) {
          font = 2.4
        } else if (value.length < 8) {
          font = 2.2
        } else if (value.length < 12) {
          font = 2
        } else if (value.length > 12) {
          font = 1.8
        }

        return {
          fontSize: font + 'rem',
          verticalAlign: 'middle',
        }
      }
    },
    formattedValue() {
      let { value: cost } = this.cardData
      if (isEmpty(cost) || isEmpty(cost.value)) {
        return '--'
      }

      let { value } = cost
      return format(',')(parseInt(value))
    },
  },
}
</script>
<style lang="scss" scoped>
.cost-card {
  background-image: linear-gradient(to bottom, #ec637f, #843f78);
}
.cost-text {
  font-size: 1vw;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: 20px;
  letter-spacing: 0.3px;
}
</style>
