<template>
  <div v-if="isActiveSummaryTab" class="related-readings-widget">
    <div
      v-if="isLoading"
      class="loading-container d-flex justify-content-center height100"
    >
      <spinner :show="isLoading"></spinner>
    </div>
    <div
      v-else-if="$validation.isEmpty(readingRelationships)"
      class="height100 width100 fc-empty-center"
    >
      <InlineSvg
        src="svgs/emptystate/readings-empty"
        iconClass="icon text-center icon-130 emptystate-icon-size"
      ></InlineSvg>
      <div class="fc-black-dark f18 bold">
        {{ $t('space.sites.related_readings.no_readings_module') }}
      </div>
    </div>
    <el-tabs v-else v-model="activeTab" :lazy="true">
      <template v-if="readingRelationships.length < 4">
        <el-tab-pane
          v-for="(tab, index) in readingRelationships"
          :key="tab.linkName"
          :name="tab.linkName"
          :label="tab.name"
        >
          <RelatedReadingsList
            v-if="activeTab === tab.linkName"
            :key="`${tab.linkName}${index}`"
            :siteId="details.id"
            :relDetail="tab"
            :isActive="isActive(tab.linkName)"
            :portalName="widget.key + '-topbar'"
            v-bind="$attrs"
          />
        </el-tab-pane>
      </template>
      <template v-else>
        <el-tab-pane
          v-for="(tab, index) in baseTabs"
          :key="tab.linkName"
          :name="tab.linkName"
          :label="tab.name"
        >
          <RelatedReadingsList
            v-if="activeTab === tab.linkName"
            :key="`${tab.name}${index}`"
            :siteId="details.id"
            :relDetail="tab"
            :isActive="isActive(tab.linkName)"
            :portalName="widget.key + '-topbar'"
            v-bind="$attrs"
          />
        </el-tab-pane>
        <el-tab-pane
          :key="extraTab.linkName"
          :name="extraTab.linkName"
          :label="extraTab.name"
        >
          <RelatedReadingsList
            v-if="activeTab === extraTab.linkName"
            :key="`${extraTab.linkName}`"
            :siteId="details.id"
            :relDetail="extraTab"
            :isActive="isActive(extraTab.linkName)"
            :portalName="widget.key + '-topbar'"
            v-bind="$attrs"
          />
        </el-tab-pane>
        <el-tab-pane name="extraTabs">
          <span slot="label" @click="preventTabClick">
            <el-dropdown trigger="click" @command="handleCommand">
              <span class="more-icon-container">
                <i class="el-icon-more"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  v-for="(tab, index) in extraTabs"
                  :key="`${tab.linkName}${index}`"
                  :command="tab"
                  >{{ tab.name }}</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown></span
          >
        </el-tab-pane>
      </template>
    </el-tabs>
    <portal :to="widget.key + '-title-section'">
      <div class="flex-middle justify-content-space reading-widget-header">
        <div class="fc-black-18 font-medium">
          {{ $t('space.sites.related_readings._related_readings') }}
        </div>
      </div>
    </portal>
    <div class="widget-topbar-actions">
      <portal-target :name="widget.key + '-topbar'"></portal-target>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import RelatedReadingsList from './list/RelatedReadingsList'
import { isEmpty } from '@facilio/utils/validation'
export default {
  components: { RelatedReadingsList },
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
      isLoading: false,
      activeTab: null,
      extraTab: null,
      readingRelationships: [],
    }
  },
  computed: {
    isActiveSummaryTab() {
      let currentTabName = this.$getProperty(this, '$attrs.tab.name')
      let activeTabName = this.$getProperty(this, '$attrs.activeTab')
      return currentTabName === activeTabName
    },
    baseTabs() {
      let { readingRelationships } = this
      if (readingRelationships?.length > 3) {
        return readingRelationships.slice(0, 2)
      }
      return []
    },
    extraTabs() {
      let { readingRelationships } = this
      if (readingRelationships?.length > 3) {
        return readingRelationships
          .slice(2)
          .filter(
            relationTab => relationTab.linkName !== this.extraTab.linkName
          )
      }
      return []
    },
  },
  created() {
    this.loadRelationsWithReading()
  },
  methods: {
    preventTabClick(e) {
      e.stopPropagation()
    },
    isActive(tabName) {
      return this.activeTab === tabName
    },
    async loadRelationsWithReading() {
      this.isLoading = true
      let moduleName = this.$getProperty(this, 'moduleName')
      let resourceId = this.$getProperty(this, 'details.id')
      try {
        let { data, error } = await API.get(
          'v2/reading/latestdata/getReadingsRelationships',
          {
            moduleName,
            resourceId,
          }
        )
        this.readingRelationships = []
        if (!error) {
          let relationships = this.$getProperty(data, 'result', [])
          if (!isEmpty(relationships)) {
            this.readingRelationships = relationships
            this.activeTab = relationships[0]?.linkName
            if (relationships.length > 3) this.extraTab = relationships[3]
          }
        }
      } catch (e) {
        this.readingRelationships = []
      }
      this.isLoading = false
    },
    handleCommand(command) {
      this.extraTab = command
      this.activeTab = command.linkName
    },
  },
}
</script>

<style lang="scss">
.related-readings-widget {
  #tab-extraTabs {
    padding: 0px;
    margin: 0px 5px;
  }
  .el-tabs__nav-wrap::after {
    height: 0px;
  }
  .el-tabs__nav-wrap {
    width: 60%;
    padding-left: 18px;
  }
  .el-tabs__header {
    border-bottom: 1px solid #e3e3e3;
  }
}
</style>

<style lang="scss" scoped>
.related-readings-widget {
  .more-icon-container {
    font-size: 16px;
    padding: 5px;
    padding-left: 6px;
    padding-right: 6px;
    border-radius: 20%;

    &:hover {
      background: rgb(202 212 216 / 30%);
    }
  }
  .widget-topbar-actions {
    position: absolute;
    right: 15px;
    top: 40px;
    width: 40%;
    height: 50px;
    .vue-portal-target {
      margin-top: 0px;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: right;
      .f-search-container {
        margin-top: 10px;
      }
    }
  }
}
.reading-widget-header {
  padding: 15px;
  padding-bottom: 10px;
  background: #ffffff;
}
</style>

<style lang="scss">
.related-readings-widget {
  .widget-topbar-actions {
    .vue-portal-target {
      .f-search-container {
        margin-top: 10px;
        margin-right: 0px;
      }
      .seperator {
        margin: 15px;
        margin-top: 20px;
        font-size: 20px;
        color: #ced1d5;
      }
    }
  }
  .el-tabs {
    .el-tabs__content {
      height: calc(100% - 50px);
      .el-tab-pane {
        padding-bottom: 0px;
      }
    }
  }
}
</style>
