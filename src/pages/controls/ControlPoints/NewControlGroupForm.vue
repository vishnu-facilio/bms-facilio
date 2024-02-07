<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :before-close="closeDialog"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog fc-animated slideInRight"
    >
      <el-form
        :model="controlgroupData"
        :label-position="'top'"
        ref="controlgroup"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{
                isNew
                  ? $t('common.header.new_control_group')
                  : $t('common.header.edit_control_group')
              }}
            </div>
          </div>
        </div>
        <div class="new-body-modal">
          <el-form-item :label="'Name'">
            <el-input
              autofocus
              :placeholder="$t('common.products.control_group_name')"
              class="fc-input-full-border2"
              v-model="controlgroupData.name"
            ></el-input>
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item :label="$t('common._common.asset_category')">
                <el-select
                  v-model="controlgroupData.assetCategoryId"
                  filterable
                  @change="loadThresholdFields(true)"
                  :placeholder="$t('common._common.select')"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="(category, index) in assetCategory"
                    :key="index"
                    :label="category.displayName"
                    :value="parseInt(category.id)"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('common.products.assets')">
                <el-input
                  v-model="resourceLabel"
                  disabled
                  class="fc-border-select fc-input-full-border-select2 width100"
                >
                  <i
                    @click="chooserVisibility = true"
                    slot="suffix"
                    style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                    class="el-input__icon el-icon-search"
                  ></i>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item :label="$t('common.tabs.reading')">
                <el-select
                  filterable
                  v-model="controlgroupData.metricFieldName"
                  @change="setMetricModule(controlgroupData.metricFieldName)"
                  :placeholder="$t('common.wo_report.select_reading')"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="field in assetCategoryFields"
                    :key="field.name + field.id"
                    :label="field.displayName"
                    :value="field.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('common.wo_report.buildings')">
                <el-select
                  v-model="controlgroupData.buildings"
                  collapse-tags
                  multiple
                  filterable
                  :placeholder="$t('common.wo_report.select_building')"
                  class="fc-tag width100"
                >
                  <el-option
                    v-for="building in buildingListOptions"
                    :key="building.id"
                    :label="building.name"
                    :value="building.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            type="primary"
            @click="submitForm()"
            class="modal-btn-save"
            :loading="saving"
          >
            {{
              saving
                ? $t('common._common.submitting')
                : $t('common._common._save')
            }}</el-button
          >
        </div>
      </el-form>
    </el-dialog>
    <space-asset-multi-chooser
      @associate="associateResource"
      :resourceType="[3]"
      :visibility.sync="chooserVisibility"
      :initialValues="resourceData"
      :query="resourceQuery"
      :showAsset="true"
      :disable="true"
    >
    </space-asset-multi-chooser>
  </div>
</template>
<script>
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import WorkFlowMixin from '@/mixins/WorkFlowMixin'
import { mapState } from 'vuex'
export default {
  mixins: [WorkFlowMixin],
  props: ['visibility', 'isNew', 'controlgroup', 'floorobj', 'building'],
  data() {
    return {
      saving: false,
      buildingListOptions: [],
      controlgroupData: {
        id: '',
        name: '',
        assetCategoryId: null,
        metricFieldName: null,
        buildings: null,
        floor: {
          id: null,
          name: '',
        },
      },
      selectedResourceList: [],
      chooserVisibility: false,
      resourceQuery: null,
      thresholdFields: null,
      assetCategoryFields: [],
      isIncludeResource: false,
      resourceType: 'asset',
    }
  },
  mounted: function() {
    this.getfetchGroup()
    this.loadBuildingList()
    this.init()
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
    resourceData() {
      return {
        assetCategory: this.controlgroupData.assetCategoryId,
        isIncludeResource: this.isIncludeResource,
        selectedResources: Array.isArray(this.selectedResourceList)
          ? this.selectedResourceList.map(resource => ({
              id:
                resource && typeof resource === 'object'
                  ? resource.id
                  : resource,
            }))
          : this.selectedResourceList.id,
      }
    },
    isAsset() {
      return this.resourceType === 'asset'
    },
    isMultiResource() {
      return (
        this.isAsset &&
        (!this.controlgroupData || this.controlgroupData.assetCategoryId > 0)
      )
    },
    resourceLabel() {
      if (this.controlgroupData.assetCategoryId > 0) {
        let category = this.assetCategory.filter(
          d => d.id === this.controlgroupData.assetCategoryId
        )
        let message
        let selectedCount = this.selectedResourceList.length
        if (selectedCount) {
          let includeMsg = this.isIncludeResource ? 'included' : 'excluded'
          if (selectedCount === 1) {
            return this.selectedResourceList[0].name
          }
          message =
            selectedCount +
            ' ' +
            category[0].name +
            (selectedCount > 1 ? 's' : '') +
            ' ' +
            includeMsg
        } else {
          message = 'All ' + category[0].name + 's selected'
        }
        return message
      } else if (this.selectedResourceList.id > 0) {
        return this.selectedResourceList.name
      }
      return null
    },
  },
  watch: {
    isNew: function() {
      this.init()
    },
    created() {
      this.$store.dispatch('loadAssetCategory')
    },
  },
  components: {
    SpaceAssetMultiChooser,
  },
  methods: {
    init: function() {
      if (!this.isNew) {
        this.controlgroupData = this.controlgroup
      } else {
        this.controlgroupData = {
          name: '',
          assetCategoryId: null,
          metricFieldName: null,
          floor: {
            id: null,
            name: '',
          },
          buildings: [],
        }
      }
      // load building list
      if (this.isnew) {
        this.floorobj = null
      }
      if (this.floorobj) {
        // if (!this.isnew) {
        this.siteId = this.floorobj.siteId
        this.loadBuildingList()
        this.floor = {
          name: this.floorobj && this.floorobj.name ? this.floorobj.name : null,
          area: this.floorobj.area > 0 ? this.floorobj.area : null,
          maxOccupancy:
            this.floorobj && this.floorobj.maxOccupancy > 0
              ? this.floorobj.maxOccupancy
              : null,
          description: this.floorobj.description,
          id: this.floorobj.id,
          building: {
            id:
              this.floorobj &&
              this.floorobj.building &&
              this.floorobj.building.id
                ? parseInt(this.floorobj.building.id)
                : null,
            name:
              this.floorobj.building && this.floorobj.building.name
                ? this.floorobj.building.name
                : null || (this.building && this.building.name)
                ? this.building.name
                : null,
          },
        }
      }
    },
    associateResource(selectedObj) {
      if (this.resourceType === 'asset') {
        if (selectedObj.resourceList && selectedObj.resourceList.length) {
          this.selectedResourceList = selectedObj.resourceList
          this.isIncludeResource = selectedObj.isInclude
        }
      }
      this.chooserVisibility = false
      this.resourceQuery = null
      // this.parseLabel = this.resourceLabel(this.selectedResourceList)
    },
    loadThresholdFields() {
      let self = this
      self.loading = true
      this.assetCategoryFields = []
      let url =
        '/v2/controlAction/getControllableFields?assetCategoryId=' +
        this.controlgroupData.assetCategoryId
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          if (response.data.result.controllableFields) {
            self.assetCategoryFields = response.data.result.controllableFields
          }
          self.loading = false
        }
      })
    },
    loadAssets() {
      let self = this
      let url = '/asset/all'
      this.$http.get(url).then(function(response) {
        if (response.data) {
          // self.assetList = response.data.assets
          if (!self.isnew || self.isDependant) {
            if (!self.isMultiResource) {
              self.selectedResource = response.data.assets.find(
                asset => asset.id === self.selectedResource.id
              )
            } else {
              self.selectedResourceList = response.data.assets.filter(asset =>
                self.selectedResourceList.some(
                  resource => resource.id === asset.id
                )
              )
            }
            self.loadThresholdFields(self.isMultiResource)
          }
        }
      })
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    loadBuildingList() {
      this.$util
        .loadSpace(2, null, [{ key: 'site', value: this.siteId }])
        .then(response => {
          this.buildingListOptions = response.basespaces
        })
    },
    loadBuilding() {
      this.controlgroupData.building.name = this.controlgroupData.building.name
      this.controlgroupData.building.id = parseInt(
        this.controlgroupData.building.id
      )
      if (!this.isnew) {
        let self = this
        self.$http.get('/floor/' + self.floorobj.id).then(function(response) {
          self.floor = response.data.record
        })
      }
    },
    submitForm() {
      let self = this
      self.saving = true
      let url
      let payload = {
        controlGroup: {
          name: this.controlgroupData.name,
          assetCategoryId: this.controlgroupData.assetCategoryId,
          fieldId: this.controlgroupData.metricFieldName,
          mode: 1,
          controlGroupSpaces: this.controlgroupData.buildings.map(id => ({
            spaceId: id,
          })),
          controlGroupInclExclContexts: this.selectedResourceList.map(
            resource => ({
              resourceId: resource.id,
              isInclude: this.isIncludeResource,
            })
          ),
        },
      }
      if (this.controlgroupData.id) {
        payload.controlGroup.id = this.controlgroupData.id
        url = '/v2/controlAction/updateControlGroup'
        self.$http
          .post(url, payload)
          .then(response => {
            if (response.data.responseCode === 0) {
              this.controlgroupData = {
                name: '',
                assetCategoryId: null,
                metricFieldName: null,
                floor: {
                  id: null,
                  name: '',
                },
                buildings: [],
              }
              self.$message.success(
                this.$t('common.products.control_group_edited_successfully')
              )
              self.$emit('update:visibility', false)
              self.$emit('saved')
            } else {
              self.$message.error(response.data.message)
            }
            self.saving = false
          })
          .catch(error => {
            console.log(error)
            self.saving = false
            self.$message.error(this.$t('common.wo_report.unable_to_edit'))
          })
      } else {
        url = '/v2/controlAction/addControlGroup'
        self.$http
          .post(url, payload)
          .then(response => {
            if (response.data.responseCode === 0) {
              this.controlgroupData = response.data.result.controlgroup
              this.controlgroupData = {
                name: '',
                assetCategoryId: null,
                metricFieldName: null,
                floor: {
                  id: null,
                  name: '',
                },
                buildings: [],
              }
              self.$message.success(
                this.$t('common.products.control_group_added_successfully')
              )
              self.$emit('update:visibility', false)
              self.$emit('saved')
            } else {
              self.$message.error(response.data.message)
            }
            self.saving = false
          })
          .catch(error => {
            console.log(error)
            self.$message.error(this.$t('common.wo_report.unable_to_add'))
          })
      }
    },
    getfetchGroup() {
      if (!this.controlgroup) {
        return
      }
      let self = this
      self.loading = true
      let url =
        '/v2/controlAction/getControlGroupMeta?controlGroupId=' +
        this.controlgroup.id
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          if (response.data.result.controlgroup) {
            self.controlgroupData.name = response.data.result.controlgroup
          }
          self.loading = false
        }
      })
    },
    groupRuleParse(group) {
      if (group.controlgroupData.assetCategoryId > 0) {
        this.resourceType = 'asset'
        if (
          group.controlgroupData.includedResources &&
          group.controlgroupData.includedResources.length
        ) {
          this.isIncludeResource = true
          this.selectedResourceList = group.controlgroupData.includedResources.map(
            id => ({
              id: id,
            })
          )
        } else if (group.controlgroupData.excludedResources) {
          this.selectedResourceList = group.controlgroupData.excludedResources.map(
            id => ({
              id: id,
            })
          )
        }
        this.loadAssets()
      } else {
        group.controlgroupData.assetCategoryId = null
        this.selectedResourceList =
          group.controlgroupData.matchedResources[
            Object.keys(group.controlgroupData.matchedResources)[0]
          ]
        if (this.selectedResourceList.resourceType === 2) {
          this.resourceType = 'asset'
          this.loadAssets()
          this.loadThresholdFields()
        } else {
          this.resourceType = 'space'
          this.loadThresholdFields()
        }
      }
    },
  },
}
</script>
