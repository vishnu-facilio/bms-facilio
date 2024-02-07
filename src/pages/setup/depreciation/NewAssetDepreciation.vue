<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div class="depreciation-form">
      <el-form
        :model="depreciation"
        :rules="rules"
        :label-position="'top'"
        ref="depreciationForm"
        class="fc-form"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{
                id
                  ? $t('common.header.edit_depreciation_schedule')
                  : $t('common.products.new_depreciation_schedule')
              }}
            </div>
          </div>
        </div>

        <div class="new-body-modal enpi-body-modal">
          <div class="body-scroll">
            <div v-if="loading">
              <div v-for="index in [1, 2]" :key="index">
                <el-row class="mB10">
                  <el-col :span="24">
                    <span class="lines loading-shimmer width50 mB10"></span>

                    <span
                      class="lines loading-shimmer width100 mB10 height40"
                    ></span>
                  </el-col>
                </el-row>
              </div>

              <div v-for="index in [3, 4]" :key="index">
                <el-row class="mB10 d-flex">
                  <el-col :span="12" class="mR10">
                    <span class="lines loading-shimmer width50"></span>

                    <span
                      class="lines loading-shimmer width100 mT13 mB10 height40"
                    ></span>
                  </el-col>

                  <el-col v-if="index !== 4" :span="12" class="mL10">
                    <span class="lines loading-shimmer width50"></span>

                    <span
                      class="lines loading-shimmer width100 mT13 mB10 height40"
                    ></span>
                  </el-col>
                </el-row>
              </div>
            </div>

            <div v-else>
              <el-row>
                <el-col :span="24">
                  <el-form-item prop="name">
                    <p class="fc-input-label-txt">
                      {{ $t('common.products.name') }}
                    </p>

                    <el-input
                      class="width100 fc-input-full-border2"
                      autofocus
                      v-model="depreciation.name"
                      type="text"
                      :placeholder="
                        $t('common.placeholders.enter_depreciation_name')
                      "
                    />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row class="mT10">
                <el-col :span="24">
                  <el-form-item prop="depreciationType">
                    <p class="fc-input-label-txt">
                      {{ $t('common.products.depreciation_type') }}
                    </p>

                    <el-select
                      class="fc-input-full-border2 width100"
                      v-model="depreciation.depreciationType"
                      :placeholder="
                        $t('common.placeholders.select_depreciation_type')
                      "
                    >
                      <el-option
                        v-for="(displayName, key) in depreciationTypes"
                        :key="key"
                        :label="displayName"
                        :value="Number(key)"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row class="mT10">
                <el-col :span="12" class="pR10">
                  <p class="fc-input-label-txt">
                    {{ $t('common.products.period_type') }}
                  </p>

                  <el-select
                    class="fc-input-full-border2 width100"
                    v-model="depreciation.frequencyType"
                    filterable
                  >
                    <el-option
                      v-for="(displayName, key) in frequencyTypes"
                      :key="key"
                      :label="displayName"
                      :value="Number(key)"
                    ></el-option>
                  </el-select>
                </el-col>

                <el-col :span="12" class="pL10">
                  <el-form-item prop="frequency">
                    <p class="fc-input-label-txt">
                      {{ $t('common.wo_report.period') }}
                    </p>

                    <el-input
                      class="width100 fc-input-full-border2"
                      autofocus
                      v-model="depreciation.frequency"
                      type="number"
                      :placeholder="$t('common._common.enter_period')"
                    />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row class="mT10">
                <el-col :span="12" class="pR10">
                  <el-form-item prop="startDateFieldId">
                    <p class="fc-input-label-txt">
                      {{ $t('common.header.start_date_field') }}
                    </p>

                    <el-select
                      class="fc-input-full-border2 width100"
                      v-model="depreciation.startDateFieldId"
                      filterable
                      :placeholder="$t('common._common.select_start_date')"
                    >
                      <el-option
                        v-for="(dateField, dateFldIdx) in dateFields"
                        :key="`${dateField.id}-${dateFldIdx}`"
                        :label="dateField.displayName"
                        :value="dateField.id"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>

        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="submitForm()"
            :loading="saving"
            >{{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}</el-button
          >
        </div>
      </el-form>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { isDateTypeField } from '@facilio/utils/field'
import { DEPRECIATION_TYPES, FREQUENCY_TYPES } from './DepreciationConstant'

export default {
  props: ['id'],

  data() {
    return {
      depreciation: {
        name: '',
        depreciationType: 1,
        frequency: null,
        frequencyType: 1,
        totalPriceFieldId: null,
        salvagePriceFieldId: null,
        currentPriceFieldId: null,
        startDateFieldId: null,
      },
      loading: false,
      saving: false,
      dateFields: {},
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
        frequency: {
          required: true,
          message: this.$t('common.header.please_select_period'),
          trigger: 'change',
        },
        startDateFieldId: {
          required: true,
          message: this.$t('common._common.please_select_a_start_date'),
          trigger: 'change',
        },
      },
    }
  },

  created() {
    let promises = []

    promises.push(this.$store.dispatch('view/loadModuleMeta', 'asset'))

    if (this.id) {
      promises.push(this.init())
    }

    Promise.all(promises).then(() => {
      this.filterOptions()
      this.loading = false
    })

    this.depreciationTypes = DEPRECIATION_TYPES
  },

  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),

    frequencyTypes() {
      return Object.values(FREQUENCY_TYPES).reduce((res, freq) => {
        res[freq.id] = freq.label

        return res
      }, {})
    },
  },

  methods: {
    init() {
      this.loading = true

      let param = { id: this.id }

      return API.fetchRecord('assetdepreciation', param).then(
        ({ assetdepreciation, error }) => {
          if (error) {
            this.$message.error(error.messsage || 'Error Occured')
          } else {
            this.depreciation = assetdepreciation
          }
        }
      )
    },

    filterOptions() {
      let fields = this.$getProperty(this.metaInfo, 'fields', null)

      if (!isEmpty(fields)) {
        this.dateFields = fields.filter(field => isDateTypeField(field))

        if (!this.id) {
          this.depreciation.totalPriceFieldId = (
            fields.find(field => field.name === 'unitPrice') || {}
          ).id
          this.depreciation.currentPriceFieldId = (
            fields.find(field => field.name === 'currentPrice') || {}
          ).id
          this.depreciation.salvagePriceFieldId = (
            fields.find(field => field.name === 'salvageAmount') || {}
          ).id
          this.depreciation.startDateFieldId = (
            fields.find(field => field.name === 'purchasedDate') || {}
          ).id
        }
      }
    },

    submitForm() {
      this.$refs['depreciationForm'].validate(valid => {
        if (!valid) return false

        if (this.id) {
          this.updateDepreciation()
        } else {
          this.createDepreciation()
        }
      })
    },

    createDepreciation() {
      let param = { data: { ...this.depreciation, active: true } }

      this.saving = true
      API.createRecord('assetdepreciation', param).then(
        ({ assetdepreciation, error }) => {
          if (error) {
            this.$message.error(error.messsage || 'Error Occured')
          } else {
            this.$emit('onRecordSaved', assetdepreciation)
            this.$message.success(
              this.$t('common.products.depreciation_schedule_created')
            )
          }

          this.saving = false
          this.closeDialog()
        }
      )
    },

    updateDepreciation() {
      let param = { id: this.id, data: this.depreciation }

      this.saving = true
      API.updateRecord('assetdepreciation', param).then(({ error }) => {
        if (error) {
          this.$message.error(error.messsage || 'Error Occured')
        } else {
          this.$emit('onRecordSaved')
          this.$message.success(
            this.$t('common.products.depreciation_schedule_updated')
          )
        }

        this.saving = false
        this.closeDialog()
      })
    },

    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style scoped>
.lines {
  height: 15px;
  border-radius: 5px;
}
.height40 {
  height: 40px !important;
}
</style>
