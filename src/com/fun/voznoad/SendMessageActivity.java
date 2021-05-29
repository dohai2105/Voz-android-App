package com.fun.voznoad;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.fun.voznoad.R;
import com.fun.voznoad.adapter.IconAdapter;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.model.Icon;
import com.fun.voznoad.servicecontroller.HttpUrlJSONParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SendMessageActivity extends FragmentActivity{
	
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private String url;
	private Button postSubmit;
	private EditText titleText;
	private EditText commentText;
	private Button upImgSubmit;
	private Button backButton;
	private Button iconSelect;
	private LinearLayout commentContainer2;
	private LinearLayout iconContainer;
	private ArrayList<Icon> iconList;
	private static final int PICK_IMAGE = 1;
	private WebView webview;
	private Button responseBtn;
	private LinearLayout commentContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sharedPreferences = getSharedPreferences("voz_data", MODE_PRIVATE);
		editor = sharedPreferences.edit();
		if (sharedPreferences.getString("theme", "null").equals("null")) {
			editor.putString("theme", "normal");
			editor.commit();
		}

		if (sharedPreferences.getString("theme", "null").equals("black")) {
			setTheme(R.style.AppThemeDark);
		}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmessage);
        initUI();
        initCONTROL();
        if(getIntent().getStringExtra("quickmessage")==null){
        	titleText.setVisibility(View.GONE);
        	new GetContentTask().execute();
        }else {
        	commentContainer.setVisibility(View.GONE);
			commentContainer2.setVisibility(View.VISIBLE);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
	}

	private void initUI() {
		url = getIntent().getStringExtra("url");
		if (getIntent().getStringExtra("quickmessage") == null) {
			String[] targetids = url.split("=");
			targetid = targetids[targetids.length - 1];
		}
		// Post submit
		postSubmit = (Button) findViewById(R.id.postSubmit);
		// Title Edittext
		titleText = (EditText) findViewById(R.id.titleText);
		// Comment Edittext
		commentText = (EditText) findViewById(R.id.commentText);
		// Upload Image Button
		upImgSubmit = (Button) findViewById(R.id.upImgSubmit);

		backButton = (Button) findViewById(R.id.backButton);

		iconSelect = (Button) findViewById(R.id.iconSelect);

		commentContainer2 = (LinearLayout) findViewById(R.id.commentContainer2);
		
		commentContainer = (LinearLayout) findViewById(R.id.commentContainer);
		
		iconContainer = (LinearLayout) findViewById(R.id.iconContainer);
		
		webview = (WebView) findViewById(R.id.inboxWebView);
		
		responseBtn = (Button) findViewById(R.id.responseBtn);
	}
	
	public void initCONTROL() {

		responseBtn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				commentContainer.setVisibility(View.GONE);
				commentContainer2.setVisibility(View.VISIBLE);
				commentText.setSelection(commentText.getText().length());
				commentText.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			}
		});
		
		// Post submit click
		postSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(getIntent().getStringExtra("quickmessage")==null){
					new ReplyTask().execute();
				}else {
					new QuickReplyTask().execute();
				}
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
// 				Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//				startActivityForResult(intent, PICK_IMAGE);
			}
		});

		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
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
	
	public Map<String, String> onGetCookie() {
		try{
			return Constant.getCookies(sharedPreferences.getString("cookie_data", ""));
		}catch(Exception e){
			return new HashMap<String, String>();
		}
	}
	
	private String wysiwyg="";
	private String styleid="";
	private String fromquickreply="";
	private String s="";
	private String securitytoken="";
	private String d_o="";
	private String pmid="";
	private String loggedinuser="";
	private String parseurl="";
	private String title="";
	private String recipients="";
	private String forward="";
	private String savecopy="";
	private String sbutton="";
	private String targetid;
	
	public class GetContentTask extends AsyncTask<Void,Void, Void>{

		private String comment="";
		private String content = "";
 

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Document doc = Jsoup.connect(url).timeout(Constant.timeout).cookies(onGetCookie()).get();
				content = doc.select("#post_message_").first().html();
				comment = doc.select("textarea").first().html();
				wysiwyg = doc.select("input[name = wysiwyg]").val();
				styleid = doc.select("input[name = styleid]").val();
				fromquickreply = doc.select("input[name = fromquickreply]").val();
				s = doc.select("input[name = s]").val();
				securitytoken = doc.select("input[name = securitytoken]").val();
				d_o = doc.select("input[name = do]").val();
				pmid = doc.select("input[name = pmid]").val();
				loggedinuser = doc.select("input[name = loggedinuser]").val();
				parseurl = doc.select("input[name = parseurl]").val();
				title = doc.select("input[name = title]").val();
				recipients = doc.select("input[name = recipients]").val();
				forward = doc.select("input[name = forward]").val();
				savecopy = doc.select("input[name = savecopy]").val();
				sbutton = doc.select("input[name = sbutton]").val();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
 
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			commentText.setText(Html.fromHtml(comment)+ "\n");
			int fontSizePlus = Integer.parseInt(sharedPreferences.getString("fontsize", "0"));
			webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
			webview.getSettings().setJavaScriptEnabled(true);
			webview.getSettings().setDomStorageEnabled(true);
			webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN );
			webview.getSettings().setDefaultFontSize(17+fontSizePlus);
			webview.loadDataWithBaseURL("http://vozforums.com/", content, "text/html", "UTF-8", null);
		}
	}
	
	
	public class QuickReplyTask extends AsyncTask<Void, Void, Void>{
		private ProgressDialog uploadDialog;
		private boolean isSend;

		@Override
		protected void onPreExecute() {
			uploadDialog = new ProgressDialog(SendMessageActivity.this);
			uploadDialog.setMessage("Gửi .....");
			uploadDialog.show();
			isSend = true;
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			List<NameValuePair> data = new ArrayList<NameValuePair>();
			data.add(new BasicNameValuePair("recipients", getIntent().getStringExtra("username")));
			data.add(new BasicNameValuePair("bccrecipients", ""));
			data.add(new BasicNameValuePair("title",titleText.getText().toString()));
			data.add(new BasicNameValuePair("message", commentText.getText().toString()));
			data.add(new BasicNameValuePair("wysiwyg", "0"));
			data.add(new BasicNameValuePair("iconid", "0"));
			data.add(new BasicNameValuePair("s", ""));
			data.add(new BasicNameValuePair("do", "insertpm"));
			data.add(new BasicNameValuePair("pmid", ""));
			data.add(new BasicNameValuePair("forward", ""));
			data.add(new BasicNameValuePair("sbutton", "Submit Message"));
			data.add(new BasicNameValuePair("parseurl", "1"));
			data.add(new BasicNameValuePair("securitytoken", getIntent().getStringExtra("token"))); 
			HttpUrlJSONParser jsonParser = new HttpUrlJSONParser();
			SharedPreferences sharedPreferences = getSharedPreferences("voz_data", MODE_PRIVATE);
			jsonParser.setCookie(sharedPreferences.getString("cookie_data", ""));
			jsonParser.setAllowSaveCookie(false);
			try {
				jsonParser.postForm(url, "post", data);
			} catch (Exception e) {
				e.printStackTrace();
				isSend = false;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (isSend) {
				if (uploadDialog.isShowing()) {
					uploadDialog.dismiss();
				}
				Toast.makeText(SendMessageActivity.this, "thành công", Toast.LENGTH_SHORT).show();
			}
			finish();
		}
	}

	
	public class ReplyTask extends AsyncTask<Void, Void, Void>{

		private ProgressDialog uploadDialog;
		private boolean isSend;

		@Override
		protected void onPreExecute() {
			uploadDialog = new ProgressDialog(SendMessageActivity.this);
			uploadDialog.setMessage("Gửi .....");
			uploadDialog.show();
			isSend = true;
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			List<NameValuePair> data = new ArrayList<NameValuePair>();
			data.add(new BasicNameValuePair("wysiwyg", wysiwyg));
			data.add(new BasicNameValuePair("styleid", styleid));
			data.add(new BasicNameValuePair("fromquickreply", fromquickreply));
			data.add(new BasicNameValuePair("s", s));
			data.add(new BasicNameValuePair("securitytoken", securitytoken));
			data.add(new BasicNameValuePair("do", d_o));
			data.add(new BasicNameValuePair("pmid", pmid));
			data.add(new BasicNameValuePair("loggedinuser", loggedinuser));
			data.add(new BasicNameValuePair("parseurl", parseurl));
			data.add(new BasicNameValuePair("title", title));
			data.add(new BasicNameValuePair("message", commentText.getText().toString()));
			data.add(new BasicNameValuePair("recipients", recipients));
			data.add(new BasicNameValuePair("forward", forward));
			data.add(new BasicNameValuePair("savecopy", savecopy));
			data.add(new BasicNameValuePair("sbutton", sbutton));
			HttpUrlJSONParser jsonParser = new HttpUrlJSONParser();
			SharedPreferences sharedPreferences = getSharedPreferences("voz_data", MODE_PRIVATE);
			jsonParser.setCookie(sharedPreferences.getString("cookie_data", ""));
			jsonParser.setAllowSaveCookie(false);
			try {
				jsonParser.postForm("http://vozforums.com/private.php?do=insertpm&pmid="+ targetid, "post", data);
			} catch (Exception e) {
				e.printStackTrace();
				isSend = false;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (isSend) {
				if (uploadDialog.isShowing()) {
					uploadDialog.dismiss();
				}
				Toast.makeText(SendMessageActivity.this, "thành công", Toast.LENGTH_SHORT).show();
			}
			finish();
		}
		
	}
	
	public void showIconGridView(final int currentPosition){
		iconContainer.setVisibility(View.VISIBLE);
		commentContainer2.setVisibility(View.GONE);
		GridView iconGridView = (GridView) findViewById(R.id.iconGridView);
		try{
			if (iconList==null) {
				iconList = Constant.getListIcon(SendMessageActivity.this);
			}
			IconAdapter iconAdapter = new IconAdapter(SendMessageActivity.this, iconList);
			iconGridView.setAdapter(iconAdapter);
			iconGridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int position,long arg3) {
					commentText.requestFocus();
					String comment = commentText.getText().toString();
					if(currentPosition<comment.length()){
						comment = comment.substring(0, currentPosition) + iconList.get(position).getIconSymbol() + comment.substring(currentPosition,comment.length());
					}else {
						comment = comment+iconList.get(position).getIconSymbol();
					}
					commentText.setText(comment);
					commentText.setSelection(currentPosition+iconList.get(position).getIconSymbol().length());
					iconContainer.setVisibility(View.GONE);
					commentContainer2.setVisibility(View.VISIBLE);
				}
			});
		}catch(Exception e){
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
			Uri _uri = data.getData();
			String imageFilePath = "";
			try {
				// User had pick an image.
				Cursor cursor = getContentResolver().query(_uri, null, null, null, null);
				   cursor.moveToFirst();
				   String document_id = cursor.getString(0);
				   document_id = document_id.substring(document_id.lastIndexOf(":")+1);
				   cursor.close();

				   cursor = getContentResolver().query( 
				   android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				   null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
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
	
	// Upload Imager task
		public class UploadImageTask extends AsyncTask<String, Void, Void> {

			private String FILENAME = "dohai.png";
			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String boundary = "*****";
			HttpURLConnection conn = null;
			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
	        int maxBufferSize =5 * 1024 * 1024; // 5mb
	        private ProgressDialog uploadDialog;
			private String resutlContent;
	        
	        @Override
			protected void onPreExecute() {
				uploadDialog = new ProgressDialog(SendMessageActivity.this);
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
					conn.setRequestProperty("Cookie", "csrftoken=EEPCYB93WZmdQiDMDbji3KSjdcF6BxTY; _ga=GA1.2.1374335103.1416655565; _gat=1");
					conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
//						conn.setRequestProperty("uploaded_file", FILENAME);
					

					DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	                dos.writeBytes(twoHyphens + boundary + lineEnd); 
	                dos.writeBytes("Content-Disposition: form-data; name=\"csrfmiddlewaretoken\"" + lineEnd);
	                dos.writeBytes("Content-Type: text/plain;charset=utf-8" + lineEnd);
	                dos.writeBytes(lineEnd);
	                dos.writeBytes("EEPCYB93WZmdQiDMDbji3KSjdcF6BxTY");
	                dos.writeBytes(lineEnd);
	                // Separate post param
					dos.writeBytes("--" + boundary+lineEnd);
	            	
	                dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\""+ FILENAME + "\"" + lineEnd);
	                dos.writeBytes("Content-Type:"+URLConnection.guessContentTypeFromName("icon.png") + lineEnd);
	                dos.writeBytes("Content-Transfer-Encoding: binary"+lineEnd);
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
					if (serverResponseCode==200) {
						InputStream is = conn.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
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
						String imgUrl = doc.select("img#img-result").first().attr("src");
						if (!imgUrl.equalsIgnoreCase("")) {
							commentText.requestFocus();
							commentText.setText(commentText.getText().toString() + "\n" +"[IMG]"+imgUrl+"[/IMG]"+"\n" );
							commentText.setSelection(commentText.getText().length());
						}
					} catch (Exception e) {

					}
					uploadDialog.dismiss();
				}
			}
		}
		
		@Override
		protected void onDestroy() {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
			super.onDestroy();
		}

}
