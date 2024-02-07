<template>
  <div class="fc-servernot-reachable height100">
    Server not reachable ({{ apiurl }}). &nbsp;&nbsp;<a
      href="javascript:location.reload()"
      >Refresh</a
    >
  </div>
</template>
<script>
import { getBaseURL } from 'util/baseUrl'

export default {
  data() {
    return {
      apiurl: getBaseURL(),
    }
  },
}
</script>
<style>
.fc-servernot-reachable {
  width: 560px;
  text-align: center;
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  margin: auto;
  height: 40px;
  border: 1px solid #f00;
  padding: 10px;
  background: #f00;
  color: #fff;
  font-weight: 800;
  margin-top: 300px;
}
.fc-servernot-reachable a {
  color: #fff;
}
</style>
