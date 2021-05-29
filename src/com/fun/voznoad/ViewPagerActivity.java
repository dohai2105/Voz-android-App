package com.fun.voznoad;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fun.voznoad.R;
import com.fun.voznoad.adapter.IconAdapter;
import com.fun.voznoad.adapter.MenuAdapter;
import com.fun.voznoad.adapter.ViewPagerAdapter;
import com.fun.voznoad.callback.OnNotifyViewPagerActivity;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.model.Icon;
import com.fun.voznoad.model.SlideMenuItem;
import com.fun.voznoad.servicecontroller.HttpUrlJSONParser;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ViewPagerActivity extends FragmentActivity implements OnNotifyViewPagerActivity {

	private ViewPager viewPager;
	private ActionBar bar;
	private TextView pageIdicatorTv;
	private ImageView editpageBtn;
	private TextView firstPageTv;
	private TextView lastPageTv;
	private RelativeLayout pageContainer;
	private LinearLayout commentContainer;
	private Button postSubmit;
	private String url;
	private EditText commentText;
	private Button upImgSubmit;
	private Editor editor;
	private Button iconSelect;
	private LinearLayout iconContainer;
	private static final int PICK_IMAGE = 1;
	private static final int RESULTCODE = 11;
	private boolean isEdit = false;
	
	private String token;
	private String postid;
	private String userid;
	private Button backButton;
	private SharedPreferences sharedPreferences;
	private String savePage;
	private LinearLayout commentContainer2;
	private ArrayList<Icon> iconList;

	private String title;
	private String poststarttime;
	private String posthash;
	private String p;
	private ViewPagerAdapter viewPagerAdapter;
	private String totalpage;
	private ActionBarDrawerToggle toggle;
	private ArrayList<SlideMenuItem> menuLst;
	private MenuAdapter menuAdp;
	private DrawerLayout drawerLayout;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sharedPreferences = getSharedPreferences("voz_data", MODE_PRIVATE);
		if (sharedPreferences.getString("theme", "null").equals("black")) {
			setTheme(R.style.AppThemeDark);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vp);
		initUI();
		initActionBar();
		initCONTROL();
		initPage();
		initMenu();
		if (sharedPreferences.getString("theme", "null").equals("black")) {
			initTheme();
		}
		 
	}
	
	private void initActionBar() {
		String title = getIntent().getStringExtra("title");
		bar = getActionBar();
		bar.setTitle(Html.fromHtml("<font color=\"#ffffff\">" + title + "</font>"));
		if (getIntent().getStringExtra("isReadChap")!=null){
			bar.setSubtitle(totalpage + "/?");
		}else {
			bar.setSubtitle(1 + "/"+ totalpage);
		}
 		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
	}

	@SuppressLint("NewApi") 
	public void initMenu(){
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
		bar.setHomeButtonEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
	    toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
		drawerLayout.setDrawerListener(toggle);
		ListView menuLv = (ListView) findViewById(R.id.left_drawer);
		menuLst = new ArrayList<SlideMenuItem>();
		menuAdp = new MenuAdapter(this, menuLst );
		menuLv.setAdapter(menuAdp);
		if (getIntent().getStringExtra("fromsearch")==null) {
			menuLv.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
					drawerLayout.closeDrawers();
					String username = getSharedPreferences("voz_data",MODE_PRIVATE).getString("username", "");
					// Show logout Dialog
					if (!username.equals("") && position == 0) {
						onLogoutClick(position);
						return;
					}
					onMenuClick(position);
				}

			});
			initMenuData();
		}
	}
	
	public void onMenuClick(int position) {
		if (position!=0) {
			Intent intent = new Intent();
			intent.putExtra("menu_back", position);
			setResult(RESULTCODE, intent);
			finish();
		}
	}
	
	
	public void onLogoutClick(int position) {
		AlertDialog builder = new AlertDialog.Builder(ViewPagerActivity.this)
				.setTitle("Bạn muốn thoát tài khoản ?")
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								editor.putString("username", "");
								editor.putString("password", "");
								editor.commit();
								initMenuData();
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
							}
						}).show();
	};
	
	public void initMenuData(){
		menuLst.clear();
		for(int i =0;i<Constant.menuTitle.size() ;i++){
			SlideMenuItem slideMenuItem = null;
			String username = getSharedPreferences("voz_data", MODE_PRIVATE).getString("username", "");
			if (i == 0 && !username.equals("")) {
				slideMenuItem = new SlideMenuItem("Tài khoản : " + username);
			} else {
				slideMenuItem = new SlideMenuItem(Constant.menuTitle.get(i));
			}
			menuLst.add(slideMenuItem);
		}
		menuAdp.notifyDataSetChanged();
	}

	public void initUI() {
		try {
			url = getIntent().getStringExtra("url");
			totalpage = getIntent().getStringExtra("totalpage");
			viewPager = (ViewPager) findViewById(R.id.viewpagerContainer);
			viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this, url,Integer.parseInt(totalpage));
			viewPager.setAdapter(viewPagerAdapter);
			try{
				if (getIntent().getStringExtra("isReadChap")!=null) {
					viewPager.setCurrentItem(Integer.parseInt(totalpage)-1);
				}
			}catch(Exception e) {
				
			}
			// Number of page
			pageIdicatorTv = (TextView) findViewById(R.id.pageIdicatorTv);
			if (getIntent().getStringExtra("isReadChap")!=null){
				pageIdicatorTv.setText(totalpage + "/"+"?");
			}else {
				pageIdicatorTv.setText("1/"+ totalpage);
			}
			// Show post button
			editpageBtn = (ImageView) findViewById(R.id.editpageBtn);
			// Move to first page
			firstPageTv = (TextView) findViewById(R.id.firstpageTv);
			// Move to last page
			lastPageTv = (TextView) findViewById(R.id.lastpageTv);
			// Footer indicator container
			pageContainer = (RelativeLayout) findViewById(R.id.pageContainer);
			// Comment container
			commentContainer = (LinearLayout) findViewById(R.id.commentContainer);
			// Post submit
			postSubmit = (Button) findViewById(R.id.postSubmit);
			// Comment Edittext
			commentText = (EditText) findViewById(R.id.commentText);
			// Upload Image Button
			upImgSubmit = (Button) findViewById(R.id.upImgSubmit);

			backButton = (Button) findViewById(R.id.backButton);

			iconSelect = (Button) findViewById(R.id.iconSelect);

			commentContainer2 = (LinearLayout) findViewById(R.id.commentContainer2);

			iconContainer = (LinearLayout) findViewById(R.id.iconContainer);
			

		} catch (Exception e) {
			Log.d("FragmentVPContainer", totalpage);
		}
	}

	public void initCONTROL() {
		// Viewpager change action
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int i) {
				pageIdicatorTv.setText((i + 1) + "/"+ totalpage);
				editor.putString(url, i + "");
				editor.commit();
				ActionBar actionBar = getActionBar();
				actionBar.setSubtitle((i + 1) + "/"+ totalpage);
			}

			@Override
			public void onPageScrolled(int i, float f, int i2) {
			}

			@Override
			public void onPageScrollStateChanged(int i) {
			}
		});
		// Show comment screen
		editpageBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				commentText.requestFocus();
				((RelativeLayout)findViewById(R.id.contentContainer)).setVisibility(View.GONE);
				commentContainer.setVisibility(View.VISIBLE);

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			}
		});

		// Post submit click
		postSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isEdit) {
					new EditTask().execute();
				} else {
					new QuickCommentTask().execute();
				}
			}
		});

		// Move to first page
		firstPageTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (Integer.parseInt(totalpage) == 1) {
					return;
				}
				viewPager.setCurrentItem(0);
			}
		});
		// Move to second page
		lastPageTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (Integer.parseInt(totalpage) == 1) {
					return;
				}
				viewPager.setCurrentItem(Integer.parseInt(totalpage));
			}
		});
		// Page number click - show dialog to enter page 's number
		pageIdicatorTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Integer.parseInt(totalpage) < 2) {
					return;
				}
				AlertDialog.Builder alert = new AlertDialog.Builder(
						ViewPagerActivity.this);

				alert.setTitle("Trang muốn tới 1-"+ totalpage);
				// Set an EditText view to get user input
				final EditText input = new EditText(ViewPagerActivity.this);
				input.setInputType(InputType.TYPE_CLASS_NUMBER);
				alert.setView(input);

				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String value = input.getText().toString();
								if (value.equalsIgnoreCase("")) value = "0";
								if (Integer.parseInt(value) <= Integer
										.parseInt(getIntent().getStringExtra(
												"totalpage"))) {
									viewPager.setCurrentItem(Integer
											.parseInt(value) - 1);
								} else {
									Toast.makeText(ViewPagerActivity.this,
											"Trang không hợp lệ",
											Toast.LENGTH_LONG).show();
								}
								dialog.cancel();
							}
						});
				alert.show();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			}
		});

		// Upload image button
		upImgSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE);
			}
		});

		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((RelativeLayout)findViewById(R.id.contentContainer)).setVisibility(View.VISIBLE);
				iconContainer.setVisibility(View.GONE);
				commentContainer2.setVisibility(View.VISIBLE);
				commentContainer.setVisibility(View.GONE);
				commentText.setText("");
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
			}
		});

		iconSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int currentPosition = commentText.getSelectionStart();
				showIconGridView(currentPosition);
			}
		});
		
	}

	public void showIconGridView(final int currentPosition) {
		iconContainer.setVisibility(View.VISIBLE);
		commentContainer2.setVisibility(View.GONE);
		GridView iconGridView = (GridView) findViewById(R.id.iconGridView);
		try {
			if (iconList == null) {
				iconList = Constant.getListIcon(ViewPagerActivity.this);
			}
			IconAdapter iconAdapter = new IconAdapter(ViewPagerActivity.this,iconList);
			iconGridView.setAdapter(iconAdapter);
			iconGridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView, View view,
						int position, long arg3) {
					commentText.requestFocus();
					String comment = commentText.getText().toString();
					if (currentPosition < comment.length()) {
						comment = comment.substring(0, currentPosition)+ iconList.get(position).getIconSymbol()
								+ comment.substring(currentPosition,comment.length());
					} else {
						comment = comment+ iconList.get(position).getIconSymbol();
					}
					commentText.setText(comment);
					commentText.setSelection(currentPosition+ iconList.get(position).getIconSymbol().length());
					iconContainer.setVisibility(View.GONE);
					commentContainer2.setVisibility(View.VISIBLE);
				}
			});
		} catch (Exception e) {
		}
	}
	
	@SuppressLint("NewApi") 
	public void initTheme(){
		pageContainer.setBackgroundColor(Color.parseColor("#000000")); 
		firstPageTv.setTextColor(Color.parseColor("#ffffff"));
		firstPageTv.setBackgroundColor(Color.parseColor("#000000"));
		lastPageTv.setTextColor(Color.parseColor("#ffffff"));
		lastPageTv.setBackgroundColor(Color.parseColor("#000000"));
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			editpageBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.newpost));
		} else {
			editpageBtn.setBackground(getResources().getDrawable(R.drawable.newpost));
		}
	}

	public void initPage() {
 		editor = sharedPreferences.edit();
		editor.commit();
		if (getIntent().getStringExtra("comment") != null) {
			viewPager.setCurrentItem(Integer.parseInt(totalpage)-1);
		} else if (getIntent().getStringExtra("isquote")==null&&getIntent().getStringExtra("isReadChap")==null){
			savePage = sharedPreferences.getString(url, "0");
			viewPager.setCurrentItem(Integer.parseInt(savePage));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
	}

	@Override
	public void onSetActionBarTitle(String title, String subTitle) {
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(subTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.youtubemenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (toggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.subcribeAcBar:
			new SubcribeTask().execute();
			return true;
		case R.id.newQuickCommnet:
			editpageBtn.performClick();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Upload Image callback
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
			Uri _uri = data.getData();
			String imageFilePath = "";
			try {
				// User had pick an image.
				Cursor cursor = getContentResolver().query(_uri, null, null,null, null);
				cursor.moveToFirst();
				String document_id = cursor.getString(0);
				document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
				cursor.close();

				cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								null, MediaStore.Images.Media._ID + " = ? ",
								new String[] { document_id }, null);
				cursor.moveToFirst();
				String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				cursor.close();
				imageFilePath = path;
				cursor.close();
				// Call service to upload image
			} catch (Exception e) {
				imageFilePath = _uri.getPath();
			}
			new UploadImageTask().execute(imageFilePath);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// Quick reply task .
	public class QuickCommentTask extends AsyncTask<Void, Void, Void> {
		Boolean isComment = true;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> data = new ArrayList<NameValuePair>();
				String comment =  commentText.getText().toString();
				String useSign = sharedPreferences.getString("useSign", "truez");
				if (useSign.equals("true")){
					comment = comment + "\r\n"+Constant.sign;
				}
				data.add(new BasicNameValuePair("message", comment));
				data.add(new BasicNameValuePair("wysiwyg", "0"));
				data.add(new BasicNameValuePair("styleid", "0"));
				data.add(new BasicNameValuePair("fromquickreply", "1"));
				data.add(new BasicNameValuePair("securitytoken", token));
				data.add(new BasicNameValuePair("do", "postreply"));
				data.add(new BasicNameValuePair("t", postid));
				data.add(new BasicNameValuePair("p", "who cares"));
				data.add(new BasicNameValuePair("s", ""));
				data.add(new BasicNameValuePair("specifiedpost", "0"));
				data.add(new BasicNameValuePair("parseurl", "1"));
				data.add(new BasicNameValuePair("loggedinuser", userid));
				data.add(new BasicNameValuePair("sbutton", "Post Quick Reply"));
				HttpUrlJSONParser jsonParser = new HttpUrlJSONParser();
				SharedPreferences sharedPreferences = getSharedPreferences("voz_data", MODE_PRIVATE);
				jsonParser.setCookie(sharedPreferences.getString("cookie_data",""));
				jsonParser.setAllowSaveCookie(false);
				jsonParser.postForm("http://vozforums.com/newreply.php?do=postreply&t="+ postid, "post", data);
			} catch (Exception e) {
				e.printStackTrace();
				isComment = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Refresh activity
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
			Intent intent = getIntent();
			intent.putExtra("comment", "comment");
			intent.putExtra("totalpage", totalpage);
			Constant.mutilquoteCookieArr.clear();
			finish();
			startActivity(intent);
		}
	}

	// Quick reply task .
	public class EditTask extends AsyncTask<Void, Void, Void> {
		Boolean isComment = true;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("reason", ""));
				data.add(new BasicNameValuePair("title", title));
				data.add(new BasicNameValuePair("message", commentText.getText().toString()));
				data.add(new BasicNameValuePair("wysiwyg", "0"));
				data.add(new BasicNameValuePair("s", ""));
				data.add(new BasicNameValuePair("securitytoken", token));
				data.add(new BasicNameValuePair("do", "updatepost"));
				data.add(new BasicNameValuePair("p", p));
				data.add(new BasicNameValuePair("posthash", posthash));
				data.add(new BasicNameValuePair("poststarttime", poststarttime));
				data.add(new BasicNameValuePair("sbutton", "Save Changes"));
				data.add(new BasicNameValuePair("parseurl", "1"));
				data.add(new BasicNameValuePair("emailupdate", "0"));
				HttpUrlJSONParser jsonParser = new HttpUrlJSONParser();
				SharedPreferences sharedPreferences = getSharedPreferences("voz_data", MODE_PRIVATE);
				jsonParser.setCookie(sharedPreferences.getString("cookie_data",""));
				jsonParser.setAllowSaveCookie(false);
				jsonParser.postForm("http://vozforums.com/editpost.php?do=updatepost&p="+ p, "post", data);
			} catch (Exception e) {
				e.printStackTrace();
				isComment = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Refresh activity
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
			Intent intent = getIntent();
			Constant.mutilquoteCookieArr.clear();
			finish();
			startActivity(intent);
		}
	}

	 

	// Fragment Pager3 callback to ViewpagerActivity
	@Override
	public void onSetTokentDta(String token, String postid, String userid) {
		this.token = token;
		this.postid = postid;
		this.userid = userid;
	}

	// Upload Imager task
	public class UploadImageTask extends AsyncTask<String, Void, Void> {

		private String FILENAME = "dohai.png";
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		HttpURLConnection conn = null;
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 5 * 1024 * 1024; // 5mb
		private ProgressDialog uploadDialog;
		private String resutlContent;

		@Override
		protected void onPreExecute() {
			uploadDialog = new ProgressDialog(ViewPagerActivity.this);
			uploadDialog.setMessage("Upload .....");
			uploadDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				URL url = new URL("http://pik.vn/");
				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty(
						"Cookie",
						"csrftoken=EEPCYB93WZmdQiDMDbji3KSjdcF6BxTY; _ga=GA1.2.1374335103.1416655565; _gat=1");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				// conn.setRequestProperty("uploaded_file", FILENAME);

				DataOutputStream dos = new DataOutputStream(
						conn.getOutputStream());
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"csrfmiddlewaretoken\""+ lineEnd);
				dos.writeBytes("Content-Type: text/plain;charset=utf-8"+ lineEnd);
				dos.writeBytes(lineEnd);
				dos.writeBytes("EEPCYB93WZmdQiDMDbji3KSjdcF6BxTY");
				dos.writeBytes(lineEnd);
				// Separate post param
				dos.writeBytes("--" + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\""+ FILENAME + "\"" + lineEnd);
				dos.writeBytes("Content-Type:"+ URLConnection.guessContentTypeFromName("icon.png")+ lineEnd);
				dos.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
				dos.writeBytes(lineEnd);

				File file = new File(params[0]);
				FileInputStream fileInputStream = new FileInputStream(file);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];
				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				while (bytesRead > 0) {
					dos.write(buffer, 0, bufferSize);
				}
				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				fileInputStream.close();
				// Responses from the server (code and message)
				int serverResponseCode = conn.getResponseCode();
				resutlContent = "";
				if (serverResponseCode == 200) {
					InputStream is = conn.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is, "utf-8"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					resutlContent = sb.toString();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (uploadDialog.isShowing()) {
				Document doc = Jsoup.parse(resutlContent);
				try {
					String imgUrl = doc.select("img#img-result").first()
							.attr("src");
					if (!imgUrl.equalsIgnoreCase("")) {
						commentText.requestFocus();
						commentText.setText(commentText.getText().toString()+ "\n" + "[IMG]" + imgUrl + "[/IMG]" + "\n");
						commentText.setSelection(commentText.getText().length());
					}
				} catch (Exception e) {

				}
				uploadDialog.dismiss();
			}
		}
	}

	public class SubcribeTask extends AsyncTask<Void, Void, Void> {
		private boolean isSucribe;
		private ProgressDialog uploadDialog;

		@Override
		protected void onPreExecute() {
			uploadDialog = new ProgressDialog(ViewPagerActivity.this);
			uploadDialog.setMessage("Thêm vào list .....");
			uploadDialog.show();
			isSucribe = true;
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<NameValuePair> data = new ArrayList<NameValuePair>();
			data.add(new BasicNameValuePair("securitytoken", token));
			data.add(new BasicNameValuePair("s", ""));
			data.add(new BasicNameValuePair("do", "doaddsubscription"));
			data.add(new BasicNameValuePair("emailupdate", "0"));
			data.add(new BasicNameValuePair("folderid", "0"));
			data.add(new BasicNameValuePair("threadid", postid));
			data.add(new BasicNameValuePair("url","http://vozforums.com/showthread.php?t=" + postid+ "&page=1&pp"
							+ totalpage));
			HttpUrlJSONParser jsonParser = new HttpUrlJSONParser();
			SharedPreferences sharedPreferences = getSharedPreferences("voz_data", MODE_PRIVATE);
			jsonParser.setCookie(sharedPreferences.getString("cookie_data", ""));
			jsonParser.setAllowSaveCookie(false);
			try {
				jsonParser.postForm("http://vozforums.com/subscription.php?do=doaddsubscription&threadid="+ postid, "post", data);
			} catch (Exception e) {
				e.printStackTrace();
				isSucribe = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (uploadDialog.isShowing()) {
				uploadDialog.dismiss();
				if (isSucribe) {
					Toast.makeText(ViewPagerActivity.this,"Subcribe thành công", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ViewPagerActivity.this, "Subcribe thất bại",Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	public void onNotifyQuote(String quote) {
		isEdit = false;
		commentText.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		((RelativeLayout)findViewById(R.id.contentContainer)).setVisibility(View.GONE);
//		viewPager.setVisibility(View.GONE);
//		pageContainer.setVisibility(View.GONE);
		commentContainer.setVisibility(View.VISIBLE);
		commentText.setText(quote + "\n");
		commentText.setSelection(commentText.getText().length());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Constant.mutilquoteCookieArr.clear();
		Constant.quoteListURL="";
		Log.d("dohai.com", "onDestroy-2");
	}

	@Override
	public void onNotifyTopToBottomSwipe() {
		if (pageContainer.getVisibility() == View.GONE) {
			pageContainer.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onNotifyBottomToTopSwipe() {
		if (pageContainer.getVisibility() == View.VISIBLE) {
			pageContainer.setVisibility(View.GONE);
		}
	}

	
	@Override
	public void onNotifyEdit(String quote, String title, String poststarttime,
			String posthash, String p) {
		isEdit = true;
		commentText.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		((RelativeLayout)findViewById(R.id.contentContainer)).setVisibility(View.GONE);
		commentContainer.setVisibility(View.VISIBLE);
		commentText.setText(quote + "\n");
		commentText.setSelection(commentText.getText().length());
		this.posthash = posthash;
		this.p = p;
		this.poststarttime = poststarttime;
		this.title = title;
	}

	@Override
	public void onNotifyQuoteList(String threadID, String currentPage) {
		bar.setSubtitle(currentPage + "/"+ totalpage);
		Constant.quoteListURL = "http://vozforums.com/showthread.php?t="+threadID;
		viewPager.setCurrentItem(Integer.parseInt(currentPage)-1);
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		toggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		toggle.onConfigurationChanged(newConfig); 
		 
	}

	@Override
	public void onNotifyTotalPage(String totalPage) {
		this.totalpage = totalPage;
		viewPagerAdapter.setCount(Integer.parseInt(totalPage));
		viewPagerAdapter.notifyDataSetChanged();
	}
	
}