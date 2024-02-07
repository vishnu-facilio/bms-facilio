<template>
  <div>
    <p class="fc-input-label-txt">
      Add validations to run before this transition executes.
    </p>
    <div
      class="pointer new-statetransition-config configure-blue"
      @click="addValidationRow()"
    >
      Add Validations
    </div>
    <hr class="separator-line mR40 mT20" />

    <div class="mB40">
      <template v-if="canShowValidations">
        <div
          v-for="(validation, index) in transition.validations"
          :key="index"
          class="validation-block mR50"
        >
          <div class="action-row flex-middle">
            <div class="width50">
              <p class="details-Heading pB5 line-height20 txt-color break-word">
                {{ validation.name }}
              </p>
              <p class="error-message-validation line-height18">
                {{ validation.errorMessage }}
              </p>
            </div>
            <div class="width50 action d-flex justify-content-end">
              <i
                class="el-icon-edit pointer edit-icon pR20"
                @click="editValidation(index)"
                title="Edit validation rule"
                v-tippy
              ></i>
              <i
                class="el-icon-delete delete-icon pointer pR20"
                @click="removeValidationRow(index)"
                title="Delete validation rule"
                v-tippy
              ></i>
            </div>
          </div>
          <hr class="separator-line m0" />
        </div>
      </template>
    </div>

    <el-dialog
      v-if="showValidationPopUp"
      :visible="true"
      width="50%"
      title="Validations"
      class="fieldchange-dialog-validation fc-dialog-center-container"
      custom-class="dialog-header-padding"
      :append-to-body="true"
      :before-close="close"
    >
      <div class="height400 overflow-y-scroll pB60">
        <error-banner
          v-if="error"
          :error.sync="error"
          :errorMessage="errorMessage"
          style="margin: 0 0 20px 0;"
        ></error-banner>
        <p class="fc-input-label-txt pB5">Name</p>
        <el-input
          v-model="validation.name"
          :autofocus="true"
          placeholder="Enter validation name"
          reseize="none"
          class="fc-input-full-border-select2 width100"
        ></el-input>

        <p class="fc-input-label-txt pT20 pB0">Condition</p>
        <el-select
          v-model="validation.namedCriteriaId"
          filterable
          :loading="criteriaListLoading"
          placeholder="Select Condition"
          class="fc-input-full-border2 width100 fc-tag"
          popper-class="condition-popover"
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
              Add New
            </el-button>
          </div>

          <div slot="empty" class="el-select-dropdown__empty">
            <div class="empty-text">No data</div>
            <el-button
              class="btn-green-full filter-footer-btn fw-bold pT10 pB10"
              @click="showCreate = true"
            >
              Add New
            </el-button>
          </div>
        </el-select>

        <div class="flex-center-row-space">
          <p class="fc-input-label-txt pT20 pB5">Error Message</p>
          <div
            @click="openScript = true"
            class="fc-pdf-blue-txt-13 pointer pL10 bold fc-underline-hover pT20 line-height24"
          >
            Configure Script
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
        <el-button @click="close()" class="modal-btn-cancel">CANCEL</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="save(validation)"
          >Save</el-button
        >
      </div>
    </el-dialog>

    <ConditionManagerForm
      v-if="showCreate"
      :isNew="true"
      :moduleName="module"
      @onSave="setConditions"
      @onClose="showCreate = false"
    ></ConditionManagerForm>

    <PlaceholderScriptConfig
      v-if="openScript"
      :module="module"
      :scriptTemplate.sync="validation.errorMessagePlaceHolderScript"
      @onClose="openScript = false"
    ></PlaceholderScriptConfig>
  </div>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'
import ConditionManagerForm from 'pages/setup/conditionmanager/ConditionManagerForm'
import PlaceholderScriptConfig from './popups/PlaceholderScriptConfig'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['transition', 'module', 'criteriaList', 'criteriaListLoading'],
  data() {
    return {
      error: false,
      errorMessage: '',
      showValidationPopUp: false,
      validation: null,
      index: -1,
      showCreate: false,
      openScript: false,
    }
  },
  components: {
    ConditionManagerForm,
    ErrorBanner,
    PlaceholderScriptConfig,
  },
  computed: {
    canShowValidations() {
      let { validations } = this.transition
      return !isEmpty(validations)
    },
  },
  methods: {
    setConditions(condition) {
      this.$emit('setCondition', condition)
      this.validation.namedCriteriaId = condition.id
    },
    addValidationRow() {
      this.validation = {
        name: '',
        errorMessage: '',
        namedCriteriaId: null,
        errorMessagePlaceHolderScript: null,
      }
      this.index = this.transition.validations.length
      this.showValidationPopUp = true
    },

    removeValidationRow(index) {
      this.transition.validations.splice(index, 1)
      this.error = false
      this.errorMessage = ''
      this.$emit('autoSave')
    },

    editValidation(index) {
      this.showValidationPopUp = true
      this.validation = this.$helpers.cloneObject(
        this.transition.validations[index]
      )
      this.index = index
    },

    close() {
      this.showValidationPopUp = false
      this.validation = null
      this.index = -1
    },

    save(validation) {
      let index = this.index
      let { name, errorMessage, namedCriteriaId } = validation

      if (
        !isEmpty(name) &&
        !isEmpty(errorMessage) &&
        !isEmpty(namedCriteriaId)
      ) {
        this.$set(this.transition.validations, index, { ...validation })
        this.error = false
        this.errorMessage = ''
        this.$emit('autoSave')
        this.close()
      } else {
        this.error = true
        this.errorMessage = 'Please configure a valid rule'
        return
      }
    },
  },
}
</script>
<style scoped>
.validation-block {
  padding: 0px;
  position: relative;
}
.delete-icon {
  color: red;
}
.action-row {
  padding: 10px;
  margin: 0 -10px;
}
.action-row:hover {
  background-color: #f1f8fa;
}
.fieldchange-dialog-validation {
  padding: 30px;
  overflow-y: hidden;
}
.error-message-validation {
  font-size: 12px;
  font-weight: normal;
  font-style: normal;
  letter-spacing: 0.34px;
  color: #ec7c7c;
  margin: 0px;
  word-break: break-word;
}
.action-row .action {
  visibility: hidden;
}
.action-row:hover .action {
  visibility: visible;
}
</style>
