<template>
  <div id="floormapspaceassetchooser" class="multi f-element">
    <div style="height:100%;overflow-y: hidden;">
      <div
        style="background-color:#ffffff;height:100%;width:100%;display: inline-block;"
      >
        <div></div>
        <el-row class="position-relative">
          <el-col :span="24">
            <div
              style="padding: 10px 20px;border-bottom: 1px solid rgb(230, 236, 243) !important;"
            >
              <div class="asset-chooser-select-block">
                <div
                  v-for="field in resourceConfig"
                  :key="field.spaceType"
                  class="inline"
                >
                  <template v-if="field.spaceType !== 4">
                    <span class="label-txt-black fw-bold pR10"
                      >Select {{ field.placeHolder }}:
                    </span>
                    <el-select
                      v-if="
                        !resourceType ||
                          resourceType.includes(field.spaceType + 1)
                      "
                      v-model="field.value"
                      clearable
                      :placeholder="field.placeHolder"
                      :disabled="
                        (field.spaceType === 4 && !isAsset) || field.disabled
                      "
                      @change="
                        field.spaceType === 3
                          ? loadFloorviewData(field)
                          : onLocationChange(field)
                      "
                      class="fc-input-full-border-select2 mR20"
                    >
                      <el-option
                        v-for="option in field.options"
                        :key="option.id"
                        :label="option.name"
                        :value="option.id"
                      ></el-option>
                    </el-select>
                  </template>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="24">
            <floor-plan-viewer
              v-if="floorMapdata"
              :id="floorMapdata.id"
              :assetList="assets"
            ></floor-plan-viewer>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
import ResourceMixin from '@/mixins/ResourceMixin'
import FloorPlanViewer from '@/FloorPlanViewer'
export default {
  mixins: [ResourceMixin],
  props: ['visibility', 'disable', 'hideBanner', 'assets'],
  data() {
    return {
      selectall: false,
      showQuickSearch: false,
      floorMapdata: null,
      includeType: 'all',
      selectedList: [],
    }
  },
  components: {
    FloorPlanViewer,
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    categoryName() {
      if (!this.assetCategory || this.assetCategory.value <= 0) {
        return ''
      }
      return this.assetCategory.options[this.assetCategory.value]
    },
  },
  mounted() {
    this.initResourceData()
    // this.loadDefaultFloor()
  },
  watch: {
    query(val) {
      this.quickSearchQuery = val
      if (val) {
        this.showQuickSearch = true
        this.setResourceData()
      }
    },
    initialResourcesFecthed(val) {
      if (val) {
        this.setInitialData()
      }
    },
  },
  methods: {
    reInit() {
      this.showQuickSearch = false
      this.quickSearchQuery = null
      this.checkAndInitResourceData()
    },
    loadDefaultFloor() {
      if (
        this.resourceConfig.find(rt => rt.spaceType === 1) &&
        this.resourceConfig.find(rt => rt.spaceType === 1).options.length
      ) {
        this.onLocationChange(
          this.resourceConfig.find(rt => rt.spaceType === 1).options[0]
        )
      }
    },
    deSelect(object) {
      let resource = this.resourceList.find(
        resource => resource.id === object.id
      )
      resource.selected = false
      this.setSelectedResource(resource)
      this.setIncludeTypeOnChange()
    },
    openSpace() {
      this.isAsset = false
      this.loadSpace()
    },
    toggleQuickSearch() {
      this.showQuickSearch = !this.showQuickSearch
      if (!this.showQuickSearch) {
        this.quickSearchQuery = null
        this.setResourceData()
      } else {
        this.$nextTick(() => {
          this.$refs.quickSearchQuery.focus()
        })
      }
    },
    onCategoryChange() {
      this.assetCategory.value
        ? (this.includeType = 'all')
        : this.setIncludeTypeOnChange()
      this.onCategorySelected()
      this.selectedList = []
    },
    onSelectAllChange() {
      this.selectedList = []
      this.resourceList.forEach(resource => {
        this.$set(resource, 'selected', this.selectall)
        this.setSelectedResource(resource)
      })
      this.setIncludeTypeOnChange()
    },
    toggleSelect(resource, alreadyChanged) {
      if (!alreadyChanged) {
        this.$set(resource, 'selected', !resource.selected)
      }
      this.setSelectedResource(resource)
      this.selectall = this.selectedList.length === this.resourceList.length
      this.setIncludeTypeOnChange()
    },
    setInitialData() {
      if (!this.initialData) {
        return
      }
      let selectedResources = this.initialData.selectedResources
      this.includeType =
        !selectedResources || !selectedResources.length
          ? 'all'
          : this.initialData.isIncludeResource
          ? 'include'
          : 'exclude'
      let idArray = []
      if (selectedResources) {
        this.selectedList = []
        this.resourceList.forEach(resource => {
          let resourceSelectedObj = this.initialData.selectedResources.some(
            selctedResource => selctedResource.id === resource.id
          )
          this.$set(resource, 'selected', resourceSelectedObj)
          this.setSelectedResource(resource)
        })
      }
      if (this.selectedList.length < selectedResources.length) {
        this.selectedList = []
        this.initialData.selectedResources.forEach(d => {
          idArray.push(d.id)
        })
        this.$util.loadResource(idArray).then(fields => {
          this.selectedList = fields
        })
      }
    },
    setIncludeTypeOnChange() {
      if (this.includeType === 'all') {
        this.includeType = 'include'
      } else if (!this.selectedList.length) {
        this.includeType = 'all'
      }
    },
    isSelected(resourceId) {
      return this.selectedList.find(resource => resource.id === resourceId)
    },
    loadFloorviewData(field) {
      this.floorMapdata = field.options.length
        ? field.options.find(rt => rt.id === field.value)
        : null
      this.applyFilter()
    },
    applyFilter() {
      let redirectUrl = {
        space: [
          {
            operatorId: 38,
            value: [this.floorMapdata.id + ''],
          },
        ],
      }
      let view = {}
      if (this.$route.query && this.$route.query.view) {
        view = this.$helpers.cloneObject(JSON.parse(this.$route.query.view))
        view.viewName = 'floormap'
        view.hidePagination = true
      }
      this.$router.push(
        '/app/at/assets/all?includeParentFilter=true&hidePagination=true&search=' +
          encodeURIComponent(JSON.stringify(redirectUrl)) +
          '&view=' +
          encodeURIComponent(JSON.stringify(view))
      )
    },
    onLocationChange(field) {
      if (field.spaceType === 2) {
        let self = this
        self.$http
          .get('/floor?buildingId=' + field.value)
          .then(function(response) {
            if (response.data && response.data.records) {
              self.resourceConfig.find(
                rt => rt.spaceType === 3
              ).options = response.data.records.filter(
                rt => rt.floorPlanInfo !== null
              )
            }
          })
      } else {
        this.onLocationSelected(field).then(() => {
          this.setListSelected()
        })
        this.setIncludeTypeOnChange()
      }
    },
    setResourceData() {
      this.loadResourceData().then(() => {
        this.setListSelected()
      })
    },
    setListSelected() {
      let selectedIds = this.selectedList.map(resource => resource.id)
      this.spaceList.map(space => {
        if (selectedIds.includes(space.id)) {
          this.$set(space, 'selected', true)
        }
        return space
      })
    },
    setSelectedResource(resource) {
      if (resource.selected) {
        this.selectedList.push(this.$helpers.cloneObject(resource))
      } else {
        let idx = this.selectedList.findIndex(sr => sr.id === resource.id)
        if (idx !== -1) {
          this.selectedList.splice(idx, 1)
        }
      }
    },
  },
}
</script>
<style>
.field-hint {
  padding: 8px 0;
  font-size: 12px;
  border-radius: 2px;
}
.dialogsize {
  height: 70% !important;
}
.floormapspaceassetchooser .el-dialog.fc-dialog-up {
  margin-top: 0px !important;
  width: 850px;
}
.grayicon {
  background-color: #d1d9dd;
}
.floormapspaceassetchooser .el-dialog__header .el-icon-close:before {
  content: '';
}
.txtstyle {
  font-size: 13px;
  letter-spacing: 0.9px;
  color: #333333;
}
.heading {
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 1.1px;
  color: #9aacc0;
}
.headerrow {
  font-size: 11px;
  letter-spacing: 1px;
  color: #717b85;
  padding-left: 30px;
}
#floormapspaceassetchooser tr:nth-child(odd) {
  background-color: #fbfbfb;
}

#floormapspaceassetchooser .el-icon-arrow-down {
  color: #b9c6d4;
  font-size: 13px;
  font-weight: 500;
}
.contentrow {
  font-size: 13px;
  letter-spacing: 0.4px;
  color: #333333;
}
.spaceheading {
  font-size: 16px;
  letter-spacing: 0.6px;
  color: #333333;
}
.fc-dialog-up .el-dialog__header {
  padding: 0px;
}

.sidebar {
  background-color: rgba(189, 210, 228, 0.2);
}
.fc-dialog-up .el-dialog__body {
  padding: 0px;

  line-height: 24px;
  font-size: 14px;
}
#floormapspaceassetchooser .fc-list-view-table td {
  font-size: 11px;
  font-weight: bold;
  padding-top: 8px;
  padding-bottom: 8px;
  padding-right: 10px;
  padding-left: 30px;
  /* border-bottom: 1px solid #e6ecf3; */
  /* border-top: 1px solid transparent !important; */
  cursor: pointer;
  /* width:30% */
}
#floormapspaceassetchooser .fc-list-view-table .contentrow {
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.4px;
  color: #333333;
  border-bottom: 1px solid transparent;
  border-top: 1px solid transparent;
}
#floormapspaceassetchooser.multi .fc-list-view-table tr td:nth-child(2) {
  padding-left: 6px;
}

.selectedspace {
  color: #65abb4;
  font-size: 18px;
}
.normalspace {
  color: #d1d9dd;
  font-size: 18px;
}
.tablerow.selected {
  background: #effdff;
  /* border-left: 3px solid #28b2a4; */
  border-right: 1px solid rgba(232, 232, 232, 0.35);
  border-top: 1px solid rgba(232, 232, 232, 0.35);
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
.scroll-multichooser {
  height: 100vh;
  padding-bottom: 70px;
}
.selectedrow {
  background-color: #effdff !important;
}

#floormapspaceassetchooser.multi .el-select {
  width: auto;
}

#floormapspaceassetchooser .fc-list-search-wrapper .quick-search-input {
  border-bottom: 0 !important;
}

#floormapspaceassetchooser .fc-list-view-table tbody tr.tablerow:hover {
  background-color: #effdff !important;
}

#floormapspaceassetchooser.multi .asset-category .el-input__inner {
  font-size: 16px;
  letter-spacing: 0.6px;
  color: #333333;
}
.border-right-table {
  padding-bottom: 30px;
  margin-bottom: 50px;
}
.table-scroll-chooser {
  height: calc(100vh - 300px);
  overflow-y: scroll;
  overflow-x: hidden;
  border-right: 1px solid #e6ecf3;
  border-left: 1px solid transparent;
}
.searchicon .el-icon-search {
  font-size: 16px;
  color: #a2b2c4;
  font-weight: bold;
  margin-top: 17px;
}
.chooser-search .quick-search-input {
  line-height: 2;
}
.chooser-selected-container {
  height: calc(100vh - 300px);
  padding-left: 20px;
  padding-top: 10px;
  overflow: scroll;
  position: relative;
  margin-bottom: 30px;
}
.chooser-scroll-block {
  margin-right: 20px;
}
.selected-field {
  border-radius: 3px;
  background-color: #e4f6f8;
  padding: 3px;
  margin-bottom: 10px;
  padding-left: 11px;
  padding-right: 11px;
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.4px;
  color: #333333;
}
.selected-field .remove {
  color: #1d8a99;
  text-align: right;
  font-size: 15px;
  font-weight: normal;
  float: right;
  position: relative;
  top: 4px;
}
.chooser-scroll-block .count-message {
  font-size: 14px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.6px;
  color: #39b2c2;
  padding-top: 3px;
  padding-bottom: 18px;
}
div#floormapspaceassetchooser {
  margin: 15px;
  height: calc(100vh - 140px);
  overflow: auto;
}
</style>
