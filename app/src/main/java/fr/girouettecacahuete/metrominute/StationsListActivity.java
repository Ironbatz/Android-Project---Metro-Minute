package fr.girouettecacahuete.metrominute;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class StationsListActivity extends Activity implements View.OnClickListener {
    List<Station> saved_stations= null; // Used to keep all the stations from a line, to access from anywhere

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stations_list);
        setStationsListActivityListeners();

        Intent intent = getIntent(); // We receive the line number
        if(intent != null)
        {
            appelApi(intent.getStringExtra("CODE")); // First call to the RATP API, to collect the stations list
        }
    }

    public void appelApi(final String code)
    {
        RatpServices Api = new RestAdapter.Builder().setEndpoint(RatpServices.ENDPOINT).build().create(RatpServices.class);

        Api.listStationsAsync(code, new Callback<ApiResult>() {
            @Override
            public void success(ApiResult apiresult, Response response) { // In case of success
                for (Station station : saved_stations = apiresult.result.stations) { // We save the stations for later uses

                }
                afficherStations(apiresult.result.stations, code); // Stations list is displayed
            }

            @Override
            public void failure(RetrofitError error) { // In case of failure
                TextView text = findViewById(R.id.Stations_list);
                text.setText(error.getMessage());
            }
        });
    }

    public void afficherStations (List<Station> stations, String code) {
        TextView titleView = findViewById(R.id.Stations_list);
        String title = "Ligne Metro " + code;
        titleView.setText(title);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(70, 0, 0, 75);
        TableLayout tbl = findViewById(R.id.Stations_Table_Layout);
        tbl.removeAllViews();

        for(int i = 0; i<stations.size(); i++) // We dynamically add TableRow layouts, to welcome all the stations
        {
            TableRow row = new TableRow(this);
            TextView station = new TextView(this);

            station.setText(stations.get(i).getName());
            station.setTextColor(Color.BLACK);
            station.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            station.setClickable(true);
            station.setFocusable(true);
            station.setTag("station_"+code+"_"+i);

            station.setOnClickListener(this);

            row.addView(station, layoutParams);
            tbl.addView(row);
        }


    }

    public void appelApiSchedules(final String code, final String station) // New call to the API, to collect line schedules
    {
        RatpServices Api = new RestAdapter.Builder().setEndpoint(RatpServices.ENDPOINT).build().create(RatpServices.class);

        Api.listSchedulesAAsync(code, station, new Callback<ApiResult>() {
            @Override
            public void success(ApiResult apiresult, Response response) {
                afficherSchedulesA(apiresult.result.schedules, code, station);
            }

            @Override
            public void failure(RetrofitError error) {
                TextView text = findViewById(R.id.Stations_list);
                text.setText(error.getMessage());
            }
        });
    }

    public void afficherSchedulesA(List<Schedule> schedules, final String code, final String station)
    {
        TextView titleView = findViewById(R.id.Stations_list);
        String title = "Prochains trains Metro " + code + "\nStation " + saved_stations.get(Integer.parseInt(code)).getName();
        titleView.setText(title);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(70, 0, 0, 75);
        TableLayout tbl = findViewById(R.id.Stations_Table_Layout);
        tbl.removeAllViews();

        TextView dest = new TextView(this); // A simple textView to display the destination
        String direction = "Direction: " + schedules.get(0).getDestination();
        dest.setText(direction);
        dest.setTextColor(Color.BLACK);
        dest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        dest.setTag("destA");
        dest.setPadding(0,100,0,0);

        TableRow first_row = new TableRow(this);
        first_row.addView(dest, layoutParams);
        tbl.addView(first_row);

        for(int i = 0; i<schedules.size(); i++) // Again, we add dynamically TableRow layouts, this time to welcome schedules
        {
            TableRow row = new TableRow(this);
            TextView schedule = new TextView(this);


            schedule.setText(schedules.get(i).getMessage());
            schedule.setTextColor(Color.BLACK);
            schedule.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            schedule.setTag("schedule_"+code+"_"+station+"_"+i);

            row.addView(schedule, layoutParams);
            tbl.addView(row);
        }

        RatpServices Api = new RestAdapter.Builder().setEndpoint(RatpServices.ENDPOINT).build().create(RatpServices.class); // We need to call again the API, to collect schedules from trains going the other way.

        Api.listSchedulesBAsync(code, station, new Callback<ApiResult>() {
            @Override
            public void success(ApiResult apiresult, Response response) {
                afficherSchedulesB(apiresult.result.schedules, code, station);
            }

            @Override
            public void failure(RetrofitError error) {
                TextView text = findViewById(R.id.Stations_list);
                text.setText(error.getMessage());
            }
        });


    }

    public void afficherSchedulesB(List<Schedule> schedules, String code, String station) // We add schedules to the other destination to the content
    {
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(70, 0, 0, 75);
        TableLayout tbl = findViewById(R.id.Stations_Table_Layout);

        TextView dest = new TextView(this);
        String direction = "Direction: " + schedules.get(0).getDestination();
        dest.setText(direction);
        dest.setTextColor(Color.BLACK);
        dest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        dest.setTag("destB");
        dest.setPadding(0,100,0,0);

        TableRow first_row = new TableRow(this);
        first_row.addView(dest, layoutParams);
        tbl.addView(first_row);

        for(int i = 0; i<schedules.size(); i++)
        {
            TableRow row = new TableRow(this);
            TextView schedule = new TextView(this);


            schedule.setText(schedules.get(i).getMessage());
            schedule.setTextColor(Color.BLACK);
            schedule.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            schedule.setTag("schedule_"+code+"_"+station+"_"+i);

            row.addView(schedule, layoutParams);
            tbl.addView(row);
        }
    }

    public void setStationsListActivityListeners()
    {
        Button ReturnButton = findViewById(R.id.Return_Button);
        ReturnButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        String tag = view.getTag().toString(); // We use tag to know which station is selected
        String code = null;
        String station = null;
        if(tag.contains("station_"))
        {
            code = "" + tag.charAt(8);
            if(tag.charAt(9) != '_')
            {
                code += tag.charAt(9);
                tag = tag.substring(0,8);
            }
            else
            {
                tag = tag.substring(0,8);
            }
            station = saved_stations.get(Integer.parseInt(code)).getSlug();
        }
        switch (tag)
        {
            case "Return_Button":
                Intent intent = new Intent(StationsListActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case "station_":
                appelApiSchedules(code, station);
                break;
        }
    }
}
