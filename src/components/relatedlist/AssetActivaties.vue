<template>
  <div class="block">
    <div class="flex-middle justify-content-space">
      <div>
        <div class="fc-black-com f14 bold pL10">
          {{ $t('asset.history.history') }}
        </div>
      </div>
      <div>
        <el-dropdown class="fc-dropdown-menu-border" trigger="click">
          <el-button type="primary" class="fc-btn-group-white">
            <div>
              {{ fixedFilter }}
              <i class="el-icon-arrow-down"></i>
            </div>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <!-- <el-dropdown-item>
              <div @click="getFilteredActivities('Move History')">
                {{ $t('asset.history.move_history') }}
              </div>
            </el-dropdown-item> -->
            <el-dropdown-item v-if="$helpers.isLicenseEnabled('COMMISSIONING')">
              <div @click="setCommissioningLogActive()">
                {{ $t('setup.decommission.commissioning_log') }}
              </div>
            </el-dropdown-item>
            <el-dropdown-item>
              <div @click="getFilteredActivities('Downtime History')">
                {{ $t('asset.history.downtime_history') }}
              </div>
            </el-dropdown-item>
            <el-dropdown-item>
              <div @click="getFilteredActivities('All History')">
                {{ $t('asset.history.all_history') }}
              </div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <div v-if="loading">
      <Spinner :show="loading" size="80"></Spinner>
    </div>
    <LogActivity
      v-else-if="logActive"
      :recordId="recordId"
      @autoResizeWidget="autoResize"
      :selectedModule="'asset'"
      :details="record"
    ></LogActivity>
    <template v-else>
      <div v-if="$validation.isEmpty(activities)">
        <div
          class="flex-middle width100 height80vh justify-center white-bg-block flex-direction-column shadow-none"
        >
          <InlineSvg
            src="svgs/emptystate/history"
            iconClass="icon icon-xxxxlg mR10"
          ></InlineSvg>
          <div class="nowo-label text-center pT10">
            {{ $t('asset.history.no_history_available') }}
          </div>
        </div>
      </div>
      <div v-else class="mT20">
        <el-timeline class="wo-activity">
          <template v-for="(activity, index) in activities">
            <el-timeline-item
              type="primary"
              :timestamp="activity.ttime | formatDate"
              :key="index"
              v-if="messageEmptyCheck(activity)"
            >
              <div v-if="operatingHourCheck(activity)">
                <avatar
                  size="sm"
                  :user="{ name: activity.doneBy.name }"
                ></avatar>
                {{ activity.doneBy.name }}
                <b>{{ $t('asset.history.updated_op_hrs') }}</b>
                <i>{{
                  $getProperty(activity, 'info.changeSet.0.newValue.name', '')
                }}</i>
              </div>
              <div v-else-if="activityTypeCheck(activity)">
                <avatar
                  size="sm"
                  :user="{ name: activity.doneBy.name }"
                ></avatar>
                {{ activity.doneBy.name }}
                {{ checkForSubActi(activity) ? updated : '' }}
                <span
                  v-if="!checkForSubActi(activity)"
                  v-html="message(activity)"
                ></span>
                <span
                  class="pointer fc-dark-blue4 f11 bold pL10"
                  v-if="moreCheck(activity)"
                  @click="enableSubActi(activity)"
                  >{{ $t('asset.history.more') }}</span
                >
                <div v-if="checkForSubActi(activity)">
                  <div class="pT10">
                    <span v-html="message(activity)"></span>
                    <span
                      class="pointer fc-dark-blue4 f11 bold pL10"
                      @click="disableActi(activity)"
                      >{{ $t('asset.history.less') }}</span
                    >
                  </div>
                  <div v-if="activity.info.designatedLocation" class="pT10">
                    {{ $t('asset.history.moved_status_is') }}
                    {{ activity.info.designatedLocation }}
                  </div>
                  <div v-if="activity.info.distanceMoved" class="pT10">
                    {{ $t('asset.history.distance_moved_is') }}
                    {{ activity.info.distanceMoved + 'm' }}
                  </div>
                </div>
              </div>
              <div v-else-if="messageEmptyCheck(activity)">
                <avatar
                  size="sm"
                  :user="{ name: activity.doneBy.name }"
                ></avatar>
                {{ activity.doneBy.name }}
                <span v-html="message(activity)"></span>
              </div>
            </el-timeline-item>
          </template>
        </el-timeline>
      </div>
    </template>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import Avatar from '@/Avatar'
import { isEmpty, isArray } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import LogActivity from '@/widget/decommission/LogActivities'
export default {
  props: ['record', 'serviceportal', 'module'],
  data() {
    return {
      showDetialsActivity: [],
      activities: [],
      activitiesList: [],
      fixedFilter: this.$t('asset.history.all_history'),
      loading: false,
      showSubActivaites: false,
      // userlist: [],
      logActive: false,
    }
  },
  components: {
    Avatar,
    Spinner,
    LogActivity,
  },
  mounted() {
    if (this.serviceportal) {
      this.loadServicePortalActivities()
    } else {
      this.loadActivities()
    }
  },
  computed: {
    ...mapState({
      // users: state => state.users,
      // sites: state => state.mysites,
      metaInfo: state => state.view.metaInfo,
    }),

    fieldsMap() {
      let { metaInfo } = this
      let map = {}
      if (!isEmpty(metaInfo)) {
        let fields = this.$getProperty(metaInfo, 'fields', [])
        fields.forEach(field => {
          this.$setProperty(map, `${field.name}`, field)
        })
      }
      return map
    },
    ...mapGetters([
      // 'getUser',
      'getTicketCategory',
      'getTicketPriority',
      'getTicketType',
    ]),
    recordId() {
      return this.$getProperty(this, 'record.id', null)
    },
  },
  watch: {
    record() {
      if (this.serviceportal) {
        this.loadServicePortalActivities()
      } else {
        this.loadActivities()
      }
    },
  },
  created() {
    // this.loadusers()
    this.$store.dispatch('view/loadModuleMeta', this.module)
    // this.$store.dispatch('loadSite')
    this.$store.dispatch('loadTicketPriority')
  },
  methods: {
    getFilteredActivities(filterType) {
      this.logActive = false
      this.fixedFilter = filterType
      let type
      type =
        filterType === 'Move History'
          ? 1
          : filterType === 'Status'
          ? 33
          : filterType === 'Downtime History'
          ? 34
          : null
      if (type === null) {
        this.activities = this.activitiesList
      } else {
        this.activities = this.activities.filter(i => i.type === type)
      }
    },
    autoResize() {
      this.$emit('resizeWidget')
    },
    setCommissioningLogActive() {
      this.logActive = true
      this.fixedFilter = this.$t('setup.decommission.commissioning_log')
    },
    checkForSubActi(activity) {
      if (
        this.showDetialsActivity.length &&
        this.showDetialsActivity.includes(activity.id) &&
        activity
      ) {
        return true
      } else {
        return false
      }
    },
    enableSubActi(activity) {
      this.showDetialsActivity.push(activity.id)
    },
    activityTypeCheck(act) {
      let { type } = act || {}
      return this.messageEmptyCheck(act) && type === 1
    },
    disableActi(activity) {
      this.showDetialsActivity = this.showDetialsActivity.filter(
        item => item !== activity.id
      )
    },
    loadServicePortalActivities() {
      let self = this
      self.loading = true
      self.modulename = 'workorders'
      self.$http
        .get('/v2/activity?workOrderId=' + self.record.id)
        .then(function(response) {
          self.activities = response.data
        })
        .finally(() => {
          self.loading = false
        })
    },
    openPm(id) {
      this.$router.replace({
        path: '/app/wo/planned/summary/' + id,
      })
    },
    // loadusers() {
    //   this.$http
    //     .get('/setup/portalusers')
    //     .then(({ data }) => {
    //       this.userlist = data.users
    //     })
    //     .catch(() => {})
    // },
    messageEmptyCheck(activity) {
      let { message } = this.getMessage(activity) || {}
      return message !== null
    },
    message(activity) {
      let { message } = this.getMessage(activity) || {}
      return message
    },
    moreCheck(activity) {
      let { info } = activity || {}
      return (
        info && Object.keys(info).length > 1 && !this.checkForSubActi(activity)
      )
    },
    // getUserName(activity) {
    //   let { doneBy } = activity || {}
    //   let { ouid: id } = doneBy || {}
    //   if (id) {
    //     let usr = []
    //     usr = this.users.filter(us => us.ouid === id)
    //     if (usr.length) {
    //       let userName = usr[0].name
    //       return userName
    //     } else if (this.userlist.length) {
    //       let us = []
    //       us = this.userlist.filter(u => u.ouid === id)
    //       if (us.length) {
    //         let userName = us[0].name
    //         return userName
    //       } else {
    //         return '---1'
    //       }
    //     } else {
    //       return '---2'
    //     }
    //   } else {
    //     return '---3'
    //   }
    // },
    loadActivities() {
      let self = this
      self.loading = true
      let params = null
      if (self.module === 'asset') {
        self.modulename = 'assets'
        params = '?assetId='
      } else if (self.module === 'workorder') {
        self.modulename = 'workorders'
        params = '?workOrderId='
      }
      self.$http
        .get('/v2/' + self.modulename + '/activity' + params + self.record.id)
        .then(function(response) {
          if (response.data.result && response.data.result.activity) {
            self.activities = response.data.result.activity
            self.activitiesList = response.data.result.activity
          }
        })
        .catch(() => {})
        .finally(() => {
          self.loading = false
        })
    },
    operatingHourCheck(activity) {
      let { type, info } = activity || {}
      let { changeSet } = info || {}
      let { message } = this.getMessage(activity) || {}
      return (
        type === 28 &&
        message !== null &&
        info &&
        changeSet &&
        this.$getProperty(changeSet, '0.field') === 'operatingHour'
      )
    },
    // getAttachmentsString(obj) {
    //   let str = ''
    //   for (let item = 0; item < obj.length; item++) {
    //     if (item !== 0) {
    //       if (item === obj.length - 1) {
    //         str += ' and '
    //       } else {
    //         str += ', '
    //       }
    //     }
    //     str += obj[item]
    //   }
    //   return str
    // },
    // getUserCall(names) {
    //   if (this.serviceportal) {
    //     if (this.$account.user.id === names) {
    //       return this.$account.user.name
    //     } else {
    //       return this.getUser(names).name
    //     }
    //   } else {
    //     return this.getUser(names).name
    //   }
    // },
    // getAssignedBy(updatedFields) {
    //   let user = updatedFields.find(el => el.fieldName === 'assignedTo')
    //   if (user.newValue) {
    //     return this.getUser(user.newValue.id).name
    //   } else if (user.oldValue) {
    //     return this.getUser(user.oldValue.id).name
    //   }
    // },
    // getLocation(value) {
    //   return this.$helpers.parseLocation(value)
    // },
    // compare(str1, str2) {
    //   return !str1.localeCompare(str2)
    // },
    // openPM(val) {
    //   this.$router.replace({
    //     path: '/app/wo/planned/summary/' + val,
    //   })
    // },
    // getTicket(info) {
    //   if (info.field === 'type') {
    //     return (
    //       '<b>' +
    //       'updated ' +
    //       info.displayName +
    //       '</b>' +
    //       ' as ' +
    //       '<i>' +
    //       this.getTicketType(info.newValue).name +
    //       '</i>'
    //     )
    //   } else if (info.field === 'category') {
    //     return (
    //       '<b>' +
    //       'updated ' +
    //       info.displayName +
    //       '</b>' +
    //       ' as ' +
    //       '<i>' +
    //       this.getTicketCategory(info.newValue).displayName +
    //       '</i>'
    //     )
    //   } else if (info.field === 'priority') {
    //     return (
    //       '<b>' +
    //       'updated ' +
    //       info.displayName +
    //       '</b>' +
    //       ' as ' +
    //       '<i>' +
    //       this.getTicketPriority(info.newValue).displayName +
    //       '</i>'
    //     )
    //   } else if (info.field === 'dueDate') {
    //     return (
    //       '<b>' +
    //       'updated ' +
    //       info.displayName +
    //       '</b>' +
    //       ' as ' +
    //       '<i>' +
    //       this.$options.filters.formatDate(info.newValue) +
    //       '</i>'
    //     )
    //   } else if (info.field === 'site') {
    //     return (
    //       '<b>' +
    //       'updated ' +
    //       info.displayName +
    //       '</b>' +
    //       ' as ' +
    //       '<i>' +
    //       this.getSiteName(info.newValue) +
    //       '</i>'
    //     )
    //   } else {
    //     return (
    //       '<b>' +
    //       'updated ' +
    //       info.displayName +
    //       '</b>' +
    //       ' as ' +
    //       '<i>' +
    //       info.newValue +
    //       '</i>'
    //     )
    //   }
    // },
    // getSiteName(siteId) {
    //   let site = this.sites.find(site => site.id === siteId)
    //   return site ? site.name : ''
    // },
    getMessage(activity) {
      let { module } = this
      if (activity) {
        if (
          activity.type === 24 &&
          activity.info.addPMWO &&
          activity.info.addPMWO.length > 2
        ) {
          return {
            message: '<b> created workorder from PM</b>',
          }
        } else if (activity && activity.type === 33) {
          return {
            message:
              '<b>' +
              'updated status ' +
              '</b>' +
              'from ' +
              '<i>' +
              activity.info.oldValue +
              ' to ' +
              activity.info.newValue +
              '</i>',
          }
        } else if (activity.type === 75 && !isEmpty(activity.info.changeSet)) {
          let filteredArr = activity.info.changeSet.filter(
            a =>
              a.field !== 'modifiedTime' &&
              a.newValue !== -99 &&
              a.oldValue !== a.newValue &&
              a.field !== 'moduleState' &&
              a.field !== 'sysModifiedTime'
          )
          if (!isEmpty(filteredArr)) {
            return {
              message:
                '<b>updated </b>' +
                filteredArr.reduce((accStr, activity) => {
                  return isEmpty(accStr)
                    ? this.getValue(activity)
                    : `${accStr}, ${this.getValue(activity)}`
                }, ''),
            }
          } else {
            return { message: null }
          }
        } else if (activity && activity.type === 34) {
          return {
            message:
              '<b>' +
              'recorded downtime as  ' +
              '</b>' +
              ' (from: ' +
              '<i>' +
              this.$options.filters.formatDate(
                activity.info.changeSet[0].from,
                true,
                false
              ) +
              ' to: ' +
              this.$options.filters.formatDate(
                activity.info.changeSet[0].to,
                true,
                false
              ) +
              ')' +
              '</i>',
          }
        } else if (
          activity.type === 28 &&
          activity.info &&
          activity.info.changeSet
        ) {
          if (
            activity.info.changeSet[0] &&
            activity.info.changeSet[0].field === 'operatingHour'
          ) {
            return {
              message: activity.info.changeSet[0].field,
            }
          } else {
            return {
              message: null,
            }
          }
        } else if (activity.type === 1) {
          let latLng
          if (
            activity.info &&
            activity.info.currentSite &&
            activity.info.currentSpace
          ) {
            return {
              message:
                '<b>' +
                'Location was updated to' +
                '</b>' +
                '<i> ' +
                activity.info.currentSpace +
                ' ( ' +
                activity.info.currentSite +
                ' ) ',
            }
          } else if (activity.info && activity.info.currentLocation) {
            latLng = activity.info.currentLocation.split(',')
            return {
              message:
                '<b>' +
                'Location was updated to' +
                '</b>' +
                '<i>' +
                '( ' +
                (latLng && latLng.length
                  ? 'lat: ' + latLng[0] + ' lng: ' + latLng[1]
                  : '') +
                ' ) ',
            }
          }
        } else if (activity.type === 30) {
          return {
            message:
              '<b>' +
              'attached ' +
              '</b>' +
              '<i>' +
              activity.info.attachment[0].Filename +
              '</i>',
          }
        } else if (activity.type === 26) {
          return {
            message:
              '<b>' +
              'updated status ' +
              '</b>' +
              'from ' +
              '<i>' +
              activity.info.oldValue +
              ' to ' +
              activity.info.newValue +
              '</i>',
          }
        } else if (activity.type === 29) {
          return {
            message:
              '<b> added the Comment </b>' +
              '<i>' +
              '(' +
              activity.info.Comment +
              ')' +
              '</i>',
          }
        } else if ((activity || {}).type === 72) {
          // Approval Activity Message
          let approvalMessage = ``
          let approvalStatus = this.$getProperty(activity, 'info.status', '')
          let moduleSingularDisplayName = this.$constants
            .moduleSingularDisplayNameMap[module]
            ? this.$constants.moduleSingularDisplayNameMap[module]
            : module
          if (approvalStatus === 'Approved') {
            approvalMessage = `<b> Approved the ${moduleSingularDisplayName}</b>`
          } else if (approvalStatus === 'Rejected') {
            approvalMessage = `<b> Rejected the ${moduleSingularDisplayName}</b>`
          } else if (approvalStatus === 'Requested') {
            approvalMessage = `<b> resent for Approval.</b>`
          }
          return {
            message: approvalMessage,
          }
        } else if ((activity || {}).type === 73) {
          return {
            message: `<b> initiated Approval process</b>`,
          }
        }
      }
      return {
        message: '<b>' + activity.message + '</b>',
      }
    },
    getValue(info) {
      let { fieldsMap } = this
      let { field: fieldName, newValue, oldValue } = info || {}
      let field = fieldsMap[fieldName] || {}
      let displayValue = newValue
      let displayOldValue = oldValue
      if (!isEmpty(field)) {
        if (!isArray(displayValue) && displayValue !== '---') {
          if (Number(displayValue)) {
            displayValue = Number(displayValue)
          }
        }
        displayValue = this.$fieldUtils.getDisplayValue(field, displayValue)

        if (!isArray(displayOldValue)) {
          if (Number(displayOldValue)) {
            displayOldValue = Number(displayOldValue)
          }
        }
        if (!isEmpty(displayOldValue)) {
          displayOldValue = this.$fieldUtils.getDisplayValue(
            field,
            displayOldValue
          )
        }
      }
      if (
        !isEmpty(displayOldValue) &&
        displayOldValue !== '---' &&
        displayOldValue !== -99
      ) {
        return (
          '<b>' +
          info.displayName +
          '</b>' +
          ' from ' +
          '<i>' +
          displayOldValue +
          '</i>' +
          ' to ' +
          '<i>' +
          displayValue +
          '</i>'
        )
      } else if (
        !isEmpty(displayValue) &&
        displayValue !== '---' &&
        displayValue !== -99
      ) {
        return (
          '<b>' +
          info.displayName +
          '</b>' +
          ' as ' +
          '<i>' +
          displayValue +
          '</i>'
        )
      }
      return ''
    },
  },
}
</script>
<style lang="scss">
.wo-activity {
  .el-timeline-item__timestamp.is-bottom {
    margin-top: 8px;
    position: absolute;
    left: -70px;
    top: -1px;
  }

  .el-timeline-item__wrapper {
    position: relative;
    padding-left: 130px;
    top: -3px;
  }

  .el-timeline-item {
    position: relative;
    padding-bottom: 20px;
    margin-left: 40px;
  }

  .el-timeline-item__node--normal {
    left: 80px;
    width: 10px;
    height: 10px;
    top: 5px;
    letter-spacing: 0.3px;
    font-size: 12px;
    border: solid 2px #ff3184;
    background: #fff;
  }

  .el-timeline-item__tail {
    margin-left: 80px;
    margin-top: 10px;
  }

  .el-timeline-item__timestamp.is-bottom {
    color: #8ca1ad;
    line-height: 1;
    font-size: 12px;
  }
}
</style>
