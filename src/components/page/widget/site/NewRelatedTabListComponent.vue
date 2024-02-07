<template>
  <div class="new-related-tab-list">
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
          class="height100 related-tab-list  overflow-x"
        ></list>
      </el-tab-pane>
    </el-tabs>

    <div class="widget-topbar-actions" :key="activeTab + '-topbar'">
      <portal-target
        :name="activeTab + '-topbar'"
        :key="activeTab + '-topbar'"
      ></portal-target>
    </div>

    <portal :to="widget.key + '-title-section'" slim>
      <div class="flex-middle justify-content-space space-white-header ">
        <div class="f18 bold">
          {{ `${moduleHeaderName}` }}
        </div>
        <div
          v-if="$hasPermission('space:CREATE')"
          @click="openNewForm"
          class="fc-pink f13 bold pointer widget-tabs-header-create"
        >
          <el-button
            type="primary"
            :disabled="decommission"
            class="tab-header-btn"
          >
            {{ buttonText }}
          </el-button>
        </div>
      </div>
    </portal>
  </div>
</template>

<script>
import RelatedTabListComponent from './RelatedTabListComponent.vue'
export default {
  extends: RelatedTabListComponent,
  computed: {
    moduleDisplayName() {
      let { widget } = this
      let { relatedList } = widget || {}
      let { module } = relatedList || {}
      let { displayName } = module || {}
      return displayName || ''
    },
    moduleHeaderName() {
      return this.moduleDisplayName + 's'
    },
    decommission() {
      return this.$getProperty(this, 'details.decommission', false)
    },
  },
}
</script>

<style lang="scss">
.new-related-tab-list {
  .table-subheading {
    .img-container {
      height: 20px;
      width: 20px;
    }
  }
  .space-row {
    height: 54px;
  }
  .icon-xlg {
    height: 20px !important;
    width: 20px !important;
  }
  .el-tabs__nav-wrap {
    padding-left: 17px;
  }
  .el-table {
    th.el-table__cell {
      background-color: #f3f1fc;
    }
  }
}
</style>

<style lang="scss" scoped>
.new-related-tab-list {
  .widget-topbar-actions {
    position: absolute;
    right: 25px;
    top: 52px;
  }
}
.widget-tabs-header-create {
  .tab-header-btn {
    color: #fff;
    background-color: #3ab2c2;
    border-color: #3ab2c2;
    border-radius: 3px;
    padding: 9px 16px;
    height: 32px;
  }
}
.space-white-header {
  background: #ffffff;
  padding: 15px;
  height: 59px;
}
</style>
