<template>
  <div class="scrollable">
    <div class="fR m10">
      <el-button @click="submit()" class="btn-blue-fill">{{
        $t('common._common.run')
      }}</el-button>
      <el-button v-if="id" @click="update" class="fc-btn-grey-fill">{{
        $t('common._common.update')
      }}</el-button>
    </div>
    <div class="pL20 pR20 pT50">
      <script-editor
        v-model="code.payload"
        :scriptClass="'pT20 pR20 height350 width100'"
      ></script-editor>
    </div>
    <div class="mT30">
      <div class="error-tags-container ">
        <el-tag
          type="danger"
          class="m5"
          v-for="(error, index) in errors"
          :key="index"
          >{{ error }}</el-tag
        >
      </div>
      <el-input
        type="textarea"
        id="resultBox"
        :rows="2"
        class="pL20 pR20 pT10 result-box"
        placeholder="Result"
        v-model="code.result"
      >
      </el-input>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { ScriptEditor } from '@facilio/ui/setup'
export default {
  data: function() {
    return {
      errors: [],
      code: {
        payload: '',
        result: '',
      },
    }
  },
  components: { ScriptEditor },
  computed: {
    id() {
      return this.$route.query.id
    },
  },

  created() {
    let { id } = this
    let defaultScript = 'void test(){\n    \n    \n}'
    if (id) {
      API.get(`/v2/workflow/getFunctionMeta?ids[0]=${id}`).then(({ data }) => {
        this.code.payload = data.workflowUserFunctions[0].workflowV2String
      })
    } else {
      let lst = window.localStorage.getItem('workflow.playground')
      if (lst) {
        defaultScript = lst
      }
      this.code.payload = defaultScript
    }
  },
  methods: {
    async submit() {
      let { code, $route } = this
      let { query } = $route || {}
      let { cloudService } = query || {}
      let { payload } = code || {}
      this.setResult('')
      window.localStorage.setItem('workflow.playground', payload)
      let url = ''

      if (cloudService === 'true') {
        url = 'v3/agent/runWorkflow'
        API.post(url, {
          data: {
            workflow: {
              workflowV2String: payload,
              isV2Script: true,
            },
          },
        })
          .then(response => {
            if (response.data.workflowResponse) {
              this.setResult(response.data.workflowResponse)
            }
            this.setError(response.data.workflowSyntaxError)
          })
          .catch(() => {})
      } else {
        url = '/v2/workflow/runWorkflow'
        API.post(url, {
          workflow: { workflowV2String: this.code.payload, isV2Script: true },
        })
          .then(response => {
            if (response.data.workflow.logString) {
              this.setResult(response.data.workflow.logString)
            }
            this.setError(response.data.workflowSyntaxError)
          })
          .catch(() => {})
      }
    },
    setError(errors) {
      if (errors) {
        this.errors = errors.errors || []
      } else {
        this.errors = []
      }
      this.errors = errors && errors.errors ? errors.errors : []
    },
    setResult(result) {
      this.code.result = result
    },
    update() {
      this.setResult('')
      window.localStorage.setItem('workflow.playground', this.code.payload)
      API.post(`/v2/workflow/updateWorkflow`, {
        workflow: { workflowV2String: this.code.payload, id: this.id },
      }).then(({ error }) => {
        if (error) {
          this.$message.error(error.message)
        } else {
          this.setResult('Updated successfully')
        }
      })
    },
  },
}
</script>
<style lang="scss">
.result-box {
  .el-textarea__inner {
    height: 280px !important;
    padding: 20px !important;
  }
}
.error-tags-container {
  position: absolute;
  right: 80px;
  top: 80px;
  z-index: 10;
  display: grid;
  padding: 10px;
}
</style>
