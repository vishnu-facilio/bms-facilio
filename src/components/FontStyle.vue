<template>
  <div>
    <el-popover placement="bottom" title="STYLE" width="200" trigger="click">
      <div class="fonts-conatiner">
        <span class="fonts-title pull-left" style="padding-top: 6px;"
          >size</span
        >
        <el-input-number
          v-model="config.fontSize"
          :min="8"
          :max="20"
          size="mini"
        ></el-input-number>
      </div>
      <div class="fonts-conatiner">
        <span class="pull-left fonts-title">color</span>
        <el-color-picker
          v-model="config.color"
          show-alpha
          :predefine="colors.predefind"
          size="mini"
        >
        </el-color-picker>
      </div>
      <el-button slot="reference" size="mini">
        <div class="font-stlye-picker">
          <div>{{ config.fontSize }} px</div>
          <div
            class="color-box"
            :style="'background:' + config.color + ';'"
          ></div>
        </div>
      </el-button>
    </el-popover>
  </div>
</template>
<script>
import colors from 'charts/helpers/colors'
export default {
  porps: ['input', 'title'],
  data() {
    return {
      config: {
        fontSize: 10,
        color: '#ddd',
      },
      colors: colors,
    }
  },
  watch: {
    config: {
      handler(newData, oldData) {
        this.init(false)
      },
      deep: true,
    },
  },
  mounted() {
    if (this.input) {
      this.init()
    }
  },
  methods: {
    init(data) {
      this.$emit('output', this.config)
    },
  },
}
</script>
<style>
.font-stlye-picker {
  display: inline-flex;
}
.color-box {
  width: 30px;
  padding: 7px;
  margin-left: 10px;
}
.fonts-conatiner {
  margin: 10px;
  margin-left: 0;
  margin-right: 0;
}
.fonts-title {
  padding-right: 15px;
}
</style>
