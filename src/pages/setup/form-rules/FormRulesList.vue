<template>
  <div class="position-relative">
    <portal to="builder-btn-portal" slim>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          class="fc-btn-green-medium-fill shadow-none"
          @click="openFormRuleCreation"
        >
          {{ addRuleText }}
        </el-button>
        <el-button
          type="primary"
          class="fc-btn-green-medium-fill shadow-none"
          @click="redirect"
          >{{ $t('common._common.done') }}</el-button
        >
      </div>
    </portal>

    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">
                  {{ $t('common.roles.name') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('maintenance._workorder.description') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('setup.setupLabel.trigger_field') }}
                </th>
                <th v-if="isSubForm" class="setting-table-th setting-th-text">
                  {{ $t('forms.rules.subform_name') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('maintenance._workorder.status') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="isLoading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="isLoading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="$validation.isEmpty(formRuleList)">
              <tr class="tablerow">
                <td
                  colspan="100%"
                  class="text-center"
                  style="padding-top: 18px;padding-bottom: 18px;"
                >
                  {{ $t('forms.rules.forms_empty') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(rule, index) in formRuleList"
                :key="index"
              >
                <td class="text-capitalize" style="width: 25%;">
                  {{ rule.name }}
                </td>

                <td style="width: 25%;">
                  {{ rule.description ? rule.description : '---' }}
                </td>

                <td class="text-capitalize" style="width: 20%;">
                  {{ getFieldsDisplayname(rule.triggerFields) }}
                </td>
                <td
                  v-if="isSubForm"
                  class="text-capitalize"
                  style="width: 20%;"
                >
                  {{ getSubFormName(rule.subFormId) }}
                </td>

                <td>
                  <el-switch
                    v-model="rule.status"
                    @change="changeRuleStatus(rule)"
                    class="Notification-toggle"
                    active-color="rgba(57, 178, 194, 0.8)"
                    inactive-color="#e5e5e5"
                  ></el-switch>
                </td>

                <td class="pR0" style="width: 20%">
                  <div
                    class="text-left actions text-center mL20"
                    style="margin-top:-3px;"
                  >
                    <i
                      class="el-icon-edit pointer"
                      @click="editFormRuleCreation(rule)"
                    ></i>
                    &nbsp;&nbsp;
                    <i
                      class="el-icon-delete pointer"
                      @click="showConfirmDelete(rule)"
                    ></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <NewRule
      v-if="showFormRule"
      :isSubForm="isSubForm"
      :visibility.sync="showFormRule"
      :selectedFormObj.sync="selectedForm"
      @updateTitle="value => (ruleTitle = value)"
      :formId="formId"
      :isNew="isNew"
      @saved="getFormRulesList"
      :formRuleId="formRuleId"
      :isUpdateForm="isUpdateForm"
      class="mB20"
    ></NewRule>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { isEmpty, isFunction } from '@facilio/utils/validation'
import NewRule from 'pages/setup/form-rules/NewRule'
import { API } from '@facilio/api'

export default {
  props: [
    'moduleName',
    'formId',
    'redirectToFormsList',
    'formObj',
    'isUpdateForm',
  ],
  components: { NewRule },

  data() {
    return {
      isLoading: false,
      triggerFieldMap: {},
      formRuleId: null,
      formRuleList: [],
      selectedForm: null,
      ruleTitle: 'Form Rule',
      showFormRule: false,
      isNew: false,
    }
  },

  computed: {
    ...mapState({
      sites: state => state.sites,
    }),
    siteObj() {
      let obj = {}
      if (!isEmpty(this.sites)) {
        this.sites.forEach(site => {
          obj[site.id] = site.name
        })
      }
      return obj
    },
    isSubForm() {
      return false
    },
    addRuleText() {
      return this.$t('forms.rules.add_new_rule')
    },
    fetchOnlySubformRules() {
      return false
    },
  },

  async created() {
    await this.getFormRulesList()
    this.$store.dispatch('loadSites')
  },

  methods: {
    getFieldsDisplayname(triggerFields) {
      return !isEmpty(triggerFields) ? triggerFields.join(',') : '---'
    },
    changeRuleStatus(rule) {
      let url = `v2/form/rule/changeRuleStatus`
      let data = {
        formRuleContext: { id: rule.id, status: rule.status },
      }
      API.post(url, data).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success('Updated Successfully')
        }
      })
    },
    async getFormRulesList() {
      this.isLoading = true
      let { moduleName, formId, formObj, isSubForm } = this
      let { error, data } = await API.get(
        'v2/form/rule/getRuleList',
        {
          moduleName,
          formId,
          fetchOnlySubformRules: isSubForm,
        },
        { force: true }
      )

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let { formRuleContexts = [] } = data
        if (!isEmpty(formRuleContexts)) {
          let { fields } = formObj || {}
          formRuleContexts = formRuleContexts.map(rules => {
            let { triggerFields } = rules || {}
            triggerFields = (triggerFields || []).map(field => field.fieldId)

            let modifiedTriggerFields = (fields || [])
              .filter(field => triggerFields.includes(field.id))
              .map(field => field.displayName)

            return { ...rules, triggerFields: modifiedTriggerFields }
          })
        }
        this.formRuleList = formRuleContexts || []
      }
      this.isLoading = false
    },
    showConfirmDelete(ruleObj) {
      let dialogObj = {
        title: 'Delete Form Rule',
        message: 'Are you sure you want to delete this rule?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          this.deleteForm(ruleObj)
        }
      })
    },
    async deleteForm(ruleObj) {
      let url = `v2/form/rule/delete`
      let data = {
        formRuleContext: { id: ruleObj.id },
      }
      let { error } = await API.post(url, data)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.getFormRulesList()
        this.$message.success('Successfully Deleted')
      }
    },
    editFormRuleCreation(rule) {
      this.isNew = false
      this.formRuleId = rule.id
      this.showFormRule = true
    },
    openFormRuleCreation() {
      this.isNew = true
      this.formRuleId = null
      this.showFormRule = true
    },
    redirect() {
      let { redirectToFormsList } = this
      if (isFunction(redirectToFormsList)) {
        redirectToFormsList()
      }
    },
    getSubFormName(id) {
      let { formObj } = this
      let { sections } = formObj || {}
      let subFormName = '---'
      sections.forEach(section => {
        let { subForm } = section || {}
        if (!isEmpty(subForm) && subForm.id === id) {
          subFormName = subForm.displayName || '---'
        }
      })
      return subFormName
    },
  },
}
</script>
