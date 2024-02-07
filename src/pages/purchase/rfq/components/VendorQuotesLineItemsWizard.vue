<template>
  <el-dialog
    :visible="true"
    :fullscreen="false"
    :title="$t('common.header.choose_vendor')"
    open="top"
    width="60%"
    custom-class="vendor-wizard assetaddvaluedialog fc-dialog-center-container fc-dialog-return inventory-store-dialog fc-web-form-dialog"
    :append-to-body="true"
    :before-close="onClose"
  >
    <el-table
      :data="lineItems"
      style="width: 100%"
      height="300px"
      class="rfq-award-vendors-table"
    >
      <el-table-column fixed width="50">
        <template v-slot="item">
          <el-radio
            v-model="selectedItemId"
            :label="item.row.id"
            class="fc-radio-btn"
          >
          </el-radio>
        </template>
      </el-table-column>
      <el-table-column
        prop="lineItems"
        :formatter="getItemName"
        :label="$t('common.header.item_name')"
        min-width="250"
      >
      </el-table-column>
      <el-table-column
        prop="lineItems"
        :formatter="getVendorName"
        :label="$t('common.products.vendor')"
        min-width="200"
      >
      </el-table-column>
      <el-table-column
        prop="unitPrice"
        :formatter="getUnitPrice"
        :label="`${$t('common.header.unit_price')} ${$currency}`"
        min-width="150"
      >
      </el-table-column>
      <el-table-column
        prop="counterPrice"
        :formatter="getCounterPrice"
        :label="`${$t('common.header.counter_price')} ${$currency}`"
        min-width="150"
      >
      </el-table-column>
    </el-table>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeDialog()">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button class="modal-btn-save" type="primary" @click="saveRecord">{{
        $t('common._common._save')
      }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import RfqMixin from '../mixins/RfqMixin'
export default {
  props: ['onClose', 'recordId', 'selectedAwardedTo', 'selectedAwardedPrice'],
  mixins: [RfqMixin],
  data() {
    return {
      lineItems: [],
      selectedItemId: null,
    }
  },
  created() {
    this.getVendorQuoteLineItems()
  },
  methods: {
    setSelectedItem(id) {
      this.selectedItemId = id
    },
    getVendorName(val) {
      return this.$getProperty(val, 'vendorQuotes.vendor.name')
    },
    async getVendorQuoteLineItems() {
      let params = {
        filters: JSON.stringify({
          requestForQuotationLineItem: {
            operatorId: 36,
            value: [this.recordId + ''],
          },
        }),
      }
      let { list, error } = await API.fetchAll('vendorQuotesLineItems', params)
      if (error) {
        this.$message.error(
          error.message || 'Error Occured while listing lineItems'
        )
      } else {
        this.lineItems = list
      }
    },
    closeDialog() {
      this.onClose()
    },
    saveRecord() {
      let selecetedLineItem = this.lineItems.filter(
        lineItem => lineItem.id === this.selectedItemId
      )

      let counterPrice = this.$getProperty(selecetedLineItem, '0.counterPrice')
      let vendor = this.$getProperty(selecetedLineItem, '0.vendorQuotes.vendor')
      let dataObj = {
        awardedTo: vendor,
        awardedPrice: counterPrice,
        lineItemId: this.recordId,
      }
      this.$emit('onSave', dataObj)
      this.onClose()
    },
  },
}
</script>
<style lang="scss">
.vendor-wizard.fc-dialog-center-container .el-dialog__body {
  padding: 0px 20px;
}
.rfq-award-vendors-table.el-table--enable-row-transition
  .el-table__body
  td.el-table__cell {
  padding-right: 20px;
}
.rfq-award-vendors-table .el-table td,
.el-table th.is-leaf {
  padding-left: 0;
  padding-right: 0;
}
</style>
