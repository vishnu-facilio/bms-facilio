<template>
  <div>
    <el-row>
      <el-col :span="14">
        <el-form-item prop="fieldId" label="Show Section" class="mB10">
          <el-select
            v-model="showField"
            placeholder="Select the field"
            class="fc-input-full-border2 width100 fc-tag"
            filterable
            @change="showSection"
            :multiple="true"
            collapse-tags
          >
            <el-option
              v-for="(sections, type) in showSectionsList"
              :key="`actionType-${type}`"
              :label="sections.sectionName"
              :value="sections.id"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="14">
        <el-form-item prop="fieldId" label="Hide Section" class="mB10">
          <el-select
            v-model="hideField"
            placeholder="Select the field"
            class="fc-input-full-border2 width100 fc-tag"
            filterable
            @change="hideSection"
            :multiple="true"
            collapse-tags
          >
            <el-option
              v-for="(sections, type) in hideSectionsList"
              :key="`actionType-${type}`"
              :label="sections.sectionName"
              :value="sections.id"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <span slot="footer" class="modal-dialog-footer row">
      <el-button class="col-6 modal-btn-cancel" @click="close()"
        >Cancel</el-button
      >

      <el-button type="primary" class="col-6 modal-btn-save" @click="save()"
        >Ok</el-button
      >
    </span>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: {
    sectionsList: {
      default: () => [],
      type: Array,
    },
    presentAction: {
      default: () => {},
      type: Object,
    },
  },
  data() {
    return {
      showField: null,
      hideField: null,
      hideSectionsList: null,
      showSectionsList: null,
      isSaved: false,
      showSectionActionList: {
        type: 3,
        actionType: 'show_section',
        action_meta: {
          actionId: -1,
          action_detail: { section_ids: null },
        },
        target_widgets: null,
      },
      hideSectionActionList: {
        type: 4,
        actionType: 'hide_section',
        action_meta: {
          actionId: -1,
          action_detail: {
            section_ids: null,
          },
        },
        target_widgets: null,
      },
    }
  },
  created() {
    this.hideSectionsList = this.presentSectionsList
    this.showSectionsList = this.presentSectionsList
    if (!isEmpty(this.presentAction)) {
      this.existingAction()
      this.isSaved = true
    }
  },
  methods: {
    existingAction() {
      let { presentAction } = this || {}
      let { showSection } = presentAction || {}
      let { hideSection } = presentAction || {}
      if (!isEmpty(showSection) && !isEmpty(hideSection)) {
        this.showSectionActionList = showSection
        this.hideSectionActionList = hideSection
        if(this.showSectionActionList.id === undefined){
        this.showSectionActionList.id  = -1
      }
      if(this.hideSectionActionList.id === undefined){
        this.hideSectionActionList.id  = -1
      }
      } else if (!isEmpty(showSection)) {
        this.showSectionActionList = showSection
        if(this.showSectionActionList.id === undefined){
        this.showSectionActionList.id  = -1
      }
      } else {
        this.hideSectionActionList = hideSection
        if(this.hideSectionActionList.id === undefined){
        this.hideSectionActionList.id  = -1
      }


      }
      console.log(this.showSectionActionList , this.hideSectionActionList)
      let { showSectionActionList, hideSectionActionList } = this || {}
      let { action_meta: showAction_meta } = showSectionActionList || {}
      let { action_detail: showAction_detail } = showAction_meta || {}
      let { section_ids: showSecIds } = showAction_detail || {}

      let { action_meta } = hideSectionActionList || {}
      let { action_detail } = action_meta || {}
      let { section_ids: hideSecIds } = action_detail || {}

      this.$set(this, 'showField', showSecIds)
      this.$set(this, 'hideField', hideSecIds)

      // this.showField = this.showSectionActionList.action_meta.action_detail.section_ids
      // this.hideField = this.hideSectionActionList.action_meta.action_detail.section_ids

      if (!isEmpty(this.showField)) this.showSection()
      if (!isEmpty(this.hideField)) this.hideSection()
    },
    close() {
      if (
        (!isEmpty(this.showField) || !isEmpty(this.hideField)) &&
        this.isSaved
      ) {
        this.$emit('closeSection', true)
      } else this.$emit('closeSection', false)
    },
    save() {
      let emptyCase = {}
      let { showField } = this || {}
      let { hideField } = this || {}
      let { showSectionActionList } = this || {}
      let { hideSectionActionList } = this || {}
      if (!isEmpty(showField) && !isEmpty(hideField)) {
        this.$emit('actions', showSectionActionList, hideSectionActionList)
      } else if (!isEmpty(showField))
        this.$emit('actions', showSectionActionList, emptyCase)
      else if (!isEmpty(hideField))
        this.$emit('actions', emptyCase, hideSectionActionList)
      this.isSaved = true
    },

    showSection() {
      let SectionsList = this.presentSectionsList.filter(section => {
        let value = this.showField.find(i => i === section.id)

        if (value === undefined) {
          return section
        } else return false
      })
      this.$set(this, 'hideSectionsList', SectionsList)
      if (!isEmpty(this.showField)) {
        this.$set(
          this.showSectionActionList.action_meta.action_detail,
          'section_ids',
          this.showField
        )
      }
    },
    hideSection() {
      let SectionsList = this.presentSectionsList.filter(section => {
        let value = this.hideField.find(i => i === section.id)

        if (value === undefined) {
          return section
        } else return false
      })
      this.$set(this, 'showSectionsList', SectionsList)
      if (!isEmpty(this.hideField)) {
        this.$set(
          this.hideSectionActionList.action_meta.action_detail,
          'section_ids',
          this.hideField
        )
      }
    },
  },
  computed: {
    presentSectionsList() {
      return this.sectionsList.filter(i => {
        if (i) return i
        else return false
      })
    },
  },
}
</script>
