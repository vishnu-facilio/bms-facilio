<template>
  <div>
    <LineItemList
      v-bind="$attrs"
      ref="invreq-lineitem-list"
      :config="listConfiguration"
      :moduleName="moduleName"
      :widgetDetails="widgetDetails"
      :additionalParams="additionalParams"
      :moduleDisplayName="moduleDisplayName"
      :viewname="viewname"
      @onDelete="refreshData"
      @onCreateOrUpdate="refreshData"
      @handleSelection="selectedRecords"
      class="height-100"
    >
      <template #bulk-actions>
        <div class="d-flex">
          <div
            v-if="checkReserveInventoryPermission() && canEditOrDelete()"
            class="btn-container margin-auto"
            @click="reserveMultipleLineItems()"
          >
            <div class="text-center">
              <Inline-svg
                src="svgs/plans/ic-check-circle-reserve"
                iconClass="icon icon-sm-md"
              ></Inline-svg>
            </div>

            <span class="reserve text-center">
              {{ $t('common._common.reserve') }}
            </span>
          </div>
          <div
            v-if="canEditOrDelete()"
            class="btn-container margin-auto"
            @click="deleteLineItems()"
          >
            <div class="text-center">
              <Inline-svg
                src="svgs/plans/ic-delete"
                iconClass="icon icon-sm-md"
              ></Inline-svg>
            </div>

            <div class="reserve text-center">
              {{ $t('common._common.delete') }}
            </div>
          </div>
        </div>
      </template>
      <template #[tableSlotList[0].name]="{ record }">
        <el-button
          v-if="showReserveButton(record)"
          class="reserve-btn visibility-hide-actions"
          @click="reserveLineItem(record)"
        >
          {{ $t('common._common.reserve') }}
        </el-button>
        <div
          v-else-if="showReservedButton(record)"
          class="flex-middle reserve-msg"
        >
          <fc-icon
            group="action"
            name="circle-tick"
            size="14"
            color="#008759"
          ></fc-icon>
          <div class="mL4">
            {{ $t('common._common.reserved') }}
          </div>
        </div>
      </template>
    </LineItemList>
    <el-dialog
      :visible.sync="showReservationSummary"
      :fullscreen="false"
      :title="$t('common._common.reservation_summary')"
      open="top"
      width="80%"
      custom-class="vendor-wizard assetaddvaluedialog fc-dialog-center-container fc-dialog-return inventory-store-dialog fc-web-form-dialog"
      :append-to-body="true"
    >
      <el-table
        :data="reservingLineItems"
        style="width: 100%"
        height="300px"
        class="rfq-award-vendors-table"
      >
        <el-table-column
          prop="reservingLineItems"
          :formatter="inventoryName"
          :label="inventoryTypeName"
          min-width="200"
        >
        </el-table-column>
        <el-table-column
          prop="reservingLineItems"
          :formatter="description"
          :label="$t('common.roles.description')"
          min-width="150"
        >
        </el-table-column>
        <el-table-column
          prop="reservingLineItems"
          :formatter="storeroomName"
          :label="$t('common.products.storeroom')"
          min-width="100"
        >
        </el-table-column>
        <el-table-column
          prop="reservingLineItems"
          :formatter="availableQuantity"
          :label="$t('common.header.available_quantity')"
          min-width="150"
        >
        </el-table-column>
        <el-table-column
          prop="quantity"
          :label="$t('common._common.quantity')"
          min-width="100"
        >
        </el-table-column>
        <el-table-column prop="reservingLineItems" min-width="200">
          <template v-slot="reservingLineItem">
            <div class="fw-550 flex-middle">
              <fc-icon
                v-if="errorMsg(reservingLineItem)"
                group="alert"
                name="circle-warning"
                color="#DE340A"
                size="14"
              ></fc-icon>
              <fc-icon
                v-else-if="warningMsg(reservingLineItem)"
                group="alert"
                name="triangle-warning"
                color="#FFAB00"
                size="14"
              ></fc-icon>

              <div class="mL4">
                {{ $getProperty(reservingLineItem, 'row.errorType', '') }}
              </div>
            </div>
            <div>
              {{ $getProperty(reservingLineItem, 'row.errorMessage', '') }}
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="text-right reservation-footer">
        <span class="reservation-summary-msg">
          <div class="quantity-reservable-text">
            {{ getReservationQuantityMsg }}
          </div>
          <div class="continue-txt">
            {{ $t('common._common.continue') }}
          </div>
        </span>

        <el-button
          class="reserve-save-btn"
          @click="reserveInvReqLineItems"
          :disabled="canShowReserveButton"
        >
          <span class="reserve-btn-text">
            {{ $t('common._common.reserve') }}
          </span>
        </el-button>
        <el-button
          class="cancel-reserve-btn"
          @click="closeReservationSummary()"
        >
          <span class="cancel-btn-text">{{ $t('common._common.cancel') }}</span>
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemList.vue'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import Vue from 'vue'
import { eventBus } from '@/page/widget/utils/eventBus'
import InventoryMixin from 'pages/Inventory/InventoryMixin'

export default {
  mixins: [InventoryMixin],
  name: 'InventoryRequestLineItems',
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'groupKey',
    'activeTab',
    'widget',
    'resizeWidget',
    'module',
  ],
  created() {
    this.$store.dispatch('loadApprovalStatus')
  },
  data() {
    return {
      recordIds: [],
      reservingLineItems: [],
      showReservationSummary: false,
      inventoryType: null,
      selectedLineItems: [],
      reservableLineItems: [],
      tableSlotList: [
        {
          name: 'additional-action-reservation',
          isActionColumn: true,
          columnAttrs: {
            width: 130,
            class: 'visibility-visible-actions',
            fixed: 'right',
          },
        },
      ],
    }
  },
  components: {
    LineItemList,
  },
  computed: {
    moduleName() {
      return 'inventoryrequestlineitems'
    },

    getReservationQuantityMsg() {
      let { reservableLineItems, reservingLineItems } = this
      let msg = `${reservableLineItems.length} of ${reservingLineItems.length} items reservable`
      return msg
    },
    additionalParams() {
      let { inventoryType } = this || {}
      let { id, storeRoom } = this.details || {}
      return {
        inventoryRequestId: {
          id: id,
        },
        inventoryType: inventoryType,
        storeRoom: {
          id: storeRoom?.id,
        },
      }
    },
    filters() {
      let { details, inventoryType } = this || {}
      let { id } = details || {}
      let filter = {
        inventoryRequestId: {
          operatorId: 9,
          value: [`${id}`],
        },
        inventoryType: {
          operatorId: 9,
          value: [`${inventoryType}`],
        },
      }
      return filter
    },
    columnCustomConfig() {
      return { canHideColumnConfig: true }
    },

    listConfiguration() {
      let {
        filters,
        searchAndFilterConfig,
        columnCustomConfig,
        formConfig,
        tableSlotList,
        canShowMultiSelect,
      } = this
      return {
        filters,
        hideListSelect: !canShowMultiSelect,
        canHideFooter: false,
        ...(columnCustomConfig || {}),
        ...(searchAndFilterConfig || {}),
        ...(formConfig || {}),
        tableSlotList: tableSlotList || [],
        mainfieldAction: () => {},
        timezone: Vue.prototype.$timezone,
        timeformat: Vue.prototype.$timeformat || 'HH:mm a',
        dateformat: Vue.prototype.$dateformat,
        isRecordEditable: record => {
          return (
            !this.$getProperty(record, 'isReserved') && this.canEditOrDelete()
          )
        },
        isRecordDeletable: record => {
          return (
            !this.$getProperty(record, 'isReserved') && this.canEditOrDelete()
          )
        },
        checkSelection: record => {
          return !this.$getProperty(record, 'isReserved')
        },
      }
    },

    widgetDetails() {
      let { widget, moduleDisplayName, emptyStateBtnList } = this
      let { relatedList } = widget || {}
      let { summaryWidgetName } = relatedList || {}
      let emptyStateText = this.$t('setup.relationship.no_module_available', {
        moduleName: moduleDisplayName,
      })
      return {
        canHideTitle: true,
        perPage: 5,
        summaryWidgetName,
        emptyStateText,
        actionButtonList: emptyStateBtnList,
      }
    },

    canShowReserveButton() {
      let { reservableLineItems } = this
      return isEmpty(reservableLineItems)
    },
  },
  methods: {
    async deleteLineItems() {
      let { selectedLineItems } = this
      this.recordIds = []
      selectedLineItems.forEach(lineItem => {
        let id = this.$getProperty(lineItem, 'id')
        this.recordIds.push(id)
      })
      let { error } = await API.deleteRecord(this.moduleName, this.recordIds)
      if (error) {
        let { message } = error
        this.$message.error(message || this.$t('common._common.error_occured'))
        return {}
      } else {
        let { moduleDisplayName } = this
        this.$message.success(
          `${moduleDisplayName} ${this.$t('custommodules.list.delete_success')}`
        )
        this.$refs['invreq-lineitem-list']?.refreshRecordList(true)
        this.$refs['invreq-lineitem-list']?.clearSelection()
        this.refreshData()
      }
    },
    hasEditPermission() {
      let { moduleName } = this || {}
      return this.$hasPermission(`${moduleName}:EDIT,UPDATE`)
    },
    refreshData() {
      eventBus.$emit('refresh-inventory-request-summary')
    },
    isReservationStatusPending() {
      let { details } = this
      let { inventoryRequestReservationStatusEnum } = details
      let isPending = inventoryRequestReservationStatusEnum === 'PENDING'
      return isPending
    },
    selectedRecords(selectedList) {
      this.selectedLineItems = selectedList
    },

    storeroomName(val) {
      return this.$getProperty(val, 'storeRoom.name', '---')
    },
    availableQuantity(val) {
      return this.$getProperty(val, 'availableQuantity', '---')
    },
    description(val) {
      return this.$getProperty(val, 'description', '---')
    },
    showReserveButton(record) {
      return (
        !this.$getProperty(record, 'isReserved') &&
        this.checkReserveInventoryPermission() &&
        this.canEditOrDelete()
      )
    },
    canEditOrDelete() {
      return !this.isRecordLocked() && !this.isRequestedState()
    },
    canShowMultiSelect() {
      return this.checkReserveInventoryPermission() || this.canEditOrDelete()
    },

    isRecordLocked() {
      let { moduleState } = this.details || {}
      let hasState = this.$getProperty(moduleState, 'id')
      return (
        hasState &&
        this.$store.getters.isStatusLocked(hasState, 'inventoryrequest')
      )
    },

    isRequestedState() {
      let { approvalStatus } = this.details || {}

      if (!isEmpty(approvalStatus)) {
        let statusObj = this.$store.getters.getApprovalStatus(approvalStatus.id)
        return this.$getProperty(statusObj, 'requestedState', false)
      }
      return false
    },
    showReservedButton(record) {
      let { isReserved } = record
      return isReserved
    },
    closeReservationSummary() {
      this.showReservationSummary = false
    },
    async reserveInvReqLineItems() {
      let { reservableLineItems } = this
      let { workorder } = this.details || {}
      if (isEmpty(workorder)) {
        this.$message.error(
          this.$t('common.inventory.workorder_cannot_be_empty')
        )
      } else {
        let workorderId = workorder?.id
        let inventoryrequestlineitems = (reservableLineItems || []).map(
          lineItem => ({
            id: this.$getProperty(lineItem, 'id'),
          })
        )
        let url = 'v3/modules/data/bulkpatch'
        let params = {
          data: {
            inventoryrequestlineitems: inventoryrequestlineitems,
          },
          moduleName: this.moduleName,
          params: {
            reserve: true,
            workorderId: workorderId,
          },
        }
        if (!isEmpty(inventoryrequestlineitems)) {
          let { error } = await API.post(url, params)
          if (error) {
            this.$message.error(
              error.message || this.$t('common._common.error_occured')
            )
          } else {
            this.$message.success(this.$t('common._common.reserve_success'))
            this.showReservationSummary = false
            this.$refs['invreq-lineitem-list']?.refreshRecordList(true)
            this.$refs['invreq-lineitem-list']?.clearSelection()
          }
        }
      }
    },

    async reserveLineItem(record) {
      let { id } = record || {}
      let params = {
        reserve: true,
        filters: JSON.stringify({
          id: { operatorId: 9, value: [`${id}`] },
        }),
      }
      let { list, error } = await API.fetchAll(this.moduleName, params)
      if (error) {
        let { message } = error
        this.$message.error(message || this.$t('common._common.error_occured'))
      } else {
        this.reservingLineItems = list || {}
        this.setReservableLineItems(this.reservingLineItems)
        this.showReservationSummary = true
      }
    },
    errorMsg(lineItem) {
      return this.$getProperty(lineItem, 'row.errorType') === 'Non-reservable'
    },
    warningMsg(lineItem) {
      return this.$getProperty(lineItem, 'row.errorType') === 'Reservable'
    },
    async reserveMultipleLineItems() {
      let { selectedLineItems } = this
      let recordIds = selectedLineItems.map(element => {
        let { id } = element
        return `${id}`
      })
      let params = {
        reserve: true,
        filters: JSON.stringify({
          id: { operatorId: 9, value: recordIds },
        }),
      }
      let { list, error } = await API.fetchAll(this.moduleName, params)
      if (error) {
        let { message } = error
        this.$message.error(message || this.$t('common._common.error_occured'))
      } else {
        this.reservingLineItems = list || {}
        this.setReservableLineItems(this.reservingLineItems)
        this.showReservationSummary = true
      }
    },
    setReservableLineItems(reservingLineItems) {
      this.reservableLineItems = (reservingLineItems || []).filter(lineItem => {
        let errorType = this.$getProperty(lineItem, 'errorType')
        return errorType !== 'Non-reservable'
      })
    },
  },
}
</script>
<style lang="scss">
.vendor-wizard.fc-dialog-center-container .el-dialog__body {
  padding-left: 30px;
  padding-right: 20px;
}
.rfq-award-vendors-table.el-table--enable-row-transition
  .el-table__body
  td.el-table__cell {
  padding-right: 20px;
}
.rfq-award-vendors-table .el-table td,
.el-table th.is-leaf {
  padding-left: 0;
  padding-right: 0;
}
</style>
<style lang="scss" scoped>
.fw-550 {
  font-weight: 550;
}

.btn-container {
  width: 96px;
  height: 40px;
  padding: 2px 25px;
  border-radius: 4px;
  background-color: #fff;
  cursor: pointer;
  &:hover {
    background-color: #dde0e4;
  }
}
</style>
<style scoped>
.fw-550 {
  font-weight: 550;
}
.reserve-save-btn {
  margin: 0 10px;
  padding: 12px 22px;
  border-radius: 4px;
  background-color: #3ab2c2;
}
.reserve-btn-text {
  font-size: 14px;
  font-weight: 500;
  text-align: center;
  color: #fff;
}
.reservation-footer {
  position: absolute;
  right: 30px;
  bottom: 30px;
  left: 0px;
}
.cancel-btn-text {
  font-size: 14px;
  font-weight: 500;
  text-align: center;
  color: #324056;
}
.cancel-reserve-btn {
  margin: 0 0 0 10px;
  padding: 12px 24px;
  border-radius: 4px;
  border: solid 1px #38b2c2;
  background-color: #fff;
}
.quantity-reservable-text {
  width: 190px;
  height: 12px;
  font-size: 12px;
  text-align: right;
  color: #324056;
}
.continue-txt {
  height: 16px;
  margin: 8px 0 0 90px;
  font-size: 16px;
  font-weight: 500;
  text-align: right;
  color: #324056;
}
.reservation-summary-msg {
  position: absolute;
  right: 250px;
}
.reserve {
  width: 46px;
  height: 12px;
  font-size: 12px;
  color: #324056;
}
.button {
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  width: fit-content;
  height: 30px;
  padding-left: 15px;
  padding-right: 15px;
  margin: 0 20px 0 0;
  border-radius: 4px;
  border: solid 1px #38b2c2;
  background-color: #fff;
}
.reserve-btn {
  width: 75px;
  padding: 8px;
  text-transform: capitalize;
  border-radius: 3px;
  background: #39b2c2;
  border: none;
  color: white;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 10px;
  font-weight: bold;
  letter-spacing: 1px;
  text-align: center;
  border-radius: 3px;
  text-transform: uppercase;
  cursor: pointer;
}

.reserve-btn:hover {
  background: #33a6b5;
  cursor: pointer;
  color: #fff;
}
.reserve-msg {
  font-weight: 500;
  background: #fff;
  color: green;
}
.mL4 {
  margin-left: 4px;
}
</style>
