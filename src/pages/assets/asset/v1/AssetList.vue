<template>
  <CommonListLayout
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => viewName"
    :pathPrefix="`${parentPath}/${modulePrefix}/`"
    :hideSubHeader="canHideSubHeader"
    :recordCount="recordCount"
    :recordLoading="showLoading"
    class="custom-module-list-layout asset-list-page"
  >
    <template #header>
      <AdvancedSearchWrapper
        v-if="!canHideFilter"
        :key="`ftags-list-${moduleName}`"
        :filters="filters"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>

      <template v-if="!showSearch">
        <visual-type
          v-if="canShowVisualSwitch"
          @onSwitchVisualize="val => (canShowListView = val)"
        ></visual-type>

        <div v-if="$account.org.id === 186">
          <el-dropdown @command="switchView">
            <img class="as-view-icon" src="~assets/two-columns-layout.svg" />
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="list">{{
                $t('common._common.list_view')
              }}</el-dropdown-item>
              <el-dropdown-item command="map">{{
                $t('common._common.map_view')
              }}</el-dropdown-item>
              <el-dropdown-item command="floormap">{{
                $t('common._common.floor_plan_view')
              }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
        <CustomButton
          :moduleName="moduleName"
          :position="POSITION.LIST_TOP"
          class="custom-button"
          @onSuccess="onCustomButtonSuccess"
          @onError="() => {}"
        ></CustomButton>
        <button
          v-if="$hasPermission('asset:CREATE')"
          class="fc-create-btn"
          @click="addAsset"
        >
          {{ $t('common._common._new') }} {{ moduleDisplayName }}
        </button>
      </template>
    </template>

    <template v-if="canShowCalendarHeader" #sub-header>
      <CalendarDateWrapper v-if="!showListView" />
    </template>

    <template #sub-header-actions>
      <template v-if="!showSearch && !isEmpty(records)">
        <pagination
          :total="recordCount"
          :currentPageCount="currentPageCount"
          :perPage="perPage"
          :skipTotalCount="true"
          class="pL15 fc-black-small-txt-12"
        ></pagination>
        <span class="separator" v-if="recordCount > 0">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.sort')"
          placement="right"
        >
          <Sort
            :key="`${moduleName}-sort`"
            :moduleName="moduleName"
            @onSortChange="updateSort"
          ></Sort>
        </el-tooltip>
        <span class="separator">|</span>
        <template v-if="!isAltayerNonPrivilagedUser">
          <el-tooltip
            effect="dark"
            :content="$t('common._common.export')"
            placement="left"
          >
            <f-export-settings
              :module="moduleName"
              :viewDetail="viewDetail"
              :showViewScheduler="false"
              :showMail="false"
              :filters="filters"
            ></f-export-settings>
          </el-tooltip>
        </template>
      </template>
    </template>

    <!-- List Content -->
    <div class="height100">
      <Spinner v-if="showLoading" class="mT40" :show="showLoading"></Spinner>
      <div v-else-if="view === 'map'" class="height100">
        <asset-mapview :markers="mapAssets"></asset-mapview>
      </div>
      <div v-else-if="view === 'floormap'">
        <floor-map :assets="records"></floor-map>
      </div>
      <template v-else-if="showListView">
        <div
          v-if="$validation.isEmpty(records) && !showLoading"
          class="cm-empty-state-container white-bg flex-middle justify-content-center  m10 mT0 "
        >
          <inline-svg
            src="svgs/emptystate/assets-listempty"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="q-item-label nowo-label">
            {{ $t('asset.assets.no_asset') }}
          </div>
        </div>
        <div
          class="height100"
          v-if="!showLoading && !$validation.isEmpty(records)"
        >
          <div class="cm-list-container">
            <div
              class="column-customization-icon"
              :disabled="!isColumnCustomizable"
              @click="toShowColumnSettings"
            >
              <el-tooltip
                :disabled="isColumnCustomizable"
                placement="top"
                :content="$t('common._common.you_dont_have_permission')"
              >
                <inline-svg
                  src="column-setting"
                  class="text-center position-absolute icon"
                />
              </el-tooltip>
            </div>

            <div
              class="pull-left table-header-actions"
              v-if="selectedListItemsIds.length > 0"
            >
              <div
                class="action-btn-slide btn-block"
                v-if="$hasPermission('asset:UPDATE')"
              >
                <button class="btn btn--tertiary" @click="bulkbool = true">
                  <i class="fa fa-refresh b-icon"></i>
                  {{ $t('common._common.update') }}
                </button>
              </div>
              <div
                class="action-btn-slide btn-block"
                v-if="$hasPermission('asset:DELETE')"
              >
                <button
                  class="btn btn--tertiary pointer"
                  @click="deleteAsset(selectedListItemsIds)"
                  :class="{ disabled: deleteLoading }"
                >
                  <i
                    class="fa fa-circle-o-notch b-icon fa-spin"
                    aria-hidden="true"
                    v-if="deleteLoading"
                  ></i>
                  <i class="fa fa-trash-o b-icon" v-else></i>

                  {{ $t('asset.assets.delete') }}
                </button>
              </div>
              <div class="action-btn-slide btn-block">
                <button
                  class="btn btn--tertiary pointer"
                  @click="redirectToQrPage"
                >
                  <inline-svg
                    src="printer"
                    class="vertical-middle"
                    iconClass="icon icon-sm mR5"
                  ></inline-svg>
                  {{ $t('asset.assets.print_qr') }}
                </button>
              </div>
              <div class="action-btn-slide btn-block">
                <button
                  class="btn btn--tertiary pointer"
                  @click="
                    canGenerateZip = true
                    onQrSelected()
                  "
                >
                  <inline-svg
                    src="download3"
                    class="vertical-middle"
                    iconClass="icon icon-sm mR5"
                  ></inline-svg>
                  {{ $t('asset.assets.download_qr') }}
                </button>
              </div>
              <CustomButton
                :moduleName="moduleName"
                :position="POSITION.LIST_BAR"
                class="custom-button"
                @onSuccess="onCustomButtonSuccess"
                @onError="() => {}"
                :selectedRecords="selectedListItemsObj"
              ></CustomButton>
            </div>
            <CommonList
              :viewDetail="viewDetail"
              :records="records"
              :moduleName="moduleName"
              :redirectToOverview="openAssetOverview"
              @selection-change="selectAsset"
              :slotList="slotList"
              canShowCustomButton="true"
              :refreshList="onCustomButtonSuccess"
            >
              <template #[slotList[0].name]="{record}">
                <div class="d-flex">
                  <div class="fc-id">{{ '#' + record.id }}</div>
                </div>
              </template>
              <template #[slotList[1].criteria]="{record}">
                <router-link
                  class="d-flex fw5 label-txt-black ellipsis main-field-column"
                  :to="openAssetOverview(record.id)"
                >
                  <asset-avatar
                    :name="false"
                    size="lg"
                    :asset="record"
                  ></asset-avatar>
                  <el-tooltip
                    effect="dark"
                    :content="record.name || '---'"
                    placement="top-start"
                    :open-delay="600"
                  >
                    <div class="fw5 width200px">
                      <span class="list-main-field">{{
                        record.name || '---'
                      }}</span>
                    </div>
                  </el-tooltip>
                </router-link>
              </template>
              <template #[slotList[2].name]="{record}">
                <div class="d-flex text-center">
                  <i
                    v-if="$hasPermission(`asset:UPDATE`)"
                    class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                    data-arrow="true"
                    :title="$t('common._common.edit')"
                    @click="openAssetCreation(record)"
                    v-tippy
                  ></i>
                  <i
                    v-if="$hasPermission(`asset:DELETE`)"
                    class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                    data-arrow="true"
                    :title="$t('common._common.delete')"
                    @click="deleteAsset([record.id])"
                    v-tippy
                  ></i>
                </div>
              </template>
            </CommonList>
          </div>
          <f-dialog
            v-if="showQrDialog"
            :visible.sync="showQrDialog"
            width="30%"
            @save="generateQRCode"
            @close="showQrDialog = false"
            :confirmTitle="$t('asset.assets.generate')"
            :stayOnSave="true"
            :loading="fetchingAllAssets"
            :loadingTitle="$t('asset.assets.generating')"
            customClass="qr-dialog"
            :title="$t('asset.assets.generate_qr')"
          >
            <div>Selected asset geneated</div>
            <!-- <div class="fc-setup-modal-title fL">
              {{ $t('asset.assets.generate_qr') }}
            </div> -->
            <!-- <div style="padding: 25px 0px">
              <el-radio
                class="fc-radio-btn pT15"
                style="width: 100%"
                color="secondary"
                v-model="qrParam"
                :label="$t('common._common.current')"
              >
                <div class="inline">
                  {{ $t('asset.assets.selected_assets_only') }}
                </div>
                <div class="inline">
                  (
                  <pagination
                    :total="recordCount"
                    :perPage="perPage"
                    :hideToggle="true"
                    class="inline"
                  ></pagination
                  >)
                </div>
              </el-radio>
              <el-radio
                class="fc-radio-btn pT15"
                color="secondary"
                v-model="qrParam"
                :label="$t('common._common._all')"
                >{{ `All assets ${recordCount}` }}</el-radio
              >
            </div> -->
          </f-dialog>
          <div v-if="showPrintPreview">
            <generate-q-r-code :assets="assetsForQr"></generate-q-r-code>
          </div>
        </div>
      </template>
      <CalendarView
        v-else-if="!showListView"
        ref="calendar"
        :moduleName="moduleName"
        :record="records"
        :viewDetail="viewDetail"
        :viewname="viewname"
        :filters="filters"
      ></CalendarView>

      <new-asset
        v-if="canShowAssetCreation"
        ref="new-asset"
        :canShowAssetCreation.sync="canShowAssetCreation"
        moduleName="asset"
        :selectedCategory.sync="selectedCategory"
        :dataId="assetId"
        :moduleDisplayName="'Asset'"
        @refreshlist="refreshRecordDetails"
      ></new-asset>
      <AssetDuplicationViewer
        v-if="showAssetDuplicationDialog"
        :moduleName="duplicateAssetObj.categoryModuleName || 'asset'"
        listName="Asset"
        :selectedRecord="duplicateAssetObj.id"
        :selectedRecordObj="duplicateAssetObj"
        @sucess="refreshRecordDetails"
        @closed="showAssetDuplicationDialog = false"
      ></AssetDuplicationViewer>
      <bulk-update-viewer
        v-if="bulkbool"
        :module="moduleName"
        :fieldlist="bulkfieldlist"
        @submit="bulkAction"
        @closed="bulkbool = false"
        :content-css="{
          padding: '0px',
          background: '#f7f8fa',
          Width: '10vw',
          Height: '30vh',
        }"
      ></bulk-update-viewer>
      <column-customization
        :visible.sync="showColumnSettings"
        :moduleName="moduleName"
        :viewName="viewname"
      ></column-customization>
      <CategoryListDialog
        v-if="canShowCategoryDialog"
        :canShowCategoryDialog.sync="canShowCategoryDialog"
        :selectedCategory.sync="selectedCategory"
        @openAssetCreation="openAssetCreation"
      ></CategoryListDialog>
      <DeleteDialog
        v-if="showDialog"
        :moduleName="moduleName"
        :errorMap="errorMap"
        @onClose="showDialog = false"
        :type="errorType"
        @refresh="refreshRelatedList()"
      >
      </DeleteDialog>
      <portal to="view-manager-link">
        <router-link
          tag="div"
          :to="{
            path: `${parentPath}/${moduleName}/viewmanager`,
          }"
          class="view-manager-btn"
        >
          <inline-svg
            src="svgs/hamburger-menu"
            class="d-flex"
            iconClass="icon icon-sm"
          ></inline-svg>
          <span class="label mL10 text-uppercase">
            {{ $t('viewsmanager.list.views_manager') }}
          </span>
        </router-link>
      </portal>
    </div>
  </CommonListLayout>
</template>
<script>
import AssetMapview from '@/AssetMapView'
import NewAsset from 'pages/assets/asset/v1/NewAsset'
import GenerateQRCode from 'pages/assets/GenerateQRCode'
import FDialog from '@/FDialogNew'
import AssetDuplicationViewer from '@/AssetDuplicationViewer'
import BulkUpdateViewer from '@/BulkUpdateViewer'
import FloorMap from '@/FloorMap'
import { isEmpty } from '@facilio/utils/validation'
import QRious from 'qrious'
import JSZip from 'jszip'
import { saveAs } from 'file-saver'
import CategoryListDialog from '@/CategoryListDialog'
import { mapState, mapGetters } from 'vuex'
import {
  isWebTabsEnabled,
  findRouteForModule,
  getApp,
  pageTypes,
} from '@facilio/router'
import { API } from '@facilio/api'
import AssetAvatar from '@/avatar/Asset'
import DeleteDialog from 'src/pages/spacemanagement/overview/components/DeleteDialog.vue'
import OtherMixin from '@/mixins/OtherMixin'
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'

export default {
  mixins: [OtherMixin],
  extends: CommonModuleList,
  name: 'AssetList',
  components: {
    AssetDuplicationViewer,
    BulkUpdateViewer,
    AssetMapview,
    AssetAvatar,
    NewAsset,
    GenerateQRCode,
    FDialog,
    FloorMap,
    CategoryListDialog,
    DeleteDialog,
  },
  data() {
    return {
      records: [],
      bulkfieldlist: [
        'type',
        'department',
        'manufacturer',
        'supplier',
        'model',
        'unitPrice',
      ],
      bulkbool: false,
      loading: true,
      showPrintPreview: false,
      selectAll: false,
      selectedListItemsIds: [],
      selectedListItemsObj: [],
      duplicateAssetObj: {},
      recordCount: null,
      assetQr: [],
      deleteLoading: false,
      showColumnSettings: false,
      perPage: 50,
      qrParam: 'current',
      assetsForQr: null,
      fetchingAllAssets: false,
      showQrDialog: false,
      canGenerateZip: false,
      showAssetDuplicationDialog: false,
      canShowAssetCreation: false,
      canShowCategoryDialog: false,
      assetId: null,
      selectedCategory: null,
      errorMap: null,
      errorType: null,
      showDialog: false,
    }
  },
  created() {
    this.$store.dispatch('loadAssetType')
    this.$store.dispatch('loadTicketStatus', 'asset')
    this.$store.dispatch('loadAssetDepartment')
    this.$store.dispatch('loadAssetCategory')
  },
  mounted() {
    if (this.$account.user.orgId === 324) {
      this.bulkfieldlist.push('space')
    }
  },
  computed: {
    ...mapState({
      showSearch: state => state.search.active,
      assetCategory: state => state.assetCategory,
    }),
    ...mapGetters({
      getSiteById: 'getSite',
      getAsset_CategoryById: 'getAssetCategory',
      getAssetTypeById: 'getAssetType',
      getAssetDepartmentById: 'getAssetDepartment',
      getAssetCategoryByModule: 'getAssetCategoryByModule',
    }),
    parentPath() {
      return '/app/at'
    },
    modulePrefix() {
      return 'assets'
    },
    view() {
      if (this.$route.query.view) {
        let data = JSON.parse(this.$route.query.view)
        if (data.viewName) {
          return data.viewName
        } else {
          return false
        }
      } else {
        return false
      }
    },
    categoryList() {
      let categories = this.assetCategory
      if (!isEmpty(categories)) {
        return categories
      }
      return []
    },
    viewName() {
      let viewList = {
        all: 'All Assets',
        energy: 'Energy Assets',
        hvac: 'HVAC Assets',
        active: 'Active Assets',
        retired: 'Retired Assets',
      }
      let title = this.viewname
        ? this.$getProperty(viewList, this.viewname)
        : null

      if (title) return title
      return 'Assets'
    },
    mapAssets() {
      let location = []

      this.records.forEach(asset => {
        if (asset.currentLocation) {
          if (location.indexOf(asset.currentLocation) < 0) {
            location.push(asset.currentLocation)
          }
        }
      })

      let markers = this.assets.map(asset => {
        let { geoLocationEnabled, currentLocation } = asset

        if (geoLocationEnabled && currentLocation) {
          let location = {
            lat:
              currentLocation.split(',').length > 1
                ? Number(currentLocation.split(',')[0])
                : 0,
            lng:
              currentLocation.split(',').length > 1
                ? Number(currentLocation.split(',')[1])
                : 0,
          }
          let markerUrl =
            'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2ZXJzaW9uPSIxLjEiIGlkPSJMYXllcl8xIiB4PSIwcHgiIHk9IjBweCIgdmlld0JveD0iMCAwIDUxMiA1MTIiIHN0eWxlPSJlbmFibGUtYmFja2dyb3VuZDpuZXcgMCAwIDUxMiA1MTI7IiB4bWw6c3BhY2U9InByZXNlcnZlIiB3aWR0aD0iNTEyIiBoZWlnaHQ9IjUxMiIgY2xhc3M9IiI+PGc+PGc+Cgk8Zz4KCQk8cGF0aCBkPSJNMjU2LDBDMTE0LjgzNywwLDAsMTE0LjgzNywwLDI1NnMxMTQuODM3LDI1NiwyNTYsMjU2czI1Ni0xMTQuODM3LDI1Ni0yNTZTMzk3LjE2MywwLDI1NiwweiIgZGF0YS1vcmlnaW5hbD0iIzAwMDAwMCIgY2xhc3M9ImFjdGl2ZS1wYXRoIiBzdHlsZT0iZmlsbDojRjYxODQ1IiBkYXRhLW9sZF9jb2xvcj0iI0Y2MTk0NiI+PC9wYXRoPgoJPC9nPgo8L2c+PC9nPiA8L3N2Zz4='
          return { ...asset, location, markerUrl, marker: false }
        } else {
          return asset
        }
      })
      return markers
    },
    slotList() {
      return [
        {
          name: 'id',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 110,
            label: 'ID',
            fixed: 'left',
          },
        },
        {
          criteria: JSON.stringify({ name: 'name' }),
        },
        {
          name: 'editDelete',
          isActionColumn: true,
          columnAttrs: {
            width: 130,
            class: 'visibility-visible-actions',
            fixed: 'right',
          },
        },
      ]
    },
    printQrSelectedUrl() {
      let url
      let appName = getApp().linkName
      let encodedIds = encodeURIComponent(this.selectedListItemsIds.join(','))
      if (isWebTabsEnabled()) {
        url = `/${appName}/pdf/asset/${encodedIds}`
      } else {
        url = `/app/pdf/asset/${encodedIds}`
      }
      return window.location.protocol + '//' + window.location.host + url
    },
  },
  methods: {
    async bulkAction(actions) {
      let fields = {}
      for (let action of actions) {
        if (action.field === 'category') {
          let id = parseInt(action.valueArray)
          let cate = this.getAsset_CategoryById(id) || {}

          fields.category = {
            id,
            name: cate.displayName,
          }
        } else if (action.fieldObj[0].name === 'type') {
          fields.type = {
            id: action.valueArray,
            name: this.getAssetTypeById(parseInt(action.valueArray)).name,
          }
        } else if (action.fieldObj[0].name === 'department') {
          fields.department = {
            id: action.valueArray,
            name: this.getAssetDepartmentById(parseInt(action.valueArray)).name,
          }
        } else if (action.field === 'assignedTo') {
          if (action.value != null) {
            fields.assignedTo = { id: parseInt(action.value) }
          }
          if (action.valueArray != null) {
            fields.assignmentGroup = { id: parseInt(action.valueArray) }
          }
        } else {
          fields[action.field] = action.value
        }
      }
      let { moduleName, selectedListItemsIds } = this
      let recordMap = selectedListItemsIds.map(id => {
        return { ...(fields || {}), id }
      })
      let params = {
        moduleName: moduleName,
        data: {
          [moduleName]: recordMap,
        },
      }

      this.loading = true
      let { error } = await API.post(
        `v3/modules/bulkPatch/${moduleName}`,
        params
      )
      if (error) {
        this.$message.error(
          error?.message || this.$t('common._common.failed_to_update')
        )
      } else {
        this.$message.success(
          this.$t('common._common.bulk_asset_updated_successfully')
        )
      }
      await this.refreshRecordDetails()
      this.bulkbool = false
    },
    onQrSelected() {
      if (
        this.selectedListItemsIds.length === this.perPage &&
        this.recordCount > this.perPage
      ) {
        this.showQrDialog = true
      } else {
        this.qrParam = 'current'
        this.generateQRCode()
      }
    },
    async invokeAssetduplicationDialog(asset) {
      let { error, [this.moduleName]: record } = await API.fetchRecord(
        this.moduleName,
        { id: asset.id }
      )

      if (!error) {
        this.duplicateAssetObj = record || {}
      }
      this.showAssetDuplicationDialog = true
    },
    selectAsset(selectAssetcheck) {
      this.selectedListItemsObj = selectAssetcheck
      this.selectedListItemsIds = selectAssetcheck.map(value => value.id)
    },
    async generateQRCode() {
      let { canGenerateZip } = this
      let generate = () => {
        if (canGenerateZip) {
          this.generateQRZip()
        } else {
          this.showPrintPreview = true
          this.$nextTick(function() {
            window.print()
          })
        }
      }
      this.assetsForQr = []

      let fetchqr = () => {
        this.assetQr = this.assetsForQr.filter(asset => !asset.qrVal)
        if (this.assetQr.length) {
          let param = []
          for (let i in this.assetQr) {
            param.push(this.assetQr[i].id)
          }
          // eslint-disable-next-line @facilio/no-http
          this.$http
            .post('v2/assets/generateqr', { id: param })
            .then(response => {
              let keys = Object.keys(response.data.result.mappedqr)
              for (let i in keys) {
                let assetindex = this.records.find(asset => asset.id == keys[i])
                let assetForindex = this.assetsForQr.find(
                  asset => asset.id == keys[i]
                )
                let ai = this.records.indexOf(assetindex)
                let afi = this.assetsForQr.indexOf(assetForindex)
                this.records[ai].qrVal = response.data.result.mappedqr[keys[i]]
                this.assetsForQr[afi].qrVal =
                  response.data.result.mappedqr[keys[i]]
              }
              this.$nextTick(() => {
                generate()
              })
            })
            .catch(() => {
              this.fetchingAllAssets = false
            })
        } else {
          this.$nextTick(() => {
            generate()
          })
        }
      }
      if (this.qrParam === 'current') {
        this.showQrDialog = false
        this.assetsForQr = this.records.filter(asset =>
          this.selectedListItemsIds.includes(asset.id)
        )
        fetchqr()
      } else {
        this.fetchingAllAssets = true
        let { viewname, filters } = this
        let url =
          '/asset/' + viewname + '?selectFields[0]=name&selectFields[1]=qrVal'
        if (filters) {
          url = url + '&filters=' + encodeURIComponent(JSON.stringify(filters))
        }
        let { data, error } = await API.get(url)

        if (!error) {
          this.fetchingAllAssets = false
          this.showQrDialog = false
          this.assetsForQr = data.assets
          fetchqr()
        }
      }
    },
    generateQRZip() {
      let { assetsForQr } = this
      if (!isEmpty(assetsForQr)) {
        let qrZip = new JSZip()
        let qrZipFolder = qrZip.folder('asset-qr')
        assetsForQr.forEach(asset => {
          let { qrVal, name, siteId } = asset
          let siteName
          if (!isEmpty(siteId)) {
            let site = this.getSiteById(siteId)
            siteName = site ? site.name : ''
          }
          if (!isEmpty(qrVal) && !isEmpty(QRious)) {
            let value = new QRious({
              value: qrVal,
              size: 120,
            })
            // https://github.com/Stuk/jszip/issues/404
            // Have to remove data:image/png;base64
            let assetImgData = value.toDataURL()
            qrZipFolder.file(
              `${siteName}/${name.replace('/', '-')}.png`,
              assetImgData.split('base64,')[1],
              {
                base64: true,
                createFolder: true,
              }
            )
          }
        })
        qrZipFolder.generateAsync({ type: 'blob' }).then(content => {
          saveAs(content, 'asset-qr')
        })
      }
    },
    addAsset() {
      if (this.moduleName === 'asset') {
        this.canShowCategoryDialog = true
      } else {
        this.selectedCategory = this.getAssetCategoryByModule(this.moduleName)
        this.openAssetCreation()
      }
    },
    openAssetCreation(asset) {
      if (!isEmpty(asset)) {
        let { id, category } = asset
        let { id: categoryId } = category
        if (!isEmpty(categoryId)) {
          let selectedCategory = this.getAsset_CategoryById(categoryId)
          if (!isEmpty(selectedCategory)) {
            this.$set(this, 'selectedCategory', selectedCategory)
          }
        }
        this.$set(this, 'assetId', id)
      } else {
        this.$set(this, 'assetId', null)
      }
      this.$set(this, 'canShowCategoryDialog', false)
      this.$nextTick(() => {
        this.$set(this, 'canShowAssetCreation', true)
      })
    },
    async deleteAsset(idList) {
      let { moduleName } = this
      let { error } = await API.updateRecord(moduleName, {
        id: idList[0],
        data: {},
        params: { fetchChildCount: true, recordIds: idList },
      })
      if (!error || (error && error.code != 3)) {
        this.$dialog
          .confirm({
            title: this.$t('asset.assets.delete_asset'),
            message: this.$t('asset.assets.delete_asset_msg'),
            rbDanger: true,
            rbLabel: this.$t('common._common.delete'),
          })
          .then(async value => {
            if (value) {
              this.deleteLoading = true
              let { error } = await API.deleteRecord(moduleName, idList)

              if (error) {
                this.$message.error(this.$t('asset.assets.asset_delete_failed'))
              } else {
                this.$message.success(
                  this.$t('asset.assets.asset_delete_success')
                )
                await this.refreshRecordDetails()
              }
              this.deleteLoading = false
            }
          })
      } else {
        let map = JSON.parse(error.message)
        this.errorMap = map
        this.errorType = error.code
        this.showDialog = true
      }
    },
    openAssetOverview(id) {
      let { viewname, parentPath, $route } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('asset', pageTypes.OVERVIEW) || {}
        let route = {
          name,
          params: { viewname, id },
          query: $route.query,
        }
        return name && route
      } else {
        return {
          path: `${parentPath}/assets/${viewname}/${id}/overview`,
          query: $route.query,
        }
      }
    },
    switchView(command) {
      if (this.$route.query) {
        let query = this.$helpers.cloneObject(this.$route.query) || {}

        query.view =
          command === 'floormap'
            ? JSON.stringify({ viewName: command, hidePagination: true })
            : JSON.stringify({ viewName: command })
        this.$router.push({ query })
      }
    },
    unSelectRecords() {
      this.selectedListItemsIds = []
      this.selectedListItemsObj = []
    },
    redirectToQrPage() {
      window.open(this.printQrSelectedUrl)
    },
  },
}
</script>

<style>
.qr-dialog .el-radio + .el-radio {
  margin-left: 0px;
}
@media print {
  .fc-list-table-container .el-table {
    display: none !important;
  }
  .table-header-actions {
    display: none !important;
  }
  .fc-list-table-container {
    display: none !important;
  }
  .cm-common-list-layout .view-sub-header-container,
  .custom-module-list-layout .cm-list-container,
  .view-panel {
    display: none;
  }
}
.as-view-icon {
  width: 15px;
}
</style>
