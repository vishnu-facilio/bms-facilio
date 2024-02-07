<template>
  <div class="picker-container pB10">
    <div
      class="picker-color"
      v-for="tile in colors"
      :key="tile.label"
      :style="{ 'background-color': tile.color }"
      :class="{
        'active-color': tile.color == color.color,
      }"
      @click="changeColor(tile)"
    ></div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  model: {
    prop: 'color',
    event: 'click',
  },
  props: {
    color: {
      type: Object,
      required: true,
    },
  },
  created() {
    this.setDefaultColor()
  },
  data() {
    return {
      colors: [
        { label: 'blue', color: '#406dce', background:  '#FAFBFF' },
        { label: 'orange', color: '#e9913b', background: '#FFFDFB' },
        {
          label: 'olive-green',
          color: '#4a798c',
          background: '#F9FDFF',
        },
        { label: 'red', color: '#ff6c6a', background: '#FFF7F7' },
        { label: 'green', color: '#458462', background: '#F9FFFB'},
        { label: 'purple', color: '#bc5bb1', background: '#FFFAFE' },
        { label: 'voilet', color: '#644d9e', background: '#FBF9FF' },
        {
          label: 'light-green',
          color: '#70b599',
          background: '#F4FFFA',
        },
      ],
    }
  },
  methods: {
    setDefaultColor() {
      const self = this
      const selectedColor = this.colors.find(color => {
        return (color.label = self.color.label)
      })
      if (isEmpty(selectedColor)) {
        const [firstColor] = this.colors
        this.changeColor(firstColor)
      }
    },
    changeColor(color) {
      this.$emit('click', color)
    },
  },
}
</script>

<style lang="scss" scoped>
.picker-container {
  margin-top: 20px;
  padding-right: 62px;
  display: flex;
  justify-content: space-between;
}
.active-color {
  border: 3px solid #409eff;
  height: 25px;
  width: 25px;
  border-radius: 3px;
}
.picker-color {
  height: 36px;
  width: 36px;
  border-radius: 2px;
  cursor: pointer;
}
</style>
