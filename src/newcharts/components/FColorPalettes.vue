<template>
  <div>
    <div class="flex-middle">
      <p class="label-txt-black pT20 line-height20 mB10">Color Palette</p>
    </div>
    <div class="flex-middle">
      <div class="colorPalette-main" @click="openDialog">
        <div
          v-for="(color, idx) in Colors[chosenColors]"
          :key="idx"
          class="colorBlock"
          :style="{ background: color }"
        ></div>
      </div>
      <div @click="openDialog" class="flex-middle mL30 pB10 pointer">
        <div class="fc-link2 f11">Change</div>
        <InlineSvg
          src="svgs/painter-palette"
          class="vertical-middle pointer color-pallete"
          iconClass="icon icon-xs mL5"
          style="top: 2px;"
        ></InlineSvg>
      </div>
    </div>
    <el-dialog
      title="Colour palettes"
      width="35%"
      :visible.sync="showColorPalette"
      :append-to-body="true"
      class="fc-dialog-center-container color-pallete-lock"
      :lock-scroll="true"
    >
      <div style="height: 580px;">
        <el-row :gutter="20">
          <el-col
            :span="12"
            v-for="(colors, index) in Colors"
            :key="index"
            class="dialogcolorpalette mB10"
          >
            <!-- loading effect -->
            <div class="color-palette-loading-con mB10" v-if="loading">
              <div class="fc-animated-background width100px height20"></div>
              <div class="width100 height20 mT10 fc-animated-background"></div>
            </div>
            <div v-else>
              <div class="fc-input-label-txt f13 flex-middle position-relative">
                {{ formaterIndex(index) }}
                <div
                  class="selected-icon-color position-absolute"
                  v-if="chosenColors === index"
                >
                  <InlineSvg
                    src="svgs/check-icon"
                    class="vertical-middle"
                    iconClass="icon icon-xs mL10"
                  ></InlineSvg>
                </div>
              </div>
              <div
                class="colorPalette position-relative"
                @click="changeColor(index)"
              >
                <div
                  v-for="(color, idx) in colors"
                  :key="idx"
                  class="colorBlock"
                  :style="{ background: color }"
                ></div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import InlineSvg from '@/InlineSvg'
export default {
  props: ['colorIndex'],
  data() {
    return {
      Colors: {
        1: ['#1d7f01', '#6cb302', '#e9f501', '#fda504', '#fb5905'],
        2: ['#93d5ed', '#45a5f5', '#4285f4', '#2f5ec4', '#073a93'],
        3: ['#04b9bd', '#007e80', '#004852', '#f63700', '#fb6900'],
        4: ['#ffaf00', '#ff7300', '#ff0057', '#500083', '#0b0411'],
        5: ['#b0f400', '#74c700', '#00696d', '#002e5b', '#2e004d'],
        6: ['#00fff1', '#00c0d0', '#4c60a6', '#4d177c', '#4a0052'],
        7: ['#00effc', '#00b9e7', '#357ac9', '#793fad', '#980084'],
        8: ['#c5ff00', '#fef800', '#00daff', '#0050ff', '#1700ff'],
        9: ['#39678a', '#27916e', '#becd70', '#d7e1b2', '#9db4a4'],
        10: ['#eeebff', '#c0b8dd', '#9184ba', '#635198', '#341e75'],
        11: ['#e1f6de', '#a9d0b2', '#71a986', '#38835a', '#005c2e'],
        12: ['#ffed95', '#f0b277', '#e0775a', '#d13b3c', '#c1001e'],
        13: ['#eaf9a8', '#b1c5a0', '#779299', '#3d5e91', '#042a89'],
        14: ['#ffc300', '#eead09', '#de9812', '#cd821b', '#bc6c24'],
        15: ['#c5dcfb', '#6fe8f2', '#ecf365', '#f89c3e', '#f82422'],
      },
      chosenColors: 1,
      showColorPalette: false,
      loading: true,
      updateTimeout: null,
    }
  },
  mounted() {
    if (this.Colors[this.colorIndex]) {
      this.chosenColors = this.colorIndex
    }
  },
  watch: {
    colorIndex: {
      handler() {
        if (this.chosenColors !== this.colorIndex) {
          this.chosenColors = this.colorIndex
          this.$emit(
            'colorSelected',
            this.chosenColors,
            this.Colors[this.chosenColors]
          )
        }
      },
    },
  },
  components: {
    InlineSvg,
  },
  methods: {
    openDialog() {
      this.showColorPalette = true
      this.loading = true
      if (this.updateTimeout) {
        clearTimeout(this.updateTimeout)
      }
      this.updateTimeout = setTimeout(() => {
        this.loading = false
      }, 500)
    },
    changeColor(index) {
      this.chosenColors = index
      this.showColorPalette = false
      this.$emit(
        'colorSelected',
        this.chosenColors,
        this.Colors[this.chosenColors]
      )
    },
    formaterIndex(index) {
      if (index === '1') {
        return 'Default'
      } else {
        return 'Palette ' + index
      }
    },
  },
}
</script>
<style lang="scss">
.colorPalette,
.colorPalette-main {
  // width: 100%;
  /* background-color: #ffffff; */
  // background-color: #e4eaf0;
  position: relative;
  margin-bottom: 10px;
  flex-direction: row;
  display: flex;
  align-items: center;
  border-radius: 3px;
  cursor: pointer;
  border: 1px solid #e4eaf0;
  box-shadow: 0 2px 5px 0 rgba(198, 198, 198, 0.5);
  -webkit-box-shadow: 0 2px 5px 0 rgba(198, 198, 198, 0.5);
  -moz-box-shadow: 0 2px 5px 0 rgba(198, 198, 198, 0.5);
  &:hover {
    box-shadow: 1px 1px 7px 3px rgba(198, 198, 198, 0.5);
  }
}

.colorPalette-main {
  .colorBlock {
    width: 35px;
    height: 15px;
  }
}

.colorPalette .colorBlock {
  width: 46px;
  height: 25px;
  position: inline;
  cursor: pointer;
}
.colorBlock:first-child {
  border-top-left-radius: 3px;
  border-bottom-left-radius: 3px;
}
.colorBlock:last-child {
  border-top-right-radius: 3px;
  border-bottom-right-radius: 3px;
}
.colorPalette-selected {
  background: rgba(255, 255, 255, 0.3);
  position: absolute;
  width: 100%;
  height: 30px;
}
.colorPalette-selected .el-icon-check {
  color: rgba(0, 0, 0, 0.6);
  font-size: 14px;
  text-align: center;
  position: relative;
  left: 50%;
  top: 8px;
  font-weight: 600;
}
.color-pallete-lock .el-dialog {
  margin-top: 10vh !important;
}
.selected-icon-color {
  padding-left: 60px;
}
.color-pallete {
  fill: #6c6a91;
}
</style>
