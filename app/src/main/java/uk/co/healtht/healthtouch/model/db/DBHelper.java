package uk.co.healtht.healthtouch.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.AccountInfo;
import uk.co.healtht.healthtouch.model.entities.HTBranding;
import uk.co.healtht.healthtouch.model.entities.HTForm;
import uk.co.healtht.healthtouch.model.entities.HTFormQuestion;
import uk.co.healtht.healthtouch.model.entities.HTFormType;
import uk.co.healtht.healthtouch.model.entities.HTMedicationImage;
import uk.co.healtht.healthtouch.model.entities.HTMedicationReminder;
import uk.co.healtht.healthtouch.model.entities.HTMedicationTakenTracker;
import uk.co.healtht.healthtouch.model.entities.HTPPGoodToKnow;
import uk.co.healtht.healthtouch.model.entities.HTPPKeyContact;
import uk.co.healtht.healthtouch.model.entities.HTPPMedicalHistory;
import uk.co.healtht.healthtouch.model.entities.HTPPMedicalHistoryDetail;
import uk.co.healtht.healthtouch.model.entities.HTPPMedicalHistoryDetailType;
import uk.co.healtht.healthtouch.model.entities.HTServiceMember;
import uk.co.healtht.healthtouch.model.entities.HTServiceNew;
import uk.co.healtht.healthtouch.model.entities.HTShareNew;
import uk.co.healtht.healthtouch.model.entities.HTTrackerBlood;
import uk.co.healtht.healthtouch.model.entities.HTTrackerBloodSugar;
import uk.co.healtht.healthtouch.model.entities.HTTrackerBreathing;
import uk.co.healtht.healthtouch.model.entities.HTTrackerFluidIntake;
import uk.co.healtht.healthtouch.model.entities.HTTrackerFluidOutput;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMedication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMessage;
import uk.co.healtht.healthtouch.model.entities.HTTrackerOxygen;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPeakFlow;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPulse;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminder;
import uk.co.healtht.healthtouch.model.entities.HTTrackerStool;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;
import uk.co.healtht.healthtouch.model.entities.HTTrackerWeight;
import uk.co.healtht.healthtouch.model.entities.HTUserNew;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;


import uk.co.healtht.healthtouch.model.entities.HTTrackerSweating;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPain;
import uk.co.healtht.healthtouch.model.entities.HTTrackerActivity;
import uk.co.healtht.healthtouch.model.entities.HTTrackerGastrointestinal;
/**
 * Created by Najeeb.Idrees on 05-July-17
 */


/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper
{
	private static DBHelper dbHelper;
	//	private static final AtomicInteger usageCounter = new AtomicInteger(0);

	private Dao<HTTrackerSync, Integer> hTTrackerSyncDao = null;
	private Dao<HTTrackerBlood, Integer> hTTrackerBloodDao = null;
	private Dao<HTTrackerBloodSugar, Integer> hTTrackerBloodSugarDao = null;
	private Dao<HTTrackerBreathing, Integer> hTTrackerBreathingDao = null;
	private Dao<HTTrackerFluidIntake, Integer> htTrackerFluidIntakeDao = null;
	private Dao<HTTrackerFluidOutput, Integer> hTTrackerFluidOutputDao = null;
	private Dao<HTTrackerOxygen, Integer> hTTrackerOxygenDao = null;
	private Dao<HTTrackerWeight, Integer> htTrackerWeightDao = null;
	private Dao<HTTrackerStool, Integer> htTrackerStoolDao = null;
	private Dao<HTTrackerPulse, Integer> htTrackerPulseDao = null;
	private Dao<HTTrackerPeakFlow, Integer> htTrackerPeakFlowDao = null;
	private Dao<HTTrackerThreshold, Integer> hTTrackerThresholdDao = null;
	private Dao<HTTrackerReminder, Integer> hTTrackerReminderDao = null;
	private Dao<HTTrackerMessage, Integer> htTrackerMessageDao = null;
	private Dao<HTTrackerMedication, Integer> htTrackerMedicationDao = null;
	private Dao<HTShareNew, Integer> htShareNewDao = null;
	private Dao<HTServiceNew, Integer> htServiceNewDao = null;
	private Dao<HTServiceMember, Integer> htServiceMemberDao = null;
	private Dao<HTFormType, Integer> htFormTypeDao = null;
	private Dao<HTFormQuestion, Integer> htFormQuestionDao = null;
	private Dao<HTForm, Integer> htFormDao = null;
	private Dao<HTBranding, Integer> htBrandingDao = null;
	private Dao<HTUserNew, Integer> htUserNewDao = null;
	private Dao<AccountInfo, Integer> accountInfoDao = null;
	private Dao<HTPPGoodToKnow, Integer> htppGoodToKnowDao = null;
	private Dao<HTPPKeyContact, Integer> htppKeyContactDao = null;
	private Dao<HTPPMedicalHistory, Integer> htppMedicalHistoryDao = null;
	private Dao<HTPPMedicalHistoryDetail, Integer> htppMedicalHistoryDetailDao = null;
	private Dao<HTPPMedicalHistoryDetailType, Integer> htppMedicalHistoryDetailTypeDao = null;
	//	private Dao<HTDeviceRegistration, Integer> htDeviceRegistrationDao = null;

	private Dao<HTTrackerSweating, Integer> htTrackerSweatingDao = null;
	private Dao<HTTrackerPain, Integer> htTrackerPainDao = null;
	private Dao<HTTrackerGastrointestinal, Integer> htTrackerGastrointestinalDao = null;
	private Dao<HTTrackerActivity, Integer> htTrackerActivityDao = null;

	private Dao<HTMedicationReminder, Integer> htMedicationReminderDao = null;
	private Dao<HTMedicationTakenTracker, Integer> htMedicationTakenTrackerDao = null;
	private Dao<HTMedicationImage, Integer> htMedicationImageDao = null;

	public DBHelper(Context context)
	{
		//		super(context, DBConstant.FILE_NAME_DB, null, DBConstant.DATABASE_VERSION, R.raw.ormlite_config);
		super(context, HTApplication.preferencesManager.getStringValueDefaultDBName(PreferencesManager.USER_EMAIL_ID)
				, null, DBConstant.DATABASE_VERSION, R.raw.ormlite_config);
	}

	public static synchronized DBHelper getHelper(Context context)
	{
		if (dbHelper == null)
		{
			dbHelper = new DBHelper(context);
		}

		//		usageCounter.incrementAndGet();
		return dbHelper;
	}

	/**
	 * This is called when the database is first created. Usually  you should
	 * call createTable statements here to create the tables that will store
	 * your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
	{
		try
		{
			Log.i(DBHelper.class.getName(), "onCreate");

			TableUtils.createTableIfNotExists(connectionSource, HTTrackerSync.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerBlood.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerBloodSugar.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerBreathing.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerFluidIntake.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerFluidOutput.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerOxygen.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerWeight.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerPulse.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerPeakFlow.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerStool.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerThreshold.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerReminder.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerMessage.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerMedication.class);
			TableUtils.createTableIfNotExists(connectionSource, HTShareNew.class);
			TableUtils.createTableIfNotExists(connectionSource, HTServiceNew.class);
			TableUtils.createTableIfNotExists(connectionSource, HTServiceMember.class);
			TableUtils.createTableIfNotExists(connectionSource, HTFormType.class);
			TableUtils.createTableIfNotExists(connectionSource, HTFormQuestion.class);
			TableUtils.createTableIfNotExists(connectionSource, HTForm.class);
			TableUtils.createTableIfNotExists(connectionSource, HTBranding.class);
			TableUtils.createTableIfNotExists(connectionSource, HTUserNew.class);
			TableUtils.createTableIfNotExists(connectionSource, HTPPGoodToKnow.class);
			TableUtils.createTableIfNotExists(connectionSource, HTPPKeyContact.class);
			TableUtils.createTableIfNotExists(connectionSource, HTPPMedicalHistory.class);
			TableUtils.createTableIfNotExists(connectionSource, HTPPMedicalHistoryDetail.class);
			TableUtils.createTableIfNotExists(connectionSource, HTPPMedicalHistoryDetailType.class);
			TableUtils.createTableIfNotExists(connectionSource, AccountInfo.class);
			//			TableUtils.createTableIfNotExists(connectionSource, HTDeviceRegistration.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerSweating.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerPain.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerActivity.class);
			TableUtils.createTableIfNotExists(connectionSource, HTTrackerGastrointestinal.class);

			TableUtils.createTableIfNotExists(connectionSource, HTMedicationReminder.class);
			TableUtils.createTableIfNotExists(connectionSource, HTMedicationTakenTracker.class);
			TableUtils.createTableIfNotExists(connectionSource, HTMedicationImage.class);

		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion)
	{
		try {

			LogUtils.i(DBHelper.class.getName(), "onUpgrade");

			onCreate(db, connectionSource);
			addColumnIfNotExists(getAccountInfoDao(), DBConstant.TABLE_ACCOUNT_INFO, AccountInfo.APP_VERSION_CODE, "INTEGER");

			addColumnIfNotExists(getHTUserNewDao(), DBConstant.TABLE_USER_NEW, HTUserNew.SECONDS_BETWEEN_SYNCS, "INTEGER");

			addColumnIfNotExists(getHTTrackerReminderDao(), DBConstant.TABLE_TRACKER_REMINDER, HTTrackerReminder.MESSAGE, "TEXT");
			addColumnIfNotExists(getHTTrackerReminderDao(), DBConstant.TABLE_TRACKER_REMINDER, HTTrackerReminder.START_DATE, "DATETIME");

			addColumnIfNotExists(getHTTrackerMedicationDao(), DBConstant.TABLE_TRACKER_MEDICATION, HTTrackerMedication.EDITABLE, "INTEGER");

		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

/*		try
		{
			switch (oldVersion)
			{
				case 1:
				{
					Dao<AccountInfo, Integer> accountInfoDao = getAccountInfoDao();
					accountInfoDao.executeRaw("ALTER TABLE " + DBConstant.TABLE_ACCOUNT_INFO + " ADD COLUMN "
							+ AccountInfo.APP_VERSION_CODE + "  INTEGER;");

//					accountInfoDao.executeRaw("ALTER TABLE " + DBConstant.TABLE_ACCOUNT_INFO + " DROP COLUMN "
//							+ AccountInfo.LOCAL_ID + "  INTEGER;");


				}
			}
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}*/
	}

	private void addColumnIfNotExists(Dao dao, String tablename, String colname, String coltype)
	{
		try {
			String sql = "ALTER TABLE " + tablename + " ADD COLUMN " + colname + " " + coltype + ";";
			dao.executeRaw(sql);
		}
		catch (SQLException e)
		{
			// ignore;
		}
	}
	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It
	 * will create it or just give the cached value.
	 *
	 * @throws SQLException
	 */

	public Dao<HTTrackerSync, Integer> getHTTrackerSyncDao()
	{
		if (hTTrackerSyncDao == null)
		{
			try
			{
				hTTrackerSyncDao = getDao(HTTrackerSync.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return hTTrackerSyncDao;
	}

	public Dao<HTTrackerBlood, Integer> getHTTrackerBloodDao()
	{
		if (hTTrackerBloodDao == null)
		{
			try
			{
				hTTrackerBloodDao = getDao(HTTrackerBlood.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return hTTrackerBloodDao;
	}

	public Dao<HTTrackerBloodSugar, Integer> getHTTrackerBloodSugarDao()
	{
		if (hTTrackerBloodSugarDao == null)
		{
			try
			{
				hTTrackerBloodSugarDao = getDao(HTTrackerBloodSugar.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return hTTrackerBloodSugarDao;
	}

	public Dao<HTTrackerBreathing, Integer> getHTTrackerBreathingDao()
	{
		if (hTTrackerBreathingDao == null)
		{
			try
			{
				hTTrackerBreathingDao = getDao(HTTrackerBreathing.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return hTTrackerBreathingDao;
	}

	public Dao<HTTrackerFluidIntake, Integer> getHTTrackerFluidIntakeDao()
	{
		if (htTrackerFluidIntakeDao == null)
		{
			try
			{
				htTrackerFluidIntakeDao = getDao(HTTrackerFluidIntake.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htTrackerFluidIntakeDao;
	}

	public Dao<HTTrackerFluidOutput, Integer> getHTTrackerFluidOutputDao()
	{
		if (hTTrackerFluidOutputDao == null)
		{
			try
			{
				hTTrackerFluidOutputDao = getDao(HTTrackerFluidOutput.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return hTTrackerFluidOutputDao;
	}

	public Dao<HTTrackerOxygen, Integer> getHTTrackerOxygenDao()
	{
		if (hTTrackerOxygenDao == null)
		{
			try
			{
				hTTrackerOxygenDao = getDao(HTTrackerOxygen.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return hTTrackerOxygenDao;
	}

	public Dao<HTTrackerWeight, Integer> getHTTrackerWeightDao()
	{
		if (htTrackerWeightDao == null)
		{
			try
			{
				htTrackerWeightDao = getDao(HTTrackerWeight.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htTrackerWeightDao;
	}

	public Dao<HTTrackerStool, Integer> getHTTrackerStoolDao()
	{
		if (htTrackerStoolDao == null)
		{
			try
			{
				htTrackerStoolDao = getDao(HTTrackerStool.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htTrackerStoolDao;
	}

	public Dao<HTTrackerPulse, Integer> getHTTrackerPulseDao()
	{
		if (htTrackerPulseDao == null)
		{
			try
			{
				htTrackerPulseDao = getDao(HTTrackerPulse.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htTrackerPulseDao;
	}

	public Dao<HTTrackerPeakFlow, Integer> getHTTrackerPeakFlowDao()
	{
		if (htTrackerPeakFlowDao == null)
		{
			try
			{
				htTrackerPeakFlowDao = getDao(HTTrackerPeakFlow.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htTrackerPeakFlowDao;
	}

	public Dao<HTTrackerThreshold, Integer> getHTTrackerThresholdDao()
	{
		if (hTTrackerThresholdDao == null)
		{
			try
			{
				hTTrackerThresholdDao = getDao(HTTrackerThreshold.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return hTTrackerThresholdDao;
	}

	public Dao<HTTrackerReminder, Integer> getHTTrackerReminderDao()
	{
		if (hTTrackerReminderDao == null)
		{
			try
			{
				hTTrackerReminderDao = getDao(HTTrackerReminder.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return hTTrackerReminderDao;
	}

	public Dao<HTTrackerMessage, Integer> getHTTrackerMessageDao()
	{
		if (htTrackerMessageDao == null)
		{
			try
			{
				htTrackerMessageDao = getDao(HTTrackerMessage.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htTrackerMessageDao;
	}

	public Dao<HTTrackerMedication, Integer> getHTTrackerMedicationDao()
	{
		if (htTrackerMedicationDao == null)
		{
			try
			{
				htTrackerMedicationDao = getDao(HTTrackerMedication.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htTrackerMedicationDao;
	}

	public Dao<HTMedicationTakenTracker, Integer> getHTMedicationTakenTrackerDao()
	{
		if (htMedicationTakenTrackerDao == null)
		{
			try
			{
				htMedicationTakenTrackerDao = getDao(HTMedicationTakenTracker.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htMedicationTakenTrackerDao;
	}

	public Dao<HTMedicationImage, Integer> getHTMedicationImageDao()
	{
		if (htMedicationImageDao == null)
		{
			try
			{
				htMedicationImageDao = getDao(HTMedicationImage.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htMedicationImageDao;
	}

	public Dao<HTMedicationReminder, Integer> getHTMedicationReminderDao()
	{
		if (htMedicationReminderDao == null)
		{
			try
			{
				htMedicationReminderDao = getDao(HTMedicationReminder.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htMedicationReminderDao;
	}

	public Dao<HTServiceNew, Integer> getHTServiceNewDao()
	{
		if (htServiceNewDao == null)
		{
			try
			{
				htServiceNewDao = getDao(HTServiceNew.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htServiceNewDao;
	}

	public Dao<HTServiceMember, Integer> getHTServiceMemberDao()
	{
		if (htServiceMemberDao == null)
		{
			try
			{
				htServiceMemberDao = getDao(HTServiceMember.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htServiceMemberDao;
	}

	public Dao<HTShareNew, Integer> getHTShareNewDao()
	{
		if (htShareNewDao == null)
		{
			try
			{
				htShareNewDao = getDao(HTShareNew.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htShareNewDao;
	}

	public Dao<HTFormType, Integer> getHTFormTypeDao()
	{
		if (htFormTypeDao == null)
		{
			try
			{
				htFormTypeDao = getDao(HTFormType.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htFormTypeDao;
	}

	public Dao<HTFormQuestion, Integer> getHTFormQuestionDao()
	{
		if (htFormQuestionDao == null)
		{
			try
			{
				htFormQuestionDao = getDao(HTFormQuestion.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htFormQuestionDao;
	}

	public Dao<HTForm, Integer> getHTFormDao()
	{
		if (htFormDao == null)
		{
			try
			{
				htFormDao = getDao(HTForm.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htFormDao;
	}

	public Dao<HTBranding, Integer> getHTBrandingDao()
	{
		if (htBrandingDao == null)
		{
			try
			{
				htBrandingDao = getDao(HTBranding.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htBrandingDao;
	}

	public Dao<HTUserNew, Integer> getHTUserNewDao()
	{
		if (htUserNewDao == null)
		{
			try
			{
				htUserNewDao = getDao(HTUserNew.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htUserNewDao;
	}

	public Dao<AccountInfo, Integer> getAccountInfoDao()
	{
		if (accountInfoDao == null)
		{
			try
			{
				accountInfoDao = getDao(AccountInfo.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return accountInfoDao;
	}

	public Dao<HTPPGoodToKnow, Integer> getHTPPGoodToKnowDao()
	{
		if (htppGoodToKnowDao == null)
		{
			try
			{
				htppGoodToKnowDao = getDao(HTPPGoodToKnow.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htppGoodToKnowDao;
	}

	public Dao<HTPPKeyContact, Integer> getHTPPKeyContactDao()
	{
		if (htppKeyContactDao == null)
		{
			try
			{
				htppKeyContactDao = getDao(HTPPKeyContact.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htppKeyContactDao;
	}

	public Dao<HTPPMedicalHistory, Integer> getHTPPMedicalHistoryDao()
	{
		if (htppMedicalHistoryDao == null)
		{
			try
			{
				htppMedicalHistoryDao = getDao(HTPPMedicalHistory.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htppMedicalHistoryDao;
	}

	public Dao<HTPPMedicalHistoryDetail, Integer> getHTPPMedicalHistoryDetailDao()
	{
		if (htppMedicalHistoryDetailDao == null)
		{
			try
			{
				htppMedicalHistoryDetailDao = getDao(HTPPMedicalHistoryDetail.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htppMedicalHistoryDetailDao;
	}

	public Dao<HTPPMedicalHistoryDetailType, Integer> getHTPPMedicalHistoryDetailTypeDao()
	{
		if (htppMedicalHistoryDetailTypeDao == null)
		{
			try
			{
				htppMedicalHistoryDetailTypeDao = getDao(HTPPMedicalHistoryDetailType.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htppMedicalHistoryDetailTypeDao;
	}

	public Dao<HTTrackerSweating, Integer> getHTTrackerSweatingDao()
	{
		if (htTrackerSweatingDao == null)
		{
			try
			{
				htTrackerSweatingDao = getDao(HTTrackerSweating.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htTrackerSweatingDao;
	}

	public Dao<HTTrackerGastrointestinal, Integer> getHTTrackerGastrointestinalDao()
	{
		if (htTrackerGastrointestinalDao == null)
		{
			try
			{
				htTrackerGastrointestinalDao = getDao(HTTrackerGastrointestinal.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htTrackerGastrointestinalDao;
	}

	public Dao<HTTrackerActivity, Integer> getHTTrackerActivityDao()
	{
		if (htTrackerActivityDao == null)
		{
			try
			{
				htTrackerActivityDao = getDao(HTTrackerActivity.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htTrackerActivityDao;
	}

	public Dao<HTTrackerPain, Integer> getHTTrackerPainDao()
	{
		if (htTrackerPainDao == null)
		{
			try
			{
				htTrackerPainDao = getDao(HTTrackerPain.class);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return htTrackerPainDao;
	}

	//	public Dao<HTDeviceRegistration, Integer> getHTDeviceRegistrationDao()
	//	{
	//		if (htDeviceRegistrationDao == null)
	//		{
	//			try
	//			{
	//				htDeviceRegistrationDao = getDao(HTDeviceRegistration.class);
	//			}
	//			catch (SQLException e)
	//			{
	//				e.printStackTrace();
	//			}
	//		}
	//		return htDeviceRegistrationDao;
	//	}


	/**
	 * Close the database connections and clear any cached DAOs. For each call to {@link #getHelper(Context)}, there
	 * should be 1 and only 1 call to this method. If there were 3 calls to {@link #getHelper(Context)} then on the 3rd
	 * call to this method, the helper and the underlying database connections will be closed.
	 */
	@Override
	public void close()
	{
		//		if (usageCounter.decrementAndGet() == 0)
		//		{
		super.close();
		dbHelper = null;
		//		}
	}
}
