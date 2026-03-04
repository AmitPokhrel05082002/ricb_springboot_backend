package bt.ricb.ricb_api.util;

 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import org.json.JSONArray;
 import org.json.JSONObject;
 import org.springframework.stereotype.Service;
 
 
 @Service
 public class ToJSON
 {
   public JSONArray toJSONArray(ResultSet rs) {
     JSONArray json = new JSONArray();
     
     try {
       ResultSetMetaData rsmd = rs.getMetaData();
       
       while (rs.next()) {
         int numColumns = rsmd.getColumnCount();
         JSONObject obj = new JSONObject();
         
         for (int i = 1; i <= numColumns; i++) {
           String column_name = rsmd.getColumnName(i);
           
           switch (rsmd.getColumnType(i)) {
             case 2003:
               obj.put(column_name, rs.getArray(column_name));
               break;
             case -5:
               obj.put(column_name, rs.getLong(column_name));
               break;
             case 16:
               obj.put(column_name, rs.getBoolean(column_name));
               break;
 
             
             case 2004:
               break;
             
             default:
               obj.put(column_name, rs.getObject(column_name));
               break;
           } 
         
         } 
         json.put(obj);
       } 
     } catch (Exception e) {
       e.printStackTrace();
     } 
     
     return json;
   }
 }
