<template>
  <div class="height100">
    <div class="layout container">
      <div class="height100">
        <div v-if="openWorkorderId === -1" class="row height100">
          <div class="col-12">
            <div class="height100 p10">
              <div class="row" v-if="!showQuickSearch">
                <div
                  class="col-12 fc-wo-requests-actions"
                  v-show="selectedWorkorders.length"
                >
                  <div class="pull-left">
                    <div class="action-btn-slide btn-block">
                      <q-checkbox
                        v-model="selectAll"
                        class="wo-check-box mR10"
                        color="secondary"
                      />
                      <button
                        class="btn btn--tertiary"
                        @click="deleteWorkOrder(selectedWorkorders)"
                        :class="{ disabled: actions.delete.loading }"
                      >
                        <i
                          class="fa fa-ban b-icon"
                          v-if="!actions.delete.loading"
                        ></i>
                        <i
                          class="fa fa-circle-o-notch b-icon fa-spin"
                          aria-hidden="true"
                          v-if="actions.delete.loading"
                        ></i>
                        {{ $t('common._common.delete') }}
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="row" v-if="showQuickSearch">
                <div class="col-12 fc-list-search">
                  <div class="fc-list-search-wrapper relative">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="32"
                      height="32"
                      viewBox="0 0 32 32"
                      class="search-icon"
                    >
                      <title>{{ $t('common._common.search') }}</title>
                      <path
                        d="M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"
                      ></path>
                    </svg>
                    <input
                      ref="quickSearchQuery"
                      autofocus
                      type="text"
                      v-model="quickSearchQuery"
                      @keyup.enter="quickSearch"
                      placeholder="Search"
                      class="quick-search-input"
                    />
                    <svg
                      @click="hideQuickSearchBox"
                      xmlns="http://www.w3.org/2000/svg"
                      width="32"
                      height="32"
                      viewBox="0 0 32 32"
                      class="close-icon"
                      aria-hidden="true"
                    >
                      <title>{{ $t('common._common.close') }}</title>
                      <path
                        d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"
                      ></path>
                    </svg>
                  </div>
                </div>
              </div>
              <div
                v-if="loading"
                class="full-layout-white height100 text-center"
              >
                <spinner :show="loading" size="80"></spinner>
              </div>
              <div class="full-layout-white height100" v-else>
                <div v-if="!workorders.length" class="row container nowor">
                  <div class="justify-center nowo height80vh flex-middle">
                    <div>
                      <inline-svg
                        src="svgs/emptystate/approval"
                        iconClass="icon text-center icon-xxxxlg"
                      ></inline-svg>
                      <div class="nowo-label">
                        {{ $t('maintenance.wr_list.no_request') }}
                      </div>
                    </div>
                  </div>
                </div>
                <div class="scrollable130-y100 fc-full-border" v-else>
                  <div
                    v-for="workorder in workorders"
                    class="row workorder-entry"
                    v-bind:class="{
                      checkselected: isSelected(workorder.id),
                      selected: openWorkorderId === workorder.id,
                    }"
                    :key="workorder.id"
                  >
                    <div
                      v-on:click="openSummaryView(workorder.id)"
                      class="col-5 errorClass"
                      style="padding-left: 20px;"
                    >
                      <div class="q-item q-item-division relative-position">
                        <q-checkbox
                          v-model="selectedWorkorders"
                          class="workorderCheckbox"
                          :val="workorder.id"
                          color="secondary"
                          v-if="workorder.status.status !== 'Closed'"
                        />
                        <q-checkbox
                          v-model="selectedWorkorders"
                          class="workorderCheckbox"
                          color="secondary"
                          v-else
                          disable
                        />
                        <div
                          class="q-item-side q-item-side-left q-item-section text-primary"
                          style="margin-left: 9px;"
                        >
                          <user-avatar
                            size="md"
                            :user="workorder.requestedBy"
                            :showPopover="true"
                            :name="false"
                          ></user-avatar>
                        </div>
                        <div class="q-item-main q-item-section">
                          <div class="q-item-label display-flex">
                            <div
                              class="ellipsis fc-list-subject max-width300px"
                              :title="workorder.subject"
                              v-tippy="{
                                placement: 'top',
                                animation: 'shift-away',
                                arrow: true,
                              }"
                            >
                              {{ workorder.subject }}
                            </div>
                            <div
                              class="fc-tag-urgent mL10"
                              v-if="workorder.urgency > 0"
                              :style="{
                                background: urgencyColorCode[workorder.urgency],
                              }"
                            >
                              {{ $constants.WO_URGENCY[workorder.urgency] }}
                            </div>
                          </div>
                          <div class="q-item-sublabel ellipsis fc-list-desc">
                            {{ workorder.description }}
                          </div>
                          <div class="q-item-label wo-requester width500px">
                            <div class="ellipsis fc-id">
                              #{{ workorder.serialNumber }}
                            </div>
                            <template v-if="workorder.sourceType">
                              <span class="separator">|</span>
                              <div style="color: #2ea2b2; padding-right: 5px;">
                                <i
                                  v-if="workorder.sourceType === 2"
                                  class="fa fa-envelope-o"
                                ></i>
                                <i
                                  v-else-if="workorder.sourceType === 10"
                                  class="f14 fa fa-globe"
                                ></i>
                                <i
                                  v-else-if="workorder.sourceType === 1"
                                  class="f14 el-icon-service approval-el-icon-service"
                                ></i>
                                <i
                                  v-else-if="workorder.sourceType === 4"
                                  class="f14 el-icon-bell approval-el-icon-bell"
                                ></i>
                                <i
                                  v-else-if="workorder.sourceType === 5"
                                  class="f14 el-icon-date approval-el-icon-date"
                                ></i>
                              </div>
                              <div
                                v-if="workorder.requester"
                                class="worker-name pT0"
                                style="padding-right: 8px;color: #8ca1ad;"
                              >
                                {{
                                  workorder.requester.id > 0
                                    ? workorder.requester.name
                                    : '---'
                                }}
                              </div>
                            </template>
                            <div
                              v-if="
                                workorder.resource && workorder.resource.id > 0
                              "
                              class="fc-grey2-text12"
                            >
                              <span class="separator">|</span>
                              <img
                                v-if="workorder.resource.resourceType === 1"
                                src="~statics/space/space-resource.svg"
                                style="height:12px; width:14px;"
                                class="mR5"
                              />
                              <img
                                v-else
                                src="~statics/space/asset-resource.svg"
                                style="height:12px; width:14px;"
                                class="mR5"
                              />
                              {{ workorder.resource.name }}
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div
                      v-on:click="openSummaryView(workorder.id)"
                      class="col-1"
                    ></div>
                    <div class="col-6">
                      <approve-reject
                        v-if="
                          workorder.moduleState && workorder.moduleState.id > 0
                        "
                        :record="workorder"
                        moduleName="workorder"
                        :transformFn="transformFormData"
                        updateUrl="/v2/workorders/update"
                        class="approve-btn-hover mR50"
                        approveClass="work-approve-btn"
                        rejectClass="work-reject-btn"
                        @transitionSuccess="onFormSaved"
                        :onHover="true"
                      ></approve-reject>
                      <div
                        class="q-item q-item-division relative-position fR mR100 hover-hide"
                        style="width: 203px;max-width: 210px;padding-top: 0;"
                      >
                        <div class="q-item-main q-item-section">
                          <span
                            v-if="workorder.priority"
                            class="q-item-label"
                            style="color: #999;"
                          ></span>
                          <span class="q-item-label mT10 show"
                            ><i
                              v-if="workorder.priority"
                              class="fa fa-circle prioritytag mR10"
                              v-bind:style="{
                                color: getTicketPriority(workorder.priority.id)
                                  .colour,
                              }"
                              aria-hidden="true"
                            ></i
                            >{{
                              workorder.priority && workorder.priority.id > 0
                                ? getTicketPriority(workorder.priority.id)
                                    .displayName
                                : '---'
                            }}</span
                          >
                          <div class="creted-time mT10">
                            <i
                              class="fa fa-clock-o fc-due-date-clock"
                              style="padding-right: 5px;font-size: 15px"
                            ></i>
                            <span style="font-size: 13px">
                              Created {{ workorder.createdTime | fromNow }}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="fc-column-view height100" v-else>
          <div class="row fc-column-view-title height100">
            <div class="col-4 fc-approval-view-left height100">
              <div class="row container col-header" v-if="!showQuickSearch">
                <div class="col-12" v-if="!selectedWorkorders.length">
                  <div class="pull-left">
                    <q-checkbox
                      v-model="selectAll"
                      class="wo-check-box"
                      color="secondary"
                    />
                    <el-dropdown
                      @command="
                        $router.push({ path: '/app/wo/approvals/wr/' + $event })
                      "
                      class="approval-list-dropdown"
                    >
                      <span class="el-dropdown-link pointer">
                        {{ currentViewDetail.displayName
                        }}<i class="el-icon-arrow-down el-icon--right"></i>
                      </span>
                      <el-dropdown-menu slot="dropdown" class="wo-dropdownmenu">
                        <el-dropdown-item
                          :command="view.name"
                          v-for="(view, idx) in views"
                          :key="idx"
                          v-if="view.name !== currentViewDetail.name"
                        >
                          {{ view.displayName }}
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </div>
                </div>
                <div class="col-12" v-else>
                  <div class="pull-left">
                    <q-checkbox
                      v-model="selectAll"
                      class="wo-check-box"
                      color="secondary"
                    />
                  </div>
                  <div
                    class="inline"
                    style="padding-left: 10px; margin-top: -1px;"
                  >
                    <div class="action-btn-slide btn-block">
                      <button
                        class="btn btn--tertiary"
                        @click="deleteWorkOrder(selectedWorkorders)"
                        :class="{ disabled: actions.delete.loading }"
                      >
                        <i
                          class="fa fa-ban b-icon"
                          v-if="!actions.delete.loading"
                        ></i>
                        <i
                          class="fa fa-circle-o-notch b-icon fa-spin"
                          aria-hidden="true"
                          v-if="actions.delete.loading"
                        ></i>
                        {{ $t('common._common.delete') }}
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="row" v-if="showQuickSearch">
                <div class="col-12 fc-list-search">
                  <div class="fc-list-search-wrapper relative">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="32"
                      height="32"
                      viewBox="0 0 32 32"
                      class="search-icon"
                    >
                      <title>{{ $t('common._common.search') }}</title>
                      <path
                        d="M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"
                      ></path>
                    </svg>
                    <input
                      ref="quickSearchQuery"
                      autofocus
                      type="text"
                      v-model="quickSearchQuery"
                      @keyup.enter="quickSearch"
                      placeholder="Search"
                      class="quick-search-input"
                    />
                    <svg
                      @click="hideQuickSearchBox"
                      xmlns="http://www.w3.org/2000/svg"
                      width="32"
                      height="32"
                      viewBox="0 0 32 32"
                      class="close-icon"
                      aria-hidden="true"
                    >
                      <title>{{ $t('common._common.close') }}</title>
                      <path
                        d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"
                      ></path>
                    </svg>
                  </div>
                </div>
              </div>
              <div class="row fc-column-view-records height100">
                <table
                  class="fc-list-view-table"
                  style="height: 100%; overflow-y: scroll; display: block; padding-bottom: 120px;"
                >
                  <tbody v-if="workorders.length">
                    <tr
                      v-bind:class="{
                        checkselected: isSelected(workorder.id),
                        selected: openWorkorderId === workorder.id,
                      }"
                      class="approval-inner-list"
                      v-for="(workorder, index) in workorders"
                      :key="index"
                    >
                      <td style="width:2%;">
                        <q-checkbox
                          v-model="selectedWorkorders"
                          :val="workorder.id"
                          class="wo-check-box pL3"
                          color="secondary"
                          v-if="workorder.status.status !== 'Closed'"
                        />
                        <q-checkbox
                          class="wo-check-box"
                          color="secondary"
                          v-else
                          disable
                          v-model="selectedWorkorders"
                        />
                      </td>
                      <td
                        v-on:click="openSummaryView(workorder.id)"
                        class="text-left width90 pL5"
                      >
                        <div class="relative-position">
                          <div style="width: 10%;float:left;">
                            <user-avatar
                              size="md"
                              :user="workorder.requestedBy"
                              :showPopover="true"
                              :name="false"
                            ></user-avatar>
                          </div>
                          <div class="q-item-main q-item-section">
                            <div
                              class="q-item-label primary-field workRequest-heading"
                            >
                              {{ workorder.subject }}
                            </div>
                            <div class="request-hours fc-grey2-text12 fR pT8">
                              <i
                                class="fa fa-clock-o fc-due-date-clock mR5"
                              ></i>
                              {{ workorder.createdTime | fromNow }}
                            </div>
                            <div
                              class="row"
                              style="padding-top: 5px;align-items: center;padding-left: 5px;"
                            >
                              <div class="ellipsis fc-id">
                                #{{ workorder.serialNumber }}
                              </div>
                              <template v-if="workorder.sourceType">
                                <span class="separator">|</span>
                                <div
                                  style="color: #2ea2b2; padding-right: 5px;"
                                >
                                  <i
                                    v-if="workorder.sourceType === 2"
                                    class="fa fa-envelope-o"
                                  ></i>
                                  <i
                                    v-else-if="workorder.sourceType === 10"
                                    class="f14 fa fa-globe"
                                  ></i>
                                  <i
                                    v-else-if="workorder.sourceType === 1"
                                    class="f14 el-icon-service approval-el-icon-service"
                                  ></i>
                                  <i
                                    v-else-if="workorder.sourceType === 4"
                                    class="f14 el-icon-bell approval-el-icon-bell"
                                  ></i>
                                  <i
                                    v-else-if="workorder.sourceType === 5"
                                    class="f14 el-icon-date approval-el-icon-date"
                                  ></i>
                                </div>
                              </template>
                            </div>
                          </div>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
            <div class="col-8 fc-column-view-right-approval">
              <div slot="right">
                <router-view name="summary"></router-view>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import UserAvatar from '@/avatar/User'
import { mapState, mapGetters } from 'vuex'
import ApproveReject from '@/stateflow/ApproveReject'
import { isEmpty } from '@facilio/utils/validation'
import { Toast, QCheckbox } from 'quasar'
import transformMixin from 'pages/workorder/workorders/v1/mixins/workorderTransform'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  mixins: [transformMixin],
  data() {
    return {
      loading: true,
      selectedWorkorders: [],
      selectAll: false,
      showFilter: false,
      showName: '',
      emptyName: {
        name: 'Unknow',
      },
      actions: {
        approve: {
          loading: false,
        },
        reject: {
          loading: false,
        },
        delete: {
          loading: false,
        },
      },
      sortConfig: {
        orderBy: {
          label: 'Recent',
          value: 'recent',
        },
        list: [
          {
            label: 'Recent',
            value: 'recent',
          },
          {
            label: 'Oldest',
            value: 'oldest',
          },
        ],
      },
      sortValue: {
        orderBy: {
          label: 'Recent',
          value: 'createdTime',
        },
        orderType: 'desc',
      },
      showQuickSearch: false,
      quickSearchQuery: null,
      urgencyColorCode: {
        1: '#7fa5ff',
        2: '#f56837',
        3: '#e65244',
      },
    }
  },
  title() {
    return this.$options.filters.viewName(this.currentView)
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketPriority')
  },
  mounted() {
    this.loadApprovals()
    this.loadApprovalCounts()
  },
  computed: {
    workorders() {
      return this.$store.state.workorder.workorders
    },
    canEdit() {
      let moduleId = this.$getProperty(this.workorder, 'moduleState.id', null)
      return (
        !isEmpty(moduleId) &&
        !this.$store.getters.isStatusLocked(
          this.workorder.moduleState.id,
          'workorder'
        )
      )
    },
    includeParentFilter() {
      return (
        this.$route.query.includeParentFilter &&
        (this.$route.query.includeParentFilter === 'true' ||
          this.$route.query.includeParentFilter === true)
      )
    },
    openWorkorderId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    page() {
      return this.$route.query.page || 1
    },
    canShowFilteredFields() {
      let canShow = false
      if (this.filters) {
        Object.keys(this.filters).forEach(key => {
          if (this.filters[key].value.length) {
            canShow = true
          }
        })
      }
      return canShow
    },
    showCreateNewDialog() {
      if (this.$route.query.create) {
        return true
      }
      return false
    },
    ...mapState({
      users: state => state.users,
      ticketcategory: state => state.ticketCategory,
      ticketpriority: state => state.ticketPriority,
      views: state => state.view.views,
      currentViewDetail: state => state.view.currentViewDetail,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
    ...mapGetters(['getTicketPriority']),
  },
  methods: {
    resetSelectAll() {
      this.selectedWorkorders = []
      this.selectAll = false
    },
    loadApprovals() {
      let queryObj = {
        viewname: this.currentView,
        page: this.page,
        filters: this.filters,
        orderBy: this.sortValue.orderBy.value,
        orderType: this.sortValue.orderType,
        search: this.quickSearchQuery,
        includeParentFilter: this.includeParentFilter,
        isNew: true,
      }
      this.loading = true
      this.$store
        .dispatch('workorder/fetchApproval', queryObj)
        .then(response => {
          this.loading = false
        })
        .catch(error => {
          if (error) {
            this.loading = false
          }
        })
      this.setTitle(this.$options.filters.viewName(this.currentView))
    },
    loadApprovalCounts() {
      let queryObj = {
        viewname: this.currentView,
        filters: this.filters,
        search: this.quickSearchQuery,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        count: true,
      }
      let url = '/v2/approvals/view/count?viewName=' + queryObj.viewname
      let params
      params = 'count=' + queryObj.count
      if (queryObj.filters) {
        params =
          params +
          '&filters=' +
          encodeURIComponent(JSON.stringify(queryObj.filters))
      }
      if (queryObj.search) {
        params = params + '&search=' + queryObj.search
      }
      if (queryObj.criteriaIds) {
        params = params + '&criteriaIds=' + queryObj.criteriaIds
      }
      if (queryObj.includeParentFilter) {
        params = params + '&includeParentFilter=' + queryObj.includeParentFilter
      }
      url = url + '&' + params
      this.$http
        .get(url)
        .then(response => {
          this.listcount = response.data.woCount
          this.$emit('syncCount', response.data.result.workorderscount)
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    openSummaryView(id) {
      this.$router.push({
        path:
          '/app/wo/approvals/wr/' +
          this.$route.params.viewname +
          '/summary/' +
          id,
      })
    },
    deleteWorkOrder(idList) {
      this.$dialog
        .confirm({
          title: this.$t('maintenance._workorder.delete_wo'),
          message: this.$t('maintenance._workorder.delete_wo_body'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.actions.delete.loading = true
            this.$store
              .dispatch('workorder/deleteWorkOrder', { id: idList })
              .then(() => {
                this.actions.delete.loading = false
                this.$dialog.notify(
                  this.$t('maintenance._workorder.wo_delete_success')
                )
                this.resetSelectAll()
              })
          }
        })
    },
    filterApplied(filterData) {
      let currentFilter = this.filters ? this.filters : {}
      Object.keys(filterData).forEach(function(key) {
        currentFilter[key] = Object.assign({}, filterData[key])
      })
      this.$router.replace({ query: { search: JSON.stringify(currentFilter) } })
    },
    clearFiters() {
      this.$router.replace({ path: this.$route.path, query: {} })
    },
    removeFilterField(fl, index) {
      fl.value.splice(index, 1)
      this.$router.replace({
        path: this.$route.path,
        query: { search: JSON.stringify(this.filters) },
      })
    },
    closeFilterDialog() {
      this.showFilter = false
    },
    isSelected(id) {
      for (let x in this.selectedWorkorders) {
        if (this.selectedWorkorders[x] === id) {
          return true
        }
      }
      return false
    },
    notify(eventName) {
      Toast.create(`Event "${eventName}" was triggered.`)
    },
    updateSort(sorting) {
      if (sorting.orderBy.value === 'recent') {
        this.sortValue.orderBy.label = 'Recent'
        this.sortValue.orderBy.value = 'createdTime'
        this.sortValue.orderType = 'desc'
      } else {
        this.sortValue.orderBy.label = 'Oldest'
        this.sortValue.orderBy.value = 'createdTime'
        this.sortValue.orderType = 'asc'
      }
      this.loadApprovals()
    },
    showQuickSearchBox() {
      this.showQuickSearch = true
    },
    hideQuickSearchBox() {
      this.showQuickSearch = false
      if (this.quickSearchQuery && this.quickSearchQuery.trim().length > 0) {
        this.quickSearchQuery = null
        this.loadApprovals()
      }
    },
    quickSearch() {
      if (this.quickSearchQuery && this.quickSearchQuery.trim().length > 0) {
        this.loadApprovals()
      }
    },
    onFormSaved() {
      this.loadApprovals()
    },
  },
  watch: {
    currentView: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadApprovals()
        this.loadApprovalCounts()
      }
    },
    filters: function(newVal) {
      this.loadApprovals()
      this.loadApprovalCounts()
    },
    selectAll: function(val) {
      if (val) {
        this.selectedWorkorders = []
        this.workorders.filter(workorder => {
          this.selectedWorkorders.push(workorder.id)
        })
      } else {
        if (this.selectedWorkorders.length === this.workorders.length) {
          this.selectedWorkorders = []
        }
      }
    },
    selectedWorkorders: function() {
      if (this.selectedWorkorders.length !== this.workorders.length) {
        this.selectAll = false
      }
    },
    page: function(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadApprovals()
      }
    },
    workorders: function() {
      if (this.openWorkorderId > 0) {
        if (!this.workorders.length) {
          let newpath = this.$route.path.substring(
            0,
            this.$route.path.indexOf('/summary/')
          )
          this.$router.replace({ path: newpath })
        } else {
          let wo = this.workorders.find(
            workorder => workorder.id === this.openWorkorderId
          )
          if (!wo) {
            let newpath = this.$route.path.substring(
              0,
              this.$route.path.indexOf('/summary/')
            )
            newpath += '/summary/' + this.workorders[0].id
            this.$router.replace({ path: newpath })
          }
        }
      }
    },
  },
  components: {
    QCheckbox,
    UserAvatar,
    ApproveReject,
  },
  filters: {
    viewName: function(name) {
      if (name === 'open') {
        return 'Open Work Requests'
      } else if (name === 'all') {
        return 'All Work Requests'
      } else if (name === 'rejected') {
        return 'Rejected Work Requests'
      }
      return 'Approval'
    },
  },
}
</script>
<style>
.workorder-entry {
  cursor: pointer;
  background: #fff;
  padding: 5px;
  border-left: 3px solid transparent;
  border-right: 1px solid transparent;
  border-top: 1px solid transparent;
  border-bottom: 1px solid #f2f5f6;
  align-items: center;
  min-height: 90px;
  position: relative;
}
.row.workorder-entry.selected {
  background: #f3f4f7 !important;
  border-left: 3px solid #ff2d81 !important;
  border-right: 1px solid rgba(232, 232, 232, 0.35) !important;
  border-top: 1px solid rgba(232, 232, 232, 0.35) !important;
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
.row.workorder-entry.checkselected {
  background: #e2f1ef;
  border-left: 3px solid #28b2a4;
  border-right: 1px solid rgba(232, 232, 232, 0.35);
  border-top: 1px solid rgba(232, 232, 232, 0.35);
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
.row.workorder-entry:hover {
  background: #fafbfc !important;
  border-left: 3px solid #39b2c2 !important;
}
.row.workorder-entry:hover .request-time {
  display: none;
}
.row.workorder-entry:hover .approval-actions {
  display: block;
  padding-top: 10px;
  padding-left: 50px;
  margin-right: 20px;
}
.requester-info {
  border-right: 1px solid #e7ebf4;
}
.requester-info .q-item-label {
  color: #18b2a4;
}
.requester-info .q-item-main.q-item-section div {
  padding: 2px;
}
.workorderCheckbox {
  position: relative;
  right: 15px;
  background-color: transparent !important;
  border: 0;
}
.workorderCheckbox i.q-checkbox-unchecked.cursor-pointer.q-icon.material-icons,
.workorderCheckboxall
  i.q-checkbox-unchecked.cursor-pointer.q-icon.material-icons {
  color: #cdcdcd !important;
  font-size: 22px;
  font-weight: normal;
}
.row.workorder-entry:hover
  .workorderCheckbox
  i.q-checkbox-unchecked.cursor-pointer.q-icon.material-icons {
  color: gray !important;
}
.workorderCheckboxall:hover
  i.q-checkbox-unchecked.cursor-pointer.q-icon.material-icons {
  color: gray !important;
}
.q-checkbox.q-option.cursor-pointer.no-outline.q-focusable.row.inline.no-wrap.items-center.workorderCheckboxall {
  position: relative;
  left: 10px;
  background-color: transparent !important;
  border: 0;
}
.workorderCheckboxall span.q-option-label {
  margin-left: 8px;
  font-size: 12px;
  color: gray;
  margin: 10px;
  padding-right: 10p;
  padding-right: 10px;
}
.q-chip.row.inline.items-center.wo-chip.square {
  width: 100px;
  background: lightgray;
  font-size: 12px;
}
.wo-chip i.on-right.q-icon.fa.fa-close {
  font-size: 13px;
  position: relative;
  left: 31px;
  cursor: pointer;
  color: palevioletred;
}
.selectallbutton span.q-btn-inner.row.col.flex-center {
  border-bottom: 0px;
  padding: 0px;
  background-color: transparent;
  color: #5bbaa7;
  font-size: 10px;
}
.selectallbutton i.on-right.q-icon.material-icons {
  font-size: 12px;
  color: #c53d3d;
}
button.q-btn.row.inline.flex-center.q-focusable.q-hoverable.relative-position.selectallbutton.q-btn-rectangle.q-btn-standard.q-btn-flat {
  background-color: #e2f1ef;
  width: 100px;
  border-radius: 5px;
}
.wo-requests div.col-4.errorClass {
  position: fixed;
  width: 100%;
  max-width: 32.3%;
  height: 84%;
  overflow: scroll;
}
.wo-requests div.col-8.wor {
  position: fixed;
  left: 37.5%;
  width: 62.2%;
  overflow: scroll;
  height: 84%;
}
.wo-requests div.col-8.wor::-webkit-scrollbar-y-track {
  -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
  background-color: transparent;
}
.wo-requests div.col-8.wor::-moz-scrollbar-y-track {
  -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
  background-color: transparent;
}
.wo-requests div.col-8.wor::-webkit-scrollbar {
  display: none;
}
.wo-requests div.col-8.wor::-moz-scrollbar {
  display: none;
}
.nowor {
  background: white;
  padding-top: 5%;
}
.nowo {
  text-align: center;
  width: 100%;
  position: relative;
}
.nowo-btn i.q-icon.material-icons.on-left {
  font-size: 10px;
  margin-right: 2px;
}
.nowo-btn {
  font-size: 10px;
  font-weight: 500;
  margin-top: 15px;
  background: #fd4b92 !important;
}
.nowo-logo {
  height: 65px;
  width: 65px;
}
.wo-requester,
.worker-name {
  font-size: 13px;
  display: inline-flex;
  flex-direction: row;
  align-items: center;
  padding-right: 5px;
  padding-top: 10px;
  color: #454448;
  letter-spacing: 0.4px;
}
.wo-requests .q-item-sublabel {
  font-size: 14px;
  font-weight: normal;
  line-height: 1.57;
  letter-spacing: normal;
}
.wo-requests .q-item-section + .q-item-section {
  margin-left: 10px !important;
}
.tablerow.selected {
  background: #f4fafb !important;
  border-left: 3px solid #ff2d81 !important;
  border-right: 1px solid rgba(232, 232, 232, 0.35) !important;
  border-top: 1px solid rgba(232, 232, 232, 0.35) !important;
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
.tablerow.checkselected {
  background: #e2f1ef;
  border-left: 3px solid #28b2a4;
  border-right: 1px solid rgba(232, 232, 232, 0.35);
  border-top: 1px solid rgba(232, 232, 232, 0.35);
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
tr.tablerow.selected td:first-child {
  border-left: 3px solid #43bbb0 !important;
}
tr.tablerow.checkselected td:first-child {
  border-left: 3px solid #43bbb0 !important;
}
tr.tablerow.selected td,
tr.tablerow.checkselected td {
  border-top: 1px solid #ececec !important;
  border-bottom: 1px solid #ececec !important;
}
i.fa.fa-sort-desc.wl-icon-downarrow {
  position: relative;
  left: 7px;
  bottom: 2px;
  font-size: 13px;
}
.work-approve-btn {
  background: #39b2c2;
  padding: 8px 16px;
  text-align: center;
  color: #fff !important;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  margin-right: 5px;
}
.work-approve-btn.el-button:hover {
  background: #39b2c2;
  border-color: none;
}
.work-reject-btn {
  padding: 7px 16px;
  background: #dc7171;
  border: 1px solid transparent;
  border-radius: 3px;
  margin-left: 10px;
  text-transform: uppercase;
  color: #fff !important;
  font-weight: 600;
  font-size: 11px;
  margin-left: 5px;
}
.work-reject-btn.el-button:hover {
  background: #dc7171;
  border-color: none;
}

.urgent {
  font-size: 13px;
}
.not-urgent {
  text-align: right;
  margin-right: 20px;
  font-size: 13px;
  color: #333333;
}
.request-building {
  font-size: 13px;
  letter-spacing: 0.4px;
  text-align: left;
  color: #2ea2b2;
  padding-top: 2px;
}
.workorder-entry .q-item-section + .q-item-section {
  margin-left: 14px !important;
  position: relative;
}
.list-view-filter {
  top: -14px !important;
}
.approval-list-dropdown {
  vertical-align: middle !important;
  display: inline-block !important;
  position: relative !important;
  margin-left: 10px !important;
  font-size: 13px !important;
  color: #000000 !important;
  font-weight: 500 !important;
  letter-spacing: 0.4px;
}
.el-icon--right {
  margin-left: 5px;
  vertical-align: middle;
  align-items: center;
  color: #000;
  font-weight: 500;
}
.q-option-inner {
  color: #cdcdcd;
}
.fc-list-view-table .workRequest-heading {
  width: 250px;
  font-weight: 500;
  overflow: hidden;
  position: relative;
  white-space: nowrap;
  text-overflow: ellipsis;
  padding-left: 5px;
  font-size: 14px;
  letter-spacing: 0.6px;
  color: #324056;
  display: block;
}
.creted-time {
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.3px;
  color: #666666;
}
.creted-time .el-icon-date {
  opacity: 0.4;
  font-size: 14px;
  font-weight: normal;
  color: #000000;
}
.envelope-icon {
  width: 14px;
  height: 14px;
  vertical-align: text-top;
  margin-right: 5px;
}
.wo-req-desc {
  font-size: 13px;
  line-height: 1.57;
  letter-spacing: normal;
  color: #78787e;
  text-transform: lowercase;
}
.approve-btn-hover {
  height: 80%;
  position: absolute;
  right: 50px;
  z-index: 1;
  background: #fafbfc !important;
  top: 12px;
}
.fc-approval-view-left {
  height: 100vh;
  max-width: 28% !important;
  background: #fff;
}
.grey-circle {
  color: #cecbd7;
  padding-left: 10px;
  font-size: 8px;
}
.list-created-time {
  font-size: 11px;
  letter-spacing: 0.3px;
  text-align: right;
  color: #242427;
  position: relative;
  top: 9px;
}
.fc-column-view-right-approval {
  background-color: #f7f8f9;
  min-height: 100vh;
  position: relative;
  margin-left: -2px;
  border-left: 1px solid rgba(0, 0, 0, 0.1);
  flex: 0 0 71.99999% !important;
  max-width: 71.99999% !important;
}
.approval-list-dropdown {
  letter-spacing: 0.4px;
}
.approval-list-dropdown .el-icon--right {
  font-size: 14px;
}
.fc-list-view-table tbody.selected {
  background-color: #f4fafb;
}

.approval-state {
  font-size: 12px;
  letter-spacing: 0.4px;
  color: #272729;
}
.approval-actions {
  margin-top: 7px;
}
@media screen and (max-width: 1280px) and (min-width: 800px) {
  .fc-column-view-right-approval {
    flex: 0 0 67.99999% !important;
    max-width: 67.99999% !important;
  }
  .fc-approval-view-left {
    max-width: 32% !important;
  }
}
</style>
