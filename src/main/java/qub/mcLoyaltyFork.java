// afam2000 First Plugin
package qub;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Random;

public final class mcLoyaltyFork extends JavaPlugin implements Listener {
    private FileConfiguration config;
    private boolean simLooting = true;
    private boolean canImpalePlayer = true;
    private boolean tridentHasHome = true;
    private boolean pickupFromGround = true;
    private boolean pickupDrops = true;
    private boolean canLead = true;
    private boolean canPearl = true;

    // Helper Function to get random int in range
    private static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private void fillConfig() {
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();
        this.config = this.getConfig();
        this.config.addDefault("simLooting", true);
        this.config.addDefault("canImpalePlayer", true);
        this.config.addDefault("tridentHasHome", true);
        this.config.addDefault("pickupFromGround", true);
        this.config.addDefault("pickupDrops", true);
        this.config.addDefault("canLead", true);
        this.config.addDefault("canPearl", true);
    }

    private void matchConfig() {
        this.simLooting = this.config.getBoolean("simLooting");
        this.canImpalePlayer = this.config.getBoolean("canImpalePlayer");
        this.tridentHasHome = this.config.getBoolean("tridentHasHome");
        this.pickupFromGround = this.config.getBoolean("pickupFromGround");
        this.pickupDrops = this.config.getBoolean("pickupDrops");
        this.canLead = this.config.getBoolean("canLead");
        this.canPearl = this.config.getBoolean("canPearl");
    }

    @Override
    public void onEnable() {
        fillConfig();
        matchConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("toggleLootSim")) {
            simLooting = !simLooting;
            config.set("simLooting",simLooting);
            saveConfig();
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage("Offhand Looting Simulation Toggled: " + simLooting);
            }
        }
        else if (command.getName().equals("toggleCanImpale")) {
            canImpalePlayer = !canImpalePlayer;
            config.set("canImpalePlayer",canImpalePlayer);
            saveConfig();
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage("canImpalePlayer toggled to: " + canImpalePlayer);
            }
        }
        else if (command.getName().equals("toggleTridentHome")) {
            tridentHasHome = !tridentHasHome;
            config.set("tridentHasHome",tridentHasHome);
            saveConfig();
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage("tridentHasHome toggled to: " + tridentHasHome);
            }
        }
        else if (command.getName().equals("toggleGroundPickup")) {
            pickupFromGround = !pickupFromGround;
            config.set("pickupFromGround",pickupFromGround);
            saveConfig();
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage("pickupFromGround toggled to: " + pickupFromGround);
            }
        }
        else if (command.getName().equals("toggleDropPickup")) {
            pickupDrops = !pickupDrops;
            config.set("pickupDrops",pickupDrops);
            saveConfig();
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage("pickupDrops toggled to: " + tridentHasHome);
            }
        }
        else if (command.getName().equals("toggleCanPearl")) {
            canPearl = !canPearl;
            config.set("canPearl",canPearl);
            saveConfig();
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage("canPearl toggled to: " + canPearl);
            }
        }
        else if (command.getName().equals("toggleCanLead")) {
            canLead = !canLead;
            config.set("canLead",canLead);
            saveConfig();
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage("canLead toggled to: " + canLead);
            }
        }
        else if(command.getName().equals("clearTridentHome"))
        {
            if(sender instanceof Player)
            {
                PlayerInventory pi =((Player)sender).getInventory();
                for(ItemStack seeker : pi.getContents())
                {
                    if (seeker!=null && seeker.toString().contains("ThrownTrident"))
                    {
                        pi.remove(seeker);
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public void pickupDrops(EntityDeathEvent ev) {
        Entity moribund = ev.getEntity();
        Player player = ev.getEntity().getKiller();
        World world = moribund.getWorld();
        boolean hasRare = false; // Used to simulate looting on wither skeletons

        if (simLooting) {
            if (player != null) {
                PlayerInventory pi = player.getInventory();
                ItemStack offhand = pi.getItemInOffHand();
                ItemStack mainhand = pi.getItemInMainHand();
                if (offhand.containsEnchantment(Enchantment.LOOT_BONUS_MOBS) && !mainhand.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
                    for (ItemStack is : ev.getDrops()) {
                        if (is.toString().contains("WITHER") || is.toString().contains("INGOT")||is.toString().contains("FOOT")|| is.toString().contains("SHULKER")) {
                            hasRare = true;
                            continue;
                        }

                        if (moribund instanceof Wither || moribund instanceof IronGolem || moribund instanceof Snowman || moribund instanceof Fox || moribund instanceof Fish
                                || is.toString().contains("WOOL") || is.toString().contains("SPONGE") || is.toString().contains("UNDYING") || is.toString().contains("SKULL")
                                || is.toString().contains("SWORD") || is.toString().contains("AXE") || is.toString().contains("SHOVEL") || is.toString().contains("BOW")
                                || is.toString().contains("INGOT") || is.toString().contains("POTATO") || is.toString().contains("CARROT")
                                || is.toString().contains("HELMET") || is.toString().contains("BOOTS") || is.toString().contains("LEGGINGS") || is.toString().contains("CHEST")
                                || is.toString().contains("PUMPKIN") || is.toString().contains("JACK") || is.toString().contains("BANNER") || is.toString().contains("CHICKEN")
                                || is.toString().contains("SADDLE")|| is.toString().contains("NAUTILUS")|| is.toString().contains("CARPET")) {
                            continue;
                        }
                        for (int i = 0; i < offhand.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) * 2; i++) {
                            int random = new Random().nextInt();
                            if (random % 5 == 0) {
                                is.setAmount(is.getAmount() + 1);
                            }
                        }
                    }

                    // Simulate extra chance to find rare drop
                    if (!hasRare)
                    {
                        int rareChance = getRandomNumber(0, 30);
                        if (rareChance < offhand.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS))
                        {
                            if (moribund instanceof WitherSkeleton)
                                ev.getDrops().add(new ItemStack(Material.WITHER_SKELETON_SKULL));
                            if (moribund instanceof Rabbit)
                                ev.getDrops().add(new ItemStack(Material.RABBIT_FOOT));
                            if (moribund instanceof Shulker)
                                ev.getDrops().add(new ItemStack(Material.SHULKER_SHELL));
                        }
                    }
                }
            }
        }


        if(pickupDrops)
        {
            boolean attached = false;
            int exp = ev.getDroppedExp();
            if (player != null) {
                world.spawn(player.getLocation(), ExperienceOrb.class).setExperience(exp);
                ev.setDroppedExp(0);
            }
            Trident trident;
            for (Entity seeker : moribund.getNearbyEntities(2, 2, 2)) {
                for (ItemStack itemStack : ev.getDrops()) {
                    if (seeker instanceof Trident) {
                        trident = (Trident) seeker;
                        trident.addPassenger(world.dropItem(seeker.getLocation(), itemStack));
                        attached = true;
                        trident.addPassenger(moribund);
                    }
                }
            }
            if (attached)
                ev.getDrops().clear();
        }
    }

    @EventHandler
    public void onHitByTrident(EntityDamageByEntityEvent ev) {
        if (ev.getDamager() instanceof Trident) {
            Trident trident = (Trident)ev.getDamager();
            Player player = (Player)trident.getShooter();
            if (this.canLead && (player.getInventory().getItemInOffHand().toString().contains("LEAD") || player.getInventory().getItemInMainHand().toString().contains("LEAD"))) {
                trident.setDamage(0.0D);
                ev.setDamage(0.0D);
                ev.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onTridentHit(ProjectileHitEvent ev) {
        Projectile proj = ev.getEntity();
        ProjectileSource ps = proj.getShooter();
        if (proj instanceof Trident && ps instanceof Player) {
            Player shooter = (Player)ps;
            Entity victim = ev.getHitEntity();
            PlayerInventory pi = shooter.getInventory();
            if (canLead && (pi.getItemInOffHand().toString().contains("LEAD") || pi.getItemInMainHand().toString().contains("LEAD"))) {
                if (victim instanceof Player) {
                    if (canImpalePlayer) {
                        proj.addPassenger(victim);
                    }
                } else if (victim instanceof Creature && !(victim instanceof Wither)) {
                    proj.addPassenger(victim);
                }
            }

            if (pickupFromGround) {
                //Pickup nearby items
                for (Entity ent : proj.getNearbyEntities(1.5, 1.0, 1.5)) {
                    if (ent instanceof Item || ent instanceof ExperienceOrb) {
                        proj.addPassenger(ent);
                    }
                    else if(ent instanceof Projectile)
                    {
                        ent.leaveVehicle();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLoyaltyThrown(ProjectileLaunchEvent ev) {
        Projectile proj = ev.getEntity();
        ProjectileSource ps = ev.getEntity().getShooter();
        //World world = ev.getEntity().getWorld();
        if (proj instanceof Trident && ps instanceof Player) {
            Trident trident = (Trident)proj;
            Player player = (Player)ps;
            PlayerInventory pi = player.getInventory();
            if (this.canPearl && pi.getItemInOffHand().toString().contains("PEARL")) {
                trident.addPassenger(player.launchProjectile(EnderPearl.class));
                pi.getItemInOffHand().setAmount(pi.getItemInOffHand().getAmount() - 1);
            }

            if (trident.getItemStack().containsEnchantment(Enchantment.LOYALTY) && this.tridentHasHome) {
                ItemStack TRIDENTS_RECALL = new ItemStack(Material.WOODEN_SWORD);
                ItemMeta tm = TRIDENTS_RECALL.getItemMeta();
                tm.setDisplayName("ThrownTrident");
                tm.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                TRIDENTS_RECALL.setItemMeta(tm);
                TRIDENTS_RECALL.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, -9);
                if (pi.firstEmpty() != -1) {
                    if (pi.getItemInMainHand().containsEnchantment(Enchantment.LOYALTY)) {
                        pi.setItemInMainHand(TRIDENTS_RECALL);
                    } else if (pi.getItemInOffHand().containsEnchantment(Enchantment.LOYALTY)) {
                        pi.setItemInOffHand(TRIDENTS_RECALL);
                    }
                }
            }
        }

    }
    @EventHandler
    public void findTridentHome(PlayerPickupItemEvent ev) {
        if (this.tridentHasHome) {
            int i = 0;
            ItemStack aa = ev.getItem().getItemStack();
            Player player = ev.getPlayer();
            if (aa.containsEnchantment(Enchantment.LOYALTY)) {
                for(ItemStack seeker:player.getInventory().getContents())
                {
                    if (seeker != null && seeker.toString().contains("ThrownTrident")) {
                        player.getInventory().remove(seeker);
                        ItemStack trident = aa.clone();
                        player.getInventory().setItem(i, trident);
                        ev.setCancelled(true);
                        ev.getItem().remove();
                        break;
                    }
                    ++i;
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent ev) {
        if (ev.getItemDrop().getItemStack().toString().contains("ThrownTrident")) {
            ev.setCancelled(true);
        }
    }
}
