/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package primera.service.enquiry;

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
    Enquiry enq = new Enquiry();
    String ID = enq.getID();
    String VDate = enq.getValueDate();
    String BookDate = enq.getBookingDate();
    String ProcessDate = enq.getProcessingDate();
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
    public EnquiryResponse FTHistory(@WebParam(name = "FTReferenceID") String FTReferenceID, @WebParam(name = "operand") String operand, @WebParam(name = "hash") String hashstring) throws Exception {

        EnquiryResponse ftresp = new EnquiryResponse();
        weblogger.info("FTHistory");

        try {

            String stringtohash = FTReferenceID;

            String requesthash = hashstring;

            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);

            if (hash.equals(requesthash)) {
                ArrayList<List<String>> result = t24.getOfsData("FT.HIST", Ofsuser, Ofspass, "@ID:EQ=" + FTReferenceID.trim());
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                dresp = ResponseCodes.SUCCESS;
                ftresp.setResponseCode(dresp.getCode());
                ftresp.setResponseText(dresp.getMessage());
                //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
            } else {
                dresp = ResponseCodes.Security_violation;
                ftresp.setResponseCode(dresp.getCode());
                ftresp.setResponseText(dresp.getMessage());

                weblogger.error(ftresp);
                return ftresp;
            }

        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            ftresp.setResponseCode(dresp.getCode());
            ftresp.setResponseText(dresp.getMessage());
            ftresp.setIsSuccessful(false);
            ftresp.setMessage(d.getMessage());

        }
        return ftresp;
    }

    @WebMethod(operationName = "FTNarrate")
    public EnquiryResponse FTNarrate(@WebParam(name = "FTReferenceID") String FTReferenceID, @WebParam(name = "operand") String operand, @WebParam(name = "hash") String hashstring) throws Exception {
        EnquiryResponse ftnresp = new EnquiryResponse();

        weblogger.info("FtNarrateRequest");

        try {
            String stringtohash = FTReferenceID;

            String requesthash = hashstring;

            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);

            if (hash.equals(requesthash)) {
                ArrayList<List<String>> result = t24.getOfsData("FT.NARRATE", Ofsuser, Ofspass, "@ID:EQ=" + FTReferenceID.trim());
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                dresp = ResponseCodes.SUCCESS;
                ftnresp.setResponseCode(dresp.getCode());
                ftnresp.setResponseText(dresp.getMessage());
                //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
            } else {
                dresp = ResponseCodes.Security_violation;
                ftnresp.setResponseCode(dresp.getCode());
                ftnresp.setResponseText(dresp.getMessage());

                weblogger.error(ftnresp);
                return ftnresp;
            }

        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            ftnresp.setResponseCode(dresp.getCode());
            ftnresp.setResponseText(dresp.getMessage());
            ftnresp.setIsSuccessful(false);
            ftnresp.setMessage(d.getMessage());

        }

        return ftnresp;
    }

    @WebMethod(operationName = "CustomerAmend")
    public EnquiryResponse CustomerAmend(@WebParam(name = "CustomerID") String CustomerID, @WebParam(name = "operand") String operand, @WebParam(name = "hash") String hashstring) throws Exception {
        EnquiryResponse curesp = new EnquiryResponse();

        weblogger.info("CustomerAmend");
        try {
            String stringtohash = CustomerID;

            String requesthash = hashstring;

            String hash = options.get_SHA_512_Hash(stringtohash, APIKey);

            if (hash.equals(requesthash)) {
                ArrayList<List<String>> result = t24.getOfsData("CUSTOMER.NAU.AMEND", Ofsuser, Ofspass, "@ID:EQ=" + CustomerID.trim());
                List<String> headers = result.get(0);

                if (headers.size() != result.get(1).size()) {

                    throw new Exception(result.get(1).get(0));

                }
                dresp = ResponseCodes.SUCCESS;
                curesp.setResponseCode(dresp.getCode());
                curesp.setResponseText(dresp.getMessage());
                //ftreq.setCrAcctNo(result.get(1).get(0).replace("\"", ""));
            } else {
                dresp = ResponseCodes.Security_violation;
                curesp.setResponseCode(dresp.getCode());
                curesp.setResponseText(dresp.getMessage());

                weblogger.error(curesp);
                return curesp;
            }
        } catch (Exception d) {
            dresp = ResponseCodes.Invalid_transaction;
            curesp.setIsSuccessful(false);
            curesp.setMessage(d.getMessage());
            curesp.setResponseCode(dresp.getCode());
            curesp.setResponseText(dresp.getMessage());
            weblogger.error(curesp);

        }

        return curesp;

    }

}
