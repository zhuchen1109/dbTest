/**
 * CopyRight	2014 ZhuYan
 *	@author Zhu Yan
 *  
 *	All right reserved
 *	
 *	Created	on	2014-3-19  ����10:12:42
 */
package com.example.dbtest.ormlite;

import android.R.integer;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author Zhu Yan
 *
 * Created	on	2014-3-19  ����10:12:42
 */
public class OrmHelper extends OrmLiteSqliteOpenHelper 
{
	
	private Dao<User,Integer> userDao = null;
	/**
	 * @param context
	 * @param databaseName
	 * @param factory
	 * @param databaseVersion
	 */
    public OrmHelper(Context context)
    {
	    super(context, "ormlite.db", null, 1);
	    // TODO Auto-generated constructor stub
    }

	@Override
    public void onCreate(SQLiteDatabase db, ConnectionSource arg1)
    {
		try
        {
			TableUtils.createTable(arg1, User.class);
			System.out.println("orm:�������ݿ�ɹ�");
        }
        catch (Exception e)
        {
        	System.out.println("orm:�������ݿ�ʧ��");
	        // TODO: handle exception
        	e.printStackTrace();
        }
    }

	@Override
    public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
            int arg3)
    {
    }

	@Override
    public void close()
    {
	    super.close();
	    userDao = null;
    }

	/**
	 * @return the userDao
	 */
	public Dao<User, Integer> getUserDao()
	{
		try
        {
	        if(userDao ==null){
	        	userDao = getDao(User.class);
	        }
        }
        catch (Exception e)
        {
	        // TODO: handle exception
        	e.printStackTrace();
        }
		return userDao;
	}
	
	
	
}
