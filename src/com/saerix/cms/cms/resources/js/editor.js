(function( $ ){
	 var methods = {
		init : function(options) {	
			$(this).data('editor', CodeMirror.fromTextArea(document.getElementById(options.codeid),{
		        lineNumbers: true,
		        matchBrackets: true,
			}));
			
			$(this).hide();
			
			$(this).data('tabs', new Array());
			
			$(this).prepend('<div style="clear:left;"></div>');
			$(this).prepend('<div class="editor-tabs"></div>');
		},
		open : function(id, type, name) {
			var g = this;
			var tabs = $(this).children('.editor-tabs').first();
			$(this).show();
			if($(tabs).children('.'+type+id+'.editor-tab.'+type).length == 0) {
				$.get(url+'editor/get?type='+type+'&id='+id, function(data) {					
					methods.create.apply(g, new Array(type, name, data, id));
					methods.select.apply(g, new Array(id, type));
				}).error(function() { alert('Could not load the file.'); });
			}
			else
				methods.select.apply(this, new Array(id, type));
		},
		select: function(id, type) {
			var local;
			if(typeof type === 'object' || ! type)
				local = $(id);
			else
				local = $(this).children('.editor-tabs').first().children('.'+type+id+'.editor-tab');
			
			var selected = $(this).children('.editor-tabs').first().children('.editor-tab.selected');
			
			if(selected != null) {
				$(selected).removeClass("selected");
			}
			
			$(local).addClass('selected');
			
			var editor = $(this).data('editor');
			var data = $(local).data('tab');
			var mode;
			if(data.type == 'controller' || data.type == 'model')
				mode = 'text/x-groovy';
			else if(data.type == 'view')
				mode = 'view';
			else
				alert('Could not found a mode for the selected document');
			
			editor.setOption("mode", mode);
			editor.setOption("onChange", function() {});
			editor.setValue($(local).data('tab').content);
			editor.setOption("onChange", function(instance) {
				data.content = instance.getValue();
				if(!$(local).hasClass('changed')) {
					$(local).addClass('changed');
					$(local).children('.body').append('*')
				}
			});
		},
		close : function(id, type) {
			var editor = $(this).data('editor');
			var tabc = $(this).children('.editor-tabs');
			var tabs = $(tabc).children('.editor-tab');
			var local = $(tabc).children('.'+type+id+'.editor-tab');
			
			if($(local).hasClass('changed')) {
				if(!confirm('Discard changes?'))
					return;
			}
			
			if($(local).hasClass('selected')) {
				if($(tabs).length == 1) {
					$(this).hide();
				}
				else if($(tabs).first() == $(local)) {
					methods.select.apply(this, new Array($(tabs).get(1)));
				}
				else {
					var index = $(tabs).index(local)-1;
					methods.select.apply(this, new Array($(tabs).get(index)));
				}
			}
			
			$(local).remove();
		}, 
		save: function(id, type) {
			var local;
			if(typeof type === 'object' || ! type) {
				if(id == 'selected')
					local = $(this).children('.editor-tabs').first().children('.editor-tab.selected');
			}
			else
				local = $(this).children('.editor-tabs').first().children('.'+type+id+'.editor-tab');
			
			var data = $(local).data('tab');
			
			$.post(url+'editor/save', data, function(data) {
				if(data != '') {
					var error = window.open( "", "", "status = 1, height = 300, width = 300, resizable = 0" );
					error.document.write(data);
					error.document.close();
				}
				else {
					$(local).children('.body').html($(local).children('.body').html().replace("*", ""));
					$(local).removeClass('changed');
				}
			});
		},
		create: function(type, name, contentPara, id) {
			var tabs = $(this).children('.editor-tabs').first();
			var content = "";
			var g = this;
			if((typeof contentPara === 'object' || ! contentPara) && (typeof id === 'object' || ! id)) {
				if(type in defaults) {
					content = defaults[type];
				}
			}
			else {
				content = contentPara;
			}
			
			$('<div class="editor-tab '+type+' '+type+id+'"><div class="body">'+name+'</div><div class="close"></div></div>')
			.data('tab', {'id' : id, 'type' : type, content : content, 'name' : name})
			.appendTo($(tabs))
			.children('div').click(function() {
				if($(this).hasClass('body'))
					methods.select.apply(g, new Array(id, type));
				else if($(this).hasClass('close'))
					methods.close.apply(g, new Array(id, type));
			});
		},
		newitem : function(type, name, callback) {
			var g = this;
			$.get(url+'editor/newitem?type='+type+'&name='+name, function(data) {
				if(data.indexOf("error:") != -1) {
					alert(data.replace("error:", ""));
					return;
				}
				
				callback.apply(g, new Array(data));
			});
		}
	 };
	
	$.fn.editor = function(method) {
	    if (methods[method]) {
	    	if($(this).data('editor') == null)
	    		$.error( 'Editor is not initiated.' );
	    	else
	    		return methods[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
	    }
		else if (typeof method === 'object' || ! method)
			return methods.init.apply( this, arguments );
		else
			$.error('Method ' +  method + ' does not exist');
	};
	
	$.ctrl = function(key, callback, args) {
	    var isCtrl = false;
	    $(document).keydown(function(e) {
	        if(!args) args=[]; // IE barks when args is null

	        if(e.ctrlKey) isCtrl = true;
	        if(e.keyCode == key.charCodeAt(0) && isCtrl) {
	            callback.apply(this, args);
	            return false;
	        }
	    }).keyup(function(e) {
	        if(e.ctrlKey) isCtrl = false;
	    });
	};
	
	
})( jQuery );