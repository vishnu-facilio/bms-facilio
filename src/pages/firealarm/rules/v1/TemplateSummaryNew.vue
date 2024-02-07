<template>
  <div class="rules-template-body fc-v1-rules-template-body">
    <div class="width100">
      <div class="height100 pB50">
        <div class="alarm-template-container height100">
          <div class="alarm-template-header">
            <div class="fL">
              <div class="summary-back" @click="goBack()">
                <i class="el-icon-back mR10"></i>
                {{ $t('common._common.back_list') }}
              </div>
              <div class="heading-black22 mT10">
                <span class="heading-black22">{{ ruleTemplate.name }}</span>
              </div>
            </div>
            <div style="margin-left: auto;" class="mL10 fR">
              <el-button type="button" class="fc-btn-edit">
                <i class="el-icon-edit" @click="editThresholdRule()"></i>
              </el-button>
            </div>
          </div>
          <div class="fR rules-body-sum-right white-bg p20 mR20">
            <div v-if="ruleTemplate"></div>
            <place-holder
              v-if="placeHolderVisible"
              :ruleTemplate="ruleTemplate"
              :templateId="ruleTemplate.id"
              :dialogViewer="dialogViewer"
              :appliedDetail="assetDetail"
              @childToParent="onChildClick"
            ></place-holder>
            <div v-else>
              <div class="warnig-icon bold f16">
                <i class="el-icon-warning-outline f16 fwBold pR5"></i>
                {{ title }}
              </div>
              <div class="label-txt-black pT10 bold">{{ message[0] }}</div>
              <div class="pT10">{{ message[1] }}</div>
              <div class="">{{ message[2] }}</div>
              <div
                v-if="
                  dialogViewer === 3 || dialogViewer === 4 || dialogViewer === 5
                "
              >
                <div v-if="dialogViewer === 3">
                  <el-checkbox v-model="checked" disabled
                    >Skip unavailable assets</el-checkbox
                  >
                </div>
                <!-- <el-button @click="placeHolderVisible = true"
                  >Continue</el-button
                > -->
                <div slot="footer" class="dialpg-footer modal-dialog-footer">
                  <el-button
                    class="btn-grey-fill width100 rounded-none"
                    type="primary"
                    @click="placeHolderVisible = true"
                    >Cancel</el-button
                  >
                </div>
              </div>
            </div>
          </div>
          <page
            :key="null"
            :module="currentModuleName"
            :id="templateId"
            :details="ruleTemplate"
            :primaryFields="primaryFields"
          ></page>
          <!-- <new-rule v-if="thresholddialog" :rules="selectedRules" :ruleId="selectedRule" :isEditLoading="isEditLoading"
          :visibility.sync="thresholddialog" @onSaveRule="loadReadingRules"></new-rule>-->
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Page from '@/page/PageBuilder'
import PlaceHolder from 'pages/firealarm/rules/v1/PlaceHolder'
import TemplateHelper from 'pages/firealarm/rules/templateHepler'

export default {
  components: { Page, PlaceHolder },
  mixins: [TemplateHelper],

  created() {
    let params = {
      defaultWorkflowId: 4,
      paramList: [2],
    }
    this.$http
      .post(`/v2/workflow/getDefaultWorkflowResult`, { params })
      .then(response => (this.nothing = response))
  },
  mounted() {
    //this.loadRuleDetails()
    this.ruleTemplate = this.$store.state.ruleTemplate.selectedTemplate
    this.appendAssetDetails()
    if (this.ruleTemplate) {
      this.groupedPlaceHolder = this.placeHolderGroupFields()
    }
    this.placeHolderVisibility()
  },
  data() {
    return {
      nothing: null,
      showHistoryDialog: false,
      rule: null,
      thresholddialog: false,
      ruleOverviewLoading: false,
      isEditLoading: false,
      selectedRules: null,
      selectedRule: null,
      isLoading: false,
      currentModuleName: 'readingtemplate',
      primaryFields: [
        'name',
        'description',
        'category',
        'type',
        'siteId',
        'space',
        'qrVal',
      ],
      CardDetails: null,
      ruleTemplate: null,
      templates: [],
      selectedResourceList: [],
      groupedPlaceHolder: [],
      placeHolderVisible: false,
      dialogViewer: 0,
      message: [],
      title: null,
      checked: true,
    }
  },
  computed: {
    assetDetail() {
      return this.$store.state.ruleTemplate.selectedTemplateNew
    },
    templateId() {
      return parseInt(this.$route.params.id)
    },
  },
  watch: {
    ruleId: function() {
      this.loadRuleDetails()
    },
  },
  methods: {
    onChildClick() {
      this.placeHolderVisible = false
    },
    placeHolderVisibility() {
      this.message = this.ruleTemplate.message
      this.dialogViewer = this.ruleTemplate.dialogViewer
      this.title = this.ruleTemplate.message.title
      if (this.dialogViewer == 2) {
        this.placeHolderVisible = true
      } else {
        this.placeHolderVisible = false
      }
    },
    editThresholdRule() {
      this.getRuleContext()
    },
    getRuleContext() {
      let placeHolder = {}
      let index = 0
      Object.keys(this.groupedPlaceHolder).forEach(key => {
        this.groupedPlaceHolder[key].forEach((d, i) => {
          if (
            d.selection_criteria.displayType === 'ASSETCHOOSER' &&
            this.selectedResourceList.length > 0
          ) {
            let fieldName = this.isIncludeResource
              ? 'includeResource'
              : 'excludeResource'
            d.uniqueId = fieldName
            if (this.selectedResourceList.length > 0) {
              d.default_value = this.selectedResourceList.map(
                resource => resource.id
              )
            } else {
              d.default_value = null
            }
          }
          placeHolder[index] = d
          index++
        })
      })
      let params = {
        id: this.ruleTemplate.id,
        placeHolder: placeHolder,
      }
      // console.log('params' + params)
      let url = `/v2/rules/template/getRuleContext`
      this.$http
        .post(url, params)
        .then(response => {
          if (response.data.responseCode === 0 && response.data.result) {
            this.selectedRules = response.data.result
            this.selectedRule = -1
            this.thresholddialog = true
            this.$store.dispatch(
              'ruleTemplate/setRuleContext',
              this.selectedRules
            )
            let url = '/app/fa/rule/edit/template/' + this.ruleTemplate.id
            this.$router.replace({ path: url })
            this.$router.push()
            // this.$message.success('Rule created successfully')
            // this.$router.push({ path: `/app/fa/rules/active` })
          }
        })
        .catch(error => {
          // this.$message.success('Something went wrong')
        })
    },
    placeHolderGroupFields() {
      // this.checkFields()
      Object.keys(this.ruleTemplate.placeHolder).forEach(key => {
        this.templates.push(this.ruleTemplate.placeHolder[key])
      })
      return this.groupByField(this.templates, 'type')
    },
    loadReadingRules() {},
    appendAssetDetails() {
      this.ruleTemplate.assetDetail = this.assetDetail
    },
    goBack() {
      window.history.go(-1)
    },
    loadRuleDetails() {
      let self = this
      this.ruleOverviewLoading = true
      let params = { ruleId: this.ruleId, isSummary: true }
      self.$http
        .post('/v2/alarm/rules/fetchRule', params)
        .then(function(response) {
          self.rule = response.data.result
          self.ruleOverviewLoading = false
        })
    },
    changeRuleStatus(rule) {
      this.rule.alarmRule.preRequsite.status = !this.rule.alarmRule.preRequsite
        .status
      this.$util
        .changeRuleStatus('alarm', rule.preRequsite.id, rule.preRequsite.status)
        .then(response => {
          this.isLoading = false
          this.$message.success(
            (rule.preRequsite.status ? 'Activate' : 'Deactivate') +
              ' rule successfully.'
          )
        })
        .catch(function(error) {
          console.log(error)
        })
    },
  },
}
</script>
<style scoped>
.asset-details {
  flex-grow: 1;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.asset-details .asset-id {
  font-size: 12px;
  color: #39b2c2;
}
.asset-details .asset-name {
  font-size: 22px;
  color: #324056;
}
.asset-details .asset-space {
  font-size: 13px;
  color: #8ca1ad;
}
</style>
