package com.example.dhruv.twitteranalyser;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.Button;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    public ViewPager mPager;
    public SlidingTabLayout mTabs;
    ListView listView;

    public static MyAdapter adapter;
    public static List<TweetItem> list;
    public static String searchterm;
    Button b, analyse, graphit;
    EditText et, et2;
    public static Classifier<String, String> bayes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.app_bar_parent);
        bayes = new BayesClassifier<String, String>();
        bayes.setMemoryCapacity(15000);
        et2 = (EditText) findViewById(R.id.atext);
        setSupportActionBar(toolbar);
        graphit = (Button) findViewById(R.id.graph);
        graphit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PieChart.class);
                startActivity(i);
            }
        });
        list = new ArrayList<TweetItem>();
        b = (Button) findViewById(R.id.search);
        new LearnSync().execute();
        analyse = (Button) findViewById(R.id.analyse);
        analyse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = et2.getText().toString();
                String[] val = s.split("\\s");
                new MyPost(val).execute();
                // String cat = bayes.classify(Arrays.asList(val)).getCategory();
                //Toast.makeText(MainActivity.this,"It's "+cat,Toast.LENGTH_SHORT).show();
            }
        });
        et = (EditText) findViewById(R.id.term);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                searchterm = et.getText().toString();

                new AsyncTweet(MainActivity.this).execute();
            }
        });


        listView = (ListView) findViewById(R.id.tweetsview);
        adapter = new MyAdapter(this, R.layout.item, list);
        listView.setAdapter(adapter);
        //MainActivity.list.add(new TweetItem("WHY", true, 60));
        //adapter.notifyDataSetChanged();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Twitter Tweets");
        // mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        // mTabs.setDistributeEvenly(true);
        //mPager = (ViewPager) findViewById(R.id.mpager);
        //mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        //mTabs.setViewPager(mPager);
        //new AsyncTweet().execute();


    }


    public class MyAdapter extends ArrayAdapter<String>

    {
        List<TweetItem> items = new ArrayList<>();
        Context c;
        int resource;

        public MyAdapter(Context context, int resource, List<TweetItem> items) {
            super(context, resource);
            c = context;
            this.resource = resource;
            this.items = items;
        }

        @Override
        public int getCount() {
            //  return 1;
            return items.size();
            //return names.size();
        }


        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
//                convertView = inflater.inflate(R.layout.name_list_item, null);
                convertView = inflater.inflate(R.layout.item, null);
            }

            TextView tv = (TextView) convertView.findViewById(R.id.tweet);
            tv.setText(items.get(position).getMessage());
            //set text for current object
            ImageView i_sentiment = (ImageView) convertView.findViewById(R.id.sentiment);
            if (items.get(position).getIsPositive())
                i_sentiment.setImageResource(R.drawable.up);
            else
                i_sentiment.setImageResource(R.drawable.down);
           /* if(position%2==0)
                i_sentiment.setImageResource(R.drawable.up);
            else
                i_sentiment.setImageResource(R.drawable.down);*/

            //TextView percentage = (TextView) convertView.findViewById(R.id.percentage);
            //percentage.setText(items.get(position).getPercentage() + "%");

            return convertView;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private String[] tabs = {"Tweets Analysis", "Search Analysis"};

        public MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new Tweet();


                case 1:
                    return new Tweet();


                default:
                    break;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public class LearnSync extends AsyncTask<Void, Void, Void> {
        ProgressDialog p;

        @Override
        protected void onPreExecute() {
            p = new ProgressDialog(MainActivity.this);
            p.setMessage("Learning from DataSet");
            p.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            p.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            learn();
            return null;
        }

        public void learn() {


            File sdcard = Environment.getExternalStorageDirectory();
            // Toast.makeText(MainActivity.this,sdcard.getAbsolutePath(),Toast.LENGTH_SHORT).show();

//Get the text file
            File file = new File(sdcard, "positive.txt");
            Log.d("TAG", file.getAbsolutePath());
//Read text from file

            File neg = new File(sdcard, "negative.txt");
            int i = 0;
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                BufferedReader br1 = new BufferedReader(new FileReader(neg));
                String line;

                while ((line = br.readLine()) != null) {
                    //Log.d("TAG",line);
                    i++;
                    bayes.learn("positive", Arrays.asList(line.split("\\s")));
                       /* text.append(line);
                        text.append('\n');*/
                    //Log.d("TAG",text.toString());
                }

                br.close();
                while ((line = br1.readLine()) != null) {
                    //Log.d("TAG",line);
                    i++;
                    bayes.learn("negative", Arrays.asList(line.split("\\s")));

                    //Log.d("TAG",text.toString());
                }

                String poo = bayes.classify(Arrays.asList(new String("I am happy. I love you. Yay!").split("\\s"))).getCategory();
                String po1 = bayes.classify(Arrays.asList(new String("I hate this.It is wrong").split("\\s"))).getCategory();
                Log.d("---TAG---", poo);
                Log.d("---TAG-----", po1);
                Log.d("TAG", String.valueOf(i));
                String[] unknownText1 = "today is a sunny day".split("\\s");
                String[] unknownText2 = "there will be rain".split("\\s");
                ((BayesClassifier<String, String>) bayes).classifyDetailed(
                        Arrays.asList(unknownText1));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public class MyPost extends AsyncTask<Void, Void, Void> {
        StringBuilder url = new StringBuilder("http://www.sentiment140.com/api/classify?text=");
        String res,decider;
        public MyPost(String[] val) {
            if (val.length > 0) {

                int i = 0;
                for (i = 0; i < val.length - 1; i++) {
                    url.append(val[i] + "+");
                }
                url.append(val[i]);
               // url.append("?appid=dbcoolster@gmail.com");
                url.append("&callback=myJsFunction");
                Log.d("------TAG----", url.toString());
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(decider.equals("0"))
                Toast.makeText(MainActivity.this,"It is Negative",Toast.LENGTH_SHORT).show();
            else if(decider.equals("2"))
                Toast.makeText(MainActivity.this, "It is Neutral", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this,"It is Positive",Toast.LENGTH_SHORT).show();

            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            res = GET(url.toString());
            Log.d("-------TAG-----", res);
            Log.d("TAG",String.valueOf(res.indexOf("polarity")));
            int i = res.indexOf("polarity")+10;
            decider = String.valueOf(res.charAt(i));
            Log.d("TAG",String.valueOf(res.charAt(i)));
            try {
                JSONObject jsonObject = new JSONObject(res);
                JSONArray j = jsonObject.getJSONArray("results");
                Log.d("TAG",j.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String GET(String url) {
            InputStream inputStream = null;
            String result = "";
            try {

                // create HttpClient
                HttpClient httpclient = new DefaultHttpClient();

                // make GET request to the given URL
                HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

                // receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // convert inputstream to string
                if (inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";

            } catch (Exception e) {
               e.printStackTrace();
            }

            return result;
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }




  /*  private class HttpAsyncTask extends AsyncTask<Void, Void, Void> {

        public String call_url = "http://www.sentiment140.com/api/classify?text=new+moon+is+awesome&query=new+moon&callback=myJsFunction";
        List<String> tweets = new ArrayList<String>();

        public HttpAsyncTask(List<String> list) {
            this.tweets = list;
        }


        public Void doInBackground(Void... urls) {

            String response =  GET(call_url);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
           // etResponse.setText(result);
        }
        public  String GET(String url){
            InputStream inputStream = null;
            String result = "";
            try {

                // create HttpClient
                HttpClient httpclient = new DefaultHttpClient();

                // make GET request to the given URL
                HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

                // receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // convert inputstream to string
                if(inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }

            return result;
        }
        private String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }*/

    }
}
