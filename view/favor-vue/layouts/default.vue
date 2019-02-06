<template>
  <div>
  <div class="ui icon message">
    <i class="inbox icon"></i>
    <div class="content">
      <div class="header">
        나는 당신이 어떤 운명으로 살 지 모른다. 하지만 이것 만은 장담할 수 있다.
      </div>
      <p>정말로 행복한 사람들은 어떻게 봉사할 지 찾고 발견한 사람들이다. -알버트 슈바이처</p>         
    </div>
  </div>
  <div class="ui menu">
  <div class="header item">
    Helper 3.0
  </div>
  <nuxt-link to="/helper" class="item">
    봉사활동
  </nuxt-link>
  <a class="item">
    Jobs
  </a>
  <div class="right menu">
    <a class="item">My info.</a>
    <a class="item"><div class="ui primary button"  @click="logout">Logout</div></a>
  </div>
  </div>
    <div style="width: 210px; float: left; height: 100%;">
      <div class="ui vertical menu">
        <nuxt-link to="/helper/dashboard" class="item">
          <div class="ui teal label">☆</div>
          Dashboard
        </nuxt-link>
        <nuxt-link to="/helper/project" class="item" v-if="isHelperAdminRole() == true">
          <div class="ui label">☆</div>
          Project
        </nuxt-link>
        <nuxt-link to="/helper/myhelper" class="item">
          <div class="ui label">☆</div>
          나의 봉사
         </nuxt-link>
      </div>
    </div>
    <div style="margin-left: 215px; height: 100%;">
      <nuxt/>
    </div>    
  </div>
</template>

<script>
$('body').css('width', '100%');
$('body').css('height', '100%');

export default {
  created: function(){
      this.usr = this.$auth.$state.user;
  },
  data: function(){
    return {
      usr: {}
    }
  },
  methods: {
    async logout(){
      await this.$auth.logout();
      $nuxt.$router.push({name: 'login', query: {}});
    },
    isHelperAdminRole(){
        for(let item in this.usr.roles){
          if(this.usr.roles[item].authority == 'ROLE_HELPER_ADMIN'){
            return true;
          }
        }
      return false;
    }
  }
}

</script>

<style>

</style>
