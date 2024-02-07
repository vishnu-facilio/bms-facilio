<template>
  <div :style="getCellStyle()">
    <data-bar-view
      v-if="displayType == 'data-bar'"
      :style="textBackgroundStyle"
      :value="value"
      :referenceValue="referenceValue"
      :visualConfig="visualConfig"
      :prop="prop"
      :pivotTable="pivotTable"
    ></data-bar-view>
    <compare-view
      v-else-if="displayType == 'compare'"
      :style="textBackgroundStyle"
      :value="value"
      :referenceValue="referenceValue"
      :visualConfig="visualConfig"
      :prop="prop"
      :pivotTable="pivotTable"
      :alignment="alignment"
    />
    <circle-view
      v-else-if="displayType == 'circle'"
      :style="textBackgroundStyle"
      :value="value"
      :referenceValue="referenceValue"
      :visualConfig="visualConfig"
      :prop="prop"
      :pivotTable="pivotTable"
    >
    </circle-view>
    <text-view
      v-else
      :style="textBackgroundStyle"
      :value="value"
      :prop="prop"
      :pivotTable="pivotTable"
    />
  </div>
</template>

<script>
import TextView from './TableCell/TextView.vue'
import DataBarView from './TableCell/DataBarView'
import CompareView from './TableCell/CompareView'
import CircleView from './TableCell/CircleView'
// compare
export default {
  props: [
    'textBackgroundStyle',
    'value',
    'prop',
    'pivotTable',
    'alignment',
    'visualConfig',
    'referenceValue',
  ],
  computed: {
    displayType() {
      return this.visualConfig.visualType
        ? this.visualConfig.visualType
        : 'text'
    },
  },
  components: {
    TextView,
    DataBarView,
    CompareView,
    CircleView,
  },
  methods: {
    getCellStyle() {
      let style = {}

      if (this.displayType == 'data-bar') return {}

      style['display'] = 'flex'
      if (this.alignment == 'right') {
        style['justify-content'] = 'flex-end'
      } else if (this.alignment == 'left') {
        style['justify-content'] = 'flex-start'
      } else {
        style['justify-content'] = 'space-around'
      }
      style['align-items'] = 'center'
      return style
    },
  },
}
</script>
