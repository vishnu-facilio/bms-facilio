<template>
  <el-dialog
    :visible.sync="canShowWizard"
    width="70%"
    top="5vh"
    class="fc-dialog-center-container f-lookup-wizard scale-up-center"
    :append-to-body="true"
    :show-close="false"
    :before-close="resetDefaultValues"
  >
    <template slot="title">
      <div class="header d-flex">
        <div class="el-dialog__title self-center">{{ title }}</div>
        <pagination
          v-if="!hidePaginationSearch"
          :currentPage.sync="page"
          :total="totalCount"
          :perPage="perPage"
          class="self-center mL-auto"
        ></pagination>
        <span
          v-if="!$validation.isEmpty(totalCount) && !hidePaginationSearch"
          class="separator pL0 self-center"
          >|</span
        >
        <div
          class="close-btn self-center cursor-pointer"
          v-bind:class="hidePaginationSearch ? 'mL-auto' : ''"
          @click="canShowWizard = false"
        >
          <i class="el-dialog__close el-icon el-icon-close"></i>
        </div>
      </div>
    </template>
    <div>
      <div
        v-if="isLoading"
        class="loading-container d-flex justify-content-center height550"
      >
        <Spinner :show="isLoading"></Spinner>
      </div>
      <div v-else class="height550">
        <div
          class="fc-list-view fc-list-table-container fc-table-td-height fc-table-viewchooser"
        >
          <div>
            <el-table
              :data="modulesList || []"
              style="width: 100%;"
              @selection-change="selectedItems"
              :fit="true"
              height="250"
            >
              <template slot="empty">
                <img
                  class="mT20"
                  src="~statics/noData-light.png"
                  width="100"
                  height="100"
                />
                <div class="mT10 label-txt-black f14">
                  No
                  {{ moduleDisplayName ? moduleDisplayName : moduleName }}
                  available.
                </div>
              </template>
              <el-table-column
                type="selection"
                width="60"
                fixed
              ></el-table-column>
              <el-table-column
                v-if="!$validation.isEmpty(mainFieldColumn)"
                :label="mainFieldColumn.displayName"
                :prop="mainFieldColumn.name"
                fixed
                min-width="200"
              >
                <template v-slot="item">
                  <div class="table-subheading">
                    <div class="d-flex">
                      <div class="self-center name bold">
                        {{
                          getColumnDisplayValue(mainFieldColumn, item.row) ||
                            '---'
                        }}
                      </div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                v-for="(field, index) in filteredViewColumns"
                :key="index"
                :prop="field.name"
                :label="field.displayName"
                :align="
                  (field.field || {}).dataTypeEnum === 'DECIMAL'
                    ? 'right'
                    : 'left'
                "
                min-width="200"
              >
                <template v-slot="scope">
                  <keep-alive v-if="isSpecialHandlingField(field)">
                    <component
                      :is="(listComponentsMap[moduleName] || {}).componentName"
                      :field="field"
                      :moduleData="scope.row"
                    ></component>
                  </keep-alive>
                  <div
                    v-else
                    class="table-subheading"
                    :class="{
                      'text-right':
                        (field.field || {}).dataTypeEnum === 'DECIMAL',
                    }"
                  >
                    {{ getColumnDisplayValue(field, scope.row) || '---' }}
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </div>
    <div v-if="!isLoading" class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="canShowWizard = false"
        >CANCEL</el-button
      >
      <el-button
        :loading="loading"
        type="primary"
        class="modal-btn-save"
        @click="updateItem()"
        >Update</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import RelatedListWidget from '@/page/widget/common/RelatedListWidget'
import Constants from 'util/constant'
import { isEmpty, isBoolean } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import { eventBus } from '@/page/widget/utils/eventBus'
import { getBaseURL } from 'util/baseUrl'
import { API } from '@facilio/api'

const fetchListUrlAndResultMap = {
  assetHazard: {
    url: `v2/assetHazard/list`,
    result: 'assetHazards',
  },
}
export default {
  name: 'SpacePlanParentUpdateViewer',
  components: { Spinner },
  extends: RelatedListWidget,
  props: ['canShowWizardList', 'childModule', 'recordsList', 'details'],
  computed: {
    canShowWizard: {
      get() {
        return this.canShowWizardList
      },
      set(value) {
        this.$emit('update:canShowWizardList', value)
      },
    },
    addResourceFilter() {
      let { passRecordFilter } = this
      return isBoolean(passRecordFilter) && passRecordFilter === true
    },
    moduleName() {
      let { childModule, addResourceFilter } = this
      if (
        ['workorderHazard', 'safetyPlanHazard', 'assetHazard'].includes(
          childModule
        )
      ) {
        if (childModule === 'workorderHazard' && addResourceFilter) {
          return 'assetHazard'
        }
        return 'hazard'
      }
      if (['hazardPrecaution'].includes(childModule)) {
        return 'precaution'
      }
      return childModule
    },
    moduleDisplayName() {
      let { moduleName } = this
      if (moduleName === 'hazard') {
        return 'Hazards'
      }
      if (moduleName === 'precaution') {
        return 'Precaution'
      }
      if (moduleName === 'assetHazard') {
        return 'Suggested Hazards'
      }
      if (moduleName === 'site') {
        return 'Site'
      }
      return ''
    },
    parentId() {
      let { $route } = this
      let { params, name } = $route || {}
      let { id, assetid } = params || {}
      return (name === 'assetsummary' ? assetid : id) || -1
    },
    title() {
      let { moduleDisplayName, moduleName } = this
      if (moduleName === 'assetHazard') {
        return moduleDisplayName
      }
      return `${this.$t('common.text.choose')} ${moduleDisplayName}`
    },
    routeName() {
      let { $route } = this
      let { name } = $route || {}
      return name || ''
    },
    selectedLabel() {
      let { moduleDisplayName } = this
      return `Selected ${moduleDisplayName} :`
    },
    selectedItem() {
      let { selectedItemId, modulesList } = this
      let selectedItem = null
      if (!isEmpty(selectedItemId)) {
        selectedItem = modulesList.find(module => module.id === selectedItemId)
      }
      return selectedItem
    },
    filters() {
      let {
        mainField,
        searchText,
        recordsList,
        moduleName,
        addResourceFilter,
        details,
      } = this
      let filterObj = {}
      // search filter
      if (
        !isEmpty(mainField) &&
        !isEmpty(searchText) &&
        searchText.length > 0
      ) {
        let { name, field } = mainField
        let { dataTypeEnum } = field
        let value = [searchText]
        let operatorId = Constants.FILTER_OPERATORID_HASH[dataTypeEnum]
        filterObj[name] = {
          operatorId,
          value,
        }
      }

      if (moduleName === 'hazard' && !isEmpty(recordsList)) {
        filterObj.id = {
          operatorId: 37,
          value: recordsList.map(record => record.hazard.id + ''),
        }
        return filterObj
      }
      if (moduleName === 'precaution' && !isEmpty(recordsList)) {
        filterObj.id = {
          operatorId: 37,
          value: (recordsList || []).map(record => record.precaution.id + ''),
        }
        return filterObj
      }
      if (moduleName === 'site' && !isEmpty(recordsList)) {
        filterObj.client = {
          operatorId: 1,
        }
        return filterObj
      }
      if (addResourceFilter && !isEmpty(details)) {
        let resourceType = this.$getProperty(
          this,
          'details.resource.resourceType',
          null
        )
        let resourceId = this.$getProperty(this, 'details.resource.id', null)
        if (!isEmpty(resourceType) && !isEmpty(resourceId)) {
          if (resourceType === 2) {
            this.$set(filterObj, 'asset', {
              operatorId: 36,
              value: [String(resourceId)],
            })
            if (!isEmpty(recordsList)) {
              this.$set(filterObj, 'hazard', {
                operatorId: 37,
                value: (recordsList || []).map(record => record.hazard.id + ''),
              })
            }
            return filterObj
          }
        }
        return { id: { operatorId: 1 } }
      }
      return filterObj
    },
  },
  data() {
    return {
      selectedItemId: null,
      selectedRecords: [],
      defaultHideColumns: ['photo', 'moduleState', 'stateFlowId'],
      listComponentsMap: {},
      loading: false,
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      this.viewDetailsExcludedModules = []
      this.fetchListUrlAndResultMap = this.$helpers.cloneObject(
        fetchListUrlAndResultMap
      )
      this.debounceMainFieldSearch = this.$helpers.debounce(() => {
        this.fetchModulesData()
        this.loadDataCount()
      }, 2000)
      this.loadData()
      this.loadDataCount()
    },
    selectedItems(selectedItem) {
      this.selectedRecords = selectedItem
    },
    async updateItem() {
      let { routeName, parentId, addResourceFilter, moduleName } = this
      if ((this.selectedRecords || []).length) {
        let url
        let param
        if (routeName === 'safetyPlanSummary') {
          let records = []
          this.selectedRecords.filter(record => {
            records.push({
              safetyPlan: { id: parentId },
              hazard: { id: record.id },
            })
          })
          url = `/v2/safetyPlanHazard/add`
          param = { safetyPlanHazardList: records }
        } else if (routeName === 'hazardSummary') {
          let records = []
          this.selectedRecords.filter(record => {
            records.push({
              hazard: { id: parentId },
              precaution: { id: record.id },
            })
          })
          let { hazard, error } = await API.createRecord('hazardPrecaution', {
            data: records,
          })
          if (!error) {
            this.$emit('saved')
            eventBus.$emit('refesh-parent')
            this.canShowWizard = false
            this.loading = false
          }
        } else if (
          ['vendorWorkorderSummary', 'wosummarynew'].includes(routeName)
        ) {
          let records = []
          if (addResourceFilter && moduleName === 'assetHazard') {
            this.selectedRecords.filter(record => {
              if (!isEmpty((record.hazard || {}).id)) {
                records.push({
                  workorder: { id: parentId },
                  hazard: { id: (record.hazard || {}).id },
                })
              }
            })
          } else {
            this.selectedRecords.filter(record => {
              records.push({
                workorder: { id: parentId },
                hazard: { id: record.id },
              })
            })
          }
          url = `/v2/workorderHazard/add`
          param = { workorderHazardList: records }
        } else if (routeName === 'assetsummary') {
          let records = []
          this.selectedRecords.filter(record => {
            records.push({
              asset: { id: parentId },
              hazard: { id: record.id },
            })
          })
          url = `/v2/assetHazard/add`
          param = { assetHazardList: records }
        } else if (routeName === 'clientSummary') {
          let records = []
          this.selectedRecords.filter(record => {
            records.push(record.id)
          })
          url = `/v2/client/associateClient`
          param = { clientId: parentId, siteIds: records }
        }
        if (url) {
          this.loading = true
          this.$http
            .post(url, param)
            .then(({ data: { message, responseCode } }) => {
              if (responseCode === 0) {
                this.$emit('saved')
                eventBus.$emit('refesh-parent')
                this.canShowWizard = false
                this.loading = false
              } else {
                throw new Error(message)
              }
            })
            .catch(({ message }) => {
              this.loading = false
              this.$message.error(message)
            })
        }
      }
    },
    setSelectedItem() {
      let { mainField } = this
      if (isEmpty(mainField)) {
        mainField = {
          name: 'name',
        }
      }
      this.canShowWizard = false
    },
    resetDefaultValues() {
      this.$set(this, 'showMainFieldSearch', false)
    },
    getImage(photoId) {
      return `${getBaseURL()}/v2/files/preview/${photoId}`
    },
  },
}
</script>

<style lang="scss">
.fc-dialog-center-container {
  &.f-lookup-wizard {
    overflow: hidden;
    .header {
      min-height: 35px;
    }
    .el-dialog__header {
      padding: 15px 20px;
    }
    .el-dialog__body {
      padding: 0;
      .fc-table-td-height,
      .fc-list-table-container {
        .el-table {
          tr {
            td:first-child + td,
            th:first-child + th {
              padding-left: 0px !important;
            }
          }
        }
      }
    }
    .fc-input-full-border2 {
      .el-input__inner {
        height: 35px !important;
      }
    }
    .separator {
      font-size: 18px;
    }
    .cell {
      .name {
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
      }
    }
    .search-icon {
      fill: #91969d;
    }
  }
  .fc-list-table-container {
    .el-table {
      height: 500px !important;
    }
  }
  .task-icon {
    fill: #dddddd;
  }
}
</style>
