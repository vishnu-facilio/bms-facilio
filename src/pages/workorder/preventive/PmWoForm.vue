<template>
  <div class="fc-pm-form-right-main2">
    <div v-if="!model.isEdit" class="heading-black22 mT20 mB20">
      {{ $t('maintenance.wr_list.new_pm') }}
    </div>
    <div v-else class="heading-black22 mT20 mB20">
      {{ $t('maintenance.wr_list.edit_pm') }}
    </div>
    <div v-if="loading" class="fc-pm-main-bg">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-show="!loading" class="fc-pm-main-bg">
      <div class="fc-pm-main-content2 pm-form-active-border-remove">
        <facilio-web-form
          :editObj="model.woData.woModel"
          :emitForm="model.emitForm"
          :name="'web_pm'"
          :model.sync="model.woData.woModel"
          @loaded="setAfterLoadParams"
          @validated="data => relay('validated', data)"
          @failed="data => relay('failed', data)"
          class="fc-pm-form"
        >
        </facilio-web-form>
        <div class="fc-pm-form-asset-container">
          <el-row>
            <el-col :span="4">
              <div class="fc-text-pink">
                {{ $t('maintenance.pm_list.pm_type') }}
              </div>
            </el-col>
            <el-col :span="19" class="mL8">
              <el-radio-group v-model="model.woData.workOrderType">
                <el-col :span="12">
                  <el-radio
                    label="single"
                    class="fc-radio-btn pm-formradio-label"
                    >{{ $t('maintenance.pm_list.single_wo') }}</el-radio
                  >
                  <div class="fc-grey2-text13 mL30">
                    {{ $t('maintenance.pm_list.create_single_wo') }}
                  </div>
                </el-col>
                <el-col :span="12">
                  <el-radio label="bulk" class="fc-radio-btn bold">{{
                    $t('maintenance.pm_list.multiple_workorder')
                  }}</el-radio>
                  <div class="fc-grey2-text13 mL30 width300px line-height20">
                    {{ $t('maintenance.pm_list.multi_wo_1') }} <br />
                    {{ $t('maintenance.pm_list.multi_wo_2') }}
                  </div>
                </el-col>
              </el-radio-group>
            </el-col>
          </el-row>
          <el-row class="mT30" v-if="model.woData.workOrderType === 'single'">
            <el-col :span="4">
              <div class="mT13 form-label-txt14">
                {{ $t('maintenance.wr_list.space_asset') }}
              </div>
            </el-col>
            <el-col :span="17" class="mL8">
              <el-input
                @change="
                  ;(model.woData.quickSearchQuery =
                    model.woData.spaceAssetDisplayName),
                    (singleAssetSpacevisibility = true)
                "
                v-model="model.woData.spaceAssetDisplayName"
                style="width:100%;max-width: 574px;"
                type="text"
                :placeholder="$t('maintenance.wr_list.to_search_type')"
                class="fc-input-full-border width100"
              >
                <i
                  @click="singleAssetSpacevisibility = true"
                  slot="suffix"
                  class="el-input__icon el-icon-search pm-form-search-icon"
                ></i>
              </el-input>
            </el-col>
          </el-row>
          <el-row class="mT30" v-if="model.woData.workOrderType === 'bulk'">
            <!-- <spinner :show="loadingOption" size="80"></spinner> -->
            <el-col :span="4">
              <div class="fc-dark-grey-txt14 mT10 mB10">
                {{ $t('maintenance.wr_list.category') }}
              </div>
            </el-col>
            <el-col :span="5" class="mL10">
              <el-select
                v-model="model.woData.resourceType"
                :loading="loadingOption"
                :disabled="
                  !(
                    (model.woData.woModel.site.id &&
                      model.woData.woModel.site.id > 0) ||
                    (model.woData.woModel.site && model.woData.woModel.site > 0)
                  )
                "
                class="fc-input-full-border-select2 width200px"
              >
                <el-option
                  v-show="showFloorOption"
                  :label="'All Floors'"
                  :value="'ALL_FLOORS'"
                ></el-option>
                <el-option
                  v-show="fetchedSpaces.length"
                  :label="'Space Category'"
                  :value="'SPACE_CATEGORY'"
                ></el-option>
                <el-option
                  v-show="fetchedAssets.length"
                  :label="'Asset Category'"
                  :value="'ASSET_CATEGORY'"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="1" class="mL20">
              <div class="fc-dark-grey-txt14 mT10 mB10 mL30">
                {{ $t('maintenance.pm_list.building') }}
              </div>
            </el-col>
            <el-col :span="5" class="mL65">
              <el-select
                v-model="model.woData.selectedBuilding"
                :disabled="
                  !(
                    model.woData.woModel.site.id &&
                    model.woData.woModel.site.id > 0
                  ) || !model.woData.resourceType
                "
                filterable
                clearable
                placeholder="Select"
                class="fc-input-full-border-select2 width200px"
                @change="onBuildingChange"
              >
                <el-option
                  v-for="(item, key) in buildings"
                  :key="key"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
          <div
            v-if="
              model.woData.workOrderType === 'bulk' &&
                !(
                  model.woData.woModel.site.id &&
                  model.woData.woModel.site.id > 0
                )
            "
            class="mT20 form-msg-txt"
          >
            *{{ $t('maintenance.pm_list.please_choose_site') }}.
          </div>
          <el-row
            class="mT30"
            v-if="
              model.woData.workOrderType === 'bulk' &&
                model.woData.resourceType === 'SPACE_CATEGORY'
            "
          >
            <el-col :span="4">
              <div class="fc-dark-grey-txt14 mT10">
                {{ $t('maintenance.pm_list.space_category') }}
              </div>
            </el-col>
            <el-col :span="5" class="mR30 mL8">
              <el-select
                v-model="model.woData.spacecategoryId"
                filterable
                :placeholder="$t('maintenance.pm_list.space_category')"
                class="fc-input-full-border-select2 width200px"
              >
                <el-option
                  v-for="(item, key) in spacecategory"
                  :key="key"
                  :label="item"
                  :value="key"
                  v-if="fetchedSpaces.includes(Number(key))"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="1" class="mL20">
              <div
                v-if="model.woData.resourceType === 'SPACE_CATEGORY'"
                class="fc-dark-grey-txt14 mT10 mB10"
              >
                {{ $t('maintenance.pm_list.space') }}
              </div>
            </el-col>
            <el-col :span="5" class="mL40 position-relative">
              <el-select
                class="multi suffix-disabled fc-input-full-border-select2 width200px fc-tag tag-overflow"
                filterable
                multiple
                v-model="dummyValue1"
                disabled
              >
                <el-option :label="spaceLabel" :value="1"></el-option>
              </el-select>
              <i
                @click=";(showAsset = false), (chooserVisibility = true)"
                class="el-icon-search web-form-search-icon"
              ></i>
            </el-col>
          </el-row>
          <el-row
            class="mT30"
            v-else-if="
              model.woData.workOrderType === 'bulk' &&
                model.woData.resourceType === 'ASSET_CATEGORY'
            "
          >
            <el-col :span="4">
              <div class="fc-dark-grey-txt14 mT10">
                {{ $t('maintenance.pm_list.asset_category') }}
              </div>
            </el-col>
            <el-col :span="5" class="mR30 mL8">
              <el-select
                v-model="model.woData.assetCategoryId"
                :placeholder="$t('maintenance.pm_list.asset_category')"
                filterable
                class="fc-input-full-border-select2 width200px fc-tag tag-overflow"
              >
                <el-option
                  v-for="(item, key) in assetcategory"
                  :key="key"
                  :label="item.name"
                  :value="item.id"
                  v-if="fetchedAssets.includes(Number(item.id))"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="1" class="mL20">
              <div
                v-if="model.woData.resourceType === 'ASSET_CATEGORY'"
                class="fc-dark-grey-txt14 mT10 mB10"
              >
                {{ $t('maintenance._workorder.asset') }}
              </div>
            </el-col>
            <el-col
              :span="5"
              v-if="model.woData.resourceType === 'ASSET_CATEGORY'"
              class="position-relative mL40"
            >
              <el-select
                class="multi suffix-disabled fc-input-full-border-select2 width200px fc-tag tag-overflow"
                filterable
                multiple
                v-model="dummyValue"
                disabled
              >
                <el-option :label="resourceLabel" :value="1"></el-option>
              </el-select>
              <i
                @click=";(showAsset = true), (chooserVisibility = true)"
                class="el-icon-search web-form-search-icon"
              ></i>
            </el-col>
          </el-row>
          <el-row
            v-if="
              model.woData.workOrderType === 'bulk' &&
                model.woData.resourceType === 'ALL_FLOORS' &&
                model.woData.selectedBuilding
            "
            class="mT30"
          >
            <el-col :span="4" class="mT20">
              <div class="fc-dark-grey-txt14">Floor</div>
            </el-col>
            <el-col :span="15" class="position-relative mL8">
              <el-select
                class="multi mT10 suffix-disabled fc-input-full-border-select2 width500px fc-tag"
                filterable
                multiple
                v-model="dummyValue2"
                disabled
              >
                <el-option :label="floorLabel" :value="1"></el-option>
              </el-select>
              <i
                @click=";(showAsset = false), (chooserVisibility = true)"
                class="el-icon-search web-form-search-icon2"
              ></i>
            </el-col>
          </el-row>
          <div class="mT40">
            <div v-if="model.woData.workOrderType === 'bulk'">
              <el-checkbox
                v-model="model.woData.isPreventOnNoTask"
                class="fw4"
                >{{
                  $t('maintenance.pm_list.prevent_pm_with_no_task')
                }}</el-checkbox
              >
            </div>
            <div class="mT10">
              <el-checkbox
                v-model="model.woData.isSignatureRequired"
                class="fw4"
                >{{ $t('maintenance.pm_list.enable_e_sign') }}</el-checkbox
              >
            </div>
            <div class="mT10">
              <el-checkbox v-model="model.woData.sendForApproval" class="fw4"
                >Send for approval</el-checkbox
              >
            </div>
            <div class="mT10" v-if="isWorkPermitLicenseEnabled">
              <el-checkbox v-model="model.woData.workPermitNeeded" class="fw4"
                >Work permit required</el-checkbox
              >
            </div>
            <div class="mT10 flex align-center">
              <el-checkbox v-model="model.woData.isOffsetWOCreation" class="fw4"
                >Pre-generate work orders before</el-checkbox
              >
              <div
                v-if="model.woData.isOffsetWOCreation"
                class="flex items-baseline mL15"
              >
                <span>Days</span>
                <el-input
                  v-model="model.woData.offsetDays"
                  class="mL15 input-w35 fw4"
                ></el-input>
              </div>
              <div
                v-if="model.woData.isOffsetWOCreation"
                class="flex items-baseline mL15"
              >
                <span>Hours</span>
                <el-input
                  class="mL15 input-w35 fw4"
                  v-model="model.woData.offsetHours"
                ></el-input>
              </div>
            </div>
            <div class="mT10">
              <el-checkbox v-model="model.woData.markIgnoredWo" class="fw4"
                >Skip workorders if not started till next due</el-checkbox
              >
            </div>
          </div>
        </div>
        <div
          v-if="model.woData.workOrderType === 'bulk'"
          class="fc-pm-form-footer-content"
        >
          <el-row class="fc-pm-border-top">
            <el-col :span="1">
              <img
                src="~assets/monitor-black.svg"
                width="24px"
                height="24px"
                class="mT25"
              />
            </el-col>
            <el-col :span="20">
              <div class="fc-black-15 mT15 mL10">
                <span class="bold">
                  {{ model.woData.resourceList.length }}
                  {{ $t('maintenance.pm_list.workorders') }}
                </span>
                {{ $t('maintenance.pm_list.no_of_workorder_mapped_1') }} <br />
                {{ $t('maintenance.pm_list.no_of_workorder_mapped_2') }}
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          @click="moveToNext"
          type="button"
          class="fc-full-btn-fill-green f13"
          >PROCEED TO NEXT
          <img
            src="~assets/arrow-pointing-white-right.svg"
            width="17px"
            class="fR"
        /></el-button>
      </div>
    </div>
    <space-asset-multi-chooser
      @associate="associateResource"
      :hideBanner="true"
      :visibility.sync="chooserVisibility"
      :query="model.woData.resourceQuery"
      :showAsset="showAsset"
      :disable="true"
      :initialValues="initialValues"
      :filter="filter"
      :resourceType="resourceFilter"
    ></space-asset-multi-chooser>
    <space-asset-chooser
      v-if="singleAssetSpacevisibility"
      @associate="associateSingleAssetSpace"
      :visibility.sync="singleAssetSpacevisibility"
      :quickSearchQuery="model.woData.quickSearchQuery"
      :filter="filter"
    ></space-asset-chooser>
  </div>
</template>
<script>
import FacilioWebForm from '@/FacilioWebForm'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import { mapState, mapGetters } from 'vuex'
import PMMixin from '@/mixins/PMMixin'
export default {
  mixins: [PMMixin],
  props: ['model', 'tenant'],
  components: {
    FacilioWebForm,
    SpaceAssetMultiChooser,
    SpaceAssetChooser,
  },
  data() {
    return {
      loadingOption: false,
      showFloorOption: true,
      showSpaceCategOption: true,
      showAssetCategOption: true,
      fetchedAssets: [],
      fetchedSpaces: [],
      buildings: [],
      dummyValue: [1],
      dummyValue1: [1],
      dummyValue2: [1],
      chooserVisibility: false,
      showAsset: true,
      singleAssetSpacevisibility: false,
      loading: true,
    }
  },
  watch: {
    'model.isLoading': function(val) {
      if (!this.model.isLoading && this.model.isEdit) {
        this.onBuildingChange(this.model.woData.selectedBuilding)
      }
    },
    'model.woData.woModel.site.id': {
      handler: function(val, oldVal) {
        if (this.model.isLoading) {
          return
        }
        if (this.model.woData.woModel.hasOwnProperty('groups')) {
          this.model.woData.woModel.groups.id = ''
        }
        this.model.woData.singleResource = null
        this.model.woData.selectedBuilding = null
        this.model.woData.resourceType = null
        this.model.woData.spacecategoryId = null
        this.model.woData.assetCategoryId = null
        this.model.woData.resourceList = []
        this.model.woData.spaceAssetDisplayName = null
        this.getScopeFilteredValues(val)
      },
      deep: true,
    },
    'model.woData.workOrderType': function(val) {
      if (val && val === 'single') {
        this.model.woData.spacecategoryId = null
        this.model.woData.assetCategoryId = null
        this.model.woData.selectedBuilding = null
        this.model.woData.resourceType = null
        this.model.woData.selectedResourceList = []
        this.model.woData.selectedFloorList = []
        this.model.woData.selectedSpaceList = []
        this.$set(this.model.woData, 'resourceList', [])
      } else if (val && val === 'bulk') {
        this.model.woData.singleResource = null
        this.model.woData.spaceAssetDisplayName = null
      }
    },
    'model.woData.singleResource': function(val) {
      if (val) {
        this.model.woData.spaceAssetDisplayName = val.name
      }
    },
    'model.woData.spacecategoryId': {
      handler: function(newVal, oldVal) {
        if (this.model.woData.workOrderType === 'bulk') {
          if (newVal !== oldVal) {
            this.resourceListHandler(newVal, oldVal)
          }
        }
      },
      deep: true,
    },
    'model.woData.assetCategoryId': {
      handler: function(newVal, oldVal) {
        if (this.model.woData.workOrderType === 'bulk') {
          if (newVal !== oldVal) {
            this.resourceListHandler(newVal, oldVal)
          }
        }
      },
      deep: true,
    },
    'model.woData.resourceType': {
      handler: function(newVal, oldVal) {
        if (this.model.isLoading) {
          return
        }
        this.model.woData.selectedBuilding = null
        this.model.woData.spacecategoryId = null
        this.model.woData.assetCategoryId = null
        this.model.woData.selectedResourceList = []
        this.model.woData.selectedFloorList = []
        this.model.woData.selectedSpaceList = []
        this.$set(this.model.woData, 'resourceList', [])
        this.resourceListAllFloorsHandler(newVal, oldVal)
      },
      deep: true,
    },
    'model.woData.selectedBuilding': function(newVal, oldVal) {
      if (this.model.woData.workOrderType === 'bulk') {
        if (this.model.isLoading) {
          return
        }
        this.model.woData.selectedResourceList = []
        this.model.woData.selectedFloorList = []
        this.model.woData.selectedSpaceList = []
        this.model.woData.spacecategoryId = null
        this.model.woData.assetCategoryId = null
        this.$set(this.model.woData, 'resourceList', [])
      }
    },
    'model.woData.selectedFloorList': {
      handler: function(newVal, oldVal) {
        this.resourceListAllFloorsHandler(newVal, oldVal)
      },
      deep: true,
    },
    'model.woData.selectedSpaceList': {
      handler: function(newVal, oldVal) {
        this.resourceListAllFloorsHandler(newVal, oldVal)
      },
      deep: true,
    },
    'model.woData.selectedResourceList': {
      handler: function(newVal, oldVal) {
        this.resourceListAllFloorsHandler(newVal, oldVal)
      },
      deep: true,
    },
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadBuildings')
  },
  computed: {
    ...mapState({
      assetcategory: state => state.assetCategory,
    }),
    ...mapGetters(['getSpaceCategoryPickList']),
    spacecategory() {
      return this.getSpaceCategoryPickList()
    },
    categoryName() {
      if (this.model.woData.assetCategoryId > 0 && this.assetcategory) {
        let category = this.assetcategory.find(
          category => category.id === this.model.woData.assetCategoryId
        )
        if (category) {
          return category.name
        }
      }
      return ''
    },
    isWorkPermitLicenseEnabled() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers || {}

      return isLicenseEnabled('WORK_PERMIT')
    },
    spaceCategoryName() {
      if (this.model.woData.spacecategoryId > 0 && this.spacecategory) {
        let category = this.spacecategory[this.model.woData.spacecategoryId]
        if (category) {
          return category
        }
      }
      return ''
    },
    floorLabel() {
      this.dummyValue2 = [1]
      if (this.model.woData.selectedFloorList) {
        let message
        let selectedCount = this.model.woData.selectedFloorList.length
        if (selectedCount) {
          message = `${selectedCount} Floors`
        } else {
          message = `All Floors`
        }
        this.model.woData.resourceLabel = message
        return message
      } else {
        this.dummyValue2 = []
      }
    },
    spaceLabel() {
      this.dummyValue1 = [1]
      if (this.model.woData.spacecategoryId > 0) {
        let message
        let selectedCount = this.model.woData.selectedSpaceList.length
        if (selectedCount) {
          message = `${selectedCount} ${this.spaceCategoryName}`
        } else {
          message = `All ${this.spaceCategoryName}`
        }
        this.model.woData.resourceLabel = message
        return message
      } else {
        this.dummyValue1 = []
      }
    },
    resourceLabel() {
      this.dummyValue = [1]
      if (this.model.woData.assetCategoryId > 0) {
        let message
        let selectedCount = this.model.woData.selectedResourceList.length
        if (selectedCount) {
          if (selectedCount === 1) {
            return this.model.woData.selectedResourceList[0].name
          }
          message = `${selectedCount} ${this.categoryName}`
        } else {
          message = `All ${this.categoryName}`
        }
        this.model.woData.resourceLabel = message
        return message
      } else {
        this.dummyValue = []
      }
    },
    initialValues() {
      if (this.model.woData.resourceType === 'ASSET_CATEGORY') {
        return {
          assetCategory: this.model.woData.assetCategoryId,
          isIncludeResource: this.model.woData.isIncludeResource,
          selectedResources: this.model.woData.selectedResourceList.map(
            resource => ({
              id:
                resource && typeof resource === 'object'
                  ? resource.id
                  : resource,
            })
          ),
        }
      } else if (this.model.woData.resourceType === 'SPACE_CATEGORY') {
        return {
          spaceCategory: this.model.woData.spacecategoryId,
          isIncludeResource: this.model.woData.isIncludeSpace,
          selectedResources: this.model.woData.selectedSpaceList.map(
            resource => ({
              id:
                resource && typeof resource === 'object'
                  ? resource.id
                  : resource,
            })
          ),
        }
      } else if (this.model.woData.resourceType === 'ALL_FLOORS') {
        return {
          isIncludeResource: this.model.woData.isIncludeFloor,
          selectedResources: this.model.woData.selectedFloorList.map(
            resource => ({
              id:
                resource && typeof resource === 'object'
                  ? resource.id
                  : resource,
            })
          ),
        }
      }
    },
    filter() {
      let filter = {}
      if (this.model.woData.woModel && this.model.woData.woModel.site.id) {
        filter.site = Number(this.model.woData.woModel.site.id)
      }
      if (
        this.model.woData.selectedBuilding &&
        this.model.woData.workOrderType === 'bulk'
      ) {
        filter.buildingId = Number(this.model.woData.selectedBuilding)
      }
      return filter
    },
    resourceFilter() {
      if (this.model.woData.resourceType === 'ALL_FLOORS') {
        return [3]
      }
      return null
    },
  },
  methods: {
    relay(event, data) {
      this.$emit(event, data)
    },
    resetEmit() {
      this.model.emitForm = false
    },
    setAfterLoadParams() {
      this.loading = false
      if (this.tenant) {
        this.model.woData.woModel.tenant.id = this.tenant
      }
    },
    getScopeFilteredValues(siteId, buildingId) {
      if (!siteId && !buildingId) {
        return
      }
      let params = ''
      if (siteId) {
        params = `siteId=${siteId}`
      }
      if (buildingId) {
        params = params
          ? `${params}&buildingId=${buildingId}`
          : `buildingId=${buildingId}`
      }
      this.loadingOption = true
      this.$http
        .get(`/workorder/getScopeFilteredValuesForPM?${params}`)
        .then(response => {
          this.showFloorOption = response.data.hasFloor
          this.fetchedAssets = response.data.assetCategoryIds
          this.fetchedSpaces = response.data.spaceCategoryIds
          this.buildings = response.data.buildings || []
          this.loadingOption = false
        })
    },
    onBuildingChange(buildingId) {
      this.getScopeFilteredValues(this.model.woData.woModel.site.id, buildingId)
    },
    resourceListHandler() {
      if (this.model.isEdit && this.model.isLoading) {
        return
      }
      if (this.model.woData.workOrderType === 'bulk') {
        if (this.model.woData.resourceType === 'SPACE_CATEGORY') {
          if (
            this.model.woData.selectedSpaceList &&
            this.model.woData.selectedSpaceList.length !== 0
          ) {
            if (
              this.model.woData.isIncludeSpace === null ||
              this.model.woData.isIncludeSpace === undefined ||
              this.model.woData.isIncludeSpace
            ) {
              let presentInSpaceList = {}
              this.model.woData.selectedSpaceList.forEach(
                i => (presentInSpaceList[i.id] = true)
              )
              let presentInRPList = {}
              this.model.woData.resourceList.forEach(resource => {
                presentInRPList[resource.id] = true
              })
              this.model.woData.selectedSpaceList.forEach(i => {
                if (!presentInRPList[i.id]) {
                  this.model.woData.resourceList.push({
                    id: i.id,
                    name: i.name,
                    triggerNames: ['0'],
                    assignedTo: null,
                    notifications: [],
                    selected: false,
                  })
                }
              })
              let newResourceList = []
              this.model.woData.resourceList.forEach(resource => {
                if (presentInSpaceList[resource.id]) {
                  newResourceList.push(resource)
                }
              })
              this.$set(this.model.woData, 'resourceList', newResourceList)
            } else {
              if (
                this.model.woData.spacecategoryId &&
                this.model.woData.spacecategoryId > 0
              ) {
                this.$util
                  .loadSpacesContext(4, null, [
                    {
                      key: 'site',
                      operator: 'is',
                      value: this.model.woData.woModel.site.id,
                    },
                    {
                      key: 'building',
                      operator: 'is',
                      value: this.model.woData.selectedBuilding,
                    },
                    {
                      key: 'spaceCategory',
                      operator: 'is',
                      value: this.model.woData.spacecategoryId,
                    },
                  ])
                  .then(response => {
                    this.$set(
                      this.model.woData,
                      'resourceList',
                      response.records
                    )
                    this.model.woData.resourceList = this.model.woData.resourceList.filter(
                      i => {
                        for (
                          let j = 0;
                          j < this.model.woData.selectedSpaceList.length;
                          j++
                        ) {
                          if (
                            this.model.woData.selectedSpaceList[j].id === i.id
                          ) {
                            return false
                          }
                        }
                        return true
                      }
                    )
                    this.model.woData.resourceList.forEach(resource => {
                      resource.triggerNames = ['0']
                      resource.assignedTo = null
                      resource.notifications = []
                      resource.selected = false
                    })
                  })
              }
            }
          } else {
            if (
              this.model.woData.spacecategoryId &&
              this.model.woData.spacecategoryId > 0
            ) {
              this.$util
                .loadSpacesContext(4, null, [
                  {
                    key: 'site',
                    operator: 'is',
                    value: this.model.woData.woModel.site.id,
                  },
                  {
                    key: 'building',
                    operator: 'is',
                    value: this.model.woData.selectedBuilding,
                  },
                  {
                    key: 'spaceCategory',
                    operator: 'is',
                    value: this.model.woData.spacecategoryId,
                  },
                ])
                .then(response => {
                  this.$set(this.model.woData, 'resourceList', response.records)
                  this.model.woData.resourceList.forEach(resource => {
                    resource.triggerNames = ['0']
                    resource.assignedTo = null
                    resource.notifications = []
                    resource.selected = false
                  })
                })
            }
          }
        } else if (this.model.woData.resourceType === 'ASSET_CATEGORY') {
          if (
            this.model.woData.selectedResourceList &&
            this.model.woData.selectedResourceList.length !== 0
          ) {
            if (
              this.model.woData.isIncludeResource === null ||
              this.model.woData.isIncludeResource === undefined ||
              this.model.woData.isIncludeResource
            ) {
              let presentInAssetList = {}
              this.model.woData.selectedResourceList.forEach(
                i => (presentInAssetList[i.id] = true)
              )
              let presentInRPList = {}
              this.model.woData.resourceList.forEach(resource => {
                presentInRPList[resource.id] = true
              })
              this.model.woData.selectedResourceList.forEach(i => {
                if (!presentInRPList[i.id]) {
                  this.model.woData.resourceList.push({
                    id: i.id,
                    name: i.name,
                    triggerNames: ['0'],
                    assignedTo: null,
                    notifications: [],
                    selected: false,
                  })
                }
              })
              let newResourceList = []
              this.model.woData.resourceList.forEach(resource => {
                if (presentInAssetList[resource.id]) {
                  newResourceList.push(resource)
                }
              })
              this.$set(this.model.woData, 'resourceList', newResourceList)
            } else {
              if (
                this.model.woData.assetCategoryId &&
                this.model.woData.assetCategoryId > 0
              ) {
                let spaceId = this.model.woData.selectedBuilding
                if (
                  !this.model.woData.selectedBuilding ||
                  this.model.woData.selectedBuilding < 0
                ) {
                  spaceId = this.model.woData.woModel.site.id
                }
                this.$util
                  .loadAsset({
                    spaceId: spaceId,
                    categoryId: this.model.woData.assetCategoryId,
                  })
                  .then(response => {
                    this.$set(
                      this.model.woData,
                      'resourceList',
                      response.assets.filter(i => {
                        for (
                          let j = 0;
                          j < this.model.woData.selectedResourceList.length;
                          j++
                        ) {
                          if (
                            this.model.woData.selectedResourceList[j].id ===
                            i.id
                          ) {
                            return false
                          }
                        }
                        return true
                      })
                    )
                    this.model.woData.resourceList.forEach(resource => {
                      resource.triggerNames = ['0']
                      resource.assignedTo = null
                      resource.notifications = []
                      resource.selected = false
                    })
                  })
              }
            }
          } else {
            if (
              this.model.woData.assetCategoryId &&
              this.model.woData.assetCategoryId > 0
            ) {
              let spaceId = this.model.woData.selectedBuilding
              if (
                !this.model.woData.selectedBuilding ||
                this.model.woData.selectedBuilding < 0
              ) {
                spaceId = this.model.woData.woModel.site.id
              }
              this.$util
                .loadAsset({
                  spaceId: spaceId,
                  categoryId: this.model.woData.assetCategoryId,
                })
                .then(response => {
                  this.$set(this.model.woData, 'resourceList', response.assets)
                  this.model.woData.resourceList.forEach(resource => {
                    resource.normalizedName = resource.name.trim().toLowerCase()
                    resource.triggerNames = ['0']
                    resource.assignedTo = null
                    resource.notifications = []
                    resource.selected = false
                  })
                })
            }
          }
        }
      }
    },
    resourceListAllFloorsHandler(newVal, oldVal) {
      if (this.model.isEdit && this.model.isLoading) {
        return
      }
      if (this.model.woData.workOrderType === 'bulk' && newVal !== oldVal) {
        if (this.model.woData.resourceType === 'ALL_FLOORS') {
          if (
            this.model.woData.selectedFloorList &&
            this.model.woData.selectedFloorList.length !== 0
          ) {
            if (
              this.model.woData.isIncludeFloor === null ||
              this.model.woData.isIncludeFloor === undefined ||
              this.model.woData.isIncludeFloor
            ) {
              let presentInFloorList = {}
              this.model.woData.selectedFloorList.forEach(
                i => (presentInFloorList[i.id] = true)
              )
              let presentInRPList = {}
              this.model.woData.resourceList.forEach(resource => {
                presentInRPList[resource.id] = true
              })
              this.model.woData.selectedFloorList.forEach(i => {
                if (!presentInRPList[i.id]) {
                  this.model.woData.resourceList.push({
                    id: i.id,
                    name: i.name,
                    triggerNames: ['0'],
                    assignedTo: null,
                    notifications: [],
                    selected: false,
                  })
                }
              })
              let newResourceList = []
              this.model.woData.resourceList.forEach(resource => {
                if (presentInFloorList[resource.id]) {
                  newResourceList.push(resource)
                }
              })
              this.$set(this.model.woData, 'resourceList', newResourceList)
            } else {
              this.$util
                .loadSpace([3], null, [
                  { key: 'site', value: this.model.woData.woModel.site.id },
                  {
                    key: 'building',
                    value: this.model.woData.selectedBuilding,
                  },
                ])
                .then(data => {
                  let rList = data.basespaces.filter(i => {
                    for (
                      let j = 0;
                      j < this.model.woData.selectedFloorList.length;
                      j++
                    ) {
                      if (this.model.woData.selectedFloorList[j].id === i.id) {
                        return false
                      }
                    }
                    return true
                  })
                  this.$set(this.model.woData, 'resourceList', rList)
                  this.model.woData.resourceList.forEach(resource => {
                    resource.triggerNames = ['0']
                    resource.assignedTo = null
                    resource.notifications = []
                    resource.selected = false
                  })
                })
            }
          } else {
            this.$util
              .loadSpace([3], null, [
                { key: 'site', value: this.model.woData.woModel.site.id },
                { key: 'building', value: this.model.woData.selectedBuilding },
              ])
              .then(data => {
                this.$set(this.model.woData, 'resourceList', data.basespaces)
                this.model.woData.resourceList.forEach(resource => {
                  resource.triggerNames = ['0']
                  resource.assignedTo = null
                  resource.notifications = []
                  resource.selected = false
                })
              })
          }
        }
      }
    },
    associateResource(selectedObj) {
      if (selectedObj.resourceList && selectedObj.resourceList.length) {
        if (this.model.woData.resourceType === 'ASSET_CATEGORY') {
          this.model.woData.selectedResourceList = selectedObj.resourceList
          this.model.woData.isIncludeResource = selectedObj.isInclude
        } else if (this.model.woData.resourceType === 'SPACE_CATEGORY') {
          this.model.woData.selectedSpaceList = selectedObj.resourceList
          this.model.woData.isIncludeSpace = selectedObj.isInclude
        } else if (this.model.woData.resourceType === 'ALL_FLOORS') {
          this.model.woData.selectedFloorList = selectedObj.resourceList
          this.model.woData.isIncludeFloor = selectedObj.isInclude
        }
        this.resourceListHandler()
      } else {
        this.model.woData.selectedResourceList = []
        this.model.woData.isIncludeResource = true
        this.model.woData.selectedSpaceList = []
        this.model.woData.isIncludeSpace = true
        this.model.woData.selectedFloorList = []
        this.model.woData.isIncludeFloor = true
      }
      this.chooserVisibility = false
      this.model.woData.resourceQuery = null
    },
    associateSingleAssetSpace(selectedObj) {
      this.singleAssetSpacevisibility = false
      this.model.woData.spaceAssetDisplayName = selectedObj.name
      this.model.woData.singleResource = selectedObj
    },
    moveToNext() {
      let isValid = this.validateWO()
      if (isValid) {
        this.$emit('next')
      }
    },
  },
}
</script>
<style lang="scss">
.suffix-disabled .el-select__caret {
  display: none;
}
.input-w35 {
  width: 35px;
}
</style>
