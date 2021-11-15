package com.example.myapplication;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.News.News;
import com.example.myapplication.News.NewsListAdapter;
import com.example.myapplication.api.covid.CovidApi;
import com.example.myapplication.api.covid.NewsApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Home extends Fragment {
    // Variables that used
    private HashMap<String, ArrayList<Integer>> data_provice = new HashMap<String, ArrayList<Integer>>();
    private HashMap<String, Integer> data_today = new HashMap<String, Integer>();
    private ArrayList<String> province_list = new ArrayList<>(Arrays.asList("Select Province","กระบี่","กรุงเทพมหานคร","กาญจนบุรี"
            ,"กาฬสินธุ์", "กำแพงเพชร","ขอนแก่น","จันทบุรี","ฉะเชิงเทรา","ชลบุรี","ชัยนาท", "ชัยภูมิ","ชุมพร", "ตรัง", "ตราด", "ตาก", "นครนายก",
            "นครปฐม", "นครพนม", "นครราชสีมา", "นครศรีธรรมราช", "นครสวรรค์", "นนทบุรี", "นราธิวาส", "น่าน", "บึงกาฬ", "บุรีรัมย์", "ปทุมธานี",
            "ประจวบคีรีขันธ์", "ปราจีนบุรี", "ปัตตานี", "พระนครศรีอยุธยา","พะเยา","พังงา","พัทลุง","พิจิตร","พิษณุโลก","ภูเก็ต","มหาสารคาม","มุกดาหาร",
            "ยะลา","ยโสธร","ร้อยเอ็ด","ระนอง","ระยอง","ราชบุรี","ลพบุรี","ลำปาง","ลำพูน","ศรีสะเกษ","สกลนคร","สงขลา","สตูล","สมุทรปราการ",
            "สมุทรสงคราม","สมุทรสาคร","สระบุรี","สระแก้ว","สิงห์บุรี","สุพรรณบุรี","สุราษฎร์ธานี","สุรินทร์","สุโขทัย","หนองคาย","หนองบัวลำภู","อ่างทอง",
            "อำนาจเจริญ","อุดรธานี","อุตรดิตถ์","อุทัยธานี","อุบลราชธานี","เชียงราย","เชียงใหม่","เพชรบุรี","เพชรบูรณ์","เลย","แพร่","แม่ฮ่องสอน"));
    private TextView new_infected_p;
    private TextView total_infected_province;
    private TextView new_death_province;
    private TextView total_death_province;
    private TextView new_infected_country;
    private TextView total_infected_country;
    private TextView new_death_country;
    private TextView total_death_country;
    private TextView new_recovered;
    private TextView total_recovered;

    private NewsListAdapter adapter_news;
    private ListView listview;
    private ArrayList<News> arrayListnews;
    private AutoCompleteTextView search_province;
    private TextView date_update;
    private String date;
    private ArrayAdapter<String> adapter;
    private Context context;

    // Json Request for getting data from API JSON
    private final JsonArrayRequest jsonArrayRequest_today = new JsonArrayRequest(Request.Method.GET, CovidApi.TODAY_CASES, null, new Response.Listener<JSONArray>() {
        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onResponse(JSONArray response) {
            try {
                JSONObject object = response.getJSONObject(0);
                data_today.put("new_case", object.getInt("new_case"));
                data_today.put("total_case", object.getInt("total_case"));
                data_today.put("new_death", object.getInt("new_death"));
                data_today.put("total_death", object.getInt("total_death"));
                data_today.put("new_recovered", object.getInt("new_recovered"));
                data_today.put("total_recovered", object.getInt("total_recovered"));

                // Set Country Textview
                new_infected_country.setText("+ " + String.valueOf(new DecimalFormat("###,###,###").format(data_today.get("new_case"))));
                total_infected_country.setText(String.valueOf(new DecimalFormat("###,###,###").format(data_today.get("total_case"))));
                new_death_country.setText("+ " + String.valueOf(new DecimalFormat("###,###,###").format(data_today.get("new_death"))));
                total_death_country.setText(String.valueOf(new DecimalFormat("###,###,###").format(data_today.get("total_death"))));
                new_recovered.setText(String.valueOf("+ " + new DecimalFormat("###,###,###").format(data_today.get("new_recovered"))));
                total_recovered.setText(String.valueOf(new DecimalFormat("###,###,###").format(data_today.get("total_recovered"))));

                // Set Date
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                date = dateFormat.format(calendar.getTime());
                date_update.setText(date);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }, error -> Log.e("Timelines", error.toString()));

    private final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, CovidApi.TODAY_CASES_PROVINCES, null, new Response.Listener<JSONArray>() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onResponse(JSONArray response) {
            for (int n = 0; n < response.length(); n++) {
                try {
                    JSONObject object = response.getJSONObject(n);
                    String province = object.getString("province");
                    Integer new_case = object.getInt("new_case");
                    Integer new_death = object.getInt("new_death");
                    Integer total_case = object.getInt("total_case");
                    Integer total_death = object.getInt("total_death");
                    ArrayList<Integer> data = new ArrayList<Integer>();
                    data.add(new_case);
                    data.add(new_death);
                    data.add(total_case);
                    data.add(total_death);
                    data_provice.put(province, data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }, error -> Log.e("Timelines", error.toString()));

    private final JsonObjectRequest NewsJsonRequest = new JsonObjectRequest(Request.Method.GET,
            NewsApi.NEWS,
            null,
            new Response.Listener<JSONObject>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        arrayListnews = new ArrayList<News>(response.getInt("totalResults"));
                        for (int i = 0; i < response.getInt("totalResults"); i++) {
                            String title = String.valueOf(((JSONArray) response.get("articles")).getJSONObject(i).get("title"));
                            String urlimage = String.valueOf(((JSONArray) response.get("articles")).getJSONObject(i).get("urlToImage"));
                            String urlnews = String.valueOf(((JSONArray) response.get("articles")).getJSONObject(i).get("url"));
                            arrayListnews.add(new News(title, urlimage, urlnews));
                        }

                        adapter_news = new NewsListAdapter(context, R.layout.news_layout, arrayListnews);
                        listview.setAdapter(adapter_news);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            error -> Log.e("Timelines", error.toString())) {
        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("User-agent", "Mozilla/5.0");
            return headers;
        }
    };

    // Methods

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAllComponent(){

        // Textview For Province
        new_infected_p = (TextView) getActivity().findViewById(R.id.new_infected_province);
        total_infected_province = (TextView) getActivity().findViewById(R.id.total_infected_province);
        new_death_province = (TextView) getActivity().findViewById(R.id.new_death_province);
        total_death_province = (TextView) getActivity().findViewById(R.id.total_death_province);

        // Textview For Country
        new_infected_country = (TextView) getActivity().findViewById(R.id.new_infected_country);
        total_infected_country = (TextView) getActivity().findViewById(R.id.total_infected_country);
        new_death_country = (TextView) getActivity().findViewById(R.id.new_death_country);
        total_death_country = (TextView) getActivity().findViewById(R.id.total_death_country);
        new_recovered = (TextView) getActivity().findViewById(R.id.new_recovered);
        total_recovered = (TextView) getActivity().findViewById(R.id.total_recovered);

        // Textview For Date Update
        date_update = (TextView) getActivity().findViewById(R.id.date_update);
        listview = (ListView) getActivity().findViewById(R.id.list_news);


        search_province = (AutoCompleteTextView) getActivity().findViewById(R.id.seach_province);

        // Create Adapter and set
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, province_list);
        search_province.setAdapter(adapter);

        //set Click on Suggestion dropdown
        search_province.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String province = parent.getItemAtPosition(position).toString();
                // Set province Textview
                setProvinceData(province);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });

        //set "Enter" key listener
        search_province.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                    if(!province_list.contains(search_province.getText().toString())) {
                        if (!adapter.isEmpty()) {
                            if (!adapter.getItem(0).equals("Select Province")) {
                                String suggestion = adapter.getItem(0).toString();
                                search_province.setText(suggestion);
                                search_province.dismissDropDown();
                                setProvinceData(suggestion);
                            }
                        }

                    }
                    if(search_province.getText().equals("")){
                        ClearData();
                    }
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
                return false;
            }
        });
    }

    // set Province Data by String province
    public void setProvinceData(String province){
        new_infected_p.setText("+ " + new DecimalFormat("###,###,###").format((data_provice.get(province)).get(0)).toString());
        total_infected_province.setText(new DecimalFormat("###,###,###").format((data_provice.get(province)).get(2)).toString());
        new_death_province.setText("+ " + new DecimalFormat("###,###,###").format((data_provice.get(province)).get(1)).toString());
        total_death_province.setText(new DecimalFormat("###,###,###").format((data_provice.get(province)).get(3)).toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container,false);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NewsJsonRequest.addMarker("");
        // Set Context for use
        context = getContext();
        setAllComponent();
        Volley.newRequestQueue(context).add(jsonArrayRequest);
        Volley.newRequestQueue(context).add(jsonArrayRequest_today);
        Volley.newRequestQueue(context).add(NewsJsonRequest);

    }
    // Clear Province Data
    public void ClearData(){
        new_infected_p.setText("0");
        total_infected_province.setText("0");
        new_death_province.setText("0");
        total_death_province.setText("0");
        search_province.setText("");
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}