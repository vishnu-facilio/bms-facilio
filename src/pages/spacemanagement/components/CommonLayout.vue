<template>
  <div class="layout common-list-layout d-flex">
    <div class="list-content-container">
      <div
        :class="[
          'view-header-container subheader-section',
          hasQuery === true && 'filter-class',
        ]"
        style="z-index : 1"
      >
        <slot name="views">
          <ViewHeader
            :moduleName="moduleName"
            :showRearrange="showViewRearrange"
            :showCurrentViewOnly="showSearch"
            :maxVisibleMenu="visibleViewCount"
            :showEditIcon="showViewEdit"
            :pathPrefix="pathPrefix"
            :retainFilters="filtersToRetain"
            :canShowViewsSidePanel.sync="canShowViewsSidePanel"
            @viewsLoaded="$emit('viewsLoaded')"
            :key="moduleName"
          >
          </ViewHeader>
        </slot>
        <div class="fR fc-subheader-right">
          <slot name="header"></slot>
        </div>
      </div>
      <div class="height100 common-content-container">
        <slot name="content"></slot>
        <slot></slot>
      </div>
    </div>
    <div :v-if="canShowViewsSidePanel" style="z-index:1">
      <slot name="views-list">
        <el-container>
          <el-aside style="postiton:absolute;width: unset !important;">
            <ViewsList
              style="position:fixed;top:60px;left:60px;height:100vh"
              v-if="canShowViewsSidePanel"
              :canShowViewsSidePanel.sync="canShowViewsSidePanel"
              :groupViews="groupViews"
              :moduleName="moduleName"
              @onChange="goToView"
              :key="moduleName"
            ></ViewsList>
          </el-aside>
        </el-container>
      </slot>
    </div>
  </div>
</template>
<script>
import ViewHeader from './ViewHeader'
import CommonLayout from 'newapp/list/DeprecatedCommonLayout.vue'
import ViewsList from './ViewsSidePanel'
import { isEmpty } from '@facilio/utils/validation'
import { isWebTabsEnabled } from '@facilio/router'

export default {
  extends: CommonLayout,
  components: {
    ViewHeader,
    ViewsList,
  },
  watch: {
    canShowViewsSidePanel: {
      async handler(newVal) {
        this.$emit('resizeList', newVal)
      },
      immediate: true,
    },
  },
  methods: {
    goToView(view, group = null) {
      if (!isEmpty(view)) {
        let { name: viewname } = view
        let { moduleName } = this
        if (viewname === 'rearrange-views') {
          this.showReorderPanel = true
          return
        }

        let { query } = this.$route

        if (isWebTabsEnabled()) {
          this.$router
            .push({
              name:
                moduleName === 'building'
                  ? 'building-portfolio-module'
                  : 'site-portfolio-module',
              params: { moduleName, viewname },
            })
            .catch(error => console.warn('Could not switch view\n', error))
        } else {
          let path = this.getPathForView(view, group)
          this.$router.push({ path, query }).catch(() => {})
        }
      }
    },
  },
}
</script>
