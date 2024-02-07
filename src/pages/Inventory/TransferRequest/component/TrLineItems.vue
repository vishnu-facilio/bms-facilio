<template>
  <div ref="lineItems-container">
    <div
      v-if="$validation.isEmpty(details.transferrequestlineitems)"
      class="fc-align-center-column height100"
    >
      <div>
        <inline-svg
          src="svgs/emptystate/data-empty"
          class="vertical-middle'"
          iconClass="icon icon-60"
        ></inline-svg>
      </div>
      <div class="fc-black3-16 self-center bold">
        {{ $t('common._common.no_lineitems_available') }}
      </div>
    </div>
    <div v-else class="item-summary-table width100 pT20 pL30 pB20 pR30">
      <div class="widget-title mL0 mB10">
        {{ $t('common._common.transferred_items_tools') }}
      </div>
      <el-table
        :data="details.transferrequestlineitems"
        style="width: 100%"
        height="210px"
      >
        <el-table-column
          prop="inventoryTypeEnum"
          :label="$t('common._common.inventory_type')"
          min-width="200"
        >
        </el-table-column>
        <el-table-column
          prop="details.transferrequestlineitems"
          :formatter="inventoryName"
          :label="$t('common.roles.name')"
          min-width="200"
        >
        </el-table-column>
        <el-table-column
          prop="details.transferrequestlineitems"
          :formatter="description"
          :label="$t('common.roles.description')"
          min-width="200"
        >
        </el-table-column>
        <el-table-column
          prop="quantity"
          :label="$t('common._common.quantity_transferred')"
          min-width="150"
          class-name="right-align-ir-items"
        >
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['details'],
  methods: {
    inventoryName(val) {
      if (!isEmpty(val.toolType)) {
        return this.$getProperty(val.toolType, 'name')
      } else {
        return this.$getProperty(val.itemType, 'name')
      }
    },
    description(val) {
      if (!isEmpty(val.toolType)) {
        return this.$getProperty(val.toolType, 'description', '---')
      } else {
        return this.$getProperty(val.itemType, 'description', '---')
      }
    },
  },
}
</script>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
</style>
