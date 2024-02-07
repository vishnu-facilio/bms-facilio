<template>
  <div>
    <div
      class="label-txt-black pT15 pB15 fw-bold  pL30 pR30 header-bottom"
    >
      {{$t('common.tabs.readings')}}
    </div>
    <el-tabs v-model="activeTab" :before-leave="resetWidgetSize">
      <el-tab-pane
        v-for="(tab, index) in tabs"
        :key="tab.name"
        :name="tab.name"
        :label="tab.displayName"
        lazy
      >
        <list
          v-if="tab.isActive"
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

    <!-- portal for pagination and search -->
    <div class="widget-topbar-actions">
      <portal-target :name="widget.key + '-topbar'"></portal-target>
    </div>
    <!-- portal -->
  </div>
</template>
<script>
import List from './list/Readings'

export default {
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
      activeTab: 'connected',
      defaultWidgetHeight: this.layoutParams.h,
    }
  },
  components: { List },
  computed: {
    tabs() {
      let activeTab = this.activeTab

      return [
        {
          type: 'connected',
          name: 'connected',
          displayName: this.$t('asset.readings.connected'),
          isActive: activeTab === 'connected',
        },
        {
          type: 'logged',
          name: 'logged',
          displayName: this.$t('asset.readings.logged'),
          isActive: activeTab === 'logged',
        },
        {
          type: 'formula',
          name: 'formula',
          displayName: this.$t('asset.readings.kpis'),
          isActive: activeTab === 'formula',
        },
        {
          type: 'available',
          name: 'available',
          displayName: this.$t('asset.readings.available'),
          isActive: activeTab === 'available',
        },
      ]
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
  .widget-topbar-actions{
    top: 46px;
}
.header-bottom{
  border-bottom: 1px solid #e5e5ea;
}
</style>
