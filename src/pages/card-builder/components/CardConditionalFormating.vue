<template>
  <div class="conditional-formatting p10" v-if="!loading">
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
        <div class="pT20">
          <div class="conditional-formatting">
            <new-criteria-builder
              class="graphics-criteria-builder"
              title="Specify conditions"
              :exrule="conditional.criteria"
              :index="index"
              @condition="getCriteria"
              :showSiteField="true"
              :module="'cardbuilder'"
              :variables="filteredVariables"
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
                  :span="7"
                  v-if="conditional.styles.hasOwnProperty('primaryColor')"
                >
                  <div class="fc-input-label-txt mb5">Primary</div>
                  <div class="fc-color-picker card-color-container">
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="conditional.styles.primaryColor"
                      @change="emitData"
                    ></el-color-picker>
                  </div>
                </el-col>
                <el-col
                  :span="7"
                  v-if="conditional.styles.hasOwnProperty('secondaryColor')"
                >
                  <div class="fc-input-label-txt mb5">Secondary</div>
                  <div class="fc-color-picker card-color-container">
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="conditional.styles.secondaryColor"
                      @change="emitData"
                    ></el-color-picker>
                  </div>
                </el-col>
                <el-col
                  :span="7"
                  v-if="conditional.styles.hasOwnProperty('arrowUpColor')"
                >
                  <div class="fc-input-label-txt mb5">Arrow Up color</div>
                  <div class="fc-color-picker card-color-container">
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="conditional.styles.arrowUpColor"
                      @change="emitData"
                    ></el-color-picker>
                  </div>
                </el-col>
                <el-col
                  :span="7"
                  v-if="conditional.styles.hasOwnProperty('arrowDownColor')"
                >
                  <div class="fc-input-label-txt mb5">Arrow Down color</div>
                  <div class="fc-color-picker card-color-container">
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="conditional.styles.arrowDownColor"
                      @change="emitData"
                    ></el-color-picker>
                  </div>
                </el-col>
                <el-col
                  :span="7"
                  v-if="conditional.styles.hasOwnProperty('backgroundColor')"
                >
                  <div class="fc-input-label-txt mb5">BG</div>
                  <div class="fc-color-picker card-color-container">
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="conditional.styles.backgroundColor"
                      @change="emitData"
                    ></el-color-picker>
                  </div>
                </el-col>
                <el-col
                  :span="12"
                  v-if="
                    conditional.styles.hasOwnProperty('colors') &&
                      ['gauge_layout_1', 'gauge_layout_3'].includes(cardLayout)
                  "
                >
                  <p class="fc-input-label-txt pB5">Gauge Colors</p>
                  <div class="mT5 card-color-container">
                    <div
                      v-for="(color, index) in conditional.styles.colors"
                      :key="index"
                      class="mR10 fc-color-picker card-color-container"
                    >
                      <el-color-picker
                        v-model="color.hex"
                        :key="'' + color.id + color.hex"
                        :predefine="predefinedColors"
                        @change="emitData"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </div>
                </el-col>
                <el-col
                  :span="12"
                  v-if="
                    conditional.styles.hasOwnProperty('color') &&
                      ['gauge_layout_2', 'gauge_layout_4'].includes(cardLayout)
                  "
                >
                  <p class="fc-input-label-txt pB5">Gauge Color</p>
                  <div class="mT5">
                    <div class="mR10 fc-color-picker card-color-container">
                      <el-color-picker
                        v-model="conditional.styles.color.hex"
                        @change="emitData"
                        :key="
                          '' +
                            conditional.styles.color.id +
                            conditional.styles.color.hex
                        "
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </div>
                </el-col>
                <el-col
                  :span="12"
                  v-if="
                    conditional.styles.hasOwnProperty('tickColor') &&
                      ['gauge_layout_3', 'gauge_layout_4'].includes(cardLayout)
                  "
                  class="mB50"
                >
                  <p class="fc-input-label-txt pB5">Tick Color</p>
                  <div class="mT5 card-color-container">
                    <div class="mR10 fc-color-picker card-color-container">
                      <el-color-picker
                        v-model="conditional.styles.tickColor.hex"
                        @change="emitData"
                        :key="
                          '' +
                            conditional.styles.tickColor.id +
                            conditional.styles.tickColor.hex
                        "
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </div>
                </el-col>
                <el-col
                  :span="12"
                  v-if="conditional.styles.hasOwnProperty('colorBarProgress')"
                >
                  <p class="fc-input-label-txt pB5">Bar start Color</p>
                  <div class="d-flex mT5 card-color-container">
                    <div class="mR10 fc-color-picker card-color-container">
                      <el-color-picker
                        @change="emitData"
                        v-model="conditional.styles.colorBarProgress"
                        :key="'' + conditional.styles.colorBarProgress"
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </div>
                </el-col>
                <el-col
                  :span="12"
                  v-if="
                    conditional.styles.hasOwnProperty('colorBarProgressEnd')
                  "
                >
                  <p class="fc-input-label-txt pB5">Bar end color</p>
                  <div class="d-flex mT5 card-color-container">
                    <div class="mR10 fc-color-picker card-color-container">
                      <el-color-picker
                        @change="emitData"
                        v-model="conditional.styles.colorBarProgressEnd"
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </div>
                </el-col>
                <el-col
                  :span="12"
                  v-if="conditional.styles.hasOwnProperty('blink')"
                >
                  <div class="mT40">
                    <el-checkbox
                      v-model="conditional.styles.blink"
                      @change="emitData"
                      >Blink</el-checkbox
                    >
                  </div>
                </el-col>
                <el-col
                  :span="12"
                  v-if="conditional.styles.hasOwnProperty('displayValue')"
                >
                  <p class="fc-input-label-txt pB5">Display value</p>
                  <div>
                    <el-input
                      v-model="conditional.styles.displayValue"
                      class="fc-border-select"
                    ></el-input>
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
      class="fc-btn-green-medium-fill mT20 f14 fwBold"
      @click="addRule"
      icon="el-icon-plus"
      circle
    ></el-button>
  </div>
</template>
<script>
import NewCriteriaBuilder from 'src/components/NewCriteriaBuilder'
import colors from 'charts/helpers/colors'
import { predefinedColors } from 'pages/card-builder/card-constants'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['value', 'cardStyles', 'variables', 'cardLayout'],
  components: {
    NewCriteriaBuilder,
  },
  data() {
    return {
      loading: false,
      predefinedColors,
      activeNames: ['0'],
      conditionalFormatting: [],
      styles: {
        primaryColor: '#110d24',
        secondaryColor: '#969caf',
        backgroundColor: '#FFF',
        tickColor: { id: 0, hex: '#969aa2' },
        color: { id: 0, hex: '#FF728E' },
        colors: [
          { id: 0, hex: '#ff7878' },
          { id: 1, hex: '#7d49ff' },
          { id: 2, hex: '#514dff' },
          { id: 3, hex: '#1eb9b7' },
        ],
        backgroundColors: [
          { id: 0, hex: '#fff' },
          { id: 0, hex: '#fff' },
        ],
        colorBarProgress: '#3adaad',
        colorBarProgressEnd: '#0232ab',
        arrowUpColor: '#008000',
        arrowDownColor: '#ff0100',
        blink: false,
        displayValue: null,
      },
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
  computed: {
    filteredVariables() {
      return this.variables.filter(rt => rt.name !== 'title')
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
      this.emitData()
    },
    initConditionalFormatting() {
      if (
        !isEmpty(this.$getProperty(this, 'value.conditionalFormatting', null))
      ) {
        let { value } = this
        this.conditionalFormatting = this.getExtraStyles(
          value.conditionalFormatting
        )
      } else {
        this.conditionalFormatting = []
        this.addRule()
      }
    },
    getExtraStyles(conditionalFormatting) {
      conditionalFormatting.forEach(rt => {
        let { styles } = rt
        if (!styles.hasOwnProperty('displayValue')) {
          this.$set(styles, 'displayValue', null)
        }
        if (!styles.hasOwnProperty('blink')) {
          this.$set(styles, 'blink', false)
        }
      })
      return conditionalFormatting
    },
    getCriteria(criteria, index) {
      this.conditionalFormatting[index].criteria = criteria
    },
    getPredefinedColors() {
      return colors.readingcardColors
    },
    deleteRule(key) {
      this.conditionalFormatting.splice(key, 1)
    },
    getEmptyCFObject() {
      let cf = {}
      cf.criteria = {
        pattern: '',
      }
      cf.styles = {}
      Object.keys(this.cardStyles).forEach(rt => {
        this.$set(cf.styles, rt, this.styles[rt])
      })
      cf.styles = this.setAdditionalStats(cf.styles)
      return cf
    },
    setAdditionalStats(styles) {
      this.$set(styles, 'blink', false)
      this.$set(styles, 'displayValue', null)
      return styles
    },
    addRule() {
      this.conditionalFormatting.push(this.getEmptyCFObject())
    },
    emitData() {
      let conditionalFormatting = {
        conditionalFormatting: this.conditionalFormatting,
      }
      this.$emit('input', conditionalFormatting)
    },
  },
}
</script>

<style></style>
