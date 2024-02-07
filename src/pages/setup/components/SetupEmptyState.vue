<template>
  <div class="fc-setup-empty">
    <div class="fc-setup-empty-image">
      <inline-svg
        src="svgs/list-empty"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
    </div>
    <div class="fc-setup-empty-txt">
      <slot name="emptyHeading"></slot>
    </div>
    <div class="fc-setup-empty-txt-desc">
      <slot name="emptyDescription"></slot>
    </div>
  </div>
</template>
<style lang="scss">
.fc-setup-empty {
  height: 80vh;
  background: #fff;
  box-shadow: 0 3px 7px 0 rgba(233, 233, 226, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  .fc-setup-empty-txt {
    font-weight: 500;
    font-size: 18px;
    color: #324056;
    letter-spacing: 0.5px;
  }
  .fc-setup-empty-txt-desc {
    font-size: 16px;
    color: #324056;
    letter-spacing: 0.5px;
  }
}
</style>
