<template>
  <div class="pane-switch" :class="{ expanded: isPaneExpanded }">
    <div class="">
      <div
        class="text-center analytics-report-icon-bg line-height50 height50"
        :class="{ 'analytics-report-icon-bg-active': activeTab == 'CONFIG' }"
        @click="switchTab('CONFIG')"
      >
        <InlineSvg
          src="statistics"
          iconClass="icon icon-sm-md  vertical-middle"
        ></InlineSvg>
      </div>
      <div
        class="text-center analytics-report-icon-bg line-height50 height50"
        :class="{
          'analytics-report-icon-bg-active': activeTab == 'SETTINGS',
        }"
        @click="switchTab('SETTINGS')"
      >
        <InlineSvg
          src="settings-grey"
          iconClass="icon icon-sm-md vertical-middle"
        ></InlineSvg>
      </div>
      <div
        @click="togglePaneVisibility()"
        class="text-center analytics-report-icon-bg line-height50 height50"
      >
        <InlineSvg
          src="arrow-pointing-to-left2"
          iconClass="icon icon-sm-md vertical-middle"
          :class="[{ rotate180: !isPaneExpanded }]"
          class="pane-arrow"
        ></InlineSvg>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['isPaneExpanded', 'activeTab'],
  methods: {
    togglePaneVisibility() {
      this.$emit('update:isPaneExpanded', !this.isPaneExpanded)
    },
    switchTab(tab) {
      this.$emit('update:activeTab', tab)
    },
  },
}
</script>

<style lang="scss" scoped>
.pane-switch {
  border: 1px solid #eceded;
  background: #fff;
  height: 150px;
  width: 50px;
  position: absolute;
  border-left: none;
  border-top: none;

  top: 0;
  left: 0;
  &.expanded {
    // left: 345px;
  }

  z-index: 120;
  transition: left 0.5s ease;

  .pane-arrow {
    transition: transform 0.3s ease 0.5s;
  }
}
</style>
