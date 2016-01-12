package com.example.dhruv.twitteranalyser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.internal.signpost.OAuthConsumer;
import com.parse.internal.signpost.commonshttp.CommonsHttpOAuthConsumer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

//import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

/**
 * Created by Dhruv on 25-Nov-15.
 */
public class AsyncTweet extends AsyncTask<Void, Void, Void> {

    Context context;
    ProgressDialog p;

    public static int postive_count = 0;
    public static int negative_count = 0;

    public AsyncTweet(Context context) {
        this.context = context;
    }

    public String CONSUMER_KEY = "wOJJLnEglECZcV1ohKvgE0mK4";
    public String CONSUMER_SECRET = "WjEI9HD5PhDuBS4cPCpOEQwC65KRt3kWAtzMnCCbf8rI9xIhSD";
    public String ACCESS_TOKEN = "143141774-cjyoqfHMth5On8FXcZsk4aqXa4ItQsCmSVXqwHpY";
    public String ACCESS_TOKEN_SECRET = "WqSJwEmtP4kf27yqmUPYdSIWhw61aReCkTqhzlXa8xtcH";
    public String mSearchTerm = "apple";

    @Override
    protected Void doInBackground(Void... params) {

        try {
            DefaultHttpClient client = new DefaultHttpClient();
            OAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY,
                    CONSUMER_SECRET);
            consumer.setTokenWithSecret(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);

            HttpGet request = new HttpGet();
            request.setURI(new URI("https://stream.twitter.com/1/statuses/filter.json?track=" + MainActivity.searchterm));
            consumer.sign(request);
            HttpResponse response = client.execute(request);
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            //Log.d("TweetData",reader.toString());
            parseTweets(reader);

            in.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        p = new ProgressDialog(context);
        p.setMessage("Getting Live Stream Data");
        p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        p.setIndeterminate(false);
        p.setProgress(0);
        p.setMax(100);
        p.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        MainActivity.adapter.notifyDataSetChanged();
        p.dismiss();


    }

    public void parseTweets(BufferedReader reader) {
        postive_count =0 ;
        negative_count =0 ;

        try {
            String line = "";
            int i = 0;
            do {
                Log.d("Tweet", "------------------------ITERATION" + i + "---------------------");
                line = reader.readLine();
                Log.d("Twitter", "Keep Running: " //+ mKeepRunning
                        + " Line: " + line);
                JSONObject tweet = new JSONObject(line);
                HashMap<String, String> tweetMap = new HashMap<String, String>();
                if (tweet.has("text")) {
                    Log.d("Tweet", tweet.getString("text"));
                    if (Sentiment(tweet.getString("text")))
                        postive_count++;
                    else
                        negative_count++;


                    MainActivity.list.add(new TweetItem(tweet.getString("text"), Sentiment(tweet.getString("text")), 60));
                    tweetMap.put("Tweet", tweet.getString("text"));
                    tweetMap.put("From", tweet.getJSONObject("user")
                            .getString("screen_name"));
                 /*   mTweets.add(0, tweetMap);
                    if (mTweets.size() > 10) {
                        mTweets.remove(mTweets.size() - 1);
                    }
                    //mAdapter.notifyDataSetChanged();
                    publishProgress(1);*/
                }

                i++;
                p.incrementProgressBy(5);
                //p.setProgress(i*5);
                Log.d("-----TAG----------", String.valueOf(i * 5));
            } while (i < 20);

            // while (mKeepRunning && line.length() > 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public boolean Sentiment(String s) {
        boolean r = true;
        String[] test = s.split("\\s");
        String val = MainActivity.bayes.classify(Arrays.asList(test)).getCategory();

        if (val.equals("positive"))
            return true;
        else
            return false;

    }
}
