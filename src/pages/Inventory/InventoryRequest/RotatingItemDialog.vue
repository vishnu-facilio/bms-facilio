<template>
  <el-dialog
    :visible="true"
    :before-close="closeDialog"
    :fullscreen="false"
    :title="$t('common.header.issue_inventory_request')"
    open="top"
    custom-class="fc-dialog-center-container"
    :append-to-body="true"
  >
    <div class="height400">
      <el-steps :active="stepStatus" finish-status="success">
        <el-step
          v-for="lineItem in lineItemList"
          :title="lineItem.title"
          :description="lineItem.description"
          :key="lineItem.id"
        ></el-step>
      </el-steps>

      <el-table
        ref="issueTable"
        height="200"
        :data="rotatingItemList"
        v-loading="iTloading"
        empty-text="No Rotating assets available."
        @selection-change="selectRotatingAsset"
        :default-sort="{ prop: 'costDate', order: 'descending' }"
        class="width100 inventory-inner-table"
      >
        <el-table-column type="selection" width="60"></el-table-column>
        <el-table-column
          prop="serialNumber"
          sortable
          label="SERIAL NUMBER"
        ></el-table-column>
        <el-table-column sortable label="PRICE">
          <template v-slot="scope">
            <currency
              :value="scope.row.unitPrice > 0 ? scope.row.unitPrice : 0"
            ></currency>
          </template>
        </el-table-column>
        <el-table-column
          sortable
          prop="purchasedDate"
          :formatter="getDateTimePurchasedDate"
          label="PURCHASED TIME"
        ></el-table-column>
      </el-table>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialog()">
          {{ $t('common._common.cancel') }}
        </el-button>
        <el-button
          class="modal-btn-save"
          type="primary"
          :loading="saving"
          @click="nextStep()"
        >
          {{ issueBtnText }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'

const inventoryTypes = {
  ITEM_TYPE: 1,
  TOOL_TYPE: 2,
}

export default {
  props: ['rotatingLineItemsList', 'inventoryrequest', 'selectedStoreId'],

  data() {
    return {
      iTloading: false,
      rotatingItemList: [],
      stepStatus: 0,
      selectedItem: [],
      saving: false,
    }
  },

  created() {
    this.loadRotatingAsset()
  },

  computed: {
    lineItemList() {
      let { rotatingLineItemsList, selectedItem, stepStatus } = this

      return rotatingLineItemsList.map((lineItem, idx) => {
        let {
          inventoryType,
          itemType,
          toolType,
          assetIds,
          quantity,
          id,
        } = lineItem
        let { name: itemName } = itemType || {}
        let { name: toolName } = toolType || {}
        let title =
          inventoryType === inventoryTypes.ITEM_TYPE ? itemName : toolName
        let description = `${
          idx === stepStatus
            ? selectedItem.length
            : assetIds
            ? assetIds.length
            : '0'
        }/${quantity}`

        return { id, title, description }
      })
    },
    issueBtnText() {
      let { stepStatus, rotatingLineItemsList } = this
      let listItemLength = rotatingLineItemsList.length - 1

      return stepStatus >= listItemLength ? 'Issue' : 'Next'
    },
  },

  methods: {
    async loadRotatingAsset() {
      this.iTloading = true

      let {
        rotatingLineItemsList,
        stepStatus,
        selectedStoreId,
        inventoryrequest: { storeRoom } = {},
      } = this
      let { inventoryType, itemType, toolType } =
        rotatingLineItemsList[stepStatus] || {}
      let queryObj = {
        inventoryType,
        storeRoomId: selectedStoreId || (storeRoom || {}).id,
      }

      if (inventoryType === inventoryTypes.ITEM_TYPE) {
        queryObj.itemTypeId = (itemType || {}).id
      } else {
        queryObj.toolTypeId = (toolType || {}).id
      }

      let { error, data } = await API.post('/v2/assets/typeAndStore', queryObj)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.rotatingItemList = data.assets || []
      }

      this.iTloading = false
    },
    getDateTimePurchasedDate(val) {
      let value = val.purchasedDate
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value, true)
    },
    selectRotatingAsset(val) {
      let selectedCount = val.length
      let { rotatingLineItemsList, stepStatus } = this
      let { quantity } = rotatingLineItemsList[stepStatus]

      if (selectedCount <= quantity) {
        this.selectedItem = val
      } else {
        this.$refs.issueTable.clearSelection()
        this.$message.error(
          `Approved Count ${quantity} Selected Count ${selectedCount}`
        )
        this.selectedItem = []
      }
    },
    nextStep() {
      let { stepStatus, selectedItem } = this
      let assetIds = selectedItem.map(item => item.id)

      this.$emit('nextStep', assetIds, stepStatus)
      this.selectedItem = []
      this.stepStatus += 1

      if (this.stepStatus === this.rotatingLineItemsList.length) {
        this.saving = true
        this.$emit('requestIssue')
      } else {
        this.loadRotatingAsset()
      }
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
