<template>
  <div class="related-list-container" ref="relatedListContainer">
    <div class="related-list-header">
      <div class="header justify-content-space">
        <div class="widget-title d-flex flex-direction-column justify-center">
          {{ moduleDisplayName ? moduleDisplayName : moduleName }}
        </div>
        <template v-if="!$validation.isEmpty(modulesList)">
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
              v-if="
                !widgetParams.hideSearchField ||
                  !hideSearchFieldList.includes(moduleName)
              "
              src="svgs/search"
              class="vertical-middle cursor-pointer"
              iconClass="icon icon-sm mT5 mR5 search-icon"
            ></inline-svg>
          </span>
          <span
            v-if="
              !$validation.isEmpty(totalCount) &&
                !$validation.isEmpty(modulesList) &&
                !hideSearchFieldList.includes(moduleName)
            "
            class="separator self-center"
            >|</span
          >
          <pagination
            :currentPage.sync="page"
            :total="totalCount"
            :perPage="perPage"
            class="self-center"
          ></pagination>
        </template>
      </div>
    </div>
    <div class="position-relative">
      <div
        v-if="isLoading"
        class="loading-container d-flex justify-content-center"
      >
        <spinner :show="isLoading"></spinner>
      </div>

      <div
        v-else-if="$validation.isEmpty(modulesList)"
        class="flex-middle justify-content-center wo-flex-col flex-direction-column"
        style="margin-top:4%"
      >
        <inline-svg
          :src="`svgs/emptystate/readings-empty`"
          iconClass="icon text-center icon-xxxlg"
        ></inline-svg>
        <div class="pT10 fc-black-dark f18 bold pB50">
          {{ $t('common.products.no_mod_available') }}
          {{ moduleDisplayName ? moduleDisplayName : moduleName }}
          {{ $t('common._common.available') }}
        </div>
      </div>
      <div v-else class="fc-list-view fc-table-td-height">
        <div
          v-if="canShowColumnIcon"
          class="view-column-chooser"
          @click="showColumnCustomization"
        >
          <img
            src="~assets/column-setting.svg"
            class="column-customization-icon"
          />
        </div>

        <el-table
          :data="modulesList"
          style="width: 100%;"
          :fit="true"
          :height="tableHeight"
        >
          <el-table-column :label="'Name'" fixed min-width="200">
            <template v-slot="induction">
              <div
                class="table-subheading  main-field-column"
                @click="redirectToOverview(induction.row)"
              >
                <div class="d-flex">
                  <div class="self-center name bold">
                    {{ (induction.row.parent || {}).name || '---' }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            v-for="(field, index) in filteredViewColumns"
            :key="index"
            :prop="field.name"
            :label="getColumnHeaderLabel(field)"
            :align="
              (field.field || {}).dataTypeEnum === 'DECIMAL' ? 'right' : 'left'
            "
            min-width="200"
          >
            <template v-slot="scope">
              <div v-if="isFileTypeField((field || {}).field)">
                <FilePreviewColumn
                  :field="field"
                  :record="scope.row"
                  :isV2="true"
                />
              </div>
              <div
                v-else
                class="table-subheading"
                :class="{
                  'text-right': (field.field || {}).dataTypeEnum === 'DECIMAL',
                }"
              >
                {{ getColumnDisplayValue(field, scope.row) || '---' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            v-if="canShowDelete || canShowEdit"
            prop
            label
            width="130"
            class="visibility-visible-actions"
          >
            <template v-slot="item">
              <div class="text-center">
                <span
                  v-if="canShowDelete"
                  @click="invokeDeleteDialog(item.row)"
                >
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
    <ColumnCustomization
      :visible.sync="canShowColumnCustomization"
      :moduleName="moduleName"
      :columnConfig="columnCustomizationConfig"
      :relatedViewDetail="viewDetail"
      :relatedMetaInfo="currentMetaInfo"
      :viewName="'hidden-all'"
      @refreshRelatedList="refreshRelatedList"
    />
  </div>
</template>
<script>
import RelatedListWidget from '@/page/widget/common/RelatedListWidget'
import { isEmpty, isNullOrUndefined } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import isEqual from 'lodash/isEqual'

export default {
  props: ['details', 'widget', 'resizeWidget'],
  name: 'InductionHistory',
  extends: RelatedListWidget,
  watch: {
    page: {
      async handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          let { isSearchDataLoading } = this
          if (!isSearchDataLoading) {
            this.isLoading = true
            await this.fetchModulesData(true)
            await this.loadDataCount(true)
            this.isLoading = false
          }
        }
      },
    },
  },
  computed: {
    moduleName() {
      return 'inductionResponse'
    },
    moduleDisplayName() {
      return 'Induction'
    },
    moduleHeaderName() {
      return 'Induction'
    },
    filters() {
      let { details } = this || {}
      let { id } = details || {}
      return { parent: { operatorId: 36, value: [`${id}`] } }
    },
  },
  methods: {
    fetchModulesData() {
      let { moduleName, page } = this
      return API.fetchAll(moduleName, {
        page,
        perPage: 10,
        withCount: true,
        filters: this.filters ? JSON.stringify(this.filters) : null,
      })
        .then(({ list, error }) => {
          if (error) {
            this.$message.error(error.message)
          } else {
            if (!isEmpty(list)) {
              let moduleDatas = list
              if (!isNullOrUndefined(moduleDatas)) {
                this.$set(this, 'modulesList', moduleDatas)
              }
            }
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
        .finally(() => {
          this.isSearchDataLoading = false
          this.autoResize()
        })
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['relatedListContainer']

        if (container) {
          let width = container.scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height: 500, width })
          }
        }
      })
    },
    redirectToOverview(record) {
      let { id } = record || {}
      let { moduleName } = this
      let route
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW)
        if (name)
          route = this.$router.resolve({
            name,
            params: { id, viewname: 'all' },
          })
      } else {
        route = this.$router.resolve({
          name: 'individualInductionSummary',
          params: { id, viewname: 'all' },
        })
      }
      route && window.open(route.href, '_blank')
    },
  },
}
</script>

<style></style>
