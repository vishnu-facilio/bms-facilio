<template>
  <div>
    <el-row
      v-for="(kpi, index) in kpidata"
      class="row mB10 mL0"
      :key="index + 'row'"
      :gutter="10"
    >
      <template v-if="type === 'moduleKPI'">
        <el-col :span="24" class="pL0 pR5">
          <div class="d-flex justify-content-space">
            <p class="fc-input-label-txt">
              {{ $t('common.products.choose_kpi') }}
            </p>
            <div class="text-fc-green pointer pT3" @click="addKPI">
              <inline-svg
                src="add-icon"
                class="vertical-middle"
                iconClass="icon icon-md mR5"
              ></inline-svg
              >{{ $t('common.header.add_new') }}
            </div>
          </div>
          <el-form-item>
            <el-col :span="24" class="p0">
              <el-form-item prop="reading" class="mB10">
                <FieldLoader
                  v-if="isLoading"
                  :isLoading="isLoading"
                ></FieldLoader>
                <template v-else>
                  <el-select
                    v-model="kpi.kpiId"
                    filterable
                    :placeholder="$t('common.header.please_select_kpi')"
                    class="width100 el-input-textbox-full-border"
                    @change="emitReading"
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
                </template>
              </el-form-item>
            </el-col>
          </el-form-item>
        </el-col>
      </template>
      <template v-else>
        <el-col :span="24" class="pL0 pR5">
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
                    @change="emptyKpiAndAsset"
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
            <div class="d-flex justify-content-space">
              <p class="fc-input-label-txt pT7">
                {{ $t('common.products.kpi_type') }}
              </p>
            </div>
            <el-radio-group v-model="kpi.type" class="mT15" @change="emptykpi">
              <el-radio label="ALL" class="fc-radio-btn">{{
                $t('common._common._all')
              }}</el-radio>
              <el-radio label="COMPUTED" class="fc-radio-btn">{{
                $t('kpi.kpi.computed_kpi')
              }}</el-radio>
              <el-radio label="DYNAMIC" class="fc-radio-btn">{{
                $t('kpi.kpi.real_time_kpi')
              }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <div class="d-flex justify-content-space">
              <p class="fc-input-label-txt pT7">
                {{ $t('common.products.choose_kpi') }}
              </p>
            </div>
            <el-row :gutter="10">
              <el-col :span="kpi.type != 'DYNAMIC' ? 10 : 12">
                <el-form-item prop="reading" class="mB10">
                  <!-- <p class="fc-input-label-txt pB5">KPI</p> -->
                  <div v-if="kpiloading">
                    <FieldLoader :isLoading="true"> </FieldLoader>
                  </div>
                  <div v-else>
                    <el-select
                      v-model="kpi.kpiId"
                      filterable
                      clearable
                      remote
                      @change="kpiId => assetSpaceList(kpiId, 0)"
                      @clear="getKpiList"
                      :placeholder="$t('common.header.please_select_kpi')"
                      class="width100 el-input-textbox-full-border"
                      :remote-method="value => searchkpilist(null, value)"
                    >
                      <template
                        v-for="(category, index) in getKpiListType(
                          kpi.categoryId
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
              <el-col :span="kpi.type != 'DYNAMIC' ? 10 : 12">
                <el-form-item prop="reading" class="mB10">
                  <!-- <p class="fc-input-label-txt pB5">resources</p> -->
                  <div v-if="assetloading">
                    <FieldLoader :isLoading="true"> </FieldLoader>
                  </div>
                  <div v-else>
                    <el-select
                      v-model="kpi.parentId"
                      :loading="loading"
                      :placeholder="$t('common.header.please_select_asset')"
                      class="width100 el-input-textbox-full-border"
                      @change="emitReading"
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
              <el-col :span="4" v-if="kpi.type != 'DYNAMIC'">
                <el-form-item prop="reading" class="mB10">
                  <!-- <p class="fc-input-label-txt pB5">agg</p> -->
                  <el-select
                    v-model="kpi.yAggr"
                    :placeholder="$t('common._common.aggr')"
                    class="width100 el-input-textbox-full-border"
                    @change="emitReading"
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
        </el-col>
      </template>
    </el-row>

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
import { v4 as uuid } from 'uuid'
import { aggregateFunctions } from 'pages/card-builder/card-constants'
import AddModuleKpi from 'pages/energy/kpi/components/AddModuleKpi'
import { isEmpty } from '@facilio/utils/validation'
import { getFieldOptions } from 'util/picklist'
import FieldLoader from '@/forms/FieldLoader.vue'
import isString from 'lodash/isString'
import { API } from '@facilio/api'
import debounce from 'lodash/debounce'
import cloneDeep from 'lodash/cloneDeep'

export default {
  components: { AddModuleKpi, FieldLoader },
  props: ['type', 'initialReading', 'moduleId', 'clearData'],
  data() {
    return {
      canShow: false,
      moduleKPI: [],
      emptydata: {
        parentId: null,
        parentType: 'Asset',
        type: 'ALL',
        categoryId: null,
        kpiId: null,
        yAggr: 'sum',
      },
      kpidata: [
        {
          parentId: null,
          parentType: 'Asset',
          type: 'ALL',
          categoryId: null,
          kpiId: null,
          yAggr: 'sum',
          isNewKpi: false,
        },
      ],
      kpiCategoryList: null,
      kpiList: [],
      kpiListtype: [],
      aggregateFunctions,
      isNewKpi: true,
      kpiObj: {},
      isLoading: false,
      assetSpaceObject: {},
      loading: true,
      categoryLoading: true,
      kpiloading: true,
      assetloading: true,
    }
  },
  created() {
    if (!isEmpty(this.initialReading))
      this.deserializeReading(this.initialReading)
  },
  watch: {
    moduleId: function() {
      this.getModulekpiList()
    },
    // clearData(value){
    //   if(value){
    //     this.kpidata = [this.emptydata]
    //     this.getModulekpiList()
    //   }
    //}
  },
  mounted() {
    if (this.clearData) {
      this.kpidata = [this.emptydata]
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
      this.searchkpilist = debounce(this.getKpiList, 500)
    }
  },
  methods: {
    getKpiListType(categoryId) {
      let kpiType = this.kpidata[0].type
      return this.kpiList.filter(kpi => {
        if (Number(kpi.kpiCategory) === Number(categoryId)) {
          if (kpiType === 'ALL') {
            return true
          } else if (kpiType === 'COMPUTED') {
            return kpi.kpiTypeEnum === 'LIVE' || kpi.kpiTypeEnum === 'SCHEDULED'
              ? true
              : false
          } else if (kpiType === 'DYNAMIC') {
            return kpiType === kpi.kpiTypeEnum
          }
        }
      })
    },
    emptykpi() {
      this.kpidata[0].kpiId = null
      this.kpidata[0].parentId = null
    },
    emptyKpiAndAsset() {
      this.emptykpi()
      this.kpidata[0].type = 'ALL'
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
        let { type } = this.kpidata[0] || {}
        this.kpidata[0].type =
          type === 'ALL' && kpi.kpiTypeEnum === 'DYNAMIC' ? 'DYNAMIC' : type
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
      this.assetSpaceObject = obj
      this.assetloading = false
    },
    getModulekpiList() {
      this.isLoading = true
      let url = `/v2/kpi/module/list`

      if (this.moduleId) {
        let filters = {
          moduleId: {
            operatorId: 9, // ==
            value: [this.moduleId + ''],
          },
        }
        url = `${url}?filters=${encodeURIComponent(JSON.stringify(filters))}`
      }
      API.get(url)
        .then(({ data }) => {
          this.moduleKPI = data.kpis || []
        })
        .finally(() => (this.isLoading = false))
    },
    emitReading() {
      let { kpidata } = this
      if (this.type === 'moduleKPI') {
        let filterData = this.serializeReading(kpidata)
        delete filterData.parentId
        delete filterData.parentType
        delete filterData.categoryId
        delete filterData.yAggr
        this.$emit('onReadingSelect', filterData)
        this.moduleKpiSelected(filterData)
      } else {
        let kpiId = kpidata[0]?.kpiId ?? null
        if (isString(kpiId) && kpiId.includes('new')) {
          kpidata[0].isNewKpi = true
        }
        this.$emit('onReadingSelect', this.serializeReading(kpidata))
      }
    },
    moduleKpiSelected(filterData) {
      let selectedObject = this.moduleKPI.find(rt => rt.id === filterData.kpiId)
      this.$emit('onModuleKpiSelect', selectedObject)
    },
    serializeReading(data) {
      return data[0]
    },
    deserializeReading(initialReading) {
      if (initialReading?.isNewKpi && !isString(initialReading?.kpiId)) {
        initialReading.kpiId = initialReading.kpiId + '_new'
      }
      initialReading.type = isEmpty(initialReading.type)
        ? 'ALL'
        : initialReading.type
      this.$set(this.kpidata, 0, { ...initialReading, id: uuid() })
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
        oldKpiList = formulaList.map(oldKpi => {
          oldKpi.kpiTypeEnum = 'SCHEDULED'
          return oldKpi
        })
      }
      this.kpiList = [...newKpiList, ...oldKpiList]
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
        this.kpiList = [...this.kpiList, ...kpis]
      }
      this.kpiloading = false
      this.loading = false
      this.kpidata.forEach(element => this.assetOrSpaceList(element.kpiId))
    },
    onKpiCreated(kpiObj) {
      if (this.type === 'moduleKPI') {
        this.getModulekpiList()
        this.kpidata[0].kpiId = kpiObj.id
        this.emitReading()
      }
    },
    addKPI() {
      this.kpiObj = {}
      this.isNewKpi = true
      this.canShow = true
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
