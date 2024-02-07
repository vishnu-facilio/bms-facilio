<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog assignment-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <error-banner
      :error.sync="error"
      :errorMessage.sync="errorText"
    ></error-banner>
    <div>
      <el-form
        :model="workflowRuleContext"
        :label-position="'top'"
        ref="ruleForm"
      >
        <!-- header -->
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{
                isNew
                  ? $t('common.products.new_assigment_rule')
                  : $t('common.products.edit_assignment_rule')
              }}
            </div>
          </div>
        </div>

        <div class="new-body-modal">
          <el-row :gutter="20">
            <el-col :span="24">
              <p class="fc-input-label-txt pB10">
                {{ $t('common.products.assignment_name') }}
              </p>
              <el-form-item prop="name">
                <el-input
                  id="rulename"
                  :autofocus="true"
                  v-model="workflowRuleContext.name"
                  class="fc-input-full-border2 width100"
                  :placeholder="$t('common.products._new_assignment_rule')"
                >
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <p class="fc-input-label-txt pB10">
                {{ $t('setup.approvalprocess.description') }}
              </p>
              <el-form-item prop="description">
                <el-input
                  v-model="workflowRuleContext.description"
                  type="textarea"
                  :placeholder="$t('common._common.enter_desc')"
                  autoComplete="off"
                  :autosize="{ minRows: 4, maxRows: 4 }"
                  resize="none"
                  class="fc-input-full-border-select2 width100"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt pB1">
                {{ $t('common.products.site') }}
              </p>
              <Lookup
                v-model="workflowRuleContext.siteId"
                :field="fields.site"
                :hideLookupIcon="true"
                @recordSelected="setSelectedValue"
                @showLookupWizard="showLookupWizardSite"
              ></Lookup>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24" class="mT20">
              <div class="transaction-form-criteria-builder-label">
                {{ $t('setup.users_management.criteria') }}
              </div>
              <div class="fc-sub-title-desc">
                {{ $t('setup.setup.specify_rules') }}
              </div>
              <CriteriaBuilder
                v-model="workflowRuleContext.criteria"
                :moduleName="moduleName"
                ref="criteria-builder"
              />
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <p class="fc-input-label-txt pB10">
                {{ $t('common.wo_report.assign_to_') }}
              </p>
              <div class="fc-border-input-div2">
                <span>{{ getTeamStaffLabel(assignmentTemplate) }}</span>
                <span style="float: right;padding-right: 12px;">
                  <img
                    class="svg-icon team-down-icon"
                    src="~assets/down-arrow.svg"
                  />
                </span>
              </div>
              <f-assignment
                :model="assignmentTemplate"
                :siteId="$getProperty(workflowRuleContext, 'siteId', null)"
                viewtype="form"
              ></f-assignment>
              <div></div>
            </el-col>
          </el-row>
          <!-- </el-main> -->
        </div>
        <!-- </el-container> -->
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            type="primary"
            class="modal-btn-save"
            :loading="saving"
            @click="save('ruleForm')"
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
import { mapState } from 'vuex'
import FAssignment from '@/FAssignment'
import { CriteriaBuilder } from '@facilio/criteria'
import ErrorBanner from '@/ErrorBanner'
import { Lookup } from '@facilio/ui/forms'
import TeamStaffMixin from '@/mixins/TeamStaffMixin'
const fields = {
  site: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'site',
    field: {
      lookupModule: {
        name: 'site',
        displayName: 'Sites',
      },
    },

    multiple: false,
    additionalParams: {
      orderBy: 'spaceType',
      orderType: 'asc',
    },
  },
}
export default {
  props: ['visible', 'assignmentRule', 'isNew', 'visibility', 'rule'],
  mixins: [TeamStaffMixin],
  data() {
    return {
      fields,
      moduleMeta: null,
      callmail: false,
      callsms: false,
      errorText: '',
      module: 'alarm',
      moduleName: 'workorder',
      saving: false,
      error: false,
      workflowRuleContext: {
        name: '',
        description: '',
        siteId: null,
        criteria: null,
        executionOrder: 1,
        status: true,
        event: {
          moduleName: 'workorder',
          activityType: 3,
        },
      },
      assignmentTemplate: {
        assignmentGroup: {
          id: -1,
        },
        assignedTo: {
          id: -1,
        },
      },
    }
  },
  components: {
    Lookup,
    FAssignment,
    CriteriaBuilder,
    ErrorBanner,
  },
  computed: {
    ...mapState({
      users: state => state.users,
      site: state => state.site,
    }),
    showDialog() {
      return this.visible
    },
  },
  methods: {
    validation(rules) {
      this.error = false
      this.errorText = ''
      if (!rules.name) {
        this.errorText = this.$t('common.header.please_enter_name')
        this.error = true
      } else if (!rules.criteria) {
        this.errorText = this.$t('common.header.Please fill the criteria')
        this.error = true
      } else {
        this.errorText = ''
        this.error = false
      }
    },
    showLookupWizardSite(field, canShow) {
      canShow = false
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizardSite', canShow)
    },
    setSelectedValue(selectedValues, field) {
      selectedValues
      field
    },
    save(ruleForm) {
      this.$refs[ruleForm].validate(async valid => {
        let { workflowRuleContext } = this
        this.validation(workflowRuleContext)
        if (this.error) {
          return
        }

        let criteriaValidation = await this.$refs[
          'criteria-builder'
        ]?.validate()
        if (!valid && !criteriaValidation) return

        this.saving = true

        if (!workflowRuleContext.siteId) {
          workflowRuleContext.siteId = -1
        }
        let assignTemplate = null
        if (
          this.assignmentTemplate.assignmentGroup.id > 0 ||
          this.assignmentTemplate.assignedTo.id > 0
        ) {
          assignTemplate = {
            assignedGroupId: this.assignmentTemplate.assignmentGroup.id,
            assignedUserId: this.assignmentTemplate.assignedTo.id,
          }
        }
        this.$util
          .addOrUpdateSlaOrAssignmentRule(
            'workorder',
            {
              rule: workflowRuleContext,
              assignmentTemplate: assignTemplate,
            },
            !this.isNew,
            !this.isNew ? 'updateassignment' : 'addassignment'
          )
          .then(rule => {
            this.$emit('update:visibility', false)
            this.$emit('onsave', rule)
            this.saving = false
            this.$refs[ruleForm].resetFields()
          })
      })
    },
    close() {
      this.$emit('update:visible', false)
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },

    resetForm() {
      this.$refs['ruleForm'].resetFields()
    },
    cancel() {
      this.$emit('canceled')
    },
    showCreateNewDialog() {
      if (this.$route.query.create) {
        return true
      }
      return false
    },
    backto() {
      this.$router.push({ name: 'assignmentrules' })
    },
  },
  created() {
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadGroups')
  },
  mounted() {
    if (!this.isNew) {
      this.$helpers.copy(this.workflowRuleContext, this.assignmentRule)
      this.workflowRuleContext.id = this.assignmentRule.id
      if (
        this.assignmentRule.actions &&
        this.assignmentRule.actions[0].template
      ) {
        this.assignmentTemplate.assignmentGroup.id = this.assignmentRule.actions[0].template.assignedGroupId
        this.assignmentTemplate.assignedTo.id = this.assignmentRule.actions[0].template.assignedUserId
      }
    } else {
      if (this.site.length === 1) {
        this.workflowRuleContext.siteId = this.site[0].id
      }
    }
  },
}
</script>
<style>
main.el-main.indent {
  padding-top: 0;
}
@media screen and (max-width: 1280px) and (min-width: 800px) {
  .assignment-dialog {
    width: 55% !important;
  }
}
</style>
