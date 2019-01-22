<template>
  <div>
  <div class="ui icon message">
    <i class="inbox icon"></i>
    <div class="content">
      <div class="header">
        Have you heard about our mailing list?
      </div>
      <p>Get the best news in your e-mail every day.</p>         
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
          <div class="ui teal label">1</div>
          Dashboard
        </nuxt-link>
        <nuxt-link to="/helper/project" class="item">
          <div class="ui label">1</div>
          Project
        </nuxt-link>
        <a class="item">
          <div class="ui label">1</div>
          Updates
        </a>
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
  methods: {
    async logout(){
      await this.$auth.logout();
      $nuxt.$router.push({name: 'login', query: {}});
    }
  }
}

</script>

<style>

</style>
