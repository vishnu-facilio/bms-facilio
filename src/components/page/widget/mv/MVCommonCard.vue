<template>
  <div class="mvcard-page">
    <div class="d-flex mv-card justify-around">
      <div
        class="d-flex justify-content-center mv-card-item"
        :class="[evenlySpaced ? 'flex-even' : '']"
        v-for="(card, index) in cardArr"
        :key="index"
      >
        <div class="d-flex flex-direction-column items-center">
          <div class="fc-black-11 fwBold text-uppercase">{{ card.title }}</div>
          <div
            :class="card.className ? card.className : ''"
            class="f35 pT25 flex-middle"
          >
            <currency
              v-if="card.isCurrenyCard"
              :value="card.value"
              :valueSize="getFontSize('value', card.value)"
              :symbolSize="getFontSize('symbol', card.value)"
            ></currency>
            <div
              v-else
              class="card-value-ellipsis"
              :title="card.value"
              v-tippy="{
                placement: 'top',
                animation: 'shift-away',
                arrow: true,
              }"
            >
              {{ card.value }}
            </div>
            <span v-if="card.unit" class="energy f16 pL5 pT7">{{
              card.unit
            }}</span>
          </div>
          <div class="fc-grey2 f13 text-center pT5 pR10 mT-auto">
            {{ frequencyLabel }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    cardArr: {
      type: Array,
      required: true,
    },
    frequencyLabel: {
      type: String,
      required: true,
    },
    evenlySpaced: {
      type: Boolean,
    },
  },
  methods: {
    getFontSize(type, value) {
      let length = value.toString().length
      if (length >= 8) {
        return 'xs'
      } else if (length <= 7 && length >= 6) {
        return 'md'
      } else if (length <= 5 && length >= 4) {
        return 'lg'
      } else if (length <= 3 && length >= 1) {
        return 'xlg'
      }
    },
  },
}
</script>

<style lang="scss">
.mvcard-page {
  .mv-card {
    .mv-card-item {
      border-right: 1px solid #edf4fa;
    }
    .mv-card-item:last-of-type {
      border: none;
    }
  }
  .card-value-ellipsis {
    max-width: 163px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
  }
  .flex-even {
    flex: 0 0 50%;
  }
}
</style>
