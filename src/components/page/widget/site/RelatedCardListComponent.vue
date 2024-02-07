<template>
  <div
    :class="
      `site-buildings-main pT0 ${
        type === 'card' && !$validation.isEmpty(modulesList)
          ? 'building-card-component'
          : 'building-list-component'
      }`
    "
  >
    <portal :to="widget.key + '-title-section'">
      <div
        class="flex-middle justify-content-space space-transparent-header mB10"
      >
        <div class="fc-grey2 uppercase f11 fwBold">
          {{ `${moduleHeaderName}` }}
        </div>
        <div class="flex-middle">
          <el-input
            ref="mainFieldSearchInput"
            v-if="showMainFieldSearch"
            class="fc-input-full-border2 width-auto mL-auto"
            suffix-icon="el-icon-search"
            v-model="searchText"
            autofocus
            @blur="hideMainFieldSearch"
            style="margin-top: -10px;"
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
            >|</span
          >
          <sort
            :key="'space-sort'"
            :config="sortConfig"
            :sortList="sortConfigLists"
            @onchange="updateSort"
          ></sort>
          <span
            v-if="
              !$validation.isEmpty(totalCount) &&
                !$validation.isEmpty(modulesList)
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
          <span v-if="showWidgetTypeSwitch" class="separator self-center"
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
          </div>
          <span
            v-if="$hasPermission('space:CREATE')"
            class="separator self-center"
            >|</span
          >
          <div
            v-if="$hasPermission('space:CREATE')"
            @click="openNewForm"
            class="fc-pink f13 bold pointer"
          >
            <i class="el-icon-plus pR5"></i>
            {{
              moduleName === 'building'
                ? $t('space.sites.newbuilding')
                : $t('space.sites.new_space')
            }}
          </div>
        </div>
      </div>
    </portal>
    <div ref="component-container">
      <div
        v-if="isLoading || isSearchDataLoading"
        class="loading-container d-flex justify-content-center"
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
          :height="calculateTableHeight() || tableHeight"
          class="related-list-widget-table"
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
                  <div v-if="item.row.avatarUrl">
                    <img :src="item.row.avatarUrl" class="img-container" />
                  </div>
                  <div v-else>
                    <InlineSvg
                      :src="`svgs/spacemanagement/${moduleName}`"
                      class="width100"
                      iconClass="icon icon-xlg"
                    ></InlineSvg>
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
            :align="
              (field.field || {}).dataTypeEnum === 'DECIMAL' ? 'right' : 'left'
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
                  'text-right': (field.field || {}).dataTypeEnum === 'DECIMAL',
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
      @onClose="closeDialog()"
      :id="deletingRecordId"
      :type="errorType"
      @refresh="refreshRelatedList()"
    >
    </DeleteDialog>
  </div>
</template>
<script>
import RelatedListWidget from '@/page/widget/common/RelatedListWidget'
import Constants from 'util/constant'
import BuildingCards from './BuildingCards'
import SpaceCards from './SpaceCards'
import { eventBus } from '@/page/widget/utils/eventBus'
import { isEmpty } from '@facilio/utils/validation'
import ListNoData from './ListNoData'
import ColumnCustomization from '@/ColumnCustomization'
import { isWebTabsEnabled, findRouteForTab, tabTypes } from '@facilio/router'
import { API } from '@facilio/api'
import DeleteDialog from 'src/pages/spacemanagement/overview/components/DeleteDialog.vue'
import { mapState, mapActions } from 'vuex'
import Sort from 'newapp/components/Sort'

const customModulesColumnConfig = {
  fixedColumns: ['name'],
  fixedSelectableColumns: ['photo'],
}
export default {
  name: 'RelatedCardListComponent',
  props: [
    'details',
    'widget',
    'resizeWidget',
    'calculateDimensions',
    'layoutParams',
  ],
  extends: RelatedListWidget,
  components: {
    BuildingCards,
    ListNoData,
    SpaceCards,
    ColumnCustomization,
    DeleteDialog,
    Sort,
  },
  data() {
    return {
      type: 'list',
      componentMap: {
        building: {
          list: 'BuildingList',
          card: 'BuildingCards',
        },
        space: {
          card: 'SpaceCards',
        },
      },
      viewDetailsExcludedModules: [],
      listComponentsMap: {},
      relatedListColumnConfig: null,
      relatedListModuleName: null,
      relatedViewDetail: null,
      relatedMetaInfo: null,
      relatedViewName: null,
      canShowColumnCustomization: false,
      showDialog: false,
      deletingRecordId: null,
      errorMap: null,
      errorType: null,
      sortConfig: {
        orderBy: {
          label: 'System Created Time',
          value: 'sysCreatedTime',
        },
        orderType: 'desc',
      },
      sortConfigLists: [],
      tableHeight: '550px',
    }
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
    }),
    ...mapActions({
      loadViewDetail: 'view/loadViewDetail',
    }),
    parentModuleName() {
      return this.$attrs.moduleName
    },
    moduleName() {
      let { widget } = this
      let { relatedList } = widget || {}
      let { module } = relatedList || {}
      let { name } = module || {}
      return name || ''
    },
    moduleDisplayName() {
      let { widget } = this
      let { relatedList } = widget || {}
      let { module } = relatedList || {}
      let { displayName } = module || {}
      return displayName || ''
    },
    moduleHeaderName() {
      return this.moduleDisplayName + 's'
    },
    filters() {
      let {
        mainField,
        searchText,
        details = {},
        moduleName,
        parentModuleName,
      } = this
      let { id } = details
      let filter
      if (parentModuleName === 'site') {
        if (moduleName === 'building') {
          filter = {
            siteId: {
              operatorId: 36,
              value: [`${id}`],
            },
          }
        } else if (moduleName === 'space') {
          filter = {
            siteId: {
              operatorId: 36,
              value: [`${id}`],
            },
            building: {
              operatorId: 1,
            },
            floor: {
              operatorId: 1,
            },
            space1: {
              operatorId: 1,
            },
            space2: {
              operatorId: 1,
            },
            space3: {
              operatorId: 1,
            },
          }
        }
      } else if (parentModuleName === 'floor') {
        if (moduleName === 'space') {
          filter = {
            floor: {
              operatorId: 36,
              value: [`${id}`],
            },
            space1: {
              operatorId: 1,
            },
            space2: {
              operatorId: 1,
            },
            space3: {
              operatorId: 1,
            },
          }
        }
      } else if (parentModuleName === 'space') {
        if (moduleName === 'space') {
          if (details.spaceId1 < 0) {
            filter = {
              space1: {
                operatorId: 36,
                value: [`${id}`],
              },
              space2: {
                operatorId: 1,
              },
              space3: {
                operatorId: 1,
              },
            }
          } else if (details.spaceId2 < 0) {
            filter = {
              space2: {
                operatorId: 36,
                value: [`${id}`],
              },
              space3: {
                operatorId: 1,
              },
            }
          } else {
            filter = {
              space3: {
                operatorId: 36,
                value: [`${id}`],
              },
            }
          }
        }
      }

      if (
        !isEmpty(mainField) &&
        !isEmpty(searchText) &&
        searchText.length > 0
      ) {
        let { name, field } = mainField
        let { dataTypeEnum } = field
        let value = [searchText]
        let operatorId = Constants.FILTER_OPERATORID_HASH[dataTypeEnum]
        filter[name] = {
          operatorId,
          value,
        }
      }

      return filter
    },
    showWidgetTypeSwitch() {
      let { totalCount, searchText } = this
      return !isEmpty(searchText) || totalCount > 3
    },
  },
  created() {
    this.init()
    eventBus.$on('autoResizeContainer', moduleName => {
      if (moduleName === this.moduleName) {
        this.autoResize()
      }
    })
    if (this.moduleName === 'building') {
      eventBus.$on('refreshRelatedBuildingsList', () => {
        this.refreshRelatedList()
      })
    }
    if (this.moduleName === 'space') {
      eventBus.$on('refreshRelatedSpaceList', () => {
        this.refreshRelatedList()
      })
    }
  },
  mounted() {
    this.loadFields()
  },
  methods: {
    getViewDetails() {
      this.$store.dispatch('view/loadViewDetail', {
        viewName: 'hidden-all',
        moduleName: this.moduleName,
      })
    },
    loadFields() {
      let { moduleName } = this
      this.$util.loadFields(moduleName, false).then(fields => {
        this.sortConfigLists = fields.map(field => field.name)
      })
    },
    async updateSort(sorting) {
      let { viewDetail } = this
      if (isEmpty(viewDetail.id)) {
        let fields = viewDetail.fields

        await this.$store.dispatch('view/customizeColumns', {
          moduleName: this.moduleName,
          viewName: 'hidden-all',
          fields: fields,
        })
        this.saveSorting(sorting)
      } else {
        this.saveSorting(sorting)
      }
    },
    saveSorting(sorting) {
      let { moduleName } = this
      let sortObj = {
        moduleName,
        viewName: 'hidden-all',
        orderBy: sorting.orderBy,
        orderType: sorting.orderType,
        skipDispatch: true,
      }
      this.$store
        .dispatch('view/savesorting', sortObj)
        .then(() => this.refreshRelatedList())
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs[`component-container`]
        if (!container) return

        let height = container.scrollHeight
        if (this.type === 'list') {
          height += 60
        } else {
          height += 30
        }
        let width = container.scrollWidth

        let { h } = this.calculateDimensions({ height, width })
        let params = {}
        if (h <= 0) {
          params = { height, width }
        } else if (this.type === 'list' && h < 7) {
          params = { h: 7 }
        } else {
          params = { h: h }
        }
        this.resizeWidget(params)
      })
    },
    closeDialog() {
      this.showDialog = false
    },
    openNewForm() {
      let { moduleName, parentModuleName } = this
      if (parentModuleName === 'site') {
        if (moduleName === 'building') {
          eventBus.$emit('openSpaceManagementForm', {
            isNew: true,
            visibility: true,
            module: 'building',
            site: this.details,
          })
        } else if (moduleName === 'space') {
          eventBus.$emit('openSpaceManagementForm', {
            isNew: true,
            visibility: true,
            module: 'space',
            site: this.details,
          })
        }
      } else if (parentModuleName === 'floor' && moduleName === 'space') {
        eventBus.$emit('openSpaceManagementForm', {
          isNew: true,
          visibility: true,
          module: 'space',
          floor: this.details,
        })
      } else if (parentModuleName === 'space' && moduleName === 'space') {
        eventBus.$emit('openSpaceManagementForm', {
          isNew: true,
          visibility: true,
          module: 'space',
          spaceParent: this.details,
        })
      }
    },
    editItem(data) {
      let { moduleName } = this
      if (moduleName === 'building') {
        eventBus.$emit('openSpaceManagementForm', {
          visibility: true,
          module: 'building',
          buildingObj: data,
        })
      } else if (moduleName === 'space') {
        let spaceParent = data.spaceParent
          ? data.spaceParent
          : data.space4
          ? data.space4
          : data.space3
          ? data.space3
          : data.space2
          ? data.space2
          : data.space1
          ? data.space1
          : {}
        eventBus.$emit('openSpaceManagementForm', {
          visibility: true,
          module: 'space',
          spaceobj: data,
          floor: data.floor,
          spaceParent: spaceParent,
        })
      }
    },
    async invokeDeleteDialog(moduleData) {
      let { id } = moduleData
      let { moduleName } = this

      let messageString = `space.sites.delete_${moduleName}_msg`

      let { error } = await API.fetchRecord(
        this.moduleName,
        {
          id,
          fetchChildCount: true,
        },
        { force: true }
      )

      if (!error) {
        let value = await this.$dialog.confirm({
          title: this.$t(`space.sites.delete_${moduleName}`),
          message: this.$t(messageString),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })

        if (value) {
          let { error } = await API.deleteRecord(moduleName, [id])
          if (!error) {
            this.$message.success(this.$t('space.sites.delete_success'))
            this.refreshRelatedList()
            eventBus.$emit('reloadTree')
          } else {
            this.$message.error(error)
          }
        }
      } else {
        this.deletingRecordId = id
        let map = JSON.parse(error.message)
        this.errorMap = map
        this.errorType = error.code
        this.showDialog = true
      }
    },
    calculateTableHeight() {
      this.$nextTick(() => {
        let { $refs } = this
        let height = '250px'
        let tableContainer = $refs['related-table-list']
        if (!isEmpty(tableContainer)) {
          let containerHeight = (tableContainer || {}).scrollHeight - 90
          height = `${containerHeight}px`
        }
        this.autoResize()
        return height
      })
    },
    findRoute() {
      if (isWebTabsEnabled()) {
        let { $router } = this
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let route = findRouteForTab(tabType, { config }) || {}

        if (!isEmpty(route)) {
          return $router.resolve({ name: route.name }).href
        } else {
          return null
        }
      } else {
        return '/app/home/portfolio'
      }
    },
    routeToSummary(record) {
      let { siteid } = this.$route.params
      let { id } = record
      let parentPath = this.findRoute()

      if (parentPath && siteid) {
        if (this.moduleName === 'building') {
          this.$router.push({
            path: `${parentPath}/site/${siteid}/building/${id}`,
          })
        } else if (this.moduleName === 'floor') {
          this.$router.push({
            path: `${parentPath}/site/${siteid}/floor/${id}`,
          })
        } else if (this.moduleName === 'space') {
          this.$router.push({
            path: `${parentPath}/site/${siteid}/space/${id}`,
          })
        }
      }
    },
    showColumnCustomization() {
      let { moduleName, viewDetail } = this
      let url = `/module/meta?moduleName=${moduleName}`
      let columnConfig = customModulesColumnConfig
      this.$http
        .get(url)
        .then(({ data }) => {
          if (!isEmpty(data)) {
            let { meta: metaInfo } = data
            this.$set(this, 'relatedListColumnConfig', columnConfig)
            this.$set(this, 'relatedListModuleName', moduleName)
            this.$set(this, 'relatedViewDetail', viewDetail)
            this.$set(this, 'relatedMetaInfo', metaInfo)
            this.$set(this, 'relatedViewName', 'hidden-all')
            this.$set(this, 'canShowColumnCustomization', true)
          }
        })
        .catch(errMsg => {
          this.$message.error(errMsg)
        })
    },
  },
}
</script>
