<template>
  <div :class="prefixCls">
    <div class="chart-wrapper" :style="{ height: 100 }">
      <v-chart :force-fit="true" :height="150" :data="dataSource" :scale="scale" :padding="[40, 40, 40, 40]">
        <v-tooltip />
        <v-smooth-line position="x*y" :size="2" />
        <v-smooth-area position="x*y" />
        <v-axis dataKey="x" :visible="true" />
        <!-- 显示 Y 轴 -->
        <v-axis dataKey="y" :visible="true" />
      </v-chart>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MiniSmoothArea',
  props: {
    prefixCls: {
      type: String,
      default: 'ant-pro-smooth-area'
    },
    scale: {
      type: [Object, Array],
      required: true
    },
    dataSource: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      height: 100
    }
  }
}
</script>

<style lang="less" scoped>
  @import "smooth.area.less";
</style>
