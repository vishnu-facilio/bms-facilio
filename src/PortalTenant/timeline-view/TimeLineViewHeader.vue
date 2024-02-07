<template>
  <div
    :class="['portal-timeline-view-header', canShowViewsSidePanel && 'active']"
    @click="openViewsSidePanel"
  >
    <inline-svg
      src="svgs/hamburger-menu"
      class="hamburger-icon"
      iconClass="icon icon-sm"
    ></inline-svg>
    <div
      v-if="$validation.isEmpty(currentViewDisplayName)"
      class="view-name-loading loading-shimmer"
    ></div>
    <div v-else class="view-name">{{ currentViewDisplayName }}</div>
  </div>
</template>
<script>
import ViewHeader from 'newapp/timeline-view/SchedulerViewHeader.vue'

export default {
  extends: ViewHeader,
  methods: {
    openViewsSidePanel() {
      this.$emit('update:canShowViewsSidePanel', !this.canShowViewsSidePanel)
    },
  },
}
</script>
<style lang="scss" scoped>
.portal-timeline-view-header {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px;

  &:hover,
  &.active {
    background: #f3f4f7;
    border-radius: 4px;
  }
  .hamburger-icon {
    padding: 5px;
    cursor: pointer;
    display: flex;
  }
  .view-name {
    margin: 0 20px 0 5px;
    color: #2d2d52;
    font-weight: 500;
    font-size: 14px;
  }
  .view-name-loading {
    height: 20px;
    width: 100px;
    border-radius: 5px;
    margin: 0 20px 0 5px;
  }
}
</style>
