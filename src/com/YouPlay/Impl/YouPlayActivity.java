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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.YouPlay.Helper.AlertDialogManager;
import com.YouPlay.Helper.JSONParser;
import com.YouPlay.Helper.connectionDetector;


public class YouPlayActivity extends ListActivity implements OnScrollListener{
	/** Called when the activity is first created. */
	connectionDetector cd;

	AlertDialogManager  alert = new AlertDialogManager();
	private String category_id,category_name;
	private ProgressDialog progressDialog;
	private ListView lv;
	private static final String TAG_ID = "categoryid";
	private static final String TAG_CATEGORY = "categoryname";
	private static final String TAG_PAGE = "currentpage";
	JSONArray categories = null;
	private static final String URL_ALBUMS = "http://anddev.comuv.com/docs/albums.php";
	private static final String TAG_NAME = "categoryname";
	private static final String TAG_URL = "categoryurl";
	private int currentPage = 1;
	JSONParser jsonParser = new JSONParser();
	JSONArray datalist = null;
	ArrayList<HashMap<String, String>> categoryList;
	View footer;
	private boolean m_isbackpressedalready;
	private boolean noMorePages = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//        PaginationComposerAdapter adapter;
//		final boolean customTitle = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		m_isbackpressedalready = false;
		setContentView(R.layout.category_activity);
		footer = (View)getLayoutInflater().inflate(R.layout.footer,null);
		//        if(customTitle)
		//        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);

		cd = new connectionDetector(getApplicationContext());
		categoryList = new ArrayList<HashMap<String,String>>();
		// Check for internet connection
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(YouPlayActivity.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		new LoadCategories().execute("1");
		lv = getListView();

		lv.setOnScrollListener(this);
		lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {


			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {


				Intent i = new Intent(getApplicationContext(), SubCategoryActivity.class);

				// send album id to tracklist activity to get list of songs under that album
				String category_id = ((TextView) view.findViewById(R.id.category_id)).getText().toString();
				i.putExtra("category_id", category_id);              

				startActivity(i);
				overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);
			}
		});
	}

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
			params.add(new BasicNameValuePair(TAG_PAGE, args[0]));
			// getting JSON string from URL
			String json = jsonParser.makeHttpRequest(URL_ALBUMS, "GET",
					params);

			// Check your log cat for JSON reponse
			Log.d("Albums JSON: ", "> " + json);

			try {
				categories = new JSONArray(json);

				if (categories != null) {
					// looping through All albums
					for (int i = 0; i < categories.length(); i++) {
						JSONObject c = categories.getJSONObject(i);
						// Storing each json item values in variable
						String id = c.getString(TAG_ID);
						String name = c.getString(TAG_NAME);
						String url = c.getString(TAG_URL);
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();
						// adding each child node to HashMap key => value
						map.put(TAG_ID, id);
						map.put(TAG_NAME, name);
						map.put(TAG_URL, url);
						// adding HashList to ArrayList
						categoryList.add(map);
					}
					if(categories.length()==0)
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
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					int index = lv.getFirstVisiblePosition();
					View v = lv.getChildAt(0);
					int top = (v == null) ? 0 : v.getTop();
					
					CategoryAdapter adapter = new CategoryAdapter(YouPlayActivity.this, R.layout.category_item, TAG_ID, TAG_NAME,TAG_URL, categoryList,"CategoryActivity");
					lv.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					lv.setSelectionFromTop(index, top);
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
			if (lv.getLastVisiblePosition() >= lv.getCount() - 2 ) {
				currentPage++;
				new LoadCategories().execute(Integer.toString(currentPage));

			}
		}

	}
	
}

