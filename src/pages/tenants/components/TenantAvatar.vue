<template>
  <div v-if="user || name" class="fc-avatar-inline">
    <div
      :class="[
        'fc-avatar',
        moduleName === 'tenantunit'
          ? 'avatar-size-tenant-unit'
          : 'avatar-size-tenant',
      ]"
      :style="bgColor"
    >
      {{ trimmedName }}
    </div>
  </div>
</template>
<script>
import Avatar from '@/Avatar'
export default {
  extends: Avatar,
  props: ['moduleName'],
}
</script>
<style scoped>
.avatar-size-tenant {
  font-size: 20px;
  height: 70px;
  line-height: 30px;
  width: 70px;
}
.avatar-size-tenant-unit {
  font-size: 28px;
  height: 120px;
  line-height: 30px;
  width: 120px;
}
</style>
