<template>
  <div class="dragabale-card height100">
    <card-loading v-if="isLoading"></card-loading>
    <div
      v-else
      class="cb-card-container d-flex flex-direction-column"
      :style="{ backgroundColor: cardStyle.backgroundColor }"
    >
      <div class="d-flex flex-direction-row">
        <div class="d-flex flex-direction-column width100">
          <div
            class="p20 pB0 f15 bold"
            :style="{ color: cardStyle.primaryColor }"
          >
            {{ cardData.title || 'Reading Card' }}
          </div>
          <div class="p20 inline-flex pointer" @click="triggerAction()">
            <div v-if="cardData.image" class="cb-card-image">
              <img :src="getPreviewUrl(cardData.image)" />
            </div>
            <div class="mB-auto">
              <div
                class="f35"
                :style="{
                  color: cardStyle.primaryColor,
                  ...getAdditionalStyle(cardStyle),
                }"
                v-if="cardStyle.displayValue"
              >
                {{ cardStyle.displayValue }}
              </div>
              <div
                class="f35"
                :style="{
                  color: cardStyle.primaryColor,
                  ...getAdditionalStyle(cardStyle),
                }"
                v-else
              >
                <span class="f30 bold" v-if="isCostUnit(cardData.value)">{{
                  cardData.value && cardData.value.unit
                }}</span>
                {{ cardDataValue }}
                <span class="f30 bold" v-if="!isCostUnit(cardData.value)">{{
                  cardData.value && cardData.value.unit
                }}</span>
              </div>
              <div class="f13" :style="{ color: cardStyle.secondaryColor }">
                {{ cardPeriod || 'This Month' }}
              </div>
            </div>
            <div class="mB-auto mL-auto mT10">
              <arrow
                :arrowStyles="cardStyle"
                :value="cardData.value.value"
                :baselineValue="cardData.baselineValue.value"
              ></arrow>
            </div>
          </div>
        </div>
      </div>
      <div
        class="p20 d-flex flex-wrap mT-auto"
        style="border-top: solid 1px rgba(0, 0, 0, 0.04);"
      >
        <div class="f13" :style="{ color: cardStyle.secondaryColor }">
          {{ cardData.baselinePeriod || 'Last Month' }}
        </div>
        <div class="f13 pL15 bold" :style="{ color: cardStyle.primaryColor }">
          <span class="f13 bold" v-if="isCostUnit(cardData.baselineValue)">{{
            cardData.value && cardData.baselineValue.unit
          }}</span>
          {{ formattedBaseline }}
          <span class="f13 bold" v-if="!isCostUnit(cardData.baselineValue)">{{
            cardData.value && cardData.baselineValue.unit
          }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import arrow from 'pages/card-builder/cards/common/GrowthArrow'
import Card from '../base/Card'
import helper from 'pages/card-builder/card-helpers'
import { isEmpty, isNumber } from '@facilio/utils/validation'
export default {
  extends: Card,
  components: { arrow },
  mixins: [helper],

  computed: {
    formattedValue() {
      let { cardData } = this
      let value = this.$getProperty(cardData, 'value.value')
      if (value !== null && isNumber(value)) {
        return this.formatDecimal(value)
      }
      return '---'
    },
    formattedBaseline() {
      let {
        cardData: { baselineValue },
      } = this

      let decimalPlace =
        this.cardStyle && this.cardStyle.hasOwnProperty('decimalPlace')
          ? this.cardStyle.decimalPlace
          : -1
      let hasValue = baselineValue && isNumber(baselineValue.value)

      if (hasValue) {
        if (decimalPlace > 0) {
          return this.formatDecimal(baselineValue.value, decimalPlace)
        } else if (decimalPlace > -1) {
          return this.formatDecimal(baselineValue.value, 0)
        } else {
          return this.formatDecimal(baselineValue.value)
        }
      } else return '--'
    },
  },
  methods: {
    isCostUnit(value) {
      if (value && value.unit) {
        if (value.unit === '$') {
          return true
        } else if (value.unit === '₹') {
          return true
        }
      }
      return false
    },
  },
}
</script>
<style lang="scss" scoped>
.cb-card-image {
  max-width: 60px;
  max-height: 60px;
  margin-right: 10px;
  overflow: hidden;
  img {
    width: 100%;
  }
}
</style>
