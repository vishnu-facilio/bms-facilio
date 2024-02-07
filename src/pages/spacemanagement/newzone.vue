<template>
  <el-dialog
    :visible.sync="showNewZone"
    :fullscreen="true"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog45 setup-dialog show-right-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <error-banner
      :error.sync="error"
      :errorMessage="errorMessage"
    ></error-banner>
    <el-form :model="zone" :label-position="'top'" ref="ruleForm">
      <div class="form-header">
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ isnew ? 'NEW ZONE' : 'EDIT ZONE' }}
            </div>
          </div>
        </div>

        <div class="new-body-modal">
          <div id="container" style="width:100%;display:inline-block;">
            <el-row align="middle" :gutter="20">
              <el-col :span="24">
                <div class="fc-input-label-txt">Name</div>
                <div class="form-input">
                  <el-form-item prop="name">
                    <el-input
                      class="required header"
                      :autofocus="true"
                      v-model="zone.name"
                      type="text"
                      autoComplete="off"
                      placeholder="Enter Zone Name"
                    />
                  </el-form-item>
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <div class="fc-input-label-txt">Description</div>
                <div class="form-input">
                  <el-input
                    v-model="zone.description"
                    :placeholder="$t('common._common.enter_desc')"
                    type="textarea"
                    :autosize="{ minRows: 4, maxRows: 4 }"
                    resize="none"
                    class="fc-input-txt"
                  ></el-input>
                </div>
              </el-col>
            </el-row>
            <el-row
              align="middle"
              style="margin:0px;padding-top:30px;"
              :gutter="50"
            >
              <!-- <el-col style="padding-left:0px;" :span="12">
            <div class="fc-input-label-txt">Space Name </div>
            <div class="form-input">
              <el-input type="text" class="text required header" v-model="zone.building.name" autoComplete="off"></el-input>
            </div>
          </el-col> -->

              <el-col style="padding-left:0px;" :span="12">
                <div class="fc-input-label-txt">Zone Location</div>
                <div class="form-input">
                  <el-input
                    @change="
                      quickSearchQuery = spaceAssetDisplayName
                      showSpaceAssetChooser()
                    "
                    v-model="spaceAssetDisplayName"
                    style="width:100%"
                    type="text"
                    :placeholder="$t('common._common.to_search_type')"
                    class="fc-input-border-remove"
                  >
                    <i
                      @click="visibility = true"
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                    ></i>
                  </el-input>
                </div>
              </el-col>

              <el-col style="padding-left:0px;padding-right: 0;" :span="12">
                <div class="fc-input-label-txt">Spaces</div>
                <div class="form-input">
                  <el-input
                    v-model="selectedResourceLabel"
                    disabled
                    class="fc-border-select"
                  >
                    <i
                      @click="chooserVisibility = true"
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                    ></i>
                  </el-input>
                  <!-- <el-select v-model="spaceId" filterable multiple clearable placeholder="Select Spaces" style="width: 100%;">
                 <el-option v-for="user in spaces" :key="user.id" :label="user.name" :value="parseInt(user.id)"></el-option>
              </el-select> -->
                </div>
              </el-col>
            </el-row>
            <el-row
              align="middle"
              style="margin:0px;padding-top:30px;"
              :gutter="50"
            >
              <el-col style="padding-left:0px;" :span="12">
                <div class="fc-input-label-txt">Max Occupant Count</div>
                <div class="form-input">
                  <el-input
                    type="text"
                    class="text required header"
                    v-model="zone.maxOccupancy"
                    autoComplete="off"
                    placeholder="Enter Max Occupant Count"
                  ></el-input>
                </div>
              </el-col>
              <el-col style="padding-left:0px;padding-right: 0;" :span="12">
                <div class="fc-input-label-txt">Area</div>
                <div class="form-input">
                  <el-input
                    type="text"
                    class="text required header"
                    v-model="zone.area"
                    autoComplete="off"
                    placeholder="Enter Zone Area"
                  ></el-input>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
      <space-asset-chooser
        @associate="associate"
        :visibility.sync="visibility"
        :query="quickSearchQuery"
        :showAsset="false"
        :appendToBody="false"
      ></space-asset-chooser>
      <space-asset-multi-chooser
        v-if="chooserVisibility"
        @associate="associateResource"
        :visibility.sync="chooserVisibility"
        :initialValues="resourceData"
        :resourceType="'2, 3, 4'"
        :showAsset="false"
        :hideBanner="true"
        :selectedResource="selectedeResourceObj"
        :filter="filter"
      ></space-asset-multi-chooser>

      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="submitZone()"
          :loading="saving"
          >{{ saving ? 'Saving...' : 'Save' }}
        </el-button>
        <el-button class="modal-btn-cancel" @click="closeDialog()">
          CANCEL</el-button
        >
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'

export default {
  props: ['showNewZone', 'isnew', 'building', 'zoneobj'],
  data() {
    return {
      selectedeResourceObj: [],
      errorMessage: '',
      error: false,
      spaces: {},
      saving: false,
      initialFloorValue: {},
      spaceId: [],
      children: [],
      zone: {
        name: '',
        area: null,
        description: '',
        site: {
          id: null,
          name: null,
        },
        maxOccupancy: null,
        building: {
          id: null,
          name: '',
        },
        id: -1,
      },
      selectedResourceLabel: null,
      chooserVisibility: false,
      woSpaceAssetvisibility: {},
      mandatoryFields: {},
      visibility: false,
      quickSearchQuery: '',
      spaceAssetDisplayName: '',
    }
  },
  computed: {
    filter() {
      let filter = {}
      if (this.zone.site && this.zone.site.id) {
        filter.siteId = Number(this.zone.site.id)
      }
      return filter
    },
  },
  components: {
    SpaceAssetChooser,
    SpaceAssetMultiChooser,
    ErrorBanner,
  },
  mounted() {
    // if (!this.isnew) {
    //   this.init()
    // }
    // else {
    this.loadBuilding()
    if (!this.isnew) {
      this.zone = this.zoneobj
      this.zone.area = this.zone.area > -1 ? this.zone.area : 0
      this.zone.maxOccupancy =
        this.zone.maxOccupancy > -1 ? this.zone.maxOccupancy : ''
      this.spaceAssetDisplayName = this.zone.baseSpaceContext
        ? this.zone.baseSpaceContext.name
        : '--'
      this.loadChildren(this.zone.id)
    }
    // }
  },
  methods: {
    loadBuilding() {
      let self = this
      self.$util.loadSpace().then(function(response) {
        if (response.basespaces) {
          self.spaces = response.basespaces.filter(
            space => space.spaceTypeEnum !== 'ZONE'
          )
        }
      })
    },
    associateResource(selectedObj) {
      if (selectedObj.resourceList && selectedObj.resourceList.length) {
        selectedObj.resourceList.forEach(element => {
          this.spaceId.push(element.id)
        })
        this.selectedResourceLabel = this.resourceLabel(this.spaceId)
      }
      this.chooserVisibility = false
    },
    resourceLabel(selectedResourceObject) {
      if (selectedResourceObject) {
        let message
        let selectedCount = selectedResourceObject.length
        let categoryName = 'Space'
        if (selectedCount) {
          message =
            selectedCount +
            ' ' +
            (selectedCount > 1 ? categoryName + 's' : categoryName)
        }
        return message
      }
    },
    associate(selectedObj) {
      this.spaceAssetDisplayName = selectedObj.name
      this.zone.site = { id: selectedObj.id }
      this.visibility = false
    },
    loadChildren(id) {
      let self = this
      self.childrenLoading = true
      let url = '/zone/children?zoneId=' + id
      self.$http.get(url).then(function(response) {
        self.childrenLoading = false
        self.children = response.data.children ? response.data.children : []
        if (self.children) {
          self.selectedeResourceObj = self.children
          self.resourceData = {
            isIncludeResource: true,
            selectedResources: self.children.map(resource => ({
              id:
                resource && typeof resource === 'object'
                  ? parseInt(resource.id)
                  : parseInt(resource),
            })),
          }
          self.children.forEach(d => {
            self.spaceId.push(d.id)
          })
          self.selectedResourceLabel = self.resourceLabel(self.spaceId)
        }
      })
    },
    cancel() {
      this.$emit('canceled')
    },
    validateForm(zoneObj) {
      this.errorMessage = ''
      this.error = false
      let intRegx = /^[0-9]+$/
      let errorMsg = ''
      if (!zoneObj.name) {
        errorMsg += 'Zone name is required'
        return errorMsg
      }
      if (this.spaceId.length <= 0) {
        errorMsg += 'Please select a space to add zone'
        return errorMsg
      }
      if (zoneObj.area) {
        if (intRegx.test(zoneObj.area) === false) {
          errorMsg += 'Please give a valid area'
          return errorMsg
        }
      } else {
        zoneObj.area = null
      }
      if (zoneObj.maxOccupancy) {
        if (intRegx.test(zoneObj.maxOccupancy) === false) {
          errorMsg += 'Please give a valid number of occupancy'
          return errorMsg
        }
      } else {
        zoneObj.maxOccupancy = null
      }
    },
    submitZone() {
      let self = this
      let validate = this.validateForm(this.zone)
      if (validate) {
        this.error = true
        this.errorMessage = validate
        return false
      }
      if (this.zone.building.id === null) {
        this.zone.building.id = -1
      }
      if (this.zone.site.id === null) {
        this.zone.site.id = -1
      }
      let url = ''
      if (this.isnew) {
        url = '/zone/add'
      } else {
        url = '/zone/update'
      }
      self.$http
        .post(url, { zone: this.zone, spaceId: this.spaceId })
        .then(function(response) {
          if (response.data.zoneId) {
            self.$message.success('Zone Added Successfully')
            self.$emit('refreshlist')
            self.closeDialog()
          } else {
            self.$message.error('Failed to add zone ')
          }
        })
    },
    closeDialog() {
      this.showCreateDialog = false
      this.$emit('update:showNewZone', false)
    },
  },
}
</script>
