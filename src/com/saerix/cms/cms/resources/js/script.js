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
	var add = function() {
		var parent = $(this).parent();
		var li = $('<li />').addClass('name-input');
		var type = $(parent).attr('class');
		var ul = $(this).parent().children('ul');
		
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
				$('#editor').editor('open', id, type, name);
				$(li).remove();
				$('<li><a href="javascript:void(0)">'+name+'</a></li>').click(function() {
					$('#editor').editor('open', id, type, name);
				}).appendTo(ul);
			});
		}).appendTo($(li));
	}
	
	$('#editor').editor({codeid:'code'});
	
	$.ctrl('S', function() {
		$('#editor').editor('save', 'selected');
	});
	
	var clicked = false;
	
	var tick = $('<img />');

	$('#editor-browser .add').click(function() {
		add.apply(this, new Array());
	});
	
	
	$.get(url+'editor/getall?type=controller', function(data) {
		$(data).find('row').each(function() {
			var g = this;
			$('<li><a href="javascript:void(0)">'+$(this).find('controller_name').text()+'</a></li>').click(function() {
				$('#editor').editor('open', $(g).find('controller_id').text(), "controller", $(g).find('controller_name').text());
			}).appendTo($('#editor-browser li.controller ul'));
		});
	});
	
	$.get(url+'editor/getall?type=view', function(data) {
		$(data).find('row').each(function() {
			var g = this;
			$('<li><a href="javascript:void(0)">'+$(this).find('view_name').text()+'</a></li>').click(function() {
				$('#editor').editor('open', $(g).find('view_id').text(), "view", $(g).find('view_name').text());
			}).appendTo($('#editor-browser li.view ul'));
		});
	});
	
	$.get(url+'editor/getall?type=database', function(data) {
		var xml = $(data);
		$(xml).each(function() {
			var li = $('<li class="model">'+$('database_name', this).text()+'</li>').appendTo('#editor-browser li.database ul').append(' ');
			$('<img src="'+url+'res/img/add.png" class="add" />').click(function() {
				add.apply(this, new Array());
			}).appendTo(li);
			var ul = $('<ul />');
			$(li).append(ul);
			
			
			$('database_models > result', this).each(function() {
				var g = this;
				$('<li><a href="javascript:void(0)">'+$('model_tablename', this).text()+'</a></li>').click(function() {
					$('#editor').editor('open', $('model_id', g).text(), "model", $('model_tablename', g).text());
				}).appendTo(ul);
			});
		});
	});
	
});