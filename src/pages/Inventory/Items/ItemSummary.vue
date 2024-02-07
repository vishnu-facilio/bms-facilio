<template>
  <div class="custom-module-overview">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header p20">
        <div class="custom-module-details">
          <div class="d-flex flex-middle align-center">
            <div @click="showAvatarPreview()">
              <item-avatar
                :name="false"
                size="xlg"
                module="item"
                :recordData="record.itemType"
              ></item-avatar>
            </div>
            <div class="mL10">
              <div class="custom-module-id pB10">
                <i
                  v-if="$account.portalInfo"
                  class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                  content="back"
                  arrow
                  v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                  @click="back"
                ></i>
                #{{ record && record.id }}
              </div>
              <div class="custom-module-name d-flex max-width300px">
                <el-tooltip
                  placement="bottom"
                  effect="dark"
                  :content="$getProperty(record, 'itemType.name')"
                >
                  <span class="whitespace-pre-wrap custom-header">{{
                    $getProperty(record, 'itemType.name')
                  }}</span>
                </el-tooltip>
              </div>
            </div>
          </div>
        </div>
        <div class="marginL-auto flex-middle">
          <CustomButton
            class="pR10"
            :record="record"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="refreshData()"
            @onError="() => {}"
          />
          <el-button
            class="fc__add__btn mR15"
            v-if="$hasPermission('inventory:CREATE') && !rotating"
            @click="openStockItem()"
            >{{ $t('common.header.stock_item') }}</el-button
          >
          <el-dropdown
            v-if="$hasPermission('inventory:UPDATE,UPDATE_OWN')"
            class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
            trigger="click"
            @command="action => summaryDropDownAction(action)"
          >
            <span class="el-dropdown-link">
              <inline-svg
                src="svgs/menu"
                class="vertical-middle"
                iconClass="icon icon-md"
              >
              </inline-svg>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item :key="1" command="edit">{{
                $t('common._common.edit')
              }}</el-dropdown-item>
              <el-dropdown-item :key="2" command="go_to_itemType">{{
                $t('common._common.go_to_itemType')
              }}</el-dropdown-item>
              <el-dropdown-item
                v-if="canIssueItem(record)"
                :key="3"
                command="issue_item"
                >{{ $t('common.products.issue_item') }}</el-dropdown-item
              >
              <el-dropdown-item
                v-if="canReturnItem(record)"
                :key="4"
                command="return_item"
                >{{ $t('common._common.return_item') }}</el-dropdown-item
              >
              <el-dropdown-item
                v-if="!rotating"
                :key="5"
                command="adjust_balance"
                >{{ $t('common._common.adjust_balance') }}</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <Page
        :key="record.id"
        :module="moduleName"
        :id="record.id"
        :details="record"
        :primaryFields="primaryFields"
        :notesModuleName="notesModuleName"
        :isV3Api="true"
        :attachmentsModuleName="attachmentsModuleName"
        :showDescriptionTitle="true"
      ></Page>
    </template>
    <el-dialog
      v-if="photoUrl"
      :visible.sync="showPreviewImage"
      width="60%"
      :append-to-body="true"
      style="z-index: 9999999999;"
    >
      <img style="width:100%" :src="photoUrl" />
    </el-dialog>
    <item-issue-return
      v-if="showItemIssueReturn"
      :item="record"
      :itemType="record.itemType"
      :showItemIssueReturn.sync="showItemIssueReturn"
      :type="issueReturnType"
      @refresh="refreshItem"
      :key="record.id"
    ></item-issue-return>
    <update-item-form
      :visibility.sync="newFormVisibility"
      :itemData="itemEditObj"
      :editId="editId"
      v-if="newFormVisibility"
      @saved="loadRecord(true)"
    ></update-item-form>
    <stock-item
      :visibility.sync="stockItemVisibility"
      :record="record"
      :moduleName="moduleName"
      @saved="loadRecord(true)"
    ></stock-item>
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import { API } from '@facilio/api'
import ItemAvatar from '@/avatar/ItemTool'
import ItemIssueReturn from './ItemIssueReturn'
import UpdateItemForm from './UpdateItemForm'
import StockItem from './components/StockItem'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: CustomModuleSummary,
  props: ['viewname'],
  mixins: [FetchViewsMixin],
  components: {
    ItemAvatar,
    ItemIssueReturn,
    UpdateItemForm,
    StockItem,
  },
  data() {
    return {
      notesModuleName: 'itemnotes',
      attachmentsModuleName: 'itemattachments',
      primaryFields: [
        'localId',
        'category',
        'lastPurchasedDate',
        'lastPurchasedPrice',
        'reservedQuantity',
        'storeRoom',
        'quantity',
      ],
      newFormVisibility: false,
      itemEditObj: false,
      emitForm: false,
      resetForm: false,
      saving: false,
      showPreviewImage: false,
      showItemIssueReturn: false,
      refreshItemTransactions: false,
      refreshItemStoreRoom: false,
      editId: null,
      stockItemVisibility: false,
    }
  },
  computed: {
    moduleName() {
      return 'item'
    },
    rotating() {
      return this.$getProperty(this, 'record.itemType.isRotating')
    },
    photoUrl() {
      return this.$getProperty(this, 'record.itemType.photoUrl', null)
    },
  },
  title() {
    'Item'
  },
  methods: {
    openStockItem() {
      this.stockItemVisibility = true
    },
    editData(data) {
      this.newFormVisibility = true
      this.itemEditObj = data
      this.editId = data.id
    },
    refreshItem() {
      this.refreshItemTransactions = true
      this.refreshItemStoreRoom = true
      this.loadRecord(true)
    },
    openIssueDialog() {
      this.showItemIssueReturn = true
      this.issueReturnType = 'issue'
    },
    openReturnDialog() {
      this.showItemIssueReturn = true
      this.issueReturnType = 'return'
    },
    openAdjustmentDialog() {
      this.showItemIssueReturn = true
      this.issueReturnType = 'adjustment'
    },
    summaryDropDownAction(action) {
      if (action === 'adjust_balance') {
        this.openAdjustmentDialog()
      } else if (action === 'issue_item') {
        this.openIssueDialog()
      } else if (action === 'return_item') {
        this.openReturnDialog()
      } else if (action === 'edit') {
        let { record } = this
        this.editData(record)
      } else if (action === 'go_to_itemType') {
        this.redirectToItemTypeSummary(
          this.$getProperty(this.record, 'itemType.id')
        )
      }
    },
    async redirectToItemTypeSummary(id) {
      let moduleName = 'itemTypes'
      let currentView = await this.fetchView(moduleName)

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
              id,
            },
          })
      } else {
        this.$router.push({
          name: 'itemtypeSummary',
          params: {
            viewname: currentView,
            id,
          },
        })
      }
    },
    showAvatarPreview() {
      if (this.photoUrl) {
        this.showPreviewImage = true
      }
    },
    cancelForm() {
      this.resetForm = true
      if (this.newFormVisibility) {
        this.newFormVisibility = false
      }
    },
    canIssueItem(record) {
      let { itemType, storeRoom } = record || {}
      let { consumable, rotating, approvalNeeded } = itemType || {}
      let { approvalNeeded: storeRoomApproval } = storeRoom || {}

      return !(approvalNeeded || storeRoomApproval) && consumable && !rotating
    },
    canReturnItem(record) {
      let { itemType } = record || {}
      let { consumable, rotating } = itemType || {}
      return consumable && !rotating
    },
    async submitForm(data) {
      let { record } = this
      let { purchasedItems } = data
      let storeRoomId = this.$getProperty(data, 'storeRoom.id')
      if (this.newFormVisibility) {
        this.newFormVisibility = false
        let param
        if (!record.isRotating) {
          for (let i = purchasedItems.length - 1; i >= 0; i--) {
            if (
              purchasedItems[i].hasOwnProperty('unitcost') &&
              purchasedItems[i].unitcost === null &&
              purchasedItems[i].hasOwnProperty('serialNumber') &&
              purchasedItems[i].serialNumber === null
            ) {
              purchasedItems.splice(i, 1)
            }
          }
        }
        param = { storeRoom: storeRoomId, item: data }
        let url = '/v2/' + 'item' + '/add'
        let { error } = await API.post(url, param)
        if (error) {
          this.$message.error(error)
        } else {
          this.$message.success(this.$t('common._common.added_successfully'))
          this.loadRecord(true)
        }
        this.cancelForm()
      }
      this.emitForm = false
    },
  },
}
</script>
