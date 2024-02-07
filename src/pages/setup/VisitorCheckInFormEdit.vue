<template>
  <div class="position-relative">
    <div class="visitor-setting-con-width mT20">
      <div class="visitor-hor-card scale-up-left">
        <el-row class="flex-middle">
          <el-col :span="22">
            <div class="fc-black-15 fwBold">{{ 'Check-In form' }}</div>
            <div class="fc-grey4-13 pT5">
              {{ 'Kiosk Check-in form edit option' }}
            </div>
          </el-col>
          <el-col :span="2" class="text-right">
            <div>
              <div class="">
                <el-button
                  type="primary"
                  class="el-button setup-el-btn el-button--primary scale-up-left"
                  @click="openFormBuilder"
                  >{{ $t('common._common.edit') }}</el-button
                >
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <div class="visitor-setup-form">
      <el-dialog
        v-if="canShowFormBuilder"
        :fullscreen="true"
        :append-to-body="true"
        :visible.sync="canShowFormBuilder"
      >
        <FormsEdit
          v-if="canShowFormBuilder"
          :moduleName="moduleName"
          :id="activeFormId"
          :onSave="closeForm"
          :showDeleteForField="({ name }) => true"
          :isUpdateForm="true"
          class="formbuilder-container"
        />
      </el-dialog>
    </div>
  </div>
</template>

<script>
import FormsEdit from 'pages/setup/modules/FormsEdit'

export default {
  created() {
    this.form = this.formsList[0]
  },
  components: {
    FormsEdit,
  },
  name: 'Customization',
  props: ['formsList'],
  data() {
    return {
      form: null,
      canShowFormBuilder: false,
      activeFormId: null,
    }
  },
  computed: {
    moduleName() {
      return 'visitorlog'
    },
  },
  methods: {
    openFormBuilder() {
      this.activeFormId = this.form.id
      this.canShowFormBuilder = true
    },

    closeForm() {
      this.activeFormId = null
      this.canShowFormBuilder = false
    },
  },
}
</script>
<style lang="scss" scoped>
.fc-layout-aside {
  z-index: none !important;
}
</style>
