package com.netease.tdg;


import java.io.*;
import java.lang.reflect.*;
import java.nio.*;
import java.util.*;
import java.util.Map.*;
import java.util.regex.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.app.*;
import android.os.*;
import android.os.storage.*;
import android.util.Log;
import android.view.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.opengl.*;

@SuppressLint("NewApi")
public class Launcher extends Activity
{
	// CPU核数
	private static int getCoreNumber() 
	{
		// Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter 
		{
			@Override
			public boolean accept(File pathname) 
			{
				// Check if filename is "cpu", followed by a single digit number
				if (Pattern.matches("cpu[0-9]", pathname.getName())) 
				{
					return true;
				}
				return false;
			}
		}
		try 
		{
			// Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			// Return the number of cores (virtual CPU devices)
			return files.length;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return 1;
		}
	}
	
	// 统计给定路径下可用空间的大小
	private static long getStat(String path)
	{
		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize();
		long availableBlocks  = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	// 收集filelist.txt的数据
	private static HashMap<String, String> collectFileList(InputStream filelist_txt) throws IOException
	{
		HashMap<String, String> md5map = new HashMap<String, String>();
	 	BufferedReader reader = new BufferedReader(new InputStreamReader(filelist_txt)); 
	 	String line = "";
		while((line = reader.readLine()) != null)
		{
			String[] parts = line.split("\t");
			String fpath = parts[0].replaceAll("\\\\","/");
			md5map.put(fpath, parts[parts.length-1]);		 		
	 	}
		return md5map;
	}
	
	private static final int KITKAT_UI_OPTION = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
			| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
			| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
			| View.SYSTEM_UI_FLAG_IMMERSIVE;

	private static final int OTHER_UI_OPTION = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN		            
			| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
			| View.SYSTEM_UI_FLAG_LOW_PROFILE
			| View.SYSTEM_UI_FLAG_FULLSCREEN ;
	
	private GLSurfaceView m_view;
	private ProgressDialog m_progress_dlg;
//	private int m_width = 0;
//	private int m_height = 0;
//	private int m_preferred_w = 0;
//	private int m_preferred_h = 0;
//	private boolean m_enable_auto_size;
	HashMap<String, String> m_asset_filelist;
		
	private Launcher m_launcher;
	private PlatformConfigParser m_platform_config;
	private boolean m_is_gl_loaded;
	
	public final static int STORAGE_INTERNAL = 0;
	public final static int STORAGE_EXTERNAL = 1;
	public final static int STORAGE_DATA = 2;
	
	class AssetInfo
	{
		public String Path;
		public String MD5;
		public long Size;
		
		public AssetInfo(String p, String md5, long size)
		{
			Path = p;
			MD5 = md5;
			Size = size;
		}
	}
	
	private HashMap<String, AssetInfo> m_asset_to_copy = new HashMap<String, AssetInfo>();

	class StorageStatus
	{
		public int Type;
		public long AvailableSize;
		public String Path;
		public String UIString;
		
		private Context m_context;
		public StorageStatus(Context context, int storage_type)
		{
			m_context = context;
			Type = storage_type;
			AvailableSize = 0L;
			switch(storage_type)
			{
			case STORAGE_INTERNAL:
				UIString = m_context.getString(R.string.launcher_internal_sd);
				break;
			case STORAGE_EXTERNAL:
				UIString = m_context.getString(R.string.launcher_external_sd);
				break;
			case STORAGE_DATA:
				UIString = m_context.getString(R.string.launcher_data_sd);
				break;
			default:
				UIString = null;
				break;
			}
		}
	}
	
	private StorageStatus[] m_storage_statuses = new StorageStatus[3];
	private StorageStatus m_current_storage = null;
	private String m_neox_root = null;
	private long m_size_to_copy = 0;
  
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
				super.onCreate(savedInstanceState);
				// 初始化m_progress_dlg
				m_progress_dlg = new ProgressDialog(this);
				m_progress_dlg.setIndeterminate(false);
				m_progress_dlg.setCanceledOnTouchOutside(false);
				m_progress_dlg.setCancelable(false);
				m_progress_dlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				m_progress_dlg.setMax(100);
				m_progress_dlg.setTitle(R.string.launcher_copy_data);
				m_progress_dlg.setIcon(R.drawable.ic_launcher);

				// 初始化PlatformConfig的变量
				m_platform_config = new PlatformConfigParser();
				m_platform_config.addVariable("SDK_INT", Build.VERSION.SDK_INT);
				m_platform_config.addVariable("CORE_NUM", getCoreNumber());
				m_platform_config.addVariable("MODEL", Build.MODEL);
				m_platform_config.addVariable("MANUFACTURER", Build.MANUFACTURER);
				m_is_gl_loaded = false;
        
        m_view = new GLSurfaceView(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
        {
	    		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) 
	    		{
	    			m_view.setSystemUiVisibility(KITKAT_UI_OPTION);
	    		}
	    		else
	    		{
	    			m_view.setSystemUiVisibility(OTHER_UI_OPTION);
	    		}
        }
        m_launcher = this;
        m_view.setEGLContextClientVersion(2);
        m_view.setRenderer(new GLSurfaceView.Renderer() {
        	private final float[] VERTICE = { -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,  1.0f, 1.0f,  1.0f, };
        	private final float[] UVS = {0.0f, 1.0f, 1.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,};
        	private final String m_vs_code = 
        			"attribute vec4 pos;\n" + 
        			"attribute vec4 uv_in;\n" + 
        			"varying vec2 uv_out;\n" + 
        			"void main()\n" +
        			"{\n" +
        			"	gl_Position = pos;\n" + 
        			"	uv_out = uv_in.xy;\n" + 
        			"}\n";
        	
        	private final String m_ps_code = 
        			"varying highp vec2 uv_out;\n" +
        			"uniform sampler2D bg;\n" +
        			"void main()\n" +
        			"{\n" +
        			"	gl_FragColor = texture2D(bg, uv_out);\n" +  
        			"}\n";
        	
        	private int m_pos_attrib;
        	private int m_uv_attrib;
        	private int m_bg_sampler;
        	private int m_program;
        	private FloatBuffer m_pos_buffer;
        	private FloatBuffer m_uv_buffer;
        	private int m_texture;
        	
        	public int loadShader(int type, String shaderCode)
        	{
        	    //Create a Vertex Shader Type Or a Fragment Shader Type (GLES20.GL_VERTEX_SHADER OR GLES20.GL_FRAGMENT_SHADER)
        	    int shader = GLES20.glCreateShader(type);

        	    //Add The Source Code and Compile it
        	    GLES20.glShaderSource(shader, shaderCode);
        	    GLES20.glCompileShader(shader);

        	    return shader;
        	}
        	
			@Override
			public void onSurfaceCreated(GL10 gl, EGLConfig config) 
			{
				final String renderer = GLES20.glGetString(GLES20.GL_RENDERER);
				final String vendor = GLES20.glGetString(GLES20.GL_VENDOR);
				final String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);				
				String _version = GLES20.glGetString(GLES20.GL_VERSION);
				final String version = _version == null ? "null":_version;
				m_launcher.runOnUiThread(new Runnable()
				{
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (!m_launcher.m_is_gl_loaded)
						{
							m_launcher.m_is_gl_loaded = true;
							m_launcher.m_platform_config.addVariable("GL_RENDERER", renderer);
							m_launcher.m_platform_config.addVariable("GL_VENDOR", vendor);
							m_launcher.m_platform_config.addVariable("GL_VERSION", version);
							m_launcher.m_platform_config.addVariable("GL_EXTENSIONS", extensions);
							m_launcher.launch();
						}
					}
				});
				int vs = loadShader(GLES20.GL_VERTEX_SHADER, m_vs_code);
				int ps = loadShader(GLES20.GL_FRAGMENT_SHADER, m_ps_code);
				m_program = GLES20.glCreateProgram();
				GLES20.glAttachShader(m_program, vs);
				GLES20.glAttachShader(m_program, ps);
				GLES20.glLinkProgram(m_program);
				m_pos_attrib = GLES20.glGetAttribLocation(m_program, "pos");
				m_uv_attrib = GLES20.glGetAttribLocation(m_program, "uv_in");
				m_bg_sampler = GLES20.glGetUniformLocation(m_program, "bg");
				
				ByteBuffer bb = ByteBuffer.allocateDirect(VERTICE.length * 4);
				bb.order(ByteOrder.nativeOrder());
				m_pos_buffer = bb.asFloatBuffer();
				m_pos_buffer.put(VERTICE);
				m_pos_buffer.position(0);
				
				bb = ByteBuffer.allocateDirect(UVS.length * 4);
				bb.order(ByteOrder.nativeOrder());
				m_uv_buffer = bb.asFloatBuffer();
				m_uv_buffer.put(UVS);
				m_uv_buffer.position(0);
				
				///加载纹理
				final int[] textureHandle = new int[1];
				GLES20.glGenTextures(1, textureHandle, 0);
				m_texture = textureHandle[0];
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_texture);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inScaled = false;
//				final Bitmap bmp = BitmapFactory.decodeResource(m_launcher.getResources(), R.drawable.init, options);
//				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
//				bmp.recycle();
			}
			
			@Override
			public void onSurfaceChanged(GL10 gl, int width, int height) 
			{
				gl.glViewport(0, 0, width, height);
			}
			
			@Override
			public void onDrawFrame(GL10 gl) 
			{
				GLES20.glUseProgram(m_program);
				GLES20.glEnableVertexAttribArray(m_pos_attrib);
				GLES20.glEnableVertexAttribArray(m_uv_attrib);
				GLES20.glVertexAttribPointer(m_pos_attrib, 2, GLES20.GL_FLOAT, false, 0, m_pos_buffer);
				GLES20.glVertexAttribPointer(m_uv_attrib, 2, GLES20.GL_FLOAT, false, 0, m_uv_buffer);
				GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_texture);
				GLES20.glUniform1i(m_bg_sampler, 0);
				GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
			}
		});
        setContentView(m_view);

        // 开启屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
	
	@Override
	public void onBackPressed()
	{
	}
	
	void initStorageStatus()
	{
		for (int i = 0; i < m_storage_statuses.length; ++i)
		{
			m_storage_statuses[i] = new StorageStatus(this, i);
		}
		// 使用getVolumePaths获取真实可用的存储器
		StorageManager sm= (StorageManager)getSystemService(Context.STORAGE_SERVICE);
		Method method_getVolumePaths = null;
	    Method method_getVolumeState = null;  
		try
		{
			method_getVolumePaths = sm.getClass().getMethod("getVolumePaths");
			method_getVolumeState = sm.getClass().getMethod("getVolumeState", String.class);
		}
		catch(NoSuchMethodException ex)
		{
        }
		if (method_getVolumePaths != null && method_getVolumeState != null)
		{
			try
			{
	            String []paths = (String[])method_getVolumePaths.invoke(sm);//调用该方法  
	            for(int i = 0; i < paths.length; ++i)
	            {
	            	String status = (String)method_getVolumeState.invoke(sm, paths[i]);
	            	if (status.equals(Environment.MEDIA_MOUNTED))
	            	{
	            		long size = getStat(paths[i]);
	            		if (i == 0)
	            		{
	            			m_storage_statuses[STORAGE_INTERNAL].Path = paths[i];
	            			m_storage_statuses[STORAGE_INTERNAL].AvailableSize = size;
	            		}
	            		else
	            		{
	            			if (m_storage_statuses[STORAGE_EXTERNAL].AvailableSize == 0)
	            			{
		            			m_storage_statuses[STORAGE_EXTERNAL].Path = paths[i];
		            			m_storage_statuses[STORAGE_EXTERNAL].AvailableSize = size;	            				
	            			}
	            		}
	            	}
	            }
			}
			catch(IllegalArgumentException ex)
			{  
				ex.printStackTrace();  
			}
			catch(IllegalAccessException ex)
			{  
				ex.printStackTrace();     
			}
			catch(InvocationTargetException ex)
			{  
				ex.printStackTrace();  
			}
		}
		
		if (m_storage_statuses[STORAGE_INTERNAL].AvailableSize == 0 && m_storage_statuses[STORAGE_EXTERNAL].AvailableSize == 0)
		{
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			{
				m_storage_statuses[STORAGE_INTERNAL].Path = Environment.getExternalStorageDirectory().getPath();
				m_storage_statuses[STORAGE_INTERNAL].AvailableSize = getStat(m_storage_statuses[STORAGE_INTERNAL].Path);
			}
		}
		
		String curBuildType = "@CMAKE_BUILD_TYPE@";
		if (curBuildType.equals("MinSizeRel") && m_storage_statuses[STORAGE_INTERNAL].AvailableSize > 0)
		{
			m_storage_statuses[STORAGE_INTERNAL].Path = getApplicationContext().getExternalFilesDir(null).getPath();
		}
		m_storage_statuses[STORAGE_DATA].Path = getApplicationContext().getFilesDir().getPath();
		m_storage_statuses[STORAGE_DATA].AvailableSize = getStat(m_storage_statuses[STORAGE_DATA].Path);
		
		SharedPreferences neox_config = getSharedPreferences("neox_config", 0);
		int storage_type = neox_config.getInt("Storage", STORAGE_INTERNAL);
		if (m_storage_statuses[storage_type].AvailableSize > 0)
		{
			m_current_storage = m_storage_statuses[storage_type]; 
		}
		else
		{
			for(int i = 0; i < STORAGE_DATA + 1; ++i)
			{
				if (m_storage_statuses[i].AvailableSize > 0)
				{
					m_current_storage = m_storage_statuses[i];
					break;
				}				
			}
		}
	}
	
	void savePreference()
	{
		
		SharedPreferences neox_config = getSharedPreferences("neox_config", 0);
		SharedPreferences.Editor editor = neox_config.edit();
		editor = editor.putInt("Storage", m_current_storage.Type).putString("NeoXRoot", m_neox_root);
		HashMap<String, Boolean> options = m_platform_config.getOptions();
		for(Entry<String, Boolean> entry: options.entrySet())
		{
			editor = editor.putBoolean(entry.getKey(), entry.getValue().booleanValue());
		}
		editor.commit();
	}
	
	void startGame()
	{
		if (m_timer != null)
		{
			m_timer.cancel();
		}
		if (m_progress_dlg != null)
		{
			m_progress_dlg.dismiss();
		}
		savePreference();
		Intent clientIntent = new Intent(m_launcher, Client.class) ;
		m_launcher.startActivity(clientIntent);
		finish();
	}
	
	void updateCopyingFile(String path)
	{
		String t = getResources().getString(R.string.launcher_copying) + " " + path;
		m_progress_dlg.setTitle(t);
	}
	
	void updateCopiedSize(long copiedSize)
	{
		int percent = (int)(copiedSize * 100 / m_size_to_copy);
		m_progress_dlg.setProgress(percent);
	}
	
	@Override
	public void onWindowFocusChanged (boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus)
		{
	        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
	        {
	    		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) 
	    		{
	    			m_view.setSystemUiVisibility(KITKAT_UI_OPTION);
	    		}
	    		else
	    		{
	    			m_view.setSystemUiVisibility(OTHER_UI_OPTION);
	    		}
	        }
		}
	}

	// 统计有多少Asset要拷贝的
	long calcAssetToCopy(String neox_root)
	{
		m_asset_to_copy.clear();
		// 读取目标目录下的filelist.txt
		HashMap<String, String> dest_filelist_txt = null;
		if (neox_root != null)
		{
	        try
	        {
	        	InputStream inputStream = new FileInputStream(new File(neox_root, "filelist.txt"));
	        	dest_filelist_txt = Launcher.collectFileList(inputStream);
	        }
	        catch(IOException e)
	        {
	        	dest_filelist_txt = null;
	        }
		}
		if (dest_filelist_txt == null)
		{
			dest_filelist_txt = new HashMap<String, String>();
		}
		
		long total_size = 0;
		AssetManager am = getAssets();
		for (String key: m_asset_filelist.keySet())
		{
    		String srcmd5 = m_asset_filelist.get(key);
    		String dstmd5 = dest_filelist_txt.get(key);
    		if(!srcmd5.equals(dstmd5))
    		{
    			long size = 0;
    			try
    			{
    				InputStream inputStream = am.open(key);
    				size = inputStream.available();
    			}
    			catch(IOException e)
    			{
    				e.printStackTrace();
    				size = 0;
    			}
    			m_asset_to_copy.put(key, new AssetInfo(key, srcmd5, size));
    			total_size += size;
    		}
		}
		return total_size;
	}
	
	boolean determineStorage()
	{
        // 初始化StorageStatus
        initStorageStatus();
        // 读取assets下的filelist.txt
        try
        {
        	AssetManager am = getAssets();
        	InputStream filelist_txt = am.open("filelist.txt");
        	m_asset_filelist = Launcher.collectFileList(filelist_txt);
        }
        catch(IOException ex)
        {
        	m_asset_filelist = new HashMap<String, String>();
        }
        SharedPreferences neox_config = getSharedPreferences("neox_config", 0);
        m_neox_root = neox_config.getString("NeoXRoot", null);
        if (m_neox_root == null)
        {
        	m_size_to_copy = calcAssetToCopy(null);
        	m_current_storage = null;
            for (StorageStatus ss: m_storage_statuses)
            {
            	if (m_size_to_copy + 1024 * 1024 < ss.AvailableSize)
            	{
            		m_current_storage = ss;
            		break;
            	}
            }
            if (m_current_storage == null)
            {
            	return false;
            }
            else
            {
            	m_neox_root = getResources().getString(R.string.neox_root);
                if (m_neox_root.startsWith("/sdcard/"))
                {
                	m_neox_root = m_neox_root.substring(7);
                }
                m_neox_root = m_current_storage.Path + m_neox_root;
                return true;
            }
        }
        else
        {
        	m_current_storage = m_storage_statuses[neox_config.getInt("Storage", 0)];
        	m_size_to_copy = calcAssetToCopy(m_neox_root);
        	if (m_size_to_copy + 1024 * 1024 > m_current_storage.AvailableSize)
        	{
        		return false;
        	}
        	return true;
        }
	}
	
	private CopyFile m_copy_file;
	private Timer m_timer;
	void launch()
	{
        // 决定使用哪个存储器
        if (determineStorage())
        {
        	File neoxDir = new File(m_neox_root);
        	if (!neoxDir.exists())
        	{
        		neoxDir.mkdirs();
        	}
        	else
        	{
        		if (!neoxDir.isDirectory())
        		{
        			Log.e("NeoXDevice", m_neox_root + " must be a directory!");
        			finish();
        			return;
        		}
        	}
    		InputStream inputstream = null;
    		try
    		{
    			inputstream = getAssets().open("PlatformConfig.xml");
    		}
    		catch (IOException ex)
    		{
    			ex.printStackTrace();
    		}
    		if (inputstream != null)
    		{
    			m_platform_config.parse(inputstream);
    		}
    		m_progress_dlg.setProgress(0);
    		m_progress_dlg.show();
    		
    		m_copy_file = new CopyFile();
    		Thread thread = new Thread(m_copy_file);
    		thread.start();
    		if (m_timer != null)
    		{
    			m_timer.cancel();
    		}
    		m_timer = new Timer();
    		m_timer.scheduleAtFixedRate(new TimerTask()
    		{
    			@Override
    			public void run()
    			{
    				Handler handler = new UpdateHandler(Looper.getMainLooper());
    				handler.sendEmptyMessage(1);
    			}
    		}, 1, 60);
        }
        else
        {
			String text = getResources().getString(
					R.string.launcher_asset_size_to_copy)
					+ " "
					+ android.text.format.Formatter.formatFileSize(this, m_size_to_copy) 
					+ "\n";
        	if (m_current_storage == null)
        	{
        		text += getResources().getString(R.string.launcher_no_enough_space);
        	}
        	else
        	{
    			text += m_current_storage.UIString
    					+ " "
    					+ android.text.format.Formatter.formatFileSize(this,
    							m_current_storage.AvailableSize);
        	}
			AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage(text).setIcon(R.drawable.ic_launcher);
			builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() 
			{	
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					// TODO Auto-generated method stub
					m_launcher.finish();
				}
			});
			builder.create().show();
        }
	}
	
	class CopyFile implements Runnable
	{
		private long m_copied_size = 0;
		private String m_copying_file = null;
		private final static int BUFFER_SIZE = 4096;
		private byte [] m_buffer = new byte[BUFFER_SIZE];
		
		public long getCopiedSize()
		{
			return m_copied_size;
		}
		public String getCopyingFile()
		{
			return m_copying_file;
		}
		private void copyAsset(String path, long fileSize)
		{
			AssetManager assetManager = m_launcher.getAssets();
			long last_copied_size = m_copied_size;
			try
			{
				InputStream inputStream = assetManager.open(path);
				File outfile = new File(m_neox_root, path);
				if (!outfile.exists())
				{
					File parent = outfile.getParentFile();
					if (parent != null && !parent.exists())
					{
						parent.mkdirs();
					}
					outfile.createNewFile();
				}
				FileOutputStream outputstream = new FileOutputStream(outfile);
				int length;
				while ((length = inputStream.read(m_buffer)) > 0)
				{
					outputstream.write(m_buffer, 0, length);
					m_copied_size += length;
				}
                outputstream.flush();
                outputstream.close();
                inputStream.close();
			}
			catch(Exception e)
			{
				m_copied_size = last_copied_size + fileSize;
				Log.e("NeoXDevice", "Failed to copy asset file " + path);
			}
		}
		public void run()
		{
			m_copied_size = 0;
			m_copying_file = null;
			for(Entry<String, AssetInfo> entry: m_asset_to_copy.entrySet())
			{
				m_copying_file = entry.getValue().Path;
				copyAsset(m_copying_file, entry.getValue().Size);
			}
			// 拷贝filelist.txt
			copyAsset("filelist.txt", 0);
			m_launcher.runOnUiThread(new Runnable()
			{
				@Override
				public void run() 
				{
					// TODO Auto-generated method stub
					m_launcher.startGame();
				}
			});
		}
    }

	class UpdateHandler extends Handler
	{
		public UpdateHandler(Looper looper)
		{
			super(looper);
		}
		
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if (m_progress_dlg != null && m_copy_file != null)
			{
				String title = m_launcher.getResources().getString(R.string.launcher_copy_data);
				String copyingFile = m_copy_file.getCopyingFile();
				long copiedSize = m_copy_file.getCopiedSize();
				if (copyingFile != null)
				{
					title = m_launcher.getResources().getString(R.string.launcher_copying) + " " + copyingFile;
				}
				int progress = 100;
				if (m_size_to_copy > 0)
				{
					progress = (int)(copiedSize * 100 / m_size_to_copy);
				}
				m_progress_dlg.setTitle(title);
				m_progress_dlg.setProgress(progress);
			}
		}
	}
}
