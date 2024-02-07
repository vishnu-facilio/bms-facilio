<template>
  <CommonListLayout
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => moduleDisplayName"
    :pathPrefix="parentPath"
    :hideSubHeader="isEmpty(records)"
    :recordCount="listCount"
    :recordLoading="showLoading"
    :key="moduleName + '-list-layout'"
  >
    <template #header>
      <AdvancedSearchWrapper
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>

      <template v-if="showNewButton">
        <button class="fc-create-btn" @click="redirectToFormCreation()">
          New {{ moduleDisplayName ? moduleDisplayName : '' }}
        </button>
      </template>
    </template>
    <template #sub-header-actions>
      <template v-if="!showSearch">
        <pagination
          :total="listCount"
          :perPage="50"
          class="pL15 fc-black-small-txt-12"
        ></pagination>
        <span class="separator" v-if="listCount > 0">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.sort')"
          placement="right"
        >
          <sort
            :key="moduleName + '-sort'"
            :config="sortConfig"
            :sortList="sortConfigLists"
            @onchange="updateSort"
          ></sort>
        </el-tooltip>
        <span class="separator">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.export')"
          placement="left"
        >
          <f-export-settings
            :module="moduleName"
            :viewDetail="viewDetail"
            :showViewScheduler="false"
            :showMail="false"
            :filters="filters"
          ></f-export-settings>
        </el-tooltip>
      </template>
    </template>
    <template #content>
      <Spinner v-if="showLoading" class="mT40" :show="showLoading"></Spinner>

      <div
        v-if="$validation.isEmpty(records) && !showLoading"
        class="d-flex flex-direction-column m10 list-empty-state justify-content-center"
      >
        <img
          class="mT20 self-center"
          src="~statics/noData-light.png"
          width="100"
          height="100"
        />
        <div class="mT10 label-txt-black f14 self-center">
          No
          {{ moduleDisplayName ? moduleDisplayName.toLowerCase() : moduleName }}
          available.
        </div>
      </div>

      <template v-if="!showLoading && !$validation.isEmpty(records)">
        <div
          class="fc-card-popup-list-view fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
        >
          <div class="view-column-chooser" @click="showColumnSettings = true">
            <img
              src="~assets/column-setting.svg"
              class="text-center position-absolute"
              style="top: 35%;right: 29%;"
            />
          </div>
          <el-table
            :data="records"
            style="width: 100%;"
            height="100%"
            :fit="true"
            @selection-change="selectItems"
          >
            <el-table-column
              type="selection"
              width="60"
              fixed
              v-model="selectAll"
            ></el-table-column>
            <el-table-column
              v-if="!$validation.isEmpty(nameColumn)"
              :label="nameColumn.displayName"
              :prop="nameColumn.name"
              fixed
              min-width="250"
            >
              <template v-slot="item">
                <div
                  @click="redirectToOverview(item.row)"
                  class="table-subheading"
                >
                  <div class="d-flex">
                    <div v-if="item.row.photoId > 0">
                      <img
                        :src="getImage(item.row.photoId)"
                        class="img-container"
                      />
                    </div>
                    <el-tooltip
                      effect="dark"
                      :content="
                        getColumnDisplayValue(nameColumn, item.row) || ''
                      "
                      placement="top"
                      :open-delay="600"
                    >
                      <div class="self-center mL5">
                        {{
                          getColumnDisplayValue(nameColumn, item.row) || '---'
                        }}
                      </div>
                    </el-tooltip>
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
                field.field && field.field.dataTypeEnum === 'DECIMAL'
                  ? 'right'
                  : 'left'
              "
              min-width="230"
            >
              <template v-slot="scope">
                <div
                  v-if="
                    $getProperty(field, 'field.displayType') === 'SIGNATURE'
                  "
                >
                  <SignatureField
                    :field="(field || {}).field"
                    :record="scope.row"
                  />
                </div>
                <div
                  v-else
                  @click="redirectToOverview(scope.row)"
                  class="table-subheading"
                  :class="{
                    'text-right':
                      field.field && field.field.dataTypeEnum === 'DECIMAL',
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
                  <span
                    v-if="
                      $org.id === 321 && moduleName === 'custom_tenantbilling'
                    "
                    @click="downloadTenantBilling(item.row)"
                  >
                    <inline-svg
                      src="svgs/download2"
                      class="download-icon visibility-hide-actions"
                      iconClass="icon icon-sm mR20 pointer"
                    ></inline-svg>
                    <iframe
                      v-if="downloadUrl"
                      :src="downloadUrl"
                      style="display: none;"
                    ></iframe>
                  </span>
                  <span @click="editModule(item.row)">
                    <inline-svg
                      src="svgs/edit"
                      class="edit-icon-color visibility-hide-actions"
                      iconClass="icon icon-sm mR5 icon-edit"
                    ></inline-svg>
                  </span>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <column-customization
          :visible.sync="showColumnSettings"
          :moduleName="moduleName"
          :viewName="view"
          :columnConfig="columnConfig"
        ></column-customization>
      </template>
    </template>
    <BillSummaryDialog
      v-if="showBillSummary"
      :summary="selectedRow"
      :viewname="viewname"
      @onClose="showBillSummary = false"
    ></BillSummaryDialog>
    <portal to="view-manager-link">
      <div @click="getViewManagerRoute" class="view-manager-btn">
        <inline-svg
          src="svgs/hamburger-menu"
          class="d-flex"
          iconClass="icon icon-sm"
        ></inline-svg>
        <span class="label mL10 text-uppercase">
          {{ $t('viewsmanager.list.views_manager') }}
        </span>
      </div>
    </portal>
  </CommonListLayout>
</template>
<script>
import listView from 'pages/base-module-v2/ModuleList.vue'
import { pageTypes } from '@facilio/router'
import { findRouterForModuleInApp } from 'newapp/viewmanager/routeUtil'
export default {
  extends: listView,
  methods: {
    getViewManagerRoute() {
      let { moduleName, $route } = this
      let { query } = $route

      let route = findRouterForModuleInApp(moduleName, pageTypes.VIEW_MANAGER)
      if (route)
        this.$router.push({
          ...route,
          params: { moduleName },
          query: { ...query },
        })
    },
  },
}
</script>
