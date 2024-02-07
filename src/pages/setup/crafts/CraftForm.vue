<template>
  <el-dialog
    :visible="dialogVisibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog45 setup-dialog fc-tabs-layout-page"
    style="z-index: 999999"
  >
    <el-form
      :model="craftFormDetails"
      :rules="rules"
      :label-position="'top'"
      ref="craftAndSkills"
      class="fc-form"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{
              isNew
                ? $t('setup.crafts.add_crafts')
                : $t('setup.crafts.edit_crafts')
            }}
          </div>
        </div>
      </div>
      <div v-if="loading" class="new-body-modal crafts-form">
        <div v-for="index in [1, 2, 3]" :key="index">
          <el-row class="mB20">
            <el-col :span="24">
              <span class="lines loading-shimmer width50 mB10"></span>
              <span class="lines loading-shimmer width95 mB10 height40"></span>
            </el-col>
          </el-row>
        </div>

        <el-row class="mB10 mT10">
          <el-col :span="24">
            <span class="lines loading-shimmer width50 mB10"></span>
            <div class="mT10 d-flex">
              <div
                v-for="index in [1, 2, 3]"
                :key="index"
                class="flex-middle flex-grow"
              >
                <span class="circle loading-shimmer"></span>
                <span class="lines loading-shimmer mL10 mR30 flex-grow"></span>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <div v-else class="new-body-modal enpi-body-modal">
        <div class="body-scroll">
          <el-row>
            <el-col :span="24">
              <el-form-item prop="name">
                <p class="fc-input-label-txt">
                  {{ $t('setup.crafts.form.name') }}
                  <span class="mandatory-field-color">*</span>
                </p>

                <el-input
                  class="width100 fc-input-full-border2"
                  autofocus
                  v-model="craftFormDetails.name"
                  type="text"
                  :placeholder="$t('setup.crafts.form.name')"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item prop="desc">
                <p class="fc-input-label-txt">
                  {{ $t('setup.crafts.form.desc') }}
                </p>

                <el-input
                  class="width100 fc-input-full-border-textarea"
                  autofocus
                  v-model="craftFormDetails.description"
                  type="textarea"
                  :autosize="{ minRows: 4, maxRows: 6 }"
                  :placeholder="$t('setup.crafts.form.desc')"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item prop="standardRate">
                <p class="fc-input-label-txt">
                  {{ $t('setup.crafts.form.standardRate') }}
                </p>

                <el-input
                  class="width100 fc-input-full-border2"
                  autofocus
                  v-model="craftFormDetails.standardRate"
                  type="text"
                  :placeholder="$t('setup.crafts.form.standardRate')"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <div>
            <p class="craft-label">
              {{ $t('setup.crafts.form.addSkills') }}
            </p>
            <SkillAddBuilder
              ref="skillAddBuilder"
              :isNew="isNew"
              :editSkills.sync="craftFormDetails.craftSkill"
            >
            </SkillAddBuilder>
          </div>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">
          {{ $t('common._common.cancel') }}
        </el-button>
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="submitForm()"
          :loading="saving"
        >
          {{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}
        </el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import SkillAddBuilder from './skillAddBuilder'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  components: { SkillAddBuilder },
  props: ['isNew', 'selectedCraftId'],
  data() {
    return {
      craftFormDetails: {
        name: '',
        description: '',
        standardRate: '',
        craftSkill: [
          {
            name: '',
            description: '',
            standardRate: '',
            skillLevelRank: '',
          },
        ],
      },
      dialogVisibility: true,
      saving: false,
      loading: false,
      rules: {
        name: {
          required: true,
          message: this.$t(`setup.labour.mandatory_fields.name`),
          trigger: 'blur',
        },
      },
    }
  },
  async created() {
    await this.initCraft()
  },
  methods: {
    submitForm() {
      this.$refs['craftAndSkills'].validate(async value => {
        if (!value) return
        this.saving = true
        this.craftFormDetails.craftSkill = this.serialize_skills()
        let { standardRate } = this.craftFormDetails || {}
        this.craftFormDetails.standardRate = isEmpty(standardRate)
          ? 0.0
          : parseInt(standardRate)
        if (this.isNew) {
          let { error } = await API.createRecord('crafts', {
            data: this.craftFormDetails,
          })
          if (error) {
            this.$message.error(
              error.message || this.$t(`setup.crafts.crafts_add_failure`)
            )
          } else {
            this.$message.success(this.$t(`setup.crafts.crafts_added`))
            this.$emit('onSubmit')
            this.closeDialog()
          }
        } else {
          let deletingSkills = this.$refs['skillAddBuilder'].deleteSkillsList

          if (!isEmpty(deletingSkills)) {
            let { error } = await API.deleteRecord('craftSkill', deletingSkills)
            if (error) {
              let { message } = error
              this.$message.error(
                message || this.$t(`setup.crafts.delete_skills_failed`)
              )
            } else {
              this.$message.success(
                this.$t(`setup.crafts.delete_skills_success`)
              )
            }
          }
          let { error } = await API.updateRecord('crafts', {
            data: this.craftFormDetails,
            id: this.selectedCraftId,
          })
          if (error) {
            this.$message.error(
              error.message || this.$t(`setup.crafts.update_crafts_failure`)
            )
          } else {
            this.$message.success(this.$t(`setup.crafts.update_crafts_success`))
            this.$emit('onSubmit')
            this.closeDialog()
          }
        }
        this.saving = false
      })
    },
    serialize_skills() {
      let { craftSkill } = this.craftFormDetails
      return (craftSkill || []).filter(skill => {
        let { name } = skill || {}
        return !isEmpty(name)
      })
    },
    async initCraft() {
      if (this.isNew) return

      this.loading = true
      let { crafts, error } = await API.fetchRecord('crafts', {
        id: this.selectedCraftId,
      })
      if (error) {
        this.$message.error(
          error.message || this.$t(`setup.crafts.fetch_craft_error`)
        )
      } else {
        this.craftFormDetails = crafts
        let { skills } = crafts || []

        let craftskills = [
          {
            name: '',
            description: '',
            standardRate: '',
            skillLevelRank: '',
          },
        ]
        this.$set(
          this.craftFormDetails,
          'craftSkill',
          !isEmpty(skills) ? skills : craftskills
        )
      }
      this.loading = false
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss" scoped>
.crafts-form {
  .height40 {
    height: 40px;
  }
  .width95 {
    width: 95%;
  }
  .lines {
    height: 15px;
    border-radius: 5px;
  }
}
.mandatory-field-color {
  color: #d54141;
}
.craft-label {
  font-size: 16px;
  font-weight: 500;
  color: #3ab2c1;
  margin-top: 10px;
  margin-bottom: 15px;
}
</style>
