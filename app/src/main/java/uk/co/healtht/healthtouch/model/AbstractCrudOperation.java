package uk.co.healtht.healthtouch.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Najeeb.Idrees on 19-Apr-17.
 */

public abstract class AbstractCrudOperation<T>
{
	public abstract T get(Integer id);

	public abstract T getByServerId(Integer id);

	public abstract T getByCreatedAt(Date createdAt);

	public abstract List<T> getAll();

	public abstract List<T> getAllWhereDeleteAtIsNull();

	//	public abstract List<T> getAllBySynced(boolean synced);

	public abstract List<T> getAllBySynced(boolean synced, long limit);

	public abstract int delete(T arg);

	public abstract boolean deleteAll();

	//	public abstract boolean add(T arg);

	public abstract boolean add(Object arg);

	public abstract boolean addAll(List<T> arg);

	public abstract boolean update(T arg);


	//	public abstract int getCount();

}
