<template>
  <!-- Checkin flow starts here -->
  <div>
    <full-page-stepper
      ref="stepper"
      :steps="steps"
      :initialShared="preFillData"
      :initialStep="steps[0]"
      @exit="handleExit"
      @finalStep="stepsOver"
      :keepAliveData="true"
      v-if="!loading && isAuthDone && !blocked"
    >
    </full-page-stepper>
    <mobile-or-qr
      @authDone="handleAuth"
      action="checkin"
      v-else-if="!loading && !isAuthDone"
    >
    </mobile-or-qr>

    <div class="kiosk-page-loading" v-else-if="loading">
      <facilio-new-loader> </facilio-new-loader>
    </div>

    <div class="fc-kiosk-form-footer">
      <portal-target name="kiosk-footer-logo" :disabled="isAuthDone">
        <!-- USE BLACK LOGO except when mobile or QR screen is shown , Mobile or QR comp alternates between black and white logos -->
        <img
          src="~assets/facilio-logo-black.svg"
          style="width: 70px; height: 20px;"
        />
      </portal-target>
    </div>
  </div>
</template>

<script>
import getProperty from 'dlv'
import facilioNewLoader from 'src/components/facilio-loading'
import { getVisitorSetting } from './VisitorApis'
import { errorRedirect } from './visitorHelpers'
import kioskVisitorType from 'src/devices/VisitorKiosk/KioskVisitorType'
// stepper needs a unique title for each step to find active index, just put step-numbers in this case
import kioskAgreement from 'src/devices/VisitorKiosk/KioskAgreement'
import kioskIDScan from 'src/devices/VisitorKiosk/KioskIDScanStep'
import fullPageStepper from 'src/devices/VisitorKiosk/FullPageStepper'
import kioskPhoto from 'src/devices/VisitorKiosk/KioskPhotoStep'
import { mapGetters } from 'vuex'
import mobileOrQr from 'src/devices/VisitorKiosk/MobileOrQR'
import kioskVisitorDetails from 'src/devices/VisitorKiosk/KioskVisitorDetails'
import { API } from '@facilio/api'
export default {
  components: {
    fullPageStepper,
    facilioNewLoader,
    mobileOrQr,
  },
  computed: {
    ...mapGetters(['currentAccount']),
  },
  beforeDestroy() {
    window.clearTimeout(this.timer)
  },
  data() {
    return {
      visitorStatus: null, //new,returning,preRegistered
      visitorTypes: [],
      isAuthDone: false,
      formFields: [],
      isTypeDone: false, //first stepper finished with visitor type choice
      blocked: false,
      loading: false,
      badgeEnabled: false,
      preFillData: {},
      visitorTypeSettings: null,
      userData: {
        phone: null,
      },
      stateTransitionId: null,

      steps: [],
      timer: null,
    }
  },
  methods: {
    async getVisitorForm() {
      API.get('/v2/forms', { moduleName: 'visitor' }).then(
        ({ error, data }) => {
          if (!error) {
            return data.forms[0]
          }
        }
      )
    },
    async constructStepperForVisitorType(visitorTypeId) {
      let { visitorSettings } = await getVisitorSetting(visitorTypeId)
      this.visitorTypeSettings = visitorSettings

      this.visitorForm = await this.getVisitorForm() //visitor form in addition to the logging form recieved in settings
      this.processVisitorAndLogFormFields()
      this.steps.push({
        title: 'Kiosk full form Step',
        component: kioskVisitorDetails,
        props: {
          visitorFormFields: this.visitorFormFields,
          visitorLogFormFields: this.visitorLogFormFields,
          hostSettings: this.visitorTypeSettings.hostSettings,
        },
      })

      if (this.visitorTypeSettings.photoEnabled) {
        this.steps.push({
          title: 'take photo',
          component: kioskPhoto,
          props: { photoSettings: this.visitorTypeSettings.photoSettings },
        })
      }
      if (this.visitorTypeSettings.idScanEnabled) {
        this.steps.push({
          title: 'ID Scan',
          component: kioskIDScan,
          props: {},
        })
      }

      if (this.visitorTypeSettings.ndaEnabled) {
        this.steps.push({
          title: 'kiosk agreement',
          component: kioskAgreement,
          props: { ndaContent: this.visitorTypeSettings.finalNdaContent },
        })
      }

      this.badgeEnabled = this.visitorTypeSettings.badgeEnabled
      return
    },

    async handleAuth(authData) {
      this.loading = true

      if (authData.type == 'qr') {
        //pre registered visitor flow
        this.visitorStatus = 'preRegistered'
        let qr = authData.qr
        let splitQr = qr.split('_')
        let passcode = (this.visitorLogId = splitQr[1])
        this.getVisitorFromInvite(passcode)
          .then(preFillFromInvite => {
            this.$set(this.preFillData, 'preFill', preFillFromInvite)

            this.constructStepperForVisitorType(this.visitorTypeId).then(() => {
              this.isAuthDone = true
              this.loading = false
              this.isTypeDone = true
            })
          })
          .catch(() => {
            this.$message.error(
              'Invalid invite! Please try with valid invite QR or Code.'
            )
            errorRedirect()
          })
      } else if (authData.type == 'mobile') {
        //change authdata.mobile to phone

        this.userData.phone = authData.mobile
        //existing or new user

        let visitor = await this.getVisitorDetails(this.userData.phone)
        if (visitor) {
          //for returning visitor check for blocked status
          this.visitorStatus = 'returning' //pass shared data inside stepper comp for returning visitor

          this.$set(this.preFillData, 'preFill', visitor)
          this.steps = [
            {
              title: 'Visitor Type step',
              component: kioskVisitorType,
            },
          ]
          this.isAuthDone = true
        } else {
          this.visitorStatus = 'new'
          this.steps = [
            {
              title: 'Visitor Type step',
              component: kioskVisitorType,
            },
          ]
          this.isAuthDone = true
          //no pre fill
        }

        this.loading = false
      }
    },

    async getVisitorDetails(phoneNumber) {
      let filters = {
        phone: {
          operatorId: 3,
          value: [phoneNumber + ''],
        },
      }
      let url = '/v2/visitor/all'
      url += '?filters=' + encodeURIComponent(JSON.stringify(filters))
      let resp = await this.$http.get(url)
      let visitors = resp.data.result.visitors
      if (visitors.length == 0) {
        return null
      } else {
        return visitors[0]
      }
    },

    async getVisitorFromInvite(passCode) {
      let url = '/v2/visitorLogging/getVisitorLogforPassCode'
      let resp = await this.$http.post(url, { passCode })

      if (resp.data.responseCode != 0) {
        throw { message: resp.data.message }
      }

      this.stateTransitionId = resp.data.result.transition_id
      let visitorLog = resp.data.result.visitorlogging
      this.visitorLogId = visitorLog.id
      let visitor = visitorLog.visitor
      //set required info so as to send in normal visitor flow request
      this.userData.phone = visitor.phone
      this.preFillData.visitorTypeId = visitorLog.visitorType.id
      this.visitorTypeId = visitorLog.visitorType.id

      //to do , need to check how to prefill custom fields also

      let hostId = getProperty(visitorLog, 'host.id')

      return {
        ...visitor,
        host: hostId,
        ...visitorLog.data,
        purposeOfVisit: visitorLog.purposeOfVisit,
      }
    },
    handleExit() {
      this.$router.replace({ name: 'welcome' })
    },

    stepsOver(stepperData) {
      if (!this.isTypeDone) {
        //
        //first time wizrd ends with type selection
        this.isTypeDone = true
        this.constructStepperForVisitorType(stepperData.visitorTypeId).then(
          () => {
            this.$refs['stepper'].nextStep({})
          }
        )
      } else {
        //actual data submit happens here , all steps are done
        //add data in format required by context obj
        this.loading = true
        this.addVisitorAndLog(stepperData)
          .then(checkinResp => {
            this.afterCheckin(checkinResp, stepperData)
          })
          .catch(e => {
            console.error('error checking in', e)
            this.$message({
              message: 'Eror occured during checkin ,please contact admin',
              type: 'error',
            })
            errorRedirect()
          })
      }
    },
    //After checkin redrirect to approval needed/blocked /badge /thanks page accordingly
    afterCheckin(checkinResp, stepperData) {
      let checkinLog = checkinResp.data.result.visitorloggingrecords[0]
      stepperData.visitorLogId = checkinLog.id
      stepperData.passCode = checkinLog.passCode

      if (checkinLog.approvalNeeded) {
        this.$router.replace({
          name: 'visitormessage',
          query: { messageType: 'approval' },
        })
      } else if (checkinLog.blocked) {
        this.$router.replace({
          name: 'visitormessage',
          query: { messageType: 'blocked' },
        })
      }
      //no need for approval and not blocked , proceed to print badge if badge enable for type ,else show success and redirect to welcome
      else {
        if (this.badgeEnabled) {
          this.$store.dispatch('devices/saveSharedData', stepperData)
          this.$router.replace({ name: 'visitorbadge' })
        } else {
          this.$router.replace({
            name: 'visitorthankyou',
            query: { messageType: 'checkIn' },
          })
        }
      }
    },
    async addVisitorAndLog(stepperData) {
      let visitorLogUrl = '/v2/visitorLogging/add'
      let formData = new FormData()

      let visit = stepperData.formFields.visit

      //fill formFields handled by other steps

      if (this.visitorStatus == 'preRegistered') {
        if (this.stateTransitionId) {
          visitorLogUrl = '/v2/visitorLogging/update'
          visit.id = this.visitorLogId
          formData.append('stateTransitionId', this.stateTransitionId)
        } else if (stepperData.preFill.host) {
          visit.host = {
            id: stepperData.preFill.host,
          }
        }
      }

      if (stepperData.idProof) {
        stepperData.idProof.forEach(file => {
          formData.append('attachedFiles', file)
        })
      }

      stepperData.formFields.visitor.phone = this.userData.phone
      stepperData.formFields.visit.avatar = stepperData.avatar
      stepperData.formFields.visit.avatar = stepperData.avatar
      //if Nda step present , send is signed flag as false
      if (stepperData.signature) {
        stepperData.formFields.visit.signature = stepperData.signature
        stepperData.formFields.visit.isNdaSigned = true
      }

      //construct context object of the form

      // visitorLoggingRecords[0]{
      //     visit:{

      //     },
      //     host:{
      //       id
      //     },
      //     avatar:file
      //     purposeOfVisit:'',
      //     data{
      //       ...customfields
      //     }
      // }

      visit.visitorType = {
        id: stepperData.visitorTypeId,
      }
      visit.visitor = stepperData.formFields.visitor

      //for returning user send visitor ID also
      if (stepperData.preFill) {
        visit.visitor.id = stepperData.preFill.id
      }

      let visitorLoggingRecords = []
      visitorLoggingRecords.push(visit)

      //convert to form data format required by struts
      this.$helpers.setFormData(
        'visitorLoggingRecords',
        visitorLoggingRecords,
        formData
      )

      let resp = await this.$http.post(visitorLogUrl, formData)

      return resp
    },
    processVisitorAndLogFormFields() {
      this.visitorFormFields = this.visitorForm.fields.filter(field => {
        return field.name == 'name' || field.name == 'email'
      })
      this.visitorLogFormFields = this.visitorTypeSettings.visitorLogForm.sections[1].fields
      //if host field exists take from 1st section and push inside the field list
      if (this.visitorTypeSettings.hostEnabled) {
        let hostField = this.visitorTypeSettings.visitorLogForm.sections[0].fields.find(
          field => field.name == 'host'
        )

        if (hostField) this.visitorLogFormFields.unshift(hostField)
      }
    },
  },
}
</script>

<style>
.kiosk-page-loading {
  width: 100%;
  height: 100vh;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
