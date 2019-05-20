/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.enquiry;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.naming.InitialContext;
import org.apache.log4j.Logger;
import primera.service.T24Link;
import prm.tools.AppParams;
import static prm.tools.AppParams.isNullOrEmpty;
import prm.tools.BCrypt;
import prm.tools.DBConnector;
import prm.tools.ResponseCodes;

/**
 *
 * @author dogor-Igbosuah
 */
@WebService(serviceName = "PrimeraEnquiryInterface")
public class PrimeraEnquiryInterface {

    T24Link t24;
    Logger weblogger = Logger.getLogger(PrimeraEnquiryInterface.class.getName());
    private String Ofsuser;
    private String Ofspass;
    AppParams options;
    ResponseCodes dresp;
    private DBConnector db;
    String apikey = "";
    Connection conn = null;
    StringWriter writer;
    PrintWriter printWriter;

    public PrimeraEnquiryInterface() {

        try {

            javax.naming.Context ctx = (javax.naming.Context) new InitialContext().lookup("java:comp/env");
            String Host = (String) ctx.lookup("HOST");
            int port = Integer.parseInt((String) ctx.lookup("PORT"));
            String OFSsource = (String) ctx.lookup("OFSsource");
            Ofsuser = (String) ctx.lookup("OFSuser");
            Ofspass = (String) ctx.lookup("OFSpass");
            t24 = new T24Link(Host, port, OFSsource);
            options = new AppParams();
            writer = new StringWriter();
            printWriter = new PrintWriter(writer);

            db = new DBConnector(options.getDBserver(), options.getDBuser(), options.getDBpass(), "PrimeraWeb");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @WebMethod(operationName = "FTHistory")
    public List<FtHistResponse> FTHistory(@WebParam(name = "FTHistory") FtHistRequest fthistory) throws Exception {

        String acc = fthistory.getAccountNumber();
        String crdr = fthistory.getCreditORdebit();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        List<FtHistResponse> allfth = new ArrayList<>();
        ArrayList<List<String>> result = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        weblogger.info("FTHistory");

        try {
            String APIKEY = fthistory.getApikey();

            String AUTHID = fthistory.getAuthenticationID();

            String APPLICATIONID = fthistory.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;
                FtHistResponse fth = new FtHistResponse();
                fth.setResponseCode(dresp.getCode());
                fth.setResponseText(dresp.getMessage());
                fth.setTransactionDate(zzdf.format(today));
                allfth.add(fth);
                weblogger.error(allfth);
                return allfth;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    FtHistResponse fth = new FtHistResponse();
                    fth.setResponseCode(dresp.getCode());
                    fth.setResponseText(dresp.getMessage());
                    fth.setTransactionDate(zzdf.format(today));
                    allfth.add(fth);
                    weblogger.error(allfth);
                    return allfth;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    FtHistResponse fth = new FtHistResponse();
                    fth.setResponseCode(dresp.getCode());
                    fth.setResponseText(dresp.getMessage());
                    fth.setTransactionDate(zzdf.format(today));
                    allfth.add(fth);
                    weblogger.error(allfth);
                    return allfth;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;
                FtHistResponse fth = new FtHistResponse();
                fth.setResponseCode(dresp.getCode());
                fth.setResponseText(dresp.getMessage());
                fth.setTransactionDate(zzdf.format(today));
                allfth.add(fth);
                weblogger.error(allfth);
                return allfth;
            }
            String stringtohash = fthistory.getAccountNumber() + fthistory.getCreditORdebit() + APIKEY;

            String requesthash = fthistory.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {
                switch (crdr) {
                    case "Credit":
                        result = t24.getOfsData("FTHIST", Ofsuser, Ofspass, "CREDIT.ACCT.NO:EQ=" + acc.trim());
                        headers = result.get(0);
                        if (headers.size() != result.get(1).size()) {

                            throw new Exception(result.get(1).get(0));
                        }
                        for (int i = 1; i < result.size(); i++) {
                            FtHistResponse fth = new FtHistResponse();
                            dresp = ResponseCodes.SUCCESS;
                            fth.setResponseCode(dresp.getCode());
                            fth.setResponseText(dresp.getMessage());
                            fth.setTransactionDate(zzdf.format(today));
                            fth.setCreditAcctNo(result.get(i).get(headers.indexOf("CR ACCT NO")).replace("\"", ""));
                            fth.setCreditAmount(result.get(i).get(headers.indexOf("CR AMOUNT")).replace("\"", ""));
                            fth.setCreditValueDate(result.get(i).get(headers.indexOf("CR VAL DATE")).replace("\"", ""));
                            fth.setCurrency(result.get(i).get(headers.indexOf("CR CCY")).replace("\"", ""));
                            fth.setDebitAcctNo(result.get(i).get(headers.indexOf("DR ACCT NO")).replace("\"", ""));
                            fth.setDebitAmount(result.get(i).get(headers.indexOf("DR AMOUNT")).replace("\"", ""));
                            fth.setDebitValueDate(result.get(i).get(headers.indexOf("DR VAL DATE")).replace("\"", ""));
                            fth.setTransferReference(result.get(i).get(headers.indexOf("TRANS REF")).replace("\"", ""));
                            fth.setTransactionDetails(result.get(i).get(headers.indexOf("TRANSACTION DETAILS")).replace("\"", ""));
                            fth.setProfitCenterDepartment(result.get(i).get(headers.indexOf("PROFIT.CENTRE.DEPT")).replace("\"", ""));
                            allfth.add(fth);
                        }
                        break;
                    case "Debit":
                        result = t24.getOfsData("FTHIST", Ofsuser, Ofspass, "DEBIT.ACCT.NO:EQ=" + acc.trim());
                        headers = result.get(0);
                        if (headers.size() != result.get(1).size()) {

                            throw new Exception(result.get(1).get(0));
                        }
                        for (int i = 1; i < result.size(); i++) {
                            FtHistResponse fth = new FtHistResponse();
                            dresp = ResponseCodes.SUCCESS;
                            fth.setResponseCode(dresp.getCode());
                            fth.setResponseText(dresp.getMessage());
                            fth.setTransactionDate(zzdf.format(today));
                            fth.setCreditAcctNo(result.get(i).get(headers.indexOf("CR ACCT NO")).replace("\"", ""));
                            fth.setCreditAmount(result.get(i).get(headers.indexOf("CR AMOUNT")).replace("\"", ""));
                            fth.setCreditValueDate(result.get(i).get(headers.indexOf("CR VAL DATE")).replace("\"", ""));
                            fth.setCurrency(result.get(i).get(headers.indexOf("CR CCY")).replace("\"", ""));
                            fth.setDebitAcctNo(result.get(i).get(headers.indexOf("DR ACCT NO")).replace("\"", ""));
                            fth.setDebitAmount(result.get(i).get(headers.indexOf("DR AMOUNT")).replace("\"", ""));
                            fth.setDebitValueDate(result.get(i).get(headers.indexOf("DR VAL DATE")).replace("\"", ""));
                            fth.setTransferReference(result.get(i).get(headers.indexOf("TRANS REF")).replace("\"", ""));
                            fth.setTransactionDetails(result.get(i).get(headers.indexOf("TRANSACTION DETAILS")).replace("\"", ""));
                            fth.setProfitCenterDepartment(result.get(i).get(headers.indexOf("PROFIT.CENTRE.DEPT")).replace("\"", ""));
                            allfth.add(fth);
                        }
                        break;
                }
//
            } else {
                dresp = ResponseCodes.Security_violation;
                FtHistResponse fth = new FtHistResponse();
                fth.setResponseCode(dresp.getCode());
                fth.setResponseText(dresp.getMessage());
                fth.setTransactionDate(zzdf.format(today));
                allfth.add(fth);
                return allfth;
            }

        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            FtHistResponse fth = new FtHistResponse();
            fth.setResponseCode(dresp.getCode());
            fth.setResponseText(d.toString());
            fth.setTransactionDate(zzdf.format(today));
            allfth.add(fth);
            weblogger.fatal(fth, d);

        }
        return allfth;
    }

    @WebMethod(operationName = "FTNarrateReq")
    public List<FtNarrateResponse> FTNarrateReq(@WebParam(name = "FtNarrateRequest") FtNarrateRequest ftnreq) throws Exception {
//      
        String acc = ftnreq.getAccountNumber();
        String crdr = ftnreq.getCreditORdebit();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        ArrayList<List<String>> result = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        List<FtNarrateResponse> allftn = new ArrayList<>();
        weblogger.info("FtNarrateRequest");

        try {
            String APIKEY = ftnreq.getApikey();

            String AUTHID = ftnreq.getAuthenticationID();

            String APPLICATIONID = ftnreq.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;
                FtNarrateResponse ftn = new FtNarrateResponse();
                ftn.setResponseCode(dresp.getCode());
                ftn.setResponseText(dresp.getMessage());
                ftn.setTransactionDate(zzdf.format(today));
                allftn.add(ftn);
                weblogger.error(allftn);
                return allftn;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    FtNarrateResponse ftn = new FtNarrateResponse();
                    ftn.setResponseCode(dresp.getCode());
                    ftn.setResponseText(dresp.getMessage());
                    ftn.setTransactionDate(zzdf.format(today));
                    allftn.add(ftn);
                    weblogger.error(allftn);
                    return allftn;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    FtNarrateResponse ftn = new FtNarrateResponse();
                    ftn.setResponseCode(dresp.getCode());
                    ftn.setResponseText(dresp.getMessage());
                    ftn.setMessage(dresp.getMessage());
                    ftn.setTransactionDate(zzdf.format(today));
                    allftn.add(ftn);
                    weblogger.error(allftn);
                    return allftn;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;
                FtNarrateResponse ftn = new FtNarrateResponse();
                ftn.setResponseCode(dresp.getCode());
                ftn.setResponseText(dresp.getMessage());

                ftn.setTransactionDate(zzdf.format(today));
                allftn.add(ftn);
                weblogger.error(allftn);
                return allftn;
            }
            String stringtohash = ftnreq.getAccountNumber() + ftnreq.getCreditORdebit() + APIKEY;

            String requesthash = ftnreq.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {
                switch (crdr) {
                    case "Credit":
                        result = t24.getOfsData("FT.NARRATE", Ofsuser, Ofspass, "CREDIT.ACCT.NO:EQ=" + acc.trim());
                        headers = result.get(0);

                        if (headers.size() != result.get(1).size()) {

                            throw new Exception(result.get(1).get(0));

                        }
                        for (int i = 1; i < result.size(); i++) {
                            FtNarrateResponse ftn = new FtNarrateResponse();
                            dresp = ResponseCodes.SUCCESS;
                            ftn.setResponseCode(dresp.getCode());
                            ftn.setResponseText(dresp.getMessage());
                            ftn.setTransactionDate(zzdf.format(today));
                            ftn.setCreditAccountName(result.get(i).get(headers.indexOf("CR ACCT NAME")).replace("\"", ""));
                            ftn.setCreditAcctNo(result.get(i).get(headers.indexOf("CREDIT ACCT NO")).replace("\"", ""));
                            ftn.setDebitAccountName(result.get(i).get(headers.indexOf("DR ACCT NAME")).replace("\"", ""));
                            ftn.setDebitAcctNo(result.get(i).get(headers.indexOf("DEBIT ACCT NO")).replace("\"", ""));
                            ftn.setDebitAmount(result.get(i).get(headers.indexOf("DR AMOUNT")).replace("\"", ""));
                            ftn.setDebitValueDate(result.get(i).get(headers.indexOf("DR VALUE DATE")).replace("\"", ""));
                            ftn.setFundsTransferID(result.get(i).get(headers.indexOf("ID")).replace("\"", ""));
                            ftn.setPaymentDetails(result.get(i).get(headers.indexOf("PAYMENT DETAILS")).replace("\"", ""));
                            allftn.add(ftn);

                        }
                        break;

                    case "Debit":
                        result = t24.getOfsData("FT.NARRATE", Ofsuser, Ofspass, "CREDIT.ACCT.NO:EQ=" + acc.trim());
                        headers = result.get(0);

                        if (headers.size() != result.get(1).size()) {

                            throw new Exception(result.get(1).get(0));

                        }
                        for (int i = 1; i < result.size(); i++) {
                            FtNarrateResponse ftn = new FtNarrateResponse();
                            dresp = ResponseCodes.SUCCESS;
                            ftn.setResponseCode(dresp.getCode());
                            ftn.setResponseText(dresp.getMessage());
                            ftn.setTransactionDate(zzdf.format(today));
                            ftn.setCreditAccountName(result.get(i).get(headers.indexOf("CR ACCT NAME")).replace("\"", ""));
                            ftn.setCreditAcctNo(result.get(i).get(headers.indexOf("CREDIT ACCT NO")).replace("\"", ""));
                            ftn.setDebitAccountName(result.get(i).get(headers.indexOf("DR ACCT NAME")).replace("\"", ""));
                            ftn.setDebitAcctNo(result.get(i).get(headers.indexOf("DEBIT ACCT NO")).replace("\"", ""));
                            ftn.setDebitAmount(result.get(i).get(headers.indexOf("DR AMOUNT")).replace("\"", ""));
                            ftn.setDebitValueDate(result.get(i).get(headers.indexOf("DR VALUE DATE")).replace("\"", ""));
                            ftn.setFundsTransferID(result.get(i).get(headers.indexOf("ID")).replace("\"", ""));
                            ftn.setPaymentDetails(result.get(i).get(headers.indexOf("PAYMENT DETAILS")).replace("\"", ""));
                            allftn.add(ftn);
                        }
                        break;
                }
            } else {
                dresp = ResponseCodes.Security_violation;
                FtNarrateResponse ftn = new FtNarrateResponse();
                ftn.setResponseCode(dresp.getCode());
                ftn.setResponseText(dresp.getMessage());
                ftn.setTransactionDate(zzdf.format(today));
                allftn.add(ftn);
                weblogger.error(allftn);
                return allftn;
            }
        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            FtNarrateResponse ftn = new FtNarrateResponse();
            ftn.setResponseCode(dresp.getCode());
            ftn.setResponseText(d.toString());
            ftn.setTransactionDate(zzdf.format(today));
            allftn.add(ftn);
            weblogger.fatal(ftn, d);
        }

        return allftn;
    }

    @WebMethod(operationName = "CustomerAmend")
    public CustomerNAUAmendResponse CustomerAmend(@WebParam(name = "CustomerAmendRequest") CustomerNauAmendRequest cuamend) throws Exception {

        String cusno = cuamend.getCustomerNo();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();

        CustomerNAUAmendResponse cumresp = new CustomerNAUAmendResponse();

        weblogger.info("CustomerAmendRequest");
        try {
            ArrayList<List<String>> result = t24.getOfsData("CUSTOMER.NAU.AMEND", Ofsuser, Ofspass, "CUSTOMER.NO:EQ=" + cusno.trim());
            List<String> headers = result.get(0);
            dresp = ResponseCodes.SUCCESS;
            cumresp.setResponseCode(dresp.getCode());
            cumresp.setResponseText(dresp.getMessage());
            cumresp.setFirstName(result.get(1).get(headers.indexOf("Name")).replace("\"", ""));
            cumresp.setIndustry(result.get(1).get(headers.indexOf("Industry")).replace("\"", ""));
            cumresp.setRelationshipOfficer(result.get(1).get(headers.indexOf("Relationship Officer")).replace("\"", ""));
            cumresp.setInputter(result.get(1).get(headers.indexOf("Inputter")).replace("\"", ""));
            cumresp.setCustomerNo(result.get(1).get(headers.indexOf("Customer No")).replace("\"", ""));
            cumresp.setSector(result.get(1).get(headers.indexOf("Sector")).replace("\"", ""));

        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            cumresp.setMessage(d.getMessage());
            cumresp.setResponseCode(dresp.getCode());
            cumresp.setMessage(d.toString());
            weblogger.fatal(cumresp, d);
        }
        return cumresp;

    }

    @WebMethod(operationName = "AccountDetailsReq")
    public AccountDetailsResponse AccountDetailsReq(@WebParam(name = "AccountDetailsRequest") AccountDetailsRequest acdetails) throws Exception {

        String accNo = acdetails.getAccountNumber();
        AccountDetailsResponse acresp = new AccountDetailsResponse();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        weblogger.info("AccountDetailsRequest");

        try {
            String APIKEY = acdetails.getApikey();

            String AUTHID = acdetails.getAuthenticationID();

            String APPLICATIONID = acdetails.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;

                acresp.setResponseCode(dresp.getCode());
                acresp.setResponseText(dresp.getMessage());
                acresp.setTransactionDate(zzdf.format(today));

                weblogger.error(acresp);
                return acresp;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;

                    acresp.setResponseCode(dresp.getCode());
                    acresp.setResponseText(dresp.getMessage());
                    acresp.setTransactionDate(zzdf.format(today));

                    weblogger.error(acresp);
                    return acresp;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;

                    acresp.setResponseCode(dresp.getCode());
                    acresp.setResponseText(dresp.getMessage());
                    acresp.setTransactionDate(zzdf.format(today));

                    weblogger.error(acresp);
                    return acresp;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;

                acresp.setResponseCode(dresp.getCode());
                acresp.setResponseText(dresp.getMessage());
                acresp.setTransactionDate(zzdf.format(today));
                weblogger.error(acresp);
                return acresp;
            }
            String stringtohash = acdetails.getAccountNumber() + APIKEY;

            String requesthash = acdetails.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);

            if (matched == true) {
                ArrayList<List<String>> result = t24.getDOfsData("ACCOUNT.DETAILS.2", Ofsuser, Ofspass, "ACCOUNT.NUMBER:EQ=" + accNo.trim());
                List<String> headers = result.get(0);
                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                dresp = ResponseCodes.SUCCESS;
                acresp.setResponseCode(dresp.getCode());
                acresp.setResponseText(dresp.getMessage());
                acresp.setTransactionDate(zzdf.format(today));
                acresp.setAccountNumber(result.get(1).get(headers.indexOf("Account No")).replace("\"", ""));
                acresp.setCustomerNumber(result.get(1).get(headers.indexOf("Customer")).replace("\"", ""));
                acresp.setOnlineBalance(result.get(1).get(headers.indexOf("Online Balance")).replace("\"", ""));
                acresp.setCustomerName(result.get(1).get(headers.indexOf("Name")).replace("\"", ""));
                acresp.setAccountOfficer(result.get(1).get(headers.indexOf("Account Officer")).replace("\"", ""));
                acresp.setCcy(result.get(1).get(headers.indexOf("Ccy")).replace("\"", ""));
                acresp.setProduct(result.get(1).get(headers.indexOf("Product")).replace("\"", ""));
                acresp.setAcctOpeningDate(result.get(1).get(headers.indexOf("Acct Opening Date")).replace("\"", ""));
                acresp.setAccountOfficer(result.get(1).get(headers.indexOf("Account Officer")).replace("\"", ""));
                //acresp.setPostRestriction(result.get(1).get(headers.indexOf("Post Restriction")).replace("\"", ""));
                acresp.setSnapInputter(result.get(1).get(headers.indexOf("Snap Inputter")).replace("\"", ""));
                acresp.setSnapAuthoriser(result.get(1).get(headers.indexOf("Snap Authoriser")).replace("\"", ""));
                acresp.setLoanType(result.get(1).get(headers.indexOf("Loan\'s Type")));

            } else {
                dresp = ResponseCodes.Security_violation;
                acresp.setResponseCode(dresp.getCode());
                acresp.setResponseText(dresp.getMessage());
                acresp.setTransactionDate(zzdf.format(today));

                weblogger.error(acresp);
                return acresp;
            }
        } catch (Exception d) {

            dresp = ResponseCodes.Invalid_transaction;
            acresp.setResponseCode(dresp.getCode());
            acresp.setResponseText(d.toString());
            acresp.setTransactionDate(zzdf.format(today));
            weblogger.fatal(acresp, d);

        }

        return acresp;
    }

    @WebMethod(operationName = "AccountBalance")
    public AcctBalResponse AccountBalance(@WebParam(name = "accbal") AcctBalRequest accbal) throws Exception {

        String acctNo = accbal.getAccountNo();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        AcctBalResponse accresp = new AcctBalResponse();
        weblogger.info("AccountBalanceRequest");

        try {

            String APIKEY = accbal.getApikey();

            String AUTHID = accbal.getAuthenticationID();

            String APPLICATIONID = accbal.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;

                accresp.setResponseCode(dresp.getCode());
                accresp.setResponseText(dresp.getMessage());
                accresp.setTransactionDate(zzdf.format(today));

                weblogger.error(accresp);
                return accresp;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;

                    accresp.setResponseCode(dresp.getCode());
                    accresp.setResponseText(dresp.getMessage());
                    accresp.setTransactionDate(zzdf.format(today));

                    weblogger.error(accresp);
                    return accresp;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;

                    accresp.setResponseCode(dresp.getCode());
                    accresp.setResponseText(dresp.getMessage());
                    accresp.setTransactionDate(zzdf.format(today));

                    weblogger.error(accresp);
                    return accresp;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;

                accresp.setResponseCode(dresp.getCode());
                accresp.setResponseText(dresp.getMessage());
                accresp.setTransactionDate(zzdf.format(today));

                weblogger.error(accresp);
                return accresp;
            }
            String stringtohash = accbal.getAccountNo() + APIKEY;

            String requesthash = accbal.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {
                ArrayList<List<String>> result = t24.getOfsData("ACCT.BAL.NEW3", Ofsuser, Ofspass, "ACCOUNT.NUMBER:EQ=" + acctNo.trim());
                result.remove(1);
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                dresp = ResponseCodes.SUCCESS;
                accresp.setResponseCode(dresp.getCode());
                accresp.setResponseText(dresp.getMessage());
                accresp.setTransactionDate(zzdf.format(today));
                accresp.setAccountNumber(result.get(1).get(headers.indexOf("Account No")).replace("\"", ""));
                accresp.setCustomer(result.get(1).get(headers.indexOf("Customer")).replace("\"", ""));
                accresp.setName(result.get(1).get(headers.indexOf("Name")).replace("\"", ""));
                accresp.setLimitReference(result.get(1).get(headers.indexOf("Limit ref")).replace("\"", ""));
                accresp.setProduct(result.get(1).get(headers.indexOf("Product")).replace("\"", ""));
                accresp.setCurrency(result.get(1).get(headers.indexOf("Ccy")).replace("\"", ""));
                accresp.setWorkingBalance(result.get(1).get(headers.indexOf("Working Bal")).replace("\"", ""));
                accresp.setLedgerBalance(result.get(1).get(headers.indexOf("Ledger Bal")).replace("\"", ""));
                accresp.setClearedBalance(result.get(1).get(headers.indexOf("Cleared Bal")).replace("\"", ""));
                accresp.setLockedAmount(result.get(1).get(headers.indexOf("Locked Amount")).replace("\"", ""));
                accresp.setTransactionDate(zzdf.format(today));
            } else {
                dresp = ResponseCodes.Security_violation;
                accresp.setResponseCode(dresp.getCode());
                accresp.setResponseText(dresp.getMessage());
                accresp.setTransactionDate(zzdf.format(today));

                weblogger.error(accresp);
                return accresp;
            }
        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            accresp.setResponseCode(dresp.getCode());
            accresp.setResponseText(d.toString());
            accresp.setTransactionDate(zzdf.format(today));
            weblogger.fatal(accresp, d);

        }
        return accresp;
    }

    @WebMethod(operationName = "PandLCategEntries")
    public List<CategEntBookResponse> PandLCategEntries(@WebParam(name = "PandLCateg") PandLCategRequest PLRequest) throws Exception {

        String PLCategory = PLRequest.getPLCategory();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        List<CategEntBookResponse> allctn = new ArrayList<>();
        String startdate = PLRequest.getStartDate();
        String enddate = PLRequest.getEndDate();

        weblogger.info("CategEntryRequest");

        try {
            String APIKEY = PLRequest.getApikey();

            String AUTHID = PLRequest.getAuthenticationID();

            String APPLICATIONID = PLRequest.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;
                CategEntBookResponse ctresp = new CategEntBookResponse();
                ctresp.setResponseCode(dresp.getCode());
                ctresp.setResponseText(dresp.getMessage());
                ctresp.setTransactionDate(zzdf.format(today));
                allctn.add(ctresp);
                weblogger.error(allctn);
                return allctn;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    CategEntBookResponse ctresp = new CategEntBookResponse();
                    ctresp.setResponseCode(dresp.getCode());
                    ctresp.setResponseText(dresp.getMessage());
                    ctresp.setTransactionDate(zzdf.format(today));
                    allctn.add(ctresp);
                    weblogger.error(allctn);
                    return allctn;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    CategEntBookResponse ctresp = new CategEntBookResponse();
                    ctresp.setResponseCode(dresp.getCode());
                    ctresp.setResponseText(dresp.getMessage());
                    ctresp.setTransactionDate(zzdf.format(today));
                    allctn.add(ctresp);
                    weblogger.error(allctn);
                    return allctn;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;
                CategEntBookResponse ctresp = new CategEntBookResponse();
                ctresp.setResponseCode(dresp.getCode());
                ctresp.setResponseText(dresp.getMessage());
                ctresp.setTransactionDate(zzdf.format(today));
                allctn.add(ctresp);
                weblogger.error(allctn);
                return allctn;
            }
            String stringtohash = PLRequest.getPLCategory() + PLRequest.getStartDate() + PLRequest.getEndDate() + APIKEY;

            String requesthash = PLRequest.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
                Date Start = sdf.parse(startdate);
                Date End = sdf.parse(enddate);

                startdate = ndf.format(Start);
                enddate = ndf.format(End);
                ArrayList<List<String>> result = t24.getOfsData("CATEG.ENT.BOOK.ZZ", Ofsuser, Ofspass, "PL.CATEGORY:EQ=" + PLCategory.trim() + ",BOOKING.DATE:RG=" + startdate + " " + enddate);
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }

                for (int i = 0; i < result.size(); i++) {
                    CategEntBookResponse ctresp = new CategEntBookResponse();
                    dresp = ResponseCodes.SUCCESS;
                    ctresp.setResponseCode(dresp.getCode());
                    ctresp.setResponseText(dresp.getMessage());
                    ctresp.setTransactionDate(zzdf.format(today));
                    ctresp.setBookingDate(result.get(i).get(headers.indexOf("Booking Date")).replace("\"", ""));
                    ctresp.setNarration(result.get(i).get(headers.indexOf("Narration")).replace("\"", ""));
                    ctresp.setRefNo(result.get(i).get(headers.indexOf("Ref No")).replace("\"", ""));
                    ctresp.setValueDate(result.get(i).get(headers.indexOf("Value Date")).replace("\"", ""));
                    ctresp.setDebit(result.get(i).get(headers.indexOf("Debit")).replace("\"", ""));
                    ctresp.setCredit(result.get(i).get(headers.indexOf("Credit")).replace("\"", ""));
                    ctresp.setClosingBalance(result.get(i).get(headers.indexOf("Closing Balance")).replace("\"", ""));
                    allctn.add(ctresp);
                }
            } else {
                dresp = ResponseCodes.Security_violation;
                CategEntBookResponse ctresp = new CategEntBookResponse();
                ctresp.setResponseCode(dresp.getCode());
                ctresp.setResponseText(dresp.getMessage());
                ctresp.setTransactionDate(zzdf.format(today));
                allctn.add(ctresp);
                weblogger.error(allctn);
                return allctn;
            }

        } catch (Exception d) {
            CategEntBookResponse ctresp = new CategEntBookResponse();
            dresp = ResponseCodes.Invalid_transaction;
            ctresp.setResponseCode(dresp.getCode());
            ctresp.setResponseText(d.toString());
            ctresp.setTransactionDate(zzdf.format(today));
            allctn.add(ctresp);
            weblogger.fatal(ctresp, d);

        }
        return allctn;
    }

    @WebMethod(operationName = "ICLChequeReq")
    public List<IclChequeResponse> ICLChequeReq(@WebParam(name = "ICLChequeRequest") IclChequeRequest dayreq) throws Exception {

        String icl = dayreq.getT24AcctNo();
        List<IclChequeResponse> allicl = new ArrayList<>();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        weblogger.info("ICLChequeRequest");

        try {
            String APIKEY = dayreq.getApikey();

            String AUTHID = dayreq.getAuthenticationID();

            String APPLICATIONID = dayreq.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;
                IclChequeResponse dayresp = new IclChequeResponse();
                dayresp.setResponseCode(dresp.getCode());
                dayresp.setResponseText(dresp.getMessage());
                dayresp.setTransactionDate(zzdf.format(today));
                allicl.add(dayresp);
                weblogger.error(allicl);
                return allicl;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    IclChequeResponse dayresp = new IclChequeResponse();
                    dayresp.setResponseCode(dresp.getCode());
                    dayresp.setResponseText(dresp.getMessage());
                    dayresp.setTransactionDate(zzdf.format(today));
                    allicl.add(dayresp);
                    weblogger.error(allicl);
                    return allicl;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    IclChequeResponse dayresp = new IclChequeResponse();
                    dayresp.setResponseCode(dresp.getCode());
                    dayresp.setResponseText(dresp.getMessage());
                    dayresp.setTransactionDate(zzdf.format(today));
                    allicl.add(dayresp);
                    weblogger.error(allicl);
                    return allicl;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;
                IclChequeResponse dayresp = new IclChequeResponse();
                dayresp.setResponseCode(dresp.getCode());
                dayresp.setResponseText(dresp.getMessage());
                dayresp.setTransactionDate(zzdf.format(today));
                allicl.add(dayresp);
                weblogger.error(allicl);
                return allicl;
            }

            String stringtohash = dayreq.getT24AcctNo() + APIKEY;

            String requesthash = dayreq.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {

                ArrayList<List<String>> result = t24.getOfsData("E.ICL.CHEQUE", Ofsuser, Ofspass, "T24.ACCT.NO:EQ=" + icl.trim());
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                for (int i = 1; i < result.size(); i++) {
                    IclChequeResponse dayresp = new IclChequeResponse();
                    dresp = ResponseCodes.SUCCESS;
                    dayresp.setResponseCode(dresp.getCode());
                    dayresp.setResponseText(dresp.getMessage());
                    dayresp.setTransactionDate(zzdf.format(today));
                    dayresp.setT24ChequeID(result.get(i).get(headers.indexOf("T24 Cheque ID")).replace("\"", ""));
                    dayresp.setT24AcctNo(result.get(i).get(headers.indexOf("T24 Acct No")).replace("\"", ""));
                    dayresp.setCustomerCIF(result.get(i).get(headers.indexOf("Cust CIF")).replace("\"", ""));
                    dayresp.setCustomerLoanID(result.get(i).get(headers.indexOf("Cust Loan ID")).replace("\"", ""));
                    dayresp.setCustomerBank(result.get(i).get(headers.indexOf("Customer Bank")).replace("\"", ""));
                    dayresp.setCustomerChequeSerialNumber(result.get(i).get(headers.indexOf("Customer Cheque\'s Serial Number")));
                    dayresp.setCustomerName(result.get(i).get(headers.indexOf("Customer Name")).replace("\"", ""));
                    dayresp.setCustomerChequeStatus(result.get(i).get(headers.indexOf("Customer Cheque Status")).replace("\"", ""));
                    dayresp.setCustomerAccount(result.get(i).get(headers.indexOf("Customer Account")).replace("\"", ""));
                    dayresp.setSalesOfficer(result.get(i).get(headers.indexOf("Sales Officer")).replace("\"", ""));
                    dayresp.setDateCollected(result.get(i).get(headers.indexOf("DATE")).replace("\"", ""));
                    dayresp.setComment(result.get(i).get(headers.indexOf("Comment")).replace("\"", ""));
                    allicl.add(dayresp);
                }
            } else {
                dresp = ResponseCodes.Security_violation;
                IclChequeResponse dayresp = new IclChequeResponse();
                dayresp.setResponseCode(dresp.getCode());
                dayresp.setResponseText(dresp.getMessage());
                dayresp.setTransactionDate(zzdf.format(today));
                allicl.add(dayresp);
                weblogger.error(allicl);
                return allicl;
            }

        } catch (Exception d) {
            IclChequeResponse dayresp = new IclChequeResponse();
            dresp = ResponseCodes.Invalid_transaction;
            dayresp.setResponseCode(dresp.getCode());
            dayresp.setResponseText(d.toString());
            dayresp.setTransactionDate(zzdf.format(today));
            allicl.add(dayresp);
            weblogger.fatal(dayresp, d);

        }
        return allicl;
    }

    @WebMethod(operationName = "DailyExpectedReq")
    public DailyExpectedResponse DailyExpectedReq(@WebParam(name = "DailyExpectedRequest") DailyExpectedRequest dayreq) throws Exception {

        String day = dayreq.getID();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        DailyExpectedResponse dayresp = new DailyExpectedResponse();
        weblogger.info("DailyExpectedRequest");

        try {
            String APIKEY = dayreq.getApikey();

            String AUTHID = dayreq.getAuthenticationID();

            String APPLICATIONID = dayreq.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;
                dayresp.setResponseCode(dresp.getCode());
                dayresp.setResponseText(dresp.getMessage());
                dayresp.setTransactionDate(zzdf.format(today));

                weblogger.error(dayresp);
                return dayresp;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;

                    dayresp.setResponseCode(dresp.getCode());
                    dayresp.setResponseText(dresp.getMessage());
                    dayresp.setTransactionDate(zzdf.format(today));

                    weblogger.error(dayresp);
                    return dayresp;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;

                    dayresp.setResponseCode(dresp.getCode());
                    dayresp.setResponseText(dresp.getMessage());
                    dayresp.setTransactionDate(zzdf.format(today));

                    weblogger.error(dayresp);
                    return dayresp;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;

                dayresp.setResponseCode(dresp.getCode());
                dayresp.setResponseText(dresp.getMessage());
                dayresp.setTransactionDate(zzdf.format(today));

                weblogger.error(dayresp);
                return dayresp;
            }
            String stringtohash = dayreq.getID() + APIKEY;

            String requesthash = dayreq.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {
                ArrayList<List<String>> result = t24.getOfsData("DAILY.EXPECTED.1", Ofsuser, Ofspass, "@ID:EQ=" + day.trim());
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                dresp = ResponseCodes.SUCCESS;
                dayresp.setResponseCode(dresp.getCode());
                dayresp.setResponseText(dresp.getMessage());
                dayresp.setTransactionDate(zzdf.format(today));
                dayresp.setID(result.get(1).get(headers.indexOf("ID")).replace("\"", ""));
                dayresp.setType(result.get(1).get(headers.indexOf("TYPE")).replace("\"", ""));
                dayresp.setRepaymentDate(result.get(1).get(headers.indexOf("REPAYMENT DATE")).replace("\"", ""));
                dayresp.setPrinAmount(result.get(1).get(headers.indexOf("PRIN AMOUNT")).replace("\"", ""));
                dayresp.setInterestRepay(result.get(1).get(headers.indexOf("INTEREST REPAY")).replace("\"", ""));
                dayresp.setTotalRepayment(result.get(1).get(headers.indexOf("TOTAL REPAYMENT")).replace("\"", ""));

            } else {
                dresp = ResponseCodes.Security_violation;
                dayresp.setResponseCode(dresp.getCode());
                dayresp.setResponseText(dresp.getMessage());
                dayresp.setTransactionDate(zzdf.format(today));

                weblogger.error(dayresp);
                return dayresp;
            }
        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            dayresp.setResponseCode(dresp.getCode());
            dayresp.setResponseText(d.toString());
            dayresp.setTransactionDate(zzdf.format(today));
            weblogger.fatal(dayresp, d);

        }
        return dayresp;
    }

    @WebMethod(operationName = "BalanceSumRequest")
    public LdBalancesSumResponse BalanceSumRequest(@WebParam(name = "LDBalancesSumRequest") LdBalancesSumRequest bsreq) throws Exception {

        String ID = bsreq.getDate();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        LdBalancesSumResponse bsresp = new LdBalancesSumResponse();
        weblogger.info("BalanceSumRequest");

        try {

            String APIKEY = bsreq.getApikey();

            String AUTHID = bsreq.getAuthenticationID();

            String APPLICATIONID = bsreq.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Invalid_Sender;

                bsresp.setResponseCode(dresp.getCode());
                bsresp.setResponseText(dresp.getMessage());
                bsresp.setTransactionDate(zzdf.format(today));
                weblogger.error(bsresp);
                return bsresp;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Security_violation;

                    bsresp.setResponseCode(dresp.getCode());
                    bsresp.setResponseText(dresp.getMessage());
                    bsresp.setTransactionDate(zzdf.format(today));

                    weblogger.error(bsresp);
                    return bsresp;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Security_violation;

                    bsresp.setResponseCode(dresp.getCode());
                    bsresp.setResponseText(dresp.getMessage());
                    bsresp.setTransactionDate(zzdf.format(today));

                    weblogger.error(bsresp);
                    return bsresp;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;

                bsresp.setResponseCode(dresp.getCode());
                bsresp.setResponseText(dresp.getMessage());
                bsresp.setTransactionDate(zzdf.format(today));

                weblogger.error(bsresp);
                return bsresp;
            }
            String stringtohash = bsreq.getDate() + APIKEY;

            String requesthash = bsreq.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {

                ArrayList<List<String>> result = t24.getOfsData("EM.LD.BALANCES.SUM", Ofsuser, Ofspass, "@ID:EQ=" + ID.trim());
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                dresp = ResponseCodes.SUCCESS;
                bsresp.setResponseCode(dresp.getCode());
                bsresp.setResponseText(dresp.getMessage());

            } else {
                dresp = ResponseCodes.Security_violation;
                bsresp.setResponseCode(dresp.getCode());
                bsresp.setResponseText(dresp.getMessage());
                bsresp.setTransactionDate(zzdf.format(today));

                weblogger.error(bsresp);
                return bsresp;
            }
        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            bsresp.setResponseCode(dresp.getCode());
            bsresp.setResponseText(d.toString());
            bsresp.setTransactionDate(zzdf.format(today));
            weblogger.fatal(bsresp, d);

        }
        return bsresp;
    }

    @WebMethod(operationName = "LDRpmHistReq")
    public List<LdRpmHistResponse> LdRpmHistReq(@WebParam(name = "LDRpmHistRequest") LdRpmHistRequest ldreq) throws Exception {
        List<LdRpmHistResponse> allht = new ArrayList<>();
        String ldp = ldreq.getLoanID();

        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        weblogger.info("LDRpmHistRequest");

        try {
            String APIKEY = ldreq.getApikey();

            String AUTHID = ldreq.getAuthenticationID();

            String APPLICATIONID = ldreq.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;
                LdRpmHistResponse rhresp = new LdRpmHistResponse();
                rhresp.setResponseCode(dresp.getCode());
                rhresp.setResponseText(dresp.getMessage());
                rhresp.setTransactionDate(zzdf.format(today));
                allht.add(rhresp);
                weblogger.error(allht);
                return allht;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    LdRpmHistResponse rhresp = new LdRpmHistResponse();
                    rhresp.setResponseCode(dresp.getCode());
                    rhresp.setResponseText(dresp.getMessage());
                    rhresp.setTransactionDate(zzdf.format(today));
                    allht.add(rhresp);
                    weblogger.error(allht);
                    return allht;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    LdRpmHistResponse rhresp = new LdRpmHistResponse();
                    rhresp.setResponseCode(dresp.getCode());
                    rhresp.setResponseText(dresp.getMessage());
                    rhresp.setTransactionDate(zzdf.format(today));
                    allht.add(rhresp);
                    weblogger.error(allht);
                    return allht;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;
                LdRpmHistResponse rhresp = new LdRpmHistResponse();
                rhresp.setResponseCode(dresp.getCode());
                rhresp.setResponseText(dresp.getMessage());
                rhresp.setTransactionDate(zzdf.format(today));
                allht.add(rhresp);
                weblogger.error(allht);
                return allht;
            }
            String stringtohash = ldreq.getLoanID() + APIKEY;

            String requesthash = ldreq.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {
                ArrayList<List<String>> result = t24.getOfsData("EM.LD.RPM.HIST", Ofsuser, Ofspass, "@ID:EQ=" + ldp.trim());
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                for (int i = 1; i < result.size(); i++) {
                    LdRpmHistResponse rhresp = new LdRpmHistResponse();
                    dresp = ResponseCodes.SUCCESS;
                    rhresp.setResponseCode(dresp.getCode());
                    rhresp.setResponseText(dresp.getMessage());
                    rhresp.setTransactionDate(zzdf.format(today));
                    rhresp.setDate(result.get(i).get(headers.indexOf("DATE")).replace("\"", ""));
                    rhresp.setSchType(result.get(i).get(headers.indexOf("SCH.TYPE")).replace("\"", ""));
                    rhresp.setAmountDue(result.get(i).get(headers.indexOf("AMOUNT DUE")).replace("\"", ""));
                    rhresp.setAmountPaid(result.get(i).get(headers.indexOf("AMOUNT PAID")).replace("\"", ""));
                    allht.add(rhresp);
                }
            } else {
                dresp = ResponseCodes.Security_violation;
                LdRpmHistResponse rhresp = new LdRpmHistResponse();
                rhresp.setResponseCode(dresp.getCode());
                rhresp.setResponseText(dresp.getMessage());
                rhresp.setTransactionDate(zzdf.format(today));
                allht.add(rhresp);
                weblogger.error(allht);
                return allht;
            }

            //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
        } catch (Exception d) {
            LdRpmHistResponse rhresp = new LdRpmHistResponse();
            dresp = ResponseCodes.Invalid_transaction;
            rhresp.setResponseCode(dresp.getCode());
            rhresp.setResponseText(d.toString());
            rhresp.setTransactionDate(zzdf.format(today));
            allht.add(rhresp);
            weblogger.fatal(rhresp, d);

        }
        return allht;
    }

    @WebMethod(operationName = "PCLListAccount")
    public GICListAccountResponse PCLListAccount(@WebParam(name = "PCLListAccountRequest") GICListAccountRequest gcl) throws Exception {

        String gclreq = gcl.getAccountNumber();
        GICListAccountResponse laresp = new GICListAccountResponse();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        weblogger.info("PCLListAccountEnquiry");

        try {
            String APIKEY = gcl.getApikey();

            String AUTHID = gcl.getAuthenticationID();

            String APPLICATIONID = gcl.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;

                laresp.setResponseCode(dresp.getCode());
                laresp.setResponseText(dresp.getMessage());
                laresp.setTransactionDate(zzdf.format(today));
                weblogger.error(laresp);
                return laresp;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;

                    laresp.setResponseCode(dresp.getCode());
                    laresp.setResponseText(dresp.getMessage());
                    laresp.setTransactionDate(zzdf.format(today));

                    weblogger.error(laresp);
                    return laresp;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    laresp.setResponseCode(dresp.getCode());
                    laresp.setResponseText(dresp.getMessage());
                    laresp.setTransactionDate(zzdf.format(today));

                    weblogger.error(laresp);
                    return laresp;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;

                laresp.setResponseCode(dresp.getCode());
                laresp.setResponseText(dresp.getMessage());
                laresp.setTransactionDate(zzdf.format(today));

                weblogger.error(laresp);
                return laresp;
            }
            String stringtohash = gcl.getAccountNumber() + APIKEY;

            String requesthash = gcl.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {
                ArrayList<List<String>> result = t24.getOfsData("PCL.GIC.LIST.ACCOUNT", Ofsuser, Ofspass, "ACCOUNT.NUMBER:EQ=" + gclreq.trim());
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                dresp = ResponseCodes.SUCCESS;
                laresp.setResponseCode(dresp.getCode());
                laresp.setResponseText(dresp.getMessage());
                laresp.setTransactionDate(zzdf.format(today));
                laresp.setAccountNumber(result.get(1).get(headers.indexOf("Account No")).replace("\"", ""));
                laresp.setCustomerName(result.get(1).get(headers.indexOf("Customer Name")).replace("\"", ""));
                laresp.setCategory(result.get(1).get(headers.indexOf("Category")).replace("\"", ""));
                laresp.setCurrency(result.get(1).get(headers.indexOf("Currency")).replace("\"", ""));
                laresp.setOnlineBalance(result.get(1).get(headers.indexOf("Online Balance")).replace("\"", ""));
                laresp.setAccountMnemonic(result.get(1).get(headers.indexOf("Account Mnemonic")).replace("\"", ""));
                laresp.setOpeningDate(result.get(1).get(headers.indexOf("Open Date")).replace("\"", ""));
                laresp.setMaturityDate(result.get(1).get(headers.indexOf("Maturity Date")).replace("\"", ""));
                laresp.setCustomerNo(result.get(1).get(headers.indexOf("Customer No")).replace("\"", ""));

            } else {
                dresp = ResponseCodes.Security_violation;
                laresp.setResponseCode(dresp.getCode());
                laresp.setResponseText(dresp.getMessage());
                laresp.setTransactionDate(zzdf.format(today));
                weblogger.error(laresp);
                return laresp;
            }
        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            laresp.setResponseCode(dresp.getCode());
            laresp.setResponseText(d.toString());
            laresp.setTransactionDate(zzdf.format(today));
            weblogger.fatal(laresp, d);
        }
        return laresp;
    }

    @WebMethod(operationName = "LoanDisbursedReq")
    public List<LoanDisbursedResponse> LoanDisbursedReq(@WebParam(name = "LoanDisbursedRequest") LoanDisbursedRequest loreq) throws Exception {

        String LDReferenceID = loreq.getCustomerNumber();
        List<LoanDisbursedResponse> alllt = new ArrayList<>();

        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        weblogger.info("LoanDisbursedRequest");

        try {
            String APIKEY = loreq.getApikey();

            String AUTHID = loreq.getAuthenticationID();

            String APPLICATIONID = loreq.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;
                LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
                ldresp.setResponseCode(dresp.getCode());
                ldresp.setResponseText(dresp.getMessage());
                ldresp.setTransactionDate(zzdf.format(today));
                alllt.add(ldresp);
                weblogger.error(alllt);
                return alllt;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
                    ldresp.setResponseCode(dresp.getCode());
                    ldresp.setResponseText(dresp.getMessage());
                    ldresp.setTransactionDate(zzdf.format(today));
                    alllt.add(ldresp);
                    weblogger.error(alllt);
                    return alllt;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
                    ldresp.setResponseCode(dresp.getCode());
                    ldresp.setResponseText(dresp.getMessage());
                    ldresp.setTransactionDate(zzdf.format(today));
                    alllt.add(ldresp);
                    weblogger.error(alllt);
                    return alllt;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;
                LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
                ldresp.setResponseCode(dresp.getCode());
                ldresp.setResponseText(dresp.getMessage());
                ldresp.setTransactionDate(zzdf.format(today));
                alllt.add(ldresp);
                weblogger.error(alllt);
                return alllt;
            }

            String stringtohash = loreq.getCustomerNumber() + APIKEY;

            String requesthash = loreq.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {
                ArrayList<List<String>> result = t24.getOfsData("LD.LOAN.DISBURSED.FIN.CUS.2", Ofsuser, Ofspass, "CUSTOMER.ID:EQ=" + LDReferenceID.trim());
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                for (int i = 1; i < result.size(); i++) {
                    LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
                    dresp = ResponseCodes.SUCCESS;
                    ldresp.setResponseCode(dresp.getCode());
                    ldresp.setResponseText(dresp.getMessage());
                    ldresp.setTransactionDate(zzdf.format(today));
                    ldresp.setTransactionRef(result.get(i).get(headers.indexOf("Transaction Ref")).replace("\"", ""));
                    ldresp.setCustomerName(result.get(i).get(headers.indexOf("Customer Name")).replace("\"", ""));
                    ldresp.setProduct(result.get(i).get(headers.indexOf("Product")).replace("\"", ""));
                    ldresp.setT24AccountNumber(result.get(i).get(headers.indexOf("T24 Account No")).replace("\"", ""));
                    ldresp.setExtAcctNo(result.get(i).get(headers.indexOf("Ext Acct No")).replace("\"", ""));
                    ldresp.setOracleNumber(result.get(i).get(headers.indexOf("Oracle number")).replace("\"", ""));
                    ldresp.setEmployerName(result.get(i).get(headers.indexOf("Employer\'s Name")));
                    ldresp.setBankBranch(result.get(i).get(headers.indexOf("BankBranch Name")).replace("\"", ""));
                    ldresp.setCustomerEmail(result.get(i).get(headers.indexOf("Cusomer\'s Email")));
                    ldresp.setMobileNo(result.get(i).get(headers.indexOf("Mobile No")).replace("\"", ""));
                    ldresp.setCustomerRelationNumber(result.get(i).get(headers.indexOf("Customer Relation No")).replace("\"", ""));
                    ldresp.setBookedAmount(result.get(i).get(headers.indexOf("Booked Amount")).replace("\"", ""));
                    ldresp.setDisbursedAmount(result.get(i).get(headers.indexOf("Disbursed Amount")).replace("\"", ""));
                    ldresp.setValueDate(result.get(i).get(headers.indexOf("Value Date")).replace("\"", ""));
                    ldresp.setRepaymentStartDate(result.get(i).get(headers.indexOf("Repayment Start Date")).replace("\"", ""));
                    ldresp.setMaturityDate(result.get(i).get(headers.indexOf("Maturity Date")).replace("\"", ""));
                    ldresp.setRepaymentType(result.get(i).get(headers.indexOf("Repayment Type")).replace("\"", ""));
                    ldresp.setMeansOfPayment(result.get(i).get(headers.indexOf("Means Of Payment")).replace("\"", ""));
                    ldresp.setLoanTenor(result.get(i).get(headers.indexOf("Loan Tenor")).replace("\"", ""));
                    ldresp.setInterestRate(result.get(i).get(headers.indexOf("Interest Rate")).replace("\"", ""));
                    ldresp.setPrincipalRepayment(result.get(i).get(headers.indexOf("Principal Repayment")).replace("\"", ""));
                    ldresp.setIntRepay(result.get(i).get(headers.indexOf("Int Repay")).replace("\"", ""));
                    ldresp.setTotalMonthlyRepay(result.get(i).get(headers.indexOf("Total Monthly Repay")).replace("\"", ""));
                    ldresp.setAcctOfficer(result.get(i).get(headers.indexOf("Acct Officer")).replace("\"", ""));
                    ldresp.setLoanType(result.get(i).get(headers.indexOf("Loan\'s Type")));
                    ldresp.setGender(result.get(i).get(headers.indexOf("Gender")).replace("\"", ""));
                    ldresp.setIndustry(result.get(i).get(headers.indexOf("Industry")).replace("\"", ""));
                    ldresp.setSector(result.get(i).get(headers.indexOf("Sector")).replace("\"", ""));
                    ldresp.setGuarantorName(result.get(i).get(headers.indexOf("Guarantor\'s Name")));
                    ldresp.setAge(result.get(i).get(headers.indexOf("Age")).replace("\"", ""));
                    ldresp.setOldTransactionRef(result.get(i).get(headers.indexOf("Old Transaction Ref")).replace("\"", ""));
                    ldresp.setOriginalLoanAmount(result.get(i).get(headers.indexOf("Original Loan Amt")).replace("\"", ""));
                    ldresp.setOriginalDisbursedAmount(result.get(i).get(headers.indexOf("Original Disbus Amt")).replace("\"", ""));
                    ldresp.setIntroducer(result.get(i).get(headers.indexOf("Introducer")).replace("\"", ""));
                    ldresp.setLoanDisbursedAmount(result.get(i).get(headers.indexOf("Loan Disbursed Amount")).replace("\"", ""));
                    alllt.add(ldresp);
                }

            } else {
                dresp = ResponseCodes.Security_violation;
                LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
                ldresp.setResponseCode(dresp.getCode());
                ldresp.setResponseText(dresp.getMessage());
                ldresp.setTransactionDate(zzdf.format(today));
                alllt.add(ldresp);
                weblogger.error(alllt);
                return alllt;
            }
            //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
        } catch (Exception d) {
            LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
            dresp = ResponseCodes.Invalid_transaction;
            ldresp.setResponseCode(dresp.getCode());
            ldresp.setResponseText(d.toString());
            ldresp.setTransactionDate(zzdf.format(today));
            alllt.add(ldresp);
            weblogger.fatal(ldresp, d);
        }
        return alllt;
    }

    @WebMethod(operationName = "LoanDisbursedList")
    public List<LoanDisbursedResponse> LoanDisbursedList(@WebParam(name = "LoanDisbursedList") LoanDisbursedRequest loreq) throws Exception {

        String sd = loreq.getStartdate();
        String ed = loreq.getEnddate();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        List<LoanDisbursedResponse> alllt = new ArrayList<>();

        weblogger.info("LoanDisbursedRequest");

        try {
            String APIKEY = loreq.getApikey();

            String AUTHID = loreq.getAuthenticationID();

            String APPLICATIONID = loreq.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;
                LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
                ldresp.setResponseCode(dresp.getCode());
                ldresp.setResponseText(dresp.getMessage());
                ldresp.setTransactionDate(zzdf.format(today));
                alllt.add(ldresp);
                weblogger.error(alllt);
                return alllt;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
                    ldresp.setResponseCode(dresp.getCode());
                    ldresp.setResponseText(dresp.getMessage());
                    ldresp.setTransactionDate(zzdf.format(today));
                    alllt.add(ldresp);
                    weblogger.error(alllt);
                    return alllt;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
                    ldresp.setResponseCode(dresp.getCode());
                    ldresp.setResponseText(dresp.getMessage());
                    ldresp.setTransactionDate(zzdf.format(today));
                    alllt.add(ldresp);
                    weblogger.error(alllt);
                    return alllt;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;
                LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
                ldresp.setResponseCode(dresp.getCode());
                ldresp.setResponseText(dresp.getMessage());
                ldresp.setTransactionDate(zzdf.format(today));
                alllt.add(ldresp);
                weblogger.error(alllt);
                return alllt;
            }

            String stringtohash = loreq.getStartdate() + loreq.getEnddate() + APIKEY;

            String requesthash = loreq.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {
                SimpleDateFormat sdfs = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat ndfs = new SimpleDateFormat("yyyyMMdd");
                Date Start = sdfs.parse(sd);
                Date End = sdfs.parse(ed);

                sd = ndfs.format(Start);
                ed = ndfs.format(End);
                ArrayList<List<String>> result = t24.getOfsData("LD.LOAN.DISBURSED.FIN.CUS.2", Ofsuser, Ofspass, "VALUE.DATE:RG=" + sd.trim() + " " + ed.trim());
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                for (int i = 1; i < result.size(); i++) {
                    LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
                    dresp = ResponseCodes.SUCCESS;
                    ldresp.setResponseCode(dresp.getCode());
                    ldresp.setResponseText(dresp.getMessage());
                    ldresp.setTransactionDate(zzdf.format(today));
                    ldresp.setTransactionRef(result.get(i).get(headers.indexOf("Transaction Ref")).replace("\"", ""));
                    ldresp.setCustomerName(result.get(i).get(headers.indexOf("Customer Name")).replace("\"", ""));
                    ldresp.setProduct(result.get(i).get(headers.indexOf("Product")).replace("\"", ""));
                    ldresp.setT24AccountNumber(result.get(i).get(headers.indexOf("T24 Account No")).replace("\"", ""));
                    ldresp.setExtAcctNo(result.get(i).get(headers.indexOf("Ext Acct No")).replace("\"", ""));
                    ldresp.setOracleNumber(result.get(i).get(headers.indexOf("Oracle number")).replace("\"", ""));
                    ldresp.setEmployerName(result.get(i).get(headers.indexOf("Employer\'s Name")));
                    ldresp.setBankBranch(result.get(i).get(headers.indexOf("BankBranch Name")).replace("\"", ""));
                    ldresp.setCustomerEmail(result.get(i).get(headers.indexOf("Cusomer\'s Email")));
                    ldresp.setMobileNo(result.get(i).get(headers.indexOf("Mobile No")).replace("\"", ""));
                    ldresp.setCustomerRelationNumber(result.get(i).get(headers.indexOf("Customer Relation No")).replace("\"", ""));
                    ldresp.setBookedAmount(result.get(i).get(headers.indexOf("Booked Amount")).replace("\"", ""));
                    ldresp.setDisbursedAmount(result.get(i).get(headers.indexOf("Disbursed Amount")).replace("\"", ""));
                    ldresp.setValueDate(result.get(i).get(headers.indexOf("Value Date")).replace("\"", ""));
                    ldresp.setRepaymentStartDate(result.get(i).get(headers.indexOf("Repayment Start Date")).replace("\"", ""));
                    ldresp.setMaturityDate(result.get(i).get(headers.indexOf("Maturity Date")).replace("\"", ""));
                    ldresp.setRepaymentType(result.get(i).get(headers.indexOf("Repayment Type")).replace("\"", ""));
                    ldresp.setMeansOfPayment(result.get(i).get(headers.indexOf("Means Of Payment")).replace("\"", ""));
                    ldresp.setLoanTenor(result.get(i).get(headers.indexOf("Loan Tenor")).replace("\"", ""));
                    ldresp.setInterestRate(result.get(i).get(headers.indexOf("Interest Rate")).replace("\"", ""));
                    ldresp.setPrincipalRepayment(result.get(i).get(headers.indexOf("Principal Repayment")).replace("\"", ""));
                    ldresp.setIntRepay(result.get(i).get(headers.indexOf("Int Repay")).replace("\"", ""));
                    ldresp.setTotalMonthlyRepay(result.get(i).get(headers.indexOf("Total Monthly Repay")).replace("\"", ""));
                    ldresp.setAcctOfficer(result.get(i).get(headers.indexOf("Acct Officer")).replace("\"", ""));
                    ldresp.setLoanType(result.get(i).get(headers.indexOf("Loan\'s Type")));
                    ldresp.setGender(result.get(i).get(headers.indexOf("Gender")).replace("\"", ""));
                    ldresp.setIndustry(result.get(i).get(headers.indexOf("Industry")).replace("\"", ""));
                    ldresp.setSector(result.get(i).get(headers.indexOf("Sector")).replace("\"", ""));
                    ldresp.setGuarantorName(result.get(i).get(headers.indexOf("Guarantor\'s Name")));
                    ldresp.setAge(result.get(i).get(headers.indexOf("Age")).replace("\"", ""));
                    ldresp.setOldTransactionRef(result.get(i).get(headers.indexOf("Old Transaction Ref")).replace("\"", ""));
                    ldresp.setOriginalLoanAmount(result.get(i).get(headers.indexOf("Original Loan Amt")).replace("\"", ""));
                    ldresp.setOriginalDisbursedAmount(result.get(i).get(headers.indexOf("Original Disbus Amt")).replace("\"", ""));
                    ldresp.setIntroducer(result.get(i).get(headers.indexOf("Introducer")).replace("\"", ""));
                    ldresp.setLoanDisbursedAmount(result.get(i).get(headers.indexOf("Loan Disbursed Amount")).replace("\"", ""));
                    alllt.add(ldresp);
                }

            } else {
                dresp = ResponseCodes.Security_violation;
                LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
                ldresp.setResponseCode(dresp.getCode());
                ldresp.setResponseText(dresp.getMessage());
                ldresp.setTransactionDate(zzdf.format(today));
                alllt.add(ldresp);
                weblogger.error(alllt);
                return alllt;
            }
            //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
        } catch (Exception d) {
            LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
            dresp = ResponseCodes.Invalid_transaction;
            ldresp.setResponseCode(dresp.getCode());
            ldresp.setResponseText(d.toString());
            ldresp.setTransactionDate(zzdf.format(today));
            alllt.add(ldresp);
            weblogger.fatal(ldresp, d);
        }
        return alllt;
    }

    @WebMethod(operationName = "SnapAccountReq")
    public SnapAccountResponse SnapAccountReq(@WebParam(name = "SnapAccountRequest") SnapAccountRequest snp) throws Exception {

        String SnapID = snp.getSnapID();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        SnapAccountResponse saresp = new SnapAccountResponse();
        weblogger.info("SnapAccountEnquiry");

        try {
            String APIKEY = snp.getApikey();

            String AUTHID = snp.getAuthenticationID();

            String APPLICATIONID = snp.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;

                saresp.setResponseCode(dresp.getCode());
                saresp.setResponseText(dresp.getMessage());
                saresp.setTransactionDate(zzdf.format(today));

                weblogger.error(saresp);
                return saresp;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    saresp.setResponseCode(dresp.getCode());
                    saresp.setResponseText(dresp.getMessage());
                    saresp.setTransactionDate(zzdf.format(today));

                    weblogger.error(saresp);
                    return saresp;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;

                    saresp.setResponseCode(dresp.getCode());
                    saresp.setResponseText(dresp.getMessage());
                    saresp.setTransactionDate(zzdf.format(today));

                    weblogger.error(saresp);
                    return saresp;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;

                saresp.setResponseCode(dresp.getCode());
                saresp.setResponseText(dresp.getMessage());
                saresp.setTransactionDate(zzdf.format(today));

                weblogger.error(saresp);
                return saresp;
            }
            String stringtohash = snp.getSnapID() + APIKEY;

            String requesthash = snp.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {
                ArrayList<List<String>> result = t24.getOfsData("SNAP.AC", Ofsuser, Ofspass, "SNAP.ID:EQ=" + SnapID.trim());
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                dresp = ResponseCodes.SUCCESS;
                saresp.setResponseCode(dresp.getCode());
                saresp.setResponseText(dresp.getMessage());
                saresp.setTransactionDate(zzdf.format(today));
                saresp.setT24AccountNumber(result.get(1).get(headers.indexOf("T24 ACCOUNT NUMBER")).replace("\"", ""));
                saresp.setT24Cif(result.get(1).get(headers.indexOf("T24 CIF")).replace("\"", ""));
                saresp.setCategory(result.get(1).get(headers.indexOf("CATEGORY")).replace("\"", ""));
                saresp.setAccountName(result.get(1).get(headers.indexOf("ACCOUNT NAME")).replace("\"", ""));
                saresp.setSnapID(result.get(1).get(headers.indexOf("SNAP ID")).replace("\"", ""));
                saresp.setSnapInputter(result.get(1).get(headers.indexOf("SNAP INPUTTER")).replace("\"", ""));
                saresp.setSnapAuthoriser(result.get(1).get(headers.indexOf("SNAP AUTHORISER")).replace("\"", ""));

                //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
            } else {
                dresp = ResponseCodes.Security_violation;

                saresp.setResponseCode(dresp.getCode());
                saresp.setResponseText(dresp.getMessage());
                saresp.setTransactionDate(zzdf.format(today));

                weblogger.error(saresp);
                return saresp;
            }
        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            saresp.setResponseCode(dresp.getCode());
            saresp.setResponseText(d.toString());
            saresp.setTransactionDate(zzdf.format(today));
            weblogger.fatal(saresp, d);

        }
        return saresp;
    }

    @WebMethod(operationName = "StmtEntBook")
    public List<StmtResponse> StmtEntBook(@WebParam(name = "StmtEntBookRequest") StmtEntBookRequest stmt) throws Exception {

        String act = stmt.getAccountNo();
        String sd = stmt.getStartDate();
        String ed = stmt.getEndDate();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        List<StmtResponse> allstmt = new ArrayList<>();
        weblogger.info("StmtEntBookRequest");

        try {
            String APIKEY = stmt.getApikey();

            String AUTHID = stmt.getAuthenticationID();

            String APPLICATIONID = stmt.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;
                StmtResponse seresp = new StmtResponse();
                seresp.setResponseCode(dresp.getCode());
                seresp.setResponseText(dresp.getMessage());
                seresp.setTransactionDate(zzdf.format(today));
                allstmt.add(seresp);
                weblogger.error(allstmt);
                return allstmt;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    StmtResponse seresp = new StmtResponse();
                    seresp.setResponseCode(dresp.getCode());
                    seresp.setResponseText(dresp.getMessage());
                    seresp.setTransactionDate(zzdf.format(today));
                    allstmt.add(seresp);
                    weblogger.error(allstmt);
                    return allstmt;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    StmtResponse seresp = new StmtResponse();
                    seresp.setResponseCode(dresp.getCode());
                    seresp.setResponseText(dresp.getMessage());
                    seresp.setTransactionDate(zzdf.format(today));
                    allstmt.add(seresp);
                    weblogger.error(allstmt);
                    return allstmt;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;
                StmtResponse seresp = new StmtResponse();
                seresp.setResponseCode(dresp.getCode());
                seresp.setResponseText(dresp.getMessage());
                seresp.setTransactionDate(zzdf.format(today));
                allstmt.add(seresp);
                weblogger.error(allstmt);
                return allstmt;
            }

            String stringtohash = stmt.getAccountNo() + stmt.getStartDate() + stmt.getEndDate() + APIKEY;

            String requesthash = stmt.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
                Date Start = sdf.parse(sd);
                Date End = sdf.parse(ed);

                sd = ndf.format(Start);
                ed = ndf.format(End);
                ArrayList<List<String>> result = t24.getOfsData("STMT.ENT.BOOK3", Ofsuser, Ofspass, "ACCOUNT.NUMBER:EQ=" + act.trim() + ",VALUE.DATE:RG=" + sd.trim() + " " + ed.trim());
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                for (int i = 1; i < result.size(); i++) {
                    StmtResponse seresp = new StmtResponse();
                    dresp = ResponseCodes.SUCCESS;
                    seresp.setResponseCode(dresp.getCode());
                    seresp.setResponseText(dresp.getMessage());
                    seresp.setTransactionDate(zzdf.format(today));
                    seresp.setBookingDate(result.get(i).get(headers.indexOf("Booking Date")).replace("\"", ""));
                    seresp.setReference(result.get(i).get(headers.indexOf("Reference")).replace("\"", ""));
                    seresp.setDescription(result.get(i).get(headers.indexOf("Descriptions")).replace("\"", ""));
                    seresp.setValueDate(result.get(i).get(headers.indexOf("Value Date")).replace("\"", ""));
                    seresp.setDebitAmount(result.get(i).get(headers.indexOf("Debit")).replace("\"", ""));
                    seresp.setCreditAmount(result.get(i).get(headers.indexOf("Credit")).replace("\"", ""));
                    seresp.setClosingBalance(result.get(i).get(headers.indexOf("Closing Balance")).replace("\"", ""));
                    allstmt.add(seresp);
                }

                //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
            } else {
                dresp = ResponseCodes.Security_violation;
                StmtResponse seresp = new StmtResponse();
                seresp.setResponseCode(dresp.getCode());
                seresp.setResponseText(dresp.getMessage());
                seresp.setTransactionDate(zzdf.format(today));
                allstmt.add(seresp);
                weblogger.error(allstmt);
                return allstmt;
            }
        } catch (Exception d) {
            StmtResponse seresp = new StmtResponse();
            dresp = ResponseCodes.Invalid_transaction;
            seresp.setResponseCode(dresp.getCode());
            seresp.setResponseText(d.toString());
            seresp.setTransactionDate(zzdf.format(today));
            allstmt.add(seresp);
            weblogger.fatal(seresp, d);
        }
        return allstmt;
    }

    @WebMethod(operationName = "StmtEntBookNostroReq")
    public List<StmtEntBookNostroResponse> StmtEntBookNostroReq(@WebParam(name = "StmtEntBookNostroRepay") StmtEntBookNostroRepay stnos) throws Exception {

        String acc = stnos.getAccountNo();
        String sd = stnos.getStartDate();
        String ed = stnos.getEnddate();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        List<StmtEntBookNostroResponse> allstnos = new ArrayList<>();
        weblogger.info("StmtEntBookNostroRepay");

        try {
            String APIKEY = stnos.getApikey();

            String AUTHID = stnos.getAuthenticationID();

            String APPLICATIONID = stnos.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;
                StmtEntBookNostroResponse sebresp = new StmtEntBookNostroResponse();
                sebresp.setResponseCode(dresp.getCode());
                sebresp.setResponseText(dresp.getMessage());
                sebresp.setTransactionDate(zzdf.format(today));
                allstnos.add(sebresp);
                weblogger.error(allstnos);
                return allstnos;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    StmtEntBookNostroResponse sebresp = new StmtEntBookNostroResponse();
                    sebresp.setResponseCode(dresp.getCode());
                    sebresp.setResponseText(dresp.getMessage());
                    sebresp.setTransactionDate(zzdf.format(today));
                    allstnos.add(sebresp);
                    weblogger.error(allstnos);
                    return allstnos;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    StmtEntBookNostroResponse sebresp = new StmtEntBookNostroResponse();
                    sebresp.setResponseCode(dresp.getCode());
                    sebresp.setResponseText(dresp.getMessage());
                    sebresp.setTransactionDate(zzdf.format(today));
                    allstnos.add(sebresp);
                    weblogger.error(allstnos);
                    return allstnos;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;
                StmtEntBookNostroResponse sebresp = new StmtEntBookNostroResponse();
                sebresp.setResponseCode(dresp.getCode());
                sebresp.setResponseText(dresp.getMessage());
                sebresp.setTransactionDate(zzdf.format(today));
                allstnos.add(sebresp);
                weblogger.error(allstnos);
                return allstnos;
            }

            String stringtohash = stnos.getAccountNo() + stnos.getStartDate() + stnos.getEnddate() + APIKEY;

            String requesthash = stnos.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
                Date Start = sdf.parse(sd);
                Date End = sdf.parse(ed);

                sd = ndf.format(Start);
                ed = ndf.format(End);
                ArrayList<List<String>> result = t24.getOfsData("STMT.ENT.BOOK.NOSTRO.REPAY.22", Ofsuser, Ofspass, "ACCOUNT.NUMBER:EQ=" + acc.trim() + ",VALUE.DATE:RG=" + sd + " " + ed);
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                for (int i = 1; i < result.size(); i++) {
                    StmtEntBookNostroResponse sebresp = new StmtEntBookNostroResponse();
                    dresp = ResponseCodes.SUCCESS;
                    sebresp.setResponseCode(dresp.getCode());
                    sebresp.setResponseText(dresp.getMessage());
                    sebresp.setTransactionDate(zzdf.format(today));
                    sebresp.setBookingDate(result.get(i).get(headers.indexOf("Booking Date")).replace("\"", ""));
                    sebresp.setReference(result.get(i).get(headers.indexOf("Reference")).replace("\"", ""));
                    sebresp.setDescription(result.get(i).get(headers.indexOf("Description")).replace("\"", ""));
                    sebresp.setNarrative(result.get(i).get(headers.indexOf("Narrative")).replace("\"", ""));
                    sebresp.setCreditAcctNumber(result.get(i).get(headers.indexOf("Credit Acct Name")).replace("\"", ""));
                    sebresp.setValueDate(result.get(i).get(headers.indexOf("Value Date")).replace("\"", ""));
                    sebresp.setDebitAmount(result.get(i).get(headers.indexOf("Debit")).replace("\"", ""));
                    sebresp.setCreditAmount(result.get(i).get(headers.indexOf("Credit")).replace("\"", ""));
                    sebresp.setClosingBalance(result.get(i).get(headers.indexOf("Closing Balance")).replace("\"", ""));
                    allstnos.add(sebresp);
                }

            } else {
                dresp = ResponseCodes.Security_violation;
                StmtEntBookNostroResponse sebresp = new StmtEntBookNostroResponse();
                sebresp.setResponseCode(dresp.getCode());
                sebresp.setResponseText(dresp.getMessage());
                sebresp.setTransactionDate(zzdf.format(today));
                allstnos.add(sebresp);
                weblogger.error(allstnos);
                return allstnos;
            }

            //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
        } catch (Exception d) {
            StmtEntBookNostroResponse sebresp = new StmtEntBookNostroResponse();
            dresp = ResponseCodes.Invalid_transaction;
            sebresp.setResponseCode(dresp.getCode());
            sebresp.setResponseText(d.toString());
            sebresp.setTransactionDate(zzdf.format(today));
            allstnos.add(sebresp);
            weblogger.fatal(sebresp, d);

        }
        return allstnos;
    }

    @WebMethod(operationName = "AccountStatement")
    public List<StatementResp> AccountStatement(@WebParam(name = "StatementRequest") StatementRequest stm) throws Exception {
        String acct = stm.getAccountNo();
        String sd = stm.getStartDate();
        String ed = stm.getEndDate();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        List<StatementResp> allstm = new ArrayList<>();
        weblogger.info("AccountStatement");

        try {
            String APIKEY = stm.getApikey();

            String AUTHID = stm.getAuthenticationID();

            String APPLICATIONID = stm.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {

                dresp = ResponseCodes.Credentials_missing_or_null;
                StatementResp stmresp = new StatementResp();
                stmresp.setResponseCode(dresp.getCode());
                stmresp.setResponseText(dresp.getMessage());
                stmresp.setTransactionDate(zzdf.format(today));
                allstm.add(stmresp);
                return allstm;
            }
//            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
//            if (rs.next()) {
//                apikey = rs.getString("APIKey");
//                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
//                if (!apikey.trim().equals(receivedapikey.trim())) {
//                    dresp = ResponseCodes.Credentials_Encryption_Error;
//                    StatementResp stmresp = new StatementResp();
//                    stmresp.setResponseCode(dresp.getCode());
//                    stmresp.setResponseText(dresp.getMessage());
//                    stmresp.setTransactionDate(zzdf.format(today));
//                    allstm.add(stmresp);
//                    weblogger.error(allstm);
//                    return allstm;
//                }
//
//                String authid = rs.getString("AuthenticationID");
//                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
//                if (!authid.trim().equals(receivedauthID.trim())) {
//                    dresp = ResponseCodes.Credentials_Encryption_Error;
//                    StatementResp stmresp = new StatementResp();
//                    stmresp.setResponseCode(dresp.getCode());
//                    stmresp.setResponseText(dresp.getMessage());
//                    stmresp.setTransactionDate(zzdf.format(today));
//                    allstm.add(stmresp);
//                    weblogger.error(allstm);
//                    return allstm;
//                }
//            } else {
//                dresp = ResponseCodes.Invalid_Sender;
//                StatementResp stmresp = new StatementResp();
//                stmresp.setResponseCode(dresp.getCode());
//                stmresp.setResponseText(dresp.getMessage());
//                stmresp.setTransactionDate(zzdf.format(today));
//                allstm.add(stmresp);
//                weblogger.error(allstm);
//                return allstm;
//            }
//
           String stringtohash = stm.getAccountNo() + stm.getStartDate() + stm.getEndDate() + APIKEY;

            String requesthash = stm.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
                Date Start = sdf.parse(sd);
                Date End = sdf.parse(ed);

                sd = ndf.format(Start);
                ed = ndf.format(End);
                ArrayList<List<String>> result = t24.getOfsData("STMT.ENT.BOOK.CUSTOMISED.2", Ofsuser, Ofspass, "ACCT.ID:EQ=" + acct.trim() + ",VALUE.DATE:RG=" + sd + " " + ed);
                result.remove(1);
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                for (int i = 1; i < result.size(); i++) {
                    StatementResp stmresp = new StatementResp();
                    dresp = ResponseCodes.SUCCESS;
                    stmresp.setResponseCode(dresp.getCode());
                    stmresp.setResponseText(dresp.getMessage());
                    stmresp.setTransactionDate(zzdf.format(today));
                    stmresp.setAccountNumber(result.get(i).get(headers.indexOf("Account No")).replace("\"", ""));
                    stmresp.setCIF(result.get(i).get(headers.indexOf("Cif")).replace("\"", ""));
                    stmresp.setCustomerName(result.get(i).get(headers.indexOf("Customer Name")).replace("\"", ""));
                    stmresp.setBookingDate(result.get(i).get(headers.indexOf("Booking Date")).replace("\"", ""));
                    stmresp.setValueDate(result.get(i).get(headers.indexOf("Value Date")).replace("\"", ""));
                    stmresp.setDescription(result.get(i).get(headers.indexOf("Description")).replace("\"", ""));
                    stmresp.setNarrative(result.get(i).get(headers.indexOf("NARRATIVE")).replace("\"", ""));
                    stmresp.setDebitAmount(result.get(i).get(headers.indexOf("Debit")).replace("\"", ""));
                    stmresp.setCreditAmount(result.get(i).get(headers.indexOf("Credit")).replace("\"", ""));
                    stmresp.setClosingBalance(result.get(i).get(headers.indexOf("Closing Balance")).replace("\"", ""));
                    allstm.add(stmresp);
                }

                //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
            } else {
                dresp = ResponseCodes.Security_violation;
                StatementResp stmresp = new StatementResp();
                stmresp.setResponseCode(dresp.getCode());
                stmresp.setResponseText(dresp.getMessage());
                stmresp.setTransactionDate(zzdf.format(today));
                allstm.add(stmresp);
                weblogger.error(allstm);
                return allstm;
            }
        } catch (Exception d) {
            StatementResp stmresp = new StatementResp();
            dresp = ResponseCodes.Invalid_transaction;
            stmresp.setResponseCode(dresp.getCode());
            stmresp.setResponseText(d.toString());
            stmresp.setTransactionDate(zzdf.format(today));
            allstm.add(stmresp);
            weblogger.fatal(stmresp, d);

        }
        return allstm;
    }

    @WebMethod(operationName = "CustomerDetails")
    public CustomerDetailResponse CustomerDetails(@WebParam(name = "CustomerDetailsRequest") CustomerDetailsRequest cusreq) throws Exception {

        String cus = cusreq.getCustomerNo();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        CustomerDetailResponse curesp = new CustomerDetailResponse();
        weblogger.info("CustomerDetails");

        try {
            String APIKEY = cusreq.getApikey();

            String AUTHID = cusreq.getAuthenticationID();

            String APPLICATIONID = cusreq.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {
                dresp = ResponseCodes.Credentials_missing_or_null;
                curesp.setResponseCode(dresp.getCode());
                curesp.setResponseText(dresp.getMessage());
                curesp.setTransactionDate(zzdf.format(today));
                weblogger.error(curesp);
                return curesp;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);
            if (rs.next()) {
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    curesp.setResponseCode(dresp.getCode());
                    curesp.setResponseText(dresp.getMessage());
                    curesp.setTransactionDate(zzdf.format(today));
                    weblogger.error(curesp);
                    return curesp;
                }

                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;

                    curesp.setResponseCode(dresp.getCode());
                    curesp.setResponseText(dresp.getMessage());
                    curesp.setTransactionDate(zzdf.format(today));

                    weblogger.error(curesp);
                    return curesp;
                }
            } else {
                dresp = ResponseCodes.Invalid_Sender;

                curesp.setResponseCode(dresp.getCode());
                curesp.setResponseText(dresp.getMessage());
                curesp.setTransactionDate(zzdf.format(today));

                weblogger.error(curesp);
                return curesp;
            }
            String stringtohash = cusreq.getCustomerNo() + APIKEY;

            String requesthash = cusreq.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {

                ArrayList<List<String>> result = t24.getOfsData("EM.GIC.MEMBER.LIST.3", Ofsuser, Ofspass, "CUSTOMER.CODE:EQ=" + cus.trim());
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }

                dresp = ResponseCodes.SUCCESS;
                curesp.setResponseCode(dresp.getCode());
                curesp.setResponseText(dresp.getMessage());
                curesp.setTransactionDate(zzdf.format(today));
                curesp.setCustomerNumber(result.get(1).get(headers.indexOf("Customer No")).replace("\"", ""));
                curesp.setMnemonic(result.get(1).get(headers.indexOf("Mnemonic")).replace("\"", ""));
                curesp.setSurname(result.get(1).get(headers.indexOf("Surname")).replace("\"", ""));
                curesp.setFirstName(result.get(1).get(headers.indexOf("First Name")).replace("\"", ""));
                curesp.setPreferredName(result.get(1).get(headers.indexOf("Prefered Name")).replace("\"", ""));
                curesp.setAddress(result.get(1).get(headers.indexOf("Address")).replace("\"", ""));
                curesp.setStatus(result.get(1).get(headers.indexOf("Status")).replace("\"", ""));
                curesp.setOfficer(result.get(1).get(headers.indexOf("Officer")).replace("\"", ""));
                curesp.setAccountOfficer(result.get(1).get(headers.indexOf("Account Officer")).replace("\"", ""));
                curesp.setAccountMnemonic(result.get(1).get(headers.indexOf("Account Mnemonic")).replace("\"", ""));
                curesp.setAge(result.get(1).get(headers.indexOf("Age")).replace("\"", ""));
                curesp.setEmail(result.get(1).get(headers.indexOf("Email")).replace("\"", ""));
                curesp.setTelephoneNo(result.get(1).get(headers.indexOf("Telephone")).replace("\"", ""));
                curesp.setEmployerName(result.get(1).get(headers.indexOf("EMPLOYER\'S NAME")).replace("\"", ""));
                curesp.setEmployerAddress(result.get(1).get(headers.indexOf("EMPLOYER\'S ADDRESS")).replace("\"", ""));
                curesp.setNextofKinName(result.get(1).get(headers.indexOf("Next of Kin Name")).replace("\"", ""));
                curesp.setNextofKinTelephonenumber(result.get(1).get(headers.indexOf("Next of Kin Tel Num")).replace("\"", ""));
                curesp.setCustomerBVN(result.get(1).get(headers.indexOf("Customer BVN No")).replace("\"", ""));
                curesp.setGuarantor(result.get(1).get(headers.indexOf("Guarantor")).replace("\"", ""));
                curesp.setGender(result.get(1).get(headers.indexOf("Gender")).replace("\"", ""));
                curesp.setBVNNumber(result.get(1).get(headers.indexOf("BVN.NUMBER")).replace("\"", ""));
                curesp.setHomeAddress(result.get(1).get(headers.indexOf("Home Address")).replace("\"", ""));
                curesp.setLoanType(result.get(1).get(headers.indexOf("Loan Type")).replace("\"", ""));

                //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
            } else {
                dresp = ResponseCodes.Security_violation;

                curesp.setResponseCode(dresp.getCode());
                curesp.setResponseText(dresp.getMessage());
                curesp.setTransactionDate(zzdf.format(today));

                weblogger.error(curesp);
                return curesp;
            }

        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            curesp.setResponseCode(dresp.getCode());
            curesp.setResponseText(d.toString());
            curesp.setTransactionDate(zzdf.format(today));
            weblogger.fatal(curesp, d);

        }
        return curesp;
    }

    @WebMethod(operationName = "CustomerDetailsList")
    public List<CustomerDetailResponse> CustomerDetailsList(@WebParam(name = "CustomerDetailsRequest") CustomerDetailsRequest cusreq) throws Exception {

        String sd = cusreq.getStartdate();
        String ed = cusreq.getEnddate();
        SimpleDateFormat zzdf;
        zzdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date today = new Date();
        List<CustomerDetailResponse> allcuresp = new ArrayList<>();

        weblogger.info("CustomerDetails");

        try {
            String APIKEY = cusreq.getApikey();

            String AUTHID = cusreq.getAuthenticationID();

            String APPLICATIONID = cusreq.getApplicationID();

            if (isNullOrEmpty(AUTHID) == true || isNullOrEmpty(APIKEY) == true || isNullOrEmpty(APPLICATIONID) == true) {
                CustomerDetailResponse curesp = new CustomerDetailResponse();
                dresp = ResponseCodes.Credentials_missing_or_null;
                curesp.setResponseCode(dresp.getCode());
                curesp.setResponseText(dresp.getMessage());
                curesp.setTransactionDate(zzdf.format(today));
                weblogger.error(curesp);
                allcuresp.add(curesp);
                return allcuresp;
            }
            ResultSet rs = db.getData("select * from PrimeraClients where ApplicationID = '" + APPLICATIONID.trim() + "';", conn);

            if (rs.next()) {
                CustomerDetailResponse curesp = new CustomerDetailResponse();
                apikey = rs.getString("APIKey");
                String receivedapikey = options.get_SHA_512_Hash(APIKEY, APIKEY);
                if (!apikey.trim().equals(receivedapikey.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    curesp.setResponseCode(dresp.getCode());
                    curesp.setResponseText(dresp.getMessage());
                    curesp.setTransactionDate(zzdf.format(today));
                    weblogger.error(curesp);
                    allcuresp.add(curesp);
                    return allcuresp;
                }
                String authid = rs.getString("AuthenticationID");
                String receivedauthID = options.get_SHA_512_Hash(AUTHID, AUTHID);
                if (!authid.trim().equals(receivedauthID.trim())) {
                    dresp = ResponseCodes.Credentials_Encryption_Error;
                    curesp.setResponseCode(dresp.getCode());
                    curesp.setResponseText(dresp.getMessage());
                    curesp.setTransactionDate(zzdf.format(today));

                    weblogger.error(curesp);
                    allcuresp.add(curesp);
                    return allcuresp;
                }
            } else {
                CustomerDetailResponse curesp = new CustomerDetailResponse();
                dresp = ResponseCodes.Invalid_Sender;

                curesp.setResponseCode(dresp.getCode());
                curesp.setResponseText(dresp.getMessage());
                curesp.setTransactionDate(zzdf.format(today));

                weblogger.error(curesp);
                allcuresp.add(curesp);
                return allcuresp;
            }
            String stringtohash = cusreq.getStartdate() + cusreq.getEnddate() + APIKEY;

            String requesthash = cusreq.getHash();

            //String hash = options.generateBCrypthash(stringtohash);
            boolean matched = BCrypt.checkpw(stringtohash, requesthash);
            if (matched == true) {
                SimpleDateFormat sdfss = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat ndfss = new SimpleDateFormat("yyyyMMdd");
                Date Start = sdfss.parse(sd);
                Date End = sdfss.parse(ed);

                sd = ndfss.format(Start);
                ed = ndfss.format(End);
                ArrayList<List<String>> result = t24.getOfsData("EM.GIC.MEMBER.LIST.3", Ofsuser, Ofspass, "OPENING.DATE:RG=" + sd.trim() + " " + ed.trim());

                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                for (int i = 1; i < result.size(); i++) {
                    CustomerDetailResponse curesp = new CustomerDetailResponse();
                    dresp = ResponseCodes.SUCCESS;
                    curesp.setResponseCode(dresp.getCode());
                    curesp.setResponseText(dresp.getMessage());
                    curesp.setTransactionDate(zzdf.format(today));
                    curesp.setCustomerNumber(result.get(i).get(headers.indexOf("Customer No")).replace("\"", ""));
                    curesp.setMnemonic(result.get(i).get(headers.indexOf("Mnemonic")).replace("\"", ""));
                    curesp.setSurname(result.get(i).get(headers.indexOf("Surname")).replace("\"", ""));
                    curesp.setFirstName(result.get(i).get(headers.indexOf("First Name")).replace("\"", ""));
                    curesp.setPreferredName(result.get(i).get(headers.indexOf("Prefered Name")).replace("\"", ""));
                    curesp.setAddress(result.get(i).get(headers.indexOf("Address")).replace("\"", ""));
                    curesp.setStatus(result.get(i).get(headers.indexOf("Status")).replace("\"", ""));
                    curesp.setOfficer(result.get(i).get(headers.indexOf("Officer")).replace("\"", ""));
                    curesp.setAccountOfficer(result.get(i).get(headers.indexOf("Account Officer")).replace("\"", ""));
                    curesp.setAccountMnemonic(result.get(i).get(headers.indexOf("Account Mnemonic")).replace("\"", ""));
                    curesp.setAge(result.get(i).get(headers.indexOf("Age")).replace("\"", ""));
                    curesp.setEmail(result.get(i).get(headers.indexOf("Email")).replace("\"", ""));
                    curesp.setTelephoneNo(result.get(i).get(headers.indexOf("Telephone")).replace("\"", ""));
                    curesp.setEmployerName(result.get(i).get(headers.indexOf("EMPLOYER\'S NAME")).replace("\"", ""));
                    curesp.setEmployerAddress(result.get(i).get(headers.indexOf("EMPLOYER\'S ADDRESS")).replace("\"", ""));
                    curesp.setNextofKinName(result.get(i).get(headers.indexOf("Next of Kin Name")).replace("\"", ""));
                    curesp.setNextofKinTelephonenumber(result.get(i).get(headers.indexOf("Next of Kin Tel Num")).replace("\"", ""));
                    curesp.setCustomerBVN(result.get(i).get(headers.indexOf("Customer BVN No")).replace("\"", ""));
                    curesp.setGuarantor(result.get(i).get(headers.indexOf("Guarantor")).replace("\"", ""));
                    curesp.setGender(result.get(i).get(headers.indexOf("Gender")).replace("\"", ""));
                    curesp.setBVNNumber(result.get(i).get(headers.indexOf("BVN.NUMBER")).replace("\"", ""));
                    curesp.setHomeAddress(result.get(i).get(headers.indexOf("Home Address")).replace("\"", ""));
                    curesp.setLoanType(result.get(i).get(headers.indexOf("Loan Type")).replace("\"", ""));
                    allcuresp.add(curesp);

                }

                //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
            } else {
                CustomerDetailResponse curesp = new CustomerDetailResponse();
                dresp = ResponseCodes.Security_violation;

                curesp.setResponseCode(dresp.getCode());
                curesp.setResponseText(dresp.getMessage());
                curesp.setTransactionDate(zzdf.format(today));

                weblogger.error(curesp);
                allcuresp.add(curesp);
                return allcuresp;
            }
        } catch (Exception d) {
            CustomerDetailResponse curesp = new CustomerDetailResponse();
            dresp = ResponseCodes.Invalid_transaction;
            curesp.setResponseCode(dresp.getCode());
            curesp.setResponseText(d.toString());
            curesp.setTransactionDate(zzdf.format(today));
            weblogger.fatal(curesp, d);
            allcuresp.add(curesp);

        }

        return allcuresp;
    }
}
