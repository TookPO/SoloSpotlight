/**
 * 
 */
 
 $(window).resize(function(){
	if($(window).width()<992){
		$('.div_menu-btn').removeClass('ml-4');
	}
 	if($(window).width()<768){
  		$('.div_title').removeClass('col-9');
		$('.div_title').addClass('col-8 mr-4');
		
		$('.div_menu-btn').removeClass('col-2');
		$('.div_menu-btn').addClass('col-3');

 	}

});