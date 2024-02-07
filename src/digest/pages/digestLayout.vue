<template>
  <router-view></router-view>
</template>
<script>
import { constructBaseURL, setBaseURL } from 'util/baseUrl'

export default {
  async created() {
    let baseURL = constructBaseURL('digest')
    setBaseURL(baseURL)
  },
}
</script>
