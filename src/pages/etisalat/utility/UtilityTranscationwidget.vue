<template>
  <div>
    <template>
      <div ref="preview-container">
        <div class="">
          <div v-if="showLoading" class="flex-middle fc-empty-white">
            <spinner :show="showLoading" size="80"></spinner>
          </div>
          <div
            v-if="$validation.isEmpty(bills) && !showLoading"
            class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column"
          >
            <inline-svg
              src="svgs/emptystate/readings-empty"
              iconClass="icon text-center icon-xxxlg"
            ></inline-svg>
            <div class="nowo-label">
              No Utility accounts payments available
            </div>
          </div>
          <div v-if="!showLoading && !$validation.isEmpty(bills)">
            <el-table
              :data="bills"
              style="width: 100%"
              class="utility-account-table"
            >
              <el-table-column label="Period" width="180">
                <template slot-scope="scope">
                  <div class="fwBold">Bill Month:</div>
                  <div>
                    {{ getDate(scope.row.data.date_5, 'MMM YYYY', false) }}
                  </div>
                  <div class="fwBold pT10">Bill Period:</div>
                  <div>
                    {{ getDate(scope.row.data.date_2, 'MMM YYYY', false) }} -
                    {{ getDate(scope.row.data.date_3, 'MMM YYYY', false) }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="Payment Memo" width="250">
                <template slot-scope="scope">
                  <div class="fwBold">Memo Id:</div>
                  <div @click="goToInvoice(scope.row)">
                    {{
                      scope.row.data.invoice &&
                      scope.row.data.invoice.data &&
                      scope.row.data.invoice.data.singleline_11
                        ? scope.row.data.invoice.data.singleline_11
                        : '---'
                    }}
                  </div>
                  <div class="fwBold pT10">Arrear Memo id:</div>
                  <div>
                    {{ getFieldData(scope.row, 'arrearinvoice', 'id') }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="Amount" width="180">
                <template slot-scope="scope">
                  <div class="fwBold">Total Amount:</div>
                  <div>
                    {{
                      scope.row.data.decimal_11
                        ? `${formatCurrency(scope.row.data.decimal_11)} AED`
                        : '0 AED'
                    }}
                  </div>
                  <div class="fwBold pT10">Arrear amount:</div>
                  <div>
                    {{
                      scope.row.data.decimal_14
                        ? `${formatCurrency(scope.row.data.decimal_14)} AED`
                        : '0 AED'
                    }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="Status">
                <template slot-scope="scope">
                  <div class="fwBold">Bill Status:</div>
                  <div class="fc-red4-13">
                    {{
                      scope.row.moduleState.displayName
                        ? scope.row.moduleState.displayName
                        : '---'
                    }}
                  </div>
                  <div class="fwBold pT10">Memo Status:</div>
                  <div>
                    {{
                      scope.row.data &&
                      scope.row.data.invoice &&
                      scope.row.data.invoice.moduleState &&
                      scope.row.data.invoice.moduleState.displayName
                        ? scope.row.data.invoice.moduleState.displayName
                        : '---'
                    }}
                  </div>
                  <div class="fwBold">Arrear Memo Status:</div>
                  <div>
                    {{ getFieldData(scope.row, 'arrearinvoice', 'status') }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="Action">
                <template slot-scope="scope">
                  <div
                    class="fc-blue-txt3-13"
                    @click="showBillAlertSummary(scope.row)"
                  >
                    View Bill
                  </div>
                  <div class="fc-blue-txt3-13" @click="goToInvoice(scope.row)">
                    View Payment Memo
                  </div>
                  <!-- <div class="fc-blue-txt3-13" @click="goToArrearInvoice(scope.row)">
                    View arrear payment memo
                  </div> -->
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
        <BillSummaryDialog
          v-if="showBillSummary && selectedBill"
          :visibility.sync="showBillSummary"
          :summary="selectedBill"
        ></BillSummaryDialog>
      </div>
    </template>
  </div>
</template>
<script>
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'
import BillSummaryDialog from 'src/pages/etisalat/UtilityBills/UtilityBillsSummaryDialog'
import { formatCurrency } from 'charts/helpers/formatter'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['details', 'resizeWidget'],
  data() {
    return {
      showBillSummary: false,
      showLoading: false,
      bills: [],
      fieldObject: null,
      selectedBill: null,
      reviewpage: {
        from: 1,
        to: 50,
        totalCount: 50,
        page: 1,
      },
    }
  },
  components: {
    BillSummaryDialog,
  },
  computed: {
    fields() {
      if (this.fieldObject) {
        return this.fieldObject.fields
      }
      return []
    },
  },
  mounted() {
    this.reviewData()
    this.getFields()
    this.autoResize()
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        if (!isEmpty(this.$refs['preview-container'])) {
          let height = this.$refs['preview-container'].scrollHeight + 100
          let width = this.$refs['preview-container'].scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height, width })
          }
        }
      })
    },
    getFields() {
      this.$http
        .get('/module/meta?moduleName=custom_utilitybills')
        .then(response => {
          if (response.data.meta) {
            this.fieldObject = response.data.meta
          }
          this.loading = false
        })
    },
    getDate(time, formatter, uppercase) {
      if (uppercase) {
        return moment(Number(time))
          .tz(this.$timezone)
          .format(formatter)
          .toUpperCase()
      } else {
        return moment(Number(time))
          .tz(this.$timezone)
          .format(formatter)
      }
    },
    showBillAlertSummary(scope) {
      this.selectedBill = scope
      this.showBillSummary = true
    },
    goToInvoice(scope) {
      let invoice = scope.data.invoice
      if (invoice) {
        let routeData

        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('custom_invoices', pageTypes.OVERVIEW) || {}

          if (name) {
            routeData = this.$router.resolve({
              name,
              params: { viewname: 'all', id: invoice.id },
            })
          }
        } else {
          routeData = this.$router.resolve({
            path: `/app/supp/custom_invoices/all/${invoice.id}/summary`,
          })
        }
        routeData && window.open(routeData.href, '_blank')
      }
    },
    formatCurrency(value) {
      return formatCurrency(value, 2)
    },
    getFieldData(data, fieldName, field2) {
      if (fieldName) {
        let fieldobj = this.fields.find(rt => rt.name === fieldName)
        if (fieldName === 'arrearinvoice' && field2 === 'id') {
          return data.arrearinvoice && data.arrearinvoice.id
            ? data.arrearinvoice.id
            : '---'
        } else if (fieldName === 'arrearinvoice' && field2 === 'status') {
          return data.arrearinvoice && data.arrearinvoice.moduleState
            ? this.$getProperty(data.arrearinvoice, 'moduleState.displayName')
            : '---'
        }
      }
    },
    reviewData() {
      this.showLoading = true
      let supplier = this.details.data.supplier
      let supplierrid = supplier.id
      let { details, reviewpage } = this
      this.underReviewloading = true
      let supliervalue = []
      supliervalue.push(supplierrid + '')
      let utilityAccountId = []
      utilityAccountId.push(this.details.id + '')
      let filter = {
        date_5: {
          operatorId: 44,
        },
        utility_account: {
          operatorId: 36,
          value: utilityAccountId,
          selectedLabel: this.details.name,
        },
        supplier: {
          operatorId: 36,
          value: supliervalue,
          selectedLabel: supplier.name,
        },
      }
      let url = `/v2/module/data/list?moduleName=custom_utilitybills&page=${
        reviewpage.page
      }&perPage=${50}&filters=${encodeURIComponent(
        JSON.stringify(filter)
      )}&viewName=all&includeParentFilter=true`

      this.$http.get(url).then(resp => {
        console.log('deta')
        if (resp.data.responseCode === 0) {
          this.bills =
            resp.data.result && resp.data.result.moduleDatas
              ? resp.data.result.moduleDatas
              : []
        }
        this.showLoading = false
        this.underReviewloading = false
      })
    },
  },
}
</script>
<style lang="scss">
.utility-account-table {
  tr:nth-child(odd) {
    background-color: #f8fafc;
  }
  tr:nth-child(even) {
    background-color: #ffffff;
  }
  td {
    vertical-align: top;
    border-right: 1px solid #dee7ef;
  }
  td:last-child {
    border-right: none;
  }
  th:last-child {
    border-right: none;
  }
  th {
    border-right: 1px solid #dee7ef;
  }
  th > .cell {
    padding-left: 0;
  }
}
</style>
