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
                                <td>{{project.startDate}}</td>
                            </tr>
                            <tr>
                                <td>종료일</td>
                                <td>{{project.endDate}}</td>
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
                    <div class="ui tiny header">프론티어 지원 
                        <button class="ui right floated mini basic button" style="margin-top: -5px;" @click="addProjectShift" v-if="projectId != '' && existAuth('ROLE_HELPER_USER')"><i class="icon user"></i>Shift 추가</button>
                    </div>
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
                                <button class="ui primary basic mini button" @click="handup(item.helpDate)" v-if="!existRequestHelper(item.requests)">지원하기</button> 
                                <button class="ui red basic mini button" @click="handdown(item.helpDate)" v-if="existRequestHelper(item.requests)">취소하기</button> 
                                <div class="ui label"><i class="user icon"></i>{{requestCount(item.requests)}}<a class="detail"></a></div>
                            </td>
                            </tr>                        
                        </tbody>
                    </table>
                </td>
            </tr>
        </table>
        <div id="helperShiftRegist" class="ui modal">
            <i class="close icon"></i>
            <div class="header">
                프로젝트 시프트 등록
            </div>
            <div class="content">
                <form class="ui form" id="helperShiftRegForm">
                    <div class="ui message">
                        <div class="header">안내사항</div>
                        <ul class="list">
                            <li>프로젝트 시프트를 관리할 수 있습니다</li>         
                        </ul>    
                    </div>
                    <div class="field">
                        <label>Help Date</label>
                        <div class="ui calendar" id="helpDate">
                            <div class="ui input left icon">
                                <i class="calendar icon" />
                                <input type="text" name="helpDate" placeholder="Date" v-model="projectShift.helpDate" />
                            </div>
                        </div>
                    </div>     
                    <div class="field">     
                        <label>상태</label>         
                        <div class="fields">                                
                            <div class="field">
                                <div class="ui radio checkbox">
                                    <input type="radio" name="status" v-model="projectShift.status" value="OPEN" >
                                    <label>OPEN</label>
                                </div>
                            </div>
                            <div class="field">
                                <div class="ui radio checkbox">
                                    <input type="radio" name="status" v-model="projectShift.status" value="CLOSE">
                                    <label>CLOSE</label>
                                </div>
                            </div>           
                        </div>
                    </div>
                                      
                    <div class="ui error message">

                    </div>
                </form>
            </div>            
            <div class="actions">
                <div class="ui button" @click="saveProjectShift">저장</div>
            </div>
        </div>
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

        console.log(this.usr)
  },
  data: function(){
      return {
        usr: {},
        projectId: '',
        project: [],
        projectShifts: [],
        projectShift: {
            helpDate: '',
            status: 'OPEN'
        },
        dropdownProject: [],
        config: {
            weekends: true,
            defaultView: 'month',
            header: {
                left:   'title',
                center: '',
                right:  'today prev,next'
            }, 
            editable: false
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
    existRequestHelper(request){
          for(let i=0; i<request.length; i++){
              if(request[i].helper.knoxId == this.usr.loginId && request[i].status == 'HAND_UP'){
                  return true;
              }
          }
          return false;
    },


    existAuth(auth){
        let returnValue = false;
        for(let i = 0; i < this.usr.roles.length; i++){
            if(this.usr.roles[i].authority == auth){
                returnValue = true;
                break;
            }
        }
        return returnValue;
    },


      requestCount(request){
          let count = 0;
          for(let i=0; i<request.length; i++){
              if(request[i].status == 'HAND_UP'){
                  count++;
              }
          }
          return count;
      },

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

      async getProjectShiftListOpen(){
          let that = this;
          await this.$axios.get('/api/helper/getProjectShiftList?projectId='+this.projectId+'&projectStatus=OPEN')
            .then(res => {
                that.projectShifts = res.data
            }).catch(err=> this.$toast.error(err));
          
      },

      async getProjectShiftListClose(){
          let that = this;
          await this.$axios.get('/api/helper/getProjectShiftList?projectId='+this.projectId+'&projectStatus=CLOSE')
            .then(res => {
                
                that.events = [];
                for(let i = 0; i < res.data.length; i++){
                    for(let j = 0; j < res.data[i].helpers.length; j++){    
                        that.events.push({title : res.data[i].helpers[j].userName , start : res.data[i].helpDate});
                    }
                }

            }).catch(err=> this.$toast.error(err));
          
      },

      async addProjectShift(){
            let that = this;
            $('#helperShiftRegist').modal('show');

            $('#helpDate').calendar({ type: 'date',
                formatter: {
                    date: function (date, settings) {
                    if (!date) return '';
                    var day = date.getDate();
                    var month = date.getMonth() + 1;
                    var year = date.getFullYear();
                    that.projectShift.helpDate = year + '-' + pad(month, 2) + '-' + pad(day, 2);
                    return year + '-' + pad(month, 2) + '-' + pad(day, 2);
                    }
            }});

            $('.ui.checkbox').checkbox();
      },

      async saveProjectShift(){
          await this.$axios.post('/api/helper/project/shift/save', {
              'projectId': this.projectId,
              'helpDate': this.projectShift.helpDate,
              'status': this.projectShift.status
          }).then(res => this.$toast.success('Shift가 등록되었습니다')).catch(err => this.$toast.error(err));
          
          this.refresh();
          $('#helperShiftRegist').modal('hide');
      },

      async handup(helpDate){
          await this.$axios.post('/api/helper/project/shift/request/handup', {
              'projectId': this.projectId,
              'helpDate': substring(helpDate,0,10)
          }).then(res => this.$toast.success('성공적으로 봉사지원을 하였습니다.')).catch(err => this.$toast.error(err));
          
          this.refresh();
      },

      async handdown(helpDate){
          await this.$axios.post('/api/helper/project/shift/request/handdown', {
              'projectId': this.projectId,
              'helpDate': substring(helpDate,0,10) 
          }).then(res => this.$toast.success('봉사지원을 취소하였습니다.')).catch(err => this.$toast.error(err));
          
          this.refresh();
      },

      refresh(){
          this.getProject();
          this.getProjectShiftListOpen(); // 프론티어 지원 영역
          this.getProjectShiftListClose(); // 확정 헬퍼 캐린더 표시
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
@import "node_modules/semantic-ui-calendar/dist/calendar.min.css";
.fc-toolbar {
    height: 40px;
}
</style>
