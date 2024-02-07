<template>
  <div class="classification-picker-wizard">
    <el-input
      v-model="classificationExtendsName"
      :placeholder="$t('setup.classification.add_classification_extends')"
      class="fc-input-full-border-select2 width100"
      disabled
    >
      <div slot="suffix" class="flRight d-flex pointer slu-icons" v-if="isNew">
        <div
          v-if="canShowClearOption"
          class="icon-class-alignment "
          @click="clearSelectedClassification"
        >
          <i class="el-icon-circle-close pointer  f13"></i>
        </div>
        <div
          v-if="$validation.isEmpty(classificationExtendsName)"
          class="icon-class-alignment"
          @click="openSpecificationWizard"
        >
          <inline-svg
            src="svgs/classification-wizard"
            iconClass="slu-lookup-icon"
          ></inline-svg>
        </div>
      </div>
    </el-input>
    <SpecificationWizard
      v-if="canShowSpecificationWizard"
      @onClose="canShowSpecificationWizard = false"
      @onSave="saveCls"
    ></SpecificationWizard>
  </div>
</template>
<script>
import SpecificationWizard from './SpecificationWizard'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['classificationData', 'isNew'],
  data() {
    return {
      clsModule: null,
      classificationExtendsName: null,
      specificationModuleId: null,
      canShowSpecificationWizard: false,
    }
  },
  components: { SpecificationWizard },
  created() {
    if (
      !this.isNew &&
      !isEmpty(this.classificationData?.parentClassification)
    ) {
      this.classificationExtendsName = this.classificationData.classificationPath
    }
  },
  computed: {
    canShowClearOption() {
      return !isEmpty(this.classificationExtendsName)
    },
  },
  watch: {
    classificationExtendsName() {
      this.$emit('update:classificationData', this.clsModule)
    },
  },
  methods: {
    saveCls(item, breadCrumb) {
      this.clsModule = item
      let path = this.getClassificationPath(breadCrumb)
      this.clsModule.classificationPath = `${path}/${item.name}`
      this.classificationExtendsName = `${path} / ${item.name}`
    },
    openSpecificationWizard() {
      this.canShowSpecificationWizard = true
    },
    clearSelectedClassification() {
      this.classificationExtendsName = null
      this.clsModule = null
    },
    getClassificationPath(classificationPath) {
      let path = classificationPath.map(path => path.name)
      if (path.length > 2) {
        path = [path[0], '...', path[path.length - 1]]
      }
      path = path.join(' / ')
      return path
    },
  },
}
</script>
<style lang="scss">
.classification-picker-wizard {
  position: relative;
  &:hover {
    .flookup-remove-icon {
      display: flex;
    }
  }
  .slu-icons {
    height: 100%;
    color: #000000;
  }
  .icon-sm {
    height: 10px;
    width: 10px;
  }

  .icon-class-alignment {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 1px 5px;
  }
  .slu-lookup-icon {
    width: 28px;
    height: 28px;
  }
}
</style>
