<template>
  <div>
    <div v-if="type === 'issue'">
      <div>
        <el-dialog
          :visible.sync="showToolIssueReturn"
          title="ISSUE TOOL"
          :before-close="cancelForm"
          :key="storeRoom.id"
          custom-class="fc-dialog-center-container"
        >
          <div v-if="loading" class="mT50">
            <spinner :show="loading" size="80"></spinner>
          </div>
          <div>
            <div class="fc-black3-16 text-left fw4">
              Tool
            </div>
            <el-select
              v-model="selectedTool"
              @change="selectedToolFill(selectedTool)"
              filterable
              class="fc-input-full-border-select2 mT5"
            >
              <template v-for="tool in toolList">
                <el-option
                  :label="tool.toolType.name"
                  v-if="
                    !(
                      tool.toolType.approvalNeeded ||
                      tool.storeRoom.approvalNeeded
                    )
                  "
                  :value="tool.id"
                  :key="tool.id"
                ></el-option>
              </template>
            </el-select>
          </div>
          <div class="height400 mT20">
            <div v-if="tool && tool.toolType.isRotating && tool">
              <el-table
                v-loading="purLoading"
                height="200"
                :data="purchasedUnUsedToolList"
                empty-text="No Rotating assets available."
                class="width100 inventory-inner-table"
                @selection-change="toggleUnUsedToolSelection"
                :default-sort="{ prop: 'costDate', order: 'descending' }"
              >
                <el-table-column type="selection" width="60"></el-table-column>
                <el-table-column
                  prop="serialNumber"
                  sortable
                  label="SERIAL NUMBER"
                ></el-table-column>
                <el-table-column sortable label="RATE/HR">
                  <template>
                    <currency :value="tool.rate"></currency>
                  </template>
                </el-table-column>
                <el-table-column
                  sortable
                  prop="purchasedDate"
                  :formatter="getDateTime"
                  label="PURCHASED TIME"
                ></el-table-column>
              </el-table>
              <el-row class="mT20">
                <el-col :span="12">
                  <p class="fc-black3-16 text-left fw4">
                    Issue To
                  </p>
                  <el-select
                    v-model="userid"
                    filterable
                    clearable
                    :placeholder="'Select User'"
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
            <div v-if="tool && !tool.toolType.isRotating && tool">
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
                    Issue Quantity
                  </div>
                  <el-input
                    placeholder="Issue Quantity"
                    :min="1"
                    :max="tool.currentQuantity"
                    type="number"
                    v-model="quantity"
                    class="fc-input-full-border2 mT5"
                  >
                    <span slot="suffix" class="width100 item-input-txt"
                      >Tool</span
                    >
                  </el-input>
                  <div class="green-txt2-13 mT10 fw4">
                    {{ tool.currentQuantity }} Current Balance
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="fc-black3-16 text-left fw4">
                    Issue To
                  </div>
                  <el-select
                    v-model="userid"
                    filterable
                    clearable
                    :placeholder="'Select User'"
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
            <el-button class="modal-btn-cancel" @click="cancelForm()"
              >CANCEL</el-button
            >
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="saveIssueForm()"
              :loading="saving"
              >{{ saving ? 'Submitting...' : 'SAVE' }}</el-button
            >
          </div>
        </el-dialog>
      </div>
    </div>
    <div v-if="type === 'return'">
      <el-dialog
        :visible.sync="showToolIssueReturn"
        :key="storeRoom.id"
        title="RETURN TOOL"
        :before-close="cancelForm"
        width="65%"
        custom-class="fc-dialog-center-container"
      >
        <spinner
          v-if="showLoading"
          :show="showLoading"
          size="80"
          class="height400"
        ></spinner>

        <div
          v-else-if="$validation.isEmpty(toolList)"
          class="tools-return-empty-state height400"
        >
          <inline-svg
            src="svgs/list-empty"
            iconClass="icon icon-130"
          ></inline-svg>
          <div class="tools-return-empty-state-text">
            {{ $t('common.products.no_tools_available_in_this_storeroom') }}
          </div>
        </div>
        <div v-else>
          <div>
            <div class="fc-black3-16 text-left fw4">
              Tool
            </div>
            <el-select
              v-model="selectedTool"
              @change="selectedToolFill(selectedTool)"
              filterable
              class="fc-input-full-border-select2 mT5"
            >
              <el-option
                v-for="tool in toolList"
                :label="tool.toolType.name"
                :value="tool.id"
                :key="tool.id"
              ></el-option>
            </el-select>
          </div>
          <div class="height400 mT20">
            <div v-if="issueLoading">
              <spinner :show="issueLoading"></spinner>
            </div>
            <div v-if="!issueLoading && tool && !tool.toolType.isRotating">
              <el-table
                :data="issuedTools"
                v-loading="issueLoading"
                empty-text="This tool is not issued to anyone."
                height="300"
                class="inventory-inner-table width100"
                :default-sort="{
                  prop: 'tool.storeRoom.name',
                  order: 'descending',
                }"
              >
                <el-table-column
                  prop="tool.storeRoom.name"
                  sortable
                  width="190"
                  label="STOREROOM NAME"
                ></el-table-column>
                <el-table-column
                  prop="remainingQuantity"
                  sortable
                  label="QUANTITY"
                ></el-table-column>
                <el-table-column
                  prop="sysModifiedTime"
                  :formatter="getDateTimeIssuedTime"
                  width="160"
                  sortable
                  label="ISSUED TIME"
                ></el-table-column>
                <el-table-column label="ISSUED TO">
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
                <el-table-column width="180" label="RETURN QUANTITY">
                  <template v-slot="issuedTool">
                    <div>
                      <el-input
                        placeholder="Quantity"
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
            <div v-if="!issueLoading && tool && tool.toolType.isRotating">
              <el-table
                :data="issuedTools"
                v-loading="issueLoading"
                empty-text="This tool is not issued to anyone."
                height="300"
                class="inventory-inner-table width100"
                @selection-change="toggleReturnToolSelection"
                :default-sort="{ prop: 'storeRoom.name', order: 'descending' }"
              >
                <el-table-column type="selection" width="60"></el-table-column>
                <el-table-column
                  prop="tool.storeRoom.name"
                  sortable
                  label="STOREROOM NAME"
                  width="190"
                ></el-table-column>
                <el-table-column
                  prop="asset.name"
                  sortable
                  label="NAME"
                  width="140"
                ></el-table-column>
                <el-table-column
                  prop="asset.serialNumber"
                  sortable
                  label="SERIAL NUMBER"
                  width="170"
                ></el-table-column>
                <el-table-column label="ISSUED TO" width="150">
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
                  label="ISSUED TIME"
                ></el-table-column>
              </el-table>
            </div>
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelForm()"
            >CLOSE</el-button
          >
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="returnTool()"
            :loading="saving"
            >{{ saving ? 'Submitting...' : 'Return' }}</el-button
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
import { getFilteredToolList } from 'pages/Inventory/InventoryUtil'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
const transactionTypeEnum = {
  STOCK: 1,
  MANUAL: 3,
}
const transactionStateEnum = {
  ISSUE: 2,
  RETURN: 3,
}
export default {
  props: ['storeRoom', 'showToolIssueReturn', 'type'],
  components: {
    UserAvatar,
    StoreRoomAvatar,
  },
  mixins: [inventoryMixin],
  data() {
    return {
      saving: false,
      loading: false,
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
      tool: null,
      issueLoading: false,
      selectedTool: null,
      showLoading: true,
      toolList: [],
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
  },
  mounted() {
    this.loadToolforStoreRoom()
  },
  methods: {
    async loadToolforStoreRoom() {
      this.showLoading = true
      let filters = {
        storeRoom: {
          operatorId: 36,
          value: [this.storeRoom.id + ''],
        },
      }
      this.toolList = await getFilteredToolList(filters)
      if (isEmpty(this.toolList)) {
        return
      }
      this.selectedTool = this.toolList[0].id
      this.tool = this.toolList[0]
      if (this.type === 'issue') {
        this.loadPurchasedUnUsedToolsList()
      } else if (this.type === 'return') {
        this.loadIssuedTool()
      }
      this.showLoading = false
    },
    savedForm() {
      this.cancelForm()
    },
    cancelForm() {
      this.$emit('refresh')
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
    async saveIssueForm() {
      if (!this.userid) {
        this.$message.error('Choose a User')
      } else if (
        this.userid &&
        !this.tool.toolType.isRotating &&
        this.quantity
      ) {
        let toolTransactionData = {
          tool: {
            id: this.tool.id,
          },
          parentId: this.userid,
          transactionType: transactionTypeEnum.MANUAL,
          transactionState: transactionStateEnum.ISSUE,
          issuedTo: { ouid: this.userid },
          quantity: this.quantity,
        }
        await this.issueTool(toolTransactionData)
      } else if (
        this.userid &&
        this.tool.toolType.isRotating &&
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
        }
        await this.issueTool(toolTransactionData)
      }
    },
    async loadPurchasedUnUsedToolsList() {
      if (this.tool.toolType.isRotating) {
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
      self.issueLoading = true
      self.$http
        .get(
          '/v2/toolTransactions/view/showToolTransactionListForReturn?showToolsForReturn=true&filters=' +
            encodeURIComponent(JSON.stringify(filters))
        )
        .then(function(response) {
          self.issueLoading = false
          self.issuedTools = response.data.result.toolTransactions
        })
        .catch(function(error) {
          if (error) {
            self.issueLoading = false
            self.fetchingMore = false
          }
        })
    },
    isValid(val) {
      if (val !== null && val !== '' && val !== undefined) {
        return true
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
        this.$emit('refreshInventory')
      }
    },
    async returnTool() {
      if (!this.tool.toolType.isRotating) {
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
        await this.returnToolCall(
          toolTransaction,
          this.$getProperty(this.tool, 'toolType.isRotating', null)
        )
      } else if (this.tool.toolType.isRotating) {
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
        await this.returnToolCall(
          toolTransaction,
          this.$getProperty(this.tool, 'toolType.isRotating')
        )
      }
    },
    selectedToolFill(val) {
      if (val) {
        this.tool = this.toolList.find(it => val === it.id)
        if (this.type === 'issue') {
          this.loadPurchasedUnUsedToolsList()
        } else if (this.type === 'return') {
          this.loadIssuedTool()
        }
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.tools-return-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: auto;

  .tools-return-empty-state-text {
    font-size: 16px;
    font-weight: 500;
    line-height: 1.5;
    letter-spacing: 0.27px;
    color: #324056;
    margin: 20px auto;
  }
}
</style>
