package encryption;

import com.google.protobuf.InvalidProtocolBufferException;
import com.webank.wedpr.common.Utils;
import com.webank.wedpr.common.WedprException;
import com.webank.wedpr.scd.*;
import com.webank.wedpr.scd.proto.AttributeDict;
import com.webank.wedpr.scd.proto.Predicate;
import com.webank.wedpr.scd.proto.StringToStringPair;
import com.webank.wedpr.scd.proto.VerificationRuleSet;
import domain.User;

import java.util.*;

public class EncryptUtils {
    // issuer 颁发证书
    private static IssuerClient issuerClient = null;
    // user 用户
    private static UserClient userClient = null;
    // verifier 验证证书
    private static VerifierClient verifierClient = null;

    //所有定义的字段
    private static final String USER_ID = "userId";

    private static final String NAME = "name";
    private static final String ID_CARD = "idCard";
    private static final String LOCATION = "location";
    private static final String APPLICATION_TITLE = "applicationTitle";
    private static final String USER_INFO = "userInfo";
    private static final String SCD_MEDICAL_RECORD = "SCDMedicalRecord";
    private static final String VCL_ASSET = "VCLAsset";
    private static final String RECEIVE_ACCOUNT = "receiveAccount";
    private static final String TIME = "time";
    private static final String TARGET = "target";

    //默认user id
    private static final String DEFAULT_USER_ID = "userId";

    //TODO: 全改小写
//    private static final String USER_ID = "user_id";
//    private static final String NAME = "name";
//    private static final String ID_CARD = "id_card";
//    private static final String LOCATION = "location";
//    private static final String APPLICATION_TITLE = "application_title";
//    private static final String USER_INFO = "user_info";
//    private static final String SCD_MEDICAL_RECORD = "scd_medical_record";
//    private static final String VCL_ASSET = "vcl_asset";
//    private static final String RECEIVE_ACCOUNT = "receive_account";
//    private static final String TIME = "time";
//    private static final String TARGET = "target";

    static {
        issuerClient = new IssuerClient();
        userClient = new UserClient();
        verifierClient = new VerifierClient();
    }

    public static Map<String, String> encrypt(User user) {

        //加密字段
        List<String> schema = Arrays.asList(NAME, ID_CARD, LOCATION, APPLICATION_TITLE, USER_INFO,
                SCD_MEDICAL_RECORD, VCL_ASSET, RECEIVE_ACCOUNT, TIME, TARGET);
        //权威机构认证结果
        IssuerResult issuerResult = null;
        try {
            issuerResult = issuerClient.makeCertificateTemplate(schema);
        } catch (WedprException e) {
            //TODO: 更改异常捕获方式
            System.err.println("ERROR: Cannot create template!");
//            e.printStackTrace();
        }
        //证书模板
        assert issuerResult != null;
        String certificateTemplate = issuerResult.certificateTemplate;
        //私钥模板
        String templatePrivateKey = issuerResult.templatePrivateKey;


        //用户输入的信息
        Map<String, String> certificateDataInput = new HashMap<>();

        //存放转换为数字的各个信息
        certificateDataInput.put(NAME, TransformUtils.transformStringToInt(user.getName()));
        certificateDataInput.put(ID_CARD, TransformUtils.transformStringToInt(user.getIdCard()));
        certificateDataInput.put(LOCATION, TransformUtils.transformStringToInt(user.getLocation()));
        certificateDataInput.put(APPLICATION_TITLE, TransformUtils.transformStringToInt(user.getApplicationTitle()));
        certificateDataInput.put(USER_INFO, TransformUtils.transformStringToInt(user.getUserInfo()));
        certificateDataInput.put(SCD_MEDICAL_RECORD, TransformUtils.transformStringToInt(user.getSCDMedicalRecord()));
        certificateDataInput.put(VCL_ASSET, user.getVCLAsset());
        certificateDataInput.put(RECEIVE_ACCOUNT, TransformUtils.transformStringToInt(user.getReceiveAccount()));
        certificateDataInput.put(TIME, TransformUtils.transformStringToInt(user.getTime()));
        certificateDataInput.put(TARGET, TransformUtils.transformStringToInt(user.getTarget()));

        //加密的数据
        String certificateData = userClient.encodeAttributeDict(certificateDataInput);
        //填写模板
        UserResult userResult = null;
        try {
            userResult = userClient.fillCertificate(certificateData, certificateTemplate);
        } catch (WedprException e) {
            //TODO: 更改异常捕获方式
            System.err.println("ERROR: Cannot fill template!");
            //e.printStackTrace();
        }

        //用户请求证书
        assert userResult != null;
        String signCertificateRequest = userResult.signCertificateRequest;

        String userPrivateKey = userResult.userPrivateKey;
        //用户混淆因子
        String certificateSecretsBlindingFactors = userResult.certificateSecretsBlindingFactors;
        //用户时间戳
        String userNonce = userResult.userNonce;

        //权威机构通过用户签名证书请求签名证书
        try {
            issuerResult =
                    issuerClient.signCertificate(
                            certificateTemplate,
                            templatePrivateKey,
                            signCertificateRequest,

                            //Fixme: 待定
                            DEFAULT_USER_ID,

                            userNonce);
        } catch (WedprException e) {
            //TODO: 更改异常捕获方式
            System.err.println("ERROR: Cannot sign certificate!");
            //e.printStackTrace();
        }

        //发放证书签名
        String certificateSignature = issuerResult.certificateSignature;
        //发放时间戳
        String issuerNonce = issuerResult.issuerNonce;

        //用户混淆证书签名防止跟踪
        try {
            userResult =
                    userClient.blindCertificateSignature(
                            certificateSignature,
                            certificateData,
                            certificateTemplate,
                            userPrivateKey,
                            certificateSecretsBlindingFactors,
                            issuerNonce);
        } catch (WedprException e) {
            //TODO:混淆异常的捕获
            System.err.println("ERROR: Cannot blind the certificate!");
            e.printStackTrace();

        }
        //混淆后的签名
        String blindedCertificateSignature = userResult.certificateSignature;
        Map<String, String> userMap = new HashMap<>();
        //需用户保存
        userMap.put("userPrivateKey", userPrivateKey);
        userMap.put("blindedCertificateSignature", blindedCertificateSignature);
        //可明文保存
        userMap.put("certificateData", certificateData);
        userMap.put("certificateTemplate", certificateTemplate);
        return userMap;
    }

    public static Map<String, String> selectiveDisclosure(
            Map<String, String> userMap, List<String> fieldList) {
        VerificationRuleSet.Builder verificationRuleSetBuilder = VerificationRuleSet.newBuilder();
        //将要披露的字段添加到builder
        for (String name : fieldList) {
            verificationRuleSetBuilder.addRevealedAttributeName(name);
        }
        //进行编码处理
        String encodedVerificationRuleSet =
                verifierClient.protoToEncodedString(verificationRuleSetBuilder.build());

        String verificationNonce = null;
        try {
            verificationNonce = verifierClient.getVerificationNonce().verificationNonce;
        } catch (WedprException e) {
            //TODO: 更改异常捕获方式
            System.err.println("ERROR: Cannot get verification nonce!");
            //e.printStackTrace();
        }

        UserResult userResult =
                null;
        try {
            userResult = userClient.proveSelectiveDisclosure(encodedVerificationRuleSet,
                    userMap.get("blindedCertificateSignature"),
                    userMap.get("certificateData"),
                    userMap.get("certificateTemplate"),
                    userMap.get("userPrivateKey"),
                    verificationNonce
            );
        } catch (WedprException e) {
            //TODO: 更改异常捕获方式
            System.err.println("ERROR: Cannot prove user identity!");
            //e.printStackTrace();
        }
        assert userResult != null;
        String verifyRequest = userResult.verifyRequest;
        VerifierResult verifierResult = null;
        try {
            verifierResult = verifierClient.getRevealedAttributes(verifyRequest);
        } catch (WedprException e) {
            //TODO: 更改异常捕获方式
            System.err.println("ERROR: Cannot reveal the field!");
            //e.printStackTrace();
        }
        assert verifierResult != null;
        String encodedRevealedCertificateData = verifierResult.revealedAttributeDict;
        AttributeDict revealedCertificateData =
                null;
        try {
            revealedCertificateData = AttributeDict.parseFrom(Utils.stringToBytes(encodedRevealedCertificateData));
        } catch (InvalidProtocolBufferException e) {
            //TODO: 更改异常捕获方式
            System.err.println("ERROR: Cannot parse the field!");
            //e.printStackTrace();
        }
        assert revealedCertificateData != null;
        //获取存放披露数据的集合
        List<StringToStringPair> pairList = revealedCertificateData.getPairList();
        Iterator<StringToStringPair> pairIterator = pairList.iterator();
        Map<String, String> finMap = new HashMap<>();
        while (pairIterator.hasNext()) {
            StringToStringPair pair = pairIterator.next();
            //将披露数据进行数字串到字符串的转换处理
            finMap.put(pair.getKey(), TransformUtils.transformIntToString(pair.getValue()));
        }
        return finMap;
    }

    public static boolean verifyField(Map<String, String> userMap, int value) {
        VerificationRuleSet.Builder verificationRuleSetBuilder = VerificationRuleSet.newBuilder();
        Predicate predicate =
                Predicate.newBuilder()
                        .setAttributeName(VCL_ASSET)
                        .setPredicateType(PredicateType.LT.name())
                        .setPredicateValue(value)
                        .build();
        verificationRuleSetBuilder.addAttributePredicate(predicate);
        //需要导入protobuf包
        String encodedVerificationRuleSet =
                verifierClient.protoToEncodedString(verificationRuleSetBuilder.build());

        String verificationNonce = null;
        try {
            verificationNonce = verifierClient.getVerificationNonce().verificationNonce;
        } catch (WedprException e) {
            //TODO: 更改异常捕获方式
            System.err.println("ERROR: Cannot get verification nonce!");
            //e.printStackTrace();
        }

        UserResult userResult =
                null;
        try {
            userResult = userClient.proveSelectiveDisclosure(encodedVerificationRuleSet,
                    userMap.get("blindedCertificateSignature"),
                    userMap.get("certificateData"),
                    userMap.get("certificateTemplate"),
                    userMap.get("userPrivateKey"),
                    verificationNonce
            );
        } catch (WedprException e) {
            //TODO: 更改异常捕获方式
            System.err.println("ERROR: Cannot prove user identity!");
            //FIXME: 此处超范围会抛异常

            //e.printStackTrace();
        }
        assert userResult != null;
        String verifyRequest = userResult.verifyRequest;
        VerifierResult verifierResult =
                null;
        try {
            verifierResult = verifierClient.verifySelectiveDisclosure(encodedVerificationRuleSet, verifyRequest);
        } catch (WedprException e) {
            //TODO: 更改异常捕获方式
            System.err.println("ERROR: Cannot verify the field!");
            //e.printStackTrace();
        }
//        System.out.println("Proof verification result = " + verifierResult.boolResult);
        assert verifierResult != null;
        return verifierResult.boolResult;
    }
}
