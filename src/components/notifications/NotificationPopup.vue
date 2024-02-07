<template>
  <el-popover
    placement="bottom-end"
    width="420"
    trigger="click"
    v-model="canShowPopup"
    popper-class="notification-popover pT10 pB20 pL0 pR0"
    @show="notificationSeenUpdate"
    :tabindex="-1"
  >
    <div slot="reference" class="flex items-center">
      <slot name="icon" :count="unseenNotificationCount">
        <el-badge
          :value="unseenNotificationCount"
          :max="99"
          class="notifications-unread-new"
        >
          <i
            aria-hidden="true"
            class="q-icon fa fa-bell-o"
            style="font-size: 18px;"
            role="img"
            aria-label="Notification"
          ></i>
        </el-badge>
      </slot>
    </div>
    <div class="border-bottom16 flex-center-row-space pB15 pR15 pL15 pT10">
      <div class="notification-title fwBold f13 letter-spacing1">
        {{ $t('setup.setup.notifications') }}
      </div>
      <i
        class="el-icon-close pointer f16 fwBold"
        @click="canShowPopup = false"
      ></i>
    </div>
    <NotificationList
      :showNotificationInTab="false"
      @notificationItemClicked="notificationItemClicked"
      @hasNotificationData="hasNotificationData"
    />
    <div
      v-if="hasNotification"
      class="notification-footer"
      @click="canShowPopup = false"
    >
      <router-link :to="getNotificationRoute()" class="notification-showmore">{{
        $t('common.notification.show_more')
      }}</router-link>
    </div>
  </el-popover>
</template>

<script>
import NotificationList from './NotificationList'
import { API } from '@facilio/api'
import { mapState, mapActions } from 'vuex'
export default {
  name: 'NotificationPopup',
  components: {
    NotificationList,
  },
  data() {
    return {
      unseenNotificationCount: null,
      canShowPopup: false,
      hasNotification: false,
    }
  },
  created() {
    this.getNotificationCount().then(() => {
      if (this.notificationUnseen > 0) {
        this.unseenNotificationCount = this.notificationUnseen
      }
    })
  },
  computed: {
    ...mapState({
      notificationUnseen: state => state.notification.unseen,
    }),
  },
  methods: {
    ...mapActions({
      getNotificationCount: 'notification/fetchNotificationCount',
    }),
    notificationSeenUpdate() {
      API.get('v3/usernotification/update/seen')
      this.unseenNotificationCount = null
    },
    notificationItemClicked(hasAction) {
      this.canShowPopup = false
      let notificationPath = '/app/personalsettings/notifications'
      if (!hasAction) {
        this.$router
          .push({
            path: notificationPath,
          })
          .catch(() => {})
      }
    },
    hasNotificationData(isNotificationPresent) {
      if (isNotificationPresent) {
        this.hasNotification = true
      }
    },
    getNotificationRoute() {
      let app = window.location.pathname.slice(1).split('/')[0]
      return {
        name: 'notifications',
        params: { app },
      }
    },
  },
}
</script>

<style lang="scss">
.notification-popover {
  .notification-showmore {
    font-size: 13px;
    font-weight: 500;
    letter-spacing: 0.5px;
    text-align: center;
    color: #017aff;
    cursor: pointer;
  }
  .notification-footer {
    bottom: 0;
    right: 0;
    left: 0;
    text-align: center;
    display: block;
    padding-top: 14px;
    position: sticky;
    z-index: 11;
    background: #fdfdfd;
  }
}
</style>
