/**
 * 
 */
// 전역 변수
var userId = $('#userId').val();
var params = {};
window.location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(str, key, value) {
	params[key] = value;
 });
var categoryPage = "";
var sendUrl = window.location.href;
var replyAtId = "";
var replyAtValue = "";
const POSTURL = window.location.pathname + window.location.search

// 카카오톡 공유
function shareKakao() {

}

// 인스타 공유
function shareInstar() {

}

// 페이스북 공유
function shareFaceBook() {
    alert('');
}

// 네이버 공유
function shareNaver() {

}

// 게시글 삭제
function postDelete() {
    var act = confirm('정말 삭제하시겠습니까?' +
        '\n삭제 후 해당 게시글 및 댓글은 복원할 수 없습니다.');
    if (act) {
        var data = {
            postId: params['id']
        }; // data end
        $.ajax({
            type: 'DELETE',
            url: '/blog/' + userId + '/delete',
            dataType: 'text',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function(data) {
			if(data == 1){
            	alert('게시글을 삭제하였습니다!');
				location.reload();
			}else{
				alert('삭제에 실패하였습니다.');
			}	           
        }).fail(function(error) {
            alert('삭제하지 못했습니다.');
            console.log(JSON.stringify(error));
        }); // ajax end
    } // if end
} // postDelete end

// 답글 버튼
function replyAtAdd(replyId, writerName, writerId) {
    if ($('#div_replyAt').length > 0) {
        $('#div_replyAt').remove();
    }
    $('#reply_content')
        .before(
            '<div id="div_replyAt" class="mb-2">' +
            '	<a style="font-size: 20px;">@' + writerName + '</a>' +
            '	<a onClick="replyAtRemove()">답글 취소</a>' +
            '</div>');
    // 작성 시, 추가 할 @유저이름
    replyAtValue = '<a class="a_reply-at-writed" style="font-size: 20px;" href="/user/info?id=' + replyId + '">@' + writerName + '</a>';
    replyAtId = writerId;
}

// 답글 취소
function replyAtRemove() {
    $('#div_replyAt').remove();
    replyAtValue = "";
    replyAtId = "";
}

$(document).ready(function() {
    prevCategoryPage = params['id']
    nextCategoryPage = params['id'] // 카테고리 리스트의 맨 마지막 게시글의 id

    // [Ajax]
    var main = {
        init: function() {
            var _this = this;
            // 댓글 작성
            $('#btn_reply').on('click', function() {
                _this.add_reply();
            });
            // 좋아요 버튼
            $('#btn_good').on('click', function() {
                _this.add_good();
            });
            // 이전 페이지
            $('#btn_list-prev').on('click', function() {
                _this.change_prev();
            });
            // 다음 페이지
            $('#btn_list-next').on('click', function() {
                _this.change_next();
            });
            // 스폿 추가
            $('#btn_spot-add').on('click', function() {
                _this.add_spot();
            });
        }, // init end

        // [댓글 작성 클릭 시]
        add_reply: function() {
            var data = {
                postId: params['id'],
                content: (replyAtValue + $('#reply_content').val()),
                replyAtId: replyAtId,
                replyAtInfo: POSTURL
            }; // data end
            $.ajax({
                type: 'POST',
                url: '/blog/' + userId + '/reply/add',
                dataType: 'text',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(data)
            }).done(function(data) {
                alert('작성에 성공하였습니다!!');
                location.reload();
            }).fail(function(error) {
                alert('작성에 실패하였습니다.');
                console.log(JSON.stringify(error));
            });
        }, // add_reply end

        // [좋아요 버튼 클릭 시]
        add_good: function() {
            var data = {
                postId: params['id']
            }; // data end
            $.ajax({
                type: 'POST',
                url: '/blog/' + userId + '/good/add',
                dataType: 'text',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(data)
            }).done(function(data) {
                if (data == 0) {
                    alert('이미 좋아요를 눌렀습니다.');
                } else {
                    location.reload();
                }
            }).fail(function(error) {
                alert('좋아요를 누를 수 없습니다.');
                console.log(JSON.stringify(error));
            });
        }, // add_good end

        // [이전 리스트] (최근 게시글 쪽으로)
        change_prev: function() {
            var data = {
                postId: prevCategoryPage // 이전, 다음 페이지를 누르면 값이 바뀜
            }; // data end
            $.ajax({
                type: 'POST',
                url: '/blog/' + userId + '/category/list/prev',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(data)
            }).done(function(data) {
                if (data.ret == 0 || data.ret == null) { // 페이지 없으면 0
                    alert('이전 페이지가 없습니다.');
                } else { // 페이지가 있으면 0초과
                    // Map으로 변환
                    const dataMap = new Map();
                    for (const [key, value] of Object.entries(data)) {
                        dataMap.set(key, value);
                    }
                    // list의 컬럼을 전부 지움
                    $('#tbody_category-page-list').empty();
                    var i = 0;
                    prevCategoryPage = dataMap.get('postId' + i); // 최근 게시물의 번호를 가져옴
                    while (dataMap.get('postId' + i) != null) {
                        var thisPost = "";
                        if (params['id'] == dataMap.get('postId' + i)) { // 만약 현재 게시글 일 경우 글씨를 bold로 변경
                            thisPost = "font-weight-bold";
                        }
                        $('#tbody_category-page-list')
                            .append(
                                '<tr>' +
                                '<td colspan="2">' +
                                '<a href="/blog/' + userId + '/view?id=' + dataMap.get('postId' + i) + '" class="' + thisPost + '" style="font-size: 13px;">' +
                                dataMap.get('postTitle' + i) + ' (' + dataMap.get('postMaxReply' + i) + ')' +
                                '</a>' +
                                '</td>' +
                                '<td>' +
                                dataMap.get('postDate' + i) +
                                '</td>' +
                                '</tr>');
                        nextCategoryPage = dataMap.get('postId' + i); // 오래된 게시물의 번호를 가져옴
                        thisPost = "";
                        i++;
                    }
                }
            }).fail(function(error) {
                alert('페이지가 없습니다.');
                console.log(JSON.stringify(error));
            });
        }, // change_prev end

        // [다음 리스트] (최근 게시글 쪽으로)
        change_next: function() {
            var data = {
                postId: nextCategoryPage // 이전, 다음 페이지를 누르면 값이 바뀜
            }; // data end
            $.ajax({
                type: 'POST',
                url: '/blog/' + userId + '/category/list/next',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(data)
            }).done(function(data) {
                if (data.ret == 0 || data.ret == null) { // 페이지 없으면 0
                    alert('다음 페이지가 없습니다.');
                } else { // 페이지가 있으면 0초과
                    // Map으로 변환
                    const dataMap = new Map();
                    for (const [key, value] of Object.entries(data)) {
                        dataMap.set(key, value);
                    }
                    // list의 컬럼을 전부 지움
                    $('#tbody_category-page-list').empty();
                    var i = 0;
                    prevCategoryPage = dataMap.get('postId' + i); // 최근 게시물의 번호를 가져옴
                    while (dataMap.get('postId' + i) != null) {
                        var thisPost = "";
                        if (params['id'] == dataMap.get('postId' + i)) { // 만약 현재 게시글 일 경우 글씨를 bold로 변경
                            thisPost = "font-weight-bold";
                        }
                        $('#tbody_category-page-list')
                            .append(
                                '<tr>' +
                                '<td colspan="2">' +
                                '<a href="/blog/' + userId + '/view?id=' + dataMap.get('postId' + i) + '" class="' + thisPost + '" style="font-size: 13px;">' +
                                dataMap.get('postTitle' + i) + ' (' + dataMap.get('postMaxReply' + i) + ')' +
                                '</a>' +
                                '</td>' +
                                '<td>' +
                                dataMap.get('postDate' + i) +
                                '</td>' +
                                '</tr>');
                        nextCategoryPage = dataMap.get('postId' + i); // 이번에는 마지막 값(제일 낮은)이 들어감
                        thisPost = "";
                        i++;
                    }
                }
            }).fail(function(error) {
                alert('페이지가 없습니다.');
                console.log(JSON.stringify(error));
            });
        }, // change_next end

        // [스폿하기 버튼 클릭 시]
        add_spot: function() {
            var data = {
                followeeId: userId
            }; // data end
            $.ajax({
                type: 'POST',
                url: '/blog/spot/add',
                dataType: 'text',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(data)
            }).done(function(data) {
                if (data == "true") {
                    alert('스폿이 되었습니다!');
                } else if (data == "same") {
                    alert('자신을 스폿할 수 없습니다.');
                } else {
                    alert('이미 스폿이 되어있습니다.');
                }
            }).fail(function(error) {
                alert('스폿을 처리 할 수 없습니다.');
                console.log(JSON.stringify(error));
            });
        } // add_spot end
    } // main end
    main.init();
});