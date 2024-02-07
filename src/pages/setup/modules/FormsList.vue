<template>
  <div>
    <portal to="module-summary-actions" slim>
      <el-select
        @change="loadForms"
        v-model="appId"
        placeholder="Select App"
        filterable
        class="fc-input-full-border2 width200px pR15"
      >
        <el-option
          v-for="app in apps"
          :key="app.linkName"
          :label="app.name"
          :value="app.id"
        >
        </el-option>
      </el-select>

      <el-button
        type="primary"
        class="setup-el-btn"
        @click="openFormCreation()"
      >
        ADD TEMPLATE
      </el-button>
    </portal>

    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table header-row">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">
                  {{ $t('common.roles.name') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('maintenance._workorder.description') }}
                </th>

                <th class="setting-table-th setting-th-text">
                  {{ $t('setup.setup.stateflow') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
                <th class="setting-table-th setting-th-text"></th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="isLoading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="isLoading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="forms.length === 0">
              <tr class="tablerow">
                <td
                  colspan="100%"
                  class="text-center"
                  style="padding-top: 18px; padding-bottom: 18px"
                >
                  No templates available
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <template v-for="(form, index) in forms">
                <tr
                  class="tablerow"
                  :key="index"
                  v-if="form.name !== 'default_asset_mobile'"
                >
                  <td
                    @click="editForm(form)"
                    class="text-capitalize"
                    style="width: 20%"
                  >
                    {{ form.displayName }}
                  </td>
                  <td style="width: 20%">
                    {{ form.description ? form.description : '---' }}
                  </td>

                  <td class="text-capitalize" style="width: 10%">
                    {{
                      form.stateFlowId > 0 && stateFlowsMap[form.stateFlowId]
                        ? stateFlowsMap[form.stateFlowId]
                        : '---'
                    }}
                  </td>
                  <td class="text-capitalize" style="width: 16%">
                    <img
                      :src="getIconSrc(form.showInMobile)"
                      class="mR10 pointer vertical-bottom"
                    />
                    <span class="fc-black-12 pointer">
                      {{ form.showInMobile ? 'Enabled' : 'Disabled' }}
                    </span>
                  </td>
                  <td class="text-capitalize" style="width: 7%">
                    <el-switch
                      v-if="
                        ['Standard'].includes(form.displayName) ||
                          ['multi_web_pm'].includes(form.name) ||
                          verifyStandardForm(form)
                      "
                      v-model="formStatus"
                      :disabled="true"
                    ></el-switch>
                    <el-switch
                      v-else
                      @change="updateStatus(form, 'web')"
                      v-model="form.showInWeb"
                    ></el-switch>
                  </td>
                  <td class="pR0" style="width: 20%">
                    <div
                      class="text-left actions text-center mL20"
                      style="margin-top: -3px"
                      v-if="
                        !(['multi_web_pm'].includes(form.name) || form.locked)
                      "
                    >
                      <i
                        class="el-icon-edit pointer"
                        @click="openTemplateCreation(form)"
                      ></i>
                      &nbsp;&nbsp;
                      <el-dropdown trigger="click">
                        <span class="el-dropdown-link">
                          <InlineSvg
                            src="svgs/menu"
                            iconClass="icon icon-xs mR10"
                          ></InlineSvg>
                        </span>
                        <el-dropdown-menu slot="dropdown">
                          <el-dropdown-item>
                            <div
                              v-if="canShowDelete(form)"
                              @click="showConfirmDelete(form)"
                            >
                              {{ $t('setup.delete.delete') }}
                            </div>
                          </el-dropdown-item>
                          <el-dropdown-item>
                            <div @click="updateStatus(form, 'mobile')">
                              {{ showInMobileText(form) }}
                            </div>
                          </el-dropdown-item>
                          <el-dropdown-item
                            v-if="form.id > 0 && !checkFormSharingEnabled"
                          >
                            <div @click="showTemplateSharingPermission(form)">
                              {{ $t('setup.setup.template_permissions') }}
                            </div>
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </el-dropdown>
                    </div>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <!--
      New form dialogue box
    -->
    <FormsNew
      v-if="showFormCreation"
      :showFormCreation.sync="showFormCreation"
      :stateFlowList="stateFlows"
      :error.sync="error"
      :errorMessage.sync="errorMessage"
      :formList="formObj"
      :isEdit="isEdit"
      :apps="apps"
      :saving="saving"
      :moduleName="moduleName"
      @saveRecord="saveRecord"
      @setFormValid="setFormValid"
    ></FormsNew>
    <TemplateSharingPermission
      v-if="showSharingRolePermission"
      :selectedForm="selectedForm"
      @close="showSharingRolePermission = false"
    ></TemplateSharingPermission>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import FormsNew from '@/form-builder/FormsNew'
import Constants from 'util/constant'
import { serializeProps } from '@facilio/utils/utility-methods'
import TemplateSharingPermission from '@/forms/TemplateSharingPermission.vue'

export default {
  props: ['moduleName'],
  components: { FormsNew, TemplateSharingPermission },

  data() {
    return {
      apps: [],
      appId: null,
      isLoading: true,
      forms: [],
      stateFlows: [],
      formStatus: true,
      stateFlowsMap: {},
      formObj: {},
      saving: false,
      isEdit: false,
      showFormCreation: false,
      errorMessage: null,
      error: false,
      selectedForm: null,
      showSharingRolePermission: false,
    }
  },
  computed: {
    checkFormSharingEnabled() {
      return this.$helpers.isLicenseEnabled('DISABLE_FORM_SHARING')
    },
  },
  async created() {
    let { query } = this.$route
    let { appId } = query || {}
    if (!isEmpty(appId) && appId > 0) {
      this.appId = parseInt(appId)
    }

    await this.availableApps()
    await this.loadForms()
    await this.getStateFlowList()
  },

  watch: {
    showFormCreation: {
      handler(newVal) {
        if (!newVal) {
          this.isEdit = false
          this.formObj = {}
        }
      },
    },
  },

  methods: {
    async availableApps() {
      let { moduleName } = this
      let { data, error } = await API.get('v2/application/fetchList', {
        moduleName,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.apps = data.application || []

        if (isEmpty(this.appId)) {
          this.appId = (
            this.apps.find(app => app.linkName === 'newapp') || this.apps[0]
          ).id
        }
      }
    },
    async loadForms() {
      let { moduleName, appId } = this
      let params = {
        moduleName,
        appId,
        fetchDisabledForms: true,
        skipTemplatePermission: true,
      }

      this.isLoading = true
      let { data, error } = await API.get('/v2/forms', params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.forms = data.forms || []
      }
      this.isLoading = false
    },
    verifyStandardForm({ name }) {
      if (name.indexOf('_default') >= 0 || name.indexOf('default_') >= 0) {
        return true
      } else {
        return false
      }
    },
    async getStateFlowList() {
      let { moduleName } = this
      let { error, data } = await API.post('/v2/stateflow/list', { moduleName })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { stateFlows } = data
        if (!isEmpty(stateFlows)) {
          stateFlows = stateFlows.filter(s => !s.draft)

          if (!isEmpty(stateFlows)) {
            this.stateFlowsMap = stateFlows.reduce((listObj, stateFlow) => {
              let { id, name } = stateFlow
              listObj[id] = name
              return listObj
            }, {})
            this.stateFlows = stateFlows
          }
        }
      }
    },
    openTemplateCreation(form) {
      if (form.stateFlowId < 0) {
        form.stateFlowId = null
      }

      this.formObj = form
      this.isEdit = true
      this.showFormCreation = true
    },
    getIconSrc(isEnabled) {
      let svgName = isEnabled ? 'mobile-blue.svg' : 'mobile-grey.svg'
      return require(`assets/${svgName}`)
    },
    showInMobileText(form) {
      let { showInMobile } = form
      return showInMobile ? 'Disable in Mobile' : 'Enable in Mobile'
    },
    canShowDelete({ name }) {
      let isDefault = name.startsWith('default_')
      return !isDefault
    },
    showConfirmDelete({ id }) {
      let dialogObj = {
        title: 'Delete Template',
        message: 'Are you sure you want to delete this Template?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      this.$dialog.confirm(dialogObj).then(async value => {
        if (value) {
          let { error } = await API.post('v2/forms/delete', { formId: id })

          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.forms = this.forms.filter(form => form.id !== id)
            this.$message.success('Successfully Deleted')
          }
        }
      })
    },
    showTemplateSharingPermission(form) {
      this.showSharingRolePermission = true
      this.selectedForm = form
    },
    async updateStatus(form, status) {
      if (status === 'web' && !form.showInWeb) {
        form.showInMobile = false
      } else {
        form.showInMobile = !form.showInMobile
      }

      let { moduleName } = this
      let { error } = await API.post('/v2/forms/update', { form, moduleName })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success('Updated Successfully')
      }
    },
    editForm(form) {
      if (form.locked) {
        return
      }
      let { moduleName, appId } = this
      let { id } = form
      if (id === -1) {
        /* Have to add the default forms to the DB, before editing them */
        let formData = serializeProps(form, Constants.FORM_RESOURCE_PROPS)
        let data = {
          form: formData,
        }
        data.form.appId = appId
        this.saveRecord(data)
      } else {
        let currentPath = this.$router.resolve({
          name: 'modules-details',
          params: { moduleName },
        }).href

        this.$router.push({
          path: `${currentPath}/layouts/${id}/edit`,
        })
      }
    },
    async saveRecord(data) {
      this.saving = true
      let { moduleName, appId } = this
      let { linkName } = this.apps.find(app => app.id === appId) || {}
      let url = this.isEdit ? '/v2/forms/update' : 'v2/forms/add'

      data.form.appId = appId
      data.appLinkName = linkName
      data.form.appLinkName = linkName
      data.moduleName = moduleName
      if (!isEmpty(data.form) && isEmpty(data.form.stateFlowId)) {
        data.form.stateFlowId = -99
      }

      let { error, data: dataObj } = await API.post(url, data)

      if (error) {
        if (this.isEdit) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.error = true
          this.errorMessage = error.message
        }
      } else {
        if (this.isEdit) {
          this.showFormCreation = false
          this.isEdit = false
          this.formObj = {}
          this.$message.success('Form updated successfully')
        } else {
          let {
            form: { id },
          } = dataObj || {}
          let currentPath = this.$router.resolve({
            name: 'modules-details',
            params: { moduleName },
          }).href

          this.$router.push({
            path: `${currentPath}/layouts/${id}/edit`,
          })
        }
      }
      this.saving = false
    },
    setFormValid() {
      this.error = false
      this.errorMessage = ''
    },
    openFormCreation() {
      this.$set(this.formObj, 'appId', this.appId)
      this.showFormCreation = true
    },
  },
}
</script>
<style lang="scss" scoped>
.setting-list-view-table th {
  background-color: #f3f1fc;
}
</style>
