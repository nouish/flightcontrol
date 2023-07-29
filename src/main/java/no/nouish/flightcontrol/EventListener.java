/*
 * This file is part of FlightControl, a plugin for Spigot servers.
 *
 * Copyright (C) 2023  Erik Eide
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package no.nouish.flightcontrol;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

import lombok.extern.log4j.Log4j2;

import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@Log4j2(topic = "FlightControl")
final class EventListener implements Listener
{
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  private final FlightControl flightControl;

  EventListener(FlightControl flightControl)
  {
    this.flightControl = Objects.requireNonNull(flightControl);
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  void onEntityDamageByEntity(EntityDamageByEntityEvent event)
  {
    Entity entity = event.getEntity();
    Location location = entity.getLocation();
    if (!isInTheEnd(location))
    {
      return;
    }

    ItemFrame itemFrame = safeToItemFrame(entity);
    if (itemFrame == null || !isNaturalSpawnedElytra(itemFrame.getItem()))
    {
      return;
    }

    Entity damager;
    if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Entity shooter)
    {
      damager = shooter;
    }
    else
    {
      damager = event.getDamager();
    }

    if (damager instanceof Player player)
    {
      attemptToTakeElytra(player, itemFrame, location, event);
    }
    else
    {
      event.setCancelled(true);
      //noinspection DataFlowIssue
      LOGGER.info("Protected elytra at {}, {}, {} in {} from {}.",
          location.getBlockX(), location.getBlockY(), location.getBlockZ(),
          location.getWorld().getName(), damager.getType());
    }
  }

  private void attemptToTakeElytra(Player player, ItemFrame itemFrame, Location location, Cancellable event)
  {
    PersistentDataContainer itemFramePdc = itemFrame.getPersistentDataContainer();
    String placedBy = itemFramePdc.get(FlightControlConstant.PLACED_BY, PersistentDataType.STRING);

    if (placedBy != null)
    {
      // Not a naturally spawned elytra.
      itemFramePdc.remove(FlightControlConstant.PLACED_BY);
      return;
    }

    final int maxCount = flightControl.getConfiguration().getMaxCount();
    final long periodHours = flightControl.getConfiguration().getPeriodHours();
    LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
    int countInPeriod;

    PersistentDataContainer pdc = player.getPersistentDataContainer();
    long periodStartLong = pdc.getOrDefault(FlightControlConstant.UNIX_PERIOD, PersistentDataType.LONG, (long) -1);
    if (periodStartLong != -1)
    {
      Instant periodStart = Instant.ofEpochSecond(periodStartLong);
      LocalDateTime start = LocalDateTime.ofInstant(periodStart, ZoneOffset.UTC);
      countInPeriod = pdc.getOrDefault(FlightControlConstant.COUNT_PERIOD, PersistentDataType.INTEGER, 0);
      LocalDateTime when = start.plusHours(periodHours);

      if (when.isBefore(now))
      {
        long epochSecond = now.toInstant(ZoneOffset.UTC).getEpochSecond();
        pdc.set(FlightControlConstant.UNIX_PERIOD, PersistentDataType.LONG, epochSecond);
        LOGGER.info("Period started {} reset for {} at {}. Found {} elytra.", start, player.getName(), now, countInPeriod);
        countInPeriod = 0;
      }
      else if (countInPeriod >= flightControl.getConfiguration().getMaxCount())
      {
        event.setCancelled(true);

        String wait;
        Duration timeToWait = Duration.between(now, when);

        if (timeToWait.toDays() >= 7)
        {
          wait = timeToWait.toDays() + " days";
        }
        else
        {
          wait = when.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
              + " at " + TIME_FORMATTER.format(when) + " UTC";
        }

        player.sendMessage("§4You must wait until " + wait + " to take any more elytra.");
        return;
      }
    }
    else
    {
      LOGGER.info("Starting new period for {} at {}.", player.getName(), now);
      countInPeriod = 0;
      long epochSecond = now.toInstant(ZoneOffset.UTC).getEpochSecond();
      pdc.set(FlightControlConstant.UNIX_PERIOD, PersistentDataType.LONG, epochSecond);
    }

    pdc.set(FlightControlConstant.COUNT_PERIOD, PersistentDataType.INTEGER, countInPeriod + 1);

    int countTotal = pdc.getOrDefault(FlightControlConstant.COUNT_TOTAL, PersistentDataType.INTEGER, 0) + 1;
    pdc.set(FlightControlConstant.COUNT_TOTAL, PersistentDataType.INTEGER, countTotal);

    player.sendMessage(String.format(Locale.ENGLISH,
        "§aYou've found %d / %d elytra this period. Your total count is: %,d.",
        countInPeriod + 1, maxCount, countTotal));

    //noinspection DataFlowIssue
    LOGGER.info("{} found an elytra at {}, {}, {} in {}. They've found {} total.",
        player.getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(),
        location.getWorld().getName(), countTotal);
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  void onHangingBreak(HangingBreakEvent event)
  {
    Entity entity = event.getEntity();
    Location location = entity.getLocation();
    if (!isInTheEnd(location))
    {
      return;
    }

    ItemFrame itemFrame = safeToItemFrame(entity);
    if (itemFrame == null || !isNaturalSpawnedElytra(itemFrame.getItem()))
    {
      return;
    }

    event.setCancelled(true);
    //noinspection DataFlowIssue
    LOGGER.info("Protected elytra at {}, {}, {} in {} from breaking.",
        location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName());
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  void onPlayerInteractEntity(PlayerInteractEntityEvent event)
  {
    ItemFrame itemFrame = safeToItemFrame(event.getRightClicked());
    if (itemFrame == null)
    {
      return;
    }

    Player player = event.getPlayer();
    ItemStack itemInHand = player.getInventory().getItemInMainHand();

    if (!isNaturalSpawnedElytra(itemInHand))
    {
      return;
    }

    PersistentDataContainer pdc = itemFrame.getPersistentDataContainer();
    pdc.set(FlightControlConstant.PLACED_BY, PersistentDataType.STRING, player.getUniqueId().toString());
  }

  private ItemFrame safeToItemFrame(Entity entity)
  {
    // End ships only ever have regular item frames, not glowing ones.
    if (entity.getType() != EntityType.ITEM_FRAME)
    {
      return null;
    }

    if (entity instanceof ItemFrame itemFrame)
    {
      return itemFrame;
    }

    return null;
  }

  private boolean isNaturalSpawnedElytra(ItemStack item)
  {
    if (item.getType() != Material.ELYTRA)
    {
      return false;
    }

    ItemMeta meta = item.getItemMeta();
    if (meta == null)
    {
      return true;
    }

    return !meta.hasEnchants() && !meta.hasLore() && !meta.hasDisplayName();
  }

  private boolean isInTheEnd(Location location)
  {
    World world = location.getWorld();
    return world != null && world.getEnvironment() == World.Environment.THE_END;
  }
}
