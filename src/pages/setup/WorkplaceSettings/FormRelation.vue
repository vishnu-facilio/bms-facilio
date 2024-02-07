<template>
  <div class="form-relation-container">
    <portal to="booking-policy-settings-header-form-relation">
      <div class="display-flex-between-space m20">
        <div class="setting-title-block">
          <div class="setting-form-title">
            {{ $t('workplace.form_relation.formrelations') }}
          </div>
          <div class="heading-description">
            {{ $t('workplace.form_relation.description') }}
          </div>
        </div>

        <el-select
          @change="init()"
          v-model="appId"
          placeholder="Select App"
          filterable
          class="fc-input-full-border2 width200px pR15"
        >
          <el-option
            v-for="app in apps"
            :key="app.linkName"
            :label="app.name"
            :value="app.id"
          >
          </el-option>
        </el-select>
      </div>
    </portal>
    <div>
      <div v-if="isLoading"></div>
      <div v-else>
        <SetupEmpty
          class="m10"
          :class="resourceEmpty"
          v-if="$validation.isEmpty(spaceCategoryList) && !isLoading"
        >
          <template #emptyImage>
            <inline-svg
              src="svgs/copy2"
              iconClass="icon icon-sm-md"
            ></inline-svg>
          </template>
          <template #emptyHeading>
            {{ $t('workplace.form_relation.no_form_relation') }}
          </template>
        </SetupEmpty>
        <template v-else>
          <div
            v-for="(category, index) in spaceCategoryList"
            :key="index"
            class="visitor-hor-card scale-up-left flex-middle select-input"
          >
            <div>
              <div class="fc-black-15 fwBold">{{ category.name }}</div>
              <div class="fc-grey4-13 pT5">{{ category.description }}</div>
            </div>
            <div>
              <el-select
                :placeholder="$t('common._common.select')"
                v-model="selectedForm[category.id]"
                class="fc-input-full-border-select2  height100 scrollable "
                @change="updateFormRelation(category)"
              >
                <el-option
                  v-for="form in forms"
                  :key="form.id"
                  :label="form.displayName"
                  :value="form.id"
                ></el-option>
              </el-select>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  data() {
    return {
      isLoading: true,
      appId: null,
      apps: [],
      moduleName: 'spacebooking',
      spaceCategoryList: [],
      formRelationList: [],
      forms: [],
      selectedForm: {},
      formRelationData: {
        appId: null,
        parentModuleName: null,
        parentModuleId: null,
        categoryId: null,
        moduleFormId: null,
      },
    }
  },

  async mounted() {
    await this.init()
  },
  components: {
    SetupEmpty,
  },
  computed: {
    resourceEmpty() {
      let { spaceCategoryList } = this
      return !isEmpty(spaceCategoryList)
        ? 'resource-filter-applied-empty'
        : 'resource-empty'
    },
  },
  methods: {
    async init() {
      this.isLoading = true
      await this.setAppId()
      await this.availableApps()
      await this.loadSpaceCategoryList()
      await this.loadForms()
      await this.loadFormRelation()
      await this.afterInithook()
      this.isLoading = false
    },
    afterInithook() {
      this.setSelectedForm()
    },
    setAppId() {
      let { query } = this.$route
      let { appId } = query || {}
      if (!isEmpty(appId) && appId > 0) {
        this.appId = parseInt(appId)
      }
    },
    async availableApps() {
      let { moduleName } = this
      let { data, error } = await API.get('v2/application/fetchList', {
        moduleName,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.apps = data.application || []

        if (isEmpty(this.appId)) {
          this.appId = (
            this.apps.find(app => app.linkName === 'newapp') || this.apps[0]
          ).id
        }
      }
    },
    async loadSpaceCategoryList() {
      let { moduleName } = 'spaceCategory'
      let url = 'spacecategory/getlist'
      let params = { moduleName }
      let { error, data } = await API.get(url, params)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.spaceCategoryList = data.spaceCategories
      }
    },
    async loadForms() {
      let { appId } = this
      let moduleName = 'spacebooking'
      let params = {
        moduleName,
        appId,
      }

      let { data, error } = await API.get('/v2/forms', params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.forms = data.forms || []
      }
    },
    updateSelectedForm() {
      if (this.formRelationList.length) {
        this.formRelationList.forEach(value => {
          if (this.appId === value.appId) {
            this.$set(
              this.selectedForm,
              `${value.categoryId}`,
              value.moduleFormId
            )
          }
        })
      }
    },
    setSelectedForm() {
      this.selectedForm = {}
      if (this.formRelationList.length) {
        this.formRelationList.forEach(value => {
          if (this.appId === value.appId) {
            this.$set(
              this.selectedForm,
              `${value.categoryId}`,
              value.moduleFormId
            )
          }
        })
      }
    },
    async loadFormRelation() {
      let url = 'v3/formrelation/list'

      let { error, data } = await API.get(url)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.formRelationList = data?.getFormRelationData
          ? data.getFormRelationData
          : []
      }
    },

    async updateFormRelation(object) {
      let url = 'v3/formrelation/addOrUpdate'
      let params = {}

      this.formRelationData.appId = this.appId
      this.formRelationData.parentModuleName = 'spacebooking'
      this.formRelationData.categoryId = object.id
      this.formRelationData.moduleFormId = this.selectedForm[object.id]

      params.formRelationData = this.formRelationData

      let { error } = await API.post(url, params)
      if (error) {
        this.$message.error(
          error.message || 'Error occurred while updating Form Relation'
        )
      } else {
        this.showFormCreation = false
        this.$message.success('Updated successfully')
      }
    },
  },
}
</script>
<style scoped lang="scss">
.form-relation-container {
  padding: 0px 20px;
  height: calc(100vh - 206px);
  overflow: scroll;

  .resource-empty {
    height: 75vh !important;
  }

  .q-item-label {
    &:hover {
      color: #46a2bf !important;
      text-decoration: underline !important;
    }
  }
  .select-input {
    justify-content: space-between;
  }
  .visitor-hor-card {
    padding: 20px;
  }
}
</style>
