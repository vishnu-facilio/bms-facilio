<template>
  <el-tree
    :props="treeProps"
    node-key="key"
    :load="loadChildren"
    :highlight-current="true"
    @node-click="handleNodeClick"
    accordion
    lazy
  >
  </el-tree>
</template>
<script>
export default {
  name: 'f-simple-tree',
  props: ['model', 'load'],
  data() {
    return {
      treeProps: {
        label: 'name',
        children: 'spaces',
        isLeaf: 'leaf',
      },
    }
  },
  methods: {
    loadChildren(node, resolve) {
      if (node.level === 0) {
        this.load(this.model, resolve)
      } else {
        this.load(node.data, resolve)
      }
    },
    handleNodeClick(node, resolve) {
      if (node.path) {
        this.load(node, resolve)
        this.$router.push({ path: node.path })
      }
    },
  },
}
</script>
