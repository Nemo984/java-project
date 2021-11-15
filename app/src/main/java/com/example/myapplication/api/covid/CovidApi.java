package com.example.myapplication.api.covid;


public class CovidApi {

    // URL ENDPOINTS https://covid19.ddc.moph.go.th/

    //รายงานสถานการณ์ COVID-19 ประจำวัน
    public static final String TODAY_CASES = "https://covid19.ddc.moph.go.th/api/Cases/today-cases-all";

    //รายงานสถานการณ์ COVID-19 ประจำวัน แยกตามรายจังหวัด
    public static final String TODAY_CASES_PROVINCES = "https://covid19.ddc.moph.go.th/api/Cases/today-cases-by-provinces";

    //รายงานสถานการณ์ COVID-19 ระลอก 3 (ตั้งแต่ 01/04/2021 –ปัจจุบัน)
    public static final String ALL_CASES_ROUND_3 = "https://covid19.ddc.moph.go.th/api/Cases/timeline-cases-all";

    //รายงานสถานการณ์ COVID-19 ระลอก 3 (ตั้งแต่ 01/04/2021 –ปัจจุบัน) แยกตามรายจังหวัด
    public static final String ALL_CASES_ROUND_3_PROVINCES = "https://covid19.ddc.moph.go.th/api/Cases/timeline-cases-by-provinces";

    //ข้อมูลผู้ป่วยระลอก 3 (ตั้งแต่ 01/04/2021 –ปัจจุบัน)
    public static final String PATIENT_INFO_ROUND3  = "https://covid19.ddc.moph.go.th/api/Cases/round-3-line-lists";

    //รายงานสถานการณ์ COVID-19 ระลอก 1 ถึงระลอก 2 (ตั้งแต่ 12/01/2020 – 31/03/2021)
    public static final String ALL_CASES_ROUND12 = "https://covid19.ddc.moph.go.th/api/Cases/round-1to2-all";

    //รายงานสถานการณ์ COVID-19 ระลอก 1 ถึงระลอก 2 (ตั้งแต่ 12/01/2020 – 31/03/2021) แยกตามรายจังหวัด
    public static final String ALL_CASES_ROUND12_PROVINCES = "https://covid19.ddc.moph.go.th/api/Cases/round-1to2-by-provinces";
    
}