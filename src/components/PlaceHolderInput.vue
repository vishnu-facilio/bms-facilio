<template>
  <div class="placeholder-input ">
    <el-input
      width="250px"
      v-model="valueInput"
      @change="applyChanges"
      class="el-input-textbox-full-border"
    >
    </el-input>
    <div
      class="ghost-click-elemnt"
      @click="visible = false"
      v-if="visible"
    ></div>
    <el-popover placement="left" width="300" trigger="manual" v-model="visible">
      <el-row :gutter="20" class="st-search-bar">
        <el-col :span="22">
          <el-input
            v-model="searchVariables"
            class="input-box-1 mt-1 el-input-textbox-full-border "
            size="medium"
            placeholder="Search variables"
            suffix-icon="el-icon-search"
          ></el-input>
        </el-col>
        <el-col :span="2" class="p0">
          <el-tooltip
            class="item"
            effect="dark"
            content="Close"
            placement="top"
          >
            <div @click="closePopover" class="pointer st-close-icon">
              <i class="el-icon-circle-close"></i>
            </div>
          </el-tooltip>
        </el-col>
      </el-row>
      <div class="filter-area" v-if="selectedField.length">
        <el-tag
          v-for="(field, index) in selectedField"
          :key="index"
          closable
          size="small"
          :type="''"
          @close="handleCloseTag(field)"
          :color="'#deecff'"
        >
          {{ field.displayName }}
        </el-tag>
        <el-button
          plain
          size="mini"
          class="ft-clear-btn"
          @click="handleClearAll()"
          >Clear all</el-button
        >
      </div>
      <div class="checkbox-popover">
        <div
          v-for="(field, index) in filterdFieldList"
          :key="index"
          class="pT10"
        >
          <el-checkbox
            @change="addModuleText(field, moduleName)"
            v-model="field.checked"
            >{{ field.displayName }}</el-checkbox
          >
        </div>
      </div>

      <div slot="reference" @click="visible = !visible">
        <el-tooltip
          class="item"
          effect="dark"
          content="select variables"
          placement="top"
        >
          <el-button class="filter-btn-new">
            <div class="filter-indicator" v-if="selectedField.length"></div>
            <inline-svg
              src="svgs/filter-new"
              iconClass="icon text-center icon-md"
            ></inline-svg>
          </el-button>
        </el-tooltip>
      </div>
    </el-popover>
  </div>
</template>
<script>
export default {
  props: ['fieldList', 'moduleName', 'value'],
  data() {
    return {
      valueInput: '',
      searchVariables: null,
      selectedField: [],
      visible: false,
    }
  },
  mounted() {
    this.valueInput = this.value
    this.getFilterdList()
    // this.fields = this.$helpers.cloneObject(this.fieldList)
  },
  computed: {
    fields: {
      get() {
        return this.$helpers.cloneObject(this.fieldList)
      },
    },
    filterdFieldList() {
      let fields = []
      if (this.fields && this.fields.length) {
        this.fields.forEach(rt => {
          rt['checked'] = this.validateTextField(rt, this.moduleName)
          if (this.searchVariables === null) {
            fields.push(rt)
          } else if (
            rt.displayName
              .toLowerCase()
              .indexOf(this.searchVariables.toLowerCase()) >= 0
          ) {
            fields.push(rt)
          }
        })
      }

      return fields
    },
  },
  methods: {
    closePopover() {
      this.visible = false
    },
    handleCloseTag(field) {
      field.checked = !field.checked

      this.addModuleText(field, this.moduleName)

      // this.$set(field, 'checked', false)
    },
    getText(field, moduleName) {
      let text = '${'
      if (field?.lookupModule?.name) {
        text += `${moduleName}.${field.name}.name`
      } else {
        text += `${moduleName}.${field.name}`
      }
      text += '}'
      return text
    },
    handleClearAll() {
      this.valueInput = ''
      this.applyChanges()
      this.selectedField = []
    },
    validateTextField(field, moduleName) {
      // this method used to validate place holder presention and the will return
      let text = this.getText(field, moduleName)
      let { valueInput } = this
      if (valueInput && valueInput.indexOf(text) > -1) {
        return true
      }
      return false
    },
    applyChanges() {
      // this.$emit('update:value', this.valueInput)
      this.$emit('input', this.valueInput)
      this.getFilterdList()

      // this.setSettings()
    },
    getFilterdList() {
      if (this.fields && this.fields.length) {
        this.selectedField = this.fields.filter(rt => rt.checked)
      }
    },
    addModuleText(field, moduleName) {
      if (field?.checked) {
        this.valueInput += this.getText(field, moduleName)
      } else {
        this.removeModuleText(field, moduleName)
      }
      this.applyChanges()
    },
    removeModuleText(field, moduleName) {
      let text = this.getText(field, moduleName)
      this.valueInput = this.valueInput.replace(text, '')
    },
  },
}
</script>
<style>
.st-close-icon i.el-icon-circle-close:hover {
  color: #38b2c2;
}
.st-search-bar {
  align-items: center;
  display: flex;
}
.ghost-click-elemnt {
  position: fixed;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  background: transparent;
}
.filter-indicator {
  background: #ff3184;
  width: 6px;
  height: 6px;
  z-index: 10;
  position: absolute;
  border-radius: 6px;
  right: 10px;
}
.filter-btn-new {
  padding: 9px;
  margin-left: 10px !important;
  position: relative;
  margin: 0px;
}
.filter-area {
  padding: 10px 0px;
  display: flex;
  flex-wrap: wrap;
}
.placeholder-input {
  display: inline-flex;
  width: 100%;
}
.filter-area .el-tag {
  margin: 2.5px 5px 2.5px 0px;
}
.ft-clear-btn {
  margin: 2.5px 5px 2.5px 0px;
  padding: 5px 8px;
}
</style>
