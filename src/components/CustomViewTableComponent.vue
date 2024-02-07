<template>
  <div class="height100 row">
    <div class="height100 f-list-view">
      <CommonListLayout
        :moduleName="moduleName"
        :showViewRearrange="true"
        :showViewEdit="true"
        :visibleViewCount="3"
        :getPageTitle="() => moduleDisplayName"
        :cancelRedirect="true"
        :hideleftViewHeader="true"
        :pathPrefix="parentPath"
        :viewName="viewname"
      >
        <template #views>
          <ViewHeader
            :target="targetDetail"
            :moduleName="moduleName"
            :showRearrange="showViewRearrange"
            :showCurrentViewOnly="showSearch"
            :maxVisibleMenu="3"
            :showEditIcon="true"
            :pathPrefix="parentPath"
            :retainFilters="filtersToRetain"
            :cancelRedirect="true"
            :currentViewName="viewname"
            :hideleftViewHeader="true"
            @viewsLoaded="$emit('viewsLoaded')"
            class="custom-view-table-actions"
          >
          </ViewHeader>
        </template>
        <template #header>
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
          </template>
        </template>
        <template #content>
          <div></div>
          <Spinner
            v-if="showLoading"
            class="mT40"
            :show="showLoading"
          ></Spinner>
          <template v-else>
            <div
              class="fc-list-view fc-list-table-container  fc-table-td-height fc-table-viewchooser pB100 fc-popup-table-height"
            >
              <div
                class="pull-left table-header-actions"
                v-if="!$validation.isEmpty(selectedListItems)"
              >
                <div class="action-btn-slide btn-block">
                  <button
                    class="btn btn--tertiary pointer"
                    @click="showConfirmDelete()"
                  >
                    Delete
                  </button>
                </div>
              </div>
              <el-table
                :data="records"
                style="width: 100%;"
                height="100%"
                :fit="true"
                @selection-change="selectItems"
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
                  v-if="!$validation.isEmpty(nameColumn)"
                  :label="nameColumn.displayName"
                  :prop="nameColumn.name"
                  fixed
                  min-width="250"
                >
                  <template v-slot="item">
                    <div class="table-subheading">
                      <div class="d-flex">
                        <div v-if="item.row.photoId > 0">
                          <img
                            :src="getImage(item.row.photoId)"
                            class="img-container"
                          />
                        </div>
                        <div class="self-center mL5">
                          {{
                            getColumnDisplayValue(nameColumn, item.row) || '---'
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
                        $getProperty(field, 'name') === 'status' &&
                          moduleName === 'workorder'
                      "
                      class="table-subheading"
                      :class="{
                        'text-right':
                          $getProperty(field, 'field.dataTypeEnum') ===
                          'DECIMAL',
                      }"
                    >
                      {{ getTicketStatusDisplayName(scope.row) }}
                    </div>
                    <div
                      v-else-if="
                        $validation.isEmpty(field) &&
                          $validation.isEmpty(field.field) &&
                          field.field.displayTypeInt === 23
                      "
                      class="table-subheading"
                    >
                      {{ getDurationColumnValue(field, scope.row) }}
                    </div>
                    <div
                      v-else
                      @click="redirect(field, scope.row)"
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
                </el-table-column>
              </el-table>
            </div>
          </template>
        </template>
      </CommonListLayout>
    </div>
  </div>
</template>
<script>
import customModuleListView from 'pages/base-module-v2/ModuleList'
import ViewHeader from 'src/components/card-builder-utils/ViewHeader'
import { isEmpty, isNull } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'
import CommonListLayout from 'newapp/list/DeprecatedCommonLayout'

export default {
  extends: customModuleListView,
  props: ['showViewRearrange', 'viewname', 'filterJSON', 'targetDetail'],
  components: { ViewHeader, CommonListLayout },
  computed: {
    ...mapGetters(['getTicketStatus']),
    view() {
      let { viewname } = this
      return isEmpty(viewname) ? 'all' : viewname
    },
    filtersToRetain() {
      let { retainFilters } = this
      if (isNull(retainFilters)) {
        return []
      } else if (!isEmpty(retainFilters)) {
        return retainFilters
      } else {
        return ['search', 'includeParentFilter']
      }
    },
    filters() {
      if (this.filterJSON) {
        return this.filterJSON
      }
      return null
    },
  },
  methods: {
    redirectToOverview(row) {},
    getTicketStatusDisplayName(record) {
      return (
        this.getTicketStatus(
          this.$getProperty(record, 'moduleState.id', -1),
          this.moduleName
        ) || {}
      ).displayName
    },
    redirect(field, row) {
      // temp for operational visibility - should be removed
      let appNameFromUrl = window.location.pathname.slice(1).split('/')[0]
      if (
        appNameFromUrl === 'operations' &&
        field.field &&
        field.field.module &&
        field.field.module.name === 'custom_buildingperformance'
      ) {
        let userFilters = {}
        let buildingFieldName = null
        let timeFieldName = null
        let url = null
        switch (field.name) {
          case 'decimal':
            buildingFieldName = 1179738
            timeFieldName = 1179736
            url = '/operations/asset/assetperformance/assethealth'
            break
          case 'decimal_1':
            buildingFieldName = 1179702
            timeFieldName = 1179810
            url = '/operations/omv/visibility/omvisibility'
            break
          case 'decimal_2':
            buildingFieldName = 1179685
            timeFieldName = 1179760
            url = '/operations/tenants/satisfaction/tenantsatisfaction'
            break
          case 'decimal_3':
            buildingFieldName = 1179823
            timeFieldName = 1179822
            url = '/operations/sustainability/utilityspend/utilityspends'
            break
          case 'decimal_4':
            buildingFieldName = 1179868
            timeFieldName = 1179866
            url = '/operations/building/buildingperformance/buildingperformance'
            break
          default:
            break
        }
        if (buildingFieldName && timeFieldName && url) {
          if (this.filterJSON.picklist) {
            userFilters[timeFieldName] = [`${this.filterJSON.picklist.value}`]
          }
          if (row.data && row.data.building) {
            userFilters[buildingFieldName] = [`${row.data.building.id}`]
          }
          let dbFilters = { userFilters: userFilters }
          this.$router.push({
            path: `${url}?dbFilters=${encodeURIComponent(
              JSON.stringify(dbFilters)
            )}`,
          })
        }
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.img-container {
  width: 37px;
  height: 37px;
  border: 1px solid #f9f9f9;
  border-radius: 50%;
}
.list-view-widget {
  height: calc(100vh - 100px);
}
.custom-view-table-actions {
  float: right;
  margin-right: 20px;
}
.fc-popup-table-heigh {
  height: calc(100vh - 200px) !important;
}
</style>
