<template>
  <div class="scrollable-y100" style="width:100%">
    <div class="assets-qrcode-printview">
      <div v-if="assets">
        <div v-for="(asset, index) in assets" :key="index">
          <div style="text-align:center;" class="pT25 page-break-inside-avoid">
            <div style="text-align:center;">
              <qriously
                :value="asset.qrVal || 'facilio_' + $org.id + '_' + asset.id"
                :size="500"
                :level="'H'"
              />
            </div>
            <div style="text-align:center;">
              <h3 style="font-size: 40px;color: #333333;font-weight: 500;">
                {{ asset.name }}
              </h3>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['assets'],
}
</script>

<style>
/* @media print
{
  .assets-qrcode-printview {
    width: 100%;
    height: 100%;
    position: fixed;
    top: 0px;
    left: 0px;
    background: white;
    z-index: 100000;
  }

  .print-page {
    page-break-after: always;
  }
} */
@media print {
  @page {
    orphans: 4;
    widows: 2;
    margin-top: 28px;
    z-index: 900;
  }
  .tags-parent-container {
    display: none;
  }

  .height100 {
    display: block !important;
  }
  .assets-qrcode-printview {
    width: 100%;
    height: 100%;
    top: 0px;
    left: 0px;
    position: relative;
    background: white;
    z-index: 100000;
    column-count: 3;
    /* padding-top: 30px; */
    padding-bottom: 200px;
    /* column-gap: 80px;
    column-fill: auto; */
  }

  .print-page {
    page-break-after: avoid;
    page-break-inside: avoid;
  }

  .subheader-section {
    display: none;
  }

  header.layout-header.transition-generic.fixed-top {
    display: none;
  }

  aside.fc-layout-aside {
    display: none;
  }

  .scrollable-y100 {
    height: auto !important;
    overflow: auto !important;
    display: block;
  }

  .layout-page-container.transition-generic {
    padding: 0px !important;
  }
  .height100.mT15 {
    /*! display: block; */
  }
  .height100.pL50.pL60 {
    padding: 0px;
  }

  .scrollable {
    overflow: auto;
  }

  main.layout-page {
    height: 100% !important;
    overflow: auto !important;
    min-height: auto !important;
  }

  body.mat.desktop.no-touch.fc-white-theme {
    overflow: scroll;
  }

  .row.full-layout-white.fc-border-1,
  .filter-container {
    display: none !important;
  }

  .fc-list-view.p20.pT0.height100.mT15.scrollable {
    display: none;
  }

  .height100.mT15 {
    margin-top: 0;
  }
  .tag-container {
    display: none;
  }
  canvas {
    height: 120px;
    width: 120px;
  }

  h3 {
    font-size: 1em !important;
  }
  .page-break-inside-avoid {
    page-break-inside: avoid;
  }
}
</style>
