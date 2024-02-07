<template>
  <el-dialog
    class="reading-picker"
    :visible="true"
    width="50%"
    height="610px"
    :append-to-body="true"
    :show-close="false"
    :before-close="close"
  >
    <div class="fc__layout__has__row">
      <el-row>
        <el-col class="dialog_header d-flex justify-content-space">
          {{ title ? title : 'READINGS' }}
        </el-col>
      </el-row>

      <el-row>
        <el-col class="d-flex justify-around pT20 pB20">
          <template v-for="(field, index) in quickFilters">
            <FLookupField
              :key="index"
              :model.sync="field.value"
              :hideLookupIcon="true"
              :field="field"
              :disabled="field.disabled"
              :preHookFilterConstruction="resourceFilterConstruction"
              :fetchOptionsMethod="fetchOptions"
              @recordSelected="value => setLookUpFilter(field, value)"
              class="mL10 mR10"
            ></FLookupField>
          </template>
        </el-col>
      </el-row>

      <el-row>
        <el-col
          class="pL20 pB10 fc-black-color letter-spacing0_5 f13 bold d-flex items-baseline flex-direction-row flex-wrap"
        >
          <div class="pT5 pB10 mR10 flex-shrink-0">Selected</div>
          <template v-if="!$validation.isEmpty(selectedItems)">
            <template v-for="(name, index) in selectedItems">
              <div v-if="index < 1" :key="index" class="action-badge mR10 mb5">
                {{ name.assetName }} | {{ name.readingName }}
                <i
                  class="el-icon-close pointer mL-auto mB-auto mT-auto"
                  @click="handleClose(index)"
                ></i>
              </div>
            </template>
            <el-popover
              v-if="selectedItems.length > 1"
              placement="bottom"
              trigger="hover"
              popper-class="p0"
            >
              <slot :slot="'reference'">
                <div class="action-badge mR10 mb5">
                  +{{ selectedItems.length - 1 }}
                </div>
              </slot>
              <div class="reading-picker">
                <div
                  v-for="(name, index) in selectedItems.slice(1)"
                  :key="index"
                  class="mT5 mb5 d-flex close-icon f13 pT5 pB5 pL15 pR15"
                >
                  {{ name.assetName }} | {{ name.readingName }}
                  <i
                    class="el-icon-close mL-auto mB-auto mT-auto hover-actions"
                    @click="handleClose(index + 1)"
                  ></i>
                </div>
              </div>
            </el-popover>
          </template>
          <div v-else class="empty-selection-state">No Reading Selected</div>
        </el-col>
      </el-row>

      <el-row
        v-if="tab1EmptyState && !loading"
        class="height380 d-flex align-center"
      >
        <el-col>
          <div class="fc__layout__has__row mB30 align-center">
            <inline-svg
              src="svgs/emptystate/kpi"
              class="vertical-middle"
              iconClass="icon icon-xxxxlg"
            ></inline-svg>
            <div class="f12 grey-text2">No readings in applied filter</div>
          </div>
        </el-col>
      </el-row>

      <el-row v-else class="list-header">
        <el-col :span="12" class="border-right height380">
          <el-tabs v-model="activeTab" class="height-100">
            <div class="d-flex pT5 pB5 pL30 pR5 border-bottom">
              <el-input
                v-model="tab1SearchQuery"
                :placeholder="`Search ${placeHolder[activeTab]}`"
                :autofocus="true"
                @input="tab1FilterSearchQuery()"
                clearable
              ></el-input>
            </div>
            <el-tab-pane label="ASSETS" name="asset">
              <div
                v-if="tab1SearchLoading || loading"
                class="pL30 pR15 pT10 pB10 fc__layout__has__row"
              >
                <div
                  v-for="index in [1, 2, 3, 4, 5]"
                  :key="index"
                  class="flex-center-row-space pT10 pB10"
                >
                  <span class="circle loading-shimmer mR10"></span>
                  <span class="lines loading-shimmer width100"></span>
                </div>
              </div>
              <div v-else class="pL30 pR15 pT10 pB10 fc__layout__has__row">
                <el-radio
                  v-for="(name, id) in assetList"
                  :key="id"
                  :label="id"
                  v-model="tab1SelectedList"
                  class="fc-radio-btn pT10 pB10 mR0"
                  @change="
                    setSelectedObj({
                      assetId: id,
                      assetName: name,
                    })
                  "
                >
                  {{ name }}
                </el-radio>
              </div>
            </el-tab-pane>
            <el-tab-pane label="POINTS" name="point">
              <div
                v-if="tab1SearchLoading || loading"
                class="pL30 pR15 pT10 pB10 fc__layout__has__row"
              >
                <div
                  v-for="index in [1, 2, 3, 4, 5]"
                  :key="index"
                  class="flex-center-row-space pT10 pB10"
                >
                  <span class="circle loading-shimmer mR10"></span>
                  <span class="lines loading-shimmer width100"></span>
                </div>
              </div>
              <div v-else class="pL30 pR15 pT10 pB10 fc__layout__has__row">
                <el-radio
                  v-for="(name, id) in pointList"
                  :key="id"
                  :label="id"
                  v-model="tab1SelectedList"
                  class="fc-radio-btn pT10 pB10 mR0"
                  @change="
                    setSelectedObj({
                      readingId: id,
                      readingName: name,
                    })
                  "
                >
                  {{ name }}
                </el-radio>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-col>

        <el-col :span="12" class="height380 fc__layout__has__row">
          <div class="selected_header border-bottom text-uppercase">
            Select Readings
          </div>
          <div class="d-flex pT5 pB5 pL30 pR5 border-bottom">
            <el-input
              v-model="tab2SearchQuery"
              placeholder="Search Readings"
              :autofocus="true"
              @input="tab2FilterSearchQuery()"
              clearable
            ></el-input>
          </div>
          <div
            v-if="tab2SearchLoading || loading"
            class="pL30 pR15 pT10 pB10 fc__layout__has__row"
          >
            <div
              v-for="index in [1, 2, 3]"
              :key="index"
              class="flex-center-row-space pT10 pB10"
            >
              <span
                v-if="multiSelect"
                class="rectangle loading-shimmer mR10"
              ></span>
              <span v-else class="circle loading-shimmer mR10"></span>
              <span class="lines loading-shimmer width100"></span>
            </div>
          </div>
          <template v-else>
            <el-checkbox-group
              v-if="multiSelect"
              v-model="tab2SelectedList"
              class="pL30 pR15 pT10 pB10 overflow-scroll height-auto fc__layout__has__row"
            >
              <el-checkbox
                v-for="(item, index) in listToSelect"
                :key="index"
                :label="
                  `${item[selectedItemKey[0]]}_${item[selectedItemKey[1]]}`
                "
                class="checkbox"
              >
                {{ item[selectedItemDisplayKey] }}
              </el-checkbox>
            </el-checkbox-group>

            <div
              v-else
              class="pL30 pR15 pT10 pB10 fc__layout__has__row overflow-scroll height-auto"
            >
              <el-radio
                v-for="(item, index) in listToSelect"
                :key="index"
                :label="
                  `${item[selectedItemKey[0]]}_${item[selectedItemKey[1]]}`
                "
                v-model="tab2SelectedList"
                class="fc-radio-btn pT10 pB10 mR0"
                @change="save"
              >
                {{ item[selectedItemDisplayKey] }}
              </el-radio>
            </div>
          </template>
        </el-col>
      </el-row>

      <div v-if="multiSelect" class="modal-dialog-footer position-relative">
        <el-button @click="close()" class="modal-btn-cancel">
          CANCEL
        </el-button>
        <el-button type="primary" class="modal-btn-save" @click="save()">
          Save
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import debounce from 'lodash/debounce'
import { API } from '@facilio/api'
import FLookupField from '@/forms/FLookupField'
import FilterMixin from './ReadingPickerFiltersMixin'

export default {
  props: ['title', 'selectedList', 'multiSelect', 'filters'],
  mixins: [FilterMixin],
  components: { FLookupField },

  data() {
    return {
      activeTab: 'asset',
      placeHolder: {
        asset: 'Assets',
        point: 'Points',
      },
      loading: false,
      tab1SearchLoading: false,
      tab2SearchLoading: false,
      tab1SearchQuery: null,
      tab2SearchQuery: null,
      assetList: {},
      pointList: {},
      assetsVsPoints: {},
      pointsVsAssets: {},
      tab1SelectedList: null,
      selectedItems: [],
      selectedObj: {},
      selectedItemKey: ['assetId', 'readingId'],
    }
  },

  created() {
    if (!isEmpty(this.selectedList)) {
      let lastIndex = this.selectedList.length - 1
      let selectedObj = this.selectedList[lastIndex]

      this.selectedItems = [...this.selectedList]
      this.tab1SelectedList = selectedObj.assetId
    }

    this.constructQuickFilters()
    this.loadData()
  },

  computed: {
    tab1EmptyState() {
      let { assetList, pointList } = this

      return isEmpty(assetList) || isEmpty(pointList)
    },

    tab2EmptyState() {
      let { activeTab } = this

      if (activeTab === 'asset') return 'Select any asset to view its readings'
      else return 'Select any point to view its assets'
    },

    selectedItemDisplayKey() {
      let { activeTab } = this

      if (activeTab === 'asset') return 'readingName'
      else return 'assetName'
    },

    tab2SelectedList: {
      get() {
        let { selectedItems, multiSelect, selectedItemKey } = this
        let filteredList = selectedItems.map(
          item => `${item[selectedItemKey[0]]}_${item[selectedItemKey[1]]}`
        )

        return multiSelect ? filteredList : filteredList[0]
      },
      set(value) {
        let { multiSelect, selectedItemKey, listToSelect } = this
        let fieldList = multiSelect ? value : [value]

        let nonCurrentItemsId = fieldList.filter(
          item =>
            listToSelect.findIndex(
              list =>
                item ===
                `${list[selectedItemKey[0]]}_${list[selectedItemKey[1]]}`
            ) === -1
        )
        let nonCurrentItems = this.selectedItems.filter(list => {
          return nonCurrentItemsId.find(
            id =>
              id === `${list[selectedItemKey[0]]}_${list[selectedItemKey[1]]}`
          )
        })

        let currentItems = listToSelect.filter(list => {
          return fieldList.find(
            val =>
              val === `${list[selectedItemKey[0]]}_${list[selectedItemKey[1]]}`
          )
        })

        this.selectedItems = [...nonCurrentItems, ...currentItems]
      },
    },

    listToSelect() {
      let {
        selectedObj,
        selectedItemDisplayKey,
        pointsVsAssets,
        assetsVsPoints,
      } = this
      let listItems = []

      if (!isEmpty(selectedObj) && selectedItemDisplayKey) {
        if (selectedItemDisplayKey === 'readingName') {
          Object.entries(pointsVsAssets).forEach(([id, name]) => {
            let listObj = { ...selectedObj }

            listObj['readingId'] = id
            listObj['readingName'] = name

            listItems.push(listObj)
          })
        } else {
          Object.entries(assetsVsPoints).forEach(([id, name]) => {
            let listObj = { ...selectedObj }

            listObj['assetId'] = id
            listObj['assetName'] = name

            listItems.push(listObj)
          })
        }
      }

      return listItems
    },
  },

  watch: {
    activeTab() {
      this.tab1SelectedList = null
      this.onDataLoad()
    },
  },

  methods: {
    loadData() {
      let promises = []

      this.loading = true
      promises.push(this.loadAssets())
      promises.push(this.loadReadings())

      Promise.all(promises)
        .then(([assets, readings]) => {
          this.assetList = assets
          this.pointList = readings
        })
        .finally(() => {
          this.onDataLoad()
          this.loading = false
        })
    },

    getParams(fetchList, filters) {
      let { search = null, fieldIds = [], assetIds = [] } = filters || {}
      let params = {
        page: 1,
        perPage: 50,
      }

      if (fetchList === 'asset') {
        params.fetchOnlyAssets = true
      } else {
        params.fetchOnlyReadings = true
      }
      if (!isEmpty(search)) params.search = search
      if (!isEmpty(fieldIds)) params.fieldIds = fieldIds
      if (!isEmpty(assetIds)) params.assetIds = assetIds
      if (!isEmpty(this.buildingIds)) params.buildingIds = this.buildingIds
      if (!isEmpty(this.categoryIds)) params.categoryIds = this.categoryIds

      return params
    },

    loadAssets(filters) {
      let url = 'asset/getAssetsWithReadings'
      let params = this.getParams('asset', filters)

      return API.post(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error('Cannot find filtered Asset')
        } else {
          return data.assets
        }
      })
    },

    loadReadings(filters) {
      let url = 'asset/getAssetsWithReadings'
      let params = this.getParams('reading', filters)

      return API.post(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error('Cannot find filtered Readings')
        } else {
          let { fields = {} } = data
          let pointList = {}

          Object.entries(fields).forEach(([key, value]) => {
            pointList[key] = value['displayName']
          })
          return pointList
        }
      })
    },

    onDataLoad() {
      let selectedObj

      if (!this.tab1EmptyState) {
        if (this.activeTab === 'asset') {
          let selectedItem = this.tab1SelectedList
            ? this.assetList[this.tab1SelectedList]
            : null

          if (!selectedItem) {
            this.tab1SelectedList = Object.keys(this.assetList)[0]
            selectedItem = this.assetList[this.tab1SelectedList]
          }

          selectedObj = {
            assetId: this.tab1SelectedList,
            assetName: selectedItem,
          }
        } else {
          let selectedItem = this.tab1SelectedList
            ? this.pointList[this.tab1SelectedList]
            : null

          if (!selectedItem) {
            this.tab1SelectedList = Object.keys(this.pointList)[0]
            selectedItem = this.pointList[this.tab1SelectedList]
          }

          selectedObj = {
            readingId: this.tab1SelectedList,
            readingName: selectedItem,
          }
        }

        this.setSelectedObj(selectedObj)
      }
    },

    tab1FilterSearchQuery: debounce(function() {
      this.tab1SearchLoading = true
      let filter = { search: this.tab1SearchQuery }

      if (this.activeTab === 'asset') {
        this.loadAssets(filter).then(assets => {
          this.assetList = assets
          this.tab1SearchLoading = false
        })
      } else {
        this.loadReadings(filter).then(readings => {
          this.pointList = readings
          this.tab1SearchLoading = false
        })
      }
    }, 1000),

    tab2FilterSearchQuery: debounce(function() {
      this.tab2SearchLoading = true
      let filter = { search: this.tab2SearchQuery }

      if (this.activeTab === 'asset') {
        filter.assetIds = [this.selectedObj.assetId]
        this.loadReadings(filter).then(readings => {
          this.pointsVsAssets = readings
          this.tab2SearchLoading = false
        })
      } else {
        filter.fieldIds = [this.selectedObj.readingId]
        this.loadAssets(filter).then(assets => {
          this.assetsVsPoints = assets
          this.tab2SearchLoading = false
        })
      }
    }, 1000),

    setSelectedObj(selectedId) {
      this.tab2SearchLoading = true
      if (this.selectedItemDisplayKey === 'readingName') {
        let params = { assetIds: [selectedId.assetId] }

        this.loadReadings(params).then(readings => {
          this.pointsVsAssets = readings
          this.tab2SearchLoading = false
        })
      } else {
        let params = { fieldIds: [selectedId.readingId] }

        this.loadAssets(params).then(assets => {
          this.assetsVsPoints = assets
          this.tab2SearchLoading = false
        })
      }
      this.selectedObj = selectedId
    },

    handleClose(index) {
      this.selectedItems.splice(index, 1)
    },

    save() {
      this.$emit('selectedItems', this.selectedItems)
      this.close()
    },

    close() {
      this.$emit('close')
    },
  },
}
</script>
<style lang="scss">
.reading-picker {
  .el-dialog__header {
    padding: 0px;
  }

  .el-dialog__body {
    padding: 0px;
  }

  .el-tabs__header {
    background: #f9f9f9;
    padding-left: 30px;
    border-bottom: 1px solid rgb(229, 228, 228);
    margin-bottom: 0px;
  }

  .el-tabs__content {
    display: flex;
    flex-direction: column;
    height: 340px;
  }

  .el-tab-pane {
    overflow: scroll;
  }

  .el-radio__label {
    padding-right: 10px;
  }

  .el-input .el-input__inner {
    border: none;
  }

  .list-header {
    border-top: 1px solid rgb(229, 228, 228);
  }

  .border-bottom {
    border-bottom: 1px solid #e5e4e4;
  }

  .dialog_header {
    font-size: 14px;
    font-weight: bold;
    letter-spacing: 0.5px;
    padding: 20px 30px;
    color: #25243e;
    border-bottom: 1px solid #e5e4e4;
  }

  .selected_header {
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 0.5px;
    padding: 12px 30px;
    color: #25243e;
  }

  .checkbox {
    padding: 11px 0px;
    margin-right: 0;
  }

  .action-badge {
    border-radius: 12px;
    border: solid 1px #39b2c2;
    color: #39b2c2;
    font-size: 11px;
    padding: 5px 10px;
  }

  .lines {
    height: 16px;
    border-radius: 5px;
  }

  .circle {
    height: 22px;
    width: 22px;
    border-radius: 50%;
  }

  .rectangle {
    height: 18px;
    width: 20px;
    margin: 2px 0px;
  }

  .empty-selection-state {
    color: #c8c8c8;
    font-size: 11px;
  }

  .close-icon .hover-actions {
    visibility: hidden;
  }

  .close-icon:hover {
    background-color: #f5f7fa;
  }

  .close-icon:hover .hover-actions {
    visibility: visible;
    cursor: pointer;
  }
}
</style>
