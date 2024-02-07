<template>
  <div class="dragabale-card height100">
    <card-loading v-if="isLoading"></card-loading>
    <div
      v-else
      :class="['cb-card-container d-flex flex-direction-row p20 energy-card']"
    >
      <div v-if="cardData.image" class="d-flex flex-direction-column">
        <img class="fc-avatar profilemini-avatar" :src="cardData.image" />
      </div>
      <div v-else class="d-flex flex-direction-column">
        <inline-svg
          src="svgs/cardbuilder/placeholder-icons/building"
          iconClass="icon icon-building"
        ></inline-svg>
      </div>
      <div
        class="d-flex flex-direction-column pT5 mL20 pointer"
        @click="triggerAction()"
      >
        <div class="fvw18 bold pB10">{{ cardData.title }}</div>
        <div class="sb-secondary-color mb5">{{ cardData.period }}</div>
        <div class="fvw20" v-if="!$validation.isEmpty(cardData.value)">
          <span class="bold">{{ formattedValue }}</span>
          <span v-if="cardData.value.unit" class="fvw14 mL5">
            {{ cardData.value.unit }}
          </span>
        </div>
        <div v-if="showTrend">
          <arrow
            class="pT5"
            :arrowStyles="cardStyle"
            :value="formatDecimal(cardData.value.value, 0)"
            :baselineValue="formatDecimal(cardData.baselineValue.value, 0)"
          ></arrow>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Card from '../base/Card'
import { isEmpty } from '@facilio/utils/validation'
import arrow from 'pages/card-builder/cards/common/GrowthArrow'
import helpers from 'pages/card-builder/card-helpers'

export default {
  extends: Card,
  mixins: [helpers],
  components: { arrow },
  computed: {
    formattedValue() {
      let { cardData } = this
      let value = this.$getProperty(cardData, 'value.value')
      return value ? this.formatDecimal(value) : '--'
    },
    showTrend() {
      let { cardData } = this
      return (
        !isEmpty(this.$getProperty(cardData, 'value.value', null)) &&
        !isEmpty(this.$getProperty(cardData, 'baselineValue.value', null))
      )
    },
  },
}
</script>
<style lang="scss">
.energy-card {
  align-items: center;

  .icon-building {
    border: 1px solid rgba(0, 0, 0, 0.1);
  }
  .profilemini-avatar,
  .icon-building {
    width: 6vw;
    height: 6vw;
    border-radius: 50%;
  }
  .growth-arrow {
    .arrow-class {
      font-size: 12px;
      padding-top: 5px;
    }
    .value-class {
      font-size: 18px;
      padding-left: 5px;
    }
    .percentage-class {
      font-size: 14px;
      padding-left: 0px;
    }
  }
}
</style>
