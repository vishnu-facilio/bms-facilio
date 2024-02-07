<template>
  <f-form
    ref="editSpaceCategoryForm"
    type="dialog40"
    :title="formTitle"
    v-if="spaceCategorymodel"
    :model="spaceCategorymodel"
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
        <el-col :span="12" class="form-item mT20">
          {{ $t('common._common.is_common_area') }}
        </el-col>
        <el-col :span="12" class="mT20">
          <q-toggle
            v-model="fscope.formModel.commonArea"
            :float-label="$t('common._common.is_common_area')"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24"> </el-col>
      </el-row>
    </template>
  </f-form>
</template>
<script>
import { QInput, QToggle } from 'quasar'
import FForm from '@/FForm'

export default {
  props: ['data'],
  components: {
    QInput,
    FForm,
    QToggle,
  },
  data() {
    return {
      formTitle: 'Edit Category',
      spaceCategorymodel: null,
    }
  },
  created() {
    this.$store.dispatch('loadSpaceCategory')
  },
  methods: {
    open(spaceCategory) {
      let self = this
      this.spaceCategorymodel = spaceCategory
      this.$nextTick(function() {
        self.$refs.editSpaceCategoryForm.open()
      })
    },
    close() {
      this.$refs.editSpaceCategoryForm.close()
    },
    save() {
      let formModel = this.$refs.editSpaceCategoryForm.getFormModel()
      console.log('save called ', formModel)
      console.log('save called ', { spaceCategory: formModel })
      let self = this
      self.$http
        .post('/spacecategory/update', {
          spaceCategory: formModel,
          spaceCategoryIds: [formModel.id],
        })
        .then(function(response) {
          let resp = response.data
          console.log('####### resp :', resp)
          self.$message.success(
            this.$t('common._common.space_category_updated_success')
          )
          self.close()
        })
    },
  },
}
</script>
