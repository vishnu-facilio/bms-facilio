<template>
  <div>
    <portal to="module-summary-actions" slim>
      <el-select
        @change="loadAppData"
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
    </portal>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">
                  {{ $t('common._common.type') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('common._common.form') }}
                </th>

                <th class="setting-table-th setting-th-text">
                  {{ $t('common._common.status') }}
                </th>
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
            <tbody v-else-if="visitorType.length === 0">
              <tr class="tablerow">
                <td
                  colspan="100%"
                  class="text-center"
                  style="padding-top: 18px; padding-bottom: 18px"
                >
                  {{ $t('common.header.no_templates_available') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <template v-for="(vType, index) in visitorType">
                <tr class="tablerow" :key="index" v-if="vType">
                  <td
                    @click="editForm(vType)"
                    class="text-capitalize"
                    style="width: 30%"
                  >
                    {{ vType.name }}
                  </td>
                  <td
                    @click="editForm(vType)"
                    class="text-capitalize"
                    style="width: 30%"
                    v-if="vType.form"
                  >
                    {{ vType.form.displayName }}
                  </td>
                  <td v-else>
                    {{ '---' }}
                  </td>
                  <td class="text-capitalize">
                    <el-switch
                      @change="updateStatus(vType)"
                      v-model="vType.formEnabled"
                      :disabled="vType.visitorFormId < 0"
                    ></el-switch>
                  </td>
                  <td>
                    <div
                      class="text-left actions text-center mL20"
                      style="margin-top: -3px"
                    >
                      <i
                        class="el-icon-edit pointer"
                        @click="openTypeFormCreation(vType)"
                      ></i>
                    </div>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <VisitorTypeFormsEditForm
      v-if="showFormCreation"
      :showFormCreation.sync="showFormCreation"
      :error.sync="error"
      :errorMessage.sync="errorMessage"
      :isEdit="isEdit"
      :appId="appId"
      :formList="forms"
      :moduleName="moduleName"
      :visitorType="selectedType"
      @saveRecord="updateStatus"
    >
    </VisitorTypeFormsEditForm>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import VisitorTypeFormsEditForm from 'src/pages/setup/VisitorTypeFormsEditForm'
export default {
  props: ['moduleName', 'visitorTypeId'],
  components: { VisitorTypeFormsEditForm },
  data() {
    return {
      apps: [],
      appId: null,
      isLoading: true,
      forms: [],
      isEdit: false,
      showFormCreation: false,
      errorMessage: null,
      error: false,
      visitorType: [],
      selectedType: null,
    }
  },
  async created() {
    await this.availableApps()
    await this.loadVisitorType()
    await this.loadForms()
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
    async loadVisitorType() {
      let { appId, moduleName } = this
      this.isLoading = true
      let url = 'v3/modules/data/list'
      let param = {
        moduleName: 'visitorType',
        appId,
        parentModuleName: moduleName,
        showAll: true,
      }
      let { data, error } = await API.get(url, param)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { visitorType } = data
        this.visitorType = visitorType || []
      }
      this.isLoading = false
    },
    openTypeFormCreation(visitorType) {
      this.selectedType = visitorType
      this.showFormCreation = true
    },
    loadAppData() {
      this.loadVisitorType()
      this.loadForms()
    },
    async updateStatus(vType) {
      let url = 'v3/visitorTypeForm/update'
      let param = {
        parentModuleName: this.moduleName,
        appId: this.appId,
        visitorType: vType,
      }
      let { error } = await API.post(url, param)
      if (error) {
        this.$message.error(
          error.message || 'Error occurred while updating Visitor Type Form'
        )
      } else {
        this.showFormCreation = false
        this.loadVisitorType()
        this.$message.success('Updated successfully')
      }
    },
  },
}
</script>
