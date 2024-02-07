<template>
  <div
    class="f-criteria-group"
    :class="{
      rootgroup: isroot && !threshold,
      subgroup: !isroot && !threshold,
      threshold: threshold,
    }"
  >
    <el-card class="box-card">
      <div slot="header">
        <span v-if="isroot && !threshold">
          <span class="MATCH-TYPE">&nbsp;&nbsp; MATCH TYPE &nbsp;&nbsp;</span>
          <span v-if="isroot" class="MATCH-TYPE"></span>

          <el-dropdown
            @command="switchOperator"
            trigger="click"
            v-model="rule.operator"
          >
            <span class="el-dropdown-link">
              <span class="pointer"
                ><span class="allwhite">
                  {{ getOperatorLabel(rule.operator)
                  }}<i
                    class="el-icon-arrow-down el-icon--right"
                  ></i></span></span
            ></span>

            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="all">ALL</el-dropdown-item>
              <el-dropdown-item command="any">ANY</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </span>
        <span v-if="!isroot">
          <span class="WATCH-TYPE">&nbsp; MATCH TYPE &nbsp;</span>
          <span v-if="!isroot" class="WATCH-TYPE"></span>
          <el-dropdown
            @command="switchOperator"
            trigger="click"
            v-model="rule.operator"
          >
            <span class="el-dropdown-link">
              <span class="pointer"
                ><span class="all">
                  {{ rule.operator
                  }}<i
                    class="el-icon-arrow-down el-icon--right"
                  ></i></span></span
            ></span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="ALL">ALL</el-dropdown-item>
              <el-dropdown-item command="ANY">ANY</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </span>
        <el-button
          @click="remove"
          v-if="!isroot"
          style="float: right; padding: 3px 0; font-size: 17px;"
          type="text"
          icon="el-icon-minus"
        ></el-button>
      </div>

      <component
        v-for="(child, idx) in rule.children"
        :key="idx"
        :index="idx"
        :is="child.type"
        :rule="child"
        :size="size"
        :moduleMeta="moduleMeta"
        @removeChild="removeChild"
        :threshold="threshold"
        @add="addRule"
        :thresholdFieldName="thresholdFieldName"
        @thresholdmetric="thresholdmetric"
      >
      </component>
      <el-button
        @click="addRule"
        v-if="!isroot"
        style=" font-size: 17px; width: 16px;color: #8778bb;"
        type="text"
        icon="el-icon-circle-plus"
        class="adcomp"
      ></el-button>

      <div
        v-if="isroot && !threshold"
        style="margin-top:21px;margin-bottom:8px;margin-left: 0px;"
      >
        <el-button
          round
          :size="size"
          @click="addGroup"
          plain
          icon="el-icon-plus"
          style="border-color:#39b2c2;font-size: 11px; color:#39b2c2;font-weight: bold;"
          >ADD GROUP</el-button
        >
        <el-button
          v-if="!threshold"
          round
          :size="size"
          @click="addRule"
          style="border-color:#39b2c2;font-size: 11px; color:#39b2c2;font-weight: bold;"
          icon="el-icon-plus"
          >ADD CONDITION</el-button
        >
      </div>
    </el-card>
  </div>
</template>

<script>
import FCriteriaRule from './FCriteriaRule'
export default {
  data() {
    return {
      click: false,
      rules: {
        label: '',
        value: '',
      },
    }
  },
  name: 'f-criteria-group',
  components: {
    FCriteriaRule,
  },
  mounted() {
    this.addRuleForRoot()
  },
  props: [
    'index',
    'rule',
    'moduleMeta',
    'size',
    'isroot',
    'threshold',
    'thresholdFieldName',
  ],
  methods: {
    thresholdmetric(val) {
      this.$emit('thresholdmetric', val)
    },
    addRuleForRoot() {
      if (this.isroot) {
        this.rule.children.push({
          type: 'f-criteria-rule',
          fieldName: '',
          operator: '',
          value: '',
          active: true,
        })
      }
    },
    addRule() {
      // if (!this.rule.children[i].fieldName === null) {
      for (let i = 0; i < this.rule.children.length; i++) {
        let r = this.rule.children[i]
        if (r.fieldName !== '') {
          r.active = false
        }
      }
      this.rule.children.push({
        type: 'f-criteria-rule',
        fieldName: '',
        operator: '',
        value: '',
        active: true,
      })
    },
    switchOperator(operator) {
      this.rule.operator = operator
    },
    test() {},
    addGroup() {
      let self = this
      self.rule.children.push({
        type: 'f-criteria-group',
        operator: 'ALL',
        children: [
          {
            type: 'f-criteria-rule',
            fieldName: '',
            operator: '',
            value: '',
            active: true,
          },
        ],
      })
    },
    removeChild(index) {
      this.rule.children.splice(index, 1)
    },
    remove() {
      this.$emit('removeChild', this.index)
    },
    getOperatorLabel(operator) {
      if (operator === 'all') {
        return 'ALL'
      }
      return 'ANY'
    },
  },
}
</script>
<style>
.rootgroup-body {
  background: #ffffff;
}
.subgroup-body {
  background: #f3f3f9;
}
.corner {
  position: absolute;
  top: 50px;
  right: 0px;
}

.adcomp:last-child {
  position: absolute;
  bottom: 42px;
  right: 13px;
}
/* .adcomp:last-child{

 } */
.MATCH-TYPE {
  position: relative;
  width: 77px;
  height: 22px;
  opacity: 0.8;
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.9px;
  text-align: left;
  color: #ffffff;
}
.WATCH-TYPE {
  position: relative;
  width: 77px;
  height: 22px;
  opacity: 0.7;
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.9px;
  text-align: left;
  color: #70678f;
}
.Shape {
  width: 18px;
  height: 18px;
  background-color: #8778bb;
}
.el-icon-circle-plus:before {
  background: #f3f3f9;
  border-radius: 150px;
}
.all {
  width: 23px;
  height: 22px;
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.9px;
  color: #333333;
}
.allwhite {
  width: 23px;
  height: 22px;
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.9px;
  color: #ffffff;
}
.f-criteria-group .el-button--small,
.el-button--small.is-round {
  padding: 6px 5px;
}
.el-icon-plus {
  font-weight: bold;
}
</style>
