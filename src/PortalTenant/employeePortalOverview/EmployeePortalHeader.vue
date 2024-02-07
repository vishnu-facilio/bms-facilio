<template>
  <div class="fc-employee-portal-home-header pL15 pR10" height="70">
    <div class="fc-employee-heder-left">
      <portal-target
        name="employee-portal-top-header"
        @change="handleUpdate"
        :slot-props="{ targetRecived: true }"
      >
      </portal-target>
      <span v-if="showTabName">{{ currentTab.name }}</span>
    </div>
    <div class="fc-employee-heder-right">
      <GlobalAdd> </GlobalAdd>
      <div
        class="pL20 pR10 pointer"
        style="position: relative;top: 0;right: 5px;"
      >
        <Notification
          :iconSrc="'svgs/employeePortal/bell'"
          ref="notification"
          iconClass="icon text-center icon-xl vertical-bottom"
          style="font-size: 15px"
        />
      </div>
      <el-dropdown @command="profileAction" trigger="click" class="UserAvatar">
        <span class="el-dropdown-link">
          <UserAvatar
            class="pointer portal-profile-hover"
            size="md"
            :user="$portaluser"
            :name="false"
            iconClass="icon text-center icon-xxl"
          ></UserAvatar>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="myprofile">
            <span class="pointer">
              {{ $t('tenant.announcement.my_profile') }}
            </span>
          </el-dropdown-item>
          <el-dropdown-item command="logout" @change="logout">
            <span class="pointer">{{ $t('tenant.announcement.logout') }}</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>
<script>
import GlobalAdd from 'PortalTenant/employeePortalOverview/GlobalAddButton'
import Notification from 'PortalTenant/employeePortalOverview/Notification'
import UserAvatar from '@/avatar/User'
import { getApp } from '@facilio/router'
import homeMixin from 'src/PortalTenant/auth/portalHomeMixin'
import { mapState } from 'vuex'
export default {
  mixins: [homeMixin],
  data() {
    return {
      showTabName: true,
    }
  },
  components: {
    GlobalAdd,
    Notification,
    UserAvatar,
  },
  computed: {
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    appName() {
      return getApp().linkName
    },
  },
  methods: {
    handleUpdate({ from }) {
      if (from && from !== undefined) {
        this.showTabName = false
      } else {
        this.showTabName = true
      }
    },
    handleClick() {},
    profileAction(command) {
      if (command === 'myprofile') {
        this.$router.push({
          path: `/${this.appName}/profile/myprofile`,
        })
      } else if (command === 'logout') {
        this.logout()
      }
    },
  },
}
</script>
<style lang="scss">
.fc-employee-portal-home-header {
  width: 100%;
  height: 56px;
  line-height: 60px;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #ebedf4;
  .fc-show-result-txt {
    font-size: 12px;
    font-weight: normal;
    line-height: normal;
    letter-spacing: normal;

    color: #b1b1b1;
  }
  .fc-employee-heder-right {
    display: flex;
    align-items: center;
  }
  .fc-employee-heder-left {
    font-size: 18px;
    font-weight: 600 !important;
    color: #324056;
  }
}
.fc-portal-badge {
  .el-badge__content {
    width: 20px;
    height: 20px;
    border: solid 0.5px #fff;
    background-color: #c2535a;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    margin-right: 10px;
    margin-top: 8px;
  }
}
</style>
<style>
.el-dropdown-menu {
  margin-top: 0 !important;
}
.portal-profile-hover .fc-avatar-element {
  height: 100%;
}
.portal-profile-hover:hover {
  background: rgb(178 186 189 / 30%);
  -webkit-transition: 0.2s all;
  transition: 0.2s all;
}
.portal-profile-hover {
  height: 37px;
  width: 37px;
  position: relative;
  padding: 3px;
  padding-bottom: 6px;
  border-radius: 18px;
  padding-left: 0.25rem;
}
.fc-employee-portal-home-header .el-badge__content.is-fixed {
  line-height: 14px;
  position: absolute;
  top: 13px;
  right: 18px;
  -webkit-transform: translateY(-50%) translateX(100%);
  transform: translateY(-50%) translateX(100%);
  border-radius: 8px;
  padding: 0px;
  width: fit-content;
  min-width: 16px;
  font-size: 11px;
  height: 16px;
  padding-left: 4px;
  padding-right: 4px;
}
.el-dropdown-menu {
  margin-top: 0;
}
</style>
