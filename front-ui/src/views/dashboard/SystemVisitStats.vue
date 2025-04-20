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
    this.loading = true
    getSystemVisitStats().then((res) => {
      this.systemVisitStats = res.result.map((item) => {
        return {
          x: item.date,
          y: parseInt(item.count)
        }
      })
      if (res.result.length === 1) {
        this.systemVisitStats.push({
          x: moment().subtract(1, 'days').format('YYYY-MM-DD'),
          y: 0
        })
      }
      this.loading = false
    })
      .catch((error) => {
        // Handle any errors
        console.error(error)
        this.loading = false
      })
  },
  computed: {
    // Define your computed properties here
    total () {
      return this.systemVisitStats.reduce((acc, item) => acc + item.y, 0)
    },
    avgerage () {
      return this.systemVisitStats.length > 0 ? (this.total / this.systemVisitStats.length) : 0
    }
  }
}
</script>
<style scoped lang="less">

</style>
