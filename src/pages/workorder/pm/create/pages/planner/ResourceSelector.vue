<template>
  <el-dialog
    :visible.sync="canShowWizard"
    width="70%"
    top="5vh"
    class="fc-dialog-center-container f-lookup-wizard scale-up-center fc-wizard-table-width100"
    :append-to-body="true"
    :show-close="false"
    :before-close="closeWizard"
    :close-on-click-modal="false"
  >
    <template slot="title">
      <div class="flex items-center">
        <div class="lookup-wizard-title">
          {{ title }}
        </div>
        <div class="ml-auto flex items-center">
          <div class="resource-filter-icon">
            <AdvancedSearch
              :key="`${moduleName}-search`"
              :moduleName="moduleName"
              :moduleDisplayName="moduleDisplayName"
              :hideQuery="true"
              :onSave="applyFilters"
              :filterList="filters"
            >
              <template #icon>
                <el-badge
                  is-dot
                  class="item"
                  :hidden="$validation.isEmpty(filters)"
                >
                  <div class="resource-icons">
                    <InlineSvg
                      :src="`svgs/filter2`"
                      iconClass="icon icon-sm"
                      class="fc-grey-svg2"
                      style="top: 2px;"
                    ></InlineSvg>
                  </div>
                </el-badge>
              </template>
            </AdvancedSearch>
          </div>
          <span class="separator">|</span>
          <pagination
            :currentPage.sync="page"
            :total="totalCount"
            :perPage="perPage"
            class="self-center"
          ></pagination>
          <span v-if="totalCount" class="separator">|</span>
          <div
            @click="closeWizard"
            class="el-dialog__close el-icon el-icon-close close-icon cursor-pointer resource-icons"
          ></div>
        </div>
      </div>
    </template>
    <div v-if="showFTags" class="wizard-f-tag-container">
      <FTags
        :moduleName="moduleName"
        :filters="filters"
        :hideQuery="true"
        @updateFilters="updateFilters"
        @clearFilters="applyFilters({ filters: {} })"
        :hideSaveAs="true"
      />
    </div>
    <div v-if="isLoading" class="f-ui-lookup-empty">
      <Spinner :show="isLoading" size="80"></Spinner>
    </div>
    <div v-else-if="$validation.isEmpty(moduleList)" class="f-ui-lookup-empty">
      <img
        class="mT20"
        src="~statics/noData-light.png"
        width="100"
        height="100"
      />
      <div class="mT10 label-txt-black f14">
        {{
          $t('maintenance.pm.no_module_available', {
            moduleName: moduleDisplayName
              ? moduleDisplayName.toLowerCase()
              : moduleName,
          })
        }}
      </div>
    </div>
    <div class="f-ui-lookup-table-container" v-else>
      <div></div>
      <div class="flex flex-wrap w-10/12">
        <div
          v-if="!$validation.isEmpty(selectedItem)"
          class="pT20 pL20 pB10 fc-black-color letter-spacing0_5 f13 bold d-flex items-baseline flex-direction-row flex-wrap"
        >
          <template>
            <div class="mR10 mB10 mT5 flex-shrink-0">{{ selectedLabel }}</div>
            <div
              v-for="(name, index) in filteredSelectedItems"
              :key="index"
              class="action-badge mR10 mb5"
            >
              {{ name }}
              <i
                v-if="multiSelect"
                class="el-icon-close pointer"
                @click="handleSelectedClose(name)"
              ></i>
            </div>
            <div
              class="action-badge mR10 mb5"
              v-if="selectedRemainingCount !== 0"
            >
              +{{ selectedRemainingCount }}
            </div>
          </template>
        </div>
      </div>
      <BulkActionBar
        ref="bulk-action-bar"
        :visible="showBulkBar"
        :totalCount="totalCount"
        :perPage="perPage"
        :isAllSelected.sync="isAllSelected"
        :moduleName="moduleName"
      />
      <CommonList
        ref="lookupCommonList"
        v-if="!isEmpty(moduleList)"
        :viewDetail="viewDetail"
        :records="moduleList"
        :moduleName="moduleName"
        :slotList="slotList"
        :columnConfig="columnConfig"
        :hideListSelect="!multiSelect"
        @selection-change="handleSelection"
        @selectAll="selectAll"
      >
        <template #select="{ record }">
          <el-radio
            v-if="!multiSelect"
            :label="record.id"
            v-model="selectedItemId"
            @change="setSelectedItem"
          ></el-radio
        ></template>
        <template
          v-if="!$validation.isEmpty(actualMainfield)"
          #[slotList[1].criteria]="{ record }"
        >
          {{ getMainFieldValue(record) }}</template
        >
        <template v-else #[slotList[1].name]="{ record }">
          {{ getMainFieldValue(record) }}</template
        >
      </CommonList>
      <div class="footer-container">
        <el-button
          class="resource-selector-save"
          type="primary"
          @click="saveSelected"
        >
          {{ $t('maintenance.pm.proceed') }}
        </el-button>
        <el-button
          class="resource-selector-cancel"
          type="primary"
          @click="closeWizard"
        >
          {{ $t('maintenance._workorder.cancel') }}
        </el-button>
      </div>
    </div>
    <BulkSaveDialog
      v-if="showProgressDialog"
      :closeDialog="clearSelection"
      :totalCount="totalCount"
      :saveRecord="selectAllSave"
      @cancel="clearSelection"
    />
  </el-dialog>
</template>

<script>
import { LookupWizard } from '@facilio/ui/forms'
import { CommonList } from '@facilio/ui/app'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import Spinner from '@/Spinner'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { getResourcePlaceholder } from '../../utils/pm-utils.js'
import { FTags } from '@facilio/criteria'
import BulkActionBar from 'src/newapp/list/BulkActionBar'
import BulkSaveDialog from './BulkSaveDialog.vue'

const MAX_SELECTION = 5

export default {
  name: 'ResourceSelector',
  props: ['planner', 'pmRecord'],
  extends: LookupWizard,
  components: {
    Pagination,
    Spinner,
    CommonList,
    AdvancedSearch,
    FTags,
    BulkActionBar,
    BulkSaveDialog,
  },
  data: () => ({
    filters: null,
    columnConfig: {},
    isAllSelected: false,
    showBulkActionBar: false,
    showProgressDialog: false,
  }),
  computed: {
    showBulkBar() {
      let { showBulkActionBar, totalCount, perPage } = this || {}
      return showBulkActionBar && totalCount > perPage
    },
    title() {
      let { pmRecord } = this || {}
      let placeholder = getResourcePlaceholder(pmRecord, true)
      return `Select ${placeholder}`
    },
    showFTags() {
      let { filters } = this
      return !isEmpty(filters)
    },
    moduleDisplayName() {
      let { pmRecord } = this || {}
      return getResourcePlaceholder(pmRecord, true)
    },
    filteredSelectedItems() {
      let { selectedItemName } = this || {}
      return (selectedItemName || []).filter(
        (_, index) => index < MAX_SELECTION
      )
    },
    selectedRemainingCount() {
      let { selectedListLength: length } = this || {}
      if (length > MAX_SELECTION) {
        return Math.abs(length - MAX_SELECTION)
      } else {
        return 0
      }
    },
    selectedListLength() {
      let { selectedItem } = this || {}
      let { length } = selectedItem || {}
      return length
    },
    customFilters() {
      let { field } = this || {}
      let { filters } = field || {}
      return filters || {}
    },
  },
  watch: {
    isAllSelected(value) {
      if (value) {
        this.showProgressDialog = true
      }
    },
  },
  methods: {
    saveSelected() {
      let { selectedItem } = this || {}
      if (isEmpty(selectedItem)) {
        this.$message.error(this.$t('maintenance.pm.resources_proceed'))
      } else {
        this.setSelectedItem()
      }
    },
    selectAll() {
      let { selectedItem } = this || {}
      if (isEmpty(selectedItem)) {
        this.showBulkActionBar = false
      } else {
        this.showBulkActionBar = true
      }
    },
    closeWizard() {
      this.$emit('closeWizard')
    },
    async applyFilters({ filters }) {
      this.isLoading = true
      this.filters = filters
      this.selectedItem = []
      this.showBulkActionBar = false
      await this.fetchModuleList()

      this.isLoading = false
    },
    async fetchModuleList() {
      let { moduleName, page, perPage, planner, customFilters, filters } =
        this || {}
      if (!isEmpty(customFilters)) {
        filters = { ...filters, ...customFilters }
      }
      if (moduleName === 'asset') {
        filters = {
          ...filters,
          storeRoom: {
            operatorId: 1,
          },
        }
      }
      let { id } = planner || {}
      let params = {
        viewname: 'hidden-all',
        includeParentFilter: true,
        page,
        perPage,
        withCount: true,
        plannerId: id,
      }
      if (!isEmpty(filters)) {
        params['filters'] = JSON.stringify(filters)
      }
      let { list, error, meta } = await API.fetchAll(moduleName, params)
      if (isEmpty(error)) {
        this.moduleList = list
        this.totalCount = this.$getProperty(meta, 'pagination.totalCount', 0)
      }
    },
    handleSelection(selectedList) {
      if (isEmpty(selectedList)) {
        this.showBulkActionBar = false
      }
      let { mainFieldName } = this || {}

      let nonCurrentItems = this.getNonCurrentList(this.selectedItem)
      let currentItems = (selectedList || []).map(list => {
        let { id } = list
        return {
          label: list[mainFieldName],
          value: id,
        }
      })

      this.selectedItem = [...nonCurrentItems, ...currentItems]
    },
    clearSelection() {
      this.showProgressDialog = false
      this.isAllSelected = false
    },
    async selectAllSave() {
      let { field, filters, totalCount } = this || {}
      this.$emit('setLookupFieldValue', {
        field,
        isBulkSelect: true,
        filters: filters || {},
        totalCount,
      })
    },
  },
}
</script>

<style lang="scss">
.el-dialog__body {
  .bulk-action-container {
    padding: 5px;
    background-color: #f3f4f5;
    border-bottom: 1px solid #ebeef5;
  }
  .f-ui-lookup-empty {
    height: calc(100vh - 450px);
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }
  .f-ui-lookup-table-container {
    position: relative;
    height: calc(100vh - 450px);
    .el-table th > .cell {
      font-size: 11px;
      font-weight: bold;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      color: #333333;
      white-space: nowrap;
    }
    .el-table .el-table__cell {
      padding: 10px 20px;
    }
  }
}
</style>

<style scoped lang="scss">
.action-badge {
  border-radius: 12px;
  border: solid 1px #39b2c2;
  color: #39b2c2;
  font-size: 11px;
  padding: 5px 10px;
}
.lookup-wizard-title {
  font-size: 14px;
  font-weight: 700;
  color: rgb(50, 64, 86);
  text-transform: uppercase;
  line-height: 24px;
}
.footer-container {
  background-color: #ffffff;
  padding: 10px 20px;
  display: flex;
  justify-content: flex-end;
}
.resource-selector-save {
  background-color: #39b2c2;
  padding: 10px 20px;
  color: #ffffff;
}
.resource-selector-cancel {
  border: solid 1px #39b2c2;
  padding: 10px 20px;
  background-color: transparent;
  color: #39b2c2;
}
.separator {
  font-weight: 300;
  color: #d8d8d8 !important;
  padding-right: 10px;
  padding-left: 10px;
  padding-bottom: 2px;
}
.resource-filter-icon {
  cursor: pointer;
}
.resource-icons {
  padding: 5px;
  border: solid 1px transparent;
  border-radius: 3px;
  &:hover {
    color: #615e88;
    background: #f5f6f8;
    border: 1px solid #dae0e8;
  }
}
</style>
