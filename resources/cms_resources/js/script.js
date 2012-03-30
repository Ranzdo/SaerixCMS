CodeMirror.defineMode("view", function(config, parserConfig) {
  var groovyOverlay = {
    token: function(stream, state) {
      var ch;
      if (stream.match("{")) {
        while ((ch = stream.next()) != null)
          if (ch == "}") break;
        return "groovy";
      }
      while (stream.next() != null && !stream.match("{{", false)) {}
      return null;
    }
  };
  return CodeMirror.overlayParser(CodeMirror.getMode(config, parserConfig.backdrop || "text/html"), mustacheOverlay);
});
