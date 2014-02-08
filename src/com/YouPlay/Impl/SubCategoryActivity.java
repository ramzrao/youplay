package com.YouPlay.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.YouPlay.Helper.AlertDialogManager;
import com.YouPlay.Helper.connectionDetector;
import com.YouPlay.Helper.JSONParser;
import com.YouPlay.Impl.YouPlayActivity.LoadCategories;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

public class SubCategoryActivity extends ListActivity implements OnScrollListener{

	connectionDetector cd;

	AlertDialogManager alertDialog = new AlertDialogManager();

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();

	ArrayList<HashMap<String, String>> subCategoryList;

	// tracks JSONArray
	JSONArray albums = null;

	// Album id
	String category_id, category_name;

	private boolean m_isbackpressedalready;

	private static final String URL_ALBUMS = "http://anddev.comuv.com/docs/album_tracks.php";

	// ALL JSON node names
	private static final String TAG_SubCategories = "SubCategories";
	private static final String TAG_ID = "categoryid";
	private static final String TAG_SUBID = "subcategoryid";
	private static final String TAG_NAME = "subcategoryname";
	private static final String TAG_URL = "subcategoryurl";
	private static final String TAG_CATEGORY = "Category";
	private static final String TAG_PAGE = "currentpage";
	ListView lv;
	View footer;
	private int currentPage = 1;
	private boolean noMorePages = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subcategory_activity);

		cd = new connectionDetector(getApplicationContext());

		if(!cd.isConnectingToInternet())
		{
			alertDialog.showAlertDialog(SubCategoryActivity.this, "Internet not working", "Please connect to working Internet connection", false);
			return;
		}
		footer = (View)getLayoutInflater().inflate(R.layout.footer,null);
		Intent i = getIntent();
		category_id = i.getStringExtra("category_id");
		m_isbackpressedalready = false;
		subCategoryList = new ArrayList<HashMap<String,String>>();
		new LoadSubCategories().execute("1");
		lv = getListView();
		lv.setOnScrollListener(this);
		lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				// On selecting single track get song information
				Intent i = new Intent(getApplicationContext(), PostsActivity.class);
				String subcategory_id = ((TextView) view.findViewById(R.id.subcategory_id)).getText().toString();
				i.putExtra("subcategory_id", subcategory_id);
				startActivity(i);
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

	class LoadSubCategories extends AsyncTask<String, String, String> {

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
		 * getting tracks json and parsing
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// post album id as GET parameter
			params.add(new BasicNameValuePair(TAG_ID, category_id));
			params.add(new BasicNameValuePair(TAG_PAGE, args[0]));
			// getting JSON string from URL
			String json = jsonParser.makeHttpRequest(URL_ALBUMS, "GET",
					params);

			// Check your log cat for JSON reponse
			Log.d("Track List JSON: ", json);

			try {
				albums =  new JSONArray(json);

				if (albums != null) {
					// looping through All songs
					for (int i = 0; i < albums.length(); i++) {
						JSONObject c = albums.getJSONObject(i);
						// Storing each json item in variable
						String subcategory_id = c.getString(TAG_SUBID);
						// track no - increment i value
						String name = c.getString(TAG_NAME);
						
						String url = c.getString(TAG_URL);
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();
						// adding each child node to HashMap key => value
						map.put(TAG_SUBID, subcategory_id);
						map.put(TAG_NAME, name);
						map.put(TAG_URL, url);
						// adding HashList to ArrayList
						subCategoryList.add(map);
					}
					if(albums.length()==0)
					{
						noMorePages = true;
					}
				} else {
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
					CategoryAdapter adapter = new CategoryAdapter(SubCategoryActivity.this, R.layout.subcategory_item, TAG_SUBID, TAG_NAME,TAG_URL, subCategoryList,"SubCategoryActivity");
					lv.setAdapter(adapter);
					adapter.notifyDataSetChanged();
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
			if (lv.getLastVisiblePosition() >= lv.getCount()-1 ) {
				currentPage++;
				new LoadSubCategories().execute(Integer.toString(currentPage));
			}
		}

	}
}
