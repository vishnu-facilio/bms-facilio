<template>
  <div>
    <div class="fc-v1-rules-Cbig">
      <div class="fc-v1-rules-Cbig-sidebar">
        <el-header height="60" class="p20 pB0 fc-v1-rules-sidebar-header">
          <div class="flex-middle justify-content-space">
            <div
              class="fc-black-10 text-uppercase bold summary-back"
              @click="$router.push({ path: 'all' })"
            >
              <i class="el-icon-back mR10"></i>
              {{ $t('common._common.back_list') }}
            </div>
            <div class="rules-search-ico pointer" style="visibility: hidden;">
              <i class="el-icon-search"></i>
            </div>
          </div>
        </el-header>
        <div class="fc-v1-rules-Cbig-sidebar-scroll">
          <div
            class="pT20 pB10 fc-text-pink12 bold pL30 text-uppercase text-left"
          >
            {{ $t('servicecatalog.setup.category') }}
          </div>
          <div class="fc-v1-rules-sidelist-block">
            <div
              class="fc-v1-rules-sidelist-item"
              :class="{ listActive: active_el_all == null }"
              @click="
                filterTemplates(null, 2)
                activate(null, 2)
                selectedTree = 1
              "
            >
              {{ $t('common._common.all') }}
            </div>
            <div
              class="fc-v1-rules-sidelist-item"
              :class="{ listActive: active_el_all == value }"
              v-for="value in assetCategories"
              :key="value"
              @click="
                filterTemplates(value, 2)
                activate(value, 2)
                selectedTree = 2
              "
            >
              {{ value }}
            </div>
          </div>
          <div class="fc-text-pink12 text-uppercase bold pL30 pT20 p20 pB10">
            {{ $t('common._common.recommended') }}
          </div>
          <div class="fc-v1-rules-sidelist-block">
            <div
              class="fc-v1-rules-sidelist-item"
              :class="{ listActive: active_el == null }"
              @click="
                filterTemplates(null, 1)
                activate(null, 1)
                selectedTree = 1
              "
            >
              {{ $t('common._common.all') }}
            </div>
            <div
              class="fc-v1-rules-sidelist-item"
              :class="{ listActive: active_el == value }"
              v-for="(templates, value) in categoryTemplatesList"
              :key="value"
              @click="
                filterTemplates(value, 1)
                activate(value, 1)
                selectedTree = 1
              "
            >
              {{ value }}
            </div>
          </div>
        </div>
      </div>
      <div class="fc-v1-rules-Cbig-main">
        <el-header height="80" class="pL0 pR0">
          <div class="flex-middle">
            <div style="width: 79.6% !important;">
              <el-input
                autofocus
                @keyup="quickSearch"
                v-model="quickSearchQuery"
                :placeholder="$t('common._common.search_custom_rules')"
                class="fc-rules-temp-search fc-input-full-border"
              ></el-input>
            </div>
            <div class="width10 pL20">
              <el-button class="fc-border-btn-green" @click="quickSearch">{{
                $t('common._common.search')
              }}</el-button>
            </div>
          </div>
        </el-header>
        <div class="fc-v1-rules-Cbig-main-scroll">
          <div
            v-if="Object.keys(filterRules).length < 1"
            class="height80vh flex-middle justify-content-center flex-direction-column"
          >
            <InlineSvg
              src="svgs/emptystate/alarmEmpty"
              iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
            ></InlineSvg>
            <div class="fc-black-dark f18 bold">
              {{ $t('common.products.no_rule_template_available') }}
            </div>
          </div>
          <div v-else class="alarm-template-body">
            <div>
              <div
                class="fc-black-16 mB20"
                v-for="(templates, category) in filterRules"
                :key="category"
              >
                <!-- {{ category }} -->
                <div class="alarm-template-grid-sec">
                  <div
                    class="alarm-template-grid scale-up-center"
                    v-for="item in templates"
                    :key="item.name"
                  >
                    <div
                      class="alarm-template-inner-body pointer"
                      @click="Retrieve(item, 'Summary')"
                    >
                      <div class="fc-black2-16 textoverflow-height-ellipsis4">
                        {{ item.name }}
                        <!-- {{item.originalTemplate.preRequsite.name}} -->
                      </div>
                      <div
                        class="fc-black-com f13 line-height20 mT10 fw4 textoverflow-height-ellipsis3"
                      >
                        {{ item.description }}
                        <!-- {{item.originalTemplate.preRequsite.description}} -->
                      </div>
                      <!-- <div class="fc-dark-blue-txt13 pointer pT10"  @click="templateSummary = true, selectedTemplate = item">
                  View template
                      </div>-->
                      <!-- <div class="position-absolute" style="bottom: 80px;">
                        <div class="fc-grey2 f13 letter-spacing0_5">
                          Threshold Metric
                        </div>
                        <div class="fc-black-com f13 mT5 fw4">
                          {{ item.threshold_metric_display }}
                        </div>
                      </div>-->
                    </div>
                    <div class="alarm-template-grid-footer flex-middle">
                      <el-button
                        class="fc-btn-grey-lg-border2 width100 border-left-right-bottom-none f11 bold"
                        @click="
                          Retrieve(item, 'apply')
                          ;(selectedTemplate = item), (checked = true)
                        "
                        >{{ $t('common.dashboard.apply_template') }}</el-button
                      >
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog
      :visible.sync="dialogVisible"
      :title="$t('common.header.template_warning')"
      class="fc-tempdialog-center-container fc-dialog-center-container"
      width="40%"
    >
      <div class="height150">
        <el-row>
          <el-col :span="2">
            <div>
              <i
                v-if="title === 'Error'"
                class="el-icon-error label-txt-red f40"
              ></i>
              <i
                v-if="title === 'Warning'"
                class="el-icon-warning-outline warnig-icon f40"
              ></i>
            </div>
          </el-col>
          <el-col :span="21" class="pL10">
            <div>
              <div class="label-txt-black bold f16" v-if="msg[0]">
                {{ msg[0] }}
              </div>
              <div class="label-txt-black pT5" v-if="msg[1]">{{ msg[1] }}</div>
              <div class="label-txt-black" v-if="msg[2]">{{ msg[2] }}</div>
            </div>
          </el-col>
        </el-row>
        <div class="pT20" v-if="dialogViewer == 3">
          <el-checkbox v-model="checked" disabled="true"></el-checkbox
          >{{ $t('common.dialog.skip_unavailable_assets') }}
        </div>
        <div class="label-txt-black"></div>
      </div>

      <div slot="footer" class="dialpg-footer modal-dialog-footer">
        <el-button
          v-if="dialogViewer == 1"
          class="btn-grey-fill width100"
          type="primary"
          @click="dialogVisible = false"
          >{{ $t('common._common.canel') }}</el-button
        >
        <!-- <el-button
          v-else-if ="dialogViewer == 4"
          type="primary"
          @click="placeholdervisible()"
          class="fc-btn-green-lg-fill width100"
          >Proceed</el-button
        >-->
        <div v-else>
          <el-button
            class="modal-btn-cancel"
            type="primary"
            @click="dialogVisible = false"
            >{{ $t('common._common.cancel') }}</el-button
          >
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="placeholdervisible()"
            >{{ $t('common.header.proceed') }}</el-button
          >
        </div>
      </div>
    </el-dialog>
    <el-dialog
      :visible.sync="showPlaceholder"
      v-on:childToParent="onChildClick"
      width="35%"
      :append-to-body="true"
      class="fc-dialog-header-hide fc-dialog-approval-reject new-rule-template-dialog fc-dialog-center-body-p0"
    >
      <place-holder
        :ruleTemplate="selectedTemplate"
        :appliedDetail="ruleAppliedDetail"
        :checkedApply="checked"
        @childToParent="onChildClick"
      ></place-holder>
      <template-summary
        v-show="templateSummary"
        :visibility.sync="templateSummary"
        :template="selectedTemplate"
      ></template-summary>
    </el-dialog>
  </div>
</template>
<script>
import TemplateSummary from 'pages/firealarm/rules/TemplateSummary'
import PlaceHolder from 'pages/firealarm/rules/v1/PlaceHolder'
import TemplateHelper from 'pages/firealarm/rules/templateHepler'
export default {
  data() {
    return {
      input: '',
      showPlaceholder: false,
      templates: [],
      alltemplates: [],
      categoryTemplates: null,
      categoryTemplatesList: null,
      allcategoryTemplates: null,
      allcategoryTemplatesList: null,
      selectedCategory: null,
      selectedTemplate: null,
      value: '',
      usedTemplate: {},
      customRuleDialog: false,
      templateSummary: false,
      active_el: -1,
      active_el_all: null,
      filterRules: [],
      quickSearchQuery: '',
      ruleAppliedDetail: null,
      checked: true,
      alreadyUsed: false,
      dialogVisible: false,
      allAppliedCaseDialog: false,
      itemused: null,
      viewPartialAssets: false,
      viewPartialAssetsChild: false,
      dialogViewer: 0,
      msg: [],
      title: '',
      assetCategories: ['AHU', 'FAHU', 'FCU'],
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  components: {
    TemplateSummary,
    PlaceHolder,
  },
  mixins: [TemplateHelper],
  mounted() {
    // this.loadTemplates()
    this.loadActiveTemplates()
  },
  methods: {
    onChildClick(value) {
      this.showPlaceholder = false
    },
    dialogMessage(selectedItem) {
      this.msg = []
      if (this.dialogViewer == 1) {
        if (selectedItem === 'apply') {
          this.dialogVisible = true
        }
        this.title = 'Error'
        this.msg[0] =
          'Rule cannot be applied since no readings for assets are available'
        this.msg[1] = 'Tip: Commission asset readings to apply rule'
      } else if (this.dialogViewer == 2) {
        this.placeholdervisible()
      } else if (this.dialogViewer == 3) {
        if (selectedItem === 'apply') {
          this.dialogVisible = true
        }
        this.title = 'Warning'
        this.msg[0] = 'Few assets have missing readings'
        this.msg[1] =
          'Asigned assets: Rule will be applied to ' +
          this.ruleAppliedDetail.available +
          ' assets'
        this.msg[2] =
          'Unassigned assets: Rule will be skipped for ' +
          this.ruleAppliedDetail.unavailable +
          ' assets'
      } else if (this.dialogViewer == 4) {
        if (selectedItem === 'apply') {
          this.dialogVisible = true
        }
        this.title = 'Warning'
        this.msg[0] = 'Rule template in use'
        this.msg[1] = 'Do you want to create a duplicate of the rule?'
      } else if (this.dialogViewer == 5) {
        if (selectedItem === 'apply') {
          this.dialogVisible = true
        }
        this.msg[0] =
          'Rule template cannot be applied since the required readings for assets are not available'
        this.msg[1] = 'Do you want to create duplicate of the rule'
      }
    },
    createrule() {
      this.createRule(this.itemused)
    },
    activate: function(el, val) {
      if (val === 1) {
        this.active_el = el
        this.active_el_all = -1
      }
      if (val === 2) {
        this.active_el_all = el
        this.active_el = -1
      }
    },
    placeholdervisible() {
      if (this.checked == true) {
        this.showPlaceholder = true
        this.dialogVisible = false
      } else {
        this.$message.error(
          this.$t('common._common.rule_cannot_be_applied_without_skipping')
        )
        this.dialogVisible = true
      }
    },
    Retrieve(para, selectedItem) {
      let params = {
        category: para.category,
        Fields: para.field_metric,
      }
      let url = '/v2/ruleTemplate/getAssetAvailabilityCount'
      this.$http
        .post(url, {
          params,
        })
        .then(response => {
          this.alreadyUsed = false
          this.ruleAppliedDetail = response.data.result.availability

          this.assetsIds = response.data.result.assets

          if (this.usedTemplate[para.id]) {
            this.alreadyUsed = true
            this.dialogViewer = 4
            if (
              this.ruleAppliedDetail.available ==
              this.ruleAppliedDetail.totalAssets
            ) {
              this.dialogViewer = 5
            }
          } else {
            if (
              this.ruleAppliedDetail.unavailable ==
              this.ruleAppliedDetail.totalAssets
            ) {
              this.dialogViewer = 1
            } else if (
              this.ruleAppliedDetail.available ==
              this.ruleAppliedDetail.totalAssets
            ) {
              this.itemused = para
              this.dialogViewer = 2
            } else {
              //this.showPlaceholder = true
              this.dialogViewer = 3
            }
          }
          if (selectedItem === 'apply') {
            this.dialogMessage(selectedItem)
          }
          if (selectedItem === 'Summary') {
            this.dialogMessage(selectedItem)
            this.openTemplateSummary(para)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
    },
    quickSearch() {
      let string = this.quickSearchQuery
      if (string) {
        Object.keys(this.filterRules).forEach(key => {
          this.filterRules[key] = this.filterRules[key].filter(function(cust) {
            return cust.name.toLowerCase().indexOf(string.toLowerCase()) >= 0
          })
        })
      } else {
        if (this.selectedTree === 1) {
          this.filterRules = this.$helpers.cloneObject(this.categoryTemplates)
        } else {
          this.filterRules = this.$helpers.cloneObject(
            this.allcategoryTemplates
          )
        }
      }
    },
    openTemplateSummary(item) {
      item.dialogViewer = this.dialogViewer
      item.message = this.msg
      item.message.title = this.title
      item.ruleId = this.usedTemplate[item.id]
      // this.$store.setter.ruleTemplate.setSelectedTemplate(item)
      this.$store.dispatch('ruleTemplate/setSelectedTemplate', item)
      this.$store.dispatch(
        'ruleTemplate/setSelectedTemplateNew',
        this.ruleAppliedDetail
      )
      this.$router.push({
        path: 'templates/' + item.id + '/summary',
      })
    },
    loadTemplates() {
      let url = `/v2/ruleTemplate/recommendedList`
      this.$http.get(url).then(response => {
        if (response.data) {
          this.templates = []
          this.allTemplates = []
          response.data.result.templates.recommended.forEach(d => {
            d.json.fdd_rule.id = d.id
            this.templates.push(d.json.fdd_rule)
          })
          response.data.result.templates.all.forEach(d => {
            d.json.fdd_rule.id = d.id
            this.allTemplates.push(d.json.fdd_rule)
          })
          this.alltemplates = this.$helpers.cloneObject(this.allTemplates)
          this.categoryTemplatesList = this.groupByField(
            this.templates,
            'category'
          )

          this.allcategoryTemplatesList = this.groupByField(
            this.allTemplates,
            'category'
          )
          this.categoryTemplates = this.$helpers.cloneObject(
            this.categoryTemplatesList
          )
          this.allcategoryTemplates = this.$helpers.cloneObject(
            this.allcategoryTemplatesList
          )
          this.filterRules = this.$helpers.cloneObject(
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
            this.$message.success(
              this.$t('common._common.rule_created_successfully')
            )
            this.$router.push({
              path: 'active',
            })
          }
        })
        .catch(error => {
          this.$message.success(this.$t('common.dialog.something_went_wrong'))
        })
    },
    filterTemplates(selectedCategoryParameter, val) {
      this.selectedCategory = selectedCategoryParameter
      this.categoryTemplates = this.$helpers.cloneObject(
        this.categoryTemplatesList
      )
      this.allcategoryTemplates = this.$helpers.cloneObject(
        this.allcategoryTemplatesList
      )
      if (val === 1) {
        if (this.selectedCategory) {
          Object.keys(this.categoryTemplatesList)
            .filter(key => key !== this.selectedCategory)
            .forEach(key => delete this.categoryTemplates[key])
          this.filterRules = this.$helpers.cloneObject(this.categoryTemplates)
        } else {
          this.filterRules = this.$helpers.cloneObject(this.categoryTemplates)
        }
      }
      if (val === 2) {
        if (this.selectedCategory) {
          Object.keys(this.allcategoryTemplatesList)
            .filter(key => key !== this.selectedCategory)
            .forEach(key => delete this.allcategoryTemplates[key])
          this.filterRules = this.$helpers.cloneObject(
            this.allcategoryTemplates
          )
        } else {
          this.filterRules = this.$helpers.cloneObject(
            this.allcategoryTemplates
          )
        }
      }
    },
  },
}
</script>
<style lang="scss">
.fc-v1-rules-Cbig-main-scroll {
  .alarm-template-body {
    padding: 0px 10px 30px 0px;
  }

  .alarm-template-inner-body {
    padding: 20px 20px;
  }

  .alarm-template-grid {
    width: 234px;
    max-width: 100%;
    height: 200px;
    max-height: 100%;
    box-shadow: 0 2px 7px 0 rgba(223, 226, 232, 0.5);
    background-color: #ffffff;
    margin-right: 20px;
    margin-bottom: 30px;
    position: relative;
    border: 1px solid #ebedf4;
    overflow: hidden;

    &:hover {
      box-shadow: 0 8px 29px 0 rgba(15, 17, 48, 0.09);
      -webkit-box-shadow: 0 8px 29px 0 rgba(15, 17, 48, 0.09);
      -moz-box-shadow: 0 8px 29px 0 rgba(15, 17, 48, 0.09);

      .alarm-template-grid-footer {
        transition: 0.25s;
        transition-timing-function: ease-out;
        transform: translateY(0);
        opacity: 1;

        .fc-btn-grey-lg-border2 {
          background-color: #39b2c2 !important;
          color: #fff !important;
          border-color: #39b2c2 !important;
        }
      }
    }

    .alarm-template-grid-footer {
      width: 100%;
      position: absolute;
      bottom: 0;
      opacity: 0;
      text-align: center;
      font-size: 11px;
      font-weight: 500;
      letter-spacing: 1px;
      text-align: center;
      color: #666666;
      cursor: pointer;
      transform: translateY(10%);
      -webkit-transform: translateY(10%);
      -moz-transform: translateY(10%);
      transition-timing-function: ease-in;
      -webkit-transition-timing-function: ease-in;
      -moz-transition-timing-function: ease-in;
    }
  }
}
</style>
