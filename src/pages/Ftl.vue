<template>
  <div class="ftl">
    <el-row
      :gutter="20"
      v-if="
        $account.user.email.endsWith('@facilio.com') ||
          $account.user.email.startsWith('system+')
      "
    >
      <el-col class="ftl-col" :span="12">
        <div style="min-height:50vh">
          Template
          <el-input
            type="textarea"
            class="pT20"
            :autosize="{ minRows: 10 }"
            v-model="ftl"
          ></el-input>
        </div>
        <div style="min-height:50vh; padding-bottom: 100px;">
          Workflow
          <el-input
            type="textarea"
            class="pT20"
            :autosize="{ minRows: 10 }"
            v-model="workflow"
          ></el-input>
          Parameters
          <el-row
            :gutter="20"
            v-for="(param, index) in parameters"
            :key="index"
          >
            <el-col :span="6"><el-input v-model="param.key"></el-input></el-col>
            <el-col :span="15"
              ><el-input
                type="textarea"
                :autosize="{ minRows: 2 }"
                v-model="param.value"
              ></el-input
            ></el-col>
            <el-col :span="3">
              <span @click="addParam" v-if="index + 1 === parameters.length">
                <img src="~assets/add.svg" style="height:14px" />
              </span>
              &nbsp;&nbsp;
              <span v-if="parameters.length > 1" @click="removeParam(index)">
                <img src="~assets/remove.svg" style="height:14px;width:14px;" />
              </span>
            </el-col>
          </el-row>
          <el-row></el-row>
          <button
            type="button"
            class="footer-btn footer-btn-primary col-6"
            @click="parseFtl"
          >
            <span>Generate</span>
          </button>
        </div>
      </el-col>
      <el-col class="ftl-col" :span="12">
        <div v-html="html"></div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
export default {
  data() {
    return {
      ftl: '',
      workflow: '',
      html: '',
      parameters: [],
    }
  },
  mounted() {
    this.addParam()
  },
  methods: {
    addParam() {
      this.parameters.push({ key: '', value: '' })
    },
    removeParam(idx) {
      this.parameters.splice(idx, 1)
    },
    parseFtl() {
      let params
      if (this.parameters.length) {
        params = {}
        this.parameters.reduce((target, param) => {
          try {
            target[param.key] = JSON.parse(param.value)
          } catch (e) {
            target[param.key] = param.value
          }
          return target
        }, params)
      }
      let postParams = { ftl: this.ftl, parameters: params }
      if (this.workflow.startsWith('<?xml')) {
        postParams.workflow = { workflowString: this.workflow }
      } else {
        postParams.workflow = {
          workflowV2String: this.workflow,
          isV2Script: true,
        }
      }
      this.$http.post('/v2/parseftl', postParams).then(response => {
        if (response.data.responseCode === 0) {
          this.html = response.data.result.parsedFtl
        } else {
          this.$message.error(response.data.message)
        }
      })
    },
  },
}
</script>
<style>
.ftl .ftl-col {
  box-shadow: 0 1px 15px 0 rgba(0, 0, 0, 0.05);
  border: solid 1px rgba(0, 0, 0, 0.08);
  height: 100vh;
  overflow: scroll;
}
</style>
