<template>
  <FContainer class="control-criteria-container">
    <FContainer padding="containerXLarge">
      <FText appearance="headingMed16"> {{ getHeaderName }}</FText>
    </FContainer>
    <FContainer
      v-if="showBanner"
      display="flex"
      alignItems="center"
      class="criteria-banner"
    >
      <FIcon
        group="action"
        name="info"
        color="white"
        size="16"
        class="pR5"
        :pressable="false"
      />
      <FContainer display="flex" width="100%">
        <FText appearance="bodyReg14">{{
          `In the absence of defined criteria, all ${getModuleName} will be selected.`
        }}</FText></FContainer
      >
      <FContainer display="flex" justifyContent="left">
        <FIcon
          group="action"
          name="cross-circle"
          size="16"
          @click="hideBanner()"
        />
      </FContainer>
    </FContainer>
    <FContainer padding="containerXLarge" minHeight="120px">
      <NewCriteriaBuilder
        ref="criteriaBuilder"
        v-model="renderDetails.criteria"
        :moduleName="getModuleName"
        :isOneLevelEnabled="true"
      />
    </FContainer>
  </FContainer>
</template>
<script>
import { FContainer, FText, FIcon } from '@facilio/design-system'
import { NewCriteriaBuilder } from '@facilio/criteria'

export default {
  components: { FContainer, NewCriteriaBuilder, FText, FIcon },
  props: ['renderDetails'],
  data() {
    return {
      enableBanner: true,
    }
  },
  computed: {
    getModuleName() {
      return this.$getProperty(this, 'renderDetails.moduleName', 'asset')
    },
    showBanner() {
      let { enableBanner } = this
      return (
        this.$getProperty(this, 'renderDetails.infoBanner', false) &&
        enableBanner
      )
    },
    getHeaderName() {
      return this.$getProperty(
        this,
        'renderDetails.headerName',
        'Default Header'
      )
    },
  },
  methods: {
    hideBanner() {
      this.enableBanner = false
    },
  },
}
</script>
<style lang="scss" scoped>
.control-criteria-container {
  width: 100%;
  border-radius: 8px;
  border: 1px solid #fff;
  box-shadow: 0px 2px 4px 0px rgba(5, 16, 30, 0.1),
    0px 1px 2px 0px rgba(5, 16, 30, 0.25);
  .pB10 {
    padding-bottom: 10px;
  }
  .pR5 {
    padding-right: 5px;
  }
  .fR {
    float: right;
  }
  .criteria-banner {
    width: 100%;
    padding: 5px 16px;
    background-color: #0059d6;
    color: white;
  }
}
</style>
