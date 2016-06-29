var base_url = "http://localhost:8080/MovRecTwo/rest/rec/";

$(document).ready(function() {
    
    	insertView_start();
});



function insertMovieInfo(imdb_id, element_id) {
	$.ajax({
	      url: "http://www.omdbapi.com/?i=tt" + imdb_id,
	      datatype: "jsonp",
	        success: function(data){
	            // Image
	        	$("." + element_id + " img.poster").attr("src", data["Poster"]);
	        	
	        	// Description
	        	$("." + element_id + " .desc").html(data["Plot"])
	        }
	    });
};



/* VIEWS */

function clear_view() {
	$("#content-area").empty();
	$(".nav.navbar-nav").children().removeClass("active");
}

function insertView_start() {
	// clear view
	clear_view();
	
	// set menu
	$(".menu_start").addClass("active");
	
	// load/insert base plugins
	var html_carousel_base = '<!-- Carousel ================================================== -->';
	html_carousel_base += '<div id="myCarousel" class="carousel slide" data-ride="carousel">';
	 html_carousel_base += '<!-- Indicators -->';
	  html_carousel_base += '<ol class="carousel-indicators">';
	html_carousel_base += '<!--  MovRec: carousel indicators -->';
	  html_carousel_base += '</ol>';
	  html_carousel_base += '<div class="carousel-inner" role="listbox">';
	html_carousel_base += '<!--  MovRec: carousel content -->';
	  html_carousel_base += '</div>';
	  html_carousel_base += '<a class="left carousel-control" href="#myCarousel" role="button" data-slide="prev">';
	html_carousel_base += '<span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>';
	html_carousel_base += '<span class="sr-only">Previous</span>';
	  html_carousel_base += '</a>';
	  html_carousel_base += '<a class="right carousel-control" href="#myCarousel" role="button" data-slide="next">';
	html_carousel_base += '<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>';
	html_carousel_base += '<span class="sr-only">Next</span>';
	  html_carousel_base += '</a>';
	html_carousel_base += '</div><!-- /.carousel -->';
	$("#content-area").append(html_carousel_base);
	
	var html_bubbles_base = '<div class="container marketing">';
	html_bubbles_base += '<!-- Three columns of text below the carousel -->';
	html_bubbles_base += '<div class="row bubbles">';
	html_bubbles_base += '<!--  MovRec: item bubbles content -->';
	html_bubbles_base += '</div><!-- /.row -->';
	html_bubbles_base += '</div>';
	$("#content-area").append(html_bubbles_base);
	
	
	// Load dynamic content
	$.ajax({
		dataType: 'json',
        url: base_url+"getMoviesForRating"
    }).then(function(data) {
    	var item_counter = 0;
    	jQuery.each(data, function(movieID, movieInfo) {

    		// generate carousel
    		var html_carousel_indicators = '<li data-target="#myCarousel" data-slide-to="'+item_counter+'" class=""></li>';
    		if(item_counter == 0) {
    			html_carousel_indicators = '<li data-target="#myCarousel" data-slide-to="'+item_counter+'" class="active"></li>';
    		}
    		$("#myCarousel .carousel-indicators").append(html_carousel_indicators);
    		
    		var html_carousel = '<div class="item movie_'+movieID+'">';
    		if(item_counter == 0){
    			html_carousel = '<div class="item active movie_'+movieID+'">';
    		}
	    		html_carousel += '<div class="container">';
	    		html_carousel += '<div class="carousel-caption"><div class="image">';
	    		html_carousel += '<img class="poster" src="data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==" alt="First slide">';
	    		html_carousel += '</div><div class="info"><h1>'+movieInfo[0]+'</h1>';
	    		html_carousel += '<p class="desc"></p>';
	    		html_carousel += '<p><a class="btn btn-lg btn-primary" href="#" role="button">View details</a></p>';
	    		html_carousel += '</div></div>';
	    		html_carousel += '</div>';
	    		html_carousel += '</div>';
	    	$("#myCarousel .carousel-inner").append(html_carousel);
    		
    	    // generate overview
    		var html = '<div class="col-lg-4 movie_'+movieID+'"><img class="poster img-circle" src="data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==" alt="Generic placeholder image" width="140" height="140">';
    			html += '<h2>'+ movieInfo[0] +'</h2>';
    			html += '<p>'+ movieInfo[1] +'</p>';
    			html += '<p><a class="btn btn-default" href="#" role="button">View details &raquo;</a></p>';
    			html += '</div><!-- /.col-lg-4 -->';    			
    			
    		$(".container.marketing .bubbles").append(html);
    		
    		// Load further information
    		insertMovieInfo(movieInfo[2], "movie_"+movieID);
    		
    		item_counter++;
    	});
    });
}



function insertView_ratings() {
	// clear view
	clear_view();
	
	// set menu
	$(".menu_ratings").addClass("active");
	
	// load/insert base plugins
	var html_base = '<div class="container marketing">';
	html_base += '<div><h1>Rate some movies to get recommendations</h1></div>';
	html_base += '</div>';
	$("#content-area").append(html_base);
	
	$.ajax({
		dataType: 'json',
        url: base_url+"getMoviesForRating"
    }).then(function(data) {
    	var item_counter = 0;
    	jQuery.each(data, function(movieID, movieInfo) {

    		// generate featurette
    		var html_featurette_base = '<hr class="featurette-divider">';
    		html_featurette_base += '<div class="row featurette movie_'+movieID+'">';
    		html_featurette_base += '<div class="col-md-7">';
    		html_featurette_base += '<h2 class="featurette-heading">'+movieInfo[0]+'</h2>';
    		html_featurette_base += '<p class="lead desc"></p>';
    		html_featurette_base += '</div>';
    		html_featurette_base += '<div class="col-md-5">';
    		html_featurette_base += '<img class="featurette-image img-responsive center-block poster" data-src="" alt="Poster">';
    		html_featurette_base += '</div>';
    		html_featurette_base += '</div>';
    		$(".container.marketing").append(html_featurette_base);
    		
    		// Load further information
    		insertMovieInfo(movieInfo[2], "movie_"+movieID);
    		
    		item_counter++;
    	});
    });
}