<template>
  <div class="height100" v-if="openRuleSummary">
    <div class="flex flex-row">
      <div class="cm-side-bar-container">
        <SummarySidebar
          :list="records"
          :isLoading.sync="isLoading"
          :activeRecordId="selectedRuleId"
          :total="recordCount"
          :currentCount="(records || []).length"
        >
          <template #title>
            <el-row class="cm-sidebar-header">
              <el-col :span="2">
                <span @click="openList()">
                  <inline-svg
                    @click="openList()"
                    src="svgs/arrow"
                    class="rotate-90 pointer"
                    iconClass="icon icon-sm"
                  ></inline-svg>
                </span>
              </el-col>

              <el-col :span="22">
                <div class="bold">{{ currentViewDetail }}</div>
              </el-col>
            </el-row>
          </template>
          <template v-slot="{ record }">
            <div
              class="sp-li ellipsis f12 pointer asset-item p20"
              @click="redirectToOverview(record.id)"
            >
              <el-row>
                <el-col :span="24">
                  <div
                    class="f14 truncate-text"
                    :title="record.name || '---'"
                    v-tippy="{
                      placement: 'top',
                      animation: 'shift-away',
                      arrow: true,
                    }"
                  >
                    {{ record.name || '---' }}
                  </div>
                </el-col>
              </el-row>
            </div>
          </template>
        </SummarySidebar>
      </div>
      <div class="flex-1">
        <router-view :viewname="viewname"></router-view>
      </div>
    </div>
  </div>
  <CommonListLayout
    v-else
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => moduleDisplayName"
    pathPrefix="/app/fa/newrules"
    :key="moduleName + '-list-layout'"
    :hideSubHeader="canHideSubHeader"
    :recordCount="recordCount"
    :recordLoading="showLoading"
  >
    <template #header>
      <AdvancedSearchWrapper
        v-if="!canHideFilter"
        :key="`ftags-list-${moduleName}`"
        :filters="filters"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>
      <visual-type
        v-if="canShowVisualSwitch"
        @onSwitchVisualize="val => (canShowListView = val)"
      ></visual-type>
      <button
        v-if="$hasPermission('alarmrules:CREATE')"
        class="fc-create-btn "
        @click="createNewRule()"
      >
        {{ $t('common.products.new_rule') }}
      </button>
      <!--Template page is not yet supported for newRule-->
      <!-- <el-dropdown @command="handleNewRule" class="new-rule-btn">
        <button
          v-if="$hasPermission('alarmrules:CREATE')"
          class="fc-create-btn create-btn mL20"
        >
          {{ $t('common.products.new_rule') }}
          <i class="el-icon-arrow-down pL10 fc-white-14 bold"></i>
        </button>
        <el-dropdown-menu slot="dropdown" class="m30">
          <el-dropdown-item command="library">{{
            $t('common.products.add_from_library')
          }}</el-dropdown-item>
          <el-dropdown-item command="custom">{{
            $t('rule.create.new_alarm_rule')
          }}</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown> -->
    </template>

    <template v-if="canShowCalendarHeader" #sub-header>
      <CalendarDateWrapper v-if="!showListView" />
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(records) && showListView">
        <Pagination
          :total="recordCount"
          :perPage="perPage"
          :skipTotalCount="true"
          class="mT2"
        ></Pagination>
      </template>
    </template>
    <template #content>
      <Spinner v-if="showLoading" class="mT40" :show="showLoading"></Spinner>
      <template v-else-if="showListView">
        <div class="new-rules-list-container">
          <div
            v-if="$validation.isEmpty(records) && !isLoading"
            class="fc-list-empty-state-container"
          >
            <inline-svg
              src="svgs/list-empty"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="mT10 fc-black-dark f16 fw6">
              <div class="mT10 label-txt-black f14">
                {{ $t('setup.users_management.no_rules_available') }}
              </div>
            </div>
          </div>
          <div v-else class="height100">
            <div
              class="column-customization-icon"
              :disabled="!isColumnCustomizable"
              @click="toShowColumnSettings"
            >
              <el-tooltip
                :disabled="isColumnCustomizable"
                placement="top"
                :content="$t('common._common.you_dont_have_permission')"
              >
                <inline-svg
                  src="column-setting"
                  class="text-center position-absolute icon"
                />
              </el-tooltip>
            </div>
            <CommonList
              :viewDetail="viewDetail"
              :records="records"
              hideListSelect="true"
              :moduleName="moduleName"
              :slotList="slotList"
              :redirectToOverview="redirectToOverview"
            >
              <template #[slotList[0].name]="{record}">
                <div class="d-flex">
                  <div class="fc-id">{{ '#' + record.id }}</div>
                </div>
              </template>
              <template #[slotList[1].criteria]="{record}">
                <div
                  @click="redirectToOverview(record.id)"
                  class="d-flex main-field-column label-txt-black"
                >
                  <el-tooltip
                    effect="dark"
                    :content="record.name || '---'"
                    placement="top"
                    :open-delay="600"
                  >
                    <div class="self-center truncate-text mL5">
                      {{ record.name || '---' }}
                    </div>
                  </el-tooltip>
                </div>
              </template>
              <template #[slotList[2].name]="{record}">
                <div class="table-subheading">
                  {{ getSpaceOrAsset(record) }}
                </div>
              </template>
              <template #[slotList[3].criteria]="{record}">
                <div class="d-flex">
                  <div class="self-center mL5">
                    {{ getCategoryName(record) || 'Space' }}
                  </div>
                </div>
              </template>
              <template #[slotList[4].name]="{record}">
                <div class="d-flex">
                  <div class="self-center mL5">
                    {{ getResourceName(record) }}
                  </div>
                </div>
              </template>
              <template #[slotList[5].name]="{record}">
                <div>
                  <i
                    class="fa fa-circle prioritytag"
                    v-if="record.alarmDetails.severityId"
                    v-bind:style="{
                      color: (getSeverity(record) || {}).color,
                    }"
                    aria-hidden="true"
                  ></i>
                  {{ (getSeverity(record) || {}).displayName || '---' }}
                </div>
              </template>
              <template
                #[slotList[6].criteria]="{record}"
                v-if="
                  $hasPermission('alarmrules:UPDATE,UPDATE_TEAM,UPDATE_OWN')
                "
              >
                <div class="d-flex">
                  <div class="self-center mL5">
                    <el-switch
                      v-model="record.status"
                      @change="updateStatus(record)"
                    ></el-switch>
                  </div>
                </div>
              </template>
              <template #[slotList[7].criteria]="{ record }">
                <div>
                  <span class="q-item-label f13 letter-spacing0_4">
                    {{ getReadingFieldName(record) }}
                  </span>
                </div>
              </template>
              <template #[slotList[8].criteria]="{ record }">
                <div>
                  <span class="q-item-label f13 letter-spacing0_4">
                    {{ getReadingModuleName(record) }}
                  </span>
                </div>
              </template>
              <template #[slotList[9].name]="{record}">
                <div class="d-flex text-center">
                  <i
                    v-if="
                      $hasPermission('alarmrules:UPDATE,UPDATE_TEAM,UPDATE_OWN')
                    "
                    class="el-icon-edit pointer edit-icon-color visibility-hide-actions"
                    data-arrow="true"
                    :title="$t('rule.create.edit_rule')"
                    @click="editRule(record)"
                    v-tippy
                  ></i>
                  <i
                    v-if="$hasPermission('alarmrules:DELETE')"
                    class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                    data-arrow="true"
                    :title="$t('rule.create.delete_rule')"
                    @click="deleteRule([record.id])"
                    v-tippy
                  ></i>
                </div>
              </template>
            </CommonList>
          </div>
        </div>
      </template>
      <CalendarView
        v-else-if="!showListView"
        ref="calendar"
        :moduleName="moduleName"
        :record="records"
        :viewDetail="viewDetail"
        :viewname="viewname"
        :filters="filters"
      ></CalendarView>
      <column-customization
        :visible.sync="showColumnSettings"
        :moduleName="moduleName"
        :viewName="viewname"
      ></column-customization>
    </template>
    <portal to="view-manager-link">
      <router-link
        tag="div"
        :to="{
          path: `/app/fa/newrules/newreadingrules/viewmanager`,
        }"
        class="view-manager-btn"
      >
        <inline-svg
          src="svgs/hamburger-menu"
          class="d-flex"
          iconClass="icon icon-sm"
        ></inline-svg>
        <span class="label mL10 text-uppercase">
          {{ $t('viewsmanager.list.views_manager') }}
        </span>
      </router-link>
    </portal>
  </CommonListLayout>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import CommonListLayout from 'newapp/list/CommonLayout'
import SummarySidebar from 'newapp/components/SummarySideBar'
import CommonList from 'newapp/list/CommonList'
import { API } from '@facilio/api'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData.js'
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: CommonModuleList,
  components: {
    Spinner,
    CommonList,
    CommonListLayout,
    SummarySidebar,
  },
  data() {
    return {
      showColumnSettings: false,
      selectedListItemsIds: [],
      selectedListItemsObj: [],
      sortConfig: {
        orderBy: {
          label: 'System Created Time',
          value: 'sysCreatedTime',
        },
        orderType: 'desc',
      },
      sortConfigLists: [],
      records: null,
      rulesList: [],
      perPage: 50,
      recordCount: null,
    }
  },
  created() {
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    ...mapState({
      assetCategoryList: state => state.assetCategory,
      viewDetail: state => state.view.currentViewDetail,
    }),
    ...mapGetters(['getAlarmSeverity']),
    currentViewDetail() {
      let { viewDetail, filters } = this
      if (!isEmpty(filters)) {
        return this.$t('custommodules.list.filtered_view')
      } else {
        return viewDetail.displayName
      }
    },
    moduleName() {
      return 'newreadingrules'
    },
    moduleDisplayName() {
      return 'Rules'
    },
    modelDataClass() {
      return CustomModuleData
    },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    slotList() {
      return [
        {
          name: 'id',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 120,
            fixed: 'left',
            label: 'ID',
          },
        }, //0
        {
          criteria: JSON.stringify({ name: 'name' }),
        }, //1
        {
          name: 'type',
          isHardcodedColumn: true,
          columnAttrs: {
            width: 200,
            label: 'TYPE',
          },
        }, //2
        {
          criteria: JSON.stringify({ name: 'assetCategory' }),
          columnAttrs: {
            label: 'CATEGORY',
          },
        }, //3
        {
          name: 'space/asset',
          isHardcodedColumn: true,
          columnAttrs: {
            width: 250,
            'max-width': 300,
            label: 'SPACE/ASSET',
          },
        }, //4
        {
          name: 'severity',
          isHardcodedColumn: true,
          columnAttrs: {
            width: 250,
            'max-width': 250,
            label: 'SEVERITY',
          },
        }, //5
        {
          criteria: JSON.stringify({ name: 'status' }),
        }, //6
        {
          criteria: JSON.stringify({ name: 'readingFieldId' }),
          columnAttrs: {
            label: 'READING FIELD',
          },
        }, //7
        {
          criteria: JSON.stringify({ name: 'readingModuleId' }),
          columnAttrs: {
            label: 'READING MODULE',
          },
        }, //8
        {
          name: 'editDelete',
          isActionColumn: true,
          columnAttrs: {
            width: 130,
            class: 'visibility-visible-actions',
            fixed: 'right',
          },
        }, //9
      ]
    },
    selectedRuleId() {
      let id = this.$getProperty(this, '$route.params.id')
      if (id) {
        return parseInt(id)
      }
      return -1
    },
    openRuleSummary() {
      return !isEmpty(this.selectedRuleId)
    },
    page() {
      return this.$route.query.page || 1
    },
    isV3Api() {
      return true
    },
    viewname() {
      return this.$route.params.viewname
    },
  },
  methods: {
    async updateStatus(record) {
      let { id } = record || {}
      let url = 'v3/modules/data/patch'
      let params = { data: record, moduleName: this.moduleName, id: id }
      let { error, data } = await API.post(url, params)

      if (error) {
        this.$message.error('Error Occurred')
      } else {
        if (!isEmpty(data)) {
          this.$message.success('Status updated successfully')
        }
      }
    },
    openList() {
      let { viewname, $route } = this || {}
      let { query } = $route || {}

      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            viewname: viewname,
            query,
          })
      } else {
        this.$router.push({
          name: 'newrules',
          params: { viewname },
          query,
        })
      }
    },
    redirectToOverview(id) {
      let { viewname, $route } = this
      let { query } = $route || {}
      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: { id, viewname },
            query,
          })
      } else {
        this.$router.push({
          name: 'newRulesSummary',
          params: { id, viewname },
          query,
        })
      }
    },
    getCategoryName(record) {
      let { assetCategory } = record || {}
      let { id } = assetCategory || {}
      if (id > 0 && this.assetCategoryList) {
        let category = this.assetCategoryList.find(
          category => category.id === id
        )
        if (category) {
          return category.displayName
        }
      }
    },
    getReadingFieldName(record) {
      return this.$getProperty(record, 'readingFieldName', '---')
    },
    getReadingModuleName(record) {
      return this.$getProperty(record, 'readingModuleName', '---')
    },
    getSpaceOrAsset(record) {
      let { assetCategory } = record || {}
      let { id } = assetCategory || {}
      return id > 0 ? 'Asset' : 'Space'
    },
    getResourceName(rule) {
      let { assetCategory, assets, excludedResources } = rule || {}
      let { id } = assetCategory || {}
      if (id > 0) {
        let message
        let isIncluded = !isEmpty(assets)
        let selectedCount
        if (isIncluded) {
          selectedCount = assets.length
        } else if (excludedResources && excludedResources.length) {
          selectedCount = excludedResources.length
        }
        let categoryName = this.getCategoryName(rule)
        if (selectedCount) {
          message =
            (isIncluded ? selectedCount : 'Some') +
            ' ' +
            categoryName +
            (!isIncluded || selectedCount > 1 ? 's' : '')
        } else {
          message = 'All ' + categoryName + 's'
        }
        return message
      } else if (rule.resourceId > 0) {
        return rule.matchedResources
          ? rule.matchedResources[Object.keys(rule.matchedResources)[0]].name
          : '--- '
      }
      return '---'
    },
    createNewRule() {
      let { moduleName, viewname } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}
        name && this.$router.push({ name, params: { viewname } })
      } else {
        this.$router.push({
          name: 'rule-creation-new',
          params: { viewname },
          query: this.$route.query,
        })
      }
    },
    // Template page is not yet supported for newRule
    // handleNewRule(option) {
    //   if (option == 'library') {
    //     let url = '/app/fa/rules/templates'
    //     this.$router.push({ path: url })
    //   }
    //   if (option == 'custom') {
    //     this.$router.push({ path: `/app/fa/newrule/new` })
    //   }
    // },
    getSeverity(record) {
      let severityId = this.$getProperty(record, 'alarmDetails.severityId', -1)
      return this.getAlarmSeverity(severityId)
    },
    editRule(record) {
      let { id } = record || {}
      let { viewname, moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        name && this.$router.push({ name, params: { viewname, id } })
      } else {
        this.$router.push({
          name: 'new-rule-edit',
          params: { viewname, id },
          query: this.$route.query,
        })
      }
    },
    async deleteRule(id) {
      let { moduleName, moduleDisplayName } = this
      let value = await this.$dialog.confirm({
        title: `${this.$t('custommodules.list.delete')} ${moduleDisplayName}`,
        message: `${this.$t(
          'custommodules.list.delete_confirmation'
        )} ${moduleDisplayName}?`,
        rbDanger: true,
        rbLabel: this.$t('custommodules.list.delete'),
      })
      if (value) {
        this.isLoading = true

        try {
          await this.modelDataClass.delete(moduleName, id)
          this.$message.success(
            `${moduleDisplayName} ${this.$t(
              'custommodules.list.delete_success'
            )}`
          )
          await this.loadRecords()
        } catch (errorMsg) {
          this.$message.error(errorMsg)
        }
        this.isLoading = false
      }
    },
    closeEditForm(close) {
      if (close) {
        this.canShowEditForm = false
      }
    },
  },
}
</script>
<style scoped lang="scss">
.column-customization-icon {
  position: absolute;
  right: 11px;
  display: block;
  width: 45px;
  height: 50px;
  cursor: pointer;
  text-align: center;
  background-color: #ffffff;
  border-left: 1px solid #f2f5f6;
  z-index: 20;
  .icon {
    top: 35%;
    right: 29%;
  }
  margin-top: 2px;
}
.new-rules-list-container {
  border-width: 0px !important;
  border-style: solid;
  padding: 0px 10px 70px 10px;
  height: calc(100vh - 100px) !important;
}
.cm-side-bar-container {
  flex: 0 0 380px;
  max-width: 380px;
  background: white;
  position: relative;
  height: 100vh;
  border-right: 1px solid #ececec;
  border-left: 1px solid #ececec;
}
.cm-sidebar-header {
  padding: 20px 15px;
  border-bottom: 1px solid #f2f2f2;
}
.cm-sidebar-list-item {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  padding: 20px;
  font-size: 12px;
  cursor: pointer;
}
.summary-item-heading {
  width: 280px;
  text-overflow: ellipsis;
  overflow: hidden;
  padding-top: 20px;
  padding-left: 20px;
  font-size: 12px;
  cursor: pointer;
}
.summary-main-div {
  display: flex;
  justify-content: space-between;
}
.new-rules-list {
  display: flex;
  width: 100%;
  height: 100%;
  overflow: hidden;
}
.list-container {
  margin: 10px;
  flex-grow: 1;
  overflow-x: scroll;
}
.rules-summary-bar {
  flex: 0 0 320px;
  max-width: 320px;
}
</style>
<style lang="scss">
.new-rules-list-container {
  .el-table td {
    padding: 10px 20px;
  }
  .el-table th.is-leaf {
    padding: 15px 20px;
  }
  .el-table th > .cell {
    font-size: 11px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    color: #333;
    white-space: nowrap;
    padding-left: 0;
    padding-right: 0;
  }
  .hover-actions {
    visibility: hidden;
  }
  .el-table__body tr.hover-row > td .hover-actions {
    visibility: visible;
  }
}
</style>
