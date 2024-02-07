<template>
  <div>
    <el-form :rules="rules" ref="naming-form" :model="viewNamingSection">
      <el-form-item
        :label="$t('viewsmanager.naming.display_name')"
        prop="displayName"
      >
        <TextInput
          v-model="viewNamingSection.displayName"
          :placeholder="placeholder"
        />
      </el-form-item>
      <el-form-item
        :label="$t('viewsmanager.naming.folder_name')"
        prop="groupId"
      >
        <el-select
          v-model="viewNamingSection.groupId"
          placeholder="Select Folder"
          class="fc-input-full-border2 width100"
          filterable
          :loading="isLoading"
          popper-class="view-creation-folder-selection-popper"
        >
          <el-option
            v-for="(group, index) in groupViews"
            :key="index"
            :label="group.displayName || group.name"
            :value="group.id"
          ></el-option>
          <template #empty>
            <el-button
              @click="showFolderCreationDialog = true"
              class="pL20 width100 text-left addFolder-btn"
            >
              + {{ $t('viewsmanager.list.add_folder') }}
            </el-button>
          </template>
          <div class="search-select-filter-btn">
            <el-button
              @click="showFolderCreationDialog = true"
              class="pL20 width100 text-left addFolder-btn"
            >
              + {{ $t('viewsmanager.list.add_folder') }}
            </el-button>
          </div>
        </el-select>
      </el-form-item>
    </el-form>
    <FolderCreation
      v-if="showFolderCreationDialog"
      :moduleName="moduleName"
      :appId="appId"
      @onClose="showFolderCreationDialog = false"
      @onSave="onUpdateGroupId"
    ></FolderCreation>
  </div>
</template>
<script>
import FolderCreation from '../FolderCreation.vue'
import { mapState } from 'vuex'
import { TextInput } from '@facilio/ui/forms'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['viewDetail', 'appId', 'saveAsNew', 'moduleName', 'isLoading'],
  components: { FolderCreation, TextInput },
  data() {
    return {
      placeholder: 'Enter View Name',
      showFolderCreationDialog: false,
      viewNamingSection: {
        displayName: null,
        groupId: null,
      },
      rules: {
        displayName: {
          validator: function(rule, value, callback) {
            if (isEmpty(value)) {
              callback(new Error(this.$t('common._common.please_enter_a_name')))
            } else {
              callback()
            }
          }.bind(this),
          required: true,
          trigger: 'blur',
        },
        groupId: {
          validator: function(rule, value, callback) {
            if (isEmpty(value)) {
              callback(new Error('Please select view group folder'))
            } else {
              callback()
            }
          }.bind(this),
          required: true,
          trigger: 'change',
        },
      },
    }
  },
  created() {
    this.initData()
  },
  computed: {
    ...mapState('view', {
      groupViews: state => state.groupViews,
    }),
    isNewView() {
      let { viewDetail, saveAsNew } = this
      return isEmpty(viewDetail) || !!saveAsNew
    },
  },
  methods: {
    initData() {
      let { viewDetail, isNewView } = this

      if (!isEmpty(viewDetail)) {
        let { displayName, groupId, id, name } = viewDetail || {}
        if (!isNewView) {
          this.viewNamingSection = { displayName, id, name }
        }
        this.$set(this.viewNamingSection, 'groupId', groupId || null)
      }
    },
    onUpdateGroupId(val) {
      this.viewNamingSection.groupId = val.id
    },
    async validate() {
      try {
        return await this.$refs['naming-form'].validate()
      } catch {
        return false
      }
    },
    serializeData() {
      return this.viewNamingSection
    },
  },
}
</script>
<style lang="scss">
.view-creation-folder-selection-popper {
  .el-select-dropdown__list {
    padding-bottom: 42px;
  }
  .addFolder-btn {
    height: 40px;
    width: 100%;
    text-align: left;
    color: #3ab2c1;
    border: none;
    font-weight: 500;

    &:hover {
      background-color: #f5f7fa;
    }
  }
}
</style>
