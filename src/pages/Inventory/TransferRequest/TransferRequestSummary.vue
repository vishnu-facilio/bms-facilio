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
                <!-- lock icon -->
                <i
                  v-if="record.isRecordLocked()"
                  class="fa fa-lock locked-wo"
                  data-arrow="true"
                  :title="$t('common._common.locked_state')"
                  v-tippy
                ></i>
                <div class="d-flex max-width300px">
                  <el-tooltip
                    placement="bottom"
                    effect="dark"
                    :content="record[mainFieldKey]"
                  >
                    <span class="whitespace-pre-wrap custom-header">{{
                      record[mainFieldKey]
                    }}</span>
                  </el-tooltip>
                </div>
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
          <el-button
            v-if="showEdit"
            type="button"
            class="fc-wo-border-btn pL15 pR15 self-center"
            @click="editRecord"
          >
            <i class="el-icon-edit"></i>
          </el-button>
          <el-dropdown
            v-if="!record.isApprovalEnabled()"
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
                :command="'stage_request'"
                >{{ $t('common.header.stage_request') }}</el-dropdown-item
              >
              <el-dropdown-item
                v-if="checkState(record) === 2"
                :key="2"
                :command="'ship_request'"
                >{{ $t('common.header.ship_request') }}</el-dropdown-item
              >
              <el-dropdown-item
                v-if="checkState(record) === 3"
                :key="3"
                :command="'complete_request'"
                >{{ $t('common.header.complete_request') }}</el-dropdown-item
              >
              <el-dropdown-item
                v-if="checkShipmentState(record) === 4"
                :key="4"
                :command="'go_to_shipment'"
                >{{ $t('common.products.go_to_shipment') }}</el-dropdown-item
              >
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
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: CustomModuleSummary,
  props: ['viewname'],
  data() {
    return {
      notesModuleName: 'transferrequestnotes',
      attachmentsModuleName: 'transferrequestattachments',
      primaryFields: [
        'localId',
        'approvalFlowId',
        'approvalStatus',
        'moduleState',
        'stateFlowId',
        'transferFromStore',
        'transferToStore',
        'transferInitiatedOn',
        'expectedCompletionDate',
        'sysCreatedBy',
        'transferredBy',
        'description',
      ],
    }
  },
  computed: {
    moduleName() {
      return 'transferrequest'
    },
    mainFieldKey() {
      return 'requestSubject'
    },
    showEdit() {
      let canShowEdit = this.$hasPermission(`transferrequest:UPDATE`)
      let isNotLocked = this.isStateFlowEnabled
        ? !this.isRecordLocked && !this.isRequestedState
        : true
      return canShowEdit && isNotLocked && !this.record.isStaged
    },
  },
  title() {
    'Transfer Request'
  },
  methods: {
    editRecord() {
      let { id } = this
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
            name: 'edit-transferrequest',
            params: { id },
          })
      }
    },
    checkState(record) {
      if (record.isStaged === false) {
        return 1
      } else if (
        record.isShipped === false &&
        record.isShipmentTrackingNeeded === true
      ) {
        return 2
      } else if (record.isCompleted === false) {
        return 3
      }
    },
    checkShipmentState(record) {
      if (record.isShipped === true) {
        return 4
      }
    },
    async summaryDropDownAction(action, record) {
      let { moduleName, id } = this || {}
      let successMsg
      if (
        ['stage_request', 'ship_request', 'complete_request'].includes(action)
      ) {
        if (action === 'stage_request') {
          record.isStaged = true
          successMsg = 'Staged Successfully'
        } else if (action === 'ship_request') {
          record.isShipped = true
        } else if (action === 'complete_request') {
          record.isCompleted = true
          successMsg = 'Transfer Request Completed'
        }
        let { error } = await API.updateRecord(moduleName, {
          id: id,
          data: record,
        })
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          if (!isEmpty(successMsg)) {
            this.$message.success(successMsg)
            this.loadRecord(true)
          }
        }
        if (action === 'ship_request') {
          let shipmentRecord = {}
          this.$set(shipmentRecord, 'transferRequest', { id: id })
          this.$set(
            shipmentRecord,
            'expectedCompletionDate',
            record.expectedCompletionDate
          )
          this.$set(shipmentRecord, 'isCompleted', record.isCompleted)
          let { error } = await API.createRecord('transferrequestshipment', {
            data: shipmentRecord,
          })
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            let successMsg = 'Shipment Record Created'
            this.$message.success(successMsg)
            this.loadRecord(true)
          }
        }
      }
      if (action === 'go_to_shipment') {
        let id = this.$getProperty(this, 'record.shipmentId')

        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('transferrequestshipment', pageTypes.OVERVIEW) ||
            {}
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
            path: `/app/inventory/trShipment/all/${id}/overview`,
          })
        }
      }
    },
  },
}
</script>
