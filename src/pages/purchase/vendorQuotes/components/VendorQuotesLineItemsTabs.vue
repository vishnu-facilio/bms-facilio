<template>
  <el-tabs v-model="activeTab" class="vendor-quotes-lineitems-tabs">
    <el-tab-pane
      v-if="canShowAwardedVendors"
      :label="$t('common.inventory.awarded_items')"
      :name="tabNames.AWARDED_ITEMS"
    >
      <div v-if="!canShowAwardedVendors">
        <div
          class="flex-middle justify-content-center wo-flex-col flex-direction-column mT80"
        >
          <inline-svg
            :src="`svgs/emptystate/readings-empty`"
            iconClass="icon text-center icon-xxxlg"
          ></inline-svg>
          <div class="pT10 fc-black-dark f18 bold">
            {{ $t('common.inventory.no_lineitems_awarded') }}
          </div>
        </div>
      </div>
      <VendorQuotesLineItemsTable
        v-else
        :vendorQuotesLineItemsData="awardedVendorQuotesLineItems"
        :key="awardedVendorQuotesLineItems.length"
      ></VendorQuotesLineItemsTable>
    </el-tab-pane>
    <el-tab-pane
      :label="$t('common.inventory.all_quoted_items')"
      :name="tabNames.ALL_VENDORS"
    >
      <div v-if="$validation.isEmpty(vendorQuotesLineItems)">
        <div
          class="flex-middle justify-content-center wo-flex-col flex-direction-column mT80"
        >
          <inline-svg
            :src="`svgs/emptystate/readings-empty`"
            iconClass="icon text-center icon-xxxlg"
          ></inline-svg>
          <div class="pT10 fc-black-dark f18 bold">
            {{ $t('common.inventory.no_lineitems_quoted') }}
          </div>
        </div>
      </div>
      <VendorQuotesLineItemsTable
        v-else
        :vendorQuotesLineItemsData="vendorQuotesLineItems"
        :key="vendorQuotesLineItems.length"
      ></VendorQuotesLineItemsTable>
    </el-tab-pane>
  </el-tabs>
</template>
<script>
import VendorQuotesLineItemsTable from 'src/pages/purchase/vendorQuotes/components/VendorQuotesLineItemsTable.vue'
import { isEmpty } from '@facilio/utils/validation'
const TAB_NAMES = {
  AWARDED_ITEMS: 'awardedItems',
  ALL_VENDORS: 'allQuotedItems',
}

export default {
  props: ['details'],
  components: {
    VendorQuotesLineItemsTable,
  },
  created() {
    this.getVendorQuotesLineItems()
    this.setDefaultTab()
  },
  data() {
    return {
      activeTab: TAB_NAMES.ALL_VENDORS,
      vendorQuotesLineItems: [],
      awardedVendorQuotesLineItems: [],
      tabNames: TAB_NAMES,
    }
  },
  computed: {
    canShowAwardedVendors() {
      let { awardedVendorQuotesLineItems } = this || {}
      return !isEmpty(awardedVendorQuotesLineItems)
    },
  },
  methods: {
    setDefaultTab() {
      let { canShowAwardedVendors } = this || {}
      if (canShowAwardedVendors) {
        this.activeTab = TAB_NAMES.AWARDED_ITEMS
      }
    },
    getVendorQuotesLineItems() {
      let { details } = this
      let { vendorQuotesLineItems } = details || {}
      this.vendorQuotesLineItems = vendorQuotesLineItems
      if (!isEmpty(vendorQuotesLineItems)) {
        let awardedVendorQuotesLineItems = vendorQuotesLineItems.filter(
          lineItem => {
            return lineItem.isLineItemAwarded
          }
        )
        this.awardedVendorQuotesLineItems = awardedVendorQuotesLineItems
      }
    },
  },
}
</script>
<style lang="scss">
.vendor-quotes-lineitems-tabs {
  .el-tabs__nav-wrap {
    padding-left: 25px;
  }
  .widget-card .el-tabs__content {
    overflow: scroll;
  }
}
</style>
