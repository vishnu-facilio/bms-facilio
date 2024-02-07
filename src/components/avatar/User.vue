<template>
  <div v-if="userObj" class="inline">
    <div class="fc-avatar-element q-item-division relative-position">
      <div class="q-item-side-left q-item-section">
        <avatarPopover
          :user="userObj"
          :moduleName="moduleName"
          v-if="showPopover && userObj.id !== -1"
        >
          <span slot="reference">
            <avatar :size="size" :user="userObj" :color="color"> </avatar>
          </span>
        </avatarPopover>
        <span v-else
          ><avatar :size="size" :user="userObj" :color="color"> </avatar
        ></span>
      </div>
      <template v-if="showLabel">
        <div
          class="wo-team-txt"
          v-if="(user && user.id > 0) || (group && group.id > 0)"
        >
          <div :class="{ 'color-d': !user }" class="assignment-avatar-name">
            {{
              user && user.id > 0
                ? user.name ||
                  (users.find(usr => usr.id === user.id) || {}).name
                : '---'
            }}
          </div>
          <div :class="{ 'color-d': !group }" class="assignment-group-name">
            {{
              group
                ? group.name || ($store.getters.getGroup(group.id) || {}).name
                : '---'
            }}
          </div>
        </div>
        <div
          v-else
          style="padding-top: 7px;font-size: 14px;color: #aaa;letter-spacing: 0.2px;"
          class="wo-team-txt"
        >
          ---/---
        </div>
      </template>
      <div v-else-if="name != false">
        <div>
          <span class="q-item-label d-flex flex-wrap">{{ userObj.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Avatar from '@/Avatar'
import AvatarPopover from '@/AvatarPopover'
import { mapState } from 'vuex'

export default {
  created() {
    this.$store.dispatch('loadGroups')
  },
  components: {
    Avatar,
    AvatarPopover,
  },
  props: {
    size: {
      type: String,
    },
    user: {
      type: Object,
    },
    avatarStyle: {
      type: Object,
    },
    name: {
      type: Boolean,
      default: () => {
        return true
      },
    },
    showLabel: {
      type: Boolean,
    },
    showPopover: {
      type: Boolean,
    },
    moduleName: {
      type: String,
    },
    group: {
      type: Object,
    },
    hovercard: {
      type: Boolean,
    },
  },
  data() {
    return {
      hoverCardId: 'userHoverCardId-' + (this.user ? this.user.id : 'unkown'),
      loadHoverCardData: false,
      labelStatus: this.hideLabel,
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
    userObj() {
      let hasUser = this.user && this.user.id > 0
      let user = hasUser
        ? this.$getProperty(this.user, 'name')
          ? this.user
          : this.users.find(usr => usr.id === this.user.id)
        : null

      if (user) {
        return user
      } else {
        return { id: -1, name: 'Unknown' }
      }
    },
    color() {
      if (this.userObj.id !== -1) {
        return ''
      }
      return this.group ? '#c3c3c3' : '#e3e3e3'
    },
  },
  methods: {
    loadHoverCard() {
      this.loadHoverCardData = true
    },
    hideHoverCard() {
      this.loadHoverCardData = false
    },
  },
}
</script>
<style>
.assignment-avatar-name {
  font-size: 14px;
  letter-spacing: 0.2px;
  color: #333333;
  max-width: 200px;
  overflow-x: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.wo-team-txt {
  margin-left: 10px !important;
  overflow-x: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;
}
</style>
