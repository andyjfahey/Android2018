package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import uk.co.healtht.healthtouch.model.entities.HTTrackerReminderAbstract;

/**
 * Created by andyj on 16/01/2018.
 */

public abstract class AbstractReminderDelegate<T extends HTTrackerReminderAbstract> extends HTAbstractSyncEntityDelegate<T> {

    public abstract List<T> getAllByFKId(Integer FK_Id);
}