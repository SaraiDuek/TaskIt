/*
    Slider
*/
// function refreshPage () {
//     var page_y = document.getElementsByTagName("body")[0].scrollTop;
//     window.location.href = window.location.href.split('?')[0] + '?page_y=' + page_y;
// }


// window.onload = function refresh() {
//     setTimeout(refreshPage, 35000);
//     if ( window.location.href.indexOf('page_y') != -1 ) {
//         var match = window.location.href.split('?')[1].split("&")[0].split("=");
//         document.getElementsByTagName("body")[0].scrollTop = match[1];
//     }
// };

// $('#ref').each(function(){
//      this.href += $(window).scrollTop();;
// })


var ScrollSneak = function(prefix, wait) {
    // clean up arguments (allows prefix to be optional - a bit of overkill)
    if (typeof(wait) == 'undefined' && prefix === true) prefix = null, wait = true;
    prefix = (typeof(prefix) == 'string' ? prefix : window.location.host).split('_').join('');
    var pre_name;
 
    // scroll function, if window.name matches, then scroll to that position and clean up window.name
    this.scroll = function() {
        if (window.name.search('^'+prefix+'_(\\d+)_(\\d+)_') == 0) {
            var name = window.name.split('_');
            window.scrollTo(name[1], name[2]);
            window.name = name.slice(3).join('_');
        }
    }
    // if not wait, scroll immediately
    if (!wait) this.scroll();
 
    this.sneak = function() {
    // prevent multiple clicks from getting stored on window.name
    if (typeof(pre_name) == 'undefined') pre_name = window.name;
 
    // get the scroll positions
        var top = 0, left = 0;
        if (typeof(window.pageYOffset) == 'number') { // netscape
            top = window.pageYOffset, left = window.pageXOffset;
        } else if (document.body && (document.body.scrollLeft || document.body.scrollTop)) { // dom
            top = document.body.scrollTop, left = document.body.scrollLeft;
        } else if (document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) { // ie6
            top = document.documentElement.scrollTop, left = document.documentElement.scrollLeft;
        }
    // store the scroll
        if (top || left) window.name = prefix + '_' + left + '_' + top + '_' + pre_name;
        return true;
    }
}


$(document).ready(function(){
    var sneaky = new ScrollSneak(location.hostname);
    document.getElementsByTagName("a").onclick = sneaky.sneak;
    // you want to prevent scrolling when form #my-form is submitted
    //document.getElementById('my-form').onsubmit = sneaky.sneak;

    // or maybe you want to prevent scrolling whenever any link within 
    // a list-option is clicked:
    //document.getElementsByTagName("p").each(function(){
        // note the use of 'this' instead of '$(this)', because we
        // want the raw element, not the jQuery object       
        this.onclick = sneaky.sneak;
    });

$(window).load(function() {
    $('.flexslider').flexslider({
        animation: "slide",
        controlNav: "thumbnails"
    });
});

$("#logout").click(onLogout);

function onLogout() {
	$.ajax({
		type : "POST",
		url : "/HW5/Logout",
		success : onLogoutPost,
		error :onLogoutPost,
	});
	return false;
}

function onLogoutPost() {
	window.location.href = "/HW5";
}


/*
    Filterable portfolio
*/
jQuery(document).ready(function() {
    $clientsHolder = $('ul.portfolio-img');
    $clientsClone = $clientsHolder.clone(); 
 
    $('.filter-portfolio a').click(function(e) {
        e.preventDefault();
        $filterClass = $(this).attr('class');
 
        $('.filter-portfolio a').attr('id', '');
        $(this).attr('id', 'active-imgs');
 
        if($filterClass == 'all'){
            $filters = $clientsClone.find('li');
        }
        else {
            $filters = $clientsClone.find('li[data-type~='+ $filterClass +']');
        }
 
        $clientsHolder.quicksand($filters, {duration: 700}, function() {
            $("a[rel^='prettyPhoto']").prettyPhoto({social_tools: false});
        });
    });
});


/*
    Pretty Photo
*/
jQuery(document).ready(function() {
    $("a[rel^='prettyPhoto']").prettyPhoto({social_tools: false});
});


/*
    Show latest tweets
*/
jQuery(function($) {
    $(".show-tweets").tweet({
        username: "anli_zaimi",
        page: 1,
        count: 10,
        loading_text: "loading ..."
    }).bind("loaded", function() {
        var ul = $(this).find(".tweet_list");
        var ticker = function() {
            setTimeout(function() {
                ul.find('li:first').animate( {marginTop: '-4em'}, 500, function() {
                    $(this).detach().appendTo(ul).removeAttr('style');
                });
                ticker();
            }, 5000);
        };
        ticker();
    });
});


/*
    Flickr feed
*/
$(document).ready(function() {
    $('.flickr-feed').jflickrfeed({
        limit: 8,
        qstrings: {
            id: '52617155@N08'
        },
        itemTemplate: '<li><a href="{{link}}" target="_blank"><img src="{{image_s}}" alt="{{title}}" /></a></li>'
    });
});


/*
    Google maps
*/
jQuery(document).ready(function() {
    var position = new google.maps.LatLng(45.067883, 7.687231);
    $('.map').gmap({'center': position,'zoom': 15, 'disableDefaultUI':true, 'callback': function() {
            var self = this;
            self.addMarker({'position': this.get('map').getCenter() });	
        }
    }); 
});


/*
    Contact form
*/
jQuery(document).ready(function() {
    $('.contact-form form').submit(function() {

        $('.contact-form form .nameLabel').html('Name');
        $('.contact-form form .emailLabel').html('Email');
        $('.contact-form form .messageLabel').html('Message');

        var postdata = $('.contact-form form').serialize();
        $.ajax({
            type: 'POST',
            url: 'assets/sendmail.php',
            data: postdata,
            dataType: 'json',
            success: function(json) {
                if(json.nameMessage != '') {
                    $('.contact-form form .nameLabel').append(' - <span class="violet" style="font-size: 13px; font-style: italic"> ' + json.nameMessage + '</span>');
                }
                if(json.emailMessage != '') {
                    $('.contact-form form .emailLabel').append(' - <span class="violet" style="font-size: 13px; font-style: italic"> ' + json.emailMessage + '</span>');
                }
                if(json.messageMessage != '') {
                    $('.contact-form form .messageLabel').append(' - <span class="violet" style="font-size: 13px; font-style: italic"> ' + json.messageMessage + '</span>');
                }
                if(json.nameMessage == '' && json.emailMessage == '' && json.messageMessage == '') {
                    $('.contact-form form').fadeOut('fast', function() {
                        $('.contact-form').append('<p><span class="violet">Thanks for contacting us!</span> We will get back to you very soon.</p>');
                    });
                }
            }
        });
        return false;
    });
});

jQuery(document).ready(function() {

    /*
        Tooltips
    */
    $('.links a.home').tooltip();
    $('.links a.blog').tooltip();

    /*
        Form validation
    */
    $('.register form').submit(function(){
        $(this).find("label[for='firstname']").html('First Name');
        $(this).find("label[for='lastname']").html('Last Name');
        $(this).find("label[for='username']").html('Username');
        $(this).find("label[for='email']").html('Email');
        $(this).find("label[for='password']").html('Password');
        ////
        var firstname = $(this).find('input#firstname').val();
        var lastname = $(this).find('input#lastname').val();
        var username = $(this).find('input#username').val();
        var email = $(this).find('input#email').val();
        var password = $(this).find('input#password').val();
        if(firstname == '') {
            $(this).find("label[for='firstname']").append("<span style='display:none' class='red'> - Please enter your first name.</span>");
            $(this).find("label[for='firstname'] span").fadeIn('medium');
            return false;
        }
        if(lastname == '') {
            $(this).find("label[for='lastname']").append("<span style='display:none' class='red'> - Please enter your last name.</span>");
            $(this).find("label[for='lastname'] span").fadeIn('medium');
            return false;
        }
        if(username == '') {
            $(this).find("label[for='username']").append("<span style='display:none' class='red'> - Please enter a valid username.</span>");
            $(this).find("label[for='username'] span").fadeIn('medium');
            return false;
        }
        if(email == '') {
            $(this).find("label[for='email']").append("<span style='display:none' class='red'> - Please enter a valid email.</span>");
            $(this).find("label[for='email'] span").fadeIn('medium');
            return false;
        }
        if(password == '') {
            $(this).find("label[for='password']").append("<span style='display:none' class='red'> - Please enter a valid password.</span>");
            $(this).find("label[for='password'] span").fadeIn('medium');
            return false;
        }
    });


});
