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
              <tool-avatar
                :name="false"
                size="xlg"
                module="tool"
                :recordData="record.toolType"
              ></tool-avatar>
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
                  :content="$getProperty(record, 'toolType.name')"
                >
                  <span class="whitespace-pre-wrap custom-header">{{
                    $getProperty(record, 'toolType.name')
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
            class="fc__add__btn mR20"
            v-if="$hasPermission('inventory:CREATE') && !rotating"
            @click="openStockTool()"
            >{{ $t('common.header.stock_tool') }}</el-button
          >
          <el-dropdown
            class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
            trigger="click"
            @command="action => summaryDropDownAction(action)"
            v-if="$hasPermission('inventory:UPDATE,UPDATE_OWN')"
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
              <el-dropdown-item :key="2" command="go_to_toolType">{{
                $t('common._common.go_to_toolType')
              }}</el-dropdown-item>
              <el-dropdown-item
                v-if="
                  !(
                    record.toolType.approvalNeeded ||
                    record.storeRoom.approvalNeeded
                  )
                "
                :key="3"
                command="issue_tool"
                >{{ $t('common.products.issue_tool') }}</el-dropdown-item
              >
              <el-dropdown-item :key="4" command="return_tool">{{
                $t('common._common.return_tool')
              }}</el-dropdown-item>
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
    <tool-issue-return
      v-if="showToolIssueReturn"
      :tool="record"
      :toolType="record.toolType"
      :type="issueReturnType"
      @refresh="refreshTool"
      :showToolIssueReturn.sync="showToolIssueReturn"
      :key="record.id"
    ></tool-issue-return>
    <update-tool-form
      :visibility.sync="newFormVisibility"
      :editId="editId"
      v-if="newFormVisibility"
      @saved="loadRecord(true)"
    ></update-tool-form>
    <stock-tool
      :visibility.sync="stockToolVisibility"
      :record="record"
      :moduleName="moduleName"
      @saved="loadRecord(true)"
    ></stock-tool>
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import ToolAvatar from '@/avatar/ItemTool'
import ToolIssueReturn from './ToolIssueReturn'
import UpdateToolForm from './components/UpdateToolForm'
import StockTool from './components/StockTool'
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
    ToolAvatar,
    ToolIssueReturn,
    UpdateToolForm,
    StockTool,
  },
  data() {
    return {
      notesModuleName: 'toolnotes',
      attachmentsModuleName: 'toolattachments',
      primaryFields: [
        'localId',
        'lastPurchasedDate',
        'rate',
        'minimumQuantity',
        'storeRoom',
        'currentQuantity',
      ],
      newFormVisibility: false,
      emitForm: false,
      resetForm: false,
      saving: false,
      showPreviewImage: false,
      showToolIssueReturn: false,
      refreshToolTransactions: false,
      refreshToolStoreRoom: false,
      editId: null,
      stockToolVisibility: false,
    }
  },
  computed: {
    moduleName() {
      return 'tool'
    },
    rotating() {
      return this.$getProperty(this, 'record.toolType.isRotating')
    },
    photoUrl() {
      return this.$getProperty(this, 'record.toolType.photoUrl', null)
    },
  },
  title() {
    'Tool'
  },
  methods: {
    openStockTool() {
      this.stockToolVisibility = true
    },
    editData(data) {
      this.newFormVisibility = true
      this.editId = data.id
    },
    refreshTool() {
      this.refreshToolTransactions = true
      this.refreshToolStoreRoom = true
      this.loadRecord()
    },
    summaryDropDownAction(action) {
      if (action === 'adjust_balance') {
        this.openAdjustmentDialog()
      } else if (action === 'issue_tool') {
        this.openIssueDialog()
      } else if (action === 'return_tool') {
        this.openReturnDialog()
      } else if (action === 'edit') {
        let { record } = this
        this.editData(record)
      } else if (action === 'go_to_toolType') {
        this.redirectToToolTypeSummary(
          this.$getProperty(this.record, 'toolType.id')
        )
      }
    },
    async redirectToToolTypeSummary(id) {
      let moduleName = 'toolTypes'
      let currentView = await this.fetchView(moduleName)

      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
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
          name: 'tooltypeSummary',
          params: {
            viewname: currentView,
            id,
          },
        })
      }
    },
    openIssueDialog() {
      this.showToolIssueReturn = true
      this.issueReturnType = 'issue'
    },
    openAdjustmentDialog() {
      this.showToolIssueReturn = true
      this.issueReturnType = 'adjustment'
    },
    openReturnDialog() {
      this.showToolIssueReturn = true
      this.issueReturnType = 'return'
    },
    showAvatarPreview() {
      if (this.photoUrl) {
        this.showPreviewImage = true
      }
    },
  },
}
</script>
