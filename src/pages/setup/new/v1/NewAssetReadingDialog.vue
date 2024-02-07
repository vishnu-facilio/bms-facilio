<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <error-banner
      :error.sync="error"
      :errorMessage="errorMessage"
    ></error-banner>
    <el-form ref="newAssetReadingForm" :model="module" :label-position="'top'">
      <div class="new-header-container">
        <div class="setup-modal-title">
          {{ $t('common.wo_report.new_asset_reading') }}
        </div>
      </div>
      <div
        class="new-body-modal pB100 pR20"
        style="height: calc(100vh - 100px);"
      >
        <div>
          <p class="label-txt2">{{ $t('common.products.select_category') }}</p>
          <div>
            <el-select
              v-model="module.categoryId"
              filterable
              :disabled="categoryId > 0"
              :placeholder="$t('common.products.select_category')"
              class="fc-input-full-border-select2 pT10"
            >
              <el-option
                v-for="category in assetCategory"
                :key="category.id"
                :label="category.displayName"
                :value="category.id"
              ></el-option>
            </el-select>
          </div>
          <new-f-module-builder
            v-model="module"
            :settingsIcon="true"
          ></new-f-module-builder>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button type="primary" @click="save" class="modal-btn-save">{{
          $t('common._common._save')
        }}</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import NewFModuleBuilder from 'pages/setup/new/v1/NewFmoduleBuilder'
import ErrorBanner from '@/ErrorBanner'
import { mapState } from 'vuex'

export default {
  props: ['visibility', 'model', 'hideFormula', 'categoryId'],
  components: {
    NewFModuleBuilder,
    ErrorBanner,
  },
  data() {
    return {
      module: {
        categoryId: null,
        fields: [
          {
            safeLimitPattern: 'none',
            raiseSafeLimitAlarm: false,
            displayName: '',
            dataType: 3,
            dataTypeTemp: 3,
            safeLimitSeverity: 'Minor',
            lesserThan: null,
            greaterThan: null,
            betweenTo: null,
            betweenFrom: null,
            inputPatternSeverity: 'Minor',
            counterField: false,
          },
        ],
        includeValidations: true,
      },
      error: false,
      errorMessage: null,
      dummyAssertValue: [1],
      chooserVisibility: false,
      alarmSeverity: ['Critical', 'Major', 'Minor'],
    }
  },
  mounted() {
    if (this.model) {
      Object.assign(this.module, this.model)
    }
    if (this.categoryId > 0) {
      this.module.categoryId = this.categoryId
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),

    resourceData() {
      return {
        assetCategory: this.module.categoryId,
        isIncludeResource: true,
      }
    },
    categoryName() {
      if (this.module.categoryId) {
        let category = this.assetCategory.find(
          category => category.id === this.module.categoryId
        )
        if (category) {
          return category.name
        }
      }
      return ''
    },
  },
  methods: {
    cancel() {
      this.$emit('canceled')
    },
    save() {
      this.saveNew()
    },
    saveNew() {
      let formModel = this.module
      let url
      let param
      this.error = false
      let fieldReadingRules = []

      this.module.fields.forEach(field => {
        if (field.dataType === 2 || field.dataType === 3) {
          fieldReadingRules.push(
            this.$common.getSafeLimitRules(field, this.module.categoryId)
          )
        }
      })
      if (!formModel.categoryId) {
        this.error = true
        this.errorMessage = this.$t(
          'common.header.please_select_category_fields'
        )
        return
      } else if (!this.module.fields[0].displayName) {
        this.error = true
        this.errorMessage = this.$t('common.header.please_enter_name_fields')
        return
      } else {
        this.module.fields.forEach(d => {
          if (!d.displayName) {
            this.error = true
            this.errorMessage = this.$t(
              'common.header.please_enter_name_fields'
            )
            return
          }
          if (d.dataType === 8) {
            if (!d.values || d.values.length < 2) {
              this.error = true
              this.errorMessage = this.$t(
                'common.header.please_enter_atleast_option_picklist'
              )
            }
          }
        })
        if (this.error) {
          return
        }
      }
      url = '/reading/addsetupreading'
      let moduleName = this.module.fields[0].displayName
      param = {
        resourceType: 'Asset',
        fieldJsons: this.module.fields,
        parentCategoryId: formModel.categoryId,
        readingName: moduleName,
        fieldReadingRules,
      }

      this.$http.post(url, param).then(response => {
        if (typeof response.data === 'object') {
          this.$message.success(
            this.$t('common.wo_report.new_reading_added_successfully')
          )
          this.reset()
          this.$emit('saved')
          this.closeDialog()
        } else {
          this.$message.error(
            this.$t('common.wo_report.new_reading_addition_failed')
          )
        }
      })
    },
    closeDialog() {
      this.reset()
      this.$emit('update:visibility', false)
    },
    reset() {
      this.$refs.newAssetReadingForm.categoryId = ''
      this.$refs.newAssetReadingForm.fields = []
    },
  },
}
</script>
<style>
.new-header-container {
  margin-top: 0 !important;
}
.asset-field-input .el-input .el-input__inner,
.el-textarea .el-textarea__inner {
  height: 18px;
}
</style>
