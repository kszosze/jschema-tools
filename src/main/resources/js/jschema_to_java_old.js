/*
  This javascript file provides functionality for generating java source code for working with
  JSON documents that satisfy a given jSchema
*/


//function to generate everything

function generateAll(classname, jschema){
    var String = "";
    var parsed_schema = JSON.parse(jschema);
    if(isArray(parsed_schema)){
      for(var i = 0; i < parsed_schema.length; i++){
        String += generateClass(classname);
        String += generateFields(jschema[i]);
        String += generateEnums(jschema[i]);
        String += generateObject(jschema[i], classname);
        String += generateGET(jschema[i]);
        String += generateSET(jschema[i]);
        String += "}\n";
      }
    }
    else{
        String += generateClass(classname);
        String += generateFields(jschema);
        String += generateEnums(jschema);
        String += generateObject(jschema, classname);
        String += generateGET(jschema);
        String += generateSET(jschema);
        String += "}\n";
    }
    return String;
}


//for errors, throw runtime exception
//Generates the Class Header line. Does not currently close the object with a "}".
function generateClass(classname){
  var className = "public class " + classname + "{\n";
  return className;
}


function generateEnums(jschema){
  var enums_present = check_for_enums(jschema);
  var enum_check = false;
  if(!enums_present){
    return "";
  }
  else{
     var str = JSON.parse(jschema);
     var String = "";
     var members = [];
     for(var i in str){
       enum_check = false;
       var str_1 = str.toString().charAt(0);
       if(str_1 == '['){                                //check for if it's bracket
         for(var j in str[i]){
           var strj_1 = str[i].toString().charAt(0);
             if(strj_1 == '@'){                          //if there's an @ sign, it's not an enum
               enum_check = true;
             }
          }
       }
       if(!enum_check){
         members += str[i].toString();
         String += "public enum "  + i + "{";
         String += "\n" + "  ";
         for(var k = 0; k < members.length; k++){
           String += members[k];
           if(members[k] == ','){
             String += "\n  ";
           }
         }
         String += "\n}\n";
         String += "private " + i + " _" + i + ";\n";
       }
     }
     return String;
  }
}
//Generates Java Object Based on jSchema input. Object name will be className.
function generateObject(jschema, className){
  var parsed_schema = JSON.parse(jschema);
  var String = "";

  //creates constructor. the for loop loops through each key/value pair. Each parsed_schema[i]
  //is the value corresponding to its key i.

  String += "public " + className + "(";
  for (var i in parsed_schema){
    var checkwildcard = jschema_parser(parsed_schema[i]);
    if(checkwildcard === "*"){
        String += i + " ";
        String += i.toUpperCase() + ", ";
    }else{
        String += jschema_parser(parsed_schema[i]);
        String += i + ", ";
    }
  }

  //populates constructor
  String = String.substring(0, String.length - 2) + "){\n";
  for(var i in parsed_schema){
    var checkwildcardagain = jschema_parser(parsed_schema[i]);
    if(checkwildcardagain === "*"){
      String += "_" + i + " = " + i.toUpperCase() + ";\n";
    }
    else{
      String += "_" + i + " = " + i + ";\n";
    }
  }
  String += "}\n";
  return String;
}

function generateFields(jschema){
  var parsed_schema = JSON.parse(jschema);
  var String = "";
  var checkwildcard;

  //loops through each key/value pair. Each parsed_schema[i]
  //is the value corresponding to its key i.
  for(var i in parsed_schema){
    checkwildcard = jschema_parser(parsed_schema[i]);
    if(checkwildcard !== "*"){
      String += "private " + checkwildcard;
      if(i != 0){
        String += "_" + i + ";\n";
      }
    }
  }
  return String;
}
//Generate Get methods for the created Java Object
function generateGET(jschema){
    var parsed_schema = JSON.parse(jschema);
    var String = "";
    var checkwildcard;

    for(var i in parsed_schema){
        checkwildcard = jschema_parser(parsed_schema[i]);
        if(checkwildcard === "*"){
            String += "public " + i + " get" + i.toUpperCase() + "()"; //first half of output
        }else{
            String += "public " + checkwildcard + "get" + i + "()";
        }
        String += "{return _" + i + ";}\n";      //second half of output
    }
    return String;
}

//Generate Set methods for the created Java Object
function generateSET(jschema){
    var parsed_schema = JSON.parse(jschema);
    var String = "";
    var checkwildcard;

    for(var i in parsed_schema){
        checkwildcard = jschema_parser(parsed_schema[i]);
        String += "public void set" + i + "(";
        if( checkwildcard === "*"){
            String += i + " " + i.toUpperCase() + "){_" + i + " = " + i.toUpperCase() + ";}\n";
        }else {
            String += checkwildcard + i + "){_" + i + " = " + i + ";}\n";
        }

    }
  return String;
}

function generateError(jschema){
  return jschema;
}


//Main jschema parser method. Takes in a parsed jschema tree, and checks the
//first character.
function jschema_parser(str){
  str_1 = str.toString().charAt(0);
  //if enum, return empty string
  switch(str_1){
    case '@' : return parse_core_type(str);
               break;
    case '[' : return parse_array_type(str);
               break;
    default : return "*";
  }
}

//Checks for core types, returns appropriate value.
function parse_core_type(str){

  //Case 1: if str is an array, return appropriate Java variable
  if(str instanceof Array){
    switch(str.toString()){
      case "@string" : return "String[] ";
                       break;
      case "@boolean": return "boolean[] ";
                       break;
      case "@date"   : return "Date[] ";
                       break;
      case "@uri"    : return "uri[] ";
                       break;
      case "@int"    : return "int[] ";
                       break;
      case "@number" : return "double[] ";
                       break;
      default        : return "ARRAY ** ";
    }
  }

  //Generic case: if type is just a variable, return the variable name
  switch(str){
    case "@string" : return "String ";
                     break;
    case "@boolean": return "boolean ";
                     break;
    case "@date"   : return "Date ";
                     break;
    case "@uri"    : return "uri ";
                     break;
    case "@int"    : return "int ";
                     break;
    case "@number" : return "double ";
                     break;
    default        : return "CORE ** ";
  }
}

function parse_struct_type(str){
  return str + "hello";
}

//Parses a jschema array. For loop behaves like those above.
function parse_array_type(str){
  var String = "";
  for(var i in str){
    if(isObject(str[i])){
      return parse_struct_type(str);
    }
    String += jschema_parser(str[i]);
    String += "_" + i + ";\n";
  }
  return String;
}

function check_for_enums(jschema){
   var str = JSON.parse(jschema);
   for(var i in str){
     var str_1 = str.toString().charAt(0);
     if(str_1 == '['){
        for(var j in str[i]){
          var strj_1 = str[i].toString().charAt(0);
          if(strj_1 != '@'){
            return true;
          }
        }
     }
   }
   return false;
}

function isArray(value) {
    return Object.prototype.toString.call(value) === "[object Array]";
}

function isObject(value) {
    return typeof value === "object";
}

function isString(value){
  return typeof value === "string";
}