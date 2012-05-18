CodeMirror.defineMode("view", function(config, parserConfig) {
  var viewOverlay = {
    token: function(stream, state) {
      var ch;
      var step = 0;
      if (stream.match("{")) {
        while ((ch = stream.next()) != null)
          if (ch == "}" && step == 0) break;
          else if(ch == "{") step++;
          else if(ch == "}") step--;
        return "view";
      }
      while (stream.next() != null && !stream.match("{", false)) {}
      return null;
    }
  };
  return CodeMirror.overlayParser(CodeMirror.getMode(config, parserConfig.backdrop || "text/html"), viewOverlay);
});

$(function() {
	var current_dropdown = "";
	
	var add = function() {
		var parent = $(this).parent();
		var li = $('<li />').addClass('name-input');
		var type = $(parent).attr('class');
		var ul = $(this).parent().children('ul');
		var tick = $('<img />');
		
		$(li).appendTo(ul);
		var removeitnow = false;
		var input = $('<input type="text" />').blur(function() {
			var g = this;
			var t = setTimeout(function() {
				$(li).remove();
			}, 100);
			$(tick).click(function() {
				clearTimeout(t);
				$(g).focus();
			});
		}).appendTo($(li)).focus();
		
		$(tick).attr('src', url+'res/img/tick.png').click(function() {
			var name = $(input).attr('value');
			$('#editor').editor('newitem', type, name, function(id) {
				$('#editor').editor('open', type, name);
				$(li).remove();
				$('<li><a href="javascript:void(0)">'+name+'</a></li>').click(function() {
					$('#editor').editor('open', type, name);
				}).appendTo(ul);
			});
		}).appendTo($(li));
	}
	
	function del(type, name) {
		$.get(url+'editor/delete?type=controller&name='+name);
	}
	
	function dropdown(rel, options) {
		closedropdown();
		var ul = $('<ul class="dropdown" />');
		ul.hide();
		
		$.each(options, function(key, value) {
			$('<li>'+key+'</li>').click(function() {value.apply(rel, new Array());}).appendTo(ul);
		});

		ul.css({
			position: 'absolute',
			top: $(rel).offset().top + 13,
			left: $(rel).offset().left
		});
		$('#editor-browser').append(ul);
		ul.slideDown(50);
		current_dropdown = ul;
	}
	
	function closedropdown() {
		if(current_dropdown != "") {
			$(current_dropdown).remove();
		}	
	}
	
	$(document).click(function(event) {
		event.stopPropagation();
		closedropdown();
	});
	
	$('#editor').editor({codeid:'code'});
	
	$.ctrl('S', function() {
		$('#editor').editor('save', 'selected');
	});
	
	var clicked = false;

	$('#editor-browser .add').click(function() {
		add.apply(this, new Array());
	});
	
	
	$.get(url+'editor/getall?type=controller', function(data) {
		$(data).find('row').each(function() {
			var g = this;
			var controllerName = $(this).find('controller_name').text();
			$('<li><div class="dropdownbtn"></div><a href="javascript:void(0)">'+controllerName+'</a></li>').appendTo($('#editor-browser li.controller ul')).children('a').click(function() {
				$('#editor').editor('open', "controller", controllerName);
			}).parent('li').children('div.dropdownbtn').click(function(event) {
				event.stopPropagation();
				dropdown(this, {
				Remove: function(){
					if(confirm('Are you sure you want to delete the controller "'+controllerName+'" permanently?')) {
						del("controller", controllerName);
						$(this).parent('li').remove();
					}
				},
				Noob: function() {
					
				}
				
				});
			});
		});
	});
	
	$.get(url+'editor/getall?type=view', function(data) {
		$(data).find('row').each(function() {
			var g = this;
			$('<li><a href="javascript:void(0)">'+$(this).find('view_name').text()+'</a></li>').appendTo($('#editor-browser li.view ul')).children('a').click(function() {
				$('#editor').editor('open', "view", $(g).find('view_name').text());
			});
		});
	});
	
	$.get(url+'editor/getall?type=database', function(data) {
		var xml = $(data);
		$('row', xml).each(function() {
			var databaseName = $('database_name', this).text();
			var li = $('<li class="model">'+databaseName+'</li>').appendTo('#editor-browser li.database ul').append(' ');
			$('<img src="'+url+'res/img/add.png" class="add" />').click(function() {
				add.apply(this, new Array());
			}).appendTo(li);
			var ul = $('<ul />');
			$(li).append(ul);
			
			$('database_models > result', this).each(function() {
				var g = this;
				$('<li><a href="javascript:void(0)">'+$('model_tablename', this).text()+'</a></li>').click(function() {
					$('#editor').editor('open', "model", $('model_tablename', g).text(), databaseName);
				}).appendTo(ul);
			});
		});
	});
	
	
	
	
});