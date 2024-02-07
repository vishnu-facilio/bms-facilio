<template>
  <div
    class="custom-module-overview"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header p20">
        <div class="custom-module-details">
          <div class="d-flex flex-middle align-center">
            <div class="mL5">
              <div class="custom-module-id">#{{ record && record.id }}</div>
              <div class="custom-module-name d-flex max-width300px">
                <el-tooltip
                  placement="bottom"
                  effect="dark"
                  :content="record[mainFieldKey]"
                >
                  <span class="whitespace-pre-wrap custom-header">{{
                    record[mainFieldKey]
                  }}</span>
                </el-tooltip>
              </div>
            </div>
          </div>
        </div>
        <div class="marginL-auto flex-middle">
          <div class="fc__layout__align">
            <!-- <el-popover
              v-if="$hasPermission('inventory:UPDATE,UPDATE_OWN')"
              placement="top-start"
              width="180"
              trigger="hover"
            >
              <el-button slot="reference" class="fc__border__btn">
                {{ $t('common.products.new_transaction') }}
                <i class="el-icon-arrow-down f14 text-right"></i
              ></el-button>
              <div
                @click="openStoreIssueDialog"
                class="label-txt-black pT10 pB10 pointer pL10 list-hover"
              >
                {{ $t('common.products.issue') }}
              </div>
              <div
                @click="openItemReturnDialog"
                class="label-txt-black pT10 pB10 pointer pL10 list-hover"
              >
                {{ $t('common._common.return_item') }}
              </div>
              <div
                @click="openToolReturnDialog"
                class="label-txt-black pT10 pB10 pointer pL10 list-hover"
              >
                {{ $t('common._common.return_tool') }}
              </div>
            </el-popover> -->
            <el-button
              v-if="$hasPermission('inventory:UPDATE,UPDATE_OWN')"
              type="button"
              class="fc-wo-border-btn mL20 pL15 pR15 self-center"
              @click="editRecord"
            >
              <i class="el-icon-edit"></i>
            </el-button>
            <el-dropdown
              class="mL15 fc-btn-ico-lg pT5 pB5 pL8 pR8 pointer"
              @command="handleCommand"
            >
              <span class="el-dropdown-link">
                <img src="~assets/menu.svg" height="18" width="18" />
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="genNotifyPref">{{
                  $t('common.header.notification_preferences')
                }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
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
        :showEdit="false"
        :showDescriptionTitle="true"
      ></Page>
    </template>
    <template v-if="showItemIssueReturn">
      <item-issue-return
        :storeRoom="record"
        :showItemIssueReturn.sync="showItemIssueReturn"
        :type="issueReturnType"
        @refreshInventory="refreshData"
      ></item-issue-return>
    </template>
    <template v-if="showToolIssueReturn">
      <tool-issue-return
        :storeRoom="record"
        :type="issueReturnType"
        :showToolIssueReturn.sync="showToolIssueReturn"
        @refreshInventory="refreshData"
      ></tool-issue-return>
    </template>
    <template v-if="showStoreIssueDialog">
      <inventory-store-issue
        :storeRoom="record"
        :visibility.sync="showStoreIssueDialog"
        :itemList="items"
        :toolList="tools"
        @refreshInventory="refreshData"
      ></inventory-store-issue>
    </template>
    <template v-if="notificationSettingsVisibility">
      <store-room-notification-preference
        :storeRoom="record"
        :visibility.sync="notificationSettingsVisibility"
      ></store-room-notification-preference>
    </template>
    <template v-if="preferenceVisibility">
      <el-dialog
        :visible.sync="preferenceVisibility"
        :title="$t('common.header.notification_preference')"
        :fullscreen="false"
        :before-close="genNotficationPreferencesToggle"
        :append-to-body="true"
        key="1"
        custom-class="fc-animated slideInRight fc-dialog-form contract-notification-form-heading fc-dialog-right width50"
      >
        <notification-preference
          :moduleName="'storeRoom'"
          :recordId="record.id"
          :visibility.sync="preferenceVisibility"
        ></notification-preference>
      </el-dialog>
    </template>
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import ItemIssueReturn from './ItemStoreIssueReturn'
import ToolIssueReturn from './ToolStoreIssueReturn'
import InventoryStoreIssue from './InventoryStoreIssue'
import StoreRoomNotificationPreference from 'pages/Inventory/component/StoreRoomNotificationPreference'
import NotificationPreference from 'pages/contract/components/NotificationPreference'
import { API } from '@facilio/api'
import { eventBus } from '@/page/widget/utils/eventBus'

import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import {
  getFilteredItemList,
  getFilteredToolList,
} from 'pages/Inventory/InventoryUtil'
export default {
  extends: CustomModuleSummary,
  props: ['viewname'],
  components: {
    ItemIssueReturn,
    ToolIssueReturn,
    InventoryStoreIssue,
    StoreRoomNotificationPreference,
    NotificationPreference,
  },
  data() {
    return {
      notesModuleName: 'storeroomnotes',
      attachmentsModuleName: 'storeroomattachments',
      primaryFields: [
        'description',
        'lastPurchasedDate',
        'location',
        'noOfItemTypes',
        'noOfToolTypes',
        'owner',
        'servingsites',
      ],
      showItemIssueReturn: false,
      showToolIssueReturn: false,
      issueReturnType: null,
      showStoreIssueDialog: false,
      notificationSettingsVisibility: false,
      preferenceVisibility: false,
      storeRoomEditObj: null,
      storeRoomEditFormVisibility: false,
      emitForm: false,
      saving: false,
      resetForm: false,
      itemTypeFormChooser: false,
      toolTypeFormChooser: false,
      items: [],
      tools: [],
    }
  },
  computed: {
    currentStoreRoomId() {
      return parseInt(this.$route.params.id)
    },
    mainFieldKey() {
      return 'name'
    },
    moduleName() {
      return 'storeRoom'
    },
    showEdit() {
      let canShowEdit = this.$hasPermission(`inventory:UPDATE,UPDATE_OWN`)
      let isNotLocked = this.isStateFlowEnabled
        ? !this.isRecordLocked && !this.isRequestedState
        : true
      return canShowEdit && isNotLocked && !this.record.isStaged
    },
  },
  title() {
    'Store Room'
  },
  mounted() {
    eventBus.$on('refresh', this.refreshData)
    this.refreshItemAndTool()
  },
  methods: {
    saveForm() {
      this.emitForm = true
    },
    cancelForm() {
      this.resetForm = true
      this.storeRoomEditFormVisibility = false
      this.newAddItemFormVisibility = false
      this.newAddToolFormVisibility = false
      this.itemTypeFormChooser = false
      this.toolTypeFormChooser = false
    },
    editRecord() {
      let { id } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
          })
      } else {
        id &&
          this.$router.push({
            name: 'edit-storeroom',
            params: { id },
          })
      }
    },
    openItemReturnDialog() {
      this.showItemIssueReturn = true
      this.issueReturnType = 'return'
    },
    openToolReturnDialog() {
      this.showToolIssueReturn = true
      this.issueReturnType = 'return'
    },
    openStoreIssueDialog() {
      this.showStoreIssueDialog = true
    },
    notficationPreferencesToggle() {
      this.notificationSettingsVisibility = !this.notificationSettingsVisibility
    },
    genNotficationPreferencesToggle() {
      this.preferenceVisibility = !this.preferenceVisibility
    },
    editStoreRoomForm() {
      this.storeRoomEditObj = {
        ...this.record,
        customTitle: this.$t('common.header.edit_storeroom'),
      }
      this.storeRoomEditFormVisibility = true
    },
    handleCommand(command) {
      if (command === 'edit') {
        this.editStoreRoomForm()
      } else if (command === 'notifypref') {
        this.notficationPreferencesToggle()
      } else if (command === 'genNotifyPref') {
        this.genNotficationPreferencesToggle()
      }
    },
    async loadItemforStoreRoom() {
      let filters = {
        storeRoom: {
          operatorId: 36,
          value: [this.currentStoreRoomId + ''],
        },
      }
      this.items = await getFilteredItemList(filters)
    },
    async loadToolforStoreRoom() {
      let filters = {
        storeRoom: {
          operatorId: 36,
          value: [this.currentStoreRoomId + ''],
        },
      }
      this.tools = await getFilteredToolList(filters)
    },
    refreshItemAndTool() {
      this.loadItemforStoreRoom()
      this.loadToolforStoreRoom()
    },
    submitForm(data) {
      let self = this
      if (this.storeRoomEditFormVisibility) {
        this.storeRoomEditFormVisibility = false
        let param
        param = { data }
        param['id'] = this.currentStoreRoomId
        param['moduleName'] = this.moduleName
        let { data } = param
        let { location } = data || {}
        let { id } = location || {}
        if (id < 0) {
          param.data.location = null
        }
        let url = '/v3/modules/data/patch'

        let { response, error } = API.post(url, param)
        if (error) {
          self.$message.error(error.message)
        } else if (response) {
          self.$message.success(
            this.$t('common.wo_report.edit_successfull'),
            this.refreshData()
          )
        }
        self.cancelForm()
      } else if (this.newAddItemFormVisibility) {
        this.newAddItemFormVisibility = false
        let param
        param = { storeRoom: data.storeRoom.id, item: data }
        let url = '/v2/item/add'

        let { error } = API.post(url, param)
        if (error) {
          self.$message.error(error.message)
        } else {
          self.$message.success(this.$t('common._common.added_successfully'))
          self.refreshItemStoreRoom = true
          this.refreshData()
        }
        self.cancelForm()
      } else if (this.newAddToolFormVisibility) {
        this.newAddToolFormVisibility = false
        let param
        param = { storeRoom: data.storeRoom.id, tool: data }
        let url = '/v2/tool/add'
        let { error } = API.post(url, param)
        if (error) {
          self.$message.error(error.message)
        } else {
          self.$message.success(this.$t('common._common.added_successfully'))
          self.refreshToolStoreRoom = true
          this.refreshData()
        }
        self.cancelForm()
      }
      self.emitForm = false
    },
  },
}
</script>
