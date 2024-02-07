<template>
  <div>
    <div class="display-flex-between-space pR10 pL10 pT10 pB20">
      <div class="setup-modal-title pT10 pL20 pB18">
        {{ $t('setup.setup.notifications') }}
      </div>
      <portal-target
        name="#notification-pagination"
        class="pT10 pR25 pB18"
      ></portal-target>
    </div>
    <div class="notification-page-block">
      <NotificationList :showNotificationInTab="true" />
    </div>
  </div>
</template>
<script>
import NotificationList from '@/notifications/NotificationList'
export default {
  components: {
    NotificationList,
  },
  name: 'Notifications',
  title() {
    return 'Notifications'
  },
}
</script>
<style lang="scss">
.notification-page-block {
  height: calc(100vh - 190px);
  overflow-y: scroll;
  overflow-x: hidden;
}
</style>
