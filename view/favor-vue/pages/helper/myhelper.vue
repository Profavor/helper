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
                            <button class="ui basic button"  @click="changeHelperRequest">
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
                <div class="card">
                    <div class="content">
                    <img class="right floated mini ui image">
                    <div class="header">
                        김유현
                    </div>
                    <div class="meta">
                        2019-03-21
                    </div>
                    <div class="description">
                       <div class="ui list">
                            <div class="item">
                                <i class="map marker icon"></i>
                                <div class="content">
                                <div class="header">아름다운가게 행궁점</div>
                                <div class="description">봉사 교체원합니다.</div>
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
                <div class="card">
                    <div class="content">
                    <img class="right floated mini ui image">
                    <div class="header">
                        Jenny Hess
                    </div>
                    <div class="meta">
                        New Member
                    </div>
                    <div class="description">
                        Jenny wants to add you to the group <b>best friends</b>
                    </div>
                    </div>
                    <div class="extra content">
                        <div class="ui two buttons">
                            <div class="ui basic green button">Approve</div>
                            <div class="ui basic red button">Decline</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div id="modal" class="iziModal">
            <div class="ui segment">
                <div class="ui cards">
                    <div class="card"> 
                        <div class="content">
                        <div class="header">서동권</div>
                        <div class="description">
                            봉사일 : 2019-03-22 ss
                        </div>
                        </div>
                       <button class="ui button">
                        Follow
                        </button>
                    </div>
                    <div class="card">
                        <div class="content">
                        <div class="header">Veronika Ossi</div>
                        <div class="description">
                            Veronika Ossi is a set designer living in New York who enjoys kittens, music, and partying.
                        </div>
                        </div>
                        <div class="ui bottom attached button">
                        <i class="add icon"></i>
                        Add Friend
                        </div>
                    </div>
                    <div class="card">
                        <div class="content">
                        <div class="header">Jenny Hess</div>
                        <div class="description">
                            Jenny is a student studying Media Management at the New School
                        </div>
                        </div>
                        <div class="ui bottom attached button">
                        <i class="add icon"></i>
                        Add Friend
                        </div>
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
        this.getProjectShiftListByKnoxId();
    },
    data: function(){
        return {
             usr: {},
             projectShifts: []
        }
    },
    methods: {
        changeFrontierPopup(){
            var modal = $('#modal').iziModal({
                title: '봉사교체 요청',
                subtitle: '2019-03-21',
                fullscreen: true,
                width: 625,

            });
            modal.iziModal('open');
        },

        async changeHelper(){
            await this.$axios.post('/api/helper/getProjectShiftListByKnoxId?knoxId='+this.usr.loginId)
                .then(res => this.projectShifts = res.data).catch(err=> this.$toast.error(err));
        },

        async changeHelperRequest(){
            let that = this;
            await this.$axios.post('/api/helper/changeRequest', {
                'changeHelper': {
                    'knoxId': 'test3'
                },
                'projectShift': {
                    'helpDate': '2019-02-13',
                    'projectId': 'b12ae082687068d30168706c760d0001'
                }
            }).then(res => console.log(res)).catch(err=> this.$toast.error(err));
            
        },

        async getProjectShiftListByKnoxId(){
            let that = this;
            await this.$axios.get('/api/helper/getProjectShiftListByKnoxId?knoxId='+this.usr.loginId)
                .then(res => this.projectShifts = res.data).catch(err=> this.$toast.error(err));
            
            console.log(this.projectShifts);
        },
    }
}
</script>

