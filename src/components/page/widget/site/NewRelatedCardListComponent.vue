<template>
  <div ref="site-buildings-widget" :class="getWidgetClasses()">
    <portal :to="widget.key + '-title-section'">
      <div class="flex-middle justify-content-space space-white-header ">
        <div class="f18 bold">
          {{ `${moduleHeaderName}` }}
        </div>
        <div class="flex-middle widget-table-header-actions">
          <el-input
            ref="mainFieldSearchInput"
            v-if="showMainFieldSearch"
            class="fc-input-full-border2 width-auto mL-auto"
            suffix-icon="el-icon-search"
            v-model="searchText"
            autofocus
            @blur="hideMainFieldSearch"
          ></el-input>
          <span v-else class="self-center mL-auto" @click="openMainFieldSearch">
            <inline-svg
              src="svgs/search"
              class="vertical-middle cursor-pointer"
              iconClass="icon icon-sm mT5 mR5 search-icon"
            ></inline-svg>
          </span>
          <span v-if="canShowSorting" class="separator self-center">|</span>
          <sort
            v-if="canShowSorting"
            :key="'space-sort'"
            :config="sortConfig"
            :sortList="sortConfigLists"
            @onchange="updateSort"
          ></sort>
          <span v-if="canShowSorting" class="separator self-center">|</span>
          <pagination
            :currentPage.sync="page"
            :total="totalCount"
            :perPage="perPage"
            class="self-center"
          ></pagination>
          <!-- <span v-if="showWidgetTypeSwitch" class="separator self-center"
            >|</span
          >
          <div v-if="showWidgetTypeSwitch" @click="type = 'card'">
            <inline-svg
              src="svgs/list-col"
              class="vertical-middle pointer"
              :iconClass="
                `icon icon-md ${
                  type === 'card'
                    ? ' stroke-grey-active'
                    : 'stroke-grey-inactive'
                }`
              "
            >
            </inline-svg>
          </div>

          <span v-if="showWidgetTypeSwitch" class="separator self-center"
            >|</span
          >
          <div v-if="showWidgetTypeSwitch" @click="type = 'list'">
            <inline-svg
              src="svgs/list-view"
              class="vertical-middle pointer"
              :iconClass="
                `icon icon-md ${
                  type === 'list' ? ' fill-grey-active' : 'fill-grey-inactive'
                }`
              "
            >
            </inline-svg>
          </div> -->
          <span
            v-if="$hasPermission('space:CREATE')"
            class="separator self-center"
            >|</span
          >
          <div
            v-if="$hasPermission('space:CREATE')"
            @click="openNewForm"
            class="f13 bold pointer"
          >
            <el-button
              type="primary"
              class="table-header-btn"
              :disabled="decommission"
            >
              {{
                moduleName === 'building'
                  ? $t('space.sites.newbuilding')
                  : $t('space.sites.new_space')
              }}
            </el-button>
          </div>
        </div>
      </div>
    </portal>
    <div ref="component-container" class="height100">
      <div
        v-if="isLoading || isSearchDataLoading"
        class="loading-container d-flex justify-content-center height100"
      >
        <spinner :show="isLoading || isSearchDataLoading"></spinner>
      </div>
      <div v-else-if="$validation.isEmpty(modulesList)" class="height300">
        <ListNoData
          :iconPath="`svgs/spacemanagement/${moduleName}`"
          :moduleDisplayName="moduleHeaderName"
          :moduleName="moduleName"
        ></ListNoData>
      </div>
      <component
        v-else-if="type === 'card'"
        :is="(componentMap[moduleName] || {})[type]"
        :details="details"
        :modulesList="modulesList"
        :moduleName="moduleName"
      ></component>

      <div
        v-else-if="type === 'list'"
        class="fc-list-view fc-table-td-height related-list-container"
        ref="related-table-list"
      >
        <div class="view-column-chooser" @click="showColumnCustomization">
          <img
            src="~assets/column-setting.svg"
            class="column-customization-icon"
          />
        </div>
        <el-table
          :data="modulesList"
          :fit="true"
          :height="getTableHeight()"
          row-class-name="building-row"
        >
          <el-table-column
            v-if="!$validation.isEmpty(mainFieldColumn)"
            :label="mainFieldColumn.displayName"
            :prop="mainFieldColumn.name"
            fixed
            min-width="200"
          >
            <template v-slot="item">
              <div @click="routeToSummary(item.row)" class="table-subheading">
                <div class="d-flex">
                  <div v-if="item.row.avatarUrl" class="flex-middle">
                    <img :src="item.row.avatarUrl" class="img-container" />
                  </div>
                  <div v-else>
                    <fc-icon
                      group="default"
                      :name="moduleName"
                      size="22"
                    ></fc-icon>
                  </div>
                  <div class="self-center name bold mL10">
                    {{
                      getColumnDisplayValue(mainFieldColumn, item.row) || '---'
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
              <div v-else class="table-subheading">
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
                <span
                  v-if="$hasPermission('space:UPDATE')"
                  @click="editItem(item.row)"
                >
                  <inline-svg
                    src="svgs/edit"
                    class="edit-icon-color visibility-hide-actions"
                    iconClass="icon icon-sm mR5 icon-edit"
                  ></inline-svg>
                </span>
                <span
                  v-if="$hasPermission('space:DELETE')"
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
    <column-customization
      :visible.sync="canShowColumnCustomization"
      :moduleName="relatedListModuleName"
      :columnConfig="relatedListColumnConfig"
      :relatedViewDetail="relatedViewDetail"
      :relatedMetaInfo="relatedMetaInfo"
      :viewName="relatedViewName"
      @refreshRelatedList="refreshRelatedList"
    ></column-customization>
    <DeleteDialog
      v-if="showDialog"
      :moduleName="moduleName"
      :errorMap="errorMap"
      @closeDialog="closeDialog()"
      :id="deletingRecordId"
      :type="errorType"
      @refresh="refreshRelatedList()"
    >
    </DeleteDialog>
  </div>
</template>

<script>
import RelatedCardListComponent from './RelatedCardListComponent.vue'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: RelatedCardListComponent,
  computed: {
    canShowSorting() {
      return !isEmpty(this.totalCount) && !isEmpty(this.modulesList)
    },
    decommission() {
      return this.$getProperty(this, 'details.decommission', false)
    },
  },
  methods: {
    getTableHeight() {
      // this.$nextTick(() => {
      let widget = this.$refs['site-buildings-widget']
      let height
      if (!isEmpty(widget)) {
        height = widget.clientHeight
      }
      return height
      // })
    },
    getWidgetClasses() {
      return `site-buildings-main site-buildings-lavender pT0 ${
        this.type === 'card' && isEmpty(this.modulesList)
          ? 'building-card-component'
          : 'building-list-component'
      }`
    },
  },
}
</script>

<style lang="scss">
.site-buildings-lavender {
  .related-list-container {
    .building-row {
      height: 53px;
    }
  }
  .el-table th.el-table__cell {
    background-color: #f3f1fc;
  }
}
</style>

<style lang="scss" scoped>
.site-buildings-lavender {
  .view-column-chooser {
    background-color: #f3f1fc;
  }
  .table-subheading {
    .img-container {
      height: 24px;
      width: 24px;
    }
  }
}
.widget-table-header-actions {
  .table-header-btn {
    color: #fff;
    background-color: #3ab2c2;
    border-color: #3ab2c2;
    border-radius: 3px;
    padding: 9px 16px;
    height: 32px;
  }
}
.space-white-header {
  background: #ffffff;
  padding: 15px;
  height: 58px;
}
</style>
