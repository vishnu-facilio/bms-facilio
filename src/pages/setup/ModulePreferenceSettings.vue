<template>
  <div class="visitor-setting-con-width mT20">
    <div v-if="loading" class="mT50 fc-webform-loading">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else>
      <div v-if="!preferenceList || preferenceList.length === 0">
        <el-row>
          <el-col :span="24">
            <div class="attendance-transaction-no-data">
              <img src="~statics/noData-light.png" width="100" height="100" />
              <div class="mT10 label-txt-black f14">
                No Preferences Available
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <div
        v-for="(preference, index) in preferenceList"
        class="visitor-hor-card scale-up-left"
        :key="index"
      >
        <el-row :span="24" class="flex-middle visibility-visible-actions">
          <el-col :span="20">
            <div class="fc-black-15 fwBold">{{ preference.displayName }}</div>
            <div class="fc-grey4-13 pT5">{{ preference.description }}</div>
          </el-col>
          <el-col :span="3" class="text-right">
            <div class="flex-middle">
              <div class>
                <el-switch
                  v-model="preference.isEnabled"
                  @change="preferenceCheckboxChangeActions(preference)"
                ></el-switch>
                <i
                  v-if="preference.isEnabled && !showSettingsDialog"
                  class="el-icon-edit pointer edit-icon-color mL20 visibility-hide-actions"
                  data-arrow="true"
                  title="Edit"
                  @click="preferenceCheckboxChangeActions(preference)"
                  v-tippy
                ></i>
              </div>
            </div>
          </el-col>
        </el-row>
        <el-dialog
          :visible="showSettingsDialog"
          :append-to-body="true"
          :width="'350px'"
          :key="index"
          @close="showSettingsDialog = false"
          :title="preference.displayName"
          class="fc-dialog-center-container"
        >
          <div class="max-height500 pB50 overflow-y-scroll">
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
          </div>
          <div class="modal-dialog-footer">
            <el-button
              type="button"
              class="modal-btn-cancel"
              @click="toggleShowForm()"
              >CLOSE</el-button
            >
            <el-button
              type="button"
              class="modal-btn-save"
              @click="addPreference(preference)"
              >SAVE</el-button
            >
          </div>
        </el-dialog>
      </div>
    </div>
  </div>
</template>
<script>
import FacilioWebForm from '@/FacilioWebForm'
import moment from 'moment-timezone'
export default {
  props: ['moduleName'],
  components: {
    FacilioWebForm,
  },
  data() {
    return {
      loading: false,
      saveLoading: false,
      preferenceList: null,
      enabledPreferenceList: null,
      showSettingsDialog: false,
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
            this.showSettingsDialog = false
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
      allPrefParam['fetchModuleSpecific'] = true
      let allPrefList = this.$http.post(
        '/v2/preference/getAllPrefslist',
        allPrefParam
      )
      let prefParam = {}
      prefParam['moduleName'] = this.moduleName
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
      if (preference.isEnabled) {
        this.showSettingsDialog = true
      }
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
    toggleShowForm() {
      this.showSettingsDialog = false
    },
  },
}
</script>
<style scoped>
.preference-col .el-checkbox {
  margin-right: 10px;
}
.preference_form_dialog .el-dialog__body {
  height: 100%;
  max-height: 400px;
  min-height: 100px;
  overflow-y: scroll;
  padding-bottom: 50px;
  padding-top: 0;
}
</style>
