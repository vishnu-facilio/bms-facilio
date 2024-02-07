<template>
  <div class="fc-pm-form-right-main2 fc-pm-multipm-con">
    <div v-if="!model.isEdit" class="heading-black22 mT20 mB20">
      {{ $t('maintenance.wr_list.new_pm') }}
    </div>
    <div v-else class="heading-black22 mT20 mB20">
      {{ $t('maintenance.wr_list.edit_pm') }}
    </div>
    <div class="fc-pm-main-bg fc-pm-multi-bg-con pB150">
      <div class="fc-pm-main-content2 pm-form-active-border-remove">
        <div class="fc-pm-form">
          <div class="fc-pm-main-content-H">
            CONFIGURATION
            <div class="fc-heading-border-width43 mT15"></div>
          </div>
          <el-row>
            <el-col :span="4">
              <div class="fc-dark-grey-txt14 mT10 mB10">
                Sites
              </div>
            </el-col>
            <el-col :span="16" class="fc-width-reduce-pm-col">
              <FLookupField
                :model.sync="model.woData.sites"
                :field="siteField"
                :hideDropDown="false"
                @showLookupWizard="showLookupWizard"
              />

              <FLookupFieldWizard
                v-if="canShowLookupWizard"
                :canShowLookupWizard.sync="canShowLookupWizard"
                :selectedLookupField="selectedLookupField"
                :withReadings="true"
                @setLookupFieldValue="setLookupFieldValue"
              />
            </el-col>
          </el-row>
          <div
            :class="{
              activeSingleSite:
                model.woData.sites && model.woData.sites.length <= 1,
            }"
          >
            <el-row class="mT30">
              <!-- <spinner :show="loadingOption" size="80"></spinner> -->
              <el-col :span="4">
                <div class="fc-dark-grey-txt14 mT10 mB10">
                  {{ $t('maintenance.wr_list.category') }}
                </div>
              </el-col>
              <el-col :span="5" class="fc-width-reduce-pm-col">
                <el-select
                  v-model="model.woData.resourceType"
                  :loading="loadingOption"
                  :disabled="
                    !(model.woData.sites && model.woData.sites.length > 0)
                  "
                  class="fc-input-full-border-select2 width575px"
                >
                  <el-option
                    v-show="showFloorOption && model.woData.sites.length === 1"
                    :label="'All Floors'"
                    :value="'ALL_FLOORS'"
                  ></el-option>
                  <el-option
                    v-show="fetchedSpaces.length"
                    :label="'Space Category'"
                    :value="'SPACE_CATEGORY'"
                  >
                  </el-option>
                  <el-option
                    v-show="fetchedAssets.length"
                    :label="'Asset Category'"
                    :value="'ASSET_CATEGORY'"
                  >
                  </el-option>
                  <el-option
                    v-show="buildings && buildings.length"
                    :label="'Buildings'"
                    :value="'BUILDINGS'"
                  ></el-option>
                  <!-- Add option for Site Level PM-->
                  <el-option
                    v-show="model.woData.sites && model.woData.sites.length > 0"
                    :label="'Sites'"
                    :value="'ALL_SITES'"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
            <el-row
              v-if="model.woData.sites && model.woData.sites.length <= 1"
              class="mT30"
            >
              <!-- <spinner :show="loadingOption" size="80"></spinner> -->
              <el-col
                :span="4"
                class="fc-building-col-left"
                v-if="
                  model.woData.resourceType != 'BUILDINGS' &&
                    model.woData.resourceType != 'ALL_SITES'
                "
              >
                <div class="fc-dark-grey-txt14 mT10 mB10">
                  {{ $t('maintenance.pm_list.building') }}
                </div>
              </el-col>
              <el-col
                :span="5"
                class="fc-width-reduce-pm-col2"
                v-if="
                  model.woData.resourceType != 'BUILDINGS' &&
                    model.woData.resourceType != 'ALL_SITES'
                "
              >
                <el-select
                  v-model="model.woData.selectedBuilding"
                  :disabled="
                    !(model.woData.sites && model.woData.sites.length > 0) ||
                      !model.woData.resourceType
                  "
                  filterable
                  clearable
                  placeholder="Select"
                  class="fc-input-full-border-select2 width575px"
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
          </div>
          <el-row>
            <div
              v-if="!(model.woData.sites && model.woData.sites.length > 0)"
              class="mT20 form-msg-txt"
            >
              *{{ $t('maintenance.pm_list.please_choose_site') }}.
            </div>
          </el-row>
          <el-row class="mT30" v-if="model.woData.resourceType === 'BUILDINGS'">
            <el-col :span="4" class="fc-building-col-left">
              <div class="fc-dark-grey-txt14 mT10 mB10">
                {{ $t('maintenance.pm_list.building') }}
              </div>
            </el-col>
            <el-col class="width575px">
              <el-select
                v-model="model.woData.selectedBuildingList"
                multiple
                class="fc-select-multiple-tag width100 fc-tag width575px"
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
          <el-row
            class="mT30"
            v-if="model.woData.resourceType === 'SPACE_CATEGORY'"
          >
            <el-col :span="4">
              <div class="fc-dark-grey-txt14 mT10">
                {{ $t('maintenance.pm_list.space_category') }}
              </div>
            </el-col>
            <el-col :span="5" class="mR30">
              <el-select
                v-model="model.woData.spacecategoryId"
                filterable
                :placeholder="$t('maintenance.pm_list.space_category')"
                class="fc-input-full-border-select2 pm-select-width"
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
            <el-col :span="2" class="mL40">
              <div
                v-if="model.woData.resourceType === 'SPACE_CATEGORY'"
                class="fc-dark-grey-txt14 mT10 mB10 position-relative fc-pm-left23"
              >
                {{ $t('maintenance.pm_list.space') }}
              </div>
            </el-col>
            <el-col :span="5" class="mL40 position-relative">
              <el-select
                class="multi suffix-disabled fc-input-full-border-select2 fc-tag tag-overflow pm-select-width"
                filterable
                multiple
                collapse-tags
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
            v-else-if="model.woData.resourceType === 'ASSET_CATEGORY'"
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
                collapse-tags
                class="fc-input-full-border-select2 pm-select-width fc-tag tag-overflow"
              >
                <el-option
                  v-for="(item, key) in assetcategory"
                  :key="key"
                  :label="item.displayName"
                  :value="item.id"
                  v-if="fetchedAssets.includes(Number(item.id))"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="2" class="mL35">
              <div
                v-if="model.woData.resourceType === 'ASSET_CATEGORY'"
                class="fc-dark-grey-txt14 mT10 mB10 position-relative fc-pm-left23"
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
                class="multi suffix-disabled fc-input-full-border-select2 pm-select-width fc-tag tag-overflow"
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
              model.woData.resourceType === 'ALL_FLOORS' &&
                model.woData.selectedBuilding
            "
            class="mT20"
          >
            <el-col :span="4" class="mT20">
              <div class="fc-dark-grey-txt14">Floor</div>
            </el-col>
            <el-col :span="15" class="position-relative">
              <el-select
                class="multi mT10 suffix-disabled fc-input-full-border-select2 width575px fc-tag"
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
          <div class="mT40 pB30">
            <div>
              <el-checkbox
                v-model="model.woData.isPreventOnNoTask"
                class="fw4"
                >{{
                  $t('maintenance.pm_list.prevent_pm_with_no_task')
                }}</el-checkbox
              >
            </div>
            <div class="mT20">
              <el-checkbox
                v-model="model.woData.isSignatureRequired"
                class="fw4"
                >{{ $t('maintenance.pm_list.enable_e_sign') }}</el-checkbox
              >
            </div>
            <div class="mT20">
              <el-checkbox v-model="model.woData.sendForApproval" class="fw4"
                >Send for approval</el-checkbox
              >
            </div>
            <div class="mT20" v-if="isWorkPermitLicenseEnabled">
              <el-checkbox v-model="model.woData.workPermitNeeded" class="fw4"
                >Work permit required</el-checkbox
              >
            </div>
            <div class="mT20 flex align-center">
              <el-checkbox v-model="model.woData.isOffsetWOCreation" class="fw4"
                >Pre-generate work orders before
              </el-checkbox>
              <div
                v-if="model.woData.isOffsetWOCreation"
                class="flex items-baseline mL15"
              >
                <span class="fwBold">Days</span>
                <el-input
                  v-model="model.woData.offsetDays"
                  class="mL15 pm-input-width70 fw4 fc-input-full-border2"
                ></el-input>
              </div>
              <div
                v-if="model.woData.isOffsetWOCreation"
                class="flex items-baseline mL15"
              >
                <span class="fwBold">Hours</span>
                <el-input
                  class="mL15 pm-input-width70 fw4 fc-input-full-border2"
                  v-model="model.woData.offsetHours"
                ></el-input>
              </div>
            </div>
            <div class="mT20">
              <el-checkbox v-model="model.woData.markIgnoredWo" class="fw4">
                Skip workorders if not started till next due
              </el-checkbox>
            </div>
          </div>
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
                  {{ resourceListLength }}
                  {{ $t('maintenance.pm_list.workorders') }}
                </span>
                {{ $t('maintenance.pm_list.no_of_workorder_mapped_1') }} <br />
                {{ $t('maintenance.pm_list.no_of_workorder_mapped_2') }}
              </div>
            </el-col>
          </el-row>
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
          >
          </space-asset-multi-chooser>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import FLookupField from '@/forms/FLookupField'
import FLookupFieldWizard from '@/FLookupFieldWizard'

import { mapState, mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['model'],
  components: {
    SpaceAssetMultiChooser,
    FLookupFieldWizard,
    FLookupField,
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadGroups') // if required, get users/groups from pmnewform.vue as props, which is fetched from PMMixin.getScopedGroupsAndUsers().
    this.$store.dispatch('loadBuildings')
  },
  data() {
    return {
      siteField: {
        isDataLoading: false,
        lookupModuleName: 'site',
        field: {
          lookupModule: {
            name: 'site',
            displayName: 'Site',
          },
        },
        filters: {},
        multiple: true,
      },
      canShowLookupWizard: false,
      selectedLookupField: null,
      loadingOption: false,
      showFloorOption: true,
      fetchedAssets: [],
      fetchedSpaces: [],
      buildings: [],
      dummyValue: [1],
      dummyValue1: [1],
      dummyValue2: [1],
      chooserVisibility: false,
      showAsset: true,
      isFromAssociateResourceMethod: false, // flag to note the entry and exit of associateResource(selectedObj) method.
    }
  },
  methods: {
    showLookupWizard(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizard', canShow)
    },
    setLookupFieldValue({ field }) {
      this.model.woData.sites = field.selectedItems.map(item => item.value)

      let { options } = this.fields
      let selectedItemsInOptions = options.filter(option =>
        this.model.woData.sites.includes(option.value)
      )
      let selectedItemIdsInOptions = selectedItemsInOptions.map(
        item => item.value
      )
      let selectedItemsNotInOptions = field.selectedItems.filter(
        item => !selectedItemIdsInOptions.includes(item.value)
      )
      this.$set(this.fields, 'options', [
        ...options,
        ...selectedItemsNotInOptions,
      ])
    },
    moveToNext() {
      this.$emit('next')
    },
    resourceListAllFloorsHandler(newVal, oldVal) {
      if (this.model.isEdit && this.model.isLoading) {
        return
      }
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
                {
                  key: 'site',
                  value: this.model.woData.woModel.site.id,
                },
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
              {
                key: 'building',
                value: this.model.woData.selectedBuilding,
              },
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
      } else if (
        this.model.woData.resourceType === 'ALL_SITES' &&
        !isEmpty(this.model.woData.sites) &&
        this.model.woData.sites.length > 0
      ) {
        // Handling Site Level PM - Sites
        let selectedSites = this.model.woData.sites
        let resourceList = []
        selectedSites.forEach(siteId => {
          if (this.allSitesMap[siteId]) {
            resourceList.push(this.allSitesMap[siteId])
          }
        })
        // Add into this.model.woData.resourceList
        this.$set(this.model.woData, 'resourceList', resourceList)
        // Add into this.model.woData.selectedSitesList
        this.model.woData.selectedSitesList = resourceList
        this.model.woData.resourceList.forEach(resource => {
          resource.triggerNames = ['0']
          resource.assignedTo = null
          resource.notifications = []
          resource.selected = false
        })
      }
    },
    associateResource(selectedObj) {
      this.isFromAssociateResourceMethod = true
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
        this.model.woData.resourceList = []
        this.model.woData.selectedResourceList = []
        this.model.woData.isIncludeResource = true
        this.model.woData.selectedSpaceList = []
        this.model.woData.isIncludeSpace = true
        this.model.woData.selectedFloorList = []
        this.model.woData.isIncludeFloor = true
      }
      this.chooserVisibility = false
      this.model.woData.resourceQuery = null
      this.isFromAssociateResourceMethod = false
    },
    resourceListHandler() {
      if (this.model.isEdit && this.model.isLoading) {
        return
      }

      if (this.model.woData.resourceType === 'SPACE_CATEGORY') {
        if (
          this.model.woData.selectedSpaceList &&
          this.model.woData.selectedSpaceList.length !== 0
        ) {
          if (
            this.isFromAssociateResourceMethod &&
            (this.model.woData.isIncludeSpace === null ||
              this.model.woData.isIncludeSpace === undefined ||
              this.model.woData.isIncludeSpace)
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
                  triggerNames: ['0'],
                  assignedTo: null,
                  notifications: [],
                  selected: false,
                  name: i.name,
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
          } else if (
            this.model.woData.spacecategoryId &&
            this.model.woData.spacecategoryId > 0
          ) {
            if (this.isMultiSite()) {
              this.loadMultiSiteSpaces()
            } else {
              this.loadSingleSiteSpaces()
            }
          }
        } else {
          if (
            this.model.woData.spacecategoryId &&
            this.model.woData.spacecategoryId > 0
          ) {
            if (this.isMultiSite()) {
              this.loadMultiSiteSpaces()
            } else {
              this.loadSingleSiteSpaces()
            }
          }
        }
      } else if (this.model.woData.resourceType === 'ASSET_CATEGORY') {
        if (
          this.model.woData.selectedResourceList &&
          this.model.woData.selectedResourceList.length !== 0
        ) {
          if (
            this.isFromAssociateResourceMethod &&
            (this.model.woData.isIncludeResource === null ||
              this.model.woData.isIncludeResource === undefined ||
              this.model.woData.isIncludeResource)
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
                  triggerNames: ['0'],
                  assignedTo: null,
                  notifications: [],
                  selected: false,
                  name: i.name,
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
          } else if (
            this.model.woData.assetCategoryId &&
            this.model.woData.assetCategoryId > 0
          ) {
            if (this.isMultiSite()) {
              this.loadMultiSiteAssets()
            } else {
              this.loadSingleSiteAssets()
            }
          }
        } else {
          if (
            this.model.woData.assetCategoryId &&
            this.model.woData.assetCategoryId > 0
          ) {
            if (this.isMultiSite()) {
              this.loadMultiSiteAssets()
            } else {
              this.loadSingleSiteAssets()
            }
          }
        }
      }
    },
    onBuildingChange(buildingId) {
      this.getScopeFilteredValues(this.model.woData.sites, buildingId)
    },
    getScopeFilteredValues(siteIds, buildingId) {
      if (!siteIds && !buildingId) {
        return
      }
      if (siteIds && siteIds.length === 0) {
        return
      }
      let params = ''
      if (siteIds) {
        if (siteIds.length === 1) {
          params = `siteIds=${siteIds[0]}`
        } else {
          params = `siteIds=${siteIds[0]}`
          for (let i = 1; i < siteIds.length; i++) {
            params = params + `&siteIds=${siteIds[i]}`
          }
        }
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
          this.fetchedAssets = response.data.assetCategoryIds || []
          this.fetchedSpaces = response.data.spaceCategoryIds || []
          this.buildings = response.data.buildings || []
          this.loadingOption = false
        })
    },
    isMultiSite() {
      return this.model.woData.sites && this.model.woData.sites.length > 1
    },
    loadSingleSiteSpaces() {
      let filter = []
      if (this.model.woData.sites[0]) {
        filter.push({
          key: 'site',
          operator: 'is',
          value: this.model.woData.sites[0],
        })
      }
      if (
        this.model.woData.selectedBuilding &&
        this.model.woData.selectedBuilding > 0
      ) {
        filter.push({
          key: 'building',
          operator: 'is',
          value: this.model.woData.selectedBuilding,
        })
      }
      filter.push({
        key: 'spaceCategory',
        operator: 'is',
        value: this.model.woData.spacecategoryId,
      })
      this.$util
        .loadSpacesContextPmConfigForm(4, null, filter)
        .then(response => {
          let spaces = response.records.filter(i => {
            for (
              let j = 0;
              j < this.model.woData.selectedSpaceList.length;
              j++
            ) {
              if (this.model.woData.selectedSpaceList[j].id === i.id) {
                return false
              }
            }
            return true
          })
          this.$set(this.model.woData, 'resourceList', spaces)
          this.$set(this.model.woData, 'selectedSpaceList', spaces)
          this.model.woData.resourceList.forEach(resource => {
            resource.triggerNames = ['0']
            resource.assignedTo = null
            resource.notifications = []
            resource.selected = false
          })
        })
    },
    loadMultiSiteSpaces() {
      let sitesStr = []
      if (this.model.woData.sites && this.model.woData.sites.length > 0) {
        if (this.model.woData.sites.length > 1) {
          for (let i = 0; i < this.model.woData.sites.length; i++) {
            if (this.model.woData.sites[i] > 0) {
              sitesStr.push(String(this.model.woData.sites[i]))
            }
          }
        }
      }
      if (
        this.model.woData.spacecategoryId &&
        this.model.woData.spacecategoryId > 0
      ) {
        let filters = []
        if (sitesStr) {
          filters.push({
            key: 'site',
            operator: 'is',
            value: sitesStr,
          })
        }
        if (
          this.model.woData.selectedBuilding &&
          this.model.woData.selectedBuilding > 0
        ) {
          filters.push({
            key: 'building',
            operator: 'is',
            value: this.model.woData.selectedBuilding,
          })
        }
        if (this.model.woData.spacecategoryId > 0) {
          filters.push({
            key: 'spaceCategory',
            operator: 'is',
            value: this.model.woData.spacecategoryId,
          })
        }
        this.$util
          .loadSpacesContextPmConfigForm(4, null, filters)
          .then(response => {
            this.$set(this.model.woData, 'resourceList', response.records)
            this.$set(this.model.woData, 'selectedSpaceList', response.records)
            this.model.woData.resourceList.forEach(resource => {
              resource.triggerNames = ['0']
              resource.assignedTo = null
              resource.notifications = []
              resource.selected = false
            })
          })
      }
    },
    loadMultiSiteAssets() {
      this.$util
        .loadAsset({
          spaceIds: this.model.woData.sites,
          categoryId: this.model.woData.assetCategoryId,
        })
        .then(response => {
          this.$set(this.model.woData, 'resourceList', response.assets)
          this.$set(this.model.woData, 'selectedResourceList', response.assets)
          this.model.woData.resourceList.forEach(resource => {
            resource.triggerNames = ['0']
            resource.assignedTo = null
            resource.notifications = []
            resource.selected = false
          })
        })
    },
    loadSingleSiteAssets() {
      let spaceId = this.model.woData.selectedBuilding
      if (
        !this.model.woData.selectedBuilding ||
        this.model.woData.selectedBuilding < 0
      ) {
        spaceId = this.model.woData.sites[0]
      }
      this.$util
        .loadAsset({
          spaceId: spaceId,
          categoryId: this.model.woData.assetCategoryId,
        })
        .then(response => {
          let assets = response.assets.filter(i => {
            for (
              let j = 0;
              j < this.model.woData.selectedResourceList.length;
              j++
            ) {
              if (this.model.woData.selectedResourceList[j].id === i.id) {
                return false
              }
            }
            return true
          })
          this.$set(this.model.woData, 'resourceList', assets)
          this.$set(this.model.woData, 'selectedResourceList', assets)
          this.model.woData.resourceList.forEach(resource => {
            resource.triggerNames = ['0']
            resource.assignedTo = null
            resource.notifications = []
            resource.selected = false
          })
        })
    },
  },
  watch: {
    'model.isLoading': function(val) {
      if (!this.model.isLoading && this.model.isEdit) {
        this.onBuildingChange(
          this.model.woData.selectedBuilding,
          this.model.woData.selectedBuildingList
        )
      }
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
        this.model.woData.selectedBuildingList = []
        this.model.woData.selectedSitesList = []
        this.$set(this.model.woData, 'resourceList', [])
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
    'model.woData.selectedFloorList': {
      handler: function(newVal, oldVal) {
        this.resourceListAllFloorsHandler(newVal, oldVal)
      },
      deep: true,
    },
    'model.woData.selectedBuilding': function(newVal, oldVal) {
      if (this.model.isLoading) {
        return
      }
      this.model.woData.selectedResourceList = []
      this.model.woData.selectedFloorList = []
      this.model.woData.selectedSpaceList = []
      this.model.woData.spacecategoryId = null
      this.model.woData.assetCategoryId = null
      this.$set(this.model.woData, 'resourceList', [])
    },
    'model.woData.sites': {
      handler: function(val, oldVal) {
        if (this.model.isLoading) {
          return
        }
        if (this.model.woData.woModel.hasOwnProperty('groups')) {
          this.model.woData.woModel.groups.id = ''
        }
        //this.model.woData.selectedBuilding = null
        this.model.woData.resourceType = null
        this.model.woData.spacecategoryId = null
        this.model.woData.assetCategoryId = null
        this.model.woData.resourceList = []
        this.getScopeFilteredValues(val)
      },
      deep: true,
    },
    'model.woData.selectedBuildingList': {
      handler: function(val, oldVal) {
        if (this.loadingOption) {
          return
        }
        if (this.model.isEdit && this.model.isLoading) {
          return
        }
        let resourceList = []
        if (this.model.woData.resourceType !== 'BUILDINGS') {
          return
        }
        if (val && val.length > 0) {
          let { model, buildings = [] } = this
          let { woData } = model || {}
          let { resourceList: _resourceList = [] } = woData || {}
          let resourceListMap = {}
          let buildingsListMap = {}

          _resourceList.forEach((resource, index) => {
            resourceListMap[resource.id] = index
          })
          buildings.forEach((building, index) => {
            buildingsListMap[building.id] = index
          })

          for (let i = 0; i < val.length; i++) {
            let buildingId = val[i] >= 0 ? val[i] : -1
            let buildingIndex = this.$getProperty(
              buildingsListMap,
              `${buildingId}`,
              -1
            )
            if (buildingIndex > -1) {
              let building = buildings[buildingIndex] || {}
              let { id } = building
              if (buildingId === id) {
                let index = resourceListMap[building.id]
                let defaultResourceMeta = {
                  triggerNames: ['0'],
                  assignedTo: null,
                  notifications: [],
                  selected: false,
                }
                let resource = _resourceList[index] || defaultResourceMeta
                resourceList.push({
                  ...building,
                  ...resource,
                })
              }
            }
          }
        } else {
          resourceList = this.$getProperty(this, 'buildings', [])
          if (!isEmpty(resourceList)) {
            resourceList = resourceList.map(resource => {
              return {
                ...resource,
                triggerNames: ['0'],
                assignedTo: null,
                notifications: [],
                selected: false,
              }
            })
          }
          this.model.woData.selectedBuildingList = []
          if (this.buildings) {
            for (let i = 0; i < this.buildings.length; i++) {
              this.model.woData.selectedBuildingList.push(this.buildings[i].id)
            }
          }
        }
        this.$set(this.model.woData, 'resourceList', resourceList)
      },
      deep: true,
    },
    'model.woData.assetCategoryId': {
      handler: function(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.resourceListHandler(newVal, oldVal)
        }
      },
      deep: true,
    },
    'model.woData.spacecategoryId': {
      handler: function(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.resourceListHandler(newVal, oldVal)
        }
      },
      deep: true,
    },
  },
  computed: {
    ...mapState({
      allsites: state => state.sites,
      assetcategory: state => state.assetCategory,
    }),
    ...mapGetters(['getSpaceCategoryPickList']),
    spacecategory() {
      return this.getSpaceCategoryPickList()
    },
    // Adding `allSitesMap` for easier lookup of site objects
    allSitesMap() {
      // Converting `this.allsites` into map/object
      let siteMapping = {}
      this.allsites.forEach(function(site) {
        siteMapping[site.id] = site
      })
      return siteMapping
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
    isWorkPermitLicenseEnabled() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers || {}

      return isLicenseEnabled('WORK_PERMIT')
    },
    filter() {
      let filter = {}
      let { model } = this
      let { woData } = model || {}
      let { sites = [] } = woData || {}

      if (!isEmpty(sites) && sites.length > 1) {
        filter.site = sites.map(site => {
          return site ? Number(site) : -1
        })
      } else filter.site = Number(sites[0]) || -1

      if (this.model.woData.selectedBuilding) {
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
    resourceListLength() {
      let { model } = this
      let { woData } = model || {}
      let { resourceList = [] } = woData || {}
      return resourceList.length
    },
  },
}
</script>
<style lang="scss">
.activeSingleSite {
  display: flex;
  .fc-width-reduce-pm-col {
    margin-left: 85px;
  }
  .fc-width-reduce-pm-col2 {
    margin-left: 32px;
  }
  .width575px {
    width: 212px !important;
  }
  .fc-building-col-left {
    margin-left: 56px;
  }
}
.fc-pm-left23 {
  left: 23px;
}

.resource-search {
  .el-input {
    .el-input__prefix {
      right: -1px;
      left: 55%;
      z-index: 10;
    }
  }
}
</style>
