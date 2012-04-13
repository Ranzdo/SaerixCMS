(function( $ ){
	 var methods = {
		init : function(options) {	
			$(this).data('editor', CodeMirror.fromTextArea(document.getElementById(options.codeid),{
		        lineNumbers: true,
		        matchBrackets: true,
			}));
			
			$(this).data('tabs', new Array());
			
			$(this).prepend('<div class="editor-tabs"></div>');
		},
		open : function(id, type, name) {
			var g = this;
			if($(this).children('.editor-tabs').first().children('.'+type+id+'.editor-tab.'+type).length == 0) {
				$.get(url+'editor/get?type='+type+'&id='+id, function(data) {
					$('.editor-tabs').append('<div class="editor-tab '+type+' '+type+id+'">'+name+'</div>');
					$('.editor-tab').last().data('tab', {'id' : id, 'type' : type, content : data});
					methods.select.apply(g, new Array(id, type));
				}).error(function() { alert('Could not load the file.'); });
			}
			else
				methods.select.apply(this, new Array(id, type));
		},
		select: function(id, type) {
			var local = $(this).children('.editor-tabs').first().children('.'+type+id+'.editor-tab.'+type);
			var selected = $(this).children('.editor-tabs').first().children('.editor-tab, .selected');
			
			if(selected != null) {
				$(selected).removeClass("selected");
			}
			
			$(local).addClass('selected');
			
			var editor = $(this).data('editor');
			var mode;
			if(type == 'controller' || type == 'model')
				mode = 'text/x-groovy';
			else if(type == 'view')
				mode = 'view';
			else
				alert('Could not found a mode for the selected document');
			
			editor.setOption("mode", mode);
			editor.setValue($(local).data('tab').content);
		}
	 };
	
	$.fn.editor = function(method, parameters) {
	    if (methods[method]) {
	    	if($(this).data('editor') == null)
	    		$.error( 'Editor is not initiated.' );
	    	else
	    		return methods[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
	    }
		else if (typeof method === 'object' || ! parameters)
			return methods.init.apply( this, arguments );
		else
			$.error('Method ' +  method + ' does not exist');
	};
})( jQuery );