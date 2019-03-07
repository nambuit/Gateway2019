///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package primera.service;
//
//import com.google.gson.Gson;
//import java.util.UUID;
////import primera.service.objects.AccountDetailsRequest;
//
///**
// *
// * @author dogor-Igbosuah
// */
//public class PrimeraInterface {
//   
//    public String APIKey = "4466FA2C-1886-4366-B014-AD140712BE38";
//    private String s;
//    public String AccountDetails(String input)
//  {
//    try
//    {
//      Gson gson = new Gson();
//      
//     //AccountDetailsRequest request = (AccountDetailsRequest)gson.fromJson(input, AccountDetailsRequest.class);
//     
//     request.setRequestID(UUID.randomUUID().toString());
//     
//     RestClient client = new RestClient("http://127.0.0.1:8080/PrimeraGateway/webresources/PrimeraInterface");//172.16.10.5:
//      
//     String stringtohash = request.getRequestID()+ request.getAccountNumber();
//    
//     String hash = client.get_SHA_512_Hash(stringtohash, APIKey);
//      
//     request.setHash(hash);
//     
//     String payload = gson.toJson(request);
//     String responsebody = client.ProcessPrimeraRequest(payload, "GetAccountByAccountNo");
//      
//      
//      
//      //RegisterMerchantResponse response = (RegisterMerchantResponse)gson.fromJson(responsebody, RegisterMerchantResponse.class);
//      
//     // return response.getHash()+ '#' + response.getMerchantCode()+ '#' + response.getResponseCode() + '#' + response.getSessionID();
//    }
//    catch (Exception e){
//        System.out.println(e.getMessage());
//    }
//    
//    return s;
//   }
//    
//    }
//
//    
