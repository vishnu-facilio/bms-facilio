<template>
  <div class="height100 function-page">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">{{ $t('setup.setup.functions') }}</div>
        <div class="heading-description">
          {{ $t('setup.list.list_functions') }}
        </div>
      </div>

      <div class="nameSpace-btn setting-page-btn flex-middle">
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="addNamespaceDialog = true"
        >
          {{ $t('setup.add.add_function') }}</el-button
        >
      </div>
    </div>

    <div class="pR10 width300px fR">
      <f-search v-model="nameSpaces"></f-search>
    </div>

    <div class="container-scroll clearboth">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12 overflow-x">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">FUNCTION NAME</th>
                <th class="setting-table-th setting-th-text">RETURN TYPE</th>
                <th class="setting-table-th setting-th-text">MODIFIED TIME</th>
                <th class="setting-table-th setting-th-text">MODIFIED BY</th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="emptyState">
              <tr>
                <td colspan="100%" class="text-center height50vh">
                  <inline-svg
                    src="svgs/emptystate/coding"
                    iconClass="icon text-center icon-80"
                  ></inline-svg>
                  <div class="pT10 bold fc-black-14">
                    {{ $t('setup.empty.empty_functions') }}
                  </div>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <template
                class="tablerow"
                v-for="(nameSpace, idex) in nameSpaces"
              >
                <tr
                  class="tablerow visibility-visible-actions namespace-bg"
                  :key="idex"
                >
                  <td colspan="100%">
                    <div>
                      <span class="fc-pink bold text-left">{{
                        $getProperty(nameSpace, 'name', '')
                      }}</span>
                      <span class="pR50 fR">
                        <i
                          v-if="checkIfFunctionsAvailable(nameSpace)"
                          class="visibility-hide-actions el-icon-delete delete-icon-danger pointer"
                          @click="deleteNameSpace(nameSpace)"
                        ></i>
                      </span>
                    </div>
                  </td>
                </tr>
                <tr
                  class="tablerow visibility-visible-actions"
                  v-for="(functionObj, idx1) in nameSpace.functions"
                  :key="idex + '-' + idx1"
                >
                  <td>
                    <div class="fc-black-14 text-left bold pointer">
                      {{ functionObj.name ? functionObj.name : ' ' }}
                    </div>
                  </td>
                  <td style="width: 10%">
                    <div class="fc-black-14 text-left" style="width: 100px;">
                      {{ functionObj.returnTypeString }}
                    </div>
                  </td>
                  <td>
                    {{ functionObj.sysModifiedTime | formatDate() }}
                  </td>
                  <td>
                    <div class="fc-black-14 flex-middle text-left">
                      {{ $getProperty(functionObj.sysModifiedByObj, 'name') }}
                    </div>
                  </td>
                  <td>
                    <div class="visibility-hide-actions">
                      <i
                        class="el-icon-edit pointer pR10"
                        @click="editFunction(nameSpace, functionObj)"
                      ></i>
                      <i
                        class="el-icon-delete delete-icon-danger pointer"
                        @click="deleteFunction(functionObj)"
                      ></i>
                    </div>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <el-dialog
      :visible.sync="addNamespaceDialog"
      :fullscreen="false"
      :before-close="cancel"
      open="top"
      width="35%"
      :title="$t('setup.add.add_function')"
      custom-class="assetaddvaluedialog fc-dialog-center-container  fc-web-form-dialog function-dialog"
    >
      <el-form
        :model="data"
        :rules="rules"
        ref="ruleForm"
        label-width="120px"
        class="demo-ruleForm pB60"
      >
        <el-form-item prop="nameSpace" class="label-top-form-item">
          <p class="label-txt2">{{ $t('setup.setupLabel.namespace') }}</p>
          <el-autocomplete
            popper-class="my-autocomplete fc-input-full-border-select2"
            class="fc-input-full-border-select2 width100"
            v-model="data.nameSpace"
            :fetch-suggestions="querySearch"
            :placeholder="$t('setup.setupLabel.namespace')"
            @select="selectNameSpace"
          >
            <template v-slot="{ item }">
              <div class="dashboard-folder-name">{{ item.name }}</div>
            </template>
          </el-autocomplete>
        </el-form-item>
        <el-form-item prop="functionname" class="label-top-form-item pT10">
          <p class="label-txt2">
            {{ $t('setup.setupLabel.function_name') }}
          </p>
          <el-input
            :placeholder="$t('setup.setupLabel.function_name')"
            v-model="data.functionname"
            class="fc-input-full-border-select2 "
          >
          </el-input>
        </el-form-item>
        <el-form-item prop="returnType" class="label-top-form-item pT10">
          <p class="label-txt2">
            {{ $t('setup.setupLabel.return_type') }}
          </p>
          <el-select
            v-model="data.returnType"
            :placeholder="$t('setup.setupLabel.return_type')"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              :label="value"
              :value="key"
              v-for="(key, value) in returnTypes"
              :key="key"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="returnType" class="label-top-form-item pT10">
          <p class="label-txt2">{{ $t('setup.setupLabel.arguments') }}</p>
          <el-row
            v-for="(arg, i) in data.arguments"
            :key="i"
            class="arg-container p5"
          >
            <el-col :span="11">
              <el-input
                :placeholder="$t('setup.setupLabel.function_name')"
                v-model="arg.argName"
                class="fc-input-full-border-select2 "
              >
              </el-input>
            </el-col>
            <el-col :span="9">
              <el-select
                v-model="arg.dataType"
                :placeholder="$t('setup.setupLabel.data_type')"
                class="fc-input-full-border-select2 width100 pL10"
              >
                <el-option
                  :label="value"
                  :value="returnTypesToName[key]"
                  v-for="(key, value) in returnTypes"
                  :key="key"
                  v-if="key > 0"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="4" class="text-center pT5">
              <div class="pL10 inline_flex ">
                <i
                  class="el-icon-circle-plus-outline f22 fw4 pointer fc-row-add-icon"
                  @click="addArguments(arg)"
                ></i>
                <i
                  class="el-icon-remove-outline f22 fw4 pointer fc-row-delete-icon pL5"
                  @click="removeArguments(i)"
                  v-if="data.arguments.length > 1"
                ></i>
              </div>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="cancel()">{{
          $t('setup.users_management.cancel')
        }}</el-button>
        <el-button class="modal-btn-save" type="primary" @click="submitForm()"
          >{{ $t('setup.users_management.add') }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import Avatar from '@/Avatar'
import FSearch from '@/FSearch'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
export default {
  components: {
    Avatar,
    FSearch,
  },
  data() {
    let nameSpaceValidate = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('Please Enter the namespace'))
      } else if (value.split(' ').length > 1) {
        callback(new Error('Please Enter the valid namespace'))
      } else {
        callback()
      }
    }
    let functionNameValidate = (rule, value, callback) => {
      if (value === '') {
        callback(new Error(this.$t('setup.setupLabel.enter_function_name')))
      } else if (value.split(' ').length > 1) {
        callback(
          new Error(this.$t('setup.setupLabel.enter_valid_fucntion_name'))
        )
      } else {
        callback()
      }
    }
    return {
      isNew: true,
      loading: false,
      returnTypes: {
        Void: 0,
        String: 1,
        Number: 2,
        Boolean: 3,
        Map: 4,
        List: 5,
      },
      returnTypesToName: {
        0: 'void',
        1: 'String',
        2: 'Number',
        3: 'Boolean',
        4: 'Map',
        5: 'List',
      },
      data: {
        functionname: '',
        nameSpace: '',
        description: '',
        returnType: null,
        workflowNameSpace: null,
        workflowUserFunction: null,
        arguments: [
          {
            argName: '',
            dataType: null,
          },
        ],
        code: {
          payload: 'void test(){\n    \n    \n}',
          result: '',
        },
      },
      rules: {
        nameSpace: [
          {
            validator: nameSpaceValidate,
            trigger: 'blur',
          },
        ],
        functionname: [
          {
            validator: functionNameValidate,
            trigger: 'blur',
          },
        ],
      },
      addNamespaceDialog: false,
      functionname: '',
      nameSpaces: [],
      newFunction: true,
    }
  },
  mounted() {
    this.loadNameSpaceList()
  },
  computed: {
    emptyState() {
      let { nameSpaces, loading } = this
      return isEmpty(nameSpaces) && !loading
    },
  },
  methods: {
    handleNameSpace(command) {
      if (command === 'delete') {
        this.deleteFunction()
      }
    },
    checkIfFunctionsAvailable(nameSpace) {
      let { functions } = nameSpace
      return isEmpty(functions)
    },
    querySearch(queryString, cb) {
      let results = queryString
        ? this.nameSpaces.filter(this.createFilter(queryString))
        : this.nameSpaces
      cb(results)
    },
    createFilter(queryString) {
      return link => {
        return link.name.toLowerCase().indexOf(queryString.toLowerCase()) === 0
      }
    },
    editFunction(namespace, fun) {
      if (namespace && fun) {
        this.setData(namespace, fun)
        this.newFunction = true
        this.$router.push({
          name: 'functions.edit',
          params: {
            namespaceId: namespace?.id,
            functionId: fun?.id,
          },
        })
      }
    },
    selectNameSpace(item) {
      if (item) {
        this.data.nameSpace = item.name
        this.data.workflowNameSpace = item
      }
    },
    setData(namespace, fun) {
      if (namespace && fun) {
        this.data.workflowNameSpace = namespace
        this.data.workflowUserFunction = fun
        this.data.functionname = fun.name
        this.data.nameSpace = namespace.name
        this.data.returnType = fun.returnType
        this.data.code.payload = fun.workflowV2String
      }
    },
    async loadNameSpaceList(force = false) {
      this.loading = true
      let params = {}
      let { error, data } = await API.get(
        '/v2/workflow/getNameSpaceListWithFunctions',
        params,
        {
          force,
        }
      )
      if (error) this.$message.error(error.message || 'Error Occured')
      else {
        let { workflowNameSpaceList } = data || {}
        this.nameSpaces = workflowNameSpaceList || {}
      }
      this.loading = false
    },
    setnameSpace() {
      this.data.nameSpace = this.data.functionname.replace(/\W+(.)/g, function(
        match,
        chr
      ) {
        return chr.toUpperCase()
      })
    },
    cancel() {
      this.addNamespaceDialog = false
    },
    submitForm() {
      this.$refs['ruleForm'].validate(valid => {
        if (valid) {
          this.saveNameSpace()
        } else {
          return false
        }
      })
    },
    async deleteNameSpace(data) {
      let params = {
        namespace: data,
      }
      let { error } = await API.post('v2/workflow/deleteNameSpace', params)
      if (!error) {
        this.$message.success(this.$t('setup.setup.namespace_delete'))
        this.loadNameSpaceList(true)
      } else this.$message.error(error.message || 'Error Occurred')
    },
    async deleteFunction(functionObj) {
      let value = await this.$dialog.confirm({
        title: 'Delete Function',
        htmlMessage: 'Are you sure want to delete this Function?',
        rbDanger: true,
        rbLabel: 'Confirm',
      })

      if (!value) return
      let { id } = functionObj || {}
      let params = {
        workflow: {
          id,
        },
      }
      let { error } = await API.post('v2/workflow/deleteWorkflow', params)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.$message.success('Function deleted successfully')
        this.loadNameSpaceList(true)
      }
    },
    clearData() {
      this.data = {
        functionname: '',
        nameSpace: '',
        description: '',
        returnType: null,
        workflowNameSpace: null,
        workflowUserFunction: null,
        arguments: [
          {
            argName: '',
            dataType: null,
          },
        ],
        code: {
          payload: 'void test(){\n    \n    \n}',
          result: '',
        },
      }
    },
    saveNameSpace() {
      if (this.data.workflowNameSpace && this.data.workflowNameSpace.id) {
        this.addFunction(this.data)
      } else {
        this.$http
          .post('/v2/workflow/addNameSpace', {
            namespace: {
              name: this.data.nameSpace,
            },
          })
          .then(response => {
            if (
              response.data.result &&
              response.data.result.workflowNameSpace
            ) {
              this.data.workflowNameSpace =
                response.data.result.workflowNameSpace
              this.addNamespaceDialog = false
              this.addFunction(this.data)
            }
            console.log(response)
          })
          .catch(function(error) {
            if (error) {
              this.$message.error(error.message || 'Error Occurred')
            }
          })
      }
    },
    addFunction(data) {
      let params = {}
      this.constructCodePayload(data)
      params['userFunction'] = {
        workflowV2String: data.code.payload,
        isV2Script: true,
      }
      params['userFunction']['nameSpaceId'] = data.workflowNameSpace.id
      params['userFunction']['returnType'] = data.returnType
      API.post('/v2/workflow/addUserFunction', params)
        .then(response => {
          if (
            response.data.result &&
            response.data.result.workflowUserFunction
          ) {
            this.data.workflowUserFunction =
              response.data.result.workflowUserFunction
          }

          let namespaceId = response?.data?.workflowUserFunction?.nameSpaceId
          let functionId = response?.data?.workflowUserFunction?.id

          this.addNamespaceDialog = false
          this.$router.push({
            name: 'functions.edit',
            params: {
              namespaceId: namespaceId,
              functionId: functionId,
            },
          })
        })
        .catch(function(error) {
          if (error) {
            this.$message.error(error.message || 'Error Occurred')
          }
        })
    },
    constructCodePayload(data) {
      if (data && data.functionname) {
        data.code.payload =
          this.returnTypesToName[data.returnType] +
          ' ' +
          data.functionname +
          this.setArguments(data) +
          '{\n    \n    \n}'
      }
    },
    addArguments(arg) {
      if (arg.argName.length > 0 && arg.dataType) {
        this.data.arguments.push({
          argName: '',
          dataType: null,
        })
      } else {
        this.$message.error('Plase Enter all the fields')
      }
    },
    removeArguments(i) {
      this.data.arguments.splice(i, 1)
    },
    setArguments(data) {
      if (data && data.arguments && data.arguments.length) {
        if (data.hasOwnProperty('returnType')) {
          let str = '('
          data.arguments.forEach((arg, index) => {
            if (arg.dataType) {
              str += arg.dataType + ' ' + arg.argName
              if (index !== data.arguments.length - 1) {
                str += ', '
              }
            }
          })
          return str + ')'
        } else {
          return '()'
        }
      } else {
        return '()'
      }
    },
  },
}
</script>
<style lang="scss">
.function-dialog .el-dialog__body {
  min-height: 440px;
  overflow: auto;
  overflow-y: scroll;
  max-height: 440px;
}

.function-con .border-bottom22 {
  border-bottom: none;
}

.function-con {
  border-bottom: 1px solid rgb(223 232 235 / 50%);
}
.namespace-bg {
  background: #fbfbfb !important;
  :hover {
    background: #fbfbfb !important;
  }
}
.function-page {
  .setting-list-view-table tbody tr:not(.nohover):hover {
    border: 1px solid transparent !important;
  }

  tr:nth-child(odd) {
    background: #fff;
  }

  .el-icon-edit {
    color: #319aa8;
    font-size: 16px;
  }
}
</style>
