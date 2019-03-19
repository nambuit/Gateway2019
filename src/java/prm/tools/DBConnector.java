package prm.tools;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Tope
 */
public class DBConnector {
    private final String JDBC_DRIVER;
    private final String DB_URL;
    private final String USER;
    private final String PASS;
    
    
    
    public DBConnector(String Server, String username,String password, String databaseName){
     this.JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
     this.DB_URL = "jdbc:sqlserver://"+Server+":1433;databaseName="+databaseName+";integratedSecurity=false;";
     this.USER =username;
     this.PASS =password;
    }
public ResultSet getData(String sql, Connection conn)  throws Exception{

      
         
   
        
        try
        {
           
            Class.forName(JDBC_DRIVER);

           conn = DriverManager.getConnection(DB_URL,USER,PASS);
            PreparedStatement prestmt = conn.prepareStatement(sql);
            ResultSet ds = prestmt.executeQuery();
           
      
            return ds;      
         
     }catch (Exception e){
        throw(e);
             }

}
 


public ResultSet getData(PreparedStatement prestmt, Connection conn)  throws Exception{

      
         
   
        
        try
        {
           
            Class.forName(JDBC_DRIVER);

           conn = DriverManager.getConnection(DB_URL,USER,PASS);
            ResultSet ds = prestmt.executeQuery();
           
      
            return ds;      
         
     }catch (Exception e){
        throw(e);
             }

}



public void Execute (String sql)  throws Exception{

         Connection conn = null;
       PreparedStatement prestmt =null;   
       
        
        try
        {
            Class.forName(JDBC_DRIVER);

           conn = DriverManager.getConnection(DB_URL,USER,PASS);
            prestmt = conn.prepareStatement(sql);
            prestmt.executeUpdate();      
         
     }catch (Exception e){
        throw(e);
             }
        finally{
            try{
                      prestmt.close();
                     conn.close(); 
            }
            catch(Exception s){
                
            }
        }
}


public void insertData(List<String> fieldNames, Object [] data, String tableName) throws Exception{

 Connection conn = null;
       PreparedStatement prestmt =null;
     try
        {
            Class.forName(JDBC_DRIVER);

           conn = DriverManager.getConnection(DB_URL,USER,PASS);
         
         
           String [] symbols = new String [fieldNames.size()];
           
           for(int i=0;i<symbols.length;i++){
               symbols[i] = "?";
           }
           
           
           
      String insertSQL = "INSERT INTO "+tableName+" ("+String.join(",",fieldNames)+") VALUES ("+String.join(",", symbols)+")";
      PreparedStatement preparedStatement = conn.prepareStatement(insertSQL);
      
       for(int i=0;i<data.length;i++){
           
           Object object = data[i];
           if(object.getClass() == String.class){
           
             preparedStatement.setString(i+1,object.toString());
           }
           
              
                 if(object.getClass() == Integer.class){
           
             preparedStatement.setInt(i+1,Integer.parseInt(object.toString()));
           }
                 
                           if(object.getClass() == Double.class){
           
             preparedStatement.setDouble(i+1,Double.parseDouble(object.toString()));
           }
           
                              if(object.getClass() == Date.class){
                       Date date = (Date) object;
             preparedStatement.setTimestamp(i+1, new java.sql.Timestamp(date.getTime()));
           }
                           
           }
       
       preparedStatement.executeUpdate();
         
     }catch (Exception e){
          throw(e);
             }
        finally{
            try{
                      prestmt.close();
                     conn.close(); 
            }
            catch(Exception s){
                
            }
        }

}


}
    


