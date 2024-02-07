<template>
  <div class="dragabale-card height100">
    <card-loading v-if="isLoading"></card-loading>
    <div
      v-else
      class="cb-card-container d-flex flex-direction-column"
      :style="{ backgroundColor: cardStyle.backgroundColor }"
    >
      <div class=" p25 pB20">
        <div class="f15 bold mB20" :style="{ color: cardStyle.primaryColor }">
          {{ cardData.title || 'Reading Card' }}
        </div>
        <div class="mB-auto">
          <div
            class="f35"
            :style="{
              color: cardStyle.primaryColor,
              ...getAdditionalStyle(cardStyle),
            }"
            @click="triggerAction()"
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
            @click="triggerAction()"
            v-else
          >
            {{ cardDataValue }}

            <span class="f30 bold">{{
              cardData.value && cardData.value.unit
            }}</span>
          </div>
          <div class="f13" :style="{ color: cardStyle.secondaryColor }">
            {{ cardData.period || 'This Month' }}
          </div>
        </div>
      </div>
      <card-trend
        v-if="isTrendChart"
        :widget="widget"
        :widgetMeta="widgetMeta"
        class="card-trend"
        :cardData="cardData"
        :cardParams="cardParams"
        :trendColor="cardStyle.graphColor"
      ></card-trend>
    </div>
  </div>
</template>

<script>
import CardTrend from 'pages/card-builder/cards/common/CardTrend'
import card from '../type1/Card'
export default {
  extends: card,
  data() {
    return {
      isTrendChart: true,
      widgetMeta: {
        width: 0,
        height: 0,
      },
    }
  },
  components: {
    CardTrend,
  },
  mounted() {
    setTimeout(() => {
      this.render()
    }, 200)
  },
  watch: {
    '$attrs.widgetConfig.w': {
      handler() {
        setTimeout(() => {
          this.render()
        }, 200)
      },
    },
    '$attrs.widgetConfig.h': {
      handler() {
        setTimeout(() => {
          this.render()
        }, 200)
      },
    },
    cardData: {
      handler() {
        this.isTrendChart = false
        setTimeout(() => {
          this.isTrendChart = true
        }, 100)
      },
    },
  },
  methods: {
    render() {
      this.widgetMeta.width = this.$el.offsetWidth + 5
      this.widgetMeta.height = this.$el.offsetHeight / 3
    },
  },
}
</script>

<style>
.card-trend {
  position: absolute;
  bottom: -5px;
  left: -2px;
}
</style>
