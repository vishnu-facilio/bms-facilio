<template>
  <div class="p30 white-bg-block mB20">
    <div class="flex-middle">
      <div class="fc-v1-icon-bg text-center">
        <InlineSvg
          src="svgs/service"
          iconClass="icon icon-lg fill-white"
        ></InlineSvg>
      </div>
      <div class="fc-black3-16 mL10">
        {{ $t('common.products.services') }}
      </div>
    </div>

    <div
      class="inventory-table inventory-tool-table inventory-labour-tool-table pT20  overflow-y-scroll"
    >
      <table class="width100" v-if="loading.woService">
        <tr>
          <td colspan="100%">
            <div class="iTloading in-no-data">
              <spinner :show="true" size="80"></spinner>
            </div>
          </td>
        </tr>
      </table>

      <table class="width100" v-else>
        <thead>
          <tr>
            <th class style="width: 180px;">
              {{ $t('common.products._service') }}
            </th>
            <th class="text-right width110px">
              {{ $t('common.wo_report._start_time') }}
            </th>
            <th class="text-right width110px pL20">
              {{ $t('common.wo_report._end_time') }}
            </th>
            <th class="text-right width130px">
              {{ $t('common._common.duration_hr') }}
            </th>
            <th class="text-right width130px">
              {{ $t('common._common._quantity') }}
            </th>
            <th class="text-right">{{ $t('common.header._price') }}</th>
            <th class="text-right">{{ $t('common.tabs._cost') }}</th>
            <th class="width40px"></th>
          </tr>
        </thead>
        <tbody v-if="!workorderServiceList.length">
          <tr>
            <td
              @click="actionRule ? addNewWOService() : null"
              :class="{ disabled: !actionRule }"
              class="inventory-td-selected pL20 pT10 pB10"
            >
              <div>{{ $t('common.header.add_service') }}</div>
            </td>
            <td class="pT10 pB10">
              <div class="fc-black-13 text-right">-- -- --</div>
            </td>
            <td class="pT10 pB10 pL20">
              <div class="fc-black-13 text-right">-- -- --</div>
            </td>
            <td>
              <div class="text-right">
                <el-input
                  placeholder
                  type="number"
                  class="fc-input-full-border2 width50px inventory-input-width text-center mR10"
                  disabled
                ></el-input>
              </div>
            </td>
            <td>
              <div class="text-right">
                <el-input
                  placeholder
                  type="number"
                  class="fc-input-full-border2 width50px inventory-input-width text-center mR10"
                  disabled
                ></el-input>
              </div>
            </td>
            <td class="pT10 pB10">
              <div class="fc-grey3-13 text-right">
                <currency
                  :value="0"
                  :recordCurrency="recordCurrency"
                ></currency>
              </div>
            </td>
            <td class="pT10 pB10">
              <div class="fc-grey3-13 text-right">
                <currency
                  :value="0"
                  :recordCurrency="recordCurrency"
                ></currency>
              </div>
            </td>
            <td></td>
          </tr>
        </tbody>
        <tbody v-else>
          <tr
            v-for="workService in workorderServiceList"
            :key="workService.id"
            class="borderB1px border-color11 visibility-visible-actions pointer"
          >
            <td>
              <div class="pL17" style="width: 180px;">
                <div>{{ workService.service.name }}</div>
              </div>
            </td>
            <td>
              <div class="in-Quantity width110px">
                <div class="fc-black-13 text-right pR17">
                  {{
                    formatTime(Number(workService.startTime), 'hh:mm a', false)
                  }}
                </div>
                <div class="fc-grey2-text12 text-right">
                  <el-date-picker
                    v-model="workService.startTime"
                    format="dd-MM-yyyy hh:mm a"
                    value-format="timestamp"
                    type="datetime"
                    :disabled="
                      !actionRule ||
                        $getProperty(workService, 'service.paymentType') === 1
                    "
                    placeholder="Start time"
                    class="fc-input-border-remove inventory-date-picker pL40 fc-grey2-text12"
                    @change="changeWorkServiceField(workService, 1)"
                  ></el-date-picker>
                </div>
              </div>
            </td>
            <td class="pL20">
              <div class="in-Quantity width110px">
                <div class="fc-black-13 text-right">
                  {{
                    formatTime(Number(workService.endTime), 'hh:mm a', false)
                  }}
                </div>
                <div class="fc-grey2-text12 text-right">
                  <el-date-picker
                    v-model="workService.endTime"
                    format="dd-MM-yyyy hh:mm a"
                    value-format="timestamp"
                    type="datetime"
                    :disabled="
                      !actionRule ||
                        $getProperty(workService, 'service.paymentType') === 1
                    "
                    placeholder="End time"
                    class="fc-input-border-remove inventory-date-picker pL40 fc-grey2-text12"
                    @change="changeWorkServiceField(workService, 2)"
                    :picker-options="{
                      disabledDate(time) {
                        return time.getTime() < workService.startTime
                      },
                    }"
                  ></el-date-picker>
                </div>
              </div>
            </td>
            <td>
              <div
                class="in-Quantity"
                style="width: 70px; text-align: right; float: right;"
                :title="workService.duration"
                v-tippy="{
                  placement: 'top',
                  arrow: true,
                  animation: 'shift-away',
                }"
              >
                <el-input
                  :placeholder="$t('common._common._duration')"
                  v-model="workService.duration"
                  @change="changeWorkServiceField(workService, 3)"
                  :disabled="
                    !actionRule ||
                      $getProperty(workService, 'service.paymentType') === 1
                  "
                  class="pL10 labour-inventory-items-input labour-inventory-input pR0 fc-input-full-border-select2"
                ></el-input>
              </div>
            </td>

            <td>
              <div
                class="in-Quantity"
                style="width: 70px; text-align: right; float: right;"
                :title="workService.quantity"
                v-tippy="{
                  placement: 'top',
                  arrow: true,
                  animation: 'shift-away',
                }"
              >
                <el-input
                  :placeholder="$t('common._common._duration')"
                  v-model="workService.quantity"
                  @change="changeWorkServiceField(workService, 4)"
                  :disabled="!actionRule"
                  class="pL10 labour-inventory-items-input labour-inventory-input pR0 fc-input-full-border-select2"
                ></el-input>
              </div>
            </td>
            <td>
              <div class="text-right">
                <currency
                  :value="(workService.service || {}).buyingPrice || 0"
                  :recordCurrency="recordCurrency"
                ></currency>
              </div>
            </td>
            <td>
              <div class="text-right">
                <currency
                  :value="workService.cost"
                  :recordCurrency="recordCurrency"
                ></currency>
              </div>
            </td>
            <td v-show="actionRule">
              <i
                class="el-icon-delete pointer inv-delet-icon visibility-hide-actions pR10 pL10"
                data-arrow="true"
                :title="$t('common._common.delete')"
                v-tippy
                @click="deleteWoService(workService)"
              ></i>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="item-add" v-if="loading.woService"></div>
    <div class="item-add" v-else>
      <div
        class="fL"
        v-show="
          isNotPortal && actionRule && (workorderServiceList || []).length
        "
      >
        <div
          class="green-txt-13 fc-v1-add-txt pointer"
          @click="addNewWOService"
        >
          <img src="~assets/add-icon.svg" />{{
            $t('common.header.add_service')
          }}
        </div>
      </div>
      <div class="fR inline-flex mR44">
        <div class="bold mR50">{{ $t('common.header._total') }}</div>
        <div class="fc-black3-16 text-right bold pL10">
          <currency
            :value="workServiceCost"
            :recordCurrency="recordCurrency"
          ></currency>
        </div>
      </div>
    </div>
    <el-dialog
      :visible.sync="serviceAddDialogVisibility"
      :fullscreen="false"
      open="top"
      custom-class="fc-dialog-up Inventoryaddvaluedialog"
      :append-to-body="true"
    >
      <div class="new-header-container">
        <div class="fc-setup-modal-title">
          {{ $t('common.products.services_list') }}
        </div>
      </div>
      <div class="fc-inv-container-body">
        <spinner
          :show="loading.serviceList"
          size="80"
          v-if="loading.serviceList"
          class="flex-middle justify-content-center flex-direction-column"
        ></spinner>
        <table
          v-else
          class="setting-list-view-table width100 invent-table-dialog"
        >
          <thead>
            <th class="setting-table-th setting-th-text"></th>
            <th class="setting-table-th setting-th-text">
              {{ $t('common.products.name') }}
            </th>
            <th class="setting-table-th setting-th-text">
              {{ `Buying Price (${$currency})` }}
            </th>
          </thead>
          <tbody v-if="!allServiceList.length">
            <tr>
              <td colspan="100%">
                <div
                  class="flex-middle justify-content-center flex-direction-column"
                >
                  <inline-svg
                    src="svgs/emptystate/inventory"
                    iconClass="icon text-center icon-100"
                  ></inline-svg>
                  <div class="nowo-label f14 bold">
                    {{ $t('common.products.no_services_present') }}
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr
              class="tablerow asset-hover-td"
              v-for="service in allServiceList"
              :key="service.id"
            >
              <td style="width:10%;">
                <el-checkbox v-model="service.checked"></el-checkbox>
              </td>
              <td>{{ service.name }}</td>
              <td>{{ service.buyingPrice || '---' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="modal-dialog-footer-parts-dialog">
        <el-button class="modal-btn-cancel" @click="closeServiceDialog()">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          class="modal-btn-save"
          style="margin-left:0px !important;"
          type="primary"
          @click="addWoService()"
          :loading="loading.woServiceSave"
          >{{ $t('common._common._add') }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import moment from 'moment-timezone'
import Vue from 'vue'
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import InlineSvg from '@/InlineSvg'
import { eventBus } from '@/page/widget/utils/eventBus'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import {
  getFilteredItemListWithParentModule,
  getFilteredToolListWithParentModule,
} from 'pages/Inventory/InventoryUtil'
import { getRelatedFieldName } from 'src/util/relatedFieldUtil'

export default {
  props: ['details', 'resizeWidget'],
  mixins: [workorderMixin],
  data() {
    return {
      newItemPartToggle: false,
      selectedInventory: null,
      hidequrry: true,
      additionalCost: {
        name: null,
        cost: null,
      },
      loading: {
        deleteAdditionalCost: false,
        serviceList: false,
        woService: false,
        woServiceSave: false,
      },
      toolLoading: false,
      addissueditem: false,
      selectedInventoryList: [],
      inventoryRequestForWOLoading: false,
      iTloading: false,
      itemnsloading: false,
      toolsloading: false,
      labourLoading: false,
      selectedTool: null,
      workorderItemsList: [],
      workLabourTotalCost: null,
      workorderToolsList: [],
      workorderLabourList: [],
      workorderServiceList: [],
      newToolPartToggle: false,
      workOrderCostList: [],
      individualItemList: [],
      individualToolList: [],
      labourList: [],
      newLabourToggle: false,
      inventory: [],
      stockedTools: [],
      selectedLabour: [],
      tempStoreList: [],
      temptoolStoreList: [],
      itemSerachQuerry: null,
      selectedStoreList: [],
      selectedToolStoreList: [],
      inventoryRequestDialogVisibility: false,
      issueToolIRDialogVisibility: false,
      issueItemIRDialogVisibility: false,
      issueInventoryIRDialogVisibility: false,
      inventoryRequestList: null,
      allToolListLoading: false,
      inventoryListLoading: false,
      serviceAddDialogVisibility: false,
      allServiceList: [],
      items: [],
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.init()
  },
  mounted() {
    eventBus.$on('refresh-inventory-summary', () => {
      this.reload()
    })
  },
  components: {
    InlineSvg,
  },
  computed: {
    workorder() {
      return this.details.workorder
    },
    inventoryList() {
      let list = []
      list = this.inventory.filter(item => {
        return !(item.itemType.approvalNeeded || item.storeRoom.approvalNeeded)
      })
      let self = this
      if (this.selectedStoreList.length) {
        list = list.filter(function(rt) {
          if (
            self.selectedStoreList.findIndex(rl => rl === rt.storeRoom.id) > -1
          ) {
            return rt
          }
        })
      }
      if (this.itemSerachQuerry) {
        let self = this
        return list.filter(data => {
          if (
            data.itemType.name
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) > -1 ||
            data.storeRoom.name
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) > -1 ||
            JSON.stringify(data.quantity)
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) === 0
          ) {
            return data
          }
        })
      } else {
        return list
      }
    },
    toolsList() {
      let list = []
      list = this.stockedTools.filter(item => {
        return !(item.toolType.approvalNeeded || item.storeRoom.approvalNeeded)
      })
      let self = this
      if (this.selectedToolStoreList.length) {
        list = list.filter(function(rt) {
          if (
            self.selectedToolStoreList.findIndex(rl => rl === rt.storeRoom.id) >
            -1
          ) {
            return rt
          }
        })
      }
      if (this.itemSerachQuerry) {
        let self = this
        return list.filter(data => {
          if (
            data.toolType.name
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) > -1 ||
            data.storeRoom.name
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) > -1 ||
            JSON.stringify(data.quantity)
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) === 0
          ) {
            return data
          }
        })
      } else {
        return list
      }
    },
    individualItemListwrapper() {
      let list = this.individualItemList
      if (this.itemSerachQuerry) {
        let self = this
        return list.filter(data => {
          if (
            data.serialNumber
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) > -1
          ) {
            return data
          }
        })
      } else {
        return list
      }
    },
    individualToolListwrapper() {
      let list = this.individualToolList
      if (this.itemSerachQuerry) {
        let self = this
        return list.filter(data => {
          if (
            data.serialNumber
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) > -1
          ) {
            return data
          }
        })
      } else {
        return list
      }
    },
    workToolTotalCost() {
      if (this.workOrderCostList.length) {
        if (this.workOrderCostList.find(rt => rt.costTypeEnum === 'tools')) {
          return this.workOrderCostList.find(rt => rt.costTypeEnum === 'tools')
            .cost
        } else {
          return 0
        }
      } else {
        return 0
      }
    },
    actionRule() {
      if (this.canEdit) {
        return true
      } else {
        return false
      }
    },
    workItemTotalCost() {
      if (this.workOrderCostList.length) {
        if (this.workOrderCostList.find(rt => rt.costTypeEnum === 'items')) {
          return this.workOrderCostList.find(rt => rt.costTypeEnum === 'items')
            .cost
        } else {
          return 0
        }
      } else {
        return 0
      }
    },
    workOrderTotalCost() {
      if (this.workOrderCostList && this.workOrderCostList.length) {
        let cost = 0
        for (let i = 0; i < this.workOrderCostList.length; i++) {
          cost += this.workOrderCostList[i].cost
        }
        return cost
      } else {
        return 0
      }
    },
    workServiceCost() {
      return (
        (
          (this.workOrderCostList || []).find(
            rt => rt.costTypeEnum === 'service'
          ) || {}
        ).cost || 0
      )
    },
    individualTracking() {
      if (this.inventory && this.inventory.length && this.selectedInventory) {
        return true
      } else {
        return false
      }
    },
    individualToolTracking() {
      if (this.stockedTools && this.stockedTools.length && this.selectedTool) {
        return true
      } else {
        return false
      }
    },
    canEdit() {
      if (this.isStateFlowEnabled) {
        return !this.$store.getters.isStatusLocked(
          this.workorder.moduleState.id,
          'workorder'
        )
      } else {
        return this.workorder && this.workorder.approvalState !== 2
      }
    },
    isStateFlowEnabled() {
      return Boolean(
        this.workorder.moduleState && this.workorder.moduleState.id
      )
    },
  },
  methods: {
    init() {
      // this.loadWoItemParts(true)
      // this.loadWorkOrderToolParts(true)
      // this.getWorkOrderCostList(true)
      // this.loadWorkOrderLabour(true)
      this.loadWorkOrderService(true)
      // this.loadItem(true)
      this.loadInventoryRequest()
    },
    reload() {
      this.loadWoItemParts(true)
      this.loadInventoryRequest()
    },
    loadInventory() {
      let self = this
      this.itemSerachQuerry = null
      this.inventoryListLoading = true
      this.$http
        .get(
          'v2/item/view/all?showForWorkorder=true&includeServingSite=true&siteId=' +
            this.workorder.siteId
        )
        .then(response => {
          this.inventoryListLoading = false
          let inventory = []
          inventory =
            response.data.result && response.data.result.items
              ? response.data.result.items
              : []
          if (response) {
            for (let item of inventory) {
              item.checked = false
              item.addedQuantity = 0
              item.invidualList = []
              if (
                item.storeRoom &&
                self.tempStoreList.findIndex(
                  rt => rt.id === item.storeRoom.id
                ) < 0
              ) {
                self.tempStoreList.push(item.storeRoom)
              }
            }
            let self2 = self
            self.inventory = inventory.filter(function(rt) {
              if (self2.itemshowRule(rt)) {
                return rt
              }
            })
          }
        })
    },
    loadToolslist() {
      let self = this
      this.itemSerachQuerry = null
      this.allToolListLoading = true
      this.$http.get('v2/tool/view/all').then(response => {
        let stockedTools = []
        stockedTools =
          response.data.result && response.data.result.tool
            ? response.data.result.tool
            : []
        this.allToolListLoading = false
        if (response) {
          for (let item of stockedTools) {
            item.checked = false
            item.addedQuantity = 0
            item.invidualList = []
            if (
              item.storeRoom &&
              self.temptoolStoreList.findIndex(
                rt => rt.id === item.storeRoom.id
              ) < 0
            ) {
              self.temptoolStoreList.push(item.storeRoom)
            }
          }
          let self2 = self
          self.stockedTools = stockedTools.filter(function(rt) {
            if (self2.toolshowRule(rt)) {
              return rt
            }
          })
        }
      })
    },
    getItemCount(workItem) {
      if (workItem) {
        let diff
        if (workItem.requestedLineItem) {
          diff =
            Number(workItem.tempQuantity) +
            Number(workItem.requestedLineItem.issuedQuantity) -
            Number(workItem.requestedLineItem.usedQuantity) -
            Number(workItem.quantity)
        } else if (workItem.parentTransactionId !== -1) {
          diff =
            Number(workItem.tempQuantity) +
            Number(workItem.remainingQuantity) -
            Number(workItem.quantity)
        } else {
          diff =
            Number(workItem.tempQuantity) +
            Number(workItem.tempItem.quantity) -
            Number(workItem.quantity)
        }
        if (Number(workItem.quantity) < 0) {
          return 'Quantity must be nonzero'
        } else if (diff < 0) {
          return 'Exceeded'
        } else {
          return diff
        }
      } else {
        return ''
      }
    },
    getItemQuantityState(workItem) {
      if (workItem) {
        let diff
        if (workItem.requestedLineItem) {
          diff =
            Number(workItem.tempQuantity) +
            Number(workItem.requestedLineItem.issuedQuantity) -
            Number(workItem.requestedLineItem.usedQuantity) -
            Number(workItem.quantity)
        } else if (workItem.parentTransactionId !== -1) {
          diff =
            Number(workItem.tempQuantity) +
            Number(workItem.remainingQuantity) -
            Number(workItem.quantity)
        } else {
          diff =
            Number(workItem.tempQuantity) +
            Number(workItem.tempItem.quantity) -
            Number(workItem.quantity)
        }
        if (Number(workItem.quantity) < 0) {
          return true
        } else if (diff < 0) {
          return true
        } else {
          return false
        }
      } else {
        return false
      }
    },
    getToolCount(worktool) {
      if (worktool) {
        let diff
        if (worktool.requestedLineItem) {
          diff =
            Number(worktool.requestedLineItem.issuedQuantity) -
            Number(worktool.quantity)
        } else if (worktool.parentTransactionId !== -1) {
          diff =
            Number(worktool.tempQuantity) +
            Number(worktool.remainingQuantity) -
            Number(worktool.quantity)
        } else {
          diff =
            Number(worktool.tempQuantity) +
            Number(worktool.tempTool.currentQuantity) -
            Number(worktool.quantity)
        }
        if (Number(worktool.quantity) < 0) {
          return 'Quantity must be nonzero'
        } else if (diff < 0) {
          return 'Exceeded'
        } else {
          return diff
        }
      } else {
        return ''
      }
    },
    getToolQuantityState(worktool) {
      if (worktool) {
        let diff
        if (worktool.requestedLineItem) {
          diff =
            Number(worktool.requestedLineItem.issuedQuantity) -
            Number(worktool.quantity)
        } else if (worktool.parentTransactionId !== -1) {
          diff =
            Number(worktool.tempQuantity) +
            Number(worktool.remainingQuantity) -
            Number(worktool.quantity)
        } else {
          diff =
            Number(worktool.tempQuantity) +
            Number(worktool.tempTool.currentQuantity) -
            Number(worktool.quantity)
        }
        if (Number(worktool.quantity) < 0) {
          return true
        } else if (diff < 0) {
          return true
        } else {
          return false
        }
      } else {
        return false
      }
    },
    addNewWOLabour() {
      this.loadLabour(true)
      this.newLabourToggle = true
    },
    addNewWOItem() {
      let self = this
      this.individualItemList = []
      this.inventory = this.inventory.filter(function(rt) {
        if (self.itemshowRule(rt)) {
          rt.checked = false
          return rt
        }
      })
      this.newItemPartToggle = true
    },
    addNewWOTool() {
      let self = this
      this.individualToolList = []
      this.stockedTools = this.stockedTools.filter(function(rt) {
        if (self.toolshowRule(rt)) {
          rt.checked = false
          return rt
        }
      })
      this.newToolPartToggle = true
    },
    ticketstatusvalue(id) {
      if (id !== null) {
        let statusObj = this.$store.getters.getTicketStatus(id, 'workorder')
        if (!statusObj) {
          return ''
        }
        let status = statusObj.status
        if (status) {
          return status
        } else {
          return ''
        }
      }
      return ''
    },
    itemshowRule(invt) {
      if (!invt.itemType.isRotating) {
        let tempWorkItem = this.workorderItemsList.filter(i => {
          return i.item.id === invt.id
        })
        let check = true
        tempWorkItem.forEach(i => {
          if (i.parentTransactionId === -1) {
            check = false
          }
        })
        return check
      } else {
        if (invt.quantity > 0 && invt.itemType.currentQuantity > 0) {
          return true
        } else {
          return false
        }
      }
    },
    toolshowRule(invt) {
      if (!invt.toolType.isRotating) {
        let tempWorkItem = this.workorderToolsList.filter(i => {
          return i.tool.id === invt.id
        })
        let check = true
        tempWorkItem.forEach(i => {
          if (i.parentTransactionId === -1) {
            check = false
          }
        })
        return check
      } else {
        if (invt.toolType.currentQuantity > 0) {
          return true
        } else {
          return false
        }
      }
    },
    addAdditionalCost() {
      let self = this
      if (this.additionalCost.name && this.additionalCost.cost !== null) {
        let param = {
          workorderCost: {
            name: this.additionalCost.name,
            parentId: {
              id: this.workorder.id,
            },
            cost: Number(this.additionalCost.cost),
            costType: 5,
          },
        }
        this.$http.post('v2/workorderCosts/add', param).then(response => {
          if (response.data.responseCode === 0) {
            self.$message.success(
              this.$t('common._common.cost_added_successfully')
            )
            self.actionToWorkorderStatusChange()
            self.additionalCost = { name: null, cost: null }
            self.getWorkOrderCostList()
          } else {
            self.$message.error(response.data.message)
          }
        })
      }
    },
    updateAdditionalCost(data) {
      if (data) {
        let self = this
        let param = {
          id: data.id,
          name: data.name,
          parentId: {
            id: this.workorder.id,
          },
          costType: 5,
          cost: Number(data.cost),
        }
        this.$http.post('v2/workorderCosts/update', param).then(response => {
          self.actionToWorkorderStatusChange()
          if (response.data.responseCode === 0) {
            self.$message.success(
              this.$t('common._common.cost_edited_successfully')
            )
            self.getWorkOrderCostList()
          } else {
            self.$message.error(response.data.message)
          }
        })
      }
    },
    deleteAdditionalCost(id) {
      if (id) {
        let self = this
        let param = {
          workordercostId: [id],
          parentId: this.workorder.id,
        }
        this.$http.post('v2/workorderCosts/delete', param).then(response => {
          self.actionToWorkorderStatusChange()
          if (response.data.responseCode === 0) {
            self.getWorkOrderCostList()
            self.$message.success('Cost Deleted')
          } else {
            self.$message.error(response.data.message)
          }
        })
      }
    },
    deleteWorkorderLabour(workLabour) {
      let self = this
      let param = { parentId: this.workorder.id, workorderLabourIds: [] }
      param.workorderLabourIds.push(workLabour.id)
      this.$http
        .post('v2/workorderLabour/delete', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            self.$message.success(
              this.$t('common.products.labour_deleted_successfully')
            )
            self.actionToWorkorderStatusChange()
            self.loadWorkOrderLabour()
            self.getWorkOrderCostList()
          } else {
            self.$message.error(response.data.message)
          }
        })
        .catch(error => {
          console.log(error)
        })
    },
    deleteWorkorderTool(workTool, index) {
      if (!workTool.tool.hasOwnProperty('storeRoom')) {
        this.workorderToolsList.pop()
      } else {
        let self = this
        let param = { parentId: this.workorder.id, workorderToolsIds: [] }
        param.workorderToolsIds.push(workTool.id)
        this.$http
          .post('v2/workorderTools/delete', param)
          .then(response => {
            if (response.data.responseCode === 0) {
              self.actionToWorkorderStatusChange()
              self.loadToolslist()
              self.loadWorkOrderToolParts()
              self.getWorkOrderCostList()
              self.$message.success('Tool Deleted')
              self.workorderToolsList.splice(index, 1)
            } else {
              self.$message.error(response.data.message)
            }
          })
          .catch()
      }
    },
    deleteWorkorderItem(workItem, index) {
      let self = this
      if (!workItem.hasOwnProperty('purchasedItem')) {
        this.workorderItemsList.pop()
      } else {
        let param = { parentId: this.workorder.id, workorderItemsId: [] }
        param.workorderItemsId.push(workItem.id)
        this.$http
          .post('v2/workorderItems/delete', param)
          .then(response => {
            if (response.data.responseCode === 0) {
              self.actionToWorkorderStatusChange()
              self.loadInventory()
              self.$message.success('Item Deleted')
              self.getWorkOrderCostList()
              self.workorderItemsList.splice(index, 1)
            } else {
              self.$message.error(response.data.message)
            }
          })
          .catch(error => {
            console.log(error)
          })
      }
    },
    cancelLabourDialog() {
      this.newLabourToggle = false
    },
    cancelToolPartsDialog() {
      this.newToolPartToggle = false
      this.selectedTool = null
      this.selectedTool = null
    },
    getWorkOrderCostList(loading) {
      let self = this
      this.$http
        .get('v2/workorderCostsList/parent/' + this.workorder.id)
        .then(response => {
          if (!loading) {
            self.itemnsloading = false
          }
          self.workOrderCostList = response.data.result.workorderCost
        })
    },
    getCurrentTime() {
      return moment()
        .tz(Vue.prototype.$timezone)
        .valueOf()
    },
    addLabourSave() {
      let self = this
      let param = { workorderLabourList: [] }
      if (!this.labourList.length) {
        return
      }
      for (let i = 0; i < this.labourList.length; i++) {
        let labour = this.labourList[i]
        if (labour.checked) {
          let temp = {
            parentId: this.workorder.id,
            startTime: -1,
            endTime: -1,
            labour: labour,
          }
          param.workorderLabourList.push(temp)
          this.labourList[i] = false
        }
      }

      this.labourLoading = true
      this.$http
        .post('/v2/workorderLabour/addOrUpdate', param)
        .then(response => {
          self.labourLoading = true
          if (response.data.responseCode !== 0) {
            this.$message.error(response.data.message)
          } else {
            self.$message.success(
              this.$t('common._common.added_available_labour_successfully')
            )

            self.actionToWorkorderStatusChange()
            self.loadWorkOrderLabour()
            self.getWorkOrderCostList()
          }
        })
        .catch(error => {
          console.log(error)
          this.$message.error(this.$t('common.wo_report.unable_to_add_labour'))
          self.labourLoading = false
        })
      this.newLabourToggle = false
    },
    addToolPartSave() {
      let self = this
      let param = { workorderToolsList: [] }
      let purchasedTools = []
      if (this.individualToolList.length) {
        purchasedTools = this.individualToolList.map(function(rt) {
          if (rt.checked) {
            return rt.id
          }
        })
        purchasedTools = purchasedTools.filter(rt => typeof rt !== 'undefined')
      }
      let temp = {
        parentId: this.workorder.id,
        tool: { id: this.selectedTool },
        quantity: 1,
        assetIds: purchasedTools,
      }
      param.workorderToolsList.push(temp)
      this.toolsloading = true
      this.$http
        .post('/v2/workorderTools/addOrUpdate', param)
        .then(response => {
          if (response.data.responseCode !== 0) {
            this.$message.error(response.data.message)
          } else {
            self.$message.success(this.$t('common._common.added_successfully'))
            self.selectedTool = null
            self.actionToWorkorderStatusChange()
            self.loadWorkOrderToolParts(false, true)
            self.getWorkOrderCostList()
          }
        })
        .catch(error => {
          console.log(error)
          this.$message.error(this.$t('common.wo_report.unable_to_update'))
        })
      this.newToolPartToggle = false
    },
    bulkaddItem() {
      let selectedItems = this.inventory.filter(rt => rt.checked)
      let bulkItem = selectedItems.filter(function(rt) {
        if (rt && rt.itemType && !rt.itemType.isRotating) {
          return rt
        }
      })
      if (bulkItem.length) {
        this.addbulkItem(bulkItem)
        if (!this.individualItemList.length) {
          this.newItemPartToggle = false
        }
      }
      if (this.individualTracking && this.individualItemList.length) {
        this.addbulkItem(false, this.individualItemList)
        this.newItemPartToggle = false
      }
    },
    bulkaddTool() {
      let selectedItems = this.stockedTools.filter(rt => rt.checked)
      let bulkItem = selectedItems.filter(function(rt) {
        if (rt && rt.toolType && !rt.toolType.isRotating) {
          return rt
        }
      })
      if (bulkItem.length) {
        this.addbulkTool(bulkItem)
        if (!this.individualToolList.length) {
          this.newToolPartToggle = false
        }
      }
      if (this.individualToolTracking && this.individualToolList.length) {
        this.addbulkTool(false, this.individualToolList)
        this.newToolPartToggle = false
      }
    },
    addbulkTool(tools, individualToolList) {
      let self = this
      let param = { workorderToolsList: [] }
      let obj = {}
      let assetIds = []
      let data = []
      if (individualToolList && individualToolList.length) {
        for (let i = 0; i < individualToolList.length; i++) {
          data = individualToolList[i]
          if (data.checked) {
            assetIds = [data.id]
            obj = {
              parentId: this.workorder.id,
              tool: { id: data.tool.id },
              quantity: 1,
              assetIds: assetIds,
            }
            param.workorderToolsList.push(obj)
          }
        }
      } else if (tools) {
        data = []
        for (let i = 0; i < tools.length; i++) {
          let data = tools[i]
          obj = {
            parentId: this.workorder.id,
            tool: { id: data.id },
            quantity: 1,
            assetIds: assetIds,
          }
          param.workorderToolsList.push(obj)
        }
      }
      this.toolsloading = true
      this.$http
        .post('/v2/workorderTools/addOrUpdate', param)
        .then(response => {
          if (response.data.responseCode !== 0) {
            this.$message.error(response.data.message)
          } else {
            self.$message.success(this.$t('common._common.added_successfully'))
            self.selectedTool = null
            self.actionToWorkorderStatusChange()
            self.loadWorkOrderToolParts(false, true)
            self.getWorkOrderCostList()
          }
        })
        .catch(() => {
          this.$message.error(this.$t('common.wo_report.unable_to_update'))
        })
    },
    addbulkItem(items, individualItemList) {
      let param = { workorderItems: [] }
      let obj = {}
      let assetIds = []
      let data = []
      if (individualItemList && individualItemList.length) {
        for (let i = 0; i < individualItemList.length; i++) {
          data = individualItemList[i]
          if (data.checked) {
            assetIds = [data.id]
            obj = {
              parentId: this.workorder.id,
              item: { id: data.item.id },
              quantity: 1,
              assetIds: assetIds,
              remainingQuantity: 0,
            }
            param.workorderItems.push(obj)
          }
        }
      } else if (items) {
        data = []
        for (let i = 0; i < items.length; i++) {
          let data = items[i]
          obj = {
            parentId: this.workorder.id,
            item: { id: data.id },
            quantity: 1,
            assetIds: assetIds,
            remainingQuantity: 0,
          }
          param.workorderItems.push(obj)
        }
      }

      let self = this
      this.$http
        .post('/v2/workorderItems/addOrUpdate', param)
        .then(response => {
          if (response.data.responseCode !== 0) {
            self.$message.error(response.data.message)
          } else {
            self.$message.success(
              this.$t('common.header.item_added_successfully')
            )
            self.actionToWorkorderStatusChange()
            self.loadWoItemParts(false, true)
          }
        })
        .catch(() => {})
    },
    addNewbulkItem() {
      let param = { workorderItems: [] }
      let obj = {}
      let assetIds = []
      this.inventoryList.forEach(item => {
        obj = {}
        assetIds = []
        if (item.itemType.isRotating) {
          if (item.invidualList.length) {
            assetIds = item.invidualList.map(rl => rl.id)
            obj = {
              parentId: this.workorder.id,
              item: { id: item.id },
              quantity: item.invidualList.length,
              assetIds: assetIds,
            }
            param.workorderItems.push(obj)
          }
        } else {
          if (item.addedQuantity > 0) {
            obj = {
              parentId: this.workorder.id,
              item: { id: item.id },
              quantity: item.addedQuantity,
              assetIds: assetIds,
            }
            param.workorderItems.push(obj)
          }
        }
      })
      this.selectedInventory = null
      this.individualItemList = []
      this.individualToolList = []
      this.newItemPartToggle = false
      let self = this
      this.$http
        .post('/v2/workorderItems/addOrUpdate', param)
        .then(response => {
          if (response.data.responseCode !== 0) {
            self.$message.error(response.data.message)
          } else {
            self.$message.success(
              this.$t('common.header.item_added_successfully')
            )
            self.actionToWorkorderStatusChange()
            self.loadWoItemParts(false, true)
          }
        })
        .catch(error => {
          console.log(error)
        })
    },
    addNewbulkTool() {
      let self = this
      let param = { workorderToolsList: [] }
      let obj = {}
      let assetIds = []
      this.toolsList.forEach(item => {
        obj = {}
        assetIds = []
        if (item.toolType.isRotating) {
          if (item.invidualList.length) {
            assetIds = item.invidualList.map(rl => rl.id)
            obj = {
              parentId: this.workorder.id,
              tool: { id: item.id },
              quantity: item.invidualList.length,
              assetIds: assetIds,
            }
            param.workorderToolsList.push(obj)
          }
        } else {
          if (item.addedQuantity > 0) {
            obj = {
              parentId: this.workorder.id,
              tool: { id: item.id },
              quantity: item.addedQuantity,
              assetIds: assetIds,
            }
            param.workorderToolsList.push(obj)
          }
        }
      })
      this.selectedTool = null
      this.individualItemList = []
      this.individualToolList = []
      this.newToolPartToggle = false
      this.$http
        .post('/v2/workorderTools/addOrUpdate', param)
        .then(response => {
          if (response.data.responseCode !== 0) {
            self.$message.error(response.data.message)
          } else {
            self.$message.success(
              this.$t('common.products.tools_added_successfully')
            )
            self.selectedTool = null
            self.actionToWorkorderStatusChange()
            self.loadWorkOrderToolParts(false, true)
            self.getWorkOrderCostList()
          }
        })
        .catch(() => {})
    },
    actionToWorkorderStatusChange() {
      this.fetchWo()
    },
    cancelItemPartsDialog() {
      this.newItemPartToggle = false
      this.selectedInventory = null
      this.selectedTool = null
    },
    async loadItem() {
      this.items = await getFilteredItemListWithParentModule('workorder')
    },
    async loadStockedTools() {
      this.tools = await getFilteredToolListWithParentModule('workorder')
    },
    changeWorkLabourField(workLabour, index, changedfield) {
      let self = this
      let duration = Number(workLabour.duration).toFixed(2)
      let param = { workorderLabourList: [] }
      if (workLabour.startTime === -1) {
        workLabour.startTime = null
      }
      if (workLabour.endTime === -1) {
        workLabour.endTime = null
      }
      let startTime =
        workLabour.startTime !== null
          ? moment(workLabour.startTime)
              .tz(this.$timezone)
              .valueOf()
          : workLabour.startTime
      let endTime =
        workLabour.endTime !== null
          ? moment(workLabour.endTime)
              .tz(this.$timezone)
              .valueOf()
          : workLabour.endTime

      let temp = {
        id: workLabour.id,
        parentId: this.workorder.id,
        duration: changedfield === 3 || changedfield === 1 ? duration : -1,
        labour: workLabour.labour,
        startTime: startTime ? startTime : -99,
        endTime: changedfield === 2 ? endTime : -99,
      }
      param.workorderLabourList.push(temp)
      this.$http
        .post('/v2/workorderLabour/addOrUpdate', param)
        .then(response => {
          self.actionToWorkorderStatusChange()
          if (response.data.responseCode !== 0) {
            self.$message.error(response.data.message)
          } else {
            self.$message.success(
              this.$t('common._common.updated_successfully')
            )
            self.loadWorkOrderLabour()
          }
        })
        .catch(() => {})
    },
    changeWorkToolField(workTool, index, durationHour, changedField) {
      if (
        Number(workTool.quantity) <=
        Number(workTool.tempQuantity) + Number(workTool.tempTool.quantity)
      ) {
        let param = { workorderToolsList: [] }
        if (workTool.issueTime === -1) {
          workTool.issueTime = null
        }
        if (workTool.returnTime === -1) {
          workTool.returnTime = null
        }
        let issueTime =
          workTool.issueTime !== null
            ? moment(workTool.issueTime)
                .tz(this.$timezone)
                .valueOf()
            : workTool.issueTime
        let returnTime =
          workTool.returnTime !== null
            ? moment(workTool.returnTime)
                .tz(this.$timezone)
                .valueOf()
            : workTool.returnTime
        let temp = {}
        let duration = Number(workTool.duration)
        if (durationHour) {
          duration = Number(workTool.durationInHour).toFixed(2)
          if (issueTime !== null && issueTime > 0 && duration > 0) {
            returnTime = Number(issueTime) + Number(duration)
          }
        } else {
          duration = Number(duration).toFixed(2)
        }
        if (issueTime === 0 || issueTime === null) {
          issueTime = -1
        }
        if (returnTime === 0 || returnTime === null) {
          returnTime = -1
        }
        this.workorderToolsList[index].duration = duration
        temp = {
          parentId: this.workorder.id,
          id: workTool.id,
          tool: { id: workTool.tool.id },
          quantity: parseInt(workTool.quantity),
          duration: changedField == 1 || changedField == 3 ? duration : -1,
          issueTime: issueTime ? issueTime : -99,
          returnTime: changedField === 2 ? returnTime : -99,
          requestedLineItem: workTool.requestedLineItem,
          parentTransactionId: workTool.parentTransactionId,
        }

        param.workorderToolsList.push(temp)
        let self = this
        this.$http
          .post('/v2/workorderTools/addOrUpdate', param)
          .then(response => {
            if (response.data.responseCode === 0) {
              self.$message.success(
                this.$t('common._common.edited_successfully')
              )
              self.actionToWorkorderStatusChange()
              self.loadWorkOrderToolParts()
              self.getWorkOrderCostList()
            } else {
              self.$message.error(response.data.message)
            }
          })
      } else {
        this.$message.error(this.$t('common.dashboard.quantity_should_be_less'))
      }
    },
    changeWorkItemQuantity(workItem) {
      if (
        workItem.quantity <=
        workItem.tempQuantity + workItem.tempItem.quantity
      ) {
        let param = { workorderItems: [] }
        let temp = {
          parentId: this.workorder.id,
          id: workItem.id,
          item: { id: workItem.item.id },
          quantity: workItem.quantity,
          requestedLineItem: workItem.requestedLineItem,
          parentTransactionId: workItem.parentTransactionId,
          remainingQuantity: workItem.remainingQuantity + workItem.tempQuantity,
        }
        param.workorderItems.push(temp)
        let self = this
        this.$http
          .post('/v2/workorderItems/addOrUpdate', param)
          .then(response => {
            if (response.data.responseCode === 0) {
              self.loadWoItemParts()
              self.actionToWorkorderStatusChange()
              self.$message.success(
                this.$t('common._common.updated_successfully')
              )
            } else {
              self.$message.error(response.data.message)
            }
          })
      } else {
        this.$message.error(this.$t('common.dashboard.quantity_should_be_less'))
      }
    },
    loadWorkOrderToolParts(loading, inventory) {
      if (loading) {
        this.toolLoading = true
      }
      let self = this
      this.$http
        .get('/v2/workorderToolsList/parent/' + this.workorder.id)
        .then(response => {
          let wktool = {}
          this.workorderToolsList = response.data.result.workorderTools
          for (let workTool in response.data.result.workorderTools) {
            this.workorderToolsList[
              workTool
            ].durationInHour = this.workorderToolsList[
              workTool
            ].duration.toFixed(2)
            this.workorderToolsList[workTool].issueTime =
              this.workorderToolsList[workTool].issueTime === -1 ||
              this.workorderToolsList[workTool].issueTime === null
                ? null
                : this.workorderToolsList[workTool].issueTime
            this.workorderToolsList[workTool].returnTime =
              this.workorderToolsList[workTool].returnTime === -1 ||
              this.workorderToolsList[workTool].returnTime === null
                ? null
                : this.workorderToolsList[workTool].returnTime
            wktool = response.data.result.workorderTools[workTool]
            wktool.tempQuantity = wktool.quantity
            wktool.tempTool = wktool.tool
          }
          self.workorderToolsList = self.$helpers.cloneObject(
            response.data.result.workorderTools
          )
          if (loading) {
            self.loadToolslist()
          }
          if (inventory) {
            self.loadToolslist()
          }
          this.toolLoading = false
        })
        .catch(error => {
          console.log(error)
          this.$message.error(this.$t('common.wo_report.unable_to_load_tools'))
          this.toolLoading = false
        })
      this.loadStockedTools()
    },
    loadLabour() {
      let self = this
      this.$http
        .get('/v2/labour/labourList')
        .then(response => {
          self.labourList = response.data.result.labours
          for (let i = 0; i < self.labourList.length; i++) {
            let labourItem = self.labourList[i]
            if (self.selectedLabour.includes(labourItem.id)) {
              self.labourList.splice(i, 1)
            }
          }
        })
        .catch(error => {
          console.log(error)
          self.$message.error(this.$t('common.wo_report.unable_to_labor_list'))
        })
    },
    loadWorkOrderLabour(loading) {
      if (loading) {
        this.labourLoading = true
      }
      let self = this
      this.$http
        .get('/v2/workorderLabourList/parent/' + this.workorder.id)
        .then(response => {
          this.workLabourTotalCost = 0
          for (let workLabour in response.data.result.workorderLabour) {
            this.workLabourTotalCost =
              this.workLabourTotalCost +
              response.data.result.workorderLabour[workLabour].cost
          }
          self.workorderLabourList = self.$helpers.cloneObject(
            response.data.result.workorderLabour
          )
          self.selectedLabour = []
          for (let index in self.workorderLabourList) {
            let workLabourItem = self.workorderLabourList[index]
            self.selectedLabour.push(workLabourItem.labour.id)
            workLabourItem.startTime =
              workLabourItem.startTime <= 0 ? null : workLabourItem.startTime
            workLabourItem.endTime =
              workLabourItem.endTime <= 0 ? null : workLabourItem.endTime
            workLabourItem.duration = workLabourItem.duration.toFixed(2)
          }
          self.getWorkOrderCostList()
          self.labourLoading = false
        })
        .catch(error => {
          console.log(error)
          self.$message.error(this.$t('common.wo_report.unable_to_load_labor'))
          self.labourLoading = false
        })
    },
    loadWoItemParts(loading, inventory) {
      this.loadWorkOrderItemParts = []
      let self = this
      let self1 = this
      if (loading) {
        this.itemnsloading = true
      }
      this.$http
        .get('/v2/workorderItemsList/parent/' + this.workorder.id)
        .then(response => {
          let workItem = {}
          for (let item in response.data.result.workorderItem) {
            workItem = response.data.result.workorderItem[item]
            workItem.tempQuantity = workItem.quantity
            workItem.tempItem = workItem.item
          }
          if (loading || inventory) {
            self.loadInventory()
          }

          self1.itemnsloading = false
          self.workorderItemsList = self.$helpers.cloneObject(
            response.data.result.workorderItem
          )
          self1.getWorkOrderCostList()
        })
        .catch(() => {
          self.$message.error(this.$t('common.wo_report.unable_to_load_parts'))
        })
      this.loadItem()
    },
    async loadindividualTrackingList(items) {
      this.individualItemList = []
      this.iTloading = true

      let filters = {
        rotatingItem: {
          operatorId: 36,
          value: items.map(rt => rt.id + ''),
        },
        isUsed: { operatorId: 15, value: ['false'] },
      }
      let individualItemList = await this.$util.getFilteredAssetList(filters)

      this.individualItemList = individualItemList.map(item => ({
        ...item,
        checked: false,
      }))
      this.iTloading = false
    },
    async loadindividualToolTrackingList(items) {
      this.individualToolList = []
      this.iTloading = true

      let filters = {
        rotatingTool: {
          operatorId: 36,
          value: items.map(rt => rt.id + ''),
        },
        isUsed: { operatorId: 15, value: [false + ''] },
      }
      let individualToolList = await this.$util.getFilteredAssetList(filters)

      this.individualToolList = individualToolList.map(item => ({
        ...item,
        checked: false,
      }))
      this.iTloading = false
    },
    async loadInventoryRequest(force = false) {
      this.inventoryRequestForWOLoading = true
      let { workorder } = this
      let { id } = workorder || {}
      let relatedFieldName = getRelatedFieldName(
        'workorder',
        'inventoryrequest'
      )
      let relatedConfig = {
        moduleName: 'workorder',
        id,
        relatedModuleName: 'inventoryrequest',
        relatedFieldName,
      }

      let { error, list } = await API.fetchAllRelatedList(
        relatedConfig,
        {},
        {
          force,
        }
      )

      if (!error) {
        if (!isEmpty(list)) {
          this.inventoryRequestList = list
        }
      } else {
        this.$error.message(error.message || 'Error Occured')
      }

      this.inventoryRequestForWOLoading = false
    },
    async loadWorkOrderService(loading) {
      if (loading) {
        this.loading.woService = true
      }

      const route = `/v2/workorderServiceList/parent/${this.workorder.id}`
      let { data, error } = await API.get(route, null, { force: true })

      if (error) {
        let { message } = error
        this.$message.error(
          message ||
            this.$t('common._common.error_occured_while_fetching_workorder')
        )
      } else {
        let { workorderServices = [] } = data
        workorderServices.forEach(woService => {
          if (isEmpty(woService.endTime)) {
            woService.endTime = null
          }
          if (isEmpty(woService.startTime)) {
            woService.startTime = null
          }
          if (woService.cost <= 0) {
            woService.cost = 0
          }
          if (woService.duration <= 0) {
            woService.duration = 0
          } else if (!isEmpty(woService.duration)) {
            let duration = woService.duration / (60 * 60)
            woService.duration = Number(duration).toFixed(2)
          }
        })
        this.workorderServiceList = workorderServices || []
      }
      this.loading.woService = false
    },
    addNewWOService() {
      this.loadAllServices()
      this.serviceAddDialogVisibility = true
    },
    async loadAllServices() {
      let allWoServiceIds = (this.workorderServiceList || []).map(woService =>
        this.$getProperty(woService, 'service.id', -1)
      )
      this.loading.serviceList = true
      let { data, error } = await API.get('/v2/service/all')
      if (error) {
        let { message } = error
        this.$message.error(
          message ||
            this.$t('common._common.error_occured_while_fetching_services_list')
        )
      } else {
        let { services = [] } = data
        if (!isEmpty(services)) {
          services =
            services.filter(service => !allWoServiceIds.includes(service.id)) ||
            []
          services.forEach(service => {
            this.$setProperty(service, 'checked', false)
          })
        }
        this.allServiceList = services || []
      }
      this.loading.serviceList = false
    },
    closeServiceDialog() {
      this.serviceAddDialogVisibility = false
    },
    async addWoService() {
      let param = { workorderServiceList: [] }
      let { allServiceList = [] } = this
      allServiceList.forEach(service => {
        if (service.checked) {
          let tempObj = {
            parentId: this.workorder.id,
            startTime: -1,
            endTime: -1,
            service,
          }
          param.workorderServiceList.push(tempObj)
        }
      })
      if (!isEmpty(param.workorderServiceList)) {
        this.loading.woServiceSave = true
        let { error } = await API.post(
          '/v2/workorderService/addOrUpdate',
          param
        )
        if (error) {
          this.$message.error(
            error ||
              this.$t(
                'common._common.error_occured_while_adding_workorder_services'
              )
          )
        } else {
          this.$message.success(
            this.$t('common._common.added_service_successfully')
          )
          this.closeServiceDialog()
          this.serviceUpdateActions()
        }
        this.loading.woServiceSave = false
      } else {
        this.closeServiceDialog()
      }
    },
    serviceUpdateActions() {
      this.actionToWorkorderStatusChange()
      this.loadWorkOrderService(true)
      this.getWorkOrderCostList()
    },
    async deleteWoService(woService) {
      let param = { parentId: this.workorder.id, workorderServiceIds: [] }
      param.workorderServiceIds.push(woService.id)
      this.loading.woService = true
      let { error } = await API.post('v2/workorderService/delete', param)
      if (error) {
        let { message } = error
        this.$message.error(
          message ||
            this.$t(
              'common._common.error_occured_while_deleting_workorder_service'
            )
        )
      } else {
        this.$message.success(
          this.$t('common.products.service_deleted_successfully')
        )
        this.serviceUpdateActions()
      }
      this.loading.woService = true
    },
    async changeWorkServiceField(workService, changedfield) {
      let duration = Number(workService.duration).toFixed(2)
      let param = { workorderServiceList: [] }
      if (isEmpty(workService.startTime)) {
        workService.startTime = null
      }
      if (isEmpty(workService.endTime)) {
        workService.endTime = null
      }
      let startTime =
        workService.startTime !== null
          ? moment(workService.startTime)
              .tz(this.$timezone)
              .valueOf()
          : workService.startTime
      let endTime =
        workService.endTime !== null
          ? moment(workService.endTime)
              .tz(this.$timezone)
              .valueOf()
          : workService.endTime

      let temp = {
        id: workService.id,
        parentId: this.workorder.id,
        duration: changedfield === 3 || changedfield === 1 ? duration : -1,
        service: workService.service,
        startTime: startTime ? startTime : -99,
        endTime: changedfield === 2 ? endTime : -99,
        quantity: workService.quantity,
      }
      param.workorderServiceList.push(temp)
      let { error } = await API.post('/v2/workorderService/addOrUpdate', param)
      if (error) {
        let { message } = error
        this.$message.error(
          message ||
            this.$t(
              'common._common.error_occured_while_updating_workorder_service'
            )
        )
      } else {
        this.$message.success(this.$t('common._common.updated_successfully'))
        this.loadWorkOrderService(true)
        this.getWorkOrderCostList()
      }
    },
  },
}
</script>
<style>
.item-add {
  padding-top: 20px;
  padding-bottom: 20px;
}
.inventory-tool-table .in-Quantity .el-input--prefix input.el-input__inner {
  padding-left: 0 !important;
  padding-right: 0;
}
.inventory-tool-table .in-Quantity .el-input--prefix .el-input__prefix {
  display: none;
}
.inventory-table thead > tr {
  height: 55px;
  border-top: 1px solid #eceef1;
  border-bottom: 1px solid #eceef1;
}
.inventory-table th {
  white-space: nowrap;
}
.inventory-table.pB20.pT20.tbody.tr:hover .el-input__inner {
  border-color: #d0d9e2 !important;
}
.inv-name {
  height: 40px;
  line-height: 40px;
  padding-left: 15px;
  padding-right: 15px;
  border-radius: 3px;
  background-color: #ffffff;
  border: solid 1px #d0d9e2 !important;
  font-size: 14px;
  font-weight: normal;
  letter-spacing: 0.4px;
  color: #333333;
  text-overflow: ellipsis;
  font-weight: 400;
  padding-right: 30px;
  white-space: nowrap;
}
.inventory-table tbody td .p5 {
  padding: 6px;
  padding-left: 15px;
}
.in-Quantity .item {
  margin: 0px;
}
.inv-item:hover {
  background: #fafbfc;
}
.inv-id-bac-icon {
  position: relative;
  right: 1px;
  margin-right: 10px;
  font-weight: bold;
  color: #324056;
}
.in-no-data {
  height: 100px;
  width: 100%;
  text-align: center;
  justify-content: center;
  display: flex;
  align-items: center;
}
.new-in-header {
  font-size: 16px;
  letter-spacing: 0.7px;
  color: #324056;
  padding-bottom: 20px;
  padding-top: 30px;
}
.search-bar .el-icon-search {
  font-size: 14px;
  color: #50506c;
  font-weight: bold;
}
.inv-search-grey .el-icon-search {
  margin-right: 0;
  color: #50506c;
  font-weight: normal;
  color: #d0d9e2;
}
.total-amount {
  font-size: 24px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: right;
  color: #324056;
}
.invent-table-dialog tbody tr.tablerow.active1 td:first-child {
  border-left: 3px solid #39b2c2 !important;
}
.quant .el-input__inner {
  background: transparent !important;
  padding-right: 0;
  width: 80px;
  text-align: center;
  padding: 0;
}
.inv-icon {
  left: 11px;
  position: relative;
  top: 9px;
}
.additionalCostAdd {
  font-size: 15px;
  color: #00b395;
  font-weight: 900;
  position: absolute;
  right: 5px;
  top: 10px;
  cursor: pointer;
}
.additionalCostDelete {
  font-size: 15px;
  color: #e1573f;
  font-weight: 900;
  position: absolute;
  right: 5px;
  top: 0px;
  cursor: pointer;
}
.additionalCostEnter input.el-input__inner {
  text-align: right;
  padding-right: 7px;
}
.overallCost {
  padding: 20px 30px;
  padding-top: 0;
}
.inv-delet-icon {
  color: #e1573f;
  font-size: 14px;
}
.workItem-quantity {
  width: 80px;
  text-align: center;
  height: 40px;
  align-items: center;
  padding-top: 10px;
  cursor: no-drop;
}
.in-Quantity .el-form-item {
  margin: 0px;
}
.inventory-table table > tbody tr:last-child {
  border-bottom: 1px solid #eceef1 !important;
}
.fc-inv-container-body {
  height: 50vh;
  overflow: auto;
}
.invent-table-dialog .fc-setting-table-th setting-th-text {
  padding: 15px 30px;
}
.exceeded .el-input__inner,
.exceeded .el-input__inner:focus {
  border-color: #f56c6c !important;
}
.search-bar {
  width: 40%;
  justify-content: right;
  align-items: right;
  text-align: right;
  position: absolute;
  right: 28px;
  top: 10px;
  overflow: hidden;
  height: 40px;
}
.search-bar.active {
  width: 10%;
}
.popup-container-new {
  width: 100%;
  padding: 18px 30px 18px;
  border-bottom: 1px solid #edeeef;
  position: -webkit-sticky;
  position: sticky;
  background: #fff;
  display: flex;
  width: 100%;
  align-items: center;
}
.fc-setup-modal-title2 {
  font-size: 14px;
  font-weight: bold;
  letter-spacing: 0.3px;
  letter-spacing: 0.9px;
  color: #333333;
  text-transform: uppercase;
  width: 40%;
}
.store-room-bar {
  position: absolute;
  right: 60px !important;
}
.store-room-bar.active {
  position: absolute;
  left: 0;
}
.store-room-bar .el-input .el-input__inner {
  border: 0px;
}
/* .store-room-bar .el-input__suffix {
  right: -8px;
}
.store-room-bar .el-input__suffix {
  top: 7px;
} */
.worktool-cost-edit-sticky {
  background-color: #ffffff;
  position: sticky;
  z-index: 500;
  right: 0;
  animation: slide-down 0.7s;
  opacity: 1;
}
.worktool-cost-sticky {
  background-color: #ffffff;
  position: sticky;
  right: 35px;
  z-index: 500;
  padding-left: 10px;
  padding-right: 10px;
  animation: slide-down 0.7s;
  opacity: 1;
  box-shadow: -10px 0 34px 0 rgba(24, 19, 59, 0.05);
  -webkit-box-shadow: -10px 0 34px 0 rgba(24, 19, 59, 0.05);
  -moz-box-shadow: -10px 0 34px 0 rgba(24, 19, 59, 0.05);
}
.inventory-table-body-border-none table > tbody tr:last-child {
  border-bottom: none !important;
}
.secondary-quantity-text {
  font-size: 12px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.55px;
  color: #92959a;
  padding-top: 2px;
  align-items: center;
}
.text-align-inventory {
  text-align: center;
}
.inventory-table-body-border-none th:last-child {
  border-right: none !important;
}
@keyframes slide-down {
  0% {
    opacity: 1;
    transform: translateY(-100%);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
<style scoped>
.fc-create-btn.disabled:hover {
  box-shadow: 0 2px 4px 0 #ffc9de;
}

.v3-layout-override {
  flex-direction: row !important;
}
</style>
