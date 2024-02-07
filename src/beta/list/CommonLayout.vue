<template>
  <FSidebar
    :sidebarWidth="250"
    toggleText="Views"
    :title="$t('viewsmanager.list.views')"
    :defaultOpen="false"
  >
    <template #sidebar>
      <ViewsList
        :groupViews="groupViews"
        :moduleName="moduleName"
        :hideArrowIcon="true"
        @onChange="goToView"
      />
    </template>
    <template #content>
      <FContainer padding="containerNone containerXLarge">
        <FContainer
          backgroundColor="backgroundMidgroundSubtle"
          borderRadius="high"
          padding="containerXLarge"
          display="flex"
          justifyContent="space-between"
          alignItems="center"
        >
          <div>
            <ViewHeader
              :moduleName="moduleName"
              :showRearrange="showViewRearrange"
              :showEditIcon="showViewEdit"
              :pathPrefix="pathPrefix"
              :retainFilters="filtersToRetain"
              :canShowViewsSidePanel.sync="canShowViewsSidePanel"
              @viewsLoaded="$emit('viewsLoaded')"
              @hideViewSidebar="val => (hideViewSidebar = val)"
            >
            </ViewHeader>
          </div>
          <div>
            <slot name="header"></slot>
          </div>
        </FContainer>
        <FContainer
          border="solid 1px"
          borderRadius="high"
          borderColor="borderNeutralBaseSubtle"
          marginTop="containerLarge"
          overflow="hidden"
        >
          <slot name="sub-header"></slot>
          <slot></slot>
        </FContainer>
      </FContainer>
    </template>
  </FSidebar>
</template>

<script>
import { FContainer, FSidebar } from '@facilio/design-system'
import ViewHeader from 'src/beta/list/views/ViewHeader.vue'
import CommonLayout from 'src/newapp/list/CommonLayout.vue'
import ViewsList from 'src/beta/list/views/ViewsList.vue'
export default {
  extends: CommonLayout,
  name: 'CommonLayout',
  components: { FContainer, ViewHeader, FSidebar, ViewsList },
}
</script>
