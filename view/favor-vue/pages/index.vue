<template>
    <div>
        <table class="ui celled padded table">
  <thead>
    <tr><th class="single line">봉사횟수</th>
    <th>Knox ID</th>
    <th>이름</th>
    <th>부서</th>
  </tr></thead>
  <tbody>
    <tr v-for="item in helpers" :key="item.knoxId">
      <td>
        <h2 class="ui center aligned header">{{item.helpCount}}</h2>
      </td>
      <td>
        {{item.knoxId}}
      </td>
      <td class="single line">
        {{item.userName}}
      </td>
      <td>
        {{item.deptCode}}
      </td>
    </tr>
  </tbody>
</table>
    </div>
</template>

<script>
export default {
  created: function(){
    this.getHelperListWithCount();
  },
  components: {
  },
  data: function(){
      return {
        helpers: []
      }
  },
  methods: {
      async getHelperListWithCount(){
        let that = this;
        await this.$axios.post('/api/helper/getHelperListWithCount').then(res => this.helpers = res.data.results).catch(err=> this.$toast.error(err));
      }
  }
}
</script>