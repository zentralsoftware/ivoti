package com.c1info.preparation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PersiapanPerolehanSuaraCLI {

	private String configFile = "config.properties";
	private Properties properties = new Properties();
	private PersiapanPerolehanSuara process;
	
	public void init() throws IOException
	{
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFile);
		if (inputStream == null)
		{
			throw new FileNotFoundException(configFile + " not found");
		}
		properties.load(inputStream);
		process = new PersiapanPerolehanSuara();
		process.setInputDirectory(properties.getProperty("inputDirectory"));
		process.setOutputDirectory(properties.getProperty("outputDirectory"));
		process.setPattern(properties.getProperty("pattern"));
		process.setPercGridX(Double.parseDouble(properties.getProperty("percGridX")));
		process.setPercGridY(Double.parseDouble(properties.getProperty("percGridY")));
		process.setPosX(Double.parseDouble(properties.getProperty("posX")));
		process.setPosY(Double.parseDouble(properties.getProperty("posY")));
		process.setCountGridX(Double.parseDouble(properties.getProperty("countGridX")));
		process.setCountGridY(Double.parseDouble(properties.getProperty("countGridY")));
	}	

	public void run() throws IOException
	{
		process.execute();
	}
	
	public static void main(String[] args) throws IOException
	{
		PersiapanPerolehanSuaraCLI cli = new PersiapanPerolehanSuaraCLI();
		cli.init();
		cli.run();
	}
}
