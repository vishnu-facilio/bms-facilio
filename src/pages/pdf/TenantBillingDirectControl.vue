<template>
  <div class="height100vh">
    <div
      class="scrollable header-sidebar-hide quotation-pdf-con"
      :class="{
        'header-sidebar-hide': $route.path === 'pdf/tenantbillings',
      }"
    >
      <div v-if="isLoading" class="flex-middle fc-empty-white">
        <spinner :show="isLoading" size="80"></spinner>
      </div>
      <div v-else>
        <div class="print-break-page tenantBilling-print-con pT40">
          <div class="width100">
            <!-- header part -->
            <div>
              <div class="flex-middle justify-content-space">
                <div class="">
                  <div v-if="$org.logoUrl" class="fc-quotation-logo">
                    <img
                      :src="$prependBaseUrl($org.logoUrl)"
                      style="width: 100px;"
                    />
                  </div>
                  <div>
                    <div
                      v-if="$org.name"
                      class="fc-black-14 text-left bold line-height25"
                    >
                      {{ $org.name }}
                    </div>
                    <div
                      v-if="$org.street"
                      class="fc-black-14 text-left line-height25"
                    >
                      {{ $org.street }}
                    </div>
                    <div v-if="$org.phone" class="fc-black-14 text-left">
                      {{ $org.phone }}
                    </div>
                  </div>
                </div>
                <div>
                  <div
                    class="fc-black-com letter-spacing1 text-right f36 fwBold"
                  >
                    Utility Bill
                  </div>
                  <div class="fc-id text-right">#{{ detail.id }}</div>
                </div>
              </div>
            </div>
            <!-- tenant Details -->
            <div class="fc-quotation-bill-con p15 pB20 mT20">
              <span
                class="fc-black-12 text-left fc-bill-to-txt position-relative"
              >
                Bill To
              </span>
              <el-row class="pT10">
                <el-col :span="14">
                  <div
                    class="fc-black-15 fwBold line-height25 break-word pR10 pointer"
                    v-if="tenatData.lookup.name"
                  >
                    {{ tenatData.lookup.name }}
                  </div>
                  <div
                    class="fc-black-14 text-left"
                    v-if="tenatData.lookup.primaryContactEmail"
                  >
                    {{ tenatData.lookup.primaryContactEmail }}
                  </div>
                  <div
                    class="fc-black-14 text-left"
                    v-if="tenatData.lookup.primaryContactPhone"
                  >
                    {{ tenatData.lookup.primaryContactPhone }}
                  </div>
                </el-col>
                <el-col :span="10">
                  <el-row v-if="tenatData.singleline">
                    <el-col :span="12">
                      <div class="fc-black-12 text-left fwBold text-uppercase">
                        Bill Month
                      </div>
                    </el-col>
                    <el-col :span="12">
                      <div class="fc-black-12 text-left pL15">
                        {{ tenatData.singleline }}
                      </div>
                    </el-col>
                  </el-row>

                  <el-row v-if="tenatData.date" class="pT10">
                    <el-col :span="12">
                      <div class="fc-black-12 text-left fwBold text-uppercase">
                        Bill Period
                      </div>
                    </el-col>
                    <el-col :span="12">
                      <div class="fc-black-12 text-left pL15">
                        {{
                          tenatData.date
                            ? getDate(tenatData.date, 'DD MMM YYYY')
                            : '---'
                        }}
                        -
                        {{
                          tenatData.date_1
                            ? getDate(tenatData.date_1, 'DD MMM YYYY')
                            : '---'
                        }}
                      </div>
                    </el-col>
                  </el-row>
                  <el-row class="pT10" v-if="tenatData.date_2">
                    <el-col :span="12">
                      <div class="fc-black-12 text-left fwBold text-uppercase">
                        Bill Due Date
                      </div>
                    </el-col>
                    <el-col :span="12">
                      <div class="fc-black-12 text-left pL15">
                        {{
                          tenatData.date_2
                            ? getDate(tenatData.date_2, 'DD MMM YYYY')
                            : '---'
                        }}
                      </div>
                    </el-col>
                  </el-row>
                </el-col>
              </el-row>
            </div>
            <!-- tenant Line items -->
            <div class="fc-tenantBilling-summary-table">
              <table>
                <thead>
                  <th class="text-left">
                    Item Description
                  </th>
                  <th class="text-right">
                    Units Consumed
                  </th>
                  <th class="text-right">
                    Cost per Unit
                  </th>
                  <th class="text-left">
                    Unit of measure
                  </th>
                  <th class="text-right">
                    Total Cost
                  </th>
                </thead>
                <tbody>
                  <tr v-for="(lineItem, index) in lineItems" :key="index">
                    <td>
                      <div class="pT5">{{ lineItem.name }}</div>
                    </td>
                    <td class="text-right">
                      {{
                        $d3.format(',.2f')(getLineFieldData(lineItem, 'number'))
                      }}
                    </td>
                    <td class="text-right">
                      {{ `${$currency}`
                      }}{{ getLineFieldData(lineItem, 'number_1') }}
                    </td>
                    <td>
                      {{ getLineFieldData(lineItem, 'singleline') }}
                    </td>
                    <td class="text-right">
                      {{ `${$currency}`
                      }}{{
                        $d3.format(',.2f')(
                          getLineFieldData(lineItem, 'number_2')
                        )
                      }}
                    </td>
                  </tr>
                  <tr v-if="tenatData.decimal_1">
                    <td colspan="3" class="border-none-bottom-left"></td>
                    <td class="text-right bold text-uppercase">
                      Sub Total
                    </td>
                    <td class="text-right f14 fwBold">
                      {{ `${$currency}`
                      }}{{ $d3.format(',.2f')(tenatData.decimal) }}
                    </td>
                  </tr>
                  <tr v-if="tenatData.decimal_1">
                    <td colspan="3" class="border-none-bottom-left"></td>
                    <td class="text-right bold text-uppercase">
                      Tax Amount(15%) {{ `${$currency}` }}
                    </td>
                    <td class="text-right f14 fwBold">
                      {{ `${$currency}`
                      }}{{ $d3.format(',.2f')(tenatData.decimal_1) }}
                    </td>
                  </tr>
                  <tr>
                    <td colspan="3" class="border-none-bottom-left"></td>
                    <td class="text-right bold text-uppercase">
                      Total Bill Amount
                    </td>
                    <td class="text-right f14 fwBold">
                      {{ `${$currency}`
                      }}{{ $d3.format(',.2f')(tenatData.decimal_2) }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import moment from 'moment-timezone'
export default {
  data() {
    return {
      isLoading: false,
      fieldObject: null,
      detail: null,
      tenatData: null,
      lineItems: null,
    }
  },
  computed: {
    fields() {
      if (this.fieldObject) {
        return this.fieldObject.fields
      }
    },
    data() {
      return this.details.data || null
    },
    details() {
      if (this.detail) {
        return this.detail
      }
      return this.summary
    },
    id() {
      return this.$route.query.id ? parseInt(this.$route.query.id) : ''
    },
  },
  mounted() {
    this.getSummaryData()
    this.getFields()
  },
  methods: {
    getSummaryData() {
      let { id } = this
      this.isLoading = true
      this.$http
        .get(`/v2/module/data/${id}?moduleName=custom_tenantbilling`)
        .then(response => {
          this.detail = response.data.result.moduleData
            ? response.data.result.moduleData
            : []
          this.tenatData = response.data.result.moduleData
            ? response.data.result.moduleData &&
              response.data.result.moduleData.data
            : []
          this.getFields()
        })
        .catch(() => {
          this.agentLogs = []
          this.loading = false
        })
    },
    getFields() {
      this.$http
        .get('/module/meta?moduleName=custom_tenantbilling')
        .then(response => {
          if (response.data.meta) {
            this.fieldObject = response.data.meta
          }
          this.loadTenantLineItems()
          this.isLoading = false
        })
    },
    loadTenantLineItems() {
      let filters = `{"tenantbill":{"operatorId":36,"value":["${this.detail.id}"]}}`
      this.$http
        .get(
          `v2/module/data/list?moduleName=custom_tenantbillinglineitems&page=1&perPage=50&filters=${encodeURIComponent(
            filters
          )}&viewName=all&includeParentFilter=true`
        )
        .then(response => {
          if (
            response.data.responseCode === 0 &&
            response.data.result &&
            response.data.result.moduleDatas
          ) {
            this.lineItems = response.data.result.moduleDatas
          }
        })
    },
    getDate(time, formatter) {
      return moment(Number(time))
        .tz(this.$timezone)
        .format(formatter)
    },
    getLineFieldData(item, fieldName) {
      if (fieldName && item && item.data) {
        let fieldobj = this.fields.find(rt => rt.name === fieldName)
        let lineItem = item.data
        if (fieldName === 'name') {
          return lineItem.name ? lineItem.name : '---'
        } else if (fieldName === 'decimal') {
          return lineItem.decimal ? lineItem.decimal : '---'
        } else if (fieldName === 'number') {
          return lineItem.number ? lineItem.number : '---'
        } else if (fieldName === 'decimal_1') {
          return lineItem.decimal_1 ? lineItem.decimal_1 : '---'
        } else if (fieldName === 'singleline') {
          return lineItem.singleline ? lineItem.singleline : '---'
        } else if (fieldName === 'number_2') {
          return lineItem.number_2 ? lineItem.number_2 : '---'
        } else if (fieldName === 'singleline_1') {
          return lineItem.singleline_1 ? lineItem.singleline_1 : ' '
        } else if (fieldName === 'number_1') {
          return lineItem.number_1 ? lineItem.number_1 : ' '
        }
      }
    },
  },
}
</script>
<!-- Dont remove the styles !-->
<style lang="scss">
.tenantBilling-print-con {
  max-width: 1100px;
  margin-left: auto;
  margin-right: auto;
}

.fc-tenantBilling-summary-table {
  width: 100%;
  margin-top: 30px;

  table {
    width: 100%;
  }

  th {
    background-color: #584a8f;
    font-size: 11px;
    font-weight: bold;
    text-transform: uppercase;
    line-height: normal;
    letter-spacing: 1px;
    color: #ffffff;
    border-right: 1px solid #584a8f;
    padding: 15px 10px;
    text-align: center;
  }

  td {
    padding: 12px 10px;
    border-right: 1px solid #e4eaed;
    border-bottom: 1px solid #e4eaed;
    font-size: 13px;
    font-weight: normal;
    letter-spacing: 0.4px;
    color: #324056;
    border-bottom-width: thin;
    vertical-align: middle;
  }

  td:first-child {
    border-left: 1px solid #e4eaed;
  }

  td:last-child {
    border-right: 1px solid #e4eaed;
  }
}

@media print {
  @page {
    orphans: 4;
    widows: 2;
    // margin-bottom: 10px;
    margin-top: 28px;
    z-index: 900;
  }

  html {
    min-height: 100%;
    text-rendering: optimizeLegibility;
  }

  .normal main {
    height: 100vh !important;
    background: #fff;
    min-height: 100vh !important;
  }

  .header-sidebar-hide {
    width: 100%;
    height: 100%;
    position: inherit;
    top: 0;
    left: 0;
    right: 0;
    page-break-after: always;
    page-break-inside: avoid;
  }

  .layout-page .pL60 {
    padding-left: 0 !important;
    position: inherit !important;
    height: initial !important;
  }

  .print-break-page {
    height: 100%;
    overflow-y: scroll;
    display: block;
    page-break-after: always;
  }

  body {
    overflow: scroll;
    background: #fff;
    display: block;
    width: auto;
    height: auto;
    overflow: visible;
    min-height: 100%;
    text-rendering: optimizeLegibility;
    counter-reset: page;
  }

  .tenantBilling-print-con {
    max-width: 1000px;
    width: 100%;
    padding: 20px 30px 0;
    margin-left: auto;
    height: 100%;
    margin-right: auto;
    position: relative;
    overflow: visible;
    display: flex;
    flex-direction: column;
    margin-top: 0 !important;
  }

  tr {
    page-break-inside: avoid;
    page-break-after: auto;
  }

  td {
    page-break-inside: avoid;
    page-break-after: auto;
  }

  .border-none-bottom-left {
    border-bottom: none !important;
    border-left: none !important;
  }

  .fc-tenantBilling-summary-table {
    table {
      border: none !important;
    }
  }
}
</style>
