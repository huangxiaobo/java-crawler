package com.example.decode;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	Context m_context;
	private PlatformConfigParser m_platform_config;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		m_context = this.getApplicationContext();

		// 初始化PlatformConfig的变量
		m_platform_config = new PlatformConfigParser(m_context);
		m_platform_config.addVariable("SDK_INT", Build.VERSION.SDK_INT);
		m_platform_config.addVariable("CORE_NUM", 2);
		m_platform_config.addVariable("MODEL", Build.MODEL);
		m_platform_config.addVariable("MANUFACTURER", Build.MANUFACTURER);
		m_platform_config.addVariable("GL_RENDERER", "GC1000");
		// m_launcher.m_platform_config.addVariable("GL_VENDOR", vendor);
		// m_launcher.m_platform_config.addVariable("GL_VERSION", version);
		// m_launcher.m_platform_config.addVariable("GL_EXTENSIONS",
		// extensions);

		InputStream inputStream = null;
		try {
			inputStream = getAssets().open("EncrytedPlatformConfig.xml");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (inputStream != null) {
			m_platform_config.parse(inputStream, true);
		}

		HashMap<String, Boolean> options = m_platform_config.getOptions();
		for (Map.Entry entry : options.entrySet()) {
			System.out.println(entry.getKey() + ", " + entry.getValue());
		}
	}

	private File encryptFile(InputStream inputStream) {
		byte[] buffer = new byte[1024];
		int readCount = 0;
		try {
			File outputFile = createTempFile();
			FileOutputStream outputstream = new FileOutputStream(outputFile);

			while ((readCount = inputStream.read(buffer)) > 0) {
				encryptData(buffer);
				outputstream.write(buffer, 0, readCount);
			}

			inputStream.close();
			outputstream.flush();
			outputstream.close();

			return outputFile;
		} catch (Exception e) {
			Log.e("NeoXDevice", "PlatformConfigParser encryptFile failed!:" + e);
		}
		return null;
	}

	private File decryptFile(InputStream inputStream) {
		byte[] buffer = new byte[1024];
		int readCount = 0;
		try {
			File decryptedFile = createTempFile();
			FileOutputStream outputStream = new FileOutputStream(decryptedFile);

			while ((readCount = inputStream.read(buffer)) > 0) {
				decryptData(buffer);
				outputStream.write(buffer, 0, readCount);
			}

			inputStream.close();
			outputStream.flush();
			outputStream.close();

			return decryptedFile;
		} catch (Exception e) {
			Log.e("NeoXDevice", "PlatformConfigParser decryptFile failed!:" + e);
		}
		return null;
	}

	private File createTempFile() {
		try {
			File outputDir = m_context.getCacheDir(); // context being the
														// Activity pointer
			File outputFile = File.createTempFile("EncrytedPlatformConfig",
					"xml", outputDir);
			Log.d("amw", outputFile.getAbsolutePath());
			if (!outputFile.exists()) {
				File parent = outputFile.getParentFile();
				if (parent != null && !parent.exists()) {
					parent.mkdirs();
				}
				outputFile.createNewFile();
			}
			return outputFile;
		} catch (Exception e) {
			;
		}
		return null;
	}

	private void encryptData(byte[] data) {
		for (int i = 0; i < data.length; ++i) {
			data[i] ^= 0xff;
		}
	}

	private void decryptData(byte[] data) {
		encryptData(data);
	}
}
