<template>
  <chart-card :loading="loading" :title="$t('dashboard.analysis.visits')" :total="total" style="margin-bottom: 24px;border-radius: 8px;">
    <div>
      <mini-area :data="systemVisitStats"/>
    </div>
    <template slot="footer">{{ $t('dashboard.analysis.day-visits') }}<span> {{ avgerage }}</span>
    </template>
  </chart-card>
</template>
<script>
import { getSystemVisitStats } from '@/api/system/monitor'
import { ChartCard, MiniArea } from '@/components'
import moment from 'moment'

export default {
  name: 'SystemVisitStats',
  components: { MiniArea, ChartCard },
  data () {
    return {
      loading: false,
      systemVisitStats: []
      // Define your data properties here
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
      getSystemVisitStats().then((res) => {
        // 合并后端数据
        this.systemVisitStats = fullDateList.map(item => {
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
          console.error(error)
          this.loading = false
        })
    }
  },
  computed: {
    // Define your computed properties here
    total () {
      return this.systemVisitStats.reduce((acc, item) => acc + item.y, 0)
    },
    avgerage () {
      return this.systemVisitStats.length > 0 ? (this.total / this.systemVisitStats.length).toFixed(2) : 0
    }
  }
}
</script>
<style scoped lang="less">

</style>
