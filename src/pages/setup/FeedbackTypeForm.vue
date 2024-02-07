<template>
  <el-dialog
    :title="$t('common._common.feedback_types')"
    v-bind:append-to-body="false"
    :visible.sync="drawerVisibility"
    v-bind:destroy-on-close="true"
    :width="isEdit ? '40%' : '30%'"
    class="fc-dialog-center-container scale-up-center"
    @close="$emit('update:drawerVisibility', false)"
  >
    <div class="pB100 max-height500">
      <el-form
        @submit.native.prevent="saveRecord"
        ref="feedbackkioskForm"
        :label-position="labelPosition"
        :rules="rules"
        :model="formModel"
      >
        <el-form-item prop="name" class="mb15" :required="true">
          <el-input
            label="Name"
            :placeholder="$t('common._common.enter_name')"
            v-model="formModel.name"
            class="fc-input-full-border-select2"
          ></el-input>
        </el-form-item>

        <el-form-item label="" prop="catalogs" class="mB10" v-if="isEdit">
          <div
            v-if="isServiceCatalogsLoading"
            class="flex-middle height400 m10 fc-agent-empty-state"
          >
            <spinner :show="isServiceCatalogsLoading" size="80"></spinner>
          </div>
          <div
            v-else-if="
              $validation.isEmpty(serviceCatalogs) && !isServiceCatalogsLoading
            "
            class="flex-middle height400 justify-content-center flex-direction-column m10 fc-agent-empty-state"
          >
            <inline-svg
              src="svgs/list-empty"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="q-item-label nowo-label">No Catalogs available</div>
          </div>
          <div v-else>
            <el-table
              ref="elTable"
              :data="serviceCatalogs"
              height="350"
              :fit="true"
              class="feedbacktype-form-table"
              @selection-change="selectCatalogs"
              v-if="!loading && !$validation.isEmpty(serviceCatalogs)"
            >
              <!-- <el-table-column>
            <template v-slot="checked">

            </template>
          </el-table-column> -->
              <el-table-column type="selection" width="60"></el-table-column>
              <el-table-column label="Name">
                <template v-slot="catalog" width="180">
                  <div class="flex-middle">
                    <div v-if="catalog.row.photoUrl" class="pR10">
                      <img
                        :src="catalog.row.photoUrl"
                        width="30"
                        height="30"
                        class="feedback-form-category-img"
                      />
                    </div>
                    <div v-else class="pR10">
                      <img
                        src="~assets/assets.svg"
                        width="30"
                        height="30"
                        class="feedback-form-category-img"
                      />
                    </div>
                    {{ catalog.row.name }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="category" label="category" width="180">
                <template v-slot="catalog">
                  {{ catalog.row.group.name }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <div class="modal-dialog-footer">
      <el-button
        @click="$emit('update:drawerVisibility', false)"
        class="modal-btn-cancel"
        >{{ $t('common.roles.cancel') }}</el-button
      >
      <!-- <el-button type="primary" @click="addVisitorType" class="modal-btn-save"
      >Save</el-button >-->
      <el-button
        :loading="isSaving"
        type="primary"
        class="modal-btn-save"
        @click="saveRecord"
        >{{ $t('common._common.confirm') }}</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import { deepCloneObject } from 'util/utility-methods'
import { areValuesEmpty, isEmpty } from '@facilio/utils/validation'

export default {
  props: ['drawerVisibility', 'isEdit', 'kioskContext'],
  components: {},
  data() {
    return {
      serviceCatalogs: [],
      loading: false,
      catalog: null,
      formModel: {
        name: '',
      },
      labelPosition: 'left',
      isServiceCatalogsLoading: true,
      isSaving: false,
      rules: {
        name: [
          {
            required: true,

            trigger: 'blur',
          },
        ],
      },
      selectedCatlogs: [],
      selectedCatalogList: [],
      selectAll: false,
    }
  },
  mounted() {
    this.loadSericeCatalogs().then(() => {
      if (this.isEdit) {
        this.setFormModel(this.kioskContext)
      }
    })
  },
  computed: {},
  methods: {
    setFormModel(kioskContext) {
      //edit mode , initilize values
      this.formModel.name = kioskContext.name
      kioskContext.catalogs.forEach(catalog => {
        //need to preset selection with service catalog object
        this.$refs['elTable'].toggleRowSelection(
          this.serviceCatalogs.find(e => e.id == catalog.id),
          true
        )
      })
    },
    selectCatalogs(selectedCa) {
      this.formModel.catalogs = selectedCa
    },
    saveRecord() {
      this.$refs['feedbackkioskForm'].validate(valid => {
        if (valid) {
          this.isSaving = true
          let formObj = deepCloneObject(this.formModel)

          this.$emit('save', formObj)
        }
      })
    },

    async loadSericeCatalogs() {
      let resp = await this.$http.get('v2/servicecatalog/list')
      this.serviceCatalogs = resp.data.result.serviceCatalogs
      this.isServiceCatalogsLoading = false
    },
  },
}
</script>

<style lang="scss">
.feedbacktype-form-table {
  th > .cell {
    padding-left: 0;
  }
  .feedback-form-category-img {
    border-radius: 50%;
    box-shadow: 0 4px 15px 0 rgba(14, 15, 43, 0.03);
    border: solid 1px #edf2f4;
  }
}
</style>
