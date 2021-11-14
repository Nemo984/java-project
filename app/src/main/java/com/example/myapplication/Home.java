package com.example.myapplication;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.api.covid.CovidApi;
import com.example.myapplication.api.covid.NewsApi;
import com.example.myapplication.cluster.MyItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Home extends Fragment {
    // Variables that used
    HashMap<String, ArrayList<Integer>> data_provice = new HashMap<String, ArrayList<Integer>>();
    HashMap<String, Integer> data_today = new HashMap<String, Integer>();
    ArrayList<String> province_list = new ArrayList<>(Arrays.asList("Select Province","กระบี่","กรุงเทพมหานคร","กาญจนบุรี"
            ,"กาฬสินธุ์", "กำแพงเพชร","ขอนแก่น","จันทบุรี","ฉะเชิงเทรา","ชลบุรี","ชัยนาท"
            , "ชัยภูมิ","ชุมพร", "ตรัง", "ตราด", "ตาก", "นครนายก", "นครปฐม", "นครพนม", "นครราชสีมา"
            , "นครศรีธรรมราช", "นครสวรรค์", "นนทบุรี", "นราธิวาส", "น่าน", "บึงกาฬ", "บุรีรัมย์", "ปทุมธานี", "ประจวบคีรีขันธ์", "ปราจีนบุรี", "ปัตตานี"
            , "พระนครศรีอยุธยา","พะเยา","พังงา","พัทลุง","พิจิตร","พิษณุโลก","ภูเก็ต","มหาสารคาม","มุกดาหาร","ยะลา","ยโสธร","ร้อยเอ็ด","ระนอง","ระยอง","ราชบุรี","ลพบุรี","ลำปาง"
            ,"ลำพูน","ศรีสะเกษ","สกลนคร","สงขลา","สตูล","สมุทรปราการ","สมุทรสงคราม","สมุทรสาคร","สระบุรี","สระแก้ว","สิงห์บุรี","สุพรรณบุรี","สุราษฎร์ธานี","สุรินทร์","สุโขทัย","หนองคาย"
            ,"หนองบัวลำภู","อ่างทอง","อำนาจเจริญ","อุดรธานี","อุตรดิตถ์","อุทัยธานี","อุบลราชธานี","เชียงราย","เชียงใหม่","เพชรบุรี","เพชรบูรณ์","เลย","แพร่","แม่ฮ่องสอน"));
    SwipeRefreshLayout swipe;
    TextView new_infected_p;
    TextView total_infected_province;
    TextView new_death_province;
    TextView total_death_province;
    TextView new_infected_country;
    TextView total_infected_country;
    TextView new_death_country;
    TextView total_death_country;
    TextView new_recovered;
    TextView total_recovered;
    TextView News_1;
    TextView News_2;

    AutoCompleteTextView search_province;
    TextView date_update;
    Calendar calendar;
    SimpleDateFormat dateFormat;
    String date;
    ArrayAdapter<String> adapter;
    JsonArrayRequest jsonArrayRequest_today = new JsonArrayRequest(Request.Method.GET, CovidApi.TODAY_CASES, null, new Response.Listener<JSONArray>() {
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }, error -> Log.e("Timelines", error.toString()));

    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, CovidApi.TODAY_CASES_PROVINCES, null, new Response.Listener<JSONArray>() {
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
            calendar = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            date = dateFormat.format(calendar.getTime());
            date_update.setText(date);

            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            calendar = Calendar.getInstance();
                            date = dateFormat.format(calendar.getTime());
                            date_update.setText(date);
                            swipe.setRefreshing(false);
                        }
                    }, 1000);
                }
            });
        }

    }, error -> Log.e("Timelines", error.toString()));

    JsonObjectRequest NewsJsonRequest = new JsonObjectRequest(Request.Method.GET, NewsApi.NEWS, null, new Response.Listener<JSONObject>() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onResponse(JSONObject response) {
            try {
                News_1.setText(String.valueOf(((JSONArray) response.get("articles")).getJSONObject(0).get("description")));
                News_2.setText(String.valueOf(((JSONArray) response.get("articles")).getJSONObject(1).get("description")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }, error -> Log.e("Timelines", error.toString()));

    // Methods
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container,false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);
        Volley.newRequestQueue(getContext()).add(jsonArrayRequest_today);
        Volley.newRequestQueue(getContext()).add(NewsJsonRequest);
        swipe = (SwipeRefreshLayout) getActivity().findViewById(R.id.swiperefresh);

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

        // Textview for News
        News_1 = (TextView) getActivity().findViewById(R.id.News_1);
        News_2 = (TextView) getActivity().findViewById(R.id.News_2);

        search_province = (AutoCompleteTextView) getActivity().findViewById(R.id.seach_province);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, province_list);
        search_province.setAdapter(adapter);

        search_province.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String province = parent.getItemAtPosition(position).toString();
                // Set province Textview
                new_infected_p.setText("+ " + new DecimalFormat("###,###,###").format((data_provice.get(province)).get(0)).toString());
                total_infected_province.setText(new DecimalFormat("###,###,###").format((data_provice.get(province)).get(2)).toString());
                new_death_province.setText("+ " + new DecimalFormat("###,###,###").format((data_provice.get(province)).get(1)).toString());
                total_death_province.setText(new DecimalFormat("###,###,###").format((data_provice.get(province)).get(3)).toString());
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}