package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import uk.co.healtht.healthtouch.model.AbstractCrudOperation;
import uk.co.healtht.healthtouch.model.entities.HTAbstractSyncEntity;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public abstract class HTAbstractSyncEntityDelegate<T extends uk.co.healtht.healthtouch.model.entities.HTAbstractSyncEntity> extends AbstractCrudOperation<T>
{
	private Dao<T, Integer> candidateDao;
	protected QueryBuilder<T, Integer> qb;

	public HTAbstractSyncEntityDelegate()
	{
		candidateDao = getCandidateDao();
		qb = candidateDao.queryBuilder();
	}

	public abstract Dao<T, Integer>  getCandidateDao();

	@Override
	public T get(Integer id)
	{
		try
		{
			qb.where().eq(T.LOCAL_ID, id);
			return qb.queryForFirst();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public T getByServerId(Integer server_id)
	{
		try
		{
			qb.where().eq(T.SERVER_ID, server_id);
			return qb.queryForFirst();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public T getByCreatedAt(Date createdAt)
	{
		try
		{
			qb.where().eq(T.CREATED_AT, createdAt);
			return qb.queryForFirst();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	
	@Override
	public List<T> getAll()
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

	@Override
	public List<T> getAllWhereDeleteAtIsNull()
	{
		try
		{
			qb.where().isNull(T.DELETED_AT);
			qb.orderBy(T.UPDATED_AT, false);
			return qb.query();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<T> getAllBySynced(boolean synced, long limit)
	{
		try
		{
			qb.where().eq(T.SYNCED, synced);
			qb.orderBy(HTAbstractTracker.CREATED_AT, false);
			qb.limit(limit);

			return qb.query();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}


	@Override
	public int delete(T entity)
	{
		DeleteBuilder<T, Integer> deleteBuilder = candidateDao.deleteBuilder();

		try
		{
			deleteBuilder.where().eq(T.LOCAL_ID, entity.localId);
			return deleteBuilder.delete();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return -1;
	}

	@Override
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



	@Override
	public boolean addAll(final List<T> arg)
	{
		boolean isInserted = true;
		try
		{
			candidateDao.callBatchTasks(new Callable<T>()
			{

				@Override
				public T call() throws Exception
				{
					for (T candidate : arg)
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

	public boolean add(T arg)
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

	@Override
	public boolean add(Object arg)
	{
		return add((T)arg);
	}

	@Override
	public boolean update(T arg)
	{
		return add(arg);
	}
}
