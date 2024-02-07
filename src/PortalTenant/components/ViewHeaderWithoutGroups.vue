<template>
  <div class="view-header-wg" @click="toggleViewsSidePanel">
    <inline-svg
      :class="['hamburger-icon', canShowViewsSidePanel && 'active']"
      src="svgs/hamburger-menu"
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
import ViewHeader from './ViewHeader.vue'
import { mapGetters } from 'vuex'

export default {
  extends: ViewHeader,
  computed: {
    ...mapGetters({
      getCurrentViewDetail: 'view/getCurrentViewDetail',
    }),
    currentViewDisplayName() {
      let { currentGroup, currentView = '', currentViewDetail } = this
      let { views: currentViewList } = currentGroup || {}

      let { displayName } =
        (currentViewList || []).find(view => view.name === currentView) || {}

      return displayName || currentViewDetail?.displayName || ''
    },
    currentViewDetail() {
      return this.getCurrentViewDetail()
    },
  },
  methods: {
    toggleViewsSidePanel() {
      this.$emit('update:canShowViewsSidePanel', !this.canShowViewsSidePanel)
    },
  },
}
</script>
<style lang="scss">
.view-header-wg {
  display: flex;
  align-items: center;
  cursor: pointer;
  .active {
    background-color: #ebedf4;
    border-radius: 15%;
  }

  .hamburger-icon {
    padding: 5px;
    cursor: pointer;
    display: flex;

    &:hover,
    &.active {
      background-color: #ebedf4;
      border-radius: 15%;
    }
  }
  .view-name {
    margin: 0 20px 0 5px;
    color: #2d2d52;
    text-transform: capitalize;
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
