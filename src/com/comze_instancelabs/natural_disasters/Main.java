package com.comze_instancelabs.natural_disasters;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;


public class Main extends JavaPlugin implements Listener {

	
	double percentage;
	
	public void onEnable(){
		getConfig().addDefault("config.chance_per_minute", 0.5D);
		getConfig().addDefault("config.regions_blacklist", "");
		getConfig().addDefault("config.blocks_blacklist", "");
		getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		percentage = getConfig().getDouble("config.chance_per_minute");
		
		final JavaPlugin pl = this;
		// reset timer
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run(){
				if(chance()){
					if(Bukkit.getOnlinePlayers().length > 0){
						Random r = new Random();
						double d = Math.random();
						Location l = Bukkit.getOnlinePlayers()[r.nextInt(Bukkit.getOnlinePlayers().length)].getLocation();
						if (d < 0.33){
						    tornado(pl,l);
						}else if (d > 0.33 && d < 0.66){
						    earthquake(pl,l);
						}else{
							tsunami(pl,l);
						}
					}
				}
			}
		}, 20 * 60, 20 * 60); // 20*60 = 60 seconds;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("dis") || cmd.getName().equalsIgnoreCase("disaster")){
			if(args.length > 0){
				String action = args[0];
				if(sender instanceof Player){
					if(action.equalsIgnoreCase("tornado")){
						Location l = ((Player)sender).getLocation();
						tornado(this, l);
					}else if(action.equalsIgnoreCase("earthquake")){
						Location l = ((Player)sender).getLocation();
						earthquake(this, l);
					}else if(action.equalsIgnoreCase("tsunami")){
						Location l = ((Player)sender).getLocation();
						tsunami(this, l);
					}
				}
			}else{
				sender.sendMessage("Possible disasters: tornado, earthquake, tsunami");
			}
			return true;
		}
		return false;
	}
	
	
	
	public static void tornado(JavaPlugin p, Location l){
		Byte b = (byte) 0;
		Vector vec = new Vector(2, 0, 2);
		Tornado.spawnTornado(p, l, Material.DIRT, b, vec, 0.3D, 200, 600, true, false, l.getWorld());
	}
	
	public static void earthquake(JavaPlugin p, Location l){
		new EarthQuake(l.getBlock(), 15, p).start();
	}
	
	public static void tsunami(JavaPlugin p, Location l){
		new Tsunami(l.getBlock(), 15, p).start();
	}
	
	
	public boolean chance(){
		double d = Math.random();
		if (d < percentage){
		    return true;
		}else {
		    return false;
		}
	}

}
