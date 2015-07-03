$(function(){
	var items = (Math.floor(Math.random() * ($('#aplos-testimonials li').length)));
	$('#aplos-testimonials li').hide().eq(items).show();
	
  function next(){
		$('#aplos-testimonials li:visible').delay(5000).fadeOut('slow',function(){
			$(this).appendTo('#aplos-testimonials ul');
			$('#aplos-testimonials li:first').fadeIn('slow',next);
    });
   }
  next();
});