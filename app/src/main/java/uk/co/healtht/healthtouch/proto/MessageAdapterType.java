//package uk.co.healtht.healthtouch.proto;
//
//import uk.co.healtht.healthtouch.R;
//import uk.co.healtht.healthtouch.model.entities.HTTrackerMessage;
//
//public class MessageAdapterType
//{
//	public int iconSubjectId = 0;
//
//	public static MessageAdapterType configureAdapterType(HTTrackerMessage htTrackerMessage, HTTrackerMessage entry)
//	{
//		MessageAdapterType messageAdapterType = new MessageAdapterType();
//
//		if (htTrackerMessage.type.equalsIgnoreCase("medication"))
//		{
//			messageAdapterType.iconSubjectId = R.drawable.pill;
//		}
//		else if (htTrackerMessage.type.equalsIgnoreCase("care")
//				|| htTrackerMessage.type.equalsIgnoreCase("share"))
//		{
//			messageAdapterType.iconSubjectId = R.drawable.ic_stethoscope;
//		}
//		else if (htTrackerMessage.type.equalsIgnoreCase("forms"))
//		{
//			messageAdapterType.iconSubjectId = R.drawable.settings_profile;
//		}
//		else if (htTrackerMessage.type.equalsIgnoreCase("thresholds"))
//		{
//			messageAdapterType.iconSubjectId = R.drawable.settings_threshold;
//		}
//		else if (htTrackerMessage.type.equalsIgnoreCase("tracker"))
//		{
//			messageAdapterType.iconSubjectId = R.drawable.ic_tracker_stats;
//		}
//		else if (htTrackerMessage.type.equalsIgnoreCase("profile"))
//		{
//			messageAdapterType.iconSubjectId = R.drawable.settings_profile;
//		}
//
//		return messageAdapterType;
//	}
//}
