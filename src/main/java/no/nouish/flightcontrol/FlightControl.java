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

import java.io.File;

import lombok.Getter;

import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Log4j2(topic = "FlightControl")
public final class FlightControl extends JavaPlugin
{
  static FlightControl getInstance()
  {
    return (FlightControl) FlightControl.getProvidingPlugin(FlightControl.class);
  }

  @Getter
  private final Configuration configuration = new Configuration(this);

  @Override
  public void onLoad()
  {
    setupLogging();
    saveDefaultConfig();
  }

  @Override
  public void onEnable()
  {
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new EventListener(this), this);

    FlightControlCommand flightControlCommand = new FlightControlCommand(this);
    PluginCommand pluginCommand = safeGetCommand("flightcontrol");
    pluginCommand.setExecutor(flightControlCommand);
    pluginCommand.setTabCompleter(flightControlCommand);
  }

  @NotNull
  private PluginCommand safeGetCommand(@NotNull String name)
  {
    PluginCommand command = getCommand(name);
    if (command == null)
    {
      throw new IllegalStateException("Undefined command '" + name + "'");
    }
    return command;
  }

  private void setupLogging()
  {
    LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
    FileAppender appender = FileAppender.newBuilder()
        .setName(LOGGER.getName())
        .withAppend(true)
        .withFileName(new File(getDataFolder(), "logs/output.log").toString())
        .setLayout(PatternLayout.newBuilder()
            .withPattern("%date{HH:mm:ss.SSS}{UTC} %-5level - %m%n")
            .build())
        .setConfiguration(loggerContext.getConfiguration()).build();
    Logger coreLogger = (Logger) LOGGER;
    // Additivity ensures it's logged both in our file,
    // and the defaults provided in the server software (ie. paper).
    coreLogger.setAdditive(true);
    coreLogger.addAppender(appender);
    appender.start();
    loggerContext.getConfiguration().addAppender(appender);
    loggerContext.updateLoggers();
  }
}
