<template>
  <div>
    <FileFieldAttachments
      :module="'templatefilefieldattachment'"
      :fileFieldsList="fileFieldsList"
      :fileFieldIds.sync="fileFields"
    ></FileFieldAttachments>
  </div>
</template>
<script>
import FileFieldAttachments from 'pages/setup/EmailTemplates/components/FileFieldAttachment'
import { isEmpty } from 'util/validation'
import { mapState } from 'vuex'
export default {
  props: ['fileIdsList'],
  data() {
    return {}
  },
  components: {
    FileFieldAttachments,
  },
  mounted() {
    // if (this.fileIdsList && this.fileIdsList.length) {
    //   this.fileFieldIds = this.fileIdsList
    // }
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
    fileFields: {
      get() {
        return this.fileIdsList
      },
      set(value) {
        this.$emit('update:fileIdsList', value)
      },
    },
    fileFieldsList() {
      let { moduleMeta } = this
      if (!isEmpty(moduleMeta)) {
        let { fields } = moduleMeta
        let fileFields = []
        if (!isEmpty(fields)) {
          fields.forEach(field => {
            let { dataTypeEnum, displayType } = field || {}
            if (
              !isEmpty(displayType) &&
              displayType._name === 'FILE' &&
              !isEmpty(dataTypeEnum) &&
              dataTypeEnum._name === 'FILE'
            ) {
              fileFields.push(field)
            }
          })
          return fileFields
        }
      }
      return []
    },
  },
}
</script>
