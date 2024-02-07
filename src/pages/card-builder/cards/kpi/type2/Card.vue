<template>
  <div id="kpi_layout2" class="dragabale-card height100">
    <card-loading v-if="isLoading"></card-loading>
    <div
      v-else
      class="cb-card-container d-flex flex-direction-column"
      :style="{
        backgroundColor: deftheme ? '#170238' : cardStyle.backgroundColor,
      }"
    >
      <div
        class="p20 pB0 f15 bold mB-auto"
        :style="{ color: deftheme ? '#fff' : cardStyle.primaryColor }"
      >
        {{ cardData.title || 'Kpi Card' }}
      </div>
      <div class="p20 inline-flex pointer">
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
              <span
                class=" bold weather-unit"
                v-if="localUnit.position === 1"
                >{{ localUnit.unit }}</span
              >
              {{ cardDataValue }}
              <span
                class=" bold weather-unit"
                v-if="localUnit.position === 2"
                >{{ localUnit.unit }}</span
              >
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
        <div class="mB-auto mL-auto mT10">
          <arrow
            v-if="cardData.value"
            :arrowStyles="cardStyle"
            :value="cardData.value.value"
            :baselineValue="cardData.baselineValue.value"
          ></arrow>
        </div>
      </div>
      <div
        class="p20 d-flex flex-wrap mT-auto"
        style="border-top: solid 1px rgba(0, 0, 0, 0.04);"
      >
        <div
          class="f13"
          :style="{
            color:
              cardStyle.backgroundColor === '#FFF' &&
              cardStyle.primaryColor === '#110d24' &&
              cardStyle.secondaryColor === '#969caf' &&
              curtheme
                ? '#fff'
                : cardStyle.secondaryColor,
          }"
        >
          {{ cardData.baselinePeriod || 'Last Month' }}
        </div>
        <div
          class="f13 pL15 bold"
          :style="{
            color:
              cardStyle.backgroundColor === '#FFF' &&
              cardStyle.primaryColor === '#110d24' &&
              cardStyle.secondaryColor === '#969caf' &&
              curtheme
                ? '#fff'
                : cardStyle.primaryColor,
          }"
        >
          <span class=" bold weather-unit" v-if="localUnit.position === 1">{{
            localUnit.unit
          }}</span>
          {{ formattedBaseline }}
          <span class=" bold weather-unit" v-if="localUnit.position === 2">{{
            localUnit.unit
          }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Card from '../type1/Card'
import { isEmpty, isNumber } from '@facilio/utils/validation'
import helper from 'pages/card-builder/card-helpers'
import arrow from 'pages/card-builder/cards/common/GrowthArrow'
export default {
  extends: Card,
  mixins: [helper],
  components: { arrow },
  computed: {
    deftheme() {
      if (
        this.cardStyle.backgroundColor === '#FFF' &&
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
    formattedBaseline() {
      let {
        cardData: { baselineValue },
      } = this

      let decimalPlace =
        this.cardStyle && this.cardStyle.hasOwnProperty('decimalPlace')
          ? this.cardStyle.decimalPlace
          : -1
      let hasValue =
        baselineValue &&
        isNumber(baselineValue.value) &&
        baselineValue.value >= 0

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
}
</script>
