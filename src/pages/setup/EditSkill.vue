<template>
  <f-form
    ref="editSkillForm"
    type="dialog40"
    :title="formTitle"
    v-if="skillObjectModel"
    :model="skillObjectModel"
    @save="save"
  >
    <template v-slot:form="fscope">
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
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
      formTitle: 'Edit Skill',
      skillObjectModel: null,
    }
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
  },
  methods: {
    open(skill) {
      skill.category = { id: skill.category.id }
      let self = this
      this.skillObjectModel = skill
      this.$nextTick(function() {
        self.$refs.editSkillForm.open()
      })
    },
    close() {
      this.$refs.editSkillForm.close()
    },
    save() {
      let formModel = this.$refs.editSkillForm.getFormModel()
      console.log('save called ', formModel)
      let self = this
      self.$http
        .post('/skill/update', { skill: formModel, skillIds: [formModel.id] })
        .then(function(response) {
          let resp = response.data
          console.log('####### resp :', resp)
          self.$message.success('Skill updated Successfully.')
          self.close()
        })
    },
  },
}
</script>
