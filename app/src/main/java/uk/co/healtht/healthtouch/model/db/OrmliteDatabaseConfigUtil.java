package uk.co.healtht.healthtouch.model.db;

/**
 * Created by Najeeb.Idrees on 14-Mar-17.
 */


import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

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
import uk.co.healtht.healthtouch.model.entities.HTTrackerActivity;
import uk.co.healtht.healthtouch.model.entities.HTTrackerBlood;
import uk.co.healtht.healthtouch.model.entities.HTTrackerBloodSugar;
import uk.co.healtht.healthtouch.model.entities.HTTrackerBreathing;
import uk.co.healtht.healthtouch.model.entities.HTTrackerFluidIntake;
import uk.co.healtht.healthtouch.model.entities.HTTrackerFluidOutput;
import uk.co.healtht.healthtouch.model.entities.HTTrackerGastrointestinal;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMedication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMessage;
import uk.co.healtht.healthtouch.model.entities.HTTrackerOxygen;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPain;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPeakFlow;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPulse;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminder;
import uk.co.healtht.healthtouch.model.entities.HTTrackerStool;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSweating;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;
import uk.co.healtht.healthtouch.model.entities.HTTrackerWeight;
import uk.co.healtht.healthtouch.model.entities.HTUserNew;

/**
 * OrmliteDatabaseConfigUtil is a separate program from the actual android app,
 * that is used to generate the database structure configuration before runtime.
 * It uses the models provided via a list of class objects,
 * and use the provided annotations to generate the configuration accordingly.
 */
public class OrmliteDatabaseConfigUtil extends OrmLiteConfigUtil
{
	/**
	 * classes represents the models to use for generating the ormlite_config.txt file
	 */
	private static final Class<?>[] classes = new Class[]
			{
					HTTrackerSync.class,
					HTTrackerBlood.class,
					HTTrackerBloodSugar.class,
					HTTrackerBreathing.class,
					HTTrackerFluidIntake.class,
					HTTrackerFluidOutput.class,
					HTTrackerOxygen.class,
					HTTrackerWeight.class,
					HTTrackerPulse.class,
					HTTrackerPeakFlow.class,
					HTTrackerStool.class,
					HTTrackerThreshold.class,
					HTTrackerReminder.class,
					HTTrackerMessage.class,
					HTTrackerMedication.class,
					HTShareNew.class,
					HTServiceNew.class,
					HTServiceMember.class,
					HTForm.class,
					HTFormType.class,
					HTFormQuestion.class,
					HTBranding.class,
					HTUserNew.class,
					HTPPGoodToKnow.class,
					HTPPKeyContact.class,
					HTPPMedicalHistory.class,
					HTPPMedicalHistoryDetail.class,
					HTPPMedicalHistoryDetailType.class,

					HTTrackerActivity.class,
					HTTrackerGastrointestinal.class,
					HTTrackerPain.class,
					HTTrackerSweating.class,

					HTMedicationReminder.class,
					HTMedicationTakenTracker.class,
					HTMedicationImage.class,

//					HTDeviceRegistration.class,
					AccountInfo.class
			};

	/**
	 * Given that this is a separate program from the android app, we have to use
	 * a static main java method to create the configuration file.
	 *
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void main(String[] args) throws IOException, SQLException
	{

		String currDirectory = "user.dir";

		String configPath = "/app/src/main/res/raw/ormlite_config.txt";

		/**
		 * Gets the project root directory
		 */
		String projectRoot = System.getProperty(currDirectory);

		/**
		 * Full configuration path includes the project root path, and the location
		 * of the ormlite_config.txt file appended to it
		 */
		String fullConfigPath = projectRoot + configPath;

		File configFile = new File(fullConfigPath);

		/**
		 * In the a scenario where we run this program serveral times, it will recreate the
		 * configuration file each time with the updated configurations.
		 */
		if (configFile.exists())
		{
			configFile.delete();
			configFile = new File(fullConfigPath);
		}

		/**
		 * writeConfigFile is a util method used to write the necessary configurations
		 * to the ormlite_config.txt file.
		 */
		writeConfigFile(configFile, classes);
	}
}