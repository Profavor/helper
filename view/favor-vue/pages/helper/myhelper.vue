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
                    <col width="300">
                </colgroup>
                <thead>
                    <tr>
                    <th>Project</th>
                    <th>봉사일</th>
                    <th>Test</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>[2019] 아름다운가게 행궁점</td>
                        <td>2019-03-21</td>
                        <td>
                            <button class="ui basic button" @click="changeFrontier">
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
                        봉사 교체 요청합니다.
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
                            봉사일 : 2019-03-22
                        </div>
                        </div>
                        <div class="ui bottom attached button">
                        <i class="add icon"></i>
                        Add Friend
                        </div>
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
    methods: {
        changeFrontier(){
            var modal = $('#modal').iziModal({
                title: '봉사교체 요청',
                subtitle: '2019-03-21',
                fullscreen: true,
                width: 625,

            });
            console.log(modal);

            modal.iziModal('open');
        }
    }
}
</script>

