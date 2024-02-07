<template>
  <el-dialog
    :visible.sync="showDialog"
    :fullscreen="true"
    custom-class="cc-dialog"
    title="View Settings"
    :append-to-body="true"
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
          <div v-if="view">{{ menu ? view.label : view.displayName }}</div>
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
        <span>Cancel</span>
      </button>
      <button
        type="button"
        class="el-button col-6 uppercase ls9 f12 form-btn  el-button--default"
        style="margin-left: 0px;color: white;background-color: #39b2c2;border: 0;border-radius: 0px;font-size: 13px;font-weight: 500;letter-spacing: 0.8px;text-align: center;color: #ffffff;"
        @click="save"
      >
        <span>Save</span>
      </button>
    </div>
  </el-dialog>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import draggable from 'vuedraggable'

export default {
  props: ['visible', 'moduleName', 'menu', 'reload'],
  data() {
    return {
      showDialog: this.visible,
      viewList: [],
      lists: {},
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
    if (!isEmpty(this.menu)) {
      this.viewList = this.$helpers.cloneObject(this.menu)
    } else {
      this.viewList = this.$helpers.cloneObject(this.views)
    }
  },
  watch: {
    visible(val) {
      this.showDialog = val
    },
    showDialog(val) {
      this.$emit('update:visible', val)
    },
    views(val) {
      this.viewList = this.$helpers.cloneObject(val)
    },
    menu(val) {
      this.viewList = this.$helpers.cloneObject(val)
    },
  },
  methods: {
    save() {
      this.$store
        .dispatch('view/customizeView', {
          moduleName: this.moduleName,
          views: this.viewList.map((view, index) => {
            return { id: view.id, sequenceNumber: index + 1, name: view.name }
          }),
        })
        .then(() => {
          this.showDialog = false
          if (this.reload) {
            this.$emit('onchange')
          }
        })
    },
  },
}
</script>
<style>
@import './../assets/styles/customization.css';
</style>
