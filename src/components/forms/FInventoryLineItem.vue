<template>
  <div>
    <FInventoryRequestLineItem
      v-if="isInvReqLineItem"
      :model="model"
      :lineItems="lineItems"
      :isEdit="isEdit"
      :module="module"
      ref="f-inventory-request-line-item"
    >
    </FInventoryRequestLineItem>
    <FTransferRequestLineItem
      v-if="isTransferReqLineItem"
      :model="model"
      :lineItems="lineItems"
      :field="field"
      :isEdit="isEdit"
      :module="module"
      ref="f-transfer-request-line-item"
    >
    </FTransferRequestLineItem>
  </div>
</template>
<script>
import FInventoryRequestLineItem from 'src/components/forms/FInventoryRequestLineItem'
import FTransferRequestLineItem from 'src/components/forms/FTransferRequestLineItem'
export default {
  props: ['model', 'field', 'lineItems', 'isEdit', 'module'],
  components: {
    FInventoryRequestLineItem,
    FTransferRequestLineItem,
  },
  computed: {
    isInvReqLineItem() {
      let { field } = this
      let { name } = field || {}
      return name === 'inventoryrequestlineitems'
    },
    isTransferReqLineItem() {
      let { field } = this
      let { name } = field || {}
      return name === 'transferrequestlineitems'
    },
  },
}
</script>
