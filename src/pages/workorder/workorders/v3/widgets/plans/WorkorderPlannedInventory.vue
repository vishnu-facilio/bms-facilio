<template>
  <div>
    <LineItemList
      v-bind="$attrs"
      ref="lineItemTable"
      :config="listConfiguration"
      :moduleName="moduleName"
      :widgetDetails="widgetDetails"
      :additionalParams="additionalParams"
      :moduleDisplayName="moduleDisplayName"
      viewname="all"
      @onDelete="reloadCost()"
      @onCreateOrUpdate="reloadCost()"
      @handleSelection="selectedRecords"
      class="height-100"
    >
      <template #bulk-actions>
        <div class="d-flex">
          <span
            :class="{
              'cursor-not-allowed disabled': disableActionableUIElements,
            }"
          >
            <div
              class="btn-container margin-auto"
              @click="reserveMultipleLineItems()"
              v-if="checkWoManageInventoryPermission()"
              :class="{
                'pointer-events-none': disableActionableUIElements,
              }"
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
          </span>
          <span
            :class="{
              'cursor-not-allowed disabled': disableActionableUIElements,
            }"
          >
            <div
              class="btn-container margin-auto"
              @click="deleteLineItems()"
              :class="{
                'pointer-events-none': disableActionableUIElements,
              }"
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
          </span>
        </div>
      </template>
      <template #[tableSlotList[0].name]="{record}">
        <span
          v-if="showReserveButton(record)"
          :class="{
            'cursor-not-allowed disabled': disableActionableUIElements,
          }"
        >
          <el-button
            class="reserve-btn visibility-hide-actions"
            @click="reserveLineItem(record)"
            :class="{
              'pointer-events-none': disableActionableUIElements,
            }"
          >
            {{ $t('common._common.reserve') }}
          </el-button>
        </span>
        <div v-else-if="isReserved(record)" class="flex-middle reserve-msg">
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
          :label="$t('common.header.item')"
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
          @click="reservePlannedItems"
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
import { isEmpty } from '@facilio/utils/validation'
import WorkOrderPlans from './WorkOrderPlans.vue'
import InventoryMixin from 'pages/Inventory/InventoryMixin'

export default {
  name: 'Plans',
  extends: WorkOrderPlans,
  mixins: [InventoryMixin],
  props: ['workOrderId', 'widget', 'disableActionableUIElements'],
  data() {
    return {
      recordIds: [],
      recordsCount: null,
      reservingLineItems: [],
      showReservationSummary: false,
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
    listConfiguration() {
      let {
        formConfig,
        tableSlotList,
        filters,
        disableActionableUIElements,
      } = this
      return {
        filters,
        skipModulePermission: {
          editPermission: true,
          deletePermission: true,
        },
        ...(formConfig || {}),
        tableSlotList: tableSlotList || [],
        isRecordEditable: record => {
          return !this.$getProperty(record, 'isReserved')
        },
        isRecordDeletable: record => {
          return !this.$getProperty(record, 'isReserved')
        },
        checkSelection: record => {
          return !this.$getProperty(record, 'isReserved')
        },
        disableActionableUIElements: disableActionableUIElements,
      }
    },

    widgetDetails() {
      let { widget, moduleDisplayName, emptyStateBtnList } = this
      let { widgetParams } = widget || {}
      let { summaryWidgetName } = widgetParams || {}
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
        this.checkWoManageInventoryPermission()
      )
    },
    isReserved(record) {
      return this.$getProperty(record, 'isReserved')
    },
    closeReservationSummary() {
      this.showReservationSummary = false
    },

    errorMsg(lineItem) {
      return this.$getProperty(lineItem, 'row.errorType') === 'Non-reservable'
    },
    warningMsg(lineItem) {
      return this.$getProperty(lineItem, 'row.errorType') === 'Reservable'
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
