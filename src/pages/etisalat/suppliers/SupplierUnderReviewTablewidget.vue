<template>
  <div class="width100" ref="preview-container">
    <el-tabs v-model="activeName" @tab-click="tabSwitch">
      <el-tab-pane label="Active Outstanding" name="outstanding">
        <div class="pT15 pL30 pB10" style="height: 55px;">
          <template v-if="selectedOutstanding.length">
            <el-button
              class="fc-light-blue-btn"
              @click="createMemo(selectedOutstanding)"
              >Create Payment Memo
            </el-button>
            <el-button
              class="fc-light-blue-btn mL10"
              @click="changeState(selectedOutstanding, 3)"
              >Cancel</el-button
            >
            <el-button
              class="fc-light-blue-btn mL10"
              @click="changeState(selectedOutstanding, 2)"
              >Move to Review
            </el-button>
          </template>
          <pagination
            class="fR pT5"
            :total="underpage.totalCount"
            :perPage="perPage"
            :pageNo="underpage.page"
            ref="f-page"
            @onPageChanged="getPage"
          ></pagination>
        </div>
        <div>
          <div v-if="loading" class="flex-middle justify-content-center">
            <spinner :show="true" :size="80"></spinner>
          </div>
          <div
            v-if="$validation.isEmpty(outStandingTableData) && !loading"
            class="height50vh fc-empty-white flex-middle justify-content-center flex-direction-column"
          >
            <inline-svg
              src="svgs/emptystate/readings-empty"
              iconClass="icon text-center icon-xxxlg"
            ></inline-svg>
            <div class="nowo-label">
              No data available
            </div>
          </div>
          <div v-if="!loading && !$validation.isEmpty(outStandingTableData)">
            <el-table
              ref="multipleTable"
              :data="outStandingTableData"
              style="width: 100%"
              class="fc-supplier-review-table"
              @selection-change="selectedItems"
              height="450"
            >
              <el-table-column type="selection" width="70"></el-table-column>
              <el-table-column
                label="Account NO."
                width="250"
                class="text-right"
              >
                <template slot-scope="scope">{{
                  scope.row.accountNo
                }}</template>
              </el-table-column>
              <el-table-column
                property="arrear"
                label="Arrear Amount"
                width="280"
              >
                <template slot-scope="scope">
                  {{ scope.row.arrearAmount }}
                </template>
              </el-table-column>
              <el-table-column property="adjusted" label="Adjusted Amount">
                <template slot-scope="scope">
                  <div class="fL">
                    {{ scope.row.addjustedAmount }}
                  </div>
                  <div
                    @click="adjustArear(scope.row)"
                    class="fc-blue-txt3-13 fR"
                  >
                    Adjust Arear
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-tab-pane>
      <!-- <el-tab-pane label="Active Invoice" name="activeInvoice">
        <div class>
          <pagination
            class="fR p20"
            :total="invoicepage.totalCount"
            :perPage="perPage"
            ref="f-page1"
            :pageNo="invoicepage.page"
            @onPageChanged="getPage"
          ></pagination>
          <el-table
            ref="multipleTable"
            :data="activeInvoiceTableData"
            style="width: 100%"
            class="fc-supplier-review-table"
            @selection-change="selectedItems2"
            height="450"
          >
            <el-table-column type="selection" width="70"></el-table-column>
            <el-table-column label="Account NO." width="250" class="text-right">
              <template slot-scope="scope">{{ scope.row.accountNo }}</template>
            </el-table-column>
            <el-table-column property="arrear" label="Arrear Amount">
              <template slot-scope="scope">
                {{ scope.row.arrearAmount }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane> -->
      <el-tab-pane label="Under Review" name="review">
        <div class="pT15 pL30 pB10" style="height: 55px;">
          <template v-if="underReviewTableData.length">
            <el-button
              class="fc-light-blue-btn mL10"
              @click="changeState(underReviewTableData, 3)"
              >Cancel</el-button
            >
            <el-button
              class="fc-light-blue-btn mL10"
              @click="changeState(underReviewTableData, 1)"
              >Move to Active
            </el-button>
          </template>
          <pagination
            class="fR pT5"
            :total="reviewpage.totalCount"
            :perPage="perPage"
            ref="f-page2"
            :pageNo="reviewpage.page"
            @onPageChanged="getPage"
          ></pagination>
        </div>
        <div class>
          <div v-if="loading" class="flex-middle justify-content-center">
            <spinner :show="true" :size="80"></spinner>
          </div>
          <div
            v-if="$validation.isEmpty(underReviewTableData) && !loading"
            class="height50vh fc-empty-white flex-middle justify-content-center flex-direction-column"
          >
            <inline-svg
              src="svgs/emptystate/readings-empty"
              iconClass="icon text-center icon-xxxlg"
            ></inline-svg>
            <div class="nowo-label">
              No data available
            </div>
          </div>
          <div v-if="!loading && !$validation.isEmpty(underReviewTableData)">
            <el-table
              ref="multipleTable"
              :data="underReviewTableData"
              style="width: 100%"
              class="fc-supplier-review-table"
              @selection-change="selectedItems4"
              height="450"
            >
              <el-table-column type="selection" width="70"></el-table-column>
              <el-table-column
                label="Account NO."
                width="250"
                class="text-right"
              >
                <template slot-scope="scope">{{
                  scope.row.accountNo
                }}</template>
              </el-table-column>
              <el-table-column property="arrear" label="Arrear Amount">
                <template slot-scope="scope">
                  {{ scope.row.arrearAmount }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="Cancelled Arrears" name="cancel">
        <div class="pT15 pL30 pB10" style="height: 55px;">
          <template v-if="canceledTableData.length">
            <el-button
              class="fc-light-blue-btn mL10"
              @click="changeState(canceledTableData, 1)"
              >Move to Active
            </el-button>
          </template>
          <pagination
            class="fR pT5"
            :total="cancelpage.totalCount"
            :perPage="perPage"
            ref="f-page3"
            :pageNo="cancelpage.page"
            @onPageChanged="getPage"
          ></pagination>
        </div>
        <div class>
          <div v-if="loading" class="flex-middle justify-content-center">
            <spinner :show="true" :size="80"></spinner>
          </div>
          <div
            v-if="$validation.isEmpty(canceledTableData) && !loading"
            class="height50vh fc-empty-white flex-middle justify-content-center flex-direction-column"
          >
            <inline-svg
              src="svgs/emptystate/readings-empty"
              iconClass="icon text-center icon-xxxlg"
            ></inline-svg>
            <div class="nowo-label">
              No data available
            </div>
          </div>
          <div v-if="!loading && !$validation.isEmpty(canceledTableData)">
            <el-table
              ref="multipleTable"
              :data="canceledTableData"
              style="width: 100%"
              class="fc-supplier-review-table"
              @selection-change="selectedItems3"
              height="450"
            >
              <el-table-column type="selection" width="70"></el-table-column>
              <el-table-column
                label="Account NO."
                width="250"
                class="text-right"
              >
                <template slot-scope="scope">{{
                  scope.row.accountNo
                }}</template>
              </el-table-column>
              <el-table-column property="arrear" label="Arrear Amount">
                <template slot-scope="scope">
                  {{ scope.row.arrearAmount }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="All Arrears" name="arrears">
        <div class="pT15 pL30 pB10" style="height: 55px;">
          <pagination
            class="fR pT5"
            :total="page.totalCount"
            :perPage="perPage"
            ref="f-page4"
            :pageNo="page.page"
            @onPageChanged="getPage"
          ></pagination>
        </div>
        <div class>
          <div v-if="loading" class="flex-middle justify-content-center">
            <spinner :show="true" :size="80"></spinner>
          </div>
          <div
            v-if="$validation.isEmpty(allArrearsTableData) && !loading"
            class="height50vh fc-empty-white flex-middle justify-content-center flex-direction-column"
          >
            <inline-svg
              src="svgs/emptystate/readings-empty"
              iconClass="icon text-center icon-xxxlg"
            ></inline-svg>
            <div class="nowo-label">
              No data available
            </div>
          </div>
          <div v-if="!loading && !$validation.isEmpty(allArrearsTableData)">
            <el-table
              ref="multipleTable"
              :data="allArrearsTableData"
              style="width: 100%"
              class="fc-supplier-review-table"
              height="450"
              @selection-change="selectedItems5"
            >
              <el-table-column
                label="Account NO."
                width="250"
                class="text-right"
              >
                <template slot-scope="scope">{{
                  scope.row.accountNo
                }}</template>
              </el-table-column>
              <el-table-column
                property="arrear"
                label="Arrear Amount"
                width="280"
              >
                <template slot-scope="scope">
                  {{ scope.row.arrearAmount }}
                </template>
              </el-table-column>
              <el-table-column property="status" label="Arrear status">
                <template slot-scope="scope">
                  <div class="fR pT5">
                    {{ scope.row.status }}
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    <el-dialog
      :visible.sync="visibility"
      :append-to-body="true"
      custom-class="fc-dialog-center-container width30"
      :before-close="closeadjDialog"
      style="z-index: 999999"
      title="Adjust Arrear"
    >
      <div class="content-section height230">
        <div class>
          <el-row align="middle" class="">
            <el-col :span="24" class="">
              <div class="fc-input-label-txt">
                Account No:
                <span class="bold">{{
                  adjustamount.data ? adjustamount.data.accountNo : ''
                }}</span>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="fc-input-label-txt">
                Arrear Amount:
                <span class="bold">{{
                  adjustamount.data ? adjustamount.data.arrearAmount : ''
                }}</span>
              </div>
            </el-col>
            <el-col :span="24">
              <el-form
                :label-position="'top'"
                :model="adjustamount"
                status-icon
                :rules="rules"
                ref="ruleForm"
                label-width="120px"
                class="demo-ruleForm"
              >
                <el-form-item label="Amount" prop="value">
                  <div class="form-input">
                    <el-input
                      type="number"
                      v-model="adjustamount.value"
                      placeholder="Enter the adjust Amount"
                      class="fc-input-full-border2 width100"
                    ></el-input>
                  </div>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeadjDialog()" class="modal-btn-cancel"
            >Cancel</el-button
          >
          <el-button
            type="primary"
            @click="addadjustamount"
            :loading="isSaving"
            class="modal-btn-save"
          >
            {{ isSaving ? 'Saving...' : 'ADD' }}</el-button
          >
        </div>
      </div>
    </el-dialog>

    <el-dialog
      title="Create payment Memo"
      :visible.sync="dialogVisible"
      width="35%"
      :show-close="false"
      :before-close="handleClose"
      class="fc-dialog-center-container"
    >
      <div class="postion-relative">
        <div class="height200" v-if="createPMmeta.buttonInfo">
          <div class="pB5">
            {{ `Supplier name - ${createPMmeta.data.supplier}` }}
          </div>
          <!-- <div class="pB5">
            {{ `Bill Month - ${createPMmeta.data.billMonth}` }}
          </div>
          <div class="pB5">
            {{ `Total Amount - ${createPMmeta.data.cost}` }}
          </div> -->

          <div class="pB5">
            {{ `No of bills - ${createPMmeta.data.count}` }}
          </div>
        </div>
        <div class="height200" v-else>
          <div
            class="pB5"
            v-if="createPMmeta.data.invoice.id"
            @click="goToInvoice(createPMmeta.data.invoice)"
          >
            {{
              `Payment memo #$${createPMmeta.data.invoice.id} created successfully`
            }}
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button
            v-if="createPMmeta.buttonInfo"
            :disabled="saving"
            @click="closeDialog()"
            class="modal-btn-cancel"
            >CANCEL</el-button
          >
          <el-button v-else @click="getActivedata()" class="modal-btn-cancel"
            >CANCEL</el-button
          >
          <el-button
            v-if="createPMmeta.buttonInfo"
            type="primary"
            @click="createInvoice"
            :loading="saving"
            class="modal-btn-save "
          >
            {{
              saving ? 'Creating payment Memo...' : 'Create payment Memo'
            }}</el-button
          >
          <el-button
            v-else
            type="primary"
            @click="getActivedata()"
            class="modal-btn-save "
          >
            {{ 'OK' }}</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import moment from 'moment-timezone'
import { formatCurrency } from 'charts/helpers/formatter'
import { isEmpty } from '@facilio/utils/validation'
import NewDateHelper from '@/mixins/NewDateHelper'
import axios from 'axios'
import pagination from 'src/components/list/FPagination'
export default {
  props: ['details', 'moduleName', 'calculateDimensions', 'resizeWidget'],
  mixins: [NewDateHelper],
  components: {
    pagination,
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
    fields() {
      return this.moduleMeta.fields || []
    },
    fieldDisplayNames() {
      if (this.moduleMeta && this.moduleMeta.fields) {
        let fieldMap = {}
        this.moduleMeta.fields.forEach(field => {
          this.$set(fieldMap, field.name, field.displayName)
        })
        return fieldMap
      }
      return []
    },
    billfields() {
      if (this.fieldObject) {
        return this.fieldObject.fields
      }
      return []
    },
    currentModuleState() {
      let { moduleName, row } = this
      let currentStateId = this.$getProperty(row, 'moduleState.id')
      let currentState = this.$store.getters.getTicketStatus(
        currentStateId,
        moduleName
      )
      let { displayName, status } = currentState || {}

      if (!isEmpty(displayName)) {
        return displayName
      }
      return status || null
    },
  },
  data() {
    let checkAdjustAmount = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('Please input the amount'))
      }
      setTimeout(() => {
        if (value > this.adjustamount.data.arrearAmountRaw) {
          callback(new Error('Amount must be less than arrear amount'))
        } else {
          callback()
        }
      }, 1000)
    }
    return {
      dateFilter: NewDateHelper.getDatePickerObject(44),
      activeName: 'outstanding',
      billState: null,
      alertState: null,
      multipleSelection: [],
      loading: false,
      pdfopen: false,
      currentPage: 0,
      pageCount: 0,
      visibility: false,
      dialogVisible: false,
      createPMmeta: {
        info: '',
        buttonInfo: true,
        data: {},
      },
      saving: false,
      page: {
        from: 1,
        to: 50,
        page: 1,
        totalCount: 50,
      },
      underpage: {
        from: 1,
        to: 50,
        totalCount: 50,
        page: 1,
      },
      reviewpage: {
        from: 1,
        to: 50,
        totalCount: 50,
        page: 1,
      },
      cancelpage: {
        from: 1,
        to: 50,
        totalCount: 50,
        page: 1,
      },
      invoicepage: {
        from: 1,
        to: 50,
        totalCount: 50,
        page: 1,
      },
      adjustamount: {
        id: null,
        value: null,
        data: null,
      },
      perPage: 50,
      isSaving: false,
      outStandingTableData: [],
      underReviewTableData: [],
      canceledTableData: [],
      allArrearsTableData: [],
      activeInvoiceTableData: [],
      selectedOutstanding: [],
      selectedInvoice: [],
      selectedCancel: [],
      selectedUnder: [],
      selectedItem: [],
      outStandingLoading: false,
      outStandingData: [],
      underReviewData: [],
      underReviewloading: false,
      canceledData: [],
      canceledLoading: false,
      activeInvoiceData: [],
      activeInvoiceLoading: false,
      fieldObject: null,
      params: {
        nameSpace: 'SupplierSummary',
        functionName: 'getArrearSummaryCount',
        paramList: [],
      },
      rules: {
        value: [
          {
            validator: checkAdjustAmount,
            trigger: 'blur',
          },
        ],
      },
    }
  },
  mounted() {
    this.getBillStateList()
    this.getAlertStateList()
    this.getFields()
  },
  methods: {
    loadAllData() {
      this.getData()
      this.getData1()
      this.loadunderData()
      this.reviewData()
      // this.getactiveInvoiceData()
      this.cancelData()
      this.loadunderData1()
      this.reviewData1()
      // this.getactiveInvoiceData1()
      this.cancelData1()
    },
    getActivedata() {
      this.closeDialog()
      this.loadunderData()
      this.loadunderData1()
    },
    getBillStateList() {
      this.$http
        .get(`v2/state/list?parentModuleName=custom_utilitybills`)
        .then(({ data }) => {
          this.billState = data.result.status || []
          this.loadAllData()
        })
    },
    getAlertStateList() {
      this.$http
        .get(`v2/state/list?parentModuleName=custom_alert`)
        .then(({ data }) => {
          this.alertState = data.result.status || []
        })
    },
    getPage(page) {
      if (this.activeName === 'outstanding') {
        this.underpage.page = page
        this.loadunderData()
      } else if (this.activeName === 'activeInvoice') {
        this.invoicepage.page = page
        this.getactiveInvoiceData()
      } else if (this.activeName === 'review') {
        this.reviewpage.page = page
        this.reviewData()
      } else if (this.activeName === 'cancel') {
        this.cancelpage.page = page
        this.cancelData()
      } else if (this.activeName === 'arrears') {
        this.page.page = page

        this.getData()
      }
    },

    tabSwitch() {
      if (this.activeName === 'outstanding') {
        this.loadunderData()
      } else if (this.activeName === 'activeInvoice') {
        this.getactiveInvoiceData()
      } else if (this.activeName === 'review') {
        this.reviewData()
      } else if (this.activeName === 'cancel') {
        this.cancelData()
      } else if (this.activeName === 'arrears') {
        this.getData()
      }
    },

    getbillfieldData(fieldName, data) {
      if (fieldName) {
        let fieldobj = this.billfields.find(rt => rt.name === fieldName)
        if (fieldName === 'picklist') {
          let { enumMap } = fieldobj
          return enumMap[data.data['picklist']] || '---'
        }
      }
      return ''
    },
    loadunderData() {
      let supplierrid = this.details.id
      let { details, underpage } = this
      this.outStandingLoading = true
      this.params.paramList.push(supplierrid)
      let supliervalue = []
      supliervalue.push(supplierrid + '')
      let statusId = this.billState.find(rt => rt.status === 'alertprocessed')
        .id
      let stateValue = []
      stateValue.push(statusId + '')
      statusId = this.billState.find(rt => rt.status === 'invoicegenerated').id
      stateValue.push(statusId + '')
      let filter = {
        decimal_14: { operatorId: 13, value: ['0'] },
        picklist: {
          operatorId: 54,
          value: ['1'],
        },
        moduleState: { operatorId: 36, value: stateValue },

        supplier: {
          operatorId: 36,
          value: supliervalue,
          selectedLabel: this.details.name,
        },
      }
      this.outStandingTableData = []
      this.loading = true
      let url = `/v2/module/data/list?moduleName=custom_utilitybills&page=${
        underpage.page
      }&perPage=${underpage.to}&filters=${encodeURIComponent(
        JSON.stringify(filter)
      )}&viewName=all&includeParentFilter=true`
      this.$http.get(url).then(resp => {
        console.log('deta')
        if (resp.data.responseCode === 0) {
          let bills =
            resp.data.result && resp.data.result.moduleDatas
              ? resp.data.result.moduleDatas
              : []
          if (bills && bills.length) {
            bills.forEach(rt => {
              if (rt.data && rt.data.decimal_14 > 0) {
                let data = {
                  id: rt.id,
                  accountNo: rt.data.singleline_2,
                  arrearAmountRaw: rt.data.decimal_14,
                  arrearAmount: `${
                    rt.data.decimal_14
                      ? this.formatCurrency(rt.data.decimal_14)
                      : 0
                  } AED`,
                  addjustedAmount: `${
                    rt.data.decimal_15
                      ? this.formatCurrency(rt.data.decimal_15)
                      : 0
                  } AED`,
                }
                this.outStandingTableData.push(data)
                this.loading = false
              }
            })
          }
        }
        this.outStandingLoading = false
      })
    },
    loadunderData1() {
      let supplierrid = this.details.id
      let { details, underpage } = this
      this.outStandingLoading = true
      this.params.paramList.push(supplierrid)
      let supliervalue = []
      supliervalue.push(supplierrid + '')

      let statusId = this.billState.find(rt => rt.status === 'alertprocessed')
        .id
      let stateValue = []
      stateValue.push(statusId + '')
      statusId = this.billState.find(rt => rt.status === 'invoicegenerated').id
      stateValue.push(statusId + '')
      let filter = {
        picklist: {
          operatorId: 54,
          value: ['1'],
        },
        moduleState: { operatorId: 36, value: stateValue },
        decimal_14: { operatorId: 13, value: ['0'] },
        supplier: {
          operatorId: 36,
          value: supliervalue,
          selectedLabel: this.details.name,
        },
      }
      let url = `/v2/module/data/list?moduleName=custom_utilitybills&page=${
        underpage.page
      }&perPage=${50}&filters=${encodeURIComponent(
        JSON.stringify(filter)
      )}&viewName=all&fetchCount=true&includeParentFilter=true`
      this.$http.get(url).then(resp => {
        console.log('deta')
        if (resp.data.responseCode === 0) {
          underpage.totalCount = resp.data.result.count
        }
        this.outStandingLoading = false
      })
    },
    reviewData() {
      let supplierrid = this.details.id
      let { details, reviewpage } = this
      this.underReviewloading = true
      this.params.paramList.push(supplierrid)
      let supliervalue = []
      this.underReviewTableData = []
      supliervalue.push(supplierrid + '')
      let statusId = this.billState.find(rt => rt.status === 'alertprocessed')
        .id
      let stateValue = []
      stateValue.push(statusId + '')
      statusId = this.billState.find(rt => rt.status === 'invoicegenerated').id
      stateValue.push(statusId + '')
      let filter = {
        picklist: {
          operatorId: 54,
          value: ['2'],
        },
        moduleState: { operatorId: 36, value: stateValue },
        decimal_14: { operatorId: 13, value: ['0'] },
        supplier: {
          operatorId: 36,
          value: supliervalue,
          selectedLabel: this.details.name,
        },
      }
      this.loading = true
      let url = `/v2/module/data/list?moduleName=custom_utilitybills&page=${
        reviewpage.page
      }&perPage=${50}&filters=${encodeURIComponent(
        JSON.stringify(filter)
      )}&viewName=all&includeParentFilter=true`
      this.$http.get(url).then(resp => {
        console.log('deta')
        if (resp.data.responseCode === 0) {
          let bills =
            resp.data.result && resp.data.result.moduleDatas
              ? resp.data.result.moduleDatas
              : []
          if (bills && bills.length) {
            bills.forEach(rt => {
              if (rt.data && rt.data.decimal_14 > 0) {
                let data = {
                  id: rt.id,
                  accountNo: rt.data.singleline_2,
                  arrearAmountRaw: rt.data.decimal_14,
                  arrearAmount: `${
                    rt.data.decimal_14
                      ? this.formatCurrency(rt.data.decimal_14)
                      : 0
                  } AED`,
                  addjustedAmount: `${
                    rt.data.decimal_15
                      ? this.formatCurrency(rt.data.decimal_15)
                      : 0
                  } AED`,
                }
                this.underReviewTableData.push(data)
              }
            })
          }
        }
        this.loading = false
        this.underReviewloading = false
      })
    },
    reviewData1() {
      let supplierrid = this.details.id
      let { details, reviewpage } = this
      this.underReviewloading = true
      this.params.paramList.push(supplierrid)
      let supliervalue = []
      supliervalue.push(supplierrid + '')
      let statusId = this.billState.find(rt => rt.status === 'alertprocessed')
        .id
      let stateValue = []
      stateValue.push(statusId + '')
      statusId = this.billState.find(rt => rt.status === 'invoicegenerated').id
      stateValue.push(statusId + '')
      let filter = {
        picklist: {
          operatorId: 54,
          value: ['2'],
        },
        moduleState: { operatorId: 36, value: stateValue },
        decimal_14: { operatorId: 13, value: ['0'] },
        supplier: {
          operatorId: 36,
          value: supliervalue,
          selectedLabel: this.details.name,
        },
      }
      let url = `/v2/module/data/list?moduleName=custom_utilitybills&page=${
        reviewpage.page
      }&perPage=${50}&filters=${encodeURIComponent(
        JSON.stringify(filter)
      )}&viewName=all&fetchCount=true&includeParentFilter=true`
      this.$http.get(url).then(resp => {
        console.log('deta')
        if (resp.data.responseCode === 0) {
          reviewpage.totalCount = resp.data.result.count
        }
        this.underReviewloading = false
      })
    },
    cancelData() {
      let supplierrid = this.details.id
      let { details, cancelpage } = this
      this.canceledTableData = []
      this.canceledLoading = true
      this.params.paramList.push(supplierrid)
      let supliervalue = []
      supliervalue.push(supplierrid + '')
      let statusId = this.billState.find(rt => rt.status === 'alertprocessed')
        .id
      let stateValue = []
      stateValue.push(statusId + '')
      statusId = this.billState.find(rt => rt.status === 'invoicegenerated').id
      stateValue.push(statusId + '')
      let filter = {
        picklist: {
          operatorId: 54,
          value: ['3'],
        },
        moduleState: { operatorId: 36, value: stateValue },
        decimal_14: { operatorId: 13, value: ['0'] },
        supplier: {
          operatorId: 36,
          value: supliervalue,
          selectedLabel: this.details.name,
        },
      }
      this.loading = true
      let url = `/v2/module/data/list?moduleName=custom_utilitybills&page=${
        cancelpage.page
      }&perPage=${50}&filters=${encodeURIComponent(
        JSON.stringify(filter)
      )}&viewName=all&includeParentFilter=true`
      this.$http.get(url).then(resp => {
        console.log('deta')
        if (resp.data.responseCode === 0) {
          let bills =
            resp.data.result && resp.data.result.moduleDatas
              ? resp.data.result.moduleDatas
              : []
          if (bills && bills.length) {
            bills.forEach(rt => {
              if (rt.data && rt.data.decimal_14 > 0) {
                let data = {
                  id: rt.id,
                  accountNo: rt.data.singleline_2,
                  arrearAmountRaw: rt.data.decimal_14,
                  arrearAmount: `${
                    rt.data.decimal_14
                      ? this.formatCurrency(rt.data.decimal_14)
                      : 0
                  } AED`,
                  addjustedAmount: `${
                    rt.data.decimal_15
                      ? this.formatCurrency(rt.data.decimal_15)
                      : 0
                  } AED`,
                }
                this.canceledTableData.push(data)
              }
            })
          }
        }
        this.loading = false
        this.canceledLoading = false
      })
    },
    cancelData1() {
      let supplierrid = this.details.id
      let { details, cancelpage } = this
      this.canceledLoading = true
      this.params.paramList.push(supplierrid)
      let supliervalue = []
      supliervalue.push(supplierrid + '')
      let statusId = this.billState.find(rt => rt.status === 'alertprocessed')
        .id
      let stateValue = []
      stateValue.push(statusId + '')
      statusId = this.billState.find(rt => rt.status === 'invoicegenerated').id
      stateValue.push(statusId + '')
      let filter = {
        picklist: {
          operatorId: 54,
          value: ['3'],
        },
        moduleState: { operatorId: 36, value: stateValue },
        decimal_14: { operatorId: 13, value: ['0'] },
        supplier: {
          operatorId: 36,
          value: supliervalue,
          selectedLabel: this.details.name,
        },
      }
      let url = `/v2/module/data/list?moduleName=custom_utilitybills&page=${
        cancelpage.page
      }&perPage=${50}&filters=${encodeURIComponent(
        JSON.stringify(filter)
      )}&viewName=all&fetchCount=true&includeParentFilter=true`
      this.$http.get(url).then(resp => {
        console.log('deta')
        if (resp.data.responseCode === 0) {
          cancelpage.totalCount = resp.data.result.count
        }
        this.canceledLoading = false
      })
    },
    getactiveInvoiceData() {
      let supplierrid = this.details.id
      let { details, invoicepage } = this
      this.activeInvoiceLoading = true
      this.params.paramList.push(supplierrid)
      let supliervalue = []
      this.activeInvoiceTableData = []
      supliervalue.push(supplierrid + '')
      let statusId = this.billState.find(rt => rt.status === 'alertprocessed')
        .id
      let stateValue = []
      stateValue.push(statusId + '')
      statusId = this.billState.find(rt => rt.status === 'invoicegenerated').id
      stateValue.push(statusId + '')
      let filter = {
        picklist: {
          operatorId: 54,
          value: ['4'],
        },
        moduleState: { operatorId: 36, value: stateValue },
        decimal_14: { operatorId: 13, value: ['0'] },
        supplier: {
          operatorId: 36,
          value: supliervalue,
          selectedLabel: this.details.name,
        },
      }
      this.loading = true
      let url = `/v2/module/data/list?moduleName=custom_utilitybills&page=${
        invoicepage.page
      }&perPage=${50}&filters=${encodeURIComponent(
        JSON.stringify(filter)
      )}&viewName=all&includeParentFilter=true`
      this.$http.get(url).then(resp => {
        console.log('deta')
        if (resp.data.responseCode === 0) {
          let bills =
            resp.data.result && resp.data.result.moduleDatas
              ? resp.data.result.moduleDatas
              : []
          if (bills && bills.length) {
            bills.forEach(rt => {
              if (rt.data && rt.data.decimal_14 > 0) {
                let data = {
                  id: rt.id,
                  accountNo: rt.data.singleline_2,
                  arrearAmountRaw: rt.data.decimal_14,
                  arrearAmount: `${
                    rt.data.decimal_14
                      ? this.formatCurrency(rt.data.decimal_14)
                      : 0
                  } AED`,
                  addjustedAmount: `${
                    rt.data.decimal_15
                      ? this.formatCurrency(rt.data.decimal_15)
                      : 0
                  } AED`,
                }

                this.activeInvoiceTableData.push(data)
                this.loading = false
              }
            })
          }
        }
        this.activeInvoiceLoading = false
      })
    },
    getactiveInvoiceData1() {
      let supplierrid = this.details.id
      let { details, invoicepage } = this
      this.activeInvoiceLoading = true
      this.params.paramList.push(supplierrid)
      let supliervalue = []
      supliervalue.push(supplierrid + '')
      let statusId = this.billState.find(rt => rt.status === 'alertprocessed')
        .id
      let stateValue = []
      stateValue.push(statusId + '')
      statusId = this.billState.find(rt => rt.status === 'invoicegenerated').id
      stateValue.push(statusId + '')
      let filter = {
        picklist: {
          operatorId: 54,
          value: ['4'],
        },
        moduleState: { operatorId: 36, value: stateValue },
        decimal_14: { operatorId: 13, value: ['0'] },
        supplier: {
          operatorId: 36,
          value: supliervalue,
          selectedLabel: this.details.name,
        },
      }
      let url = `/v2/module/data/list?moduleName=custom_utilitybills&page=${
        invoicepage.page
      }&perPage=${50}&filters=${encodeURIComponent(
        JSON.stringify(filter)
      )}&viewName=all&fetchCount=true&includeParentFilter=true`
      this.$http.get(url).then(resp => {
        console.log('deta')
        if (resp.data.responseCode === 0) {
          invoicepage.totalCount = resp.data.result.count
        }
        this.activeInvoiceLoading = false
      })
    },
    addadjustamount() {
      this.$refs['ruleForm'].validate(valid => {
        if (valid) {
          this.isSaving = true
          let { id, value } = this.adjustamount
          let url = `/v2/workflow/runWorkflow`
          let params = {
            nameSpace: 'arrears',
            functionName: 'updatearrearsAmount',
            paramList: [id, Number(value)],
          }
          this.$http.post(url, params).then(response => {
            if (this.isResponse(response)) {
              this.$message.success(`Success`)
              this.loadunderData()
            }
            this.closeadjDialog()
            this.isSaving = false
          })
        }
      })
    },
    closeadjDialog() {
      this.visibility = false
    },
    adjustArear(row) {
      this.adjustamount.id = row.id
      this.adjustamount.data = row
      this.visibility = true
    },
    isResponse(response) {
      if (response && response.data && response.data.responseCode === 0) {
        return true
      }
      return false
    },
    getFields() {
      this.$http
        .get('/module/meta?moduleName=custom_utilitybills')
        .then(response => {
          if (response.data.meta) {
            this.fieldObject = response.data.meta
          }
          // this.loadutilityLineItem()
          this.loading = false
        })
    },
    createInvoice() {
      let bills = this.selectedOutstanding
      let billIds = bills.map(rt => rt.id)
      this.saving = true
      let url = `${window.location.origin}/etisalat/api/generateArrearInvoiceWithBillIds`
      axios
        .post(url, {
          billIds: billIds,
          supplierId: this.details.id,
        })
        .then(response => {
          this.saving = false
          this.createPMmeta.data = {}
          this.$set(
            this.createPMmeta.data,
            'invoice',
            this.getInvoiceData(response.data)
          )
          this.createPMmeta.info = ''
          this.createPMmeta.buttonInfo = false
          this.tabSwitch()
        })
        .catch(error => {
          this.saving = false
          this.createPMmeta.buttonInfo = false
        })
    },
    getInvoiceData(data) {
      if (data.result && data.result.invoices && data.result.invoices.length) {
        return data.result.invoices[0]
      }
      return null
    },
    changeState(bills, stateId) {
      let billIds = bills.map(rt => rt.id)
      let url = `/v2/workflow/runWorkflow`
      let params = {
        nameSpace: 'arrears',
        functionName: 'updateArrearStatus',
        paramList: [billIds, stateId],
      }
      this.$http.post(url, params).then(response => {
        if (this.isResponse(response)) {
          this.$message.success(`Success`)
        }
        this.loadAllData()
        this.tabSwitch()
      })
    },
    selectedItems(selectedItem) {
      this.selectedOutstanding = selectedItem
    },
    selectedItems2(selectedItem) {
      this.selectedInvoice = selectedItem
    },
    selectedItems3(selectedItem) {
      this.selectedCancel = selectedItem
    },
    selectedItems4(selectedItem) {
      this.selectedUnder = selectedItem
    },
    selectedItems5(selectedItem) {
      this.selectedItem = selectedItem
      console.log('selected item', selectedItem)
    },
    autoResize() {
      this.$nextTick(() => {
        let height = this.$refs['preview-container'].scrollHeight
        let width = this.$refs['preview-container'].scrollWidth
        let { h } = this.calculateDimensions({
          height,
          width,
        })
        if (isEmpty(this.initialWidgetHeight)) {
          this.initialWidgetHeight = h
        }
        this.resizeWidget({
          h: this.isAllVisible ? h : this.initialWidgetHeight,
        })
      })
    },
    formatCurrency(value) {
      return formatCurrency(value, 2)
    },
    goToInvoice(invoice) {
      let routeData = this.$router.resolve({
        path: `/app/supp/custom_invoices/all/${invoice.id}/summary`,
      })
      window.open(routeData.href, '_blank')
      // this.$router.push({
      //   path: `/app/supp/custom_invoices/all/${invoice.id}/summary`,
      // })
    },
    getDate(time) {
      return moment(Number(time))
        .tz(this.$timezone)
        .format('DD MMM YYYY')
    },
    getData() {
      let supplierrid = this.details.id
      let { page, details } = this
      this.allArrearsTableData = []
      this.params.paramList.push(supplierrid)
      let supliervalue = []
      supliervalue.push(supplierrid + '')
      let statusId = this.billState.find(rt => rt.status === 'alertprocessed')
        .id
      let stateValue = []
      stateValue.push(statusId + '')
      statusId = this.billState.find(rt => rt.status === 'invoicegenerated').id
      stateValue.push(statusId + '')
      let filter = {
        picklist: {
          operatorId: 55,
          value: ['5'],
        },
        moduleState: { operatorId: 36, value: stateValue },
        decimal_14: { operatorId: 13, value: ['0'] },
        supplier: {
          operatorId: 36,
          value: supliervalue,
          selectedLabel: this.details.name,
        },
      }
      this.loading = true
      let url = `/v2/module/data/list?moduleName=custom_utilitybills&page=${
        page.page
      }&perPage=${50}&filters=${encodeURIComponent(
        JSON.stringify(filter)
      )}&viewName=all&includeParentFilter=true`
      this.$http.get(url).then(resp => {
        console.log('deta')
        if (resp.data.responseCode === 0) {
          let bills =
            resp.data.result && resp.data.result.moduleDatas
              ? resp.data.result.moduleDatas
              : []
          this.splitBills(bills)
          this.loading = false
        }
        this.autoResize()
      })
    },
    getData1() {
      let supplierrid = this.details.id
      let { page, details } = this
      this.loading = true
      this.params.paramList.push(supplierrid)
      let supliervalue = []
      supliervalue.push(supplierrid + '')
      let statusId = this.billState.find(rt => rt.status === 'alertprocessed')
        .id
      let stateValue = []
      stateValue.push(statusId + '')
      statusId = this.billState.find(rt => rt.status === 'invoicegenerated').id
      stateValue.push(statusId + '')
      let filter = {
        picklist: {
          operatorId: 55,
          value: ['5'],
        },
        moduleState: { operatorId: 36, value: stateValue },
        decimal_14: { operatorId: 13, value: ['0'] },
        supplier: {
          operatorId: 36,
          value: supliervalue,
          selectedLabel: this.details.name,
        },
      }
      let url = `/v2/module/data/list?moduleName=custom_utilitybills&page=${
        page.page
      }&perPage=${50}&filters=${encodeURIComponent(
        JSON.stringify(filter)
      )}&viewName=all&fetchCount=true&includeParentFilter=true`
      this.$http.get(url).then(resp => {
        console.log('deta')
        if (resp.data.responseCode === 0) {
          page.totalCount = resp.data.result.count
        }
        this.loading = false
      })
    },
    splitBills(bills) {
      if (bills && bills.length) {
        this.allArrears = bills
        bills.forEach(rt => {
          if (rt.data && rt.data.decimal_14 > 0) {
            let data = {
              id: rt.id,
              accountNo: rt.data.singleline_2,
              arrearAmount: `${
                rt.data.decimal_14 ? this.formatCurrency(rt.data.decimal_14) : 0
              } AED`,
              addjustedAmount: `${
                rt.data.decimal_15 ? this.formatCurrency(rt.data.decimal_15) : 0
              } AED`,
              status: `${this.getbillfieldData('picklist', rt)}`,
            }

            this.allArrearsTableData.push(data)
          }
        })
      }
    },
    createMemo(listData) {
      let billCount = listData.length
      this.dialogVisible = true
      this.$set(this.createPMmeta.data, 'supplier', this.details.name)
      // this.$set(this.createPMmeta.data, 'billMonth', month)
      // this.$set(
      //   this.createPMmeta.data,
      //   'cost',
      //   `${this.formatCurrency(totalAmount) || 0} AED`
      // )
      this.$set(this.createPMmeta.data, 'count', `${billCount || 0}`)

      this.createPMmeta.info = ''
    },
    handleClose() {
      this.dialogVisible = true
    },
    closeDialog() {
      this.dialogVisible = false
      this.createPMmeta.buttonInfo = true
    },
  },
}
</script>
