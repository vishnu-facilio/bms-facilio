<template>
  <div>
    <el-dialog
      :title="$t('commissioning.creation.new_commissioning')"
      :visible.sync="canShowDialog"
      custom-class="commissioning-form-dialog"
      width="30%"
      class="fc-dialog-center-container"
      :append-to-body="true"
    >
      <ErrorBanner
        :error.sync="error"
        :errorMessage.sync="errorMessage"
      ></ErrorBanner>
      <div v-if="isLoading" class="mT40">
        <Spinner :show="isLoading"></Spinner>
      </div>
      <div v-else>
        <div class="height350">
          <el-form
            ref="commissioningNew"
            :rules="rules"
            :model="commissioningObj"
            label-position="top"
          >
            <el-form-item
              :label="$t('commissioning.creation.agent')"
              prop="agentId"
              class="mB10"
            >
              <SelectAgent
                @onAgentFilter="onAgentChange"
                class="select-agent-width"
                hideLabel="true"
              ></SelectAgent>
            </el-form-item>
            <el-form-item
              :label="$t('commissioning.creation.protocol')"
              prop="selectedType"
              class="mB10"
              :required="true"
            >
              <el-select
                v-model="commissioningObj.selectedType"
                class="fc-input-full-border-select2 width100"
                :placeholder="$t('commissioning.sheet.select_controllerType')"
                :popper-append-to-body="false"
                filterable
                @change="onTypeChange"
              >
                <template v-for="(label, value) in $constants.ControllerTypes">
                  <el-option
                    v-if="availableTypes.includes(parseInt(value))"
                    :key="value"
                    :label="label"
                    :value="parseInt(value)"
                  ></el-option>
                </template>
              </el-select>
            </el-form-item>
            <el-form-item
              v-if="!$validation.isEmpty(commissioningObj.selectedType)"
              :label="$t('commissioning.creation.controller')"
              prop="controllerIds"
              class="mB10"
              :required="true"
            >
              <FLookupFieldWrapper
                v-model="commissioningObj.controllerIds"
                :key="moduleName"
                :field="{
                  lookupModule: {
                    name: moduleName,
                  },
                  multiple: true,
                }"
                :filterConstruction="constructFilter"
                :hideLookupIcon="false"
                :disabled="false"
              ></FLookupFieldWrapper>
            </el-form-item>
            <el-form-item>
              <el-checkbox
                v-model="prefillMlData"
                v-if="this.$helpers.isLicenseEnabled('ML_POINTS_SUGGESTIONS')"
                >{{
                  $t('commissioning.commissioning.prefill_ml_data')
                }}</el-checkbox
              >
            </el-form-item>
          </el-form>
        </div>
        <div class="modal-dialog-footer">
          <el-button
            @click="closeCommissioningCreation"
            class="modal-btn-cancel"
            >Cancel</el-button
          >
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="addNewCommissioning()"
            :loading="isSaving"
            >Confirm</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import ErrorBanner from '@/ErrorBanner'
import { isEmpty } from '@facilio/utils/validation'
import Constants from 'util/constant'
import { constructFieldOptions } from '@facilio/utils/utility-methods'
import { findRouteForTab, tabTypes, getApp } from '@facilio/router'
import Spinner from '@/Spinner'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import SelectAgent from 'src/agent/components/SelectAgent.vue'
import { API } from '@facilio/api'

export default {
  components: {
    ErrorBanner,
    Spinner,
    FLookupFieldWrapper,
    SelectAgent,
  },
  props: {
    canShowCommissioningCreation: {
      type: Boolean,
      required: true,
    },
  },
  computed: {
    canShowDialog: {
      get() {
        return this.canShowCommissioningCreation
      },
      set(value) {
        this.$emit('update:canShowCommissioningCreation', value)
      },
    },
    availableTypes() {
      let { controllerTypes } = this || {}
      if (!isEmpty(controllerTypes)) {
        return controllerTypes.map(controller => controller.controllerType)
      } else {
        return []
      }
    },
  },
  data() {
    return {
      prefillMlData: null,
      controllerTypes: [],
      error: false,
      errorMessage: null,
      isLoading: false,
      commissioningObj: {
        agentId: null,
        controllerIds: [],
      },
      rules: {
        controllerIds: [
          {
            required: true,
            message: 'Please select controller',
            trigger: 'change',
          },
        ],
        selectedType: [
          {
            required: true,
            message: 'Please select a type',
            trigger: 'change',
          },
        ],
      },
      isSaving: false,
      isControllersLoading: false,
      controllersList: [],
      moduleName: null,
      agentFilter: null,
    }
  },
  methods: {
    resetValues() {
      this.$set(this, 'controllersList', [])
      this.$set(this.commissioningObj, 'controllerIds', [])
      this.$set(this.commissioningObj, 'selectedType', null)
    },
    onTypeChange() {
      this.$set(this.commissioningObj, 'controllerIds', [])
      this.fetchControllersList()
    },
    async loadControllerTypes() {
      let { commissioningObj } = this
      let { agentId } = commissioningObj || {}
      this.$http
        .get(`/v2/controller/filterControllerTypes?agentId=${agentId}`)
        .then(response => {
          let controllerTypes = this.$getProperty(
            response,
            'data.result.data',
            []
          )
          this.controllerTypes =
            controllerTypes.filter(c => c.controllerType > -1) || []
        })
        .catch(() => {
          this.setLoading(false)
          this.controllerTypes = []
        })
    },
    async fetchControllersList(props = {}) {
      let { querySearch } = props
      let { commissioningObj } = this
      let { agentId, selectedType } = commissioningObj || {}
      this.moduleName = Constants.ControllerModuleName[selectedType]
      this.agentFilter = agentId
      this.$set(this, 'isControllersLoading', true)
      if (!isEmpty(selectedType) && !isEmpty(agentId)) {
        let url = `v2/controller/controllers?agentId=${agentId}&controllerType=${selectedType}`
        if (!isEmpty(querySearch)) {
          url = `${url}&querySearch=${querySearch}`
        } else {
          url = `${url}&page=1&perPage=100`
        }
        let {
          data: { result, responseCode, message, exception },
        } = await this.$http.get(url)
        if (responseCode === 200) {
          let { data } = result
          this.$set(this, 'controllersList', data)
        } else {
          let errMsg = isEmpty(message) ? exception : message
          this.$message.error(errMsg || 'Error occured')
          this.$set(this, 'controllersList', [])
        }
      }
      this.$set(this, 'isControllersLoading', false)
    },
    addNewCommissioning() {
      this.setFormValid()
      this.$refs['commissioningNew'].validate(async valid => {
        if (valid) {
          let { commissioningObj, prefillMlData } = this
          let { agentId, controllerIds, selectedType } = commissioningObj
          let controllers = controllerIds.map(id => {
            return { id: id }
          })
          let url = 'v2/commissioning/add'
          let params = {
            log: {
              agent: { id: agentId },
              controllerType: selectedType,
              controllerIds,
              prefillMlData,
              controllers,
            },
          }
          this.isSaving = true
          let { data, error } = await API.post(url, params)
          if (!isEmpty(error)) {
            let { message } = error || {}
            this.$message.error(message)
          } else {
            let { log } = data || {}
            let { id } = log || {}
            this.redirectToCommisisioning(id)
          }
          this.isSaving = false
        }
      })
    },
    setFormValid() {
      this.error = false
      this.errorMessage = ''
    },
    setFormInvalid(msg) {
      this.error = true
      this.errorMessage = msg
    },
    closeCommissioningCreation() {
      this.canShowDialog = false
    },
    redirectToCommisisioning(logId) {
      let { linkName: appName } = getApp()
      let { path } = findRouteForTab(tabTypes.CUSTOM, {
        config: {
          type: 'commissioning',
        },
      })
      path &&
        this.$router.push({
          path: `/${appName}/${path}/${logId}/edit`,
        })
      this.closeCommissioningCreation()
    },
    constructFilter() {
      let { commissioningObj } = this
      let { agentId } = commissioningObj
      return { agentId: { operatorId: 9, value: [`${this.agentFilter}`] } }
    },
    onAgentChange(selectAgent) {
      this.resetValues()
      this.$set(this.commissioningObj, 'agentId', selectAgent.id)
      this.loadControllerTypes()
    },
  },
}
</script>
<style lang="scss">
.commissioning-form-dialog {
  .el-dialog__body {
    height: 400px;
    overflow: scroll;
    padding: 0px 30px 70px;
  }
}
.select-agent-width {
  .fc-input-full-border-select2 {
    width: 100% !important;
  }
}
</style>
