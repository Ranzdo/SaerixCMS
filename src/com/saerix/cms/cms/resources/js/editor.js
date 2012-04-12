(function( $ ){
	
	 var methods = {
		init : function(options) {
			$(this).hide();
			
			$(this).data('editor', CodeMirror.fromTextArea(document.getElementById(options.codeid),{
		        lineNumbers: true,
		        matchBrackets: true,
			}));
			
			$(this).data('tabs', new Array());
		},
		open : function(options) {
			if('key' in editor_items) {
				 editor.setOption("mode", mode);
				 editor.setValue(editor_items[key]);
			}
			else {
				$.get(url+'editor/get?type='+type+'&id='+id, function(data) {
					 editor.setOption("mode", mode);
					 editor.setValue(data);
					 editor_items[type+id] = data;
				}).error(function() { alert("Could not load the file."); });
			}
		}
	 };
	
	$.fn.editor = function(method) {
	    if (methods[method]) {
	    	if($(this).data('editor') == null)
	    		$.error( 'Editor is not initiated.' );
	    	else
	    		return methods[ method ].apply( this, arguments);
	    }
		else if (typeof method === 'object' || ! method)
			return methods.init.apply( this, arguments );
		else
			$.error('Method ' +  method + ' does not exist');
	};
})( jQuery );