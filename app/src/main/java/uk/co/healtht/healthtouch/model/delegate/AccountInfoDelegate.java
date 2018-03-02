package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.AccountInfo;

/**
 * Created by Najeeb.Idrees on 10-July-17.
 */
public class AccountInfoDelegate
{
	private Dao<AccountInfo, Integer> candidateDao;
	private QueryBuilder<AccountInfo, Integer> qb;

	public AccountInfoDelegate()
	{
		candidateDao = HTApplication.dbHelper.getAccountInfoDao();
		qb = candidateDao.queryBuilder();
	}

//	public AccountInfo get(Integer id)
//	{
//		try
//		{
//			qb.where().eq(AccountInfo.LOCAL_ID, id);
//			return qb.queryForFirst();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		return null;
//	}

	public AccountInfo getByEmail(String email)
	{
		try
		{
			qb.where().eq(AccountInfo.EMAIL, email);
			return qb.queryForFirst();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public List<AccountInfo> getAll()
	{
		try
		{
			return qb.query();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}


//	public int delete(AccountInfo accountInfo)
//	{
//		DeleteBuilder<AccountInfo, Integer> deleteBuilder = candidateDao.deleteBuilder();
//
//		try
//		{
//			deleteBuilder.where().eq(AccountInfo.LOCAL_ID, accountInfo.localId);
//			return deleteBuilder.delete();
//		}
//		catch (SQLException e)
//		{
//			e.printStackTrace();
//		}
//
//		return -1;
//	}

	public boolean deleteAll()
	{
		try
		{
			candidateDao.delete(candidateDao.queryForAll());
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public boolean add(AccountInfo arg)
	{
		try
		{
			Dao.CreateOrUpdateStatus status = candidateDao.createOrUpdate(arg);
			return (status.isCreated() || status.isUpdated());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public boolean addAll(final List<AccountInfo> arg)
	{
		boolean isInserted = true;
		try
		{
			candidateDao.callBatchTasks(new Callable<AccountInfo>()
			{

				@Override
				public AccountInfo call() throws Exception
				{
					for (AccountInfo candidate : arg)
					{
						candidateDao.createOrUpdate(candidate);
					}
					return null;
				}

			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			isInserted = false;
		}
		return isInserted;
	}

	public boolean update(AccountInfo arg)
	{
		try
		{
			Dao.CreateOrUpdateStatus status = candidateDao.createOrUpdate(arg);
			return (status.isCreated() || status.isUpdated());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
