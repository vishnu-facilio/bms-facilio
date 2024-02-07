<template>
  <div class="height100vh">
    <div class="height100">
      <div class="setting-header2">
        <div class="setting-title-block">
          <div class="setting-form-title">Bill Template</div>
          <div class="heading-description">List of all Bill Template</div>
        </div>
        <div class="action-btn setting-page-btn">
          <!-- <button hidden class="btn btn--tertiary" :class="{disabled}" @click="generateBill()" >Generate Bill</button> -->
          <el-button
            type="primary"
            class="plain"
            @click="generateBill()"
            style="display:none;"
            >Generate Bill</el-button
          >
          <generate-bill
            :visibility.sync="genbill"
            :tempName="tempJson"
          ></generate-bill>
          <!-- <button class="btn btn--primary" @click="newtemplate()">Add Template</button> -->
          <el-button type="primary" class="setup-el-btn" @click="newtemplate()">
            {{ $t('setup.setupLabel.add_template') }}
          </el-button>
          <new-template
            :visibility.sync="newtemp"
            @onsave="appendTemplate"
          ></new-template>
        </div>
      </div>
      <div class="container-scroll">
        <div class="row setting-Rlayout mT30">
          <div class="col-lg-12 col-md-12">
            <table class="setting-list-view-table">
              <thead>
                <tr>
                  <th class="setting-table-th setting-th-text">
                    Template Name
                  </th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr
                  class="tablerow"
                  v-for="(template, index) in templates"
                  :key="index"
                >
                  <td>{{ template.name }}</td>
                  <td>
                    <button
                      class="small-border-btn"
                      @click="generateBill(template.id, template.name)"
                    >
                      Generate Bill
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import NewTemplate from 'pages/setup/bill/NewTemplate'
import GenerateBill from 'pages/setup/bill/GenerateBill'
export default {
  data() {
    return {
      newtemp: false,
      genbill: false,
      templates: [],
      tempJson: {
        templateHeader: '',
        tempId: 0,
      },
      actions: {
        generate: {
          loading: false,
        },
      },
    }
  },
  title() {
    return 'Bill Template'
  },
  components: {
    NewTemplate,
    GenerateBill,
  },
  mounted() {
    this.loadTemplates()
  },
  methods: {
    appendTemplate(tName) {
      this.templates.push({ name: tName.name, id: tName.id })
    },
    loadTemplate() {},
    loadTemplates() {
      let self = this
      self.$http.get('/tenantbilling/loadtemplates').then(function(response) {
        self.templates = response.data.excelTemplates
      })
    },
    closeCreateDialog() {
      this.$refs.createRuleModel.close()
    },
    newtemplate() {
      this.newtemp = true
      this.genbill = false
    },
    generateBill(id, name) {
      this.genbill = true
      this.newtemp = false
      this.tempJson.templateHeader = name
      this.tempJson.tempId = id
    },
  },
}
</script>
<style>
.fc-list-view-table tbody tr.tablerow:hover {
  background: #fafbfc;
}
.tablerow {
  cursor: pointer;
}
.tablerow.active {
  background: #f3f4f7 !important;
  border-left: 3px solid #ff2d81 !important;
  border-right: 1px solid rgba(232, 232, 232, 0.35) !important;
  border-top: 1px solid rgba(232, 232, 232, 0.35) !important;
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}

.fc-white-theme .tablerow.selected {
  background: #e2f1ef;
  border-left: 3px solid #28b2a4;
  border-right: 1px solid rgba(232, 232, 232, 0.35);
  border-top: 1px solid rgba(232, 232, 232, 0.35);
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
.fc-black-theme .tablerow.selected {
  background: hsla(0, 0%, 100%, 0.1) !important;
  border-left: 3px solid #28b2a4;
  border-right: 1px solid rgba(232, 232, 232, 0.35);
  border-top: 1px solid rgba(232, 232, 232, 0.35);
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
</style>
