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
      <error-banner
        :error.sync="error"
        :errorMessage="errorMessage"
      ></error-banner>
      <el-form
        ref="new-stateflow-form"
        :model="stateFlowObj"
        :rules="rules"
        :label-position="'top'"
      >
        <div class="new-header-container mR30 pL30">
          <div class="new-header-modal">
            <div class="new-header-text">
              <div class="setup-modal-title">
                {{
                  isNew
                    ? $t('setup.stateflow.header_new')
                    : $t('setup.stateflow.header_edit')
                }}
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
                  :placeholder="$t('setup.stateflow.name_placeholder')"
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
                  placeholder="Select the default state"
                  class="fc-input-full-border-select2 width100"
                  filterable
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
                  :placeholder="$t('setup.stateflow.desc_placeholder')"
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
import ErrorBanner from '@/ErrorBanner'
import NewCriteriaBuilder from '@/NewCriteriaBuilder'

export default {
  props: ['isNew', 'stateflow', 'closeDialog', 'module'],
  components: { ErrorBanner, NewCriteriaBuilder },

  created() {
    this.$store.dispatch('loadTicketStatus', this.module)
    this.stateFlowObj = { ...this.stateFlowObj, ...this.stateflow }
    this.deserialize()
  },

  computed: {
    ticketStatus() {
      return this.$store.state.ticketStatus[this.module]
    },
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
      rules: {
        name: [
          {
            required: true,
            message: 'Please enter a name for this stateflow',
            trigger: 'blur',
          },
        ],
        defaultStateId: [
          {
            required: true,
            message: 'Please select a default state',
            trigger: 'blur',
          },
        ],
        criteria: [
          {
            validator: (rule, value, callback) =>
              this.validate('criteria', value, callback),
          },
        ],
      },
    }
  },
  methods: {
    serializeCriteria(criteria) {
      Object.keys(criteria.conditions).forEach(condition => {
        if (parseInt(condition) === 1) {
          if (!criteria.conditions[1].fieldName) {
            criteria = null
          }
        } else if (
          !criteria.conditions[condition].hasOwnProperty('fieldName')
        ) {
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

    serialize(data) {
      if (data.stateFlow.criteria) {
        this.serializeCriteria(data.stateFlow.criteria)
      }
    },

    deserialize() {},

    validate(field, value, callback) {
      if (field === 'criteria') {
        // if (
        //   !this.stateFlowObj.defaltStateFlow &&
        //   !this.serializeCriteria(this.stateFlowObj.criteria)
        // ) {
        //   callback(new Error('Please configure a criteria for this stateflow'))
        // } else {
        //   callback()
        // }
        callback()
      } else {
        callback()
      }
    },

    save() {
      let { module: moduleName } = this
      let data = {}
      data['stateFlow'] = { moduleName, ...this.stateFlowObj }

      this.serialize(data)

      let form = this.$refs['new-stateflow-form']

      if (!form) return

      form.validate().then(isValid => {
        if (!isValid) return

        this.saving = true
        this.$http
          .post('/v2/stateflow/addOrUpdate', data)
          .then(({ data }) => {
            if (data.responseCode === 0) {
              this.$message({
                showClose: true,
                message:
                  data.result.message ||
                  this.$t('setup.stateflow.creation_success'),
                type: 'success',
              })
              this.closeDialog(true)
            } else {
              throw new Error(data.message)
            }
          })
          .catch(error => {
            this.$message({
              showClose: true,
              message:
                error.message || this.$t('setup.stateflow.creation_failure'),
              type: 'error',
            })
          })
          .finally(() => (this.saving = false))
      })
    },
  },
}
</script>
