/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prm.tools;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;
import primera.service.LoanAccountRequest;

/**
 *
 * @author dogor-Igbosuah
 */
public class Function {

    public String APIKey = "MWx1vW1mj26hycrX";

//  public Function(){
//  }
    public static void main(String[] args) {

        new Function().functioname();
    }

    public void functioname() {

        try {
            AppParams options = new AppParams();
            LoanAccountRequest cacc = new LoanAccountRequest();
            String stringtohash = "0011067287" + "$2a$14$xi6c.aFL.XR2zhc0r1Ta2O";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd::hh:mm:ss");

            Date date = new Date();

            String dates = sdf.format(date);   //(zzdf.format(today))
            //{"accountNumber": "0011067634", "startDate": "31 DEC 2015", "endDate": "01 MAR 2016"}
            String hashedstring = "045a7512e256e728be2aa1be16e2a317287db9219275629db5e0dfcc57ce6277c36b9ddbe7b34d1594f61179ba7a392d243bad7575b625588ac8b3a7bd03dcdc";
            String hash = options.get_SHA_512_Hash("42220270536687180055", "42220270536687180055");
            String Bcrypt = options.generateBCrypthash(stringtohash);
//            if (hash.equals(hashedstring)) {
//                System.out.println("HASH IS THE SAME");
//            } else {
//                System.out.println("HASH IS NOT THE SAME");
//            }
            System.out.println(dates);

            //System.out.println(UUID.randomUUID().toString());
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
