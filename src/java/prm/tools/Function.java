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
    public static void main(String[] args) {

        new Function().functioname();
    }

    public void functioname() {

        try {
            AppParams options = new AppParams();
            CurrentAccountRequest cacc = new CurrentAccountRequest();
            String stringtohash = "101588" + "23-04-2020" ;
            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);
            System.out.println(hash);

//            String str = "abc,def,ghi,jkl";
//            String[] twoStringArray = str.split(",", 2);
//            System.out.println(twoStringArray);
            
//           String cool =  "HEADER=Account Details HEADER=Account Details HEADER= Account not found,@ID::Account No/CUSTOMER::Customer/ACCOUNT.TITLE.1::Name/CATEGORY::Product/CURRENCY::Ccy/ACCOUNT.OFFICER::Account Officer/ONLINE.BALANCE::Online Balance/ACCT.OPEN::Acct Opening Date/POSTING.RESTRICT::Post Restriction";
//            int firstPos = cool.indexOf(",");
//            cool = cool.substring(firstPos, cool.length());
//            System.out.println("cool>>>>>"+cool);
            
            
            
        } catch (Exception ex) {
            Logger.getLogger(Function.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
