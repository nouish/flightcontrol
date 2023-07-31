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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import lombok.extern.log4j.Log4j2;

@Log4j2(topic = "FlightControl")
final class FlightControlCommand implements TabExecutor
{
  private final FlightControl flightControl;

  FlightControlCommand(@NotNull FlightControl flightControl)
  {
    this.flightControl = Objects.requireNonNull(flightControl);
  }

  @Override
  public boolean onCommand(
      @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
  {
    if (args.length >= 1 && "reload".equalsIgnoreCase(args[0]))
    {
      if (sender.hasPermission("flightcontrol.reload"))
      {
        // Because this should be a very rare occurance, I don't care it's sync right now
        flightControl.reloadConfig();
        sender.sendMessage("§aFlightControl configuration reloaded.");
        LOGGER.info("{} reloaded the config", sender.getName());
      }
      else
      {
        sender.sendMessage("§4You don't have permission to use that command.");
      }

      return true;
    }

    if (args.length >= 1)
    {
      return false;
    }

    if  (sender instanceof Player player)
    {
      PdcUtil.sendAvailableInfo(player, true);
      sender.sendMessage(String.format(Locale.ENGLISH, "§aYou've found %,d elytra total.",
          PdcUtil.getTotalCount(player)));
      return true;
    }

    return false;
  }

  @Override
  public List<String> onTabComplete(
      @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
  {
    List<String> suggestions = new ArrayList<>();

    if (args.length == 1)
    {
      if (sender.hasPermission("flightcontrol.reload"))
      {
        suggestions.add("reload");
      }
    }

    return suggestions;
  }
}
