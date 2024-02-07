<template>
  <el-dialog
    :visible.sync="showDialog"
    :fullscreen="true"
    custom-class="cc-dialog"
    :title="$t('common._common.view_settings')"
  >
    <div class="scrollable" style="padding: 30px; max-height: 85vh;">
      <draggable
        :options="{
          handle: '.icon-right',
          ghostClass: 'drag-ghost',
          dragClass: 'custom-drag',
          animation: 150,
        }"
        :list="viewList"
      >
        <div class="column-item" v-for="view in viewList" :key="view.name">
          {{ view.displayName }}
          <i class="fa fa-exchange icon-right" aria-hidden="true"></i>
        </div>
      </draggable>
    </div>
    <div slot="footer" class="row cc-dialog-footer">
      <button
        type="button"
        class="el-button col-6 uppercase ls9 f12 form-btn el-button--default"
        style="background-color: #f4f4f4;border: 0;border-left: 1px solid white;border-radius: 0px;padding: 18px;font-size: 13px;font-weight: 500;letter-spacing: 0.8px;text-align: center;color: #5f5f5f;margin: 0;"
        @click="showDialog = false"
      >
        <span>{{ $t('common._common.cancel') }}</span>
      </button>
      <button
        type="button"
        class="el-button col-6 uppercase ls9 f12 form-btn  el-button--default"
        style="margin-left: 0px;color: white;background-color: #39b2c2;border: 0;border-radius: 0px;font-size: 13px;font-weight: 500;letter-spacing: 0.8px;text-align: center;color: #ffffff;"
        @click="save"
      >
        <span>{{ $t('common._common._save') }}</span>
      </button>
    </div>
  </el-dialog>
</template>

<script>
import { mapState } from 'vuex'
import draggable from 'vuedraggable'

export default {
  props: ['visible'],
  data() {
    return {
      showDialog: this.visible,
      viewList: [],
    }
  },
  components: {
    draggable,
  },
  computed: {
    ...mapState({
      views: state => state.view.views,
    }),
  },
  mounted() {
    this.viewList = this.$helpers.cloneObject(this.views)
  },
  watch: {
    visible(val, oldVal) {
      this.showDialog = val
    },
    showDialog(val, oldVal) {
      this.$emit('update:visible', val)
    },
    views(val) {
      this.viewList = this.$helpers.cloneObject(val)
    },
  },
  methods: {
    save() {
      let self = this
      this.$store
        .dispatch('view/customizeView', {
          moduleName: 'workorder',
          views: self.viewList.map((view, index) => {
            return { id: view.id, sequenceNumber: index + 1, name: view.name }
          }),
        })
        .then(() => {
          self.showDialog = false
        })
    },
  },
}
</script>

<style>
.el-dialog.cc-dialog {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  overflow-y: hidden;
  border-radius: 0px;
  width: 30%;
  transition: transform 0.25s ease;
}

.cc-dialog .el-dialog__body {
  padding: 0;
}

.cc-dialog .el-dialog__header {
  padding-left: 30px;
}

.cc-dialog .el-dialog__footer {
  padding: 0;
}

.el-dialog__footer .cc-dialog-footer {
  position: absolute;
  bottom: 0px;
  width: 100%;
  z-index: 1000;
}

.cc-dialog .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #5a5e66;
}

.column-item {
  border: 1px solid #f1f3f5;
  padding: 8px 15px;
  margin-left: 0 !important;
  margin-bottom: 10px;
  width: 100%;
  position: relative;
}

.fixed-column {
  margin-bottom: 20px;
  border-bottom: 1px #d3d3d3 dashed;
}

.fa-exchange.icon-right {
  position: absolute;
  color: #b1a8a8;
  right: 0px;
  padding-right: 14px;
  top: 25%;
  cursor: move;
  cursor: -webkit-grabbing;
}

.drag-ghost {
  opacity: 0;
}

.custom-drag {
  background-color: #ffffff;
  color: #000;
}

.custom-drag .fa-exchange {
  color: #000;
}
</style>
