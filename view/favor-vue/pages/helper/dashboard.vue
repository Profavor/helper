<template>
    <div class="ui segment">
    <table>
        <colgroup>
            <col width="*">
            <col width="330px">
        </colgroup>
        <tr>
            <td style="vertical-align: top;"><full-calendar :events="events" :config="config"></full-calendar></td>
            <td style="vertical-align: top;">
                <div class="ui tiny header">프로젝트 정보</div>
                <table class="ui table">
                    <colgroup>
                        <col width="100px">
                        <col width="*">
                    </colgroup>
                    <thead>
                        <tr>
                        <th>항목</th>
                        <th>정보</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>봉사처</td>
                            <td>
                                <div class="ui small form">
                                    <div class="field">
                                        <div class="ui selection dropdown">
                                            <div class="text"></div>
                                            <i class="dropdown icon"></i>
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>업무</td>
                            <td>{{project.description}}</td>
                        </tr>
                        <tr>
                            <td>인원</td>
                            <td>{{project.maxHelperCount}}</td>
                        </tr>
                        <tr>
                            <td>시작일</td>
                            <td>{{new Date(project.startDate) |  dateFormat('YYYY-MM-DD')}}</td>
                        </tr>
                        <tr>
                            <td>종료일</td>
                            <td>{{new Date(project.endDate) |  dateFormat('YYYY-MM-DD')}}</td>
                        </tr>
                        <tr>
                            <td>담당자</td>
                            <td>{{project.owner}}</td>
                        </tr>
                        <tr>
                            <td>추첨일</td>
                            <td>{{project.triggerValue}}</td>
                        </tr>
                    </tbody>
                </table>
                <div class="ui tiny header">프론티어 지원 <button class="ui right floated mini basic button" style="margin-top: -5px;"><i class="icon user"></i>Shift 추가</button></div>
                <table class="ui table">
                    <colgroup>
                        <col width="100px">
                        <col width="*">
                    </colgroup>
                    <thead>
                        <tr>
                            <th>봉사일자</th>
                            <th>정보</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in projectShifts" :key="item.id">
                        <td>{{new Date(item.helpDate) |  dateFormat('YYYY-MM-DD')}}</td>
                        <td>
                            <button class="ui primary basic mini button" @click="handup(substring(item.helpDate,0,10))">지원하기</button> 
                            <div class="ui label"><i class="user icon"></i>0<a class="detail"></a></div>
                        </td>
                        </tr>                        
                    </tbody>
                </table>
            </td>
        </tr>
    </table>
    
    </div>
</template>

<script>
import { FullCalendar } from 'vue-full-calendar'
import 'fullcalendar/dist/fullcalendar.css'

export default {
  components: {
      FullCalendar
  },
  created: function(){
      this.usr = this.$auth.$state.user;
      this.getProjectDropdown();
  },
  data: function(){
      return {
        usr: {},
        projectId: '',
        project: [],
        projectShifts: [],
        dropdownProject: [],
        config: {
            weekends: true,
            defaultView: 'month',
            header: {
                left:   'title',
                center: '',
                right:  'today prev,next'
            }
        },
        events: [
            {
                title  : '김유현',
                start  : '2019-01-01',
            },
            {
                title  : '김유현',
                start  : '2019-01-01',
            } 
        ]
    }
  },
  methods: {
      async getUser(){
          return await this.$auth.fetchUser();
      },
      async getProjectDropdown(){
          let that = this;
          await this.$axios.get('/api/helper/project/list/dropdown').then(res => this.dropdownProject = res.data).catch(err => console.log(err));
          $('.ui.dropdown').dropdown({
            values: this.dropdownProject.results,
            onChange: function(value, text, a){
                if(value != ''){
                    that.projectId = value;
                    that.refresh();
                }
            }
        });        
      },
      async getProject(){
        await this.$axios.get('/api/helper/getProject?id='+this.projectId).then(res => this.project = res.data).catch(err=> this.$toast.error(err));
      },

      async getProjectShiftList(){
          await this.$axios.get('/api/helper/getProjectShiftList?projectId='+this.projectId).then(res => this.projectShifts = res.data).catch(err=> this.$toast.error(err));
          console.log(this.projectShifts);
      },

      async addProjectShift(){

      },

      handup(helpDate){
          console.log(helpDate);
      },

      refresh(){
          this.getProject();
          this.getProjectShiftList();
      },
      isHelperAdminRole(){
          for(let item in this.usr.roles){
              if(this.usr.roles[item].authority == 'ROLE_HELPER_ADMIN'){
                  return true;
              }
          }
          return false;
      }
  },
  mounted: function(){
      
  }
}
</script>

<style>
.fc-toolbar {
    height: 40px;
}
</style>