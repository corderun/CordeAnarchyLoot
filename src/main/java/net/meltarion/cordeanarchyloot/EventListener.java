package net.meltarion.cordeanarchyloot;

import net.raidstone.wgevents.WorldGuardEvents;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class EventListener implements Listener {

    CordeAnarchyLoot plugin;

    public EventListener(CordeAnarchyLoot plugin) {
        super();
        this.plugin = plugin;
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        List<String> mysticRegions = plugin.getConfig().getStringList("mystic-regions");
        boolean isInRegion = false;

        for (String region : mysticRegions) {
            if (WorldGuardEvents.isPlayerInAnyRegion(player.getUniqueId(), region)) {
                isInRegion = true;
                break;
            }
        }
        if (!isInRegion) {
            return;
        }
        if (event.getClickedBlock() == null || !event.getAction().name().contains("RIGHT_CLICK_BLOCK")) {
            return;
        }
        if(event.getClickedBlock().getType().equals(Material.CHEST)){
            if(player.hasPermission("cordeanarchyloot.admin")){
                return;
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, plugin.getConfig().getInt("glow-duration")*20, 0, false, false));
        }

    }

}
