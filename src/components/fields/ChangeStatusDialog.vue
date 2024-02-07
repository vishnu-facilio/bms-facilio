<template>
  <div>
    <el-dialog
      :visible="true"
      width="40%"
      title="Change Status"
      class="fieldchange-Dialog pB15 fc-dialog-center-container"
      custom-class="dialog-header-padding"
      :append-to-body="true"
      :before-close="close"
    >
      <div class="height120 overflow-y-scroll">
        <div>
          <el-select
            v-model="changeStatus.templateJson.new_state"
            class="fc-input-full-border-select2 width100 mT5"
            filterable
          >
            <el-option
              v-for="(status, index) in statusList"
              :key="index"
              :label="status.label"
              :value="status.value"
            >
            </el-option>
          </el-select>
        </div>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="close()" class="modal-btn-cancel">CANCEL</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="actionSave(changeStatus)"
          >Save</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
export default {
  name: 'change-status-dialog',
  props: [
    'openStatusDialog',
    'closeDialog',
    'changeStatus',
    'actionSave',
    'module',
  ],
  methods: {
    close() {
      this.closeDialog()
    },
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.module)
  },
  computed: {
    ...mapGetters(['getTicketStatusPickList']),

    statusList() {
      let statusList = []
      for (const [key, value] of Object.entries(
        this.getTicketStatusPickList(this.module)
      )) {
        let data = {}
        data['label'] = value
        data['value'] = key
        statusList.push(data)
      }
      return statusList
    },
  },
}
</script>
<style scoped>
.height120 {
  height: 120px;
}
</style>
