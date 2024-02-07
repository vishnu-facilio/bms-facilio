<template>
  <el-dialog
    custom-class="f-kfi-card-builder fc-dialog-center-container kpi-map-setup-dilaog"
    :append-to-body="true"
    :visible.sync="visibility"
    :width="'85%'"
    title="KPI CARD"
    :before-close="closedialog"
  >
    <span slot="title" class="dialog-footer">
      <span class="kpi-card-header">SMART MAP</span>
    </span>
    <el-row>
      <el-col :span="10">
        <code-mirror
          v-model="data.workflowV2String"
          class="left-con pT20 pR20"
        ></code-mirror>
      </el-col>
      <el-col :span="4">
        <el-button @click="submit()" class="btn-blue-fill p20">RUN</el-button>
      </el-col>
      <el-col :span="10">
        <el-row>
          <el-col :span="24">
            <div
              v-for="(column, index) in data.columns"
              :key="index"
              class="relative"
            >
              <el-row class="mT10 maxWidth90" :gutter="10">
                <el-col :span="4">
                  <el-input
                    v-model="column.key"
                    placeholder="key"
                    class="fc-input-full-border2"
                  ></el-input>
                </el-col>
                <el-col :span="8">
                  <el-input
                    v-model="column.label"
                    placeholder="Label"
                    class="fc-input-full-border2"
                  ></el-input>
                </el-col>
                <el-col :span="8">
                  <el-select
                    v-model="column.datatype"
                    :placeholder="$t('setup.setupLabel.data_type')"
                    class="fc-input-full-border-select2"
                  >
                    <el-option
                      v-for="(type, idx) in dataTypes"
                      :key="idx"
                      :label="type"
                      :value="type"
                    ></el-option>
                  </el-select>
                </el-col>
                <el-col :span="4">
                  <el-checkbox v-model="column.merge"></el-checkbox>
                </el-col>
                <el-col :span="4" v-if="column.merge">
                  <el-select
                    v-model="column.mergeKey"
                    :placeholder="$t('setup.setupLabel.data_type')"
                    class="fc-input-full-border-select2"
                  >
                    <el-option
                      v-for="(key, idx) in data.columns.map(rt => rt.key)"
                      :key="idx"
                      :label="key"
                      :value="key"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
              <div class="contion-action-btn2 pointer">
                <img
                  src="~assets/add-icon.svg"
                  style="height:18px;width:18px;"
                  class="add-icon"
                  v-if="data.columns.length - 1 === index"
                  @click="addContions"
                />
                <img
                  src="~assets/remove-icon.svg"
                  style="height:18px;width:18px;margin-right: 3px;margin-left: 3px;position:relative;top:10px;"
                  class="delete-icon"
                  v-if="data.columns.length > 1"
                  @click="deleteCondition(index)"
                />
              </div>
            </div>
          </el-col>
          <el-col :span="24">
            <el-select
              v-model="data.functionId"
              :placeholder="$t('setup.setupLabel.data_type')"
              class="fc-input-full-border-select2"
            >
              <el-option-group
                v-for="(fun, idx) in functions"
                :key="idx"
                :label="fun.name"
              >
                <el-option
                  v-for="(funtion, ix) in fun.functions"
                  :key="ix"
                  :label="funtion.name"
                  :value="funtion.id"
                >
                </el-option>
              </el-option-group>
            </el-select>
          </el-col>
          <el-col :span="24">
            <el-input
              v-model="data.tableName"
              placeholder="Title"
              class="fc-input-full-border2"
            ></el-input>
          </el-col>
          <el-col :span="24">
            <el-checkbox v-model="data.filters.building">building</el-checkbox>
            <el-checkbox v-model="data.filters.Assetcategory"
              >Assetcategory</el-checkbox
            >
            <el-checkbox v-model="data.filters.outOfScopeBuilding"
              >Out of Scope Building</el-checkbox
            >
          </el-col>
          <el-col :span="8">
            <el-select
              v-model="data.datefilterId"
              :placeholder="$t('setup.setupLabel.data_type')"
              class="fc-input-full-border-select2"
            >
              <el-option
                v-for="(type, idx) in dateOperators"
                :key="idx"
                :label="type.label"
                :value="type.enumValue"
              ></el-option>
              <el-option label="This Quarter" :value="68"></el-option>
              <el-option label="Last Quarter" :value="69"></el-option>
            </el-select>
          </el-col>
          <el-col :span="24">
            <el-tag
              type="danger"
              v-for="(error, index) in errors"
              :key="index"
              >{{ error }}</el-tag
            >
          </el-col>
          <el-col :span="24">
            <el-input
              type="textarea"
              id="resultBox"
              :rows="2"
              class="right-con p20"
              placeholder="Result"
              :autosize="{ minRows: 2, maxRows: 50 }"
              v-model="result"
            >
            </el-input>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closedialog">Close</el-button>

      <el-button
        type="primary"
        class="modal-btn-save"
        @click="save('update')"
        v-if="cardData"
        >Update</el-button
      >
      <el-button type="primary" class="modal-btn-save" @click="save()" v-else
        >Save</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import CodeMirror from '@/CodeMirror'
import { dateOperators } from 'pages/card-builder/card-constants'
export default {
  props: ['visibility', 'cardData'],
  data() {
    return {
      dateOperators,
      result: null,
      datefilterId: 22,
      functions: null,
      dataTypes: ['TEXT', 'BOOLEAN', 'NUMBER', 'DATE'],
      columns: ``,
      errors: [],
      data: {
        functionId: null,
        tableName: null,
        filters: {
          building: false,
          outOfScopeBuilding: true,
          Assetcategory: false,
        },
        columns: [
          {
            key: '',
            label: '',
            datatype: 'TEXT',
            width: '',
            merge: false,
            mergeKey: null,
            conditionalFormatting: [],
          },
        ],
        workflowV2String: `Map getTable(){
            date = new NameSpace("date");
            period = date.getDateRange("Today");
            db = {
              criteria: [createdTime == period]
            };

            workorder = Module("workorder").fetch(db);

            returnValue = {};
            dataList = [];
            for each index,work in workorder {
              val = {};
              val["a"] = work.serialNumber;
              val["b"] = work.subject;
              val["c"] = work.createdTime;
              val["d"] = work.noOfTasks;
              dataList.add(val);
            }
            returnValue["data"] = dataList;
            log ""+returnValue;
            return returnValue;
          }`,
      },
    }
  },
  components: {
    CodeMirror,
  },
  mounted() {
    if (this.cardData) {
      this.data = this.cardData
      if (!this.data.filters) {
        this.data.filters = {
          building: false,
          Assetcategory: false,
          outOfScopeBuilding: false,
        }
      }
    }
    this.loadNameSpaceList()
  },
  computed: {},
  methods: {
    loadNameSpaceList() {
      this.$http
        .get('/v2/workflow/getNameSpaceListWithFunctions')
        .then(response => {
          this.functions = response.data.result.workflowNameSpaceList
            ? response.data.result.workflowNameSpaceList
            : []
        })
        .catch(() => {
          this.functions = []
        })
    },
    addContions() {
      let emptyData = {
        key: 'a',
        label: 'Name',
        datatype: 'TEXT',
        width: '',
        merge: false,
        mergeKey: null,
        conditionalFormatting: [],
      }
      this.data.columns.push(emptyData)
    },
    deleteCondition(index) {
      this.data.columns.splice(index, 1)
    },
    submit() {
      this.setResult('')
      this.$http
        .post('/v2/workflow/runWorkflow', {
          workflow: {
            workflowV2String: this.data.workflowV2String,
            isV2Script: true,
          },
        })
        .then(response => {
          if (response.data.result.workflow.logString) {
            this.setResult(response.data.result.workflow.logString)
          }
          this.setError(response.data.result.workflowSyntaxError)
        })
        .catch(function() {})
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
      this.result = result
    },
    closedialog() {
      this.$emit('update:visibility', false)
      this.$emit('close', false)
    },
    save(update) {
      let valid = true
      if (valid) {
        this.$emit('update:visibility', false)
        if (update) {
          this.$emit('update', this.data)
        } else {
          this.$emit('save', this.data)
        }
      } else {
        this.$message.error('Please enter all the details !!')
      }
    },
  },
}
</script>
<style>
.contion-action-btn2 {
  position: absolute;
  right: 0;
  top: 0;
  z-index: 10;
}
</style>
