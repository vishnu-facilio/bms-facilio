<template>
  <el-popover
    v-model="popoverShown"
    popper-class="avatar-popup"
    placement="right"
    width="220"
    height="200"
    trigger="hover"
    @hide="onHide"
  >
    <div>
      <div class="p15">
        <UserAvatar
          class="pointer pB5 pT5"
          v-for="(user, key) in avatar"
          :key="key"
          size="md"
          :user="user"
          iconClass="icon text-center icon-xxl"
        ></UserAvatar>
      </div>
    </div>
    <div slot="reference">
      <slot></slot>
    </div>
  </el-popover>
</template>
<script>
import UserAvatar from 'src/components/avatar/UserAvatarDetails'
export default {
  props: ['avatar'],
  data() {
    return {
      popoverShown: false,
    }
  },
  components: {
    UserAvatar,
  },

  methods: {
    onHide() {
      this.$emit('hide')
    },
  },
}
</script>
<style>
.avatar-popup {
  background: #fdfdfd;
}
</style>
