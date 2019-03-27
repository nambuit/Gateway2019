/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prm.tools;

import java.util.logging.Level;
import java.util.logging.Logger;
import primera.service.CurrentAccountRequest;

/**
 *
 * @author dogor-Igbosuah
 */


public class Function {
    
    
    public String APIKey = "4466FA2C-1886-4366-B014-AD140712BE38";
    
    
//  public Function(){
//  }
      
    
   public static void main (String [] args){
       
       
       new Function().functioname();
   }
      
   
   public void functioname(){
       
        try {
            AppParams options = new AppParams();
            CurrentAccountRequest cacc = new CurrentAccountRequest();
            String stringtohash = "FT40494049404" ;
            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);
            System.out.println(hash);
        } catch (Exception ex) {
            Logger.getLogger(Function.class.getName()).log(Level.SEVERE, null, ex);
        }
      
      
   }
}
