<template>
  <div class="quick-summary-card-container">
    <div class="p8">
      <div class="d-flex pB10 align-center height20 justify-content-space">
        <div>
          <span class="f14 id-color">#{{ config.id }} </span>
          <div
            v-if="!$validation.isEmpty(config.moduleState)"
            class="fc-badge text-uppercase inline vertical-middle mL15 status-content "
          >
            {{ config.moduleState }}
          </div>
        </div>

        <div
          v-if="config.canShowRedirect"
          @click="redirectToSummary"
          class="open-summary"
        >
          <inline-svg
            src="svgs/external-link2"
            iconClass="icon icon-xs"
          ></inline-svg>
        </div>
      </div>
      <div class="d-flex align-center">
        <img
          v-if="config.canShowImage"
          :src="config.imageUrl"
          class="image-circle mR10"
        />
        <div class="f16 bold elipsis-txt fld-dis-name">
          {{ config.displayName }}
        </div>
      </div>
    </div>
    <slot name="fields"></slot>
  </div>
</template>
<script>
export default {
  props: ['config'],
  methods: {
    redirectToSummary() {
      this.$emit('redirectToSummary')
    },
  },
}
</script>
<style lang="scss">
.quick-summary-card-container {
  border-radius: 4px;
  background-color: #fff;
  max-width: 420px;
  min-width: 280px;

  .id-color {
    color: #667480;
  }

  .status-content {
    background-color: rgb(208, 228, 208);
    color: rgb(44, 139, 44);
  }
  .image-circle {
    height: 40px;
    width: 40px;
    border-radius: 50%;
  }
  .open-summary {
    padding: 2px 5px;
    margin: 3px;
    height: 18px;
    cursor: pointer;

    &:hover {
      background: #f3f4f7;
      border-radius: 4px;
    }
  }
  .elipsis-txt {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .fld-dis-name {
    color: #324056;
  }
  .fields {
    display: flex;
    flex-wrap: wrap;
    padding: 0px 8px;

    .fields-container {
      .fields-data-text {
        color: #1d384e;
      }
      .fields-value-text {
        color: #667480;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }

    .line {
      width: 90%;
      height: 12px;
    }
    .line1 {
      width: 70%;
      height: 10px;
    }
  }
  .fields > * {
    flex: 0 0 50%;
  }
}
.fd-field-details-card {
  .f-field-details-container {
    padding: 8px;
  }
}
</style>
