<template>
  <div>
    <div v-if="!summaryData">
      <spinner :show="!summaryData"></spinner>
    </div>
    <div v-else class="fc__layout__flexes fc__layout__has__row fc__layout__box">
      <div class="fc__layout__flexes fc__layout__has__row fc__layout__box">
        <div
          class="fc__layout__flexes fc__layout__has__columns fc__layout__box"
        >
          <div class="fc__layout__flexes fc__layout__has__columns">
            <div class="fc__layout__has__row fc__layout__box">
              <!--sub header -->
              <div
                class="fc__layout__has__row fc__submenu__left fc__layout__box"
              >
                <div
                  class="fc__layout_media_center fc__submenu__header fc__layout__has__columns pointer"
                >
                  <i class="el-icon-back fw6" @click="back"></i>
                  <div class="label-txt-black pL10">
                    <el-popover
                      placement="bottom"
                      width="170"
                      v-model="toggle"
                      popper-class="popover-height inventory-list-popover"
                      trigger="click"
                    >
                      <ul>
                        <li
                          @click="switchCategory(view)"
                          v-for="(view, index) in views"
                          :key="index"
                          :class="{
                            active:
                              currentViewDetail.displayName ===
                              view.displayName,
                          }"
                        >
                          {{ view.displayName }}
                        </li>
                      </ul>
                      <span slot="reference">
                        {{ currentViewDetail.displayName }}
                        <i
                          class="el-icon-arrow-down el-icon-arrow-down-tv"
                          style="padding-left:8px"
                        ></i>
                      </span>
                    </el-popover>
                  </div>
                  <!-- <div class="pointer" @click="showQuickSearch = !showQuickSearch">
                    <i class="fa fa-search asset__search__icon" aria-hidden="true"></i>
                  </div>-->
                  <div class="row" v-if="showQuickSearch">
                    <div class="col-12 fc-list-search">
                      <div
                        class="fc-list-search-wrapper asset__inventory_search__con"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="32"
                          height="32"
                          viewBox="0 0 32 32"
                          class="search-icon-asset hide"
                        >
                          <title>search</title>
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
                          class="quick-search-input-asset asset__inventory__search"
                        />
                        <svg
                          @click="closeSearch"
                          xmlns="http://www.w3.org/2000/svg"
                          width="32"
                          height="32"
                          viewBox="0 0 32 32"
                          class="asset__inventory__close"
                          aria-hidden="true"
                        >
                          <title>close</title>
                          <path
                            d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"
                          />
                        </svg>
                      </div>
                    </div>
                  </div>
                </div>
                <!-- sub section -->
                <div
                  class="fc__layout__flexes overflo-auto fc__layout__has__row fc__layout__box layout__box__height"
                >
                  <div
                    class="fc__submenu__section fc__layout__has__row fc__layout__max__row"
                  >
                    <div class="pT10">
                      <div
                        class="label-txt-black pb20 flex-middle justify-content-space"
                        @click="getSummaryLink(reserve.id)"
                        v-for="reserve in reservations"
                        :key="reserve.id"
                        v-bind:class="{ fcactivelist: id === reserve.id }"
                      >
                        <div
                          class="width220px textoverflow-ellipsis"
                          :title="
                            reserve.name
                              ? reserve.name
                              : '[#' + reserve.parentId + ']'
                          "
                          v-tippy="{
                            placement: 'top',
                            animation: 'shift-away',
                            arrow: true,
                          }"
                        >
                          {{
                            reserve.name
                              ? reserve.name
                              : '[#' + reserve.id + ']'
                          }}
                        </div>
                        <img
                          src="~assets/black-arrow-right.svg"
                          class="fR hide mT3"
                          width="15"
                          height="15"
                          v-bind:class="{ show: id === reserve.id }"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <!-- main section -->
              <div
                class="fc__layout__flexes fc__layout__has__row fc__layout__asset_main"
              >
                <div class>
                  <!-- man header -->
                  <div
                    class="fc__layout__align fc__asset__main__header pT24 pB20 pL20 pR20"
                  >
                    <div class="fc-dark-grey-txt18">
                      <div class="fc-id">#{{ summaryData.id }}</div>
                      <div class="fc-black-17 bold inline-flex">
                        {{ summaryData.name }}
                        <div class="fc-newsummary-chip mL10">
                          {{ getReservationStatus(summaryData.status) }}
                        </div>
                      </div>
                    </div>
                    <div class="fc__layout__align inventory-overview-btn-group">
                      <el-button
                        class="fc__add__btn fc__border__btn"
                        @click="changeStatus(2)"
                        v-if="showCheckinButtonVisibility"
                        >CHECK-IN</el-button
                      >
                      <el-button
                        class="fc__add__btn"
                        @click="changeStatus(4)"
                        v-if="summaryData.status === 1"
                        >CANCEL</el-button
                      >
                      <el-button
                        class="fc__add__btn"
                        @click="changeStatus(3)"
                        v-if="summaryData.status === 2"
                        >CHECK-OUT</el-button
                      >
                      <el-dropdown
                        v-if="summaryData.status === 1"
                        class="mL15 fc-btn-ico-lg pT5 pB5 pL8 pR8 pointer"
                        @command="handleCommand"
                      >
                        <span class="el-dropdown-link">
                          <img src="~assets/menu.svg" height="18" width="18" />
                        </span>
                        <el-dropdown-menu slot="dropdown">
                          <el-dropdown-item
                            v-if="summaryData.status === 1"
                            command="edit"
                            >Edit</el-dropdown-item
                          >
                          <el-dropdown-item
                            v-if="summaryData.status === 1"
                            command="delete"
                            >Delete</el-dropdown-item
                          >
                          <el-dropdown-item
                            v-if="summaryData.status === 1"
                            command="controllogic"
                            >Add Control Logic</el-dropdown-item
                          >
                        </el-dropdown-menu>
                      </el-dropdown>
                    </div>
                  </div>
                </div>
                <!-- main section -->
                <div
                  class="fc__layout__flexes fc__main__con__width"
                  v-if="summaryData !== null"
                >
                  <div class="fc__asset__main__scroll">
                    <div class="fc__white__bg__asset">
                      <el-row>
                        <el-col :span="16" class="mB30">
                          <el-col :span="24" class="pB30">
                            <div class="fc-blue-label">Description</div>
                            <div
                              class="label-txt-black pT10 inventory-item-desc textoverflow-height-ellipsis2 height40"
                              v-if="summaryData.description"
                            >
                              {{ summaryData.description }}
                            </div>
                            <div
                              class="label-txt-black pT10 inventory-item-desc textoverflow-height-ellipsis2 height40 pointer f13"
                              @click="
                                addDescDialog = true
                                addDescriptionText = null
                              "
                              v-else
                            >
                              <span
                                title="Click to add short description"
                                v-tippy="{ arrow: true }"
                                >No Description</span
                              >
                            </div>
                            <el-dialog
                              :visible.sync="addDescDialog"
                              :fullscreen="false"
                              title="Add Description"
                              open="top"
                              width="400px"
                              custom-class="inventory-dialog fc-dialog-center-container fc-web-form-dialog"
                              :append-to-body="true"
                            >
                              <div>
                                <div class="readingcard-setlayout">
                                  <div class="reading-setcard-body">
                                    <div style="margin-top: 7px;" class="pB10">
                                      <div
                                        class="inventory-select-item-con mT15 mB15"
                                      >
                                        <el-input
                                          type="textarea"
                                          placeholder="Enter Description"
                                          class="fc-input-full-border-textarea"
                                          v-model="addDescriptionText"
                                          :autosize="{ minRows: 3 }"
                                        ></el-input>
                                      </div>
                                    </div>
                                  </div>
                                </div>
                              </div>
                              <div class="modal-dialog-footer">
                                <el-button
                                  class="modal-btn-cancel"
                                  @click="addDescDialog = false"
                                  >CANCEL</el-button
                                >
                                <el-button
                                  class="modal-btn-save"
                                  type="primary"
                                  @click="addDescription()"
                                  >ADD</el-button
                                >
                              </div>
                            </el-dialog>
                          </el-col>
                          <el-col :span="12" class="mB30">
                            <div class="fc-blue-label">Space</div>
                            <div class="label-txt-black pT5">
                              {{
                                summaryData.space
                                  ? summaryData.space.name
                                  : '---'
                              }}
                            </div>
                          </el-col>
                          <el-col :span="12" class="mB30">
                            <div class="fc-blue-label">No Of Attendees</div>
                            <div class="label-txt-black pT5">
                              {{
                                summaryData.noOfAttendees > 0
                                  ? summaryData.noOfAttendees
                                  : '---'
                              }}
                            </div>
                          </el-col>
                          <el-col :span="12" class="mB30">
                            <div class="fc-blue-label">Reserved For</div>
                            <div class="label-txt-black pT5">
                              {{
                                summaryData.reservedFor
                                  ? summaryData.reservedFor.name
                                  : '---'
                              }}
                            </div>
                          </el-col>
                          <el-col :span="12" class="mB30">
                            <div class="fc-blue-label">Duration Type</div>
                            <div class="label-txt-black pT5">
                              {{
                                summaryData.durationType
                                  ? getDurationTypeEnumValue(
                                      summaryData.durationType
                                    )
                                  : '---'
                              }}
                            </div>
                          </el-col>
                        </el-col>
                      </el-row>
                    </div>
                    <div class="mT20 mB10 inventory-section-header">
                      Summary
                    </div>
                    <div class="fc__white__bg__info">
                      <el-row
                        :class="
                          summaryData.actualStartTime > 0 ||
                          summaryData.actualEndTime > 0
                            ? 'border-bottom6 pB20'
                            : ''
                        "
                      >
                        <el-col :span="12">
                          <el-col :span="12">
                            <div class="fc-blue-label">
                              Scheduled Start Time
                            </div>
                          </el-col>
                          <el-col :span="12">{{
                            summaryData.scheduledStartTime
                              ? $options.filters.formatDate(
                                  summaryData.scheduledStartTime,
                                  false
                                )
                              : '---'
                          }}</el-col>
                        </el-col>
                        <el-col :span="12">
                          <el-col :span="12">
                            <div class="fc-blue-label">Scheduled End Time</div>
                          </el-col>
                          <el-col :span="12">{{
                            summaryData.scheduledEndTime
                              ? $options.filters.formatDate(
                                  summaryData.scheduledEndTime,
                                  false
                                )
                              : '---'
                          }}</el-col>
                        </el-col>
                      </el-row>
                      <el-row
                        v-if="
                          summaryData.actualStartTime > 0 ||
                            summaryData.actualEndTime > 0
                        "
                        class="pT20"
                      >
                        <el-col
                          v-if="summaryData.actualStartTime > 0"
                          :span="12"
                        >
                          <el-col :span="12">
                            <div class="fc-blue-label">Actual Start Time</div>
                          </el-col>
                          <el-col :span="12">{{
                            summaryData.actualStartTime
                              ? $options.filters.formatDate(
                                  summaryData.actualStartTime,
                                  false
                                )
                              : '---'
                          }}</el-col>
                        </el-col>
                        <el-col v-if="summaryData.actualEndTime > 0" :span="12">
                          <el-col :span="12">
                            <div class="fc-blue-label">Actual End Time</div>
                          </el-col>
                          <el-col :span="12">{{
                            summaryData.actualEndTime
                              ? $options.filters.formatDate(
                                  summaryData.actualEndTime,
                                  false
                                )
                              : '---'
                          }}</el-col>
                        </el-col>
                      </el-row>
                    </div>
                    <!-- notes & attachements -->
                    <div class="fc__white__bg__p0 mT20" :key="summaryData.id">
                      <el-tabs v-model="activeName" @tab-click="handleClick">
                        <el-tab-pane label="Notes" name="first">
                          <div
                            v-if="
                              activeName === 'first' &&
                                summaryData &&
                                summaryData.id
                            "
                            class="inventory-comments"
                          >
                            <comments
                              module="reservationNotes"
                              placeholder="Add a note"
                              btnLabel="Add Note"
                              :record="summaryData"
                              :notify="false"
                            ></comments>
                          </div>
                        </el-tab-pane>
                        <el-tab-pane label="Attachments" name="second">
                          <div v-if="activeName === 'second'">
                            <attachments
                              module="reservationattachments"
                              v-if="summaryData"
                              :record="summaryData"
                            ></attachments>
                          </div>
                        </el-tab-pane>
                      </el-tabs>
                    </div>
                    <div class="fc__white__bg__p0 mT20">
                      <el-tabs
                        v-model="attendeeTab"
                        @tab-click="handleClick"
                        class="clearboth"
                      >
                        <el-tab-pane label="Internal Attendees" name="internal">
                          <el-table
                            height="400"
                            class="mT20 width100 inventory-inner-table clearboth"
                            :data="summaryData.internalAttendees || []"
                            empty-text="No Internal Attendees."
                            :default-sort="{
                              prop: 'name',
                              order: 'descending',
                            }"
                          >
                            <template slot="empty">
                              <img
                                class="mT50"
                                src="~statics/noData-light.png"
                                width="100"
                                height="100"
                              />
                              <div class="mT10 label-txt-black f14 op6">
                                No Internal Attendees.
                              </div>
                            </template>
                            <el-table-column prop="name" sortable label="NAME">
                              <template v-slot="user">
                                <user-avatar
                                  size="md"
                                  :user="user.row"
                                ></user-avatar>
                              </template>
                            </el-table-column>
                            <el-table-column
                              prop="email"
                              sortable
                              label="E-MAIL"
                            ></el-table-column>
                            <el-table-column
                              prop="phone"
                              sortable
                              label="PHONE"
                            ></el-table-column>
                          </el-table>
                        </el-tab-pane>
                        <el-tab-pane label="External Attendees" name="external">
                          <el-table
                            height="400"
                            :data="summaryData.externalAttendees || []"
                            class="width100 inventory-inner-table"
                            empty-text="No External Attendees."
                            :default-sort="{
                              prop: 'name',
                              order: 'descending',
                            }"
                          >
                            <template slot="empty">
                              <img
                                class="mT50"
                                src="~statics/noData-light.png"
                                width="100"
                                height="100"
                              />
                              <div class="mT10 label-txt-black f14 op6">
                                No External Attendees.
                              </div>
                            </template>
                            <el-table-column
                              prop="name"
                              sortable
                              label="NAME"
                            ></el-table-column>
                            <el-table-column
                              prop="email"
                              sortable
                              label="E-MAIL"
                            ></el-table-column>
                          </el-table>
                        </el-tab-pane>
                      </el-tabs>
                    </div>
                    <div class="fc__white__bg__p0 mT20">
                      <el-tabs
                        v-model="controlActionTab"
                        @tab-click="handleClick"
                        class="clearboth fc-tab-hide"
                      >
                        <el-tab-pane
                          label="Control Actions"
                          name="controlaction"
                        >
                          <el-table
                            height="400"
                            class="mT20 width100 inventory-inner-table clearboth"
                            :data="controlActions"
                            empty-text="No Control Actions Attendees."
                            :default-sort="{
                              prop: 'name',
                              order: 'descending',
                            }"
                          >
                            <template slot="empty">
                              <img
                                class="mT50"
                                src="~statics/noData-light.png"
                                width="100"
                                height="100"
                              />
                              <div class="mT10 label-txt-black f14 op6">
                                No Control Actions.
                              </div>
                            </template>
                            <el-table-column
                              prop="name"
                              sortable
                              label="NAME"
                            ></el-table-column>
                            <el-table-column
                              prop="createdTime"
                              sortable
                              label="CREATED TIME"
                            >
                              <template v-slot="scope">{{
                                scope.row.createdTime | formatDate(true)
                              }}</template>
                            </el-table-column>
                          </el-table>
                        </el-tab-pane>
                      </el-tabs>
                    </div>
                  </div>
                  <reservation-form
                    v-if="showCreateNewDialog"
                    :editObjId="summaryData.id"
                    :showCreateNewDialog.sync="showCreateNewDialog"
                    @saved="loadSummary"
                  ></reservation-form>
                  <new-control-logic-form
                    v-if="controlLogicDialogVisibility"
                    :visibility.sync="controlLogicDialogVisibility"
                    :isNew="true"
                    @saved="loadControlActions"
                    :reservationEditObj="summaryData"
                  ></new-control-logic-form>
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
import Attachments from '@/relatedlist/Attachments'
import Comments from '@/relatedlist/Comments'
import ReservationForm from './ReservationForm'
import UserAvatar from '@/avatar/User'
import moment from 'moment'
import NewControlLogicForm from 'pages/controls/ControlLogic/NewControlLogicForm'
export default {
  components: {
    Comments,
    Attachments,
    ReservationForm,
    UserAvatar,
    NewControlLogicForm,
  },
  data() {
    return {
      activeName: 'first',
      attendeeTab: 'internal',
      showQuickSearch: false,
      quickSearchQuery: null,
      toggle: false,
      loading: true,
      page: 1,
      fetchingMore: false,
      summaryData: null,
      saving: false,
      addDescDialog: false,
      addDescriptionText: null,
      showCreateNewDialog: false,
      controlActions: [],
      controlActionTab: 'controlaction',
      controlLogicDialogVisibility: false,
    }
  },
  mounted() {
    this.$store.dispatch('view/clearViews')
    this.$store.dispatch('view/loadViews', this.getCurrentModule)
    this.$store.dispatch('view/loadModuleMeta', this.getCurrentModule)
    this.getViewDetail()
    this.loadData()
    this.loadSummary()
  },
  computed: {
    id() {
      return parseInt(this.$route.params.id)
    },
    reservations() {
      return this.$store.state.reservation.reservations
    },
    getCurrentModule() {
      return 'reservation'
    },
    canLoadMore() {
      return this.$store.state.reservation.canLoadMore
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
    currentView() {
      if (this.$route.query.search) {
        this.views.push({
          displayName: 'Filtered Reservation',
          name: 'filteredreservations',
        })
        return 'Filtered Reservation'
      }
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    currentViewDetail() {
      if (this.$route.query.search) {
        return {
          displayName: 'Filtered Reservation',
          name: 'filteredreservations',
        }
      }
      return this.views.find(view => view.name === this.currentView) || {}
    },
    views() {
      return this.$store.state.view.views
    },
    searchQuery() {
      return this.$store.state.reservation.quickSearchQuery
    },
    showCheckinButtonVisibility() {
      let time = moment().valueOf()
      if (this.summaryData.status === 1) {
        if (this.summaryData.scheduledStartTime - time <= 1800000) {
          return true
        } else {
          return false
        }
      }
      return false
    },
  },
  watch: {
    reservations(newVal) {
      if (this.$route.params.id === 'null' && newVal && newVal.length > 0) {
        this.getSummaryLink(newVal[0].id)
      }
    },
    id(newVal) {
      if (this.$route.query.refresh) {
        this.loadData()
      }
      this.loadSummary()
    },
    searchQuery() {
      this.loadData()
    },
    currentView(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.loadData()
      }
    },
  },
  methods: {
    addDescription() {
      this.addDescDialog = false
      if (this.addDescriptionText) {
        let self = this
        let param = {
          reservation: {
            id: this.summaryData.id,
            description: this.addDescriptionText,
          },
        }
        this.$http
          .post('v2/reservations/update', param)
          .then(function(response) {
            if (response.data.responseCode === 0) {
              self.$message.success('Description updated successfully.')
              self.summaryData.description = self.addDescriptionText
            } else {
              self.$message.error(response.data.message)
            }
          })
          .catch(error => {
            console.log(error)
          })
      }
    },
    loadSummary() {
      let self = this
      let url = '/v2/reservations/' + this.id
      if (this.id) {
        this.$http
          .get(url)
          .then(response => {
            self.summaryData =
              response.data.result && response.data.result.reservation
                ? response.data.result.reservation
                : []
            this.loadControlActions()
          })
          .catch(function(error) {
            console.log(error)
          })
      }
    },
    loadControlActions() {
      let param = {
        moduleName: 'reservation',
        parentId: this.summaryData.id,
      }
      this.$http.post('/v2/recordrule/list', param).then(response => {
        if (response.data.responseCode === 0) {
          this.controlActions = response.data.result.recordRuleList
            ? response.data.result.recordRuleList
            : []
        }
      })
    },
    handleClick(tab, event) {},
    closeSearch() {
      this.toggleQuickSearch()
      this.quickSearchQuery = null
      this.quickSearch()
    },
    toggleQuickSearch() {
      this.showQuickSearch = !this.showQuickSearch
    },
    quickSearch() {
      this.$store.dispatch(
        'reservation/updateSearchQuery',
        this.quickSearchQuery
      )
    },
    back() {
      let url = '/app/home/reservation/list/' + this.$route.params.viewname
      this.$router.push({ path: url, query: this.$route.query })
    },
    getViewDetail() {
      this.$store.dispatch('view/loadViewDetail', {
        viewName: this.currentView,
        moduleName: this.getCurrentModule,
      })
    },
    loadData(loadMore) {
      let self = this
      this.page = loadMore ? this.page : 1
      let queryObj = {
        viewname: this.currentView,
        page: this.page,
        filters: this.$route.query.search
          ? JSON.parse(this.$route.query.search)
          : this.filters,
        search: this.searchQuery,
        includeParentFilter: this.includeParentFilter,
      }
      loadMore ? (self.fetchingMore = true) : (self.loading = true)
      self.$store
        .dispatch('reservation/fetchReservations', queryObj)
        .then(function(response) {
          self.loading = false
          self.fetchingMore = false
          self.page++
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.fetchingMore = false
          }
        })
    },
    getSummaryLink(id) {
      let url =
        '/app/home/reservation/' +
        this.$route.params.viewname +
        '/summary/' +
        id
      this.$router.push({ path: url, query: this.$route.query })
    },
    switchCategory(index) {
      let indexs
      if (indexs && indexs !== -1) {
        this.views.splice(indexs, 1)
      }
      let url = '/app/home/reservation/' + index.name + '/summary/' + null
      this.$router.push({ path: url })
      this.toggle = false
    },
    handleCommand(command) {
      if (command === 'edit') {
        this.showCreateNewDialog = true
      } else if (command === 'delete') {
        this.delete()
      } else if (command === 'controllogic') {
        this.controlLogicDialogVisibility = true
      }
    },
    delete() {
      let self = this
      let url = '/v2/reservations/delete'
      let params = {
        id: [this.summaryData.id],
      }
      this.$http
        .post(url, params)
        .then(response => {
          if (response.data.responseCode === 0) {
            self.$message.success('Deleted Successfully')
            self.$router.push({
              path: '/app/home/reservation/list/' + 'all',
            })
          }
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    getReservationStatus(status) {
      switch (status) {
        case 1:
          return 'Scheduled'
        case 2:
          return 'On Going'
        case 3:
          return 'Finished'
        case 4:
          return 'Cancelled'
      }
    },
    getDurationTypeEnumValue(status) {
      switch (status) {
        case 1:
          return '30 Minutes'
        case 2:
          return '1 Hour'
        case 3:
          return '90 Minutes'
        case 4:
          return '2 Hours'
        case 5:
          return 'All Day'
        case 6:
          return 'Custom'
      }
    },
    changeStatus(val) {
      let param = {
        reservation: {
          id: this.summaryData.id,
          status: val,
        },
      }
      this.$http
        .post('/v2/reservations/update', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success('Status Updated Successfully')
            this.loadSummary()
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(error => {
          this.$message.error(error)
        })
    },
  },
}
</script>
