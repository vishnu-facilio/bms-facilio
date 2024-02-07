<template>
  <div v-if="visibility">
    <div v-if="rotatingItemChooserDialog">
      <el-dialog
        :visible="rotatingItemChooserDialog"
        :before-close="closeDialog"
        :fullscreen="false"
        :title="$t('common.products.issue_inventory_request')"
        open="top"
        custom-class="fc-dialog-center-container"
        :append-to-body="true"
      >
        <div class="height400">
          <el-steps :active="rotatingItemStepStatus" finish-status="success">
            <el-step
              v-for="(item, i) in rotatingLineItemsList"
              :title="
                item.inventoryType === 1
                  ? item.itemType.name
                  : item.toolType.name
              "
              :description="
                (i === rotatingItemStepStatus
                  ? selectedRotatingItem.length
                  : item.assetIds
                  ? item.assetIds.length
                  : '0') +
                  '/' +
                  item.quantity
              "
              :key="item.id"
            ></el-step>
          </el-steps>
          <el-table
            ref="issueTable"
            height="200"
            :data="rotatingItemList"
            v-loading="iTloading"
            :empty-text="$t('common.products.no_rotating_assets_available')"
            @selection-change="selectRotatingAsset"
            :default-sort="{ prop: 'costDate', order: 'descending' }"
            class="width100 inventory-inner-table"
          >
            <el-table-column type="selection" width="60"></el-table-column>
            <el-table-column
              prop="serialNumber"
              sortable
              :label="$t('common._common._serial_number')"
            ></el-table-column>
            <el-table-column sortable :label="$t('common.header.price')">
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
              :label="$t('common.products.purchased_items')"
            >
            </el-table-column>
          </el-table>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="closeDialog()">{{
              $t('common._common.cancel')
            }}</el-button>
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="nextStep()"
              >{{
                rotatingItemStepStatus === rotatingLineItemsList.length - 1
                  ? 'Issue'
                  : 'Next'
              }}</el-button
            >
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
export default {
  props: ['inventoryRequest', 'storeRoom', 'visibility'],
  data() {
    return {
      rotatingItemChooserDialog: false,
      rotatingLineItemsList: [],
      rotatingItemStepStatus: 0,
      iTloading: false,
      rotatingItemList: [],
      selectedRotatingItem: [],
    }
  },
  mounted() {
    if (this.storeRoom) {
      this.issueInventoryRequestActions(this.storeRoom.id)
    }
  },
  computed: {},
  methods: {
    async issueInventoryRequest() {
      let param = {
        inventoryRequest: {
          id: this.inventoryRequest.id,
          lineItems: this.inventoryRequest.lineItems,
          storeRoom: this.storeRoom,
          requestedBy: this.inventoryRequest.requestedBy,
          requestedFor: this.inventoryRequest.requestedFor,
          status: 6,
        },
      }
      if (this.inventoryRequest.parentId > 0) {
        param.inventoryRequest['parentId'] = this.inventoryRequest.parentId
      }
      let self = this

      let url = '/v2/inventoryrequest/issueInventoryRequests'

      let { error } = await API.post(url, param)

      if (error) {
        this.$message.error(error.message)
      } else {
        self.$message.success(
          this.$t('common.products.inventory_issued_succesfully')
        )
        self.$emit('refreshInventory')
        self.closeDialog()
      }
    },
    issueInventoryRequestActions(storeId) {
      if (storeId) {
        for (let i in this.inventoryRequest.lineItems) {
          if (
            this.inventoryRequest.lineItems[i].inventoryType === 1 &&
            this.inventoryRequest.lineItems[i].itemType.isRotating
          ) {
            this.rotatingLineItemsList.push(this.inventoryRequest.lineItems[i])
          } else if (
            this.inventoryRequest.lineItems[i].inventoryType === 2 &&
            this.inventoryRequest.lineItems[i].toolType.isRotating
          ) {
            this.rotatingLineItemsList.push(this.inventoryRequest.lineItems[i])
          }
          if (parseInt(i) === this.inventoryRequest.lineItems.length - 1) {
            if (this.rotatingLineItemsList.length > 0) {
              this.rotatingItemChooserDialog = true
              this.loadRotatingAsset()
              return
            }
            this.issueInventoryRequest()
          }
        }
      }
    },
    nextStep() {
      let count = this.rotatingLineItemsList[this.rotatingItemStepStatus]
        .quantity
      if (this.selectedRotatingItem.length !== count) {
        this.$message.error(
          'Approved Count ' +
            count +
            ' Selected Count ' +
            this.selectedRotatingItem.length
        )
        return
      }
      let assetIds = this.selectedRotatingItem.map(d => d.id)
      this.$refs.issueTable.clearSelection()
      this.rotatingLineItemsList[
        this.rotatingItemStepStatus
      ].assetIds = assetIds
      this.inventoryRequest.lineItems[
        this.rotatingItemStepStatus
      ].assetIds = assetIds
      this.selectedRotatingItem = []
      if (
        this.rotatingItemStepStatus++ ===
        this.rotatingLineItemsList.length - 1
      ) {
        this.issueInventoryRequest()
        this.rotatingItemStepStatus = 0
        return
      }
      this.loadRotatingAsset()
    },
    selectRotatingAsset(val) {
      this.selectedRotatingItem = val
    },
    getDateTimePurchasedDate(val) {
      let value = val.purchasedDate
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value, true)
    },
    async loadRotatingAsset() {
      let self = this
      self.iTloading = true
      let queryObj
      if (
        this.rotatingLineItemsList[this.rotatingItemStepStatus]
          .inventoryType === 1
      ) {
        queryObj = {
          inventoryType: this.rotatingLineItemsList[this.rotatingItemStepStatus]
            .inventoryType,
          itemTypeId: this.rotatingLineItemsList[this.rotatingItemStepStatus]
            .itemType.id,
          storeRoomId: this.storeRoom.id,
        }
      } else {
        queryObj = {
          inventoryType: this.rotatingLineItemsList[this.rotatingItemStepStatus]
            .inventoryType,
          toolTypeId: this.rotatingLineItemsList[this.rotatingItemStepStatus]
            .toolType.id,
          storeRoomId: this.storeRoom.id,
        }
      }
      let url = '/v2/assets/typeAndStore'
      let { data, error } = await API.post(url, queryObj)

      if (error) {
        this.$message.error(error.message)
        self.closeDialog()
      } else {
        let individualItemList = []
        individualItemList = data.assets
        self.rotatingItemList = individualItemList
      }
      self.iTloading = false
    },
    closeDialog() {
      this.rotatingItemChooserDialog = false
      this.rotatingItemStepStatus = 0
      this.rotatingLineItemsList = []
      this.rotatingItemList = []
      this.selectedRotatingItem = []
      this.cancelForm()
    },
    cancelForm() {
      this.$emit('update:visibility', false)
    },
    storeAssetData(lineItem, ids) {
      lineItem.assetIds = ids
      lineItem.quantity = ids.length
    },
  },
}
</script>
