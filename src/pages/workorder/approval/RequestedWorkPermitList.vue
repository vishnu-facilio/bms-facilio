<template>
  <div class="height100 workpermit-appoval">
    <div class="layout container">
      <div class="height100">
        <div class="row height100">
          <div class="col-12">
            <div class="height100 p10">
              <div class="row" v-if="!showQuickSearch">
                <div
                  class="col-12 fc-wo-requests-actions"
                  v-show="selectedWorkPermits.length"
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
                        @click="deleteWorkPermits(selectedWorkPermits)"
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
                      />
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
                      />
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
                <div v-if="!workpermits" class="row container nowor">
                  <div class="justify-center nowo fc-h100vh">
                    <div>
                      <img class="nowo-logo" src="~assets/no_request.jpg" />
                      <div class="q-item-label nowo-label">
                        No Work Permits Available
                      </div>
                      <div
                        class="q-item-sublabel nowo-sublabel"
                        style="width: 24%;"
                      >
                        No WorkPermits available.
                      </div>
                    </div>
                  </div>
                </div>
                <div
                  class="fc-list-view pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
                  v-else
                >
                  <el-table
                    :data="workpermits"
                    ref="tableList"
                    class="width100"
                    height="auto"
                    :fit="true"
                  >
                    <template slot="empty">
                      <img
                        src="~statics/noData-light.png"
                        width="100"
                        height="100"
                      />
                      <div class="mT10 label-txt-black f14">
                        {{ $t('home.workpermit.workpermit_no_data') }}
                      </div>
                    </template>
                    <el-table-column fixed prop label="ID" min-width="90">
                      <template v-slot="data">
                        <div class="fc-id">{{ '#' + data.row.id }}</div>
                      </template>
                    </el-table-column>
                    <el-table-column fixed prop label="PERMIT NAME" width="300">
                      <template v-slot="data">
                        <div
                          v-tippy
                          small
                          :title="$getProperty(data, 'row.name', '---')"
                          class="flex-middle"
                        >
                          <div class="mL10">
                            <div
                              class="fw5 ellipsis textoverflow-ellipsis width200px"
                            >
                              {{ $getProperty(data, 'row.name', '---') }}
                            </div>
                          </div>
                        </div>
                      </template>
                    </el-table-column>
                    <el-table-column
                      :align="
                        field.field.dataTypeEnum === 'DECIMAL'
                          ? 'right'
                          : 'left'
                      "
                      v-for="(field, index) in viewColumns"
                      :key="index"
                      :prop="field.name"
                      :label="field.displayName"
                      min-width="200"
                      v-if="!isFixedColumn(field.name) || field.parentField"
                    >
                      <template v-slot="data">
                        <div
                          v-if="!isFixedColumn(field.name) || field.parentField"
                        >
                          <div
                            class="table-subheading"
                            :class="{
                              'text-right':
                                field.field.dataTypeEnum === 'DECIMAL',
                            }"
                          >
                            {{
                              getColumnDisplayValue(field, data.row) || '---'
                            }}
                          </div>
                        </div>
                      </template>
                    </el-table-column>
                    <el-table-column
                      prop
                      label
                      width="300"
                      class="visibility-visible-actions"
                      fixed="right"
                    >
                      <template v-slot="data">
                        <approve-reject
                          v-if="
                            data.row.moduleState && data.row.moduleState.id > 0
                          "
                          :record="data.row"
                          moduleName="workpermit"
                          moduleDisplayName="Work Permit"
                          :stateFlowList="stateflows"
                          updateUrl="/v2/workpermit/update"
                          class="approve-btn-hover visibility-hide-actions mR50"
                          approveClass="work-approve-btn"
                          rejectClass="work-reject-btn"
                          @transitionSuccess="onFormSaved"
                          :transformFn="transformFormData"
                          :onHover="false"
                        ></approve-reject>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ViewMixinHelper from '@/mixins/ViewMixin'
import { mapState } from 'vuex'
import { Toast, QCheckbox } from 'quasar'
import ApproveReject from '@/stateflow/ApproveReject'

export default {
  mixins: [ViewMixinHelper],
  data() {
    return {
      loading: true,
      selectedWorkPermits: [],
      selectAll: false,
      showFilter: false,
      showName: '',
      columnConfig: {
        fixedColumns: ['name', 'localId'],
        availableColumns: [],
        showLookupColumns: false,
      },
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
      sortValue: {
        orderBy: {
          label: 'Recent',
          value: 'createdTime',
        },
        orderType: 'desc',
      },
      showQuickSearch: false,
      quickSearchQuery: null,
    }
  },
  title() {
    return this.$options.filters.viewName(this.currentView)
  },
  mounted() {
    this.loadWorkPermits()
    this.loadWorkPermitCount()
  },
  computed: {
    workpermits() {
      let workpermitList = this.$store.state.workpermit.workpermit
      let filteredList = workpermitList.filter(el => {
        if (el.moduleState && el.moduleState.status == 'Requested') {
          return el
        }
      })
      return filteredList
    },
    stateflows() {
      return this.$store.state.workpermit.stateFlows
    },
    includeParentFilter() {
      return (
        this.$route.query.includeParentFilter &&
        (this.$route.query.includeParentFilter === 'true' ||
          this.$route.query.includeParentFilter === true)
      )
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
    ...mapState({
      users: state => state.users,
      views: state => state.view.groupViews,
      currentViewDetail: state => state.view.currentViewDetail,
    }),
  },
  methods: {
    resetSelectAll() {
      this.selectedWorkPermits = []
      this.selectAll = false
    },
    loadWorkPermits() {
      let self = this
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
      self.loading = true
      self.$store
        .dispatch('workpermit/fetchWorkPermit', queryObj)
        .then(function(response) {
          self.loading = false
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
          }
        })
      self.setTitle(self.$options.filters.viewName(self.currentView))
    },
    loadWorkPermitCount() {
      let self = this
      let queryObj = {
        viewname: this.currentView,
        filters: this.filters,
        search: this.quickSearchQuery,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        count: true,
      }
      let url = '/v2/workpermit/requested'
      let params
      params = 'fetchCount=' + queryObj.count
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
      url = url + '?' + params
      self.$http
        .get(url)
        .then(function(response) {
          self.listcount = response.data.result.recordCount
          self.$emit('syncCount', response.data.result.recordCount)
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    availableStates(record) {
      let allstates = this.stateflows[
        record.stateFlowId + '_' + record.moduleState.id
      ]
      if (allstates) {
        return [...allstates].sort((a, b) => a.buttonType - b.buttonType)
      }
      return []
    },
    updateState(transition, record) {
      let recordParam = { id: record.id }
      this.$http
        .post('/v2/workpermit/update', {
          workPermitRecords: [recordParam],
          stateTransitionId: transition.id,
        })
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success('State updated successfully.')
            this.loadWorkPermits()
            this.loadWorkPermitCount()
          } else {
            this.$message.error(response.data.message)
          }
        })
    },
    transformFormData(returnObj) {
      returnObj.workPermitRecords = [{ id: returnObj.id }]
      let formData = new FormData()
      for (let key in returnObj) {
        this.$helpers.setFormData(key, returnObj[key], formData)
      }
      return formData
    },
    onFormSaved() {
      this.loadWorkPermits()
      this.loadWorkPermitCount()
    },
    deleteWorkPermits(idList) {
      let self = this
      self.$dialog
        .confirm({
          title: 'Delete Work Permit',
          message: 'Are you sure you want to delete the Work Permit(s)?',
          rbDanger: true,
          rbLabel: self.$t('common._common.delete'),
        })
        .then(function(value) {
          if (value) {
            self.actions.delete.loading = true
            self.$http
              .post('/v2/workpermit/delete', { workPermitIds: idList })
              .then(function(response) {
                self.actions.delete.loading = false
                if (response.data.responseCode === 0) {
                  self.loadWorkPermits()
                  self.loadWorkPermitCount()
                  self.$message.success('Work Permit(s) Deleted Successfully')
                  self.resetSelectAll()
                } else {
                  self.$message.error(response.data.message)
                }
              })
          }
        })
    },
    isSelected(id) {
      for (let x in this.selectedWorkPermits) {
        if (this.selectedWorkPermits[x] === id) {
          return true
        }
      }
      return false
    },
    notify(eventName) {
      Toast.create(`Event "${eventName}" was triggered.`)
    },
    hideQuickSearchBox() {
      this.showQuickSearch = false
      if (this.quickSearchQuery && this.quickSearchQuery.trim().length > 0) {
        this.quickSearchQuery = null
        this.loadWorkPermits()
      }
    },
    quickSearch() {
      if (this.quickSearchQuery && this.quickSearchQuery.trim().length > 0) {
        this.loadWorkPermits()
      }
    },
  },
  watch: {
    currentView: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadWorkPermits()
        this.loadWorkPermitCount()
      }
    },
    filters: function(newVal) {
      this.loadWorkPermits()
      this.loadWorkPermitCount()
    },
    selectAll: function(val) {
      let self = this
      if (val) {
        this.selectedWorkPermits = []
        this.workpermits.filter(function(workpermit) {
          self.selectedWorkPermits.push(workpermit.id)
        })
      } else {
        if (
          this.selectedWorkPermits.length === this.selectedWorkPermits.length
        ) {
          this.selectedWorkPermits = []
        }
      }
    },
    selectedWorkPermits: function() {
      if (this.selectedWorkPermits.length !== this.selectedWorkPermits.length) {
        this.selectAll = false
      }
    },
    page: function(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadWorkPermits()
        // this.loadWOCounts()
      }
    },
  },
  components: {
    QCheckbox,
    ApproveReject,
  },
  filters: {
    viewName: function(name) {
      if (name === 'all') {
        return 'Pending Approval Work Permits'
      }
      return 'Pending Approval'
    },
  },
}
</script>
<style>
.workpermit-entry {
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
.row.workpermit-entry.selected {
  background: #f3f4f7 !important;
  border-left: 3px solid #ff2d81 !important;
  border-right: 1px solid rgba(232, 232, 232, 0.35) !important;
  border-top: 1px solid rgba(232, 232, 232, 0.35) !important;
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
.row.workpermit-entry.checkselected {
  background: #e2f1ef;
  border-left: 3px solid #28b2a4;
  border-right: 1px solid rgba(232, 232, 232, 0.35);
  border-top: 1px solid rgba(232, 232, 232, 0.35);
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
.row.workpermit-entry:hover {
  background: #fafbfc !important;
  border-left: 3px solid #39b2c2 !important;
}
.row.workpermit-entry:hover .request-time {
  display: none;
}
.row.workpermit-entry:hover .approval-actions {
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
.workpermitCheckbox {
  position: relative;
  right: 15px;
  background-color: transparent !important;
  border: 0;
}
.workpermitCheckbox i.q-checkbox-unchecked.cursor-pointer.q-icon.material-icons,
.workpermitCheckboxall
  i.q-checkbox-unchecked.cursor-pointer.q-icon.material-icons {
  color: #cdcdcd !important;
  font-size: 22px;
  font-weight: normal;
}
.row.workpermit-entry:hover
  .workpermitCheckbox
  i.q-checkbox-unchecked.cursor-pointer.q-icon.material-icons {
  color: gray !important;
}
.workpermitCheckboxall:hover
  i.q-checkbox-unchecked.cursor-pointer.q-icon.material-icons {
  color: gray !important;
}
.q-checkbox.q-option.cursor-pointer.no-outline.q-focusable.row.inline.no-wrap.items-center.workpermitCheckboxall {
  position: relative;
  left: 10px;
  background-color: transparent !important;
  border: 0;
}
.workpermitCheckboxall span.q-option-label {
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
.workpermit-appoval .work-approve-btn {
  background: #39b2c2;
  padding: 6px 16px;
  text-align: center;
  color: #fff !important;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  margin-right: 5px;
  margin-top: 4px;
}
.workpermit-appoval .work-reject-btn {
  padding: 5px 16px;
  background: #dc7171;
  border: 1px solid transparent;
  border-radius: 3px;
  margin-left: 10px;
  text-transform: uppercase;
  color: #fff !important;
  font-weight: 600;
  font-size: 11px;
  margin-left: 5px;
  margin-top: 4px;
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
.workpermit-entry .q-item-section + .q-item-section {
  margin-left: 14px !important;
  position: relative;
}
.list-view-filter {
  top: -14px !important;
}
.el-dropdown {
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
/* .workpermit-entry:nth-child(even){
       background-color: #fbfbfb;
    } */
.wo-req-desc {
  font-size: 13px;
  line-height: 1.57;
  letter-spacing: normal;
  color: #78787e;
  text-transform: lowercase;
}
.workpermit-appoval .approve-btn-hover {
  height: 80%;
  position: absolute;
  right: 0px;
  z-index: 1;
  background: #fafbfc !important;
  top: 0px;
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
/* .approval-inner-list:nth-child(even){
    background-color: #fbfbfb;
  } */
.fc-column-view-right-approval {
  background-color: #f7f8f9;
  min-height: 100vh;
  position: relative;
  margin-left: -2px;
  border-left: 1px solid rgba(0, 0, 0, 0.1);
  flex: 0 0 71.99999% !important;
  max-width: 71.99999% !important;
}
.el-dropdown {
  letter-spacing: 0.4px;
}
.el-dropdown .el-icon--right {
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
.workpermit-appoval .approval-actions {
  margin-top: 0px;
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

.row.workorder-entry:hover .request-actions {
  display: block;
  padding-left: 50px;
  margin-right: 20px;
}
.approve-btn {
  padding: 10px 20px;
  font-weight: 500;
  border-radius: 3px;
  background-color: #39b2c2;
  color: #fff;
  cursor: pointer;
  font-size: 12px;
  letter-spacing: 0.3px;
  margin-right: 15px;
  text-transform: uppercase;
}
.approve-btn:hover {
  background: #33a6b5;
  transition: all 0.5s ease-in-out;
  -webkit-transition: all 0.5s ease-in-out;
}
.reject-btn {
  padding: 10px 20px;
  font-weight: 500;
  border-radius: 3px;
  border: solid 1px #39b2c2;
  color: #39b2c2;
  margin-left: 10px;
  cursor: pointer;
  font-size: 12px;
  letter-spacing: 0.3px;
  text-transform: uppercase;
}
.reject-btn:hover {
  background: #39b2c2;
  color: #fff;
}
</style>
