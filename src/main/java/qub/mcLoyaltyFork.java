// afam2000 First Plugin
package qub;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.Random;

public final class mcLoyaltyFork extends JavaPlugin implements Listener {
    private static boolean simLooting = true;
    private static boolean canImpalePlayer = true;

    // Helper Function to get random int in range
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("toggleLootSim")) {
            simLooting = !simLooting;
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage("Offhand Looting Simulation Toggled: " + simLooting);
            }
            return true;
        }
        if (command.getName().equals("toggleCanImpale")) {
            canImpalePlayer = !canImpalePlayer;
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage("canImpalePlayer toggled to: " + canImpalePlayer);
            }
            return true;
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

    @EventHandler
    public void onTridentHit(ProjectileHitEvent ev) {
        Projectile proj = ev.getEntity();
        ProjectileSource ps = proj.getShooter();

        if (proj instanceof Trident && ps instanceof Player) {
            if (canImpalePlayer) {
                if (ev.getHitEntity() instanceof Player) {
                    Player victim = (Player) ev.getHitEntity();
                    if (proj.getShooter() instanceof Player) {
                        Player perp = (Player) proj.getShooter();
                        proj.addPassenger(ev.getHitEntity());
                        proj.setVelocity(new Vector(0f, 0.25f, 0f));
                    }
                }
            }

            //Pickup nearby items
            for (Entity ent : proj.getNearbyEntities(1.5, 1.0, 1.5)) {
                if (ent instanceof Item || ent instanceof ExperienceOrb) {
                    proj.addPassenger(ent);
                }
            }
        }
    }
}
