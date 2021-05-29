package com.fun.voznoad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.fun.voznoad.adapter.MenuAdapter;
import com.fun.voznoad.callback.OnMenuClickListener;
import com.fun.voznoad.callback.OnNotifyMainListener;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.fragment.InboxFragment;
import com.fun.voznoad.fragment.Page1;
import com.fun.voznoad.fragment.Page2;
import com.fun.voznoad.fragment.PageSubcribe;
import com.fun.voznoad.fragment.QuoteFragment;
import com.fun.voznoad.model.SlideMenuItem;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends FragmentActivity implements OnNotifyMainListener ,OnMenuClickListener {

    private SharedPreferences sharedPreferences;
	private Editor editor;
	private ActionBarDrawerToggle toggle;
	private DrawerLayout drawerLayout;
	private ActionBar bar;
	private ProgressBar mainProgress;
	private boolean isStop;
	private boolean firstLogin = true;
	private ArrayList<SlideMenuItem> menuLst;
	private MenuAdapter menuAdp;
	private ListView menuLv;
	private EditText usernameEdt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    // Init sharePreferences
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
        setContentView(R.layout.activity_main);
        bar = getActionBar();
        bar.setTitle((Html.fromHtml("<font color=\"#ffffff\">" + "Voz" + "</font>")));
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        initUI();
        initDATA();
        initSign();
        String username = sharedPreferences.getString("username", "a");
        String password = sharedPreferences.getString("password", "a");
        new LoginTask().execute(username+"---"+password);
    }
	
	public void initSign(){
		 String sign = sharedPreferences.getString("sign", "no");
		 if(sign.equals("no")){
			 String deviceName = getDeviceName();
			 Constant.sign = "\n\r[I]Sent from my " + deviceName + " using [URL=\"https://play.google.com/store/apps/details?id=com.fun.voz\"] vozForums[/URL][/I]";
			 editor.putString("sign", sign+"");
			 editor.commit();
		 }else {
			 Constant.sign = sign;
		 }
 	}
 
	
	@SuppressLint("NewApi") public void initUI(){
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
	    // Init Menu
//		toggle = new ActionBarDrawerToggle(this, drawerLayout,
//				 R.string.app_name, R.string.app_name) {
//			@Override
//			public void onDrawerClosed(View drawerView) {
//				super.onDrawerClosed(drawerView);
//			}
//			@Override
//			public void onDrawerOpened(View drawerView) {
//				super.onDrawerOpened(drawerView);
//			}
//		};
		drawerLayout.setDrawerListener(toggle);
		menuLv = (ListView) findViewById(R.id.left_drawer);
		menuLst = new ArrayList<SlideMenuItem>();
		menuAdp = new MenuAdapter(this, menuLst );
		menuLv.setAdapter(menuAdp);
		initMenuData();
		mainProgress = (ProgressBar) findViewById(R.id.mainProgress);
		menuLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
				drawerLayout.closeDrawers();
				String username = getSharedPreferences("voz_data", MODE_PRIVATE).getString("username", "");
				// Show logout Dialog
				if (!username.equals("") && position ==0) {
					onLogoutClick(position);
					return;
				}
				// Show login Dialog
				onMenuClick(position);
			}
		});
	}
	
	// Import First Screen
	public void importFragment (){
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Page1 fragmentData = new Page1();
		fragmentData.setNotifyMain(MainActivity.this);
		ft.replace(R.id.fragmentcontainer, fragmentData);
		ft.commit();
	}
	
	public void initDATA(){
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		Constant.screen_width = width;
		Constant.screen_height = height;
		isStop = false;
	}
	
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
	
	// SHow login dialog when user not login
	protected void initDialogLogin() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.fragment_login);
		dialog.show();
		Button loginBtn = (Button) dialog.findViewById(R.id.loginBtn);
		usernameEdt = (EditText) dialog.findViewById(R.id.usernameEdt);
		final EditText passwordEdt = (EditText) dialog.findViewById(R.id.passwordEdt);
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		loginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				new LoginTask().execute(usernameEdt.getText().toString()+"---"+passwordEdt.getText().toString());
			}
		});
	}
	
	public void initGoFastDialog(){
		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
		alert.setTitle("Bác muốn chạy tới f bao nhiêu ?");
		// Set an EditText view to get user input
		final EditText input = new EditText(MainActivity.this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);

		alert.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int whichButton) {
						String value = input.getText().toString();
						Bundle bundle = new Bundle();
						bundle.putString("url", "http://vozforums.com/forumdisplay.php?f="+value);
						bundle.putString("title", "f"+value);
						Page2 ft = new Page2();
						ft.setArguments(bundle);
						ft.setNotifyMain(MainActivity.this);
						replaceFragment(ft);
						onSetActionBarTitle("f"+value);
						dialog.cancel();
					}
				});
		alert.show();
	}
	
	// Show dialog log out
	@Override
	public void onLogoutClick(int position) {
		AlertDialog builder = new AlertDialog.Builder(MainActivity.this)
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
	
	public void initSettingDialog(){
		final Dialog dialog = new Dialog(this);
		dialog.setTitle("Setting");
		dialog.setContentView(R.layout.fragment_setting);
		dialog.show();
		Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);
		final TextView fontSizeTv = (TextView) dialog.findViewById(R.id.fontSizeTv);
		CheckBox signCheckBox = (CheckBox) dialog.findViewById(R.id.signCheckBox);
		fontSizeTv.setText(sharedPreferences.getString("fontsize", "0"));
		SeekBar seekBar1 = (SeekBar) dialog.findViewById(R.id.seekBar1);
		seekBar1.setProgress(Integer.parseInt(sharedPreferences.getString("fontsize", "0")));
		seekBar1.setMax(8);
		
		seekBar1.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
				fontSizeTv.setText("+"+progress);
			}
		});
		
		final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
//		RadioButton nomalRad = (RadioButton) dialog.findViewById(R.id.normalRad);
//		RadioButton blackRad = (RadioButton) dialog.findViewById(R.id.blackRad);
		String chooseTheme = sharedPreferences.getString("theme", "null");
		if(chooseTheme.equals("null")||chooseTheme.equals("normal")){
			radioGroup.check(R.id.normalRad);
		}else {
			radioGroup.check(R.id.blackRad);
		}
		
		String useSign = sharedPreferences.getString("useSign", "true");
		if (useSign.equals("false")){
			signCheckBox.setChecked(false);
		}else {
			signCheckBox.setChecked(true);
		}
		
		signCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean check) {
				if (check == true) {
					editor.putString("useSign", "true");
				}else {
					editor.putString("useSign", "false");				
				}
				editor.commit();
			}
		});
		
		okBtn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int radioButtonID = radioGroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radioGroup.findViewById(radioButtonID);
				String themeType = radioButton.getText().toString();
				editor.putString("fontsize", fontSizeTv.getText().toString().replace("+", ""));
				if(themeType.toLowerCase().contains("normal")){
					editor.putString("theme", "normal");
				}else if (themeType.toLowerCase().contains("black")){
					Toast.makeText(MainActivity.this, themeType, Toast.LENGTH_SHORT).show();
					editor.putString("theme", "black");
				}
				editor.commit();
				Intent intent =getIntent();
				finish();
				startActivity(intent);
				dialog.dismiss();
			}
		});
		
		cancelBtn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	@Override
	public void onMenuClick(int position) {
		if (position == 0) {
			initDialogLogin();
		} else if (position == 1) {
			importFragment();
		} else if (position == 2) {
			PageSubcribe pageSubcribe = new PageSubcribe();
			pageSubcribe.setNotifyMain(MainActivity.this);
			replaceFragment(pageSubcribe);
		} else if (position == 3) {
			initGoFastDialog();
		}   else if (position == 4) {
			newPostTopic();
		} else if (position == 5) {
			QuoteTopic();
		} else if(position ==6 ){
			InboxFragment pageSubcribe = new InboxFragment();
			pageSubcribe.setNotifyMain(MainActivity.this);
			replaceFragment(pageSubcribe);
		}else if (position == 7) {
			initSettingDialog();
		}else if (position == 8) {
			quitApp();
		} 
	}
	
	public void QuoteTopic(){
		QuoteFragment ft = new QuoteFragment();
		//ft.setArguments(bundle);
		ft.setNotifyMain(MainActivity.this);
		replaceFragment(ft);
		onSetActionBarTitle("Quote List");
	}
	
	public void newPostTopic(){
		Bundle bundle = new Bundle();
		bundle.putString("url", "http://vozforums.com/search.php?do=getnew");
		bundle.putString("title", "New posts");
		bundle.putString("fromNewPost", "yes");
		Page2 ft = new Page2();
		ft.setArguments(bundle);
		ft.setNotifyMain(MainActivity.this);
		replaceFragment(ft);
		onSetActionBarTitle("New posts");
	}
	
	public void quitApp(){
		UIHelper.killApp(true);
	}

	@Override
	public Map<String, String> onGetCookie() {
		try{
			return Constant.getCookies(sharedPreferences.getString("cookie_data", ""));
		}catch(Exception e){
			return new HashMap<String, String>();
		}
	}

	@Override
	public void replaceFragment(Fragment frag) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragmentcontainer, frag);
		ft.addToBackStack("");
		ft.commit();
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
	protected void onStart() {
		isStop = false;
		super.onStart();
	}
	protected void onStop() {
		isStop=true;
		super.onStop();
	}

	@Override
	public void onSetActionBarTitle(String title) {
		bar.setTitle(Html.fromHtml("<font color=\"#ffffff\">" + title + "</font>"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// if (id == android.R.id.home){
		// menu.toggle();
		// }
		if (toggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
	}

	// Login Task
    public class LoginTask extends AsyncTask<String,Void, Void>{

    	private String salt="";
		@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		mainProgress.setVisibility(View.VISIBLE);
    	}
		@Override
		public Void doInBackground(String... params) {
            try {
            	String username = "";
            	String password = "";
            	if (!params[0].equalsIgnoreCase("")) {
            		username = params[0].split("---")[0];
            		password = params[0].split("---")[1];
				}
            	// First time request
				Response response = Jsoup.connect("http://vozforums.com/vbdev/login_api.php").timeout(20000).data("do", "login").data("api_cookieuser", "1").data("securitytoken", "guest").data("api_vb_login_md5password", Constant.convertToMd5(password)).data("api_vb_login_md5password_utf",Constant.convertToMd5(password)).data("api_vb_login_password", password).data("api_vb_login_username", username).data("api_2factor", "").data("api_captcha", "").data("api_salt", "").method(org.jsoup.Connection.Method.POST).execute();
				Document document = response.parse();
				String s = (String)(new JSONObject(document.text())).get("captcha");
				// Second time request
			    response = Jsoup.connect("http://vozforums.com/vbdev/login_api.php").timeout(20000).data("do", "login").data("api_cookieuser", "1").data("securitytoken", "guest").data("api_vb_login_md5password", Constant.convertToMd5(password)).data("api_vb_login_md5password_utf", Constant.convertToMd5(password)).data("api_vb_login_password", password).data("api_vb_login_username", username).data("api_2factor", "").data("api_captcha", s).data("api_salt", "").method(org.jsoup.Connection.Method.POST).execute();
			    document = response.parse();
			    salt = (String)(new JSONObject(document.text())).get("salt");
			    
			    Map map = response.cookies();
			    map.remove("vflastvisit");
			    map.remove("vflastactivity");
			    String cookie =  map.toString().replace(",", ";").trim();
			    // Save user data
				editor.putString("user_data", document.text());
				editor.putString("username", username);
				editor.putString("password", password);
				editor.putString("cookie_data", cookie);
				editor.commit();
            } catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			if (!isStop) {
				//mf.initDATA();
				initMenuData();
				importFragment();
				mainProgress.setVisibility(View.GONE);
				if(firstLogin){
					firstLogin = false;
					return;
				}
				if (!salt.equals("")) {
					Toast.makeText(MainActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(MainActivity.this, "�?ăng nhập thất bại", Toast.LENGTH_SHORT).show();
				}
			}
		}
    }
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	Log.d("dohai.com", "onDestroy-1");
    }
    
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    } 
}
