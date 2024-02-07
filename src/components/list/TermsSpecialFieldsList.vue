<template>
  <div>
    <div>
      <i
        class="el-icon-s-order pointer edit-icon-color"
        data-arrow="true"
        title="Long Description"
        v-tippy
        @click="openLongDesc()"
      ></i>
    </div>
    <LongDescriptionEditor
      v-if="longDescVisibility"
      :content="$getProperty(terms, 'longDesc')"
      :disabled="!$hasPermission('termsandconditions:UPDATE')"
      @onSave="updateLongDescriptionData"
      @onClose="longDescVisibility = false"
    ></LongDescriptionEditor>
  </div>
</template>
<script>
import LongDescriptionEditor from 'src/pages/purchase/tandc/RichTextAreaEditor.vue'
import { API } from '@facilio/api'
import { eventBus } from '@/page/widget/utils/eventBus'
export default {
  props: ['terms'],
  components: {
    LongDescriptionEditor,
  },
  data() {
    return {
      longDescVisibility: false,
    }
  },
  mounted() {},
  methods: {
    openLongDesc() {
      this.longDescVisibility = true
    },
    async updateLongDescriptionData(longDesc) {
      let params = {
        id: this.$getProperty(this, 'terms.id'),
        data: { longDesc },
      }
      let { error } = await API.updateRecord('termsandconditions', params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(
          this.$t('common.products.long_description_edited_successfully')
        )
        this.longDescVisibility = false
        eventBus.$emit('refesh-parent')
      }
    },
  },
}
</script>
