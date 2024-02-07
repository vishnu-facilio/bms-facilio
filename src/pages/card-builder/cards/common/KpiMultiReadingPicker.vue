<template>
  <div>
    <div class="d-flex justify-content-space">
      <p class="fc-input-label-txt"></p>
    </div>
    <div class="text-right" v-if="type === 'moduleKPI'">
      <el-button
        class="fc-add-border-green-btn text-uppercase mB20 mR10"
        @click="canShow = true"
      >
        <i class="el-icon-plus fwBold"></i>
        {{ $t('common.header.add_new_kpi') }}
      </el-button>
    </div>
    <div class="position-relative">
      <el-row
        v-for="(kpi, index) in kpidata"
        class="row  kpi-multi-reading-border"
        :key="index + 'row'"
        style="width: 98% !important;"
        v-bind:class="{
          mb70: index === kpidata.length - 1,
          mB10: index !== kpidata.length - 1,
        }"
      >
        <el-col :span="24" class=" width100x`" v-if="type === 'moduleKPI'">
          <el-form-item>
            <el-col :span="24" class="p0">
              <el-form-item prop="reading" class="mB10">
                <p class="fc-input-label-txt pB5">
                  {{ $t('common.products.choose_kpi') }}
                </p>
                <el-select
                  v-model="kpi.kpiId"
                  :placeholder="$t('common.header.please_select_kpi')"
                  class="width100 el-input-textbox-full-border"
                  @change="emitReading(index)"
                >
                  <template v-for="(kpi, index) in moduleKPI">
                    <el-option
                      :label="kpi.name"
                      :value="kpi.id"
                      :key="index"
                    ></el-option>
                  </template>
                </el-select>

                <i
                  v-if="!$validation.isEmpty(kpi.kpiId)"
                  class="el-icon-edit pointer mT13 position-absolute"
                  style="right: 35px;"
                  data-arrow="true"
                  :title="$t('common.header.edit_select_kpi')"
                  v-tippy
                  @click="editKPI(kpi)"
                ></i>
              </el-form-item>
            </el-col>
          </el-form-item>
          <el-form-item class="mB10">
            <el-row class="mB10">
              <el-col :span="24">
                <p class="fc-input-label-txt pB5">
                  {{ $t('common.header.label') }}
                </p>

                <el-input
                  :autofocus="true"
                  class="addReading-title el-input-textbox-full-border width100"
                  v-model="kpi.label"
                  @change="emitReading"
                  :placeholder="$t('common._common.enter_the_label')"
                ></el-input>
              </el-col>
            </el-row>
          </el-form-item>
        </el-col>
        <el-col :span="24" class="width100" v-else>
          <el-form-item>
            <el-col :span="24" class="p0">
              <el-form-item prop="reading" class="mB10">
                <p class="fc-input-label-txt pB5">
                  {{ $t('common.header.kpi_category') }}
                </p>
                <div v-if="categoryLoading">
                  <FieldLoader :isLoading="true"> </FieldLoader>
                </div>
                <div v-else>
                  <el-select
                    v-model="kpi.categoryId"
                    @change="() => emptyKpiAndAsset(index)"
                    :placeholder="
                      $t('common.header.please_select_kpi_category')
                    "
                    class="width100 el-input-textbox-full-border"
                  >
                    <template v-for="(value, key) in kpiCategoryList">
                      <el-option
                        :label="value"
                        :value="key"
                        :key="key"
                      ></el-option>
                    </template>
                  </el-select>
                </div>
              </el-form-item>
            </el-col>
          </el-form-item>
          <el-form-item>
            <el-row :gutter="10">
              <el-col :span="10">
                <el-form-item prop="reading" class="mB10">
                  <!-- <p class="fc-input-label-txt pB5">KPI</p> -->
                  <div v-if="kpiloading">
                    <FieldLoader :isLoading="true"> </FieldLoader>
                  </div>
                  <div v-else>
                    <el-select
                      v-model="kpi.kpiId"
                      filterable
                      :loading="loading"
                      clearable
                      remote
                      @change="kpiId => assetSpaceList(kpiId, index)"
                      @clear="getKpiList"
                      :placeholder="$t('common.header.please_select_kpi')"
                      class="width100 el-input-textbox-full-border"
                      :remote-method="value => searchkpilist(null, value)"
                    >
                      <template
                        v-for="(category, index) in kpiList.filter(
                          rt =>
                            Number(rt.kpiCategory) === Number(kpi.categoryId)
                        )"
                      >
                        <el-option
                          :label="category.name"
                          :value="category.id"
                          :key="index"
                        ></el-option>
                      </template>
                    </el-select>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item prop="reading" class="mB10">
                  <!-- <p class="fc-input-label-txt pB5">resources</p> -->
                  <div v-if="assetloading">
                    <FieldLoader :isLoading="true"> </FieldLoader>
                  </div>
                  <div v-else>
                    <el-select
                      v-model="kpi.parentId"
                      :placeholder="$t('common.header.please_select_asset')"
                      class="width100 el-input-textbox-full-border"
                      @change="() => emitReading(index)"
                    >
                      <el-option
                        :label="parent.name"
                        :value="parent.id"
                        :key="index"
                        v-for="(parent, index) in assetSpaceObject[kpi.kpiId]"
                      ></el-option>
                    </el-select>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item prop="reading" class="mB10">
                  <!-- <p class="fc-input-label-txt pB5">agg</p> -->
                  <el-select
                    v-model="kpi.yAggr"
                    :placeholder="$t('common._common.aggr')"
                    class="width100 el-input-textbox-full-border"
                    @change="() => emitReading(index)"
                  >
                    <el-option
                      v-for="(fn, index) in aggregateFunctions"
                      :label="fn.label"
                      :value="fn.value"
                      :key="index"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form-item>

          <el-form-item class="mB10">
            <el-row class="mB10">
              <el-col :span="24">
                <p class="fc-input-label-txt pB5">
                  {{ $t('common.header.label') }}
                </p>

                <el-input
                  :autofocus="true"
                  class="addReading-title el-input-textbox-full-border width100"
                  v-model="kpi.label"
                  @change="() => emitReading(index)"
                  :placeholder="$t('common._common.enter_the_label')"
                ></el-input>
              </el-col>
            </el-row>
          </el-form-item>
        </el-col>
        <div>
          <div class="d-flex justify-content-start item-controls f16">
            <!-- <img
            src="~assets/add-icon.svg"
            style="height:18px;width:18px;"
            class="pointer mR10"
            @click="add(index)"
            v-if="index === kpidata.length - 1"
            title="Add Reading"
            v-tippy
          /> -->
            <el-button
              v-if="index === kpidata.length - 1"
              @click="add(index)"
              class="fc-add-border-green-btn text-uppercase mB20 new-row-btn"
            >
              <i class="el-icon-plus fwBold"></i>
              {{ $t('common.header.add_new') }}
            </el-button>
            <img
              src="~assets/remove-icon.svg"
              v-if="kpidata && kpidata.length > 1"
              style="height:18px;width:18px;position: absolute; top: 50%; right: -8px;"
              class="pointer"
              @click="remove(index)"
              :title="$t('common._common.remove_reading')"
              v-tippy
            />
          </div>
        </div>
      </el-row>
    </div>

    <add-module-kpi
      v-if="canShow && type === 'moduleKPI'"
      :isNew="isNewKpi"
      :kpi="kpiObj"
      @onSave="onKpiCreated"
      @onClose="canShow = false"
    ></add-module-kpi>
  </div>
</template>

<script>
import {
  aggregateFunctions,
  dateOperators,
} from 'pages/card-builder/card-constants'
import AddModuleKpi from 'pages/energy/kpi/components/AddModuleKpi'
import { isEmpty } from '@facilio/utils/validation'
import { deepCloneObject } from 'util/utility-methods'
import { getFieldOptions } from 'util/picklist'
import FieldLoader from '@/forms/FieldLoader'
import isString from 'lodash/isString'
import { API } from '@facilio/api'
import debounce from 'lodash/debounce'
import cloneDeep from 'lodash/cloneDeep'

export default {
  components: {
    AddModuleKpi,
    FieldLoader,
  },
  props: ['type', 'initialReading', 'clearData'],
  data() {
    return {
      canShow: false,
      moduleKPI: [],
      emptydata: {
        parentId: null,
        parentType: 'Asset',
        categoryId: null,
        kpiId: null,
        yAggr: 'sum',
        label: null,
      },
      kpidata: [
        {
          parentId: null,
          parentType: 'Asset',
          categoryId: null,
          kpiId: null,
          yAggr: 'sum',
          dateRange: null,
          label: null,
          showPathtext: true,
          isNewKpi: false,
        },
      ],
      emptykpi: {
        parentId: null,
        parentType: 'Asset',
        categoryId: null,
        kpiId: null,
        yAggr: 'sum',
        dateRange: null,
        showPathtext: true,
        label: null,
      },
      kpiCategoryList: null,
      kpiList: [],
      aggregateFunctions,
      isNewKpi: true,
      kpiObj: {},
      dateOperators,
      categoryLoading: true,
      loading: true,
      kpiloading: true,
      assetloading: true,
      assetSpaceObject: {},
    }
  },

  created() {
    if (!isEmpty(this.initialReading))
      this.deserializeReading(this.initialReading)
  },
  mounted() {
    if (this.clearData) {
      this.kpidata = [this.emptykpi]
    }
    if (this.type === 'moduleKPI') {
      this.getModulekpiList()
    } else {
      let defaultIds = this.kpidata.reduce((ids, kpi) => {
        let id = kpi.kpiId
        if (!isEmpty(id)) {
          if (isString(id)) {
            return [...ids, `${id.split('_')[0]}`]
          } else {
            return [...ids, `${id}`]
          }
        }
        return ids
      }, [])
      this.getKpiList(defaultIds)
      this.getKpiCategoryList()
    }
    this.searchkpilist = debounce(this.getKpiList, 500)
  },
  computed() {},
  methods: {
    emptyKpiAndAsset(index) {
      this.kpidata[index].kpiId = null
      this.kpidata[index].parentId = null
    },
    assetSpaceList(kpiId, index) {
      this.kpidata[index].parentId = null
      this.kpidata[index].isNewKpi = false
      this.assetOrSpaceList(kpiId)
    },
    async assetOrSpaceList(kpiId) {
      let { kpiList } = this
      let obj = {}
      let kpi = kpiList.find(element => {
        return element.id === kpiId
      })
      if (!isEmpty(kpi)) {
        if (isString(kpiId) && kpiId.includes('new')) {
          let key = kpi.assets ? 'id' : 'category'
          let filterValue = []
          if (!isEmpty(kpi.assets)) {
            filterValue = kpi.assets.map(id => String(id))
          } else {
            filterValue = [`${kpi.assetCategory?.id}`]
          }
          let { list, error } = await API.fetchAll('asset', {
            filters: JSON.stringify({
              [key]: {
                operatorId: 36,
                value: filterValue,
              },
            }),
          })
          if (error) {
            this.$message.error(
              error.message || this.$t('common._common.error_occured')
            )
          } else {
            obj[kpi.id] = list
          }
        } else if (!isEmpty(kpi.matchedResources)) {
          obj[kpi.id] = kpi.matchedResources
        }
      }
      this.assetSpaceObject = { ...this.assetSpaceObject, ...obj }
      this.assetloading = false
    },
    addEmpty() {
      let data = deepCloneObject(this.emptykpi)
      this.kpidata.push(data)
    },
    add() {
      this.addEmpty()
      this.getKpiList()
    },
    remove(index) {
      this.kpidata.splice(index, 1)
    },
    getModulekpiList() {
      let url = `/v2/kpi/module/list`
      this.$http.get(url).then(({ data }) => {
        this.moduleKPI = data.result.kpis || []
      })
    },
    emitReading(index) {
      let { kpidata } = this

      if (this.type === 'moduleKPI') {
        if (index > -1) {
          let kpiId = kpidata[index].kpiId
          let kpi = this.moduleKPI.find(rt => rt.id === kpiId)
          if (kpidata[index].label === null) {
            kpidata[index].label = kpi.name
          }
        }
        let filterData = this.serializeReading(kpidata)
        this.$emit('onReadingSelect', filterData)
        this.moduleKpiSelected(filterData)
      } else {
        let kpiId = kpidata[index]?.kpiId ?? null
        if (isString(kpiId) && kpiId.includes('new')) {
          kpidata[index].isNewKpi = true
        }
        this.$emit('onReadingSelect', this.serializeReading(kpidata))
      }
    },
    moduleKpiSelected(filterData) {
      let selectedObject = this.moduleKPI.find(rt => rt.id === filterData.kpiId)
      this.$emit('onModuleKpiSelect', selectedObject)
    },
    getColor() {
      return (
        '#' +
        Math.random()
          .toString(16)
          .substr(2, 6)
      )
    },
    serializeReading(data) {
      let i = 0
      data.forEach(rt => {
        if (!rt.pathColor) {
          this.$set(rt, 'pathColor', this.getColor())
        }
        if (!rt.textColor) {
          this.$set(rt, 'textColor', '#000000')
        }
        if (!rt.hasOwnProperty('showPathtext')) {
          this.$set(rt, 'showPathtext', true)
        }
        i++
      })
      return data
    },
    deserializeReading(initialReading) {
      this.kpidata = (initialReading || []).map(kpi => {
        if (kpi.isNewKpi && !isString(kpi.kpiId)) {
          kpi.kpiId = kpi.kpiId + '_new'
        }
        return kpi
      })
    },
    filterList(list) {
      let kpiIds = this.kpiList.reduce((ids, kpi) => [...ids, kpi.id], [])
      let filteredList = []
      filteredList = list.filter(kpi => {
        if (!kpiIds.includes(kpi.id)) {
          return kpi
        }
        return false
      })
      return filteredList
    },
    async getKpiCategoryList() {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'kpiCategory', skipDeserialize: true },
      })
      this.categoryLoading = false
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.kpiCategoryList = options
      }
    },
    async getKpi(params) {
      let newKpiList = []
      let oldKpiList = []
      if (this.$helpers.isLicenseEnabled('NEW_KPI')) {
        let { error, data } = await API.get(
          `/v3/modules/data/list?moduleName=readingkpi`,
          params
        )
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          let readingkpi = cloneDeep(data?.readingkpi ?? [])
          newKpiList = readingkpi.map(kpi => {
            if (!isString(kpi.id)) {
              kpi.id = kpi.id + '_new'
            }
            return kpi
          })
        }
      }
      let { error, data } = await API.get(`/v2/kpi/list?type=1&`, params)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        let formulaList = data?.formulaList ?? []
        oldKpiList = formulaList
      }
      this.kpiList = newKpiList.concat(oldKpiList)
    },
    async getKpiList(ids, searchText) {
      this.loading = true
      let params = {}
      let filter = isEmpty(searchText)
        ? {}
        : {
            name: {
              operatorId: 5,
              value: [searchText],
            },
          }
      if (!isEmpty(ids)) {
        let filter = {
          id: {
            operatorId: 9,
            value: ids,
          },
        }
        params.filters = JSON.stringify(filter)
        await this.getKpi(params)
      }
      let selectedKpi = []
      selectedKpi = this.kpiList
      params.filters = JSON.stringify(filter)
      await this.getKpi(params)
      if (!isEmpty(selectedKpi)) {
        let kpis = this.filterList(selectedKpi)
        this.kpiList = this.kpiList.concat(kpis)
      }
      this.kpiloading = false
      this.loading = false
      this.kpidata.forEach(element => this.assetOrSpaceList(element.kpiId))
    },

    onKpiCreated(kpiObj) {
      if (this.type === 'moduleKPI') {
        let kpi = this.moduleKPI.find(kpi => kpi.id === kpiObj.id)
        if (isEmpty(kpi)) this.moduleKPI.push(kpiObj)
        else {
          let index = this.moduleKPI.findIndex(kpi => kpi.id === kpiObj.id)
          this.moduleKPI.splice(index, 1, kpiObj)
        }
        this.kpidata[0].kpiId = kpiObj.id
        this.emitReading()
      }
    },
    editKPI({ kpiId }) {
      let listOfKpi = []
      if (this.type === 'moduleKPI') listOfKpi = this.moduleKPI
      this.kpiObj = listOfKpi.find(Kpi => Kpi.id === kpiId) || {}
      this.isNewKpi = false
      this.canShow = true
    },
  },
}
</script>
<style>
.new-row-btn {
  position: absolute;
  left: 00;
  bottom: -68px;
}
.mb70 {
  margin-bottom: 70px;
}
</style>
