<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :before-close="closeForm"
      :append-to-body="true"
      :title="'Tax'"
      custom-class="fc-dialog-center-container f-webform-right-dialog f-webform-center-dialog"
    >
      <div class="height500">
        <div
          v-if="isEdit"
          class="fc-warning-tag-1 flex-middle justify-content-center mB10"
        >
          <inline-svg
            src="svgs/alert"
            iconClass="icon text-left icon-sm fill-yellow vertical-text-top"
          ></inline-svg>
          <div class="pL10 break-word text-align-center">
            {{ $t('common.dialog.updating_tax_mark_excisting_inactive') }}
          </div>
        </div>
        <el-form
          :model="taxData"
          :ref="`tax_form`"
          :rules="validationRules"
          :label-position="'top'"
        >
          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item
                prop="name"
                :label="
                  type === 1
                    ? $t('quotation.common.tax_name')
                    : $t('quotation.common.tax_group_name')
                "
                :required="true"
                class="mB10"
              >
                <el-input
                  :autofocus="true"
                  v-model="taxData.name"
                  class="width100 fc-input-full-border2"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row v-if="type === 1" class="mB10">
            <el-col :span="24">
              <el-form-item
                prop="rate"
                :required="true"
                :label="$t('quotation.common.tax_rate') + `(%)`"
                class="mB10"
              >
                <el-input
                  type="number"
                  v-model="taxData.rate"
                  class="width100 fc-input-full-border2"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row v-else-if="type === 2" class="mB10">
            <el-col :span="24">
              <p class="fc-input-label-txt pB5">
                {{ $t('quotation.common.associate_taxes') }}
              </p>
              <el-table
                ref="associateTaxTable"
                :data="individualTaxList"
                @selection-change="taxSelectionChange"
                height="293"
                :empty-text="$t('common.products.no_taxes_available')"
                class="setup-tax-table"
              >
                <el-table-column type="selection" width="60"></el-table-column>
                <el-table-column
                  sortable
                  :label="$t('common.products.tax_name')"
                  prop="name"
                >
                </el-table-column>
                <el-table-column
                  sortable
                  :label="$t('common.products.rate')"
                  prop="rate"
                >
                </el-table-column>
              </el-table>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeForm()">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          :loading="isSaving"
          class="modal-btn-save"
          type="primary"
          @click="validateData()"
          >{{ $t('common._common._save') }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty, isNumber } from '@facilio/utils/validation'

export default {
  props: ['visibility', 'record', 'type', 'individualTaxes'],
  data() {
    return {
      taxData: {
        name: null,
        rate: null,
        childTaxes: [],
        id: null,
      },
      isSaving: false,
      validationRules: {
        name: [
          {
            required: true,
            message: this.$t('common._common.please_enter_tax_name'),
            trigger: 'blur',
          },
        ],
      },
      selectedRecords: [],
      selectedRecordsId: [],
      individualTaxList: [],
    }
  },
  mounted() {
    this.individualTaxList = this.$helpers.cloneObject(
      this.individualTaxes || []
    )
    this.fillData()
  },
  computed: {
    isEdit() {
      return !isEmpty(this.record)
    },
  },
  methods: {
    fillData() {
      if (this.type === 1) {
        this.$setProperty(this.validationRules, 'rate', [
          {
            validator: (rule, value, callback) =>
              this.validateTaxRate({ value, callback }),
            trigger: blur,
          },
        ])
      }
      if (!isEmpty(this.record)) {
        let tax = this.$helpers.cloneObject(this.record)
        this.$setProperty(this, 'taxData.name', tax.name)
        this.$setProperty(this, 'taxData.rate', tax.rate)
        this.$setProperty(this, 'taxData.id', tax.id)
        let { type } = tax
        if (type === 2) {
          let { childTaxes } = tax
          this.$nextTick(() => {
            if (this.$refs['associateTaxTable']) {
              childTaxes.forEach(row => {
                this.$refs['associateTaxTable'].toggleRowSelection(
                  this.individualTaxList.find(tax => tax.id === row.id, true)
                )
              })
            }
          })
        }
      }
    },
    closeForm() {
      this.$emit('update:visibility', false)
    },
    validateData() {
      this.$refs['tax_form'].validate(valid => {
        if (valid) {
          if (this.type === 2) {
            if (this.selectedRecordsId.length < 2) {
              this.$message.error(
                this.$t('common.dialog.select_atleast_two_taxes_to_associate')
              )
              return
            } else {
              this.selectedRecordsId.forEach(id =>
                this.taxData.childTaxes.push({ id: id })
              )
            }
          }
          this.saveData()
        } else {
          this.$message.error('Error Occurred')
        }
      })
    },
    validateTaxRate({ value, callback }) {
      let val = Number(value)
      if (!isNumber(val) || val > 100 || val < 0) {
        callback(new Error(this.$t('common.header.please_input_a_valid_rate')))
      } else {
        callback()
      }
    },
    taxSelectionChange(records) {
      this.selectedRecords = records
      this.selectedRecordsId = records.map(val => val.id)
    },
    async saveData() {
      let record = this.$helpers.cloneObject(this.taxData)

      if (!isEmpty(this.type)) {
        record['type'] = this.type
      }
      record['isActive'] = true
      this.isSaving = true
      let { error } = await API.createRecord('tax', {
        data: record,
      })

      if (error) {
        this.$message.error(
          error.message || this.$t('common.wo_report.unable_to_save_tax')
        )
      } else {
        this.$message.success(
          record.id > 0
            ? this.$t('common.products.tax_updated_successfully')
            : this.$t('common.products.tax_added_successfully')
        )
        this.$emit('saved')
        this.closeForm()
      }
      this.isSaving = false
    },
  },
}
</script>
