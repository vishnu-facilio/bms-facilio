<template>
  <div :class="[newSiteSummary ? 'site-readings-widget' : '']">
    <el-tabs v-model="activeTab" :before-leave="resetWidgetSize">
      <el-tab-pane
        v-for="(tab, index) in tabs"
        :key="tab.name"
        :name="tab.name"
        :label="tab.displayName"
        lazy
      >
        <list
          :key="`${tab.name}${index}`"
          :type="tab.type"
          :url="tab.url"
          :isActive="tab.isActive"
          :assetId="details.id"
          :details="details"
          :portalName="widget.key + '-topbar'"
          :resize="resizeWidgetSize"
          :reset="resetWidgetSize"
          :moduleName="moduleName"
        ></list>
      </el-tab-pane>
    </el-tabs>

    <portal v-if="newSiteSummary" :to="widget.key + '-title-section'">
      <div
        class="flex-middle justify-content-space space-transparent-header reading-widget-header"
      >
        <div class="fc-black-18 font-medium">
          {{ readingWidgetHeader }}
        </div>
      </div>
    </portal>

    <!-- portal for pagination and search -->
    <div class="widget-topbar-actions">
      <portal-target :name="widget.key + '-topbar'"></portal-target>
    </div>
    <!-- portal -->
  </div>
</template>
<script>
import List from './list/Readings'
import { isEmpty } from '@facilio/utils/validation'
import capitalize from 'lodash/capitalize'
import SpaceMixin from 'pages/spacemanagement/overview/helpers/SpaceHelper'

export default {
  mixins: [SpaceMixin],
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'sectionKey',
    'widget',
    'resizeWidget',
    'moduleName',
  ],
  data() {
    return {
      activeTab: 'logged',
      defaultWidgetHeight: this.layoutParams.h,
    }
  },
  components: { List },
  computed: {
    tabs() {
      let activeTab = this.activeTab
      return [
        {
          type: 'logged',
          name: 'logged',
          displayName: this.$t('asset.readings.logged'),
          isActive: activeTab === 'logged',
        },
        {
          type: 'available',
          name: 'available',
          displayName: this.$t('asset.readings.available'),
          isActive: activeTab === 'available',
        },
      ]
    },
    readingWidgetHeader() {
      let { moduleName } = this
      if (!isEmpty(moduleName)) {
        return `${capitalize(moduleName)} ${this.$t('common.tabs.readings')}`
      }
      return this.$t('common.tabs.readings')
    },
  },
  methods: {
    resizeWidgetSize() {
      return new Promise(resolve => {
        this.resizeWidget({ h: 14 })
        this.$nextTick(resolve)
      })
    },
    resetWidgetSize() {
      return new Promise(resolve => {
        this.resizeWidget({ h: this.defaultWidgetHeight })
        this.$nextTick(resolve)
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.site-readings-widget {
  .widget-topbar-actions {
    position: absolute;
    right: 15px;
    top: 30px;
  }
}
.reading-widget-header {
  padding: 15px;
  padding-bottom: 10px;
  background: #ffffff;
}
</style>
