<template>
  <div>
    <div class="item-summary-table">
      <el-table
        v-loading="loading"
        @cell-click="openStoreRoomOverview"
        class="width100 inventory-inner-table"
        :data="tools"
        empty-text="This tool is not available in any storeroom."
        :default-sort="{ prop: 'storeRoom.name', order: 'descending' }"
        height="250px"
      >
        <el-table-column
          prop="storeRoom.name"
          sortable
          :label="$t('common.products.storeroom_name')"
          width="300"
        >
          <template v-slot="storeRoomAvatar">
            <store-room-avatar
              name="true"
              size="lg"
              :storeRoom="storeRoomAvatar.row.storeRoom"
            ></store-room-avatar>
          </template>
        </el-table-column>
        <el-table-column label="DESCRIPTION" width="200">
          <template v-slot="desc">
            <div class="textoverflow-ellipsis width240px">
              {{ getDescription(desc) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="currentQuantity"
          sortable
          label="CURRENT BALANCE"
          :formatter="tableQuantityFormatter"
          width="250"
          class-name="right-align-items"
        ></el-table-column>
        <el-table-column
          v-if="!toolType.isRotating"
          prop="rate"
          sortable
          label="RATE/HR"
          width="200"
          class-name="right-align-items"
        >
          <template v-slot="scope">
            <CurrencyPopOver
              v-if="checkForMultiCurrency('rate', metaFieldTypeMap)"
              class="d-flex flex-row-reverse"
              :field="{
                name: 'rate',
                displayValue: scope.row.rate > 0 ? scope.row.rate : 0,
              }"
              :details="scope.row"
            />
            <currency
              v-else
              :value="scope.row.rate > 0 ? scope.row.rate : 0"
            ></currency>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog
      :visible.sync="purchasedToolDialog"
      :fullscreen="false"
      title="Purchased Tools"
      open="top"
      custom-class="assetaddvaluedialog fc-dialog-center-container inventory-store-dialog fc-web-form-dialog"
      :append-to-body="true"
    >
      <div>
        <div v-if="toolType.isRotating">
          <el-table
            v-loading="purLoading"
            :data="purchasedToolList"
            empty-text="No purchased tools available."
            :default-sort="{ prop: 'costDate', order: 'descending' }"
            class="width100 inventory-inner-table"
          >
            <el-table-column
              prop="serialNumber"
              sortable
              label="SERIAL NUMBER"
              width="170"
            ></el-table-column>
            <el-table-column prop="rate" sortable label="RATE/HR" width="120">
              <template v-slot="scope">
                <CurrencyPopOver
                  v-if="checkForMultiCurrency('rate', metaFieldTypeMap)"
                  class="d-flex flex-row-reverse"
                  :field="{
                    name: 'rate',
                    displayValue: scope.row.rate > 0 ? scope.row.rate : 0,
                  }"
                  :details="scope.row"
                />
                <currency
                  v-else
                  :value="scope.row.rate > 0 ? scope.row.rate : 0"
                ></currency>
              </template>
            </el-table-column>
            <el-table-column
              prop="isUsed"
              :formatter="isUsedHandle"
              label="IS USED"
            ></el-table-column>
            <el-table-column
              prop="costDate"
              sortable
              :formatter="getDateTime"
              label="PURCHASED DATE"
              width="180"
            ></el-table-column>
            <el-table-column width="60">
              <template v-slot="purToolIndTrack">
                <div
                  v-if="!purToolIndTrack.row.isUsed"
                  class="actions visibility-hide-actions"
                  style="margin-top:-3px;margin-right: 15px;text-align:center;"
                >
                  <i
                    class="el-icon-edit pointer edit-icon-color"
                    title="Edit Purchased Tool"
                    data-arrow="true"
                    v-tippy
                    @click="editPurchasedTool(purToolIndTrack.row)"
                  ></i>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
    <div v-if="editPurchasedToolDialog" :key="editPurchaseToolObj.id">
      <el-dialog
        :visible.sync="editPurchasedToolDialog"
        :before-close="closeEditPurchaseToolDialog"
        title="Edit Purchased Tools"
        :fullscreen="false"
        width="25%"
        custom-class="assetaddvaluedialog fc-dialog-center-container inventory-store-dialog fc-web-form-dialog purchase-item-edit-dialog"
        :append-to-body="true"
      >
        <div>
          <el-form>
            <p class="fc-input-label-txt">
              Serial Number
            </p>
            <el-form-item>
              <el-input
                placeholder="Serial Number"
                v-model="editPurchaseToolObj.serialNumber"
                class="fc-input-full-border-select2 width100"
              ></el-input>
            </el-form-item>
            <p class="fc-input-label-txt">
              Rate/Hour
            </p>
            <el-form-item>
              <el-input
                placeholder="Price"
                type="number"
                v-model="editPurchaseToolObj.rate"
                class="fc-input-full-border-select2 width100"
              ></el-input>
            </el-form-item>
          </el-form>
        </div>
        <div class="modal-dialog-footer">
          <el-button
            class="modal-btn-save width100"
            type="primary"
            @click="savePurchasedTool()"
            :loading="saving"
            >{{ saving ? 'Submitting...' : 'SAVE' }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import StoreRoomAvatar from '@/avatar/Storeroom'
import { getFilteredToolList } from 'pages/Inventory/InventoryUtil'
import { isEmpty } from '@facilio/utils/validation'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import CurrencyPopOver from 'src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue'
import {
  getMetaFieldMapForModules,
  checkForMultiCurrency,
} from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  components: {
    StoreRoomAvatar,
    CurrencyPopOver,
  },
  props: {
    toolTypeId: {
      type: Number,
    },
    refreshList: {
      type: Boolean,
    },
    toolType: {},
  },
  data() {
    return {
      loading: true,
      fetchingMore: false,
      purchasedToolDialog: false,
      purchasedToolList: null,
      purLoading: true,
      editPurchasedToolDialog: false,
      editPurchaseToolObj: null,
      saving: false,
      tools: [],
      metaFieldTypeMap: {},
      checkForMultiCurrency,
    }
  },
  async mounted() {
    this.loadToolStoreRoom()
    this.metaFieldTypeMap = await getMetaFieldMapForModules('tool')
  },
  watch: {
    refreshList() {
      this.loadToolStoreRoom()
      this.$emit('update:refreshList', false)
    },
  },
  methods: {
    getDescription(desc) {
      return this.$getProperty(desc, 'row.storeRoom.description', '---')
    },
    async loadToolStoreRoom() {
      let filters = {
        toolType: {
          operatorId: 36,
          value: [this.toolTypeId + ''],
        },
      }

      if (!this.toolTypeId) return
      this.loading = true
      this.tools = await getFilteredToolList(filters)
      this.loading = false
      this.fetchingMore = false
    },
    openPurchasedToolsDialog(tool) {
      this.purchasedToolDialog = true
      this.loadPurchasedToolList(tool)
    },
    cancelPurchaseToolsDialog() {
      this.purchasedToolDialog = false
    },
    loadPurchasedToolList(tool) {
      let self = this
      self.purLoading = true
      self.$http
        .get('v2/purchasedToolsList/tool/' + tool.id)
        .then(function(response) {
          self.purLoading = false
          if (response.data.responseCode === 0) {
            self.purchasedToolList = response.data.result.purchasedTool
            self.purLoading = false
          }
        })
        .catch(() => {})
    },
    getDateTime(val) {
      let value = val.costDate
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value, true)
    },
    openStoreRoomOverview(row, col) {
      if (col.label !== 'STOREROOM NAME') {
        return
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('tool', pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: 'all',
              id: row.id,
            },
          })
      } else {
        this.$router.push({
          path: '/app/inventory/tool/' + 'all/' + row.id + '/summary',
        })
      }
    },
    isUsedHandle(val) {
      if (val.isUsed) {
        return 'Yes'
      } else {
        return 'No'
      }
    },
    editPurchasedTool(purTool) {
      this.editPurchasedToolDialog = true
      this.editPurchaseToolObj = {
        id: purTool.id,
        serialNumber: purTool.serialNumber,
        rate: purTool.rate,
        tool: { id: purTool.tool.id },
      }
    },
    closeEditPurchaseToolDialog() {
      this.editPurchasedToolDialog = false
    },
    savePurchasedTool() {
      let self = this
      let param = {
        toolId: this.editPurchaseToolObj.tool.id,
        purchasedTools: [
          {
            id: this.editPurchaseToolObj.id,
            serialNumber: this.editPurchaseToolObj.serialNumber,
            rate: this.editPurchaseToolObj.rate,
          },
        ],
      }
      self.$http
        .post('v2/purchasedTool/update', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            self.$message.success('Purchased Tool Edit Successfully')
            self.editPurchasedToolDialog = false
            self.loadPurchasedToolList(self.editPurchaseToolObj.tool)
          } else {
            self.$message.error(response.data.message)
          }
        })
        .catch(() => {
          self.$message.error('Unable to Edit')
        })
    },
    tableQuantityFormatter(val, prop) {
      if (isEmpty(val[prop.property])) {
        return 0
      } else {
        return val[prop.property]
      }
    },
  },
}
</script>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
</style>
