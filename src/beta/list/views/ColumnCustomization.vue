<template>
  <el-dialog
    :visible.sync="showDialog"
    :fullscreen="true"
    custom-class="dsm-cc-dialog"
    :append-to-body="true"
    :show-close="false"
  >
    <FContainer position="relative">
      <FContainer
        padding="containerLarge containerXLarge"
        borderBottom="1px solid"
        borderColor="borderNeutralBaseSubtler"
      >
        <FText appearance="headingMed14">{{
          $t('common._common.column_settings')
        }}</FText>
      </FContainer>
      <FContainer
        padding="containerMedium containerXLarge"
        height="calc(100vh - 85px)"
        overflow="scroll"
      >
        <FContainer
          v-for="(field, index) in fixedFields"
          :key="field.id"
          padding="containerXLarge containerMedium"
          backgroundColor="backgroundMidgroundSubtle"
          margin="containerLarge containerNone"
          gap="containerMedium"
          borderRadius="medium"
          class="cc-fixed-selectable-container"
        >
          <FIcon
            group="action"
            name="drag"
            size="16"
            :pressable="false"
          ></FIcon>
          <FCheckbox v-model="selectedFixedFields[index]" :disabled="true">{{
            field.columnDisplayName
          }}</FCheckbox>
        </FContainer>

        <Draggable
          :options="dragOptions"
          :list="sortedFields"
          data-parent="default"
        >
          <FContainer
            v-for="(field, index) in sortedFields"
            :key="field.name"
            padding="containerLarge containerMedium"
            backgroundColor="backgroundMidgroundSubtle"
            margin="containerLarge containerNone"
            borderRadius="medium"
            class="cc-selectable-container"
          >
            <el-input
              ref="columnRenameInp"
              placeholder="Change your column name"
              class="fc-input-border-remove width90"
              :autofocus="true"
              v-if="field.edit"
              @blur="
                field.edit = false
                $emit('update')
              "
              @keyup.enter="
                field.edit = false
                $emit('update')
              "
              v-model="field.columnDisplayName"
            ></el-input>
            <FContainer
              v-else
              display="flex"
              gap="containerMedium"
              alignItems="center"
            >
              <FIcon
                group="action"
                name="drag"
                size="16"
                :pressable="false"
                class="icon-right cursor-drag-pointer"
              ></FIcon>
              <FCheckbox
                v-model="selectedFields[index]"
                @change="setSelectedColumn"
                >{{ field.columnDisplayName }}</FCheckbox
              >
            </FContainer>
            <FIcon
              group="default"
              name="edit"
              size="16"
              @click="() => showRenameColumn(field)"
              class="edit-view-icon"
            /> </FContainer
        ></Draggable>
      </FContainer>

      <FContainer
        padding="containerLarge containerXLarge"
        borderTop="1px solid"
        borderColor="borderNeutralBaseSubtler"
        gap="containerLarge"
        display="flex"
        position="fixed"
        bottom="0px"
        width="100%"
        backgroundColor="backgroundCanvas"
      >
        <FButton @click="save" :loading="isSaving">{{
          isSaving ? $t('common._common._saving') : $t('common._common._save')
        }}</FButton>
        <FButton appearance="secondary" @click="showDialog = false">{{
          $t('common._common.cancel')
        }}</FButton>
      </FContainer>
    </FContainer>
  </el-dialog>
</template>

<script>
import {
  FContainer,
  FText,
  FCheckbox,
  FIcon,
  FButton,
} from '@facilio/design-system'
import ColumnCustomization from '@/ColumnCustomization.vue'
import { isEmpty } from '@facilio/utils/validation'
import Draggable from 'vuedraggable'

export default {
  extends: ColumnCustomization,
  name: 'ColumnCustomization',
  components: { FContainer, FText, FCheckbox, FIcon, FButton, Draggable },
  data: () => ({
    selectedFields: [],
    selectedFixedFields: [],
  }),
  watch: {
    selectedColumns: {
      handler(val) {
        let { sortedFields } = this || {}
        if (!isEmpty(sortedFields)) {
          this.selectedFields = sortedFields.map(field =>
            val.includes(field.name)
          )
        }
      },
      immediate: true,
    },
    selectedFixedColumns: {
      handler(val) {
        let { fixedFields } = this || {}
        if (!isEmpty(fixedFields)) {
          this.selectedFixedFields = fixedFields.map(field =>
            val.includes(field.name)
          )
        }
      },
      immediate: true,
    },
  },
  methods: {
    setSelectedColumn() {
      let { selectedFields, sortedFields } = this || {}
      let selectedFieldList = sortedFields.filter(
        (_, index) => selectedFields[index]
      )
      selectedFieldList = selectedFieldList.map(field => field.name)
      this.selectedColumns = selectedFieldList
    },
  },
}
</script>

<style lang="scss">
.dsm-cc-dialog {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  overflow-y: hidden;
  border-radius: 0px;
  width: 25% !important;
  transition: transform 0.25s ease;
  .el-dialog__header {
    display: none;
  }
  .el-dialog__body {
    padding: 0px;
  }
  .edit-view-icon {
    visibility: hidden;
  }
  .cc-selectable-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    &:hover {
      .edit-view-icon {
        visibility: visible;
      }
    }
  }
  .cc-fixed-selectable-container {
    display: flex;
    align-items: center;
    opacity: 0.7;
    cursor: not-allowed;
  }
  .cursor-drag-pointer {
    cursor: move !important;
  }
}
</style>
