<template>
  <div>
    <el-input
      placeholder="Please input"
      class="width100 pR20 fc-input-full-border2"
      v-model="url"
    >
      <template slot="prepend">URL</template>
    </el-input>
    <span slot="footer" class="modal-dialog-footer row">
      <el-button class="col-6 modal-btn-cancel" @click="close()"
        >Cancel</el-button
      >

      <el-button type="primary" class="col-6 modal-btn-save" @click="save()"
        >Ok</el-button
      >
    </span>
  </div>
</template>
<
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['presentAction'],
  data() {
    return {
      url: null,
      isSaved: false,
      urlActionList: {
        type: 2,
        actionType: 'url',
        action_meta: {
          actionId: -1,
          action_detail: {
            url: null,
          },
        },
        target_widgets: null,
      },
    }
  },
  created() {
    if (!isEmpty(this.presentAction)) {
      let { action_meta } = this.presentAction || {}
      let { action_detail } = action_meta
      let { url } = action_detail
      this.url = url
    }
  },
  methods: {
    save() {
      this.isSaved = true

      let { urlActionList } = this || {}
      if (!isEmpty(this.presentAction)) {
        urlActionList = this.presentAction
        if(urlActionList.id === undefined){
          urlActionList.id = -1
        }
      }

      urlActionList.action_meta.action_detail.url = this.url

      this.$emit('actions', urlActionList , {})
    },
    close() {
      let url
      if (!isEmpty(this.presentAction)) {
        let { presentAction } = this || {}
        let { action_meta } = presentAction
        let { action_detail } = action_meta
        url = action_detail.url
      }
      if (!isEmpty(this.url) && (this.isSaved || !isEmpty(url))) {
        this.$emit('closeUrl', true)
      } else this.$emit('closeUrl', false)
    },
  },
}
</script>
