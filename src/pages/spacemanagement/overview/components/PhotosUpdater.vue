<template>
  <f-upload-photos
    ref="uploadDialog"
    :list="photos"
    :module="photosModuleName"
    :record="record"
  ></f-upload-photos>
</template>
<script>
import FUploadPhotos from '@/FUploadPhotos'
import { API } from '@facilio/api'
export default {
  props: ['record', 'photosModuleName'],
  components: {
    FUploadPhotos,
  },
  data() {
    return {
      photos: [],
    }
  },
  created() {
    this.loadPhotos()
  },
  methods: {
    open() {
      this.$refs['uploadDialog'].open()
    },
    async loadPhotos() {
      let { id } = this.record
      let params = { module: this.photosModuleName, parentId: id }
      let { data } = await API.get(`/photos/get`, params)
      if (data) {
        let { photos = [] } = data || {}
        this.photos = photos || []
        this.$emit('photosList', photos)
      }
    },
  },
}
</script>
