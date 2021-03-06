package org.jschema.generators;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class RunGenerators
{

  public static final String JSCHEMA_SUFFIX = ".jschema";
  public static final String JSON_SUFFIX = ".json";
  public static final String JSON_DIR = "src/test/java/samples";
  public static final String SCHEMAS_DIR = "src/test/java/schemas";
  public static final String GENERATED_DIR = "src/test/java/org/jschema/generated";
  public static final String JAVA_GENERATED_DIR = GENERATED_DIR + "/java";
  public static final String JAVASCRIPT_GENERATED_DIR = GENERATED_DIR + "/javascript";

  public static void main( String[] args )
  {
    List<File> jsonDocs=findJSON(JSON_DIR);
    generateSchema((jsonDocs));
    List<File> schemas = findSchemas( SCHEMAS_DIR );
    generateJava(schemas);
  }

  private static void generateJava( List<File> schemas )
  {
    for( File schema : schemas )
    {
      try
      {
        String name = schema.getName();
        // why does java suck so badly?
        String fixedName = Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1, name.length() - JSCHEMA_SUFFIX.length() );
        String jSchema = new String( Files.readAllBytes( schema.toPath() ) );
        Object javaCode = JSchemaToJavaRunner.generateAll( fixedName, jSchema );
        PrintWriter writer = new PrintWriter(JAVA_GENERATED_DIR + "/" + fixedName + ".java", "UTF-8");
        writer.print(javaCode);
        writer.close();
      }
      catch( Exception e )
      {
        throw new RuntimeException( e );
      }
    }
  }

  private static void generateJavascript( List<File> schemas )
  {
    for( File schema : schemas )
    {
      try
      {
        String name = schema.getName();
        // why does java suck so badly?
        String fixedName = Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1, name.length() - JSCHEMA_SUFFIX.length() );
        String jSchema = new String( Files.readAllBytes( schema.toPath() ) );
        Object javaCode = JSchemaToJavascriptRunner.generateAll( fixedName, jSchema );
        PrintWriter writer = new PrintWriter(JAVASCRIPT_GENERATED_DIR + "/" + fixedName + ".js", "UTF-8");
        writer.print(javaCode);
        writer.close();
      }
      catch( Exception e )
      {
        throw new RuntimeException( e );
      }
    }
  }

  private static void generateSchema(List<File> jsonDocs){
    for(File doc : jsonDocs){
      try{
        String name = doc.getName();
        if(!name.equals("cars.json")) {
          // why does java suck so badly?
          String fixedName = name.substring(0, name.length() - JSON_SUFFIX.length());
          String json = new String(Files.readAllBytes(doc.toPath()));
          Object schemaCode = JSONToJSchemaRunner.generateAll(json, true);
          PrintWriter writer = new PrintWriter(SCHEMAS_DIR + "/" + fixedName + ".jschema", "UTF-8");
          writer.print(schemaCode);
          writer.close();
        }
      }catch( Exception e )
      {
        throw new RuntimeException( e );
      }
    }

  }
  private static List<File> findSchemas(String relativePath) {
    File root = new File( relativePath );
    List<File> schemas = findSchemas( root, new ArrayList() );
    System.out.println("Root Dir: " + root.getAbsolutePath()  +"\n");
    System.out.println("Found : " + schemas );
    return schemas;
  }

  private static List<File> findSchemas( File root, List<File> files )
  {
    for( File file : root.listFiles() )
    {
      if( file.getName().endsWith( ".jschema" ) )
      {
        files.add(file);        
      }
      else if(file.isDirectory())
      {
        findSchemas( file, files );
      }
    }
    return files;
  }
  private static List<File> findJSON(String relativePath) {
    File root = new File( relativePath );
    List<File> jsonDocs = findJSON( root, new ArrayList() );
    System.out.println("Root Dir: " + root.getAbsolutePath()  +"\n");
    System.out.println("Found : " + jsonDocs );
    return jsonDocs;
  }

  private static List<File> findJSON( File root, List<File> files )
  {
    for( File file : root.listFiles() )
    {
      if( file.getName().endsWith( ".json" ) )
      {
        files.add(file);
      }
      else if(file.isDirectory())
      {
        findJSON( file, files );
      }
    }
    return files;
  }


}
