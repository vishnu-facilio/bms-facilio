<template>
  <div class="new-custom-button">
    <el-dialog
      :visible="true"
      :fullscreen="true"
      :append-to-body="true"
      :before-close="() => closeDialog()"
      custom-class="fc-dialog-form fc-dialog-right custom-rule-dialog setup-dialog50 setup-dialog"
      style="z-index: 1999"
    >
      <el-form
        :model="customButtonObj"
        :rules="rules"
        ref="ruleForm"
        :label-position="'top'"
      >
        <div
          class="new-header-container mR30 pL30 new-header-modal new-header-text"
        >
          <div class="setup-modal-title">
            {{
              isNew
                ? $t('setup.customButton.header_new')
                : $t('setup.customButton.header_edit')
            }}
          </div>
        </div>
        <div class="new-body-modal pL30 pR30">
          <el-row>
            <el-col :span="12">
              <el-form-item prop="name">
                <p class="fc-input-label-txt">
                  {{ $t('setup.customButton.name') }}
                </p>
                <el-input
                  :autofocus="true"
                  v-model="customButtonObj.name"
                  class="width100 pR20"
                  :placeholder="$t('setup.customButton.name_placeholder')"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="position">
                <p class="fc-input-label-txt">
                  {{ $t('setup.customButton.button_position') }}
                </p>
                <el-select
                  v-model="position"
                  clearable
                  class="width100 pR20"
                  @change="clearSelectedActions"
                  :placeholder="
                    $t('setup.customButton.button_position_placeholder')
                  "
                >
                  <el-option
                    v-for="option in positionTypeHash"
                    :key="option"
                    :value="option"
                    :label="positionLabel[option]"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item>
                <p class="fc-input-label-txt">
                  {{ $t('setup.approvalprocess.description') }}
                </p>
                <el-input
                  v-model="customButtonObj.description"
                  :min-rows="1"
                  type="textarea"
                  :autosize="{ minRows: 2, maxRows: 4 }"
                  class="fc-input-full-border-select2 width100"
                  :placeholder="$t('setup.customButton.desc_placeholder')"
                  resize="none"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <template>
            <CustomButtonPermissions
              @setProperties="setProperties"
              :module="module"
              :customButtonObj="customButtonObj"
            />

            <div
              class="fc-input-label-txt f12 letter-spacing1 bold pB0  text-uppercase display-flex-between-space pT15"
            >
              {{ $t('setup.users_management.criteria') }}
              <el-switch
                v-model="canShowCriteria"
                @change="handleCriteria"
                :disabled="position === positionTypeHash.LIST_TOP"
              ></el-switch>
            </div>
            <p class="fc-sub-title-desc">
              {{ $t('setup.customButton.criteria_subtext') }}
            </p>

            <el-form-item prop="criteria" v-if="canShowCriteria">
              <CriteriaBuilder
                v-model="customButtonObj.criteria"
                :moduleName="module"
              />
            </el-form-item>

            <hr class="separator-line" />
            <el-row>
              <el-col :span="24">
                <el-form-item prop="actionType">
                  <p class="subHeading-pink-txt text-fc-pink">
                    Behaviour
                  </p>
                  <p class="fc-sub-title-desc">
                    Specify a behaviour for this custom button
                  </p>
                  <el-select
                    v-model="selectedActionType"
                    clearable
                    @change="clearCustomBtnAction"
                    class="width100 pR20"
                    :placeholder="
                      $t('setup.customButton.action_type_placeholder')
                    "
                  >
                    <el-option
                      v-for="option in availableActions"
                      :key="option"
                      :value="option"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <CreateRecordBehaviour
              v-if="selectedActionType === ACTIONS.CREATE_RECORD"
              @setProperties="setProperties"
              :module="module"
              :customButtonObject="customButtonObj"
            />

            <UpdateRecordBehaviour
              v-if="selectedActionType === ACTIONS.UPDATE_RECORD"
              @setProperties="setProperties"
              :formData="customButtonObj.selectedFields"
              :module="module"
              :formId="customButtonObj.formId"
            />

            <RedirectUrlBehaviour
              v-if="selectedActionType === ACTIONS.REDIRECT_URL"
              :module="module"
              @setProperties="setProperties"
              :customButtonObject="customButtonObj"
              :positionType="position"
            />
            <InternalRedirectBehaviour
              v-if="
                selectedActionType === ACTIONS.OPEN_FORM ||
                  selectedActionType === ACTIONS.OPEN_SUMMARY
              "
              :selectedAction="selectedActionType"
              :customButtonObject="customButtonObj"
              :currentModuleName="module"
              @setProperties="setProperties"
            />

            <ConnectedAppWidgetBehaviour
              v-if="selectedActionType === ACTIONS.CONNECTED_APPS"
              @setProperties="setProperties"
              :customButtonObject="customButtonObj"
            />

            <CustomButtonActions
              @onAddAction="handleActionAdd"
              ref="customButtonActions"
              v-if="canShowSubAction"
              :module="module"
              :existingAction.sync="customButtonObj.actions"
            />
          </template>
        </div>
        <div class="modal-dialog-footer">
          <el-form-item style="margin-bottom:0px">
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
            >
              {{
                saving
                  ? $t('common._common._saving')
                  : $t('common._common._save')
              }}
            </el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { CriteriaBuilder } from '@facilio/criteria'
import { API } from '@facilio/api'
import { mapGetters, mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import {
  getActionType,
  ACTION_TYPES,
  POSITION_TYPE,
  POSITION_LABELS,
} from './CustomButtonUtil'

import CustomButtonActions from './components/CustomButtonActions'
import CreateRecordBehaviour from './components/CreateRecordBehaviour'
import CustomButtonPermissions from './components/CustomButtonPermissions'
import UpdateRecordBehaviour from './components/UpdateRecordBehaviour'
import RedirectUrlBehaviour from './components/RedirectUrlBehaviour'
import ConnectedAppWidgetBehaviour from './components/ConnectedAppWidgetBehaviour'
import InternalRedirectBehaviour from './components/InternalRedirectBehaviour'

export default {
  name: 'NewCustomButton',
  props: ['isNew', 'selectedCustomButton', 'closeDialog', 'module'],
  components: {
    CriteriaBuilder,
    CustomButtonPermissions,
    CustomButtonActions,
    CreateRecordBehaviour,
    UpdateRecordBehaviour,
    RedirectUrlBehaviour,
    ConnectedAppWidgetBehaviour,
    InternalRedirectBehaviour,
  },
  data() {
    return {
      customButtonObj: {
        actions: null,
        name: null,
        description: null,
        criteria: null,
        positionType: null,
        formModuleName: null,
        formId: null,
        lookupFieldId: null,
        buttonType: 1,
        approvers: null,
        selectedFields: [],
      },

      position: null,
      selectedActionType: null,
      ACTIONS: ACTION_TYPES,
      rules: {
        name: [
          {
            required: true,
            message: 'Please enter a name',
            trigger: 'blur',
          },
        ],
        position: [
          {
            validator: this.formValidate,
            trigger: 'change',
          },
        ],
        criteria: [
          {
            validator: this.formValidate,
            trigger: 'change',
          },
        ],
        formTemplateUpdateRecord: [
          {
            validator: this.formValidate,
            trigger: 'change',
          },
        ],
      },
      saving: false,
      criteriaRendered: false,
      canShowCriteria: false,
      positionTypeHash: POSITION_TYPE,
      positionLabel: POSITION_LABELS,
    }
  },
  created() {
    this.deserialize()
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
  },
  computed: {
    ...mapGetters('automationSetup', ['getAutomationModulesList']),
    ...mapState({
      users: state => state.users,
      teams: state => state.groups,
      roles: state => state.roles,
    }),
    canShowSubAction() {
      let actionType = this.$getProperty(this, 'selectedActionType')
      return [
        ACTION_TYPES.CREATE_RECORD,
        ACTION_TYPES.UPDATE_RECORD,
        ACTION_TYPES.OTHER_ACTIONS,
      ].includes(actionType)
    },
    buttonPositionOptions() {
      return Object.keys(POSITION_TYPE)
    },
    availableActions() {
      if (this.position === POSITION_TYPE.LIST_TOP) {
        return {
          REDIRECT_URL: ACTION_TYPES.REDIRECT_URL,
          CONNECTED_APPS: ACTION_TYPES.CONNECTED_APPS,
        }
      } else if (this.position === POSITION_TYPE.LIST_BAR) {
        return {
          OTHER_ACTIONS: ACTION_TYPES.OTHER_ACTIONS,
          UPDATE_RECORD: ACTION_TYPES.UPDATE_RECORD,
        }
      } else {
        return ACTION_TYPES
      }
    },
  },
  watch: {
    selectedActionType(newValue, oldValue) {
      if (newValue !== oldValue) {
        if (
          [
            ACTION_TYPES.REDIRECT_URL,
            ACTION_TYPES.CONNECTED_APPS,
            ACTION_TYPES.OPEN_FORM,
            ACTION_TYPES.OPEN_SUMMARY,
          ].includes(newValue)
        ) {
          this.customButtonObj.buttonType = 2
          this.customButtonObj.actions = null
        } else {
          this.customButtonObj.buttonType = 1
        }
      }
    },
  },
  methods: {
    clearSelectedActions() {
      this.selectedActionType = null
      this.customButtonObj.actions = null
    },
    serialize() {
      let { customButtonObj } = this

      Object.keys(customButtonObj).forEach(property => {
        if (isEmpty(customButtonObj[property])) {
          delete customButtonObj[property]
        }
      })
      if (
        !this.isNew &&
        this.selectedActionType === ACTION_TYPES.UPDATE_RECORD
      ) {
        customButtonObj.formModuleName = null
      }
      if (!this.canShowCriteria && customButtonObj.criteria) {
        delete customButtonObj.criteria
      }
      let actionsTab = this.$refs['customButtonActions']
      customButtonObj.actions = actionsTab
        ? actionsTab.serializeActions() || []
        : []
      customButtonObj.positionType = this.position
      delete customButtonObj['form']

      customButtonObj['shouldFormInterfaceApply'] = false
      return customButtonObj
    },
    deserialize() {
      this.customButtonObj = {
        ...this.customButtonObj,
        ...this.selectedCustomButton,
      }
      if (!this.isNew) {
        let { customButtonObj } = this
        if (!isEmpty(customButtonObj.criteria)) {
          this.canShowCriteria = true
        }
        this.selectedActionType = getActionType(customButtonObj)
        let fields = this.$getProperty(customButtonObj, 'form.sections')
        if (fields) {
          this.customButtonObj.selectedFields = fields
        }

        this.position = customButtonObj.positionType
      }
    },
    setProperties(mutatedObj) {
      this.customButtonObj = { ...this.customButtonObj, ...mutatedObj }
    },
    async save() {
      let rule = this.serialize(this.customButtonObj)
      let params = {
        moduleName: this.module,
        rule,
      }
      await this.$refs['ruleForm'].validate(async valid => {
        if (valid) {
          this.saving = true

          let { error } = await API.post('v2/custombutton/addOrUpdate', params)

          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.closeDialog(true)
          }
          this.saving = false
        }
      })
    },
    formValidate(rule, value, callback) {
      let fieldName = this.$getProperty(rule, 'field')
      if (fieldName === 'position') {
        if (isEmpty(this.position)) {
          callback(new Error('Please select a button position'))
        } else {
          callback()
        }
      }
      callback()
    },
    serializeCriteria(criteria) {
      Object.keys(criteria.conditions).forEach(condition => {
        if (parseInt(condition) === 1) {
          if (!criteria.conditions[1].fieldName) {
            criteria = null
          }
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
    handleActionAdd(actions) {
      this.customButtonObj.actions = actions
    },
    clearCustomBtnAction() {
      let deletableProps = [
        'formModuleName',
        'formId',
        'lookupFieldId',
        'config',
        'actions',
        'form',
        'selectedFields',
      ]
      deletableProps.forEach(prop => (this.customButtonObj[prop] = null))
    },
    handleCriteria(canShowCriteria) {
      if (!canShowCriteria) {
        this.$set(this.customButtonObj, 'criteria', null)
        this.$set(this.customButtonObj, 'criteriaId', null)
      }
    },
  },
}
</script>
