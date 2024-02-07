<template>
  <div class="notification-list-container">
    <div class="notification-inner">
      <div
        class="notification-header display-flex-between-space pR10 pL10 pT10 pB20"
      >
        <div class="setup-modal-title pT10 pL20 pB18">
          {{ $t('setup.setup.notifications') }}
        </div>
        <portal-target
          name="#notification-pagination"
          class="pT10 pR25 pB18"
        ></portal-target>
      </div>
      <!-- <div style="overflow:auto"> -->
      <NotificationList :showNotificationInTab="true"></NotificationList>
      <!-- </div> -->
    </div>
  </div>
</template>
<script>
import NotificationList from 'src/components/notifications/NotificationList.vue'
export default {
  components: { NotificationList },
}
</script>
<style>
.notification-list-container {
  background-color: #ffffff;
  height: inherit;
  width: 600px;
  padding: 20px;
  display: flex;
  margin: auto;
  border-radius: 8px;
}
.notification-inner {
  width: 100%;
  overflow: auto;
}
.notification-header {
  position: sticky;
  top: 0;
  background-color: #fff;
  z-index: 2;
}
</style>
