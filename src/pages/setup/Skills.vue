<template>
  <div class="fc-form-container full-layout-white">
    <div class="row">
      <div class="col-lg-12 col-md-12">
        <div class="pull-left form-header">Skills</div>
        <div class="action-btn pull-right">
          <button class="btn btn--primary" @click="newSkillDialog">
            New Skill
          </button>
        </div>
      </div>
    </div>
    <div class="row pT30">
      <div class="col-lg-12 col-md-12">
        <table class="fc-list-view-table fc-border-1">
          <thead>
            <tr>
              <th class="text-left" style="width:350px;">
                NAME
              </th>
              <th class="text-left" style="width:350px;">
                SKILL CATEGORY
              </th>
              <th class="text-left" style="width:300px;">
                STATUS
              </th>
              <th class="text-left"></th>
              <th class="text-left"></th>
            </tr>
          </thead>
          <tbody v-if="loading">
            <tr>
              <td colspan="100%" class="text-center">
                <spinner :show="loading" size="80"></spinner>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr class="tablerow" v-for="skill in skilllist" :key="skill.id">
              <td>
                {{ skill.name }}
              </td>
              <td v-if="skill.category !== null">
                {{ getTicketCategory(skill.category.id).displayName }}
              </td>
              <td v-else>
                No Category
              </td>
              <td>
                {{ skill.active }}
              </td>
              <td>
                <button
                  class="btn btn--primary"
                  @click="editSkillDialog(skill)"
                >
                  Edit
                </button>
              </td>
              <td>
                <button
                  class="btn btn--primary"
                  @click="deleteSkill(skill, skill.id)"
                >
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <new-skill ref="newSkill"></new-skill>
    <edit-skill ref="editSkill"></edit-skill>
  </div>
</template>
<script>
import NewSkill from 'pages/setup/NewSkill'
import EditSkill from 'pages/setup/EditSkill'
import { mapGetters } from 'vuex'
export default {
  title() {
    return 'Skills'
  },
  components: {
    NewSkill,
    EditSkill,
  },
  data() {
    return {
      loading: true,
      skilllist: [],
      isNewSkill: false,
    }
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
  },
  mounted: function() {
    this.loadskills()
  },
  computed: {
    ...mapGetters(['getTicketCategory']),
  },
  methods: {
    loadskills: function() {
      let that = this
      that.$http
        .get('/skill/skilllist')
        .then(function(response) {
          that.skilllist = response['data'].skills
          that.loading = false
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    newSkillDialog() {
      this.editingSkill = null
      this.$refs.newSkill.open()
    },
    editSkillDialog(data) {
      this.$refs.editSkill.open(data)
    },
    deleteSkill(data, id) {
      let that = this
      that.$http
        .post('/skill/delete', { skillIds: [id] })
        .then(function(response) {
          that.skilllist.splice(id, 1)
        })
    },
  },
}
</script>
<style scoped>
table.dataTable > tbody > tr > td {
  padding: 10px;
  vertical-align: middle;
  border-spacing: 0;
  border-collapse: collapse;
}

table.dataTable thead > tr > th {
  padding: 5px 10px 2px 10px;
  color: #6f7175;
  vertical-align: top;
  font-weight: 400;
  font-size: 13px;
  border-bottom: 0px;
}

table.dataTable tr.odd {
  background-color: '#fafafa';
}

table.dataTable tr.even {
  background-color: blue;
}

div.dataTables_info {
  padding-top: 8px;
  white-space: nowrap;
  padding-left: 10px;
}

div.dataTables_info,
div.dataTables_paginate {
  padding: 18px;
  white-space: nowrap;
}

div.row-title {
  font-weight: 400;
}

div.row-subtitle {
  font-weight: 400;
  color: #6f7175;
}

.dataTable tbody tr:hover {
  background: #fafafa;
  cursor: pointer;
}

.dataTable tr th .checkbox {
  padding-left: 17px !important;
}

.dataTable tbody tr:last-child td {
  border-bottom: 1px solid #e7e7e7 !important;
}

.dataTable > tbody > tr:first-child > td {
  border-top: 0px;
}

div.row.content-center {
  padding-top: 100px;
  padding-bottom: 144px;
}

table.dataTable.dtr-inline.collapsed > tbody > tr > td:first-child:before,
table.dataTable.dtr-inline.collapsed > tbody > tr > th:first-child:before {
  background-color: #50ca7c;
  font-size: 16px;
  line-height: 16px;
  display: none;
}
.no-screen-msg .row-title {
  font-size: 17px;
  color: #212121;
  padding: 10px 0;
}
.no-screen-msg .row-subtitle {
  font-size: 13px;
  padding: 1px 0px;
}

.dataTable tbody tr.selected {
  background: rgba(14, 153, 227, 0.1);
}
.record-list,
.record-summary {
  padding: 0;
  transition: all 0.3s;
}
.more-actions .dropdown-toggle {
  color: #d8d8d8;
  font-size: 18px;
}

.more-actions .dropdown-toggle:hover {
  color: #000000;
}

.more-actions .dropdown-menu {
  right: 0;
  left: initial;
}
.toggle-switch label {
  position: relative;
  display: block;
  height: 12px;
  width: 30px;
  background: #999;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.08s linear;
}

.toggle-switch label:after {
  position: absolute;
  left: 0;
  top: -2px;
  display: block;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #fafafa;
  box-shadow: rgba(0, 0, 0, 0.4) 0px 1px 3px 0px;
  content: '';
  transition: 0.08s linear;
}

.toggle-switch label:active:after {
  transform: scale(1.15, 0.85);
}

.toggle-switch .checkbox:checked ~ label {
  background: rgba(80, 202, 124, 0.5);
}

.toggle-switch .checkbox:checked ~ label:after {
  left: 14px;
  background: #50ca7c;
}

.toggle-switch .checkbox:disabled ~ label {
  background: #d5d5d5;
  cursor: not-allowed;
  pointer-events: none;
}

.toggle-switch .checkbox:disabled ~ label:after {
  background: #bcbdbc;
}
</style>
