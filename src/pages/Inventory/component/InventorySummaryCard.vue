<template>
  <div>
    <el-row class="p30">
      <el-col :span="6">
        <InventorySummaryIcons
          :detail="details.transferFromStore"
          :widgetParams="widgetParams"
          :userId="transferredById ? getUser(transferredById) : null"
          :storeroom="checkStoreroomField(displayHeader(0))"
          :price="checkPriceField(displayHeader(0))"
        >
        </InventorySummaryIcons>
      </el-col>
      <el-col :span="18">
        <div class="fc-black-13 fwBold text-uppercase text-left">
          {{ displayHeader(0) }}
        </div>
        <div
          v-if="checkCurrencyWidget(displayHeader(0), displayValue(0))"
          class="fc-black-13 text-left pT8"
        >
          <CurrencyPopOver
            v-if="checkForMultiCurrency(displayHeader(2), metaFieldTypeMap)"
            :field="{ displayValue: displayValue(0) }"
            :details="details"
            :showInfo="true"
          />
          <currency
            v-else
            :value="displayValue(0)"
            class="overflow-scroll"
          ></currency>
        </div>
        <div v-else class="fc-black-13 text-left pT8">
          {{ displayValue(0) }}
        </div>
      </el-col>
    </el-row>
    <el-row class="p30">
      <el-col :span="6">
        <InventorySummaryIcons
          :detail="details.transferToStore"
          :widgetParams="widgetParams"
          :userId="createdById ? getUser(createdById) : null"
          :individuallyTracked="checkIndividuallyTrackedField(displayHeader(1))"
        >
        </InventorySummaryIcons>
      </el-col>
      <el-col :span="18">
        <div class="fc-black-13 fwBold text-uppercase text-left">
          {{ displayHeader(1) }}
        </div>
        <div class="fc-black-13 text-left pT8">
          {{ displayValue(1) }}
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import InventorySummaryIcons from 'pages/Inventory/component/InventorySummaryIcons'
import CurrencyPopOver from 'src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue'
import {
  getMetaFieldMapForModules,
  checkForMultiCurrency,
} from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  props: ['details', 'widget'],
  components: {
    InventorySummaryIcons,
    CurrencyPopOver,
  },
  data() {
    return {
      metaFieldTypeMap: {},
      checkForMultiCurrency,
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
    ...mapGetters(['getUser']),
    widgetParams() {
      return this.$getProperty(this, 'widget.widgetParams.card')
    },
    createdById() {
      let id = this.$getProperty(this, 'details.sysCreatedBy.id', null)
      return id
    },
    transferredById() {
      let id = this.$getProperty(this, 'details.transferredBy.id', null)
      return id
    },
    individuallyTracked() {
      if (this.$getProperty(this, 'details.rotating', null)) {
        return this.$t('common.products.yes')
      }
      return this.$t('common.products._no')
    },
    individuallyTrackedItem() {
      if (this.$getProperty(this, 'details.itemType.rotating', null)) {
        return this.$t('common.products.yes')
      }
      return this.$t('common.products._no')
    },
    individuallyTrackedTool() {
      if (this.$getProperty(this, 'details.toolType.rotating', null)) {
        return this.$t('common.products.yes')
      }
      return this.$t('common.products._no')
    },
  },
  async created() {
    let { moduleName } = this.$attrs || {}
    this.metaFieldTypeMap = await getMetaFieldMapForModules(moduleName)
  },
  methods: {
    checkStoreroomField(displayHeader) {
      return displayHeader === this.$t('common.products.storeroom')
    },
    checkIndividuallyTrackedField(displayHeader) {
      return displayHeader === this.$t('common.products.individually_tracked')
    },
    checkPriceField(displayHeader) {
      return [
        this.$t('common.header.last_purchased_price_'),
        this.$t('common._common.selling_price_per_hr'),
        this.$t('common.header.rate_per_hour'),
      ].includes(displayHeader)
    },
    checkCurrencyWidget(displayHeader, value) {
      return (
        [
          this.$t('common.header.last_purchased_price_'),
          this.$t('common._common.selling_price_per_hr'),
          this.$t('common.header.rate_per_hour'),
        ].includes(displayHeader) && value !== '---'
      )
    },
    getDisplayValue(destructureString) {
      let value = this.$getProperty(this, `${destructureString}`)
      return !isEmpty(value) ? value : '---'
    },
    getDisplayDate(destructureString) {
      let date = this.$getProperty(this, `${destructureString}`)
      return !isEmpty(date)
        ? this.$options.filters.formatDate(date, true)
        : '---'
    },
    displayValue(row) {
      let {
        getDisplayValue,
        getDisplayDate,
        individuallyTrackedItem,
        individuallyTracked,
        individuallyTrackedTool,
      } = this
      const DISPLAY_VALUE_HASH = {
        transferrequestcard1: [
          getDisplayValue('details.transferFromStore.name'),
          getDisplayValue('details.transferToStore.name'),
        ],
        transferrequestcard2: [
          getDisplayDate('details.transferInitiatedOn'),
          getDisplayDate('details.expectedCompletionDate'),
        ],
        transferrequestcard3: [
          getDisplayValue('details.transferredBy.name'),
          getDisplayValue('details.sysCreatedBy.name'),
        ],
        itemtypescard1: [
          getDisplayValue('details.quantity'),
          getDisplayValue('details.minimumQuantity'),
        ],
        itemtypescard2: [
          getDisplayDate('details.lastIssuedDate'),
          getDisplayDate('details.lastPurchasedDate'),
        ],
        itemtypescard3: [
          getDisplayValue('details.lastPurchasedPrice'),
          individuallyTracked,
        ],
        tooltypescard1: [
          getDisplayValue('details.currentQuantity'),
          getDisplayValue('details.minimumQuantity'),
        ],
        tooltypescard2: [
          getDisplayDate('details.lastIssuedDate'),
          getDisplayDate('details.lastPurchasedDate'),
        ],
        tooltypescard3: [
          getDisplayValue('details.sellingPrice'),
          individuallyTracked,
        ],
        itemcard1: [
          getDisplayValue('details.storeRoom.name'),
          getDisplayDate('details.lastPurchasedDate'),
        ],
        itemcard2: [
          getDisplayValue('details.quantity'),
          getDisplayValue('details.reservedQuantity'),
        ],
        itemcard3: [
          getDisplayValue('details.lastPurchasedPrice'),
          individuallyTrackedItem,
        ],
        toolcard1: [
          getDisplayValue('details.storeRoom.name'),
          getDisplayDate('details.lastPurchasedDate'),
        ],
        toolcard2: [
          getDisplayValue('details.currentQuantity'),
          getDisplayValue('details.minimumQuantity'),
        ],
        toolcard3: [
          getDisplayValue('details.toolType.sellingPrice'),
          individuallyTrackedTool,
        ],
      }

      let { widgetParams } = this
      return DISPLAY_VALUE_HASH[widgetParams][row]
    },
    displayHeader(row) {
      const DISPLAY_HEADER_HASH = {
        transferrequestcard1: [
          this.$t('common.products.transfer_from_store'),
          this.$t('common.products.transfer_to_store'),
        ],
        transferrequestcard2: [
          this.$t('common.products.transfer_date'),
          this.$t('common.products.expected_arrival_date'),
        ],
        transferrequestcard3: [
          this.$t('common.products.transferred_by'),
          this.$t('common._common.created_by'),
        ],
        itemtypescard1: [
          this.$t('common.header.available_balance'),
          this.$t('common.wo_report.minimum_quantity'),
        ],
        itemtypescard2: [
          this.$t('common.header.last_issued_date'),
          this.$t('common.header.last_purchased_date'),
        ],
        itemtypescard3: [
          this.$t('common.header.last_purchased_price_'),
          this.$t('common.products.individually_tracked'),
          'lastPurchasedPrice',
        ],
        tooltypescard1: [
          this.$t('common.header.current_balance'),
          this.$t('common.wo_report.minimum_quantity'),
        ],
        tooltypescard2: [
          this.$t('common.header.last_issued_date'),
          this.$t('common.header.last_purchased_date'),
        ],
        tooltypescard3: [
          this.$t('common._common.selling_price_per_hr'),
          this.$t('common.products.individually_tracked'),
          'sellingPrice',
        ],
        itemcard1: [
          this.$t('common.products.storeroom'),
          this.$t('common.header.last_purchased_date'),
        ],
        itemcard2: [
          this.$t('common.header.available_balance'),
          this.$t('common.inventory.reserved_quantity'),
        ],
        itemcard3: [
          this.$t('common.header.last_purchased_price_'),
          this.$t('common.products.individually_tracked'),
          'lastPurchasedPrice',
        ],
        toolcard1: [
          this.$t('common.products.storeroom'),
          this.$t('common.header.last_purchased_date'),
        ],
        toolcard2: [
          this.$t('common.header.current_balance'),
          this.$t('common.wo_report.minimum_quantity'),
        ],
        toolcard3: [
          this.$t('common.header.rate_per_hour'),
          this.$t('common.products.individually_tracked'),
          'rate',
        ],
      }

      let { widgetParams } = this
      return DISPLAY_HEADER_HASH[widgetParams][row]
    },
  },
}
</script>
