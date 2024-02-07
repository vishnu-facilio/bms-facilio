<template>
  <div>
    <el-dialog
      :visible="true"
      :fullscreen="true"
      :append-to-body="true"
      :before-close="() => closeDialog()"
      custom-class="fc-dialog-form fc-dialog-right custom-rule-dialog setup-dialog40 setup-dialog"
      style="z-index: 1999"
    >
      <el-form
        ref="new-stateflow-form"
        :model="stateFlowObj"
        :label-position="'top'"
      >
        <div class="new-header-container mR30 pL30">
          <div class="new-header-modal">
            <div class="new-header-text">
              <div class="setup-modal-title">
                {{ $t('setup.stateflow.header_clone') }}
              </div>
            </div>
          </div>
        </div>

        <div class="new-body-modal new-stateflow-modal">
          <el-row>
            <el-col :span="12">
              <el-form-item prop="name">
                <p class="fc-input-label-txt">
                  {{ $t('setup.stateflow.name') }}
                </p>
                <el-input
                  :autofocus="true"
                  v-model="stateFlowObj.name"
                  class="width100 pR20"
                  disabled
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="defaultStateId">
                <p class="fc-input-label-txt">
                  {{ $t('setup.stateflow.default_state') }}
                </p>
                <el-select
                  v-model="stateFlowObj.defaultStateId"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                  <el-option
                    v-for="item in ticketStatus"
                    :key="item.id"
                    :label="item.displayName"
                    :value="item.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item prop="description">
                <p class="fc-input-label-txt">
                  {{ $t('setup.approvalprocess.description') }}
                </p>
                <el-input
                  v-model="stateFlowObj.description"
                  :min-rows="1"
                  type="textarea"
                  :autosize="{ minRows: 2, maxRows: 4 }"
                  class="fc-input-full-border-select2 width100"
                  disabled
                  resize="none"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <template v-if="!stateFlowObj.defaltStateFlow">
            <hr class="separator-line" />
            <p
              class="fc-input-label-txt bold mL10 pB0 text-fc-pink text-uppercase"
            >
              {{ $t('setup.users_management.criteria') }}
            </p>
            <p class="mL10 fc-input-label-txt">
              {{ $t('setup.stateflow.criteria_desc') }}
            </p>

            <el-form-item prop="criteria">
              <new-criteria-builder
                ref="criteriaBuilder"
                class="stateflow-criteria"
                v-model="stateFlowObj.criteria"
                :exrule="stateFlowObj.criteria"
                @condition="newValue => (this.stateFlowObj.criteria = newValue)"
                :module="module"
                :isRendering.sync="criteriaRendered"
                :hideTitleSection="true"
              ></new-criteria-builder>
            </el-form-item>
          </template>
        </div>

        <div class="modal-dialog-footer">
          <el-button
            @click="closeDialog()"
            class="modal-btn-cancel text-uppercase"
            >{{ $t('common._common.cancel') }}</el-button
          >
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="save"
            :loading="saving"
            >{{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}</el-button
          >
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  props: ['referenceStateflowId', 'stateflow', 'closeDialog', 'module'],
  components: { NewCriteriaBuilder },
  created() {
    this.$store.dispatch('loadTicketStatus', this.module)
    this.stateFlowObj = { ...this.stateFlowObj, ...this.stateflow }
  },
  data() {
    return {
      saving: false,
      error: false,
      errorMessage: '',
      criteriaRendered: false,
      stateFlowObj: {
        name: null,
        defaultStateId: null,
        description: null,
        criteria: null,
      },
    }
  },
  computed: {
    ticketStatus() {
      return this.$store.state.ticketStatus[this.module]
    },
  },
  methods: {
    serializeCriteria(criteria) {
      Object.keys(criteria.conditions).forEach(condition => {
        if (parseInt(condition) === 1) {
          if (!criteria.conditions[1].fieldName) {
            criteria = null
          }
        } else if (isEmpty(criteria.conditions[condition]['fieldName'])) {
          delete criteria.conditions[condition]
        } else {
          let discardKeys = ['valueArray', 'operatorsDataType', 'operatorLabel']

          discardKeys.forEach(key => {
            delete criteria.conditions[condition][key]
          })
        }
      })
      if (
        criteria &&
        criteria.conditions &&
        Object.keys(criteria.conditions).length === 0
      ) {
        return null
      } else {
        return criteria
      }
    },
    async save() {
      let { stateFlowObj, referenceStateflowId } = this
      let criteria = this.serializeCriteria(stateFlowObj.criteria)
      let params = {
        stateFlowId: referenceStateflowId,
        criteria,
      }
      this.saving = true

      let { error } = await API.post('v2/stateflow/clone', params)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.$message.success(this.$t('setup.stateflow.creation_success'))
        this.closeDialog(true)
      }

      this.saving = false
    },
  },
}
</script>
