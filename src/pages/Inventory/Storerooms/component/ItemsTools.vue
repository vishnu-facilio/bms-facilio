<template>
  <div>
    <div class="fc__white__bg__p0" style="height:100%">
      <div v-if="$hasPermission('inventory:UPDATE,UPDATE_OWN')">
        <div
          class="pull-right self-center table-btn mR100"
          v-if="LastName === 'item'"
        >
          <el-button
            icon="el-icon-edit"
            v-if="this.items.length != 0"
            @click="adjustBulkItemForm"
            class="el-button f12 fc-sites-btn el-button--text sh-button sh-button-add button-add  sp-sh-btn-sm"
            type="text"
            :title="$t('common._common.bulk_adjust_quantity')"
            v-tippy
            data-size="small"
          ></el-button>
        </div>
      </div>
      <div v-if="$hasPermission('inventory:CREATE')">
        <div
          class="pull-right self-center table-btn"
          v-if="LastName === 'item'"
        >
          <el-button
            icon="el-icon-plus"
            @click="addItemForm"
            class="el-button f12 fc-sites-btn el-button--text sh-button sh-button-add button-add  sp-sh-btn-sm"
            type="text"
            :title="$t('common.header.add_item')"
            v-tippy
            data-size="small"
          ></el-button>
        </div>
        <div
          class="pull-right self-center table-btn mR50"
          v-if="LastName === 'item'"
        >
          <el-button
            icon="el-icon-goods"
            @click="addBulkItemForm"
            class="el-button f12 fc-sites-btn el-button--text sh-button sh-button-add button-add  sp-sh-btn-sm"
            type="text"
            :title="$t('common._common.bulk_add_item')"
            v-tippy
            data-size="small"
          ></el-button>
        </div>
        <div
          class="pull-right self-center table-btn"
          v-if="LastName === 'tool'"
        >
          <el-button
            icon="el-icon-plus"
            @click="addToolForm"
            class="el-button f12 fc-sites-btn el-button--text sh-button sh-button-add button-add  sp-sh-btn-sm"
            type="text"
            :title="$t('common.products.add_tool')"
            v-tippy
            data-size="small"
          ></el-button>
        </div>
        <div
          class="pull-right self-center table-btn mR50"
          v-if="LastName === 'tool'"
        >
          <el-button
            icon="el-icon-goods"
            @click="addBulkToolForm"
            class="el-button f12 fc-sites-btn el-button--text sh-button sh-button-add button-add  sp-sh-btn-sm"
            type="text"
            :title="$t('common._common.bulk_add_tool')"
            v-tippy
            data-size="small"
          ></el-button>
        </div>
      </div>
      <el-tabs v-model="LastName" class="clearboth">
        <el-tab-pane :label="$t('common._common.items')" name="item">
          <InventoryRelatedListWidget
            :widget="widget"
            :details="details"
            moduleName="item"
            :staticWidgetHeight="'500'"
          ></InventoryRelatedListWidget>
        </el-tab-pane>
        <el-tab-pane :label="$t('common._common.tools')" name="tool">
          <InventoryRelatedListWidget
            :widget="widgetTool"
            :details="details"
            moduleName="tool"
            :staticWidgetHeight="'500'"
          ></InventoryRelatedListWidget>
        </el-tab-pane>
      </el-tabs>
    </div>
    <template v-if="newAddItemFormVisibility && selecteditemType">
      <stock-item
        :visibility.sync="newAddItemFormVisibility"
        :record="itemTypeEditObj"
        moduleName="storeRoom"
        @saved="loadStoreRoomDetails"
      ></stock-item>
    </template>
    <template
      v-if="
        newAddToolFormVisibility && !toolTypeFormChooser && selectedtoolType
      "
    >
      <stock-tool
        :visibility.sync="newAddToolFormVisibility"
        :record="toolTypeEditObj"
        moduleName="storeRoom"
        @saved="loadStoreRoomDetails"
      ></stock-tool>
    </template>
    <template v-if="itemTypeFormChooser">
      <el-dialog
        :visible.sync="itemTypeFormChooser"
        :fullscreen="false"
        open="top"
        width="30%"
        :title="$t('common.products.select_item_type')"
        custom-class="assetaddvaluedialog fc-dialog-center-container inventory-dialog fc-web-form-dialog"
      >
        <div class="inventory-select-item-con">
          <FLookupFieldWrapper
            v-model="selecteditemTypeid"
            :field="lineItemLookupData('itemTypes')"
            @recordSelected="changeItemTypeinForm(selecteditemTypeid)"
            class="width100"
          ></FLookupFieldWrapper>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelForm()">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="selectItemTypeForForm()"
            >{{ $t('common._common.ok') }}</el-button
          >
        </div>
      </el-dialog>
    </template>
    <template v-if="toolTypeFormChooser">
      <el-dialog
        :visible.sync="toolTypeFormChooser"
        :fullscreen="false"
        :title="$t('common.products.select_tool_type')"
        open="top"
        width="30%"
        custom-class="assetaddvaluedialog inventory-dialog fc-web-form-dialog"
      >
        <div class="inventory-select-item-con">
          <FLookupFieldWrapper
            v-model="selectedtoolTypeid"
            :field="lineItemLookupData('toolTypes')"
            @recordSelected="changeToolTypeinForm(selectedtoolTypeid)"
            class="width100"
          ></FLookupFieldWrapper>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelForm()">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="selectToolTypeForForm()"
            >{{ $t('common._common.ok') }}</el-button
          >
        </div>
      </el-dialog>
    </template>
    <template v-if="itemBulkAddForm">
      <item-bulk-add
        :storeRoom="storeRoom"
        :itemList="items"
        @refresh="loadStoreRoomDetails"
        :itemBulkAddForm.sync="itemBulkAddForm"
      ></item-bulk-add>
    </template>
    <template v-if="itemBulkAdjust">
      <item-bulk-adjust
        :storeRoom="storeRoom"
        :itemList="items"
        @refresh="loadStoreRoomDetails"
        :itemBulkAdjustForm.sync="itemBulkAdjust"
      ></item-bulk-adjust>
    </template>
    <template v-if="toolBulkAddForm">
      <tool-bulk-add
        :storeRoom="storeRoom"
        :toolList="tools"
        @refresh="loadStoreRoomDetails"
        :toolBulkAddForm.sync="toolBulkAddForm"
      ></tool-bulk-add>
    </template>
    <template v-if="showItemIssueReturn">
      <item-issue-return
        :storeRoom="storeRoom"
        :showItemIssueReturn.sync="showItemIssueReturn"
        :type="issueReturnType"
        @refreshInventory="loadItemforStoreRoom()"
      ></item-issue-return>
    </template>
    <template v-if="showToolIssueReturn">
      <tool-issue-return
        :storeRoom="storeRoom"
        :type="issueReturnType"
        :showToolIssueReturn.sync="showToolIssueReturn"
        @refreshInventory="loadToolforStoreRoom()"
      ></tool-issue-return>
    </template>
    <template v-if="showStoreIssueDialog">
      <inventory-store-issue
        :storeRoom="storeRoom"
        :visibility.sync="showStoreIssueDialog"
        :itemList="items"
        :toolList="tools"
        @refreshInventory="refreshItemAndTool"
      ></inventory-store-issue>
    </template>
  </div>
</template>
<script>
import ToolBulkAdd from '../ToolBulkAdd'
import ItemBulkAdd from '../ItemBulkAdd'
import ItemBulkAdjust from '../ItemBulkAdjust'
import ItemIssueReturn from '../ItemStoreIssueReturn'
import ToolIssueReturn from '../ToolStoreIssueReturn'
import InventoryStoreIssue from '../InventoryStoreIssue'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import StockItem from 'src/pages/Inventory/Items/components/StockItem'
import StockTool from 'src/pages/Inventory/Tools/components/StockTool'
import InventoryRelatedListWidget from './InventoryRelatedListWidget'
import { eventBus } from '@/page/widget/utils/eventBus'
import {
  getFilteredItemTypeList,
  getFilteredToolTypeList,
  getFilteredItemList,
  getFilteredToolList,
} from 'pages/Inventory/InventoryUtil'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['viewname', 'details'],
  components: {
    ToolBulkAdd,
    ItemBulkAdd,
    ItemIssueReturn,
    ToolIssueReturn,
    InventoryStoreIssue,
    ItemBulkAdjust,
    FLookupFieldWrapper,
    StockItem,
    StockTool,
    InventoryRelatedListWidget,
  },
  data() {
    return {
      LastName: 'item',
      loading: true,
      page: 1,
      fetchingMore: false,
      storeRoom: {},
      storeRoomEditObj: null,
      storeRoomEditFormVisibility: false,
      saving: false,
      emitForm: false,
      resetForm: false,
      newAddToolFormVisibility: false,
      newAddItemFormVisibility: false,
      itemTypeEditObj: null,
      toolTypeEditObj: null,
      toolTypeFormChooser: false,
      itemTypeFormChooser: false,
      selecteditemTypeid: null,
      selectedtoolTypeid: null,
      selecteditemType: null,
      selectedtoolType: null,
      itemBulkAddForm: false,
      itemBulkAdjust: false,
      toolBulkAddForm: false,
      itemLoading: true,
      toolLoading: true,
      showItemIssueReturn: false,
      showToolIssueReturn: false,
      issueReturnType: null,
      showStoreIssueDialog: false,
      items: [],
      itemTypes: [],
      tools: [],
      toolTypes: [],
      widget: {
        relatedList: {
          field: {
            name: 'storeRoom',
          },
          module: {
            name: 'item',
            type: 1,
            typeEnum: 'BASE_ENTITY',
          },
        },
      },
    }
  },
  created() {
    this.$store.dispatch('loadSites')
    this.$store.dispatch('view/loadModuleMeta', 'storeRoom')
    this.storeRoom = this.details
  },
  computed: {
    widgetTool() {
      let { widget } = this
      let toolsWidget = JSON.parse(JSON.stringify(widget))
      toolsWidget.relatedList.module.name = 'tool'
      return toolsWidget
    },
    currentStoreRoomId() {
      return parseInt(this.$route.params.id)
    },
    currentView() {
      return this.$route.params.viewname
    },
    currentViewDetail() {
      if (this.$route.query.search) {
        return {
          displayName: 'Filtered Storerooms',
          name: 'filteredStorerooms',
        }
      }
      return this.$store.state.view.currentViewDetail || {}
    },
    views() {
      return this.$store.state.view.views
    },
  },
  watch: {
    currentStoreRoomId: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.loadStoreRoomDetails()
        }
      },
      immediate: true,
    },
  },
  methods: {
    refreshList() {
      eventBus.$emit('refresh-related-list')
    },
    lineItemLookupData(type) {
      return {
        lookupModule: { name: type },
        multiple: false,
      }
    },
    async loadStoreRoomDetails() {
      if (!this.currentStoreRoomId) return

      let { error, data } = await API.get(
        '/v3/modules/data/summary?id=' +
          this.currentStoreRoomId +
          '&moduleName=storeRoom'
      )

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let { storeRoom } = data || {}
        let { id, name } = storeRoom || {}
        let title = `[#${id}] ${name}`

        this.setTitle(title)
        this.storeRoom = storeRoom
        this.pageLoading = false
        this.loadItemforStoreRoom()
        this.loadToolforStoreRoom()
      }
      await this.loadItemforStoreRoom()
      await this.loadToolforStoreRoom()
      this.refreshList()
    },
    async loadItemforStoreRoom() {
      let filters = {
        storeRoom: {
          operatorId: 36,
          value: [this.currentStoreRoomId + ''],
        },
      }
      this.itemLoading = true
      this.items = await getFilteredItemList(filters)
      this.itemLoading = false
      this.fetchingMore = false
    },
    async loadToolforStoreRoom() {
      let filters = {
        storeRoom: {
          operatorId: 36,
          value: [this.currentStoreRoomId + ''],
        },
      }

      this.toolLoading = true
      this.tools = await getFilteredToolList(filters)
      this.toolLoading = false
      this.fetchingMore = false
    },
    cancelForm() {
      this.resetForm = true
      this.storeRoomEditFormVisibility = false
      this.newAddItemFormVisibility = false
      this.newAddToolFormVisibility = false
      this.itemTypeFormChooser = false
      this.toolTypeFormChooser = false
    },
    saveForm() {
      this.emitForm = true
    },

    getDateTime(val) {
      let value = val.lastPurchasedDate
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value, true)
    },
    addItemForm() {
      this.itemTypeFormChooser = true
      this.loadItemTypes()
    },
    addToolForm() {
      this.toolTypeFormChooser = true
      this.loadToolTypes()
    },
    selectItemTypeForForm() {
      this.itemTypeFormChooser = false
      this.newAddItemFormVisibility = true
      this.itemTypeEditObj = {
        itemType: {
          id: this.selecteditemType.id,
          isRotating: this.selecteditemType.isRotating,
        },
        storeRoom: { id: this.currentStoreRoomId },
      }
    },
    selectToolTypeForForm() {
      this.toolTypeFormChooser = false
      this.newAddToolFormVisibility = true
      this.toolTypeEditObj = {
        toolType: {
          id: this.selectedtoolType.id,
          isRotating: this.selectedtoolType.isRotating,
        },
        storeRoom: { id: this.currentStoreRoomId },
      }
    },
    async loadToolTypes() {
      this.loading = true
      this.toolTypes = await getFilteredToolTypeList()
      this.loading = false
      this.fetchingMore = false
    },
    async loadItemTypes() {
      this.loading = true
      this.itemTypes = await getFilteredItemTypeList()
      this.loading = false
      this.fetchingMore = false
    },
    changeItemTypeinForm(itemTypeid) {
      if (itemTypeid) {
        this.selecteditemType = this.itemTypes.find(it => it.id === itemTypeid)
      }
    },
    changeToolTypeinForm(toolTypeid) {
      if (toolTypeid) {
        this.selectedtoolType = this.toolTypes.find(t => t.id === toolTypeid)
      }
    },
    openItemTypeOverview(row, col) {
      if (col.label !== 'ITEM NAME') {
        return
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('item', pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id: row.id,
              viewname: 'all',
            },
          })
      } else {
        this.$router.push({
          path: '/app/inventory/item/' + 'all/' + row.id + '/summary',
        })
      }
    },
    openToolTypeOverview(row, col) {
      if (col.label !== 'TOOL NAME') {
        return
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('tool', pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id: row.id,
              viewname: 'all',
            },
          })
      } else {
        this.$router.push({
          path: '/app/inventory/tool/' + 'all/' + row.id + '/summary',
        })
      }
    },
    addBulkItemForm() {
      this.itemBulkAddForm = true
    },
    adjustBulkItemForm() {
      this.itemBulkAdjust = true
    },
    addBulkToolForm() {
      this.toolBulkAddForm = true
    },
    tableQuantityFormatter(val, prop) {
      if (isEmpty(val[prop.property])) {
        return 0
      } else {
        return val[prop.property]
      }
    },
    cellStyle({ row, rowIndex, columnIndex }) {
      row = rowIndex
      rowIndex = row
      if (columnIndex == 1 || columnIndex == 2 || columnIndex == 4) {
        return 'padding-left: 150px'
      }
      if (columnIndex == 3) {
        return 'padding-left:35px'
      }
    },
  },
}
</script>
