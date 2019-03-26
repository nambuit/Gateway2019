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
//    String SFactor = "Transaction Successful";
//    String DEBUG_KEY_METHOD_ENTRY = "METHOD_ENTRY";
//    String DEBUG_KEY_METHOD_EXIT = "METHOD_EXIT";
//    String DEBUG_KEY_EXP = "DEBUG_KEY";
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
        weblogger.info("CurrentAccount");

        try {

            String stringtohash = accountdetails.getAccountName() + accountdetails.getCustomerNo();

            String requesthash = accountdetails.getHash();

            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);

            String intName = accountdetails.getInterfaceName();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");

            Date trandate = sdf.parse(accountdetails.getValueDate());
            Date transdate = sdf.parse(accountdetails.getOpeningDate());
            String[] ofsoptions = new String[]{"", "I", "PROCESS", "", "0"};
            String[] credentials = new String[]{Ofsuser, Ofspass};
            List<DataItem> items = new LinkedList<>();
            //List<String> headers = result.get(0);0

            accountdetails.setValueDate(ndf.format(trandate));
            accountdetails.setOpeningDate(ndf.format(transdate));

            ofsParam param = new ofsParam();
            param = new ofsParam();
            param.setCredentials(credentials);
            param.setOperation("ACCOUNT");
            param.setVersion("ICL.CURR.ACCT");
            param.setOptions(ofsoptions);
            param.setTransaction_id("");
            if (hash.equals(requesthash) && (intName != null)) {

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
                weblogger.info("The items are " + items);

            } else {

                nibbsresp = NIBBsResponseCodes.Security_violation;
                accountdetailresp.setResponseCode(nibbsresp.getCode());
                accountdetailresp.setResponseText(nibbsresp.getMessage());
//                accountdetailresp.setTransactionDate(sdf.format(trandate));
                weblogger.error(accountdetailresp);
                return accountdetailresp;
            }
            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {
                nibbsresp = NIBBsResponseCodes.SUCCESS;
                accountdetailresp.setTransactionDate(sdf.format(trandate));
                accountdetailresp.setResponseCode(nibbsresp.getCode());
                accountdetailresp.setMessage(nibbsresp.getMessage());
                weblogger.info(accountdetailresp);

            } else {
                nibbsresp = NIBBsResponseCodes.Invalid_transaction;
                //accountdetailresp.setIsSuccessful(false);
                //accountdetailresp.setMessage(result.split("/")[3]);
                accountdetailresp.setResponseCode(nibbsresp.getCode());
                accountdetailresp.setResponseText(nibbsresp.getMessage());
                weblogger.fatal(accountdetailresp);
                return accountdetailresp;
//             
            }

        } catch (ParseException ex) {

            accountdetailresp.setIsSuccessful(false);

            accountdetailresp.setMessage(ex.getMessage());

            weblogger.fatal(accountdetailresp, ex);

        }

        return accountdetailresp;

    }

    @WebMethod(operationName = "FundTransfer")
    public ObjectResponse FundTransfer(@WebParam(name = "fdetails") FundsTransferRequest fdetails) throws ParseException, Exception {

        ObjectResponse fdetailresp = new ObjectResponse();
        weblogger.info("FundTransfer");
        try {

            String stringtohash = fdetails.getCreditAccountNo() + fdetails.getDebitAccountNo();

            String requesthash = fdetails.getHash();

            String intName = fdetails.getInterfaceName();

            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");

            Date trandate = sdf.parse(fdetails.getDRValueDate());
            //Date transdate = sdf.parse(fdetails.getOpeningDate());
            String[] ofsoptions = new String[]{"", "I", "PROCESS", "", "0"};
            String[] credentials = new String[]{Ofsuser, Ofspass};
            List<DataItem> items = new LinkedList<>();
            fdetails.setDRValueDate(ndf.format(trandate));

            ofsParam param = new ofsParam();
            param = new ofsParam();
            param.setCredentials(credentials);
            param.setOperation("FUNDS.TRANSFER");
            param.setVersion("ICL.FT");
            param.setOptions(ofsoptions);
            param.setTransaction_id("");

            if (hash.equals(requesthash) && (intName != null)) {

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

                weblogger.info("The items are " + items);
            } else {
                nibbsresp = NIBBsResponseCodes.Security_violation;
                fdetailresp.setResponseCode(nibbsresp.getCode());
                fdetailresp.setResponseText(nibbsresp.getMessage());
//                accountdetailresp.setTransactionDate(sdf.format(trandate));
                weblogger.error(fdetailresp);
                return fdetailresp;

            }

            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {

                nibbsresp = NIBBsResponseCodes.SUCCESS;
                fdetailresp.setResponseCode(nibbsresp.getCode());
                fdetailresp.setResponseText(nibbsresp.getMessage());
                weblogger.info(fdetailresp);
            } else {

                nibbsresp = NIBBsResponseCodes.Invalid_transaction;
                fdetailresp.setResponseCode(nibbsresp.getCode());
                fdetailresp.setResponseText(nibbsresp.getMessage());
                weblogger.error(fdetailresp);
                return fdetailresp;
                //fdetailresp.setMessage(result.split("/")[3]);
                //details(result.split("/")[3]);
            }

        } catch (ParseException ex) {
            fdetailresp.setIsSuccessful(false);
            fdetailresp.setMessage(ex.getMessage());
            weblogger.fatal(fdetailresp, ex);

        }

        return fdetailresp;
    }

    @WebMethod(operationName = "D2RSCustomer")
    public ObjectResponse D2RSCustomer(@WebParam(name = "drdetails") D2RSPersonalDetailRequest drdetails) throws Exception {

        ObjectResponse drobjectresp = new ObjectResponse();
        weblogger.info("D2RSCustomer");
        try {

            String stringtohash = drdetails.getCustomerBVNNo() + drdetails.getFirstName();

            String requesthash = drdetails.getHash();

            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);
            String intName = drdetails.getInterfaceName();

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

            if (hash.equals(requesthash) && (intName != null)) {

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

                weblogger.info("The items are " + items);

            } else {
                nibbsresp = NIBBsResponseCodes.Security_violation;
                drobjectresp.setResponseCode(nibbsresp.getCode());
                drobjectresp.setResponseText(nibbsresp.getMessage());
//                accountdetailresp.setTransactionDate(sdf.format(trandate));
                weblogger.error(drobjectresp);
                return drobjectresp;
            }

            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {

                nibbsresp = NIBBsResponseCodes.SUCCESS;
                drobjectresp.setResponseCode(nibbsresp.getCode());
                drobjectresp.setResponseText(nibbsresp.getMessage());
                weblogger.info(drobjectresp);
                //drobjectresp.setTransactionDate(sdf.format(trandate));
            } else {
                nibbsresp = NIBBsResponseCodes.Invalid_transaction;
                //drobjectresp.setMessage(result.split("/")[3]);
                drobjectresp.setResponseCode(nibbsresp.getCode());
                drobjectresp.setResponseText(nibbsresp.getMessage());
                //details(result.split("/")[3]);
                weblogger.error(drobjectresp);
                return drobjectresp;
            }

        } catch (ParseException ex) {
            drobjectresp.setIsSuccessful(false);
            drobjectresp.setMessage(ex.getMessage());
            weblogger.fatal(drobjectresp, ex);
        }
        return drobjectresp;
    }

    @WebMethod(operationName = "MicroCreditCustomer")
    public ObjectResponse MicroCreditCustomer(@WebParam(name = "mcdetails") MicroCreditRequest mcdetails) throws Exception {
        ObjectResponse mcdetailresp = new ObjectResponse();
        weblogger.info("MicroCreditCustomer");
        try {

            String stringtohash = mcdetails.getFirstName() + mcdetails.getDateofBirth();

            String requesthash = mcdetails.getHash();

            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);
            String intName = mcdetails.getInterfaceName();

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

            if (hash.equals(requesthash) && (intName != null)) {
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

                weblogger.info("The items are " + items);

            } else {
                nibbsresp = NIBBsResponseCodes.Security_violation;
                mcdetailresp.setResponseCode(nibbsresp.getCode());
                mcdetailresp.setResponseText(nibbsresp.getMessage());
//                accountdetailresp.setTransactionDate(sdf.format(trandate));
                weblogger.error(mcdetailresp);
                return mcdetailresp;
            }

            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {
                nibbsresp = NIBBsResponseCodes.SUCCESS;
                mcdetailresp.setResponseCode(nibbsresp.getCode());
                mcdetailresp.setResponseText(nibbsresp.getMessage());
                weblogger.info(mcdetailresp);
            } else {
                nibbsresp = NIBBsResponseCodes.Invalid_transaction;
                mcdetailresp.setResponseCode(nibbsresp.getCode());
                mcdetailresp.setResponseText(nibbsresp.getMessage());
                weblogger.fatal(mcdetailresp);
                return mcdetailresp;
                //mcdetailresp.setMessage(result.split("/")[3]);
                //details(result.split("/")[3]);
            }

        } catch (ParseException ex) {
            mcdetailresp.setIsSuccessful(false);
            mcdetailresp.setMessage(ex.getMessage());
            weblogger.fatal(mcdetailresp, ex);
        }

        return mcdetailresp;

    }

    @WebMethod(operationName = "IndividualCustomer")
    public ObjectResponse IndividualCustomer(@WebParam(name = "indetails") IndividualCustomerRequest indetails) throws ParseException, Exception {
        ObjectResponse indetailresp = new ObjectResponse();
        weblogger.info("IndividualCustomer");
        try {

            String stringtohash = indetails.getFirstName() + indetails.getCustomerBVNNo();

            String requesthash = indetails.getHash();

            String intName = indetails.getInterfaceName();

            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);
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

            if (hash.equals(requesthash) && (intName != null)) {

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

                weblogger.info("The Items are " + items);

            } else {
                nibbsresp = NIBBsResponseCodes.Security_violation;
                indetailresp.setResponseCode(nibbsresp.getCode());
                indetailresp.setResponseText(nibbsresp.getMessage());
                weblogger.error(indetailresp);
                return indetailresp;
            }

            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {

                nibbsresp = NIBBsResponseCodes.SUCCESS;
                indetailresp.setResponseCode(nibbsresp.getCode());
                indetailresp.setResponseText(nibbsresp.getMessage());
                weblogger.info(indetailresp);
            } else {

                nibbsresp = NIBBsResponseCodes.Invalid_transaction;
                indetailresp.setResponseCode(nibbsresp.getCode());
                indetailresp.setResponseText(nibbsresp.getMessage());
                weblogger.fatal(indetailresp);
                return indetailresp;
                //indetailresp.setMessage(result.split("/")[3]);
                //details(result.split("/")[3]);
            }

        } catch (ParseException ex) {
            indetailresp.setIsSuccessful(false);
            indetailresp.setMessage(ex.getMessage());
            weblogger.fatal(indetailresp, ex);
        }

        return indetailresp;

    }

    @WebMethod(operationName = "NonIndividualCustomer")
    public ObjectResponse NonIndividualCustomer(@WebParam(name = "nindetails") NonIndividualCustomerRequest nindetails) throws Exception {
        ObjectResponse nindetailresp = new ObjectResponse();
        weblogger.info("NonIndividualCustomer");
        try {
            String stringtohash = nindetails.getCustomerOpeningDate() + nindetails.getDateRegistered();

            String requesthash = nindetails.getHash();

            String intName = nindetails.getInterfaceName();

            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);
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

            if (hash.equals(requesthash) && (intName != null)) {

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

                weblogger.info("The items are " + items);
            } else {
                nibbsresp = NIBBsResponseCodes.Security_violation;
                nindetailresp.setResponseCode(nibbsresp.getCode());
                nindetailresp.setResponseText(nibbsresp.getMessage());
                weblogger.error(nindetailresp);
                return nindetailresp;

            }

            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {

                nibbsresp = NIBBsResponseCodes.SUCCESS;
                nindetailresp.setResponseCode(nibbsresp.getCode());
                nindetailresp.setResponseText(nibbsresp.getMessage());
                nindetailresp.setTransactionDate(sdf.format(trandate));
                weblogger.info(nindetailresp);
            } else {

                nibbsresp = NIBBsResponseCodes.Invalid_transaction;
                nindetailresp.setResponseCode(nibbsresp.getCode());
                nindetailresp.setResponseText(nibbsresp.getMessage());
                nindetailresp.setTransactionDate(sdf.format(trandate));
                weblogger.fatal(nindetailresp);
                return nindetailresp;
                //nindetailresp.setMessage(result.split("/")[3]);

            }

        } catch (ParseException ex) {
            nindetailresp.setIsSuccessful(false);
            nindetailresp.setMessage(ex.getMessage());
            weblogger.fatal(nindetailresp, ex);
        }

        return nindetailresp;
    }

    @WebMethod(operationName = "AbjLoans")
    public ObjectResponse AbjLoans(@WebParam(name = "loansdeposits") AbjLoansRequest loansdeposits) throws Exception {
        ObjectResponse loansdepositsresp = new ObjectResponse();
        weblogger.info("AbjLoans");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");

            String stringtohash = loansdeposits.getCustomer() + loansdeposits.getLoanApprovedDate();

            String requesthash = loansdeposits.getHash();

            String intName = loansdeposits.getInterfaceName();
            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);

            Date trandate = sdf.parse(loansdeposits.getValueDate());
            Date transdate = sdf.parse(loansdeposits.getMaturityDate());
            String[] ofsoptions = new String[]{"", "I", "PROCESS", "", "0"};
            String[] credentials = new String[]{Ofsuser, Ofspass};
            List<DataItem> items = new LinkedList<>();

            loansdeposits.setValueDate(ndf.format(trandate));
            loansdeposits.setMaturityDate(ndf.format(transdate));

            ofsParam param = new ofsParam();
            param.setCredentials(credentials);
            param.setOperation("LD.LOANS.AND.DEPOSITS");
            param.setVersion("ABJ.LOANS");
            param.setOptions(ofsoptions);
            param.setTransaction_id("");
            if (hash.equals(requesthash) && (intName != null)) {
                DataItem item = new DataItem();
                item.setItemHeader("LOAN.APPL.ID");
                item.setItemValues(new String[]{loansdeposits.getLoanApplicationID()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("CUSTOMER.ID");
                item.setItemValues(new String[]{loansdeposits.getCustomer()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("AGREEMENT.DATE");
                item.setItemValues(new String[]{loansdeposits.getLoanApprovedDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("LOAN.PURPOSE");
                item.setItemValues(new String[]{loansdeposits.getLoanPurpose()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("VALUE.DATE");
                item.setItemValues(new String[]{loansdeposits.getValueDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("FIN.MAT.DATE");
                item.setItemValues(new String[]{loansdeposits.getMaturityDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("MIS.ACCT.OFFICER");
                item.setItemValues(new String[]{loansdeposits.getAccountOfficer()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("L.ACC.OFFICER");
                item.setItemValues(new String[]{loansdeposits.getIntroducer()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("PROVISION.METHOD");
                item.setItemValues(new String[]{loansdeposits.getProvisionMethod()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("APPRVD.AMT");
                item.setItemValues(new String[]{loansdeposits.getLoanDisbursedAmount()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("LOAN.TYPE");
                item.setItemValues(new String[]{loansdeposits.getLoanType()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("REPAYMENT.TYPE");
                item.setItemValues(new String[]{loansdeposits.getRepaymentType()});
                items.add(item);

                param.setDataItems(items);
                weblogger.info("The items are " + items);

            } else {
                nibbsresp = NIBBsResponseCodes.Security_violation;
                loansdepositsresp.setResponseCode(nibbsresp.getCode());
                loansdepositsresp.setResponseText(nibbsresp.getMessage());
                loansdepositsresp.setTransactionDate(sdf.format(trandate));
                weblogger.error(loansdepositsresp);
                return loansdepositsresp;
            }

            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {
                nibbsresp = NIBBsResponseCodes.SUCCESS;
                loansdepositsresp.setResponseCode(nibbsresp.getCode());
                loansdepositsresp.setResponseText(nibbsresp.getMessage());
                loansdepositsresp.setTransactionDate(sdf.format(trandate));
                weblogger.info(loansdepositsresp);

            } else {

                nibbsresp = NIBBsResponseCodes.Invalid_transaction;
                loansdepositsresp.setResponseCode(nibbsresp.getCode());
                loansdepositsresp.setResponseText(nibbsresp.getMessage());
                loansdepositsresp.setTransactionDate(sdf.format(trandate));
                weblogger.fatal(loansdepositsresp);
                return loansdepositsresp;
            }

        } catch (ParseException ex) {
            loansdepositsresp.setIsSuccessful(false);
            loansdepositsresp.setMessage(ex.getMessage());
            weblogger.fatal(loansdepositsresp, ex);

        }

        return loansdepositsresp;

    }

    @WebMethod(operationName = "CashLoan")
    public ObjectResponse CashLoan(@WebParam(name = "icldeposit") IclCashLoanRequest icldeposit) throws Exception {
        ObjectResponse icldepositresp = new ObjectResponse();
        weblogger.info("CashLoan");
        try {

            String stringtohash = icldeposit.getCustomer() + icldeposit.getMaturityDate();

            String requesthash = icldeposit.getHash();

            String intName = icldeposit.getInterfaceName();
            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);
            List<DataItem> items = new LinkedList<>();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");

            Date trandate = sdf.parse(icldeposit.getValueDate());
            Date transdate = sdf.parse(icldeposit.getMaturityDate());
            String[] ofsoptions = new String[]{"", "I", "PROCESS", "", "0"};
            String[] credentials = new String[]{Ofsuser, Ofspass};

            icldeposit.setValueDate(ndf.format(trandate));
            icldeposit.setMaturityDate(ndf.format(transdate));

            ofsParam param = new ofsParam();
            param.setCredentials(credentials);
            param.setOperation("LD.LOANS.AND.DEPOSITS");
            param.setVersion("ICL.CASH.LOAN");
            param.setOptions(ofsoptions);
            param.setTransaction_id("");

            if (hash.equals(requesthash) && (intName != null)) {
                DataItem item = new DataItem();
                item.setItemHeader("LOAN.APPL.ID");
                item.setItemValues(new String[]{icldeposit.getLoanApplicationID()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("CUSTOMER.ID");
                item.setItemValues(new String[]{icldeposit.getCustomer()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("LOAN.PURPOSE");
                item.setItemValues(new String[]{icldeposit.getLoanPurpose()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("VALUE.DATE");
                item.setItemValues(new String[]{icldeposit.getValueDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("FIN.MAT.DATE");
                item.setItemValues(new String[]{icldeposit.getMaturityDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("MIS.ACCT.OFFICER");
                item.setItemValues(new String[]{icldeposit.getAccountOfficer()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("L.ACC.OFFICER");
                item.setItemValues(new String[]{icldeposit.getIntroducer()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("PROVISION.METHOD");
                item.setItemValues(new String[]{icldeposit.getProvisionMethod()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("APPRVD.AMT");
                item.setItemValues(new String[]{icldeposit.getLoanDisbursedAmount()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("LOAN.TYPE");
                item.setItemValues(new String[]{icldeposit.getLoanType()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("REPAYMENT.TYPE");
                item.setItemValues(new String[]{icldeposit.getRepaymentType()});
                items.add(item);

                param.setDataItems(items);

                weblogger.info("The items are " + items);

            } else {
                nibbsresp = NIBBsResponseCodes.Security_violation;
                icldepositresp.setResponseCode(nibbsresp.getCode());
                icldepositresp.setResponseText(nibbsresp.getMessage());
                icldepositresp.setTransactionDate(sdf.format(trandate));
                weblogger.error(icldepositresp);
                return icldepositresp;
            }

            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {
                nibbsresp = NIBBsResponseCodes.SUCCESS;
                icldepositresp.setResponseCode(nibbsresp.getCode());
                icldepositresp.setResponseText(nibbsresp.getMessage());
                icldepositresp.setTransactionDate(sdf.format(trandate));
                weblogger.info(icldepositresp);
            } else {

                nibbsresp = NIBBsResponseCodes.Invalid_transaction;
                icldepositresp.setResponseCode(nibbsresp.getCode());
                icldepositresp.setResponseText(nibbsresp.getMessage());
                icldepositresp.setTransactionDate(sdf.format(trandate));
                weblogger.fatal(icldepositresp);
                return icldepositresp;

            }
//
        } catch (ParseException ex) {
            icldepositresp.setIsSuccessful(false);
            icldepositresp.setMessage(ex.getMessage());

            weblogger.fatal(icldepositresp, ex);
        }

        return icldepositresp;

    }

    @WebMethod(operationName = "PublicFederalLoan")
    public ObjectResponse PublicFederalLoan(@WebParam(name = "pubdeposit") IclPublicFederalRequest pubdeposit) throws Exception {
        ObjectResponse pubdepositresp = new ObjectResponse();
        weblogger.info("PublicFederalLoan");
        try {

            String stringtohash = pubdeposit.getCustomer() + pubdeposit.getLoanApprovedDate();

            String requesthash = pubdeposit.getHash();

            String intName = pubdeposit.getInterfaceName();
            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);
            List<DataItem> items = new LinkedList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");

            Date trandate = sdf.parse(pubdeposit.getValueDate());
            Date transdate = sdf.parse(pubdeposit.getMaturityDate());
            Date tranndate = sdf.parse(pubdeposit.getRepaymentStartDate());
            Date tramdate = sdf.parse(pubdeposit.getLoanApprovedDate());

            String[] ofsoptions = new String[]{"", "I", "PROCESS", "", "0"};
            String[] credentials = new String[]{Ofsuser, Ofspass};

            pubdeposit.setValueDate(ndf.format(trandate));
            pubdeposit.setMaturityDate(ndf.format(transdate));
            pubdeposit.setRepaymentStartDate(ndf.format(tranndate));
            pubdeposit.setLoanApprovedDate(ndf.format(tramdate));
            ofsParam param = new ofsParam();
            param.setCredentials(credentials);
            param.setOperation("LD.LOANS.AND.DEPOSITS");
            param.setVersion("ICL.PUBLIC.FEDERAL1");
            param.setOptions(ofsoptions);
            param.setTransaction_id("");

            if (hash.equals(requesthash) && (intName != null)) {

                DataItem item = new DataItem();
                item.setItemHeader("LOAN.APPL.ID");
                item.setItemValues(new String[]{pubdeposit.getLoanApplicationID()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("CUSTOMER.ID");
                item.setItemValues(new String[]{pubdeposit.getCustomer()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("LOAN.PURPOSE");
                item.setItemValues(new String[]{pubdeposit.getLoanPurpose()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("VALUE.DATE");
                item.setItemValues(new String[]{pubdeposit.getValueDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("FIN.MAT.DATE");
                item.setItemValues(new String[]{pubdeposit.getMaturityDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("MIS.ACCT.OFFICER");
                item.setItemValues(new String[]{pubdeposit.getAccountOfficer()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("L.ACC.OFFICER");
                item.setItemValues(new String[]{pubdeposit.getIntroducer()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("PROVISION.METHOD");
                item.setItemValues(new String[]{pubdeposit.getProvisionMethod()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("APPRVD.AMT");
                item.setItemValues(new String[]{pubdeposit.getLoanDisbursedAmount()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("LOAN.TYPE");
                item.setItemValues(new String[]{pubdeposit.getLoanType()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("AGREEMENT.DATE");
                item.setItemValues(new String[]{pubdeposit.getLoanApprovedDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("INT.START.DT");
                item.setItemValues(new String[]{pubdeposit.getRepaymentStartDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("INT.ELEMENT");
                item.setItemValues(new String[]{pubdeposit.getPrincipalAmount()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("MEANS.PAY");
                item.setItemValues(new String[]{pubdeposit.getMeansOfPayment()});

                item = new DataItem();
                item.setItemHeader("REPAYMENT.PAY");
                item.setItemValues(new String[]{pubdeposit.getRepaymentType()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("AMOUNT.APPROVED");
                item.setItemValues(new String[]{pubdeposit.getAmountApproved()});
                items.add(item);

                items.add(item);

                param.setDataItems(items);
                weblogger.info("The items are " + items);

            } else {
                nibbsresp = NIBBsResponseCodes.Security_violation;
                pubdepositresp.setResponseCode(nibbsresp.getCode());
                pubdepositresp.setResponseText(nibbsresp.getMessage());
                pubdepositresp.setTransactionDate(sdf.format(trandate));
                weblogger.error(pubdepositresp);
                return pubdepositresp;

            }

            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {
                nibbsresp = NIBBsResponseCodes.SUCCESS;
                pubdepositresp.setResponseCode(nibbsresp.getCode());
                pubdepositresp.setResponseText(nibbsresp.getMessage());
                pubdepositresp.setTransactionDate(sdf.format(trandate));
                weblogger.info(pubdepositresp);

            } else {
                nibbsresp = NIBBsResponseCodes.Invalid_transaction;
                pubdepositresp.setResponseCode(nibbsresp.getCode());
                pubdepositresp.setResponseText(nibbsresp.getMessage());
                pubdepositresp.setTransactionDate(sdf.format(trandate));
                weblogger.fatal(pubdepositresp);
                return pubdepositresp;
            }

        } catch (ParseException ex) {
            pubdepositresp.setIsSuccessful(false);
            pubdepositresp.setMessage(ex.getMessage());
            weblogger.fatal(pubdepositresp, ex);
        }

        return pubdepositresp;

    }

    @WebMethod(operationName = "PublicStateLoan")
    public ObjectResponse PublicStateLoan(@WebParam(name = "statedeposit") IclPublicStateRequest statedeposit) throws Exception {
        ObjectResponse statedepositresp = new ObjectResponse();
        weblogger.info("PublicStateLoan");

        try {

            String stringtohash = statedeposit.getCustomer() + statedeposit.getLoanApprovedDate();

            String requesthash = statedeposit.getHash();

            String intName = statedeposit.getInterfaceName();
            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");

            Date trandate = sdf.parse(statedeposit.getValueDate());
            Date transdate = sdf.parse(statedeposit.getMaturityDate());
            Date tranndate = sdf.parse(statedeposit.getRepaymentStartDate());
            Date tramdate = sdf.parse(statedeposit.getLoanApprovedDate());

            String[] ofsoptions = new String[]{"", "I", "PROCESS", "", "0"};
            String[] credentials = new String[]{Ofsuser, Ofspass};
            List<DataItem> items = new LinkedList<>();

            statedeposit.setValueDate(ndf.format(trandate));
            statedeposit.setMaturityDate(ndf.format(transdate));
            statedeposit.setRepaymentStartDate(ndf.format(tranndate));
            statedeposit.setLoanApprovedDate(ndf.format(tramdate));
            ofsParam param = new ofsParam();
            param.setCredentials(credentials);
            param.setOperation("LD.LOANS.AND.DEPOSITS");
            param.setVersion("ICL.PUBLIC.STATE1");
            param.setOptions(ofsoptions);
            param.setTransaction_id("");

            if (hash.equals(requesthash) && (intName != null)) {
                DataItem item = new DataItem();
                item.setItemHeader("LOAN.APPL.ID");
                item.setItemValues(new String[]{statedeposit.getLoanApplicationID()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("CUSTOMER.ID");
                item.setItemValues(new String[]{statedeposit.getCustomer()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("LOAN.PURPOSE");
                item.setItemValues(new String[]{statedeposit.getLoanPurpose()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("VALUE.DATE");
                item.setItemValues(new String[]{statedeposit.getValueDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("FIN.MAT.DATE");
                item.setItemValues(new String[]{statedeposit.getMaturityDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("MIS.ACCT.OFFICER");
                item.setItemValues(new String[]{statedeposit.getAccountOfficer()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("L.ACC.OFFICER");
                item.setItemValues(new String[]{statedeposit.getIntroducer()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("PROVISION.METHOD");
                item.setItemValues(new String[]{statedeposit.getProvisionMethod()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("APPRVD.AMT");
                item.setItemValues(new String[]{statedeposit.getLoanDisbursedAmount()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("LOAN.TYPE");
                item.setItemValues(new String[]{statedeposit.getLoanType()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("AGREEMENT.DATE");
                item.setItemValues(new String[]{statedeposit.getLoanApprovedDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("INT.START.DT");
                item.setItemValues(new String[]{statedeposit.getRepaymentStartDate()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("INT.ELEMENT");
                item.setItemValues(new String[]{statedeposit.getPrincipalAmount()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("MEANS.PAY");
                item.setItemValues(new String[]{statedeposit.getMeansOfPayment()});

                item = new DataItem();
                item.setItemHeader("REPAYMENT.TYPE");
                item.setItemValues(new String[]{statedeposit.getRepaymentType()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("AMOUNT.APPROVED");
                item.setItemValues(new String[]{statedeposit.getAmountApproved()});
                items.add(item);

                items.add(item);

                param.setDataItems(items);
                weblogger.info("The items are " + items);

            } else {
                nibbsresp = NIBBsResponseCodes.Security_violation;
                statedepositresp.setResponseCode(nibbsresp.getCode());
                statedepositresp.setResponseText(nibbsresp.getMessage());
                statedepositresp.setTransactionDate(sdf.format(trandate));
                weblogger.error(statedepositresp);
                return statedepositresp;
            }

            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {

                nibbsresp = NIBBsResponseCodes.SUCCESS;
                statedepositresp.setResponseCode(nibbsresp.getCode());
                statedepositresp.setResponseText(nibbsresp.getMessage());
                statedepositresp.setTransactionDate(sdf.format(trandate));
                weblogger.info(statedepositresp);

            } else {
                nibbsresp = NIBBsResponseCodes.Invalid_transaction;
                statedepositresp.setResponseCode(nibbsresp.getCode());
                statedepositresp.setResponseText(nibbsresp.getMessage());
                statedepositresp.setTransactionDate(sdf.format(trandate));
                weblogger.fatal(statedepositresp);
                return statedepositresp;
            }

        } catch (ParseException ex) {
            statedepositresp.setIsSuccessful(false);
            statedepositresp.setMessage(ex.getMessage());
            weblogger.fatal(statedepositresp, ex);

        }

        return statedepositresp;

    }

    @WebMethod(operationName = "ChequeCollection")
    public ObjectResponse ChequeCollection(@WebParam(name = "chequecollect") ChequeCollectionRequest chequecollect) throws Exception {
        ObjectResponse chequecollectresp = new ObjectResponse();
        weblogger.info("ChequeCollection");

        try {

            String stringtohash = chequecollect.getChequeAcctNo() + chequecollect.getCustomerName();

            String requesthash = chequecollect.getHash();

            String intName = chequecollect.getInterfaceName();
            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");

            Date trandate = sdf.parse(chequecollect.getDateChequePresented());
            Date transdate = sdf.parse(chequecollect.getDateCollected());

            String[] ofsoptions = new String[]{"", "I", "PROCESS", "", "0"};
            String[] credentials = new String[]{Ofsuser, Ofspass};
            List<DataItem> items = new LinkedList<>();

            chequecollect.setDateChequePresented(ndf.format(trandate));
            chequecollect.setDateCollected(ndf.format(transdate));

            ofsParam param = new ofsParam();
            param.setCredentials(credentials);
            param.setOperation("ICL.CHEQUE.COLLECTN.RECOVERY");
            param.setVersion("MAIN");
            param.setOptions(ofsoptions);
            param.setTransaction_id("");

            if (hash.equals(requesthash) && (intName != null)) {
                DataItem item = new DataItem();
                item.setItemHeader("LD.NUMBER");
                item.setItemValues(new String[]{chequecollect.getCustomerLoanNo()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("T24.ACCT.NO");
                item.setItemValues(new String[]{chequecollect.getT24AcctNo()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("CUS.NAME");
                item.setItemValues(new String[]{chequecollect.getCustomerName()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("BRANCH.NAME");
                item.setItemValues(new String[]{chequecollect.getCustomerBankBranch()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("CHEQUE.NUMBER");
                item.setItemValues(new String[]{chequecollect.getChequeSerialNumber()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("ACCOUNT");
                item.setItemValues(new String[]{chequecollect.getChequeAcctNo()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("PRESENTED.DATE");
                item.setItemValues(new String[]{chequecollect.getDateCollected()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("REPRESENTED.DD");
                item.setItemValues(new String[]{chequecollect.getDateChequePresented()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("SIGNED.BY");
                item.setItemValues(new String[]{chequecollect.getChequeSignedBy()});
                items.add(item);

                item = new DataItem();
                item.setItemHeader("COMMENT");
                item.setItemValues(new String[]{chequecollect.getComment()});
                items.add(item);

                items.add(item);

                param.setDataItems(items);
                weblogger.info("The items are " + items);

            } else {
                nibbsresp = NIBBsResponseCodes.Security_violation;
                chequecollectresp.setResponseCode(nibbsresp.getCode());
                chequecollectresp.setResponseText(nibbsresp.getMessage());
                chequecollectresp.setTransactionDate(sdf.format(trandate));
                weblogger.error(chequecollectresp);
                return chequecollectresp;
            }

            String ofstring = t24.generateOFSTransactString(param);

            String result = t24.PostMsg(ofstring);

            if (t24.IsSuccessful(result)) {

                nibbsresp = NIBBsResponseCodes.SUCCESS;
                chequecollectresp.setResponseCode(nibbsresp.getCode());
                chequecollectresp.setResponseText(nibbsresp.getMessage());
                chequecollectresp.setTransactionDate(sdf.format(trandate));
                weblogger.info(chequecollectresp);

            } else {
                nibbsresp = NIBBsResponseCodes.Invalid_transaction;
                chequecollectresp.setResponseCode(nibbsresp.getCode());
                chequecollectresp.setResponseText(nibbsresp.getMessage());
                chequecollectresp.setTransactionDate(sdf.format(trandate));
                weblogger.fatal(chequecollectresp);
                return chequecollectresp;
            }

        } catch (ParseException ex) {
            chequecollectresp.setIsSuccessful(false);
            chequecollectresp.setMessage(ex.getMessage());
            weblogger.fatal(chequecollectresp, ex);

        }

        return chequecollectresp;

    }

}
