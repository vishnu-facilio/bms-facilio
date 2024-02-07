<template>
  <div>
    <el-dialog
      :visible="true"
      :fullscreen="true"
      :append-to-body="true"
      :before-close="closeDialog"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog"
      style="z-index: 999999"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            <div id="policy-header" class="section-header">
              {{ title }}
            </div>
          </div>
        </div>
      </div>
      <div v-if="isLoading" class="form-builder-shimmer-lines mT10">
        <div v-for="i in 2" :key="i">
          <span class="span-name loading-shimmer"></span>
          <span class="lines loading-shimmer"></span>
        </div>
        <span class="span-name loading-shimmer"></span>
        <span class="rectangle loading-shimmer"></span>
      </div>
      <div v-else class="height400 overflow-y-scroll pB60 m20">
        <p class="fc-input-label-txt pB5">
          {{ $t('setup.form_builder.validation.name') }}
        </p>
        <el-input
          v-model="validation.name"
          :autofocus="true"
          placeholder="Enter validation name"
          reseize="none"
          class="fc-input-full-border-select2 width100"
        ></el-input>
        <p class="fc-input-label-txt pT20 pB0">
          {{ $t('setup.form_builder.validation.condition') }}
        </p>
        <el-select
          v-model="validation.namedCriteriaId"
          filterable
          :loading="conditionLoading"
          placeholder="Select Condition"
          class="fc-input-full-border2 width100 fc-tag"
          popper-class="form-builder-condition-popover"
        >
          <el-option
            v-for="criteria in criteriaList"
            :key="criteria.id"
            :label="criteria.name"
            :value="criteria.id"
          ></el-option>

          <div class="search-select-filter-btn pT5">
            <el-button
              class="btn-green-full filter-footer-btn fw-bold pT10 pB10"
              @click="showCreate = true"
            >
              {{ $t('setup.form_builder.validation.add_new') }}
            </el-button>
          </div>
          <div slot="empty" class="el-select-dropdown__empty">
            <div class="empty-text">
              {{ $t('setup.form_builder.validation.no_data') }}
            </div>
            <el-button
              class="btn-green-full filter-footer-btn fw-bold pT10 pB10"
              @click="showCreate = true"
            >
              {{ $t('setup.form_builder.validation.add_new') }}
            </el-button>
          </div>
        </el-select>
        <div class="flex-center-row-space">
          <p class="fc-input-label-txt pT20 pB5">
            {{ $t('setup.form_builder.validation.error_message') }}
          </p>
          <div
            @click="openScript = true"
            class="fc-pdf-blue-txt-13 pointer pL10 bold fc-underline-hover pT20 line-height24"
          >
            {{ $t('setup.form_builder.validation.configure_script') }}
          </div>
        </div>
        <el-input
          v-model="validation.errorMessage"
          type="textarea"
          :min-rows="3"
          :autosize="{ minRows: 3, maxRows: 4 }"
          placeholder="Enter an error message"
          reseize="none"
          class="fc-input-full-border-select2 width100"
        ></el-input>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog" class="modal-btn-cancel">{{
          $t('setup.form_builder.validation.cancel')
        }}</el-button>
        <el-button type="primary" class="modal-btn-save" @click="onSave">{{
          $t('setup.form_builder.validation.save')
        }}</el-button>
      </div>
    </el-dialog>
    <ConditionManagerForm
      v-if="showCreate"
      :isNew="true"
      :moduleName="moduleName"
      @onSave="setConditions"
      @onClose="showCreate = false"
    ></ConditionManagerForm>
    <PlaceholderScriptConfig
      v-if="openScript"
      :module="moduleName"
      :scriptTemplate.sync="validation.errorMessagePlaceHolderScript"
      @onClose="openScript = false"
    ></PlaceholderScriptConfig>
  </div>
</template>
<script>
import ConditionManagerForm from 'pages/setup/conditionmanager/ConditionManagerForm'
import PlaceholderScriptConfig from 'pages/setup/stateflow/popups/PlaceholderScriptConfig'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { FormValidation } from './FormValidationModel'

export default {
  props: ['moduleName', 'selectedId', 'formId'],
  components: { ConditionManagerForm, PlaceholderScriptConfig },
  data() {
    return {
      isLoading: true,
      showCreate: false,
      openScript: false,
      validation: {},
      criteriaList: [],
      conditionLoading: false,
    }
  },
  async created() {
    await this.loadCriteriaList()
    await this.loadValidationData()
  },
  computed: {
    isNew() {
      return isEmpty(this.selectedId)
    },
    title() {
      return this.isNew
        ? this.$t('setup.form_builder.validation.add_form')
        : this.$t('setup.form_builder.validation.edit_form')
    },
  },
  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    setConditions(condition) {
      this.conditionLoading = true
      this.criteriaList.push(condition)
      this.$nextTick(() => {
        this.validation.namedCriteriaId = condition.id
        this.conditionLoading = false
      })
    },
    async loadValidationData() {
      this.isLoading = true

      if (this.isNew) {
        this.validation = new FormValidation()
      } else {
        try {
          let params = { id: this.selectedId }
          this.validation = await FormValidation.fetch(params)
        } catch (errorMsg) {
          this.$message.error(errorMsg)
        }
      }
      this.isLoading = false
    },
    async loadCriteriaList() {
      this.conditionLoading = true

      let { moduleName } = this
      let { data, error } = await API.post('v2/namedCriteria/list', {
        moduleName,
      })
      if (!error) {
        this.criteriaList = data?.namedCriteriaList || []
      } else {
        this.$message.error(error.message)
      }
      this.conditionLoading = false
    },
    async onSave() {
      try {
        if (isEmpty(this.selectedId)) {
          this.validation.parentId = this.formId
          await this.validation.save()
          this.$message.success(
            this.$t('setup.form_builder.validation.creation_success')
          )
        } else {
          await this.validation.patch()
          this.$message.success(
            this.$t('setup.form_builder.validation.update_success')
          )
        }
        this.$emit('onSave')
        this.closeDialog()
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
    },
  },
}
</script>
<style lang="scss">
.form-builder-condition-popover {
  .el-select-dropdown__list {
    padding-bottom: 50px;
  }
  .el-select-dropdown__item {
    font-weight: 400;
  }
  .el-select-dropdown.el-popper .el-scrollbar__wrap {
    max-width: none;
  }
  .empty-text {
    padding: 10px 0;
    margin: 0;
    text-align: center;
    color: #999;
    font-size: 14px;
    line-height: 24px;
  }
}
.form-builder-shimmer-lines .lines {
  height: 30px;
  width: 90%;
  margin: 0px 20px 10px;
  border-radius: 5px;
}
.form-builder-shimmer-lines .rectangle {
  height: 90px;
  width: 90%;
  margin: 0px 20px 10px;
  border-radius: 5px;
}
.form-builder-shimmer-lines .span-name {
  height: 10px;
  width: 50%;
  margin: 20px 20px 5px;
  border-radius: 5px;
}
</style>
