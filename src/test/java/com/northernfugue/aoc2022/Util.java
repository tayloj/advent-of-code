package com.northernfugue.aoc2022;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Util {

  private Util() {
    // utility
  }

  private static final Map<String, Path> inputs = new HashMap<>();

  public static Path inputForClass(Class<?> clazz, String s) {
    String name = clazz.getName().replace('.', '/').concat("/").concat(s);
    URL url = Objects.requireNonNull(Util.class.getClassLoader().getResource(name),
        () -> "No resource for name: " + name);
    URI uri;
    try {
      uri = url.toURI();
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
    return Paths.get(uri);
  }

  public static Path input(String s) throws IOException {
    if (!inputs.containsKey(s)) {
      Path p;
      try {
        p = Paths.get(Util.class.getClassLoader().getResource(s).toURI());
      } catch (URISyntaxException e) {
        throw new IOException("Error getting Path for resource: " + s, e);
      }
      inputs.put(s, p);
      return p;
    } else {
      return inputs.get(s);
    }
  }


}
