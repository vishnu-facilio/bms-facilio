<template>
  <div class="pT20 fc-card-builder-page-animation">
    <div class="header pL30 pR30">
      <span class="pointer" @click="switchToCardView">
        <inline-svg
          v-if="showLayoutView"
          src="svgs/arrow"
          class="vertical-top rotate-90 mR20"
          iconClass="icon"
        ></inline-svg>
      </span>
      {{ title }}
      <span class="pointer" @click="onClose">
        <inline-svg
          src="svgs/close"
          class="vertical-middle fR"
          iconClass="icon icon-sm"
        ></inline-svg>
      </span>
    </div>
    <div
      :class="['container', showLayoutView && 'card-type-container-bg']"
      class="pT20 pL30 pB40"
    >
      <template v-if="!showLayoutView">
        <div
          v-for="card in cardTypes"
          :key="card.type"
          @click="switchToLayoutView(card)"
          class="item pointer"
        >
          <inline-svg
            :src="card.icon"
            class="vertical-middle"
            iconClass="icon icon-xxlg"
          ></inline-svg>
          <div class="name fc-black-15">{{ card.name }}</div>
        </div>
      </template>
      <template v-else>
        <div class="flex-middle flex-wrap">
          <div
            v-for="layout in selectedCard.layouts"
            :key="layout.type"
            @click="onSelect(selectedCard.type, layout)"
            class="item layout-item pointer"
          >
            <img
              :src="require('assets/card-layouts/' + layout.icon + '.jpg')"
              :style="layout.style ? layout.style : ''"
              iconClass="icon layout-card-icon"
            />
            <div class="name fc-black-13 text-left">{{ layout.name }}</div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['onSelect', 'onClose', 'cardTypes'],
  data() {
    return {
      showLayoutView: false,
      selectedCard: null,
    }
  },
  computed: {
    title() {
      if (this.showLayoutView) return 'Choose Layout'
      else return 'Add Card'
    },
  },
  methods: {
    openLayoutView(defaultSelection) {
      if (defaultSelection) {
        let card = this.cardTypes.find(c => c.type === defaultSelection)

        if (!isEmpty(card)) {
          this.showLayoutView = true
          this.selectedCard = card
        }
      }
    },
    switchToLayoutView(card) {
      if (card.layouts && card.layouts.length === 1) {
        this.onSelect(card.type, card.layouts[0])
      } else {
        this.selectedCard = card
        this.showLayoutView = true
      }
    },
    switchToCardView() {
      this.showLayoutView = false
      this.selectedCard = null
    },
  },
}
</script>
<style lang="scss">
.icon {
  &.layout-card-icon {
    width: 206px;
    height: 237px;
    border-radius: 3px;
    &:hover {
      box-shadow: 7px 0px 20px 0px #ebeeefa3;
    }
  }
}
</style>
<style scoped lang="scss">
.header {
  font-size: 22px;
  font-weight: 300;
  font-weight: 400;
  color: #324056;
}
.card-type-container-bg {
  background-color: #f3f7fd;
  padding-right: 15px;
  .name {
    font-weight: bold !important;
    text-transform: uppercase;
  }
}

.container {
  margin-top: 20px;
  margin-bottom: 30px;
  flex-wrap: wrap;
  overflow: auto;
  max-height: 700px;

  .item {
    align-items: center;
    border: 1px solid #ebeeef;
    transition: box-shadow 200ms ease-in-out;
    min-height: 120px;
    margin-right: 15px;
    margin-bottom: 15px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    flex-basis: 21.9%;
    border-radius: 5px;

    &.layout-item {
      border: none;
      flex-basis: auto;
      width: auto;
      padding-bottom: 10px;
      align-items: left !important;
    }
    img {
      border: 1px solid #ebeeef;
      border-radius: 3px;
    }

    &:hover {
      filter: none;
      border: 1px solid #483a9e;
      transition: 0.8s;
      .name {
        color: #483a9e;
        transition: 0.3s;
      }
      &.layout-item {
        border: none;
        box-shadow: none;
      }
      img {
        box-shadow: 0 8px 75px 0 rgba(83, 86, 92, 0.08);
        transition: box-shadow 200ms ease-in-out;
      }
    }

    .name {
      letter-spacing: 0.4px;
      color: #324056;
      font-weight: 500;
      padding-top: 15px;
    }
  }
}

@media screen and (min-width: 2000px) {
  .container {
    .name {
      flex-basis: 23.4% !important;
    }
    .layout-item {
      width: 150px;
      flex-basis: 150px;
    }
  }
}
</style>
