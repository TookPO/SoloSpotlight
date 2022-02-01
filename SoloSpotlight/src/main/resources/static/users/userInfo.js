/**
 * 
 */

$(window).resize(function(){
 	if($(window).width()<769){
  		$('.div_user-box').removeClass('col-3');
		$('.div_user-box').addClass('col-10 mr-4 ml-4');
		
		$('.div_history-box').removeClass('col-8');
		$('.div_history-box').addClass('col-10 mr-4 mt-2');
		
		$('.div_fix-btn').addClass('div_layout-border');
		$('.btn_info-fix').addClass('mb-4');
		
 	}

});