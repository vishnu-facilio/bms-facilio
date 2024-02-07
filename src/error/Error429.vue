<template>
  <Error40X>
    <template #errorCode>{{ errorCode }}</template>
    <template #errorSubText1>{{ getErrorMessage }}</template>
    <template #action>
      <el-button type="primary" round @click="logout" class="error-btn">
        {{ $t('common.profile.logout') }}
      </el-button>
    </template>
  </Error40X>
</template>

<script>
import Error40X from './Error40X'
import { isEmpty } from '@facilio/utils/validation'
import ErrorMixin from './ErrorMixin'
import helpers from 'src/util/helpers.js'

export default {
  components: { Error40X },
  mixins: [ErrorMixin],
  data() {
    return {
      errorCode: 429,
    }
  },
  computed: {
    getErrorMessage() {
      let { errMessage } = this
      if (!isEmpty(errMessage)) {
        return errMessage
      }
      return this.$t('auth.error.to_many_requst')
    },
  },
  methods: {
    logout() {
      this.$store.commit('LOGIN_REQUIRED', true)
      helpers.logout()
    },
  },
}
</script>
