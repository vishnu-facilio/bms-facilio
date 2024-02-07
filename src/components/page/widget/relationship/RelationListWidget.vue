<template>
  <div class="relationship-list-container">
    <div class="relationship-header-container">
      <div class="header-title">
        <div class="title-name">{{ relationshipDisplayName }}</div>
        <div class="title-link">
          {{ relationName }} -
          {{ toModuleDisplayName }}
        </div>
      </div>
      <div v-if="!isLoading" class="header-action">
        <template v-if="hasRelationList || hasSearchText">
          <el-input
            v-if="showMainFieldSearch"
            v-model="searchText"
            ref="mainFieldSearchInput"
            class="fc-input-full-border2 width-auto mL-auto"
            clearable
            @blur="hideMainFieldSearch"
            @clear="hideMainFieldSearch"
            :placeholder="$t('common._common.search')"
          ></el-input>
          <span v-else @click="openMainFieldSearch">
            <inline-svg
              src="svgs/search"
              class="vertical-middle cursor-pointer"
              iconClass="icon icon-sm mT5 mR5 search-icon"
            ></inline-svg>
          </span>
        </template>

        <template v-if="hasListCount">
          <span class="separator self-center">|</span>
          <Pagination
            :currentPage.sync="page"
            :total="totalCount"
            :perPage="perPage"
            @onChange="loadRelationData"
          ></Pagination>
        </template>

        <template v-if="$hasPermission(`${moduleName}:UPDATE`)">
          <span
            v-if="hasListCount || hasRelationList || hasSearchText"
            class="separator"
            >|</span
          >
          <div class="associate-relation" @click="showLookupFieldWizard = true">
            <i class="el-icon-plus"></i>
            <div class="associate-text">
              {{ $t('setup.relationship.associate') }}
            </div>
          </div>
        </template>
      </div>
    </div>

    <spinner
      v-if="isLoading"
      :show="isLoading"
      class="loading-container d-flex justify-content-center"
    ></spinner>

    <template v-else>
      <div v-if="!hasRelationList" class="relationship-empty-state">
        <inline-svg
          :src="`svgs/emptystate/readings-empty`"
          iconClass="icon text-center icon-xxxlg"
        ></inline-svg>
        <div class="pT10 fc-black-dark f18 bold pB50">
          {{
            $t('setup.relationship.no_module_available', {
              moduleName: toModuleDisplayName,
            })
          }}
        </div>
      </div>
      <template v-else>
        <div
          class="column-customization-icon"
          @click="showColumnSettings = true"
        >
          <img
            src="~assets/column-setting.svg"
            class="text-center position-absolute icon"
          />
        </div>

        <CommonList
          :viewDetail="viewDetail"
          :records="relationslist"
          :moduleName="toModuleName"
          :columnConfig="columnConfig"
          :redirectToOverview="openRecordSummary"
          :hideListSelect="true"
          class="fc-common-table-list-module"
          :slotList="slotList"
        >
          <template #[slotList[0].criteria]="{record}">
            <div
              @click="openRecordSummary(record.id)"
              class="d-flex label-txt-black"
              :class="{ 'main-field-column': isLink }"
            >
              <el-tooltip
                effect="dark"
                :content="getFixName(record)"
                placement="top"
                :open-delay="600"
              >
                <div class="self-center mL0 ">
                  {{ getFixName(record) }}
                </div>
              </el-tooltip>
            </div>
          </template>

          <template #[slotList[1].name]="{record}">
            <div
              @click="deleteRecords([record.id])"
              class="d-flex justify-end mR20"
            >
              <el-tooltip
                effect="dark"
                :content="$t('setup.relationship.dissociate')"
                placement="top"
                :open-delay="600"
              >
                <inline-svg
                  src="svgs/unlink"
                  class="pointer visibility-hide-actions mT7"
                  iconClass="icon text-center icon-lg fill-delete"
                ></inline-svg>
              </el-tooltip>
            </div>
          </template>
        </CommonList>

        <column-customization
          :visible.sync="showColumnSettings"
          :moduleName="toModuleName"
          viewName="hidden-all"
          :columnConfig="columnConfig"
          :relatedViewDetail="viewDetail"
          @refreshRelatedList="reloadData"
        ></column-customization>
      </template>
    </template>

    <LookupWizard
      v-if="showLookupFieldWizard"
      :key="`${relationName}-${toModuleName}`"
      :canShowLookupWizard.sync="showLookupFieldWizard"
      :listUrlConfig="listUrlConfig"
      :config="{}"
      @setListValues="setListValues"
    ></LookupWizard>
  </div>
</template>
<script>
import Pagination from 'pageWidgets/utils/WidgetPagination'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { LookupWizard } from '@facilio/ui/forms'
import CommonList from 'src/newapp/list/CommonList.vue'
import ColumnCustomization from '@/ColumnCustomization'
import isEqual from 'lodash/isEqual'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['details', 'widget', 'toModuleName', 'parentId', 'moduleName'],
  components: {
    CommonList,
    Pagination,
    ColumnCustomization,
    LookupWizard,
  },
  data() {
    return {
      relationslist: [],
      page: 1,
      perPage: 10,
      showMainFieldSearch: false,
      searchText: null,
      viewDetail: {},
      totalCount: null,
      showLookupFieldWizard: false,
      isLoading: false,
      showColumnSettings: false,
    }
  },
  created() {
    this.init()
    this.debounceMainFieldSearch = this.$helpers.debounce(() => {
      this.page = 1
      this.loadRelationData()
    }, 700)
  },
  computed: {
    listUrlConfig() {
      let { searchText, details } = this
      let { relation } = this.widget || {}
      let { fromModuleName, reverseRelationLinkName } = relation || {}
      let params = {
        unAssociated: true,
      }
      if (!isEmpty(searchText)) {
        params.search = searchText
      }
      return {
        url: `v3/modules/${fromModuleName}/${details?.id}/relationship/${reverseRelationLinkName}`,
        toModuleName: this.toModuleName,
        params,
        multiple: true,
      }
    },
    mainFieldName() {
      let { viewDetail } = this
      let { fields } = viewDetail || {}
      let mainFieldObj = (fields || []).find(fld => fld?.field?.mainField) || {}
      let { field } = mainFieldObj || {}
      let { name } = field || {}

      return name
    },
    columnConfig() {
      let { mainFieldName } = this
      return { fixedColumns: [mainFieldName] }
    },
    slotList() {
      return [
        { criteria: JSON.stringify({ name: this.mainFieldName }) },
        {
          name: 'editDelete',
          isActionColumn: true,
          columnAttrs: {
            width: 120,
            class: 'visibility-visible-actions',
            fixed: 'right',
          },
        },
      ]
    },
    toModuleDisplayName() {
      return this.$getProperty(this.widget, 'relation.toModule.displayName')
    },
    relationName() {
      return this.$getProperty(this.widget, 'relation.relationName')
    },
    isLink() {
      let isCustomModule = this.$getProperty(
        this.widget,
        'relation.toModule.custom'
      )
      return isCustomModule || isWebTabsEnabled()
    },
    filterObj() {
      let filterObj = {}
      let { mainFieldName, searchText } = this

      if (!isEmpty(mainFieldName) && !isEmpty(searchText)) {
        filterObj[mainFieldName] = { operatorId: 5, value: [searchText] }
      }

      return filterObj
    },
    hasRelationList() {
      return !isEmpty(this.relationslist)
    },
    hasSearchText() {
      return !isEmpty(this.searchText)
    },
    hasListCount() {
      return !isEmpty(this.totalCount) && this.totalCount !== 0
    },
    relationshipDisplayName() {
      return this.$getProperty(this.widget, 'relation.name')
    },
  },
  watch: {
    filterObj(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) this.debounceMainFieldSearch()
    },
  },
  methods: {
    init() {
      this.loadRelationData()
      this.loadViewDetails()
    },
    reloadData() {
      this.showColumnSettings = false
      this.init()
    },
    async loadViewDetails() {
      let viewDetailUrl = `v2/views/hidden-all?moduleName=${this.toModuleName}`
      let { data, error } = await API.get(viewDetailUrl)

      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.viewDetail = data?.viewDetail || {}
      }
    },
    getFixName(record) {
      let { mainFieldName } = this
      let { [mainFieldName]: recordName } = record || {}
      return recordName || '---'
    },
    async loadRelationData() {
      let { relation } = this.widget || {}
      let { reverseRelationLinkName } = relation || {}
      let params = {
        viewName: 'hidden-all',
        page: this.page,
        includeParentFilter: true,
        withCount: true,
        perPage: this.perPage,
        filters: !isEmpty(this.filterObj)
          ? JSON.stringify(this.filterObj)
          : null,
      }

      this.isLoading = true

      let config = { force: true }
      let url = `v3/modules/${this.moduleName}/${this.details?.id}/relationship/${reverseRelationLinkName}`
      let { data, meta, error } = await API.get(url, params, config)

      if (error) {
        let { isCancelled } = error || {}
        if (!isCancelled)
          this.$message.error(this.$t('common._common.error_occured'))
      } else {
        this.relationslist = data[this.toModuleName] || []
        this.totalCount = this.$getProperty(meta, 'pagination.totalCount', null)
      }
      this.isLoading = false
    },
    setListValues(selectedItems) {
      if (!isEmpty(selectedItems)) {
        let selectedId = selectedItems.map(item => {
          let { value } = item || {}
          return value
        })

        this.bulkCreateRelations(selectedId)
      }
    },
    openRecordSummary(id) {
      let { widget } = this
      let route
      let isCustomModule = this.$getProperty(widget, 'relation.toModule.custom')
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.toModuleName, pageTypes.OVERVIEW) || {}

        if (name) {
          route = this.$router.resolve({
            name,
            params: {
              viewname: 'all',
              id: id,
            },
          }).href
        }
      } else {
        if (isCustomModule) {
          route = this.$router.resolve({
            path: `/app/ca/modules/${this.toModuleName}/all/${id}/summary`,
          }).href
        }
      }
      if (route) window.open(route, '_blank')
      return route
    },
    openMainFieldSearch() {
      this.showMainFieldSearch = true

      this.$nextTick(() => {
        let mainFieldSearchInput = this.$refs['mainFieldSearchInput']

        if (!isEmpty(mainFieldSearchInput)) {
          mainFieldSearchInput.focus()
        }
      })
    },
    hideMainFieldSearch() {
      if (isEmpty(this.searchText)) this.showMainFieldSearch = false
    },
    async bulkCreateRelations(selectedIds) {
      this.isLoading = true

      let { widget, parentId } = this
      let { relation } = widget || {}
      let { fromModuleName, toModuleName, forwardRelationLinkName } =
        relation || {}
      let url = `v3/modules/${fromModuleName}/${parentId}/relationship/${forwardRelationLinkName}`
      let params = { data: { [toModuleName]: selectedIds } }
      let { error } = await API.post(url, params)

      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
        this.isLoading = false
      } else this.loadRelationData()
    },
    async deleteRecords(idList) {
      let value = await this.$dialog.confirm({
        title: this.$t(`setup.relationship.dissociate`),
        message: this.$t(`setup.relationship.delete_message`),
        rbDanger: true,
        rbLabel: this.$t('setup.relationship.dissociate'),
      })

      if (!value) return

      this.isLoading = true

      let { widget, parentId } = this
      let { relation } = widget || {}
      let { fromModuleName, toModuleName, forwardRelationLinkName } =
        relation || {}
      let url = `v3/modules/${fromModuleName}/${parentId}/relationship/${forwardRelationLinkName}`
      let params = { data: { [toModuleName]: idList } }
      let { error } = await API.patch(url, params)

      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
        this.isLoading = false
      } else this.loadRelationData()
    },
  },
}
</script>
<style lang="scss">
.relationship-list-container {
  .relationship-header-container {
    border-bottom: 1px solid #f7f8f9;
    padding: 10px 15px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    min-height: 60px;

    .header-title {
      text-transform: capitalize;
      margin-left: 4px;
      line-height: 20px;

      .title-name {
        font-size: 14px;
        letter-spacing: 1px;
        font-weight: 500;
        color: #385571;
      }
      .title-link {
        font-size: 12px;
        color: #808080;
        letter-spacing: 0.3px;
        font-weight: 400;
      }
    }
    .header-action {
      display: flex;
      align-items: center;
      flex-shrink: 0;
      min-height: 40px;

      .associate-relation {
        display: flex;
        align-items: center;
        color: #ff3184;
        cursor: pointer;

        .associate-text {
          font-size: 13px;
          font-weight: bold;
          padding-left: 5px;
          line-height: normal;
          letter-spacing: 0.5px;
        }
      }
    }
  }
  .relationship-empty-state {
    height: calc(100% - 61px);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
  }
  .column-customization-icon {
    top: 75px;
    right: 45px;
    height: 16px;
    width: 16px;
    position: absolute;
    z-index: 20;
    cursor: pointer;
  }
  .fc-common-table-list-module {
    height: 285px !important;
    overflow-y: scroll;

    .el-table__body td {
      padding-right: 20px !important;
      padding-left: 20px !important;
    }

    .el-table__body td.el-table__cell div {
      font-size: 14px !important;
      font-weight: normal !important;
    }
    th.el-table__cell {
      background-color: #f3f1fc;
    }
  }
}
</style>
