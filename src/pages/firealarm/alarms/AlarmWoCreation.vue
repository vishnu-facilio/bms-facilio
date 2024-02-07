<template>
  <el-dialog
    :title="title"
    :visible.sync="canShow"
    class="wo-state-transition-dialog-center alarm-wo-creation"
    custom-class="wo-web-form"
    :width="'35%'"
    :append-to-body="true"
  >
    <Spinner v-if="isLoading" size="80" :show="true"></Spinner>
    <template v-else>
      <f-webform
        :form.sync="formObj"
        :module="moduleName"
        :isSaving="isSaving"
        :isV3Api="isV3Api"
        :canShowPrimaryBtn="true"
        :canShowSecondaryBtn="true"
        :isEdit="false"
        formLabelPosition="top"
        @save="saveRecord"
        @cancel="closeAction"
      ></f-webform>
    </template>
  </el-dialog>
</template>
<script>
import FormCreation from '@/base/FormCreation'
import WorkorderCreation from 'pages/workorder/workorders/v1/mixins/workorderCreation'
import FWebform from '@/FWebform'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  name: 'alarm-workorder-creation',
  extends: FormCreation,
  components: { FWebform },
  mixins: [WorkorderCreation],
  props: ['canShowDialog', 'currentAlarmId'],
  data() {
    return {
      isSaving: false,
      moduleName: 'workorder',
      moduleDisplayName: 'Workorder',
      selectedForm: {
        id: -1,
        name: 'alarm_workorder_web',
      },
    }
  },
  computed: {
    canShow: {
      get() {
        return this.canShowDialog
      },
      set(value) {
        this.$emit('update:canShowDialog', value)
      },
    },
    alarmId: {
      get() {
        return this.currentAlarmId
      },
      set(value) {
        this.$emit('update:currentAlarmId', value)
      },
    },
    title() {
      let { moduleDisplayName } = this
      return `Create ${moduleDisplayName}`
    },
  },
  async created() {
    await this.loadFormData()
  },
  methods: {
    init() {
      // override to prevent default code in formcreation
    },
    saveRecord(formModel) {
      let { alarmId } = this
      let { tasksString, ticketattachments } = formModel
      delete formModel.ticketattachments
      delete formModel.tasksString

      let { selectedForm } = this
      let formId = (selectedForm || {}).id

      let formData = {
        workorder: {
          data: {},
          formId: formId || -1,
        },
      }

      this.isSaving = true

      let serializedData = this.serializedData(formData, formModel)

      if (!isEmpty(ticketattachments)) {
        serializedData['ticketattachments'] = ticketattachments
      }
      if (!isEmpty(tasksString)) {
        serializedData['tasksString'] = JSON.stringify(tasksString)
      }
      serializedData.id = alarmId

      API.post('v2/alarmOccurrence/createWO', serializedData)
        .then(response => {
          this.successHandler(response)
        })
        .finally(() => (this.isSaving = false))
    },
    successHandler(props) {
      let { data, error } = props
      let { currentAlarmId } = this
      let { woId } = data || {}
      if (!error) {
        this.$message.success('Workorder created successfully!')
        this.$emit('onSuccess', {
          lastOccurenceId: currentAlarmId,
          woId,
        })
        this.closeAction()
      } else {
        this.$message.error(error.message || 'Error Occurred')
      }
    },
    closeAction() {
      this.canShow = false
      this.alarmId = null
    },
  },
}
</script>
<style lang="scss">
.alarm-wo-creation {
  .el-dialog__body {
    height: 500px;
    overflow: scroll;
    .f-webform-container {
      .el-form {
        padding-bottom: 50px;
        .section-container {
          .section-header {
            display: none;
          }
        }
      }
      .fc-web-form-action-btn {
        position: absolute;
        bottom: 0;
        right: 0;
        left: 0;
        z-index: 2000;
      }
    }
  }
}
</style>
