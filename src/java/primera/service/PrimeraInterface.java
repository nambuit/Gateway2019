/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package primera.service;

import com.common.service.util.logging.LogService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.naming.InitialContext;
import prm.tools.AppParams;
import prm.tools.NIBBsResponseCodes;
import org.apache.log4j.Logger;

/**
 *
 * @author dogor-Igbosuah
 */
@WebService(serviceName = "PrimeraInterface")
public class PrimeraInterface {

    Logger weblogger = Logger.getLogger(PrimeraInterface.class.getName());
    LogService logger = LogService.getLogger();
    T24Link t24;
    private String Ofsuser;
    private String Ofspass;
    AppParams options;
    String logfilename = "PrimeraInterface";
    NIBBsResponseCodes nibbsresp;
    String SFactor = "Transaction Successful";
    String DEBUG_KEY_METHOD_ENTRY = "METHOD_ENTRY";
    String DEBUG_KEY_METHOD_EXIT = "METHOD_EXIT";
    String DEBUG_KEY_EXP = "DEBUG_KEY";
    public String APIKey = "4466FA2C-1886-4366-B014-AD140712BE38";

    public PrimeraInterface() {
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

    @WebMethod(operationName = "CurrentAccount")
    public ObjectResponse CurrentAccount(@WebParam(name = "accountdetails") CurrentAccountRequest accountdetails) throws Exception {
        ObjectResponse accountdetailresp = new ObjectResponse();

        String[] params
                = {
                    "CurrentAccount"
                };
        logger.logDebug(DEBUG_KEY_METHOD_ENTRY,
                params);
        weblogger.info("Method Entered");
        try {

            String stringtohash = accountdetails.getAccountName() + accountdetails.getCustomerNo();

            String requesthash = accountdetails.getHash();

            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");

            Date trandate = sdf.parse(accountdetails.getValueDate());
            Date transdate = sdf.parse(accountdetails.getOpeningDate());
            String[] ofsoptions = new String[]{"", "I", "PROCESS", "", "0"};
            String[] credentials = new String[]{Ofsuser, Ofspass};
            List<DataItem> items = new LinkedList<>();
            //List<String> headers = result.get(0);

            accountdetails.setValueDate(ndf.format(trandate));
            accountdetails.setOpeningDate(ndf.format(transdate));

            ofsParam param = new ofsParam();
            param.setCredentials(credentials);
            param.setOperation("ACCOUNT");
            param.setVersion("ICL.CURR.ACCT");
            param.setOptions(ofsoptions);
            param.setTransaction_id("");

            if (hash.equals(requesthash)) {
                DataItem item = new DataItem();
                item.setItemHeader("CUSTOMER");
                item.setItemValues(new String[]{accountdetails.getCustomerNo()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("ACCOUNT.TITLE.1");
                item.setItemValues(new String[]{accountdetails.getAccountName()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("ACCOUNT.OFFICER");
                item.setItemValues(new String[]{accountdetails.getBusinessSegmentCode()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("LIMIT.REF");
                item.setItemValues(new String[]{accountdetails.getLimitReference()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("POSTING.RESTRICT");
                item.setItemValues(new String[]{accountdetails.getBlockedReasons()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("OPENING.DATE");
                item.setItemValues(new String[]{accountdetails.getOpeningDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("VALUE.DATE");
                item.setItemValues(new String[]{accountdetails.getValueDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("ENT.DETAILS");
                item.setItemValues(new String[]{accountdetails.getIPPISnumber()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("MIS.CODE");
                item.setItemValues(new String[]{accountdetails.getBranchLocation()});
                items.add(item);

                param.setDataItems(items);

                String[] params1
                        = {
                            "The Items Are =[" + items + "]"
                        };
                logger.logDebug(DEBUG_KEY_METHOD_ENTRY,
                        params1);
                weblogger.info("The items are " + items);

            } 
            else {
                
               nibbsresp = NIBBsResponseCodes.Security_violation;
               accountdetailresp.setResponseCode(nibbsresp.getCode());
                accountdetailresp.setResponseText(nibbsresp.getMessage());
                return accountdetailresp;
            }
            
            
            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {

                accountdetailresp.setResponseCode("00");
                accountdetailresp.setIsSuccessful(true);
                accountdetailresp.setMessage(SFactor);
                accountdetailresp.setTransactionDate(sdf.format(trandate));
                //accountdetailresp.setTransactionID(result.getClass()

                //txn.setTransactionID(result.get(i).get(headers.indexOf("@ID")).replace("\"", "")); 
            } else {

                accountdetailresp.setIsSuccessful(false);
                accountdetailresp.setMessage(result.split("/")[3]);
                //details(result.split("/")[3]);
            }

        } catch (ParseException ex) {
            String[] param2
                    = {
                        "param =[" + accountdetails + "]"
                    };
            logger.logError(DEBUG_KEY_EXP,
                    param2, ex);
            accountdetailresp.setIsSuccessful(false);
            accountdetailresp.setMessage(ex.getMessage());

        }
        //ogger.getLogger(PrimeraInterface.class.getName()).log(Level.SEVERE, null, e//;

        return accountdetailresp;

    }

    @WebMethod(operationName = "FundTransfer")
    public ObjectResponse FundTransfer(@WebParam(name = "fdetails") FundsTransferRequest fdetails) throws ParseException {

        ObjectResponse fdetailresp = new ObjectResponse();

        String[] params
                = {
                    "FundsTransfer"
                };
        logger.logDebug(DEBUG_KEY_METHOD_ENTRY,
                params);
        weblogger.info("Method Entered");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");

            Date trandate = sdf.parse(fdetails.getDRValueDate());
            //Date transdate = sdf.parse(fdetails.getOpeningDate());
            String[] ofsoptions = new String[]{"", "I", "PROCESS", "", "0"};
            String[] credentials = new String[]{Ofsuser, Ofspass};
            List<DataItem> items = new LinkedList<>();
            fdetails.setDRValueDate(ndf.format(trandate));

            ofsParam param = new ofsParam();
            param.setCredentials(credentials);
            param.setOperation("FUNDS.TRANSFER");
            param.setVersion("ICL.FT");
            param.setOptions(ofsoptions);
            param.setTransaction_id("");

            DataItem item = new DataItem();
            item.setItemHeader("DR.FULL.NAME");
            item.setItemValues(new String[]{fdetails.getDebitCustomer()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("DR.WORK.BAL");
            item.setItemValues(new String[]{fdetails.getAccountBalance()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("CONS.DISCLOSE");
            item.setItemValues(new String[]{fdetails.getSignInstructions()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("DEBIT.ACCT.NO");
            item.setItemValues(new String[]{fdetails.getDebitAccountNo()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("DEBIT.AMOUNT");
            item.setItemValues(new String[]{fdetails.getDebitAmount()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("DEBIT.VALUE.DATE");
            item.setItemValues(new String[]{fdetails.getDRValueDate()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("DEBIT.THEIR.REF");
            item.setItemValues(new String[]{fdetails.getDebitRef()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("ORDERING.CUST");
            item.setItemValues(new String[]{fdetails.getOrderedBy()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("PROFIT.CENTRE.DEPT");
            item.setItemValues(new String[]{fdetails.getCostCenter()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("CR.FULL.NAME");
            item.setItemValues(new String[]{fdetails.getCreditCustomer()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("CREDIT.ACCT.NO");
            item.setItemValues(new String[]{fdetails.getCreditAccountNo()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("CREDIT.VALUE.DATE");
            item.setItemValues(new String[]{fdetails.getDRValueDate()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("CREDIT.THEIR.REF");
            item.setItemValues(new String[]{fdetails.getCreditRef()});
            items.add(item);

            param.setDataItems(items);
            String[] params1
                    = {
                        "The Items Are =[" + items + "]"
                    };
            logger.logDebug(DEBUG_KEY_METHOD_ENTRY,
                    params1);
            weblogger.info("The items are " + items);
            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {

                fdetailresp.setResponseCode("00");
                fdetailresp.setIsSuccessful(true);
                fdetailresp.setMessage(SFactor);
                fdetailresp.setTransactionDate(sdf.format(trandate));
            } else {

                fdetailresp.setIsSuccessful(false);
                fdetailresp.setMessage(result.split("/")[3]);
                //details(result.split("/")[3]);
            }
        } catch (ParseException ex) {
            fdetailresp.setIsSuccessful(false);
            fdetailresp.setMessage(ex.getMessage());

        }

        return fdetailresp;
    }

    @WebMethod(operationName = "D2RSCustomer")
    public ObjectResponse D2RSCustomer(@WebParam(name = "drdetails") D2RSPersonalDetailRequest drdetails) {

        ObjectResponse drobjectresp = new ObjectResponse();
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");

            Date trandate = sdf.parse(drdetails.getDateofEvaluation());
            //Date transdate = sdf.parse(fdetails.getOpeningDate());
            String[] ofsoptions = new String[]{"", "I", "PROCESS", "", "0"};
            String[] credentials = new String[]{Ofsuser, Ofspass};
            List<DataItem> items = new LinkedList<>();
            drdetails.setDateofEvaluation(ndf.format(trandate));

            ofsParam param = new ofsParam();
            param.setCredentials(credentials);
            param.setOperation("CUSTOMER");
            param.setVersion("ICL.CUST.D2RS");
            param.setOptions(ofsoptions);
            param.setTransaction_id("");

            DataItem item = new DataItem();
            item.setItemHeader("TITLE");
            item.setItemValues(new String[]{drdetails.getTitle()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("SHORT.NAME");
            item.setItemValues(new String[]{drdetails.getSurname()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("NAME.2");
            item.setItemValues(new String[]{drdetails.getMiddleName()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("NAME.1");
            item.setItemValues(new String[]{drdetails.getFirstName()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("MARITAL.STATUS");
            item.setItemValues(new String[]{drdetails.getMaritalStatus()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("FORMER.NAME");
            item.setItemValues(new String[]{drdetails.getMaidenName()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("BIRTH.INCORP.DATE");
            item.setItemValues(new String[]{drdetails.getDateofBirth()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("INITIALS");
            item.setItemValues(new String[]{drdetails.getInitials()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("INDUSTRY");
            item.setItemValues(new String[]{drdetails.getIndustry()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("ACCOUNT.OFFICER");
            item.setItemValues(new String[]{drdetails.getLoanOfficer()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("OPENING.DATE");
            item.setItemValues(new String[]{drdetails.getDateofEvaluation()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("GENDER");
            item.setItemValues(new String[]{drdetails.getGender()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("BVN.NUMBER");
            item.setItemValues(new String[]{drdetails.getCustomerBVNNo()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("GROUP");
            item.setItemValues(new String[]{drdetails.getGroupName()});
            items.add(item);

            param.setDataItems(items);
            String[] params1
                    = {
                        "The Items Are =[" + items + "]"
                    };
            logger.logDebug(DEBUG_KEY_METHOD_ENTRY,
                    params1);

            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {

                drobjectresp.setResponseCode("00");
                drobjectresp.setIsSuccessful(true);
                drobjectresp.setMessage(SFactor);
                drobjectresp.setTransactionDate(sdf.format(trandate));
            } else {

                drobjectresp.setIsSuccessful(false);
                drobjectresp.setMessage(result.split("/")[3]);
                //details(result.split("/")[3]);
            }

        } catch (ParseException ex) {

        }
        return drobjectresp;
    }

    @WebMethod(operationName = "MicroCreditCustomer")
    public ObjectResponse MicroCreditCustomer(@WebParam(name = "mcdetails") MicroCreditRequest mcdetails) {
        ObjectResponse mcdetailresp = new ObjectResponse();
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");

            Date trandate = sdf.parse(mcdetails.getDateofBirth());
            Date transdate = sdf.parse(mcdetails.getCustomerOpeningDate());
            String[] ofsoptions = new String[]{"", "I", "PROCESS", "", "0"};
            String[] credentials = new String[]{Ofsuser, Ofspass};
            List<DataItem> items = new LinkedList<>();
            mcdetails.setDateofBirth(ndf.format(trandate));
            mcdetails.setCustomerOpeningDate(ndf.format(transdate));
            ofsParam param = new ofsParam();
            param.setCredentials(credentials);
            param.setOperation("CUSTOMER");
            param.setVersion("MICRO.CREDIT.1");
            param.setOptions(ofsoptions);
            param.setTransaction_id("");

            DataItem item = new DataItem();
            item.setItemHeader("TITLE");
            item.setItemValues(new String[]{mcdetails.getTitle()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("SHORT.NAME");
            item.setItemValues(new String[]{mcdetails.getSurname()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("NAME.2");
            item.setItemValues(new String[]{mcdetails.getMiddleName()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("NAME.1");
            item.setItemValues(new String[]{mcdetails.getFirstName()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("BIRTH.INCORP.DATE");
            item.setItemValues(new String[]{mcdetails.getDateofBirth()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("INITIALS");
            item.setItemValues(new String[]{mcdetails.getInitials()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("OPENING.DATE");
            item.setItemValues(new String[]{mcdetails.getCustomerOpeningDate()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("INDUSTRY");
            item.setItemValues(new String[]{mcdetails.getIndustry()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("L.ACC.OFFICER");
            item.setItemValues(new String[]{mcdetails.getIntroducer()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("REPAYMENT.TYPE");
            item.setItemValues(new String[]{mcdetails.getRepaymentType()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("MIS.CODE");
            item.setItemValues(new String[]{mcdetails.getBranchMISCode()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("GUARANTOR.NAME");
            item.setItemValues(new String[]{mcdetails.getGuarantorName()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("ACCOUNT.OFFICER");
            item.setItemValues(new String[]{mcdetails.getAccountOfficer()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("YOUR.REFER");
            item.setItemValues(new String[]{mcdetails.getPrimeraRefer()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("REFER.NAME");
            item.setItemValues(new String[]{mcdetails.getRefereeName()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("REFEREE.PHONE");
            item.setItemValues(new String[]{mcdetails.getRefereePhoneNo()});
            items.add(item);

            param.setDataItems(items);
            String[] params1
                    = {
                        "The Items Are =[" + items + "]"
                    };
            logger.logDebug(DEBUG_KEY_METHOD_ENTRY,
                    params1);

            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {

                mcdetailresp.setResponseCode("00");
                mcdetailresp.setIsSuccessful(true);
                mcdetailresp.setMessage(SFactor);
                mcdetailresp.setTransactionDate(sdf.format(trandate));
            } else {

                mcdetailresp.setIsSuccessful(false);
                mcdetailresp.setMessage(result.split("/")[3]);
                //details(result.split("/")[3]);
            }

        } catch (ParseException ex) {

        }

        return mcdetailresp;

    }

    @WebMethod(operationName = "IndividualCustomer")
    public ObjectResponse IndividualCustomer(@WebParam(name = "indetails") IndividualCustomerRequest indetails) throws ParseException {
        ObjectResponse indetailresp = new ObjectResponse();
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");

            Date trandate = sdf.parse(indetails.getDateofBirth());
            Date transdate = sdf.parse(indetails.getCustomerOpeningDate());
            String[] ofsoptions = new String[]{"", "I", "PROCESS", "", "0"};
            String[] credentials = new String[]{Ofsuser, Ofspass};
            List<DataItem> items = new LinkedList<>();
            indetails.setDateofBirth(ndf.format(trandate));
            indetails.setCustomerOpeningDate(ndf.format(transdate));

            ofsParam param = new ofsParam();
            param.setCredentials(credentials);
            param.setOperation("CUSTOMER");
            param.setVersion("ICL.CUST.INDIVIDUAL");
            param.setOptions(ofsoptions);
            param.setTransaction_id("");

            DataItem item = new DataItem();
            item.setItemHeader("TITLE");
            item.setItemValues(new String[]{indetails.getTitle()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("SHORT.NAME");
            item.setItemValues(new String[]{indetails.getSurname()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("NAME.2");
            item.setItemValues(new String[]{indetails.getMiddleName()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("NAME.1");
            item.setItemValues(new String[]{indetails.getFirstName()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("MARITAL.STATUS");
            item.setItemValues(new String[]{indetails.getMaritalStatus()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("FORMER.NAME");
            item.setItemValues(new String[]{indetails.getMaidenName()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("BIRTH.INCORP.DATE");
            item.setItemValues(new String[]{indetails.getDateofBirth()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("INITIALS");
            item.setItemValues(new String[]{indetails.getInitials()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("INDUSTRY");
            item.setItemValues(new String[]{indetails.getIndustry()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("ACCOUNT.OFFICER");
            item.setItemValues(new String[]{indetails.getAccountOfficer()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("OPENING.DATE");
            item.setItemValues(new String[]{indetails.getCustomerOpeningDate()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("GENDER");
            item.setItemValues(new String[]{indetails.getGender()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("MEANS.PAY");
            item.setItemValues(new String[]{indetails.getMeansOfPayment()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("REPAYMENT.TYPE");
            item.setItemValues(new String[]{indetails.getRepaymentType()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("LOAN.TYPE");
            item.setItemValues(new String[]{indetails.getTypeOfLoan()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("BVN.NUMBER");
            item.setItemValues(new String[]{indetails.getCustomerBVNNo()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("MIS.CODE");
            item.setItemValues(new String[]{indetails.getBranchMISCode()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("L.ACC.OFFICER");
            item.setItemValues(new String[]{indetails.getIntroducer()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("075CLUB.ID");
            item.setItemValues(new String[]{indetails.getCLUBREFNO()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("YOUR.REFER");
            item.setItemValues(new String[]{indetails.getPrimeraRefer()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("REFER.NAME");
            item.setItemValues(new String[]{indetails.getRefereeName()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("REFEREE.PHONE");
            item.setItemValues(new String[]{indetails.getRefereePhoneNo()});
            items.add(item);

            param.setDataItems(items);
            String[] params1
                    = {
                        "The Items Are =[" + items + "]"
                    };
            logger.logDebug(DEBUG_KEY_METHOD_ENTRY,
                    params1);

            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {

                indetailresp.setResponseCode("00");
                indetailresp.setIsSuccessful(true);
                indetailresp.setMessage(SFactor);
                indetailresp.setTransactionDate(sdf.format(trandate));
            } else {

                indetailresp.setIsSuccessful(false);
                indetailresp.setMessage(result.split("/")[3]);
                //details(result.split("/")[3]);
            }

        } catch (ParseException ex) {

        }

        return indetailresp;

    }

    @WebMethod(operationName = "NonIndividualCustomer")
    public ObjectResponse NonIndividualCustomer(@WebParam(name = "nindetails") NonIndividualCustomerRequest nindetails) {
        ObjectResponse nindetailresp = new ObjectResponse();
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");

            Date trandate = sdf.parse(nindetails.getDateRegistered());
            Date transdate = sdf.parse(nindetails.getCustomerOpeningDate());
            Date transsdate = sdf.parse(nindetails.getDateRegistered());
            String[] ofsoptions = new String[]{"", "I", "PROCESS", "", "0"};
            String[] credentials = new String[]{Ofsuser, Ofspass};
            List<DataItem> items = new LinkedList<>();
            nindetails.setDateRegistered(ndf.format(trandate));
            nindetails.setCustomerOpeningDate(ndf.format(transdate));
            nindetails.setDateofSignature(ndf.format(transsdate));

            ofsParam param = new ofsParam();
            param.setCredentials(credentials);
            param.setOperation("CUSTOMER");
            param.setVersion("ICL.CUST.NON-INDIVIDUAL");
            param.setOptions(ofsoptions);
            param.setTransaction_id("");

            DataItem item = new DataItem();
            item.setItemHeader("SHORT.NAME");
            item.setItemValues(new String[]{nindetails.getNameofEntity()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("NAME.1");
            item.setItemValues(new String[]{nindetails.getAbbreviatedName()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("BIRTH.INCORP.DATE");
            item.setItemValues(new String[]{nindetails.getDateRegistered()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("LEGAL.ID");
            item.setItemValues(new String[]{nindetails.getRegistrationNo()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("OPENING.DATE");
            item.setItemValues(new String[]{nindetails.getCustomerOpeningDate()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("INDUSTRY");
            item.setItemValues(new String[]{nindetails.getIndustry()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("CRDT.IND");
            item.setItemValues(new String[]{nindetails.getCreditIndicator()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("DATE.OF.SIG");
            item.setItemValues(new String[]{nindetails.getDateofSignature()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("LOANS.WOF");
            item.setItemValues(new String[]{nindetails.getLoansWrittenOff()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("ACCOUNT.OFFICER");
            item.setItemValues(new String[]{nindetails.getAccountOfficer()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("YOUR.REFER");
            item.setItemValues(new String[]{nindetails.getPrimeraRefer()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("REFER.NAME");
            item.setItemValues(new String[]{nindetails.getRefereeName()});
            items.add(item);

            item = new DataItem();
            item.setItemHeader("REFEREE.PHONE");
            item.setItemValues(new String[]{nindetails.getRefereePhoneNo()});
            items.add(item);

            param.setDataItems(items);
//            String[] params1 =
//            {
//                "The Items Are =[" + items + "]"
//            };
//            logger.logDebug(DEBUG_KEY_METHOD_ENTRY,
//                    params1); 

            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {

                nindetailresp.setResponseCode("00");
                nindetailresp.setIsSuccessful(true);
                nindetailresp.setMessage(SFactor);
                nindetailresp.setTransactionDate(sdf.format(trandate));
            } else {

                nindetailresp.setIsSuccessful(false);
                nindetailresp.setMessage(result.split("/")[3]);
                //details(result.split("/")[3]);
            }

        } catch (ParseException ex) {

        }

        return nindetailresp;
    }

}
