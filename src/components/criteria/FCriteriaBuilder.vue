<template>
  <div class="f-criteria-builder" v-if="!loading">
    <f-criteria-group
      :rule="criteriaGroup"
      :moduleMeta="moduleMeta"
      :size="fieldSize"
      isroot="true"
      @removeChild="removeChild"
      :threshold="threshold"
      @thresholdmetric="thresholdmetric"
      :thresholdFieldName="thresholdFieldName"
    ></f-criteria-group>
  </div>
</template>

<script>
import FCriteriaGroup from './FCriteriaGroup'
export default {
  props: [
    'module',
    'value',
    'size',
    'threshold',
    'readingobj',
    'resourceType',
    'thresholdFieldName',
  ],
  components: {
    FCriteriaGroup,
  },
  data() {
    return {
      loading: true,
      moduleMeta: null,
      criteriaGroup: null,
    }
  },
  created() {
    this.$store.dispatch('loadSpaceCategory')
  },
  mounted() {
    let self = this
    this.criteriaGroup = this.parseCriteriaObject()
    this.loadModuleMeta()

    this.$watch(
      'criteriaGroup',
      function(newVal) {
        let criteriaObject = self.constructCriteriaObject(newVal)
        this.$emit('input', criteriaObject)
      },
      {
        deep: true,
      }
    )
  },
  // watch: {
  //   readingobj (val) {
  //     if (this.threshold && this.module === 'asset') {
  //       this.loadModuleMeta()
  //     }
  //   }
  // },
  computed: {
    fieldSize() {
      if (this.size) {
        return this.size
      }
      return 'small'
    },
  },
  methods: {
    thresholdmetric(val) {
      if (
        this.threshold &&
        (this.module === 'asset' || this.module === 'space')
      ) {
        this.$emit(
          'thresholdfield',
          this.moduleMeta.fields.find(field => field.name === val)
        )
      }
    },
    loadModuleMeta() {
      let self = this
      self.loading = true
      if (
        self.threshold &&
        (self.module === 'asset' || self.module === 'space')
      ) {
        let url = null
        if (self.module === 'asset') {
          url =
            '/module/meta?moduleName=' +
            self.module +
            '&assetId=' +
            self.readingobj.id +
            '&categoryId=' +
            self.readingobj.category.id +
            '&resourceType=' +
            self.module
        } else if (self.module === 'space') {
          url =
            '/module/meta?moduleName=' +
            self.module +
            '&assetId=' +
            self.readingobj.id +
            '&categoryId=' +
            self.readingobj.spaceCategory.id +
            '&resourceType=' +
            self.module
        }
        // self.$http.get('/reading/getassetlatestdata?parentId=' + self.readingobj.id + '&parentCategoryId=' + self.readingobj.category.id)
        self.$http.get(url).then(function(response) {
          self.moduleMeta = response.data.meta
          self.loading = false
        })
      } else {
        self.$http
          .get('/module/meta?moduleName=' + this.module)
          .then(function(response) {
            self.moduleMeta = response.data.meta
            self.loading = false
          })
      }
    },
    parseCriteriaObject() {
      let criteriaObj = {
        type: 'f-criteria-group',
        operator: 'all',
        children: [],
      }
      if (this.value) {
        let SPLIT_REGX = /([1-9]\d*)|(\()|(\))|(and)|(or)/gi
        let matchList = this.value.pattern.match(SPLIT_REGX)
        if (matchList && matchList.length) {
          if (matchList.length === 1) {
            matchList.splice(0, 0, '(')
            matchList.splice(2, 0, ')')
          }

          criteriaObj = this.parseCriteriaGroup(matchList, 1)
        }
      }
      return criteriaObj
    },
    parseCriteriaGroup(list, index) {
      let group = {
        type: 'f-criteria-group',
        operator: 'all',
        children: [],
      }
      for (let i = index; i < list.length; i++) {
        let match = list[i]
        if (match === '(') {
          group.children.push(this.parseCriteriaGroup(list, i + 1))
          return group
        } else if (match === ')') {
          return group
        } else if (match === 'or' || match === 'and') {
          group.operator = match === 'or' ? 'any' : 'all'
        } else {
          if (this.value.conditions[match]) {
            let condition = this.value.conditions[match]
            condition.type = 'f-criteria-rule'
            if (condition.field && condition.value && condition.value !== '') {
              if (condition.field.dataTypeEnum._name.indexOf('DATE') !== -1) {
                let arr = condition.value.split(',')
                if (arr.length === 1) {
                  condition.value = new Date(parseInt(arr[0]))
                } else {
                  let dateList = []
                  for (let dateStr of arr) {
                    dateList.push(new Date(parseInt(dateStr)))
                  }
                  condition.value = dateList
                }
              } else if (condition.field.dataTypeEnum._name === 'LOOKUP') {
                condition.value = condition.value.split(',')
              }
            }
            group.children.push(condition)
          }
        }
      }
      return group
    },
    constructCriteriaObject(obj) {
      let pattern = ''
      let conditions = {}
      let seq = 1
      if (obj) {
        pattern = this.constructCriteriaGroup(
          seq,
          conditions,
          obj.operator,
          obj.children
        )
      }

      if (
        pattern !== '' &&
        pattern !== '()' &&
        Object.keys(conditions).length > 0
      ) {
        return {
          pattern: pattern,
          conditions: conditions,
        }
      } else {
        return null
      }
    },
    constructCriteriaGroup(seq, conditions, operator, children) {
      if (!children.length) {
        return ''
      }
      let ptn = '('
      for (let i = 0; i < children.length; i++) {
        let child = children[i]

        if (
          child.type === 'f-criteria-rule' &&
          (!child.fieldName ||
            child.fieldName === '' ||
            !child.operator ||
            child.operator === '')
        ) {
          // if condition is empty, skip it in the criteria
          continue
        } else if (
          child.type === 'f-criteria-group' &&
          !child.children.length
        ) {
          // if condition group is empty, skip it in the criteria
          continue
        }

        if (i !== 0) {
          ptn += ' ' + (operator === 'all' ? 'and' : 'or') + ' '
        }

        if (child.type === 'f-criteria-group' && child.children.length) {
          ptn += this.constructCriteriaGroup(
            seq,
            conditions,
            child.operator,
            child.children
          )
        } else {
          let condition = this.$helpers.cloneObject(child)
          let field = this.moduleMeta.fields.find(
            field => field.name === condition.fieldName
          )
          if (Array.isArray(condition.value)) {
            if (
              condition.value.length === 2 &&
              condition.value[0] instanceof Date
            ) {
              // date range
              let val =
                condition.value[0].getTime() +
                ',' +
                condition.value[1].getTime()
              condition.value = val
            } else {
              condition.value = condition.value.join()
            }
          } else if (condition.value instanceof Date) {
            condition.value = condition.value.getTime()
          }
          if (condition.operator) {
            condition.operatorId = this.moduleMeta.operators[
              field.dataTypeEnum._name
            ][condition.operator].operatorId
            delete condition.operator
          }
          condition.columnName =
            field.extendedModule.tableName + '.' + field.columnName
          ptn += seq
          conditions[seq] = condition
          seq++
        }
      }
      ptn += ')'
      return ptn
    },
    removeChild(index) {
      this.criteriaObj.children.splice(index, 1)
    },
  },
}
</script>
<style>
.f-criteria-builder .f-criteria-group.threshold .el-card.box-card {
  border: none !important;
  margin-top: 0px !important;
  padding-left: 15px !important;
  padding-right: 15px !important;
  background: none !important;
}
.f-criteria-builder .f-criteria-group.threshold .el-card__header {
  padding: 0px !important;
  border-bottom: none !important;
}
.f-criteria-builder .f-criteria-group.threshold .el-card__body {
  padding: 0px !important;
  margin: 0px !important;
  height: 56px !important;
}
.f-criteria-builder
  .f-criteria-group.threshold
  .el-card__body
  .f-criteria-rule {
  margin: 0px !important;
}
.f-criteria-builder
  .f-criteria-group.threshold
  .el-card__body
  .el-card.box-card.active {
  padding: 0px !important;
  margin: 0px !important;
  border: none !important;
  box-shadow: none !important;
}
.f-criteria-builder
  .f-criteria-group.threshold
  .el-card__body
  .el-card.box-card.active
  .el-row.is-align-middle {
  margin: 0px !important;
}

.f-criteria-builder .box-card {
  margin: 8px 0;
  /* -webkit-box-shadow: none !important; */
  -webkit-border-radius: 0 !important;
  box-shadow: none !important;
  width: 75%;
}

.f-criteria-builder .rootgroup .el-card__header {
  background: #938cab;
  padding: 10px;
  height: 40px;
}

.f-criteria-builder .rootgroup .box-card {
  border: solid 1px #938cab;
  background: #fff;
}
.f-criteria-builder .f-criteria-group .el-card__body {
  padding: 13px !important;
  margin-left: 9px;
  margin-right: 7px;
  /* margin-bottom: -16px; */
}
.f-criteria-builder .f-criteria-group.subgroup .box-card .el-card__header {
  padding: 10px;
  background: #dfdce8;
  height: 40px;
}
.f-criteria-builder .f-criteria-group:not(.rootgroup) .el-card {
  position: relative;
  background: #f3f3f9;
  border: 1px;
  border: solid 1px #dfdce8;
  margin-top: 20px;
  margin-bottom: 10px;
  width: 100%;
}
.f-criteria-builder .f-criteria-group:not(.rootgroup) .el-card.active {
  position: relative;
  background: #fff;
  border: 1px bold !important;
  box-shadow: 0 2px 6px 0 #dfdee3 !important;

  margin-bottom: 5px;
  margin-top: 10px;
  width: 100%;
  /* margin-bottom: 15px; */
}

.f-criteria-builder .f-criteria-rule .el-card.normal {
  /* height: 45px; */
  /* background-image: linear-gradient(to right, #675a94 10%, rgba(255, 255, 255, 0) 0%);
     background-position: top;
  background-size: 10px 1px;
  background-repeat: repeat-x; */

  background-color: #f3f3f9;
  border: 1px dashed #675a94;
  margin-top: 10px;
  /* border-width: 1px;
 margin-bottom: 5px;
           margin-top: 5px;


  /* margin-bottom: 13px; */
}

.f-criteria-builder .f-criteria-rule .el-card__body {
  padding: 5px !important;
  margin-top: 0px;
  margin-bottom: 7px;
  margin-left: 2px;
  margin-right: 0px;
  height: 45px;
}
.f-criteria-builder .f-criteria-rule .el-card {
  margin-bottom: 21px;
}
.f-criteria-builder .f-criteria-rule .el-card.active {
  /* height: 60px; */
  background-color: #ffffff !important;
  box-shadow: 0 2px 6px 0 #dfdee3;
  border: solid 1px #bfbbcd;
  box-shadow: 0 2px 6px 0 #dfdee3 !important;
  margin-bottom: 5px;
  margin-top: 10px;
}
.f-criteria-builder .el-select .el-input__inner {
  cursor: pointer !important;
  background: #fff;
}
.f-criteria-builder .f-criteria-rule {
  margin-bottom: 22px;
  background-color: #fff;
}
.dotted-spaced-left {
  background-image: linear-gradient(
    to bottom,
    #333 10%,
    rgba(255, 255, 255, 0) 0%
  );
  background-position: left;
  background-size: 1px 10px;
  background-repeat: repeat-y;
}

.dotted-spaced-right {
  background-image: linear-gradient(
    to bottom,
    #333 10%,
    rgba(255, 255, 255, 0) 0%
  );
  background-position: right;
  background-size: 1px 10px;
  background-repeat: repeat-y;
  padding-right: 0px;
}
.dotted-spaced-top {
  background-image: linear-gradient(
    to right,
    #333 10%,
    rgba(255, 255, 255, 0) 0%
  );
  background-position: top;
  background-size: 10px 1px;
  background-repeat: repeat-x;
}
.dotted-spaced-bottom {
  background-image: linear-gradient(
    to right,
    #333 10%,
    rgba(255, 255, 255, 0) 0%
  );
  background-position: bottom;
  background-size: 10px 1px;
  background-repeat: repeat-x;
}
/* .f-criteria-builder .el-main {
    background: #fff;
} */
.f-criteria-builder .f-criteria-rule .el-card {
  width: 100%;
}
</style>
