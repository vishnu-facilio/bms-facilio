<template>
  <div
    class="custom-module-overview"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header p20">
        <div class="custom-module-details">
          <div class="d-flex flex-middle align-center">
            <div class="mL5">
              <div class="custom-module-id">
                <i
                  v-if="$account.portalInfo"
                  class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                  content="back"
                  arrow
                  v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                  @click="back"
                ></i>
                #{{ record && record.id }}
              </div>
              <div class="custom-module-name d-flex">
                {{ record.transferRequest.requestSubject }}
                <div
                  v-if="
                    record.isStateFlowEnabled() && record.currentModuleState()
                  "
                  class="fc-badge text-uppercase inline vertical-middle mL15"
                >
                  {{ record.currentModuleState() }}
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="marginL-auto flex-middle">
          <CustomButton
            class="pR10"
            :record="record"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="refreshData()"
            @onError="() => {}"
          />
          <template v-if="record.isStateFlowEnabled()">
            <TransitionButtons
              class="mR10"
              :key="`${record.id}transitions`"
              :moduleName="moduleName"
              :record="record"
              :disabled="record.isApprovalEnabled()"
              buttonClass="asset-el-btn"
              @currentState="() => {}"
              @transitionSuccess="refreshData()"
              @transitionFailure="() => {}"
            ></TransitionButtons>
          </template>
          <portal to="pagebuilder-sticky-top" v-if="record.isApprovalEnabled()">
            <ApprovalBar
              :moduleName="moduleName"
              :key="record.id + 'approval-bar'"
              :record="record"
              :hideApprovers="shouldHideApprovers"
              @onSuccess="refreshData()"
              @onFailure="() => {}"
              class="approval-bar-shadow"
            ></ApprovalBar>
          </portal>
          <div class="fc__layout__align inventory-overview-btn-group">
            <el-button
              v-if="
                !$validation.isEmpty(
                  $getProperty(
                    record,
                    'transferRequest.transferrequestlineitems'
                  )
                )
              "
              class="fc__add__btn mR10"
              style="width: unset"
              @click="addReceiptsDialog"
              >{{ $t('common.products.add_reciept') }}</el-button
            >
          </div>
          <el-button
            v-if="showEdit"
            type="button"
            class="fc-wo-border-btn pL15 pR15 self-center"
            @click="editRecord"
          >
            <i class="el-icon-edit"></i>
          </el-button>
          <el-dropdown
            class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
            trigger="click"
            @command="action => summaryDropDownAction(action, record)"
          >
            <span class="el-dropdown-link">
              <inline-svg
                src="svgs/menu"
                class="vertical-middle"
                iconClass="icon icon-md"
              >
              </inline-svg>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                v-if="checkState(record) === 1"
                :key="1"
                :command="'complete_request'"
                >{{ $t('common.header.complete_request') }}</el-dropdown-item
              >
              <el-dropdown-item :key="2" :command="'go_to_transfer_request'">{{
                $t('common.products.go_to_transfer_request')
              }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>

      <Page
        :key="record.id"
        :module="moduleName"
        :id="record.id"
        :details="record"
        :primaryFields="primaryFields"
        :notesModuleName="notesModuleName"
        :isV3Api="true"
        :attachmentsModuleName="attachmentsModuleName"
      ></Page>
    </template>
    <div v-if="addReceiptVisibilty">
      <el-dialog
        :visible.sync="addReceiptVisibilty"
        :fullscreen="false"
        open="top"
        width="90%"
        custom-class="assetaddvaluedialog fc-dialog-center-container fc-dialog-return inventory-store-dialog fc-web-form-dialog"
        :append-to-body="true"
      >
        <template slot="title">
          <div class="label-txt-black fw-bold text-uppercase fL">
            {{ $t('common.products.add_reciept') }}
          </div>
        </template>
        <div class="clearboth">
          <div
            v-if="selectedAddReceiptObj.length > 0"
            @click="markAsFullyReceived(pendingLineItems)"
            class="rv-sum-table-click"
          >
            <inline-svg src="mark-as-fully-recieved" class="mR10"></inline-svg>
            {{ $t('common.wo_report.mark_as_fully_recieved') }}
          </div>
          <el-table
            height="300"
            ref="receiveTable"
            @selection-change="selectAddReceiptsActions"
            :data="
              $getProperty(record, 'transferRequest.transferrequestlineitems')
            "
            :default-sort="{ prop: 'sysCreatedTime', order: 'descending' }"
            class="width100 tr-summary-table"
          >
            <el-table-column width="60" type="selection"></el-table-column>
            <el-table-column
              :label="$t('common.products.line_item')"
              width="200"
            >
              <template v-slot="recData">
                <tool-avatar
                  name="true"
                  size="lg"
                  module="item"
                  :recordData="recData.row.itemType"
                  v-if="recData.row.inventoryType === 1"
                ></tool-avatar>
                <item-avatar
                  name="true"
                  size="lg"
                  module="tool"
                  :recordData="recData.row.toolType"
                  v-else-if="recData.row.inventoryType === 2"
                ></item-avatar>
              </template>
            </el-table-column>
            <el-table-column
              prop="quantity"
              :label="$t('common._common.ordered_quantity')"
              width="200"
            ></el-table-column>
            <el-table-column
              :formatter="quantityDue"
              :label="$t('common.dashboard.quantity_due')"
              width="200"
            ></el-table-column>
            <el-table-column
              width="200"
              :label="$t('common.dashboard.recieving_quantity')"
            >
              <template v-slot="penLineItem">
                <input
                  :placeholder="$t('common._common.quantity')"
                  type="text"
                  v-model.number="penLineItem.row.receivedQuantity"
                  class="form-item fc-input-full-border-select2"
                />
              </template>
            </el-table-column>
            <el-table-column
              width="235"
              :label="$t('common._common.received_date')"
            >
              <template v-slot="penLineItem">
                <f-date-picker
                  type="date"
                  class="form-item fc-input-full-border-select2"
                  v-model="penLineItem.row.receiptDate"
                  value-format="timestamp"
                ></f-date-picker>
              </template>
            </el-table-column>
            <el-table-column
              width="200"
              :label="$t('common.wo_report.remarks')"
            >
              <template v-slot="penLineItem">
                <input
                  :placeholder="$t('common.wo_report.remarks')"
                  v-model="penLineItem.row.remarks"
                  class="form-item fc-input-full-border-select2"
                  type="text"
                />
              </template>
            </el-table-column>
          </el-table>
          <div class="modal-dialog-footer">
            <el-button
              class="modal-btn-cancel"
              @click="addReceiptVisibilty = false"
              >{{ $t('common._common.cancel') }}</el-button
            >
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="saveReceipts"
              >{{ $t('common._common._save') }}</el-button
            >
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import ToolAvatar from '@/avatar/ItemTool'
import ItemAvatar from '@/avatar/ItemTool'
import moment from 'moment-timezone'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  components: {
    FDatePicker,
    ToolAvatar,
    ItemAvatar,
  },
  extends: CustomModuleSummary,
  props: ['viewname'],
  data() {
    return {
      notesModuleName: 'shipmentNotes',
      attachmentsModuleName: 'shipmentAttachments',
      primaryFields: [
        'localId',
        'approvalFlowId',
        'approvalStatus',
        'moduleState',
        'stateFlowId',
      ],
      addReceiptVisibilty: false,
      selectedAddReceiptObj: [],
      pendingLineItems: null,
    }
  },
  computed: {
    moduleName() {
      return 'transferrequestshipment'
    },
    showEdit() {
      let canShowEdit = this.$hasPermission(`transferrequest:UPDATE`)
      let isNotLocked = this.isStateFlowEnabled
        ? !this.isRecordLocked && !this.isRequestedState
        : true
      return canShowEdit && isNotLocked && !this.record.isCompleted
    },
  },
  title() {
    'Transfer Request Shipment'
  },
  methods: {
    checkState(record) {
      if (record.isCompleted === false) {
        return 1
      }
    },
    quantityDue(val) {
      if (isEmpty(val.quantityReceived)) {
        return val.quantity
      } else {
        return val.quantity - val.quantityReceived
      }
    },
    async saveReceipts() {
      let { record } = this
      let lineItems = this.$getProperty(
        record,
        'transferRequest.transferrequestlineitems'
      )
      let promises = lineItems.map(async lineItem => {
        if (!isEmpty(lineItem.receivedQuantity)) {
          let data = {
            shipment: {
              id: record.id,
            },
            lineItem: {
              id: lineItem.id,
            },
            inventoryType: lineItem.inventoryType,

            quantityReceived: lineItem.receivedQuantity,
            receiptDate: lineItem.receiptDate,
            remarks: lineItem.remarks,
          }
          if (lineItem.inventoryType === 1) {
            this.$set(data, 'itemType', { id: lineItem.itemType.id })
          } else {
            this.$set(data, 'toolType', { id: lineItem.toolType.id })
          }
          let param = {
            data: data,
          }
          let { error } = await API.createRecord(
            'transferrequestshipmentreceivables',
            param
          )
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          }
        }
      })

      Promise.all(promises).finally(async () => {
        this.addReceiptVisibilty = false
        await this.loadRecord(true)
      })
    },
    editRecord() {
      let id = this.record.id
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
          })
      } else {
        id &&
          this.$router.push({
            name: 'edit-trShipment',
            params: { id },
          })
      }
    },
    addReceiptsDialog() {
      this.addReceiptVisibilty = true
      let currentmillis = moment.tz(this.timeZone).valueOf()
      let lineItems = this.$getProperty(
        this.record,
        'transferRequest.transferrequestlineitems'
      )
      lineItems.forEach(item => {
        item.receivedQuantity = null
        item.receiptDate = currentmillis
        item.remarks = ''
      })
      this.$set(
        this.record,
        'transferRequest.transferrequestlineitems',
        lineItems
      )
      this.$set(this, 'pendingLineItems', lineItems)
    },
    async summaryDropDownAction(action, record) {
      let id = this.$getProperty(this, 'record.transferRequest.id')
      if (action === 'go_to_transfer_request') {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('transferrequest', pageTypes.OVERVIEW) || {}
          name &&
            this.$router.push({
              name,
              params: {
                id,
                viewname: this.viewname,
                query: this.$route.query,
              },
            })
        } else {
          this.$router.push({
            path: `/app/inventory/transferrequest/all/${id}/overview`,
          })
        }
      }
      if (action === 'complete_request') {
        record.isCompleted = true
        let { error } = await API.updateRecord('transferrequest', {
          id: id,
          data: record,
        })
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let successMsg = 'Transfer Request Completed'
          this.$message.success(successMsg)
          this.loadRecord(true)
        }
      }
    },
    selectAddReceiptsActions(val) {
      this.selectedAddReceiptObj = val
    },
    markAsFullyReceived(val) {
      val.forEach(element => {
        this.selectedAddReceiptObj.forEach(obj => {
          if (element.id === obj.id) {
            element.receivedQuantity = this.quantityDue(element)
          }
        })
      })
      this.$refs.receiveTable.clearSelection()
    },
  },
}
</script>
<style lang="scss">
.fc-dialog-return .el-dialog__header {
  padding: 30px 30px 40px;
  .el-icon-close {
    margin-top: 9px;
    cursor: pointer;
    color: #202d43;
    font-size: 16px;
    font-weight: bold;
  }
}
.fc-dialog-return .el-dialog__headerbtn::before {
  content: '';
  width: 1px;
  height: 20px;
  background: #dde1e4;
  display: block;
  position: absolute;
  left: -16px;
  top: 7px;
}
.fc-dialog-return .el-dialog__body {
  padding: 0 20px 0 !important;
}
input[type='text']:not(.q-input-target):not(.quick-search-input):not(.el-input__inner):not(.el-select__input),
input[type='password'],
select,
input.text {
  margin: 0;
  line-height: 22px;
}
.el-dialog__body {
  font-size: 12px;
}
.tr-summary-table {
  .el-table th.is-leaf,
  .el-table th.el-table__cell > .cell {
    padding-left: 0;
    padding-right: 0;
  }
}
.el-table td,
.el-table th.is-leaf,
.el-table th.el-table__cell > .cell {
  padding-left: 0;
  padding-right: 0;
}
</style>
