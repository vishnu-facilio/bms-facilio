<template>
  <div class="block p30 portal-activity-scroll">
    <el-timeline class="wo-activity">
      <div>
        <div class=" width100 height100 flex-middle text-center" v-if="loading">
          <div class="flex-middle width100">
            <spinner :show="loading" size="80"></spinner>
          </div>
        </div>
        <div v-else-if="$validation.isEmpty(activities)">
          <div
            class="flex-middle width100 height80vh justify-center shadow-none white-bg-block flex-direction-column"
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
        <template v-else v-for="(activity, index) in activities">
          <el-timeline-item
            v-if="getMessage(activity).message !== null"
            type="primary"
            :color="activity.color"
            :timestamp="activity.ttime | formatDate"
            :key="index"
          >
            <div
              v-if="
                activity.type === 24 && getMessage(activity).message !== null
              "
            >
              <avatar size="sm" :user="{ name: activity.doneBy.name }"></avatar>
              {{ activity.doneBy.name }}
              <span v-html="getMessage(activity).message"></span>
              <i
                ><span
                  @click="openPm(activity.info.addPMWO[0].pmid)"
                  class="fc-id pointer"
                >
                  # {{ activity.info.addPMWO[0].pmid }}</span
                ></i
              >
            </div>
            <div
              v-else-if="
                activity.type !== 24 && getMessage(activity).message !== null
              "
            >
              <avatar size="sm" :user="{ name: activity.doneBy.name }"></avatar>
              {{ activity.doneBy.name }}
              <span v-html="getMessage(activity).message"></span>
            </div>
          </el-timeline-item>
        </template>
      </div>
    </el-timeline>
  </div>
</template>
<script>
// import { mapState, mapGetters } from 'vuex'
import Avatar from '@/Avatar'
export default {
  props: ['details', 'serviceportal', 'module', 'moduleMetaObject'],
  data() {
    return {
      activities: [],
      loading: true,
      // userlist: [],
    }
  },
  components: {
    Avatar,
  },
  mounted() {
    this.loadServicePortalActivities()
  },
  // computed: {
  //   ...mapState({
  //     users: state => state.users,
  //     sites: state => state.account.data.sites,
  //     ticketcategory: state => state.account.data.ticketCategory,
  //     ticketPriority: state => state.account.data.ticketPriority,
  //     ticketStatus: state => state.account.data.ticketStatus,
  //   }),
  //   ...mapGetters(['getUser']),
  // },
  watch: {
    details() {
      this.loadServicePortalActivities()
    },
  },
  // created() {
  //   this.loadusers()
  // },
  methods: {
    loadServicePortalActivities() {
      this.loading = true
      this.modulename = 'workorders'
      this.$http
        .get('/v2/workorders/activity?workOrderId=' + this.details.id)
        .then(response => {
          this.activities = response.data.result.activity
          this.loading = false
        })
    },
    openPm(id) {
      this.$router.replace({ path: '/app/wo/planned/summary/' + id })
    },
    // loadusers() {
    //   this.$http.get('setup/allPortalUsers').then(response => {
    //     this.userlist = response['data'].users
    //   })
    // },
    // getUserName(id) {
    //   if (id) {
    //     let usr = []
    //     usr = this.users.filter(us => us.ouid === id)
    //     if (usr && usr.length > 0) {
    //       let userName = usr[0].name
    //       return userName
    //     } else if (this.userlist && this.userlist.length > 0) {
    //       let us = []
    //       us = this.userlist.filter(u => u.ouid === id)
    //       if (us && us.length > 0) {
    //         let userName = us[0].name
    //         return userName
    //       } else {
    //         return '---'
    //       }
    //     } else {
    //       return '---'
    //     }
    //   } else {
    //     return '---'
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
        .get('/v2/' + self.modulename + '/activity' + params + self.details.id)
        .then(function(response) {
          try {
            if (response.data.result && response.data.result.activity) {
              self.activities = response.data.result.activity
            }
            // eslint-disable-next-line no-empty
          } catch {}
          self.loading = false
        })
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
    //   this.$router.replace({ path: '/app/wo/planned/summary/' + val })
    // },
    // getTicket(info) {
    //   if (info.field === 'type') {
    //     return null
    //   } else if (info.field === 'category') {
    //     return (
    //       '<span class="fw5">' +
    //       'updated ' +
    //       info.displayName +
    //       '</span>' +
    //       ' as ' +
    //       '<i>' +
    //       this.ticketcategory.filter(us => us.id === info.newValue)[0]
    //         .displayName +
    //       '</i>'
    //     )
    //   } else if (info.field === 'priority') {
    //     return (
    //       '<span class="fw5">' +
    //       'updated ' +
    //       info.displayName +
    //       '</span>' +
    //       ' as ' +
    //       '<i>' +
    //       this.ticketPriority.filter(us => us.id === info.newValue)[0]
    //         .displayName +
    //       '</i>'
    //     )
    //   } else if (info.field === 'dueDate') {
    //     return (
    //       '<span class="fw5">' +
    //       'updated ' +
    //       info.displayName +
    //       '</span>' +
    //       ' as ' +
    //       '<i>' +
    //       this.$options.filters.formatDate(info.newValue) +
    //       '</i>'
    //     )
    //   } else if (info.field === 'site') {
    //     return (
    //       '<span class="fw5">' +
    //       'updated ' +
    //       info.displayName +
    //       '</span>' +
    //       ' as ' +
    //       '<i>' +
    //       this.getSiteName(info.newValue) +
    //       '</i>'
    //     )
    //   } else {
    //     return (
    //       '<span class="fw5">' +
    //       'updated ' +
    //       info.displayName +
    //       '</span>' +
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
      if (
        activity &&
        activity.type === 4 &&
        activity.info &&
        activity.info.addWO
      ) {
        return { message: '<span class="fw5"> raised the request</span>' }
      }
      if (
        activity &&
        activity.type === 5 &&
        activity.info.status &&
        activity.info.status
      ) {
        return {
          message:
            '<span class="fw5">' +
            activity.info.status.toLowerCase() +
            '</span>' +
            ' the Work Order',
        }
      } else if (activity && activity.type === 5 && activity.info.woupdate) {
        return { message: null }
      } else if (activity && activity.type === 26) {
        return {
          message:
            '<span class="fw5">' +
            'updated status ' +
            '</span>' +
            'from ' +
            '<i>' +
            activity.info.oldValue +
            ' to ' +
            activity.info.newValue +
            '</i>',
        }
      } else if (activity.type === 9) {
        return {
          message:
            '<span class="fw5"> added the Comment </span>' +
            '<i>' +
            '(' +
            activity.info.Comment +
            ')' +
            '</i>',
        }
      } else if (activity.type === 10) {
        return {
          message:
            '<b>' +
            'attached ' +
            '</b>' +
            '<i>' +
            activity.info.attachment[0].Filename +
            '</i>',
        }
      }
      return { message: '<span class="fw5">' + activity.message + '</span>' }
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
.portal-activity-scroll {
  height: 100%;
  padding-bottom: 100px;
  overflow-y: scroll;
}
</style>
