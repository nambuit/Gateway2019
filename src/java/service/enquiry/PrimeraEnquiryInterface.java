/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.enquiry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.naming.InitialContext;
import org.apache.log4j.Logger;
import primera.service.T24Link;
import prm.tools.AppParams;
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
//    Enquiry enq = new Enquiry();
//    String ID = enq.getID();
//    String VDate = enq.getValueDate();
//    String BookDate = enq.getBookingDate();
//    String ProcessDate = enq.getProcessingDate();
    public String APIKey = "4466FA2C-1886-4366-B014-AD140712BE38";

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

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @WebMethod(operationName = "FTHistory")
    public List<FtHistResponse> FTHistory(@WebParam(name = "FTHistory") FtHistRequest fthistory) throws Exception {

        String acc = fthistory.getAccountNumber();
        String crdr = fthistory.getCreditORdebit();
        List<FtHistResponse> allfth = new ArrayList<>();

        ArrayList<List<String>> result = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        weblogger.info("FTHistory");

        try {
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

        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            FtHistResponse fth = new FtHistResponse();
            fth.setResponseCode(dresp.getCode());
            fth.setResponseText(dresp.getMessage());
            fth.setMessage(d.getMessage());
            allfth.add(fth);
            weblogger.fatal(allfth, d);

        }
        return allfth;
    }

    @WebMethod(operationName = "FTNarrateReq")
    public List<FtNarrateResponse> FTNarrateReq(@WebParam(name = "FtNarrateRequest") FtNarrateRequest ftnreq) throws Exception {
//      
        String acc = ftnreq.getAccountNumber();
        String crdr = ftnreq.getCreditORdebit();
        ArrayList<List<String>> result = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        List<FtNarrateResponse> allftn = new ArrayList<>();
        weblogger.info("FtNarrateRequest");

        try {
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

        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            FtNarrateResponse ftn = new FtNarrateResponse();
            ftn.setResponseCode(dresp.getCode());
            ftn.setResponseText(dresp.getMessage());
            ftn.setMessage(d.getMessage());
            allftn.add(ftn);
            weblogger.fatal(allftn, d);

        }

        return allftn;
    }

    @WebMethod(operationName = "CustomerAmend")
    public CustomerNAUAmendResponse CustomerAmend(@WebParam(name = "CustomerAmendRequest") CustomerNauAmendRequest cuamend) throws Exception {

        String cusno = cuamend.getCustomerNo();

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
            cumresp.setResponseText(dresp.getMessage());
            weblogger.fatal(cumresp, d);

        }

        return cumresp;

    }

    @WebMethod(operationName = "AccountDetailsReq")
    public AccountDetailsResponse AccountDetailsReq(@WebParam(name = "AccountNumber") AccountDetailsRequest acdetails) throws Exception {

        String accNo = acdetails.getAccountNo();
        AccountDetailsResponse acresp = new AccountDetailsResponse();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//        SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
        weblogger.info("AccountDetailsRequest");

        try {

            ArrayList<List<String>> result = t24.getDOfsData("ACCOUNT.DETAILS.2", Ofsuser, Ofspass, "ACCOUNT.NUMBER:EQ=" + accNo.trim());
            List<String> headers = result.get(0);

            if (headers.size() != result.get(1).size()) {

                throw new Exception(result.get(1).get(0));

            }
            dresp = ResponseCodes.SUCCESS;
            acresp.setResponseCode(dresp.getCode());
            acresp.setResponseText(dresp.getMessage());
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
        } catch (Exception d) {

            dresp = ResponseCodes.Invalid_transaction;
            acresp.setResponseCode(dresp.getCode());
            acresp.setResponseText(dresp.getMessage());
            acresp.setMessage(d.getMessage());
            weblogger.fatal(acresp, d);

        }

        return acresp;
    }

    @WebMethod(operationName = "AccountBalance")
    public AcctBalResponse AccountBalance(@WebParam(name = "AccountNumber") AcctBalRequest accbal) throws Exception {

        String acctNo = accbal.getAccountNo();
        AcctBalResponse accresp = new AcctBalResponse();
        weblogger.info("AccountBalanceRequest");

        try {

            ArrayList<List<String>> result = t24.getOfsData("ACCT.BAL.2", Ofsuser, Ofspass, "ACCOUNT.NUMBER:EQ=" + acctNo.trim());
            List<String> headers = result.get(0);

            if (headers.size() != result.get(1).size()) {

                throw new Exception(result.get(1).get(0));

            }
            dresp = ResponseCodes.SUCCESS;
            accresp.setResponseCode(dresp.getCode());
            accresp.setResponseText(dresp.getMessage());
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

        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            accresp.setResponseCode(dresp.getCode());
            accresp.setResponseText(dresp.getMessage());
            accresp.setMessage(d.getMessage());
            weblogger.fatal(accresp, d);

        }
        return accresp;
    }

    @WebMethod(operationName = "PandLCategEntries")
    public List<CategEntBookResponse> PandLCategEntries(@WebParam(name = "PLCategory") String PLCategory, @WebParam(name = "StartDate") String startdate, @WebParam(name = "EndDate") String enddate, @WebParam(name = "hash") String hash) throws Exception {

        List<CategEntBookResponse> allctn = new ArrayList<>();

        weblogger.info("CategEntryRequest");

        try {

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
                ctresp.setBookingDate(result.get(i).get(headers.indexOf("Booking Date")).replace("\"", ""));
                ctresp.setNarration(result.get(i).get(headers.indexOf("Narration")).replace("\"", ""));
                ctresp.setRefNo(result.get(i).get(headers.indexOf("Ref No")).replace("\"", ""));
                ctresp.setValueDate(result.get(i).get(headers.indexOf("Value Date")).replace("\"", ""));
                ctresp.setDebit(result.get(i).get(headers.indexOf("Debit")).replace("\"", ""));
                ctresp.setCredit(result.get(i).get(headers.indexOf("Credit")).replace("\"", ""));
                ctresp.setClosingBalance(result.get(i).get(headers.indexOf("Closing Balance")).replace("\"", ""));
                allctn.add(ctresp);
            }

        } catch (Exception d) {
            CategEntBookResponse ctresp = new CategEntBookResponse();
            dresp = ResponseCodes.Invalid_transaction;
            ctresp.setResponseCode(dresp.getCode());
            ctresp.setResponseText(dresp.getMessage());
            ctresp.setMessage(d.getMessage());
            allctn.add(ctresp);
            weblogger.fatal(ctresp, d);

        }
        return allctn;
    }

    @WebMethod(operationName = "ICLChequeReq")
    public List<IclChequeResponse> ICLChequeReq(@WebParam(name = "ICLChequeRequest") IclChequeRequest dayreq) throws Exception {

        String icl = dayreq.getT24AcctNo();
        List<IclChequeResponse> allicl = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
        weblogger.info("ICLChequeRequest");

        try {

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

        } catch (Exception d) {
            IclChequeResponse dayresp = new IclChequeResponse();
            dresp = ResponseCodes.Invalid_transaction;
            dayresp.setResponseCode(dresp.getCode());
            dayresp.setResponseText(dresp.getMessage());
            dayresp.setMessage(d.getMessage());
            allicl.add(dayresp);
            weblogger.fatal(dayresp, d);

        }
        return allicl;
    }

    @WebMethod(operationName = "DailyExpectedReq")
    public DailyExpectedResponse DailyExpectedReq(@WebParam(name = "DailyExpectedRequest") DailyExpectedRequest dayreq) throws Exception {

        String day = dayreq.getID();
        DailyExpectedResponse dayresp = new DailyExpectedResponse();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
        weblogger.info("DailyExpectedRequest");

        try {

            ArrayList<List<String>> result = t24.getOfsData("DAILY.EXPECTED.1", Ofsuser, Ofspass, "@ID:EQ=" + day.trim());
            List<String> headers = result.get(0);

            if (headers.size() != result.get(1).size()) {

                throw new Exception(result.get(1).get(0));

            }
            dresp = ResponseCodes.SUCCESS;
            dayresp.setResponseCode(dresp.getCode());
            dayresp.setResponseText(dresp.getMessage());
            dayresp.setID(result.get(1).get(headers.indexOf("ID")).replace("\"", ""));
            dayresp.setType(result.get(1).get(headers.indexOf("TYPE")).replace("\"", ""));
            dayresp.setRepaymentDate(result.get(1).get(headers.indexOf("REPAYMENT DATE")).replace("\"", ""));
            dayresp.setPrinAmount(result.get(1).get(headers.indexOf("PRIN AMOUNT")).replace("\"", ""));
            dayresp.setInterestRepay(result.get(1).get(headers.indexOf("INTEREST REPAY")).replace("\"", ""));
            dayresp.setTotalRepayment(result.get(1).get(headers.indexOf("TOTAL REPAYMENT")).replace("\"", ""));

        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            dayresp.setResponseCode(dresp.getCode());
            dayresp.setResponseText(dresp.getMessage());
            dayresp.setMessage(d.getMessage());
            weblogger.fatal(dayresp, d);

        }
        return dayresp;
    }

    @WebMethod(operationName = "BalanceSumRequest")
    public LdBalancesSumResponse BalanceSumRequest(@WebParam(name = "LDBalancesSumRequest") LdBalancesSumRequest bsreq) throws Exception {

        String ID = bsreq.getDate();
        LdBalancesSumResponse bsresp = new LdBalancesSumResponse();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
        weblogger.info("BalanceSumRequest");

        try {

            ArrayList<List<String>> result = t24.getOfsData("EM.LD.BALANCES.SUM", Ofsuser, Ofspass, "@ID:EQ=" + ID.trim());
            List<String> headers = result.get(0);

            if (headers.size() != result.get(1).size()) {

                throw new Exception(result.get(1).get(0));

            }
            dresp = ResponseCodes.SUCCESS;
            bsresp.setResponseCode(dresp.getCode());
            bsresp.setResponseText(dresp.getMessage());

        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            bsresp.setResponseCode(dresp.getCode());
            bsresp.setResponseText(dresp.getMessage());
            bsresp.setMessage(d.getMessage());
            weblogger.fatal(bsresp, d);

        }
        return bsresp;
    }

    @WebMethod(operationName = "LDRpmHistReq")
    public List<LdRpmHistResponse> LdRpmHistReq(@WebParam(name = "LDRpmHistRequest") LdRpmHistRequest ldreq) throws Exception {
        List<LdRpmHistResponse> allht = new ArrayList<>();
        String ldp = ldreq.getLoanID();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
        weblogger.info("LDRpmHistRequest");

        try {
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
                rhresp.setDate(result.get(i).get(headers.indexOf("DATE")).replace("\"", ""));
                rhresp.setSchType(result.get(i).get(headers.indexOf("SCH.TYPE")).replace("\"", ""));
                rhresp.setAmountDue(result.get(i).get(headers.indexOf("AMOUNT DUE")).replace("\"", ""));
                rhresp.setAmountPaid(result.get(i).get(headers.indexOf("AMOUNT PAID")).replace("\"", ""));
                allht.add(rhresp);
            }

            //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
        } catch (Exception d) {
            LdRpmHistResponse rhresp = new LdRpmHistResponse();
            dresp = ResponseCodes.Invalid_transaction;
            rhresp.setResponseCode(dresp.getCode());
            rhresp.setResponseText(dresp.getMessage());
            rhresp.setMessage(d.getMessage());
            allht.add(rhresp);
            weblogger.fatal(rhresp, d);

        }
        return allht;
    }

    @WebMethod(operationName = "PCLListAccount")
    public GICListAccountResponse PCLListAccount(@WebParam(name = "PCLListAccountRequest") GICListAccountRequest gcl) throws Exception {

        String gclreq = gcl.getAccountNumber();
        GICListAccountResponse laresp = new GICListAccountResponse();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
        weblogger.info("PCLListAccountEnquiry");

        try {

            ArrayList<List<String>> result = t24.getOfsData("PCL.GIC.LIST.ACCOUNT", Ofsuser, Ofspass, "ACCOUNT.NUMBER:EQ=" + gclreq.trim());
            List<String> headers = result.get(0);

            if (headers.size() != result.get(1).size()) {

                throw new Exception(result.get(1).get(0));

            }
            dresp = ResponseCodes.SUCCESS;
            laresp.setResponseCode(dresp.getCode());
            laresp.setResponseText(dresp.getMessage());
            laresp.setAccountNumber(result.get(1).get(headers.indexOf("Account No")).replace("\"", ""));
            laresp.setCustomerName(result.get(1).get(headers.indexOf("Customer Name")).replace("\"", ""));
            laresp.setCategory(result.get(1).get(headers.indexOf("Category")).replace("\"", ""));
            laresp.setCurrency(result.get(1).get(headers.indexOf("Currency")).replace("\"", ""));
            laresp.setOnlineBalance(result.get(1).get(headers.indexOf("Online Balance")).replace("\"", ""));
            laresp.setAccountMnemonic(result.get(1).get(headers.indexOf("Account Mnemonic")).replace("\"", ""));
            laresp.setOpeningDate(result.get(1).get(headers.indexOf("Open Date")).replace("\"", ""));
            laresp.setMaturityDate(result.get(1).get(headers.indexOf("Maturity Date")).replace("\"", ""));
            laresp.setCustomerNo(result.get(1).get(headers.indexOf("Customer No")).replace("\"", ""));

        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            laresp.setResponseCode(dresp.getCode());
            laresp.setResponseText(dresp.getMessage());
            laresp.setMessage(d.getMessage());
            weblogger.fatal(laresp, d);

        }
        return laresp;
    }

    @WebMethod(operationName = "LoanDisbursedReq")
    public List<LoanDisbursedResponse> LoanDisbursedReq(@WebParam(name = "LoanDisbursedRequest") LoanDisbursedRequest ldreq) throws Exception {

        String LDReferenceID = ldreq.getCustomerNumber();
        List<LoanDisbursedResponse> alllt = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
        weblogger.info("LoanDisbursedRequest");

        try {
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

            //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
        } catch (Exception d) {
            LoanDisbursedResponse ldresp = new LoanDisbursedResponse();
            dresp = ResponseCodes.Invalid_transaction;
            ldresp.setResponseCode(dresp.getCode());
            ldresp.setResponseText(dresp.getMessage());
            ldresp.setMessage(d.getMessage());
            alllt.add(ldresp);
            weblogger.fatal(ldresp, d);
        }
        return alllt;
    }

    @WebMethod(operationName = "SnapAccountReq")
    public SnapAccountResponse SnapAccountReq(@WebParam(name = "SnapAccountRequest") SnapAccountRequest snp) throws Exception {

        String SnapID = snp.getSnapID();
        SnapAccountResponse saresp = new SnapAccountResponse();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
        weblogger.info("SnapAccountEnquiry");

        try {

            ArrayList<List<String>> result = t24.getOfsData("SNAP.AC", Ofsuser, Ofspass, "SNAP.ID:EQ=" + SnapID.trim());
            List<String> headers = result.get(0);

            if (headers.size() != result.get(1).size()) {

                throw new Exception(result.get(1).get(0));

            }
            dresp = ResponseCodes.SUCCESS;
            saresp.setResponseCode(dresp.getCode());
            saresp.setResponseText(dresp.getMessage());
            saresp.setT24AccountNumber(result.get(1).get(headers.indexOf("T24 ACCOUNT NUMBER")).replace("\"", ""));
            saresp.setT24Cif(result.get(1).get(headers.indexOf("T24 CIF")).replace("\"", ""));
            saresp.setCategory(result.get(1).get(headers.indexOf("CATEGORY")).replace("\"", ""));
            saresp.setAccountName(result.get(1).get(headers.indexOf("ACCOUNT NAME")).replace("\"", ""));
            saresp.setSnapID(result.get(1).get(headers.indexOf("SNAP ID")).replace("\"", ""));
            saresp.setSnapInputter(result.get(1).get(headers.indexOf("SNAP INPUTTER")).replace("\"", ""));
            saresp.setSnapAuthoriser(result.get(1).get(headers.indexOf("SNAP AUTHORISER")).replace("\"", ""));

            //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            saresp.setResponseCode(dresp.getCode());
            saresp.setResponseText(dresp.getMessage());
            saresp.setMessage(d.getMessage());
            weblogger.fatal(saresp, d);

        }
        return saresp;
    }

    @WebMethod(operationName = "StmtEntBook")
    public List<StmtResponse> StmtEntBook(@WebParam(name = "AccountNumber") String acctno, @WebParam(name = "StartDate") String Startdate, @WebParam(name = "EndDate") String Enddate) throws Exception {

        List<StmtResponse> allstmt = new ArrayList<>();

        weblogger.info("StmtEntBookRequest");

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
            Date Start = sdf.parse(Startdate);
            Date End = sdf.parse(Enddate);

            Startdate = ndf.format(Start);
            Enddate = ndf.format(End);
            ArrayList<List<String>> result = t24.getOfsData("STMT.ENT.BOOK3", Ofsuser, Ofspass, "ACCOUNT.NUMBER:EQ=" + acctno.trim() + ",VALUE.DATE:RG=" + Startdate + " " + Enddate);
            List<String> headers = result.get(0);

            if (headers.size() != result.get(1).size()) {

                throw new Exception(result.get(1).get(0));

            }
            for (int i = 1; i < result.size(); i++) {
                StmtResponse seresp = new StmtResponse();
                dresp = ResponseCodes.SUCCESS;
                seresp.setResponseCode(dresp.getCode());
                seresp.setResponseText(dresp.getMessage());
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
        } catch (Exception d) {
            StmtResponse seresp = new StmtResponse();
            dresp = ResponseCodes.Invalid_transaction;
            seresp.setResponseCode(dresp.getCode());
            seresp.setResponseText(dresp.getMessage());
            seresp.setMessage(d.getMessage());
            allstmt.add(seresp);
            weblogger.fatal(seresp, d);
        }
        return allstmt;
    }

    @WebMethod(operationName = "StmtEntBookNostroReq")
    public List<StmtEntBookNostroResponse> StmtEntBookNostroReq(@WebParam(name = "AccountNumber") String acctno, @WebParam(name = "StartDate") String Startdate, @WebParam(name = "EndDate") String Enddate) throws Exception {
        List<StmtEntBookNostroResponse> allstmt = new ArrayList<>();
        weblogger.info("StmtEntBookNostroRepay");

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
            Date Start = sdf.parse(Startdate);
            Date End = sdf.parse(Enddate);

            Startdate = ndf.format(Start);
            Enddate = ndf.format(End);
            ArrayList<List<String>> result = t24.getOfsData("STMT.ENT.BOOK.NOSTRO.REPAY.22", Ofsuser, Ofspass, "ACCOUNT.NUMBER:EQ=" + acctno.trim() + ",VALUE.DATE:RG=" + Startdate + " " + Enddate);
            List<String> headers = result.get(0);

            if (headers.size() != result.get(1).size()) {

                throw new Exception(result.get(1).get(0));

            }
            for (int i = 1; i < result.size(); i++) {
                StmtEntBookNostroResponse sebresp = new StmtEntBookNostroResponse();
                dresp = ResponseCodes.SUCCESS;
                sebresp.setResponseCode(dresp.getCode());
                sebresp.setResponseText(dresp.getMessage());
                sebresp.setBookingDate(result.get(i).get(headers.indexOf("Booking Date")).replace("\"", ""));
                sebresp.setReference(result.get(i).get(headers.indexOf("Reference")).replace("\"", ""));
                sebresp.setDescription(result.get(i).get(headers.indexOf("Description")).replace("\"", ""));
                sebresp.setNarrative(result.get(i).get(headers.indexOf("Narrative")).replace("\"", ""));
                sebresp.setCreditAcctNumber(result.get(i).get(headers.indexOf("Credit Acct Name")).replace("\"", ""));
                sebresp.setValueDate(result.get(i).get(headers.indexOf("Value Date")).replace("\"", ""));
                sebresp.setDebitAmount(result.get(i).get(headers.indexOf("Debit")).replace("\"", ""));
                sebresp.setCreditAmount(result.get(i).get(headers.indexOf("Credit")).replace("\"", ""));
                sebresp.setClosingBalance(result.get(i).get(headers.indexOf("Closing Balance")).replace("\"", ""));
                allstmt.add(sebresp);
            }

            //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
        } catch (Exception d) {
            StmtEntBookNostroResponse sebresp = new StmtEntBookNostroResponse();
            dresp = ResponseCodes.Invalid_transaction;
            sebresp.setResponseCode(dresp.getCode());
            sebresp.setResponseText(dresp.getMessage());
            sebresp.setMessage(d.getMessage());
            allstmt.add(sebresp);
            weblogger.fatal(sebresp, d);

        }
        return allstmt;
    }

    @WebMethod(operationName = "AccountStatement")
    public List<StatementResp> AccountStatement(@WebParam(name = "AccountNumber") String acctno, @WebParam(name = "StartDate") String Startdate, @WebParam(name = "EndDate") String Enddate, @WebParam(name = "Hash") String hash) throws Exception {
        List<StatementResp> allstmt = new ArrayList<>();
        weblogger.info("AccountStatement");

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
            Date Start = sdf.parse(Startdate);
            Date End = sdf.parse(Enddate);

            Startdate = ndf.format(Start);
            Enddate = ndf.format(End);
            ArrayList<List<String>> result = t24.getOfsData("STMT.ENT.BOOK.CUSTOMISED.2", Ofsuser, Ofspass, "ACCT.ID:EQ=" + acctno.trim() + ",VALUE.DATE:RG=" + Startdate + " " + Enddate);
            result.remove(1);
            List<String> headers = result.get(0);

            if (headers.size() != result.get(1).size()) {

                throw new Exception(result.get(1).get(0));

            }

            for (int i = 1; i < result.size(); i++) {
                StatementResp sebresp = new StatementResp();
                dresp = ResponseCodes.SUCCESS;
                sebresp.setResponseCode(dresp.getCode());
                sebresp.setResponseText(dresp.getMessage());
                sebresp.setAccountNumber(result.get(i).get(headers.indexOf("Account No")).replace("\"", ""));
                sebresp.setCIF(result.get(i).get(headers.indexOf("Cif")).replace("\"", ""));
                sebresp.setCustomerName(result.get(i).get(headers.indexOf("Customer Name")).replace("\"", ""));
                sebresp.setBookingDate(result.get(i).get(headers.indexOf("Booking Date")).replace("\"", ""));
                sebresp.setValueDate(result.get(i).get(headers.indexOf("Value Date")).replace("\"", ""));
                sebresp.setDescription(result.get(i).get(headers.indexOf("Description")).replace("\"", ""));
                sebresp.setNarrative(result.get(i).get(headers.indexOf("NARRATIVE")).replace("\"", ""));
                sebresp.setDebitAmount(result.get(i).get(headers.indexOf("Debit")).replace("\"", ""));
                sebresp.setCreditAmount(result.get(i).get(headers.indexOf("Credit")).replace("\"", ""));
                sebresp.setClosingBalance(result.get(i).get(headers.indexOf("Closing Balance")).replace("\"", ""));
                allstmt.add(sebresp);
            }

            //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
        } catch (Exception d) {
            StatementResp sebresp = new StatementResp();
            dresp = ResponseCodes.Invalid_transaction;
            sebresp.setResponseCode(dresp.getCode());
            sebresp.setResponseText(dresp.getMessage());
            sebresp.setMessage(d.getMessage());
            allstmt.add(sebresp);
            weblogger.fatal(sebresp, d);

        }
        return allstmt;
    }

    @WebMethod(operationName = "CustomerDetails")
    public CustomerDetailResponse CustomerDetails(@WebParam(name = "CustomerNumber") String custno, @WebParam(name = "hash") String hash) throws Exception {
        CustomerDetailResponse curesp = new CustomerDetailResponse();
        weblogger.info("CustomerDetails");

        try {

            ArrayList<List<String>> result = t24.getOfsData("EM.GIC.MEMBER.LIST.3", Ofsuser, Ofspass, "CUSTOMER.CODE:EQ=" + custno.trim());
            List<String> headers = result.get(0);

            if (headers.size() != result.get(1).size()) {

                throw new Exception(result.get(1).get(0));

            }

            dresp = ResponseCodes.SUCCESS;
            curesp.setResponseCode(dresp.getCode());
            curesp.setResponseText(dresp.getMessage());
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
        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            curesp.setResponseCode(dresp.getCode());
            curesp.setResponseText(dresp.getMessage());
            curesp.setMessage(d.getMessage());
            weblogger.fatal(curesp, d);

        }
        return curesp;
    }
}
