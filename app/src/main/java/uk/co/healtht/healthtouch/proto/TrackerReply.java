package uk.co.healtht.healthtouch.proto;

import java.util.List;

import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;

public class TrackerReply extends Reply
{
	public List<HTTrackerSync> data;

	//	public HTTrackerSync getTrackerByMonitorId(String monitorId)
	//	{
	//		if (monitorId != null)
	//		{
	//			if (!monitorId.startsWith("/monitors/"))
	//			{
	//				monitorId = "/monitors/" + monitorId;
	//			}
	//
	//			for (HTTrackerSync tracker : data)
	//			{
	//				for (Monitor monitor : tracker.monitors)
	//				{
	//					if (monitorId.equals(monitor.uri))
	//					{
	//						return tracker;
	//					}
	//				}
	//			}
	//
	//		}
	//
	//		return null;
	//	}

	public HTTrackerSync getTrackerById(int trackerId)
	{
		if (trackerId != 0)
		{
			//			if (!trackerId.startsWith("/trackers/"))
			//			{
			//				trackerId = "/trackers/" + trackerId;
			//			}

			for (HTTrackerSync tracker : data)
			{
				if (trackerId == tracker.tracker_id)
				{
					return tracker;
				}
			}

		}

		return null;
	}

	// TODO need to change following code once we successfully change Tracker with HTTrackerSync Model
	public Tracker getTrackerById(String trackerId)
	{
		return new Tracker();
	}
}
