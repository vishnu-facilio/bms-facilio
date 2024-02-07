<template>
  <div class="notification-list-component">
    <portal to="#notification-pagination">
      <pagination
        v-if="totalNotification"
        :total="totalNotification"
        :perPage="perPage"
        class="pL15 fc-black-small-txt-12 pagination-notification"
      ></pagination>
    </portal>
    <div class="notifications-empty" v-if="$validation.isEmpty(notifications)">
      {{ $t('common.notification.no_notification') }}
    </div>
    <div class="notifications-list" v-else>
      <div v-if="loading" class="full-layout-white height100 text-center">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div v-else>
        <div
          v-for="(notification, index) in notifications"
          :key="notification.id"
          :class="[
            'notifications-list-item-container',
            notification.isRead ? '' : 'not-read pointer',
            showNotificationInTab
              ? ''
              : 'pB10' && hasAction(notification) && 'pointer',
          ]"
        >
          <div class="notifications-list-item">
            <div class="notification-seen-dot-container">
              <div
                :class="[
                  'notification-dot',
                  notification.isRead ? '' : 'notification-seen-dot-unseen',
                ]"
              ></div>
            </div>
            <div
              class="notification-avatar-block"
              @click="
                showNotificationInTab
                  ? markNotificationAsRead(notification)
                  : notificationClickAction(notification)
              "
              :style="{
                backgroundColor: getBackgroundColor(notification.subject),
              }"
            >
              <InlineSvg
                :src="getIcon(getModuleName(notification))"
                iconClass="icon vertical-middle"
              ></InlineSvg>
            </div>
            <div class="notification-msg">
              <div
                :class="[
                  'pB3 notification-title bold p0 fwBold',
                  showNotificationInTab ? '' : 'truncate-text',
                ]"
                @click="
                  showNotificationInTab
                    ? markNotificationAsRead(notification)
                    : notificationClickAction(notification)
                "
              >
                {{ notification.title }}
              </div>
              <div
                :class="getSubjectClassname(notification, index)"
                @click="
                  showNotificationInTab
                    ? markNotificationAsRead(notification)
                    : notificationClickAction(notification)
                "
              >
                {{ notification.subject }}
              </div>
              <div
                v-if="showNotificationInTab"
                class="flex flex-direction-row pT6"
              >
                <a
                  v-if="canShowViewMore(notification)"
                  @click="toggleVisibility(index, notification)"
                  class="text-capitalize letter-spacing0_3 f13 pR8 pointer"
                  >{{ showMoreLinkText(index) }}</a
                >
                <div
                  v-if="
                    canShowViewMore(notification) && hasAction(notification)
                  "
                  class="margin-go-to-summary"
                ></div>
                <a
                  v-if="showNotificationInTab && hasAction(notification)"
                  @click="
                    notificationClickAction(notification, showNotificationInTab)
                  "
                  :class="[
                    'text-capitalize letter-spacing0_3 f13 pointer',
                    canShowViewMore(notification) && 'pL8',
                  ]"
                  >Go to Summary
                  <InlineSvg
                    src="svgs/new-tab"
                    iconClass="icon vertical-middle icon-xs mL3"
                  ></InlineSvg>
                </a>
              </div>
              <div
                v-if="showNotificationInTab"
                class="notifications-time pT4 pB10"
              >
                {{ getTime(notification.sysCreatedTime) }}
              </div>
            </div>

            <div
              class="has-action"
              v-if="!showNotificationInTab && hasAction(notification)"
              @click="
                notificationClickAction(notification, showNotificationInTab)
              "
            >
              <InlineSvg
                src="svgs/new-tab"
                iconClass="icon vertical-middle notification-svg icon-sm"
              ></InlineSvg>
            </div>
          </div>
          <div
            v-if="!showNotificationInTab"
            class="notifications-time-in-popover"
          >
            {{ getTime(notification.sysCreatedTime) }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Pagination from 'pages/approvals/components/ActivityPagination'
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import { htmlToText } from '@facilio/utils/filters'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

let MODULE_NAME = {
  alarm: 'svgs/notifications/alarms',
  task: 'svgs/notifications/tasks',
  workorder: 'svgs/notifications/workorder',
  contact: 'svgs/notifications/contact',
  asset: 'svgs/notifications/assets',
  quote: 'svgs/notifications/notifications-bell',
  purchaseorder: 'svgs/notifications/notifications-bell',
  purchaserequest: 'svgs/notifications/notifications-bell',
  default: 'svgs/notifications/notifications-bell',
}
let NOTIFICATION_BACKGROUND = [
  '#6eb1f4',
  '#f4d16e',
  '#f4a66e',
  '#ff8b8b',
  '#dfd758',
]

export default {
  props: ['showNotificationInTab'],
  components: { Pagination },
  mixins: [FetchViewsMixin],
  data() {
    return {
      totalCount: null,
      perPage: 50,
      loading: false,
      selectedIndex: null,
    }
  },
  created() {
    if (this.showNotificationInTab) this.loadNotifications()
    else this.loadNotificationForPopup()
  },
  computed: {
    ...mapState({
      notificationListData: state => state.notification.notifications,
      notificationPopupData: state => state.notification.notificationsForPopup,
      totalNotification: state => state.notification.totalCount,
    }),
    page() {
      return this.$route.query.page || 1
    },
    showNotificationsInTab() {
      return this.showNotificationInTab
    },
    notifications() {
      let notificationData
      if (!this.showNotificationsInTab) {
        let { notificationPopupData } = this

        notificationData = notificationPopupData
      } else {
        let { notificationListData } = this
        notificationData = notificationListData
      }

      return notificationData
    },
  },

  watch: {
    page() {
      if (this.showNotificationInTab) this.loadNotifications()
    },
  },

  methods: {
    showMoreLinkText(index) {
      return this.selectedIndex === index ? 'View Less' : 'View More'
    },
    hasAction(notification) {
      let moduleName = this.getModuleName(notification)
      let recordId = this.getRecordId(notification)
      let actionType = this.getActionType(notification)
      return !isEmpty(moduleName) && !isEmpty(recordId) && !isEmpty(actionType)
    },
    loadNotifications() {
      if (this.showNotificationInTab) this.loading = true
      this.$store
        .dispatch('notification/fetchNotifications', {
          page: this.page || 1,
          perPage: this.perPage,
        })
        .then(() => {
          this.loading = false
        })
    },
    async loadNotificationForPopup() {
      await this.$store.dispatch('notification/fetchNotificationsPopup')
      if (isEmpty(this.notificationPopupData)) {
        this.$emit('hasNotificationData', false)
      } else {
        this.$emit('hasNotificationData', true)
      }
    },
    async notificationClickAction(notification) {
      notification.isRead = true

      let moduleName = this.getModuleName(notification)
      let recordId = this.getRecordId(notification)
      let actionType = this.getActionType(notification)
      let hasAction = false

      if (!isEmpty(moduleName) && !isEmpty(recordId) && !isEmpty(actionType)) {
        let viewname = await this.fetchView(moduleName)

        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

          if (name) {
            let routerObj = { name, params: { viewname, id: recordId } }

            if (this.showNotificationsInTab) {
              let routerData = this.$router.resolve(routerObj)
              window.open(routerData.href)
            } else {
              this.$router.push(routerObj)
            }
          }
        } else {
          let routes = {
            asset: {
              name: 'assetsummary',
              params: { viewname, assetid: recordId },
            },
            workorder: {
              name: 'wosummarynew',
              params: { id: recordId },
            },
            alarms: {
              name: `FaultSummary`,
              params: { viewName: viewname, id: recordId },
            },
            tenant: {
              name: 'tenantSummary',
              params: { viewname, id: recordId },
            },
            quote: {
              name: 'quotationSummary',
              params: { viewname, id: recordId },
            },
            purchaseorder: {
              name: 'poSummary',
              params: { viewname, id: recordId },
            },
            purchaserequest: {
              name: 'prSummary',
              params: { viewname, id: recordId },
            },
            custom: {
              name: 'custommodules-summary',
              params: { viewname, id: recordId, moduleName },
            },
          }
          let routerObj = routes[moduleName] || routes['custom']

          if (this.showNotificationsInTab) {
            let routerData = this.$router.resolve(routerObj) || null
            routerData && window.open(routerData.href)
          } else {
            this.$router.push(routerObj).catch(() => {})
          }
        }

        hasAction = true
      }

      if (!this.showNotificationInTab) {
        this.$emit('notificationItemClicked', hasAction)
      }

      await this.$store.dispatch('notification/updateStatus', notification)
      this.loadNotificationForPopup()
    },
    async markNotificationAsRead(notification) {
      notification.isRead = true
      await this.$store.dispatch('notification/updateStatus', notification)
      this.loadNotificationForPopup()
    },
    getTime(miliseconds) {
      return this.$options.filters.fromNow(miliseconds)
    },
    getIcon(modulename) {
      return MODULE_NAME[modulename] || MODULE_NAME['default']
    },
    getBackgroundColor(item) {
      let userKey = item
      let userUniqueNum = Array.from(userKey)
        .map(letter => letter.charCodeAt(0))
        .reduce((current, previous) => previous + current)
      let colorIndex = userUniqueNum % 5
      return NOTIFICATION_BACKGROUND[colorIndex]
    },
    getModuleName(item) {
      let moduleObj = this.$getProperty(item, 'action.actionData.module')
      let moduleName = this.$getProperty(moduleObj, 'name')
      let extendedModuleName = this.$getProperty(moduleObj, 'extendModule.name')
      let isBaseEntity =
        this.$getProperty(moduleObj, 'typeEnum') === 'BASE_ENTITY'

      if (isBaseEntity) {
        return moduleName || extendedModuleName
      }
    },
    getRecordId(item) {
      let recordId = this.$getProperty(item, 'action.actionData.recordId')

      if (!isEmpty(recordId) && recordId !== -1) {
        return recordId
      }
    },
    getActionType(item) {
      let actionType = this.$getProperty(item, 'action.actionType')

      if (!isEmpty(actionType)) {
        return actionType
      }
    },
    canShowMore(item) {
      let htmlToString = htmlToText(item.subject).split(/\r\n|\r|\n/).length
      return htmlToString > 4
    },
    async toggleVisibility(index, notification) {
      if (this.selectedIndex !== index) {
        this.selectedIndex = index

        notification.isRead = true
        await this.$store.dispatch('notification/updateStatus', notification)
        this.loadNotificationForPopup()
      } else {
        this.selectedIndex = null
      }
    },
    getSubjectClassname(notification, index) {
      if (this.showNotificationInTab && this.canShowMore(notification)) {
        if (this.selectedIndex === index) {
          return 'notification-subject notification-subject-viewMore'
        } else {
          return 'notification-subject notification-subject-viewLess'
        }
      } else {
        return 'popover-subject'
      }
    },
    canShowViewMore(notification) {
      return this.showNotificationInTab && this.canShowMore(notification)
    },
  },
}
</script>
<style lang="scss" scoped>
.notification-list-component {
  .notifications-list-item-container {
    border-bottom: 1px solid #f3f5f6;
    &:hover {
      background: #f4f6ff;
    }
  }
  .notifications-list-item {
    display: flex;
    flex-direction: row;
    padding: 5px 0px 5px 0px;
  }
  .notifications-list-item-container .has-action {
    margin: 0px 15px 0px 0px;
    visibility: hidden;
    display: flex;
    align-items: center;
  }
  .notifications-list-item-container:hover .has-action {
    visibility: visible;
    margin-left: auto;
  }
  .not-read {
    background-color: #f9fbfd;
  }
  .notification-seen-dot-container {
    padding: 22px 0px 0px 8px;
  }
  .notification-dot {
    height: 7px;
    width: 7px;
    border-radius: 50%;
    display: inline-block;
  }
  .notification-seen-dot-unseen {
    background-color: #9295ff;
  }
  .notification-avatar-block {
    float: left;
    width: 35px;
    height: 35px;
    border-radius: 17.5px;
    margin: 13px 0px 0px 7px;
  }
  .notification-avatar-block .inline {
    padding: 9px;
    display: flex;
    justify-content: center;
  }

  .notification-msg {
    overflow: auto;
    font-size: 13px;
    letter-spacing: 0.5px;
    text-align: left;
    color: #333333;
    padding: 10px 13px 0px 13px;
    line-height: 1.36;
    text-overflow: ellipsis;
    display: flex;
    flex-direction: column;
    align-items: inherit;
  }
  .notification-subject::first-letter {
    text-transform: uppercase;
  }
  .popover-subject {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    width: 100%;
    word-break: break-word;
    letter-spacing: 0.5px;
    line-height: 1.43;
    margin-top: 2px;
  }

  .notification-subject {
    white-space: pre-line;
    overflow: hidden;
    margin-top: 2px;
  }

  .notification-subject-viewLess {
    max-height: 70px;
    transition: max-height 0.3s ease-in;
  }
  .notification-subject-viewMore {
    max-height: 500px;
    transition: max-height 0.2s ease-in;
  }
  .margin-go-to-summary {
    border-right: 1px solid #cccccc;
  }
  .notifications-time {
    font-size: 11px;
    font-weight: normal;
    letter-spacing: 0.4px;
    color: #999999;
    float: left;
    padding-top: 5px;
  }
  .notifications-time-in-popover {
    font-size: 11px;
    font-weight: normal;
    letter-spacing: 0.4px;
    color: #999999;
    display: flex;
    margin-right: 10px;
    justify-content: flex-end;
    width: 100;
  }
  .notifications-empty {
    padding: 50px;
    color: #999999;
    text-align: center;
  }
}
</style>
