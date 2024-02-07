<template>
  <div class="height100 overflow-y-scroll pB50 alarm-rules-template-page">
    <div class="alarm-template-container">
      <div class="alarm-template-header">
        <div class="fL">
          <div class="summary-back" @click="$router.push({ path: 'all' })">
            <i class="el-icon-back mR10"></i>
            {{ $t('common._common.back_list') }}
          </div>
          <div class="heading-black22 mT10">
            <span class="heading-black22">Rule Templates</span>
            <el-select
              v-model="selectedCategory"
              clearable
              placeholder="Select"
              @change="filterTemplates()"
              class="mL10 fc-input-full-border-h35"
            >
              <el-option
                :key="null"
                :label="'All Category'"
                :value="null"
              ></el-option>
              <el-option
                v-for="(templates, value) in categoryTemplatesList"
                :key="value"
                :label="value"
                :value="value"
              ></el-option>
            </el-select>
          </div>
        </div>
        <div class="fR">
          <!-- <el-button-group class="fc-btn-group-pink-ho mR10">
            <el-button type="primary">
              <InlineSvg src="list" iconClass="icon icon-md vertical-middle alarm-rule-icon-list"></InlineSvg>
            </el-button>
            <el-button type="primary">
              <InlineSvg src="gallery" iconClass="icon icon-md vertical-middle alarm-rule-icon-gallery"></InlineSvg>
            </el-button>
          </el-button-group>-->
          <el-button
            class="fc-create-btn mT10 pT12 pB12"
            @click="$router.push({ path: `/app/fa/rule/new` })"
            >NEW CUSTOM RULE</el-button
          >
        </div>
      </div>
      <!-- template body -->
      <div class="alarm-template-body">
        <div class>
          <div
            class="fc-black-16 mB20"
            v-for="(templates, category) in categoryTemplates"
            :key="category"
          >
            {{ category }}
            <div class="alarm-template-grid-sec">
              <div
                class="alarm-template-grid"
                v-for="item in templates"
                :key="item.name"
              >
                <div
                  class="alarm-template-inner-body pointer"
                  @click="openTemplateSummary(item)"
                >
                  <div class="fc-black2-16">
                    {{ item.name }}
                    <!-- {{item.originalTemplate.preRequsite.name}} -->
                  </div>
                  <div class="fc-black-com f13 line-height20 mT10 fw4">
                    {{ item.description }}
                    <!-- {{item.originalTemplate.preRequsite.description}} -->
                  </div>
                  <!-- <div class="fc-dark-blue-txt13 pointer pT10"  @click="templateSummary = true, selectedTemplate = item">
                  View template
                  </div>-->
                  <div class="position-absolute" style="bottom: 80px;">
                    <div class="fc-grey2 f13 letter-spacing0_5">
                      Threshold Metric
                    </div>
                    <div class="fc-black-com f13 mT5 fw4">
                      {{ item.threshold_metric_display }}
                    </div>
                  </div>
                </div>
                <div class="alarm-template-grid-footer flex-middle">
                  <el-button
                    class="fc-btn-grey-lg-border2 width100 border-left-right-bottom-none f11 bold"
                    @click="
                      ;(showPlaceholder = true), (selectedTemplate = item)
                    "
                    >APPLY TEMPLATE</el-button
                  >
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <el-dialog
      :visible.sync="showPlaceholder"
      width="35%"
      :append-to-body="true"
      class="fc-dialog-header-hide fc-dialog-approval-reject new-rule-template-dialog fc-dialog-center-body-p0"
    >
      <place-holder
        :ruleTemplate="selectedTemplate"
        :ruleId="selectedTemplate ? usedTemplate[selectedTemplate.id] : null"
      ></place-holder>
    </el-dialog>
    <template-summary
      v-show="templateSummary"
      :visibility.sync="templateSummary"
      :template="selectedTemplate"
    ></template-summary>
    <!-- <new-rule v-if="customRuleDialog" :visibility.sync="customRuleDialog" @closed="closeNewDialog"></new-rule> -->
  </div>
</template>
<script>
// import NewRule from 'pages/firealarm/rules/NewRule'
import TemplateHelper from 'pages/firealarm/rules/templateHepler'
import PlaceHolder from '@/templates/PlaceHolder'
import TemplateSummary from 'pages/firealarm/rules/TemplateSummary'
export default {
  components: {
    // NewRule,
    TemplateSummary,
    PlaceHolder,
  },
  mixins: [TemplateHelper],
  data() {
    return {
      showPlaceholder: false,
      templates: [],
      categoryTemplates: null,
      categoryTemplatesList: null,
      selectedCategory: null,
      selectedTemplate: null,
      value: '',
      usedTemplate: {},
      customRuleDialog: false,
      templateSummary: false,
      radio: '1',
      input: '',
    }
  },
  mounted() {
    // this.loadTemplates()
    this.loadActiveTemplates()
  },
  methods: {
    openTemplateSummary(item) {
      item.ruleId = this.usedTemplate[item.id]
      // this.$store.setter.ruleTemplate.setSelectedTemplate(item)
      this.$store.dispatch('ruleTemplate/setSelectedTemplate', item)
      this.$router.push({ path: 'templates/' + item.id + '/summary' })
    },
    loadTemplates() {
      let url = `/v2/rules/template/fetchDefaultRules`
      this.$http.get(url).then(response => {
        if (response.data) {
          this.templates = []
          response.data.result.templates.forEach(d => {
            d.json.fdd_rule.id = d.id
            this.templates.push(d.json.fdd_rule)
          })
          this.categoryTemplatesList = this.groupByField(
            this.templates,
            'category'
          )
          this.categoryTemplates = this.$helpers.cloneObject(
            this.categoryTemplatesList
          )
        }
      })
    },
    loadActiveTemplates() {
      let url = `/v2/rules/template/fetchActiveTemplates`
      this.$http.get(url).then(response => {
        if (response.data) {
          // this.usedTemplate = response.data.result.ruleTemplateRel
          response.data.result.ruleTemplateRel.forEach(d => {
            this.usedTemplate[d.defaultTemplateId] = d.ruleId
          })
          this.loadTemplates()
        }
      })
    },
    createRule(item) {
      if (item.placeHolder) {
        Object.keys(item.placeHolder).forEach(key => {
          if (item.placeHolder[key].displayType === 'ASSETCHOOSER') {
            item.placeHolder[key].default_value = null
          }
          console.log(item.placeHolder[key])
        })
      }
      let params = {
        id: item.id,
        placeHolder: item.placeHolder,
      }
      let url = `/v2/rules/template/createRule`
      this.$http
        .post(url, params)
        .then(response => {
          if (response.data.responseCode === 0 && response.data.result) {
            this.$message.success('Rule created successfully')
            this.$router.push({
              path: 'active',
            })
          }
        })
        .catch(error => {
          this.$message.success('Something went wrong')
        })
    },
    filterTemplates() {
      this.categoryTemplates = this.$helpers.cloneObject(
        this.categoryTemplatesList
      )
      if (this.selectedCategory) {
        Object.keys(this.categoryTemplatesList)
          .filter(key => key !== this.selectedCategory)
          .forEach(key => delete this.categoryTemplates[key])
      }
    },
  },
}
</script>

<style lang="scss">
.alarm-rules-template-page {
}
.new-rule-template-dialog {
  .el-dialog__body {
    padding: 0;
  }
  .rules-sum-header {
    line-height: 20px;
  }
}
</style>
