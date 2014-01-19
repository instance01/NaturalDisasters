package com.comze_instancelabs.natural_disasters;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;

public class Tsunami implements Runnable {
	public Tsunami(Block epicentre, final int runcount, JavaPlugin p) {
		this.active.add(epicentre);
		this.maxrun = runcount;
		this.p = p;
	}

	private int task;
	private final int maxrun;
	private int runcount = 0;
	private Set<Block> activated = new HashSet<Block>();
	private Set<Block> active = new HashSet<Block>();
	private Set<Block> newactive = new HashSet<Block>();
	private Random rand = new Random();
	private JavaPlugin p;

	public void start() {
		this.task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(p, this, 0, 3);
	}

	public void cancel() {
		Bukkit.getServer().getScheduler().cancelTask(this.task);
	}

	Random r = new Random();
	public void run(Block active) {
		if (this.activated.add(active)) {
			Block up = active.getRelative(BlockFace.UP);
			if (this.activated.add(up)) {
				if (rand.nextInt(2) == 0) {
					up.setTypeIdAndData(active.getTypeId(), active.getData(), true);
					up.getWorld().getBlockAt(new Location(up.getWorld(), up.getLocation().getBlockX(),up.getLocation().getBlockY() + r.nextInt(5), up.getLocation().getBlockZ())).setTypeId(8);
				}
				active.getWorld().createExplosion(active.getLocation(), 0);

				Block oldside, newside;
				for (BlockFace face : new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST }) {
					oldside = active.getRelative(face.getModX(), -1, face.getModZ());
					while (oldside.getY() < 127) {
						newside = oldside.getRelative(BlockFace.UP);
						if (newside.getTypeId() == 0) {
							break;
						} else {
							oldside = newside;
						}
					}
					if (oldside.getTypeId() != 0) {
						this.newactive.add(oldside);
					} else {
						this.activated.add(oldside);
					}
				}
			}
		}
	}

	public void run() {
		active.addAll(newactive);
		newactive.clear();

		this.active.removeAll(this.activated);

		for (Block active : this.active){
			run(active);
		}

		if (runcount++ >= maxrun){
			this.cancel();
		}
	}
}

