<template>
  <div class="currency-table">
    <el-table
      :data="currencyList"
      height="470"
      style="width: 97%"
      class="fc-table-th-pLalign-reduce etable m15 overflow-x-hidden"
      :header-cell-style="{ background: '#f3f1fc', fontWeight: '500' }"
      :empty-text="$t('setup.currency.no_currency')"
    >
      <el-table-column
        width="230"
        :label="$t('setup.currency.name')"
        prop="displayName"
      >
        <template v-slot="currency">
          {{ currency.row.displayName }}
          <span class="description" v-if="currency.row.baseCurrency">
            {{ `(${$t('common._common.default')})` }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        label="Code/Symbol"
        width="180"
        prop="displaySymbol"
      ></el-table-column>
      <el-table-column width="180" prop="exchangeRate">
        <template #header>
          <div>{{ $t('common._common.conversion_rate') }}</div>
          <span v-if="currencyCode" class="description">{{
            `(1 ${currencyCode} = )`
          }}</span>
        </template>
        <template v-slot="currency">
          <span v-bind:class="{ description: currency.row.baseCurrency }">
            {{ currency.row.exchangeRate }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('setup.currency.modified_by')"
        width="180"
        prop="sysModifiedBy"
      >
        <template v-slot="currency">
          <span v-bind:class="{ description: currency.row.baseCurrency }">
            {{ currency.row.sysModifiedBy }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        width="150"
        :label="$t('setup.currency.status')"
        prop="status"
      >
        <template v-slot="currency">
          <el-switch
            v-if="!currency.row.baseCurrency"
            v-model="currency.row.status"
            @change="updateStatus(currency.row)"
            class="Notification-toggle"
            active-color="rgba(57, 178, 194, 0.8)"
            inactive-color="#e5e5e5"
          ></el-switch>
          <span class="description" v-else>
            {{ $t('common.wo_report.na') }}
          </span>
        </template>
      </el-table-column>
      <el-table-column width="124%">
        <template v-slot="currency">
          <div class="line-item-list-edit-delete-btn">
            <span
              v-if="currency.row.id && !currency.row.baseCurrency"
              @click="openForm(currency.row)"
            >
              <inline-svg
                src="svgs/edit"
                class="pointer edit-icon-color visibility-hide-actions"
                iconClass="icon icon-sm mR20"
              ></inline-svg>
            </span>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import { CurrencyListModel } from 'src/pages/setup/organizationSetting/currency/CurrencyModel'

export default {
  props: ['currencyData', 'currencyCode'],
  computed: {
    currencyList() {
      return (this.currencyData || []).map(currency => {
        let {
          displayName,
          displaySymbol,
          exchangeRate,
          sysModifiedBy,
          status,
          currencyCode,
          baseCurrency,
          id,
          decimalPlaces,
        } = currency
        let symbol = `${currencyCode || ''} - ${displaySymbol || '---'}`
        let result = {
          displayName: displayName || '---',
          displaySymbol: symbol,
          status,
          baseCurrency,
          id,
          symbol: displaySymbol,
          currencyCode,
          decimalPlaces,
          sysModifiedBy,
        }
        if (baseCurrency) {
          return {
            ...result,
            exchangeRate: this.$t('common.wo_report.na'),
            sysModifiedBy: this.$t('common.wo_report.na'),
          }
        } else {
          return {
            ...result,
            exchangeRate: exchangeRate || '---',
          }
        }
      })
    },
  },
  methods: {
    async updateStatus(data) {
      let { id, status } = data
      let bodyParam = { currency: { status, id } }
      let { error } = await new CurrencyListModel().patch(bodyParam)
      if (error) {
        this.$message.error(
          error.message || this.$t('common.wo_report.unable_to_save_tax')
        )
      } else {
        this.$message.success(
          this.$t('common.products.currency_updated_successfully')
        )
      }
    },
    openForm(data) {
      this.$emit('edit', data)
    },
  },
}
</script>

<style lang="scss">
.currency-table {
  .etable {
    .el-table__cell {
      padding-left: 20px;
    }
  }

  .description {
    color: #808080;
    font-size: 12px;
    font-weight: normal;
    letter-spacing: 0.43px;
    font-style: italic;
  }
}
</style>
