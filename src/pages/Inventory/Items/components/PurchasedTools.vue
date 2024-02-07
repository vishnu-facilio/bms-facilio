<template>
  <div class="p20 item-summary-table">
    <div class="widget-title mL0">
      {{ $t('common.products.purchased_tools') }}
    </div>
    <div v-if="isLoading" class="mT60 loading-container d-flex">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div v-else>
      <el-table
        :data="purchasedToolList"
        :empty-text="$t('common.products.no_purchased_tools_available')"
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
            {{ $t('common.products.no_purchased_tools_available') }}
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
          prop="unitPrice"
          sortable
          :label="$t('common.header._unit_price')"
          class-name="right-align-items"
          width="196"
        >
          <template v-slot="scope">
            <currency
              :value="scope.row.unitPrice > 0 ? scope.row.unitPrice : 0"
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
            <currency
              v-if="scope.row.quantity * scope.row.unitPrice > 0"
              :value="scope.row.quantity * scope.row.unitPrice"
            ></currency>
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
import { isEmpty, isNullOrUndefined } from '@facilio/utils/validation'

import Spinner from '@/Spinner'
export default {
  props: ['details'],
  data() {
    return {
      purchasedToolList: [],
      isLoading: false,
    }
  },
  components: {
    Spinner,
  },
  created() {
    this.loadPurchasedTooList()
  },
  methods: {
    async loadPurchasedTooList() {
      let { id } = this.details || {}
      this.isLoading = true
      let moduleName = 'purchasedTool'
      let filters = {
        tool: { operatorId: 9, value: [`${id}`] },
      }
      let { list, error } = await API.fetchAll(moduleName, {
        filters: JSON.stringify(filters),
      })
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        if (!isNullOrUndefined(list)) {
          this.$set(this, 'purchasedToolList', list)
        }
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
