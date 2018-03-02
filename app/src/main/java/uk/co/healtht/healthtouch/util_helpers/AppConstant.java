package uk.co.healtht.healthtouch.util_helpers;

import uk.co.healtht.healthtouch.model.entities.HTMedicationImage;
import uk.co.healtht.healthtouch.model.entities.HTMedicationReminder;
import uk.co.healtht.healthtouch.model.entities.HTMedicationTakenTracker;

/**
 * Created by Najeeb.Idrees on 10-Jul-17.
 */

public class AppConstant
{
	public static final String INTERNET_DISCONNECT = "Please check your internet connection and try again";

	public static final String FRAGMENT_PACKAGE = "uk.co.healtht.healthtouch.ui.fragment.";
	public static final String WIDGET_PACKAGE = "uk.co.healtht.healthtouch.ui.widget.";
	public static final String DELEGATE_PACKAGE = "uk.co.healtht.healthtouch.model.delegate.";
	public static final String ENTITY_PACKAGE = "uk.co.healtht.healthtouch.model.entities.";


	public static final String[] appEntities = new String[]
			{
					"HTTrackerSync", //never need to send back
					"HTTrackerBlood",
					"HTTrackerBloodSugar",
					"HTTrackerBreathing",
					"HTTrackerFluidIntake",
					"HTTrackerFluidOutput",
					"HTTrackerOxygen",
					"HTTrackerWeight",
					"HTTrackerPulse",
					"HTTrackerPeakFlow",
					"HTTrackerStool",
					"HTTrackerThreshold",
					"HTTrackerReminder",
					"HTTrackerMessage",
					"HTTrackerMedication",
					"HTShareNew",
										"HTServiceNew",
										"HTServiceMember",
										"HTForm",
										"HTFormType",
										"HTFormQuestion",
										"HTBranding",
					"HTUserNew",

					//Patient passport models
					"HTPPGoodToKnow",
					"HTPPKeyContact",
					"HTPPMedicalHistory",
					"HTPPMedicalHistoryDetail",
					"HTPPMedicalHistoryDetailType",

					// Fabrys models
					"HTTrackerActivity",
					"HTTrackerPain",
					"HTTrackerGastrointestinal",
					"HTTrackerSweating",

					"HTMedicationReminder",
					"HTMedicationTakenTracker",
					"HTMedicationImage",
			};


	public static final String BLOOD_SUGAR_LEVEL = "BLOOD SUGAR LEVEL";
	public static final String BLOOD_PRESSURE = "BLOOD PRESSURE";
	public static final String WEIGHT = "WEIGHT";
	public static final String FLUID_INTAKE = "FLUID INTAKE";
	public static final String FLUID_OUTPUT = "FLUID OUTPUT";
	public static final String OXYGEN_SATURATION = "OXYGEN SATURATION";
	public static final String HEART_RATE = "HEART RATE";
	public static final String BREATHING = "BREATHING";
	public static final String STOOL_TYPE = "STOOL TYPE";
	public static final String PEAK_FLOW = "PEAK FLOW";


	public static final String SWEATING = "SWEATING";
	public static final String ACTIVITY = "ACTIVITY";
	public static final String GASTROINTESTINAL = "GASTROINTESTINAL";
	public static final String PAIN = "PAIN";

}
