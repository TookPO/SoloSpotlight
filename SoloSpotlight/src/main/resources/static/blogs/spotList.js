/**
 * 
 */
function addGood(postId, userId) {
    var data = {
        postId
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
}