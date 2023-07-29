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

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

final class FlightControlConstant
{
  static final JavaPlugin PLUGIN = FlightControl.getProvidingPlugin(FlightControl.class);

  // Current time period stuff

  static final NamespacedKey UNIX_PERIOD = new NamespacedKey(PLUGIN, "unix_period");
  static final NamespacedKey COUNT_PERIOD = new NamespacedKey(PLUGIN, "count_period");
  static final NamespacedKey COUNT_TOTAL = new NamespacedKey(PLUGIN, "count_total");

  // Other

  /**
   * Used to identify whether an item frame was placed by a player.
   */
  static final NamespacedKey PLACED_BY = new NamespacedKey(PLUGIN, "placed_by");

  private FlightControlConstant() {}
}
