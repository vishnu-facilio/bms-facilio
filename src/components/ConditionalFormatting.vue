<template>
  <div class="conditional-formatting p10">
    <el-collapse
      class="new-rule-collapse position-relative controllogic-collapse"
      v-model="activeNames"
      :accordion="true"
    >
      <el-collapse-item
        class="rule-border-blue mT20 position-relative"
        style="border-left: 1px solid rgb(228, 235, 241);"
        v-for="(conditional, index) in conditionalFormatting"
        :key="index"
        :name="index"
      >
        <template slot="title"> Conditional Rule {{ index + 1 }} </template>
        <div class="">
          <div class="conditional-formatting">
            <new-criteria-builder
              class="graphics-criteria-builder"
              title="Specify conditions"
              :exrule="conditional.criteria"
              :index="index"
              @condition="getCriteria"
              :showSiteField="true"
              :module="'graphicsbuilder'"
              :variables="variables"
            ></new-criteria-builder>
            <div class="style-container pB20">
              <el-row>
                <label for="name" class="fc-modal-sub-title">Formatting</label>
                <div class="fc-sub-title-desc">
                  Specify styles for the conditions
                </div>
              </el-row>
              <el-row class="mT10" :gutter="10">
                <el-col
                  :span="8"
                  v-if="conditional.actions.hasOwnProperty('blink')"
                >
                  <div class="mT40">
                    <el-checkbox
                      v-model="conditional.actions.blink"
                      @change="emitData"
                      >Blink</el-checkbox
                    >
                  </div>
                </el-col>
                <el-col
                  :span="8"
                  v-if="conditional.actions.hasOwnProperty('hide')"
                >
                  <div class="mT35">
                    <el-checkbox
                      v-model="conditional.actions.hide"
                      @change="emitData"
                      >Hide</el-checkbox
                    >
                  </div>
                </el-col>
                <el-col
                  :span="8"
                  v-if="conditional.actions.hasOwnProperty('theme')"
                >
                  <div class="fc-input-label-txt mb5">Theme</div>
                  <div>
                    <el-select
                      v-model="conditional.actions.theme"
                      @change="emitData"
                      class="fc-input-full-border-select2"
                      :filterable="true"
                    >
                      <el-option
                        v-for="(theme, index) in getColorThemes()"
                        :key="index"
                        :label="theme.label"
                        :value="theme.key"
                      ></el-option>
                    </el-select>
                  </div>
                </el-col>
                <el-col
                  :span="6"
                  v-if="conditional.actions.hasOwnProperty('animateEffect')"
                >
                  <div class="fc-input-label-txt mb5">Animate Effect</div>
                  <div>
                    <el-select
                      v-model="conditional.actions.animateEffect"
                      @change="emitData"
                      class="fc-input-full-border-select2"
                      :filterable="true"
                    >
                      <el-option
                        v-for="(effect, index) in getAnimationEffects()"
                        :key="index"
                        :label="effect.label"
                        :value="effect.key"
                      ></el-option>
                    </el-select>
                  </div>
                </el-col>
              </el-row>
              <el-row class="mT15">
                <el-col
                  :span="7"
                  v-if="conditional.actions.styles.hasOwnProperty('fill')"
                >
                  <div class="fc-input-label-txt mb5">Color</div>
                  <div class="fc-color-picker">
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="conditional.actions.styles.fill"
                      @change="emitData"
                    ></el-color-picker>
                  </div>
                </el-col>
                <el-col
                  :span="7"
                  v-if="conditional.actions.styles.hasOwnProperty('fontColor')"
                >
                  <div class="fc-input-label-txt mb5">Font Color</div>
                  <div class="fc-color-picker">
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="conditional.actions.styles.fontColor"
                      @change="emitData"
                    ></el-color-picker>
                  </div>
                </el-col>
                <el-col
                  :span="7"
                  v-if="conditional.actions.styles.hasOwnProperty('stroke')"
                >
                  <div class="fc-input-label-txt mb5">Stroke</div>
                  <div class="fc-color-picker">
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="conditional.actions.styles.stroke"
                      @change="emitData"
                    ></el-color-picker>
                  </div>
                </el-col>
                <el-col
                  :span="7"
                  v-if="
                    conditional.actions.styles.hasOwnProperty('backgroundColor')
                  "
                >
                  <div class="fc-input-label-txt mb5">Background</div>
                  <div class="fc-color-picker">
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="conditional.actions.styles.backgroundColor"
                      @change="emitData"
                    ></el-color-picker>
                  </div>
                </el-col>
                <el-col
                  :span="7"
                  v-if="conditional.actions.styles.hasOwnProperty('fontWeight')"
                >
                  <div class="fc-input-label-txt mb5">Font weight</div>
                  <div>
                    <el-radio-group
                      v-model="conditional.actions.styles.fontWeight"
                      @change="emitData"
                    >
                      <el-radio-button
                        label="Bold"
                        value="bold"
                      ></el-radio-button>
                      <el-radio-button
                        label="Normal"
                        value=""
                      ></el-radio-button>
                    </el-radio-group>
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
    </el-collapse>
    <el-button
      type="primary"
      class="fc-btn-green-medium-fill mT20"
      @click="addRule"
      icon="el-icon-plus"
      circle
    ></el-button>
  </div>
</template>

<script>
import NewCriteriaBuilder from 'src/components/NewCriteriaBuilder'
import { themes } from 'pages/assets/graphics/ColorThemes'
import colors from 'charts/helpers/colors'

export default {
  props: ['variables', 'currentObject'],
  components: {
    NewCriteriaBuilder,
  },
  data() {
    return {
      activeNames: ['0'],
      isNew: true,
      selectedIndex: 0,
      styleObj: {
        text: {
          fill: '',
          backgroundColor: '',
          fontWeight: '',
        },
        square: {
          fill: '',
          stroke: '',
        },
        circle: {
          fill: '',
          stroke: '',
        },
        line: {
          stroke: '',
        },
        button: {
          fontColor: '',
          backgroundColor: '',
        },
      },
      conditionalFormatting: [],
    }
  },
  mounted() {
    this.initConditionalFormatting()
  },
  watch: {
    currentObject: function() {
      this.initConditionalFormatting()
    },
  },
  methods: {
    getColorThemes() {
      return themes[this.currentObject.themeGroup]
    },
    getAnimationEffects() {
      return [
        {
          label: 'None',
          key: 'none',
        },
        {
          label: 'Move Right Effect',
          key: 'move_right_effect',
        },
        {
          label: 'Move Left Effect',
          key: 'move_left_effect',
        },
        {
          label: 'Move Top Effect',
          key: 'move_top_effect',
        },
        {
          label: 'Move Bottom Effect',
          key: 'move_bottom_effect',
        },
      ]
    },
    getPredefinedColors() {
      return colors.readingcardColors
    },
    getCriteria(criteria, index) {
      this.conditionalFormatting[index].criteria = criteria
    },
    emitData() {
      this.$emit('conditionaldata', this.conditionalFormatting)
    },
    deleteRule(key) {
      this.conditionalFormatting.splice(key, 1)
    },
    initConditionalFormatting() {
      if (
        this.currentObject.conditionalFormatting &&
        this.currentObject.conditionalFormatting.length
      ) {
        this.conditionalFormatting = this.currentObject.conditionalFormatting
      } else {
        this.conditionalFormatting = []
        this.addRule()
      }
    },
    getEmptyCFObject() {
      let cf = {}
      cf.criteria = {
        pattern: '',
      }

      cf.actions = {}
      cf.actions.blink = false
      cf.actions.hide = false
      cf.actions.animateEffect = 'none'

      if (this.currentObject.theme) {
        cf.actions.theme = ''
      }

      cf.actions.styles = this.styleObj[this.currentObject.type]
        ? this.styleObj[this.currentObject.type]
        : {}

      return cf
    },
    addRule() {
      this.conditionalFormatting.push(this.getEmptyCFObject())
    },
  },
}
</script>

<style></style>
