package com.github.lyokofirelyte.VTV3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.Getter;
import lombok.SneakyThrows;

public class VTV3 extends JavaPlugin {
	
	public static String DIR = "./plugins/VTV3";
	public static String DIR_CONFIG = "./plugins/VTV3/config";
	public static String DIR_TEMP = "./plugins/VTV3/temp";
	public static String SETTINGS_JSON = "./plugins/VTV3/config/settings.json";
	
	private String jre;
	private VTEvents vte;
	private int rel = 0;
	
	@Getter
	private Object compiledClass;

	@Getter
	private VTParser parser;

	@Override @SneakyThrows
	public void onEnable(){
		for (String dir : new String[]{ DIR, DIR_CONFIG, DIR_TEMP }){
			new File(dir).mkdirs();
		}
		checkDepends();
		vte = new VTEvents(this);
		parser = new VTParser();
		Bukkit.getPluginManager().registerEvents(vte, this);
		comp();
	}
	
	@Override
	public void onDisable(){
		System.out.println("I don't want to go!");
		for (String f : new File(".").list()){
			if (f.endsWith(".class") && f.startsWith("VT")){
				new File(f).delete();
			}
		}
	}
	
	@SneakyThrows
	public String getCommandOutput(String command){
	    String output = "";
	    BufferedReader reader = null;
	    InputStreamReader streamReader = null;
	    InputStream stream = null;

	    try {
	    	Process process = Runtime.getRuntime().exec(command);
	    	stream = process.getInputStream();
	        streamReader = new InputStreamReader(stream);
	        reader = new BufferedReader(streamReader);

	        String currentLine = null;
	        StringBuilder commandOutput = new StringBuilder();
	        
	        while ((currentLine = reader.readLine()) != null) {
	            commandOutput.append(currentLine);
	        }

	        int returnCode = process.waitFor();
	        
	        if (returnCode == 0) {
	            output = commandOutput.toString();
	        }

	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	stream.close();
	        streamReader.close();
	    }
	    return output;
	}
	
	public String prettyJSON(String json){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(json);
		return gson.toJson(je);
	}
	
	@SneakyThrows
	public void writeFile(File file, JSONObject stuff){
		FileWriter writer = new FileWriter(file);
		writer.write(stuff.toJSONString());
		writer.close();
	}
	
	@SneakyThrows
	public JSONObject readFile(File file){
		JSONParser parser = new JSONParser();  
		return (JSONObject) parser.parse(new FileReader(file));
	}
	
	@SneakyThrows
	private void checkDepends(){
		File configFile = new File(SETTINGS_JSON);
		
		if (!configFile.exists()){
			configFile.createNewFile();
			FileUtils.copyInputStreamToFile(getResource("settings.json"), configFile);
			configFile = new File(SETTINGS_JSON);
		}
		
		JSONObject configFileJSON = readFile(configFile);
		
		if (!configFileJSON.containsKey("PLUGIN_ENABLED") || !(boolean) configFileJSON.get("PLUGIN_ENABLED")){
			disable("This plugin is NOT the exact same as the last version." +
				"\n* Your scripts are formatted slightly differently *" +
				"\n* Please see the Bukkit page for full instructions! *" +
				"\n*** If you understand everything, set PLUGIN_ENABLED to true in the settings.json ***"
			);
			return;
		}
		
		if (configFileJSON.containsKey("JAVA_HOME")){
			String javaHome = (String) configFileJSON.get("JAVA_HOME");
			if (!javaHome.equals("none")){
				if (SystemUtils.IS_OS_WINDOWS){
					javaHome = javaHome.replace("/", "\\");
					javaHome = javaHome.replace("\\bin\\", "");
				}
				jre = new String(System.getProperty("java.home"));
				System.setProperty("java.home", javaHome);
			} else {
				jre = new String(System.getProperty("java.home"));
				String output = getCommandOutput("where" + (SystemUtils.IS_OS_WINDOWS ? "" : "is") +  " javac").replace("\\javac.exe", "").replace("/javac", "").replace("/javac.exe", "").replace("\\", "/") + "/";
				if (SystemUtils.IS_OS_WINDOWS){
					output = output.replace("/", "\\");
					output = output.replace("\\bin\\", "");
				} else {
					output = output.split(" ")[1];
				}
				System.setProperty("java.home", output);
			}
		} else {
			disable("You must define JAVA_HOME in " + SETTINGS_JSON);
			return;
		}
	}
	
	private void disable(String reason){
		System.out.print("====== VTV3 CRITICAL ERROR ====== ");
		System.out.print(reason);
		System.out.print("====== =================== ====== ");
		Bukkit.getPluginManager().disablePlugin(this);
	}
	
	@SneakyThrows
	public void comp(){
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	
	    StringWriter writer = new StringWriter();
	    PrintWriter out = new PrintWriter(writer);
	    
	    new File(DIR_TEMP + "/temp.txt").createNewFile();
	    FileUtils.copyInputStreamToFile(getResource("template.java"), new File(DIR_TEMP + "/temp.txt"));
	    
	    List<String> code = parser.parse(Files.readAllLines(new File(DIR_TEMP + "/temp.txt").toPath()));
	    
	    for (String item : code){
	    	out.println(item.contains("%replace%") ? item.replace("%replace%", "VTV3CompilerClass_" + rel) : item);
	    }

	    out.close();
	    System.out.println(writer.toString());
	    JavaFileObject file = new JavaSourceFromString("VTV3CompilerClass_" + rel, writer.toString());
	
	    Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
	    CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);
	    boolean success = task.call();
	
	    if (success){
	    	try {
	    		URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { new File("").toURI().toURL() });
	    		compiledClass = Class.forName("VTV3CompilerClass_" + rel, true, classLoader).newInstance();
	    		compiledClass.getClass().getDeclaredMethod("main", new Class[]{ String[].class }).invoke(null, new Object[]{ null });
	    	} catch (Exception e){
	    		System.out.println("There was an error starting VTV3. Please ensure you have defined JAVA_HOME inside of the settings.json.");
	    		e.printStackTrace();
	    	}
	    } else {
	    	 System.out.println("There was an error starting VTV3. Please ensure you have defined JAVA_HOME inside of the settings.json.");
	    	 for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
	    	      System.out.println(diagnostic.getCode());
	    	      System.out.println(diagnostic.getKind());
	    	      System.out.println(diagnostic.getPosition());
	    	      System.out.println(diagnostic.getStartPosition());
	    	      System.out.println(diagnostic.getEndPosition());
	    	      System.out.println(diagnostic.getSource());
	    	      System.out.println(diagnostic.getMessage(null));
    	    }
	    }
	    
	    System.setProperty("java.home", jre);
	    rel++;
	}
	
	@SneakyThrows
	public boolean exec(String methodName, Class<?>[] types, Object... args){
		return (boolean) compiledClass.getClass().getDeclaredMethod(methodName, types).invoke(compiledClass, args);
	}

	class JavaSourceFromString extends SimpleJavaFileObject {
	  final String code;
	  JavaSourceFromString(String name, String code) {
	    super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),Kind.SOURCE);
	    this.code = code;
	  }
	  @Override
	  public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		  return code;
	  }
	}
}