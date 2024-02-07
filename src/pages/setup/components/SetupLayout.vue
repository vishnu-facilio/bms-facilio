<template>
  <div class="setup-layout-root">
    <div class="header-container ">
      <div>
        <div class="header-title">
          <slot name="heading"></slot>
        </div>
        <div class="description-style">
          <slot name="description"></slot>
        </div>
      </div>
      <div>
        <slot name="actions"></slot>
      </div>
    </div>
    <div class="second-header">
      <slot name="tabs"></slot>
      <slot name="searchAndPagination"></slot>
    </div>
    <div class="extra-filter flex-middle fc-filter-animation">
      <slot name="extraFilter"></slot>
    </div>
    <div class="setup-body">
      <slot name="body"></slot>
    </div>
  </div>
</template>
<style lang="scss" scoped>
.setup-layout-root {
  .header-title {
    font-size: 18px;
    font-weight: 500;
    letter-spacing: 0.56px;
    color: #4f4f4f;
    line-height: 30px;
  }
  .header-container {
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid #eaecf0;
    padding: 20px;
    height: 85px;
  }
  .description-style {
    max-width: 550px;
    line-height: 18px;
    color: #808080;
    font-size: 12px;
    letter-spacing: 0.43px;
  }
  .second-header {
    display: flex;
    background: #fff;
    align-items: center;
    justify-content: space-between;
    padding: 11px 20px;
    height: 45px;
  }
  .extra-filter {
    background: #fff;
    margin: 10px;
  }
  .setup-body {
    background: #fff;
    margin: 10px 10px 0px;
  }
}
</style>
