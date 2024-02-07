<template>
  <div>
    <el-tabs v-model="activeTab">
      <el-tab-pane
        v-for="tab in tabs"
        :key="tab.linkName"
        :name="tab.linkName"
        :label="tab.displayName"
        lazy
      >
        <list
          v-if="activeTab === tab.linkName"
          :module="tab.moduleName"
          :linkName="tab.linkName"
          :parentModule="moduleName"
          :displayName="tab.displayName"
          :isActive="tab.isActive"
          :portalName="tab.linkName + '-topbar'"
          :key="tab.linkName"
          :details="details"
          class="height100 related-tab-list overflow-x"
        ></list>
      </el-tab-pane>
    </el-tabs>

    <div class="widget-topbar-actions" :key="activeTab + '-topbar'">
      <portal-target
        :name="activeTab + '-topbar'"
        :key="activeTab + '-topbar'"
      ></portal-target>
    </div>

    <portal :to="sectionKey + '-title-section'" slim>
      <div
        v-if="$hasPermission('space:CREATE') && !decommission"
        @click="openNewForm"
        class="fc-pink f13 bold pointer"
      >
        <i class="el-icon-plus pR5"></i>
        {{ buttonText }}
      </div>
    </portal>
  </div>
</template>

<script>
import List from './SpaceList'
import { eventBus } from '@/page/widget/utils/eventBus'

export default {
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'sectionKey',
    'widget',
    'moduleName',
  ],
  data() {
    return {
      activeTab: this.moduleName === 'building' ? 'floor' : 'independentspace',
    }
  },
  components: { List },
  computed: {
    tabs() {
      if (this.moduleName === 'building') {
        return [
          {
            moduleName: 'floor',
            linkName: 'floor',
            displayName: 'Floors',
            isActive: this.activeTab === 'floor',
          },
          {
            moduleName: 'space',
            linkName: 'independentspace',
            displayName: 'Independent Spaces',
            isActive: this.activeTab === 'independentspace',
          },
          {
            moduleName: 'space',
            linkName: 'allspace',
            displayName: 'All Spaces',
            isActive: this.activeTab === 'allspace',
          },
        ]
      } else if (this.moduleName === 'site') {
        return [
          {
            moduleName: 'space',
            linkName: 'independentspace',
            displayName: 'Independent Spaces',
            isActive: this.activeTab === 'independentspace',
          },
          {
            moduleName: 'space',
            linkName: 'allspace',
            displayName: 'All Spaces',
            isActive: this.activeTab === 'allspace',
          },
        ]
      }
      return []
    },
    buttonText() {
      if (this.moduleName === 'building') {
        return this.activeTab === 'floor'
          ? this.$t('space.sites.newfloor')
          : this.$t('space.sites.new_space')
      } else if (this.moduleName === 'site') {
        return this.$t('space.sites.new_space')
      }
      return ''
    },
    decommission() {
      return this.$getProperty(this, 'details.decommission', false)
    },
  },
  methods: {
    openNewForm() {
      let { activeTab, moduleName } = this
      if (moduleName === 'building') {
        if (activeTab === 'floor') {
          eventBus.$emit('openSpaceManagementForm', {
            isNew: true,
            visibility: true,
            module: 'floor',
            building: this.details,
          })
        } else if (['independentspace', 'allspace'].includes(activeTab)) {
          eventBus.$emit('openSpaceManagementForm', {
            isNew: true,
            visibility: true,
            module: 'space',
            building: this.details,
          })
        }
      } else if (moduleName === 'site') {
        eventBus.$emit('openSpaceManagementForm', {
          isNew: true,
          visibility: true,
          module: 'space',
          site: this.details,
        })
      }
    },
  },
}
</script>
