<template>
  <el-dialog
    :visible="true"
    :fullscreen="false"
    open="top"
    width="75%"
    custom-class="assetaddvaluedialog fc-dialog-center-container fc-dialog-return inventory-store-dialog fc-web-form-dialog"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <template slot="title">
      <div class="label-txt-black fw-bold text-uppercase fL">
        {{ title }}
      </div>
    </template>
    <div class="clearboth">
      <div
        v-if="selectedReceiptObj.length > 0"
        @click="markAsFullyReceivedOrReturned(selectedReceiptObj)"
        class="rv-sum-table-click"
      >
        <inline-svg src="mark-as-fully-recieved" class="mR10"></inline-svg>
        {{ onMarking }}
      </div>
      <el-table
        height="300"
        ref="receiveTable"
        @selection-change="selectAction"
        :data="recordList"
        :default-sort="{ prop: 'sysCreatedTime', order: 'descending' }"
        class="width100 tr-summary-table"
      >
        <el-table-column width="60" type="selection"></el-table-column>
        <el-table-column :label="$t('common.products.line_item')" width="200">
          <template v-slot="recData">
            <tool-avatar
              name="true"
              size="lg"
              module="item"
              :recordData="recData.row.itemType"
              v-if="recData.row.inventoryType === inventoryTypes.ITEM_TYPE"
            ></tool-avatar>
            <item-avatar
              v-else
              name="true"
              size="lg"
              :module="getModuleAndDataKey(recData.row).module"
              :recordData="getModuleAndDataKey(recData.row).data"
            ></item-avatar>
          </template>
        </el-table-column>
        <el-table-column
          prop="quantity"
          :label="$t('common._common.ordered_quantity')"
          width="200"
        ></el-table-column>
        <el-table-column
          v-if="isAddReceipt"
          :formatter="quantityDue"
          :label="$t('common.dashboard.quantity_due')"
          width="200"
        ></el-table-column>
        <el-table-column
          v-else
          prop="quantityReceived"
          :label="$t('common.header.recieved_quantity')"
          width="200"
        ></el-table-column>
        <el-table-column
          v-if="isAddReceipt"
          width="200"
          :label="$t('common.dashboard.recieving_quantity')"
        >
          <template v-slot="penPo">
            <div>
              <el-input
                :placeholder="$t('common._common.quantity')"
                :min="1"
                ref="quantityElInput"
                :max="quantityDue(penPo.row)"
                type="number"
                v-model="penPo.row.receivedQuantity"
                class="fc-input-full-border-select2"
              ></el-input>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          v-else
          :label="$t('common._common.return_quantity')"
          width="160"
        >
          <template v-slot="recPo">
            <div>
              <el-input
                :placeholder="$t('common._common.quantity')"
                :min="1"
                ref="quantityElInput"
                :max="recPo.row.quantityReceived"
                type="number"
                v-model="recPo.row.returnQuantity"
                class="fc-input-full-border-select2"
              ></el-input>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="200" :label="$t('common.wo_report.remarks')">
          <template v-slot="penPo">
            <div>
              <el-input
                :placeholder="$t('common.wo_report.remarks')"
                v-model="penPo.row.remarksData"
                class="fc-input-full-border-select2"
              ></el-input>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialog">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="saveDialog()"
          >{{ $t('common._common._save') }}</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>
<script>
import ToolAvatar from '@/avatar/ItemTool'
import ItemAvatar from '@/avatar/ItemTool'
import { isEmpty } from '@facilio/utils/validation'

const inventoryTypes = {
  ITEM_TYPE: 1,
  TOOL_TYPE: 2,
  SERVICE: 3,
  DESCRIPTION: 4,
}
export default {
  components: {
    ToolAvatar,
    ItemAvatar,
  },
  props: [
    'recordList',
    'isAddReceipt',
    'isReturnReceipts',
    'title',
    'onMarking',
  ],

  data() {
    return {
      inventoryTypes,
      selectedReceiptObj: [],
    }
  },

  methods: {
    getModuleAndDataKey(recordData) {
      let { inventoryType } = recordData || {}

      let { TOOL_TYPE, SERVICE, DESCRIPTION } = inventoryTypes || {}

      let moduleAndDataKeyVsInventoryType = {
        [TOOL_TYPE]: { module: 'tool', data: recordData.toolType },
        [DESCRIPTION]: { module: 'others', data: recordData.description },
        [SERVICE]: { module: 'service', data: recordData.service },
      }

      return moduleAndDataKeyVsInventoryType[inventoryType] || {}
    },
    markAsFullyReceivedOrReturned(val) {
      val.forEach(element => {
        if (this.isAddReceipt)
          element.receivedQuantity = this.quantityDue(element)
        else element.returnQuantity = element.quantityReceived
      })
      this.$refs.receiveTable.clearSelection()
    },
    closeDialog() {
      this.$emit('onClose')
    },
    saveDialog() {
      this.$emit('onSave')
      this.closeDialog()
    },
    selectAction(val) {
      this.selectedReceiptObj = val
    },
    quantityDue(val) {
      if (isEmpty(val.quantityReceived)) {
        return val.quantity
      } else {
        return val.quantity - val.quantityReceived
      }
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
.el-table--enable-row-transition .el-table__body td {
  padding-right: 20px;
}
</style>
