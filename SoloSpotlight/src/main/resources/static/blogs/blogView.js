/**
 * 
 */
$(document).ready(function() {
	var userId = $('#userId').val();
	var params = {};
	window.location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(str, key, value) { params[key] = value; });
    // [Ajax]
    var main = {
        init: function() {
            var _this = this;
            $('#btn_reply').on('click', function() {
                _this.add();
            });
        }, // init end

        // [작성 버튼 클릭 시]
        add: function() {
        	var data = {
				postId : params['id'],
				content : $('#reply_content').val()
             }; // data end
		    $.ajax({
				type: 'POST',
				url: '/blog/'+userId+'/reply/add',
				dataType: 'text',
				contentType:'application/json; charset=utf-8',
				data: JSON.stringify(data)
			}).done(function(data){
				alert('작성에 성공하였습니다!!');
				location.reload();
			}).fail(function(error){
				alert('작성에 실패하였습니다.');
				console.log(JSON.stringify(error));
			});
       	 } // add end
    }// main end
	main.init();
});