package com.example.myapplication.api.covid;

import java.util.HashMap;

public class ProvinceLocationHashMap {
    private static HashMap<String,Double[]> hashmap;
    private static ProvinceLocationHashMap instance = null;
    public static HashMap<String,Double[]> getMap()
    {
        if (instance == null) {
            instance = new ProvinceLocationHashMap();
            hashmap = initMap();
        }

        return hashmap;
    }

    private static HashMap<String,Double[]> initMap() {
        HashMap<String,Double[]> map = new HashMap<>();
        map.put("กระบี่", new Double[]{8.0862997,98.9062835});
        map.put("กรุงเทพมหานคร", new Double[]{13.7563309,100.5017651});
        map.put("กาญจนบุรี", new Double[]{14.0227797,99.5328115});
        map.put("กาฬสินธุ์", new Double[]{16.4314078,103.5058755});
        map.put("กำแพงเพชร", new Double[]{16.4827798,99.5226618});
        map.put("ขอนแก่น", new Double[]{16.4419355,102.8359921});
        map.put("จันทบุรี", new Double[]{12.61134,102.1038546});
        map.put("ฉะเชิงเทรา", new Double[]{13.6904194,101.0779596});
        map.put("ชลบุรี", new Double[]{13.3611431,100.9846717});
        map.put("ชัยนาท", new Double[]{15.1851971,100.125125});
        map.put("ชัยภูมิ", new Double[]{15.8068173,102.0315027});
        map.put("ชุมพร", new Double[]{10.4930496,99.18001989999999});
        map.put("ตรัง", new Double[]{7.5593851,99.6110065});
        map.put("ตราด", new Double[]{12.2427563,102.5174734});
        map.put("ตาก", new Double[]{16.8839901,99.1258498});
        map.put("นครนายก", new Double[]{14.2069466,101.2130511});
        map.put("นครปฐม", new Double[]{13.8199206,100.0621676});
        map.put("นครพนม", new Double[]{17.392039,104.7695508});
        map.put("นครราชสีมา", new Double[]{14.9798997,102.0977693});
        map.put("นครศรีธรรมราช", new Double[]{8.4303975,99.96312189999999});
        map.put("นครสวรรค์", new Double[]{15.6987382,100.11996});
        map.put("นนทบุรี", new Double[]{13.8621125,100.5143528});
        map.put("นราธิวาส", new Double[]{6.4254607,101.8253143});
        map.put("น่าน", new Double[]{18.7756318,100.7730417});
        map.put("บึงกาฬ", new Double[]{18.3609104,103.6464463});
        map.put("บุรีรัมย์", new Double[]{14.9930017,103.1029191});
        map.put("ปทุมธานี", new Double[]{14.0208391,100.5250276});
        map.put("ประจวบคีรีขันธ์", new Double[]{11.812367,99.79732709999999});
        map.put("ปราจีนบุรี", new Double[]{14.0420699,101.6600874});
        map.put("ปัตตานี", new Double[]{6.761830799999999,101.3232549});
        map.put("พระนครศรีอยุธยา", new Double[]{14.3532128,100.5689599});
        map.put("พะเยา", new Double[]{19.2154367,100.2023692});
        map.put("พังงา", new Double[]{8.4501414,98.5255317});
        map.put("พัทลุง", new Double[]{7.6166823,100.0740231});
        map.put("พิจิตร", new Double[]{16.2740876,100.3346991});
        map.put("พิษณุโลก", new Double[]{16.8211238,100.2658516});
        map.put("ภูเก็ต", new Double[]{7.9519331,98.33808839999999});
        map.put("มหาสารคาม", new Double[]{16.0132015,103.1615169});
        map.put("มุกดาหาร", new Double[]{16.5695723,104.5231213});
        map.put("ยะลา", new Double[]{6.541147,101.2803947});
        map.put("ยโสธร", new Double[]{15.792641,104.1452827});
        map.put("ร้อยเอ็ด", new Double[]{16.0538196,103.6520036});
        map.put("ระนอง", new Double[]{9.9528702,98.60846409999999});
        map.put("ระยอง", new Double[]{12.707434,101.1473517});
        map.put("ราชบุรี", new Double[]{13.5282893,99.8134211});
        map.put("ลพบุรี", new Double[]{14.7995081,100.6533706});
        map.put("ลำปาง", new Double[]{18.2888404,99.49087399999999});
        map.put("ลำพูน", new Double[]{18.5744606,99.0087221});
        map.put("ศรีสะเกษ", new Double[]{15.1186009,104.3220095});
        map.put("สกลนคร", new Double[]{17.1545995,104.1348365});
        map.put("สงขลา", new Double[]{7.1756004,100.614347});
        map.put("สตูล", new Double[]{6.6238158,100.0673744});
        map.put("สมุทรปราการ", new Double[]{13.5990961,100.5998319});
        map.put("สมุทรสงคราม", new Double[]{13.4098217,100.0022645});
        map.put("สมุทรสาคร", new Double[]{13.5475216,100.2743956});
        map.put("สระบุรี", new Double[]{14.5289154,100.9101421});
        map.put("สระแก้ว", new Double[]{13.824038,102.0645839});
        map.put("สิงห์บุรี", new Double[]{14.8936253,100.3967314});
        map.put("สุพรรณบุรี", new Double[]{14.4744892,100.1177128});
        map.put("สุราษฎร์ธานี", new Double[]{9.1382389,99.3217483});
        map.put("สุรินทร์", new Double[]{14.882905,103.4937107});
        map.put("สุโขทัย", new Double[]{17.0055573,99.8263712});
        map.put("หนองคาย", new Double[]{17.8782803,102.7412638});
        map.put("หนองบัวลำภู", new Double[]{17.2218247,102.4260368});
        map.put("อ่างทอง", new Double[]{14.5896054,100.455052});
        map.put("อำนาจเจริญ", new Double[]{15.8656783,104.6257774});
        map.put("อุดรธานี", new Double[]{17.4138413,102.7872325});
        map.put("อุตรดิตถ์", new Double[]{17.6200886,100.0992942});
        map.put("อุทัยธานี", new Double[]{15.3835001,100.0245527});
        map.put("อุบลราชธานี", new Double[]{15.2286861,104.8564217});
        map.put("เชียงราย", new Double[]{19.9071656,99.830955});
        map.put("เชียงใหม่", new Double[]{18.7883439,98.98530079999999});
        map.put("เพชรบุรี", new Double[]{12.9649215,99.6425883});
        map.put("เพชรบูรณ์", new Double[]{16.301669,101.1192804});
        map.put("เลย", new Double[]{17.4860232,101.7223002});
        map.put("แพร่", new Double[]{18.1445774,100.1402831});
        map.put("แม่ฮ่องสอน", new Double[]{19.3020296,97.96543679999999});
        return map;
    }
}