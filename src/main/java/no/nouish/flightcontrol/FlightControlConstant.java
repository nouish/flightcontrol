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

import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

final class FlightControlConstant
{
  // Temporary, only to be used on TorrentSMP
  static final UUID DEVELOPER_UUID = UUID.fromString("fa8d60a7-9645-4a9f-b74d-173966174739");

  // Current time period stuff

  static final NamespacedKey UNIX_PERIOD = createNamespacedKey("unix_period");
  static final NamespacedKey COUNT_PERIOD = createNamespacedKey("count_period");
  static final NamespacedKey COUNT_TOTAL = createNamespacedKey("count_total");

  // Other

  /**
   * Used to identify whether an item frame was placed by a player.
   */
  static final NamespacedKey PLACED_BY = createNamespacedKey("placed_by");

  @NotNull
  private static NamespacedKey createNamespacedKey(@NotNull String key)
  {
    return new NamespacedKey(FlightControl.getInstance(), key);
  }

  private FlightControlConstant() {}
}
