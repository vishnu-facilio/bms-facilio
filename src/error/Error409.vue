<template>
  <Error40X>
    <template #errorCode>{{ errorCode }}</template>
    <template #errorSubText1>{{ $t('auth.error.ah-oh') }}</template>
    <template #errorSubText2>
      {{ $t('auth.error.not-authorized') }}
    </template>
    <template #action>
      <el-button type="primary" round @click="logout" class="error-btn">
        {{ $t('common.profile.logout') }}
      </el-button>
    </template>
  </Error40X>
</template>

<script>
import Error40X from './Error40X'
import helpers from 'src/util/helpers.js'

export default {
  components: { Error40X },
  data() {
    return {
      errorCode: 403,
    }
  },
  methods: {
    logout() {
      this.$store.commit('LOGIN_REQUIRED', true)
      helpers.logout()
    },
  },
}
</script>
