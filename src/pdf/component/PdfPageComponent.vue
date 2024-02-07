<template>
  <div>
    <!-- Header of the page -->
    <div class="fc-pdf-fc-pdf-page-header">
      <slot name="header"></slot>
    </div>
    <!-- Footer of the page -->
    <div class="fc-pdf-page-footer">
      <slot name="footer"></slot>
    </div>
    <div>
      <table class="width100 border-none">
        <thead>
          <tr>
            <td>
              <div class="page-header-space"></div>
            </td>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td colspan="100%">
              <!-- Main section of the page -->
              <div class="fc-pdf-page-content-section">
                <slot name="content"></slot>
              </div>
            </td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <td>
              <div class="fc-pdf-page-footer-space"></div>
            </td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
</template>
<script>
export default {
  methods: {
    printPage() {
      window.print()
    },
  },
}
</script>
<style lang="scss">
.fc-pdf-page-header,
.fc-pdf-page-header-space {
  height: 100px;
}

.fc-pdf-page-footer,
.fc-pdf-page-footer-space {
  height: 50px;
}

.fc-pdf-page-footer {
  width: 100%;
  position: fixed;
  bottom: 0;
}

.fc-pdf-page-header {
  width: 100%;
  position: fixed;
  top: 0mm;
}

.fc-pdf-page-content-section {
  padding: 20px;
}

.fc-export-pdf-btn {
  position: fixed;
  bottom: 20px;
  z-index: 100;
  left: 50%;
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
  padding-top: 10px;
  padding-right: 20px;
}

// print section

@page {
  margin: 20mm;
}

@media print {
  body {
    margin: 0;
  }

  thead {
    display: table-header-group;
  }

  tfoot {
    display: table-footer-group;
  }
  .fc-pdf-export-btn {
    display: none;
  }
}
</style>
