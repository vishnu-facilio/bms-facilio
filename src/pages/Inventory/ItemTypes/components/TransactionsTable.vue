<template>
  <div class="p30">
    <div class="fc-black-13 fwBold text-uppercase text-left mL0">
      {{ $t('common.header._transactions') }}
    </div>
    <item-type-transactions
      v-if="transactions === 'itemTransactions'"
      :itemType="details"
      :key="details.id"
    ></item-type-transactions>
    <tool-type-transactions
      v-if="transactions === 'toolTransactions'"
      :toolType="details"
      :key="details.id"
    ></tool-type-transactions>
  </div>
</template>
<script>
import ItemTypeTransactions from 'pages/Inventory/ItemTypeTransactions'
import ToolTypeTransactions from 'pages/Inventory/Tooltypes/ToolTypeTransactions'
export default {
  props: ['details', 'widget'],
  components: {
    ItemTypeTransactions,
    ToolTypeTransactions,
  },
  computed: {
    transactions() {
      return this.$getProperty(this, 'widget.widgetParams.transactions')
    },
  },
}
</script>
