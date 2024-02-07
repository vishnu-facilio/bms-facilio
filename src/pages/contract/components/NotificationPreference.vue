<template>
  <div>
    <div v-if="loading" class="mT50 fc-webform-loading">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else class="new-body-modal">
      <div v-if="!preferenceList || preferenceList.length === 0">
        <el-row>
          <el-col :span="24">
            <div class="attendance-transaction-no-data">
              <img src="~statics/noData-light.png" width="100" height="100" />
              <div class="mT10 label-txt-black f14">
                No Notification Preferences Available
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <div
        v-for="(preference, index) in preferenceList"
        class="pB20"
        :key="index"
      >
        <el-row :span="24">
          <el-col :span="24" class="preference-col">
            <el-checkbox
              v-model="preference.isEnabled"
              @change="preferenceCheckboxChangeActions(preference)"
              >{{ preference.displayName }}</el-checkbox
            >
            <span class="pL20">
              <el-button
                v-if="preference.isEnabled"
                type="button"
                class="small-border-btn-save"
                @click="
                  preference && preference.id > 0
                    ? preference.showForm
                      ? addPreference(preference)
                      : toggleShowForm(preference)
                    : addPreference(preference)
                "
                >{{
                  preference && preference.id > 0
                    ? preference.showForm
                      ? 'Save'
                      : 'Edit'
                    : 'Save'
                }}</el-button
              >
            </span>
            <span class="pL20">
              <el-button
                v-if="preference.isEnabled && preference.showForm"
                type="button"
                class="small-border-btn"
                @click="
                  preference && preference.id > 0
                    ? toggleShowForm(preference)
                    : ((preference.isEnabled = false),
                      toggleShowForm(preference))
                "
                >Close</el-button
              >
            </span>
          </el-col>
        </el-row>
        <el-row class="pT30 pL50" v-if="preference.showForm">
          <facilio-web-form
            :editObj="preference.editData"
            :formFields="preference.form"
            :emitForm="preference.emitForm"
            @failed="
              ;(preference.saving = false), (preference.emitForm = false)
            "
            @validated="data => submitForm(data, preference)"
            :reset.sync="preference.resetForm"
            class="facilio-notification-preference-web-form-body"
          ></facilio-web-form>
        </el-row>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel width100" @click="closeDialog()"
        >CANCEL</el-button
      >
    </div>
  </div>
</template>
<script>
import FacilioWebForm from '@/FacilioWebForm'
import moment from 'moment-timezone'
export default {
  props: ['moduleName', 'recordId', 'visibility'],
  components: {
    FacilioWebForm,
  },
  data() {
    return {
      loading: false,
      saveLoading: false,
      preferenceList: null,
      enabledPreferenceList: null,
    }
  },
  mounted() {
    this.loadAllPreferenceData()
  },
  computed: {},
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    submitForm(data, preference) {
      let tempData = this.$helpers.cloneObject(data)
      // Special Handlings For Data Formatting is done here
      if (this.moduleName.indexOf('contract') > 0) {
        tempData.time = this.convertSecondsToTimeHHMM(tempData.time)
        tempData['days'] = parseInt(tempData.days)
      }
      let param = {
        moduleName: this.moduleName,
        name: preference.name,
        value: tempData,
      }
      if (preference.id > 0) {
        param['preferenceId'] = preference.id
      }
      if (this.recordId > 0) {
        param['recordId'] = this.recordId
      }
      preference.saving = true
      this.$http
        .post('v2/preference/enable', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success('Preference Configured Successfully')
            let resultPref = response.data.result.preferences
            let tempSetObj = this.specialDataFormating(resultPref)
            this.$set(preference, 'editData', tempSetObj)
            this.$set(preference, 'id', resultPref.id)
            preference.isEnabled = true
            preference.showForm = false
          } else {
            this.$message.error(response.data.message)
          }
          preference.saving = false
          preference.emitForm = false
        })
        .catch(error => {
          this.$message.error(error)
          preference.saving = false
          preference.emitForm = false
        })
    },
    specialDataFormating(resultPref) {
      let tempSetObj = JSON.parse(resultPref.formData)
      if (this.moduleName.indexOf('contracts') > 0) {
        tempSetObj.time = moment(tempSetObj.time, 'HH:mm').valueOf()
      }
      return tempSetObj
    },
    loadAllPreferenceData() {
      this.loading = true
      let promises = []
      let allPrefParam = {}
      allPrefParam['moduleName'] = this.moduleName
      if (this.recordId > 0) {
        allPrefParam['isModuleSpecific'] = false
      }
      let allPrefList = this.$http.post(
        '/v2/preference/getAllPrefslist',
        allPrefParam
      )
      let prefParam = {}
      prefParam['moduleName'] = this.moduleName
      if (this.recordId > 0) {
        prefParam['recordId'] = this.recordId
      }
      let enabledPrefList = this.$http.post(
        '/v2/preference/getEnabledPrefsList',
        prefParam
      )
      promises.push(allPrefList)
      promises.push(enabledPrefList)
      Promise.all(promises)
        .then(response => {
          if (response[0].data.responseCode === 0) {
            this.preferenceList = response[0].data.result.preferenceList
            if (this.preferenceList) {
              this.preferenceList.forEach(i => {
                this.$set(i, 'isEnabled', false)
                this.$set(i, 'showForm', false)
                this.$set(i, 'emitForm', false)
                this.$set(i, 'resetForm', false)
                this.$set(i, 'saving', false)
              })
            }
          } else {
            this.$message.error(response[0].data.message)
          }
          if (response[1].data.responseCode === 0) {
            this.enabledPreferenceList = response[1].data.result.preferenceList
            if (this.enabledPreferenceList && this.preferenceList) {
              this.enabledPreferenceList.forEach(i => {
                this.preferenceList.forEach(j => {
                  if (j.name === i.preferenceName) {
                    let temp = this.specialDataFormating(i)
                    this.$set(j, 'editData', temp)
                    this.$set(j, 'id', i.id)
                    j.isEnabled = true
                  }
                })
              })
            }
          } else {
            this.$message.error(response[1].data.message)
          }
          this.loading = false
        })
        .catch(error => {
          this.$message.error(error)
        })
    },
    addPreference(preference) {
      preference.emitForm = true
    },
    convertSecondsToTimeHHMM(val) {
      return moment(val).format('HH:mm')
    },
    preferenceCheckboxChangeActions(preference) {
      if (!preference.isEnabled && preference.id > 0) {
        this.$http
          .post('/v2/preference/disable', { preferenceId: preference.id })
          .then(response => {
            if (response.data.responseCode === 0) {
              this.$message.success('Preference Disabled Successfully')
              this.loadAllPreferenceData()
            } else {
              this.$message.error(response.data.message)
            }
          })
          .catch(error => {
            this.$message.error(error)
          })
      } else if (!preference.isEnabled) {
        preference.showForm = false
      } else if (preference.isEnabled) {
        preference.showForm = true
      }
    },
    toggleShowForm(preference) {
      preference.showForm = !preference.showForm
    },
  },
}
</script>
<style scoped>
.preference-col .el-checkbox {
  margin-right: 10px;
}
</style>
