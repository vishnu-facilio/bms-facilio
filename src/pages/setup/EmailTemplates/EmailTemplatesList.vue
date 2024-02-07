<template>
  <div class="fc-email-template-page">
    <SetupHeader>
      <template #heading>
        {{ $t('setup.emailTemplates.email_templates') }}
      </template>
      <template #description>
        {{ $t('setup.emailTemplates.email_desc') }}
      </template>
      <template #actions>
        <el-button type="primary" class="setup-el-btn" @click="addTemplate">
          {{ $t('setup.emailTemplates.add_template') }}
        </el-button>
      </template>
      <template #filter>
        <div>
          <div class="fc-black-14 text-left bold pB10">
            Modules
          </div>
          <portal-target name="automation-modules"></portal-target>
        </div>
      </template>
    </SetupHeader>

    <setup-loader v-if="loading">
      <template #setupLoading>
        <spinner :show="loading" size="80"></spinner>
      </template>
    </setup-loader>
    <setup-empty v-else-if="$validation.isEmpty(templateList) && !loading">
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('setup.emailTemplates.no_template') }}
      </template>
    </setup-empty>
    <el-table
      v-else
      :data="templateList"
      class="fc-setup-table-th-borderTop fc-setup-table fc-setup-table-p0 m10 fc-email-template-table"
      height="calc(100vh - 300px)"
      :fit="true"
    >
      <el-table-column label="name" width="180">
        <template v-slot="template">
          <div
            class="fc-user-list-hover"
            @click="editTemplateEditor(template.row)"
          >
            {{ template.row.name }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="Created by" width="220">
        <template v-slot="template">
          <UserAvatar
            size="sm"
            :user="$store.getters.getUser(template.row.sysCreatedBy)"
          ></UserAvatar>
        </template>
      </el-table-column>

      <el-table-column label="Created time" width="180">
        <template v-slot="template">
          {{
            $validation.isEmpty(template.row.sysCreatedTime)
              ? '---'
              : $options.filters.formatDate(template.row.sysCreatedTime)
          }}
        </template>
      </el-table-column>
      <el-table-column label="Modified time" width="180">
        <template v-slot="template">
          {{
            $validation.isEmpty(template.row.sysModifiedTime)
              ? '---'
              : $options.filters.formatDate(template.row.sysModifiedTime)
          }}
        </template>
      </el-table-column>
      <el-table-column label="" width="120">
        <template v-slot="template">
          <el-button
            @click="publishTemplate(template.row)"
            v-if="template.row.draft === true"
            class="small-border-btn"
          >
            Publish
          </el-button>
        </template>
      </el-table-column>
      <el-table-column class="visibility-visible-actions" width="120">
        <template v-slot="template">
          <div>
            <i
              v-tippy
              content="Edit template"
              class="el-icon-edit visibility-hide-actions fc-setup-list-edit"
              @click="editTemplateEditor(template.row)"
            ></i>
            <i
              class="el-icon-delete visibility-hide-actions fc-setup-list-delete"
              v-tippy
              content="Delete template"
              @click="deleteTemplate(template.row)"
            ></i>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import SetupHeader from 'pages/setup/components/SetupHeaderTabs'
import { API } from '@facilio/api'
import UserAvatar from '@/avatar/User'
export default {
  props: ['moduleName'],
  data() {
    return {
      loading: true,
      templateList: [],
    }
  },
  components: {
    SetupLoader,
    SetupEmpty,
    SetupHeader,
    UserAvatar,
  },
  computed: {
    selectedModule: {
      get() {
        return this.$route.params.moduleName
      },
      set(moduleName) {
        let { name } = this.$route
        this.$router.replace({
          name,
          params: { moduleName },
        })
      },
    },
  },
  created() {
    this.templateListData()
  },
  methods: {
    async templateListData(force = false) {
      this.loading = true
      let {
        error,
        data,
      } = await API.get(
        `v2/template/email/list?moduleName=${this.selectedModule}&fetchAll=true`,
        { force }
      )
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.templateList = data.emailStructures
        this.$forceUpdate()
      }
      this.loading = false
    },
    editTemplateEditor(template) {
      this.$router.push({
        name: 'emailtemplates.edit',
        params: {
          id: template.id,
        },
      })
    },
    deleteTemplate(template, force = false) {
      this.$dialog
        .confirm({
          title: this.$t('setup.emailTemplates.delete_email_template'),
          message: this.$t(
            'setup.emailTemplates.are_you_sure_you_want_to_delete_this_template'
          ),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(async value => {
          if (!value) return

          let { error } = await API.post(
            '/v2/template/email/delete',
            {
              id: template.id,
            },

            { force }
          )
          if (error) {
            this.$message.error(
              error.message || this.$t('setup.delete.email_template_failed')
            )
          } else {
            this.$message.success(this.$t('setup.delete.delete_template'))
            this.templateListData()
            this.$forceUpdate()
          }
        })
    },
    addTemplate() {
      let { selectedModule } = this
      this.$router.push({
        name: 'emailtemplates.new',
        params: { selectedModule },
      })
    },
    publishTemplate(template) {
      let params = {
        id: template.id,
      }

      let { error } = API.post(`/v2/template/email/publish`, params)
      if (error) {
        this.$message.error(
          error.message || this.$t('setup.delete.email_template_publish_failed')
        )
      } else {
        this.$message.success(
          this.$t('setup.emailTemplates.email_template_publish_successfully')
        )
        this.templateListData()
      }
    },
  },
}
</script>
<style lang="scss">
.fc-email-template-page {
  .setup-header-component {
    position: sticky;
    top: 0;
    z-index: 100;
  }
  .fc-setup-empty,
  .fc-setup-loader {
    margin: 10px;
  }
}
.fc-email-template-table {
  p {
    font-size: 13px;
    font-weight: normal;
    letter-spacing: 0.4px;
    color: #324056;
    margin-bottom: 0;
  }
}
.fc-email-template-page {
  .fc-v1-setup-description {
    max-width: 600px;
  }
}
</style>
