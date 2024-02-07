<template>
  <div class="pivot-add-criteria">
    <BaseDialogBox
      :visibility.sync="visibility"
      :onConfirm="save"
      :onCancel="closeDialog"
      :onClearAll="clearAll"
      cancelText="Cancel"
      confirmText="Save"
      :title="$t('pivot.conditionalFormatting')"
      width="52%"
    >
      <div class="dialog-content-body " slot="body" v-if="!loading">
        <el-collapse
          class="new-rule-collapse f13 position-relative controllogic-collapse"
          v-model="activeNames"
          :accordion="true"
        >
          <draggable
            :list="conditionalFormatting"
            :options="draggableOptions"
            @change="handleReorder"
          >
            <el-collapse-item
              class="rule-border-blue mT20 position-relative"
              style="border-left: 1px solid rgb(228, 235, 241);"
              v-for="(conditional, index) in conditionalFormatting"
              :key="index"
              :name="index"
            >
              <template slot="title">
                <div class="task-handle mR10 pointer">
                  <img src="~assets/drag-grey.svg" />
                </div>
                {{ $t('pivot.conditionalRule') }} {{ index + 1 }}
              </template>
              <div class="pL20">
                <div class="conditional-formatting">
                  <new-criteria-builder
                    class="graphics-criteria-builder"
                    :title="$t('pivot.specifyCondition')"
                    :exrule="conditional.criteria"
                    :key="refreshKey"
                    @condition="criteria => getCriteria(criteria, index)"
                    :showSiteField="true"
                    :module="'pivotbuilder'"
                    :variables="filteredVariables"
                  ></new-criteria-builder>
                  <div class="style-container pB20">
                    <el-row>
                      <label for="name" class="fc-modal-sub-title">{{
                        $t('pivot.formatting')
                      }}</label>
                      <div class="fc-sub-title-desc">
                        {{ $t('pivot.specifyStyles') }}
                      </div>
                    </el-row>
                    <el-row class="mT10" :gutter="10">
                      <el-col :span="7">
                        <div class="pivot-fc-input-label-txt mb5">
                          {{ $t('pivot.cellBackground') }}
                        </div>
                        <div class="fc-color-picker card-color-container">
                          <el-color-picker
                            v-model="conditional.styles.bgColor"
                            :predefine="getPredefinedColors()"
                          ></el-color-picker>
                        </div>
                      </el-col>
                      <el-col :span="7">
                        <div class="pivot-fc-input-label-txt mb5">
                          {{ $t('pivot.textBackground') }}
                        </div>
                        <div class="fc-color-picker card-color-container">
                          <el-color-picker
                            v-model="conditional.styles.textBgColor"
                            :predefine="getPredefinedColors()"
                          ></el-color-picker>
                        </div>
                      </el-col>
                      <el-col :span="7">
                        <div class="pivot-fc-input-label-txt mb5">
                          {{ $t('pivot.textColor') }}
                        </div>
                        <div class="fc-color-picker card-color-container">
                          <el-color-picker
                            v-model="conditional.styles.textColor"
                            :predefine="getPredefinedColors()"
                          ></el-color-picker>
                        </div>
                      </el-col>
                    </el-row>
                    <el-row class="mT20">
                      <el-col :span="11">
                        <span
                          class="pivot-fc-input-label-txt pB5"
                          style="font-weight: 500;"
                        >
                          {{ $t('pivot.displayValue') }}
                        </span>
                        <div>
                          <el-input
                            v-model="conditional.styles.displayValue"
                            class="fc-border-select"
                          ></el-input>
                        </div>
                      </el-col>
                      <el-col :span="11">
                        <span
                          class="pivot-fc-input-label-txt pB5 mL10"
                          style="font-weight: 500;"
                        >
                          {{ $t('pivot.textStyle') }}
                        </span>
                        <div>
                          <el-select
                            v-model="conditional.styles.textStyle"
                            class="fc-border-select mL10 width100"
                            :multiple="true"
                          >
                            <el-option
                              :label="$t('pivot.italic')"
                              value="italic"
                            >
                            </el-option>
                            <el-option :label="$t('pivot.bold')" value="bold">
                            </el-option>
                            <el-option
                              label="Strikethrough"
                              value="strike-through"
                            >
                            </el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>
                    <el-row class="mT20">
                      <el-col
                        :span="12"
                        v-if="conditional.styles.hasOwnProperty('blink')"
                      >
                        <div>
                          <el-checkbox v-model="conditional.styles.blink">{{
                            $t('pivot.blink')
                          }}</el-checkbox>
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </div>
                <img
                  v-if="conditionalFormatting.length > 1"
                  src="~assets/remove-icon.svg"
                  style="height:18px;width:18px;margin-right: 3px;"
                  @click="deleteRule(index)"
                  class="delete-icon pointer"
                />
              </div>
            </el-collapse-item>
          </draggable>
        </el-collapse>
        <el-button
          type="primary"
          class="fc-btn-green-medium-fill mT20 f14 fwBold"
          @click="addRule"
          icon="el-icon-plus"
          circle
        ></el-button>
      </div>
    </BaseDialogBox>
  </div>
</template>
<script>
import NewCriteriaBuilder from 'src/components/NewCriteriaBuilder'
import colors from 'charts/helpers/colors'
import { predefinedColors } from 'pages/card-builder/card-constants'
import { isEmpty, isUndefined } from '@facilio/utils/validation'
import BaseDialogBox from './BaseDialogBox.vue'
import draggable from 'vuedraggable'

export default {
  props: ['variables', 'visibility', 'alias', 'editConfig'],
  components: {
    NewCriteriaBuilder,
    draggable,
    BaseDialogBox,
  },
  data() {
    return {
      loading: false,
      predefinedColors,
      activeNames: 0,
      conditionalFormatting: [],
      draggableOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
      refreshKey: 0,
    }
  },

  mounted() {
    this.initConditionalFormatting()
  },

  watch: {
    visibility() {
      this.initConditionalFormatting()
    },
  },

  computed: {
    filteredVariables() {
      return this.variables
    },
  },
  methods: {
    rerender() {
      this.loading = true
      setTimeout(() => {
        this.loading = false
        this.cleardata()
      }, 500)
    },
    cleardata() {
      this.conditionalFormatting = []
      this.addRule()
    },
    clearAll(){
      this.conditionalFormatting = []
    },
    initConditionalFormatting() {
      if (!isEmpty(this.editConfig) && !isUndefined(this.editConfig)) {
        this.conditionalFormatting = JSON.parse(JSON.stringify(this.editConfig))
      } else {
        this.conditionalFormatting = []
        this.addRule()
      }
    },
    getCriteria(criteria, index) {
      if (this.conditionalFormatting[index]) {
        this.conditionalFormatting[index].criteria = criteria
      }
    },
    getPredefinedColors() {
      return colors.readingcardColors
    },
    deleteRule(key) {
      this.conditionalFormatting.splice(key, 1)
    },
    handleReorder() {
      this.refreshKey++
    },
    getEmptyCFObject() {
      let cf = {}
      cf.criteria = {
        pattern: '',
      }
      cf.styles = {}
      cf.styles = this.setAdditionalStats(cf.styles)
      return cf
    },
    setAdditionalStats(styles) {
      this.$set(styles, 'blink', false)
      this.$set(styles, 'displayValue', null)
      this.$set(styles, 'textStyle', 'normal')
      this.$set(styles, 'textBgColor', null)
      this.$set(styles, 'textColor', null)
      this.$set(styles, 'bgColor', null)
      return styles
    },
    addRule() {
      this.conditionalFormatting.push(this.getEmptyCFObject())
    },
    save() {
      this.$emit('conditionFormatAdded', {
        prop: this.alias,
        formatting: this.conditionalFormatting,
      })
      this.$emit('update:visibility', false)
      // this.visibility = false
    },
    closeDialog() {
      this.$emit('closeDialog')
    },
  },
}
</script>
<style lang="scss" scoped>
.pivot-fc-input-label-txt {
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.5px;
  color: #324056 !important;
  margin: 0;
  padding-bottom: 10px;
}
.fc-sub-title-desc {
  letter-spacing: 0.5px !important;
}
</style>
