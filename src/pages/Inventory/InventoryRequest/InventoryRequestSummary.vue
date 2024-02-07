<template>
  <div class="custom-modules-overview overflow-y-scroll">
    <div class="header pT10 pB15 pL20 pR20">
      <div v-if="!$validation.isEmpty(inventoryRequest)" class="asset-details">
        <div class="d-flex flex-middle">
          <div class="mL5">
            <div class="asset-id mT10">#{{ inventoryrequestId }}</div>
            <div class="asset-name d-flex mb5 mT5">
              <!-- lock icon -->
              <i
                v-if="isRecordLocked"
                class="fa fa-lock locked-wo mT0"
                data-arrow="true"
                :title="$t('common._common.locked_state')"
                v-tippy
              ></i>
              <el-tooltip
                placement="bottom"
                effect="dark"
                :content="inventoryRequest.name"
              >
                <span class="whitespace-pre-wrap custom-header">{{
                  inventoryRequest.name
                }}</span>
              </el-tooltip>
              <div
                v-if="currentModuleState"
                class="fc-badge text-uppercase inline vertical-middle mL15"
              >
                {{ currentModuleState }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div
        v-if="!isLoading"
        class="d-flex flex-direction-row align-center"
        style="margin-left: auto;"
      >
        <CustomButton
          class="p10"
          :record="inventoryRequest"
          :key="inventoryrequestId + '-custom-button'"
          :moduleName="moduleName"
          :position="POSITION.SUMMARY"
        />
        <template v-if="isStateFlowEnabled">
          <TransitionButtons
            class="mR10"
            :key="inventoryrequestId"
            :moduleName="moduleName"
            :record="inventoryRequest"
            :transformFn="transformFn"
            :disabled="isApprovalEnabled"
            buttonClass="asset-el-btn"
            @currentState="() => {}"
            @transitionSuccess="loadInventoryRequest(true)"
            @transitionFailure="() => {}"
          ></TransitionButtons>
        </template>

        <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
          <ApprovalBar
            :moduleName="moduleName"
            :key="inventoryrequestId + 'approval-bar'"
            :record="inventoryRequest"
            :transformFn="transformFn"
            @onSuccess="loadInventoryRequest(true)"
            @onFailure="() => {}"
            class="approval-bar-shadow"
          ></ApprovalBar>
        </portal>
      </div>
    </div>
    <page
      v-if="inventoryRequest && inventoryRequest.id"
      :key="inventoryrequestId"
      :module="moduleName"
      :id="inventoryrequestId"
      :details="inventoryRequest"
      :primaryFields="['name']"
      notesModuleName="inventoryrequestnotes"
      :isV3Api="true"
      attachmentsModuleName="inventoryrequestattachments"
    ></page>
    <RotatingItemDialog
      v-if="rotatingItemChooserDialog"
      ref="rotatingItemDialog"
      :rotatingLineItemsList="rotatingLineItemsList"
      :inventoryrequest="inventoryRequest"
      :selectedStoreId="selectedStoreId"
      @nextStep="nextStep"
      @requestIssue="issueInventoryRequest"
      @onClose="closeDialog"
    ></RotatingItemDialog>
    <StoreRoomDetailsDialog
      v-if="storeRoomChooserDialogVisibility"
      :selectedStoreId.sync="selectedStoreId"
      :inventoryrequest="inventoryRequest"
      :updateStatus="updateStatus"
      @onClose="closeDialog"
    ></StoreRoomDetailsDialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import RotatingItemDialog from './RotatingItemDialog'
import StoreRoomDetailsDialog from './StoreRoomDetailsDialog'
import Page from '@/page/PageBuilder'
import TransitionButtons from '@/stateflow/TransitionButtons'
import ApprovalBar from '@/approval/ApprovalBar'
import { mapGetters } from 'vuex'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import { eventBus } from '@/page/widget/utils/eventBus'

const inventoryTypes = {
  ITEM_TYPE: 1,
  TOOL_TYPE: 2,
}

export default {
  props: ['moduleName', 'viewname', 'id'],

  components: {
    RotatingItemDialog,
    StoreRoomDetailsDialog,
    Page,
    TransitionButtons,
    ApprovalBar,
    CustomButton,
  },

  data() {
    return {
      inventoryRequest: null,
      isLoading: false,
      rotatingItemChooserDialog: false,
      storeRoomChooserDialogVisibility: false,
      rotatingLineItemsList: [],
      selectedStoreId: null,
      issueBtnLoading: false,
      POSITION: POSITION_TYPE,
    }
  },

  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
  },
  mounted() {
    eventBus.$on('refresh-inventory-request-summary', () => {
      this.loadInventoryRequest()
    })
  },
  computed: {
    ...mapGetters(['getTicketStatus', 'isStatusLocked']),
    inventoryrequestId() {
      return this.id
    },
    currentModuleState() {
      let { moduleName, inventoryRequest } = this
      let currentStateId = this.$getProperty(inventoryRequest, 'moduleState.id')
      let currentState = this.getTicketStatus(currentStateId, moduleName)
      let { displayName } = currentState || {}

      return displayName || null
    },
    isRecordLocked() {
      let { moduleName, inventoryRequest } = this

      if (this.isStateFlowEnabled) {
        let hasState = this.$getProperty(inventoryRequest, 'moduleState.id')
        return hasState && this.isStatusLocked(hasState, moduleName)
      }
      return false
    },
    canShowIssueBtn() {
      let { inventoryRequest } = this
      let { approvalFlowId, isIssued } = inventoryRequest || {}

      return isEmpty(approvalFlowId) && !isIssued
    },
    isStateFlowEnabled() {
      let hasState = this.$getProperty(this.inventoryRequest, 'moduleState.id')
      return hasState
    },
    isApprovalEnabled() {
      let { inventoryRequest } = this
      let { approvalFlowId, approvalStatus } = inventoryRequest || {}
      return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
    },
  },
  watch: {
    inventoryrequestId: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.loadInventoryRequest()
        }
      },
      immediate: true,
    },
  },
  methods: {
    async loadInventoryRequest(force) {
      this.isLoading = true
      let { moduleName, inventoryrequestId } = this

      let { error, [moduleName]: data } = await API.fetchRecord(
        moduleName,
        { id: inventoryrequestId },
        { force }
      )

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let { id, name } = data || {}

        this.inventoryRequest = data || {}
        this.setTitle(`[#${id}] ${name}`)
      }
      this.isLoading = false
    },
    transformFn(data, formValues = {}) {
      data['inventoryRequest'] = {
        ...(data['inventoryRequest'] || {}),
        id: data.id,
        ...formValues,
      }

      return data
    },
    issueInventoryRequestActions() {
      let { inventoryrequestlineitems: lineItems } = this.inventoryRequest

      ;(lineItems || []).forEach(lineItem => {
        let { inventoryType, itemType, toolType } = lineItem
        let { isRotating: itemIsRotating } = itemType || {}
        let { isRotating: toolIsRotating } = toolType || {}
        let canAddLineItem =
          (inventoryType === inventoryTypes.ITEM_TYPE && itemIsRotating) ||
          (inventoryType === inventoryTypes.TOOL_TYPE && toolIsRotating)

        canAddLineItem && this.rotatingLineItemsList.push(lineItem)
      })
      this.openIssueDialog()
    },
    openIssueDialog() {
      let { storeRoom } = this.inventoryRequest

      if (storeRoom) {
        if (this.rotatingLineItemsList.length > 0) {
          this.storeRoomChooserDialogVisibility = false
          this.rotatingItemChooserDialog = true
        } else {
          this.issueInventoryRequest()
        }
      } else {
        this.storeRoomChooserDialogVisibility = true
      }
    },
    async issueInventoryRequest() {
      this.issueBtnLoading = true

      let {
        id,
        inventoryrequestlineitems,
        storeRoom,
        parentId,
        requestedFor,
        requestedBy,
      } = this.inventoryRequest
      let inventoryRequest = {
        id,
        inventoryrequestlineitems,
        storeRoom,
        parentId,
        requestedFor,
        requestedBy,
      }
      let { error } = await API.updateRecord('inventoryrequest', {
        id,
        data: { ...inventoryRequest },
        params: { issue: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.$message.success('Items Issued Successfully')
        await this.loadInventoryRequest(true)
      }
      this.closeDialog()
    },
    nextStep(assetIds, idx) {
      let { id } = this.rotatingLineItemsList[idx]

      this.rotatingLineItemsList[idx].assetIds = assetIds
      this.inventoryRequest.inventoryrequestlineitems.forEach(lineItem => {
        if (lineItem.id === id) {
          lineItem.assetIds = assetIds
        }
      })
    },
    async updateStatus({ store = null }) {
      let { inventoryRequest } = this
      let { storeRoom } = inventoryRequest

      if (!storeRoom && store) {
        this.inventoryRequest.storeRoom = store
      }
      this.openIssueDialog()
    },
    closeDialog() {
      this.rotatingItemChooserDialog = false
      this.rotatingLineItemsList = []
      this.selectedStoreId = null
      this.storeRoomChooserDialogVisibility = false
      this.issueBtnLoading = false
    },
  },
}
</script>
<style scoped>
.custom-modules-overview .header {
  background: #fff;
  display: flex;
}
.custom-header {
  display: -webkit-box;
  max-width: 300px;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.asset-details {
  flex-grow: 1;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.asset-details .asset-id {
  font-size: 12px;
  color: #39b2c2;
}
.asset-details .asset-name {
  font-size: 16px;
  color: #324056;
  font-weight: 500;
}
.asset-details .asset-space {
  font-size: 13px;
  color: #8ca1ad;
}
.fc-badge {
  color: #fff;
  background-color: #23b096;
  padding: 3px 12px 4px;
  font-weight: bold;
}
.img-container {
  width: 40px;
  height: 40px;
  border: 1px solid #f9f9f9;
  border-radius: 50%;
}
.delete-dialog-footer {
  display: flex;
  justify-content: center;
}
.dialog-d >>> .el-dialog__header {
  padding: 0px;
}
.delete-dissociate-buttons {
  letter-spacing: 1px;
  text-align: center;
  color: #ffffff;
  font-size: 12px;
  background-color: #ec7c7c;
}
.issue-btn {
  font-weight: 500;
  border-radius: 3px;
  background-color: #39b2c2;
  color: #fff;
  cursor: pointer;
  margin-right: 15px;
  text-transform: uppercase;
  height: 40px;
  border: solid 1px #39b2c2;
  background-color: #39b2c2;
  line-height: 1;
  display: inline-block;
  letter-spacing: 0.7px;
  padding: 14px 25px;
  font-size: 13px;
  font-weight: 500;
}
.issue-btn:hover {
  color: #fff;
  background: #3cbfd0;
  transition: all 0.5s ease-in-out;
  -webkit-transition: all 0.5s ease-in-out;
}
</style>
