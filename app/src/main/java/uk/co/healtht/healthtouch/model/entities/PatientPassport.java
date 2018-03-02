package uk.co.healtht.healthtouch.model.entities;

import java.io.Serializable;

/**
 * Created by Najeeb.Idrees on 07-Aug-17.
 */

public class PatientPassport implements Serializable
{
	public String name;
	public int progress;
	public int imageResource;

	public PatientPassport(String name, int progress, int imageResource)
	{
		this.name = name;
		this.progress = progress;
		this.imageResource = imageResource;
	}

	public PatientPassport()
	{
	}
}
