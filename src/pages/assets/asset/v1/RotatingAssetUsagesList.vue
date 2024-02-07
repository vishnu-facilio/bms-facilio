<template>
  <el-dialog
    :visible="true"
    :before-close="closeDialog"
    class="rotating-asset fc-dialog-center-container"
    width="50%"
    height="70%"
    :show-close="false"
    :append-to-body="true"
  >
    <el-tabs v-model="activeTab" class="rotating-asset-tabs">
      <el-tab-pane
        v-for="tab in tabList"
        :key="tab.name"
        :label="tab.displayName"
        :name="tab.name"
      >
        <div class="rotating-asset-msg-container">
          <fc-icon
            group="alert"
            name="triangle-warning"
            class="warning-icon"
          ></fc-icon>
          <span class="rotating-asset-msg"> {{ tab.message }}</span>
        </div>
        <RotatingAssetUsagesTable
          :list="tab.list"
          :moduleName="tab.moduleName"
        ></RotatingAssetUsagesTable>
      </el-tab-pane>
    </el-tabs>
    <fc-icon
      group="action"
      name="cross"
      class="asset-cross-icon"
      size="16"
      color="#000"
      @click="closeDialog"
    ></fc-icon>
  </el-dialog>
</template>

<script>
import RotatingAssetUsagesTable from './RotatingAssetUsagesTable.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['plannedMaintenanceList', 'inspectionTemplateList'],
  components: { RotatingAssetUsagesTable },
  created() {
    this.init()
  },
  data() {
    return {
      activeTab: null,
      tabList: [],
    }
  },
  methods: {
    init() {
      let tabList = []
      if (!isEmpty(this.plannedMaintenanceList)) {
        tabList.push({
          name: 'plannedPreventiveMaintenanace',
          displayName: this.$t(
            'common.inventory.planned_preventive_maintenanace'
          ),
          message: this.$t('common.inventory.pm_warning_msg'),
          list: this.plannedMaintenanceList,
          moduleName: 'plannedmaintenance',
        })
      }
      if (!isEmpty(this.inspectionTemplateList)) {
        tabList.push({
          name: 'inspectionTemplate',
          displayName: this.$t('common.inventory.inspection_template'),
          message: this.$t('common.inventory.inspection_warning_msg'),
          list: this.inspectionTemplateList,
          moduleName: 'inspectionTemplate',
        })
      }
      this.tabList = tabList
      this.activeTab = this.$getProperty(tabList, '0.name')
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';

.rotating-asset.fc-dialog-center-container .el-dialog__body {
  position: relative;
  padding: 0;
}
.rotating-asset.rotating-asset.fc-dialog-center-container .el-dialog__header {
  padding: 6px 0;
  border: transparent;
}
.rotating-asset-tabs {
  .el-tabs__nav-wrap {
    padding-left: 16px;
  }
  .el-tabs__item {
    text-transform: capitalize;
  }
  .el-tabs__header {
    margin: 0;
  }
}
.rotating-asset-msg-container {
  border-top: 1px solid #f5f5f5;
  color: #000;
  border-bottom: 1px solid #f5f5f5;
  background-color: #f8c56e4d;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 40px;
  .warning-icon {
    padding-right: 12px;
  }
}
</style>
<style scoped lang="scss">
.asset-cross-icon {
  position: absolute;
  top: 6px;
  right: 12px;
  cursor: pointer;
  &:hover {
    color: #0074d1;
  }
}
.asset-list-header {
  display: flex;
  flex-direction: row-reverse;
}
</style>
