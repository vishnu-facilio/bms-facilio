<template>
  <Error40X>
    <template #errorCode>{{ errorCode }}</template>
    <template #errorSubText1>{{ $t('auth.error.somthing-missing') }}</template>
    <template #errorSubText2>
      {{ getErrorMessage }}
    </template>
    <template #action>
      <el-button
        v-if="canGoBack"
        type="primary"
        round
        @click="goBack"
        class="error-btn"
      >
        {{ $t('setup.setupLabel.go_back') }}
      </el-button>
    </template>
  </Error40X>
</template>

<script>
import Error40X from './Error40X'
import { isEmpty } from '@facilio/utils/validation'
import ErrorMixin from './ErrorMixin'

export default {
  mixins: [ErrorMixin],
  data() {
    return {
      errorCode: 404,
      canGoBack: window.history.length > 1,
    }
  },
  computed: {
    getErrorMessage() {
      let { errMessage } = this
      if (!isEmpty(errMessage)) {
        return errMessage
      }
      return this.$t('auth.error.page-not-exist')
    },
  },
  components: { Error40X },
  methods: {
    goBack() {
      window.history.go(-1)
    },
  },
}
</script>
