<template>
  <div>
    <div class="avatar-container">
      <UserAvatar
        class="pointer pB5 pT5"
        v-for="(user, key) in listAvatar"
        v-if="key < count"
        :key="key"
        size="md"
        :user="user"
        iconClass="icon text-center icon-xxl"
      ></UserAvatar>

      <div v-if="listAvatar.length > count" class="extended-container ">
        <AvatarTooltip :avatar="groupedAvatar">
          <button class="popover" v-if="listAvatar.length > count">
            <div class="overall-container">
              <div class="more-avatar">{{ `+${getRemainingCount}` }}</div>
              <div class="more">{{ 'Attendees' }}</div>
            </div>
          </button>
        </AvatarTooltip>
      </div>
    </div>
  </div>
</template>
<script>
import AvatarTooltip from 'src/components/GroupedAvatar/GroupedAvatarPopover.vue'
import UserAvatar from '@/avatar/UserAvatarDetails'
export default {
  props: ['avatars', 'count'],
  data() {
    return {
      stackedLimit: 3,
      stackedMenu: false,
      menuMaxHeight: `${60 * 5 + 4}px`,
      listAvatar: [],
      groupedAvatar: [],
      active: false,
    }
  },
  components: {
    UserAvatar,
    AvatarTooltip,
  },
  mounted() {
    if (this.avatars) {
      let avatarObject = this.avatars
      for (let key in avatarObject) {
        this.listAvatar.push(avatarObject[key])
        if (key >= 2) {
          this.groupedAvatar.push(avatarObject[key])
        }
      }
    }
  },
  computed: {
    getRemainingCount() {
      let remainingCount = 0
      if (this.listAvatar.length > 3) {
        remainingCount = this.listAvatar.length - 2
      }
      return remainingCount
    },
  },
}
</script>
<style>
.avatar-container {
  display: flex;
  flex-direction: column;
}
.button .grouped-avatar {
  position: absolute;
}
.overall-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  font-size: 14px;
}
.extended-container {
  left: 43px;
  height: 40px;
  width: 35px;
  display: flex;
  z-index: 3;
  /* position: absolute; */
  text-align: center;
  line-height: 30px;
  padding-top: 5px;
}
.popover .more-avatar {
  height: 30px;
  width: 30px;
  background-color: antiquewhite;
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.popover .more {
  padding-left: 12px;
}
.h-group-avatar.fc-avatar {
  border: 3px solid #fff;
}
.popover {
  border: none;
  background: none;
  color: black;
  padding-left: 0px;
}
</style>
