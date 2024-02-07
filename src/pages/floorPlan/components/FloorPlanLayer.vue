<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    title="SELECT THE FLOOR"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog25 setup-dialog show-right-dialog"
    :before-close="closeDialog"
    :append-to-body="true"
    style="z-index: 999999"
  >
    <div class="floor-plan-side-bar">
      <div class="">
        <el-tabs type="card" v-model="activeTab">
          <el-tab-pane label="Layers" name="layer">
            <span slot="label">Layers</span>
            <el-collapse v-model="activeSelections">
              <template v-for="(layer, index) in layers">
                <el-collapse-item
                  :key="index"
                  :title="layer.name"
                  :name="layer.name"
                  class="fc-graphic-legend-collapse"
                >
                  <div
                    class="pointer p5"
                    v-for="(sublayer, idx) in layer.sublayers"
                    :key="idx"
                    @click="setlayer(layer, sublayer)"
                  >
                    {{ sublayer.name }}
                  </div>
                </el-collapse-item>
              </template>
            </el-collapse>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['floorPlan', 'visibility', 'controlCategoryList'],
  data() {
    return {
      activeTab: 'layer',
      activeSelections: [''],
    }
  },
  computed: {
    layers() {
      return [
        {
          name: 'Default',
          value: 'default',
          sublayers: [
            ...this.controlCategoryList,
            ...[{ name: 'Control', _name: 'CONTROL' }],
          ],
        },
      ]
    },
  },
  mounted() {},
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    setlayer(layer, sublayer) {
      let layerInfo = {
        layer: layer.value,
        sublayer: sublayer._name,
      }
      this.$emit('layerInfo', layerInfo)
      this.closeDialog()
    },
  },
}
</script>
