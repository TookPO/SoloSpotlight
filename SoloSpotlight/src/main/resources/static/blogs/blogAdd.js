$(document).ready(function() {
    var i = 1;
    // [카테고리]
    $('#btn_category-plus').click(function() {
        if (i == 10) {
            alert('10개가 최대입니다.');
            return
        }
        $('#div_plus-category-btn').before('<div class="col-md-3 mb-3 plus_category_' + (++i) + '"> </div>');
        $('.plus_category_' + i).append('<input type="text" class="form-control div_plus-category-input category" name="category" maxlength="10" placeholder="" required="">');
        $('.plus_category_' + i).append('<div class="invalid-feedback"> 카테고리를 입력하세요. </div>');
    });

    $('#btn_category-minus').click(function() {
        $('.plus_category_' + (i--)).remove();
    });

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
       		var categoryList = [];
        	$('.category').each(function() {
				categoryList.push($(this).val());
				console.log('리스트:'+categoryList);
        	});
        	var data = {
        		name: $('#name').val(),
                intro: $('#intro').val(),
				category: categoryList,
				headerColor: $('input[name="headerColor"]:checked').val()
             }; // data end
		    $.ajax({
				type: 'POST',
				url: '/blog/add',
				dataType: 'text',
				contentType:'application/json; charset=utf-8',
				data: JSON.stringify(data)
			}).done(function(data){
				alert('블로그가 생성되었습니다!!');
				location.href = '/blog/'+data;
			}).fail(function(error){
				console.log(JSON.stringify(error));
			});
       	 } // add end
    }// main end
	main.init();
});