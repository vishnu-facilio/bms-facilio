<template>
  <div>
    <InviteCodeForm
      v-if="otpVisibility"
      @submitOtp="submitOtp"
      @onClose="closeForm"
    ></InviteCodeForm>
    <VisitsLogForm
      v-if="typeChooserVisibility"
      :showInviteCode="isVisitModule"
      :moduleName="moduleName"
      @loadForm="openForm"
      @openInviteCode="openInviteCode"
      @onClose="closeForm"
    ></VisitsLogForm>
    <VisitorLogCreation
      v-if="formVisibility && isVisitModule"
      :moduleName="currentModule"
      :formData.sync="selectedForm"
      :visitorTypeId="selectedVisitorTypeId"
      :recordData="recordData"
      :requestedBy="requestedBy"
      @closeForm="closeForm"
      @saved="savedForm"
    ></VisitorLogCreation>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['moduleName', 'formMode', 'requestedBy'],
  components: {
    InviteCodeForm: () => import('pages/visitors/visits/InviteCodeForm.vue'),
    VisitsLogForm: () => import('pages/visitors/visits/VisitsLogForm.vue'),
    VisitorLogCreation: () =>
      import('pages/visitors/visits/VisitorLogCreation.vue'),
  },
  data() {
    return {
      formVisibility: false,
      otpVisibility: false,
      typeChooserVisibility: false,
      recordData: null,
      selectedForm: null,
      selectedVisitorTypeId: null,
      currentModule: '',
    }
  },
  created() {
    this.typeChooserVisibility = true
  },
  computed: {
    isVisitModule() {
      return this.moduleName === 'visitorlog'
    },
  },
  methods: {
    openInviteCode() {
      this.typeChooserVisibility = false
      this.$nextTick(() => (this.otpVisibility = true))
    },
    submitOtp(visitorLog) {
      let { visitorType, formId } = visitorLog

      this.recordData = visitorLog
      this.loadform(visitorType, formId)
    },
    openForm(visitorType) {
      if(!this.$helpers.isLicenseEnabled('GROUP_INVITES'))
      {
        if (this.isVisitModule) {
        this.loadFormData(visitorType)
      } else {
        let { formMode } = this
        let inviteFormId = visitorType.visitorFormId
        let query = {
          visitorTypeId: visitorType.id,
          formId: inviteFormId,
        }
        if (isWebTabsEnabled()) {
          let { moduleName } = this
          let route = findRouteForModule(moduleName, pageTypes.CREATE)

          if (route) {
            if (formMode) {
              query = { ...query, formMode }
            }

            this.$router.push({
              name: route.name,
              query,
            })
          }
        } else {
          this.$router.push({
            name: 'invites-create',
            query: { formMode, ...query },
          })
        }
        this.closeForm()
       }
      }
      else{
        if (this.isVisitModule) {
        this.loadFormData(visitorType)
      } else {
        let { formMode } = this
        let inviteFormId = visitorType.visitorFormId
        let query = {
          visitorTypeId: visitorType.id,
          formId: inviteFormId,
        }
        if (isWebTabsEnabled()) {
          if (formMode == 'bulk') {
            let moduleName = 'groupinvite'
            let route = findRouteForModule(moduleName, pageTypes.CREATE)

            if (route) {
              if (formMode) {
                query = { ...query, formMode }
              }

              this.$router.push({
                name: route.name,
                query,
              })
            }
          } else {
            let { moduleName } = this
            let route = findRouteForModule(moduleName, pageTypes.CREATE)

            if (route) {
              if (formMode) {
                query = { ...query, formMode }
              }

              this.$router.push({
                name: route.name,
                query,
              })
            }
          }
        } else {
          if (formMode == 'bulk') {
            this.$router.push({
              name: 'group-invites-create',
              query: { formMode, ...query },
            })
          } else {
            this.$router.push({
              name: 'invites-create',
              query: { formMode, ...query },
            })
          }
        }
        this.closeForm()
      }

      }

    },
    loadFormForType(visitorType, visitorForm) {
      let url = 'v2/visitorSettings/get'
      let param = { visitorType: { id: visitorType.id } }

      API.post(url, param).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let { visitorSettings } = data

          this.selectedForm = this.$getProperty(
            visitorSettings,
            visitorForm,
            null
          )
          this.currentModule =
            visitorForm === 'visitorLogForm' ? 'visitorlog' : 'invitevisitor'
          this.selectedVisitorTypeId = visitorType.id
          this.typeChooserVisibility = false
          this.otpVisibility = false
          this.$nextTick(() => (this.formVisibility = true))
        }
      })
    },
    async loadFormData(visitorType) {
      this.currentModule = 'visitorlog'
      this.selectedForm = visitorType.form
      this.typeChooserVisibility = false
      this.selectedVisitorTypeId = visitorType.id
      this.otpVisibility = false
      this.$nextTick(() => (this.formVisibility = true))
    },
    async loadform(visitorType, formId) {
      let params = {
        fetchFormRuleFields: true,
        formId: formId,
      }
      this.isLoading = true
      let { data, error } = await API.get('/v2/forms/invitevisitor', params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.selectedForm = data.form
      }
      this.isLoading = false
      this.currentModule = 'invitevisitor'
      this.typeChooserVisibility = false
      this.selectedVisitorTypeId = visitorType.id
      this.otpVisibility = false
      this.$nextTick(() => (this.formVisibility = true))
    },
    savedForm() {
      this.$emit('onSave')
      this.closeForm()
    },
    closeForm() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss">
.log-visitor-select-con {
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 800px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.log-visitor-avatar {
  width: 180px;
  height: 130px;
  border-radius: 5px;
  border: solid 1px #e4e7f3;
  background-color: #ffffff;
  box-sizing: border-box;
  -moz-box-sizing: border-box;
  -webkit-box-sizing: border-box;
  margin-right: 20px;
  margin-bottom: 20px;
  &:hover {
    box-shadow: 0 11px 14px 0 rgba(219, 221, 229, 0.45);
    cursor: pointer;
    -webkit-transition: box-shadow 0.5s 0s ease-in-out;
    -moz-transition: box-shadow 0.5s 0s ease-in-out;
    -o-transition: box-shadow 0.5s 0s ease-in-out;
    transition: box-shadow 0.5s 0s ease-in-out;
  }
}
.log-visitor-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
.fc-log-16 {
  font-size: 16px;
  font-weight: 500;
  line-height: 30px;
  letter-spacing: normal;
  color: #000000;
  padding-top: 10px;
  text-align: center;
  word-break: break-word;
}

input.otp-input-box {
  width: 70px;
  height: 60px;
  padding: 5px;
  margin: 0 10px;
  font-size: 20px;
  font-weight: 600;
  text-align: center;
  border-radius: 4px;
  border: 1px solid rgba(0, 0, 0, 0.3);
  &.error {
    border: 1px solid red !important;
  }
  &:active,
  &:focus {
    border: 1px solid #39b2c2;
    border-color: #39b2c2 !important;
  }
}
</style>
