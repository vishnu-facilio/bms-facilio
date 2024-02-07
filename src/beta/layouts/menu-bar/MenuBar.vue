<template>
  <div class="flex items-center pR10 pL10">
    <SiteSwitch v-if="showSwitchVariable"> </SiteSwitch>
    <span class="separator">|</span>

    <FPopover
      v-if="canShowQuickAddMenu"
      trigger="clickToOpen"
      :visible="showQuickMenu"
      placement="bottom-end"
      @visibleChange="val => (showQuickMenuc = val)"
    >
      <div class="width200px" slot="content">
        <div class="p10 flex">
          <FText fontWeight="bolder">
            {{ $t('common.tabs.maintenance') }}</FText
          >
        </div>
        <FMenuItem value="1" class="flex items-center" @click="redirectToWO">
          <div class="flex items-center">
            <fc-icon group="platform" name="laptop" class="mR10" />
            <FText> {{ $t('panel.panel.work_order') }}</FText>
          </div>
        </FMenuItem>
        <FMenuItem value="2" @click="redirectToIr">
          <div class="flex items-center">
            <fc-icon group="platform" name="laptop" class="mR10" />
            <FText>{{ $t('panel.panel.inventory_req') }}</FText>
          </div>
        </FMenuItem>
      </div>
      <FIcon
        group="navigation"
        name="addition"
        class="mR10 cursor-pointer"
        size="18"
        :pressable="true"
      ></FIcon>
    </FPopover>
    <NotificationPopup v-if="canShowNotifications">
      <template #icon>
        <FIcon
          group="alert"
          name="notification"
          class="pointer"
          size="18"
          :pressable="true"
        ></FIcon>
      </template>
    </NotificationPopup>

    <span class="separator">|</span>
    <div @click="showProfilePanel">
      <FAvatar :userName="userName" class="pointer" />
    </div>
  </div>
</template>

<script>
import SiteSwitch from './SiteSwitch.vue'
import {
  FAvatar,
  FIcon,
  FPopover,
  FMenuItem,
  FText,
} from '@facilio/design-system'
import { mapGetters } from 'vuex'
import NotificationPopup from '@/notifications/NotificationPopup'
import { findRouteForModule, pageTypes } from '@facilio/router'

export default {
  name: 'MenuBar',
  components: {
    SiteSwitch,
    FAvatar,
    NotificationPopup,
    FIcon,
    FPopover,
    FMenuItem,
    FText,
  },
  data: () => ({
    showQuickMenu: false,
  }),
  computed: {
    ...mapGetters('webtabs', ['isAppPrefEnabled']),
    canShowQuickAddMenu() {
      return this.isAppPrefEnabled('canShowQuickCreate')
    },
    canShowNotifications() {
      return this.isAppPrefEnabled('canShowNotifications')
    },
    showSwitchVariable() {
      return this.$helpers.isLicenseEnabled('SCOPE_VARIABLE')
    },
    userName() {
      let { $account } = this || {}
      let { user } = $account || {}
      let { name } = user || {}
      return name
    },
  },
  methods: {
    showProfilePanel() {
      this.$emit('showProfilePanel')
    },
    redirectToWO() {
      let { name } = findRouteForModule('workorder', pageTypes.CREATE) || {}
      if (name) {
        this.$router.push({ name })
      }
    },
    redirectToIr() {
      let { name } =
        findRouteForModule('inventoryrequest', pageTypes.CREATE) || {}
      if (name) {
        this.$router.push({ name })
      }
    },
  },
}
</script>
