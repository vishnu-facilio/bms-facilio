<template>
  <div class="p20 item-summary-table">
    <div class="widget-title mL0">
      {{ $t('common.products.purchased_items') }}
    </div>
    <div v-if="isLoading" class="mT60 loading-container d-flex">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div v-else>
      <el-table
        :data="purchasedItemList"
        :empty-text="$t('common.products.no_purchased_items_available')"
        height="200px"
        :default-sort="{
          prop: 'costDate',
          order: 'descending',
        }"
        class="width100"
      >
        <template slot="empty">
          <img
            class="mT50"
            src="~statics/noData-light.png"
            width="100"
            height="100"
          />
          <div class="mT10 label-txt-black f14 op6">
            {{ $t('common.products.no_purchased_items_available') }}
          </div>
        </template>
        <el-table-column
          prop="quantity"
          sortable
          :label="$t('common.header.purchased_quantity')"
          class-name="right-align-items"
          width="168"
        ></el-table-column>
        <el-table-column
          prop="currentQuantity"
          sortable
          :label="$t('common.header.current_balance')"
          class-name="right-align-items"
          width="196"
        ></el-table-column>
        <el-table-column
          prop="unitcost"
          sortable
          :label="$t('common.header._unit_price')"
          class-name="right-align-items"
          width="196"
        >
          <template v-slot="scope">
            <CurrencyPopOver
              v-if="checkForMultiCurrency('unitcost', metaFieldTypeMap)"
              class="d-flex flex-row-reverse"
              :field="{
                displayValue: getCurrencyInDecimalValue(scope.row['unitcost'], {
                  decimalPlaces: 2,
                }),
              }"
              :details="scope.row"
            />
            <currency
              v-else
              :value="scope.row.unitcost > 0 ? scope.row.unitcost : 0"
            ></currency>
          </template>
        </el-table-column>
        <el-table-column
          sortable
          :label="$t('common.header.purchased_price')"
          class-name="right-align-items"
          width="196"
        >
          <template v-slot="scope">
            <template v-if="scope.row.quantity * scope.row.unitcost > 0">
              <CurrencyPopOver
                v-if="checkForMultiCurrency('unitcost', metaFieldTypeMap)"
                class="d-flex flex-row-reverse"
                :field="{
                  name: 'unitcost',
                  displayValue: getCurrencyInDecimalValue(
                    scope.row.quantity * scope.row.unitcost,
                    {
                      decimalPlaces: 2,
                    }
                  ),
                }"
                :details="scope.row"
              />
              <currency
                v-else
                :value="scope.row.quantity * scope.row.unitcost"
              ></currency>
            </template>
            <div v-else>---</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="costDate"
          sortable
          :formatter="getDateTimeCostDate"
          :label="$t('common.header.purchased_date_')"
          class-name="center-align-element"
        >
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '../../../../util/validation'
import Spinner from '@/Spinner'
import CurrencyPopOver from 'src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue'
import {
  getMetaFieldMapForModules,
  checkForMultiCurrency,
  getCurrencyInDecimalValue,
} from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  props: ['details'],
  data() {
    return {
      purchasedItemList: [],
      isLoading: false,
      metaFieldTypeMap: {},
      checkForMultiCurrency,
      getCurrencyInDecimalValue,
    }
  },
  components: {
    Spinner,
    CurrencyPopOver,
  },
  async created() {
    this.loadPurchasedItemList()
    this.metaFieldTypeMap = await getMetaFieldMapForModules('purchasedItem')
  },
  methods: {
    async loadPurchasedItemList() {
      let { id } = this.details || {}
      this.isLoading = true
      let { error, data } = await API.get(`v2/purchasedItemsList/item/${id}`)

      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        let { purchasedItem } = data
        this.purchasedItemList = purchasedItem
      }
      this.isLoading = false
    },
    getDateTimeCostDate(val) {
      let value = val.costDate
      return isEmpty(value) ? '' : this.$options.filters.formatDate(value)
    },
  },
}
</script>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
</style>
