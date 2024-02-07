<template>
  <div>
    <div class="related-list-container">
      <div class="related-list-header">
        <div class="header">
          <div class="widget-title d-flex flex-direction-column justify-center">
            {{ $t('common.header.dependent') }}
            {{ moduleDisplayName ? moduleDisplayName : moduleName }}
          </div>
          <el-input
            ref="mainFieldSearchInput"
            v-if="showMainFieldSearch"
            class="fc-input-full-border2 width-auto mL-auto"
            suffix-icon="el-icon-search"
            v-model="searchText"
            @blur="hideMainFieldSearch"
          ></el-input>
          <span v-else class="self-center mL-auto" @click="openMainFieldSearch">
            <inline-svg
              src="svgs/search"
              class="vertical-middle cursor-pointer"
              iconClass="icon icon-sm mT5 mR5 search-icon"
            ></inline-svg>
          </span>
          <span
            v-if="
              !$validation.isEmpty(totalCount) &&
                !$validation.isEmpty(modulesList)
            "
            class="separator self-center"
          >
            |
          </span>
          <pagination
            :currentPage.sync="page"
            :total="totalCount"
            :perPage="perPage"
            class="self-center"
          ></pagination>
          <template v-if="isNotPortal">
            <span class="separator self-center">|</span>
            <div class="self-center mR20">
              <el-dropdown trigger="click">
                <div class="flex-middle" ref="">
                  <i class="el-icon-plus pointer mL-auto text-fc-pink"></i>
                  <span class="pointer mL-auto text-fc-pink child-add ">{{
                    $t('common._common.add')
                  }}</span>
                </div>

                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item>
                    <div @click="showUpdateParentDialog">
                      {{ $t('common.header.existing_workorder') }}
                    </div>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <div @click="openWoCreation">
                      {{ $t('common.wo_report.new_workorder') }}
                    </div></el-dropdown-item
                  >
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </template>
        </div>
      </div>
      <div>
        <div class="fc-list-view fc-table-td-height">
          <div
            v-if="isLoading"
            class="loading-container d-flex justify-content-center"
          >
            <Spinner :show="isLoading"></Spinner>
          </div>

          <div
            v-if="$validation.isEmpty(modulesList) && !isLoading"
            class="flex-middle justify-content-center flex-col pT70 pB80"
          >
            <inline-svg
              src="svgs/emptystate/workorder"
              iconClass="icon text-center icon-xxxlg"
            ></inline-svg>
            <div class="nowo-label">
              {{
                $t('common.products.no_module_available', {
                  moduleName: moduleDisplayName
                    ? moduleDisplayName
                    : moduleName,
                })
              }}
            </div>
          </div>

          <div
            v-if="!isLoading && !$validation.isEmpty(modulesList)"
            class="view-column-select"
            @click="showColumnSettings = true"
          >
            <img
              src="~assets/column-setting.svg"
              style="text-align: center; position: absolute; top: 35%;right: 29%;"
            />
          </div>

          <el-table
            v-if="!isLoading && !$validation.isEmpty(modulesList)"
            :data="modulesList"
            style="width: 100%;"
            :fit="true"
            height="300"
          >
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
                    <div
                      class="self-center name bold"
                      @click="redirectToOverview(item.row)"
                    >
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
              :align="field.field.dataTypeEnum === 'DECIMAL' ? 'right' : 'left'"
              min-width="200"
            >
              <template v-slot="scope">
                <div
                  @click="redirectToOverview(scope.row)"
                  class="table-subheading"
                  :class="{
                    'text-right': field.field.dataTypeEnum === 'DECIMAL',
                  }"
                >
                  {{ getColumnDisplayValue(field, scope.row) || '---' }}
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop
              label
              width="130"
              class="visibility-visible-actions"
            >
              <template v-slot="item">
                <div class="text-center">
                  <span @click="invokeDeleteDialog(item.row)">
                    <inline-svg
                      src="svgs/delete"
                      class="pointer edit-icon-color visibility-hide-actions mL10"
                      iconClass="icon icon-sm icon-remove"
                    ></inline-svg>
                  </span>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
    <WoParentIdUpdateDialog
      v-if="canShowWizard"
      :canShowWoWizard.sync="canShowWizard"
      @updatedItems="updateItems"
      :parentWOId="id"
    ></WoParentIdUpdateDialog>
    <el-dialog
      :visible.sync="showDeleteDialog"
      class="dialog-d"
      custom-class="setup-dialog45"
      :show-close="false"
    >
      <div class="text-center fc-black-20">
        {{ $t('common._common.do_you_want_to_delete') }}
        {{ moduleName ? moduleName : '' }} ?
      </div>
      <span
        slot="footer"
        class="fc-dialog-center-container delete-dialog-footer padding-px18"
      >
        <el-button @click="showDeleteDialog = false">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          class="delete-dissociate-buttons"
          @click="dissociateItem()"
          >{{ $t('common._common.dissociate') }}</el-button
        >
        <el-button
          class="delete-dissociate-buttons"
          @click="deleteItemFromList()"
          >{{ $t('common.wo_report.move_to_recycle') }}</el-button
        >
      </span>
    </el-dialog>
    <column-customization
      :visible.sync="showColumnSettings"
      :moduleName="moduleName"
      viewName="hidden-all"
      :columnConfig="columnConfig"
      :relatedViewDetail="viewDetail"
      :relatedMetaInfo="metaInfo"
      @refreshRelatedList="refreshRelatedList"
    ></column-customization>
  </div>
</template>
<script>
import RelatedListWidget from '@/page/widget/common/RelatedListWidget'
import ColumnCustomization from '@/ColumnCustomization'
import WoParentIdUpdateDialog from '@/relatedlist/ParentUpdateViewer'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import { eventBus } from '@/page/widget/utils/eventBus'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
  getApp,
} from '@facilio/router'
import Constants from 'util/constant'

const columnConfigMap = {
  workorder: {
    fixedColumns: ['subject'],
    fixedSelectableColumns: ['noOfNotes', 'noOfTasks', 'noOfAttachments'],
  },
}

const customModulesColumnConfig = {
  fixedColumns: ['name'],
  fixedSelectableColumns: ['photo'],
}

export default {
  name: 'ChildRelationListWidget',
  props: ['widget', 'details'],
  extends: RelatedListWidget,
  components: {
    Spinner,
    WoParentIdUpdateDialog,
    ColumnCustomization,
  },
  data() {
    return {
      relatedList: [],
      showColumnSettings: false,
      itemToDeleteOrDissociate: {},
      showDeleteDialog: false,
      canShowWizard: false,
      deleteItem: {},
      quickSearchQueries: null,
      defaultHideColumns: ['photo', 'moduleState', 'stateFlowId'],
      columnConfig: {
        fixedColumns: ['subject'],
        fixedSelectableColumns: [
          'noOfNotes',
          'noOfTasks',
          'noOfAttachments',
          'attachmentPreview',
        ],
        availableColumns: [
          'category',
          'description',
          'dueDate',
          'status',
          'sourceType',
          'assignedTo',
          'createdTime',
          'priority',
          'resource',
          'type',
          'modifiedTime',
          'actualWorkStart',
          'actualWorkEnd',
          'totalCost',
          this.$helpers.isLicenseEnabled('TENANTS') ? 'tenant' : '',
        ],
        showLookupColumns: false,
        lookupToShow: [
          'resource',
          this.$helpers.isLicenseEnabled('TENANTS') ? 'tenant' : '',
        ],
      },
    }
  },
  created() {
    this.init()
  },
  mounted() {
    eventBus.$on('refresh-related-list', () => {
      this.refreshRelatedList()
    })
  },
  computed: {
    parentModuleName() {
      return this.$attrs.moduleName
    },
    moduleName() {
      return 'workorder'
    },
    moduleDisplayName() {
      return 'Work Orders'
    },
    id() {
      return this.details.id
    },
    filters() {
      let { mainField, searchText, moduleName } = this
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

      // special filter

      if (
        moduleName === 'workorder' &&
        this.$getProperty(this, 'selectedLookupField.field.module.name', '') ===
          'quote'
      ) {
        filterObj['isQuotationNeeded'] = {
          operatorId: 15,
          value: [String(true)],
        }
      }
      // main filter
      filterObj['parentWO'] = {
        operatorId: 36,
        value: [`${this.id}`],
      }
      return filterObj
    },
    isNotPortal() {
      let {
        appCategory: { PORTALS },
      } = Constants
      let { appCategory } = getApp()

      return appCategory !== PORTALS
    },
  },
  methods: {
    refreshRelatedList() {
      this.loadData()
    },
    openWoCreation() {
      let parentWO = this.id
      let { details } = this || {}
      let { siteId } = details || {}
      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

        name && this.$router.push({ name, query: { parentWO, siteId } })
      } else {
        this.$router.push({
          path: `/app/wo/create`,
          query: { parentWO, siteId },
        })
      }
    },
    updateItems(items) {
      if (items.length) {
        items.filter(wo => {
          this.modulesList.push(wo)
        })
      }
      this.$nextTick(() => {
        this.loadDataCount()
      })
    },
    showUpdateParentDialog() {
      this.canShowWizard = true
    },
    deleteItemFromList() {
      let { itemToDeleteOrDissociate } = this
      let item = this.modulesList.find(
        listItem => listItem.id === itemToDeleteOrDissociate.id
      )
      this.$store
        .dispatch('workorder/deleteWorkOrder', {
          id: [itemToDeleteOrDissociate.id],
        })
        .then(() => {
          let idx = this.modulesList.indexOf(item)
          this.modulesList.splice(idx, 1)
          this.$message.success(
            this.$t('maintenance._workorder.wo_delete_success')
          )
          this.loadDataCount()
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
      this.showDeleteDialog = false
    },
    dissociateItem() {
      let { itemToDeleteOrDissociate } = this
      let updateObj = {
        id: [itemToDeleteOrDissociate.id],
        fields: {
          parentWO: {
            id: -99,
          },
        },
      }
      let item = this.modulesList.find(
        listItem => listItem.id === itemToDeleteOrDissociate.id
      )
      this.$store
        .dispatch('workorder/updateWorkOrder', updateObj)
        .then(response => {
          let idx = this.modulesList.indexOf(item)
          this.modulesList.splice(idx, 1)
          this.loadDataCount()
          this.$message.success(
            this.$t('common._common.dissociated_successfully')
          )
        })
        .catch(() => {
          this.$message.error(
            this.$t('maintenance._workorder.wo_update_failed')
          )
        })
      this.showDeleteDialog = false
    },
    redirectToListView() {
      let { moduleName } = this
      let lookupFilter = {
        lookup: [
          {
            operatorId: 36,
            value: [this.id ? this.id : ''],
            selectedLabel: this.parentModuleName ? this.parentModuleName : '',
          },
        ],
      }
      this.$router.push({
        path: `/app/ca/modules/${moduleName}/all`,
        query: {
          search: JSON.stringify(lookupFilter),
          includeParentFilter: true,
        },
      })
    },
    redirectToOverview(row) {
      let route
      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          route = this.$router.resolve({
            name,
            params: {
              viewname: 'all',
              id: row.id,
            },
          }).href
        }
      } else {
        route = this.$router.resolve({
          path: `/app/wo/orders/summary/${row.id}`,
        }).href
      }
      route && window.open(route, '_blank')
    },
    invokeDeleteDialog(item) {
      this.showDeleteDialog = true
      this.itemToDeleteOrDissociate = item
    },
  },
}
</script>
<style lang="scss">
.related-list-container {
  .related-list-header {
    border-bottom: 1px solid #f7f8f9;
    padding: 10px 15px;
    display: flex;
    flex-direction: column;
    .header {
      display: flex;
      min-height: 35px;
    }
  }
  .view-column-select {
    right: 0px;
    top: 58px;
  }
  thead {
    th {
      .cell {
        text-transform: uppercase;
      }
      &.is-leaf {
        border-bottom: 1px solid #f2f5f6 !important;
        padding-left: 20px !important;
        padding-right: 20px !important;
      }
    }
  }
  .cell {
    font-size: 11px;
    font-weight: bold;
    letter-spacing: 1px;
    color: #333333;
  }
  .name {
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }
  .fc-input-full-border2 {
    .el-input__inner {
      height: 35px !important;
    }
  }
  .search-icon {
    fill: #91969d;
  }
  .column-customization-icon {
    z-index: 100;
    text-align: center;
    position: absolute;
    top: 35%;
    right: 29%;
  }
}
.delete-dialog-footer {
  display: flex;
  justify-content: center;
}
.dialog-d >>> .el-dialog__header {
  padding: 0px;
}
.delete-dissociate-buttons {
  letter-spacing: 1px;
  text-align: center;
  color: #ffffff;
  font-size: 12px;
  background-color: #ec7c7c;
}
.child-add {
  width: 51px;
  font-size: 13px;
  font-weight: bold;
  padding-left: 5px;
  line-height: normal;
  letter-spacing: 0.5px;
}
.view-column-select {
  position: absolute;
  top: 88px;
  right: 11px;
  display: block;
  width: 45px;
  height: 50px;
  cursor: pointer;
  text-align: center;
  background-color: #ffffff;
  border-left: 1px solid #f2f5f6;
  z-index: 1;
}
</style>
