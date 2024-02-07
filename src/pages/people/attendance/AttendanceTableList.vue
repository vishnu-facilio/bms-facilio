<template>
  <div>
    <div class="fc__layout__flexes fc__layout__has__row fc__layout__box">
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
                <div v-if="loading.user" class="flex-middle fc-empty-white">
                  <spinner :show="loading.user" size="80"></spinner>
                </div>
                <div
                  v-else
                  class="fc__layout_media_center fc__submenu__header fc__layout__has__columns pointer"
                >
                  <div class="people-user-title pL10">
                    {{ $t('people.attendance.users_list') }}
                  </div>
                  <div class="pointer" @click="toggleQuickSearch">
                    <i
                      class="fa fa-search asset__search__icon"
                      aria-hidden="true"
                    ></i>
                  </div>
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
                          :placeholder="$t('common._common.search')"
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
                          <title>{{ $t('common._common.close') }}</title>
                          <path
                            d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"
                          ></path>
                        </svg>
                      </div>
                    </div>
                  </div>
                </div>
                <!-- sub section -->
                <div
                  class="fc__layout__flexes overflo-auto fc__layout__has__row fc__layout__box layout__box__people__height"
                >
                  <div
                    class="fc__submenu__section__people fc__layout__has__row fc__layout__max__row"
                  >
                    <div>
                      <div
                        class="label-txt-black pb20 flex-middle justify-content-space"
                        @click="changeSelectedUser(user)"
                        v-for="user in usersList"
                        :key="user.id"
                        v-bind:class="{
                          fcpeopleactivelist: selectedUser.id === user.id,
                        }"
                      >
                        <div
                          class="width220px textoverflow-ellipsis"
                          :title="user.name"
                          v-tippy="{
                            placement: 'top',
                            animation: 'shift-away',
                            arrow: true,
                          }"
                        >
                          <user-avatar size="md" :user="user"></user-avatar>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- main section -->
            <div
              class="fc__layout__flexes fc__layout__has__row fc__layout__asset_main"
            >
              <!-- main body -->
              <div class="fc__layout__flexes fc__main__con__width">
                <div class="fc__people__main__scroll pL10 pR10">
                  <div
                    class="fc__white__bg__people__table fc__layout__align people-header-padding-25-40px mT10 height90px"
                  >
                    <div v-if="!loading.user">
                      <user-avatar
                        size="md"
                        :user="selectedUser"
                        :name="false"
                        :userDisplayNameClass="'f16 bold'"
                      ></user-avatar>
                      <span class="f16 bold mL8">{{ selectedUser.name }}</span>
                    </div>
                    <div
                      class="fc__layout__align m10 inventory-overview-btn-group"
                    >
                      <div class="shift-name mT11 pR20" v-if="userShiftDetails">
                        <span class="fc-blue3">{{
                          userShiftDetails.name + ': '
                        }}</span
                        ><span>
                          {{
                            convertSecondsToTimehhMMa(
                              userShiftDetails.startTime
                            ) +
                              ' - ' +
                              convertSecondsToTimehhMMa(
                                userShiftDetails.endTime
                              )
                          }}</span
                        >
                      </div>
                      <div v-if="userAttendanceButtonState">
                        <el-popover
                          v-if="userAttendanceButtonState.breakstart.length > 0"
                          popper-class="inventory-list-popover"
                          placement="bottom"
                          width="280"
                          trigger="click"
                        >
                          <el-button
                            slot="reference"
                            :loading="loading.transaction"
                            class="att-break-popover-btn mR20"
                          >
                            {{ $t('people.attendance.break_start') }}
                            <i
                              class="el-icon-arrow-down fw6 mL5 f14 text-right"
                            ></i
                          ></el-button>
                          <div>
                            <div
                              v-for="(state,
                              index) in userAttendanceButtonState.breakstart"
                              :key="index"
                              @click="attendanceTransaction(state)"
                              class="break-popover-body pointer"
                            >
                              <div>
                                <span class="break-heading">{{
                                  state.breakContext.name
                                }}</span>
                                <span
                                  :class="
                                    state.breakContext.breakType === 1
                                      ? 'fc-break-chip-paid'
                                      : 'fc-break-chip-unpaid'
                                  "
                                  >{{ state.breakContext.breakTypeEnum }}</span
                                ><span class="break-time-black fR mT5">{{
                                  convertSecondsToTimeHHMM(
                                    state.breakContext.breakTime
                                  ) + 'hrs'
                                }}</span>
                              </div>
                              <div class="mT15">
                                <el-row :span="24">
                                  <el-col class="fc-blue3 f13" :span="10">{{
                                    $t('common.products.used')
                                  }}</el-col>
                                  <el-col :span="2" class="fc-black-com f12"
                                    >:
                                  </el-col>
                                  <el-col :span="10" class="fc-black-com f12">{{
                                    convertSecondsToTimeHHMM(
                                      state.breakConsumedTime / 1000
                                    ) + 'hrs'
                                  }}</el-col>
                                </el-row>
                                <el-row :span="24" class="mT10">
                                  <el-col class="fc-blue3 f13" :span="10">{{
                                    $t('common.wo_report.remaining')
                                  }}</el-col>
                                  <el-col :span="2" class="fc-black-com f12"
                                    >:</el-col
                                  >
                                  <el-col :span="10" class="fc-black-com f12">{{
                                    convertSecondsToTimeHHMM(
                                      state.breakContext.breakTime -
                                        state.breakConsumedTime / 1000
                                    ) + 'hrs'
                                  }}</el-col>
                                </el-row>
                              </div>
                            </div>
                          </div>
                        </el-popover>
                        <!-- <div v-for="(state, index) in userAttendanceButtonState.breakstart" :key="index" @click="attendanceTransaction(state)" class="label-txt-black pT10 pB10 pointer pL10 list-hover">{{state.breakContext.name}} - {{convertSecondsToTimeHHMM(state.breakContext.breakTime - (state.breakConsumedTime/1000))}}</div> -->
                        <div v-if="userAttendanceButtonState.checkin">
                          <el-popover
                            placement="bottom"
                            width="150"
                            trigger="click"
                            popper-class="p0"
                          >
                            <el-button
                              slot="reference"
                              :loading="loading.transaction"
                              class="checkin-popover-btn"
                            >
                              {{ $t('people.attendance.check_in_caps') }}
                              <i
                                class="el-icon-arrow-down fw6 mL5 f14 text-right"
                              ></i
                            ></el-button>
                            <div
                              @click="
                                attendanceTransaction(
                                  userAttendanceButtonState.checkin
                                )
                              "
                              class="label-txt-black checkin-popper-padding-top pointer list-hover"
                            >
                              {{ $t('people.attendance.check_in') }}
                            </div>
                            <div
                              class="label-txt-black checkin-popper-padding-bottom pointer list-hover"
                              @click="showAddNotesDialog = true"
                            >
                              {{ $t('people.attendance.add_notes') }}
                            </div>
                          </el-popover>
                        </div>
                        <el-popover
                          placement="bottom"
                          v-if="userAttendanceButtonState.checkout"
                          width="150"
                          trigger="click"
                          popper-class="p0"
                        >
                          <el-button
                            slot="reference"
                            :loading="loading.transaction"
                            class="checkin-popover-btn"
                          >
                            {{ $t('people.attendance.check_out_caps') }}
                            <i
                              class="el-icon-arrow-down fw6 mL5 f14 text-right"
                            ></i
                          ></el-button>
                          <div
                            @click="
                              attendanceTransaction(
                                userAttendanceButtonState.checkout
                              )
                            "
                            class="label-txt-black checkin-popper-padding-top pointer list-hover"
                          >
                            {{ $t('people.attendance.check_out') }}
                          </div>
                          <div
                            class="label-txt-black pointer checkin-popper-padding-bottom list-hover"
                            @click="showAddNotesDialog = true"
                          >
                            {{ $t('people.attendance.add_notes') }}
                          </div>
                        </el-popover>
                        <el-button
                          v-if="userAttendanceButtonState.breakstop.length > 0"
                          plain
                          class="checkin-popover-btn"
                          @click="
                            attendanceTransaction(
                              userAttendanceButtonState.breakstop[0]
                            )
                          "
                          :loading="loading.transaction"
                        >
                          {{ $t('people.attendance.resume_work') }}
                        </el-button>
                      </div>
                    </div>
                  </div>
                  <div class="fc__white__bg__people__table">
                    <div
                      v-if="attendanceList && attendanceList.length > 0"
                      class="fc__layout__align fc__people__status_body"
                    >
                      <div>
                        <span
                          v-if="attendanceCount.present > 0"
                          class="q-item-label mR20"
                        >
                          <i
                            class="fa fa-circle prioritytag"
                            v-bind:style="{
                              color: getAttendanceStatusColor(1),
                            }"
                            aria-hidden="true"
                          ></i>
                          <span class="fc-black-people-days">{{
                            attendanceCount.present +
                              ' ' +
                              (attendanceCount.present > 1 ? 'Days' : 'Day')
                          }}</span>
                        </span>
                        <span
                          v-if="attendanceCount.absent > 0"
                          class="q-item-label mR20"
                        >
                          <i
                            class="fa fa-circle prioritytag"
                            v-bind:style="{
                              color: getAttendanceStatusColor(2),
                            }"
                            aria-hidden="true"
                          ></i>
                          <span class="fc-black-people-days">{{
                            attendanceCount.absent +
                              ' ' +
                              (attendanceCount.absent > 1 ? 'Days' : 'Day')
                          }}</span>
                        </span>
                        <span
                          v-if="attendanceCount.leave > 0"
                          class="q-item-label mR20"
                        >
                          <i
                            class="fa fa-circle prioritytag"
                            v-bind:style="{
                              color: getAttendanceStatusColor(3),
                            }"
                            aria-hidden="true"
                          ></i>
                          <span class="fc-black-people-days">{{
                            attendanceCount.leave +
                              ' ' +
                              (attendanceCount.leave > 1 ? 'Days' : 'Day')
                          }}</span>
                        </span>
                        <span
                          v-if="attendanceCount.holiday > 0"
                          class="q-item-label mR20"
                        >
                          <i
                            class="fa fa-circle prioritytag"
                            v-bind:style="{
                              color: getAttendanceStatusColor(4),
                            }"
                            aria-hidden="true"
                          ></i>
                          <span class="fc-black-people-days">{{
                            attendanceCount.holiday +
                              ' ' +
                              (attendanceCount.holiday > 1 ? 'Days' : 'Day')
                          }}</span>
                        </span>
                      </div>
                      <div
                        class="att-calendar-legends flex-middle"
                        v-if="viewType == 'attendancecalendarhome'"
                      >
                        <span class="mR30 flex-middle">
                          <i
                            class="fc-att-legend-icon mR10 attendance-status-P-bg"
                          >
                            P
                          </i>
                          <span class="fc-att-legend-text">
                            {{ $t('common.products.present') }}
                          </span>
                        </span>

                        <span class="mR30 flex-middle">
                          <i
                            class="fc-att-legend-icon mR10 attendance-status-A-bg"
                          >
                            A
                          </i>
                          <span class="fc-att-legend-text ">
                            {{ $t('common.products.absent') }}
                          </span>
                        </span>

                        <span class="mR30 flex-middle">
                          <i
                            class="fc-att-legend-icon mR10 attendance-status-W-bg"
                          >
                            W
                          </i>
                          <span class="fc-att-legend-text">
                            {{ $t('common._common.weekend') }}
                          </span>
                        </span>

                        <span class="mR30 flex-middle">
                          <i
                            class="fc-att-legend-icon mR10 attendance-status-L-bg"
                          >
                            L
                          </i>
                          <span class="fc-att-legend-text">
                            {{ $t('common.dialog.leave') }}
                          </span>
                        </span>
                      </div>
                    </div>
                    <div
                      class="height100 scrollable people-table-border-top"
                      v-if="viewType == 'attendancetablehome'"
                    >
                      <div
                        class="fc-list-view fc-list-table-container fc-attendance-table-td-height"
                      >
                        <div
                          v-if="loading.attendance"
                          class="flex-middle fc-empty-white"
                        >
                          <spinner
                            :show="loading.attendance"
                            size="80"
                          ></spinner>
                        </div>
                        <div v-else class="fc-attendance-chooser">
                          <div
                            class="view-column-chooser"
                            @click="showColumnSettings = true"
                          >
                            <img
                              src="~assets/column-setting.svg"
                              style="text-align: center; position: absolute; top: 35%;right: 29%;"
                            />
                          </div>
                          <el-table
                            :data="attendanceList"
                            height="auto"
                            :fit="true"
                            class="width100"
                          >
                            <template slot="empty">
                              <img
                                src="~statics/noData-light.png"
                                width="100"
                                height="100"
                                class="mT100"
                              />
                              <div class="mT10 label-txt-black f14">
                                {{ $t('people.attendance.no_attendance') }}
                              </div>
                            </template>
                            <el-table-column
                              fixed
                              prop="day"
                              label="Date"
                              width="200"
                            >
                              <template v-slot="attendance">
                                <div
                                  @click="
                                    openAttendanceTransaction(attendance.row)
                                  "
                                  class="bold"
                                >
                                  {{
                                    $options.filters.toDateFormat(
                                      attendance.row.day,
                                      'DD MMM YYYY, ddd'
                                    )
                                  }}
                                </div>
                              </template>
                            </el-table-column>
                            <el-table-column
                              :fixed="field.name === 'name'"
                              :align="
                                field.field.dataTypeEnum === 'DECIMAL'
                                  ? 'right'
                                  : 'left'
                              "
                              v-for="(field, index) in viewColumns"
                              :key="index"
                              :prop="field.name"
                              :label="field.displayName"
                              min-width="250"
                              v-if="
                                !isFixedColumn(field.name) || field.parentField
                              "
                            >
                              <template v-slot="attendance">
                                <div
                                  v-if="
                                    !isFixedColumn(field.name) ||
                                      field.parentField
                                  "
                                >
                                  <div v-if="field.name === 'movable'">
                                    {{ attendance.row.type ? 'Yes' : 'No' }}
                                  </div>
                                  <div
                                    class="table-subheading"
                                    v-else-if="
                                      field.name === 'checkInTime' ||
                                        field.name === 'checkOutTime'
                                    "
                                    :class="{
                                      'text-right':
                                        field.field.dataTypeEnum === 'DECIMAL',
                                    }"
                                  >
                                    {{
                                      $options.filters.toDateFormat(
                                        attendance.row[field.name],
                                        'hh:mm a'
                                      ) || '---'
                                    }}
                                  </div>
                                  <div
                                    class="table-subheading"
                                    v-else-if="
                                      field.name === 'workingHours' ||
                                        field.name === 'totalPaidBreakHrs' ||
                                        field.name === 'totalUnpaidBreakHrs'
                                    "
                                    :class="{
                                      'text-right':
                                        field.field.dataTypeEnum === 'DECIMAL',
                                    }"
                                  >
                                    {{
                                      convertMilliSecondsToTimeHHMM(
                                        attendance.row[field.name]
                                      ) || '---'
                                    }}
                                  </div>
                                  <div
                                    class="table-subheading"
                                    v-else-if="field.name === 'status'"
                                  >
                                    <span
                                      class="q-item-label"
                                      v-if="attendance.row.status"
                                    >
                                      <i
                                        class="fa fa-circle prioritytag"
                                        v-bind:style="{
                                          color: getAttendanceStatusColor(
                                            attendance.row.status
                                          ),
                                        }"
                                        aria-hidden="true"
                                      ></i>
                                      {{
                                        attendance.row.status
                                          ? getColumnDisplayValue(
                                              field,
                                              attendance.row
                                            )
                                          : '---'
                                      }}
                                    </span>
                                  </div>
                                  <div
                                    class="table-subheading"
                                    v-else
                                    :class="{
                                      'text-right':
                                        field.field.dataTypeEnum === 'DECIMAL',
                                    }"
                                  >
                                    {{
                                      getColumnDisplayValue(
                                        field,
                                        attendance.row
                                      ) || '---'
                                    }}
                                  </div>
                                </div>
                              </template>
                            </el-table-column>
                          </el-table>
                        </div>
                        <!-- table end -->
                        <column-customization
                          :visible.sync="showColumnSettings"
                          moduleName="attendance"
                          :viewName="currentView"
                        ></column-customization>
                        <attendance-transaction
                          v-if="attendanceTransactionVisibility"
                          :visibility.sync="attendanceTransactionVisibility"
                          :attendanceObj="attendanceObj"
                        ></attendance-transaction>
                      </div>
                    </div>
                    <attendance-calendar
                      v-if="
                        viewType == 'attendancecalendarhome' &&
                          !loading.attendance
                      "
                    >
                    </attendance-calendar>
                    <div
                      v-if="
                        viewType == 'attendancecalendarhome' &&
                          loading.attendance
                      "
                      class="flex-middle fc-empty-white"
                    >
                      <spinner :show="loading.attendance" size="80"></spinner>
                    </div>
                    <div v-if="showAddNotesDialog">
                      <el-dialog
                        :visible.sync="showAddNotesDialog"
                        :fullscreen="false"
                        open="top"
                        width="30%"
                        :title="$t('common._common.add_notes')"
                        :before-close="cancelAddNotesForm"
                        custom-class="assetaddvaluedialog fc-dialog-center-container inventory-dialog fc-web-form-dialog"
                      >
                        <div class="inventory-select-item-con">
                          <el-input
                            :placeholder="
                              userAttendanceButtonState.checkin
                                ? $t('common.dialog.add_notes_for_check_in')
                                : $t('common.dialog.add_notes_for_check_out')
                            "
                            v-model="transactionRemarks"
                            class="fc-input-full-border2"
                          ></el-input>
                        </div>
                        <div class="modal-dialog-footer">
                          <el-button
                            class="modal-btn-cancel"
                            @click="cancelAddNotesForm()"
                            >{{ $t('common._common.cancel') }}</el-button
                          >
                          <el-button
                            class="modal-btn-save"
                            type="primary"
                            @click="
                              userAttendanceButtonState.checkin
                                ? attendanceTransaction(
                                    userAttendanceButtonState.checkin
                                  )
                                : attendanceTransaction(
                                    userAttendanceButtonState.checkout
                                  )
                            "
                            :loading="loading.transaction"
                            >{{
                              userAttendanceButtonState.checkin
                                ? 'CHECK-IN'
                                : 'CHECK-OUT'
                            }}</el-button
                          >
                        </div>
                      </el-dialog>
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
</template>

<script>
import moment from 'moment-timezone'
import UserAvatar from '@/avatar/User'
import Spinner from '@/Spinner'
import ColumnCustomization from '@/ColumnCustomization'
import ViewMixinHelper from '@/mixins/ViewMixin'
import AttendanceTransaction from './AttendanceTransaction'
import AttendanceCalendar from './AttendanceCalendar'
export default {
  mixins: [ViewMixinHelper],
  components: {
    UserAvatar,
    Spinner,
    ColumnCustomization,
    AttendanceTransaction,
    AttendanceCalendar,
  },
  data() {
    return {
      viewType: null,
      users: null,
      selectedUser: null,
      currentViewDetail: {
        displayName: 'user 1',
      },
      showQuickSearch: false,
      quickSearchQuery: null,
      loading: {
        user: true,
        attendance: true,
        userState: true,
        transaction: false,
      },
      fetchingMore: false,
      showColumnSettings: false,
      attendanceTransactionVisibility: false,
      attendanceObj: null,
      userAttendanceState: null,
      dateObj: null,
      userAttendanceButtonState: null,
      userShiftDetails: null,
      transactionRemarks: null,
      workDuration: null,
      userCurrentDayAttendanceObj: null,
      showAddNotesDialog: false,
      attendanceCount: {
        present: 0,
        absent: 0,
        leave: 0,
        holiday: 0,
      },
    }
  },
  mounted() {
    this.loadUsersList()
  },
  computed: {
    usersList() {
      let list = this.users
      let self = this
      if (this.quickSearchQuery) {
        return list.filter(data => {
          if (
            data.name
              .toLowerCase()
              .indexOf(self.quickSearchQuery.toLowerCase()) > -1
          ) {
            return data
          }
        })
      } else {
        return this.users
      }
    },
    attendanceList() {
      return this.$store.state.attendance.attendanceList
    },
    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    canLoadMore() {
      return this.$store.state.attendance.canLoadMore
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
    searchQuery() {
      return this.$store.state.attendance.quickSearchQuery
    },
    startTime() {
      return this.$route.query.startTime
    },
    endTime() {
      return this.$route.query.endTime
    },
    time() {
      if (this.workDuration && this.workDuration > 0) {
        return this.userCurrentDayAttendanceObj.lastCheckInTime
      } else {
        return moment()
          .tz(this.$timezone)
          .valueOf()
      }
    },
  },
  watch: {
    startTime(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.loadAttendanceList()
      }
    },
    endTime(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.loadAttendanceList()
      }
    },
    '$route.name': {
      handler: function(newVal, oldVal) {
        this.viewType = newVal
      },
      immediate: true,
    },
  },
  methods: {
    changeSelectedUser(user) {
      if (user && user.id) {
        this.selectedUser = user
        this.loadAttendanceList()
        this.loadUserAttendanceStateObject()
        this.loadUserCurrentDayAttendanceObject()
      }
    },
    toggleQuickSearch() {
      this.showQuickSearch = !this.showQuickSearch
    },
    closeSearch() {
      this.toggleQuickSearch()
      this.quickSearchQuery = null
    },
    loadUsersList() {
      this.loading.user = true
      this.$http
        .get('/setup/userlist')
        .then(response => {
          if (response.status === 200) {
            this.users = response.data.users
            this.selectedUser = response.data.users[0]
            this.loading.user = false
            this.loadAttendanceList()
            this.loadUserAttendanceStateObject()
            this.loadUserCurrentDayAttendanceObject()
          } else {
            this.$message.error('Error Occurred')
          }
        })
        .catch(error => {
          console.log(error)
          this.loading.user = false
        })
    },
    loadAttendanceList(loadMore) {
      if (
        !this.startTime ||
        !this.endTime ||
        !this.selectedUser ||
        !this.selectedUser.id
      ) {
        return
      }
      let self = this
      let queryObj = {
        viewname: this.currentView,
        // page: this.page,
        // filters: this.filters,
        search: this.searchQuery,
        isNew: true,
        includeParentFilter: this.includeParentFilter,
      }
      queryObj['filters'] = {
        user: {
          operatorId: 36,
          value: [this.selectedUser.id + ''],
        },
        day: {
          operator: 'between',
          value: [this.startTime + '', this.endTime + ''],
        },
      }
      self.loading.attendance = true
      self.$store
        .dispatch('attendance/fetchAttendanceList', queryObj)
        .then(function(response) {
          self.loading.attendance = false
          self.fetchingMore = false
          self.fillAttendanceCount()
        })
        .catch(function(error) {
          if (error) {
            self.loading.attendance = false
            self.fetchingMore = false
          }
        })
    },
    loadUserCurrentDayAttendanceObject() {
      let self = this
      let filters = {
        user: {
          operatorId: 36,
          value: [this.selectedUser.id + ''],
        },
        day: {
          operator: 'between',
          value: [
            moment()
              .startOf('day')
              .valueOf() + '',
            moment()
              .endOf('day')
              .valueOf() + '',
          ],
        },
      }
      this.$http
        .get(
          '/v2/attendance/view/all?&filters=' +
            encodeURIComponent(JSON.stringify(filters))
        )
        .then(response => {
          if (response.data.responseCode === 0) {
            let temp = response.data.result.attendance
            if (temp && temp !== null && temp.length > 0) {
              self.userCurrentDayAttendanceObj = temp[0]
            } else {
              self.userCurrentDayAttendanceObj = null
            }
          } else {
            console.log(response.data.message)
          }
        })
        .catch(error => {
          console.log(error)
        })
    },
    fillAttendanceCount() {
      for (let key in this.attendanceCount) {
        this.attendanceCount[key] = 0
      }
      if (this.attendanceList && this.attendanceList.length > 0) {
        for (let atObj of this.attendanceList) {
          switch (atObj.status) {
            case 1:
              this.attendanceCount.present++
              break
            case 2:
              this.attendanceCount.absent++
              break
            case 3:
              this.attendanceCount.leave++
              break
            case 4:
              this.attendanceCount.holiday++
              break
          }
        }
      }
    },
    openAttendanceTransaction(attendance) {
      this.attendanceTransactionVisibility = true
      this.attendanceObj = attendance
    },
    loadUserAttendanceStateObject() {
      let param = {
        userId: this.selectedUser.id,
        time: moment().valueOf(),
      }
      this.loading.userState = true
      this.$http
        .post('/v2/attendance/getState', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.userAttendanceState = response.data.result.record
            this.userShiftDetails = response.data.result.shift
            this.loading.userState = false
            this.userStateButtonFormatting()
          } else {
            this.userAttendanceState = []
            this.$message.error(response.data.message)
            this.loading.userState = false
          }
        })
        .catch(error => {
          console.log(error)
          this.loading.userState = false
        })
    },
    userStateButtonFormatting() {
      let atState = this.userAttendanceState
      let stateButtonObj = {
        checkin: null,
        checkout: null,
        breakstart: [],
        breakstop: [],
      }
      for (let state of atState) {
        if (state.type === 'CHECKIN') {
          stateButtonObj.checkin = state
        } else if (state.type === 'CHECKOUT') {
          stateButtonObj.checkout = state
        } else if (state.type === 'BREAKSTART') {
          stateButtonObj.breakstart.push(state)
        } else if (state.type === 'BREAKSTOP') {
          stateButtonObj.breakstop.push(state)
        }
      }
      this.userAttendanceButtonState = stateButtonObj
    },
    getButtonType(state) {
      switch (state.type) {
        case 'CHECKIN':
          return 'success'
        case 'CHECKOUT':
          return 'danger'
        case 'BREAKSTART':
          return 'warning'
        case 'BREAKSTOP':
          return 'info'
      }
    },
    attendanceTransaction(state) {
      let self = this
      if (state.type === 'CHECKIN' || state.type === 'CHECKOUT') {
        let param1 = {
          attendanceTransaction: {
            sourceType: 1,
            transactionType: state.type === 'CHECKIN' ? 1 : 2,
            transactionTime: moment().valueOf(),
            user: { ouid: this.selectedUser.id },
            remarks: this.transactionRemarks,
          },
        }
        this.loading.transaction = true
        this.$http
          .post('/v2/attendance/add', param1)
          .then(response => {
            if (response.data.responseCode === 0) {
              if (state.type === 'CHECKIN') {
                self.$message.success(
                  this.$t('common.products.user_checked_in_successfully')
                )
              } else {
                self.$message.success(
                  this.$t('common.products.user_checked_out_successfully')
                )
              }
              self.refreshList()
              self.loading.transaction = false
            } else {
              self.$message.error(response.data.message)
              self.loading.transaction = false
            }
            this.cancelAddNotesForm()
          })
          .catch(error => {
            console.log(error)
          })
      } else if (state.type === 'BREAKSTART' || state.type === 'BREAKSTOP') {
        let param2 = {
          breakTransaction: {
            transactionType: state.type === 'BREAKSTART' ? 1 : 2,
            breakId: {
              id: state.breakContext.id,
            },
            sourceType: 1,
          },
          userId: this.selectedUser.id,
        }
        if (state.type === 'BREAKSTART') {
          param2.breakTransaction['startTime'] = moment().valueOf()
        } else if (state.type === 'BREAKSTOP') {
          param2.breakTransaction['stopTime'] = moment().valueOf()
        }
        this.loading.transaction = true
        this.$http
          .post('/v2/breakTransaction/add', param2)
          .then(response => {
            if (response.data.responseCode === 0) {
              if (state.type === 'BREAKSTART') {
                self.$message.success(
                  this.$t('common.products.break_started_successfully')
                )
              } else {
                self.$message.success(
                  this.$t('common.products.break_ended_successfully')
                )
              }
              self.refreshList()
              self.loading.transaction = false
            } else {
              self.$message.error(response.data.message)
              self.loading.transaction = false
            }
          })
          .catch(error => {
            console.log(error)
          })
      }
    },
    refreshList() {
      this.loadAttendanceList()
      this.loadUserAttendanceStateObject()
      this.loadUserCurrentDayAttendanceObject()
    },
    convertMilliSecondsToTimeHHMM(val) {
      if (val > 0) {
        return moment()
          .startOf('day')
          .milliseconds(val)
          .format('HH:mm')
      } else {
        return '00:00'
      }
    },
    convertSecondsToTimeHHMM(val) {
      if (val > 0) {
        return moment()
          .startOf('day')
          .seconds(val)
          .format('HH:mm')
      } else {
        return '00:00'
      }
    },
    getAttendanceStatusColor(val) {
      switch (val) {
        case 1:
          return '#bee279'
        case 2:
          return '#e48594'
        case 3:
          return '#dc4343'
        case 4:
          return '#85b9e5'
      }
    },
    timerShowConditon() {
      if (this.userCurrentDayAttendanceObj) {
        if (
          this.userCurrentDayAttendanceObj.lastCheckInTime > 0 &&
          !(this.userCurrentDayAttendanceObj.lastBreakStartTime > 0)
        ) {
          this.workDuration = this.userCurrentDayAttendanceObj.workingHours
          return true
        } else {
          this.workDuration =
            this.userCurrentDayAttendanceObj.workingHours +
            (this.userCurrentDayAttendanceObj.lastBreakStartTime -
              this.userCurrentDayAttendanceObj.lastCheckInTime)
          return false
        }
      } else {
        this.workDuration = 0
        return false
      }
    },
    cancelAddNotesForm() {
      this.showAddNotesDialog = false
      this.transactionRemarks = null
    },
    convertSecondsToTimehhMMa(val) {
      return moment()
        .startOf('day')
        .seconds(val)
        .format('hh:mm a')
    },
  },
}
</script>
<style scoped>
/* .fc-list-table-container .el-table{
  height: 60vh !important;
  padding-bottom: 100px !important;

} */
.shift-name {
  font-size: 13px;
  letter-spacing: 0.51px;
}
.fc-break-chip-paid {
  font-size: 9px;
  font-weight: bold;
  letter-spacing: 1px;
  color: #fff;
  text-transform: uppercase;
  padding: 2px 10px;
  background: #f9bb67;
  border-radius: 7px;
  margin-left: 6px;
  align-self: center;
}
.fc-break-chip-unpaid {
  font-size: 9px;
  font-weight: bold;
  letter-spacing: 1px;
  color: #fff;
  text-transform: uppercase;
  padding: 2px 10px;
  background: #f96781;
  border-radius: 7px;
  margin-left: 6px;
  align-self: center;
}
.break-popover-body {
  width: 280px;
  padding: 20px;
  border-bottom: solid 1px #ecf1f2;
}
.break-popover-body:hover {
  background: #f3f6f9;
}
.break-time-black {
  font-size: 11px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.46px;
  color: #324056;
}
.break-heading {
  font-size: 12px;
  font-weight: bold;
  letter-spacing: 0.5px;
  color: #25243e;
  text-transform: uppercase;
}
.checkin-popper-padding-top {
  padding: 15px 15px 10px 15px;
}
.checkin-popper-padding-bottom {
  padding: 10px 15px 15px 15px;
}
</style>
