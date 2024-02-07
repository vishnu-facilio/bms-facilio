<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="false"
    open="top"
    width="60%"
    custom-class=" fc-dialog-up spaceassetchooser setup-dialog60"
    :before-close="handleclose"
    :append-to-body="true"
  >
    <div id="spaceassetchooser" class="multi f-element">
      <banner
        v-if="!hideBanner"
        :message="message"
        :hideIcon="!selectedList.count"
      ></banner>
      <div style="height:600px;overflow-y: hidden;">
        <div
          style="background-color:#ffffff;height:100%;width:100%;display: inline-block;"
        >
          <div class="flex-middle justify-content-space pT20 pL20 pR20">
            <el-select
              v-if="isAsset"
              v-model="assetCategory.value"
              filterable
              :placeholder="assetCategory.placeHolder"
              :disabled="!isAsset || disable"
              @change="onCategoryChange"
              class="inline asset-category fc-input-full-border2"
            >
              <el-option
                v-for="(label, value) in assetCategory.options"
                :key="value"
                :label="label"
                :value="parseInt(value)"
              ></el-option>
            </el-select>
            <el-select
              v-else
              v-model="spaceCategory.value"
              filterable
              clearable
              :placeholder="spaceCategory.placeHolder"
              @change="onSpaceCategoryChange"
              class="inline asset-category fc-input-full-border2"
            >
              <el-option
                v-for="(label, value) in spaceCategory.options"
                :key="value"
                :label="label"
                :value="value"
              ></el-option>
            </el-select>
            <div class="pull-right" v-if="!hideBanner">
              <el-radio-group
                v-model="includeType"
                @change="onIncludeTypeChange"
              >
                <el-radio label="all" class="fc-radio">All</el-radio>
                <el-radio label="include" class="fc-radio">Include</el-radio>
                <el-radio label="exclude" class="fc-radio">Exclude</el-radio>
              </el-radio-group>
            </div>
          </div>
          <el-row>
            <el-col :span="24">
              <div
                style="padding-left:20px;padding-right:20px;border-bottom: 1px solid #e6ecf3 !important;"
                class="pT10 pB10"
              >
                <div v-if="!showQuickSearch" class="asset-chooser-select-block">
                  <div
                    v-for="field in resourceConfig"
                    :key="field.spaceType"
                    class="inline"
                  >
                    <el-select
                      v-if="
                        !resourceType ||
                          resourceType.includes(field.spaceType + 1)
                      "
                      v-model="field.value"
                      multiple
                      collapse-tags
                      filterable
                      clearable
                      :placeholder="field.placeHolder"
                      :disabled="
                        (field.spaceType === 4 && !isAsset) || field.disabled
                      "
                      @change="onLocationChange(field)"
                    >
                      <el-option
                        v-for="option in field.options"
                        :key="option.id"
                        :label="option.name"
                        :value="option.id"
                      ></el-option>
                    </el-select>
                  </div>
                  <span class="searchicon pull-right pointer"
                    ><span @click="toggleQuickSearch"
                      ><i class="el-icon-search"></i></span
                  ></span>
                </div>
                <div v-else style="display: inline-block; width: 100%;">
                  <div class="col-6 fc-list-search">
                    <div class="fc-list-search-wrapper relative chooser-search">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="32"
                        height="32"
                        viewBox="0 0 32 32"
                        class="search-icon"
                      >
                        <title>search</title>
                        <path
                          d="M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"
                        ></path>
                      </svg>
                      <input
                        ref="quickSearchQuery"
                        autofocus
                        type="text"
                        v-model="quickSearchQuery"
                        @keyup.enter="setResourceData"
                        placeholder="Search"
                        class="quick-search-input"
                      />
                      <svg
                        @click="toggleQuickSearch"
                        xmlns="http://www.w3.org/2000/svg"
                        width="32"
                        height="32"
                        viewBox="0 0 32 32"
                        class="close-icon"
                        aria-hidden="true"
                      >
                        <title>close</title>
                        <path
                          d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"
                        ></path>
                      </svg>
                    </div>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
          <div class="scroll-multichooser">
            <el-row>
              <el-col :span="hideBanner ? 16 : 24" class="table-scroll-chooser">
                <v-infinite-scroll
                  :loading="loading"
                  @bottom="nextPage"
                  :offset="20"
                  style="max-height: 500px; overflow-y: scroll;padding-bottom: 50px;"
                >
                  <table class=" pT15 fc-list-view-table border-right-table">
                    <thead>
                      <tr
                        class="tablerow pT30"
                        style="background-color:#ffffff;padding:15px"
                      >
                        <td class="headerrow ">
                          <el-checkbox
                            v-model="selectall"
                            @change="onSelectAllChange"
                            :disabled="includeType === 'exclude' && !hideBanner"
                          ></el-checkbox>
                        </td>
                        <td class="headerrow width30">#ID</td>
                        <td class="headerrow width30">NAME</td>
                        <td class="headerrow width30">CATEGORY</td>
                      </tr>
                    </thead>
                    <tbody>
                      <tr
                        v-if="!isAsset"
                        v-for="space in spaceList"
                        :key="space.id"
                        class="tablerow"
                        v-bind:class="{ selectedrow: space.selected }"
                      >
                        <td class="contentrow">
                          <el-checkbox
                            v-model="space.selected"
                            @change="toggleSelect(space, true)"
                          ></el-checkbox>
                        </td>
                        <td
                          class="contentrow width30"
                          @click.stop="toggleSelect(space)"
                        >
                          #{{
                            space.spaceType == 1
                              ? 'SI' + space.id
                              : space.spaceType == 2
                              ? 'BU' + space.id
                              : space.spaceType == 3
                              ? 'FL' + space.id
                              : space.spaceType == 4
                              ? 'SP' + space.id
                              : space.id
                          }}
                        </td>
                        <td
                          class="contentrow width30"
                          @click.stop="toggleSelect(space)"
                        >
                          {{ space.name }}
                        </td>
                        <td class="contentrow width30">
                          {{ space.spaceTypeVal }}
                        </td>
                      </tr>
                      <tr
                        v-if="isAsset"
                        v-for="asset in assetList"
                        :key="asset.id"
                        class="tablerow"
                        v-bind:class="{ selectedrow: asset.selected }"
                      >
                        <td class="contentrow">
                          <el-checkbox
                            v-model="asset.selected"
                            @change="toggleSelect(asset, true)"
                          ></el-checkbox>
                        </td>
                        <td
                          class="contentrow width30"
                          @click.stop="toggleSelect(asset)"
                        >
                          #{{ asset.id }}
                        </td>
                        <td
                          class="contentrow width30"
                          @click.stop="toggleSelect(asset)"
                        >
                          {{ asset.name }}
                        </td>
                        <td class="contentrow width30">
                          {{
                            asset.category
                              ? getAssetCategory(asset.category.id).displayName
                              : '---'
                          }}
                        </td>
                      </tr>
                      <tr
                        v-if="
                          spaceList &&
                            spaceList.length < 1 &&
                            assetList &&
                            assetList.length < 1
                        "
                        style="background: none;border: none; margin-top: 20px;"
                      >
                        <td
                          colspan="100%"
                          class="text-center label-txt-black"
                          style="border-bottom: none;font-size: 14px;padding-top: 40px;cursor: auto;"
                        >
                          No {{ isAsset ? 'Asset' : 'Space' }} to display!
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </v-infinite-scroll>
              </el-col>
              <el-col
                :span="8"
                class="chooser-selected-container"
                v-if="hideBanner"
              >
                <div class="chooser-scroll-block">
                  <div
                    v-if="selectedList && selectedList.length"
                    class="count-message"
                  >
                    {{ message }}
                  </div>
                  <div
                    v-for="(resource, value) in selectedList"
                    :key="value"
                    class="selected-field"
                  >
                    {{ resource.name }}
                    <i
                      class="el-icon-close remove"
                      @click="deSelect(resource)"
                    ></i>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
          <div class="f-footer row">
            <button
              type="button"
              class="footer-btn footer-btn-secondary col-6"
              @click="handleclose"
            >
              <span>Cancel</span>
            </button>
            <button
              type="button"
              class="footer-btn footer-btn-primary col-6"
              @click="associate"
            >
              <span>Ok</span>
            </button>
          </div>
        </div>
      </div>
      <div style="position: absolute; top: 45%;left:45%;z-index:5">
        <spinner :show="resourceLoading" size="70"></spinner>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import VInfiniteScroll from 'v-infinite-scroll'
import ResourceMixin from '@/mixins/ResourceMixin'
import Banner from '@/FBanner'
import { mapGetters } from 'vuex'
export default {
  mixins: [ResourceMixin],
  props: ['visibility', 'disable', 'hideBanner'],
  components: {
    Banner,
    VInfiniteScroll,
  },
  data() {
    return {
      selectall: false,
      showQuickSearch: false,
      includeType: 'all',
      selectedList: [],
    }
  },
  computed: {
    ...mapGetters(['getAssetCategory']),

    message() {
      let message
      let selectedCount = this.selectedList.length
      if (this.includeType === 'all' && this.categoryName) {
        message = 'All ' + this.categoryName + 's have been selected'
      } else if (this.includeType !== 'all' && selectedCount) {
        let includeMsg
        if (this.hideBanner) {
          includeMsg = ' Selected'
        } else {
          includeMsg =
            this.includeType === 'include' ? ' Included' : ' Excluded'
        }
        let resource =
          (this.categoryName ? this.categoryName : '') +
          (this.showAsset ? ' Asset' : ' Space')
        message =
          selectedCount +
          ' ' +
          resource +
          (selectedCount > 1 ? 's' : '') +
          includeMsg
      } else {
        message = this.resourceType ? 'No Space Selected' : 'No Asset Selected'
      }
      return message
    },
    categoryName() {
      if (!this.assetCategory || this.assetCategory.value <= 0) {
        return ''
      }
      return this.assetCategory.options[this.assetCategory.value]
    },
  },
  created() {
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadAssetCategory').then(() => {
      this.initResourceData()
    })
  },
  watch: {
    visibility(val) {
      if (val) {
        this.reInit()
      } else if (
        !val &&
        this.query &&
        (!this.selectedList || !this.selectedList.length)
      ) {
        this.$emit('associate', {})
      }
    },
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
    deSelect(object) {
      let resource = this.resourceList.find(
        resource => resource.id === object.id
      )
      if (resource) {
        resource.selected = false
      } else {
        resource = object
      }
      this.setSelectedResource(resource)
      this.setIncludeTypeOnChange()
    },
    handleclose() {
      this.$emit('update:visibility', false)
      this.$emit('update:siteId', null)
    },
    openSpace() {
      this.isAsset = false
      this.loadSpace()
    },
    openAsset() {
      this.isAsset = true
      this.loadAsset()
    },
    associate() {
      let obj = {}
      if (this.includeType !== 'all') {
        obj.resourceList = this.selectedList
        obj.isInclude = this.includeType === 'include'
      }
      this.$emit('associate', obj)
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
    onSpaceCategoryChange() {
      let { spaceCategory } = this
      this.onSpaceCategorySelected()
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
      if (
        this.selectedList &&
        selectedResources &&
        this.selectedList.length < selectedResources.length
      ) {
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
    onIncludeTypeChange() {
      if (this.includeType === 'all') {
        this.resourceList.forEach(resource => {
          this.$set(resource, 'selected', false)
        })
        this.selectedList = []
        this.selectall = false
      }
    },
    isSelected(resourceId) {
      return this.selectedList.find(resource => resource.id === resourceId)
    },
    onLocationChange(field) {
      this.onLocationSelected(field).then(() => {
        this.setListSelected()
      })
      this.setIncludeTypeOnChange()
    },
    setResourceData() {
      this.loadResourceData().then(() => {
        this.setListSelected()
      })
    },
    setListSelected() {
      let selectedIds = this.selectedList.map(resource => resource.id)
      if (this.isAsset) {
        this.assetList.map(asset => {
          if (selectedIds.includes(asset.id)) {
            this.$set(asset, 'selected', true)
          }
          return asset
        })
      } else {
        this.spaceList.map(space => {
          if (selectedIds.includes(space.id)) {
            this.$set(space, 'selected', true)
          }
          return space
        })
      }
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
.spaceassetchooser .el-dialog.fc-dialog-up {
  margin-top: 0px !important;
  width: 850px;
}
.grayicon {
  background-color: #d1d9dd;
}
.spaceassetchooser .el-dialog__header .el-icon-close:before {
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
#spaceassetchooser tr:nth-child(odd) {
  background-color: #fbfbfb;
}

#spaceassetchooser .el-icon-arrow-down {
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
#spaceassetchooser .fc-list-view-table td {
  font-size: 11px;
  font-weight: bold;
  padding-top: 8px;
  padding-bottom: 8px;
  padding-right: 10px;
  padding-left: 30px;
  cursor: pointer;
}
#spaceassetchooser .fc-list-view-table .contentrow {
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.4px;
  color: #333333;
  border-bottom: 1px solid transparent;
  border-top: 1px solid transparent;
}
#spaceassetchooser.multi .fc-list-view-table tr td:nth-child(2) {
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

#spaceassetchooser .el-input__inner {
  max-width: 100%;
  width: 135px;
  background-color: transparent !important;
  height: auto !important;
  white-space: nowrap;
  text-overflow: ellipsis;
  border: none !important;
}

#spaceassetchooser .el-input__inner::placeholder {
  color: #5a5e66;
}

#spaceassetchooser.multi .el-select {
  width: auto;
}

#spaceassetchooser .fc-list-search-wrapper .quick-search-input {
  border-bottom: 0 !important;
}

#spaceassetchooser .fc-list-view-table tbody tr.tablerow:hover {
  background-color: #effdff !important;
}

#spaceassetchooser.multi .asset-category .el-input__inner {
  font-size: 16px;
  letter-spacing: 0.6px;
  color: #333333;
}
.border-right-table {
  padding-bottom: 100px;
  margin-bottom: 50px;
}
.table-scroll-chooser {
  height: 100%;
  padding-bottom: 50px;
  overflow-x: hidden;
  border-right: 1px solid #e6ecf3;
  border-left: 1px solid transparent;
}
.searchicon .el-icon-search {
  font-size: 16px;
  color: #a2b2c4;
  font-weight: bold;
  margin-top: 17px;
  opacity: 0.6;
}
.chooser-search .quick-search-input {
  line-height: 2;
}
.chooser-search .close-icon {
  top: 8px;
  width: 31px !important;
  height: 31px !important;
  right: 0 !important;
}
.chooser-search .search-icon {
  width: 16px !important;
  opacity: 0.4;
}
.chooser-selected-container {
  height: 100%;
  padding-bottom: 50px;
  padding-left: 20px;
  padding-top: 10px;
  overflow: scroll;
  position: relative;
  margin-bottom: 30px;
}
.chooser-scroll-block {
  margin-right: 20px;
  max-height: 470px;
  overflow-y: scroll;
  padding-bottom: 50px;
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
</style>
