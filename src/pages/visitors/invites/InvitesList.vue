<template>
  <CommonListLayout
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => 'Invite'"
    :hideSubHeader="isEmpty(invitesList)"
    :recordCount="listCount"
    :recordLoading="showLoading"
    pathPrefix="/app/vi/invites/"
  >
    <template #header>
      <AdvancedSearchWrapper
        :key="`${moduleName}-search`"
        :moduleName="moduleName"
        moduleDisplayName="Invites"
      ></AdvancedSearchWrapper>

      <template>
        <div v-if="enable()">
          <button
            v-if="$hasPermission(`${moduleName}:CREATE`)"
            class="fc-create-btn"
            @click="openInviteForm('single')"
          >
            {{ $t('common.products.new_invite') }}
          </button>
        </div>
        <div v-else>
          <el-dropdown
            v-if="$hasPermission(`${moduleName}:CREATE`)"
            trigger="click"
            @command="openInviteForm"
          >
            <button class="fc-create-btn create-btn">
              {{ $t('common.products.new_invite') }}
              <i class="el-icon-arrow-down el-icon--right"></i>
            </button>

            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="single">
                {{ $t('common.header.single_invite') }}
              </el-dropdown-item>
              <el-dropdown-item command="bulk">
                {{ $t('common.header.group_invite') }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </template>
    </template>
    <template #sub-header-actions>
      <template v-if="!isEmpty(invitesList)">
        <pagination
          :total="listCount"
          :perPage="50"
          :skipTotalCount="true"
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
            :sortList="sortConfigList"
            @onchange="updateSort"
          ></sort>
        </el-tooltip>
        <span class="separator">|</span>
        <template v-if="!isAltayerNonPrivilagedUser">
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
    </template>
    <div class="fc-card-popup-list-view ">
      <div
        class="fc-list-view p10 pT0  fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
      >
        <spinner v-if="showLoading" class="mT40" :show="showLoading"></spinner>

        <div
          v-else-if="$validation.isEmpty(invitesList)"
          class="fc-list-empty-state-container"
        >
          <inline-svg
            src="svgs/list-empty"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="mT10 fc-black-dark f16 fw6">
            {{ $t('home.visitor.visitor_invites_no_data') }}
          </div>
        </div>

        <div v-else-if="!$validation.isEmpty(invitesList)">
          <div class="view-column-chooser" @click="showColumnSettings = true">
            <img
              src="~assets/column-setting.svg"
              style="text-align: center; position: absolute; top: 35%;right: 29%;"
            />
          </div>

          <el-table
            :data="invitesList"
            ref="tableList"
            class="width100"
            height="auto"
            :fit="true"
          >
            <el-table-column
              fixed
              :label="$t('common._common.id')"
              min-width="90"
            >
              <template v-slot="data">
                <div class="fc-id">{{ '#' + data.row.id }}</div>
              </template>
            </el-table-column>
            <el-table-column
              fixed
              :label="$t('common.products._name')"
              width="300"
            >
              <template v-slot="data">
                <div
                  v-tippy
                  small
                  @click="goToOverview(data.row)"
                  :title="$getProperty(data, 'row.visitorName', '---')"
                  class="flex-middle"
                >
                  <VisitorAvatar
                    :module="moduleName"
                    size="lg"
                    :recordData="data.row"
                  ></VisitorAvatar>
                  <div class="fw5 ellipsis width200px mL10 main-field-column">
                    {{ $getProperty(data, 'row.visitorName', '---') }}
                  </div>
                </div>
              </template>
            </el-table-column>
            <template v-for="(field, index) in viewColumns">
              <el-table-column
                class="visibility-visible-actions"
                :align="
                  field.field.dataTypeEnum === 'DECIMAL' ? 'right' : 'left'
                "
                :key="index"
                :prop="field.name"
                :label="field.displayName"
                min-width="200"
                v-if="!isFixedColumn(field.name) || field.parentField"
              >
                <template v-slot="data">
                  <template
                    v-if="field.name === 'inviteHost' && data.row.inviteHost"
                  >
                    <UserAvatar
                      size="md"
                      :user="$store.getters.getUser(data.row.inviteHost.id)"
                    ></UserAvatar>
                  </template>
                  <div
                    v-else
                    :class="[
                      'table-subheading',
                      {
                        'text-right': field.field.dataTypeEnum === 'DECIMAL',
                      },
                    ]"
                  >
                    {{ getColumnDisplayValue(field, data.row) || '---' }}
                  </div>
                </template>
              </el-table-column>
            </template>
            <el-table-column
              :width="calculateColumnWidth()"
              class="visibility-visible-actions"
            >
              <template v-slot="data">
                <div class="text-center visibility-hide-actions">
                  <TransitionButtons
                    :record="data.row"
                    :moduleName="moduleName"
                    :stateFlowList="stateflows"
                    @transitionSuccess="loadRecordList"
                  ></TransitionButtons>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              width="120"
              class="visibility-visible-actions"
              fixed="right"
            >
              <template v-slot="data">
                <div class="text-center flex-middle">
                  <div @click="goToOverview(data.row)">
                    <InlineSvg
                      src="svgs/binoculars"
                      iconClass="icon text-center icon-sm-md mR10 fill-grey2 visibility-hide-actions vertical-text-bottom"
                    ></InlineSvg>
                  </div>
                  <i
                    v-if="
                      $hasPermission(`${moduleName}:UPDATE`) &&
                        !data.row.hasCheckedIn
                    "
                    class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                    data-arrow="true"
                    :title="$t('home.visitor.edit_visitor_invite')"
                    v-tippy
                    @click="editRecord(data.row)"
                  ></i>
                  <i
                    v-if="$hasPermission(`${moduleName}:DELETE`)"
                    class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                    data-arrow="true"
                    :title="$t('home.visitor.delete_visitor_invite')"
                    v-tippy
                    @click="deleteRecord(data.row)"
                  ></i>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <portal to="view-manager-link">
      <router-link
        tag="div"
        :to="{ name: 'vi-viewmanager', params: { moduleName } }"
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

    <column-customization
      :visible.sync="showColumnSettings"
      :moduleName="moduleName"
      :viewName="currentView"
    ></column-customization>

    <VisitsAndInvitesForm
      v-if="showFormVisibility"
      :moduleName="moduleName"
      :formMode="formMode"
      @onClose="showFormVisibility = false"
    ></VisitsAndInvitesForm>

    <router-view />
  </CommonListLayout>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import VisitsAndInvitesForm from '../visits/VisitsAndInvitesForm'
import CommonListLayout from 'newapp/list/CommonLayout'
import Pagination from 'src/newapp/components/ListPagination'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import VisitorAvatar from '@/avatar/VisitorAvatar'
import TransitionButtons from '@/stateflow/TransitionButtonsForList'
import ViewMixinHelper from '@/mixins/ViewMixin'
import ColumnCustomization from '@/ColumnCustomization'
import UserAvatar from '@/avatar/User'
import { API } from '@facilio/api'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import OtherMixin from '@/mixins/OtherMixin'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper'

export default {
  props: ['moduleName', 'viewname'],
  mixins: [ViewMixinHelper, OtherMixin],
  components: {
    VisitsAndInvitesForm,
    CommonListLayout,
    Pagination,
    FExportSettings,
    Sort,
    VisitorAvatar,
    TransitionButtons,
    UserAvatar,
    ColumnCustomization,
    AdvancedSearchWrapper,
  },
  data() {
    return {
      showFormVisibility: false,
      loading: false,
      tableLoading: false,
      listCount: null,
      invitesList: null,
      stateflows: {},
      fields: {},
      formMode: 'single',
      sortConfig: { orderType: 'asc' },
      sortConfigList: [],
      showColumnSettings: false,
      availableStates: [],
      isEmpty,
    }
  },
  computed: {
    ...mapState('view', {
      views: state => state.groupViews,
      viewDetail: state => state.currentViewDetail,
      viewLoading: state => state.isLoading,
    }),
    filters() {
      let {
        $route: { query },
      } = this
      let { search } = query || {}
      return search ? JSON.parse(search) : null
    },
    currentView() {
      return this.viewname
    },
    page() {
      let {
        $route: { query },
      } = this
      let { page } = query || {}
      return page || 1
    },
    showLoading() {
      return this.viewLoading || this.loading || this.tableLoading
    },
    isV3Api() {
      return true
    },
    viewDetailFields() {
      return this.viewDetail.fields
    },
  },
  watch: {
    currentView: {
      handler(newVal, oldVal) {
        if (oldVal !== newVal && !isEmpty(newVal)) {
          this.loadRecordList()
          this.fetchMetaFields()
        }
      },
      immediate: true,
    },
    filters() {
      this.loadRecordList()
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadRecordList()
      }
    },
    viewDetail() {
      let { sortFields = [] } = this.viewDetail || {}

      if (!isEmpty(sortFields)) {
        let { name } = this.$getProperty(sortFields[0], 'sortField', {})
        this.sortConfig = {
          orderType: sortFields[0].isAscending ? 'asc' : 'desc',
          orderBy: name ? name : '',
        }
      }
    },
    viewDetailFields() {
      this.tableLoading = true
      this.$nextTick(() => {
        this.$refs.tableList ? this.$refs.tableList.doLayout() : null
        this.tableLoading = false
      })
    },
  },
  methods: {
    enable() {
      if (
        this.$helpers.isLicenseEnabled('DISABLE_GROUP_INVITE') ||
        this.$helpers.isLicenseEnabled('GROUP_INVITES')
      ) {
        return true
      } else {
        return false
      }
    },
    async fetchMetaFields() {
      let { error, data } = await API.get(
        `/module/metafields?moduleName=${this.moduleName}`
      )

      if (!error) {
        let { fields } = data.meta || {}
        if (!isEmpty(fields)) {
          this.sortConfigList = fields.map(fld => fld.name)
        }
        this.sortConfigList.push('localId')
      }
    },
    async loadRecordList() {
      this.loading = true

      let { moduleName, page, currentView, filters } = this
      let params = {
        withCount: true,
        viewName: currentView,
        includeParentFilter: this.includeParentFilter,
        page,
        perPage: 50,
        filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
      }
      let {
        list,
        meta: { pagination = {}, stateflows },
        error,
      } = await API.fetchAll(moduleName, params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.invitesList = list || []
        this.listCount = this.$getProperty(pagination, 'totalCount', null)
        this.stateflows = stateflows || {}
        this.columnWidth()
      }
      this.loading = false
    },
    updateSort(sorting) {
      let { moduleName, currentView } = this
      let sortObj = {
        viewName: currentView,
        orderBy: sorting.orderBy,
        orderType: sorting.orderType,
        moduleName,
      }
      this.$store
        .dispatch('view/savesorting', sortObj)
        .then(() => this.loadRecordList())
    },
    goToOverview({ id }) {
      let { moduleName, currentView, $route } = this
      let params = { viewname: currentView, id }
      let { query } = $route || {}

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.replace({ name, params, query })
        }
      } else {
        this.$router.replace({
          name: 'invites-overview',
          params,
          query,
        })
      }
    },
    openInviteForm(mode) {
      this.formMode = mode
      this.showFormVisibility = true
    },
    editRecord(data) {
      let { id, visitorType, formId } = data
      let query = {
        visitorTypeId: visitorType.id,
        formId: formId,
      }

      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        if (name) {
          this.$router.push({
            name,
            params: { id },
            query,
          })
        }
      } else {
        this.$router.push({
          name: 'invites-edit',
          params: { id },
          query,
        })
      }
    },
    deleteRecord({ id }) {
      this.$dialog
        .confirm({
          title: this.$t('home.visitor.delete_visitor_invite'),
          message: this.$t('home.visitor.delete_visitor_invite_confirmation'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            API.deleteRecord(this.moduleName, id).then(({ error }) => {
              if (error) {
                this.$message.error(error.messsage || 'Error Occured')
              } else {
                this.$message.success(
                  this.$t('home.visitor.visitor_invite_delete_success')
                )
                this.loadRecordList()
              }
            })
          }
        })
    },
    columnWidth() {
      let valuesArray = Object.values(this.stateflows)
      valuesArray[0]?.forEach(field => {
        let { typeEnum } = field || {}

        if (typeEnum === 'NORMAL') {
          this.availableStates.push(field)
        }
      })
    },
    calculateColumnWidth() {
      let pixels =
        this.availableStates && this.availableStates.length > 2
          ? '420'
          : this.availableStates.length == 2
          ? '350'
          : this.availableStates.length == 0
          ? '10'
          : '210'
      return pixels
    },
  },
}
</script>
<style scoped></style>
