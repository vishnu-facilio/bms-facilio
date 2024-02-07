<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    :before-close="closeDialog"
    custom-class="fc-dialog-form fc-dialog-right custom-rule-dialog setup-dialog40 setup-dialog"
    style="z-index: 1999"
  >
    <el-form
      :model="relation"
      ref="ruleForm"
      :rules="rules"
      :label-position="'top'"
    >
      <div
        class="new-header-container mR30 pL30 new-header-modal new-header-text"
      >
        <div class="setup-modal-title">
          {{
            isNew
              ? $t('setup.relationship.header_new')
              : $t('setup.relationship.header_edit')
          }}
        </div>
      </div>
      <div v-if="loading" class="new-body-modal relationship-form pL30 pR30">
        <el-row class="mB20">
          <el-col :span="24">
            <span class="lines loading-shimmer width50 mB10"></span>
            <span class="lines loading-shimmer width95 mB10 height40"></span>
          </el-col>
        </el-row>
        <el-row class="mB20">
          <el-col :span="24">
            <span class="lines loading-shimmer width50 mB10"></span>
            <span class="lines loading-shimmer width95 mB10 height80"></span>
          </el-col>
        </el-row>
        <el-row class="mB20">
          <el-col :span="24">
            <span class="lines loading-shimmer width50 mB10"></span>
            <span class="lines loading-shimmer width95 mB10 height40"></span>
          </el-col>
        </el-row>

        <el-row class="mB20">
          <el-col :span="12">
            <span class="lines loading-shimmer width50 mB10"></span>
            <span class="lines loading-shimmer width95 mB10 height40"></span>
          </el-col>
          <el-col :span="12">
            <span class="lines loading-shimmer width50 mB10"></span>
            <span class="lines loading-shimmer width95 mB10 height40"></span>
          </el-col>
        </el-row>
      </div>

      <div v-else class="new-body-modal pL30 pR30">
        <el-row>
          <el-form-item prop="name">
            <p class="fc-input-label-txt">
              {{ $t('setup.relationship.relation_title') }}
            </p>
            <el-input
              :placeholder="$t('setup.relationship.title_placeholder')"
              :autofocus="true"
              v-model="relation.name"
              class="width100 pR20"
            ></el-input>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item prop="description">
            <p class="fc-input-label-txt">
              {{ $t('setup.relationship.description') }}
            </p>
            <el-input
              v-model="relation.description"
              :min-rows="1"
              :placeholder="$t('setup.relationship.description_placeholder')"
              type="textarea"
              :autosize="{ minRows: 2, maxRows: 4 }"
              class="fc-input-full-border-select2 width100"
              resize="none"
            ></el-input>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item prop="relationType">
            <p class="fc-input-label-txt">
              {{ $t('setup.relationship.relation_type') }}
            </p>
            <el-select
              v-model="relation.relationType"
              :multiple="false"
              :disabled="!isNew"
              filterable
              allow-create
              default-first-option
              :placeholder="$t('setup.relationship.type_placeholder')"
              class="width100 pR20"
            >
              <el-option
                v-for="(relationTypeName, relationType) in relationTypeVsName"
                :key="`relationType-${relationType}-${relationTypeName}`"
                :label="relationTypeName"
                :value="parseInt(relationType)"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item prop="fromModuleId">
              <p class="fc-input-label-txt">
                {{ $t('setup.relationship.from_module') }}
              </p>
              <el-input
                v-model="relation.fromModuleDisplayName"
                :disabled="true"
                class="width100 pR20"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="toModuleId">
              <p class="fc-input-label-txt">
                {{ $t('setup.relationship.to_module') }}
              </p>
              <el-select
                v-model="relation.toModuleId"
                :multiple="false"
                :disabled="!isNew"
                filterable
                :placeholder="$t('setup.relationship.to_module_placeholder')"
                allow-create
                default-first-option
                :loading="moduleListLoading"
                class="width100 pR20"
              >
                <el-option-group
                  v-for="(moduleObj, moduleGrpName, modGrpIdx) in modulesList"
                  :key="`module-grp-${moduleGrpName}-${modGrpIdx}`"
                  :label="moduleGrpName.toUpperCase()"
                >
                  <el-option
                    v-for="(list, index) in moduleObj"
                    :key="`${list.moduleId}-${index}`"
                    :label="list.displayName"
                    :value="list.moduleId"
                  ></el-option>
                </el-option-group>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-form-item prop="relationName">
            <p class="fc-input-label-txt">
              {{ $t('setup.relationship.forward_relation') }}
            </p>
            <el-input
              :autofocus="true"
              :placeholder="
                $t('setup.relationship.forward_relation_placeholder')
              "
              v-model="relation.relationName"
              class="width100 pR20"
            ></el-input>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item prop="reverseRelationName">
            <p class="fc-input-label-txt">
              {{ $t('setup.relationship.reverse_relation') }}
            </p>
            <el-input
              :autofocus="true"
              :placeholder="
                $t('setup.relationship.reverse_relation_placeholder')
              "
              v-model="relation.reverseRelationName"
              class="width100 pR20"
            ></el-input>
          </el-form-item>
        </el-row>
      </div>
      <div class="modal-dialog-footer">
        <el-form-item class="mB0">
          <el-button
            @click="closeDialog"
            class="modal-btn-cancel text-uppercase"
            >{{ $t('setup.relationship.cancle') }}</el-button
          >
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="save"
            :loading="saving"
          >
            {{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}
          </el-button>
        </el-form-item>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { RelationShip } from './RelationshipModel'

export default {
  props: [
    'relationId',
    'moduleName',
    'moduleDisplayName',
    'relationTypeVsName',
  ],
  data() {
    return {
      relation: {},
      modulesList: [],
      moduleListLoading: false,
      saving: false,
      loading: false,
      rules: {
        name: [
          {
            required: true,
            message: 'Please enter a title',
            trigger: 'blur',
          },
        ],
        relationType: [
          {
            required: true,
            message: 'Please select a relationship type',
            trigger: 'change',
          },
        ],

        toModuleId: [
          {
            required: true,
            message: 'Please select a module',
            trigger: 'change',
          },
        ],
        relationName: [
          {
            required: true,
            message: 'Please enter a forward relationship name',
            trigger: 'blur',
          },
        ],
        reverseRelationName: [
          {
            required: true,
            message: 'Please enter a reverse relationship name',
            trigger: 'blur',
          },
        ],
      },
    }
  },
  async created() {
    await this.init()
  },
  computed: {
    isNew() {
      return isEmpty(this.relationId)
    },
  },
  methods: {
    async init() {
      let { relationId, moduleName, moduleDisplayName } = this

      if (this.isNew) {
        this.relation = new RelationShip({
          moduleName,
          fromModuleDisplayName: moduleDisplayName,
        })
      } else {
        try {
          this.loading = true
          this.relation = await RelationShip.fetch({
            id: relationId,
            moduleName,
          })
        } catch (errorMsg) {
          this.$message.error(errorMsg)
        }
      }
      await this.getModulesList()
      this.loading = false
    },
    async getModulesList() {
      this.moduleListLoading = true
      let { error, data } = await API.get('/v3/modules/list/all')

      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.modulesList = {
          'System Modules': data['systemModules'],
          'Custom Modules': data['customModules'],
        }
        let fromModule = data['systemModules'].find(list => {
          return list.name == this.moduleName
        })
        if (isEmpty(fromModule)) {
          fromModule = data['customModules'].find(list => {
            return list.name == this.moduleName
          })
        }

        this.relation.fromModuleId = fromModule?.moduleId || null
      }
      this.moduleListLoading = false
    },
    closeDialog() {
      this.$emit('onClose')
    },
    async save() {
      await this.$refs['ruleForm'].validate(async valid => {
        if (valid) {
          this.saving = true
          try {
            await this.relation.save()
            if (this.isNew)
              this.$message.success(this.$t('setup.relationship.add_success'))
            else {
              this.$message.success(
                this.$t('setup.relationship.update_success')
              )
            }
            this.$emit('onSave')
            this.closeDialog()
          } catch (errorMsg) {
            this.$message.error(errorMsg)
          }
          this.saving = false
        }
      })
    },
  },
}
</script>
<style lang="scss" scoped>
.relationship-form {
  .height40 {
    height: 40px !important;
  }
  .width95 {
    width: 95%;
  }
  .lines {
    height: 15px;
    border-radius: 5px;
  }
  .height80 {
    height: 80px !important;
  }
}
</style>
