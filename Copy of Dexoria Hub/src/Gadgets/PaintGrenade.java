package Gadgets;

import java.util.HashMap;
import java.util.Random;

import me.lewys.com.Hub;
import me.lewys.com.Points;
import me.lewys.particles.ParticleEffect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PaintGrenade implements Listener{
	
	public HashMap<String, BukkitRunnable> cooldowntask = new HashMap<String, BukkitRunnable>();
	public HashMap<String, Double> timeleft = new HashMap<String, Double>();
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if(e.getPlayer().getItemInHand().getType() != null){
			if(e.getPlayer().getItemInHand().getType() == Material.EXP_BOTTLE){
				
				e.setCancelled(true);
				
				if(Points.hasEnough(e.getPlayer().getName(), 30)){
					Points.removePoints(e.getPlayer().getName(), 30);
				}else{
					e.getPlayer().sendMessage("�2�lGadget" + ChatColor.WHITE + " > " + ChatColor.GRAY + "You don't have enough points!");
					return;
				}
				
				
				if(!cooldowntask.containsKey(e.getPlayer().getName())){
					final Player p = e.getPlayer();
					p.launchProjectile(ThrownExpBottle.class, 
							p.getLocation().getDirection().multiply(1));
					
					timeleft.put(p.getName(), 10.0);
					cooldowntask.put(p.getName(), new BukkitRunnable() {

						@Override
						public void run() {
							if(timeleft.get(p.getName()) == 0.0){
								cooldowntask.remove(p.getName());
								timeleft.remove(p.getName());
								cancel();
							}else{
								timeleft.put(p.getName(), timeleft.get(p.getName()) - 0.5);
							}
						}
					});
					
					cooldowntask.get(p.getName()).runTaskTimer(Hub.instance, 10, 10);
					
				}else{
					e.getPlayer().sendMessage("�2�lGadget" + ChatColor.WHITE + " > You must wait for " + ChatColor.RED + timeleft.get(e.getPlayer().getName()) + ChatColor.WHITE + " seconds.");
				}
			}
		}
	}
	
	
	@EventHandler
	public void onXPHit(ExpBottleEvent e){
		
		e.setExperience(0);
		
		final Location startloc = e.getEntity().getLocation();
		
		Location corner1 = startloc.add(2,1,2);
		
		Location corner2 = startloc.subtract(2,1,2);
		
		loopThrough(corner1, corner2);
		
	}
	
	
	@SuppressWarnings("deprecation")
	private void loopThrough(Location loc1, Location loc2)
	{
		
	    int minx = Math.min(loc1.getBlockX(), loc2.getBlockX()),
	        miny = Math.min(loc1.getBlockY(), loc2.getBlockY()),
	        minz = Math.min(loc1.getBlockZ(), loc2.getBlockZ()),
	        maxx = Math.max(loc1.getBlockX(), loc2.getBlockX()),
	        maxy = Math.max(loc1.getBlockY(), loc2.getBlockY()),
	        maxz = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
	    
	    for(int x = minx; x<=maxx;x++)
	    {
	        for(int y = miny; y<=maxy;y++)
	        {
	            for(int z = minz; z<=maxz;z++)
	            {
	            	
	            	Location loc = new Location(loc1.getWorld(), x,y,z);
	            	
	            	if(loc.getBlock().getType() != Material.AIR){
	            	final BlockState state = loc.getBlock().getState();
	            	
	            	Random r = new Random(); 
	        		DyeColor c = DyeColor.values()[r.nextInt(DyeColor.values().length)];
	        		
	        		loc.getBlock().setType(Material.WOOL);
	        		loc.getBlock().setData(c.getData());
	        		ParticleEffect.INSTANT_SPELL.display(loc.add(0,1,0), 0.5f, 0.5f, 0.5f, 0.05f, 5);
	        		
	        		Bukkit.getScheduler().scheduleSyncDelayedTask(Hub.instance, new Runnable(){

						@Override
						public void run() {
							state.update();
						}
	        		}, 4 * 20);
	            }
	         }
	        }
	    }
	}
}
