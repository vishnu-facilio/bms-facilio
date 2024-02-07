<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :title="title"
    :append-to-body="true"
    :show-close="false"
    custom-class="pushNotifiDialog"
    :close-on-click-modal="false"
  >
    <div v-if="loadingForm" class="mT10">
      <el-row class="mB20" v-for="index in [1, 2, 3]" :key="index">
        <el-col :span="24">
          <div class="lines loading-shimmer width40 mB10"></div>
          <div class="lines loading-shimmer width90 mB15 line-h40"></div>
        </el-col>
      </el-row>
    </div>
    <el-form
      v-else
      ref="push-notification"
      :rules="rules"
      :model="pushNotification.templateJson"
    >
      <el-form-item :label="$t('common.header.application')" prop="application">
        <el-select
          v-model="pushNotification.templateJson.application"
          class="fc-input-full-border-select2"
          filterable
          :loading="loadingApps"
          @change="appSwitch"
          style="width: 100%;"
        >
          <el-option
            v-for="app in appList"
            :key="app.id"
            :label="app.name"
            :value="app.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('common.products.to')" prop="to">
        <el-select
          v-model="pushNotification.templateJson.to"
          class="fc-input-full-border-select2"
          :placeholder="`   ${$t('common._common.select')}`"
          multiple
          filterable
          :allow-create="true"
          style="width: 100%;"
          :loading="loadingUsers"
          remote
          collapse-tags
          :remote-method="loadToValues"
          @change="changeOnToFields()"
        >
          <el-option-group
            v-for="(userGrpOptions,
            userGrpLabel,
            index) in userOptionFilteredList"
            :label="userGrpLabel"
            :key="`${userGrpLabel}-${index}`"
          >
            <el-option
              v-for="(option, optIdx) in userGrpOptions"
              :key="`${userGrpLabel}-${option.value}-${optIdx}`"
              :label="option.label"
              :value="option.value"
              class="subject"
            >
              {{ option.label }}
            </el-option>
          </el-option-group>
        </el-select>
      </el-form-item>
      <el-form-item>
        <div>
          <div
            class="fc-green4-label14 pointer"
            @click="openPlaceholderEditor()"
          >
            {{ $t('setup.emailTemplates.advanced_merge_fields') }}
          </div>
        </div>
      </el-form-item>
      <el-form-item :label="$t('common._common.subject')" prop="subject">
        <el-input
          :placeholder="$t('common.placeholders.please_enter_subject')"
          v-model="pushNotification.templateJson.subject"
          type="input"
          class="subject fc-input-full-border2"
        ></el-input>
      </el-form-item>
      <el-form-item
        :label="$t('common._common.message')"
        prop="message"
        class="mB0"
      >
        <el-input
          v-model="pushNotification.templateJson.message"
          type="textarea"
          class="subject fc-input-full-border-select2 notification-txt-area "
          :placeholder="$t('common.placeholders.type_your_message')"
          :autosize="{ minRows: 6, maxRows: 6 }"
        ></el-input>
      </el-form-item>
      <el-form-item v-if="showCheckIsPush" class="flex-middle pB10 mB40">
        <el-checkbox
          v-model="pushNotification.templateJson.isPushNotification"
          class="mT20"
          >{{ $t('setup.notification.send_push_notifications') }}</el-checkbox
        >
      </el-form-item>
    </el-form>
    <div class="modal-dialog-footer">
      <el-button type="button" class="modal-btn-cancel" @click="cancelDialog">
        {{ $t('common._common.cancel') }}
      </el-button>
      <el-button
        type="button"
        class="modal-btn-save"
        @click="save"
        :disabled="loadingForm"
      >
        {{ $t('common._common._save') }}
      </el-button>
    </div>
    <el-dialog
      :visible="showPlaceholderEditor"
      width="60%"
      class="fieldchange-Dialog pB15 fc-dialog-center-container fc-dialog-center-body-p0"
      :title="$t('common.products.script')"
      :append-to-body="true"
      :before-close="closePlaceholder"
      :close-on-click-modal="false"
    >
      <div class="height350 overflow-y-scroll pB50">
        <CodeMirror :codeeditor="true" v-model="placeholderString"></CodeMirror>
      </div>
      <div class="modal-dialog-footer" style="z-index: 900;">
        <el-button @click="closePlaceholder" class="modal-btn-cancel">{{
          $t('common._common.cancel').toUpperCase()
        }}</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="savePlaceholder"
        >
          {{ $t('common._common.save').toUpperCase() }}
        </el-button>
      </div>
    </el-dialog>
  </el-dialog>
</template>
<script>
import { getApp } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import { NotificationModel } from '../models/NotificationModel'
import CodeMirror from '@/CodeMirror'
export default {
  props: ['moduleName', 'actionObj'],
  components: { CodeMirror },
  data() {
    return {
      pushNotification: {},
      placeholderString: '',
      showPlaceholderEditor: false,
      appList: [],
      loadingApps: false,
      searchText: '',
      loadingUsers: false,
      loadingForm: false,
      canShowScriptEditor: false,
      userFields: [],
      fields: {},
      userOptionList: {
        Users: [],
        Fields: [],
        PlaceHolders: [],
      },
      rules: {
        message: {
          required: true,
          message: `${this.$t('common._common.content_should_not_be_empty')}`,
          trigger: 'change',
        },
        to: {
          required: true,
          message: `${this.$t('common._common.add_receiving_address')}`,
          trigger: 'blur',
        },
      },
    }
  },
  created() {
    this.init()
  },
  computed: {
    title() {
      return this.$t('setup.notification.send_notification')
    },
    showCheckIsPush() {
      let { templateJson } = this.pushNotification
      let { application: appId } = templateJson
      let { linkName } =
        (this.appList || []).find(app => app.id === appId) || {}
      let canShowIsPushNotif = [
        'newapp',
        'maintenance',
        'service',
        'tenant',
        'vendor',
      ].includes(linkName)
      return canShowIsPushNotif
    },
    userOptionFilteredList() {
      let arrayedList = Object.entries(this.userOptionList || {})
      let filteredList = arrayedList.reduce((prev, [key, arr]) => {
        if (!isEmpty(arr)) {
          prev[key] = arr
        }
        return prev
      }, {})
      return filteredList
    },
  },
  methods: {
    async init() {
      this.loadingForm = true
      let { actionObj = {}, moduleName } = this
      this.pushNotification = new NotificationModel({
        ...actionObj,
        moduleName,
      })
      this.placeholderString = this.$getProperty(
        this.pushNotification,
        'templateJson.userWorkflow.workflowV2String',
        ''
      )
      await this.loadAvailableApps()
      let appId = this.pushNotification?.templateJson?.application
      if (isEmpty(appId)) this.setApplicationId()
      await this.loadUsers()
      this.userOptionList.Fields = await this.pushNotification.fetchFieldList()
      this.changeOnToFields(this.userOptionList)

      this.loadingForm = false
    },
    openScriptDialog() {
      this.canShowScriptEditor = true
    },
    async loadAvailableApps() {
      this.loadingApps = true
      try {
        this.appList = await this.pushNotification.fetchAppList()
      } catch (e) {
        this.$message.error(e.message)
      }

      this.loadingApps = false
    },
    async changeOnToFields(alternateList) {
      let receiverList = alternateList || {}
      if (isEmpty(alternateList)) {
        receiverList = { Users: this.userOptionList.Users }
        let fields = await this.pushNotification.fetchFieldList()
        receiverList.Fields = fields
      }
      this.userOptionList = this.pushNotification.handleAddrChange(
        'to',
        receiverList
      )
    },
    setApplicationId() {
      let currentAppId = (getApp() || {}).id
      let [{ id: firstAppId } = {}] = this.appList || []
      this.pushNotification.templateJson.application =
        currentAppId || firstAppId
    },
    appSwitch() {
      this.$set(this.pushNotification.templateJson, 'to', [])
      this.loadUsers()
    },
    savePlaceholder() {
      this.$set(
        this.pushNotification.templateJson.userWorkflow,
        'workflowV2String',
        this.placeholderString
      )
      this.closePlaceholder(true)
    },
    closePlaceholder(isSaving) {
      if (isSaving)
        this.placeholderString = this.$getProperty(
          this.pushNotification,
          'templateJson.userWorkflow.workflowV2String',
          ''
        )
      this.showPlaceholderEditor = false
    },
    openPlaceholderEditor() {
      this.showPlaceholderEditor = true
    },
    async loadToValues(search) {
      this.loadUsers(search)
      this.userOptionList.Fields = await this.pushNotification.fetchFieldList(
        search
      )
      if (isEmpty(search)) {
        this.userOptionList = this.pushNotification.handleAddrChange(
          'to',
          this.userOptionList
        )
      } else {
        if (!isEmpty(this.userOptionList?.PlaceHolders)) {
          this.userOptionList.PlaceHolders = this.searchForValues(
            search,
            this.userOptionList.PlaceHolders
          )
        }
      }
    },
    searchForValues(searchTerm = '', searchableList = []) {
      let filteredData = searchableList.filter(item => {
        let lcLabel = (item.label || '').toLowerCase()
        let lcSearchTerm = searchTerm.toLowerCase()
        return lcLabel.match(lcSearchTerm)
      })
      return filteredData
    },
    async loadUsers(search = '') {
      this.loadingUsers = true
      let { data, error } = await this.pushNotification.getReceivingUsers(
        search
      )
      if (!error) {
        this.userOptionList.Users = data
      } else {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      }
      this.loadingUsers = false
    },
    cancelDialog() {
      this.closeDialog()
    },
    closeDialog() {
      this.$emit('onClose')
    },
    async save() {
      let isValid = false
      try {
        isValid = await this.$refs['push-notification'].validate()
      } catch {
        isValid = false
      }
      if (!isValid) return
      this.saved = true
      this.$emit('onSave', this.serializeData())
      this.closeDialog()
    },
    serializeData() {
      return this.pushNotification.serialize()
    },
  },
}
</script>
<style scoped>
.lines {
  height: 15px;
  border-radius: 5px;
}
.line-h40 {
  height: 40px;
}
</style>
<style lang="scss">
.pushNotifiDialog {
  margin: 15vh auto !important;
  width: 50% !important;
  height: 70vh !important;
  .el-dialog__body {
    padding-top: 0px !important;
    padding-bottom: 0px !important;
    height: 52vh;
    overflow: scroll;
  }
  .el-dialog__header {
    padding: 20px;
    border-bottom: 1px solid #e2e8ee !important;
    position: sticky;
    top: 0;
  }
}
</style>
