<template>
  <chart-card :loading="loading" :title="'生成图片'" :total="total" style="margin-bottom: 24px;border-radius: 8px;">
    <div>
      <mini-bar :data="chartImagesCount" />
    </div>
    <template slot="footer">日均生成图片<span> {{ avgerage }}</span></template>
  </chart-card>
</template>
<script>
import MiniBar from '@/components/Charts/MiniBar.vue'
import ChartCard from '@/components/Charts/ChartCard.vue'
import { getImageCountByDate } from '@/api/visualization'
import moment from 'moment'

export default {
  name: 'ChartImagesCount',
  components: { ChartCard, MiniBar },
  data () {
    return {
      loading: false,
      chartImagesCount: []
    }
  },
  mounted () {
      this.initData()
  },
  methods: {
    initData () {
      this.loading = true
      const startOfMonth = moment().startOf('month')
      const endOfMonth = moment().endOf('month')

      const fullDateList = []
      const current = startOfMonth.clone()

      while (current.isSameOrBefore(endOfMonth, 'day')) {
        fullDateList.push({
          x: current.format('YYYY-MM-DD'),
          y: 0
        })
        current.add(1, 'day')
      }
      getImageCountByDate()
        .then((res) => {
          // 合并后端数据（以日期为键）
          this.chartImagesCount = fullDateList.map(item => {
            const match = res.result.find(d => d.date === item.x)
            return {
              ...item,
              y: match ? parseInt(match.count) : item.y
            }
          })
          this.loading = false
        })
        .catch((error) => {
          // Handle any errors
          this.loading = false
          console.error(error)
        })
    }
  },
  computed: {
    total () {
      return this.chartImagesCount.reduce((acc, item) => acc + item.y, 0)
    },
    avgerage () {
      return this.chartImagesCount.length > 0 ? (this.total / this.chartImagesCount.length).toFixed(2) : 0
    }
  }
}
</script>
<style scoped lang="less">

</style>
