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
                ? user.name || users.find(usr => usr.id === user.id).name
                : '---'
            }}
          </div>
          <div :class="{ 'color-d': !group }" class="assignment-group-name">
            {{
              group
                ? group.name || $store.getters.getGroup(group.id).name
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
        <div class="avatar-details">
          <span class="q-item-label d-flex flex-wrap avatar-details-name">{{
            userObj.name,
          }}</span>
        </div>
        <div>
          <span
            class="q-item-label d-flex flex-wrap avatar-details-description"
            >{{ userObj.value }}</span
          >
        </div>

        <!-- <div>
          <span
            class="q-item-label d-flex flex-wrap avatar-details-description"
            >{{ userObj.email }}</span
          >
        </div> -->
        <div v-if="userObj.role && userObj.role.name">
          <span
            class="q-item-label d-flex flex-wrap avatar-details-description"
            >{{ userObj.role.name }}</span
          >
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
export default {
  extends: UserAvatar,
}
</script>
<style scoped>
.avatar-details-description {
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #949a9e;
  margin-top: 0.5px;
}
.avatar-details {
  display: flex;
  flex-direction: row;
}
.avatar-details .avatar-details-name {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #0c0c0c;
}
</style>
