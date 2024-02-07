<template>
  <div class="dragabale-card height100" v-if="isLoading">
    <card-loading></card-loading>
  </div>
  <div class="dragabale-card" v-else>
    <div class="card-header-block f15 bold">{{ cardData.title }}</div>
    <div class="mT15 d-flex">
      <linear-gauge
        :gaugeData="gaugeData"
        :cardStyle="cardStyle"
        class="mT10 pL10 pR10"
      ></linear-gauge>
      <div
        class="d-flex flex-direction-column mL-auto pR25 justify-start mT20"
        @click="triggerAction()"
      >
        <div class="pB20">
          <div class="text-right f12 letter-spacing0_4 fc-black-color bold">
            {{ agg }}
          </div>
          <div
            class="text-right f18 pT5 fc-black-color value-contianer fwBold value-contianer value-cursor"
          >
            {{ cardData.value ? formatDecimal(cardData.value.value) : '--' }}
            <span class="f12" v-if="cardData.value && cardData.value.unit">{{
              cardData.value.unit
            }}</span>
          </div>
        </div>
        <div class="pB20">
          <div class="text-right f12 letter-spacing0_4 fc-black-color bold">
            Max Value
          </div>
          <div
            class="text-right f18 pT5 fc-black-color value-contianer fwBold value-contianer value-cursor"
          >
            {{ gaugeData.data ? formatDecimal(gaugeData.data.maxValue) : '--' }}
            <span class="f12" v-if="cardData.value && cardData.value.unit">{{
              cardData.value.unit
            }}</span>
          </div>
        </div>
        <div class="pB20">
          <div class="text-right f12 letter-spacing0_4 fc-black-color bold">
            Min Value
          </div>
          <div
            class="text-right f18 pT5 fc-black-color value-contianer fwBold value-contianer value-cursor"
          >
            {{ gaugeData.data ? formatDecimal(gaugeData.data.minValue) : '--' }}
            <span class="f12" v-if="cardData.value && cardData.value.unit">{{
              cardData.value.unit
            }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Card from '../type1/Card'
import LinearGauge from '@/LinearGauge'
import { aggregateFunctions } from 'pages/card-builder/card-constants'

export default {
  components: {
    LinearGauge,
  },
  data() {
    return {
      aggregateFunctions,
    }
  },
  extends: Card,
  computed: {
    agg() {
      if (this.cardParams && this.cardParams.reading) {
        let { yAggr } = this.cardParams.reading
        return this.aggregateFunctions.find(rt => rt.value === yAggr).label
      }
      return 'Latest'
    },
  },
}
</script>
<style>
.value-cursor {
  cursor: pointer;
}
</style>
