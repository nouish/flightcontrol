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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.NotNull;

final class TimeUtil
{
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  // Prefer this over LocalDateTime.now() to ensure time is based on the same clock.
  @NotNull
  static LocalDateTime getCurrentUtcDateTime()
  {
    return LocalDateTime.now(Clock.systemUTC());
  }

  @NotNull
  static String getDisplayFormatUntil(@NotNull LocalDateTime now, @NotNull LocalDateTime when)
  {
    String wait;
    Duration timeToWait = Duration.between(now, when);

    if (timeToWait.toDays() >= 6)
    {
      wait = DurationFormatUtils.formatDurationWords(timeToWait.toMillis(), true, true);
    }
    else
    {
      wait = "until " + when.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
          + " at " + TIME_FORMATTER.format(when) + " UTC ("
          + DurationFormatUtils.formatDuration(timeToWait.toMillis(), "HH'h' mm'm' ss's'", true) + ")";
    }

    return "You must wait " + wait + " to take any more elytra";
  }

  private TimeUtil() {}
}
