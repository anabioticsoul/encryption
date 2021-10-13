import com.webank.wedpr.scd.proto.AttributeDict;
import domain.User;
import encryption.EncryptUtils;
import encryption.TransformUtils;
import org.junit.Test;

import java.util.*;

public class MyTest {

    @Test
    public void test1() {
        String userId = "12345678";
        String name = "Tom";
        String idCard = "12345600000000000X";
        String location = "北京市 海淀区 XXX街道";
        String applicationTitle = "XXX亲属需要救助";
        String userInfo = "本人xxx,xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        String SCDMedicalRecord = "XXX病，XX病，xxx病";
        String VCLAsset = "100";
        String receiveAccount = "123124125123123";
        String time = new Date().toString();
        String target = "3000000";

        Map<String, String> encrypt = EncryptUtils.encrypt(new User(userId, name, idCard, location, applicationTitle, userInfo, SCDMedicalRecord, VCLAsset, receiveAccount, time, target));
        List<String> disclosureList = new ArrayList<>();
        disclosureList.add("name");
        disclosureList.add("idCard");
        disclosureList.add("userInfo");
        disclosureList.add("location");
        disclosureList.add("time");
        Map<String, String> finMap = EncryptUtils.selectiveDisclosure(encrypt, disclosureList);

        System.out.println("name:" + finMap.get("name"));
        System.out.println("idCard:" + finMap.get("idCard"));
        System.out.println("userInfo:" + finMap.get("userInfo"));
        System.out.println("location:" + finMap.get("location"));
        System.out.println("time:" + finMap.get("time"));

        boolean b = EncryptUtils.verifyField(encrypt, 1000);
        System.out.println("asset:" + b);

        System.out.println("end");
    }

    @Test
    public void test2() {
        String name = "张三da啊实打实的阿萨的撒旦sdasd";
        byte[] bytes = name.getBytes();
        //bytes.toString();
        System.err.println("bytes.toString:" + bytes);


        String test;
        for (Byte b : bytes) {
            System.err.println(b.toString());
            // Byte.parseByte("");

        }

//        byte b1 = Byte.parseByte("-27");
//        byte[]bytes1 = new byte[1];
//        bytes1[0]=b1;
//        System.err.println(new String(bytes1));

    }

    @Test
    public void test3() {
//        Map<String,String> map = new HashMap<>();
//        map.put("张三","123");
//        map.put("张三","234");
//        System.out.println();
        String name = "张三";
        name.hashCode();
        System.err.println(name.hashCode());
    }

    @Test
    public void test4() {
        String name = "张三asda阿斯顿阿萨dsssa";
        byte[] bytes = name.getBytes();
        List<Integer> numList = new ArrayList<>();
        for (Byte b : bytes) {
            Integer i = Integer.parseInt(b.toString());
            if (i < 0) {
                i += 256;
            }
            if (i > 127) {
                //System.err.println("负数:" + i);
            } else {
                // System.err.println("正数:" + i);
            }
            numList.add(i);
        }
        String finString = "";
        Iterator<Integer> iterator = numList.iterator();
        while (iterator.hasNext()) {
            String str = String.format("%3d", iterator.next()).replace(" ", "0");
            System.out.println("str:" + str);
            finString += str;
        }
        System.out.println(finString);
        System.out.println(finString.length());
    }

    @Test
    public void test5(){
        String string = "Jerry";
        string = "Tom and " + string;
        System.out.println(string);
    }

    @Test
    public void testUtils1() {
        String string = "张三";
        String transString = TransformUtils.transformStringToInt(string);
        System.out.println("transString:" + transString);

        String srcString = null;
        srcString = TransformUtils.transformIntToString(transString);
        System.out.println("srcString:" + srcString);
    }
}
