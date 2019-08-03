/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.common;

import org.json.JSONObject;

/**
 *
 * @author adm
 */
public class ValidateMessageFields {

    public JSONObject ValidateMessage(JSONObject channelRequest) {
        int procode = Integer.parseInt(channelRequest.get("field3").toString());
        JSONObject validatetxn = new JSONObject();

        switch (procode) {
            case 110000:
                validatetxn = ValidateLogin(channelRequest);
                break;
            case 120000:
                validatetxn = ValidateRegistration(channelRequest);
                break;
            case 310000:
            case 380000:
            case 370000:
            case 600000:
                validatetxn = ValidateInquiry(channelRequest);
                break;
            case 400000:
            case 210000:
            case 10000:
                validatetxn = ValidateFT(channelRequest);
                break;
            case 430000:
            case 500000:
                validatetxn = ValidateWalletToMobile(channelRequest);
                break;
            case 420000:
                validatetxn = ValidateAirtimePurchase(channelRequest);
                break;
            case 450000:
                validatetxn = ValidateMobileToWallet(channelRequest);
                break;
            case 620000:
                validatetxn = ValidateRemmittanceInit(channelRequest);
                break;
            case 630000:
                validatetxn = ValidateRemmittanceFulfill(channelRequest);
                break;
            default:
                validatetxn.put("field99", "00");
                break;
        }
        return validatetxn;
    }

    public JSONObject ValidateLogin(JSONObject channelRequest) {
        channelRequest.put("field99", "00");
        if (!channelRequest.has("field0")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field0(MTI)");
        }
        if (!channelRequest.has("field2") && !channelRequest.has("field101")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field2(Wallet Account)");
        }
        if (!channelRequest.has("field3")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field3(Processing Code)");
        } else if (!channelRequest.has("field7")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field7(Transactions date format.. yymmddhhmmss)");
        } else if (!channelRequest.has("field11")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field11(STAN..i.e. hhmmss)");
        } else if (!channelRequest.has("field32")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field32(Channel)");
        } else if (!channelRequest.has("field37")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field37(RRN)");
        }
        return channelRequest;
    }

    public JSONObject ValidateRegistration(JSONObject channelRequest) {
        channelRequest.put("field99", "00");
        if (!channelRequest.has("field0")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field0(MTI)");
        }
        if (!channelRequest.has("field2")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field2(Wallet Account)");
        }
        if (!channelRequest.has("field3")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field3(Processing Code)");
        } else if (!channelRequest.has("field7")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field7(Transactions date format.. yymmddhhmmss)");
        } else if (!channelRequest.has("field11")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field11(STAN..i.e. hhmmss)");
        } else if (!channelRequest.has("field32")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field32(Channel)");
        } else if (!channelRequest.has("field37")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field37(RRN)");
        } else if (!channelRequest.has("field61")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field61(Sir Name)");
        } else if (!channelRequest.has("field62")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field62(First Name)");
        } //else if (!channelRequest.has("field63")) {
        //            channelRequest.put("field99", "57");
        //            channelRequest.put("field48", "Missing field63(Last Name)");
        //        } 
        else if (!channelRequest.has("field70")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field70(ID No)");
        } /*else if (!channelRequest.has("field71")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field71(Email address)");
        }*/ else if (!channelRequest.has("field72")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field72(PIN)");
        } else if (!channelRequest.has("field73")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field73(Language en(English) ki(Kiswahili))");
        } else if (!channelRequest.has("field74")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field74(IMSI))");
        } else if (!channelRequest.has("field75")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field75(DOB))");
        }
        /*else if (!channelRequest.has("field76")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field76(Gender (Male/Female))");
        }*/
 /*else if (!channelRequest.has("field77")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field77(Town))");
        }*/
 /*else if (!channelRequest.has("field78")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field78(Postal Code))");
        }*/
        return channelRequest;
    }

    public JSONObject ValidateInquiry(JSONObject channelRequest) {
        channelRequest.put("field99", "00");
        if (!channelRequest.has("field0")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field0(MTI)");
        }
        if (!channelRequest.has("field2")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field2(Wallet Account)");
        }
        if (!channelRequest.has("field3")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field3(Processing Code)");
        } else if (!channelRequest.has("field4")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field4(Amount default 0)");
        } else if (!channelRequest.has("field7")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field7(Transactions date format.. yymmddhhmmss)");
        } else if (!channelRequest.has("field11")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field11(STAN..i.e. hhmmss)");
        } else if (!channelRequest.has("field24")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field24(Transaction Type..default MM)");
        } else if (!channelRequest.has("field32")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field32(Channel)");
        } else if (!channelRequest.has("field37")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field37(RRN)");
        } else if (!channelRequest.has("field49")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field49(Currency)");
        } else if (!channelRequest.has("field68")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field68(Narration)");
        } else if (!channelRequest.has("field102")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field102(DR Account)");
        }
        return channelRequest;
    }

    public JSONObject ValidateFT(JSONObject channelRequest) {
        channelRequest.put("field99", "00");
        if (!channelRequest.has("field0")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field0(MTI)");
        }
        if (!channelRequest.has("field2")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field2(Wallet Account)");
        }
        if (!channelRequest.has("field3")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field3(Processing Code)");
        } else if (!channelRequest.has("field4")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field4(Amount default 0)");
        } else if (!channelRequest.has("field7")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field7(Transactions date format.. yymmddhhmmss)");
        } else if (!channelRequest.has("field11")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field11(STAN..i.e. hhmmss)");
        } else if (!channelRequest.has("field24")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field24(Transaction Type..default MM)");
        } else if (!channelRequest.has("field32")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field32(Channel)");
        } else if (!channelRequest.has("field37")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field37(RRN)");
        } else if (!channelRequest.has("field49")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field49(Currency)");
        } else if (!channelRequest.has("field68")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field68(Narration)");
        } else if (!channelRequest.has("field102") && !channelRequest.has("field100")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field102(DR Account)");
        } else if (!channelRequest.has("field103") && !channelRequest.has("field100")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field103(CR Account)");
        }
        if (!channelRequest.has("field100")) {
            if (channelRequest.get("field102").equals(channelRequest.get("field103"))) {
                channelRequest.put("field99", "57");
                channelRequest.put("field48", "Debit and Credit Account should not be similar(field102 and field103)");
            }
        }
        return channelRequest;
    }

    public JSONObject ValidateWalletToMobile(JSONObject channelRequest) {
        if (channelRequest.get("field3").equals("430000")) {
            channelRequest.put("field4", "10");
        }
        channelRequest.put("field99", "00");
        if (!channelRequest.has("field0")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field0(MTI)");
        }
        if (!channelRequest.has("field2")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field2(Wallet Account)");
        }
        if (!channelRequest.has("field3")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field3(Processing Code)");
        } else if (!channelRequest.has("field4")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field4(Amount default 0)");
        } else if (!channelRequest.has("field7")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field7(Transactions date format.. yymmddhhmmss)");
        } else if (!channelRequest.has("field11")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field11(STAN..i.e. hhmmss)");
        } else if (!channelRequest.has("field24")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field24(Transaction Type..default MM)");
        } else if (!channelRequest.has("field32")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field32(Channel)");
        } else if (!channelRequest.has("field37")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field37(RRN)");
        } else if (!channelRequest.has("field49")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field49(Currency)");
        } else if (!channelRequest.has("field65")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field65(Mobile Number/Biller Number)");
        } else if (!channelRequest.has("field68")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field68(Narration)");
        } else if (!channelRequest.has("field71")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field71(Service Name)");
        } else if (!channelRequest.has("field100")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field100(Mobile FT type..i.e MPESA)");
        } else if (!channelRequest.has("field102")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field102(DR Account)");
        }
        return channelRequest;
    }

    public JSONObject ValidateMobileToWallet(JSONObject channelRequest) {
        channelRequest.put("field4", "10");
        channelRequest.put("field99", "00");
        if (!channelRequest.has("field0")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field0(MTI)");
        }
        if (!channelRequest.has("field2")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field2(Wallet Account)");
        }
        if (!channelRequest.has("field3")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field3(Processing Code)");
        } else if (!channelRequest.has("field4")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field4(Amount default 0)");
        } else if (!channelRequest.has("field7")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field7(Transactions date format.. yymmddhhmmss)");
        } else if (!channelRequest.has("field11")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field11(STAN..i.e. hhmmss)");
        } else if (!channelRequest.has("field24")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field24(Transaction Type..default MM)");
        } else if (!channelRequest.has("field32")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field32(Channel)");
        } else if (!channelRequest.has("field37")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field37(RRN)");
        } else if (!channelRequest.has("field49")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field49(Currency)");
        } else if (!channelRequest.has("field65")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field65(Mobile Number/Biller Number)");
        } else if (!channelRequest.has("field68")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field68(Narration)");
        } else if (!channelRequest.has("field71")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field71(Service Name)");
        } else if (!channelRequest.has("field100")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field100(Mobile FT type..i.e MPESA)");
        } else if (!channelRequest.has("field103")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field103(CR Account)");
        }
        return channelRequest;
    }

    public JSONObject ValidateAirtimePurchase(JSONObject channelRequest) {
        channelRequest.put("field99", "00");
        if (!channelRequest.has("field0")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field0(MTI)");
        }
        if (!channelRequest.has("field2")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field2(Wallet Account)");
        }
        if (!channelRequest.has("field3")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field3(Processing Code)");
        } else if (!channelRequest.has("field4")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field4(Amount default 0)");
        } else if (!channelRequest.has("field7")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field7(Transactions date format.. yymmddhhmmss)");
        } else if (!channelRequest.has("field11")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field11(STAN..i.e. hhmmss)");
        } else if (!channelRequest.has("field24")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field24(Transaction Type..default MM)");
        } else if (!channelRequest.has("field32")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field32(Channel)");
        } else if (!channelRequest.has("field37")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field37(RRN)");
        } else if (!channelRequest.has("field49")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field49(Currency)");
        } else if (!channelRequest.has("field65")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field65(Mobile Number/Biller Number)");
        } else if (!channelRequest.has("field68")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field68(Narration)");
        } else if (!channelRequest.has("field71")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field71(Service Name)");
        } else if (!channelRequest.has("field102")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field102(DR Account)");
        }
        return channelRequest;
    }

    public JSONObject ValidateRemmittanceInit(JSONObject channelRequest) {
        channelRequest.put("field99", "00");
        if (!channelRequest.has("field0")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field0(MTI)");
        }
        if (!channelRequest.has("field2")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field2(Wallet Account)");
        }
        if (!channelRequest.has("field3")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field3(Processing Code)");
        } else if (!channelRequest.has("field4")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field4(Amount default 0)");
        } else if (!channelRequest.has("field7")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field7(Transactions date format.. yymmddhhmmss)");
        } else if (!channelRequest.has("field11")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field11(STAN..i.e. hhmmss)");
        } else if (!channelRequest.has("field24")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field24(Transaction Type..default MM)");
        } else if (!channelRequest.has("field32")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field32(Channel)");
        } else if (!channelRequest.has("field37")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field37(RRN)");
        } else if (!channelRequest.has("field49")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field49(Currency)");
        } else if (!channelRequest.has("field65")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field65(Recipient Mobile No)");
        } else if (!channelRequest.has("field68")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field68(Narration)");
        } else if (!channelRequest.has("field102")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field102(DR Account)");
        }
        return channelRequest;
    }

    public JSONObject ValidateRemmittanceFulfill(JSONObject channelRequest) {
        channelRequest.put("field99", "00");
        if (!channelRequest.has("field0")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field0(MTI)");
        }
        if (!channelRequest.has("field2")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field2(Wallet Account)");
        }
        if (!channelRequest.has("field3")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field3(Processing Code)");
        } else if (!channelRequest.has("field4")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field4(Amount default 0)");
        } else if (!channelRequest.has("field7")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field7(Transactions date format.. yymmddhhmmss)");
        } else if (!channelRequest.has("field11")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field11(STAN..i.e. hhmmss)");
        } else if (!channelRequest.has("field24")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field24(Transaction Type..default MM)");
        } else if (!channelRequest.has("field32")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field32(Channel)");
        } else if (!channelRequest.has("field37")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field37(RRN)");
        } else if (!channelRequest.has("field49")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field49(Currency)");
        } else if (!channelRequest.has("field65")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field65(Recipient Mobile No)");
        } else if (!channelRequest.has("field68")) {
            channelRequest.put("field99", "57");
            channelRequest.put("field48", "Missing field68(Narration)");
        }
        return channelRequest;
    }
}
