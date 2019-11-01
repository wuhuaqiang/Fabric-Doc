package com.certusnet.fabric.chaincode;

import com.certusnet.fabric.chaincode.bankmaster.domain.EncryptedOffer;
import com.certusnet.fabric.chaincode.bankmaster.domain.EncryptedOfferList;
import com.certusnet.fabric.chaincode.bankmaster.domain.QualifiedOffer;
import com.certusnet.fabric.chaincode.bankmaster.domain.QualifiedOfferList;
import com.certusnet.fabric.chaincode.common.util.DateTimeUtils;
import com.certusnet.fabric.chaincode.common.util.JsonUtils;
import com.certusnet.fabric.chaincode.common.util.MD5Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TestUtil {
    public static void main(String[] args) {
        EncryptedOfferList encryptedOfferList = new EncryptedOfferList();
        List<EncryptedOffer> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            EncryptedOffer encryptedOffer = new EncryptedOffer();
            encryptedOffer.setId(UUID.randomUUID().toString());
            encryptedOffer.setNumber(String.valueOf(random.nextDouble()));
            encryptedOffer.setPrice(String.valueOf(random.nextDouble()));
            encryptedOffer.setStatus(String.valueOf(i % 2));
            list.add(encryptedOffer);
        }
        encryptedOfferList.setId(UUID.randomUUID().toString());
        encryptedOfferList.setTime(DateTimeUtils.formatNow());
        encryptedOfferList.setFlag("TRUE");
        encryptedOfferList.setEncryptedOfferList(list);
        String s = JsonUtils.object2Json(encryptedOfferList);
        EncryptedOfferList encryptedOfferList1 = JsonUtils.json2Object(s, EncryptedOfferList.class);
        List<EncryptedOffer> encryptedOfferList2 = encryptedOfferList1.getEncryptedOfferList();
        for (EncryptedOffer encryptedOffer : encryptedOfferList2) {
            System.out.println(encryptedOffer.toString());
        }
        System.out.println(MD5Utils.getMD5("20", "urAD"));
        System.out.println(MD5Utils.getMD5("0.80", "urAD"));
        System.out.println(s);
        System.out.println(testQualifiedOffer());
    }

    private static String testQualifiedOffer() {
        QualifiedOfferList qualifiedOfferList = new QualifiedOfferList();
        List<QualifiedOffer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            QualifiedOffer qualifiedOffer = new QualifiedOffer();
            qualifiedOffer.setId(UUID.randomUUID().toString());
            qualifiedOffer.setStatus("o");
            qualifiedOffer.setNumber("123");
            qualifiedOffer.setPrice("123.12");
            list.add(qualifiedOffer);
        }
        qualifiedOfferList.setId(UUID.randomUUID().toString());
        qualifiedOfferList.setFlag("TRUE");
        qualifiedOfferList.setTime(DateTimeUtils.formatNow());
        qualifiedOfferList.setQualifiedOfferList(list);
        return JsonUtils.object2Json(qualifiedOfferList);
    }
}
