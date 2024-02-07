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
      <span class="kpi-card-header">SMART CARD</span>
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
export default {
  props: ['visibility', 'cardData'],
  data() {
    return {
      result: null,
      dataTypes: ['TEXT', 'BOOLEAN', 'NUMBER', 'DATE'],
      columns: ``,
      errors: [],
      data: {
        style: {
          bgColor: '#fff',
          color: '#000',
        },
        columns: [
          {
            key: '',
            label: '',
            datatype: 'TEXT',
            width: '20',
            merge: true,
            mergeKey: null,
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
    }
  },
  computed: {},
  methods: {
    addContions() {
      let emptyData = {
        key: 'a',
        label: 'Name',
        datatype: 'TEXT',
        width: '20',
        merge: true,
        mergeKey: null,
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
