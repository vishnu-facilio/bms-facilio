<template>
  <div class="f-criteria-rule">
    <div v-if="rule.active">
      <el-card class="box-card active">
        <el-row :gutter="10" :span="8" align="middle" style="margin-top:-6px;">
          <el-col :md="6" :lg="6">
            <el-select v-if="!threshold" :size="size" v-model="rule.fieldName">
              <el-option
                v-for="(field, index) in moduleMeta.fields"
                :key="index"
                :label="field.displayName"
                :value="field.name"
              ></el-option>
            </el-select>
            <el-select v-else disabled :size="size" v-model="rule.fieldName">
              <el-option
                v-for="(field, index) in moduleMeta.fields"
                :key="index"
                :label="field.displayName"
                :value="field.name"
              ></el-option>
            </el-select>
          </el-col>

          <el-col :md="6" :lg="6">
            <el-select
              :size="size"
              class="rule-part"
              v-model="rule.operator"
              :disabled="!getOperators"
              filterable
              placeholder="Select operator"
            >
              <el-option
                v-for="(operator, key) in getOperators"
                :key="operator"
                :label="key"
                :value="key"
              ></el-option>
            </el-select>
          </el-col>

          <el-col
            :md="threshold ? 12 : 11"
            :lg="threshold ? 12 : 11"
            style="background-color: #fff;"
          >
            <div v-if="!isValueNeeded">
              <el-input
                :size="size"
                disabled
                class="rule-part"
                v-model="rule.value"
              ></el-input>
            </div>
            <div v-else-if="getValueDataType">
              <el-input-number
                :size="size"
                v-if="
                  !threshold &&
                    (getValueDataType.dataType === 'NUMBER' ||
                      getValueDataType.dataType === 'DECIMAL')
                "
                class="rule-part"
                v-model="rule.value"
              ></el-input-number>
              <el-input
                v-else-if="
                  getValueDataType.dataType === 'NUMBER' ||
                    getValueDataType.dataType === 'DECIMAL'
                "
                :size="size"
                class="rule-part"
                v-model="rule.value"
                type="number"
              ></el-input>
              <el-select
                :size="size"
                v-else-if="getValueDataType.dataType === 'BOOLEAN'"
                class="rule-part"
                v-model="rule.value"
              >
                <el-option label="Yes" value="true"></el-option>
                <el-option label="No" value="false"></el-option>
              </el-select>
              <f-date-picker
                :size="size"
                v-else-if="getValueDataType.dataType === 'DATE'"
                class="rule-part"
                v-model="rule.value"
                :type="
                  rule.operator.indexOf('between') >= 0 ? 'daterange' : 'date'
                "
              ></f-date-picker>
              <f-date-picker
                :size="size"
                v-else-if="getValueDataType.dataType === 'DATE_TIME'"
                class="rule-part"
                v-model="rule.value"
                :type="
                  rule.operator.indexOf('between') >= 0
                    ? 'datetimerange'
                    : 'datetime'
                "
              ></f-date-picker>

              <el-select
                :size="size"
                v-else-if="
                  getValueDataType.dataType === 'LOOKUP' &&
                    getValueDataType.displayType === 'LOOKUP_SIMPLE'
                "
                filterable
                multiple
                class="rule-part"
                v-model="rule.value"
              >
                <el-option
                  v-for="(value, key) in picklistOptions"
                  :key="key"
                  :label="value"
                  :value="key"
                ></el-option>
              </el-select>

              <el-select
                :size="size"
                v-else-if="
                  getValueDataType.dataType === 'LOOKUP' &&
                    getValueDataType.displayType === 'LOOKUP_POPUP'
                "
                filterable
                multiple
                class="rule-part"
                v-model="rule.value"
              >
                <el-option
                  v-for="(value, key) in picklistOptions"
                  :key="key"
                  :label="value"
                  :value="key"
                ></el-option>
              </el-select>

              <el-input
                :size="size"
                v-else-if="
                  getValueDataType.dataType === 'LOOKUP' &&
                    getValueDataType.displayType === 'LOOKUP_POPUP1'
                "
                placeholder="Select records"
                :readonly="true"
                suffix-icon="el-icon-search"
                class="rule-part"
                v-model="rule.value"
              ></el-input>
              <el-input
                :size="size"
                v-else
                class="rule-part"
                v-model="rule.value"
              ></el-input>
            </div>
          </el-col>
          <el-button
            @click="remove"
            v-if="index !== 0"
            style="float:right; font-size: 13px;color: black;margin-top: -3px;"
            type="text"
            icon="el-icon-delete"
          ></el-button>
        </el-row>
      </el-card>
    </div>

    <div @click="toggle" v-else>
      <el-card class="box-card normal">
        <el-row :gutter="10" :span="8" align="middle">
          <el-col :md="6" :lg="6">
            <span class="pointer">
              <span class="Alarm-Class">{{ rule.fieldName }}</span></span
            >
          </el-col>
          <el-col :md="6" :lg="6">
            <span class="pointer"
              ><span class="Alarm-Class">{{ rule.operator }}</span></span
            >
          </el-col>
          <el-col :md="11" :lg="11">
            <span class="pointer"
              ><span class="Alarm-Class">{{ rule.value }}</span></span
            >
          </el-col>
          <el-button
            @click="remove"
            v-if="index !== 0"
            class="pull-right"
            style=" font-size: 13px;color: black; margin-top:-10px "
            type="text"
            icon="el-icon-delete"
          ></el-button>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script>
import FDatePicker from 'pages/assets/overview/FDatePicker'
import { getFieldOptions } from 'util/picklist'

export default {
  name: 'f-criteria-rule',
  props: [
    'index',
    'rule',
    'moduleMeta',
    'size',
    'threshold',
    'thresholdFieldName',
  ],
  components: {
    FDatePicker,
  },
  data() {
    return {
      picklistOptions: {},
      valueNotNeed: [
        'is empty',
        'is not empty',
        'Today',
        'Tomorrow',
        'Yesterday',
        'Starting Tomorrow',
        'Till Yesterday',
        'Last Month',
        'Current Month',
        'Next Month',
        'Last Week',
        'Current Week',
        'Next Week',
      ],
    }
  },
  watch: {
    'rule.fieldName': function(val) {
      if (this.threshold) {
        this.$emit('thresholdmetric', val)
      }
    },
  },
  created() {
    this.$store.dispatch('loadGroups')
  },
  mounted() {
    this.setThresholdMetric()
  },
  computed: {
    getOperators() {
      if (this.rule.fieldName && this.rule.fieldName !== '') {
        let field = this.moduleMeta.fields.filter(
          field => field.name === this.rule.fieldName
        )
        if (field.length) {
          if (
            field[0].dataTypeEnum._name === 'LOOKUP' &&
            field[0].specialType
          ) {
            this.loadSpecialTypePickList(field[0].specialType)
          } else if (
            field[0].dataTypeEnum._name === 'LOOKUP' &&
            field[0].lookupModule
          ) {
            this.loadPickList(field[0].lookupModule.name)
          }

          if (field[0].specialType && field[0].specialType === 'basespace') {
            let oprs = this.moduleMeta.operators[field[0].dataTypeEnum._name]
            oprs['is'] = 'BUILDING_IS'
            return oprs
          } else {
            return this.moduleMeta.operators[field[0].dataTypeEnum._name]
          }
        }
      }
      return null
    },
    getValueDataType() {
      if (this.rule.operator && this.rule.operator !== '') {
        let field = this.moduleMeta.fields.filter(
          field => field.name === this.rule.fieldName
        )

        if (
          field[0].dataTypeEnum._name === 'DATE' ||
          field[0].dataTypeEnum._name === 'DATE_TIME'
        ) {
          if (
            this.rule.operator === 'Age in Days' ||
            this.rule.operator === 'Due in Days'
          ) {
            return {
              dataType: 'NUMBER',
              displayType: 'NUMBER',
            }
          }
        }

        return {
          dataType: field[0].dataTypeEnum._name,
          displayType: field[0].displayType._name,
        }
      }
      return null
    },
    isValueNeeded() {
      if (this.rule.operator && this.rule.operator !== '') {
        let bool = this.valueNotNeed.indexOf(this.rule.operator) === -1
        if (!bool) {
          this.rule.value = ''
        }
        return bool
      }
      this.rule.value = ''
      return false
    },
  },
  methods: {
    setThresholdMetric: function() {
      this.rule.fieldName = this.thresholdFieldName
    },
    loadSpecialTypePickList(specialType) {
      let self = this
      self.picklistOptions = {}

      if (specialType === 'users') {
        let userList = self.$store.state.users
        self.picklistOptions['$' + '{LOGGED_USER}'] = 'Current User'
        for (let user of userList) {
          self.picklistOptions[user.id] = user.name
        }
      } else if (specialType === 'groups') {
        let groupList = self.$store.state.groups
        for (let group of groupList) {
          self.picklistOptions[group.groupId] = group.name
        }
      } else if (specialType === 'basespace') {
        let spaceList = self.$store.state.spaces
        for (let space of spaceList) {
          self.picklistOptions[space.id] = space.name
        }
      }
    },
    toggle() {
      this.rule.active = !this.rule.active
    },
    async loadPickList(moduleName) {
      this.picklistOptions = {}

      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: moduleName, skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.picklistOptions = options
      }
    },
    remove() {
      this.$emit('removeChild', this.index)
    },
    add() {
      if (!this.isroot) {
        this.$emit('add', this.index)
      }
    },
  },
}
</script>
<style>
.f-criteria-rule .flicker {
  background: #fff !important;
  border: 1px !important;
}
.Alarm-Class {
  width: 82px;
  height: 28px;
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: #70678f;
}
.f-criteria-rule .el-row {
  padding: 12px;
}

.innerwhite {
  background: #fff !important;
}
.textalign {
  width: 59px;
  height: 28px;
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
}
.f-criteria-rule .el-input {
  vertical-align: middle;
}
.f-criteria-rule .el-select {
  vertical-align: middle;
}
.f-criteria-rule .el-select .el-input.is-disabled .el-input__inner {
  background: white;
}
.f-criteria-rule .el-input.is-disabled .el-input__inner {
  background: white;
}
.f-criteria-rule .el-input .el-input__inner,
.f-criteria-rule .el-textarea .el-textarea__inner {
  border: 0px solid #d8dce5;
  border-bottom: 1px solid #d8dce5;
  padding: 0px;
  border-radius: 0px;
  height: 32px;
}
.mouse {
  background: #cacaf7;
}
.f-criteria-rule .el-date-editor--datetimerange.el-input,
.f-criteria-rule .el-date-editor--datetimerange.el-input__inner {
  width: auto;
}
.f-criteria-rule .el-icon-time:before {
  content: '';
}
.f-criteria-rule .el-input__icon {
  width: auto;
}
</style>
