/** 
 * 
 */ 
$(document).ready(function() {
	var userId = $('#userId').val();
    // [Ajax]
    var main = {
        init: function() {
            var _this = this;
            $('#btn_add').on('click', function() {
                _this.add();
            });
        }, // init end

        // [작성 버튼 클릭 시]
        add: function() {
        	var data = {
        		isPublic: $('select[name=isPublic]').val(),
				isRecommend: $('select[name=isRecommend]').val(),
                blogCategoryId: $('select[name=category]').val(),
				title: $('#title').val(),
				thumbnail: $('#thumbnail').val(),
				content: $('#content').val()
             }; // data end
		    $.ajax({
				type: 'POST',
				url: '/blog/'+userId+'/add',
				dataType: 'text',
				contentType:'application/json; charset=utf-8',
				data: JSON.stringify(data)
			}).done(function(data){
				alert('작성에 성공하였습니다!!');
				location.href = '/blog/'+userId+'/view?id='+data;
			}).fail(function(error){
				console.log(JSON.stringify(error));
			});
       	 } // add end
    }// main end
	main.init();
});