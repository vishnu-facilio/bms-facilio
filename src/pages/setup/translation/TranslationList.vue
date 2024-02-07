<template>
  <div class="page-container">
    <div class="header">
      <div>
        <div class="header-title">{{ $t('setup.translation.title') }}</div>
        <div class="header-content">
          {{ $t('setup.translation.list_of_languages') }}
        </div>
      </div>
      <div
        class="add-button"
        v-if="!isEmpty(unusedLanguages) && !isEmpty(configuredLanguages)"
      >
        <el-button type="primary" class="setup-el-btn" @click="showPopup = true"
          >{{ $t('setup.translation.add') }}
        </el-button>
      </div>
    </div>

    <Spinner v-if="loadingList" :show="loadingList" size="80"></Spinner>
    <div v-else class="lang-list">
      <div v-if="isEmpty(configuredLanguages)" class="empty-container">
        <InlineSvg
          src="svgs/emptystate/readings-empty"
          iconClass="icon text-center icon-130 emptystate-icon-size"
        ></InlineSvg>
        <div class="empty-state-text">
          {{ $t('setup.translation.empty_state_text') }}
        </div>
        <div class="add-button">
          <el-button
            type="primary"
            class="setup-el-btn"
            @click="showPopup = true"
            >{{ $t('setup.translation.add') }}
          </el-button>
        </div>
      </div>
      <div v-else class=" setting-list-view-table">
        <el-row class="header-row">
          <el-col :span="16" class="setting-table-th setting-th-text">
            {{ $t('setup.translation.list_title_name') }}
          </el-col>
          <el-col
            :span="4"
            class="setting-table-th setting-th-text text-center"
          >
            {{ $t('setup.translation.list_title_status') }}
          </el-col>
        </el-row>
        <el-row
          v-for="language in configuredLanguages"
          :key="language.value"
          class="body-row tablerow visibility-visible-actions"
        >
          <el-col :span="16" class="body-row-cell pL30">
            {{ language.label }}
          </el-col>
          <el-col :span="4" class="body-row-cell text-center pL45">
            <el-switch
              v-model="language.status"
              @change="changeStatus(language)"
              class="Notification-toggle"
              active-color="rgba(57, 178, 194, 0.8)"
              inactive-color="#e5e5e5"
            ></el-switch>
          </el-col>
          <el-col :span="4" class="text-right">
            <i
              class="el-icon-edit edit-icon visibility-hide-actions cursor-pointer"
              :title="$t('setup.translation.hover_edit_text')"
              data-arrow="true"
              v-tippy
              @click="goToDetails(language.value)"
            ></i>
          </el-col>
        </el-row>
      </div>
      <el-dialog
        :title="$t('setup.translation.add')"
        :visible.sync="showPopup"
        width="40%"
        :append-to-body="true"
        style="z-index: 9999999999;"
        class="language-popup"
      >
        <div
          v-for="language in unusedLanguages"
          :key="language.value"
          :class="[
            'popup-item',
            selectedLanguage == language.value && 'selected',
            'd-flex',
          ]"
          @click="selectedLanguage = language.value"
        >
          <div
            :class="[
              'tick-mark',
              selectedLanguage == language.value && 'selected',
            ]"
          >
            <InlineSvg src="tick-sign" iconClass="icon fill-green"></InlineSvg>
          </div>
          {{ language.label }}
        </div>

        <div class="d-flex justify-center p20">
          <AsyncButton
            buttonType="primary"
            class="setup-el-btn"
            :clickAction="() => addLanguage(selectedLanguage)"
            :disabled="!selectedLanguage"
          >
            {{ $t('setup.translation.proceed') }}
          </AsyncButton>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import util from 'util/constant'
import { API } from '@facilio/api'
import AsyncButton from '@/AsyncButton'
const { languages } = util
import Spinner from '@/Spinner'
import { isEmpty } from '@facilio/utils/validation'

export default {
  components: {
    AsyncButton,
    Spinner,
  },
  data() {
    return {
      showPopup: false,
      selectedLanguage: null,
      configuredLanguages: [],
      unusedLanguages: languages,
      loadingList: true,
    }
  },
  created() {
    this.getLanguagesList()
  },
  watch: {
    showPopup: function(val) {
      if (!val) this.selectedLanguage = null
    },
  },
  methods: {
    isEmpty,
    async changeStatus({ status, value }) {
      const { error } = await API.post('v2/translation/updateStatus', {
        langCode: value,
        status,
      })
      if (error) {
        this.$message.error(error.message || 'Error occurred')
      } else {
        this.$message.success('Changes saved')
      }
    },
    async getLanguagesList() {
      this.loadingList = true
      const { error, data } = await API.get('v2/translation/listLang')
      if (error) {
        this.$message.error(error || 'Error occurred')
      } else {
        let { translatedLang } = data

        // exclude default language
        const defaultLang = this.$account.org.language
        const nonDefaultLanguages = languages.filter(lang => {
          return lang.value !== defaultLang
        })
        this.configuredLanguages = translatedLang
          .map(lang => {
            const { langCode, status } = lang
            const currentLang = nonDefaultLanguages.find(
              l => l.value === langCode
            )
            if (currentLang) {
              return { ...currentLang, status }
            }
            return currentLang
          })
          .filter(l => !isEmpty(l))

        this.unusedLanguages = nonDefaultLanguages.filter(l => {
          return !translatedLang.map(lang => lang.langCode).includes(l.value)
        })
      }
      this.loadingList = false
    },
    goToDetails(languageValue) {
      let route = {
        name: 'translation-details',
        params: {
          language: languageValue,
        },
      }

      this.$router.push(route).catch(() => {})
    },
    addLanguage(code) {
      return API.get('/v2/translation/add', {
        langCode: code,
      }).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error occurred')
        } else {
          this.goToDetails(code)
        }
        this.showPopup = false
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.page-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;

  .padding {
    padding-bottom: 20px;
  }

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 80px;
    padding: 20px;
    background-color: white;
  }

  .header-title {
    font-size: 18px;
    letter-spacing: 0.5px;
    color: #324056;
    padding-bottom: 5px;
  }

  .header-content {
    font-size: 13px;
    letter-spacing: 0.5px;
    color: #808080;
  }

  .lang-list {
    flex: 1;
    background-color: white;
    margin: 20px;

    .empty-container {
      height: 100%;
      display: flex;
      justify-content: center;
      flex-direction: column;
      align-content: center;
      align-items: center;

      .empty-state-text {
        font-size: 14px;
        letter-spacing: 0.5px;
        text-align: center;
        color: #324056;
        padding: 15px;
      }
    }

    .add-button {
      margin-bottom: 20px;
    }

    .header-row {
      border-bottom: 1px solid #f2f5f6;
    }

    .body-row {
      padding: 15px 30px 15px 0;
      border-bottom: 1px solid #f7f8f9;
      &:hover {
        border: 1px solid #b0dbe1;
      }
    }
  }
}

.popup-item {
  font-size: 14px;
  padding: 10px 20px;
  &:hover {
    cursor: pointer;
    background-color: #f3fafb;
  }

  .tick-mark {
    margin: 0 5px;
    visibility: hidden;
  }

  .selected {
    visibility: visible;
  }
}

.selected {
  background-color: #f3fafb;
}
</style>

<style lang="scss">
.language-popup {
  .el-dialog__body {
    padding: 0 !important;
  }
}
</style>
