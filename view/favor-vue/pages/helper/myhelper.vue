<template>
    <div>
        <h5 class="ui top attached header">
            나의 봉사 내역
        </h5>
        <div class="ui attached segment">
            <table class="ui celled table">
                <colgroup>
                    <col width="*">
                    <col width="200">
                    <col width="200">
                </colgroup>
                <thead>
                    <tr>
                    <th>Project</th>
                    <th>봉사일</th>
                    <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                     <tr v-for="item in projectShifts" :key="item.id">
                        <td>{{item.project.projectName}}</td>
                        <td>{{item.helpDate}}</td>
                        <td>
                            <button class="ui basic button"  @click="changeFrontierPopup(item)">
                                <i class="icon user"></i>
                                봉사교체 요청
                            </button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <h5 class="ui attached header">
            봉사 교체 요청 목록
        </h5>
        
        <div class="ui attached segment">           
            <div class="ui cards">
                <div class="card" v-for="item in helper.helperChangeResponses" :key="item.id">
                    <div class="content">
                    <img class="right floated mini ui image">
                    <div class="header">
                        {{item.changeHelper.userName}}
                    </div>
                    <div class="meta">
                        {{item.projectShift.helpDate}}
                    </div>
                    <div class="description">
                       <div class="ui list">
                            <div class="item">
                                <i class="map marker icon"></i>
                                <div class="content">
                                <div class="header">{{item.projectShift.project.projectName}}</div>
                                <div class="description">{{item.changeHelper.message}}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    </div>
                    <div class="extra content">
                    <div class="ui two buttons">
                        <div class="ui basic green button">승낙</div>
                        <div class="ui basic red button">거부</div>
                    </div>
                    </div>
                </div>                
            </div>
        </div>
        
        <div id="changeHelperRequestModal" class="ui modal">
            <i class="close icon"></i>
            <div class="header">
                봉사자 교체요청
            </div>
            <div class="content" style="max-height: 420px; overflow: auto;"> 
                <div class="ui cards">
                    <div class="card" v-for="item in changeShifts" :key="item.id"> 
                        <div class="content">
                            <div class="header">{{item.helper.userName}}</div>
                            <div class="description">
                                봉사일 : {{item.helpDate}}
                            </div>
                        </div>
                       <button class="ui button" @click="changeHelperRequest(item)">
                            변경 요청
                        </button>
                    </div>                    
                </div>
            </div>
        </div>
    </div>
</template>

<script>

export default {
    created: function(){
        this.usr = this.$auth.$state.user;
        this.refreshMyHelper();
    },
    data: function(){
        return {
             usr: {},
             projectId: '',
             changeShiftDate: '',
             projectShifts: [],
             helper: {},
             changeShifts: []
        }
    },
    methods: {
        refreshMyHelper(){
            this.getProjectShiftListByKnoxId();
            this.getHelper();
        },

        changeFrontierPopup(item){
            this.projectId = item.project.id;
            this.changeShiftDate = item.helpDate;
            this.getProjectShiftHelpers();
            $('#changeHelperRequestModal').modal('show');
        },

        async changeHelper(){
            await this.$axios.post('/api/helper/getProjectShiftListByKnoxId?knoxId='+this.usr.loginId)
                .then(res => this.projectShifts = res.data).catch(err=> this.$toast.error(err));
        },

        async changeHelperRequest(item){      
            let that = this;
            await this.$axios.post('/api/helper/changeRequest', {
                'changeHelper': {
                    'knoxId': item.helper.knoxId
                },
                'projectShift': {
                    'helpDate': item.helpDate,
                    'projectId': that.projectId
                },
                'changeHelpDate': that.changeShiftDate
            }).then(res => console.log(res)).catch(err=> this.$toast.error(err));
            
        },

        async getProjectShiftListByKnoxId(){
            let that = this;
            await this.$axios.get('/api/helper/getProjectShiftListByKnoxId?knoxId='+this.usr.loginId)
                .then(res => this.projectShifts = res.data).catch(err=> this.$toast.error(err));
        },

        async getHelper(){
            let that = this;
            await this.$axios.get('/api/helper/getHelper?knoxId='+this.usr.loginId)
                .then(res => this.helper = res.data).catch(err=> this.$toast.error(err));
        },

        async getProjectShiftHelpers(){
            await this.$axios.get('/api/helper/getProjectShiftChangeHelpers?projectId='+this.projectId)
                .then(res => this.changeShifts = res.data.results).catch(err=> this.$toast.error(err)); 
        }
    }
}
</script>

