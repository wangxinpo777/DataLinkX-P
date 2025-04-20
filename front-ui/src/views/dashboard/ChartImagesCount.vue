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
    getImageCountByDate()
      .then((res) => {
        this.chartImagesCount = res.result.map((item) => {
          return {
            x: item.date,
            y: parseInt(item.count)
          }
        })
        if (res.result.length === 1) {
          this.chartImagesCount.push({
            x: moment().subtract(1, 'days').format('YYYY-MM-DD'),
            y: 0
          })
        }
      })
      .catch((error) => {
        // Handle any errors
        console.error(error)
      })
  },
  computed: {
    total () {
      return this.chartImagesCount.reduce((acc, item) => acc + item.y, 0)
    },
    avgerage () {
      return this.chartImagesCount.length > 0 ? (this.total / this.chartImagesCount.length) : 0
    }
  }
}
</script>
<style scoped lang="less">

</style>
