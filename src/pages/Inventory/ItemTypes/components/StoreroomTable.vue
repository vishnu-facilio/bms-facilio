<template>
  <div class="p30">
    <div class="fc-black-13 fwBold text-uppercase text-left mL0 mB10">
      {{ $t('common.header._storerooms') }}
    </div>
    <item-store-room
      v-if="storeroom === 'itemStoreroom'"
      :itemTypeId="details.id"
      :itemType="details"
      :key="details.id"
    ></item-store-room>
    <tool-store-room
      v-if="storeroom === 'toolStoreroom'"
      :toolTypeId="details.id"
      :toolType="details"
      :key="details.id"
    ></tool-store-room>
  </div>
</template>
<script>
import ItemStoreRoom from 'pages/Inventory/ItemStoreRoom'
import ToolStoreRoom from 'pages/Inventory/Tooltypes/ToolStoreRoom'
export default {
  props: ['details', 'widget'],
  components: {
    ItemStoreRoom,
    ToolStoreRoom,
  },
  computed: {
    storeroom() {
      return this.$getProperty(this, 'widget.widgetParams.storeroom')
    },
  },
}
</script>
