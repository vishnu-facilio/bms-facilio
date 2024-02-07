<template>
  <div class="functions-editor-container">
    <div class="editor-container">
      <script-editor
        v-model="scriptData"
        :scriptClass="'script-editor-container'"
        :diff="''"
      ></script-editor>
    </div>
    <div class="modal-dialog-footer" style="z-index: 900;">
      <el-button @click="close" class="modal-btn-cancel">CANCEL</el-button>
      <el-button type="primary" class="modal-btn-save" @click="save">
        Save
      </el-button>
    </div>
  </div>
</template>
<script>
import { ScriptEditor } from '@facilio/ui/setup'
import { isEmpty } from '@facilio/utils/validation'
export default {
  components: { ScriptEditor },
  props: ['script_action'],
  data() {
    return {
      actionObj: null,
      isSaved: false,
      scriptData: '',
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      if (!isEmpty(this.script_action)) {
        this.scriptData = this.script_action.action_meta.script
      }
    },
    save() {
      if (this.scriptData == '') {
        this.$message.error('Script is Empty')
        return
      }
      let list = {
        type: 5,
        actionType: 'script_action',
        action_meta: {
          actionId: -1,
          action_detail: {},
        },
        target_widgets: [],
      }
      list.action_meta.script = this.scriptData
      this.$emit('actions', list, {})
    },
    close() {
      if (!isEmpty(this.scriptData)) {
        this.$emit('closeScript', true)
      } else this.$emit('closeScript', false)
    },
  },
}
</script>
<style lang="scss" scoped>
.functions-editor-container {
  position: fixed;
  top: 22%;
  left: 15%;
  background: #f0f0f0;
  z-index: 20;
  height: 500px;
  width: 70%;
  display: flex;
  flex-direction: column;
  gap: 20px;

  .editor-container {
    box-sizing: border-box;
    height: 100%;
    padding: 10px 0;
    border-radius: 8px;
    display: flex;
    justify-content: center;
    align-items: center;
    background: #fff;
    .script-editor-container {
      width: 100%;
      height: 100%;
    }
  }
}
</style>
