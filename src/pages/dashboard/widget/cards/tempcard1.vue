<template>
  <div class="dragabale-card height100 fcu-list-view">
    <div class="fcu-table-header row">
      <div class="fcu-table-name col-10 text-left fcu-table-name2">
        {{ $t('panel.card.files') }}
      </div>
    </div>
    <div v-if="loading" class="shimmer-frame">
      <div class="assetcard-shimmer shine"></div>
    </div>
    <div v-else>
      <div class="fcu-list-container">
        <table>
          <tr
            class="fcu-list-header"
            style="background: #fff;border-bottom: solid 1px #e6ecf3;"
          >
            <th>
              <div>{{ $t('panel.card.file_name') }}</div>
            </th>
            <th>
              <div>{{ $t('panel.card.download') }}</div>
            </th>
          </tr>
          <tbody>
            <tr v-for="(d, index) in cardData" :key="index">
              <td class="fcu-td pointer">
                <div class="fcu-name">
                  {{ d.fileName }}
                </div>
              </td>
              <td class="fcu-td pointer">
                <div class="fcu-spacename pointer">
                  <div class="round download-circle">
                    <iframe
                      v-if="exportDownloadUrl"
                      :src="exportDownloadUrl"
                      style="display: none;"
                    ></iframe>
                    <a
                      class="el-icon-download download-hover szp-downloadicon"
                      @click="downloadAttachment(d.downloadUrl)"
                    ></a
                    ><!--:href="currentFile.downloadUrl"-->
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      loading: false,
      exportDownloadUrl: null,
      cardData: [],
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      let self = this
      this.loading = true
      self.$http
        .get('attachment?module=assetattachments&recordId=1122689')
        .then(function(response) {
          self.cardData = response.data.attachments
          self.loading = false
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
          }
        })
    },
    downloadAttachment(url) {
      if (this.exportDownloadUrl) {
        this.exportDownloadUrl = null
        this.$nextTick(() => {
          this.exportDownloadUrl = url
        })
      } else {
        this.exportDownloadUrl = url
      }
    },
  },
}
</script>
<style>
.szp-downloadicon {
  color: #fff;
  font-weight: 900;
  font-size: 20px;
  position: absolute;
  left: 5px;
  top: 3px;
}
.download-circle {
  align-items: center;
  justify-content: center;
  background: rgb(103, 103, 103);
  border-radius: 15px;
  float: left;
  margin-right: 8px;
  min-width: 0;
  width: 30px;
  height: 30px;
  padding: 0;
  line-height: 23px;
  cursor: pointer;
  color: #fff;
  position: relative;
  left: 25%;
}
.fcu-table-name2 {
  text-align: left !important;
}
</style>
