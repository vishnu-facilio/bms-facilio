<template>
  <div>
    <p class="fc-input-label-txt">
      Add confirmations required before this transition executes.
    </p>
    <div
      class="pointer new-statetransition-config configure-blue"
      @click="addValidationRow()"
    >
      Add Confirmation
    </div>
    <hr class="separator-line mR40 mT20" />

    <div class="mB40">
      <template v-if="hasConfirmations">
        <div
          v-for="(confirmationDialog, index) in transition.confirmationDialogs"
          :key="index"
          class="validation-block mR50"
        >
          <div class="action-row flex-middle">
            <div class="width50">
              <p class="details-Heading pB5 line-height20 txt-color break-word">
                {{ confirmationDialog.name }}
              </p>
              <p class="error-message-validation line-height18">
                {{ confirmationDialog.message }}
              </p>
            </div>
            <div class="width50 action d-flex justify-content-end">
              <i
                class="el-icon-edit pointer edit-icon pR20"
                @click="editConfirmation(index)"
                title="Edit validation rule"
                v-tippy
              ></i>
              <i
                class="el-icon-delete delete-icon pointer pR20"
                @click="removeConfirmation(index)"
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
      v-if="showConfirmationPopup"
      :visible="true"
      width="50%"
      title="Confirmation"
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
          v-model="confirmation.name"
          :autofocus="true"
          placeholder="Enter dialog name"
          reseize="none"
          class="fc-input-full-border-select2 width100"
        ></el-input>

        <p class="fc-input-label-txt pT20 pB0">Condition</p>
        <el-select
          v-model="confirmation.namedCriteriaId"
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
          <p class="fc-input-label-txt pT20 pB5">Message</p>
          <div
            @click="openScript = true"
            class="fc-pdf-blue-txt-13 pointer pL10 bold fc-underline-hover pT20 line-height24"
          >
            Configure Script
          </div>
        </div>
        <el-input
          v-model="confirmation.message"
          :min-rows="3"
          :autosize="{ minRows: 3, maxRows: 4 }"
          type="textarea"
          placeholder="Enter a message"
          reseize="none"
          class="fc-input-full-border-select2 width100"
        ></el-input>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="close()" class="modal-btn-cancel">CANCEL</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="save(confirmation)"
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
      :scriptTemplate.sync="confirmation.messagePlaceHolderScript"
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
      showConfirmationPopup: false,
      confirmation: null,
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
    hasConfirmations() {
      let { confirmationDialogs } = this.transition
      return !isEmpty(confirmationDialogs)
    },
  },
  methods: {
    setConditions(condition) {
      this.$emit('setCondition', condition)
      this.confirmation.namedCriteriaId = condition.id
    },
    addValidationRow() {
      this.confirmation = {
        name: '',
        message: '',
        namedCriteriaId: null,
        messagePlaceHolderScript: null,
      }
      this.index = this.transition.confirmationDialogs.length
      this.showConfirmationPopup = true
    },

    removeConfirmation(index) {
      this.transition.confirmationDialogs.splice(index, 1)
      this.error = false
      this.errorMessage = ''
      this.$emit('autoSave')
    },

    editConfirmation(index) {
      this.showConfirmationPopup = true
      let confirmation = this.transition.confirmationDialogs[index]
      let { namedCriteriaId } = confirmation || {}

      this.confirmation = {
        ...confirmation,
        namedCriteriaId: !isEmpty(namedCriteriaId) ? namedCriteriaId : null,
      }
      this.index = index
    },

    close() {
      this.showConfirmationPopup = false
      this.confirmation = null
      this.index = -1
    },

    save(confirmation) {
      let { index } = this
      let { name, message, namedCriteriaId } = confirmation

      if (isEmpty(namedCriteriaId)) {
        delete confirmation.namedCriteriaId
      }

      if (!isEmpty(name) && !isEmpty(message)) {
        this.$set(this.transition.confirmationDialogs, index, {
          ...confirmation,
        })
        this.error = false
        this.errorMessage = ''
        this.$emit('autoSave')
        this.close()
      } else {
        this.error = true
        this.errorMessage = 'Please configure a valid confirmation dialog'
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
<style lang="scss">
.condition-popover {
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
</style>
