package net.meltarion.cordeanarchyloot;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class CordeAnarchyLoot extends JavaPlugin {
    public FileConfiguration config;
    private CordeAnarchyLootExpansion expansion;
    private Random random = new Random();

    public CordeAnarchyLoot() {
    }

    public void onEnable() {
        this.config = this.getConfig();
        this.config.options().copyDefaults(true);
        this.saveConfig();
        this.getCommand("cordeanarchyloot").setExecutor(new ForceCommand(this));
        this.scheduleRespawnTask();
        this.expansion = new CordeAnarchyLootExpansion(this);
        this.expansion.register();
        this.expansion.setNextBroadcastSeconds(3600);
        expansion.initializeTimePlaceholder();
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    void scheduleRespawnTask() {
        // Установка часового пояса на МСК
        TimeZone mskTimeZone = TimeZone.getTimeZone("Europe/Moscow");
        Calendar calendar = Calendar.getInstance(mskTimeZone);

        // Вычисление времени до следующего запуска задачи (10:00 или 10:00 следующего дня)
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        long delay;

        if (currentHour < 4) {
            // Если текущее время до 10:00, то задержка равна разнице между текущим временем и 10:00
            calendar.set(Calendar.HOUR_OF_DAY, getConfig().getInt("time-start-hour"));
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } else {
            // Если текущее время после 10:00, то задержка равна разнице между текущим временем и 10:00 следующего дня
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, getConfig().getInt("time-start-hour"));
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }

        delay = (calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000;

        // Запуск задачи через задержку
        new BukkitRunnable() {
            public void run() {
                // Проверка времени
                Calendar currentTime = Calendar.getInstance(mskTimeZone);
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                if (hour >= getConfig().getInt("time-start-hour") && hour < getConfig().getInt("time-end-hour")) {
                    CordeAnarchyLoot.this.respawn();
                }
            }
        }.runTaskLater(this, delay * 20L);

        // Запуск задачи каждые 12 часов
        new BukkitRunnable() {
            public void run() {
                // Проверка времени
                Calendar currentTime = Calendar.getInstance(mskTimeZone);
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                if (hour >= getConfig().getInt("time-start-hour") && hour < getConfig().getInt("time-end-hour")) {
                    CordeAnarchyLoot.this.respawn();
                }
            }
        }.runTaskTimer(this, 12 * 60 * 60 * 20L, 12 * 60 * 60 * 20L);
    }

    void respawn() {
        Set<String> locationKeys = this.config.getConfigurationSection("locations").getKeys(false);
        List<String> locationKeyList = new ArrayList(locationKeys);
        String randomLocationKey = (String)locationKeyList.get(this.random.nextInt(locationKeyList.size()));
        final String locationName = this.config.getString("locations." + randomLocationKey + ".name");
        List<String> commands = this.config.getStringList("locations." + randomLocationKey + ".commands");
        final String locationCoords = this.config.getString("locations." + randomLocationKey + ".coords");
        this.expansion.setLocationDetails(locationName);
        this.expansion.setLocationDetailsXZ(locationCoords);
        this.expansion.setNextBroadcastSeconds(this.getConfig().getInt("time-protection") * 60);
        Iterator var7 = commands.iterator();

        while(var7.hasNext()) {
            String command = (String)var7.next();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        Bukkit.broadcastMessage(this.getConfig().getString("messages.broadcast").replace("&", "§").replace("%name%", locationName).replace("%coords%", locationCoords));
        final int[] remainingMinutes = new int[]{this.getConfig().getInt("time-protection")};
        (new BukkitRunnable() {
            public void run() {
                if (remainingMinutes[0] <= 0) {
                    this.cancel();
                    CordeAnarchyLoot.this.expansion.setLocationDetails("Неизвестно");
                    CordeAnarchyLoot.this.expansion.setLocationDetailsXZ("Неизвестно");
                    Bukkit.broadcastMessage(CordeAnarchyLoot.this.getConfig().getString("messages.lootboxes-open").replace("&", "§").replace("%name%", locationName).replace("%coords%", locationCoords));
                } else if (remainingMinutes[0] <= 5 || remainingMinutes[0] % 5 == 0) {
                    Bukkit.broadcastMessage(CordeAnarchyLoot.this.getConfig().getString("messages.broadcast-time-left").replace("&", "§").replace("%name%", locationName).replace("%coords%", locationCoords).replace("%time%", String.valueOf(remainingMinutes[0])));
                }

                int var10002 = remainingMinutes[0]--;
            }
        }).runTaskTimer(this, 0L, 1200L);
    }
}
