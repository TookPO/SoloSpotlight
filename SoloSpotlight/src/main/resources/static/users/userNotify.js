/**
 * 
 */

$(window).resize(function(){
 	if($(window).width()<768){
  		$('.div_layout-border-box').removeClass('col-5');
		$('.div_layout-border-box').addClass('col-12');
		
		$('.div_title','div_filter').removeClass('col-2');
		$('.div_title','div_filter').addClass('col-4');
		
 	}

});