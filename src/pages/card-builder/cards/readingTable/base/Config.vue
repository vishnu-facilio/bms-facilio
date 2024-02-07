<script>
import BaseConfig from 'pages/card-builder/cards/readingCard/type1/Config'
export default {
  extends: BaseConfig,
  mounted() {
    this.loadNameSpaceList()
    this.setProps()
  },
}
</script>
