<template>
  <!-- eslint-disable-next-line vue/valid-template-root -->
  <CommonListLayout
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => moduleDisplayName"
    :hideSubHeader="canHideSubHeader"
    :recordCount="recordCount"
    :recordLoading="showLoading"
    pathPrefix="/app/sr/serviceRequest/"
    class="custom-module-list-layout"
  >
    <template #header>
      <AdvancedSearchWrapper
        v-if="!canHideFilter"
        :key="`ftags-list-${moduleName}`"
        :filters="filters"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>

      <template v-if="!showSearch">
        <visual-type
          v-if="canShowVisualSwitch"
          @onSwitchVisualize="val => (canShowListView = val)"
        ></visual-type>
        <div>
          <button
            v-if="$hasPermission(`${moduleName}:CREATE`)"
            class="fc-create-btn"
            @click="redirectToFormCreation()"
          >
            New Service Request
          </button>
        </div>
      </template>
    </template>

    <template v-if="canShowCalendarHeader" #sub-header>
      <CalendarDateWrapper v-if="!showListView" />
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(records) && showListView">
        <pagination
          :total="recordCount"
          :currentPageCount="currentPageCount"
          :perPage="perPage"
          :skipTotalCount="true"
          class="pL15 fc-black-small-txt-12"
        ></pagination>
        <span class="separator" v-if="recordCount > 0">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.sort')"
          placement="right"
        >
          <Sort
            :key="`${moduleName}-sort`"
            :moduleName="moduleName"
            @onSortChange="updateSort"
          ></Sort>
        </el-tooltip>
        <span class="separator">|</span>

        <template v-if="!isAltayerNonPrivilagedUser">
          <f-export-settings
            :module="moduleName"
            :viewDetail="viewDetail"
            :showViewScheduler="false"
            :showMail="false"
            :filters="filters"
          ></f-export-settings>
        </template>
      </template>
    </template>
    <template #content>
      <template>
        <div class="height100 pB10">
          <Spinner
            v-if="showLoading"
            class="mT40"
            :show="showLoading"
          ></Spinner>
          <div v-else-if="showListView" class="fc-card-popup-list-view">
            <div
              class="fc-list-view p10 pT0  fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
            >
              <div
                v-if="$validation.isEmpty(records)"
                class="fc-list-empty-state-container"
              >
                <inline-svg
                  src="svgs/list-empty"
                  iconClass="icon text-center icon-xxxxlg"
                ></inline-svg>
                <div class="q-item-label nowo-label">
                  {{ $t('home.service_request.service_request_no_data') }}
                </div>
              </div>
              <div v-else>
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
                <div
                  v-if="!$validation.isEmpty(selectedListItemsIds)"
                  class="pull-left table-header-actions"
                >
                  <div class="action-btn-slide btn-block">
                    <el-dropdown
                      @command="assignRecords"
                      trigger="click"
                      placement="bottom"
                    >
                      <button
                        :disabled="saving"
                        class="btn btn--tertiary"
                        v-if="$hasPermission(`${moduleName}:UPDATE`)"
                      >
                        <i class="fa fa-user-o b-icon"></i>
                        Assign
                      </button>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item
                          v-for="(user, index) in users"
                          :command="{ assignedTo: { id: user.id } }"
                          :key="`user-${index}`"
                        >
                          <i
                            class="fa fa-user secondary-color2 wo_assign-icon_user"
                          ></i>
                          {{ user.name }}
                        </el-dropdown-item>
                        <el-dropdown-item
                          v-for="(group, index) in groups"
                          :command="{ assignmentGroup: { id: group.id } }"
                          :key="`group-${index}`"
                        >
                          <i
                            class="fa fa-users secondary-color2 wo_assign-icon_user"
                          ></i>
                          {{ group.name }}
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                    <button
                      class="btn btn--tertiary"
                      @click="deleteRecords(selectedListItemsIds)"
                      :disabled="saving"
                      v-if="$hasPermission(`${moduleName}:DELETE`)"
                    >
                      <i class="el-icon-delete"></i>
                      Delete
                    </button>
                  </div>
                  <CustomButton
                    :key="`${moduleName}_${viewname}_${POSITION.LIST_BAR}`"
                    :modelDataClass="modelDataClass"
                    :moduleName="moduleName"
                    :position="POSITION.LIST_BAR"
                    class="custom-button"
                    @onSuccess="onCustomButtonSuccess"
                    @onError="() => {}"
                    :selectedRecords="selectedListItemsObj"
                  ></CustomButton>
                </div>
              </div>
              <CommonList
                :records="records"
                :viewDetail="viewDetail"
                :moduleName="moduleName"
                :redirectToOverview="openRecordSummary"
                :slotList="slotList"
                height="auto"
                :fit="true"
                @selection-change="selectItems"
                canShowCustomButton="true"
                :refreshList="onCustomButtonSuccess"
              >
                <template #[slotList[0].criteria]="{record}">
                  <router-link
                    :to="openRecordSummary(record.id)"
                    class="d-flex main-field-column label-txt-black"
                  >
                    <el-tooltip
                      effect="dark"
                      :content="getModeType(record)"
                      placement="top"
                      :open-delay="600"
                    >
                      <span :class="modeClass(record)" class="flex-shrink-0">
                      </span>
                    </el-tooltip>
                    <el-tooltip
                      effect="dark"
                      :content="$getProperty(record, 'subject', '---')"
                      placement="top"
                      :open-delay="600"
                    >
                      <div class="self-center width200px">
                        <span class="list-main-field">{{
                          $getProperty(record, 'subject', '---')
                        }}</span>
                      </div>
                    </el-tooltip>
                  </router-link>
                </template>
                <template #[slotList[1].criteria]="{record}">
                  <div class="p5">
                    {{ getTeamOrStaff(record) }}
                  </div>
                </template>

                <template #[slotList[2].name]="{record}">
                  <div class="text-center">
                    <i
                      v-if="$hasPermission(`${moduleName}:UPDATE`)"
                      class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                      data-arrow="true"
                      :title="$t('common._common.edit')"
                      @click="editRecord(record)"
                      v-tippy
                    ></i>
                    <i
                      v-if="$hasPermission(`${moduleName}:DELETE`)"
                      class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                      data-arrow="true"
                      :title="$t('common._common.delete')"
                      @click="deleteRecords([record.id])"
                      v-tippy
                    ></i>
                  </div>
                </template>

                <template #[slotList[3].name]="{record}">
                  <div class="fc-id">
                    {{ `# ${record.id || '---'}` }}
                  </div>
                </template>
                <template #[slotList[4].criteria]="{record}">
                  <span>
                    {{ getModeType(record) }}
                  </span>
                </template>
              </CommonList>
            </div>
          </div>
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
        </div>
      </template>
    </template>
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
import { API } from '@facilio/api'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import { findRouterForModuleInApp } from 'newapp/viewmanager/routeUtil'
import OtherMixin from '@/mixins/OtherMixin'
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'

const modeTypes = {
  1: 'New',
  2: 'Customer Replied',
  3: 'Admin Replied',
  '-1': 'Created',
}

export default {
  extends: CommonModuleList,
  mixins: [OtherMixin],
  name: 'ServiceRequestList',
  data() {
    return {
      modeTypes,
      saving: false,
    }
  },
  created() {
    this.$store.dispatch('view/loadModuleMeta', this.moduleName)
  },
  computed: {
    ...mapState({
      showSearch: state => state.search.active,
      users: state => state.users,
      groups: state => state.groups,
    }),
    nameColumn() {
      let { viewColumns } = this
      return (viewColumns || []).find(col => col.name === 'subject') || {}
    },
    moduleDisplayName() {
      return this.metaInfo?.displayName
    },
    slotList() {
      return [
        {
          criteria: JSON.stringify({ name: 'subject' }),
        },
        {
          criteria: JSON.stringify({ name: 'assignedTo' }),
        },
        {
          name: 'editDelete',
          isActionColumn: true,
          columnAttrs: {
            width: 130,
            class: 'visibility-visible-actions',
            fixed: 'right',
          },
        },
        {
          name: 'id',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 150,
            label: 'ID',
            fixed: 'left',
          },
        },
        {
          criteria: JSON.stringify({ name: 'mode' }),
        },
      ]
    },
  },
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
    modeClass(record) {
      const modeClass = {
        1: 'is-new',
        2: 'customer-replied',
        3: 'admin-replied',
        '-1': 'created',
      }
      let { mode } = record || {}

      if (mode) {
        return `service-request-mode-in-list-page ${modeClass[mode]}`
      }
    },
    async deleteRecords(idList) {
      let value = await this.$dialog.confirm({
        title: this.$t(`home.service_request.delete_service_request`),
        message: this.$t(
          `home.service_request.delete_service_request_confirmation`
        ),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })
      if (value) {
        this.saving = true

        let { moduleName } = this
        let { error } = await API.deleteRecord(moduleName, idList)

        if (error) {
          this.$message.error(error.message || 'Error Occurred')
        } else {
          this.$message.success(
            this.$t(`home.service_request.service_request_delete_success`)
          )
          await this.refreshRecordDetails()
          this.selectedListItemsIds = []
          this.selectedListItemsObj = []
        }
        this.saving = false
      }
    },
    openRecordSummary(id) {
      let { moduleName, viewname } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        let route = {
          name,
          params: {
            viewname,
            id,
          },
          query: this.$route.query,
        }

        return name && route
      } else {
        return {
          name: 'serviceRequestSummary',
          query: this.$route.query,
          params: {
            viewname,
            id,
          },
        }
      }
    },
    redirectToFormCreation() {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

        name && this.$router.push({ name })
      } else {
        this.$router.push({ path: `/app/sr/serviceRequest/new` })
      }
    },
    editRecord({ id }) {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        name && this.$router.push({ name, params: { id } })
      } else {
        this.$router.push({
          path: `/app/sr/serviceRequest/edit/${id}`,
        })
      }
    },
    getTeamOrStaff(record) {
      let { assignmentGroup, assignedTo } = record || {}
      let assignmentGroupName = this.$getProperty(
        assignmentGroup,
        'name',
        '---'
      )
      let assignedToName = this.$getProperty(assignedTo, 'name', '---')

      if (!isEmpty(assignmentGroup) && !isEmpty(assignedTo))
        return `${assignmentGroupName} / ${assignedToName}`
      else if (!isEmpty(assignmentGroup)) return `${assignmentGroupName}`
      else if (!isEmpty(assignedToName)) return `${assignedToName}`
      else return `---`
    },
    getModeType(record) {
      let { modeTypes } = this
      let { mode } = record || {}

      return this.$getProperty(modeTypes, `${mode}`, '---')
    },
    async assignRecords({ assignedTo = null, assignmentGroup = null }) {
      let { moduleName } = this
      let recordMap = this.selectedListItemsIds.map(id => {
        let record
        if (assignedTo) {
          record = { id, assignedTo }
        } else if (assignmentGroup) {
          record = { id, assignmentGroup }
        }
        return record
      })
      let params = {
        moduleName: moduleName,
        data: {
          [moduleName]: recordMap,
        },
      }
      this.saving = true
      let { error } = await API.post('v3/modules/data/bulkpatch', params)
      if (error) {
        this.$message.error(error?.message || 'Error occurred while assigning')
      } else {
        this.loadRecords()
        this.selectedListItemsIds = []
      }
      this.saving = false
    },
  },
}
</script>

<style lang="scss" scoped>
.sr-view-column-chooser {
  top: 35% !important;
  right: 30% !important;
}
.service-request-mode-in-list-page {
  margin: 8px 7px 0px 0px;
  height: 2px;
  width: 2px;
  padding: 4px;
  border-radius: 50%;
  margin-right: 7px;

  &.is-new {
    background-color: #6cbd85;
  }
  &.customer-replied {
    background-color: #7fa5ff;
  }
  &.admin-replied {
    background-color: #7fa5ff;
  }
  &.created {
    background-color: #6cbd85;
  }
}
</style>
