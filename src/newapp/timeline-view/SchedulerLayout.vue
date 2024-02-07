<template>
  <div class="layout common-list-layout d-flex">
    <slot name="views-list">
      <ViewsList
        v-if="canShowViewsSidePanel"
        :canShowViewsSidePanel.sync="canShowViewsSidePanel"
        :groupViews="groupViews"
        :moduleName="moduleName"
        @onChange="goToView"
      ></ViewsList>
    </slot>

    <div class="list-content-container">
      <div class="scheduler-header-container">
        <slot name="header-container">
          <div class="flex-middle">
            <ViewHeader
              :moduleName="moduleName"
              :showCurrentViewOnly="true"
              :pathPrefix="`/app/scheduler/${moduleName}`"
              :canShowViewsSidePanel.sync="canShowViewsSidePanel"
            ></ViewHeader>
            <slot name="calendar-actions"> </slot>
          </div>
          <div class="d-flex">
            <slot name="header"></slot>
          </div>
        </slot>
      </div>

      <div class="common-content-container" style="height: calc(100% - 60px);">
        <slot></slot>
      </div>
    </div>
  </div>
</template>
<script>
import CommonLayout from 'src/newapp/list/DeprecatedCommonLayout.vue'
import ViewHeader from './SchedulerViewHeader'
import ViewsList from './SchedulerViewSidePanel'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: CommonLayout,
  components: { ViewHeader, ViewsList },
  methods: {
    goToView(view) {
      if (!isEmpty(view)) {
        let { name: viewname } = view
        this.$router
          .push({ params: { viewname } })
          .catch(error => console.warn('Could not switch view\n', error))
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.scheduler-header-container {
  height: 56px;
  padding: 15px;
  display: flex;
  background: #fff;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.11);
}
</style>
