<template>
    <div>
        <br/>
        <h2 class="ui center aligned icon header">
            <i class="circular users icon"></i>
            Helper 3.0
        </h2>
        <div class="ui mini message">
            <div class="header">
                New Site Features
            </div>
            <ul class="list">
                <li>이제 봉사신청을 모든 사업장에서 할 수 있습니다</li>
                <li>다른 봉사에 참여할 수 있습니다</li>
                <li>봉사 담당자가 할 일들을 대신 해줍니다</li>
            </ul>
        </div>
        <form class="ui form">
            <div class="field">
                <label>Login ID</label>
                <input name="username" type="text" placeholder="Login ID" v-model="username">
            </div>
            <div class="field">
                <label>Password</label>
                <input name="password" type="password" placeholder="Password" v-model="password" v-on:keyup.enter="login">
            </div>
            <button class="ui button" type="button" @click="login">로그인</button>
            <button class="ui button" type="button" @click="registPopup">회원가입</button>
        </form>

        <div id="usrRegist" class="ui modal">
            <i class="close icon"></i>
            <div class="header">
                회원가입 안내
            </div>
            <div class="content">
                <form class="ui form" id="usrRegForm">
                    <div class="ui message">
                        <div class="header">안내사항</div>
                        <ul class="list">
                        <li>[미라콤아이앤씨] 봉사활동 관리를 목적으로 회원정보를 수집합니다.</li>
                        <li>본 시스템은 1년 무료 클라우드에서 개발되었기 때문에 회원정보는 1년 뒤 자동 삭제됩니다.</li>
                        </ul>
                    </div>
                    <div class="field">
                        <label>Knox ID</label>
                        <input type="text" name="loginId" placeholder="Knox ID" v-model="regist.loginId">
                    </div>
                    <div class="field">
                        <label>사용자명</label>
                        <input type="text" name="username" placeholder="User Name" v-model="regist.userName">
                    </div>
                    <div class="field">
                        <label>Password</label>
                        <input type="password" name="password1" placeholder="Password" v-model="regist.password" >
                    </div>
                    <div class="field">
                        <label>Confirm Password</label>
                        <input type="password" name="password_confirm" placeholder="Password" v-model="regist.password_confirm">
                    </div>
                    <div class="field">
                        <label>회원승인코드</label>
                        <input type="text" name="accept_code" placeholder="회원승인코드" v-model="regist.acceptCode">
                    </div>
                    <div class="ui error message">

                    </div>
                </form>
            </div>
            <div class="actions">
                <div class="ui button" @click="registSave">OK</div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
  layout: 'login-layout',
  components: {
  },
  data: function(){
      return {
          'username': '',
          'password': '',
          'regist': {
              'loginId': '',
              'password': '',
              'userName': '',
              'password_confirm': '',
              'acceptCode': ''
          }
      }
  },
  methods: {
      async login(){
        await this.$auth.loginWith('local', {
            data: {
                username: this.username,
                password: this.password
            }
        }).then(()  => this.$toast.success('Successfully authenticated')).catch(function (err){
            $nuxt.$toast.error('회원 정보가 올바르지 않습니다.');
        });
      },

      registPopup(){
          $('#usrRegist').modal('show');
      },

      async registSave(){
          $('#usrRegForm').form('validate form');
          if($('#usrRegForm').form('is valid')){    
            await this.$axios.$post('/auth/createHelper', {
                'loginId': this.regist.loginId,
                'password': this.regist.password,
                'userName': this.regist.userName,
                'acceptCode': this.regist.acceptCode
            }).then(ref => {
                this.regist.loginId = '';
                this.regist.password = '';
                this.regist.userName = '';
                this.regist.password_confirm = '';
                this.regist.acceptCode = '';     
                this.$toast.success('회원 등록되었습니다.');        
                $('#usrRegist').modal('hide');
            }).catch(err => {
                $nuxt.$toast.error(err);
            });
          }          
      }
  },
  mounted: function(){
      $('#usrRegForm').form({
          keyboardShortcuts: false,
          fields: {
              loginId : {
                identifier: 'loginId',
                rules: [{
                    type   : 'minLength[4]',
                    prompt : 'Knox ID를 정확하게 입력해주세요'
                }]
              },
              password1 : {
                identifier: 'password1',
                rules: [{
                    type   : 'minLength[6]',
                    prompt : '6자리 이상 패스워드를 입력해주세요.'
                }]
              },
              username : {
                identifier: 'username',
                rules: [{
                    type   : 'empty',
                    prompt : '이름을 입력해주세요'
                }]
              },
              password_confirm : {
                identifier: 'password_confirm',
                rules: [{
                    type   : 'match[password1]',
                    prompt : '패스워드가 일치하지 않습니다'
                }]
              },
              accept_code : {
                identifier: 'accept_code',
                rules: [{
                    type   : 'minLength[6]',
                    prompt : '승인코드를 확인해주세요'
                }]
              } 
          }          
      });
  }
}
</script>