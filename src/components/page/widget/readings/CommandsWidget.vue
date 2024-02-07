<template>
  <div>
    <div
      class="label-txt-black pT15 pB15 fw-bold  pL30 pR30 header-bottom"
    >
      {{$t('asset.assets.commands')}}
    </div>
    <el-tabs v-model="activeTab">
      <el-tab-pane
        v-for="(tab) in tabs"
        :key="tab.name"
        :name="tab.name"
        :label="tab.displayName"
      >
        <Readings
          v-if="activeTab === 'readings'"
          :key="tab.name"      
          :isActive="tab.isActive"
          :assetId="details.id"
          :details="details"
          :portalName="widget.key +'-topbar'"
          :moduleName="moduleName"
        ></Readings>
        <logs
          v-if="activeTab === 'logs'"
          :key="tab.name"       
          :isActive="tab.isActive"
          :assetId="details.id"
          :details="details"
          :portalName="widget.key +'-topbar'"
          :moduleName="moduleName"
        ></logs>
      </el-tab-pane>
      
    </el-tabs>
    <!-- portal for pagination and search -->
    <div class="widget-topbar-actions widget-topbar-actions-change">
      <portal-target :name="widget.key + '-topbar'"></portal-target>
    </div>
    <!-- portal -->
  </div>
</template>
<script>
import Readings from './list/WritableReadings'
import logs from './list/CommandLogs'

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
      activeTab: 'readings',
      defaultWidgetHeight: this.layoutParams.h,
    }
  },
  components: { Readings,logs},
  computed: {
    tabs() {
      let activeTab = this.activeTab

      return [
        {
          name: 'readings',
          displayName: 'Readings',
          isActive: activeTab === 'readings',
        },
        {
          name: 'logs',
          displayName: this.$t('common.tabs.logs'),
          isActive: activeTab === 'logs',
        }
      ]
    }
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
