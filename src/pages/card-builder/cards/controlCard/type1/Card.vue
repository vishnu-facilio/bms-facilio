<template>
  <div class="dragabale-card height100">
    <card-loading v-if="isLoading"></card-loading>
    <div
      v-else
      class="cb-card-container d-flex flex-direction-column p25 pB20"
      :style="{ backgroundColor: cardStyle.backgroundColor }"
    >
      <div class="f15 bold mB-auto" :style="{ color: cardStyle.primaryColor }">
        {{ cardData.title || 'Reading Card' }}
      </div>
      <div class="mB-auto pT10">
        <div
          class="f30"
          :style="{
            color: cardStyle.primaryColor,
            ...getAdditionalStyle(cardStyle),
          }"
          v-if="cardStyle.displayValue"
        >
          {{ cardStyle.displayValue }}
        </div>
        <div
          class="f30"
          :style="{
            color: cardStyle.primaryColor,
            ...getAdditionalStyle(cardStyle),
          }"
          v-else
        >
          {{ (cardData.value && formatDecimal(cardData.value.value)) || '--' }}
          <span class="f12 bold">{{
            cardData.value && cardData.value.unit
          }}</span>
        </div>
        <div class="f12" :style="{ color: cardStyle.secondaryTextColor }">
          {{
            cardStyle.showSecondaryText &&
            cardData.value &&
            cardData.value.label
              ? cardData.value.label
              : ''
          }}
        </div>
      </div>
      <!-- prettier-ignore -->
      <component :is="'style'">
        .control-button-{{componentKey}} {
          color: {{cardStyle.secondaryColor}};
          border: 1px solid {{cardStyle.secondaryColor}};
        }
        .control-button-{{componentKey}}.fc__border__btn1:hover,
        .control-button-{{componentKey}}.fc__border__btn1:focus {
          background-color: {{cardStyle.secondaryColor}};
          border-color: {{cardStyle.secondaryColor}};
          color: #fff;
        }
      </component>
      <!-- prettier-ignore -->
      <el-button
        size="small"
        :class="`control-card-set text-uppercase fc__border__btn1  mB0 mT10 p10 pR10 pL10 f10 control-button-${componentKey}`"
        style="max-width: 70px;"
        @click="triggerAction('set-reading-button')"
        >{{ (control && control.buttonLabel) || 'SET' }}</el-button
      >
    </div>
  </div>
</template>

<script>
import Card from '../base/Card'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: Card,
  data() {
    return {
      showSetDialog: false,
    }
  },
  computed: {
    control() {
      let action = (this.cardDrilldown || {})['set-reading-button']
      let { control = {} } = (action || {}).data || {}

      if (
        isEmpty(control.controlPointId) &&
        isEmpty(control.controlGroupId) &&
        !isEmpty(this.cardData.control)
      ) {
        control = this.cardData.control
      }
      return control
    },
  },
}
</script>
