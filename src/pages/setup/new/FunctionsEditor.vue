<template>
  <div class="functions-editor-container">
    <div class="fc-email-breadcrumb-header">
      <div>
        <div class="flex-middle fc-setup-breadcrumb">
          <div
            class="fc-setup-breadcrumb-inner pointer"
            @click="setupHomeRoute"
          >
            {{ $t('common.products.home') }}
          </div>
          <div class="fc-setup-breadcrumb-inner pL10 pR10">
            <i class="el-icon-arrow-right f14 fwBold"></i>
          </div>
          <div
            class="fc-setup-breadcrumb-inner pointer"
            @click="setupFunctionsRoute"
          >
            {{ $t('common._common.function') }}
          </div>
          <div class="fc-setup-breadcrumb-inner pL10 pR10">
            <i class="el-icon-arrow-right f14 fwBold"></i>
          </div>
          <div class="fc-breadcrumbBold-active">
            {{ namespaceName ? namespaceName : $t('common._common.namespace') }}
          </div>
        </div>
        <div class="pT10">
          <el-input
            placeholder="Function Name"
            readonly
            class="fc-template-email-input fc-email-top-input-width"
            v-model="functionName"
          ></el-input>
        </div>
      </div>
      <div>
        <div class="flex-middle">
          <el-switch
            v-model="runAsAdmin"
            class="mR20"
            active-color="rgba(57, 178, 194, 0.8)"
            inactive-color="#e5e5e5"
            :active-text="$t('common._common.run_as_admin')"
          ></el-switch>
          <el-switch v-model="showDiff" class="mR20 mL20" active-text="Diff">
          </el-switch>
          <el-button
            class="fc-dropdown-menu-template2 mR10"
            size="medium"
            @click="updateFunction()"
            >{{ $t('common._common.update') }}</el-button
          >
          <el-button
            class="fc-wo-border-btn height34 pL0 pR0 text-capitalize cancel-btn"
            @click="setupFunctionsRoute"
          >
            {{ $t('common._common.cancel') }}
          </el-button>
        </div>
      </div>
    </div>

    <div class="editor-container">
      <spinner v-if="loading" :show="loading"></spinner>
      <script-editor
        v-else
        v-model="workflowV2String"
        :scriptClass="'script-editor-container'"
        :diff="diffStatus"
      ></script-editor>
    </div>
  </div>
</template>

<script>
import { getApp } from '@facilio/router'
import { ScriptEditor } from '@facilio/ui/setup'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
export default {
  components: {
    ScriptEditor,
  },
  computed: {
    appLinkName() {
      let { linkName } = getApp() || {}
      return linkName
    },
    diffStatus() {
      if (this.showDiff) {
        return { type: 'side_by_side' }
      }
      return ''
    },
  },
  data() {
    return {
      loading: true,
      workflowV2String: '',
      functionName: '',
      showDiff: false,
      namespaceName: '',
      runAsAdmin: false,
    }
  },
  async created() {
    let { data, error } = await API.get(
      '/v2/workflow/getNameSpaceListWithFunctions'
    )
    if (error) {
      this.$message.error(error.message || 'Error Occured')
      return
    }
    let spaceListData = data?.workflowNameSpaceList

    let namespaceArray = spaceListData?.filter(
      namespace => namespace.id == this.getParams('namespaceId')
    )
    let namespace = namespaceArray[0]
    let { name: namespaceName, functions: namespaceFunctions } = namespace || {}
    this.namespaceName = namespaceName
    let functionsArray = namespaceFunctions

    let functionObjArray = functionsArray.find(
      fun => fun.id === this.getParams('functionId')
    )
    let {
      name: functionName,
      workflowV2String,
      runAsAdmin,
    } = functionObjArray
    this.functionName = functionName
    this.workflowV2String = workflowV2String
    this.loading = false
    this.runAsAdmin = runAsAdmin
  },
  methods: {
    setupHomeRoute() {
      return this.$router.replace({
        path: `/${this.appLinkName}/setup/home`,
      })
    },
    setupFunctionsRoute() {
      return this.$router.replace({
        path: `/${this.appLinkName}/setup/customization/functions`,
      })
    },
    async updateFunction() {
      let params = {}
      params['userFunction'] = {
        id: this.getParams('functionId'),
        runAsAdmin: this.runAsAdmin,
        workflowV2String: this.workflowV2String,
        nameSpaceId: this.getParams('namespaceId'),
        isV2Script: true,
      }
      let { data, error } = await API.post(
        '/v2/workflow/updateUserFunction',
        params
      )
      if (error) {
        this.$message.error(error.message || 'Error Occured')
        return
      }
      if (!this.checkErrors(data)) {
        this.showerrorBanners(data)
      } else {
        this.$router.replace({
          path: `/${this.appLinkName}/setup/customization/functions`,
        })
      }
    },
    checkErrors(data) {
      let { workflowSyntaxError } = data
      let { errors } = workflowSyntaxError || []
      return isEmpty(errors)
    },
    showerrorBanners(data) {
      let { workflowSyntaxError } = data
      let { errorsAsString } = workflowSyntaxError || 'Error'
      this.$message({
        showClose: true,
        message: `  ${errorsAsString}  `,
        type: 'error',
        duration: 100000,
      })
    },
    getParams(param) {
      let { params } = this.$route
      let { [param]: parameter } = params || ''
      return parameter
    },
  },
}
</script>

<style lang="scss">
.functions-editor-container {
  position: fixed;
  top: 0;
  left: 0;
  background: #f0f0f0;
  z-index: 20;
  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
  gap: 20px;
  .fc-email-breadcrumb-header {
    height: 90px;
    padding: 13px 24px;
    background-color: #fff;
    border-bottom: 1px solid #f1f1f1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    .cancel-btn {
      line-height: 1px;
      width: 84px;
    }
  }
  .editor-container {
    box-sizing: border-box;
    height: 100%;
    margin: 0 24px 24px 24px;
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

  .func-btn {
    padding: 10px 20px;
    font-size: 14px;
    border-radius: 4px;
  }
  .primary {
    background: #3ab2c1;
    color: #fff;
    border: 1px solid #3ab2c1;
  }
  .secondary {
    background: #ffffff;
    color: #39b2c2;
    border: solid 1px #39b2c2;
  }
}
</style>
