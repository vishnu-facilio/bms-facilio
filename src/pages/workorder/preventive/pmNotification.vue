<template>
  <div>
    <el-dialog
      v-if="showConfigureNotification"
      title="NOTIFICATION"
      :visible.sync="showConfigureNotification"
      width="40%"
      :append-to-body="true"
      class="fc-dialog-center-container notification-trigger-dialog"
    >
      <!-- <div class="custom-txt" @click="showReminderTemplate(reminderTemplateEdit.index)">Customize Templates</div> -->
      <el-row class="">
        <div class="fc-input-label-txt mb5">Name</div>
        <el-col :span="24">
          <el-input
            v-model="reminderTemplateEdit.name"
            class="fc-input-full-border-select3"
          ></el-input>
        </el-col>
      </el-row>
      <el-row class="mT25">
        <el-col :span="11">
          <div class="fc-input-label-txt mb5">Duration</div>
          <el-select
            v-model="reminderTemplateEdit.duration"
            placeholder="Select"
            class="fc-input-full-border-select3 width240px"
          >
            <el-option
              v-for="item in generateReminderOptions()"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="8" class="mL45">
          <div class="fc-input-label-txt mb5">Type</div>
          <el-select
            v-model="reminderTemplateEdit.type"
            placeholder="Select"
            class="fc-input-full-border-select3 width240px"
          >
            <el-option
              v-for="item in reminderType"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-col>
      </el-row>
      <el-row class="mT25">
        <!-- <el-col :span="3">Email</el-col> -->
        <el-col :span="4">
          <el-checkbox v-model="reminderTemplateEdit.isEmail" class="mT10"
            >Email</el-checkbox
          >
        </el-col>
        <!-- <el-col :span="3">SMS</el-col> -->
        <el-col :span="4">
          <el-checkbox v-model="reminderTemplateEdit.isSms" class="mT10"
            >SMS</el-checkbox
          >
        </el-col>
        <!-- <el-col :span="3">Mobile</el-col> -->
        <el-col :span="3">
          <el-checkbox v-model="reminderTemplateEdit.isMobile" class="mT10"
            >Mobile</el-checkbox
          >
        </el-col>
      </el-row>
      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel f13"
          @click="showConfigureNotification = false"
          >Cancel</el-button
        >
        <el-button class="modal-btn-save f13" @click="addReminder"
          >Save</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  props: ['reminders', 'showConfigureNotification'],
  data() {
    return {
      reminderTemplateEdit: {},
      reminderType: [
        {
          label: 'Before Execution',
          value: 1,
        },
        {
          label: 'After Execution',
          value: 2,
        },
        {
          label: 'Before Due',
          value: 3,
        },
        {
          label: 'After Due',
          value: 4,
        },
      ],
    }
  },
  watch: {
    reminders(val, oldVal) {
      this.$emit('update:reminders', val)
    },
    showConfigureNotification(val, oldVal) {
      this.$emit('update:showConfigureNotification', val)
    },
  },
  methods: {
    generateReminderOptions() {
      let options = []
      options.push({
        label: '30 Min',
        value: 1800,
      })
      for (let i = 1; i < 24; i++) {
        options.push({
          label: i + (i === 1 ? ' Hour' : ' Hours'),
          value: i * 3600,
        })
      }
      for (let i = 1; i < 32; i++) {
        options.push({
          label: i + (i === 1 ? ' Day' : ' Days'),
          value: i * 24 * 3600,
        })
      }
      return options
    },
    addReminder() {
      this.showConfigureNotification = false
      // if (this.editNotificationConfiguration) {
      Object.assign(
        this.reminders[this.reminderTemplateEdit.index],
        this.$helpers.cloneObject(this.reminderTemplateEdit)
      )
      // }
      // else {
      //   this.model.triggerData.reminders.push(this.$helpers.cloneObject(this.reminderTemplateEdit))
      // }
    },
  },
}
</script>
<style>
.notification-trigger-dialog .el-dialog__body {
  height: 330px;
  overflow: hidden;
}
@media screen and (max-width: 1280px) and (min-width: 800px) {
  .notification-trigger-dialog .el-dialog {
    width: 45% !important;
  }
}
</style>
