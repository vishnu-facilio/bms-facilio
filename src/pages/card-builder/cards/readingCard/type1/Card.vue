<template>
  <div class="dragabale-card height100">
    <card-loading v-if="isLoading"></card-loading>
    <div
      v-else
      class="cb-card-container d-flex flex-direction-row p25 pB20"
      :style="{ backgroundColor: cardStyle.backgroundColor }"
    >
      <div class="d-flex flex-direction-column">
        <div
          class="f15 bold mB-auto"
          :style="{ color: cardStyle.primaryColor }"
        >
          {{ cardData.title || 'Reading Card' }}
        </div>
        <div
          class="mB-auto pointer d-flex "
          @click="
            () => {
              if (cardDataValue != '--') {
                triggerAction()
              }
            }
          "
        >
          <div v-if="cardData.image" class="cb-card-image">
            <img :src="getPreviewUrl(cardData.image)" />
          </div>
          <div class="d-flex flex-direction-column">
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
            <template v-else>
              <div
                class="f35"
                :style="{
                  color: cardStyle.primaryColor,
                  ...getAdditionalStyle(cardStyle),
                }"
                v-if="localUnit"
              >
                <span
                  class=" bold weather-unit"
                  v-if="localUnit.position === 1"
                  >{{ localUnit.unit }}</span
                >
                {{ cardDataValue }}
                <span class="f30 bold" v-if="localUnit.position === 2">{{
                  localUnit.unit
                }}</span>
              </div>
              <div
                class="f35"
                :style="{
                  color: cardStyle.primaryColor,
                  ...getAdditionalStyle(cardStyle),
                }"
                v-else
              >
                <span class=" bold weather-unit" v-if="unit.includes('$')">{{
                  unit
                }}</span>
                {{ cardDataValue }}
                <span class="f30 bold" v-if="!unit.includes('$')">{{
                  unit
                }}</span>
              </div>
            </template>
            <div class="f13" :style="{ color: cardStyle.secondaryColor }">
              {{ cardData.period || 'This Month' }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Card from '../base/Card'
export default {
  extends: Card,
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
