<template>
  <div class="dragabale-card height100">
    <div
      :class="[
        'cb-card-container d-flex flex-direction-column p20 weather-card text-center',
        'carbon-card',
      ]"
    >
      <div class="mT5 text-uppercase carbon-text">Carbon Emissions</div>

      <div class="mT10">
        <inline-svg
          src="co2"
          class="vertical-middle"
          iconClass="icon weather-icon"
        ></inline-svg>
        <div v-if="cardData.value" class="mL10 degree inline vertical-middle">
          <span>{{
            (formattedValue.value && formatDecimal(formattedValue.value)) ||
              '--'
          }}</span>
          <span v-if="formattedValue.unit" class="weather-unit">
            {{ formattedValue.unit }}
          </span>
        </div>
      </div>

      <div class="mT10 carbon-text text-uppercase op8">
        {{ cardData.period }}
      </div>
    </div>
  </div>
</template>
<script>
import Card from '../base/Card'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: Card,
  computed: {
    formattedValue() {
      let { value: carbon } = this.cardData

      if (isEmpty(carbon) || isEmpty(carbon.value)) {
        return { value: '---' }
      }

      let { value } = carbon

      if (value > 1000) {
        let d = {}
        d.value = (value / 1000).toFixed(2)
        d.unit = 'Tons'
        return d
      } else {
        let d = {}
        d.value = value.toFixed(2)
        d.unit = 'Kg'
        return d
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.carbon-card {
  background-image: linear-gradient(to left, #2f2e49, #2d436e);
}
.carbon-text {
  font-size: 1vw;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: 20px;
  letter-spacing: 0.3px;
}
</style>
