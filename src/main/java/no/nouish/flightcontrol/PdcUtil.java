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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PdcUtil
{
  static void clearItemFrame(@NotNull ItemFrame itemFrame)
  {
    itemFrame.getPersistentDataContainer().remove(FlightControlConstant.PLACED_BY);
  }

  @Nullable
  static String getItemFramePlacedBy(@NotNull ItemFrame itemFrame)
  {
    return itemFrame.getPersistentDataContainer().get(FlightControlConstant.PLACED_BY, PersistentDataType.STRING);
  }

  static void flagItemFrame(@NotNull ItemFrame itemFrame, @NotNull Player player)
  {
    itemFrame.getPersistentDataContainer()
        .set(FlightControlConstant.PLACED_BY, PersistentDataType.STRING, player.getUniqueId().toString());
  }

  @Nullable
  static LocalDateTime getPeriodStart(@NotNull Player player)
  {
    long periodStartLong = player.getPersistentDataContainer()
        .getOrDefault(FlightControlConstant.UNIX_PERIOD, PersistentDataType.LONG, (long) -1);
    if (periodStartLong == -1)
    {
      return null;
    }
    Instant periodStart = Instant.ofEpochSecond(periodStartLong);
    return LocalDateTime.ofInstant(periodStart, ZoneOffset.UTC);
  }

  static int getTotalCount(@NotNull Player player)
  {
    return player.getPersistentDataContainer()
        .getOrDefault(FlightControlConstant.COUNT_TOTAL, PersistentDataType.INTEGER, 0);
  }

  static void sendAvailableInfo(@NotNull Player player, boolean sendIfAvailable)
  {
    LocalDateTime start = getPeriodStart(player);
    int available = getAvailableElytras(player);

    if (start == null || available >= 1)
    {
      if (sendIfAvailable)
      {
        player.sendMessage(String.format(Locale.ENGLISH, "§aYou can still find %,d elytra.", available));
      }
      return;
    }

    LocalDateTime now = TimeUtil.getCurrentUtcDateTime();
    LocalDateTime when = start.plusHours(FlightControl.getInstance().getConfiguration().getPeriodHours());
    player.sendMessage("§4" + TimeUtil.getDisplayFormatUntil(now, when));
  }

  static int getAvailableElytras(@NotNull Player player)
  {
    FlightControl flightControl = FlightControl.getInstance();
    final int maxCount = flightControl.getConfiguration().getMaxCount();
    final long periodHours = flightControl.getConfiguration().getPeriodHours();
    LocalDateTime now = TimeUtil.getCurrentUtcDateTime();

    LocalDateTime start = PdcUtil.getPeriodStart(player);
    if (start != null)
    {
      LocalDateTime when = start.plusHours(periodHours);

      if (when.isBefore(now))
      {
        // Period expired
        return maxCount;
      }

      int periodCount = player.getPersistentDataContainer()
          .getOrDefault(FlightControlConstant.COUNT_PERIOD, PersistentDataType.INTEGER, 0);
      return maxCount - periodCount;
    }

    return maxCount;
  }

  private PdcUtil() {}
}
