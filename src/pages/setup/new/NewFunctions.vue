<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog fc-function-dialog-full new-fun-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div id="newfuntions">
      <div class="p10 con-continer row inherit function-container">
        <div class="fL pB20">
          <el-button
            @click="closeDialog"
            type="default"
            icon="el-icon-back"
            class="fc-cancel-new"
            >Back</el-button
          >
        </div>
        <div class="fR pB20">
          <!-- <el-button  @click="execute(data)" type="primary" icon="el-icon-s-tools" class="fc-create-btn uppercase">Execute</el-button> -->

          <el-button type="default" @click="showDiff = !showDiff">{{
            'Show Diff'
          }}</el-button>
          <el-button
            @click="updateFunction(data)"
            type="primary"
            class="fc-create-btn uppercase"
            >Update</el-button
          >
        </div>
      </div>
      <div
        class="p30 con-continer row width100 function-code-container height100"
      >
        <div style="width:100%">
          <div class="code-write width100" style="border-bottom: none">
            Code Write
          </div>
          <script-editor
            v-model="data.workflowUserFunction.workflowV2String"
            :scriptClass="'left-con p0  border-11 height100'"
            :diff="showDiff && { type: 'side_by_side' }"
          ></script-editor>
        </div>
        <!-- <div  style="border-left: none;width:40%" class="height100">
        <div class="code-write width100" style="border-bottom: none;padding-left:20px">Result Preview </div>
   <el-input
  type="textarea"
  id="resultBox"
  :rows="2"
  :disabled="true"
  style="border-left: none;"
  class="right-con p20  border-11 result-container height100"
  v-model="code.result">
</el-input>

    </div> -->
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { ScriptEditor } from '@facilio/ui/setup'
export default {
  props: ['visibility', 'data'],
  data() {
    return {
      code: {
        payload: 'void test(){\n    \n    \n}',
        result: '',
      },
      showDiff: false,
    }
  },
  mounted() {},
  components: { ScriptEditor },
  methods: {
    addFunction(data) {
      console.log('fun', data)
      let params = {}
      params['test'] = { workflowV2String: this.code.payload, isV2Script: true }
      params[data.uniquename]['nameSpaceId'] = data.workflowNameSpace.id
      this.$http
        .post('/v2/workflow/addUserFunction', params)
        .then(() => {})
        .catch(function(error) {
          if (error) {
            console.log(error)
          }
        })
    },
    updateFunction(data) {
      let params = {}
      let functionObj = data.workflowUserFunction
      params['userFunction'] = {
        id: functionObj.id,
        workflowV2String: functionObj.workflowV2String,
        nameSpaceId: data.workflowNameSpace.id,
        isV2Script: true,
      }
      this.$http
        .post('/v2/workflow/updateUserFunction', params)
        .then(response => {
          if (this.iserrorFreeResponse(response)) {
            this.closeDialog(response)
            this.$emit('saved')
          } else {
            this.showerrorBanners(response)
          }
        })
        .catch(function(error) {
          if (error) {
            console.log(error)
          }
        })
    },
    showerrorBanners(response) {
      if (
        response?.data?.result?.workflowSyntaxError?.errors &&
        response.data.result.workflowSyntaxError.errors.length
      ) {
        let { errorsAsString } =
          response.data.result.workflowSyntaxError || 'Error'
        this.$message({
          showClose: true,
          message: `  ${errorsAsString}  `,
          type: 'error',
          duration: 100000,
        })
      }
    },
    iserrorFreeResponse(response) {
      if (
        response?.data?.result?.workflowSyntaxError?.errors &&
        response.data.result.workflowSyntaxError.errors.length
      ) {
        return false
      }
      return true
    },
    execute() {
      this.setResult('')
      this.$http
        .post('/v2/workflow/runWorkflow', {
          workflow: { workflowV2String: this.code.payload, isV2Script: true },
        })
        .then(response => {
          if (response.data.result.workflow.logString) {
            console.log(response.data.result.workflow.logString)
            this.setResult(response.data.result.workflow.logString)
          }
        })
        .catch(function(error) {
          if (error) {
            console.log(error)
          }
        })
    },
    closeDialog() {
      this.$emit('update:visibility', false)
      this.$emit('close', true)
    },
  },
}
</script>
<style>
.fc-function-dialog-full {
  width: 70% !important;
}
.function-container {
  height: 80px;
  border-bottom: 1px solid #ededed;
  padding: 20px;
  padding-left: 30px;
  padding-right: 30px;
}
.new-fun-dialog .el-dialog__body,
#newfuntions,
#newfuntions .CodeMirror,
#newfuntions .vue-codemirror,
.result-container textarea#resultBox {
  height: 100%;
}
.function-code-container {
  height: calc(100% - 150px);
  overflow: auto;
}
.code-write {
  color: #333333;
  font-size: 11px;
  letter-spacing: 1px;
  font-weight: bold;
  text-transform: uppercase;
  background: #f9fafc;
  border: 1px solid #e6ecf3;
  padding: 10px;
  padding-left: 35px;
}
.fc-cancel-new {
  border-radius: 3px;
  box-shadow: 0px 1px 5px 0 #ededed;
  background-color: #fff;
  padding: 10px 14px;
  font-weight: bold;
  font-size: 12px;
  border-color: #ededed;
  text-transform: uppercase;
}
</style>
