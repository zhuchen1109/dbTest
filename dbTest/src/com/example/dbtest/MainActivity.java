package com.example.dbtest;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.dbtest.contentProvider.myContentProvider;
import com.example.dbtest.greendao.dao.DaoMaster;
import com.example.dbtest.greendao.dao.DaoMaster.DevOpenHelper;
import com.example.dbtest.greendao.dao.users;
import com.example.dbtest.greendao.dao.usersDao;
import com.example.dbtest.ormlite.OrmHelper;
import com.example.dbtest.ormlite.User;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.R.integer;
import android.app.ProgressDialog;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends OrmLiteBaseActivity<OrmHelper> implements OnClickListener{

	private Button button;
	private TextView view;
	
	private final static int INSERT_COUNT = 1000;
	private usersDao usersDao = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		button = (Button) findViewById(R.id.btn);
		button.setOnClickListener(this);
		view = (TextView) findViewById(R.id.tv);
		findViewById(R.id.read_btn)
				.setOnClickListener(this);
		
		//greenDao创建数据库
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "greendao.db", null);
		DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
		usersDao = daoMaster.newSession().getUsersDao();
	}

	/**
	 * 
	 */
    private long insertUserFromProvider()
    {
    	ContentValues values = new ContentValues();
    	values.put("name", "zhuyan");
    	values.put("phone", "18782956011");
    	long time = System.currentTimeMillis();
//    	System.out.println("start:");
    	getContentResolver().insert(myContentProvider.CONTENT_URI, values);
//    	System.out.println("end:"+(System.currentTimeMillis() - time));
    	return (System.currentTimeMillis() - time);
    }
    
    private long insertUserFromORMLite() throws SQLException
    {
    	User user = new User();
    	user.setName("zhuyan");
    	user.setPhone("18782956011");
    	long time = System.currentTimeMillis();
    	getHelper().getUserDao().create(user);
    	return (System.currentTimeMillis() - time);
    }
    
    /**
	 * @return
	 */
    public long insertUserFromGreenDao()
    {
    	users users = new users();
    	users.setName("zhuyan");
    	users.setPhone("phone");
    	
    	long time = System.currentTimeMillis();
    	usersDao.insert(users);
	    return System.currentTimeMillis() - time;
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
    public void onClick(View v)
    {
		if(v.getId() == R.id.btn){
			new TestTask().execute();
		}else {
			new ReadTestTask().execute();
		}
    }
	
	private class TestTask extends AsyncTask<Void, String, String>{

		StringBuilder builder = new StringBuilder();
		ProgressDialog dialog = null;
		
		@Override
        protected void onPreExecute()
        {	dialog = new ProgressDialog(MainActivity.this);
        	dialog.setCancelable(false);
        	dialog.setMessage("insert test is running");
        	dialog.setTitle("insert test is running");
        	dialog.show();
	        super.onPreExecute();
        }


		@Override
        protected String doInBackground(Void... params)
        {
			long time = System.currentTimeMillis();
			getContentResolver().delete(myContentProvider.CONTENT_URI, null, null);
			publishProgress("provider deleteall:耗时"+(System.currentTimeMillis() - time));
			
			long toalCount = 0;
			for(int i=0;i<INSERT_COUNT;i++){
				toalCount = toalCount + insertUserFromProvider();
			}
			publishProgress("provider插入"+INSERT_COUNT+"条数据：耗时"+toalCount+"ms   平均耗时:"+(toalCount*1.0/INSERT_COUNT));
			
//			toalCount = providerInsertBacth();
//			publishProgress("provider batch插入"+INSERT_COUNT+"条数据：耗时"+toalCount+"ms   平均耗时:"+(toalCount*1.0/INSERT_COUNT));
			
			try
            {
	            List<User> users = getHelper().getUserDao().queryForAll();
	            
	            //一次删除完，挂了。数据太多了。
//	            if(users.size() > 0){
//	            	time = System.currentTimeMillis();
//	            	getHelper().getUserDao().delete(users);
//	            	publishProgress("ORMLite deleteall:耗时"+(System.currentTimeMillis() - time));
//	            }
	            
	            if(users.size() > 0){
	            	time = System.currentTimeMillis();
	            	for(User user:users){
	            		getHelper().getUserDao().delete(user);
	            	}
	            	publishProgress("ORMLite deleteall:耗时"+(System.currentTimeMillis() - time));
	            }
            }
            catch (SQLException e1)
            {
	            // TODO Auto-generated catch block
	            e1.printStackTrace();
            }
			
			toalCount = 0;
			for(int i=0;i<INSERT_COUNT;i++){
				try
                {
					toalCount = toalCount + insertUserFromORMLite();
                }
                catch (Exception e)
                {
                	e.printStackTrace();
                	System.out.println("插入数据失败");
	                // TODO: handle exception
                }
			}
			publishProgress("ORMLite插入"+INSERT_COUNT+"条数据：耗时"+toalCount+"ms   平均耗时:"+(toalCount*1.0/INSERT_COUNT));
			
			try
            {
				time = System.currentTimeMillis();
	            usersDao.deleteAll();
	            publishProgress("GreenDao deleteall:耗时"+(System.currentTimeMillis() - time));
            }
            catch (Exception e)
            {
	            // TODO: handle exception
            }
			
			
			toalCount = 0;
			for(int i=0;i<INSERT_COUNT;i++){
				try
                {
					toalCount = toalCount + insertUserFromGreenDao();
                }
                catch (Exception e)
                {
                	e.printStackTrace();
                	System.out.println("插入数据失败");
	                // TODO: handle exception
                }
			}
			publishProgress("GreenDao插入"+INSERT_COUNT+"条数据：耗时"+toalCount+"ms   平均耗时:"+(toalCount*1.0/INSERT_COUNT));
			
			return null;
        }
		
		
		
		@Override
        protected void onProgressUpdate(String... values)
        {
			builder.append(values[0])
					.append("\n");
			view.setText(builder.toString());
			dialog.setMessage(values[0]);
	        super.onProgressUpdate(values);
        }

		@Override
        protected void onPostExecute(String result)
        {
			if(dialog != null && dialog.isShowing()){
				dialog.dismiss();
			}
			view.setText(builder.toString());
//			button.setText(builder.toString());
	        super.onPostExecute(result);
        }
		
		
	}
	
	private class ReadTestTask extends AsyncTask<Void, String, String>{

		StringBuilder builder = new StringBuilder();
		ProgressDialog dialog = null;
		
		@Override
        protected void onPreExecute()
        {	dialog = new ProgressDialog(MainActivity.this);
        	dialog.setCancelable(false);
        	dialog.setMessage("read test is running");
        	dialog.setTitle("read test is running");
        	dialog.show();
	        super.onPreExecute();
        }


		@Override
        protected String doInBackground(Void... params)
        {
			long time = System.currentTimeMillis();
			Cursor cursor = getContentResolver().query(myContentProvider.CONTENT_URI, null, null, null, null);
			publishProgress("provider queryAll"+cursor.getCount()+"条数据：耗时"+(System.currentTimeMillis() - time));
			//
			cursor.moveToFirst();
			List<com.example.dbtest.contentProvider.User> providerList = new ArrayList<com.example.dbtest.contentProvider.User>(cursor.getCount());
			int[] pos = new int[]{cursor.getColumnIndexOrThrow("id"),cursor.getColumnIndexOrThrow("name"),cursor.getColumnIndexOrThrow("phone")};
			do{
				com.example.dbtest.contentProvider.User user = new com.example.dbtest.contentProvider.User();
				user.setId(cursor.getInt(pos[0]));
				user.setName(cursor.getString(pos[1]));
				user.setPhone(cursor.getString(pos[2]));
				
				providerList.add(user);
			}while(cursor.moveToNext());
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			
			time = System.currentTimeMillis();
			List<User> users = Collections.EMPTY_LIST;
            try
            {
	            users = getHelper().getUserDao().queryForAll();
            }
            catch (SQLException e)
            {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
			publishProgress("ORMLite queryAll"+users.size()+"条数据：耗时"+(System.currentTimeMillis() - time));
			
			
			time = System.currentTimeMillis();
			List<users> greenDaoUsers = usersDao.loadAll();
			publishProgress("GreenDao queryAll"+users.size()+"条数据：耗时"+(System.currentTimeMillis() - time));
			
			return null;
        }
		
		
		
		@Override
        protected void onProgressUpdate(String... values)
        {
			builder.append(values[0])
					.append("\n");
			view.setText(builder.toString());
			dialog.setMessage(values[0]);
	        super.onProgressUpdate(values);
        }

		@Override
        protected void onPostExecute(String result)
        {
			if(dialog != null && dialog.isShowing()){
				dialog.dismiss();
			}
			view.setText(builder.toString());
//			button.setText(builder.toString());
	        super.onPostExecute(result);
        }
		
		
	}


	/**
	 * 
	 */
    public long providerInsertBacth()
    {
    	ContentProviderClient client = getContentResolver()
    				.acquireContentProviderClient(myContentProvider.AUTHORITY);
    	ArrayList<ContentProviderOperation> mOperations = new ArrayList<ContentProviderOperation>();
        ContentValues values = new ContentValues();
        values.put("name", "zhuyan");
        values.put("phone", "18782956011");
        long time = System.currentTimeMillis();
        for(int i =0;i<INSERT_COUNT;i++){
        	mOperations.add(ContentProviderOperation
        			.newInsert(myContentProvider.CONTENT_URI).withValues(values).build());
        	if(mOperations.size() >= 100){
        		try
                {
        			client.applyBatch(mOperations);
        			mOperations.clear();
                }
                catch (Exception e)
                {
	                // TODO: handle exception
                	e.printStackTrace();
                }
        	}
        }
        return System.currentTimeMillis() - time;
    }

}
