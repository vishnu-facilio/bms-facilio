<template>
  <el-tabs v-model="tabName">
    <el-tab-pane :label="$t('common.header.items')" name="item">
      <InventoryRequestItems
        v-if="tabName == 'item'"
        v-bind="$attrs"
        class="height-100"
      ></InventoryRequestItems>
    </el-tab-pane>
    <el-tab-pane :label="$t('common.header.tools')" name="tool">
      <InventoryRequestTools
        v-if="tabName == 'tool'"
        v-bind="$attrs"
        class="height-100"
      ></InventoryRequestTools>
    </el-tab-pane>
  </el-tabs>
</template>
<script>
import InventoryRequestItems from 'src/pages/Inventory/InventoryRequest/component/InventoryRequestItems.vue'
import InventoryRequestTools from 'src/pages/Inventory/InventoryRequest/component/InventoryRequestTools.vue'
export default {
  components: {
    InventoryRequestItems,
    InventoryRequestTools,
  },
  data() {
    return {
      tabName: 'item',
    }
  },
  computed: {},
}
</script>
