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
	
	function addAddDialog(ele, callback) {
		var li = $('<li />').addClass('name-input');
		var type = $(parent).attr('class');
		var tick = $('<img />');
		
		$(li).appendTo(ele);
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
		/*var name = $(input).attr('value');
		$('#editor').editor('newitem', type, name, function(id) {
			$('#editor').editor('open', type, name);
			$('<li><a href="javascript:void(0)">'+name+'</a></li>').click(function() {
				$('#editor').editor('open', type, name);
			}).appendTo(ul);
		});*/
		$(tick).attr('src', url+'res/img/tick.png').click(function() {li.remove();callback.apply(this, new Array($(input).attr('value')))}).appendTo($(li));
	}
	
	function del(type, name) {
		$.get(url+'editor/delete?type='+type+'&name='+name);
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
	
	//Add methods
	//Controllers
	function addController(controllerName) {
		$('<li><div class="dropdownbtn"></div><a href="javascript:void(0)">'+controllerName+'</a></li>').appendTo($('#editor-browser li.controller ul')).children('a').click(function() {
			$('#editor').editor('open', "controller", controllerName);
		}).parent('li').children('div.dropdownbtn').click(function(event) {
			event.stopPropagation();
			dropdown(this, {
			Remove: function(){
				if(confirm('Are you sure you want to delete the controller "'+controllerName+'" permanently?')) {
					del("controller", controllerName);
					$(this).parent('li').remove();
					$('#editor').editor('close', 'controller', controllerName);
				}
			}
			});
		});
	}
	
	$('#editor-browser li.controller > img.add').click(function() {
		addAddDialog($('#editor-browser li.controller ul'), function(name) {
			$('#editor').editor('newitem', 'controller', name, function(id) {
				$('#editor').editor('open', 'controller', name);
				addController(name);
			});
		});
	});
	
	
	$.get(url+'editor/getall?type=controller', function(data) {
		$(data).find('row').each(function() {
			var controllerName = $(this).find('controller_name').text();
			addController(controllerName);
		});
	});
	
	//Views
	function addView(viewName) {
		var name = viewName;
		$('<li><div class="dropdownbtn"></div><a href="javascript:void(0)">'+name+'</a></li>').appendTo($('#editor-browser li.view ul')).children('a').click(function() {
			$('#editor').editor('open', "view", name);
		}).parent('li').children('div.dropdownbtn').click(function(event) {
			event.stopPropagation();
			dropdown(this, {
			Remove: function(){
				if(confirm('Are you sure you want to delete the view "'+name+'" permanently?')) {
					del("view", name);
					$(this).parent('li').remove();
					$('#editor').editor('close', 'view', name);
				}
			},
			Rename: function() {
				var rename = prompt('Rename "'+name+'" to:');
				$.get(url+'editor/rename?type=view&current_name='+name+'&rename_to='+rename);
				$(this).parent('li').children('a').html(rename);
				name = rename;
			}
			});
		});
	}
	
	
	$('#editor-browser li.view > img.add').click(function() {
		addAddDialog($('#editor-browser li.view ul'), function(name) {
			$('#editor').editor('newitem', 'view', name, function(id) {
				$('#editor').editor('open', 'view', name);
				addView(name);
			});
		});
	});
	
	$.get(url+'editor/getall?type=view', function(data) {
		$(data).find('row').each(function() {
			var viewName = $(this).find('view_name').text();
			addView(viewName);
		});
	});
	
	//Databases
	/*function addDatabase(name)
	
	function*/
	
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