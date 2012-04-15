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
	$('#editor').editor({codeid:'code'});
	
	$.ctrl('S', function() {
		$('#editor').editor('save', 'selected');
	});
	
	var clicked = false;
	
	var tick = $('<img />');
	
	$('#editor-browser .add').click(function() {
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
	});
});