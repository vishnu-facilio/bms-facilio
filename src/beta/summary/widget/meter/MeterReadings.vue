<!-- portal for pagination and search
    <div class="widget-topbar-actions">
      <portal-target :name="widget.key + '-topbar'"></portal-target>
    </div> -->

<template>
  <FContainer paddingTop="containerXLarge">
    <FTabs v-model="activeTab" :tabsList="tabs" :appearance="selectType">
      <FTabPane v-for="tab in tabs" :activeKey="tab.value" :key="tab.value">
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
          :widget="widget"
        ></list>
      </FTabPane>
    </FTabs>
    <div class="widget-topbar-actions">
      <portal-target :name="widget.key + '-topbar'"></portal-target>
    </div>
  </FContainer>
</template>
<script>
import List from 'src/components/page/widget/readings/list/Readings.vue'
import { FContainer, FTabs, FTabPane } from '@facilio/design-system'

export default {
  components: { List, FContainer, FTabs, FTabPane },
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
      activeTab: 'available',
      defaultWidgetHeight: this.layoutParams.h,
    }
  },
  computed: {
    tabs() {
      let { activeTab } = this
      let { details } = this
      let isVirtual = this.$getProperty(details, 'isVirtual', false)
      if (isVirtual) {
        return [
          {
            type: 'logged',
            name: 'logged',
            value: 'logged',
            label: this.$t('asset.readings.logged'),
            isActive: activeTab === 'logged',
          },
          {
            type: 'formula',
            name: 'formula',
            value: 'formula',
            label: this.$t('asset.readings.kpis'),
            isActive: activeTab === 'formula',
          },
          {
            type: 'available',
            name: 'available',
            value: 'available',
            label: this.$t('asset.readings.available'),
            isActive: activeTab === 'available',
          },
        ]
      } else {
        return [
          {
            type: 'connected',
            name: 'connected',
            value: 'connected',
            label: this.$t('asset.readings.connected'),
            isActive: activeTab === 'connected',
          },
          {
            type: 'logged',
            name: 'logged',
            value: 'logged',
            label: this.$t('asset.readings.logged'),
            isActive: activeTab === 'logged',
          },
          {
            type: 'formula',
            name: 'formula',
            value: 'formula',
            label: this.$t('asset.readings.kpis'),
            isActive: activeTab === 'formula',
          },
          {
            type: 'available',
            name: 'available',
            value: 'available',
            label: this.$t('asset.readings.available'),
            isActive: activeTab === 'available',
          },
        ]
      }
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
.widget-topbar-actions {
  top: 46px;
}
.header-bottom {
  border-bottom: 1px solid #e5e5ea;
}
</style>
