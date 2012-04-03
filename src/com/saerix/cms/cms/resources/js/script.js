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

var editor = null;
var editor_items = new Object();

function switch_item(type, id, mode) {
	if(editor == null) {
		$('#msgdiv').hide();
		$('#codediv').show();
		
		editor = CodeMirror.fromTextArea(document.getElementById("code"),{
		        lineNumbers: true,
		        matchBrackets: true,
		});
	}
	
	key = type+id;
	
	if('key' in editor_items) {
		 editor.setOption("mode", mode);
		 editor.setValue(editor_items[key]);
	}
	else {
		$.get(url+'editor/item?type='+type+'&id='+id, function(data) {
			 editor.setOption("mode", mode);
			 editor.setValue(data);
			 editor_items[type+id] = data;
		}).error(function() { alert("Could not load the file."); });
	}
}