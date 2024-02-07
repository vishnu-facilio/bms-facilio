<template>
  <div v-if="isLoading" class="flex-middle fc-empty-white">
    <spinner :show="isLoading" size="80"></spinner>
  </div>
  <div v-else class="details_fc" ref="safetyPlan-desc">
    <div class="header">
      <span class="f14 bold">{{ $t('common.safetyPlan.safety_plan') }}</span>
      <div v-if="workOrderSafetyPlan" @click="deleteRecord()">
        <inline-svg
          src="svgs/delete"
          class="pointer mL10"
          iconClass="icon icon-sm"
        ></inline-svg>
      </div>
    </div>
    <div v-if="workOrderSafetyPlan">
      <div class="details_body">
        <div class="content width30 mL20">
          <span class="title">{{ $t('common.safetyPlan.name') }}</span>
          <span class="d-flex name">
            {{ getName(workOrderSafetyPlan) }}
          </span>
        </div>
        <div class="content width100">
          <span class="title">{{ $t('common.products.site') }}</span>
          <span class="d-flex name">
            {{ getSiteName }}
          </span>
        </div>
      </div>
      <div class="content width100 pL40">
        <span class="title">{{ $t('common.failure_class.description') }}</span>
        <span class="d-flex name">
          {{ getDescription(workOrderSafetyPlan) }}
        </span>
      </div>
    </div>

    <div v-else class="report-noData">
      <div class="report-body flx">
        <inline-svg
          src="svgs/spacemanagement/ic-nodata"
          iconClass="text_center icon icon-xxxlg"
          class="mL35"
        ></inline-svg>
        <div class="mL60">
          <div class="mT5 label-txt-black f14 bold">
            {{ $t('common.safetyPlan.not_added_safetyPlan') }}
          </div>
          <div>
            <button class="btn mT15" @click="showDialog">
              <span class="add">
                {{ $t('common.safetyPlan.add_safetyPlan') }}
              </span>
            </button>
          </div>
        </div>
      </div>
    </div>
    <el-dialog
      :title="$t('common.safetyPlan.add_sf')"
      :visible.sync="showMsgPopup"
      width="55%"
      :append-to-body="true"
      style="z-index: 9999999999"
      custom-class="dialog-body"
      class="sf-dialog"
    >
      <div
        class="label-txt-black line-height24 height400 overflow-y-scroll pB50"
      >
        <div class="sf-dialog-header">
          <div class="left-header">
            <span class="filter-txt">Filter By</span>
            <button
              v-bind:class="{
                selected: selectedButton === 'all',
              }"
              class="safetyPlanBtn"
              @click="fetchAllSafetyPlan()"
            >
              {{ $t('common.safetyPlan.all') }}
            </button>
            <button
              v-bind:class="{
                selected: selectedButton === 'all-workasset',
              }"
              class="safetyPlanBtn"
              @click="fetchAllWorkAssetSafetyPlan()"
            >
              {{ $t('common.safetyPlan.workasset') }}
            </button>
          </div>
          <div class="search">
            <template>
              <el-tooltip
                v-if="showSearch"
                effect="dark"
                :content="$t('common._common.search')"
                placement="left"
              >
                <SearchComponent
                  :key="moduleName + '-search'"
                  class="self-center mL-auto lookup-wizard-search"
                  :moduleName="'safetyPlan'"
                  :singleFieldSearch="true"
                  :searchMetaInfo="moduleMetaInfo"
                  :clearFilters="clearAllFilters"
                  @onChange="applyFilter"
                  @clearedFilters="clearAllFilters = false"
                >
                </SearchComponent>
              </el-tooltip>
              <el-tooltip
                effect="dark"
                content="Clear Filters"
                placement="right"
                v-if="!$validation.isEmpty(wizardFilterValue)"
              >
                <span @click="clearFilter()" class="self-center pointer"
                  ><img
                    src="~assets/filter-remove.svg"
                    width="20px"
                    height="20px"
                    style="position: relative;top: 4px;left: 10px;"
                /></span>
              </el-tooltip>
            </template>
          </div>
        </div>
        <div v-if="isFetchingSafetyPlan" class="flex-middle fc-empty-white">
          <spinner :show="isFetchingSafetyPlan" size="80"></spinner>
        </div>
        <div v-else>
          <el-table
            :data="safetyPlans"
            style="width: 100%"
            :fit="true"
            :height="300"
            class="related-list-widget-table"
          >
            <el-table-column fixed width="50" class="sf-el-cloumn">
              <template slot-scope="props">
                <el-radio
                  class="fc-radio-btn"
                  v-model="selectedSafetyPlan"
                  :label="props.row.id"
                  size="75"
                  >{{ '' }}</el-radio
                >
              </template>
            </el-table-column>
            <el-table-column
              label="Name"
              fixed
              min-width="120"
              class="sf-el-cloumn"
            >
              <template slot-scope="props">
                <div class="rowValue">
                  <span class="f12">{{ getName(props.row) }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              label="Description"
              min-width="210"
              class="sf-el-cloumn"
            >
              <template slot-scope="props">
                <div class="rowValue">
                  <span class="f12">{{ getDescription(props.row) }}</span>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel f13" @click="closeDialog()">{{
          $t('agent.agent.cancel')
        }}</el-button>
        <el-button class="modal-btn-save f13" @click="saveRecord()">{{
          $t('common.failure_class.save')
        }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { eventBus } from '@/page/widget/utils/eventBus'
import SearchComponent from 'newapp/components/WizardSearch'
import { getUnRelatedModuleSummary } from 'src/util/relatedFieldUtil'

const EQUALS_OPERATOR = 9

export default {
  props: ['moduleName', 'details', 'resizeWidget'],
  components: {
    SearchComponent,
  },
  data() {
    return {
      isLoading: false,
      isFetchingSafetyPlan: false,
      showMsgPopup: false,
      safetyPlans: [],
      selectedButton: 'all',
      selectedSafetyPlan: null,
      workOrderSafetyPlan: null,
      wizardFilterValue: null,
      clearAllFilters: false,
      moduleMetaInfo: null,
      showSearch: false,
      site: null,
    }
  },
  async created() {
    await this.fetchSiteDetails()
    this.fetchModuleMetaInfo()
  },
  mounted() {
    this.setWorkOrderSafetyPlan()
    this.autoResize()
  },
  computed: {
    getSiteName() {
      let { site = {} } = this
      return site ? site.name : '---'
    },
  },
  methods: {
    init() {
      this.workOrderSafetyPlan = null
      this.selectedSafetyPlan = null
      this.wizardFilterValue = null
      this.$set(this, 'clearAllFilters', true)
    },
    refresh(isSearchFilter) {
      if (this.selectedButton === 'all') this.fetchAllSafetyPlan()
      else if (this.selectedButton === 'all-workasset')
        this.fetchAllWorkAssetSafetyPlan(isSearchFilter)
    },
    applyFilter(filter) {
      if (!isEmpty(filter)) {
        this.wizardFilterValue = filter
        this.refresh(true)
      }
    },
    clearFilter() {
      this.wizardFilterValue = null
      this.$set(this, 'clearAllFilters', true)
      this.refresh()
    },
    fetchModuleMetaInfo() {
      this.$http
        .get('/module/metafields?moduleName=' + 'safetyPlan')
        .then(response => {
          if (!isEmpty(response.data.meta))
            this.moduleMetaInfo = response.data.meta
          this.showSearch = true
        })
        .catch(() => {})
    },
    autoResize() {
      this.$nextTick(() => {
        setTimeout(() => {
          let container = this.$refs['safetyPlan-desc']
          let { workOrderSafetyPlan } = this
          if (container && workOrderSafetyPlan) {
            let height = container.scrollHeight - 120
            let width = container.scrollWidth
            if (this.resizeWidget) {
              this.resizeWidget({ height: height, width })
            }
          }
        })
      })
    },
    async setWorkOrderSafetyPlan() {
      let { details } = this
      let { workorder } = details
      this.workOrderSafetyPlan = await this.$getProperty(
        workorder,
        'safetyPlan',
        null
      )
    },
    async fetchSiteDetails() {
      let { workorder } = this.details || {}
      let { siteId } = workorder || {}
      let { site } = await getUnRelatedModuleSummary(
        'workorder',
        'site',
        siteId
      )
      this.site = site || null
    },
    showDialog() {
      this.init()
      this.showMsgPopup = true
      this.fetchAllSafetyPlan()
    },
    closeDialog() {
      this.showMsgPopup = false
    },
    getName(item) {
      const LENGTH = 20
      let name = this.$getProperty(item, 'name', '---')

      if (name.length > LENGTH) {
        return name.slice(0, LENGTH) + '...'
      } else {
        return name
      }
    },
    getDescription(item) {
      let { showMsgPopup } = this
      const LENGTH = showMsgPopup ? 60 : 80
      let description = this.$getProperty(item, 'description', '---')

      if (description.length > LENGTH) {
        return description.slice(0, LENGTH) + '...'
      } else {
        return description
      }
    },
    async deleteRecord() {
      let moduleDisplayName = 'Safety Plan'
      let value = await this.$dialog.confirm({
        title: `${this.$t('custommodules.list.delete')} ${moduleDisplayName}`,
        message: `${this.$t(
          'custommodules.list.delete_confirmation'
        )} ${moduleDisplayName}?`,
        rbDanger: true,
        rbLabel: this.$t('custommodules.list.delete'),
      })
      if (value) {
        this.saveRecord()
        this.init()
      }
    },
    async saveRecord() {
      this.isLoading = true
      let { details, moduleName, selectedSafetyPlan } = this
      let { id } = details
      let response, message
      if (selectedSafetyPlan) {
        message = this.$t('common.safetyPlan.safety_plan_added')
        response = await API.updateRecord(moduleName, {
          id,
          data: {
            safetyPlan: {
              id: selectedSafetyPlan,
            },
          },
          params: {
            addSafetyPlanHazardPrecaution: true,
          },
        })
      } else {
        message = this.$t('common.safetyPlan.safety_plan_deleted')
        response = await API.updateRecord(moduleName, {
          id,
          data: {
            safetyPlan: null,
          },
          params: {
            addSafetyPlanHazardPrecaution: false,
          },
        })
      }
      let { error } = response
      if (!error) {
        this.$message.success(message)
        this.init()
        eventBus.$emit('refesh-parent')
      } else {
        this.$message.error(error.message || 'Error Occured')
      }
      this.isLoading = false
      this.closeDialog()
    },
    async fetchAllSafetyPlan(isSearchFilter) {
      let { wizardFilterValue, details = {} } = this
      if (!isSearchFilter) this.selectedButton = 'all'
      this.isFetchingSafetyPlan = true
      let params = {
        withCount: true,
      }
      let filter = {
        siteId: {
          operatorId: 9,
          value: [`${details.workorder?.siteId}`],
        },
      }
      if (!isEmpty(wizardFilterValue)) {
        wizardFilterValue = { ...wizardFilterValue, ...filter }
        params['filters'] = JSON.stringify(wizardFilterValue)
      } else params['filters'] = JSON.stringify(filter)

      let { list, error } = await API.fetchAll('safetyPlan', params, {
        force: true,
      })
      if (!error) {
        if (isSearchFilter) return list
        this.safetyPlans = list
      }
      this.isFetchingSafetyPlan = false
    },

    async fetchAllWorkAssetSafetyPlan(isSearchFilter) {
      this.selectedButton = 'all-workasset'
      this.isFetchingSafetyPlan = true
      let { details } = this
      let { workorder = {} } = details
      let { resource = {} } = workorder
      let { resourceTypeEnum, id } = resource
      let searchFilter = {}
      if (isEmpty(resource) || id < 0) {
        this.safetyPlans = []
      } else {
        if (isSearchFilter) {
          let safetyPlans = await this.fetchAllSafetyPlan(isSearchFilter)
          let safetyPlanIds = safetyPlans.map(plan => {
            return plan.id + ''
          })
          searchFilter = {
            safetyPlan: {
              operatorId: EQUALS_OPERATOR,
              value: safetyPlanIds,
            },
          }
        }
        let modulePlaceholder
        if (resourceTypeEnum === 'SPACE' && id > 0) {
          modulePlaceholder = 'space'
        } else if (resourceTypeEnum === 'ASSET' && id > 0) {
          modulePlaceholder = 'asset'
        }
        let filter = {
          ...searchFilter,
        }
        filter[`${modulePlaceholder}`] = {
          operatorId: EQUALS_OPERATOR,
          value: [`${id}`],
        }
        let params = {
          withCount: true,
          filters: JSON.stringify(filter),
        }

        let { list, error } = await API.fetchAll('workAsset', params, {
          force: true,
        })
        if (!error) {
          let safetyPlans = list
            .filter(resource => !resource.safetyPlan?.deleted)
            .map(resource => resource.safetyPlan)
          this.safetyPlans = safetyPlans
        }
      }
      this.isFetchingSafetyPlan = false
    },
  },
}
</script>

<style scoped lang="scss">
.details_fc {
  background-color: #fff;
}
.flx {
  display: flex;
  flex-direction: column;
  text-align: center;
}
.header {
  display: flex;
  justify-content: space-between;
  text-align: center;
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

.safetyPlanBtn {
  padding: 5px 14px;
  background: #f3f5ff;
  border: 0px;
  border-radius: 100px;
  margin-left: 10px;
  color: #000;
  cursor: pointer;
  font-size: 12px;
  font-weight: 450;
}

.safetyPlanBtn:hover {
  background: #e6eaff;
}

.sf-dialog-header {
  padding: 10px 17px 13px 17px;
  border-top: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
}

.sf-dialog-header .selected {
  background: #516ae8;
  color: #fff;
}

.rowValue {
  margin-left: 20px;
}
.fc-radio-btn {
  margin-left: 20px;
}

/* Customize the label (the container) */
.container {
  display: block;
  position: relative;
  cursor: pointer;
  user-select: none;
}

.filter-txt {
  font-size: 12px;
  font-weight: 500;
  padding-right: 5px;
}

.report-noData {
  height: 200px;
  display: flex;
  justify-content: center;
  margin: auto;
}

.add {
  font-size: 13px;
  font-weight: 500;
}

.btn {
  margin: 12px 44px 0 45px;
  border-radius: 4px;
  background-color: #3ab2c2;
}
.content {
  font-size: 12px;
  letter-spacing: 0.16px;
  color: #324056;
  margin-right: 5%;
}

.details_body {
  margin: 10px 0;
  display: flex;
  padding: 14px 22px 16px;
}

.title {
  font-size: 12px;
  font-weight: 500;
  color: #000;
}
.report-header {
  padding: 0px 24px 32px;
}

.name {
  opacity: 0.5;
  font-size: 13px;
  font-weight: 400;
  margin: 11px 0 0;
  letter-spacing: 0.57px;
  color: #000;
}

.no-data {
  width: 30%;
  letter-spacing: 0.5px;
  color: rgba(50, 64, 86, 0.5);
}
.left-header {
  margin: auto 0px;
}
.search {
  display: flex;
  margin: auto 0;
}
.dialog-padding {
  padding: 0px 20px;
}
</style>

<style lang="scss">
.dialog-body {
  .el-dialog__body {
    padding: 0;
  }
}

.el-table {
  .el-table__cell {
    padding: 8px 0;
    min-width: 0;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    text-overflow: ellipsis;
    vertical-align: middle;
    position: relative;
    text-align: left;
  }
}
.search {
  .lookup-wizard-search {
    .fc-subheader-right-search {
      bottom: 0px;
    }
    .el-input__suffix {
      display: none;
    }
    .el-input__icon.el-icon-search.filter-space-search-icon3 {
      right: 5px;
      cursor: pointer;
    }
  }
}

.search {
  .search-input-comp {
    .el-input__inner {
      width: fit-content !important;
      border-radius: 0 !important;
      border: 1px solid #d0d9e2 !important;
      padding-left: 30px !important;
      border-left: none !important;
      height: 40px;
      line-height: 40px;
      padding-left: 15px;
      padding-right: 15px;
      border-radius: 3px;
      font-size: 14px;
      font-weight: normal;
      letter-spacing: 0.4px;
      color: #333333;
      text-overflow: ellipsis;
      font-weight: 400;
      padding-right: 30px;
      white-space: nowrap;
    }
  }
}
</style>
