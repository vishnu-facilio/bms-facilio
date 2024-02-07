<template>
  <div>
    <el-popover
      placement="bottom"
      width="300"
      trigger="hover"
      :close-delay="100"
      v-if="hasSignature({ field, record })"
    >
      <div class="signature-field-container">
        <img :src="getSignatureUrl({ field, record })" />
      </div>
      <a slot="reference">Signed</a>
    </el-popover>
    <div v-else>Not Signed</div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { getBaseURL } from 'util/baseUrl'

export default {
  props: ['field', 'record'],
  methods: {
    hasSignature(props) {
      let { field, record } = props
      let { name } = field || {}
      let isDefaultField = this.$getProperty(field || {}, 'default')
      if (isDefaultField) {
        return !isEmpty(record[`${name}Id`])
      } else {
        let recordData = this.$getProperty(record, `data.${name}Id`)
        return !isEmpty(recordData) || !isEmpty(record[`${name}Id`])
      }
    },
    getSignatureUrl(props) {
      // TODO move this handling to some util
      let { field, record } = props
      let { name } = field || {}
      if (isEmpty(field)) {
        return
      }

      let url = `${getBaseURL()}`
      let previewUrl = null
      let { default: isDefault = false } = field

      if (isDefault) {
        previewUrl = this.$getProperty(record, `${name}Url`)
      } else {
        previewUrl =
          this.$getProperty(record.data, `${name}Url`) ||
          this.$getProperty(record, `${name}Url`)
      }

      if (!isEmpty(previewUrl)) {
        return url + `${previewUrl.split('/api')[1]}`
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.signature-field-container {
  height: 100px;
  display: flex;
  flex-direction: column;
  justify-content: center;

  img {
    width: 100%;
    height: auto;
  }
}
</style>
