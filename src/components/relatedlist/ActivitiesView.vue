<template>
  <div class="block">
    <spinner v-if="loading" :show="loading"></spinner>
    <div v-else-if="!activities || !activities.length">
      {{ $t('common.products.no_activities_tracked_yet') }}
    </div>
    <el-timeline v-else class="wo-activity">
      <template v-for="(activity, index) in activities">
        <el-timeline-item
          type="primary"
          :color="activity.color"
          :timestamp="activity.ttime | formatDate"
          :key="index"
          v-if="getActivityMessage(activity).message !== null"
        >
          <div v-if="activity.type === 3 && activity.info.assigned">
            <avatar size="sm" :user="{ name: activity.doneBy.name }"></avatar>
            {{ activity.doneBy.name }}
            <template v-if="activity.info.assigned.assignedTo">
              <template v-if="activity.info.assigned.assignedTo > 0">
                <span v-html="getActivityMessage(activity).message"></span>
                {{ ' ' }}
                <avatar
                  size="sm"
                  :user="{
                    name: getUserName(
                      activity.info.assigned.assignedTo,
                      activity
                    ),
                  }"
                ></avatar>
                {{ ' ' }}
                <i>{{
                  getUserName(activity.info.assigned.assignedTo, activity)
                }}</i>
              </template>
              <template v-else>
                <b>{{ $t('common._common.removed_the_staff') }}</b>
              </template>
            </template>
            <template v-else>
              <template v-if="activity.info.assigned.assignmentGroup > 0">
                <span v-html="getActivityMessage(activity).message"></span>
                {{ $t('common.wo_report.team') }}
                <i>{{
                  getGroup(activity.info.assigned.assignmentGroup, activity)
                    .name
                }}</i>
              </template>
              <template v-else>
                <b>{{ $t('common._common.removed_the_team') }}</b>
              </template>
            </template>
          </div>
          <div v-else-if="activity.type === 24">
            <avatar size="sm" :user="{ name: activity.doneBy.name }"></avatar>
            {{ activity.doneBy.name }}
            <span v-html="getActivityMessage(activity).message"></span>
            <i>
              <span @click="openPm(activity)" class="fc-id pointer">
                #{{ getPmId(activity) }}
              </span>
            </i>
          </div>
          <div v-else-if="activity.type !== 3 && activity.type !== 24">
            <avatar size="sm" :user="{ name: activity.doneBy.name }"></avatar>
            {{ activity.doneBy.name }}
            <span v-html="getActivityMessage(activity).message"></span>
          </div>
        </el-timeline-item>
      </template>
    </el-timeline>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import Avatar from '@/Avatar'
import ActivitiesMixin from '@/widget/ActivitiesMixin.vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['record', 'serviceportal', 'module'],
  data() {
    return {
      activities: [],
      loading: false,
      userlist: [],
    }
  },
  mixins: [ActivitiesMixin],
  components: { Avatar },
  async mounted() {
    if (this.serviceportal) {
      await this.loadServicePortalActivities()
    } else {
      await this.loadActivities()
    }
    this.loadusers()
  },

  computed: {
    ...mapState({
      users: state => state.users,
      sites: state => state.site,
      metaInfo: state => state.view.metaInfo,
    }),

    ...mapGetters([
      'getUser',
      'getGroup',
      'getTicketCategory',
      'getTicketPriority',
      'getTicketType',
    ]),
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
    recordId() {
      let { record } = this
      let { id } = record || {}
      return id
    },
  },

  watch: {
    async record() {
      if (this.serviceportal) {
        await this.loadServicePortalActivities()
      } else {
        await this.loadActivities()
      }
      await this.loadusers()
    },
  },
  created() {
    this.$store.dispatch('view/loadModuleMeta', this.module)
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadTicketCategory')
  },
  methods: {
    getPmId(activity) {
      let { info } = activity || {}
      let { addPMWO } = info || {}
      let pmIdObj = (addPMWO || []).find(obj =>
        Object.prototype.hasOwnProperty.call(obj, 'pmid')
      )
      if (!isEmpty(pmIdObj)) {
        return pmIdObj.pmid
      }
      return ''
    },
    async loadServicePortalActivities() {
      this.loading = true
      this.modulename = 'workorders'
      let { data } = await API.get('/v2/activity?workOrderId=' + this.recordId)
      this.activities = data
      this.loading = false
    },
    openPm(activity) {
      let id = this.getPmId(activity)
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('preventivemaintenance', pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({
            name,
            params: {
              viewname: 'all',
              id,
            },
          })
        }
      } else {
        this.$router.replace({ path: '/app/wo/planned/summary/' + id })
      }
    },
    async loadusers() {
      this.loading = true
      let { activities } = this
      // let ouids = [],
      let assignedToIds = []
      activities.forEach(activity => {
        let { $getProperty } = this
        // let ouid = $getProperty(activity, 'doneBy.ouid', null)
        let assignedToId = $getProperty(
          activity,
          'info.assigned.assignedTo',
          null
        )

        // !isEmpty(ouid) && ouids.push(`${ouid}`)
        if (!isEmpty(assignedToId) && assignedToId !== -99)
          assignedToIds.push(`${assignedToId}`)
      })

      let userIds = [...new Set([...assignedToIds])]
      if (!isEmpty(userIds)) {
        let params = {
          includeParentFilter: true,
          filters: JSON.stringify({ ouid: { operatorId: 36, value: userIds } }),
        }
        let { data, error } = await API.get('/setup/portalUsersList', params)
        if (!error) this.userlist = data?.users || []
      }
      this.loading = false
    },
    getUserName(id) {
      if (id) {
        let usr = []
        usr = this.users.filter(us => us.ouid === id)
        if (usr.length) {
          let userName = usr[0].name
          return userName
        } else if (this.userlist.length) {
          let us = []
          us = this.userlist.filter(u => u.ouid === id)
          if (us.length) {
            let userName = us[0].name
            return userName
          } else {
            return '---1'
          }
        } else {
          return '---2'
        }
      } else {
        return '---3'
      }
    },
    async loadActivities() {
      this.loading = true
      let params = {}
      if (this.module === 'asset') {
        this.modulename = 'assets'
        params.assetId = this.recordId
      } else if (this.module === 'workorder') {
        this.modulename = 'workorders'
        params.workOrderId = this.recordId
      }
      let { error, data } = await API.get(
        `/v2/${this.modulename}/activity`,
        params
      )

      if (!error) {
        this.activities = data.activity
        this.loading = false
      }
    },
    // getAttachmentsString(obj) {
    //   let str = ''
    //   for (let item = 0; item < (obj || []).length; item++) {
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
    getTicket(info) {
      let { field, displayName, newValue } = info || {}

      if (field === 'type') {
        let { name: ticketTypeName } = this.getTicketType(newValue) || {}
        return (
          '<b>' +
          displayName +
          '</b>' +
          ' as ' +
          '<i>' +
          ticketTypeName +
          '</i>'
        )
      } else if (field === 'category') {
        let { displayName: ticketCategoryDisplayName } =
          this.getTicketCategory(newValue) || {}
        return (
          '<b>' +
          displayName +
          '</b>' +
          ' as ' +
          '<i>' +
          ticketCategoryDisplayName +
          '</i>'
        )
      } else if (field === 'priority') {
        let { displayName: ticketPriorityDisplayName } =
          this.getTicketPriority(newValue) || {}
        return (
          '<b>' +
          displayName +
          '</b>' +
          ' as ' +
          '<i>' +
          ticketPriorityDisplayName +
          '</i>'
        )
      } else if (field === 'dueDate') {
        return (
          '<b>' +
          displayName +
          '</b>' +
          ' as ' +
          '<i>' +
          this.$options.filters.formatDate(newValue) +
          '</i>'
        )
      } else if (field === 'resource') {
        return '<b>' + displayName + '</b>' + ' as ' + '<i>' + newValue + '</i>'
      } else if (field === 'site') {
        return (
          '<b>' +
          displayName +
          '</b>' +
          ' as ' +
          '<i>' +
          this.getSiteName(newValue) +
          '</i>'
        )
      } else {
        let { fieldsMap } = this
        let { field: fieldName, newValue } = info || {}
        let field = fieldsMap[fieldName] || {}
        let displayValue = newValue
        if (!isEmpty(field)) {
          if (!isArray(displayValue)) {
            if (Number(displayValue)) {
              displayValue = Number(displayValue)
            }
          }
          displayValue = this.$fieldUtils.getDisplayValue(field, displayValue)
        }
        return (
          '<b>' + displayName + '</b>' + ' as ' + '<i>' + displayValue + '</i>'
        )
      }
    },
    getSiteName(siteId) {
      let site = (this.sites || []).find(site => site.id === siteId)
      return site ? site.name : ''
    },
    getActivityMessage(activity) {
      let { type, info, message } = activity || {}
      if (!isEmpty(info) && !isEmpty(type)) {
        if (type === 4 && info.addWO) {
          return { message: '<b>created workorder</b>' }
        } else if (type === 24 && info.addPMWO && info.addPMWO.length > 2) {
          return { message: '<b> created workorder from PM</b>' }
        } else if (type === 5 && info.status) {
          return {
            message: `<b>${info.status.toLowerCase()}</b> the Work Order`,
          }
        } else if (type === 5 && info.woupdate) {
          // let a3 = []
          let woupdate = info.woupdate.filter(
            a =>
              a.field !== 'modifiedTime' &&
              a.newValue !== -99 &&
              a.oldValue !== a.newValue
          )
          // a3 = a3.filter(a => a.newValue !== -99)
          if (!isEmpty(woupdate)) {
            return {
              message:
                '<b>updated </b>' +
                woupdate.reduce((accStr, activity) => {
                  return isEmpty(accStr)
                    ? this.getTicket(activity)
                    : `${accStr}, ${this.getTicket(activity)}`
                }, ''),
            }
          } else {
            return { message: null }
          }
        } else if (type === 3) {
          return { message: '<b> assigned </b> the Work Order to' }
        } else if (type === 10) {
          return {
            message: `<b> attached </b> <i> ${info.attachment[0].Filename} </i>`,
          }
        } else if (type === 36) {
          return {
            message: `<b> added prerequisite photo </b> <i> ${info.taskattachment[0].Filename} </i>`,
          }
        } else if (type === 38) {
          return {
            message: `<b> removed prerequisite photo </b> <i> ${info.taskattachment[0].Filename} </i>`,
          }
        } else if (type === 37) {
          return {
            message: `<b> approved prerequisite </b>`,
          }
        } else if (type === 23) {
          return {
            message: `<b> closed the task </b> <i> ${info.subject} </i>`,
          }
        } else if (type === 25) {
          return {
            message: `<b> reopened the task </b> <i> ${info.subject} </i>`,
          }
        } else if (type === 8) {
          return {
            message:
              '<b>' + message + '</b>' + '<i>' + info.Task[0].Task + '</i>',
          }
        } else if (type === 12) {
          return {
            message:
              '<b>' +
              'updated task ' +
              '(' +
              info.taskupdate.subject +
              ')' +
              '</b>' +
              ' input value as ' +
              '<i>' +
              info.taskupdate.newvalue +
              '</i>',
          }
        } else if (type === 35) {
          return {
            message:
              '<b>' +
              'updated prerequisite ' +
              '(' +
              info.taskupdate.subject +
              ')' +
              '</b>' +
              ' input value as ' +
              '<i>' +
              info.taskupdate.newvalue +
              '</i>',
          }
        } else if (type === 26) {
          return {
            message:
              '<b>' +
              'updated status ' +
              '</b>' +
              'from ' +
              '<i>' +
              info.oldValue +
              ' to ' +
              info.newValue +
              '</i>',
          }
        } else if (type === 9) {
          return {
            message: `<b> added the Comment </b> <i> (${info.Comment}) </i>`,
          }
        } else if (type === 113) {
          return {
            message: `<b> deleted the Comment </b> <i> (${info.Comment}) </i>`,
          }
        } else if (type === 60) {
          return {
            message: info.message,
          }
        } else if (type === 61) {
          let { message, slaEntity } = info || {}
          return {
            message:
              ' - ' +
              `<strong>${message}</strong>` +
              (slaEntity ? ' for ' : ' ') +
              (slaEntity || ''),
          }
        } else if (type === 62) {
          let { name, slaEntityName, oldDate, newDate } = info || {}
          let { formatDate } = this.$options.filters
          let slaEntityDisplayName = slaEntityName || name || 'SLA Date'

          if (!isEmpty(oldDate) && !isEmpty(newDate)) {
            return {
              message:
                `has changed <strong>${slaEntityDisplayName}</strong>  from ` +
                `<i>${formatDate(oldDate, true, false)}</i> to` +
                `<i>${formatDate(newDate, true, false)}</i>`,
            }
          } else if (!isEmpty(newDate)) {
            return {
              message:
                `has changed <strong>${slaEntityDisplayName}</strong>  to ` +
                `<i>${formatDate(newDate, true, false)}</i>`,
            }
          }
        } else {
          return this.getMessage(activity)
        }
      } else if (type === 2) {
        return {
          message: `<b> closed all the tasks  </b>`,
        }
      } else {
        return {
          message: `<b>  </b>`,
        }
      }
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
