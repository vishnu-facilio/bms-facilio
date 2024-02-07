<template>
  <div class="column">
    <div class="card_header">
      <span>{{ header }}</span>
      <div v-if="details.length" class="right-header pointer">
        <span
          class="m5"
          @click="openFailureCodeForm(failureCodeName, displayName, null)"
        >
          {{ this.$t(`common.failure_class.create_new`) }}
        </span>
        <span
          class="m5"
          @click="addFailureCodeDialog(failureCodeName, displayName)"
        >
          {{ this.$t(`common.failure_class.add_existing`) }}
        </span>
      </div>
    </div>

    <div class="card_body">
      <div v-if="!details || !details.length" class="margin-auto">
        <inline-svg
          src="svgs/spacemanagement/ic-nodata"
          iconClass="text_center icon icon-xxxlg"
          class="mL35 svg_center"
        ></inline-svg>
        <div class="mT10 label-txt-black f14 text_center">
          {{ this.$t(`common.failure_class.no_${failureCodeName}_associated`) }}
        </div>
        <div class="right-header text_center">
          <span
            class="m5 createBtn"
            v-bind:class="{
              disable: !hideCreateNew,
            }"
            @click="openFailureCodeForm(failureCodeName, displayName, null)"
          >
            {{ this.$t(`common.failure_class.create_new`) }}
          </span>
          <span
            class="m5 createBtn"
            v-bind:class="{
              disable: !hideCreateNew,
            }"
            @click="addFailureCodeDialog(failureCodeName, displayName)"
          >
            {{ this.$t(`common.failure_class.add_existing`) }}
          </span>
        </div>
      </div>
      <div
        v-else
        class="content"
        v-for="(item, index) in details"
        v-bind:key="`${item.id}-${index}`"
        v-bind:class="{ active: currentId === item.id }"
      >
        <div class="content_header" @click="activeRecordId(item)">
          <div class="code_header">{{ item.code }}</div>
          <div
            class="description f14 truncate-text"
            :title="getDescription(item, true) || '---'"
            v-tippy="{
              placement: 'top',
              animation: 'shift-away',
              arrow: true,
            }"
          >
            {{ getDescription(item) || '---' }}
          </div>
        </div>
        <div :key="`${item.id}-${index}`" class="edit_delete">
          <i
            class="el-icon-edit edit"
            @click="openFailureCodeForm(failureCodeName, displayName, item)"
          >
          </i>
          <i
            class="el-icon-delete edit"
            data-arrow="true"
            :title="$t('common._common.edit')"
            @click="deleteRecords(item.id, failureCodeName, displayName)"
          ></i>
        </div>
        <div v-if="currentId === item.id" class="arrow">
          <i class="el-icon-arrow-right"></i>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      canShowWizard: false,
      failurecodeField: {
        displayName: this.$t('common.header.failure_code'),
        name: 'failurecode',
        lookupModule: {
          displayName: this.$t('common.header.failure_code'),
          name: 'failurecode',
        },
        multiple: true,
        selectedItems: [],
      },
      showViewMore: false,
      loading: false,
      codeName: null,
      codeDisplayName: null,
      showCreateNewDialog: false,
      showEditDeleteId: null,
    }
  },
  props: [
    'details',
    'header',
    'failureCodeName',
    'displayName',
    'currentId',
    'hideCreateNew',
  ],
  methods: {
    openFailureCodeForm(codeName, displayName, data) {
      if (this.hideCreateNew)
        this.$emit('openFailureCodeForm', codeName, displayName, data)
    },
    activeRecordId(item) {
      if (this.failureCodeName === 'failurecodeproblems') {
        this.$emit('activeRecordId', item)
      } else if (this.failureCodeName === 'failurecodecauses') {
        this.$emit('activeCauseRecordId', item)
      }
    },
    addFailureCodeDialog(value, displayName) {
      if (this.hideCreateNew) {
        this.$emit('showFailureCodeDialog', value, displayName)
      }
    },
    async deleteRecords(id, codeName, displayName) {
      this.$emit('deleteRecords', id, codeName, displayName)
    },
    getDescription(item, ignoreLength = false) {
      const LENGTH = 30
      let description = this.$getProperty(item, 'description', 'NA')

      if (description.length > LENGTH && !ignoreLength) {
        this.showViewMore = true
        return description.slice(0, LENGTH) + '...'
      } else {
        return description
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.card_header {
  letter-spacing: 0.5px;
  color: #324056;
  font-size: 12px;
  font-weight: 500;
  background-color: #f7faff;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  border-bottom: 0.5px solid #ebedf4;
}
.code_header {
  letter-spacing: 0.5px;
  color: #324056;
  font-size: 14px;
  font-weight: 500;
  padding: 0 0 5px 0;
  width: 100%;
}

.column {
  border-top: 0.5px solid #ebedf4;
  border-left: 0.5px solid #ebedf4;
  border-bottom: 0.5px solid #ebedf4;
  flex: 1;
  height: 100%;
  width: 340px;
}
.column:nth-child(3) {
  border-right: 0.5px solid #ebedf4;
}
.card_body {
  height: 300px;
  overflow-y: scroll;
  display: flex;
  flex-direction: column;
}
.content {
  display: flex;
  justify-content: space-between;
  cursor: pointer;
  width: 100%;
}
.content_header {
  display: flex;
  flex-direction: column;
  padding: 15px;
  height: 100%;
  width: 248px;
  padding: 16px 0px 16px 24px;
}
.content:hover {
  background-color: #f7f7f7;
}
.content.active {
  background-color: #fafafa;
  border-left: 3px solid #39b2c2;
}
.content span {
  margin: 2px 0 0 0;
}

.svg_center {
  display: flex;
  justify-content: center;
  padding: 0 35px 0 0;
}
.text_center {
  cursor: pointer;
  text-align: center;
  margin: 8px 0 0 4px;
}
.description {
  opacity: 0.5;
  font-size: 12px;
  letter-spacing: 0.16px;
  color: #324056;
  width: 100%;
  overflow-y: scroll;
  text-align: left;
}

.content:hover > .arrow {
  display: none;
}

.content:hover .edit_delete {
  visibility: visible;
}
.edit_delete {
  display: flex;
  margin: auto 0px;
  cursor: pointer;
  visibility: hidden;
}
.edit {
  width: 20px;
  height: fit-content;
  border-radius: 50%;
  margin-right: 10px;
  display: flex;
  justify-content: center;
  padding: 3px;
}

.edit:hover {
  background-color: #d7d4d4;
}
.arrow {
  padding: 0px 14px 0 0;
  margin: auto 0px;
}
.m5 {
  margin: 5px;
}
.createBtn.disable {
  color: grey;
}
.right-header {
  color: #2b8bff !important;
  font-size: 12px;
  letter-spacing: 0.12px;
}
</style>
