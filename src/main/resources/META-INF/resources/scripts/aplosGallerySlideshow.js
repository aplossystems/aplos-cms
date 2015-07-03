
function aplosGallerySlideSwitch() {
    var active = $j('.aplos-gallery-atom-slideshow IMG.active');

    if ( active.length == 0 ) active = $j('#aplos-gallery-atom-slideshow IMG:last');

    var next =  active.next().length ? active.next()
        : $j('.aplos-gallery-atom-slideshow IMG:first');


    active.addClass('last-active');

    next.css({opacity: 0.0})
        .addClass('active')
        .animate({opacity: 1.0}, 1000, function() {
            active.removeClass('active last-active');
        });
}

$j(function() {
    setInterval( "aplosGallerySlideSwitch()", 5000 );
});

