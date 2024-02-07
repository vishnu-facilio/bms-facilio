<template>
  <Error40X>
    <template #errorCode>{{ errorCode }}</template>
    <template #errorSubText1>{{ getErrorMessage }}</template>
    <template #userDetails>
      <div class="error-sub-txt2 text-center flex justify-center pB0">
        {{ userDetails }}
      </div>
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
import { mapState } from 'vuex'
import Vue from 'vue'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import getProperty from 'dlv'
import ErrorMixin from './ErrorMixin'

export default {
  mixins: [ErrorMixin],
  components: { Error40X },
  data() {
    return {}
  },
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
      return this.$t('auth.error.somthing-missing')
    },
    errorCode() {
      let errorCode = getProperty(this, '$route.query.errorCode', '404')
      return errorCode
    },
  },
  methods: {
    logout() {
      Vue.cookie.delete('fc.currentSite')
      Vue.cookie.delete('fc.currentOrg')
      this.$store.commit('LOGIN_REQUIRED', true)

      let loginURL = '/auth/login'

      API.get('/logout', null, { force: true })
        .then(() => {
          window.location.href = loginURL
        })
        .catch(error => {
          if (error) console.warn(error)
        })
    },
  },
}
</script>
