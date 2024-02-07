<template>
  <div>
    <div v-if="visibility">
      <el-dialog
        :visible="visibility"
        :before-close="closeDialog"
        :fullscreen="false"
        title="Rotating Asset"
        open="top"
        custom-class="fc-dialog-center-container"
        :append-to-body="true"
      >
        <div class="height400">
          <el-table
            ref="rotatingAssetTable"
            height="330"
            :data="rotatingAssetList"
            v-loading="assetLoading"
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
            <el-button class="modal-btn-cancel" @click="closeDialog()"
              >CANCEL</el-button
            >
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="saveAsset()"
              >SAVE</el-button
            >
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      rotatingAssetList: [],
      selectedAssetObj: [],
      selectedAssetId: [],
      assetLoading: false,
      entryCheck: true,
    }
  },
  props: ['visibility', 'item', 'tool', 'assetIds', 'isGatePass'],
  mounted() {
    let filters = {}

    if (this.item) {
      filters = {
        rotatingItem: {
          operatorId: 36,
          value: [this.item + ''],
        },
        isUsed: { operatorId: 15, value: ['false'] },
      }
      if (this.isGatePass) {
        delete filters.isUsed
      }
    } else if (this.tool) {
      filters = {
        rotatingTool: {
          operatorId: 36,
          value: [this.tool + ''],
        },
        isUsed: { operatorId: 15, value: ['false'] },
      }
    }
    this.loadRotatingAssetList(filters)
  },
  methods: {
    async loadRotatingAssetList(filters) {
      this.assetLoading = true
      this.rotatingAssetList = await this.$util.getFilteredAssetList(filters)
      this.assetLoading = false

      if (this.assetIds) {
        this.mapAsset()
      }
    },
    selectRotatingAsset(val) {
      // Temp Fix Needs to be Handled in proper way
      if (val.length === 0 && this.entryCheck) {
        this.mapAsset()
        this.entryCheck = false
      } else {
        this.selectedAssetObj = val
        this.selectedAssetId = val.map(i => i.id)
      }
    },
    toggleRotatingAsset(rows) {
      if (rows) {
        rows.forEach(row => {
          this.$refs.rotatingAssetTable.toggleRowSelection(row)
        })
      } else {
        this.$refs.rotatingAssetTable.clearSelection()
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    saveAsset() {
      this.$emit('save', this.selectedAssetId)
      this.$emit('update:visibility', false)
    },
    getDateTimePurchasedDate(val) {
      let value = val.purchasedDate
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value, true)
    },
    mapAsset() {
      let self = this
      let assetToggleList = []
      self.assetIds.forEach(i => {
        let tempList = self.rotatingAssetList.find(j => {
          return j.id === i
        })
        assetToggleList.push(tempList)
      })
      self.selectedAssetObj = assetToggleList
      self.selectedAssetId = assetToggleList.map(i => i.id)
      self.toggleRotatingAsset(assetToggleList)
    },
  },
}
</script>
