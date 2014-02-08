package com.YouPlay.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.YouPlay.Helper.AlertDialogManager;
import com.YouPlay.Helper.connectionDetector;
import com.YouPlay.Helper.JSONParser;
import com.YouPlay.Impl.SubCategoryActivity.LoadSubCategories;

public class PostsActivity extends ListActivity implements OnScrollListener{
	connectionDetector cd;
	AlertDialogManager  alert = new AlertDialogManager();
	private ProgressDialog progressDialog;
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> postList;
	String subcategory_id;
	// albums JSONArray
	JSONArray posts = null;
	String[] Urls=null;
	String[] urlNames=null;
	// albums JSON url
	private static final String URL_ALBUMS = "http://anddev.comuv.com/docs/track.php";
	private ListView listView;
	private static final String TAG_ID = "subcategoryid";
	private static final String TAG_NAME = "postname";
	private static final String TAG_POSTID = "postid";
	private static final String TAG_URL = "posturl";
	private static final String TAG_PAGE = "currentpage";
	private int currentPage = 1;
	private boolean noMorePages = false;
	private ImageAdapter imgAdapter;

	private boolean m_isbackpressedalready;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		m_isbackpressedalready = false;
		setContentView(R.layout.post_activity);
		listView = getListView();
		cd = new connectionDetector(getApplicationContext());
		postList = new ArrayList<HashMap<String,String>>();
		// Check for internet connection
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(PostsActivity.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		Intent i = getIntent();
		subcategory_id = i.getStringExtra("subcategory_id");
		new LoadCategories().execute("1");
		ListView lv = getListView();
		lv.setOnScrollListener(this);
		lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				String video_id=Urls[arg2];
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + video_id));
				startActivity(intent);
				overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);
			}
		});
	}

	@Override
	public void onBackPressed()
	{
		if(m_isbackpressedalready == false)
		{
			m_isbackpressedalready = true;
			super.onBackPressed();
			overridePendingTransition(R.anim.reversemainfadein, R.anim.reversesplashfadeout);
		}
	}

	class LoadCategories extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			RelativeLayout ftr = (RelativeLayout)findViewById(R.id.footer);
			ftr.setVisibility(View.VISIBLE);
			ftr.invalidate();
		}

		/**
		 * getting Albums JSON
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_ID, subcategory_id));
			// getting JSON string from URL
			params.add(new BasicNameValuePair(TAG_PAGE, args[0]));
			String json = jsonParser.makeHttpRequest(URL_ALBUMS, "GET",
					params);

			// Check your log cat for JSON reponse
			Log.d("Albums JSON: ", "> " + json);

			try {
				posts = new JSONArray(json);
				Urls = new String[posts.length()];
				urlNames = new String[posts.length()];
				if (posts != null) {
					// looping through All albums
					for (int i = 0; i < posts.length(); i++) {
						JSONObject c = posts.getJSONObject(i);
						// Storing each json item values in variable
						String id = c.getString(TAG_POSTID);
						String name = c.getString(TAG_NAME);
						String url = c.getString(TAG_URL);
						HashMap<String, String> map = new HashMap<String, String>();
						// adding each child node to HashMap key => value
						map.put(TAG_POSTID, id);
						map.put(TAG_NAME, name);
						map.put(TAG_URL, url);
						postList.add(map);
						Urls[i]=url;
						urlNames[i]=name;
					}
					if(posts.length()==0)
					{
						noMorePages = true;
					}
				}else{
					Log.d("Albums: ", "null");
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all albums
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					imgAdapter = new ImageAdapter(PostsActivity.this,Urls,urlNames);
					listView.setAdapter(imgAdapter);
					imgAdapter.notifyDataSetChanged();
					RelativeLayout ftr = (RelativeLayout)findViewById(R.id.footer);
					ftr.setVisibility(View.GONE);
					ftr.invalidate();
				}
			});

		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE && !noMorePages) {
			if (listView.getLastVisiblePosition() >= listView.getCount() - 0) {
				currentPage++;
				new LoadCategories().execute(Integer.toString(currentPage));
			}
		}

	}

}
