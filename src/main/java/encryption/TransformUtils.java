package encryption;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransformUtils {

    public static String transformStringToInt(String target) {
        //转换目标串为字节数组用于转换为数字
        byte[] bytes = target.getBytes();
        //存储bytes内的数字
        List<Integer> numList = new ArrayList<>();
        for (Byte b : bytes) {
            Integer i = Integer.parseInt(b.toString());
            //拼接去掉负号，负数处理后为正数并大于正数
            if (i < 0) {
                i += 256;
            }
            //大于127为负数
            if (i > 127) {
                //System.err.println("负数:" + i);
            } else {
                // System.err.println("正数:" + i);
            }
            numList.add(i);
        }

        //拼接后的最终字符串
        StringBuilder finString = new StringBuilder();
        for (Integer integer : numList) {
            //补齐位数
            String str = String.format("%3d", integer).replace(" ", "0");
//            System.out.println("str:" + str);
            finString.append(str);
        }
//        System.out.println(finString);
//        System.out.println(finString.length());
        return finString.toString();
    }

    public static String transformIntToString(String target) {
        //判断是否需要补0
        if (target.length() % 3 != 0) {
//            StringBuilder stringBuilder = new StringBuilder(target);
//            stringBuilder.insert(0, "0");
//            target = stringBuilder.toString();
            target = "0" + target;
        }

        List<Integer> numList = new ArrayList();
        for (int i = 0; i <= target.length() - 3; i += 3) {
            String substring = target.substring(i, i + 3);
            //System.out.println(substring);
            numList.add(Integer.parseInt(substring));
        }
        //
        Iterator<Integer> iterator = numList.iterator();
        //还原字节数组
        byte[] bytes = new byte[numList.size()];
        int it = 0;
        while (iterator.hasNext()) {
            Integer num = iterator.next();
            //还原byte应有的负数值
            if (num > 127) {
                num -= 256;
            }
            bytes[it] = Byte.parseByte(num.toString());
            it++;
        }

        //还原后的字符串
        return new String(bytes);
    }

}
