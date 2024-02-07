<template>
  <div class="p30">
    <spinner v-if="loading" :show="loading" size="80"></spinner>
    <div v-else class="width50">
      <div class="d-flex mB20 flex-middle justify-content-space">
        <div class="setup-modal-title">
          {{ $t('profile.signature.signature') }}
        </div>
        <i
          class="el-icon-delete"
          :class="{ 'cursor-not-allowed': isNew }"
          @click="deleteSign"
        ></i>
      </div>
      <RichTextArea v-model="signatureContent" :isEdit="true" />
      <el-button
        :disabled="isNew"
        :loading="saving"
        @click="addSign"
        class="btn btn--primary setup-el-btn fwBold mT20"
      >
        {{ $t('profile.signature.saveKey') }}
      </el-button>
    </div>
  </div>
</template>
<script>
import RichTextArea from '@/forms/RichTextArea'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
export default {
  components: {
    RichTextArea,
  },
  data() {
    return {
      signatureContent: null,
      saving: false,
      loading: false,
      isEdit: false,
    }
  },
  created() {
    this.getSign()
  },
  computed: {
    isNew() {
      let { signatureContent } = this || {}
      return isEmpty(signatureContent)
    },
  },
  methods: {
    async addSign() {
      this.saving = true
      let { signatureContent } = this || {}
      let { error } = await API.post(
        '/v2/application/users/addOrUpdateSignature',
        {
          signatureContent,
        }
      )
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(this.$t('profile.signature.success'))
      }

      this.saving = false
    },
    async getSign() {
      this.loading = true

      let { error, data } = await API.get('/v2/application/users/getSignature')

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.signatureContent = this.$getProperty(
          data,
          'signature.signatureContent',
          ''
        )
      }
      this.loading = false
    },
    async deleteSign() {
      if (this.isNew) return

      this.loading = true

      let { error, data } = await API.get(
        '/v2/application/users/deleteSignature'
      )

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let signatureDelete = this.$getProperty(data, 'signatureDelete', '')
        if (signatureDelete == 'success') {
          this.signatureContent = ''
          this.$message.success(this.$t('profile.signature.deleteSuccess'))
        }
      }
      this.loading = false
    },
  },
}
</script>
