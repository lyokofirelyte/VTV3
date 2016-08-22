package com.github.lyokofirelyte.VTV3;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;

import lombok.SneakyThrows;

public class VTParser {
	
	public static String SCRIPTS = "./plugins/VTV3/scripts";
	public static String COMMANDS = "./plugins/VTV3/commands";
	public static String EVENTS = "./plugins/VTV3/events";
	Map<String, List<String>> codes = new HashMap<>();
	
	public VTParser(){
		new File(SCRIPTS).mkdirs();
		new File(COMMANDS).mkdirs();
		new File(EVENTS).mkdirs();
	}

	@SneakyThrows
	public List<String> parse(List<String> template){
		//template = sortThroughFiles(SCRIPTS, ".vts", "custom", template);
		template = sortThroughFiles(COMMANDS, ".vtc", "command", template, VTV.SENDER, VTV.MESSAGE);
		for (File scriptFile : new File(EVENTS).listFiles()){
			if (scriptFile.getName().endsWith(".vte")){
				List<String> result = generalParse(scriptFile, VTEvent.valueOf(scriptFile.getName().toUpperCase().replace(".VTE", "")).getVtvs());
				template.add("public boolean " + "event" + "_" + scriptFile.getName().replace(".", "") + "(" + generateConstructor(VTEvent.valueOf(scriptFile.getName().toUpperCase().replace(".VTE", "")).getVtvs()) + "){");
				for (String r : result){
					template.add(r);
				}
				template.add("}");
			}
		}
		template.add("}");
		return template;
	}
	
	public List<String> sortThroughFiles(String directory, String requiredFileExtension, String prefix, List<String> template, VTV... vtvs){
		for (File scriptFile : new File(directory).listFiles()){
			if (scriptFile.getName().endsWith(requiredFileExtension)){
				List<String> result = generalParse(scriptFile, vtvs);
				template.add("public boolean " + prefix + "_" + scriptFile.getName().replace(".", "") + "(" + generateConstructor(vtvs) + "){");
				for (String r : result){
					template.add(r);
				}
				template.add("}");
			}
		}
		return template;
	}
	
	public String generateConstructor(VTV... vtvs){
		List<VTV> newUsed = new ArrayList<VTV>();
		for (int i = 0; i < VTV.values().length; i++){
			for (VTV v : vtvs){
				if (v.getId() == i){
					newUsed.add(v);
					break;
				}
			}
		}
		String header = "";
		for (VTV v : newUsed){
			header += header.equals("") ? v.getType().getName() + " " + v.s() : ", " + v.getType().getName() + " " + v.s();
		}
		return header;
	}
	
	@SneakyThrows
	public List<String> generalParse(File file, VTV... vtvs){
		List<String> content = Files.readAllLines(file.toPath());
		List<String> newCode = new ArrayList<String>();
		newCode.add("boolean cancelled = false;");
		String passable = "";
		
		for (VTV v : vtvs){
			passable += passable.equals("") ? v.toString() : ", " + v.toString();
		}
		
		for (String line : content){
			String[] args = line.split(" ");
			String methodName = args[0].replace("@", "");
			switch (args[0]){
				case "@SETCANCELLED": // @SETCANCELLED true/false
					if (args.length == 2 && (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false"))){
						newCode.add("cancelled = " + args[1].toLowerCase() + ";");
					}
					break;
					
				case "@PLAYER": // @PLAYER <message to send to player>
					newCode.add(vtMethod(methodName, VTV.SENDER.s(), wP(argsToString(args, 1), passable, true)));
					break;
					
				case "@DEBUG": case "@!": // @!
					newCode.add(vtMethod("PLAYER", VTV.SENDER.s(), wQ(vtStyle("This is VTV3!"))));
					break;
					
				case "@SETBLOCK": // @SETBLOCK <blockid:data> <location>
					newCode.add(vtMethod(methodName, wQ(argsToString(args, 1))));
					break;	
			}
		}
		
		newCode.add("return cancelled;");
		return newCode;
	}
	
	public String wQ(String message){
		return "\"" + message + "\"";
	}
	
	// Just don't think about it, it's too confusing and it hurts my head as it is.
	// WHY DID I DO THIS? It's such a big mess.
	public String wP(String message, String passable, boolean quotes){
		return "parseItem(" + (quotes ? wQ(message) : message) + ", " + passable + ")";
	}
	
	public String vtStyle(String msg){
		return ChatColor.translateAlternateColorCodes('&', "&2VTV3 &a\u1A1F &2" + msg);
	}
	
	public String argsToString(String[] args, int startAt){
		String newString = "";
		for (int i = startAt; i < args.length; i++){
			newString += newString.equals("") ? args[i] : " " + args[i];
		}
		return newString.toString();
	}
	
	public String vtMethod(String name, String... args){
		String a = "";
		for (String arg : args){
			a += a.equals("") ? arg : ", " +  arg;
		}
		return "vta." + name + "(" + a + ");";
	}
}