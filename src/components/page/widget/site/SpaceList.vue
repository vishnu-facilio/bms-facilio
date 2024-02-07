<template>
  <div class="related-list-container" ref="relatedListContainer">
    <div
      v-if="isLoading || isSearchDataLoading"
      class="loading-container d-flex justify-content-center height100"
    >
      <spinner :show="isLoading || isSearchDataLoading"></spinner>
    </div>
    <div v-else-if="$validation.isEmpty(modulesList)" class="height100">
      <ListNoData
        :iconPath="`svgs/spacemanagement/${moduleName}`"
        :moduleDisplayName="moduleDisplayName"
      ></ListNoData>
    </div>
    <div v-else class="fc-list-view fc-table-td-height mT1">
      <div class="view-column-chooser" @click="showColumnCustomization">
        <img
          src="~assets/column-setting.svg"
          class="column-customization-icon"
        />
      </div>
      <el-table
        :data="modulesList"
        class="width100"
        :height="tableHeight"
        row-class-name="space-row"
        :fit="true"
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
                  <fc-icon
                    v-if="newSiteSummary"
                    group="default"
                    name="workspace"
                    size="22"
                  ></fc-icon>
                  <InlineSvg
                    v-else
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
    <portal :to="portalName" :key="linkName + '-portalwrap'">
      <el-input
        :key="linkName + '-input'"
        ref="mainFieldSearchInput"
        v-if="showMainFieldSearch"
        class="fc-input-full-border2 width-auto mL-auto"
        suffix-icon="el-icon-search"
        v-model="searchText"
        autofocus
        @blur="hideMainFieldSearch"
        style="margin-top: -10px;"
      ></el-input>
      <span
        :key="linkName + '-sep1'"
        v-else
        class="self-center mL-auto"
        @click="openMainFieldSearch"
      >
        <inline-svg
          :key="linkName + '-search'"
          src="svgs/search"
          class="vertical-middle cursor-pointer"
          iconClass="icon icon-sm mT5 mR5 search-icon"
        ></inline-svg>
      </span>
      <span
        v-if="
          !$validation.isEmpty(totalCount) && !$validation.isEmpty(modulesList)
        "
        :key="linkName + '-sep2'"
        class="separator self-center"
        >|</span
      >
      <sort
        v-if="!$validation.isEmpty(modulesList) && linkName !== 'floor'"
        :key="linkName + '-sort'"
        :config="sortConfig"
        :sortList="sortConfigLists"
        @onchange="updateSort"
      ></sort>
      <span
        v-if="!$validation.isEmpty(modulesList) && linkName !== 'floor'"
        :key="linkName + '-sep3'"
        class="separator self-center"
        >|</span
      >
      <pagination
        :key="linkName + '-pagination'"
        :currentPage.sync="page"
        :total="totalCount"
        :perPage="perPage"
        class="self-center"
      ></pagination>
    </portal>
    <column-customization
      :visible.sync="canShowColumnCustomization"
      :moduleName="relatedListModuleName"
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
import { eventBus } from '@/page/widget/utils/eventBus'
import { isEmpty } from '@facilio/utils/validation'
import ListNoData from './ListNoData'
import ColumnCustomization from '@/ColumnCustomization'
import Constants from 'util/constant'
import { isWebTabsEnabled, findRouteForTab, tabTypes } from '@facilio/router'
import { API } from '@facilio/api'
import DeleteDialog from 'src/pages/spacemanagement/overview/components/DeleteDialog.vue'
import Sort from 'newapp/components/Sort'
import { mapState, mapActions } from 'vuex'
import SpaceMixin from 'pages/spacemanagement/overview/helpers/SpaceHelper'

export default {
  name: 'SpaceList',
  mixins: [SpaceMixin],
  extends: RelatedListWidget,
  props: [
    'module',
    'displayName',
    'details',
    'portalName',
    'parentModule',
    'linkName',
    'spaceFields',
  ],
  components: {
    ListNoData,
    ColumnCustomization,
    DeleteDialog,
    Sort,
  },
  data() {
    return {
      listComponentsMap: {},
      viewDetailsExcludedModules: [],
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
    }
  },
  created() {
    this.init()
    if (this.module === 'floor') {
      eventBus.$on('refreshRelatedFloorsList', () => {
        this.refreshRelatedList()
      })
    }
    if (this.module === 'space') {
      eventBus.$on('refreshRelatedSpaceList', () => {
        this.refreshRelatedList()
      })
    }
  },
  mounted() {
    this.loadFields()
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
    }),
    ...mapActions({
      loadViewDetail: 'view/loadViewDetail',
    }),
    isV3Api() {
      return true
    },
    moduleName() {
      return this.module
    },
    moduleDisplayName() {
      return this.displayName
    },
    isCustomModule() {
      return false
    },
    filters() {
      let {
        mainField,
        searchText,
        details,
        moduleName,
        linkName,
        parentModule,
      } = this
      let { id } = details
      let filters
      if (parentModule === 'building') {
        if (moduleName === 'floor') {
          filters = {
            building: {
              operatorId: 36,
              value: [`${id}`],
            },
          }
        } else if (linkName === 'independentspace') {
          filters = {
            building: {
              operatorId: 36,
              value: [`${id}`],
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
        } else if (linkName === 'allspace') {
          filters = {
            building: {
              operatorId: 36,
              value: [`${id}`],
            },
          }
        }
      } else if (parentModule === 'site') {
        if (linkName === 'allspace') {
          filters = {
            site: {
              operatorId: 36,
              value: [`${id}`],
            },
          }
        } else if (linkName === 'independentspace') {
          filters = {
            site: {
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
      }

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
        filters[name] = {
          operatorId,
          value,
        }
      }

      return filters
    },
    tableHeight() {
      let { $refs } = this
      let height = '250px'
      let tableContainer = $refs['relatedListContainer']
      if (!isEmpty(tableContainer)) {
        let containerHeight = (tableContainer || {}).scrollHeight
        height = `${containerHeight}px`
      }
      return height
    },
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
    closeDialog() {
      this.showDialog = false
    },
    editItem(data) {
      let { moduleName } = this
      if (moduleName === 'floor') {
        eventBus.$emit('openSpaceManagementForm', {
          visibility: true,
          module: 'floor',
          floorObj: data,
        })
      } else {
        eventBus.$emit('openSpaceManagementForm', {
          visibility: true,
          module: 'space',
          spaceobj: data,
          site: data.site,
          building: data.building,
          floor: data.floor,
          spaceParent: data.spaceParent,
        })
      }
    },
    showColumnCustomization() {
      let { moduleName, viewDetail } = this
      let url = `/module/meta?moduleName=${moduleName}`
      this.$http
        .get(url)
        .then(({ data }) => {
          if (!isEmpty(data)) {
            let { meta: metaInfo } = data
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
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let route = findRouteForTab(tabType, { config }) || {}

        if (!isEmpty(route)) {
          return this.$router.resolve({ name: route.name }).href
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
  },
}
</script>
