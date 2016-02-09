package org.jschema.parser;

import org.jschema.parser.Token.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import static org.jschema.parser.Token.TokenType.*;

public class Parser
{

  private final Tokenizer _tokenizer;
  private Token _currentToken;

  public Parser( String src )
  {
    _tokenizer = new Tokenizer( src );
    nextToken();
  }

  //=================================================================================
  //  JSON Grammar
  //=================================================================================

  public Object parse() {
    Object value = parseValue();
    if( match( EOF ) )
    {
      return value;
    }
    else
    {
      return error();
    }
  }

  public Object parseValue()
  {
    if( match( LCURLY ) )
    {
      nextToken();
      return parseObject();
    }

    // parse arrays
    if( match( LSQUARE ) )
    {
      nextToken();
      return parseArray();
    }
      // parse arrays
      if( match( RSQUARE ) )
      {
          return _currentToken.getTokenValue();
      }



      if(match (COMMA)){
          String tokenValue = _currentToken.getTokenValue();
          nextToken();
          return tokenValue;
      }
      if(match (COLON)){
          String tokenValue = _currentToken.getTokenValue();
          nextToken();
          return tokenValue;
      }

    // parse literals (e.g. true, false, strings, numbers)
    if( match( STRING ) )
    {
      String tokenValue = _currentToken.getTokenValue();
      nextToken();
      return tokenValue;
    }

      if (match (TRUE)){
          nextToken();
          return true;
      }
      if (match (FALSE)){
          nextToken();
          return false;
      }
      if (match (NULL)){
          nextToken();
          System.out.println("here");
          return null;
      }

      if (match(NUMBER)){
          double num=_currentToken.getTokenNumberValue();
          System.out.println(num);
          //check if is an int
          if(!(_currentToken.getTokenValue().contains(".") ||_currentToken.getTokenValue().contains("e")||_currentToken.getTokenValue().contains("E"))){
              nextToken();
              return ((int)num);
          }
          nextToken();
          return num;

      }
  if(match(ERROR)){
      return error();
  }

    //TODO implement other literals

    return error();
  }


  public Object parseObject()
  {
      //TODO implement, return a map of name/value pairs, and Error if an error is detected
      //                pass the map into parseMember to populate
      HashMap<String, Object> map = new HashMap<>();
      //for empty object
      if( match( RCURLY ) )
      {
          nextToken();
          return map;
      }
        int rBrace=0;
      //make sure it matches right curly brace
      //keep going if there is a comma
      while(!match(RCURLY) && !match(EOF)) {
          map = (HashMap<String, Object>) parseMember(map);
          if(!match(RCURLY) || match(COMMA)) {
              nextToken();
          }
          if(match(RCURLY)){
              rBrace=1;
          }
      }
      nextToken();
      if(rBrace!=1){
          return error();
      }
      return map;
  }

  private Object parseMember( HashMap map )
  {

      //get the key
      String key=_currentToken.getTokenValue();
      System.out.println("key is"+key);
      Object val;
      //now check that colon is separator
      nextToken();
      if(!match(COLON)){
          error();
          nextToken();
          return map;
      }else{
          //now get value
          nextToken();
          val=parseValue();
          System.out.println("val added is"+val);
          map.put(key,val);

      }

      if (match(COMMA)){
          nextToken();
          return parseMember(map);
      } else if (match (RCURLY)){
          return map;
      }
      else
      {
          return error();
      }
      //TODO implement, parse the key and value, return the map if it is good, Error otherwise.
  }


  public Object parseArray()
  {
      ArrayList list=new ArrayList();
      //for empty arrays
      if(match (RSQUARE)) {
          nextToken();
          return list;
      }
      int rBrace=0;
      while (!match(RSQUARE) && !match(EOF)) {
          try {
              //first get item
              Object val=parseValue();
                System.out.println("val is"+val);
              //if not comma, add to list since
              if(val==null || !val.equals(",")){
                  System.out.println("adding");
                  list.add(val);
              }else{
                  //return error if not valid, already on next token
                  //so don't need to switch index
                  System.out.println("error");
                  error();
                  return new ArrayList();
              }
              //next token must be comma or right square
              //if not, print error, go to next token
              if(!(match(COMMA)||match(RSQUARE))){
                  error();
                  nextToken();
                  return new ArrayList();
              }
              //if a comma, move to next token
              //otherwise do not to stop while loop properly on RSQUARE
              if(match(COMMA)) {
                  nextToken();
              }
              if(match(RSQUARE)){
                  System.out.println("here");
                  rBrace=1;
              }
          }catch (Exception e){
              error();
              System.out.println("error");
              nextToken();
              return list;
          }

          //TODO implement, parse the elements inline, return Error if any element is error
      }
      nextToken();
      if(rBrace!=1){
          return error();
      }
      return list;
  }

  //=================================================================================
  //  Tokenizer helpers
  //=================================================================================

  private void nextToken()
  {
    _currentToken = _tokenizer.next();
  }

  private boolean match( TokenType type )
  {
    return _currentToken.getTokenType() == type;
  }

  private Error error()
  {
    return new Error("Unexpected Token: " + _currentToken.toString());
  }
}
