<template>
  <div class="error-page">
    <div class="error-card">
      <inline-svg
        src="svgs/error-ico"
        iconClass="icon icon-934"
        class="margin-115"
      ></inline-svg>
      <div class="text-center position-absolute max-width400px">
        <div class="error-txt">
          {{ errorCode }}
        </div>
        <div class="error-sub-txt">
          {{ $t('auth.error.ah-oh') }}
        </div>
        <div class="error-sub-txt2 text-center">
          {{ getErrorMessage }}
        </div>
        <div class="error-sub-txt2 text-center flex  justify-center">
          {{ userDetails }}
        </div>
        <div class="error-sub-txt2 text-center flex justify-center">
          <a :href="previousRoute">{{ previousRoute }}</a>
        </div>

        <el-button
          type="primary"
          round
          @click="goHome"
          class="error-btn margin-right-20"
        >
          {{ $t('auth.error.go-home') }}
        </el-button>
        <el-button type="primary" round @click="logout" class="error-btn">
          {{ $t('common.profile.logout') }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import Error409 from './Error409'
import Vue from 'vue'
import { isEmpty } from '@facilio/utils/validation'
import ErrorMixin from './ErrorMixin'

export default {
  extends: Error409,
  mixins: [ErrorMixin],
  computed: {
    ...mapState({ account: state => state.account }),
    userDetails() {
      let { user } = this.account || {}
      let { email } = user || {}

      return email
    },
    getErrorMessage() {
      let { errMessage } = this
      if (!isEmpty(errMessage)) {
        return errMessage
      }
      return this.$t('auth.error.not-authorized')
    },
  },
  methods: {
    goHome() {
      Vue.cookie.delete('lastvisit')
      let route = this.$router.resolve({ path: '/' })
      window.location.href = route?.href
    },
  },
}
</script>
<style lang="scss" scoped>
.margin-115 {
  margin-left: 115px;
}
</style>
