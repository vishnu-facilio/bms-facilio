<template>
  <div>
    <el-container>
      <el-header class="boxheader">
        <span>
          <el-button
            @click="cancel"
            style="font-size: 17px;width: 14px;color:#ef508f;font-weight:bold;"
            type="text"
            icon="el-icon-back"
          ></el-button>
          <span class="discription-textbox" style="padding-left:7px;">{{
            $t('common._common.back_list')
          }}</span>
        </span>
        <span class="pull-right" style="padding: 19px;">
          <el-button
            @click="cancel"
            style="color:#39b2c2;border-color:#39b2c2; letter-spacing: 0.7px;"
            >CANCEL</el-button
          >
          <el-button type="primary" @click="save">SAVE</el-button>
        </span>
        <div>
          <el-input
            v-model="model.readingRule.name"
            class="Alarm-title"
            placeholder="New Space Threshold"
          >
          </el-input>
        </div>
      </el-header>
      <el-main class="indent">
        <q-input
          type="textarea"
          v-model="model.readingRule.description"
          style="width:75%; margin-top: 0px;"
          float-label="Description"
          :min-rows="1"
          class="edittext"
        ></q-input>
        <el-select
          v-model="selectedAsset"
          filterable
          clearable
          placeholder="Select Asset"
        >
          <el-option
            v-for="asset in assetList"
            :key="asset.id"
            :label="asset.name"
            :value="asset.id"
          ></el-option>
        </el-select>
        <div
          v-if="
            (module === 'readingrule' && model.readingRule.module !== '') ||
              module !== 'readingrule'
          "
          class="boldtitle"
        >
          CONDITIONS
        </div>
        <f-criteria-builder
          v-if="selectedAsset"
          @selectedModuleId="selectedModuleId"
          :readingobj="getAsset(selectedAsset)"
          :threshold="true"
          v-model="model.readingRule.criteria"
          module="space"
        ></f-criteria-builder>
        <div class="fc-form-section" style="font-weight:bold;padding-top:10px;">
          Alarm Details
        </div>
        <el-row
          align="middle"
          style="padding-left: 30px; margin:0px;padding-top:20px;"
          :gutter="50"
        >
          <el-col :span="12" style="padding-right: 35px;padding-left: 0px;">
            <div class="textcolor">Message</div>
            <div class="add">
              <el-input
                v-model="newalarm.message"
                style="width:100%"
                type="text"
                placeholder=""
              ></el-input>
            </div>
          </el-col>
          <el-col :span="12" style="padding-right: 35px;">
            <div class="textcolor">Severity</div>
            <div class="add">
              <el-select
                v-model="newalarm.severity"
                style="width:100%"
                class="form-item "
                placeholder=""
              >
                <el-option
                  key="Critical"
                  label="Critical"
                  value="Critical"
                ></el-option>
                <el-option key="Major" label="Major" value="Major"></el-option>
                <el-option key="Minor" label="Minor" value="Minor"></el-option>
                <el-option key="Clear" label="Clear" value="Clear"></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
        <el-row
          align="middle"
          style="padding-left: 30px; margin:0px;padding-top:20px;"
          :gutter="50"
        >
          <el-col :span="12" style="padding-right: 35px;padding-left: 0px;">
            <div class="textcolor">Alarm Class</div>
            <div class="add">
              <el-input
                v-model="newalarm.alarmClass"
                style="width:100%"
                type="text"
                placeholder=""
              ></el-input>
            </div>
          </el-col>
          <el-col :span="12" style="padding-right: 35px;">
            <div class="textcolor">Priority</div>
            <div class="add">
              <el-input
                v-model="newalarm.priority"
                style="width:100%"
                type="text"
                placeholder=""
              ></el-input>
            </div>
          </el-col>
        </el-row>
        <div
          class="fc-form-section"
          style="padding-top:30px; font-weight:bold;"
        >
          Additional Information
        </div>
        <div
          class="fc-rule-actions"
          style="padding-top:30px; padding-left:30px; padding-right:30px;"
        >
          <el-card
            class="fc-rule-action box-card"
            v-for="(action, index) in model.actions"
            :key="index"
          >
            <el-row :gutter="10" align="middle">
              <el-col :md="24" :lg="24">
                <field-matcher
                  v-model="action.templateJson.fieldMatcher"
                ></field-matcher>
              </el-col>
            </el-row>
          </el-card>
        </div>
      </el-main>
    </el-container>
  </div>
</template>
<script>
import FCriteriaBuilder from '@/criteria/FCriteriaBuilder'
import FieldMatcher from '@/FieldMatcher2'
import { QInput } from 'quasar'
import { mapState } from 'vuex'
export default {
  props: ['visible'],
  data() {
    return {
      show: false,
      moduleMeta: null,
      callmail: false,
      callsms: false,
      module: 'alarm',
      saving: false,
      model: {
        readingRule: {
          module: '',
          name: '',
          description: '',
          executionOrder: 0,
          resourceId: -1,
          event: {
            moduleId: null,
            activityType: 1,
          },
          criteria: null,
          ruleType: 2,
        },
        actions: [
          {
            actionType: 6,
            templateJson: {
              fieldMatcher: [],
            },
          },
        ],
      },
      selectedAsset: null,
      assetList: [],
      readings: [],
      newalarm: {
        message: '',
        severity: 'Minor',
        alarmClass: '',
        priority: '',
      },
      rules: {
        'rule.name': [
          {
            required: true,
            message: 'Please enter rule name',
            trigger: 'blur',
          },
        ],
        'rule.activityType': [
          { required: true, message: 'Please select event type' },
        ],
      },
      duration: -1,
    }
  },

  created() {
    this.$store.dispatch('loadTicketPriority')
  },
  components: {
    FCriteriaBuilder,
    QInput,
    FieldMatcher,
  },
  computed: {
    showDialog() {
      return this.visible
    },
    ...mapState({
      ticketpriority: state => state.ticketPriority,
    }),
  },
  watch: {
    ticketpriority: function() {
      this.loadPolicyJson()
    },
  },
  mounted() {
    console.log('eeeeeeeeee')
    this.loadAssets()
  },
  methods: {
    selectedModuleId(val) {
      this.model.readingRule.event.moduleId = val
    },
    getAsset(id) {
      return this.assetList.find(at => at.id === id)
    },
    showcondition() {
      if (this.selectedAsset == null) {
        alert('asset null')
      }
      let asset = this.getAsset(this.selectedAsset)
      alert(asset.category)
    },
    close() {
      this.$emit('update:visible', false)
    },
    save() {
      let self = this
      self.model.actions[0].templateJson.fieldMatcher.push({
        field: 'message',
        value: this.newalarm.message,
      })
      self.model.actions[0].templateJson.fieldMatcher.push({
        field: 'severity',
        value: this.newalarm.severity,
      })
      self.model.actions[0].templateJson.fieldMatcher.push({
        field: 'alarmClass',
        value: this.newalarm.alarmClass,
      })
      self.model.actions[0].templateJson.fieldMatcher.push({
        field: 'priority',
        value: this.newalarm.priority,
      })
      self.model.actions[0].templateJson.fieldMatcher.push({
        field: 'assetId',
        value: this.selectedAsset,
      })
      if (this.model.readingRule.module === '') {
        this.model.readingRule.module = this.module
        self.model.readingRule.resourceId = self.selectedAsset
      }
      self.$http.post('/setup/addReadingrules', self.model)

      self.$emit('update:visible', false)
      self.$router.push({ path: '.' })
    },
    actionSaved(action) {
      this.model.actions.push(action)
    },
    cancel() {
      this.$emit('update:visible', false)
      this.$router.push({ path: '.' })
    },
    loadAssets() {
      let self = this
      let url = '/space/spacelist'
      this.$http.get(url).then(function(response) {
        if (response.data) {
          self.assetList = response.data.spaces
        }
      })
    },
    showCreateNewDialog() {
      if (this.$route.query.create) {
        return true
      }
      return false
    },
    backto() {
      this.$router.push({ path: 'thresholdrules' })
    },
  },
}
</script>
<style>
.boxheader {
  background-color: #ffffff;
  box-shadow: 3px 0 7px 0 rgba(191, 191, 191, 0.5);
  padding-left: 34px;
  height: 70px !important ;
}
.discription-textbox {
  font-size: 12px;
  text-align: left;
  color: #6b7e91;
  letter-spacing: 0.4px;
}
.indent {
  padding-top: 35px;
  padding-left: 35px;
  padding-bottom: 150px;
}
.line {
  background-color: #ffffff;
  box-shadow: 0 2px 7px 0 rgba(191, 191, 191, 0.5);
}
.font {
  font-size: 14px;
  font-weight: 500;
  text-align: left;
  color: #39b2c2;
  padding-top: 15px;
  padding-bottom: 15px;
}
.thinline {
  border: solid 1px #dff4f6;
}
.el-button.is-round {
  border-radius: 20px;
  padding: 7px 10px;
}
.el-button--primary {
  color: #fff;
  background-color: #39b2c2;
  border-color: #39b2c2;
  letter-spacing: 0.7px;
}
.el-radio-button__orig-radio:checked + .el-radio-button__inner {
  color: #70678f;
  background-color: #f3f3f9;
  border-color: #e2e8ee;
  box-shadow: 0 2px 4px 0 rgba(232, 229, 229, 0.5);
}
.el-button.is-round {
  padding: 6px 5px;
}
.Alarm-title {
  margin-top: -13px;
  width: 30%;
  font-size: 18px;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
}
.edittext {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
}
.Alarm-Created {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
}
.Action-text {
  font-size: 13px;
  letter-spacing: 0.6px;
  text-align: left;
  color: #757575;
}
.mailbody {
  font-size: 13px;
  letter-spacing: 0.6px;
  text-align: left;
  color: #757575;
  overflow: hidden;
  text-overflow: ellipsis;
}
.inline {
  /* display:inline; */
  /* padding-left:8%; */
  overflow: hidden;
  text-overflow: ellipsis;
}
.boldtitle {
  font-size: 11px;
  color: #000000;
  font-weight: 500;
  letter-spacing: 0.9px;
  padding-top: 45px;
}
.childinline {
  display: inline;
  padding-left: 56px;
}
.dropdownbutton {
  border-color: #39b2c2;
  font-size: 11px;
  color: #39b2c2;
  font-weight: bold;
  padding: 5px;
  padding-bottom: 15px;
}
.team-down-icon {
  width: 10px;
  height: 10px;
  color: #e1e4eb;
  bottom: 9px;
  right: 41%;
}
</style>
