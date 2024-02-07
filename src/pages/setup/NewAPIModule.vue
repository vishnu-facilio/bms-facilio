<template>
  <div>
    <el-dialog
      :title="$t('common._common.module')"
      width="30%"
      class="fc-dialog-center-container"
      :visible="true"
      :append-to-body="true"
      :before-close="closeDialog"
    >
      <div class="height200">
        <el-form ref="tabTypeForm" :model="webtabObj">
          <el-form-item :label="$t('common._common.module')" prop="module">
            <el-select
              v-model="webtabObj.moduleName"
              :placeholder="$t('common.header.please_select_the_module')"
              filterable
              class="fc-input-full-border-select2 width100"
            >
              <el-option-group
                v-for="(moduleObj, moduleName) in allModules"
                :key="moduleName"
                :label="moduleName.toUpperCase()"
              >
                <el-option
                  v-for="list in moduleObj"
                  :key="list.moduleId"
                  :label="list.displayName"
                  :value="list.name"
                >
                </el-option>
              </el-option-group>
            </el-select>
          </el-form-item>
        </el-form>

        <div class="modal-dialog-footer">
          <el-button @click="closeDialog" class="modal-btn-cancel">
            {{ $t('setup.users_management.cancel') }}
          </el-button>
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="saveModule()"
            :loading="saving"
          >
            {{ $t('panel.dashboard.confirm') }}
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
export default {
  props: ['isNew', 'appId'],
  data() {
    return {
      canShowModule: false,
      allModules: [],
      webtabObj: {
        moduleName: null,
      },
      saving: false,
    }
  },
  async created() {
    await this.getAllModules()
  },
  methods: {
    async getAllModules() {
      let { error, data } = await API.get('/v3/modules/list/all')

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let modulesList = Object.assign(data, {
          'System Modules': data['systemModules'],
          'Custom Modules': data['customModules'],
        })
        delete modulesList['systemModules']
        delete modulesList['customModules']

        this.allModules = modulesList || {}
      }
    },
    async saveModule() {
      let modules = [
        ...this.allModules['System Modules'],
        ...this.allModules['Custom Modules'],
      ]

      let selectedModules = modules.filter(
        m => this.webtabObj.moduleName === m.name
      )
      let moduleIds = selectedModules
        .filter(m => !isEmpty(m.moduleId))
        .map(m => m.moduleId)

      let specialTypeModules = selectedModules
        .filter(m => isEmpty(m.moduleId))
        .map(m => m.name)

      let params = {
        tab: {
          name: selectedModules[0].displayName,
          route: this.webtabObj.moduleName.replace(/\W+/g, '-').toLowerCase(),
          applicationId: this.appId,
          selectedModules,
          moduleIds,
          type: 1,
        },
      }

      this.saving = true
      let { error, data } = await API.post('/v2/tab/addOrUpdate', params)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(this.$t('common._common.new_module_added'))
        this.saving = false
        this.closeDialog()
      }
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
