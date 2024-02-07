<template>
  <div style="padding-top:20px;">
    <div v-if="loading">
      <spinner :show="loading"></spinner>
    </div>
    <div v-else-if="!activities || !activities.length">
      {{ $t('common.products.no_activities_tracked_yet') }}
    </div>
    <div
      v-else
      v-for="(activity, index) in activities"
      class="tactivity"
      :key="index"
    >
      <div class="tlContent2">
        <div class="flLeft activityIconDiv2">
          <i class="fa fa-circle-o " aria-hidden="true"></i>
        </div>
        <div class="">
          <div class="" style="display: inline-flex;">
            <div class="tlContentMain pR10 pull-left">
              <avatar size="sm" :user="{ name: activity.name }"></avatar>
            </div>
            <div class="pull-left " style="line-height: 1.4;">
              <span>{{ activity.name }}</span>
              <span v-if="activity.activityType == 16">
                {{ activity.message }}
                <avatar
                  size="sm"
                  :user="{ name: getAssignedBy(activity.info.updatedFields) }"
                ></avatar>
                <span class="updval">{{
                  getAssignedBy(activity.info.updatedFields)
                }}</span>
              </span>
              <span v-else class="tlContentMain">
                {{
                  getMessage(activity).message
                    ? getMessage(activity).message
                    : ''
                }}
                <span v-if="getMessage(activity).value" class="updval">{{
                  getMessage(activity).value
                }}</span>
              </span>
              <div class="tlDate2 col-12">
                <span>{{ activity.modifiedTime | fromNow }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="clearboth"></div>
      </div>
      <div class="clearboth"></div>
    </div>
  </div>
</template>
<script>
import Avatar from '@/Avatar'
import { mapGetters } from 'vuex'

export default {
  props: ['record', 'serviceportal'],
  data() {
    return {
      activities: [],
      loading: true,
    }
  },
  components: {
    Avatar,
  },
  mounted() {
    if (this.serviceportal) {
      this.loadServicePortalActivities()
    } else {
      this.loadActivities()
    }
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

  computed: {
    ...mapGetters(['getUser']),
  },

  methods: {
    getUserCall(names) {
      if (this.serviceportal) {
        if (this.$account.user.id === names) {
          return this.$account.user.name
        } else {
          return this.getUser(names).name
        }
      } else {
        return this.getUser(names).name
      }
    },
    getAssignedBy(updatedFields) {
      let user = updatedFields.find(el => el.fieldName === 'assignedTo')
      if (user.newValue) {
        return this.getUser(user.newValue.id).name
      } else if (user.oldValue) {
        return this.getUser(user.oldValue.id).name
      }
    },
    getMessage(activity) {
      if (activity.activityType === 131072) {
        return { message: activity.message }
      } else if (activity.activityType === 2) {
        if (
          activity.info.updatedFields &&
          activity.info.updatedFields.length > 0
        ) {
          if (
            activity.info.updatedFields.find(
              el => el.fieldName === 'category'
            ) &&
            activity.info.updatedFields.find(el => el.fieldName === 'category')
              .fieldName === 'category'
          ) {
            return {
              message: 'updated category as',
              value: activity.info.updatedFields.find(
                el => el.fieldName === 'category'
              ).newValue,
            }
          } else if (
            activity.info.updatedFields.find(
              el => el.fieldName === 'priority'
            ) &&
            activity.info.updatedFields.find(el => el.fieldName === 'priority')
              .fieldName === 'priority'
          ) {
            return {
              message: 'updated priority as',
              value: activity.info.updatedFields.find(
                el => el.fieldName === 'priority'
              ).newValue,
            }
          } else if (
            (activity.info.updatedFields.find(
              el => el.fieldName === 'actualWorkStart'
            ) &&
              activity.info.updatedFields.find(
                el => el.fieldName === 'actualWorkStart'
              ).fieldName === 'actualWorkStart') ||
            (activity.info.updatedFields.find(
              el => el.fieldName === 'resumedWorkStart'
            ) &&
              activity.info.updatedFields.find(
                el => el.fieldName === 'resumedWorkStart'
              ).fieldName === 'resumedWorkStart')
          ) {
            return { message: 'started work' }
          } else if (
            activity.info.updatedFields.find(
              el => el.fieldName === 'actualWorkEnd'
            ) &&
            activity.info.updatedFields.find(
              el => el.fieldName === 'actualWorkEnd'
            ).fieldName === 'actualWorkEnd'
          ) {
            return { message: 'resolved work' }
          } else if (
            activity.info.updatedFields.find(
              el => el.fieldName === 'dueDateString'
            ) &&
            activity.info.updatedFields.find(
              el => el.fieldName === 'dueDateString'
            ).fieldName === 'dueDateString'
          ) {
            return {
              message: 'updated Due Date as',
              value: activity.info.updatedFields.find(
                el => el.fieldName === 'dueDateString'
              ).newValue,
            }
          } else if (
            activity.info.updatedFields.find(el => el.fieldName === 'type') &&
            activity.info.updatedFields.find(el => el.fieldName === 'type')
              .fieldName === 'type'
          ) {
            return {
              message: 'updated Type as',
              value: activity.info.updatedFields.find(
                el => el.fieldName === 'type'
              ).newValue,
            }
          } else if (
            activity.info.updatedFields.find(el => el.fieldName === 'status') &&
            activity.info.updatedFields.find(el => el.fieldName === 'status')
              .fieldName === 'status'
          ) {
            return { message: 'pause the work' }
          }
        }
      } else if (activity.activityType === 16384) {
        if (activity.info.updatedFields.length > 0) {
          if (
            activity.info.updatedFields.find(
              el => el.fieldName === 'remarks'
            ) &&
            activity.info.updatedFields.find(el => el.fieldName === 'remarks')
              .fieldName === 'remarks'
          ) {
            return {
              message:
                'updated task (' + activity.info.task.subject + ') remarks as',
              value: activity.info.updatedFields.find(
                el => el.fieldName === 'remarks'
              ).newValue,
            }
          } else if (
            activity.info.updatedFields.find(
              el => el.fieldName === 'inputValue'
            ) &&
            activity.info.updatedFields.find(
              el => el.fieldName === 'inputValue'
            ).fieldName === 'inputValue'
          ) {
            return {
              message:
                'updated task (' +
                activity.info.task.subject +
                ') input Value as',
              value: activity.info.updatedFields.find(
                el => el.fieldName === 'inputValue'
              ).newValue,
            }
          }
        }
      } else if (activity.activityType === 128) {
        if (!activity.info.attachments) {
          return { message: 'attached', value: null }
        }
        return {
          message: 'attached',
          value: this.getAttachmentsString(activity.info.attachments),
        }
      } else if (activity.activityType === 256) {
        return { message: 'added the task', value: activity.info.task }
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
      return { message: activity.message }
    },
    loadActivities() {
      let self = this
      self.loading = true
      self.$http
        .get('/workorder/activities?workOrderId=' + self.record.id)
        .then(function(response) {
          self.activities = response.data.filter(
            activity =>
              activity.activityType !== 16 ||
              (activity.info.updatedFields &&
                activity.info.updatedFields.length > 0 &&
                activity.info.updatedFields.find(
                  el => el.fieldName === 'assignedTo'
                ))
          )
          self.loading = false
        })
        .catch(() => {})
    },
    loadServicePortalActivities() {
      let self = this
      self.loading = true
      self.$http
        .get('/activities?workOrderRequestId=' + self.record.id)
        .then(function(response) {
          self.activities = response.data
          self.loading = false
        })
    },
    getAttachmentsString(obj) {
      let str = ''
      for (let item = 0; item < obj.length; item++) {
        if (item !== 0) {
          if (item === obj.length - 1) {
            str += ' and '
          } else {
            str += ', '
          }
        }
        str += obj[item]
      }
      return str
    },
  },
}
</script>
<style>
.tlMain {
  padding-top: 30px;
}
.tlDate2 {
  font-size: 11px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  text-align: left;
  color: #39b2c2;
  padding-top: 2px;
}
.flLeft {
  float: left;
}
.tlContent2 {
  border-left: 1px solid #ddd;
  padding-left: 25px;
  font-size: 13px;
  letter-spacing: 0.5px;
  padding-bottom: 30px;
  width: 100%;
}
.clearboth {
  clear: both;
}
.activityIconDiv2 {
  margin-left: -31px;
  background: #fff;
  color: #fd4b92;
  font-size: 13px;
}
.tlContentMain {
  color: #333;
}
.tlContentFields .tlContentMain {
  white-space: pre-line;
}
.tlContentSecondary {
  color: #777;
}
.tlContentFields {
  padding-top: 10px;
}
.labelleft {
  float: left;
}
.w150 {
  width: 150px;
}
.clearboth {
  clear: both;
}
.updval {
  color: #9da2b1;
}
div.tactivity:last-child .tlContent2 {
  border-left: none !important;
}
div.tactivity:last-child .activityIconDiv2 {
  font-size: 14px !important;
  color: #333;
}
.w15 {
  width: 15% !important;
  min-width: 13% !important;
  max-width: 15% !important;
}
.w85 {
  width: 85% !important;
  min-width: 85% !important;
  max-width: 85% !important;
}
.pR10 {
  padding-right: 10px;
}
</style>
