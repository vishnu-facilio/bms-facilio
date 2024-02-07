<template>
  <div class="height100 pL50">
    <router-view></router-view>
  </div>
</template>
<script>
import { isWebTabsEnabled } from '@facilio/router'

export default {
  created() {
    if (!isWebTabsEnabled()) {
      if (this.$route.path === '/app/wp') {
        this.$router.push({
          path: `/app/wp/workplacetreemap`,
        })
      }
    }
  },
}
</script>
