<template>
  <div class="fc-formlayout">
    <div v-if="loading" style="padding: 50px">Loading...</div>
    <div class="fc-form-container" v-else>
      <div class="form-header">{{ layout.title }}</div>
      <el-form
        ref="newRecordForm"
        @submit.prevent="save"
        class="fc-form pT15"
        :label-position="'top'"
        :model="model"
        :rules="rules"
      >
        <el-row :gutter="30" align="middle">
          <el-col
            v-for="(panel, index) in formlayout"
            :key="index"
            :md="panel.display === 'HALF' ? 12 : 24"
            :lg="panel.display === 'HALF' ? 12 : 24"
          >
            <el-form-item
              v-for="(field, fidx) in panel.fields"
              v-if="field.displayType"
              :key="fidx"
              :label="field.displayName"
              :prop="field.fieldKey"
            >
              <el-input
                v-if="
                  field.displayType === 'TEXTBOX' ||
                    field.displayType === 'EMAIL'
                "
                v-model="model[field.fieldKey]"
              ></el-input>

              <el-input
                type="textarea"
                v-else-if="field.displayType === 'TEXTAREA'"
                :rows="4"
                v-model="model[field.fieldKey]"
              ></el-input>

              <el-input-number
                v-else-if="
                  field.displayType === 'NUMBER' ||
                    field.displayType === 'DECIMAL'
                "
                v-model="model[field.fieldKey]"
              ></el-input-number>

              <el-switch
                v-else-if="field.displayType === 'DECISION_BOX'"
                v-model="model[field.fieldKey]"
              ></el-switch>

              <el-date-picker
                v-else-if="field.displayType === 'DATE'"
                v-model="model[field.fieldKey]"
                :type="'date'"
              ></el-date-picker>

              <el-date-picker
                v-else-if="field.displayType === 'DATETIME'"
                v-model="model[field.fieldKey]"
                :type="'datetime'"
              ></el-date-picker>

              <el-select
                v-else-if="field.options"
                filterable
                v-model="model[field.fieldKey]"
              >
                <el-option
                  v-for="(option, index) in field.options"
                  :key="index"
                  :label="option.label"
                  :value="option.value"
                ></el-option>
              </el-select>

              <el-input
                v-else-if="
                  field.displayType === 'SELECTBOX' &&
                    field.displayName !== 'Country'
                "
                v-model="model[field.fieldKey]"
              ></el-input>

              <el-input
                v-else-if="
                  field.dataType === 'LOOKUP' &&
                    field.displayType === 'LOOKUP_POPUP'
                "
                placeholder="Select records"
                :readonly="true"
                suffix-icon="el-icon-search"
                v-model="model[field.fieldKey]"
              ></el-input>
            </el-form-item>
          </el-col>
          <slot></slot>
          <el-col :md="24" :lg="24" class="form-footer text-right">
            <el-form-item>
              <el-button @click="cancel">Cancel</el-button>
              <el-dropdown
                :loading="saving"
                v-if="layout.commands"
                type="primary"
                split-button
                @click="save"
                trigger="click"
                @command="handleSaveCommand"
              >
                Save
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item
                    v-for="(command, index) in layout.commands"
                    :key="index"
                    :command="command.command"
                    >{{ command.label }}</el-dropdown-item
                  >
                </el-dropdown-menu>
              </el-dropdown>
              <el-button
                type="primary"
                v-else
                @click="save"
                :loading="saving"
                >{{ saving ? 'Saving...' : 'Save' }}</el-button
              >
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>
<script>
import countries from 'util/data/countries'
import { mapState, mapGetters } from 'vuex'
import { getFieldOptions } from 'util/picklist'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  props: ['layout'],
  data() {
    return {
      loading: true,
      formlayout: null,
      model: null,
      rules: {},
      dataTypeMapping: {},
      checked: false,
      saving: false,
      templatesaving: false,
      pickListData: {},
      assets: [],
    }
  },
  async created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadGroups')
    this.loadAssetPickListData()
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      locations: state => state.locations,
      ticketstatus: state => state.ticketStatus.workorder,
      ticketpriority: state => state.ticketPriority,
      ticketcategory: state => state.ticketCategory,
    }),
  },
  ...mapStateWithLogging({
    spaces: state => state.spaces,
  }),
  ...mapGetters([
    'getTicketCategoryPickList',
    'getTicketStatusPickList',
    'getTicketPriorityPickList',
    'getSpaceCategoryPickList',
  ]),
  ticketStatusMap() {
    return this.getTicketStatusPickList('workorder')
  },
  spacecategory() {
    return this.getSpaceCategoryPickList()
  },
  mounted() {
    let self = this
    if (self.layout.params) {
      self.$watch(
        'layout.params',
        function() {
          for (let fieldKey of self.layout.params) {
            self.model[fieldKey] = self.layout.params[fieldKey]
          }
        },
        {
          deep: true,
        }
      )
    }

    self.$http
      .get(self.layout.metaurl)
      .then(function(response) {
        let resp = response.data
        self.formlayout = resp.formlayout

        let modelObj = {}
        for (let x in self.formlayout) {
          for (let y in self.formlayout[x].fields) {
            let field = self.formlayout[x].fields[y]
            let fieldKey = field.inputName.replace(/\./g, '_')

            if (field.specialType === 'users') {
              field.options = []

              for (let uidx in self.users) {
                let user = self.users[uidx]

                let userOption = {}
                userOption.label = user.name + ' (' + user.email + ')'
                userOption.value = user.id
                field.options.push(userOption)
              }
            } else if (field.specialType === 'groups') {
              field.options = []

              for (let gidx in self.groups) {
                let group = self.groups[gidx]

                let option = {}
                option.label = group.name
                option.value = group.groupId
                field.options.push(option)
              }
            } else if (
              field.displayType === 'LOOKUP_POPUP' &&
              field.lookupModule &&
              field.lookupModule.name === 'location'
            ) {
              field.options = []
              if (self.locations) {
                Object.keys(self.locations).forEach(function(key) {
                  let option = {}
                  option.label = self.locations[key]
                  option.value = key
                  field.options.push(option)
                })
              }
            } else if (
              field.displayType === 'LOOKUP_POPUP' &&
              field.lookupModule &&
              field.lookupModule.name === 'basespace'
            ) {
              field.options = []

              for (let sidx in self.spaces) {
                let space = self.spaces[sidx]

                let option = {}
                option.label = space.name + ' (' + space.spaceTypeVal + ')'
                option.value = space.id
                field.options.push(option)
              }
            } else if (
              field.displayType === 'LOOKUP_POPUP' &&
              field.lookupModule &&
              field.lookupModule.name === 'site'
            ) {
              field.options = []
              Object.keys(self.spaces).forEach(function(key) {
                let option = {}
                if (self.spaces[key].spaceTypeVal === 'Site') {
                  option.label = self.spaces[key].name
                  option.value = self.spaces[key].id
                  field.options.push(option)
                }
              })
            } else if (
              field.displayType === 'LOOKUP_POPUP' &&
              field.lookupModule &&
              field.lookupModule.name === 'building'
            ) {
              field.options = []

              Object.keys(self.spaces).forEach(function(key) {
                let option = {}
                if (self.spaces[key].spaceTypeVal === 'Building') {
                  option.label = self.spaces[key].name
                  option.value = self.spaces[key].id
                  field.options.push(option)
                }
              })
            } else if (
              field.displayType === 'LOOKUP_POPUP' &&
              field.lookupModule &&
              field.lookupModule.name === 'floor'
            ) {
              field.options = []

              Object.keys(self.spaces).forEach(function(key) {
                let option = {}
                if (self.spaces[key].spaceTypeVal === 'Floor') {
                  option.label = self.spaces[key].name
                  option.value = self.spaces[key].id
                  field.options.push(option)
                }
              })
            } else if (
              field.displayType === 'LOOKUP_POPUP' &&
              field.lookupModule &&
              field.lookupModule.name === 'asset'
            ) {
              field.options = []

              Object.keys(self.assets).forEach(function(key) {
                let option = {}
                option.label = self.assets[key]
                option.value = key
                field.options.push(option)
              })
            } else if (
              field.displayType === 'LOOKUP_SIMPLE' &&
              field.lookupModule.name === 'ticketstatus'
            ) {
              field.options = []

              Object.keys(self.ticketStatusMap).forEach(function(key) {
                let option = {}
                option.label = self.ticketStatusMap[key]
                option.value = key
                field.options.push(option)
              })
            } else if (
              field.displayType === 'LOOKUP_SIMPLE' &&
              field.lookupModule.name === 'ticketpriority'
            ) {
              field.options = []

              Object.keys(self.getTicketPriorityPickList()).forEach(function(
                key
              ) {
                let option = {}
                option.label = self.getTicketPriorityPickList()[key]
                option.value = key
                field.options.push(option)
              })
            } else if (
              field.displayType === 'LOOKUP_SIMPLE' &&
              field.lookupModule.name === 'ticketcategory'
            ) {
              field.options = []

              Object.keys(self.getTicketCategoryPickList()).forEach(function(
                key
              ) {
                let option = {}
                option.label = self.getTicketCategoryPickList()[key]
                option.value = key
                field.options.push(option)
              })
            } else if (
              field.displayType === 'LOOKUP_SIMPLE' &&
              field.lookupModule.name === 'spaceCategory'
            ) {
              field.options = []

              Object.keys(self.spacecategory).forEach(function(key) {
                let option = {}
                option.label = self.spacecategory[key]
                option.value = key
                field.options.push(option)
              })
            } else if (
              field.displayType === 'LOOKUP_SIMPLE' ||
              field.displayType === 'LOOKUP_POPUP'
            ) {
              field.options = []
              self.pickListData[field.lookupModule.name] = true
              self.loadPickList(field.lookupModule.name, field)
            } else if (
              field.displayType === 'SELECTBOX' &&
              field.inputName === 'location.country'
            ) {
              field.options = countries
            }

            if (field.displayType === 'DECISION_BOX') {
              modelObj[fieldKey] = false
            }

            if (self.layout.params && self.layout.params[fieldKey]) {
              modelObj[fieldKey] = self.layout.params[fieldKey]
            }
            if (typeof modelObj[fieldKey] === 'undefined') {
              modelObj[fieldKey] = ''
            }
            self.formlayout[x].fields[y].fieldKey = fieldKey
            if (field.required) {
              self.rules[fieldKey] = []
              self.rules[fieldKey].push({
                required: true,
                message: 'Please input ' + field.displayName,
                trigger: 'blur',
                type: field.dataTypeEnum.toLowerCase(),
              })
            }

            self.dataTypeMapping[fieldKey] = field.displayType
          }
        }
        self.model = modelObj
        if (!Object.keys(self.pickListData).length) {
          self.loading = false
        }
      })
      .catch(() => {})
  },
  methods: {
    async loadAssetPickListData() {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'asset', skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.assets = options
      }
    },
    async loadPickList(moduleName, field) {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: moduleName },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.pickListData[moduleName] = false
        field = { ...field, options }
      }
      this.loading = false
    },
    saveTemplate() {
      let self = this
      self.templatesaving = true
      this.$refs['newRecordForm'].validate(valid => {
        if (valid) {
          let formData = self.transformAsContextObj(self.model)
          this.$emit('onsavetemplate', formData)
        } else {
          self.templatesaving = false
          return false
        }
      })
    },
    save() {
      let self = this

      self.saving = true
      this.$refs['newRecordForm'].validate(valid => {
        if (valid) {
          let formData = self.transformAsContextObj(self.model)

          if (!self.layout.module) {
            this.$emit('onsave', formData)
          } else {
            let data = {}
            if (formData[self.layout.module]) {
              data = formData
            } else {
              data[self.layout.module] = formData
            }
            self.$http
              .post(self.layout.saveurl, data)
              .then(function(response) {
                if (typeof response.data === 'object') {
                  const alert1 = Alert.create({
                    html: self.layout.module + ' created successfully!',
                    color: 'positive',
                    position: 'top-center',
                  })
                  setTimeout(function() {
                    alert1.dismiss()
                  }, 1500)

                  let newpath = self.$route.path.replace('/new', '')
                  self.$router.replace({ path: newpath })
                  self.$emit('saved', response.data)
                } else {
                  const alert3 = Alert.create({
                    html: self.layout.module + ' creation failed!',
                    color: 'negative',
                    position: 'top-center',
                  })
                  setTimeout(function() {
                    alert3.dismiss()
                  }, 1500)
                  self.saving = false
                }
              })
              .catch(function(error) {
                const alert2 = Alert.create({
                  html:
                    self.layout.module + ' creation failed! [ ' + error + ']',
                  color: 'negative',
                  position: 'top-center',
                })
                setTimeout(function() {
                  alert2.dismiss()
                }, 1500)
                self.saving = false
              })
          }
        } else {
          self.saving = false
          return false
        }
      })
    },
    handleSaveCommand(cmd) {
      let self = this
      self.saving = true
      this.$refs['newRecordForm'].validate(valid => {
        if (valid) {
          let formData = self.transformAsContextObj(self.model)
          this.$emit('onsaveas', {
            save_as: cmd,
            formdata: formData,
          })
        } else {
          self.saving = false
          return false
        }
      })
    },
    cancel() {
      this.$emit('cancel', true)
    },
    transformAsContextObj(model) {
      let self = this
      let contextObj = {}

      Object.keys(model).forEach(function(key) {
        if (model[key]) {
          let modules = key.split('_')

          let obj = contextObj
          for (let x in modules) {
            if (parseInt(x) === modules.length - 1) {
              let dataType = self.dataTypeMapping[key]
              if (dataType === 'DATE' || dataType === 'DATETIME') {
                obj[modules[x]] = Date.parse(model[key])
              } else {
                obj[modules[x]] = model[key]
              }
            } else {
              if (!obj[modules[x]]) {
                obj[modules[x]] = {}
              }
              obj = obj[modules[x]]
            }
          }
        }
      })
      return contextObj
    },
  },
}
</script>
<style>
.form-section {
  font-weight: 500;
  color: #333;
  margin-bottom: -15px;
}

.fc-form-container .el-form--label-top .el-form-item__label {
  padding-bottom: 0px !important;
}

.fc-form-container .fc-form .el-select,
.fc-form-container .fc-form .el-input {
  width: 100%;
}
</style>
