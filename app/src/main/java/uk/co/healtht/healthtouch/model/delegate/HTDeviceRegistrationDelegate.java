//package uk.co.healtht.healthtouch.model.delegate;
//
//import com.j256.ormlite.dao.Dao;
//import com.j256.ormlite.stmt.DeleteBuilder;
//import com.j256.ormlite.stmt.QueryBuilder;
//
//import java.sql.SQLException;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.Callable;
//
//import uk.co.healtht.healthtouch.HTApplication;
//import uk.co.healtht.healthtouch.model.AbstractCrudOperation;
//import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;
//import uk.co.healtht.healthtouch.model.entities.HTDeviceRegistration;
//
///**
// * Created by Najeeb.Idrees on 05-July-17.
// */
//
//public class HTDeviceRegistrationDelegate extends AbstractCrudOperation<HTDeviceRegistration>
//{
//	private Dao<HTDeviceRegistration, Integer> candidateDao;
//	private QueryBuilder<HTDeviceRegistration, Integer> qb;
//
//	public HTDeviceRegistrationDelegate()
//	{
//		candidateDao = HTApplication.dbHelper.getHTDeviceRegistrationDao();
//		qb = candidateDao.queryBuilder();
//	}
//
//
//	@Override
//	public HTDeviceRegistration get(Integer id)
//	{
//		try
//		{
//			qb.where().eq(HTDeviceRegistration.LOCAL_ID, id);
//			return qb.queryForFirst();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		return null;
//	}
//
//	@Override
//	public HTDeviceRegistration getByServerId(Integer server_id)
//	{
//		try
//		{
//			qb.where().eq(HTDeviceRegistration.SERVER_ID, server_id);
//			return qb.queryForFirst();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		return null;
//	}
//
//	@Override
//	public HTDeviceRegistration getByCreatedAt(Date createdAt)
//	{
//		try
//		{
//			qb.where().eq(HTDeviceRegistration.CREATED_AT, createdAt);
//			return qb.queryForFirst();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		return null;
//	}
//
//	@Override
//	public List<HTDeviceRegistration> getAll()
//	{
//		try
//		{
//			return qb.query();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		return null;
//	}
//
//	@Override
//	public List<HTDeviceRegistration> getAllWhereDeleteAtIsNull()
//	{
//		try
//		{
//			qb.where().isNull(HTDeviceRegistration.DELETED_AT);
//			qb.orderBy(HTDeviceRegistration.UPDATED_AT, false);
//			return qb.query();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		return null;
//	}
//
//	//	@Override
//	public List<HTDeviceRegistration> getAllBySynced(boolean synced, long limit)
//	{
//		try
//		{
//			qb.where().eq(HTDeviceRegistration.SYNCED, synced);
//			qb.orderBy(HTAbstractTracker.CREATED_AT, false);
//			qb.limit(limit);
//			return qb.query();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		return null;
//	}
//
//	@Override
//	public int delete(HTDeviceRegistration HTDeviceRegistration)
//	{
//		DeleteBuilder<HTDeviceRegistration, Integer> deleteBuilder = candidateDao.deleteBuilder();
//
//		try
//		{
//			deleteBuilder.where().eq(HTDeviceRegistration.LOCAL_ID, HTDeviceRegistration.localId);
//			return deleteBuilder.delete();
//		}
//		catch (SQLException e)
//		{
//			e.printStackTrace();
//		}
//
//		return -1;
//	}
//
//	@Override
//	public boolean deleteAll()
//	{
//		try
//		{
//			candidateDao.delete(candidateDao.queryForAll());
//			return true;
//		}
//		catch (SQLException e)
//		{
//			e.printStackTrace();
//		}
//
//		return false;
//	}
//
//	@Override
//	public boolean add(Object arg)
//	{
//		try
//		{
//			Dao.CreateOrUpdateStatus status = candidateDao.createOrUpdate((HTDeviceRegistration) arg);
//			return (status.isCreated() || status.isUpdated());
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		return false;
//	}
//
//	@Override
//	public boolean addAll(final List<HTDeviceRegistration> arg)
//	{
//		boolean isInserted = true;
//		try
//		{
//			candidateDao.callBatchTasks(new Callable<HTDeviceRegistration>()
//			{
//
//				@Override
//				public HTDeviceRegistration call() throws Exception
//				{
//					for (HTDeviceRegistration candidate : arg)
//					{
//						candidateDao.createOrUpdate(candidate);
//					}
//					return null;
//				}
//
//			});
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			isInserted = false;
//		}
//		return isInserted;
//	}
//
//	@Override
//	public boolean update(HTDeviceRegistration arg)
//	{
//		try
//		{
//			Dao.CreateOrUpdateStatus status = candidateDao.createOrUpdate(arg);
//			return (status.isCreated() || status.isUpdated());
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//
//	public boolean add(HTDeviceRegistration arg)
//	{
//		try
//		{
//			Dao.CreateOrUpdateStatus status = candidateDao.createOrUpdate(arg);
//			return (status.isCreated() || status.isUpdated());
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		return false;
//	}
//}
