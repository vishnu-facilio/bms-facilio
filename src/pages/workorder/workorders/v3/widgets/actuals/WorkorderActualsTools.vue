<template>
  <div>
    <LineItemList
      v-bind="$attrs"
      ref="lineItemList"
      :config="listConfiguration"
      :moduleName="moduleName"
      :moduleDisplayName="moduleDisplayName"
      :widgetDetails="widgetDetails"
      :additionalParams="additionalParams"
      @onCreateOrUpdate="refreshData"
      @clickedActionBtn="btnValue"
      viewname="all"
    >
    </LineItemList>
    <portal
      :to="`${$attrs.groupKey}-title-section`"
      v-if="$attrs.activeTab === 'workordertools'"
    >
      <div class="workorderactuals_items-portal">
        <el-dropdown
          placement="bottom-start"
          @command="action => inventoryRequestDropdownAction(action)"
        >
          <el-button type="primary" size="small">
            {{ $t('common.inventory.inventory_request') }}
            <i class="el-icon-arrow-down el-icon--right"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item :key="1" :command="'create_inventory_request'">{{
              $t('common.inventory.create_inventory_request')
            }}</el-dropdown-item>
            <el-dropdown-item :key="2" :command="'view_inventory_request'"
              >{{ $t('common.inventory.view_inventory_request') }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </portal>
  </div>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemList.vue'
import WorkorderActualsCommon from './WorkorderActualsCommon'

export default {
  props: ['widget', 'details', 'resizeWidget', 'module'],
  extends: WorkorderActualsCommon,
  components: { LineItemList },
  computed: {
    moduleDisplayName() {
      return this.$t('common.workorder_actuals.work_order_tool')
    },
    moduleName() {
      return 'workorderTools'
    },
  },
}
</script>
