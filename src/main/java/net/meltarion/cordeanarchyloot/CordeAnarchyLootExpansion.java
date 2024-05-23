package net.meltarion.cordeanarchyloot;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.TimeZone;

public class CordeAnarchyLootExpansion extends PlaceholderExpansion {
    private final CordeAnarchyLoot plugin;
    private String locationDetails = "Неизвестно";
    private String locationDetailsXZ = "Неизвестно";
    private int nextBroadcastSeconds = 0;

    private String timePlaceholder;
    public int getNextBroadcastSeconds() {
        return this.nextBroadcastSeconds;
    }


    public CordeAnarchyLootExpansion(CordeAnarchyLoot plugin) {
        this.plugin = plugin;
        (new BukkitRunnable() {
            public void run() {
                if (CordeAnarchyLootExpansion.this.nextBroadcastSeconds > 0) {
                    CordeAnarchyLootExpansion.this.nextBroadcastSeconds--;
                } else {
                    CordeAnarchyLootExpansion.this.nextBroadcastSeconds = plugin.getConfig().getInt("hour-respawn") * 3600;
                }

            }
        }).runTaskTimer(plugin, 0L, 20L);
    }

    public void initializeTimePlaceholder() {
        int timeStartHour = plugin.getConfig().getInt("time-start-hour");
        this.timePlaceholder = "В " + timeStartHour + ":00 МСК";
    }

    public String getIdentifier() {
        return "cordeanarchyloot";
    }

    public String getAuthor() {
        return (String)this.plugin.getDescription().getAuthors().get(0);
    }

    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        TimeZone mskTimeZone = TimeZone.getTimeZone("Europe/Moscow");
        Calendar calendar = Calendar.getInstance(mskTimeZone);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        if (player == null) {
            return "";
        } else if ("sbName".equals(identifier)) {
            return this.locationDetails;
        } else if ("sbCoords".equals(identifier)) {
            return this.locationDetailsXZ;
        }
        else if ("sbTimeLeft".equals(identifier) && currentHour < plugin.getConfig().getInt("time-start-hour") || currentHour > plugin.getConfig().getInt("time-end-hour")) {
            if(currentHour < plugin.getConfig().getInt("time-start-hour")) {
                return this.timePlaceholder;
            }
            if(currentHour > plugin.getConfig().getInt("time-end-hour")){
                return this.timePlaceholder;
            }
        }
        else {
            return "sbTimeLeft".equals(identifier) ? this.formatTime(this.nextBroadcastSeconds) : null;
        }
        return "sbTimeLeft".equals(identifier) ? this.formatTime(this.nextBroadcastSeconds) : null;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public void setLocationDetailsXZ(String locationDetailsXZ) {
        this.locationDetailsXZ = locationDetailsXZ;
    }

    public void setNextBroadcastSeconds(int nextBroadcastSeconds) {
        this.nextBroadcastSeconds = nextBroadcastSeconds;
    }

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = seconds % 3600 / 60;
        return String.format("%02dч %02dм", hours, minutes);
    }
}