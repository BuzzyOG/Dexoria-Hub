package Gadgets;

import java.util.HashMap;

import me.lewys.com.Hub;
import me.lewys.com.RandomFirework;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import dexoria.core.DexCore;


public class Firework implements Listener{
	
	 private HashMap<String, Double> cooldownTime = new HashMap<String, Double>();
	 private HashMap<String, BukkitRunnable> cooldownTask = new HashMap<String, BukkitRunnable>();

    
	@EventHandler
	public void onFireworkLaunch(final PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getPlayer().getItemInHand().getType() == Material.FIREWORK){
			
				e.setCancelled(true);
				
				if(cooldownTime.containsKey(e.getPlayer().getName())){
					e.getPlayer().sendMessage(ChatColor.BLUE + "Gadget > " + ChatColor.GRAY + " > You must wait for " + ChatColor.RED + cooldownTime.get(e.getPlayer().getName()) + ChatColor.GRAY + " seconds.");
					return;
				}	
				
				if(DexCore.getCurrencySystem().hasEnoughGC(e.getPlayer().getUniqueId().toString(), 10)){
					DexCore.getCurrencySystem().removeGC(e.getPlayer().getUniqueId().toString(), 10);
					
					RandomFirework.getManager().launchRandomFirework(e.getPlayer().getLocation());
					
					final Player p = e.getPlayer();
					
					cooldownTime.put(p.getName(), 2.0);
                    cooldownTask.put(p.getName(), new BukkitRunnable() {
                            public void run() {
                                    cooldownTime.put(p.getName(), cooldownTime.get(p.getName()) - 0.5);
                                    if (cooldownTime.get(p.getName()) == 0) {
                                            cooldownTime.remove(p.getName());
                                            cooldownTask.remove(p.getName());
                                            cancel();
                                    }
                            }
                    });
                   
                    cooldownTask.get(p.getName()).runTaskTimer(Hub.getPluginInstance(), 10, 10);

						}else{
							e.getPlayer().sendMessage(ChatColor.BLUE + "Gadget > " + ChatColor.GRAY + "You don't have enough points!");
						}
					}
			}
		}
}
