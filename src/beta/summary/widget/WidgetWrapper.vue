<template lang="">
  <f-card>
    <template #header v-if="!insideGroup">
      <div class="widget-title-wrapper">
        <slot name="header"
          ><span class="widget-title"> {{ widgetName }} </span></slot
        >

        <FDivider width="100%"></FDivider>
      </div>
    </template>
    <template #footer v-if="!insideGroup">
      <div class="widget-footer-wrapper">
        <slot name="footer"></slot>
      </div>
    </template>
    <slot></slot>
  </f-card>
</template>
<script>
import { FCard, FDivider } from '@facilio/design-system'
export default {
  components: { FCard, FDivider },
  inject: {
    insideGroup: {
      default: false,
    },
    widgetName: {
      default: null,
    },
  },
}
</script>
<style lang="scss">
.widget-title-wrapper {
  display: flex;
  height: 48px;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  .widget-title {
    padding: 12px;
    font-size: 14px;
    font-style: normal;
    font-weight: 500;
  }
}
</style>
