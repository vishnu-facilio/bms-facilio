<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="false"
    open="top"
    custom-class=" fc-dialog-up spaceassetchooser setup-dialog60"
    :append-to-body="true"
    :before-close="handleclose"
  >
    <div id="spaceassetchooser">
      <div style="height: 600px;max-height: 600px;">
        <div
          class="sidebar"
          v-if="!hideSidebar"
          style="height:100%;width:33%;display: inline-block;"
        >
          <div class="pT30" style="margin-left:30px;z-index:100">
            <div
              class="flLeft"
              @click="picktype !== 'asset' ? openSpace() : null"
              style="padding-right:30px"
              :disabled="picktype === 'asset'"
            >
              <div class="flLeft" style="padding-top: 1px;">
                <i
                  class="el-icon-circle-check"
                  v-bind:class="isAsset ? 'normalspace' : 'selectedspace'"
                ></i>
              </div>
              <div class="txtstyle pointer flLeft" style="padding-left: 5px;">
                {{ $t('common.space_asset_chooser.space') }}
              </div>
              <div class="clearboth"></div>
            </div>
            <div
              class="flLeft"
              @click="picktype !== 'space' ? openAsset() : null"
              :disabled="picktype === 'space'"
            >
              <div class="flLeft" style="padding-top: 1px;">
                <i
                  class="el-icon-circle-check"
                  v-bind:class="isAsset ? 'selectedspace' : 'normalspace'"
                ></i>
              </div>
              <div class="flLeft">
                <span class="txtstyle pointer" style="padding-left: 5px;">{{
                  $t('common.space_asset_chooser.asset')
                }}</span>
              </div>
              <div class="clearboth"></div>
            </div>
            <div class="clearboth"></div>
          </div>
          <div style="margin-left:30px">
            <div class="space-heading pT30 pB5">
              {{ $t('common.space_asset_chooser.by_location') }}
            </div>
            <div
              v-for="field in resourceConfig"
              :key="field.spaceType"
              class="pB15"
            >
              <el-select
                v-model="field.value"
                filterable
                clearable
                :placeholder="field.placeHolder"
                :data-test-selector="`${field.placeHolder}`"
                :disabled="
                  (field.spaceType === 4 && !isAsset) || field.disabled
                "
                @change="onLocationSelected(field)"
              >
                <el-option
                  v-for="option in field.options"
                  :key="option.id"
                  :label="option.name"
                  :value="option.id"
                  :data-test-selector="`${field.placeHolder}_${option.name}`"
                ></el-option>
              </el-select>
            </div>
            <div class="pT30">
              <div class="space-heading pT15 pB5">
                {{ $t('common.space_asset_chooser.by_category') }}
              </div>
              <div>
                <el-select
                  v-if="isAsset"
                  v-model="assetCategory.value"
                  filterable
                  clearable
                  :placeholder="assetCategory.placeHolder"
                  :data-test-selector="
                    `${$t('common.space_asset_chooser.by_category')}`
                  "
                  :disabled="!isAsset"
                  @change="onCategorySelected"
                >
                  <el-option
                    v-for="(label, value) in assetCategory.options"
                    :key="value"
                    :label="label"
                    :value="value"
                    :data-test-selector="
                      `${$t('common.space_asset_chooser.by_category')}_${label}`
                    "
                  ></el-option>
                </el-select>
                <el-select
                  v-else
                  v-model="spaceCategory.value"
                  filterable
                  clearable
                  :placeholder="spaceCategory.placeHolder"
                  @change="onSpaceCategorySelected"
                  class="inline asset-category fc-input-full-border2"
                >
                  <el-option
                    v-for="(label, value) in spaceCategory.options"
                    :key="value"
                    :label="label"
                    :value="value"
                  ></el-option>
                </el-select>
              </div>
            </div>
          </div>
        </div>
        <div
          class="pull-right heightlist"
          :style="{ width: !hideSidebar ? '67%' : '100%' }"
        >
          <div style="padding-left:20px;padding-right:20px;">
            <div
              v-if="!showQuickSearch"
              class="flex-middle justify-content-space"
            >
              <span class="spaceheading" style="display: inline-block;">{{
                !isAsset
                  ? this.$t('common.space_asset_chooser.space_list')
                  : this.$t('common.space_asset_chooser.asset_list')
              }}</span>
              <span class="pull-right pointer"
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
                    <title>{{ $t('common._common.search') }}</title>
                    <path
                      d="M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"
                    ></path>
                  </svg>
                  <input
                    ref="quickSearchQuery"
                    autofocus="autofocus"
                    type="text"
                    v-model="quickSearchQuery"
                    @keyup.enter="loadResourceData"
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
          <div
            class="scroll"
            v-infinite-scroll="loadMores"
            infinite-scroll-distance="10"
            infinite-scroll-immediate-check="true"
          >
            <v-infinite-scroll
              :loading="loading"
              @bottom="nextPage"
              :offset="20"
              style="max-height: 500px;height: 500px; overflow-y: scroll;"
            >
              <table class=" pT15 fc-list-view-table">
                <thead>
                  <tr
                    class="tablerow pT30"
                    style="background-color:#ffffff;padding:15px"
                  >
                    <td class="headerrow width30">
                      #{{ $t('common._common.id') }}
                    </td>
                    <td class="headerrow width30">
                      {{ $t('common.space_asset_chooser.name') }}
                    </td>
                    <td v-if="!hideSidebar" class="headerrow width30">
                      {{ $t('common.space_asset_chooser.category') }}
                    </td>
                    <td></td>
                    <td></td>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-if="!isAsset"
                    v-for="space in spaceList"
                    :key="space.id"
                    class="tablerow"
                    v-on:click="select(space, 1)"
                    v-bind:class="{ selectedrow: selectedObj === space }"
                    :data-test-selector="`${space.name}`"
                  >
                    <td class="contentrow width30">
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
                    <td class="contentrow width30">{{ space.name }}</td>
                    <td class="contentrow width30">{{ space.spaceTypeVal }}</td>
                    <td></td>
                    <td>
                      <i
                        class="el-icon-check"
                        v-bind:style="{
                          visibility:
                            selectedObj === space ? 'visible' : 'hidden',
                        }"
                        style="color:#39b2c2;padding-left: 20px;"
                      ></i>
                    </td>
                  </tr>
                  <tr
                    v-if="isAsset"
                    v-for="asset in assetList"
                    :key="'asset_' + asset.id"
                    class="tablerow"
                    v-on:click="select(asset, 2)"
                    v-bind:class="{ selectedrow: selectedObj === asset }"
                    :data-test-selector="`${asset.name}`"
                  >
                    <td class="contentrow width30">#{{ asset.id }}</td>
                    <td class="contentrow width30">{{ asset.name }}</td>
                    <td v-if="!hideSidebar" class="contentrow width30">
                      {{
                        asset.category
                          ? getAssetCategory(asset.category.id).displayName
                          : '---'
                      }}
                    </td>
                    <td></td>
                    <td>
                      <i
                        class="el-icon-check"
                        v-bind:style="{
                          visibility:
                            selectedObj === asset ? 'visible' : 'hidden',
                        }"
                        style="color:#39b2c2;padding-left: 20px;"
                      ></i>
                    </td>
                  </tr>
                  <tr
                    v-if="spaceList.length < 1 && assetList.length < 1"
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
          </div>
          <div class="space-asset-footer">
            <div class="space-asset-footer-align">
              <el-button
                data-test-selector="SpaceAsset_cancel"
                @click="handleclose"
                style="font-size: 13px; font-weight: bold; border:2px solid #cde2e5;color: #8eb1b6; letter-spacing: 0.5px;padding: 8px;width: 100px;text-transform: uppercase;"
                >{{ $t('common._common.cancel') }}</el-button
              >
              <el-button
                data-test-selector="SpaceAsset_save"
                type="primary"
                style="font-size: 13px; font-weight: bold; letter-spacing: 0.5px;background-color:#39b2c2;border-color:#39b2c2;padding: 8px;text-transform: uppercase;"
                @click="associate"
                >{{ $t('common._common.select') }}</el-button
              >
            </div>
          </div>
        </div>
      </div>
      <div style="position: absolute; top: 40%;left:55%;z-index:5">
        <spinner :show="resourceLoading" size="70"></spinner>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import infiniteScroll from 'vue-infinite-scroll'
import ResourceMixin from '@/mixins/ResourceMixin'
import VInfiniteScroll from 'v-infinite-scroll'
import Spinner from '@/Spinner'
import { mapGetters } from 'vuex'
export default {
  mixins: [ResourceMixin],
  props: [
    'visibility',
    'appendToBody',
    'query',
    'picktype',
    'hideSidebar',
    'isService',
  ],
  directives: {
    infiniteScroll,
  },
  components: {
    Spinner,
    VInfiniteScroll,
  },
  data() {
    return {
      fetchingMore: false,
      selected: false,
      selectedObj: {},
      showQuickSearch: false,
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
  },
  mounted() {
    // quickSearchQuery
    if (this.picktype === 'asset') {
      this.isAsset = true
    }
    this.initResourceData()
  },
  computed: {
    ...mapGetters(['getAssetCategory']),
    scrollDisabled() {
      return false
    },
  },
  component: {
    Spinner,
  },
  watch: {
    visibility(val) {
      if (val && !this.query) {
        this.reInit()
        if (this.hideSidebar) {
          this.showQuickSearch = true
          this.$nextTick(() => {
            if (this.$refs.quickSearchQuery) {
              this.$refs.quickSearchQuery.focus()
            }
          })
        }
      } else if (
        !val &&
        this.query &&
        (!this.selectedObj || !this.selectedObj.id)
      ) {
        this.$emit('associate', {}, this.isAsset ? 'asset' : 'space')
      }
    },
    query(val) {
      this.quickSearchQuery = val
      if (val && this.$refs.quickSearchQuery) {
        this.selectedObj = {}
        this.showQuickSearch = true
        this.$nextTick(() => {
          this.$refs.quickSearchQuery.focus()
        })
        this.loadResourceData()
      }
    },
  },
  methods: {
    loadMores() {
      this.fetchingMore = true
      this.openAsset(true)
    },
    reInit() {
      this.showQuickSearch = false
      this.quickSearchQuery = null
      this.paging.page = 1
      this.checkAndInitResourceData()
    },
    handleclose() {
      this.$emit('update:visibility', false)
    },
    openSpace() {
      this.isAsset = false
      this.paging.page = 1
      this.loadSpace()
    },
    openAsset(isLoad) {
      this.isAsset = true
      this.paging.page = 1
      this.loadAsset(isLoad)
    },
    select(obj, type) {
      this.selectedObj = obj
    },
    associate() {
      this.$emit(
        'associate',
        this.selectedObj,
        this.isAsset ? 'asset' : 'space'
      )
    },
    toggleQuickSearch() {
      this.showQuickSearch = !this.showQuickSearch
      if (!this.showQuickSearch) {
        this.quickSearchQuery = null
        this.loadResourceData()
      } else {
        this.$nextTick(() => {
          this.$refs.quickSearchQuery.focus()
        })
      }
    },
  },
}
</script>
<style>
.heightlist {
  background-color: #ffffff;
  padding-top: 20px;
  display: inline-block;
}
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
  width: 60%;
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
.space-heading {
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
  color: #324056;
  font-weight: 700;
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
  padding: 10px;
  padding-right: 10px;
  padding-left: 30px;
  border-top: 1px solid transparent !important;
  cursor: pointer;
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
.spaceassetchooser .scroll {
  height: 70%;
  max-height: 70%;
  overflow-y: auto;
}
.selectedrow {
  background-color: #effdff !important;
}

#spaceassetchooser .el-input__inner {
  background-color: transparent !important;
  height: auto !important;
  white-space: nowrap;
  text-overflow: ellipsis;
  border-bottom: none;
  border-radius: 3px;
  padding-left: 10px;
  padding-right: 10px;
  max-width: 84%;
}
#spaceassetchooser .el-select .el-input .el-select__caret {
  color: #5a7591 !important;
  font-size: 14px !important;
  font-weight: 600 !important;
}

#spaceassetchooser .el-input__inner::placeholder {
  color: #5a5e66;
}

#spaceassetchooser .el-select {
  width: 90%;
}

#spaceassetchooser .fc-list-search-wrapper .quick-search-input {
  border-bottom: 0 !important;
  margin-bottom: 0px !important;
}

#spaceassetchooser .fc-list-view-table tbody tr.tablerow:hover {
  background-color: #effdff !important;
}
.space-asset-footer {
  width: 67%;
  height: 60px;
  position: absolute;
  bottom: 0;
  right: 0;
  height: 60px;
  background: #fff;
}
.space-asset-footer .space-asset-footer-align {
  padding-top: 15px;
  padding-bottom: 12px;
  float: right;
  padding-right: 30px;
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
</style>
