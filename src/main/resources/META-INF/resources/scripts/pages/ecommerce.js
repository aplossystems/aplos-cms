 function addBookmark() {
						
						if (window.sidebar) { 

							window.sidebar.addPanel(document.title, window.location,"");

						} else if ( window.external || (window.opera && window.print)) { 

							window.external.AddFavorite( window.location, document.title); 

						}
					 }