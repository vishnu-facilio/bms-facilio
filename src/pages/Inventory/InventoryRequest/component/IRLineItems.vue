<template>
  <div ref="lineItems-container">
    <div
      v-if="$validation.isEmpty(details)"
      class="d-flex justify-content-center flex-direction-column align-center height100"
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
    <div v-else class="item-summary-table width100 pT20 pL30 pB20">
      <div
        class="related-list-header header justify-content-space widget-title d-flex flex-direction-column justify-center mL0 mB10"
      >
        {{ $t('common._common.requested_line_items') }}
      </div>
      <el-table
        :data="inventoryRequestLineItem"
        style="width: 100%"
        class="pR20"
      >
        <el-table-column
          prop="inventoryType"
          label="Inventory Type"
          min-width="200"
        ></el-table-column>
        <el-table-column
          prop="itemType"
          label="ItemType"
          min-width="200"
        ></el-table-column>
        <el-table-column prop="toolType" label="TOOlType" min-width="200">
        </el-table-column>
        <el-table-column prop="storeRoom" label="StoreRoom" min-width="150">
        </el-table-column>
        <el-table-column
          prop="quantity"
          label="Quantity"
          min-width="100"
          class-name="right-align-ir-items"
        >
        </el-table-column>
        <el-table-column
          prop="issuedQuantity"
          label="Issued Quantity"
          min-width="200"
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
  computed: {
    inventoryRequestLineItem() {
      let { details } = this
      let { inventoryrequestlineitems, storeRoom } = details || {}

      return (inventoryrequestlineitems || []).map(lineItem => {
        let { inventoryType, itemType, toolType, quantity, issuedQuantity } =
          lineItem || {}
        let { name: storeRoomName } = storeRoom || {}

        if (inventoryType === 1) {
          itemType = itemType.name || '---'
          toolType = '---'
        } else {
          toolType = toolType.name || '---'
          itemType = '---'
        }

        return {
          inventoryType: inventoryType === 1 ? 'Item' : 'Tool',
          quantity: !isEmpty(quantity) ? quantity : '---',
          issuedQuantity: !isEmpty(issuedQuantity) ? issuedQuantity : '---',
          itemType,
          toolType,
          storeRoom: storeRoomName || '---',
        }
      })
    },
  },
}
</script>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
</style>
