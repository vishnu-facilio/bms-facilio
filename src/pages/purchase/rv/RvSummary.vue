<template>
  <div class="custom-module-overview" :class="customClass">
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
                #{{ record && (record.poId || {}).localId }}
              </div>
              <div class="custom-module-name d-flex">
                <div class="d-flex max-width500px">
                  {{ (record.poId || {}).name }}
                </div>
                <div
                  class="fc-badge text-uppercase inline vertical-middle mL15"
                >
                  {{ getReceivableStatus }}
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
          <div
            v-if="canShowAddOrReturnLineItem"
            class="fc__layout__align inventory-overview-btn-group"
          >
            <el-button
              class="fc__add__btn"
              style="width:unset"
              @click="addReceiptsDialog"
              >{{ $t('common.products.add_reciept') }}</el-button
            >
            <el-button
              class="fc__add__btn mR15 fc__border__btn"
              @click="returnLineItemDialog"
              >{{ $t('common._common.return') }}</el-button
            >
          </div>
        </div>
      </div>
      <addReturnReceipts
        v-if="addReceiptVisibilty"
        :recordList="pendingLineItems"
        :isAddReceipt="true"
        :title="$t('common.products.add_reciept')"
        :onMarking="$t('common.wo_report.mark_as_fully_recieved')"
        @onClose="addReceiptVisibilty = false"
        @onSave="receiveLineItem"
      >
      </addReturnReceipts>
      <addReturnReceipts
        v-if="returnLineItemVisibilty"
        :recordList="receivedPoLineItems"
        :isReturnReceipts="true"
        :title="$t('common._common.return_receipt')"
        :onMarking="$t('common.wo_report.mark_as_fully_returned')"
        @onClose="returnLineItemVisibilty = false"
        @onSave="returnLineItem"
      >
      </addReturnReceipts>
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
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import addReturnReceipts from './addReturnReceipts'

export default {
  components: {
    addReturnReceipts,
  },
  extends: CustomModuleSummary,
  props: ['viewname'],
  data() {
    return {
      notesModuleName: 'receivablenotes',
      attachmentsModuleName: 'receivableattachments',
      primaryFields: [
        'localId',
        'approvalFlowId',
        'approvalStatus',
        'moduleState',
        'stateFlowId',
      ],
      addReceiptVisibilty: false,
      returnLineItemVisibilty: false,
      pendingLineItems: null,
      receivedPoLineItems: null,
      lineItems: null,
      receipts: null,
      pendingLoading: false,
      saveLoading: {
        receive: false,
        return: false,
      },
    }
  },
  computed: {
    moduleName() {
      return 'receivable'
    },
    getReceivableStatus() {
      let receivableStatus = {
        1: 'Yet to be Received',

        2: 'Partially Received',

        3: 'Received',

        4: 'Completed',
      }

      let { status } = this.record || {}

      return receivableStatus[status]
    },
    canShowAddOrReturnLineItem() {
      const statusObj = { RECEIVED: 3 }

      let { status, poId } = this.record || {}
      let { completedTime } = poId || {}

      return status !== statusObj.RECEIVED && isEmpty(completedTime)
    },
  },
  title() {
    'Receivable'
  },
  methods: {
    async receiveLineItem() {
      let promise
      let receipts = []

      let isQuantityExceeds = this.pendingLineItems.some(item => {
        let { receivedQuantity, quantity, quantityReceived } = item || {}
        let remainingQuantity = quantity - quantityReceived
        return !isEmpty(receivedQuantity)
          ? receivedQuantity > remainingQuantity
          : false
      })
      if (isQuantityExceeds) {
        this.$message.error(
          'Receiving Quantity is Greater than Pending Quantity'
        )
      } else {
        receipts = this.pendingLineItems
          .filter(
            item =>
              !isEmpty(item?.receivedQuantity) &&
              parseInt(item?.receivedQuantity) !== 0
          )
          .map(item => {
            let { receivedQuantity, id, remarksData } = item || {}

            let { id: receivableId } = this.record || {}

            return {
              receivableId,
              lineItem: { id },
              quantity: parseInt(receivedQuantity),
              status: 1,
              remarks: remarksData,
            }
          })
      }
      if (!isEmpty(receipts)) {
        this.saveLoading.receive = true
        let url = 'v3/modules/data/bulkCreate'
        let params = {
          data: {
            receipts: receipts,
          },
          moduleName: 'receipts',
        }
        promise = await API.post(url, params)
        let { error } = (await promise) || {}
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success('Line Items Received Successfully')
          this.addReceiptVisibilty = false
          await this.loadRecord(true)
        }
        this.saveLoading.receive = false
      }
    },
    async returnLineItem() {
      let promise
      let receipts = []
      let isQuantityExceeds = this.receivedPoLineItems.some(item => {
        let { returnQuantity, quantityReceived } = item || {}
        return returnQuantity > quantityReceived ? true : false
      })
      if (isQuantityExceeds) {
        this.$message.error(
          'Returning Quantity is Greater than Received Quantity'
        )
      } else {
        receipts = this.receivedPoLineItems
          .filter(
            item =>
              !isEmpty(item?.returnQuantity) && parseInt(item?.returnQuantity)
          )
          .map(item => {
            let { returnQuantity, id, remarksData } = item || {}
            let { id: receivableId } = this.record || {}
            return {
              receivableId,
              lineItem: { id },
              quantity: parseInt(returnQuantity),
              status: 2,
              remarks: remarksData,
            }
          })
      }
      if (!isEmpty(receipts)) {
        this.saveLoading.return = true
        let url = 'v3/modules/data/bulkCreate'
        let params = {
          data: {
            receipts: receipts,
          },
          moduleName: 'receipts',
        }
        promise = await API.post(url, params)
        let { error } = (await promise) || {}
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success('Line Items Returned Successfully')
          this.returnLineItemVisibilty = false
          await this.loadRecord(true)
        }
        this.saveLoading.return = false
      }
    },
    addReceiptsDialog() {
      this.addReceiptVisibilty = true
      let lineItems = this.$getProperty(this.record, 'poId.lineItems')
      this.pendingLineItems = lineItems
        .filter(
          item =>
            isEmpty(item?.quantityReceived) ||
            item?.quantityReceived < item?.quantity
        )
        .map(item => ({ ...item, receivedQuantity: null }))
    },
    returnLineItemDialog() {
      this.returnLineItemVisibilty = true
      let lineItems = this.$getProperty(this.record, 'poId.lineItems')
      this.receivedPoLineItems = lineItems
        .filter(item => parseInt(item?.quantityReceived) > 0)
        .map(item => ({ ...item, returnQuantity: null }))
    },
  },
}
</script>
