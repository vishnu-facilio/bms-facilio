<template>
  <div
    class="rule-basic-info-container rule-basic-info-container d-flex flex-direction-column"
  >
    <div class="position-relative">
      <div class="header f12 bold mT30 mL70 mR30 d-flex">
        <div>
          <div class="text-uppercase">{{ $t('rule.create.root_cause') }}</div>
          <div class="fc-heading-border-width43 mT15"></div>
        </div>
      </div>
      <div class="rule-condition-from-content rule-basic-info-content">
        <div class="rule-info-form">
          <div class="rca-rule-table" v-if="rcaSelectObj.length > 0">
            <div class="flex-middle justify-content-space">
              <div class="fc-text-pink text-uppercase">
                {{ $t('common.header.root_cause_list') }}
              </div>
              <div class="pointer" @click="showRuleDialog = true">
                <inline-svg
                  src="svgs/add-circled"
                  class="vertical-middle"
                  iconClass="icon icon-sm icon-add mR5"
                ></inline-svg>
                <span class="btn-text f12 bold green-txt-13 text-uppercase">{{
                  $t('common.header.add_root_cause')
                }}</span>
              </div>
            </div>
            <el-table
              :data="rcaSelectObj"
              class="impact-form-table"
              :fit="true"
              style="width: 100%;"
              height="500"
            >
              <el-table-column prop="label" width="700">
                <template v-slot="rule">
                  <div class="label-txt-black">{{ rule.row.label }}</div>
                </template>
              </el-table-column>
              <el-table-column
                prop="label"
                label
                width="100"
                class="visibility-visible-actions"
              >
                <template v-slot="rule">
                  <div
                    @click="deleteRca(rule.row.value)"
                    class="text-right visibility-visible-actions"
                  >
                    <img
                      src="~assets/bin-1.svg"
                      class="mT10 visibility-hide-actions"
                      width="15"
                      height="15"
                    />
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div v-else class="rule-basic-info-content">
            <div class="d-flex flex-direction-column text-center">
              <inline-svg
                src="svgs/emptystate/data-empty"
                iconClass="icon text-center icon-xxxxlg"
              ></inline-svg>
              <div class="mT10 empty-text f15 bold">
                {{ $t('common.header.no_root_cause_available_here') }}
              </div>
              <div class="mT5 empty-text-desc f13">
                {{ $t('common.header.no_root_cause_available_here') }}
              </div>
              <div class="inline-block mT20">
                <el-button
                  class="pT10 pB10 small-border-btn text-uppercase pL15 pR15"
                  @click="showRuleDialog = true"
                  >{{ $t('common.header.add_root_cause') }}</el-button
                >
              </div>
            </div>
          </div>
          <div class="modal-dialog-footer">
            <el-button
              @click="goToPrevious"
              type="button"
              class="modal-btn-cancel"
              >{{ $t('common._common.previous') }}</el-button
            >
            <el-button
              @click="moveToNext"
              type="button"
              :loading="isSaving"
              class="modal-btn-save"
            >
              {{ $t('common.header.proceed_to_next') }}
              <img
                src="~assets/arrow-pointing-white-right.svg"
                width="17px"
                class="fR"
              />
            </el-button>
            <!-- </el-form-item> -->
          </div>
          <rca-rule-cnf-dialog
            :visibility.sync="showRuleDialog"
            :ruleObj="ruleIds"
            :rcaRuleList="ruleList"
            :categoryId="categoryId"
            :sharedData="sharedData"
            @addRcaRule="associateRcaRule"
          ></rca-rule-cnf-dialog>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import RcaRuleCnfDialog from 'pages/alarm/rule/component/RcaRuleCnfDialog'

export default {
  components: {
    RcaRuleCnfDialog,
  },
  props: {
    sharedData: {
      type: Object,
    },
  },
  computed: {
    isEdit() {
      return this.$route.name === 'rule-creation-edit'
    },
    categoryId() {
      return this.sharedData.preRequsite
        ? this.sharedData.preRequsite.assetCategoryId
        : null
    },
    ruleIds() {
      return this.sharedData.alarmRCARules
    },
  },
  created() {},
  data() {
    return {
      ruleList: null,
      category: {},
      selectedMetric: null,
      rcaSelectObj: [],
      showRuleDialog: false,
      module: 'readingRule',
      isSaving: false,
      isLoading: false,
      rcaRuleID: null,
    }
  },
  mounted() {
    this.loadCategoryRules()
  },
  methods: {
    moveToNext() {
      this.sharedData.rcaSelectObj = this.rcaSelectObj
      this.$emit('nextStep', this.sharedData)
    },
    goToPrevious() {
      this.$emit('goToPreviousStep', null)
    },
    associateRcaRule(selectedObj) {
      this.rcaSelectObj = []
      // selectedObj.forEach(id => {
      //   this.rcaSelectObj.push(this.ruleList.filter(d => d.value === id))
      // })
      selectedObj.forEach(id => {
        this.rcaSelectObj.push({ label: this.ruleList[id], value: id })
      })

      this.rcaRuleID = selectedObj
      this.showRuleDialog = false
    },
    deleteRca(id) {
      let deteleIndex
      this.rcaSelectObj.forEach((d, i) => {
        if (d.value === id) {
          deteleIndex = i
        }
      })
      this.rcaSelectObj.splice(deteleIndex, 1)
    },
    loadCategoryRules() {
      let params = { categoryId: this.categoryId }
      this.$http.post('/v2/alarm/rules/rcaRules', params).then(response => {
        this.ruleList = response.data.result.rules
        if (this.sharedData.alarmRCARules.length > 0) {
          this.sharedData.alarmRCARules.forEach(id => {
            this.rcaSelectObj.push({ label: this.ruleList[id], value: id })
            // this.rcaSelectObj.push(this.ruleList.filter(d => d.value === dds))
          })
        }
      })
    },
  },
}
</script>

<style lang="scss">
.f-webform-container {
  &.reporting-container {
    border: 1px solid #ebedf4;

    .section-container {
      padding: 0 100px 30px 50px;
      border: none;
    }
  }
}
.impact-form-table table {
  width: 100% !important;
}
</style>
