
function slideSwitch() {
    var active = $j('#slideshow IMG.active');

    if ( active.length == 0 ) active = $j('#slideshow IMG:last');

    var next =  active.next().length ? active.next()
        : $j('#slideshow IMG:first');


    active.addClass('last-active');

    next.css({opacity: 0.0})
        .addClass('active')
        .animate({opacity: 1.0}, 1000, function() {
            active.removeClass('active last-active');
        });
}

$j(function() {
    setInterval( "slideSwitch()", 5000 );
});

