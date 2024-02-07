<template>
  <div class="tenant-faq-page">
    <el-container>
      <el-header class="tenant-faq-header" height="120px">
        <h1>
          {{ $t('tenant.faq.faq_heading') }}
        </h1>
      </el-header>
      <div
        class="fc-empty-white flex-middle justify-content-center flex-direction-column m10"
        v-if="loading"
      >
        <spinner :show="true" size="80" class="align-center"></spinner>
      </div>
      <div
        v-else-if="!loading && $validation.isEmpty(faqModuleDatas)"
        class="fc-empty-white flex-middle justify-content-center flex-direction-column m10 fc-empty-state-mobile"
      >
        <div class="empty-state-container">
          <inline-svg
            src="svgs/emptystate/readings-empty"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="q-item-label nowo-label">
            {{ $t('tenant.faq.no_data') }}
          </div>
        </div>
      </div>
      <el-main v-else>
        <div v-for="(section, idx) in sections" :key="idx">
          <div class="fc-black-18 pT20 pB15">
            {{ section.name }}
          </div>
          <div class="fc-tenant-faq-collapse-section flex-col-reverse d-flex">
            <el-collapse v-model="activeNames">
              <el-collapse-item
                :name="faq.id"
                v-for="(faq, rdx) in section.questions"
                :key="rdx"
              >
                <template slot="title">
                  <div class="flex-middle">
                    <div class="dot-12 fc-dot-blue-bg"></div>
                    <div class="pL10 fc-collapse-heading">
                      {{ faq.name }}
                    </div>
                  </div>
                </template>
                <div class="fc-tenant-faq-desc">
                  {{ $getProperty(faq, 'data.answer') }}
                </div>
                <div class="fc-faq-image-align">
                  <div
                    v-for="(attachment, idex) in attachmentsData[faq.id]"
                    :key="idex"
                    :value="attachment"
                  >
                    <div
                      class="mR20 pointer"
                      @click="selectedFile(attachment)"
                      v-if="attachment.contentType.includes('image')"
                    >
                      <div>
                        <img
                          :src="$prependBaseUrl(attachment.previewUrl)"
                          alt=""
                          title=""
                          class="fc-faq-img-size mB10"
                        />
                      </div>
                    </div>
                    <div
                      class="mR20 fc-tenant-faq-file-icon pointer"
                      v-else
                      @click="selectedFile(attachment)"
                    >
                      <InlineSvg
                        :src="getFileIcon(attachment)"
                        class="d-flex "
                        iconClass="icon icon-40"
                      >
                      </InlineSvg>
                    </div>
                  </div>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </div>
      </el-main>
    </el-container>
    <preview-file
      :visibility.sync="visibility"
      v-if="visibility && selectedAttachedFile"
      :previewFile="selectedAttachedFile"
      :files="attachments"
    ></preview-file>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import PreviewFile from '@/PreviewFile'
import { isEmpty } from '@facilio/utils/validation'
import sortBy from 'lodash/sortBy'

export default {
  data() {
    return {
      faqModuleDatas: [],
      loading: false,
      activeNames: '',
      attachmentsData: {},
      faqSectionName: [],
      faqSectionData: [],
      attachments: [],
      faqs: {},
      sections: [],
      visibility: false,
      selectedAttachedFile: null,
    }
  },
  created() {
    this.loadTenantFaq()
  },
  filters: {
    trim(value) {
      return value ? value.trim() : '-'
    },
  },
  components: {
    PreviewFile,
  },
  methods: {
    async loadTenantFaq() {
      this.loading = true
      let params = {
        viewName: 'all',
        perPage: 50,
        page: 1,
        includeParentFilter: true,
      }
      let { error, data } = await API.get(
        'v2/module/data/list?moduleName=custom_faq',
        params
      )
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.faqModuleDatas = data.moduleDatas
        this.sections = []
        this.faqs = {}

        let ids = data.moduleDatas.map(rt => rt.data.section.id)
        ids = [...new Set(ids)]

        ids.forEach(id => {
          this.sections.push(
            data.moduleDatas.find(rl => rl.data.section.id === id).data.section
          )
          this.faqs[id] = data.moduleDatas.filter(
            rl => rl.data.section.id === id
          )
        })

        this.faqModuleDatas.forEach(data => {
          this.loadAttachments(data.id)
        })
      }
      this.formatSectiondata()
      this.loading = false
    },
    formatSectiondata() {
      this.sections = sortBy(this.sections, 'data.number')
      this.sections.forEach(section => {
        if (section.id) {
          let questions = sortBy(this.faqs[section.id], 'data.number')
          this.$set(section, 'questions', questions)
        }
      })
    },
    async loadAttachments(id) {
      this.loading = true
      let response = await API.get(
        `attachment?module=cmdattachments&recordId=${id}&parentModuleName=custom_faq`
      )
      if (response.error) {
        let { message } = response.error
        this.$message.error(message)
      } else {
        this.$set(this.attachmentsData, id, response.data.attachments)
      }
      this.loading = false
    },
    selectedFile(inSummary) {
      this.selectedAttachedFile = inSummary
      this.visibility = true
    },

    getFileIcon(attachment) {
      let { contentType } = attachment
      let { FILE_TYPE_ICONS } = this.$constants

      let selectedIndex = FILE_TYPE_ICONS.findIndex(icons => {
        let { fileTypes } = icons
        return fileTypes.some(type => contentType.includes(type))
      })

      if (isEmpty(selectedIndex)) return FILE_TYPE_ICONS[0].path
      else return FILE_TYPE_ICONS[selectedIndex].path
    },
  },
}
</script>
<style lang="scss">
.tenant-faq-page {
  width: 100%;
  background: #f6f5fa;
  padding-bottom: 30px;
  height: 100vh;
  overflow-y: scroll;

  .tenant-faq-header {
    width: 100%;
    height: 100px;
    background: #0a67d6;
    display: flex;

    h1 {
      color: #fff;
      font-size: 25px;
      margin: 0;
      padding-bottom: 30px;
      align-self: flex-end;
      font-weight: 500;
      letter-spacing: 0.5px;
    }
  }

  .fc-black-18 {
    font-size: 18px;
    font-weight: 500;
    line-height: normal;
    letter-spacing: normal;
    color: #2d394d;
  }

  .fc-collapse-heading {
    font-size: 16px;
    font-weight: 500;
    line-height: normal;
    letter-spacing: normal;
    color: #2d394d;
  }

  .fc-tenant-faq-desc {
    font-size: 14px;
    font-weight: normal;
    letter-spacing: normal;
    color: #2d394d;
    white-space: pre-line;
  }

  .fc-tenant-faq-collapse-section {
    width: 100%;
    background: #fff;
    margin-bottom: 2px;

    .fc-dot-blue-bg {
      width: 9px;
      height: 9px;
      background: #0a67d6;
    }

    .el-collapse-item__header {
      height: 60px;
      padding: 0 30px;
      border-bottom: none;
    }

    .el-collapse-item__content {
      padding-left: 52px;
      padding-right: 30px;
    }

    .el-collapse-item__header.is-active {
      border-bottom: none;
    }

    .el-collapse-item__arrow {
      color: #3e4a58;
      font-size: 18px;
      font-weight: 800;
      transform: rotate(90deg);
    }

    .el-collapse-item__arrow.is-active {
      transform: rotate(-90deg);
      color: #0a67d6;
    }

    .el-collapse-item__wrap {
      border-bottom: 1px solid #f8f8f9;
    }
  }

  .el-main {
    padding: 0 20px;
  }

  .el-collapse {
    border-bottom: none;
  }

  .fc-faq-image-align {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    margin-top: 30px;
  }

  .fc-faq-img-size {
    width: 70px;
    height: 70px;
    border-radius: 10px;
    border: 1px solid #e2e8ee;
  }
}

.fc-tenant-faq-file-icon {
  height: 70px;
  width: 70px;
  margin-right: 10px;
  border: solid 1px #e2e8ee;
  border-radius: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media screen and (max-width: 1000px) {
  .tenant-faq-header {
    h1 {
      font-size: 16px !important;
      text-align: center;
      align-self: center !important;
      padding-bottom: 0 !important;
    }
  }

  .tenant-faq-page .fc-faq-img-size {
    width: 100% !important;
  }

  .tenant-faq-page {
    .fc-tenant-faq-collapse-section .el-collapse-item__content {
      padding-left: 30px !important;
      padding-right: 30px !important;
    }

    .fc-tenant-faq-desc {
      font-size: 14px;
      color: #191a45 !important;
      padding-top: 10px;
    }

    .fc-collapse-heading,
    .fc-black-18 {
      color: #191a45;
      font-size: 16px !important;
    }

    .el-collapse-item {
      box-shadow: 0 2px 13px 0 rgb(230 233 236 / 26%);
    }

    .el-collapse-item__header.is-active {
      border-bottom: 1px solid #f2f4fa !important;
    }

    .fc-tenant-faq-collapse-section .el-collapse-item__header {
      border-bottom: 1px solid transparent;
      padding: 20px 0 !important;
      background: none !important;
      margin-left: 20px;
      margin-right: 20px;
      padding-left: 0 !important;
      padding-right: 0 !important;
    }

    .fc-tenant-faq-collapse-section {
      border-radius: 10px;
    }
  }

  .fc-font-size-small {
    .fc-black-14 {
      font-size: 12px !important;
      line-height: 16px;
    }
  }
}

@media screen and (max-width: 780px) {
  .width50 {
    width: 100% !important;
  }

  .fc-border-remove {
    border-bottom: 1px solid #f2f2f2;
  }

  .fc-empty-state-mobile {
    margin: 20px !important;
    width: 90% !important;
  }

  .tenant-faq-header {
    display: none !important;
  }
}
</style>
