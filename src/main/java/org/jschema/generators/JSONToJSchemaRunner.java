package org.jschema.generators;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;
import java.util.ArrayList;

public class JSONToJSchemaRunner
{
  public static void main( String[] args ) throws Exception {
    testEquals(jschemaString("{\"string\":\"String\",\"int\":20,\"boolean\":true}"), "{\"name\":\"@string\",\"age\":\"@int\",\"boolean\":\"@boolean\"}");
  }

  private static void testEquals(String first, String second) {
    if (first.equals(second)) { return; }
    System.out.println("Assertion failed: " + first + " does not equal " + second);
  }

  private static String jschemaString(String json) throws Exception {
    return (String) executeJS("generateJSchemaStringFromJSON", json);
  }

  private static Object executeJS(String functionName, Object argument) throws Exception {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    engine.eval(new FileReader("src/main/resources/js/json_to_jschema.js"));
    Invocable invocable = (Invocable) engine;
    return invocable.invokeFunction(functionName, argument);
  }
}
