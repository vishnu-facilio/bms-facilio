<template>
  <div class="">
    <div class="visitor-hor-card scale-up-left" v-if="loading">
      <spinner :show="loading" size="80"> </spinner>
    </div>

    <div
      class="text-center fc-empty-white flex-middle justify-center flex-col"
      v-else-if="$validation.isEmpty(customkioskbutton)"
    >
      <InlineSvg
        src="svgs/emptystate/approval"
        iconClass="icon icon-xxxlg vertical-middle mR10"
      >
      </InlineSvg>
      <div class="nowo-label">
        {{ $t('common._common.no_button_available') }}
      </div>
    </div>

    <div v-else class="mT20 feedback-device-table">
      <el-table
        stripe
        :data="customkioskbutton"
        style="width: 100%"
        height="auto"
      >
        <el-table-column label="Button Name" width="230">
          <template v-slot="data">
            <div class="mL10">
              <div
                class="label-txt3-14"
                style="padding-top: 13px;padding-left: 9px;"
              >
                {{ data.row.label }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="connectedApp Widgets" width="160">
          <template v-slot="data">
            <div class="label-txt3-14">
              <div class="mL10">
                <div class="label-txt3-14" style="padding-left:9px;">
                  {{ data.row.connectedAppWidgetContext[0].widgetName }}
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="100" class="visibility-visible-actions">
          <template v-slot="data">
            <i
              class="el-icon-edit visibility-hide-actions "
              @click="onEdit(data.row)"
            ></i>
            <i
              class="el-icon-delete delete-icon-danger visibility-hide-actions pL20"
              @click="onDelete(data.row)"
            ></i>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <kioskButtonForm
      v-if="showForm"
      :isEdit="isEdit"
      :kioskContext="device"
      :drawerVisibility.sync="showForm"
      @save="handleFormSubmit"
    ></kioskButtonForm>
  </div>
</template>

<script>
import kioskButtonForm from 'pages/setup/KioskButtonForm'
import FSearch from '@/FSearch'
import draggable from 'vuedraggable'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
export default {
  title() {
    return 'Kiosk Button'
  },
  components: {
    kioskButtonForm,
    FSearch,
    draggable,
  },

  data() {
    return {
      nameValidation: {
        name: [
          {
            required: true,
            message: 'Name cannot be empty',
            trigger: 'blur',
          },
        ],
      },
      isEdit: false, //form mode , edit or new
      loading: false,
      customkioskbutton: [],
      connectedAppWidgetContext: [],
      showForm: false,
      input: '',
      selectedFeedbackType: null,
      EditselectedType: false,
      saving: false,
    }
  },
  created() {
    this.loadButtonList()
  },
  methods: {
    openCatalogDialog(feedbackType) {
      this.catalogList = feedbackType.catalogs
      this.showCatalogDialog = true
    },
    async loadButtonList() {
      this.loading = true
      let queryParam = {
        moduleName: 'customkioskbutton',
      }

      let { data } = await API.get('/v3/modules/data/list', queryParam)
      this.customkioskbutton = data?.customkioskbutton
        ? data.customkioskbutton
        : []
      this.customkioskbutton.forEach(element => {
        let { connectedAppWidgetContext } = element || {}
        this.connectedAppWidgetContext = this.connectedAppWidgetContext.concat(
          connectedAppWidgetContext
        )
      })
      this.loading = false
    },

    openAddButtonDialog() {
      this.isEdit = false
      this.showForm = true
    },

    async handleFormSubmit(formModel) {
      if (this.isEdit) {
        let params = { moduleName: 'customkioskbutton', data: formModel }
        params = { ...params, id: formModel.id }
        this.$http.post('/v3/modules/data/update', params).then(() => {
          this.showForm = false
          this.loadButtonList()
        })
      } else {
        let formObj = formModel
        let params = { moduleName: 'customkioskbutton', data: formModel }
        this.$http.post('/v3/modules/data/create', params).then(() => {
          this.saving = true
          this.showForm = false
          this.loadButtonList()
        })
      }
    },
    onEdit(device) {
      this.device = device
      this.isEdit = true
      this.showForm = true
    },
    onDelete(data) {
      this.$dialog
        .confirm({
          title: this.$t('common._common.delete_button_title'),
          message: this.$t('common._common.delete_button_message'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.deleteButton(data.id)
          }
        })
    },

    async deleteButton(Id) {
      let moduleName = 'customkioskbutton'
      let { data, error } = await API.delete('v3/modules/data/delete', {
        data: { customkioskbutton: [Id] },
        moduleName,
      })
      if (!isEmpty(data)) {
        this.loadButtonList()
      } else if (error.code != 0) {
        this.$message.error(error.message)
      }
    },
  },
}
</script>
<style lang="scss"></style>
