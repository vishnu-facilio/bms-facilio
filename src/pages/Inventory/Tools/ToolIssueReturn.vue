<template>
  <div>
    <div v-if="type === 'issue'">
      <div v-if="toolType.isRotating">
        <el-dialog
          :visible.sync="showToolIssueReturn"
          :title="$t('common.products.issue_tool')"
          :before-close="cancelForm"
          :key="toolType.id"
          custom-class="fc-dialog-center-container"
        >
          <div v-if="loading" class="mT50">
            <spinner :show="loading" size="80"></spinner>
          </div>
          <div v-else>
            <div class="height400">
              <div v-if="toolType.isRotating && tool">
                <el-table
                  v-loading="purLoading"
                  height="200"
                  :data="purchasedUnUsedToolList"
                  :empty-text="
                    $t('common.products.no_rotating_assets_available')
                  "
                  class="width100 inventory-inner-table"
                  @selection-change="toggleUnUsedToolSelection"
                  :default-sort="{ prop: 'costDate', order: 'descending' }"
                >
                  <el-table-column
                    type="selection"
                    width="60"
                  ></el-table-column>
                  <el-table-column
                    prop="serialNumber"
                    sortable
                    :label="$t('common._common._serial_number')"
                  ></el-table-column>
                  <el-table-column
                    sortable
                    :label="$t('common.header.rate_hr')"
                  >
                    <template>
                      <currency :value="tool.rate"></currency>
                    </template>
                  </el-table-column>
                  <el-table-column
                    sortable
                    prop="purchasedDate"
                    :formatter="getDateTime"
                    :label="$t('common.products.purchased_time')"
                  ></el-table-column>
                </el-table>
                <el-row class="mT20">
                  <el-col :span="12">
                    <p class="fc-input-label-txt">
                      {{ $t('common.products.issue_to') }}
                    </p>
                    <el-select
                      v-model="userid"
                      filterable
                      clearable
                      :placeholder="$t('common.products.select_user')"
                      class="fc-input-full-border-select2 width100 mT5"
                    >
                      <el-option
                        v-for="user in users"
                        :key="user.id"
                        :label="user.name"
                        :value="parseInt(user.id)"
                      ></el-option>
                    </el-select>
                  </el-col>
                </el-row>
              </div>
            </div>
            <div class="modal-dialog-footer">
              <el-button class="modal-btn-cancel" @click="cancelForm()">{{
                $t('common._common.cancel')
              }}</el-button>
              <el-button
                class="modal-btn-save"
                type="primary"
                @click="saveIssueForm()"
                :loading="saving"
                >{{
                  saving
                    ? $t('common._common.submitting')
                    : $t('common._common._save')
                }}</el-button
              >
            </div>
          </div>
        </el-dialog>
      </div>
      <div v-if="!toolType.isRotating && tool">
        <el-dialog
          :visible.sync="showToolIssueReturn"
          :title="$t('common.products.issue_tool')"
          width="40%"
          :before-close="cancelForm"
          :key="toolType.id"
          custom-class="fc-dialog-center-container"
        >
          <div v-if="loading">
            <spinner :show="loading"></spinner>
          </div>
          <div
            class="height300"
            v-else-if="!tool.toolType.isRotating && tool.id"
          >
            <div>
              <el-row :gutter="20">
                <el-col :span="12">
                  <div class="fc-id">#{{ tool.toolType.id }}</div>
                  <div class="fc-black3-16 text-left">
                    {{ tool.toolType.name }}
                  </div>
                </el-col>
                <el-col :span="12">
                  <store-room-avatar
                    name="true"
                    size="lg"
                    :storeRoom="tool.storeRoom"
                  ></store-room-avatar>
                </el-col>
              </el-row>
            </div>
            <el-row :gutter="10" class="mT40">
              <el-col :span="12">
                <div class="fc-black3-16 text-left fw4">
                  {{ $t('common.products.issue_quantity') }}
                </div>
                <el-input
                  :placeholder="$t('common.products.issue_quantity')"
                  :min="1"
                  :max="tool.currentQuantity"
                  type="number"
                  v-model="quantity"
                  class="fc-input-full-border2 mT5"
                >
                  <span slot="suffix" class="width100 item-input-txt">{{
                    $t('common.header.tool')
                  }}</span>
                </el-input>
                <div class="green-txt2-13 mT10 fw4">
                  {{ tool.currentQuantity }}
                  {{ $t('common.header._current_balance') }}
                </div>
              </el-col>
              <el-col :span="12">
                <div class="fc-black3-16 text-left fw4">
                  {{ $t('common.products.issue_to') }}
                </div>
                <el-select
                  v-model="userid"
                  filterable
                  clearable
                  :placeholder="$t('common.products.select_user')"
                  class="fc-input-full-border-select2 width100 mT5"
                >
                  <el-option
                    v-for="user in users"
                    :key="user.id"
                    :label="user.name"
                    :value="parseInt(user.id)"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
          </div>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="cancelForm()">{{
              $t('common._common.cancel')
            }}</el-button>
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="saveIssueForm()"
              :loading="saving"
              >{{
                saving
                  ? $t('common._common.submitting')
                  : $t('common._common._save')
              }}</el-button
            >
          </div>
        </el-dialog>
      </div>
    </div>

    <div v-if="type === 'adjustment'">
      <div v-if="!toolType.isRotating">
        <el-dialog
          :visible.sync="showToolIssueReturn"
          :title="$t('common._common.adjust_balance')"
          width="40%"
          :before-close="cancelForm"
          :key="toolType.id"
          custom-class="fc-dialog-center-container"
        >
          <div v-if="loading">
            <spinner :show="loading"></spinner>
          </div>
          <div class="height300" v-else-if="!toolType.isRotating && tool.id">
            <div>
              <el-row :gutter="20">
                <el-col :span="12">
                  <div class="fc-id">#{{ tool.toolType.id }}</div>
                  <div class="fc-black3-16 text-left">
                    {{ tool.toolType.name }}
                  </div>
                </el-col>
                <el-col :span="12">
                  <store-room-avatar
                    name="true"
                    size="lg"
                    :storeRoom="tool.storeRoom"
                  ></store-room-avatar>
                </el-col>
              </el-row>
            </div>
            <el-row :gutter="10" class="mT40">
              <el-col :span="12">
                <div class="fc-black3-16 text-left fw4">
                  {{ $t('common.header._new_quantity') }}
                </div>
                <el-input
                  :placeholder="$t('common.header._new_quantity')"
                  type="number"
                  v-model="quantity"
                  class="fc-input-full-border2 mT5"
                >
                  <span slot="suffix" class="width100 item-input-txt">{{
                    $t('common.header.tool')
                  }}</span>
                </el-input>
                <div
                  class="green-txt2-13 mT10 fw4"
                  v-if="tool.currentQuantity >= 0"
                >
                  {{ tool.currentQuantity }}
                  {{ $t('common.header._current_quantity') }}
                </div>
                <div
                  class="green-txt2-13 mT10 fw4"
                  v-if="tool.currentQuantity === -1"
                >
                  {{ '___' }}
                  {{ $t('common.header._current_quantity') }}
                </div>
              </el-col>
              <el-col :span="12">
                <div class="fc-black3-16 text-left fw4">
                  {{ $t('common.products.purchase_price') }}
                </div>
                <el-input
                  :placeholder="$t('common.products.purchase_price')"
                  :disabled="this.quantity <= tool.currentQuantity"
                  v-model="unitPrice"
                  class="fc-input-full-border2 mT5"
                >
                  <span slot="suffix" class="width100 item-input-txt">{{
                    $t('common.products.purchase_price')
                  }}</span>
                </el-input>
              </el-col>
            </el-row>
          </div>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="cancelForm()">{{
              $t('common._common.cancel')
            }}</el-button>
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="saveAdjustmentForm()"
              :loading="saving"
              >{{
                saving
                  ? $t('common._common.submitting')
                  : $t('common._common._save')
              }}</el-button
            >
          </div>
        </el-dialog>
      </div>
    </div>
    <div class="facilio-inventory-web-form-body" v-if="type === 'return'">
      <el-dialog
        :visible.sync="showToolIssueReturn"
        :key="toolType.id"
        :title="$t('common._common.return_tool')"
        :before-close="cancelForm"
        width="65%"
        custom-class="fc-dialog-center-container"
      >
        <div>
          <div class="height400">
            <div v-if="!toolType.isRotating">
              <div
                v-if="selectedIssuedTools.length > 0"
                @click="markAsFullyReturned(issuedTools)"
                class="rv-sum-table-click"
              >
                <img src="~assets/mark-as-fully-recieved.svg" class="mR10" />
                {{ $t('common.wo_report.mark_as_fully_returned') }}
              </div>
              <el-table
                :data="issuedTools"
                ref="returnTable"
                :empty-text="$t('common.products.this_tool_not_issued_anymore')"
                height="300"
                @selection-change="selectIssuedToolActions"
                class="inventory-inner-table width100"
                :default-sort="{
                  prop: 'tool.storeRoom.name',
                  order: 'descending',
                }"
              >
                <el-table-column width="60" type="selection"></el-table-column>
                <el-table-column
                  prop="tool.storeRoom.name"
                  sortable
                  width="190"
                  :label="$t('common.products.storeroom_name')"
                ></el-table-column>
                <el-table-column
                  prop="remainingQuantity"
                  sortable
                  :label="$t('common._common.quantity')"
                ></el-table-column>
                <el-table-column
                  prop="sysModifiedTime"
                  :formatter="getDateTimeIssuedTime"
                  width="160"
                  sortable
                  :label="$t('common.products.issued_time')"
                ></el-table-column>
                <el-table-column :label="$t('common.products.issued_to')">
                  <template v-slot="tool">
                    <div v-if="tool.row.transactionType === 2">
                      WO-<router-link
                        class="fc-id"
                        :to="{
                          path: '/app/wo/orders/summary/' + tool.row.parentId,
                        }"
                      >
                        #{{ tool.row.parentId }}
                      </router-link>
                    </div>
                    <div v-if="tool.row.transactionType === 3">
                      <user-avatar
                        v-if="tool.row.resource.resourceType === 4"
                        size="md"
                        :user="$store.getters.getUser(tool.row.parentId)"
                      ></user-avatar>
                      <span v-else-if="tool.row.resource.resourceType === 2"
                        >Asset-<router-link
                          class="fc-id"
                          v-tippy="{
                            placement: 'top',
                            arrow: true,
                            animation: 'shift-away',
                          }"
                          :title="
                            tool.row.resource ? tool.row.resource.name : ''
                          "
                          :to="{
                            path:
                              '/app/at/assets/all/' +
                              tool.row.parentId +
                              '/overview/',
                          }"
                          >#{{ tool.row.parentId }}</router-link
                        >
                      </span>
                      <span v-else-if="tool.row.resource.resourceType === 1"
                        >Space-
                        <router-link
                          class="fc-id"
                          v-tippy="{
                            placement: 'top',
                            arrow: true,
                            animation: 'shift-away',
                          }"
                          :title="
                            tool.row.resource ? tool.row.resource.name : ''
                          "
                          :to="{ path: getSpaceRouteLink(tool.row.resource) }"
                          >#{{ tool.row.parentId }}</router-link
                        >
                      </span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  width="180"
                  :label="$t('common._common.return_quantity')"
                >
                  <template v-slot="issuedTool">
                    <div>
                      <el-input
                        :placeholder="$t('common._common.quantity')"
                        :min="1"
                        ref="quantityElInput"
                        :max="issuedTool.row.remainingQuantity"
                        type="number"
                        v-model="issuedTool.row.updateQuantity"
                        class="fc-input-full-border-select2"
                      ></el-input>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div v-if="toolType.isRotating">
              <el-table
                :data="issuedTools"
                :empty-text="$t('common.products.this_tool_not_issued_anymore')"
                height="310"
                class="inventory-inner-table width100"
                @selection-change="toggleReturnToolSelection"
                :default-sort="{ prop: 'storeRoom.name', order: 'descending' }"
              >
                <el-table-column type="selection" width="60"></el-table-column>
                <el-table-column
                  prop="tool.storeRoom.name"
                  sortable
                  :label="$t('common.products.storeroom_name')"
                  width="190"
                ></el-table-column>
                <el-table-column
                  prop="asset.name"
                  sortable
                  :label="$t('common.products.name')"
                  width="140"
                ></el-table-column>
                <el-table-column
                  prop="asset.serialNumber"
                  sortable
                  :label="$t('common._common._serial_number')"
                  width="170"
                ></el-table-column>
                <el-table-column
                  :label="$t('common.products.issued_to')"
                  width="150"
                >
                  <template v-slot="tool">
                    <div v-if="tool.row.transactionType === 2">
                      WO-<router-link
                        class="fc-id"
                        :to="{
                          path: '/app/wo/orders/summary/' + tool.row.parentId,
                        }"
                      >
                        #{{ tool.row.parentId }}
                      </router-link>
                    </div>
                    <div v-if="tool.row.transactionType === 3">
                      <user-avatar
                        v-if="tool.row.resource.resourceType === 4"
                        size="md"
                        :user="$store.getters.getUser(tool.row.parentId)"
                      ></user-avatar>
                      <span v-else-if="tool.row.resource.resourceType === 2"
                        >Asset-<router-link
                          class="fc-id"
                          v-tippy="{
                            placement: 'top',
                            arrow: true,
                            animation: 'shift-away',
                          }"
                          :title="
                            tool.row.resource ? tool.row.resource.name : ''
                          "
                          :to="{
                            path:
                              '/app/at/assets/all/' +
                              tool.row.parentId +
                              '/overview/',
                          }"
                          >#{{ tool.row.parentId }}</router-link
                        >
                      </span>
                      <span v-else-if="tool.row.resource.resourceType === 1"
                        >Space-
                        <router-link
                          class="fc-id"
                          v-tippy="{
                            placement: 'top',
                            arrow: true,
                            animation: 'shift-away',
                          }"
                          :title="
                            tool.row.resource ? tool.row.resource.name : ''
                          "
                          :to="{ path: getSpaceRouteLink(tool.row.resource) }"
                          >#{{ tool.row.parentId }}</router-link
                        >
                      </span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="sysModifiedTime"
                  :formatter="getDateTimeIssuedTime"
                  sortable
                  :label="$t('common.products.issued_time')"
                ></el-table-column>
              </el-table>
            </div>
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelForm()">{{
            $t('common._common.close')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="returnTool()"
            :loading="saving"
            >{{
              saving
                ? $t('common._common.submitting')
                : $t('common._common.return')
            }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import UserAvatar from '@/avatar/User'
import StoreRoomAvatar from '@/avatar/Storeroom'
import inventoryMixin from 'pages/Inventory/mixin/inventoryHelper'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
const transactionTypeEnum = {
  STOCK: 1,
  MANUAL: 3,
}
const transactionStateEnum = {
  ISSUE: 2,
  RETURN: 3,
  ADJUSTMENT_INCREASE: 7,
  ADJUSTMENT_DECREASE: 8,
}
const resourceTypeEnum = {
  USER: 4,
}
export default {
  props: ['toolType', 'showToolIssueReturn', 'type', 'tool'],
  components: {
    UserAvatar,
    StoreRoomAvatar,
  },
  mixins: [inventoryMixin],
  data() {
    return {
      saving: false,
      loading: false,
      tools: null,
      fetchingMore: false,
      quantity: null,
      userid: null,
      workorderid: null,
      purchasedUnUsedToolList: [],
      selectedUnUsedTools: null,
      issueForm: null,
      issuedTools: [],
      selectedIndTrackReturnTool: null,
      purLoading: true,
      selectedIssuedTools: [],
      unitPrice: null,
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
  },
  mounted() {
    if (this.type === 'issue') {
      this.loadPurchasedUnUsedToolsList()
    } else if (this.type === 'return') {
      this.loadIssuedTool()
    }
  },
  methods: {
    savedForm() {
      this.cancelForm()
      this.$emit('refresh')
    },
    cancelForm() {
      this.$emit('update:showToolIssueReturn', false)
    },
    async issueTool(toolTransactionData) {
      let { error } = await API.createRecord('toolTransactions', {
        data: toolTransactionData,
        params: {
          issue: true,
        },
      })
      if (error) {
        this.$message.error(
          error.message || this.$t('common.wo_report.unable_to_update')
        )
      } else {
        this.$message.success(this.$t('common.header.tool_issued_successfully'))
        this.savedForm()
      }
    },
    async saveAdjustmentForm() {
      let toolQuantity = this.$getProperty(this.tool, 'currentQuantity', null)
      let toolId = this.$getProperty(this.tool, 'id', null)
      if (!this.toolType.isRotating) {
        if (
          this.quantity === null ||
          (this.quantity === undefined && !this.quantity)
        ) {
          this.$message.error(this.$t('common._common.quantity_error'))
        } else if (this.quantity < toolQuantity) {
          let toolTransactionData = {
            tool: {
              id: toolId,
            },
            transactionType: transactionTypeEnum.STOCK,
            transactionState: transactionStateEnum.ADJUSTMENT_DECREASE,
            quantity: toolQuantity - this.quantity,
          }
          await this.adjustTool(toolTransactionData)
        } else if (this.quantity > toolQuantity) {
          if (isEmpty(this.unitPrice)) {
            this.$message.error(this.$t('common._common.purchase_price_error'))
          } else {
            let qua = 0

            if (toolQuantity >= 0) {
              qua = toolQuantity
            } else {
              qua = 0
            }
            let { quantity } = this
            quantity = quantity - qua
            let toolTransactionData = {
              tool: {
                id: toolId,
              },
              purchasedTool: {
                unitPrice: this.unitPrice,
                quantity,
              },
              transactionType: transactionTypeEnum.STOCK,
              transactionState: transactionStateEnum.ADJUSTMENT_INCREASE,
              quantity,
            }
            await this.adjustTool(toolTransactionData)
          }
        } else {
          this.$message.error('Unable to Adjust with the same Quantity')
        }
      }
    },

    async adjustTool(toolTransactionData) {
      let { error } = await API.createRecord('toolTransactions', {
        data: toolTransactionData,
        params: {
          adjustQuantity: true,
        },
      })
      if (error) {
        this.$message.error(
          error.message || this.$t('common.wo_report.unable_to_update')
        )
      } else {
        this.$message.success(this.$t('common.header.tool_issued_successfully'))
        this.savedForm()
      }
    },

    async returnToolCall(toolTransaction, rotating) {
      let url = 'v3/modules/data/bulkCreate'
      let params = {
        data: {
          toolTransactions: toolTransaction,
        },
        moduleName: 'toolTransactions',
        params: {
          return: true,
        },
      }
      let { error } = await API.post(url, params)
      if (error) {
        this.$message.error(
          error.message || this.$t('common.wo_report.unable_to_update')
        )
      } else {
        this.$message.success(
          this.$t('common.header.tool_returned_successfully')
        )
        this.loadIssuedTool()
        if (rotating) {
          this.selectedIndTrackReturnTool = null
        }
        this.savedForm()
      }
    },
    async saveIssueForm() {
      if (!this.userid) {
        this.$message.error('Choose a User')
      } else if (this.userid && !this.toolType.isRotating && this.quantity) {
        let toolTransactionData = {
          tool: {
            id: this.tool.id,
          },
          parentId: this.userid,
          transactionType: 3,
          transactionState: 2,
          issuedTo: { ouid: this.userid },
          quantity: this.quantity,
          resource: { resourceType: 4 },
        }
        await this.issueTool(toolTransactionData)
      } else if (
        this.userid &&
        this.toolType.isRotating &&
        this.selectedUnUsedTools
      ) {
        let toolTransactionData = {
          tool: {
            id: this.tool.id,
          },
          parentId: this.userid,
          transactionType: transactionTypeEnum.MANUAL,
          transactionState: transactionStateEnum.ISSUE,
          issuedTo: { ouid: this.userid },
          assetIds: this.selectedUnUsedTools,
          resource: { resourceType: resourceTypeEnum.USER },
        }
        await this.issueTool(toolTransactionData)
      }
    },
    async loadPurchasedUnUsedToolsList() {
      if (this.toolType.isRotating) {
        let filters = {
          rotatingTool: {
            operatorId: 36,
            value: [this.tool.id + ''],
          },
          isUsed: { operatorId: 15, value: [false + ''] },
        }

        this.purLoading = true
        this.purchasedUnUsedToolList = await this.$util.getFilteredAssetList(
          filters
        )
        this.purLoading = false
      }
    },
    toggleUnUsedToolSelection(selected) {
      this.selectedUnUsedTools = selected.map(value => value.id)
    },
    toggleReturnToolSelection(selected) {
      this.selectedIndTrackReturnTool = selected
    },
    getDateTime(val) {
      let value = val.purchasedDate
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value)
    },
    getDateTimeIssuedTime(val) {
      let value = val.sysModifiedTime
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value, true)
    },
    loadIssuedTool() {
      let self = this
      let filters = {
        tool: {
          operatorId: 5,
          value: [this.tool.id + ''],
        },
      }
      self.loading = true
      self.$http
        .get(
          '/v2/toolTransactions/view/showToolTransactionListForReturn?showToolsForReturn=true&filters=' +
            encodeURIComponent(JSON.stringify(filters))
        )
        .then(function(response) {
          self.loading = false
          self.issuedTools = response.data.result.toolTransactions
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.fetchingMore = false
          }
        })
    },
    isValid(val) {
      if (val !== null && val !== '' && val !== undefined) {
        return true
      }
    },
    async returnTool() {
      if (!this.toolType.isRotating) {
        let toolTransaction = []
        for (let i in this.issuedTools) {
          if (
            this.issuedTools[i].updateQuantity &&
            this.issuedTools[i].updateQuantity >
              this.issuedTools[i].remainingQuantity
          ) {
            this.$message.error(
              this.$t('common._common.return_quantity_greater_issued')
            )
            return
          }
          if (
            this.isValid(this.issuedTools[i].updateQuantity) &&
            !isNaN(this.issuedTools[i].updateQuantity)
          ) {
            let tTemp = {
              tool: {
                id: this.issuedTools[i].tool.id,
              },
              parentTransactionId: this.issuedTools[i].id,
              parentId: this.issuedTools[i].parentId,
              transactionType: this.issuedTools[i].transactionType,
              transactionState: transactionStateEnum.RETURN,
              quantity: parseInt(this.issuedTools[i].updateQuantity),
            }
            toolTransaction.push(tTemp)
          }
        }
        let isRotating = this.$getProperty(this.toolType, 'isRotating', null)
        await this.returnToolCall(toolTransaction, isRotating)
      } else if (this.toolType.isRotating) {
        let toolTransaction = []
        for (let i in this.selectedIndTrackReturnTool) {
          let temp = {
            tool: { id: this.selectedIndTrackReturnTool[i].tool.id },
            parentId: this.selectedIndTrackReturnTool[i].parentId,
            parentTransactionId: this.selectedIndTrackReturnTool[i].id,
            transactionType: this.selectedIndTrackReturnTool[i].transactionType,
            transactionState: transactionStateEnum.RETURN,
            assetIds: [this.selectedIndTrackReturnTool[i].asset.id],
          }
          toolTransaction.push(temp)
        }
        let isRotating = this.$getProperty(this.toolType, 'isRotating')
        await this.returnToolCall(toolTransaction, isRotating)
      }
    },
    selectIssuedToolActions(val) {
      this.selectedIssuedTools = val
    },
    markAsFullyReturned(val) {
      val.forEach(element => {
        this.selectedIssuedTools.forEach(obj => {
          if (element.id === obj.id) {
            element.updateQuantity = element.remainingQuantity
          }
        })
      })
      this.$refs.returnTable.clearSelection()
    },
  },
}
</script>
