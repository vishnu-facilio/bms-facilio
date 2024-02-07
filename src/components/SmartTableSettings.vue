<template>
  <div class="smart-table-conditionalformatting">
    <el-tabs type="border-card" class="mT30">
      <el-tab-pane
        label="Conditional formatting"
        v-if="!conditionalFormattingLoading"
      >
        <div
          v-for="(condition, index) in column.conditionalFormatting"
          :key="index"
          class="relative criteria-box"
        >
          <el-row class="mT10 maxWidth90" :gutter="10">
            <el-col :span="8">
              <div class="fc-input-label-txt mb5"></div>
              <div>
                <el-input
                  v-model="column.label"
                  :disabled="true"
                  class="fc-input-full-border2"
                ></el-input>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="fc-input-label-txt mb5"></div>
              <div>
                <el-select
                  v-model="condition.critera.operatorId"
                  class="fc-input-full-border-select2"
                  @change="condition.critera.key = column.key"
                >
                  <el-option
                    v-for="(value, key) in $constants.OPERATORS[
                      column.datatype
                    ]"
                    :key="key"
                    :label="key"
                    :value="value.operatorId"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="fc-input-label-txt mb5"></div>
              <div>
                <el-input
                  v-model="condition.critera.value"
                  class="fc-input-full-border2"
                ></el-input>
              </div>
            </el-col>
            <el-col :span="8" class="p10">
              <p>color</p>
              <el-color-picker
                :predefine="getPredefinedColors()"
                v-model="condition.style.color"
              ></el-color-picker>
            </el-col>
            <el-col :span="8" class="p10">
              <p>Bg color</p>
              <el-color-picker
                :predefine="getPredefinedColors()"
                v-model="condition.style.bgcolor"
              ></el-color-picker>
            </el-col>
            <template v-if="condition.action">
              <el-col
                :span="8"
                class="p10"
                v-if="condition.action.hasOwnProperty('blink')"
              >
                <p>Blink</p>
                <el-checkbox v-model="condition.action.blink"></el-checkbox>
              </el-col>
              <el-col
                :span="8"
                class="p10"
                v-if="condition.action.hasOwnProperty('blinkValue')"
              >
                <p>Blink value</p>
                <el-checkbox
                  v-model="condition.action.blinkValue"
                ></el-checkbox>
              </el-col>
            </template>
          </el-row>
          <el-row
            class="mT10 maxWidth90 p10"
            :gutter="10"
            v-if="
              condition.hasOwnProperty('display') &&
                condition.display.hasOwnProperty('displayValue')
            "
          >
            <el-col :span="12">
              <p>Display value</p>
              <!-- <el-input v-model="condition.display.displayValue" class="fc-input-full-border2 emoji-input"></el-input> -->
              <emoji-picker @emoji="emojiinsert" :search="search">
                <template v-slot:emoji-invoker="{ events }">
                  <div class="emoji-invoker" v-on="events">
                    <el-input
                      v-model="condition.display.displayValue"
                      class="fc-input-full-border2 emoji-input"
                    >
                      <template v-slot:suffix>
                        <i class="fa fa-smile-o input-emoji-icon "></i>
                      </template>
                    </el-input>
                  </div>
                </template>
                <template v-slot:emoji-picker="{ emojis }">
                  <el-popover
                    popper-class="emoji-popover"
                    width="250"
                    v-model="visible"
                    placement="right"
                    trigger="click"
                  >
                    <div>
                      <div>
                        <el-input
                          v-model="search"
                          class="fc-input-full-border2"
                        ></el-input>
                      </div>
                      <div class="emoji-content">
                        <div
                          v-for="(emojiGroup, category) in emojis"
                          :key="category"
                        >
                          <h5>{{ category }}</h5>
                          <div>
                            <span
                              v-for="(emoji, emojiName) in emojiGroup"
                              :key="emojiName"
                              @click="emojiinsert(emoji, index)"
                              :title="emojiName"
                              >{{ emoji }}</span
                            >
                          </div>
                        </div>
                      </div>
                    </div>
                  </el-popover>
                </template>
              </emoji-picker>
            </el-col>
          </el-row>
          <div class="contion-action-btn pointer">
            <img
              src="~assets/add-icon.svg"
              style="height:18px;width:18px;"
              class="add-icon"
              v-if="column.conditionalFormatting.length - 1 === index"
              @click="addContions"
            />
            <img
              src="~assets/remove-icon.svg"
              style="height:18px;width:18px;margin-right: 3px;margin-left: 3px;margin-top: 9px;"
              class="delete-icon"
              @click="deleteCondition(index)"
            />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import colors from 'charts/helpers/colors'
import DateHelper from '@/mixins/DateHelper'
import EmojiPicker from 'vue-emoji-picker'
export default {
  props: ['column', 'data', 'settings'],
  mixins: [DateHelper],
  data() {
    return {
      conditionalFormattingLoading: false,
      visible: true,
      search: '',
      coditionalObject: {
        critera: {
          key: null,
          operatorId: null,
          value: null,
        },
        action: {
          blink: false,
        },
        style: {
          color: '#000',
          bgcolor: '#fff',
        },
      },
    }
  },
  components: {
    EmojiPicker,
  },
  mounted() {
    this.conditionalFormattingLoading = true
    if (this.column && !this.column.label) {
      this.column.label = 'Value'
    }
    if (
      this.column.conditionalFormatting &&
      !this.column.conditionalFormatting.length
    ) {
      if (this.settings) {
        let obj = this.$helpers.cloneObject(this.settings)
        this.column.conditionalFormatting.push(obj)
      } else {
        this.column.conditionalFormatting.push(this.coditionalObject)
      }
    } else {
      this.$set(this.column, 'conditionalFormatting', [])
      this.column.conditionalFormatting.push(this.coditionalObject)
    }
    this.conditionalFormattingLoading = false
  },
  computed: {},
  methods: {
    emojiinsert(emoji, index) {
      this.column.conditionalFormatting[index].display.displayValue += emoji
    },
    setConditionData() {},
    getPredefinedColors() {
      return colors.readingcardColors
    },
    addContions() {
      this.conditionalFormattingLoading = true
      let obj = this.$helpers.cloneObject(this.coditionalObject)
      if (this.settings) {
        obj = this.$helpers.cloneObject(this.settings)
      }
      this.column.conditionalFormatting.push(obj)
      this.conditionalFormattingLoading = false
    },
    deleteCondition(index) {
      this.column.conditionalFormatting.splice(index, 1)
      if (!this.column.conditionalFormatting.length) {
        this.addContions()
      }
    },
  },
}
</script>

<style>
.smart-table-conditionalformatting .el-tabs__content {
  overflow: unset;
}

.maxWidth90 {
  max-width: 90%;
}

.contion-action-btn {
  position: absolute;
  right: 0;
  top: 45px;
  z-index: 10;
}
.emoji-popover {
  height: 250px;
  left: 170px;
  top: 40px;
}
.emoji-content {
  height: 200px;
  overflow: auto;
}
.input-emoji-icon {
  font-size: 16px;
  top: 10px;
  position: relative;
  cursor: pointer;
}
.emoji-input .el-input__inner {
  padding-right: 30px !important;
}
.contion-action-btn2 .add-icon {
  position: absolute;
  right: 35px;
  top: 10px;
}
</style>
