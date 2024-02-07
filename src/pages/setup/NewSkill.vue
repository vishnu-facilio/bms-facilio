<template>
  <f-form
    ref="newSkillForm"
    type="dialog40"
    :title="formTitle"
    :model="newSkillmodel"
    @save="save"
  >
    <template v-slot:form="fscope">
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <!-- {{$store.state.ticketcategory}} -->
          <q-input
            v-model="fscope.formModel.name"
            class="form-item"
            float-label="Name"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-input
            v-model="fscope.formModel.description"
            class="form-item"
            float-label="Description"
            type="textarea"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-select
            v-model="fscope.formModel.category.id"
            class="form-item"
            float-label="Category"
            :options="$store.state.ticketCategory | options"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="12" class="form-item">
          IS Active
        </el-col>
        <el-col :span="12">
          <q-toggle v-model="fscope.formModel.active" float-label="Is Active" />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24"> </el-col>
      </el-row>
    </template>
  </f-form>
</template>
<script>
import { QInput, QSelect, QToggle } from 'quasar'
import FForm from '@/FForm'
import clone from 'lodash/clone'

export default {
  props: ['data'],
  components: {
    QInput,
    QSelect,
    FForm,
    QToggle,
  },
  data() {
    return {
      formTitle: 'New Skill',
      newSkillmodel: {
        name: '',
        description: '',
        active: true,
        category: {
          id: -1,
        },
      },
      supportedStates: [true, false],
    }
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
  },
  methods: {
    open() {
      this.$refs.newSkillForm.open()
      this.categories = clone(this.$store.state.ticketCategory)
    },
    close() {
      this.$refs.newSkillForm.close()
    },
    save() {
      let formModel = this.$refs.newSkillForm.getFormModel()
      let self = this
      self.$http
        .post('/skill/add', { skill: formModel })
        .then(function(response) {
          let resp = response.data
          self.$message.success(' New Skill added Successfully.')
          self.close()
        })
    },
  },
}
</script>
