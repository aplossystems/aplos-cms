
			var css_editors = new Array();
	
			function updateCodeMirrors() {
				document.getElementById( 'css_editor' ).value = css_editor.getCode();
			}

			$j(document).ready(function() {
				initPage();
			}); 
			
			function initPage() {
				$j("#tabs").tabs({ cookie: { expires: 30, name: 'cmsPageRevisionEditTabs' } });
				var height = $j("#tabs").css('height');
				$j("#tabs").css('height','auto');
				$j("#tabs").css('min-height',height);
				ckEditorHelper.createCKEditors( { "height" : "500px" } );
			}
			
			function setConfirmUnload(on) {
			   window.onbeforeunload = (on) ? unloadMessage : null;
			}

			function unloadMessage() {
			     return "You have unsaved changes that will be lost.";
			}