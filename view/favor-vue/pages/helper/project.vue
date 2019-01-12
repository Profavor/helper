<template>
    <div class="ui segment">
        <button class="ui primary button" @click="createProjectModal">프로젝트 등록</button>
        <button class="ui button" @click="closeProject">종료</button>

        <table class="ui selectable celled table">
            <thead id='project'>
                <th><div class="ui checkbox checkall"><input id="checkall" type="checkbox"><label></label></div></th>
                <th>Project name</th>
                <th>Description</th>
                <th>시작일</th>
                <th>종료일</th>
                <th>상태</th>
                <th>투입인원</th>
                <th>추첨일</th>
                <th>등록 봉사자</th>
            </thead>
            <tbody id='projectList'>
                <tr v-for="(item, index) in projects" :key="item.id" @dblclick="getHelperList(index)" v-bind:class="{active: item.id == helper.projectId, disabled: item.status == 'CLOSE'}">
                    <td><div class="ui checkbox"><input name='project_check' :value="item.id" type="checkbox"><label></label></div></td>
                    <td><a @click="editProjectModal(item.id)" style="cursor: pointer">{{item.projectName}}</a></td>
                    <td>{{item.description}}</td>
                    <td>{{new Date(item.startDate) | dateFormat('YYYY-MM-DD') }}</td>
                    <td>{{new Date(item.endDate) | dateFormat('YYYY-MM-DD') }}</td>
                    <td>{{item.status}}</td>
                    <td>{{item.maxHelperCount}}</td>
                    <td>{{item.triggerValue}}</td>
                    <td>{{item.helpers.length}}</td>
                </tr>
            </tbody>
        </table>

        <div id="projectHelper" class="ui segment" v-show="helper.projectId != ''">
            <button class="ui primary button" @click="createHelperModal">봉사자 등록</button>
            <button class="ui button" @click="deleteHelper">삭제</button>
            <div class="ui input">
                <input type="text" placeholder="Search...">
            </div>
            <table class="ui celled table">
                <thead id='helper'>
                    <th><div class="ui checkbox checkall"><input id="checkall" type="checkbox"><label></label></div></th>
                    <th>Knox ID</th>
                    <th>이름</th>
                    <th>부서</th>
                    <th>연락처</th>
                    <th>생년월일</th>
                    <th>1365가입여부</th>
                    <th>상태</th>
                </thead>
                <tbody id='helperList'>
                    <tr v-for="item in projectHelpers" :key="item.knoxId">
                        <td><div class="ui checkbox"><input name="helper_check" :value="item.knoxId" type="checkbox"><label></label></div></td>
                        <td><a @click="editHelperModal(item.knoxId)" style="cursor: pointer">{{item.knoxId}}</a></td>
                        <td>{{item.userName}}</td>
                        <td>{{item.deptCode }}</td>
                        <td>{{item.phone }}</td>
                        <td>{{item.birthday}}</td>
                        <td>{{item.site1365}}</td>
                        <td>{{item.enable}}</td>       
                    </tr>
                </tbody>
            </table>
        </div>

        <div id="projectRegist" class="ui modal">
            <i class="close icon"></i>
            <div class="header">
                프로젝트 등록
            </div>
            <div class="content">
                <form class="ui form" id="projectRegForm">
                    <div class="ui message">
                        <div class="header">안내사항</div>
                        <ul class="list">
                            <li>봉사담당자가 자유롭게 프로젝트를 등록할 수 있습니다</li>
                        </ul>
                    </div>
                    <div class="field">
                        <label>Project Name</label>
                        <input type="text" name="projectName" placeholder="Project Name" v-model="project.projectName">
                    </div>
                    <div class="field">
                        <label>설명</label>
                        <input type="text" name="description" placeholder="description" v-model="project.description">
                    </div>
                    <div class="two fields">
                        <div class="field">
                            <label>Start Date</label>
                            <div class="ui calendar" id="startDate">
                                <div class="ui input left icon">
                                    <i class="calendar icon" />
                                    <input type="text" name="startDate" placeholder="Date" v-model="project.startDate" />
                                </div>
                            </div>
                        </div>
                        <div class="field">
                            <label>End Date</label>
                            <div class="ui calendar" id="endDate">
                                <div class="ui input left icon">
                                    <i class="calendar icon" />
                                    <input type="text" name="endDate" placeholder="Date" v-model="project.endDate" />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="field">
                        <label>투입인원</label>
                        <input type="number" name="maxHelperCount" placeholder="투입인원" v-model="project.maxHelperCount">
                    </div>
                    <div class="field">
                        <label>담당자</label>
                        <input type="text" name="owner" placeholder="담당자" v-model="project.owner">
                    </div>
                    <div class="field">
                        <label>추첨일(Cron Expression)</label>
                        <input type="text" name="triggerValue" placeholder="추첨일(Cron Expression)" v-model="project.triggerValue">
                    </div>
                    <div class="ui error message">

                    </div>
                </form>
            </div>            
            <div class="actions">
                <div class="ui button" @click="registSave">OK</div>
            </div>
        </div>


        <div id="helperRegist" class="ui modal">
            <i class="close icon"></i>
            <div class="header">
                프로젝트 봉사자 등록
            </div>
            <div class="content">
                <form class="ui form" id="helperRegForm">
                    <div class="ui message">
                        <div class="header">안내사항</div>
                        <ul class="list">
                            <li>프로젝트에 종속되 봉사자 리스트를 저장합니다</li>
                            <li>프로젝트별 봉사자를 관리할 수 있습니다</li>
                            <li>봉사자는 하나의 프로젝트에만 소속될 수 있습니다</li>
                        </ul>
                    </div>
                    <div class="field">
                        <label>Knox ID</label>
                        <div class="ui icon input">
                            <input type="text"  name="knoxId" placeholder="Knox ID" v-model="helper.knoxId" v-on:keyup.enter="searchHelper">
                            <i class="circular search link icon" @click="searchHelper"></i>
                        </div>

                    </div>
                    <div class="field">
                        <label>봉사자명</label>
                        <input type="text" name="userName" placeholder="봉사자명" v-model="helper.userName">
                    </div>
                    <div class="field">
                        <label>부서코드</label>
                        <input type="text" name="deptCode" placeholder="부서코드" v-model="helper.deptCode">
                    </div>
                    <div class="field">
                        <label>활성</label>
                        <div class="ui checkbox"><input name="enable" type="checkbox" v-model="helper.enable"><label>봉사추첨이 가능한 상태</label></div>
                    </div>

                    <div class="three fields">
                        <div class="field">
                            <label>연락처</label>
                            <input type="text" name="phone" placeholder="연락처" v-model="helper.phone">
                        </div>
                        <div class="field">
                            <label>생년월일</label>
                            <input type="text" name="birthday" minlength="6" maxlength="6" placeholder="생년월일" v-model="helper.birthday">
                        </div>
                        <div class="field">     
                            <label>1365 가입여부</label>         
                            <div class="fields">                                
                                <div class="field">
                                    <div class="ui radio checkbox">
                                        <input type="radio" name="site1365" v-model="helper.site1365" value="N" >
                                        <label>미가입</label>
                                    </div>
                                </div>
                                <div class="field">
                                    <div class="ui radio checkbox">
                                        <input type="radio" name="site1365" v-model="helper.site1365" value="Y">
                                        <label>가입</label>
                                    </div>
                                </div>           
                            </div>
                        </div>
                    </div>                    
                    <div class="ui error message">

                    </div>
                </form>
            </div>            
            <div class="actions">
                <div class="ui button" @click="saveHelper">저장</div>
            </div>
        </div>
    </div>
</template>


<script>
export default {
    data: function(){
        return {
            projects: [],
            project: {
                id: '',
                projectName: '',
                description: '',
                startDate: '',
                endDate: '',
                status: 'OPEN',
                maxHelperCount: '',
                owner: '',
                triggerValue: ''   
            },
            selectedProjectId: '',
            projectHelpers: [],
            helper: {
                knoxId: '',
                deptCode: '',
                userName: '',
                phone: '',
                site1365: 'N',
                birthday: '',
                enable: true,
                projectId: ''
            },
            checkedProject: [],
            checkedHelper: [],
            selectedProjectIndex: ''
        }
    },
    created: function(){
        this.getProjectList();
    },
    methods: {
        async getProjectList(){
            let that = this;
            await this.$axios.get('/api/helper/getProjectList').then(res => this.projects = res.data).catch(err=> this.$toast.error(err));
            $('#helper').find('.checkall').checkbox({
                onChecked: function() {
                    $('#helperList').find('.ui.checkbox').checkbox('check');
                },
                onUnchecked: function() {
                    $('#helperList').find('.ui.checkbox').checkbox('uncheck');
                }
            });

            $('#projectList').find('.ui.checkbox').checkbox({
               onChecked: function() { 
                   that.checkedProject.push($(this).val());     
               },
               onUnchecked: function() {
                   that.checkedProject.splice(findIndex(that.checkedProject, $(this).val()), 1);
               }
            });
        },

         async getProjectHelperList(){
            await this.$axios.get('/api/helper/project/'+this.helper.projectId).then(res => this.projectHelpers = res.data.helpers).catch(err=> this.$toast.error(err));
         },

        async closeProject(){
            let that = this;   
             await this.$axios.post('/api/helper/project/close', {
                 'id': this.checkedProject.toString()
             }).then().catch(err=> this.$toast.error(err));
             this.getProjectList();
             this.$toast.success('프로젝트가 종료되었습니다.')
        },

        async registSave(){
            $('#projectRegForm').form('validate form');
            if($('#projectRegForm').form('is valid')){  
                if(confirm('저장하시겠습니까?')){
                    await this.$axios.post('/api/helper/project/save', this.project).then(res => this.$toast.success('프로젝트가 등록되었습니다.')).catch(err=> this.$toast.error(err));
                    this.getProjectList();
                    $('#projectRegist').modal('hide');
                }               
            }
        },

        createProjectModal(){
            var that = this;
            $('#projectRegist').modal('show');

            $('#startDate').calendar({ type: 'date', endCalendar: $('#endDate'),
                formatter: {
                    date: function (date, settings) {
                    if (!date) return '';
                    var day = date.getDate();
                    var month = date.getMonth() + 1;
                    var year = date.getFullYear();
                    that.project.startDate = year + '-' + pad(month, 2) + '-' + pad(day, 2);
                    return year + '-' + pad(month, 2) + '-' + pad(day, 2);
                    }
            }});
            $('#endDate').calendar({ type: 'date', startCalendar: $('#startDate') ,
                formatter: {
                    date: function (date, settings) {
                    if (!date) return '';
                    var day = date.getDate();
                    var month = date.getMonth() + 1;
                    var year = date.getFullYear();
                    that.project.endDate = year + '-' + pad(month, 2) + '-' + pad(day, 2);
                    return year + '-' + pad(month, 2) + '-' + pad(day, 2);
                    }
            }});
        },

        async editProjectModal(id){ 
            var that = this;
            this.project.id = id;
            await this.searchProject();
            $('#projectRegist').modal('show');

            $('#startDate').calendar({ type: 'date', endCalendar: $('#endDate'),
                formatter: {
                    date: function (date, settings) {
                    if (!date) return '';
                    var day = date.getDate();
                    var month = date.getMonth() + 1;
                    var year = date.getFullYear();
                    that.project.startDate = year + '-' + pad(month, 2) + '-' + pad(day, 2);
                    return year + '-' + pad(month, 2) + '-' + pad(day, 2);
                    }
            }});
            $('#endDate').calendar({ type: 'date', startCalendar: $('#startDate') ,
                formatter: {
                    date: function (date, settings) {
                    if (!date) return '';
                    var day = date.getDate();
                    var month = date.getMonth() + 1;
                    var year = date.getFullYear();
                    that.project.endDate = year + '-' + pad(month, 2) + '-' + pad(day, 2);
                    return year + '-' + pad(month, 2) + '-' + pad(day, 2);
                    }
            }});
        },

        async searchProject(){
            var that = this;
            await this.$axios.get('/api/helper/getProject?id='+this.project.id).then(function(res){
                if(res.data != ''){        
                    that.project.id = res.data.id;
                    that.project.projectName = res.data.projectName;
                    that.project.description = res.data.description;
                    that.project.startDate = substring(res.data.startDate,0,10);
                    that.project.endDate =  substring(res.data.endDate,0,10);
                    that.project.status = res.data.status;
                    that.project.maxHelperCount = res.data.maxHelperCount;
                    that.project.owner = res.data.owner;
                    that.project.triggerValue = res.data.triggerValue; 
                }
            }).catch(err=> this.$toast.error(err));
        },

        createHelperModal(){
            this.initHelper();
            $('#helperRegist').find('input[name=knoxId]').attr('disabled', false);
            $('#helperRegist').modal('show');
        },

        async editHelperModal(knoxId){ 
            this.helper.knoxId = knoxId;
            $('#helperRegist').find('input[name=knoxId]').attr('disabled', true);

            await this.searchHelper();
            $('#helperRegist').modal('show');
        },

        async deleteHelper(){
            await this.$axios.post('/api/helper/project/helper/delete', {
                'projectId': this.helper.projectId,
                'knoxId': this.checkedHelper.toString()             
            }).then(res => this.$toast.success('Success')).catch(err=> this.$toast.error(err));
            this.getProjectHelperList();
        },

        closeHelperModal(){
            $('#helperRegist').modal('hide');
        },

        initProject(){
            that.project.id = '';
            that.project.projectName = '';
            that.project.description = '';
            that.project.startDate = '';
            that.project.endDate =  '';
            that.project.status = '';
            that.project.maxHelperCount = '';
            that.project.owner = '';
            that.project.triggerValue = ''; 
        },

        initHelper(){
            this.helper.knoxId = '';
            this.helper.userName = '';
            this.helper.deptCode = '';
            this.helper.phone = '';
            this.helper.birthday = '';
            this.helper.site1365 = 'N';
        },

        async getHelperList(index){
            let that = this;
            this.selectedProjectIndex = index
            this.helper.projectId = this.projects[index].id; 
            await this.getProjectHelperList(); 
            $('#helperList').find('.ui.checkbox').checkbox({
               onChecked: function() { 
                   that.checkedHelper.push($(this).val());     
               },
               onUnchecked: function() {
                   that.checkedHelper.splice(findIndex(that.checkedHelper, $(this).val()), 1);
               }
            });
        },

        async searchHelper(){
            let that = this;
            await this.$axios.get('/api/helper/getHelper?knoxId='+this.helper.knoxId).then(function(res){  
                if(res.data != ''){
                    that.helper.deptCode = res.data.deptCode;
                    that.helper.enable = res.data.enable;
                    that.helper.userName = res.data.userName;
                    that.helper.phone = res.data.phone;
                    that.helper.site1365 = res.data.site1365;
                    that.helper.birthday = res.data.birthday;
                }
            }).catch(err=> this.$toast.error(err));
        },

        async saveHelper(){
            let that = this;
            $('#helperRegForm').form('validate form');
            if($('#helperRegForm').form('is valid')){    
                if(confirm('저장하시겠습니까?')){
                    await this.$axios.post('/api/helper/project/helper/save', this.helper                        
                    ).then(function(res){
                        that.getProjectHelperList(); 
                        that.closeHelperModal();
                        
                    }).catch(err=> this.$toast.error(err));
                }
            }
        }
    },
    mounted: function(){
        $('#projectRegForm').form({
          keyboardShortcuts: false,
          fields: {
              projectName : {
                identifier: 'projectName',
                rules: [{
                    type   : 'empty',
                    prompt : '프로젝트명을 입력해주세요'
                }]  
              },
              description : {
                identifier: 'description',
                rules: [{
                    type   : 'empty',
                    prompt : '어떤 일을 하는지 적어주세요'
                }]
              },
              startDate : {
                identifier: 'startDate',
                rules: [{
                    type   : 'empty',
                    prompt : '시작일을 선택해주세요'
                }]
              },
              endDate : {
                identifier: 'endDate',
                rules: [{
                    type   : 'empty',
                    prompt : '종료일을 선택해주세요'
                }]
              },
              owner : {
                identifier: 'owner',
                rules: [{
                    type   : 'empty',
                    prompt : '봉사 담당자를 지정해주세요'
                }]
              },
              triggerValue : {
                identifier: 'triggerValue',
                rules: [{
                    type   : 'empty',
                    prompt : '추첨일(Cron Expression)을 입력해주세요'
                }]
              },
          }
      });

      $('#helperRegForm').form({
          keyboardShortcuts: false,
          fields: {
              knoxId : {
                identifier: 'knoxId',
                rules: [{
                    type   : 'empty',
                    prompt : 'Knox ID를 입력해주세요'
                }]
              },
              userName : {
                identifier: 'userName',
                rules: [{
                    type   : 'empty',
                    prompt : '봉사자명을 입력해주세요'
                }]
              },
              deptCode : {
                identifier: 'deptCode',
                rules: [{
                    type   : 'empty',
                    prompt : '부서코드를 입력해주세요'
                }]
              }
          }
      });

      $('#project').find('.checkall').checkbox({
            onChecked: function() {
                $('#projectList').find('.ui.checkbox').checkbox('check');
            },
            onUnchecked: function() {
                $('#projectList').find('.ui.checkbox').checkbox('uncheck');
            }
      });

      $('#helperRegist').find('.ui.checkbox').checkbox();
    }
}
</script>

<style>
@import "node_modules/semantic-ui-calendar/dist/calendar.min.css";
</style>