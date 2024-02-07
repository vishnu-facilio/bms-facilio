<template>
  <div style="height: auto; overflow-y: scroll;">
    <div
      class="fc__layout__align fc__asset__main__header pT20 pB20 pL20 pR20"
      style="width: auto; align-items: center !important; border-bottom: none;"
    >
      <div v-if="asset" class="asset-details">
        <div class="asset-id mb5">#{{ asset.id }}</div>
        <div class="asset-name mb5 max-width550px">
          <span
            v-if="decommission"
            v-tippy
            :title="$t('setup.decommission.decommissioned')"
            class="pT3 pointer"
            ><fc-icon
              group="alert"
              class="pR5"
              name="decommissioning"
              size="18"
            ></fc-icon
          ></span>
          <span class="whitespace-pre-wrap">{{ asset.name }}</span>
          <div
            v-if="isStateFlowEnabled && currentModuleState"
            class="fc-badge inline vertical-middle mL15"
          >
            {{ currentModuleState }}
          </div>
          <div
            v-else-if="assetState"
            class="fc-badge inline vertical-middle mL15"
          >
            {{ assetState }}
          </div>
        </div>
      </div>
      <div class="d-flex flex-direction-row" style="margin-left: auto;">
        <!-- Enable custom button when v3 is ready -->
        <CustomButton
          :record="asset"
          :moduleName="moduleName"
          :position="POSITION.SUMMARY"
          @refresh="refreshObj()"
          @onError="() => {}"
        />
        <template v-if="isStateFlowEnabled">
          <TransitionButtons
            :moduleName="moduleName"
            :record="asset"
            buttonClass="asset-el-btn"
            :disabled="isApprovalEnabled"
            @currentState="() => {}"
            @transitionSuccess="refreshObj()"
            @transitionFailure="() => {}"
          ></TransitionButtons>
        </template>
        <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
          <ApprovalBar
            :moduleName="moduleName"
            :key="asset.id + 'approval-bar'"
            :record="asset"
            :hideApprovers="false"
            @onSuccess="refreshObj()"
            @onFailure="() => {}"
            class="approval-bar-shadow"
          ></ApprovalBar>
        </portal>
        <el-dropdown
          v-if="moreOptions"
          class="mL10 fc-btn-ico-lg pT5 pB5 pL8 pR8 pointer"
          @command="handleCommand"
        >
          <span class="el-dropdown-link">
            <img src="~assets/menu.svg" height="18" width="18" />
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-if="$hasPermission('asset:UPDATE')"
              command="edit"
              >{{ $t('common._common.edit') }}</el-dropdown-item
            >
            <el-dropdown-item v-if="showMoveButton" command="move"
              >Move</el-dropdown-item
            >
            <el-dropdown-item
              v-if="$hasPermission('asset:CREATE')"
              command="duplicate"
              >{{ $t('common._common.duplicate') }}</el-dropdown-item
            >
            <el-dropdown-item
              v-if="$helpers.isLicenseEnabled('ASSET_DEPRECIATION')"
              command="addDepreciation"
            >
              Apply Depreciation
            </el-dropdown-item>
            <el-dropdown-item
              v-if="canShowStoreRoomMovement"
              command="moveToStoreRoom"
            >
              {{ $t('common.inventory.move_to_storeroom') }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <div v-if="loading" class="text-center width100 pT50 mT50">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <page
      v-if="!loading && asset"
      :key="asset.id"
      :module="currentModuleName"
      :id="asset.id"
      :details="asset"
      :primaryFields="primaryFields"
      notesModuleName="assetnotes"
      attachmentsModuleName="assetattachments"
      :isV3Api="true"
    ></page>
    <new-asset
      v-if="canShowAssetCreation"
      ref="new-asset"
      :canShowAssetCreation.sync="canShowAssetCreation"
      :selectedCategory.sync="selectedCategory"
      moduleName="asset"
      :dataId="assetId"
      :moduleDisplayName="'Asset'"
      @refreshlist="refreshObj"
    ></new-asset>
    <asset-move
      :visibility.sync="showAssetMoveDialog"
      :key="showAssetMoveDialog"
      :asset="asset"
      @refresh="refreshObj"
    ></asset-move>
    <AssetDuplicationViewer
      v-if="showAssetDuplicationDialog"
      :moduleName="asset.categoryModuleName || 'asset'"
      :selectedRecord="asset.id"
      :selectedRecordObj="asset"
      @sucess="redirectListPage"
      @closed="closeAssetduplicationDialog"
    ></AssetDuplicationViewer>
    <depreciation-form
      v-if="showAddDepreciationForm"
      :asset="asset"
      @fetchAssetDetail="refreshObj()"
      @onClose="showAddDepreciationForm = false"
    ></depreciation-form>
    <el-dialog
      :visible.sync="showStoreRoomPopUp"
      :fullscreen="false"
      open="top"
      width="30%"
      :title="$t('common.inventory.move_to_storeroom')"
      custom-class="assetaddvaluedialog fc-dialog-center-container move-to-store-dialog inventory-dialog fc-web-form-dialog"
    >
      <div v-if="isNonRotatingAsset(asset)" class="pB10">
        <div class="stock-form-field pB5">
          {{ $t('common.header.item_type') }}
        </div>
        <FLookupFieldWrapper
          v-model="selectedRotatingItemType"
          :field="rotatingItemTypeLookupData"
          :filterConstruction="rotatingFilters"
          class="width100"
        ></FLookupFieldWrapper>
      </div>
      <div class="stock-form-field pB5">
        {{ $t('common.products.storeroom') }}
      </div>
      <FLookupFieldWrapper
        v-model="selectedStoreroom"
        :field="storeRoomLookupData"
        class="width100 pB10"
      ></FLookupFieldWrapper>

      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel"
          @click="showStoreRoomPopUp = false"
          >{{ $t('common._common.cancel') }}</el-button
        >
        <el-button
          class="modal-btn-save"
          type="primary"
          :loading="isButtonLoading"
          @click="moveToStoreRoomApi"
          >{{ $t('common._common.ok') }}</el-button
        >
      </div>
    </el-dialog>
    <RotatingAssetUsagesList
      v-if="showUsagesList"
      :plannedMaintenanceList="plannedMaintenanceList"
      :inspectionTemplateList="inspectionTemplateList"
      @onClose="showUsagesList = false"
    >
    </RotatingAssetUsagesList>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { eventBus } from '@/page/widget/utils/eventBus'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import Page from '@/page/PageBuilder'
import AssetDuplicationViewer from '@/AssetDuplicationViewer'
import NewAsset from 'pages/assets/asset/v1/NewAsset'
import AssetMove from 'pages/assets/asset/v1/AssetMoveDialog'
import DepreciationForm from './AddDepreciationForm'
import TransitionButtons from '@/stateflow/TransitionButtons'
import ApprovalBar from '@/approval/ApprovalBar'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import Spinner from '@/Spinner'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import RotatingAssetUsagesList from './RotatingAssetUsagesList.vue'

export default {
  components: {
    Page,
    NewAsset,
    TransitionButtons,
    AssetMove,
    AssetDuplicationViewer,
    DepreciationForm,
    ApprovalBar,
    CustomButton,
    Spinner,
    FLookupFieldWrapper,
    RotatingAssetUsagesList,
  },
  created() {
    this.$store.dispatch('loadTicketStatus', 'asset')
    this.$store.dispatch('loadTicketStatus', 'assetmovement')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAssetType')

    eventBus.$on('refesh-parent', () => this.refreshObj())
    eventBus.$on('refresh-overview', this.refreshObj)
    eventBus.$on('stateflows', this.loadStateflows)
    eventBus.$on('currentSpaceId', this.setCurrentSpaceId)

    this.loadAssetsDetails()
    this.loadAssetMovements()
  },
  beforeDestroy() {
    eventBus.$off('refresh-overview', this.refreshObj)
  },
  data() {
    return {
      plannedMaintenanceList: [],
      inspectionTemplateList: [],
      canMoveToStore: null,
      showUsagesList: false,
      stateflows: [],
      showAssetMoveDialog: false,
      showAssetDuplicationDialog: false,
      asset: null,
      moduleName: 'asset',
      primaryFields: [
        'name',
        'description',
        'category',
        'type',
        'siteId',
        'space',
      ],
      canShowAssetCreation: false,
      selectedCategory: null,
      showAddDepreciationForm: false,
      POSITION: POSITION_TYPE,
      loading: false,
      showStoreRoomPopUp: false,
      selectedStoreroom: null,
      selectedRotatingItemType: null,
      storeRoomLookupData: {
        displayName: this.$t('common.header.choose_storeroom'),
        name: 'storeRoom',
        lookupModule: {
          displayName: this.$t('common.products.storeroom'),
          name: 'storeRoom',
        },
        forceFetchAlways: true,
        selectedItems: [],
      },
      rotatingItemTypeLookupData: {
        name: 'itemTypes',
        lookupModule: {
          displayName: this.$t('common.inventory.rotating_itemType'),
          name: 'itemTypes',
        },
        forceFetchAlways: true,
        selectedItems: [],
      },
      isButtonLoading: false,
    }
  },
  computed: {
    ...mapState({
      ticketStates: state => state.ticketStatus.asset,
    }),
    ...mapGetters(['getAssetCategory']),
    currentModuleName() {
      let { moduleName } = this.asset || {}

      return moduleName || 'asset'
    },
    canShowStoreRoomMovement() {
      let { asset } = this
      let { storeRoom } = asset || {}

      return isEmpty(storeRoom) && this.$helpers.isLicenseEnabled('INVENTORY')
    },
    currentAssetCategory() {
      let { currentModuleName, selectedCategory } = this
      if (!isEmpty(selectedCategory)) {
        let { moduleName } = selectedCategory
        return moduleName
      }
      return currentModuleName
    },
    assetId() {
      let assetId = this.$attrs.id || this.$route.params.assetid

      return assetId ? parseInt(assetId) : null
    },
    isStateFlowEnabled() {
      return Boolean(
        this.asset && this.asset.moduleState && this.asset.moduleState.id
      )
    },
    decommission() {
      return this.$getProperty(this, 'asset.decommission', false)
    },
    isApprovalEnabled() {
      let { asset } = this
      let { approvalFlowId, approvalStatus } = asset || {}
      return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
    },
    currentModuleState() {
      let currentStateId = this.asset && this.asset.moduleState.id

      let currentState =
        this.ticketStates &&
        this.ticketStates.find(state => state.id === currentStateId)

      return currentState ? currentState.displayName : null
    },
    assetState() {
      let state = this.asset.state
      if (state) {
        if (state === 1) {
          return 'Active'
        } else if (state === 2) {
          return 'In Store'
        } else if (state === 3) {
          return 'In Repair'
        } else if (state === 4) {
          return 'Inactive'
        } else if (state === 5) {
          return 'Retired'
        }
      }
      return null
    },
    showMoveButton() {
      if (this.$account.user.orgId != 155) {
        return false
      }
      if (!this.asset) {
        return false
      }
      let recordInOpenState = true
      if (!this.stateflows || Object.keys(this.stateflows).length === 0) {
        recordInOpenState = false
      }
      return (
        this.asset.geoLocationEnabled &&
        !recordInOpenState &&
        !this.asset.connected
      )
    },
    moreOptions() {
      let recordLocked = this.$getProperty(
        this,
        'asset.moduleState.recordLocked',
        false
      )
      return (
        (this.$hasPermission('asset:UPDATE') ||
          this.$hasPermission('asset:CREATE') ||
          this.showMoveButton) &&
        !recordLocked
      )
    },
  },
  methods: {
    rotatingFilters() {
      return { isRotating: { operatorId: 15, value: ['true'] } }
    },
    isNonRotatingAsset(asset) {
      let { rotatingItemType } = asset || {}
      return isEmpty(rotatingItemType)
    },
    async assetUsageList() {
      let queryParam = {
        assetId: this.asset?.id,
      }
      let { data, error } = await API.post(
        '/v3/rotatingAsset/rotatingAssetUsages',
        queryParam
      )
      if (error) {
        this.$message.error(
          this.$t('common.inventory.error_occured_while_selecting_store')
        )
        this.canMoveToStore = false
      } else {
        let { plannedMaintenanceList, inspectionTemplateList } = data || {}
        this.plannedMaintenanceList = plannedMaintenanceList || []
        this.inspectionTemplateList = inspectionTemplateList || []
        this.canMoveToStore = true
      }
    },
    async moveToStoreRoom() {
      await this.assetUsageList()
      let {
        plannedMaintenanceList,
        inspectionTemplateList,
        canMoveToStore,
      } = this
      if (!canMoveToStore) return
      if (isEmpty(plannedMaintenanceList) && isEmpty(inspectionTemplateList)) {
        this.showStoreRoomPopUp = true
      } else {
        this.showUsagesList = true
      }
    },
    async moveToStoreRoomApi() {
      this.isButtonLoading = true
      let {
        asset,
        moduleName,
        selectedStoreroom,
        selectedRotatingItemType,
      } = this

      if (isEmpty(selectedStoreroom)) {
        this.$message.error(this.$t('common.inventory.select_store_warning'))
        this.isButtonLoading = false
        return
      }
      let storeRoomData = {
        id: selectedStoreroom,
      }
      let params = {
        id: asset.id,
        data: {
          storeRoom: storeRoomData,
        },
        params: {
          moveToStoreRoom: true,
        },
      }
      if (this.isNonRotatingAsset(asset)) {
        if (isEmpty(selectedRotatingItemType)) {
          this.$message.error(
            this.$t('common.inventory.select_itemType_warning')
          )
          this.isButtonLoading = false
          return
        }
        params.data['rotatingItemType'] = {
          id: selectedRotatingItemType,
        }
      }
      let { error } = await API.updateRecord(moduleName, params)
      if (error) {
        this.$message.error(
          error.message ||
            this.$t('common.inventory.error_occured_while_selecting_store')
        )
      } else {
        let successMsg = this.$t('common.inventory.move_to_store_success')
        this.$message.success(successMsg)
        this.showStoreRoomPopUp = false
        this.loadAssetsDetails()
      }
      selectedStoreroom = null
      this.isButtonLoading = false
    },
    async loadAssetsDetails() {
      this.loading = true
      let { asset, error } = await API.fetchRecord(
        'asset',
        {
          id: this.assetId,
        },
        { force: true } // temp call only for update
      )
      if (!isEmpty(error)) {
        let { message = 'Error Occured while fetching Asset' } = error
        this.$message.error(message)
      } else {
        this.asset = asset
      }
      this.loading = false
    },
    redirectListPage() {
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST)
        let currentView =
          this.$attrs.viewname === 'filteredassets'
            ? 'all'
            : this.$attrs.viewname

        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
            },
            query: this.$route.query,
          })
      } else {
        let url =
          '/app/at/assets/' +
          (this.$route.params.viewname === 'filteredassets'
            ? 'all'
            : this.$route.params.viewname)
        this.$router.push({ path: url, query: this.$route.query })
      }
    },

    handleCommand(command) {
      if (command === 'edit') {
        this.editAsset()
      } else if (command === 'move') {
        this.showAssetMoveDialog = true
      } else if (command === 'duplicate') {
        this.invokeAssetduplicationDialog(this.asset)
      } else if (command === 'addDepreciation') {
        this.showAddDepreciationForm = true
      } else if (command === 'moveToStoreRoom') {
        this.moveToStoreRoom()
      }
    },
    invokeAssetduplicationDialog() {
      this.showAssetDuplicationDialog = true
    },
    closeAssetduplicationDialog() {
      this.showAssetDuplicationDialog = false
    },
    loadAssetMovements() {
      let filters = JSON.stringify({
        assetId: [{ operatorId: 36, value: [this.assetId + ''] }],
      })

      API.get('/v2/assetMovement/list', {
        filters,
      }).then(({ error, data }) => {
        if (!error && data.stateFlows) {
          this.stateflows = data.stateFlows
        }
      })
    },
    editAsset() {
      let { asset } = this
      let { category } = asset
      let { id: categoryId } = category
      if (!isEmpty(categoryId)) {
        let selectedCategory = this.getAssetCategory(categoryId)
        if (!isEmpty(selectedCategory)) {
          this.$set(this, 'selectedCategory', selectedCategory)
        }
      }
      this.$set(this, 'canShowAssetCreation', true)
    },
    refreshObj() {
      this.loadAssetsDetails(true)
      this.loadAssetMovements(true)
    },
    loadStateflows(value) {
      this.stateflows = value
    },
    setCurrentSpaceId(value) {
      if (value) {
        this.asset.currentSpaceId = value
      }
      this.loadAssetsDetails()
    },
  },
}
</script>
<style lang="scss">
.move-to-store-dialog.inventory-dialog .el-dialog__body {
  height: 230px;
}
</style>
<style scoped>
.asset-details {
  flex-grow: 1;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.asset-details .asset-id {
  font-size: 12px;
  color: #39b2c2;
}
.asset-details .asset-name {
  font-size: 22px;
  color: #324056;
  display: flex;
  align-items: center;
}
.asset-details .asset-space {
  font-size: 13px;
  color: #8ca1ad;
}
.fc-badge {
  color: #fff;
  background-color: #23b096;
  padding: 5px 18px;
}
</style>
